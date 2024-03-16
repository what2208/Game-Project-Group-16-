package com.skloch.game;

import java.util.HashMap;

// Used to call certain events when an object is interacted with/ events in general
public class EventManager {
    private GameScreen game;
    public HashMap<String, Integer> buildingEnergies;
    public HashMap<String, String> objectInteractions;

    public EventManager (GameScreen game) {
        this.game = game;

        buildingEnergies = new HashMap<String, Integer>();
        buildingEnergies.put("piazza", 10);

        // Define what to say when interacting with an object
        objectInteractions = new HashMap<String, String>();
        objectInteractions.put("chest", "Open the chest?");
        objectInteractions.put("piazza", "Study at the Piazza?");
        objectInteractions.put("tree", "Speak to the tree?");
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
                game.dialogueBox.hide();
                break;
            default:
                objectEvent(eventKey);
                break;

        }

    }

    public void treeEvent() {
        game.dialogueBox.hideSelectBox();
        game.dialogueBox.setText("The tree doesn't say anything back.");
    }

    public void chestEvent() {
        game.dialogueBox.hideSelectBox();
        game.dialogueBox.setText("Wow! This chest is full of so many magical items! I wonder how they will help you out on your journey! Boy, this is an awfully long piece of text, I wonder if someone is testing something?\n...\n...\n...\nHow cool!");

    }

    public void objectEvent(String object) {
        game.dialogueBox.hideSelectBox();
        game.dialogueBox.setText("This is a " +  object + "!");
    }

    public void piazzaEvent(String[] args) {
        int energyCost = buildingEnergies.get("piazza");
        if (args.length == 1) {
            game.dialogueBox.setText("Study for how long?");
            game.dialogueBox.getSelectBox().setOptions(new String[]{"2 Hours (20)", "3 Hours (30)", "4 Hours (40)"}, new String[]{"piazza-2", "piazza-3", "piazza-4"});
        } else {
            int hours = Integer.parseInt(args[1]);
            game.dialogueBox.hideSelectBox();
            game.dialogueBox.setText(String.format("You studied for %s hours!\nYou lost %d energy", args[1], hours*energyCost));
            game.decreaseEnergy(energyCost * hours);
            game.addStudyHours(hours);
            game.passTime(hours * 60);
        }
    }
}
