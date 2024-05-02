package com.skloch.game;

public class Event {
    private final String name;
    private final String text;
    private final int energyCost;
    private int streak;

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

    public int getStreak() {
        return streak;
    }

    public void perform() {
        streak++;
    }

    public void resetStreak() {
        streak = 0;
    }
}
