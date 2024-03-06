package com.skloch.game;


import java.util.HashMap;

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
        System.out.println(String.format("This is a %s!", object));
    }
}
