package org.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Output {

    private static final String REPO_ROOT = "https://github.com/cescoffier/supes-data/raw/master/characters";

    private static final String IMPORT_LINES = """
            INSERT INTO %s(id, name, otherName, picture, powers, level)
            VALUES (nextval('hibernate_sequence'), '%s', '%s', '%s', '%s', %d);
            """;

    @Inject
    ObjectMapper mapper;

    @Inject
    Logger logger;

    public void write(List<Character> characters) {
        StringBuilder importHeroes = new StringBuilder();
        StringBuilder importVillains = new StringBuilder();
        List<OutputCharacter> heroes = new ArrayList<>();
        List<OutputCharacter> villains = new ArrayList<>();
        for (Character character : characters) {
            if (character.isHero()) {
                importHeroes.append(IMPORT_LINES.formatted("hero", character.name, character.otherName,
                        REPO_ROOT + "/" + character.picture,
                        String.join("", character.powers), character.level));
                heroes.add(new OutputCharacter(character));
            } else {
                importVillains.append(IMPORT_LINES.formatted("villain", character.name, character.otherName,
                        REPO_ROOT + "/" + character.picture,
                        String.join("", character.powers), character.level));
                villains.add(new OutputCharacter(character));
            }
        }

        writeImportFiles(importHeroes, importVillains);
        writeDBFiles(heroes, villains);

        logger.infof("Wrote %d heroes", heroes.size());
        logger.infof("Wrote %d villains", villains.size());
    }

    private void writeImportFiles(StringBuilder importHeroes, StringBuilder importVillains) {
        File heroes = new File("../heroes-import.sql");
        File villains = new File("../villains-import.sql");
        try {
            Files.writeString(heroes.toPath(), importHeroes.toString(), StandardCharsets.UTF_8);
            Files.writeString(villains.toPath(), importVillains.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeDBFiles(List<OutputCharacter> importHeroes, List<OutputCharacter> importVillains) {
        File heroes = new File("../heroes.json");
        File villains = new File("../villains.json");
        try {
            Files.writeString(heroes.toPath(), mapper.writeValueAsString(importHeroes), StandardCharsets.UTF_8);
            Files.writeString(villains.toPath(), mapper.writeValueAsString(importVillains), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static class OutputCharacter {
        public String name;
        public String otherName;
        public int level;
        public String picture;
        public List<String> powers;

        public OutputCharacter(Character character) {
            this.name = character.name;
            this.otherName = character.otherName;
            this.level = character.level;
            this.picture = REPO_ROOT + "/" + character.picture;
            this.powers = character.powers;
        }
    }

}
