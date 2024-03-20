package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Align;

/**
 * @deprecated Creates a small window to recieve a yes/no response from the player
 * No longer used, but the code may still be useful
 */
public class OptionDialogue {
    private Window window;
    public Label questionLabel;
    private Label leftArrow;
    private Label rightArrow;
    boolean visible = false;
    boolean choice = false;
    private SoundManager soundManager;

    /**
     * @deprecated
     *
     * Creates a small window that can be shown or hidden to prompt the user for a yes/no input.
     * Became redundant when dialogue got selection boxes, but the code may still be useful to
     * those continuing the project.
     *
     *
     * @param question A question to display
     * @param width Width of the window
     * @param skin The game skin to use
     * @param soundManager An instance of soundManager to play sounds
     */
    public OptionDialogue (String question, int width, Skin skin, SoundManager soundManager) {
        // Create an option dialogue to prompt the player for an input
        // Attempted to make it as adaptable as possible
        // Width can be changed and text wraps
        // It will however go wrong up to a certain point

        this.soundManager = soundManager;
        window = new Window("", skin);
        window.setModal(true);
        window.setModal(true);

        Table dialogueTable = new Table();

        // dialogueTable.setDebug(true);
        // window.setDebug(true);

        questionLabel = new Label("Interact", skin, "interaction");
        questionLabel.setWrap(true);
        leftArrow = new Label(">", skin, "interaction");
        rightArrow = new Label(">", skin, "interaction");
        Label option1Label = new Label("Yes", skin, "interaction");
        Label option2Label = new Label("No", skin, "interaction");

        questionLabel.setAlignment(Align.center);

        dialogueTable.add(questionLabel).top().colspan(4).fillX();
        dialogueTable.row().pad(0, 0, 0, 0);
        dialogueTable.add(leftArrow).right().padLeft(width-290);
        dialogueTable.add(option1Label).left().padLeft(10).padRight(30);
        dialogueTable.add(rightArrow).right();
        dialogueTable.add(option2Label).left().padLeft(10).padRight(width-290);

        window.add(dialogueTable).fillX().left();

        window.pack();

        this.updateArrow();
        this.setVisible(false);

        this.setWidth(width);

    }

    /**
     * @return The optionDialogue window to be added to a screen in GameScreen
     */
    public Window getWindow () {
        return this.window;
    }

    /**
     * Set the position of the window
     * @param x
     * @param y
     */
    public void setPos(float x, float y) {
        window.setX(x);
        window.setY(y);
    }

    /**
     * Set the width of the window
     * @param x
     */
    public void setWidth (int x) {
        window.setWidth(x);
        window.setX(((float) Gdx.graphics.getWidth() / 2) - (window.getWidth() / 2));
        window.setY(((float) Gdx.graphics.getHeight() / 2) - (window.getHeight() / 2) - 150);
    }

    /**
     * Sets the text to be displayed on the window, usually a question
     * @param text
     */
    public void setQuestionText (String text) {
        questionLabel.setText(text);
    }

    /**
     * Sets the window's visible variable, used to hide/unhide
     * @param visible
     */
    public void setVisible (boolean visible) {
        this.visible = visible;
        window.setVisible(visible);
    }

    /**
     * @return Returns true if the window is visible
     */
    public boolean isVisible () {
        return visible;
    }

    /**
     * Gets the choice that the user has selected on the menu
     * @return true for yes and false for no
     */
    public boolean getChoice() {
        return choice;
    }

    /**
     * Sets the choice to a certain value
     * @param choice true for yes and false for no
     */
    public void setChoice (Boolean choice) {
        this.choice = choice;
        updateArrow();
    }


    /**
     * Takes a keycode input, and switches which value is pointed to on the dialogue.
     * If yes is pointed to, and d is pressed, no will be selected
     * This would usually be called inside an InputHandler
     * @param keycode An integer keycode
     */
    public void act(int keycode) {
        // Reacts to keypress to change which option is selected
        if (choice == true && (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT)) {
            choice = false;
        } else if (choice == false && (keycode == Input.Keys.A || keycode == Input.Keys.LEFT)) {
            choice = true;
        }

        this.updateArrow();
        soundManager.playDialogueOption();
    }

    /**
     * Sets the pointer arrow to point to a different label depending on what the user's current choice is
     * if choice == true the arrow will point to 'yes'
     */
    private void updateArrow () {
        // Updates which arrow is pointed at
        if (choice == true) {
            rightArrow.setVisible(false);
            leftArrow.setVisible(true);
        } else {
            rightArrow.setVisible(true);
            leftArrow.setVisible(false);
        }
    }
}
