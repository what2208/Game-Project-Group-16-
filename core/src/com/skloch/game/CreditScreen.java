package com.skloch.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A scene2d window consisting of a title, a scrollable widget and an exit button.
 * Used to display the assets used in the game
 */
public class CreditScreen implements Screen{

    private HustleGame game;
    private Stage creditStage;
    private OrthographicCamera camera;
    private Viewport viewport;

    /**
     * A scene2d window consisting of a title, a scrollable widget and an exit button.
     * Credits are loaded from assets/Text/credits.txt and displayed in the scrollable widget
     * Thus any changes to assets or licenses must be reflected in credits.txt
     *
     * @param game An instance of the HustleGame class
     * @param previousScreen The screen to return to when the exit button is pressed
     */
    public CreditScreen (final HustleGame game, Screen previousScreen) {

        // Basically all the same code as the settings menu
        this.game = game;
        creditStage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT));
        Gdx.input.setInputProcessor(creditStage);

        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);

        // Create the window
        Window creditMenu = new Window("", game.skin);
        creditStage.addActor(creditMenu);
        creditMenu.setModal(true);

        // Table for UI elements in window
        Table creditTable = new Table();
        creditMenu.add(creditTable).prefHeight(600);

        // Title
        Label title = new Label("Credits", game.skin, "button");
        creditTable.add(title).padTop(10);
        creditTable.row();

        // Table for things inside the scrollable widget
        Table scrollTable = new Table();

        // Scrollable widget
        ScrollPane scrollWindow = new ScrollPane(scrollTable, game.skin);
        scrollWindow.setFadeScrollBars(false);
        // scrollWindow.setDebug(true);

        // scrollWindow.setFillParent(true);
        creditTable.add(scrollWindow).padTop(20).height(350);
        creditTable.row();

        // Actual credits
        // Credits are loaded when the game is initialised
        Label text = new Label(game.credits, game.skin, "interaction");
        text.setWrap(true);
        scrollTable.add(text).width(520f).padLeft(15);

        // Exit button
        TextButton exitButton = new TextButton("Exit", game.skin);
        creditTable.add(exitButton).bottom().width(300).padTop(10);

        creditMenu.pack();

        creditMenu.setSize(600, 600);

        // Centre the window
        creditMenu.setX((viewport.getWorldWidth() / 2) - (creditMenu.getWidth() / 2));
        creditMenu.setY((viewport.getWorldHeight() / 2) - (creditMenu.getHeight() / 2));

        // Listener for the exit button
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.soundManager.playButton();
                dispose();
                game.setScreen(previousScreen);
                previousScreen.resume();
            }
        });

    }

    /**
     * Renders the credits window
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render (float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.blueBackground.draw();

        creditStage.act(delta);
        creditStage.draw();

        camera.update();
    }


    /**
     * Correctly resizes the onscreen elements when the window is resized
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        creditStage.getViewport().update(width, height);
        viewport.update(width, height);
    }

    // Other required methods from Screen
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
