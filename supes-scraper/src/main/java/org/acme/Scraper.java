package org.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.acme.Constants.CHARACTERS;

@ApplicationScoped
public class Scraper {

    @Inject
    JsoupHelper helper;

    @Inject
    ObjectMapper mapper;

    @Inject
    Logger logger;

    public Character scrape(CharacterList.Entry entry) {
        String url = Constants.SUPER_DB_ROOT + entry.url();
        logger.infof("Scrapping %s - %s", entry.name(), url);
        String id = id(entry.name());
        var document = helper.downloadAndParse(url);
        var name = document.getElementsByTag("h1").text().trim();

        var otherName = document.getElementsByTag("h2").text().trim();
        if (otherName.equalsIgnoreCase(name)) {
            otherName = "";
        }

        var level = CharacterScraper.extractLevel(document);
        var powers = CharacterScraper.extractPowers(document);
        var picture = CharacterScraper.extractPicture(document);
        var align = CharacterScraper.extractAlignment(document);

        return new Character(id, entry.url(), name, otherName, level, picture, align, powers);
    }

    public void write(Character character) throws IOException {
        var src = character.picture;
        if (src.startsWith("https://")) {
            var response = helper.client().getAbs(src).ssl(true).sendAndAwait();
            if (response.statusCode() == 200) {
                logger.infof("Downloaded picture for %s (%d)", character.name, response.body().length());
                String filename = character.id + ".jpg";
                File pic = new File(CHARACTERS, filename);
                Files.write(pic.toPath(), response.body().getBytes());
                character.picture = filename;
            } else {
                logger.warnf("Unable to download picture for %s");
                return;
            }
        }
        File file = new File(CHARACTERS, character.id + ".json");
        Files.writeString(file.toPath(), mapper.writeValueAsString(character));
        logger.infof("File written for %s", character.name);
    }

    ExecutorService executor = Executors.newFixedThreadPool(10);

    public void scrapeAll(List<CharacterList.Entry> entries) {
        if (! CHARACTERS.isDirectory()) {
            CHARACTERS.mkdirs();
        }

        CountDownLatch latch = new CountDownLatch(entries.size());

        entries.forEach(entry ->
                executor.submit(() -> {
                    var character = scrape(entry);
                    try {
                        write(character);
                    } catch (IOException e) {
                        logger.errorf("Unable to scrape %s (%s)", character.name, character.origin, e);
                    } finally {
                        latch.countDown();
                    }
                    logger.infof("Still waiting for %d items", latch.getCount());
                })
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            // Ignore me.
        }

    }

    private static String id(String name) {
        return name.replace(" ", "-").replace("/", "_").toLowerCase() + "-" + UUID.randomUUID()
                .getMostSignificantBits();
    }

}
