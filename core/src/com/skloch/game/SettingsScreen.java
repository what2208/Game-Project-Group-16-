package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class SettingsScreen implements Screen {
    private HustleGame game;
    private Stage optionStage;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Window optionMenu;
    public Slider musicSlider;
    public Slider sfxSlider;
    public Screen previousScreen;


    public SettingsScreen(final HustleGame game, Screen previousScreen) {
        // An option screen to let the player adjust the volume of music and sound effects
        this.game = game;
        this.previousScreen = previousScreen;
        optionStage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT));
        Gdx.input.setInputProcessor(optionStage);

        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);

        // Create the window
        optionMenu = new Window("", game.skin);
        optionStage.addActor(optionMenu);
        optionMenu.setModal(true);

        // Table for UI elements
        Table optionTable = new Table();
        optionMenu.add(optionTable).prefHeight(600);

        // Create all the UI elements
        // musicSlider and sfxSlider need to be accessible in render so they are already declared
        TextButton exitButton = new TextButton("Exit", game.skin);
        Label title = new Label("Settings", game.skin, "button");
        Label musicTitle = new Label("Music Volume", game.skin, "interaction");
        musicSlider = new Slider(0, 100, 1, false, game.skin, "default-horizontal");
        Label sfxTitle = new Label("SFX Volume", game.skin, "interaction");
        sfxSlider = new Slider(0, 100, 1, false, game.skin, "default-horizontal");
        Table sliderTable = new Table();
        // optionTable.setDebug(true);
        // sliderTable.setDebug(true);

        // Set to correct values
        musicSlider.setValue(game.soundManager.getMusicVolume()*100);
        sfxSlider.setValue(game.soundManager.getSfxVolume()*100);

        // Add to a smaller table to centre the labels and slider bars
        sliderTable.add(musicTitle).padRight(20);
        sliderTable.add(musicSlider).prefWidth(250);
        sliderTable.row().padTop(20);
        sliderTable.add(sfxTitle).padRight(20).right();
        sliderTable.add(sfxSlider).prefWidth(250);

        // Window UI elements
        optionTable.add(title).top().padTop(40).padBottom(50);
        optionTable.row();
        optionTable.add(sliderTable).fillX();
        optionTable.row();
        optionTable.add(exitButton).pad(40, 50, 60, 50).width(300).bottom().expandY();

        optionMenu.pack();

        optionMenu.setSize(600, 600);

        // Centre the window
        optionMenu.setX((viewport.getWorldWidth() / 2f) - (optionMenu.getWidth() / 2f));
        optionMenu.setY((viewport.getWorldHeight() / 2f) - (optionMenu.getHeight() / 2f));

        // Create exit button listener
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.soundManager.playButton();
                dispose();
                game.setScreen(previousScreen);
                previousScreen.resume();
            }
        });

        game.shapeRenderer.setProjectionMatrix(camera.combined);


    }


    @Override
    public void render (float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Draw blue background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.53f, 0.81f, 0.92f, 1);
        game.shapeRenderer.rect(0, 0, game.WIDTH, game.HEIGHT);
        game.shapeRenderer.end();

        optionStage.act(delta);
        optionStage.draw();

        // Volumes should be between 0 and 1
        game.soundManager.setMusicVolume(musicSlider.getValue() / 100);
        game.soundManager.setSfxVolume(sfxSlider.getValue() / 100);

        camera.update();

    }


    @Override
    public void resize(int width, int height) {
        optionStage.getViewport().update(width, height);
        viewport.update(width, height);
    }

    // Other required methods
    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        optionStage.dispose();
    }
}
