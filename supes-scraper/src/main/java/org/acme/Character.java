package org.acme;

import java.util.List;

public class Character {

    public String id;
    public String name;
    public String otherName;
    public int level;
    public String picture;
    public List<String> powers;
    public String origin;
    public String alignment;

    public Character() {

    }

    public Character(String id, String origin, String name, String otherName, int level, String picture, String alignment, List<String> powers) {
        this.id = id;
        this.origin = origin;
        this.name = name;
        this.level = level;
        this.picture = picture;
        this.powers = powers;
        this.otherName = otherName;
        if (alignment != null && !alignment.isBlank()) {
            if (alignment.trim().equalsIgnoreCase("Good")  || alignment.trim().equalsIgnoreCase("Neutral")) {
                this.alignment = "Hero";
            } else {
                this.alignment = "Villain";
            }
        }
    }

    public boolean validate() {
        return level > 0 && alignment != null && !alignment.isBlank() && !powers.isEmpty();
    }

    @Override
    public String toString() {
        return "Character{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", otherName='" + otherName + '\'' +
                ", level=" + level +
                ", picture='" + picture + '\'' +
                ", powers=" + powers +
                ", origin='" + origin + '\'' +
                ", alignment='" + alignment + '\'' +
                '}';
    }

    public boolean isHero() {
        return "hero".equalsIgnoreCase(alignment);
    }
}
