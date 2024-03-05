package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

public class OptionDialogue {
    private HustleGame game;
    private Window window;
    private Label questionLabel;
    private Label leftArrow;
    private Label rightArrow;

    boolean visible = false;
    boolean choice = false;
    public OptionDialogue (String question, String option1, String option2, HustleGame game) {
        this.game = game;
        window = new Window("", game.skin);
        // uiStage.addActor(dialogueMenu);
        window.setModal(true);

        Table dialogueTable = new Table();
        dialogueTable.setFillParent(true);
        window.add(dialogueTable);

        questionLabel = new Label("Interact with tree?", game.skin, "interaction");

        dialogueTable.add(questionLabel).pad(30, 40, 10, 40).colspan(4);
        dialogueTable.row();
        Table choiceTable = new Table();
        choiceTable.bottom();

        leftArrow = new Label(">", game.skin, "interaction");
        rightArrow = new Label(">", game.skin, "interaction");

        dialogueTable.add(leftArrow).right();
        dialogueTable.add(new Label(option1, game.skin, "interaction")).left().pad(0, 8, 0, 10);
        dialogueTable.add(rightArrow).right();
        dialogueTable.add(new Label(option2, game.skin, "interaction")).left().pad(0, 10, 0, 10);
        // dialogueMenu.add(choiceTable);

        window.pack();

        window.setX(((float) Gdx.graphics.getWidth() / 2) - (window.getWidth() / 2));
        window.setY(((float) Gdx.graphics.getHeight() / 2) - (window.getHeight() / 2) - 150);

        this.updateArrow();
        this.setVisible(false);
    }

    public Window getWindow () {
        return this.window;
    }

    public void setQuestionText (String text) {
        questionLabel.setText(text);
    }

    public void setVisible (boolean visible) {
        this.visible = visible;
        window.setVisible(visible);
    }

    public boolean isVisible () {
        return visible;
    }

    public boolean select() {
        // Returns the result of the choice of the player
        return choice;
    }

    public boolean getChoice() {
        return choice;
    }


    public void act(int keycode) {
        // Reacts to keypress to change which option is selected
        if (choice == true && (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT)) {
            choice = false;
        } else if (choice == false && (keycode == Input.Keys.A || keycode == Input.Keys.LEFT)) {
            choice = true;
        }

        this.updateArrow();
    }

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
