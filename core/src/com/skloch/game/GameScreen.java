package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
// import com.badlogic.gdx.maps.tiled.TmxMapLoader;


public class GameScreen implements Screen {
    final HustleGame game;
    private OrthographicCamera camera;
    private int score = 0;
    public Player player;
    public Stage escapeMenuStage;
    private Window escapeMenu;
    private Viewport viewport;
    private boolean showEscapeMenu = false;
    private boolean paused = false;
    public OrthogonalTiledMapRenderer renderer;
    private int[] backgroundLayers;
    private int[] foregroundLayers ;
    private int[] objectLayers;
    public Stage uiStage;
    public ShapeRenderer debugRenderer;
    private Label interactionLabel;
    private EventManager eventManager;
    public Window dialogueMenu;
    private boolean showingQuery;
    private OptionDialogue optionDialogue;
    protected InputMultiplexer inputMultiplexer;

    private Table uiTable;

    public GameScreen(final HustleGame game) {
        this.game = game;
        this.game.gameScreen = this;
        eventManager = new EventManager(this.game);

        // Camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);

        // Set the stage specifically to a new gameStage so buttons from menu aren't interactable
        uiStage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT));
        uiTable = new Table();
        // uiTable.setDebug(true);
        uiTable.setSize(game.WIDTH, game.HEIGHT);
//        uiTable.setPosition(viewport.getLeftGutterWidth(), viewport.getBottomGutterHeight());
        uiStage.addActor(uiTable);


        // Debug - Used to draw player hitboxes
        // Uncomment drawHitboxes() from the bottom of render to enable
        debugRenderer = new ShapeRenderer();
        debugRenderer.setProjectionMatrix(camera.combined);

        player = new Player(game);

        // Escape menu
        setupEscapeMenu(uiTable);

        // Other UI bits
        interactionLabel = new Label("Press E to interact", game.skin, "default");
        uiTable.add(interactionLabel).padTop(300);

        // Create and set the position of a yes/no option box that displays when the
        // player interacts with an object
        optionDialogue = new OptionDialogue("", 400, this.game.skin);
        Window optWin = optionDialogue.getWindow();
        optionDialogue.setPos(
                (viewport.getWorldWidth() / 2f) - (optWin.getWidth() / 2f),
                (viewport.getWorldHeight() / 2f) - (optWin.getHeight() / 2f) - 150
        );
        // Use addActor for menus that overlay other fixed text elements
        uiTable.addActor(optionDialogue.getWindow());
        optionDialogue.setVisible(false);


        // Map
        float unitScale = 50 / 16f;
        renderer = new OrthogonalTiledMapRenderer(game.map, unitScale);

        // Button presses
        InputAdapter gameKeyBoardInput = new InputAdapter() {
            @Override
            public boolean keyDown (int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (optionDialogue.isVisible()) {
                        optionDialogue.setVisible(false);
                        player.setFrozen(false);
                        return true;
                    }

                    if (escapeMenu.isVisible()) {
                        player.setFrozen(false);
                        escapeMenu.setVisible(false);
                    } else {
                        player.setFrozen(true);
                        escapeMenu.setVisible(true);
                    }
                    // Return true to indicate the keydown event was handled
                    return true;
                }

                if (keycode == Input.Keys.E || keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    if (player.nearObject()) {
                            if (optionDialogue.isVisible()) {
                                optionDialogue.setVisible(false);
                                player.setFrozen(false);

                                if (optionDialogue.getChoice()) {
                                    eventManager.event((String) player.getClosestObject().get("event"));
                                }
                            } else {
                                optionDialogue.setChoice(false);
                                optionDialogue.setQuestionText("Interact with " + player.getClosestObject().get("event") + "?");
                                player.setFrozen(true);
                                optionDialogue.setVisible(true);
                            }
                        }
                        return true;
                    }

                // If an option dialogue is open it should soak up all keypresses
                if (optionDialogue.isVisible()) {
                    optionDialogue.act(keycode);
                    return true;
                }



                return false;
            }
        };

        // Since we need to listen to inputs from the stage and from the keyboard
        // Use an input multiplexer to listen for one inputadapter and then the other
        // inputMultiplexer needs to be established before hand since we reference it on resume() when going
        // back to this screen from the settings menu
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gameKeyBoardInput);
        inputMultiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(inputMultiplexer);

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
                // Make a new gameObject with these properties, passing along the scale the map is rendered at for accurate coordinates
                player.addCollidable(new GameObject(properties, unitScale));
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

        resize(game.WIDTH, game.HEIGHT);
    }

    @Override
    public void show() {

    }

    @Override
    public void render (float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Set batch to use the same coordinate system as the camera

        // Set delta to a constant value to minimise stuttering issues when moving the camera and player
        // Solution found here: https://www.reddit.com/r/libgdx/comments/5z6qaf/can_someone_help_me_understand_timestepsstuttering/
        delta = 0.016667f;

        // Load timer bar - needs fixing and drawing
        //TextureAtlas blueBar = new TextureAtlas(Gdx.files.internal("Interface/BlueTimeBar/BlueBar.atlas"));
        //Skin blueSkin = new Skin(blueBar);
        //ProgressBar timeBar = new ProgressBar(0, 200, 1, false, blueSkin);
        //timeBar.act(delta);



        // Handles movement based on key presses
        // Also handles the player's collision
        if (!player.isFrozen()) {
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
        game.batch.draw(
                player.getCurrentFrame(),
                player.sprite.x, player.sprite.y,
                0, 0,
                player.sprite.width, player.sprite.height,
                1f, 1f, 1
        );

        // Text
        game.infoFont.draw(game.batch, "Take a shower!", 0f, game.HEIGHT-40);
        game.smallinfoFont.draw(game.batch, String.format("Score: %d", score), 0f, game.HEIGHT-80);


        renderer.render(foregroundLayers);

        game.batch.end();


        // uiStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        interactionLabel.setVisible(false);
        if (!optionDialogue.isVisible()) {
            if (player.nearObject()) {
                interactionLabel.setText("E - Interact with " + player.getClosestObject().get("event"));
                interactionLabel.setVisible(true);
            }
        }

        // Draw UI bits
        // uiStage.setViewport(viewport);
        uiStage.getViewport().apply();
        uiStage.act(delta);
        uiStage.draw();

        // Debug - Draw player hitboxes
        // drawHitboxes();
        // Debug, print the event value of the closest object to the player if there is one
//        if (player.getClosestObject() != null) {
//            System.out.println(player.getClosestObject().get("event"));
//        }


        // Focus the camera on the center of the player
        camera.position.set(player.getCentreX(), player.getCentreY(), 0);
        camera.update();

    }


    public void setupEscapeMenu(Table interfaceTable) {
        // Configures an escape menu to display when hitting 'esc'
        // Escape menu
        escapeMenu = new Window("", game.skin);
        interfaceTable.addActor(escapeMenu);
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

        // escapeMenu.setDebug(true);

        // Centre
        escapeMenu.setX((viewport.getWorldWidth() / 2) - (escapeMenu.getWidth() / 2));
        escapeMenu.setY((viewport.getWorldHeight() / 2) - (escapeMenu.getHeight() / 2));


        // Create button listeners

        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (escapeMenu.isVisible()) {
                    escapeMenu.setVisible(false);
                    player.setFrozen(false);
                }
            }
        });

        // SETTINGS BUTTON
        // I assign this object to a new var 'thisScreen' since the changeListener overrides 'this'
        // I wasn't sure of a better solution
        Screen thisScreen = this;
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (escapeMenu.isVisible()) {
                    game.setScreen(new SettingsScreen(game, thisScreen));
                }
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (escapeMenu.isVisible()) {
                    dispose();
                    game.setScreen(new MenuScreen(game));
                }
            }
        });

        escapeMenu.setVisible(false);

    }


    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height);
        viewport.update(width, height);

