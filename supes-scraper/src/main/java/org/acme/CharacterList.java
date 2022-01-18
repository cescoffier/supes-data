package org.acme;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CharacterList {

    public static final File LIST = new File("list.json");

    private final JsonArray content;
    private final List<Entry> entries;

    public CharacterList() throws IOException {
        if (! LIST.isFile()) {
            throw new IllegalStateException(LIST.getAbsolutePath() + " does not exist");
        }
        var txt = Files.readString(LIST.toPath(), StandardCharsets.UTF_8);
        content = new JsonArray(txt);
        entries = new ArrayList<>();
        content.forEach(o -> {
            var j = (JsonObject) o;
            entries.add(new Entry(j.getString("name"), j.getString("url")));
        });

    }

    public JsonArray get() {
        return content;
    }

    public List<Entry> entries() {
        return entries;
    }


    record Entry(String name, String url) {

    }

}
