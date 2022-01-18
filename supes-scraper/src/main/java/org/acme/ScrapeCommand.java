package org.acme;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import org.jboss.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Command(name = "scrape", mixinStandardHelpOptions = true)
public class ScrapeCommand implements Runnable {

    @CommandLine.Option(names = "--scrape", required = false)
    boolean scrape;

    @CommandLine.Option(names = "--generate", required = false)
    boolean generate;

    final int MAX_PAGE = 120;

    @Inject
    Logger logger;

    @Inject
    JsoupHelper helper;

    @Inject
    CharacterList characterList;

    @Inject Scraper scraper;

    @Inject CharacterDb db;

    @Inject Output output;

    @Override
    public void run() {
        List<CharacterList.Entry> entries = characterList.entries();
        if (scrape) {
            scraper.scrapeAll(entries);
        }

        db.load();
        if (generate) {
            output.write(db.list());
        }

//        var url = "/brother-voodoo/10-39/";
//        var name = "Brother Voodoo";
//        logger.infof("%s", scraper.scrape(new CharacterList.Entry(name, url)));

    }


}
