package com.skloch.game;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * A class to display a dialogue box for text and options on the screen
 */
public class DialogueBox {
    private Window dialogueWindow;
    private Table dialogueTable;
    private Label textLabel;
    private Skin skin;
    private final int MAXCHARS;
    private SelectBox selectBox;



    public DialogueBox (Skin skin) {
        int WIDTH = 800;
        int HEIGHT = 200;
        MAXCHARS = 36;
        this.skin = skin;

        dialogueWindow = new Window("", skin);

        dialogueTable = new Table();
        dialogueWindow.addActor(dialogueTable);
        dialogueTable.setFillParent(true);
//        dialogueTable.setDebug(true);

        textLabel = new Label("Are you sure you want to sleep at the Piazza? This will cost you 10 energy", skin, "dialogue");
        dialogueTable.add(textLabel).expand().width(WIDTH - 80).top().padTop(40);
        textLabel.setWrap(false);


        dialogueWindow.setWidth(WIDTH);
        dialogueWindow.setHeight(HEIGHT);

        this.selectBox = new SelectBox();
        selectBox.setOptions(new String[]{"Yes", "No"}, new String[]{"piazza", "close"});

        setText("Are you sure you want to sleep at the Piazza? This will cost you 10 energy");

    }

    /**
     * A class displaying a little selction box to the user when an input is needed in dialog
     */
    class SelectBox {
        private Window selectWindow;
        private Table selectTable;
        private int choiceIndex = 0;
        private String[] options;
        private String[] events;
        private Array<Label> optionPointers = new Array<Label>();
        private Array<String> splitText = new Array<String>();
        public SelectBox () {
            selectWindow = new Window("", skin);
            selectTable = new Table();
            selectWindow.add(selectTable);



            selectWindow.setPosition(
                    dialogueWindow.getX() + dialogueWindow.getWidth() - selectWindow.getWidth(),
                    dialogueWindow.getY() + dialogueWindow.getHeight()-24
            );



        }

        /**
         * Sets the options visible to the player when asking for a choice.
         * Also sets which events to call from each option.
         * Event strings are translated into events in EventManager
         * @see EventManager
         *
         * @param options The options available to the player e.g. "Yes" and "No"
         * @param events The events called to the option of the same index E.g. "piazza" and "closeDialogue"
         */
        public void setOptions (String[] options, String[] events) {
            selectTable.clearChildren();

            this.options = options;
            this.events = events;
            optionPointers.clear();

            for (String option : options) {
                // Add each pointer to an array so it can be shown/hidden later without searching the table
                Label pointer = new Label(">", skin, "dialogue");
                optionPointers.add(pointer);
                selectTable.add(pointer).padRight(10).padLeft(10);
                pointer.setVisible(false);

                selectTable.add(new Label(option, skin, "dialogue")).left().padRight(10);
                selectTable.row();
            }

            selectTable.pack();
            selectWindow.setWidth(selectTable.getWidth()+70);
            selectWindow.setHeight(selectTable.getHeight()+70);

            // selectWindow.add(selectTable);

            // Recenter
            selectWindow.setPosition(
                    dialogueWindow.getX() + dialogueWindow.getWidth() - selectWindow.getWidth(),
                    dialogueWindow.getY() + dialogueWindow.getHeight()-24
            );

            // Show first pointer
            setChoice(0);


        }

        /**
         * Moves the player's choice up one selection
         * Also hides the pointer at the old index, and shows the pointer at the new index
         */
        public void choiceUp () {
            optionPointers.get(choiceIndex).setVisible(false);
            choiceIndex -= 1;
            if (choiceIndex < 0) {
                choiceIndex = 0;
            }
            optionPointers.get(choiceIndex).setVisible(true);

        }

        /**
         * The same as choiceUp but in the opposite direction
         */
        public void choiceDown () {
            optionPointers.get(choiceIndex).setVisible(false);
            choiceIndex += 1;
            if (choiceIndex >= options.length) {
                choiceIndex = options.length - 1;
            }
            optionPointers.get(choiceIndex).setVisible(true);
        }

