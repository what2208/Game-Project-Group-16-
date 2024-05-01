package com.skloch.game;

public class Event {
    private final String name;
    private final String text;
    private final int energyCost;

    public Event(String name, String text, int energyCost) {
        this.name = name;
        this.text = text;
        this.energyCost = energyCost;
    }


    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getEnergyCost() {
        return energyCost;
    }
}
