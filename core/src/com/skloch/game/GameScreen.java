package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
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
    public OrthogonalTiledMapRenderer renderer;
    private int[] backgroundLayers;
    private int[] foregroundLayers ;
    private int[] objectLayers;


    public GameScreen(final HustleGame game) {
        this.game = game;
        // Set the stage specifically to a new gameStage so buttons from menu aren't interactable
        gameStage = new Stage(new ScreenViewport());

        // Camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);

        player = new Player(game);

        // Escape menu
        setupEscapeMenu();

        // Map
        float unitScale = 50 / 16f;
        renderer = new OrthogonalTiledMapRenderer(game.map, unitScale);


        // Load some textures
//        testBuilding = new Texture(Gdx.files.internal("Sprites/testbuilding.png"));
//        testBuildingHitBox = new Rectangle(600, 300, 150, 100);

        // Add the building to the list of the player's collidable objects
        // player.addCollidable(testBuildingHitBox);

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
        TiledMapTileLayer layer0 = (TiledMapTileLayer) game.map.getLayers().get(0);

        player.setPos((float) layer0.getWidth()*50 / 2, (float) layer0.getHeight()*50 / 2);

        // Define background, foreground and object layers
        backgroundLayers = new int[] {0, 1};
        foregroundLayers = new int[] {3};
        objectLayers = new int[] {2};

        // Give objects to player
        // Loop through all objects layers
        for (int layer : objectLayers) {
            // Get all objects on the layer
            MapObjects objects = game.map.getLayers().get(layer).getObjects();

            // Loop through each, handing them to the player
            for (int i = 0; i < objects.getCount(); i++) {
                // Get the properties of each object
                MapProperties properties = objects.get(i).getProperties();
                // Make a new rect and pass this to the player
                player.addCollidable(
                        new Rectangle(
                                (float) properties.get("x") * unitScale,
                                (float) properties.get("y") * unitScale,
                                (float) properties.get("width") * unitScale,
                                (float) properties.get("height") * unitScale
                        )
                );
                // Bosh
            }
        }

        // Set the player to not go outside the bounds of the map
        // Assumes the bottom left corner of the map is at 0, 0
        player.setBounds(
                new Rectangle(
                        0,
                        0,
                        layer0.getWidth()*50,
                        layer0.getHeight()*50
                )
        );
    }

    @Override
    public void show() {

    }

    @Override
    public void render (float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Set batch to use the same coordinate system as the camera

        // Set delta to a constant value to minimise stuttering issues when moving the camera and player
        // Solution found here: https://www.reddit.com/r/libgdx/comments/5z6qaf/can_someone_help_me_understand_timestepsstuttering/
        delta = 0.0167f;

        // Load timer bar - needs fixing and drawing
        //TextureAtlas blueBar = new TextureAtlas(Gdx.files.internal("Interface/BlueTimeBar/BlueBar.atlas"));
        //Skin blueSkin = new Skin(blueBar);
        //ProgressBar timeBar = new ProgressBar(0, 200, 1, false, blueSkin);
        //timeBar.act(delta);



        // Handles movement based on key presses
        // Also handles the player's collision
        if (!paused) {
            player.move(delta);
        }

        renderer.setView(camera);
        renderer.render(backgroundLayers);
        renderer.render(objectLayers);

        // LibGDX is based on openGL, which likes to draw everything at once
        // So game.batch stores everything renderable and the renders it all at once
        // This is where we put anything we want to display to the screen
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Player
        game.batch.draw(player.getCurrentFrame(), player.sprite.x, player.sprite.y, 0, 0, player.sprite.width, player.sprite.height, 1f, 1f, 1);

        // Text
        game.infoFont.draw(game.batch, "Take a shower!", 0f, game.HEIGHT-40);
        game.smallinfoFont.draw(game.batch, String.format("Score: %d", score), 0f, game.HEIGHT-80);


        renderer.render(foregroundLayers);

        // Draw popup screen
        if (showEscapeMenu) {
            gameStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            gameStage.draw();
        }

        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();


    }


    public void setupEscapeMenu() {
        // Configures an escape menu to display when hitting 'esc'
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
                if (paused) {
                    dispose();
                    game.setScreen(new MenuScreen(game));
                }
            }
        });
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
        // testBuilding.dispose();
    }
}
