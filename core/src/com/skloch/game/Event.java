package com.skloch.game;

public class Event {
    private final String name;
    private final String text;
    private final int energyCost;
    private int streak;
    private int timesPerformedToday;
    private int timesPerformedTotal;

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
        if (timesPerformedToday < 1) {
            streak++;
        }
        timesPerformedToday++;
        timesPerformedTotal++;
    }

    public void resetStreak() {
        streak = 0;
    }

    public int getTimesPerformedTotal() {
        return timesPerformedTotal;
    }

    public int getTimesPerformedToday() {
        return timesPerformedToday;
    }

    public void dayAdvanced() {
        if (timesPerformedToday == 0) {
            resetStreak();
        }
        timesPerformedToday = 0;
    }
}
