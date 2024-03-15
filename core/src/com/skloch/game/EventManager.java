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
        String[] args = eventKey.split("-");
        switch (args[0]) {
            case "tree":
                treeEvent();
                break;
            case "chest":
                chestEvent();
                break;
            case "piazza":
                piazzaEvent(args);
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
        game.dialogueBox.setText("The chest is empty...");
        game.dialogueBox.hide();
    }

    public void chestEvent() {
        game.dialogueBox.setText("The chest is empty...");
        game.dialogueBox.hide();
    }

    public void objectEvent(String object) {
        game.dialogueBox.setText("This is a " +  object + "!");
        game.dialogueBox.hide();
    }

    public void piazzaEvent(String[] args) {
        int energyCost = buildingEnergies.get("piazza");
        if (args.length == 1) {
            game.dialogueBox.setText("Study for how long?");
            game.dialogueBox.getSelectBox().setOptions(new String[]{"2 Hours (20)", "3 Hours (30)", "4 Hours (40)"}, new String[]{"piazza-2", "piazza-3", "piazza-4"});
        } else {
            game.dialogueBox.setText(String.format("You studied for %s hours!\nYou lost %d energy", args[1], Integer.parseInt(args[1])*energyCost));
            game.decreaseEnergy(energyCost * Integer.parseInt(args[1]));
            game.dialogueBox.hide();
        }
    }
}
