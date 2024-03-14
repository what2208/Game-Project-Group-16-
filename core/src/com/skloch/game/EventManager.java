package com.skloch.game;

// Used to call certain events when an object is interacted with/ events in general
public class EventManager {
    final HustleGame game;

    public EventManager (HustleGame game) {
        this.game = game;
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
        System.out.println("You ate at the Piazza!\n You lost 10 energy!");
        game.decreaseEnergy(100);
    }
}
