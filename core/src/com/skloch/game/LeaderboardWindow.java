package com.skloch.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.swing.text.View;

public class LeaderboardWindow {
    Window window;
    Skin skin;
    Viewport viewport;
    Leaderboard leaderboard;


    public LeaderboardWindow(Leaderboard leaderboard, Stage parentStage, Skin skin, Viewport viewport) {
        window = new Window("", skin);
        this.skin = skin;
        this.viewport = viewport;
        this.leaderboard = leaderboard;

        String leaderboardText = leaderboard.GetLeaderboardText();
        parentStage.addActor(buildWindow(leaderboardText));
    }

    public void show() {
        window.setVisible(true);
        window.toFront();
    }

    public void hide() {
        window.setVisible(false);
    }

    private Window buildWindow(String leaderboardText) {
        window.setVisible(false);
        Table table = new Table();
        window.add(table).prefHeight(600).prefWidth(800-20);

        // Title
        Label title = new Label("Leaderboard", skin, "button");
        table.add(title).padTop(10);
        table.row();

        // Table for things inside the scrollable widget
        Table scrollTable = new Table();

        // Scrollable widget
        ScrollPane scrollWindow = new ScrollPane(scrollTable, skin);
        scrollWindow.setFadeScrollBars(false);

        table.add(scrollWindow).padTop(20).height(350).width(870);
        table.row();

        Label text = new Label(leaderboardText, skin, "interaction");
        text.setWrap(true);
        scrollTable.add(text).width(820f).padLeft(20);

        // Exit button
        TextButton continueButton = new TextButton("Continue", skin);
        table.add(continueButton).bottom().width(300).padTop(10);

        window.pack();

        window.setSize(900, 600);

        // Centre the window
        window.setX((viewport.getWorldWidth() / 2) - (window.getWidth() / 2));
        window.setY((viewport.getWorldHeight() / 2) - (window.getHeight() / 2));

        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });

        return window;
    }
}