//        game.WIDTH = width - viewport.getRightGutterWidth() - viewport.getLeftGutterWidth();
//        game.HEIGHT = height - viewport.getTopGutterHeight() - viewport.getBottomGutterHeight();

//        escapeMenu.setX(((float) Gdx.graphics.getWidth() / 2) - (escapeMenu.getWidth() / 2));
//        escapeMenu.setY(((float) Gdx.graphics.getHeight() / 2) - (escapeMenu.getHeight() / 2));


    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        // Set the input multiplexer back to this stage
        Gdx.input.setInputProcessor(inputMultiplexer);

        // I'm not sure why, but there's a small bug where exiting the settings menu doesn't make the previous
        // button on the previous screen update, so it's stuck in the 'over' configuration until the
        // user moves the mouse.
        // Uncomment the below line to bring the bug back
        // It's an issue with changing screens, and I can't figure out why it happens, but setting the mouse position
        // to exactly where it is seems to force the stage to update itself.

        Gdx.input.setCursorPosition( Gdx.input.getX(),  Gdx.input.getY());
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose () {
        // testBuilding.dispose();
        if (debugRenderer != null) {
            debugRenderer.dispose();
        }
        uiStage.dispose();
    }

    public void drawHitboxes () {
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeType.Line);
        // Sprite
        debugRenderer.setColor(1, 0, 0, 1);
        debugRenderer.rect(player.sprite.x, player.sprite.y, player.sprite.width, player.sprite.height);
        // Feet hitbox
        debugRenderer.setColor(0, 0, 1, 1);
        debugRenderer.rect(player.feet.x, player.feet.y, player.feet.width, player.feet.height);
        // Event hitbox
        debugRenderer.setColor(0, 1, 1, 1);
        debugRenderer.rect(player.eventHitbox.x, player.eventHitbox.y, player.eventHitbox.width, player.eventHitbox.height);
        debugRenderer.end();
    }
}
