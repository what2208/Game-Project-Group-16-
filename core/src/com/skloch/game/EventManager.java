package com.skloch.game;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

// Used to call certain events when an object is interacted with/ events in general
public class EventManager {
    private GameScreen game;
    public HashMap<String, Integer> activityEnergies;
    private HashMap<String, String> objectInteractions;
    private Array<String> talkTopics;

    /**
     * A class that maps Object's event strings to actual Java functions.
     * To run a function call event(eventString), to add arguments add dashes.
     * E.g. a call to the Piazza function with an arg of 1 would be: "piazza-1"
     * Which the function interprets as "study at the piazza for 1 hour".
     * Object's event strings can be set in the Tiled map editor with a property called "event"
     *
     * @param game An instance of the GameScreen containing a player and dialogue box
     */
    public EventManager (GameScreen game) {
        this.game = game;

        // How much energy an hour of each activity should take
        activityEnergies = new HashMap<String, Integer>();
        activityEnergies.put("studying", 10);
        activityEnergies.put("meet_friends", 10);
        activityEnergies.put("eating", 10);


        // Define what to say when interacting with an object who's text won't change
        objectInteractions = new HashMap<String, String>();
        objectInteractions.put("chest", "Open the chest?");
        objectInteractions.put("comp_sci", "Study in the Computer Science building?");
        objectInteractions.put("piazza", "Meet your friends at the Piazza?");
        objectInteractions.put("accomodation", "Go to sleep for the night?");
        objectInteractions.put("ron_cooke", null); // Changes, dynamically returned in getObjectInteraction
        objectInteractions.put("tree", "Speak to the tree?");

        // Some random topics that can be chatted about
        String[] topics = {"dogs", "cats", "exams", "celebrities", "flatmates", "video games", "sports", "food", "fashion"};
        talkTopics = new Array<String>(topics);
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
            case "comp_sci":
                compSciEvent(args);
                break;
            case "ron_cooke":
                ronCookeEvent(args);
                break;
            case "accomodation":
                accomEvent(args);
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

    /**
     * Gets the interaction text associated with each object via a key
     * @param key
     * @return The object interaction text
     */
    public String getObjectInteraction(String key) {
        if (key.equals("ron_cooke")) {
            return String.format("Eat %s at the Ron Cooke Hub?", game.getMeal());
        } else {
            return objectInteractions.get(key);
        }
    }

    /**
     * @return True if the object has some custom text to display that isn't just "This is an x!"
     */
    public boolean hasCustomObjectInteraction(String key) {
        return objectInteractions.containsKey(key);
    }

    /**
     * Sets the text when talking to a tree
     */
    public void treeEvent() {
        game.dialogueBox.hideSelectBox();
        game.dialogueBox.setText("The tree doesn't say anything back.");
    }


    public void chestEvent() {
        game.dialogueBox.hideSelectBox();
        game.dialogueBox.setText("Wow! This chest is full of so many magical items! I wonder how they will help you out on your journey! Boy, this is an awfully long piece of text, I wonder if someone is testing something?\n...\n...\n...\nHow cool!");

    }

    /**
     * Sets the text when talking to an object without a dedicated function
     */
    public void objectEvent(String object) {
        game.dialogueBox.hideSelectBox();
        game.dialogueBox.setText("This is a " +  object + "!");
    }

    /**
     * Lets the player study at the piazza for x num of hours, decreases the player's energy and increments the
     * game time.
     *
     * @param args Arguments to be passed, should contain the hours the player wants to study. E.g. ["piazza", "1"]
     */
    public void piazzaEvent(String[] args) {
        int energyCost = activityEnergies.get("meet_friends");
        // If the player is too tired to meet friends
        if (game.getEnergy() < energyCost) {
            game.dialogueBox.setText("You are too tired to meet your friends right now!");

        } else if (args.length == 1) {
            // Ask the player to chat about something (makes no difference)
            String[] topics = randomTopics(3);
            game.dialogueBox.setText("What do you want to chat about?");
            game.dialogueBox.getSelectBox().setOptions(topics, new String[]{"piazza-"+topics[0], "piazza-"+topics[1], "piazza-"+topics[2]});
        } else {
            // Say that the player chatted about this topic for 1-3 hours
            int hours = ThreadLocalRandom.current().nextInt(1, 4);
            game.dialogueBox.setText(String.format("You talked about %s for %d hours!", args[1], hours));
            game.decreaseEnergy(energyCost * hours);
            game.passTime(hours * 60); // in seconds
        }
    }

    /**
     * @param amount The amount of topics to return
     * @return An array of x random topics the player can chat about
     */
    private String[] randomTopics(int amount) {
        // Returns an array of 3 random topics
        Array<String> topics = new Array<String>(amount);

        for (int i = 0;i<amount;i++) {
            String choice = talkTopics.random();
            if (!topics.contains(choice, false)) {
                topics.add(choice);
            } else {
                i -= 1;
            }
        }

        return topics.toArray(String.class);
    }

    public void compSciEvent(String[] args) {
        int energyCost = activityEnergies.get("studying");
        // If the player is too tired for any studying:
        if (game.getEnergy() < energyCost) {
            game.dialogueBox.hideSelectBox();
            game.dialogueBox.setText("You are too tired to study right now!");
        } else if (args.length == 1) {
            // If the player has not yet chosen how many hours, ask
            game.dialogueBox.setText("Study for how long?");
            game.dialogueBox.getSelectBox().setOptions(new String[]{"2 Hours (20)", "3 Hours (30)", "4 Hours (40)"}, new String[]{"piazza-2", "piazza-3", "piazza-4"});
        } else {
            int hours = Integer.parseInt(args[1]);
            // If the player does not have enough energy for the selected hours
            if (game.getEnergy() < hours*energyCost) {
                game.dialogueBox.setText("You don't have the energy to study for this long!");
            } else {
                // If they do have the energy to study
                game.dialogueBox.setText(String.format("You studied for %s hours!\nYou lost %d energy", args[1], hours*energyCost));
                game.decreaseEnergy(energyCost * hours);
                game.addStudyHours(hours);
                game.passTime(hours * 60); // in seconds
            }
        }
    }

    public void ronCookeEvent(String[] args) {
        int energyCost = activityEnergies.get("eating");
        if (game.getEnergy() < energyCost) {
            game.dialogueBox.setText("You are too tired to eat right now!");
        } else {
            game.dialogueBox.setText(String.format("You took an hour to eat %s at the Piazza!\nYou lost %d energy!", game.getMeal(), energyCost));
            game.decreaseEnergy(energyCost);
            game.passTime(60); // in seconds
        }
    }

    /**
     * The event called when sleeping in your accomodation, advances time 8 hours and restores your energy
     * @param args Unused currently
     */
    public void accomEvent(String[] args) {
        game.setEnergy(100);
        game.passTime(60*8); // in seconds
        game.dialogueBox.setText("You slept for 8 hours!\nYour energy was restored!");
    }
}
