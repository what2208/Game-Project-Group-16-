package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
// import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import java.awt.*;

public class GameScreen implements Screen {
    final HustleGame game;
    private OrthographicCamera camera;
    private int score = 0;
    public Player player;
    public Stage gameStage;
    private Window escapeMenu;
    private Viewport viewport;
    private Texture testBuilding;
    private Rectangle testBuildingHitBox;
    private boolean showEscapeMenu;
    private boolean paused = false;

    public TiledMap map = new TmxMapLoader().load("Test Map/testmap.tmx");
    public OrthogonalTiledMapRenderer renderer;




    public GameScreen(final HustleGame game) {
        this.game = game;
        // Set the stage specifically to a new gameStage so buttons from menu aren't interactable
        gameStage = new Stage(new ScreenViewport());

        // Camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);

        player = new Player(game);

        // Map
        float unitScale = 50 / 16f;
        renderer = new OrthogonalTiledMapRenderer(map, unitScale);

        // Escape menu
        escapeMenu = new Window("", game.skin);
        gameStage.addActor(escapeMenu);
        escapeMenu.setModal(true);

        Table escapeTable = new Table();
        escapeTable.setFillParent(true);

        escapeMenu.add(escapeTable);

        TextButton resumeButton = new TextButton("Resume", game.skin);
        TextButton settingsButton = new TextButton("Settings", game.skin);
        TextButton exitButton = new TextButton("Exit", game.skin);

        escapeTable.add(resumeButton).pad(60, 80, 10, 80).width(300);
        escapeTable.row();
        escapeTable.add(settingsButton).pad(10, 50, 10, 50).width(300);
        escapeTable.row();
        escapeTable.add(exitButton).pad(10, 50, 60, 50).width(300);

        escapeMenu.pack();

        // Centre
        escapeMenu.setX(((float) Gdx.graphics.getWidth() / 2) - (escapeMenu.getWidth() / 2));
        escapeMenu.setY(((float) Gdx.graphics.getHeight() / 2) - (escapeMenu.getHeight() / 2));

        // Create button listeners

        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showEscapeMenu = false;
                paused = false;
            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Show options screen
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // this.dispose();
                game.setScreen(new MenuScreen(game));
            }
        });



        // Load some textures
        testBuilding = new Texture(Gdx.files.internal("Sprites/testbuilding.png"));
        testBuildingHitBox = new Rectangle(600, 300, 150, 100);

        // Add the building to the list of the player's collidable objects
        player.addCollidable(testBuildingHitBox);

        // Button presses
        InputAdapter gameKeyBoardInput = new InputAdapter() {
            @Override
            public boolean keyDown (int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    showEscapeMenu = !showEscapeMenu;
                    paused = !paused;
                    // Return true to indicate the keydown event was handled
                    return true;
                }
                return false;
            }
        };

        // Since we need to listen to inputs from the stage and from the keyboard
        // Use an input multiplexer to listen for one inputadapter and then the other
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gameKeyBoardInput);
        multiplexer.addProcessor(gameStage);
        Gdx.input.setInputProcessor(multiplexer);

        // Set the player to the middle of the map
        // Get the dimensions of the top layer
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);

        player.setPos((float) layer.getWidth() / 2, (float) layer.getHeight() / 2);
    }

    @Override
    public void show() {

    }

    @Override
    public void render (float delta) {
        ScreenUtils.clear(0.07f, 0.43f, 0.08f, 1);
        camera.update();
        // Set batch to use the same coordinate system as the camera
        game.batch.setProjectionMatrix(camera.combined);

        camera.position.set(player.getX(), player.getY(), 0);


        // Handles movement based on key presses
        // Also handles the player's collision
        if (!paused) {
            player.move();
        }

        renderer.setView(camera);
        renderer.render();

        // LibGDX is based on openGL, which likes to draw everything at once
        // So game.batch stores everything renderable and the renders it all at once
        // This is where we put anything we want to display to the screen
        game.batch.begin();

        // Building(s)
        game.batch.draw(testBuilding, testBuildingHitBox.x, testBuildingHitBox.y);

        // Player
        game.batch.draw(player.getCurrentFrame(), player.sprite.x, player.sprite.y, 0, 0, player.sprite.width, player.sprite.height, 1f, 1f, 1);

        // Text
        game.infoFont.draw(game.batch, "Take a shower!", 0f, game.HEIGHT-40);
        game.smallinfoFont.draw(game.batch, String.format("Score: %d", score), 0f, game.HEIGHT-80);

        game.batch.end();

        // Draw popup screen
        if (showEscapeMenu) {
            gameStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            gameStage.draw();
        }




    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {
        testBuilding.dispose();
        map.dispose();
    }
}
