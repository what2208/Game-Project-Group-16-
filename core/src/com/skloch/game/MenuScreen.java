package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.Viewport;

// MENU SCREEN
// First thing the player sees, launches them into the actual game
public class MenuScreen implements Screen {
    final HustleGame game;
    private Stage menuStage;

    OrthographicCamera camera;

    private Viewport viewport;

    public MenuScreen(final HustleGame game) {
        this.game = game;
        // Create stage to draw UI on
        menuStage =  new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(menuStage);

        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);

        // Make table to draw buttons and title
        Table table = new Table();
        table.setFillParent(true);
        menuStage.addActor(table);

        // Load skin
        Skin skin = new Skin(Gdx.files.internal("Interface/BlockyInterface.json"));

        // Get fonts
        game.infoFont = skin.getFont("Button_white");
        game.smallinfoFont = skin.getFont("Button_white");
        game.smallinfoFont.getData().setScale(0.8f);

        // Creat the buttons and the title
        Label title = new Label("Heslington Hustle", skin);
        TextButton startButton = new TextButton("Start Game", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Add everything to the table using row() to go to a new line
        int buttonWidth = 340;
        table.row().pad(80, 0, 10, 0);
        table.add(title).uniformX();
        table.row().pad(100, 0, 0, 0);
        table.add(startButton).uniformX().width(buttonWidth);
        table.row().pad(10, 0, 10, 0);
        table.add(optionsButton).uniformX().width(buttonWidth);
        table.row().pad(10, 0, 10, 0);
        table.add(exitButton).uniformX().width(buttonWidth);
        table.row().pad(10, 0, 10, 0);;
        table.top();

        // Add listeners to the buttons so they do things when pressed
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        }
        );

        // OPTION BUTTON

        exitButton.addListener(new ChangeListener() {
               @Override
               public void changed(ChangeEvent event, Actor actor) {
                   Gdx.app.exit();
               }
           }
        );

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.53f, 0.81f, 0.92f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Make the stage follow actions and draw itself
        menuStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        menuStage.draw();

    }

    @Override
    public void resize(int width, int height) {
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

    }

}
