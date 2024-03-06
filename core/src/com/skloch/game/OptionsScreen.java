package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class OptionsScreen implements Screen {
    private HustleGame game;
    private Stage optionStage;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Window optionMenu;

    public OptionsScreen (final HustleGame game) {
        this.game = game;
        optionStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(optionStage);

        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);

        // Configures an escape menu to display when hitting 'esc'
        // Escape menu
        optionMenu = new Window("", game.skin);
        optionStage.addActor(optionMenu);
        optionMenu.setModal(true);

        Table optionTable = new Table();

        optionMenu.add(optionTable).prefHeight(600);

        TextButton exitButton = new TextButton("Exit", game.skin);
        Label title = new Label("Options", game.skin, "button");
        Label musicTitle = new Label("Music Volume", game.skin, "interaction");
        Slider musicSlider = new Slider(0, 100, 1, false, game.skin, "default-horizontal");
        Label sfxTitle = new Label("SFX Volume", game.skin, "interaction");
        Slider sfxSlider = new Slider(0, 100, 1, false, game.skin, "default-horizontal");
        Table sliderTable = new Table();
        // optionTable.setDebug(true);
        // sliderTable.setDebug(true);

        musicSlider.setValue(100);
        sfxSlider.setValue(100);
        musicSlider.setWidth(1000);


        sliderTable.add(musicTitle).padRight(20);
        sliderTable.add(musicSlider).prefWidth(250);
        sliderTable.row().padTop(20);
        sliderTable.add(sfxTitle).padRight(20).right();
        sliderTable.add(sfxSlider).prefWidth(250);

        optionTable.add(title).top().padTop(40).padBottom(50);
        optionTable.row();
        optionTable.add(sliderTable).fillX();
        optionTable.row();
        optionTable.add(exitButton).pad(40, 50, 60, 50).width(300).bottom().expandY();

        optionMenu.pack();

        optionMenu.setSize(600, 600);

        // Centre
        optionMenu.setX(((float) Gdx.graphics.getWidth() / 2) - (optionMenu.getWidth() / 2));
        optionMenu.setY(((float) Gdx.graphics.getHeight() / 2) - (optionMenu.getHeight() / 2));

        // Create button listeners
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new MenuScreen(game));
            }
        });


    }


    @Override
    public void render (float delta) {
        ScreenUtils.clear(0.53f, 0.81f, 0.92f, 1);

        camera.update();
        // game.batch.setProjectionMatrix(camera.combined);

        optionStage.act(delta);
        optionStage.draw();
    }


    @Override
    public void resize(int width, int height) {

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

    }
}
