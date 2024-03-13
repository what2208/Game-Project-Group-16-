package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

// MENU SCREEN
// First thing the player sees, launches them into the actual game
public class MenuScreen implements Screen {
    final HustleGame game;
    private Stage menuStage;

    OrthographicCamera camera;

    private Viewport viewport;
    private Texture titleTexture;

    public MenuScreen(final HustleGame game) {
        this.game = game;
        this.game.menuScreen = this;
        // Create stage to draw UI on
        menuStage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT));
        Gdx.input.setInputProcessor(menuStage);

        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);

        titleTexture = new Texture(Gdx.files.internal("title.png"));

        // Play menu music
        game.soundManager.playMenu();


        // Make table to draw buttons and title
        Table table = new Table();
//        table.setDebug(true);
        table.setFillParent(true);
        menuStage.addActor(table);

        // Get fonts
        game.infoFont = game.skin.getFont("Button_white");
        game.smallinfoFont = game.skin.getFont("Button_white");
        game.smallinfoFont.getData().setScale(0.8f);

        // Create the buttons and the title
//        Label title = new Label("Heslington Hustle", game.skin, "title"); // Old title, new uses a texture
        TextButton startButton = new TextButton("New Game", game.skin);
        TextButton settingsButton = new TextButton("Settings", game.skin);
        TextButton creditsButton = new TextButton("Credits", game.skin);
        TextButton exitButton = new TextButton("Exit", game.skin);

        // Add everything to the table using row() to go to a new line
        int buttonWidth = 340;
        table.add(startButton).uniformX().width(buttonWidth).padBottom(10).padTop(280);
        table.row();
        table.add(settingsButton).uniformX().width(buttonWidth).padBottom(10);
        table.row();
        table.add(creditsButton).uniformX().width(buttonWidth).padBottom(30);
        table.row();
        table.add(exitButton).uniformX().width(buttonWidth);
        table.top();

        // Add listeners to the buttons so they do things when pressed
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.soundManager.stopMenu();
                dispose();
                game.setScreen(new GameScreen(game));
            }
        }
        );

        // SETTINGS BUTTON
        Screen thisScreen = this;
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.soundManager.playButton();
                game.setScreen(new SettingsScreen(game, thisScreen));
            }
        });

        // CREDITS BUTTON
        creditsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.soundManager.playButton();
                game.setScreen(new CreditScreen(game, thisScreen));
            }
        });

        exitButton.addListener(new ChangeListener() {
               @Override
               public void changed(ChangeEvent event, Actor actor) {
                   game.soundManager.playButton();
                   Gdx.app.exit();
               }
           }
        );

        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.batch.setProjectionMatrix(camera.combined);

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.update();

        // Draw blue background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.53f, 0.81f, 0.92f, 1);
        game.shapeRenderer.rect(0, 0, game.WIDTH, game.HEIGHT);
        game.shapeRenderer.end();

        game.batch.begin();
        game.batch.draw(titleTexture, (viewport.getWorldWidth() / 2f) - (titleTexture.getWidth() / 2f), 500);
        game.batch.end();



        // Make the stage follow actions and draw itself
        menuStage.setViewport(viewport);
        menuStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        menuStage.draw();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        menuStage.getViewport().update(width, height);
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
        Gdx.input.setInputProcessor(menuStage);

        // See the comment in the resume() function in GameScreen to see why this pointless line exists
        Gdx.input.setCursorPosition(Gdx.input.getX(), Gdx.input.getY());
    }


    @Override
    public void dispose() {
        menuStage.dispose();
    }

}
