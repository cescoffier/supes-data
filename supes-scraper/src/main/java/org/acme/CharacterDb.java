package org.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CharacterDb {

    @Inject
    ObjectMapper mapper;

    @Inject
    Logger logger;

    private List<Character> characters = new ArrayList<>();

    public void load() {
        File directory = Constants.CHARACTERS;
        File[] files = directory.listFiles();
        if (files == null) {
            throw new IllegalStateException("Unable to read files");
        }
        for (File file : files) {
            if (file.getName().endsWith(".json")) {
                try {
                    characters.add(mapper.readValue(file, Character.class));
                } catch (IOException e) {
                    logger.errorf("Unable to read %s", file.getName(), e);
                    throw new UncheckedIOException(e);
                }
            }
        }
    }

    public List<Character> list() {
        return characters;
    }

}