        /**
         * Returns the event string associated with the selected choice
         * Call hide() afterwards to close the menu
         *
         * @return An event string to be passed to EventManager
         */

        public String getChoice () {
            return events[choiceIndex];
        }

        /**
         * Gets the window of the select box
         *
         * @return The window of the select box
         */
        public Window getWindow() {
            return selectWindow;
        }

        /**
         * Hides the selection widget
         */
        public void hide() {
            selectWindow.setVisible(false);
        }

        /**
         * Shows the selection widget
         */
        public void show() {
            selectWindow.setVisible(true);
        }

        /**
         * Returns whether the selection box is visible or not
         *
         * @return true if the selection box is visible
         */
        public boolean isVisible() {
            return selectWindow.isVisible();
        }

        /**
         * Sets the player's choice to a specific value, used to default to "No" for most options
         *
         * @param index The new choice index
         */
        public void setChoice(int index) {
            if (choiceIndex < options.length) {
                // Don't try and set option 4 to invisible if we only have 2 options
                optionPointers.get(choiceIndex).setVisible(false);
            }
            choiceIndex = index;
            optionPointers.get(choiceIndex).setVisible(true);
        }
    }


    /**
     * Sets the dialogue box and all its elements to a position onscreen
     *
     * @param x The x coordinate of the bottom left corner
     * @param y The y coordinate
     */
    public void setPos(float x, float y) {
        dialogueWindow.setPosition(x, y);

        selectBox.selectWindow.setPosition(
                x + dialogueWindow.getWidth() - selectBox.selectWindow.getWidth(),
                y + dialogueWindow.getHeight()-24
        );
    }

    /**
     * Sets the text on the dialogue box
     *
     * @param text The text to display
     */
    public void setText(String text) {
        // Add a newline every 36 chars
        String newString = "";
        int lastSpace = 0;
        int line = 0;
        int index = 0;
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                lastSpace = index + line*MAXCHARS;
            }

            // Account for any occuring linebreaks
            if (c == '\n') {
                lastSpace = index + line*MAXCHARS;
                index = 0;
                line += 1;
            }

            if (index >= MAXCHARS-1) {
                // Replace the last space with a linebreak
                newString = newString.substring(0, lastSpace) + "\n" + newString.substring(lastSpace+1);
                line += 1;
                index = 0;
            }
            newString = newString + c;
            index += 1;
        }
        textLabel.setText(newString);
    }

    /**
     * Makes the dialogue box visible, along with any elements that need to be shown
     */
    public void show() {
        dialogueWindow.setVisible(true);
    }

    /**
     * Hides the dialogue box and all of its elements
     */
    public void hide() {
        dialogueWindow.setVisible(false);
        selectBox.hide();
    }

    /**
     * Pressing 'confirm' on the dialogue box
     * Either selects the choice if the selectbox is open, or advances text if not
     */
    public void enter(EventManager eventManager) {
        if (selectBox.isVisible()) {
            eventManager.event(selectBox.getChoice());
        } else {
            advanceText();
        }
    }

    /**
     * Continues on to the next bit of text, or closes the window if the end is reached
     */
    public void advanceText() {
        hide();
    }

    /**
     * Hides just the selectbox window
     */
    public void hideSelectBox() {
        selectBox.hide();
    }

    /**
     * Checks if the main dialogue box is visible
     * @return true if it is visible, false otherwise
     */
    public boolean isVisible() {
        return dialogueWindow.isVisible();
    }

    /**
     * Gets the window widget of the dialogue box
     *
     * @return A window widget
     */
    public Window getWindow() {
        return dialogueWindow;
    }

    /**
     * Returns the width of the main dialogue screen widget
     * @return The width
     */

    public float getWidth() {
        return dialogueWindow.getWidth();
    }

    /**
     * Returns the height of the main dialogue screen widget
     * @return The height
     */
    public float getHeight() {
        return dialogueWindow.getHeight();
    }

    /**
     * Returns the created selectbox class
     * @return A SelectBox class
     */
    public SelectBox getSelectBox() {
        return selectBox;
    }




}
