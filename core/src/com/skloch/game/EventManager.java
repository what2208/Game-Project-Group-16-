package com.skloch.game;

import java.util.HashMap;

// Used to call certain events when an object is interacted with/ events in general
public class EventManager {
    private GameScreen game;
    private HashMap<String, Integer> buildingEnergies;
    private HashMap<String, String> objectInteractions;

    public EventManager (GameScreen game) {
        this.game = game;

        buildingEnergies = new HashMap<String, Integer>();
        buildingEnergies.put("piazza", 10);

        objectInteractions = new HashMap<String, String>();
        objectInteractions.put(
                "Chest",
                "Wow! This chest is full of so many magical items! I wonder how they will help you out on your journey! Boy, this is an awfully long piece of text, I wonder if someone is testing something?\n...\n...\n...\nHow cool!"
                );
    }

    public void event (String eventKey) {
        switch (eventKey) {
            case "tree":
                treeEvent();
                break;
            case "chest":
                chestEvent();
                break;
            case "piazza":
                piazzaEvent();
                break;
            case "exit":
                // Should do nothing and just close the dialogue menu
                break;
            default:
                objectEvent(eventKey);
                break;

        }

    }

    public void treeEvent() {
        System.out.println("This is a tree!");
    }

    public void chestEvent() {
        System.out.println("This chest is empty!");
    }

    public void objectEvent(String object) {
        System.out.println("This is a " +  object + "!");
    }

    public void piazzaEvent() {
        System.out.println(String.format("You ate at the Piazza!\n You lost %d energy!", buildingEnergies.get("piazza")));
        game.decreaseEnergy(buildingEnergies.get("piazza"));
    }
}
