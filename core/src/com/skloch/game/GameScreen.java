package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * Handles the majority of the game logic, rendering and user inputs of the game.
 * Responsible for rendering the player and the map, and calling events.
 */
public class GameScreen implements Screen {
    final HustleGame game;
    private OrthographicCamera camera;
    private int energy = 100;
    private int hoursStudied, hoursRecreational, hoursSlept;
    private float daySeconds = 0; // Current seconds elapsed in day
    private int day = 1; // What day the game is on
    private Label timeLabel, dayLabel;
    public Player player;
    private Window escapeMenu;
    private Viewport viewport;
    public OrthogonalTiledMapRenderer mapRenderer;
    public Stage uiStage;
    private Label interactionLabel;
    private EventManager eventManager;
//    private OptionDialogue optionDialogue;
    protected InputMultiplexer inputMultiplexer;
    private Table uiTable;
    private Image energyBar;
    public DialogueBox dialogueBox;
    public final Image blackScreen;
    private boolean sleeping = false;


    /**
     *
     * @param game An instance of the class HustleGame containing variables that only need to be loaded or
     *             initialised once.
     * @param avatarChoice Which avatar the player has picked, 0 for the more masculine avatar, 1 for the more feminine
     */
    public GameScreen(final HustleGame game, int avatarChoice) {
        // Important game variables
        this.game = game;
        this.game.gameScreen = this;
        eventManager = new EventManager(this);

        // Scores
        hoursStudied = hoursRecreational = hoursSlept = 0;


        // Camera and viewport settings
        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);
        game.shapeRenderer.setProjectionMatrix(camera.combined);



        // Create a stage for the user interface to be on
        uiStage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT));
        // Add a black image over everything first
        blackScreen = new Image(new Texture(Gdx.files.internal("Sprites/black_square.png")));
        blackScreen.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        blackScreen.addAction(Actions.alpha(0f));

        // UI table to put everything in
        uiTable = new Table();
        uiTable.setSize(game.WIDTH, game.HEIGHT);
        uiStage.addActor(uiTable);



        // Create a player class
        if (avatarChoice == 1) {
            player = new Player("avatar1");
        } else {
            player = new Player("avatar2");
        }



        // USER INTERFACE

        // Create and center the yes/no box that appears when interacting with objects
//        optionDialogue = new OptionDialogue("", 400, this.game.skin, game.soundManager);
//        Window optWin = optionDialogue.getWindow();
//        optionDialogue.setPos(
//                (viewport.getWorldWidth() / 2f) - (optWin.getWidth() / 2f),
//                (viewport.getWorldHeight() / 2f) - (optWin.getHeight() / 2f) - 150
//        );
//        // Use addActor to add windows to the scene
//        uiTable.addActor(optionDialogue.getWindow());
//        optionDialogue.setVisible(false);

        // Interaction label to prompt player
        interactionLabel = new Label("E - Interact", game.skin, "default");

        // Dialogue box
        dialogueBox = new DialogueBox(game.skin);
        dialogueBox.setPos(
                (viewport.getWorldWidth() - dialogueBox.getWidth()) / 2f,
                15f);
        dialogueBox.hide();




        // Load energy bar elements
        Group energyGroup = new Group();
        energyGroup.setDebug(true);
        energyBar = new Image(new Texture(Gdx.files.internal("Interface/Energy Bar/green_bar.png")));
        Image energyBarOutline = new Image(new Texture(Gdx.files.internal("Interface/Energy Bar/bar_outline.png")));
        energyBarOutline.setPosition(viewport.getWorldWidth()-energyBarOutline.getWidth() - 15, 15);
        energyBar.setPosition(energyBarOutline.getX()+16, energyBarOutline.getY()+16);
        energyGroup.addActor(energyBar);
        energyGroup.addActor(energyBarOutline);


        // Set initial time
        daySeconds = (8*60); // 8:00 am

        // Table to display date and time
        Table timeTable = new Table();
        timeTable.setFillParent(true);
        timeLabel = new Label(formatTime((int) daySeconds), game.skin, "time");
        dayLabel = new Label(String.format("Day %d", day), game.skin, "day");
        timeTable.add(timeLabel).uniformX();
        timeTable.row();
        timeTable.add(dayLabel).uniformX().left().padTop(2);
        timeTable.top().left().padLeft(10).padTop(10);

        // Set the order of rendered UI elements
        uiTable.add(interactionLabel).padTop(300);
        uiStage.addActor(energyGroup);
        uiStage.addActor(timeTable);
        uiStage.addActor(blackScreen);
        uiStage.addActor(dialogueBox.getWindow());
        uiStage.addActor(dialogueBox.getSelectBox().getWindow());
        setupEscapeMenu(uiStage);



        // Start music
        game.soundManager.playOverworldMusic();


        // Create the keyboard input adapter that defines events to be called based on
        // specific button presses
        InputAdapter gameKeyBoardInput = makeInputAdapter();

        // Since we need to listen to inputs from the stage and from the keyboard
        // Use an input multiplexer to listen for one inputadapter and then the other
        // inputMultiplexer needs to be established before hand since we reference it on resume() when going
        // back to this screen from the settings menu
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gameKeyBoardInput);
        inputMultiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(inputMultiplexer);



        // Setup map
        float unitScale = game.mapScale / game.mapSquareSize;
        mapRenderer = new OrthogonalTiledMapRenderer(game.map, unitScale);

        // Set the player to the middle of the map
        // Get the dimensions of the top layer
        TiledMapTileLayer layer0 = (TiledMapTileLayer) game.map.getLayers().get(0);
        player.setPos(layer0.getWidth()*game.mapScale / 2f, layer0.getHeight()*game.mapScale / 2f);
        // Put camera on player
        camera.position.set(player.getCentreX(), player.getCentreY(), 0);

        // Give objects to player
        for (int layer : game.objectLayers) {
            // Get all objects on the layer
            MapObjects objects = game.map.getLayers().get(layer).getObjects();

            // Loop through each, handing them to the player
            for (int i = 0; i < objects.getCount(); i++) {
                // Get the properties of each object
                MapProperties properties = objects.get(i).getProperties();
                // If this is the spawn object, move the player there and don't collide
                if (properties.get("spawn") != null) {
                    player.setPos(((float) properties.get("x")) *unitScale, ((float) properties.get("y"))*unitScale);
                    camera.position.set(player.getPosAsVec3());
                } else {
                    // Make a new gameObject with these properties, passing along the scale the map is rendered
                    // at for accurate coordinates
                    player.addCollidable(new GameObject(properties, unitScale));
                }
            }
        }

        // Set the player to not go outside the bounds of the map
        // Assumes the bottom left corner of the map is at 0, 0
        player.setBounds(
                new Rectangle(
                        0,
                        0,
                        game.mapProperties.get("width", Integer.class) * game.mapScale,
                        game.mapProperties.get("height", Integer.class) * game.mapScale
                )
        );
        game.shapeRenderer.setProjectionMatrix(camera.combined);

        // Display a little good morning message
        dialogueBox.show();
        dialogueBox.setText(getWakeUpMessage());

    }

    @Override
    public void show() {

    }

    /**
     * Renders the player, updates sound, renders the map and updates any UI elements
     * Called every frame
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render (float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply(); // Update the viewport


        // Set delta to a constant value to minimise stuttering issues when moving the camera and player
        // Solution found here: https://www.reddit.com/r/libgdx/comments/5z6qaf/can_someone_help_me_understand_timestepsstuttering/
        delta = 0.016667f;
        // Update sound timers
        game.soundManager.processTimers(delta);


        // Load timer bar - needs fixing and drawing
        //TextureAtlas blueBar = new TextureAtlas(Gdx.files.internal("Interface/BlueTimeBar/BlueBar.atlas"));
        //Skin blueSkin = new Skin(blueBar);
        //ProgressBar timeBar = new ProgressBar(0, 200, 1, false, blueSkin);
        //timeBar.act(delta);


        // Increment the time and possibly day
        if (!escapeMenu.isVisible() && !sleeping) {
            passTime(Gdx.graphics.getDeltaTime());
        }
        timeLabel.setText(formatTime((int) daySeconds));

        // Freeze the player's movement for this frame if any menus are visible
        if (escapeMenu.isVisible() || dialogueBox.isVisible() || sleeping) {
            player.setFrozen(true);
        } else {
            player.setFrozen(false);
        }

        dialogueBox.scrollText(0.8f);


        // Let the player move to keyboard presses if not frozen
        // Player.move() handles player collision
        // Also play a footstep sound if they are moving
        player.move(delta);
        if (player.isMoving()) {
            game.soundManager.playFootstep();
        } else {
            game.soundManager.footstepBool = false;
        }


        // Update the map's render position
        mapRenderer.setView(camera);
        // Draw the background layer
        mapRenderer.render(game.backgroundLayers);

        // Begin the spritebatch to draw the player on the screen
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Player, draw and scale
        game.batch.draw(
                player.getCurrentFrame(),
                player.sprite.x, player.sprite.y,
                0, 0,
                player.sprite.width, player.sprite.height,
                1f, 1f, 1
        );

        game.batch.end();

        // Render map foreground layers
        mapRenderer.render(game.foregroundLayers);


        // Check if the interaction (press e to use) label needs to be drawn
        interactionLabel.setVisible(false);
        if (!dialogueBox.isVisible() && !escapeMenu.isVisible() && !sleeping) {
            if (player.nearObject()) {
                interactionLabel.setVisible(true);
                // Change text whether pressing E will interact or just read text
                if (player.getClosestObject().get("event") != null) {
                    interactionLabel.setText("E - Interact");
                } else if (player.getClosestObject().get("text") != null) {
                    interactionLabel.setText("E - Read Sign");
                }
            }
        }


        // Update UI elements
        uiStage.getViewport().apply();
        uiStage.act(delta);
        uiStage.draw();


        // Focus the camera on the center of the player
        // Make it slide into place too
        // Change to camera.positon.set() to remove cool sliding
        camera.position.slerp(
                new Vector3(
                        player.getCentreX(),
                        player.getCentreY(),
                        0
                ),
                delta*9
        );


        // Debug - Draw player hitboxes
        // drawHitboxes();

        // Debug - print the event value of the closest object to the player if there is one
//        if (player.getClosestObject() != null) {
//            System.out.println(player.getClosestObject().get("event"));
//        }


        camera.update();
    }


    /**
     * Configures everything needed to display the escape menu window when the player presses 'escape'
     * Doesn't return anything as the variable escapeMenu is used to store the window
     * Takes a table already added to the uiStage
     *
     * @param interfaceStage The stage that the escapeMenu should be added to
     */
    public void setupEscapeMenu(Stage interfaceStage) {
        // Configures an escape menu to display when hitting 'esc'
        // Escape menu
        escapeMenu = new Window("", game.skin);
        interfaceStage.addActor(escapeMenu);
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
                    game.soundManager.playButton();
                    game.soundManager.playOverworldMusic();
                    escapeMenu.setVisible(false);
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
                    game.soundManager.playButton();
                    game.setScreen(new SettingsScreen(game, thisScreen));
                }
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (escapeMenu.isVisible()) {
                    game.soundManager.playButton();
                    game.soundManager.stopOverworldMusic();
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
    }

    @Override
    public void pause() {
    }

    /**
     * Called when switching back to this gameScreen
     */
    @Override
    public void resume() {
        // Set the input multiplexer back to this stage
        Gdx.input.setInputProcessor(inputMultiplexer);

        // I'm not sure why, but there's a small bug where exiting the settings menu doesn't make the previous
        // button on the previous screen update, so it's stuck in the 'over' configuration until the
        // user moves the mouse.
        // Uncomment the below line to bring the bug back
        // It's an issue with changing screens, and I can't figure out why it happens, but setting the mouse position
        // to exactly where it is seems to force the stage to update itself and fixes the visual issue.

        Gdx.input.setCursorPosition( Gdx.input.getX(),  Gdx.input.getY());
    }

    @Override
    public void hide() {
    }

    /**
     * Disposes of certain elements, called when the game is closed
     */
    @Override
    public void dispose () {
        uiStage.dispose();
        mapRenderer.dispose();
    }

    /**
     * DEBUG - Draws the player's 3 hitboxes
     * Uncomment use at the bottom of render to use
     */
    public void drawHitboxes () {
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeType.Line);
        // Sprite
        game.shapeRenderer.setColor(1, 0, 0, 1);
        game.shapeRenderer.rect(player.sprite.x, player.sprite.y, player.sprite.width, player.sprite.height);
        // Feet hitbox
        game.shapeRenderer.setColor(0, 0, 1, 1);
        game.shapeRenderer.rect(player.feet.x, player.feet.y, player.feet.width, player.feet.height);
        // Event hitbox
        game.shapeRenderer.setColor(0, 1, 1, 1);
        game.shapeRenderer.rect(player.eventHitbox.x, player.eventHitbox.y, player.eventHitbox.width, player.eventHitbox.height);
        game.shapeRenderer.end();
    }


    /**
     * Add a number of seconds to the time elapsed in the day
     *
     * @param delta The time in seconds to add
     */
    public void passTime(float delta) {
        daySeconds += delta;
        while (daySeconds >= 1440) {
            daySeconds -= 1440;
            day += 1;
            dayLabel.setText(String.format("Day %s", day));
        }

        if (day >= 8) {
            GameOver();
        }
    }

    /**
     * Takes a time in seconds and formats it a time in the format HH:MMam/pm
     *
     * @param seconds The seconds elapsed in a day
     * @return A formatted time on a 12 hour clock
     */
    public String formatTime(int seconds) {
        // Takes a number of seconds and converts it into a 12 hour clock time
        int hour = Math.floorDiv(seconds, 60);
        String minutes = String.format("%02d", (seconds - hour * 60));

        // Make 12 hour
        if (hour == 24 || hour == 0) {
            return String.format("12:%sam", minutes);
        } else if (hour == 12) {
            return String.format("12:%spm", minutes);
        } else if (hour > 12) {
            return String.format("%d:%spm", hour-12, minutes);
        } else {
            return String.format("%d:%sam", hour, minutes);
        }
    }


    /**
     *  Generates an InputAdapter to handle game specific keyboard inputs
     *
     * @return An InputAdapter for keyboard inputs
     */
    public InputAdapter makeInputAdapter () {
        return new InputAdapter() {
            @Override
            public boolean keyDown (int keycode) {
                // SHOW ESCAPE MENU CODE
                if (keycode == Input.Keys.ESCAPE) {
                    if (escapeMenu.isVisible()) {
                        game.soundManager.playButton();
                        game.soundManager.playOverworldMusic();
                        escapeMenu.setVisible(false);
                    } else {
                        // game.soundManager.pauseOverworldMusic();
                        game.soundManager.playButton();
                        escapeMenu.setVisible(true);
                    }
                    // Return true to indicate the keydown event was handled
                    return true;
                }

                // SHOW OPTION MENU / ACT ON OPTION MENU CODE
                if (keycode == Input.Keys.E || keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    if (!escapeMenu.isVisible()) {
                        // If a dialogue box is visible, choose an option or advance text
                        if (dialogueBox.isVisible()) {
                            dialogueBox.enter(eventManager);
                            game.soundManager.playButton();

                        } else if (player.nearObject() && !sleeping) {
                            // If the object has an event associated with it
                            if (player.getClosestObject().get("event") != null) {
                                // Show a dialogue menu asking if they want to do an interaction with the object
                                dialogueBox.show();
                                dialogueBox.getSelectBox().setOptions(new String[]{"Yes", "No"}, new String[]{(String) player.getClosestObject().get("event"), "exit"});
                                if (eventManager.hasCustomObjectInteraction((String) player.getClosestObject().get("event"))) {
                                    dialogueBox.setText(eventManager.getObjectInteraction((String) player.getClosestObject().get("event")));
                                } else {
                                    dialogueBox.setText("Interact with " + player.getClosestObject().get("event") + "?");
                                }
                                dialogueBox.show();
                                dialogueBox.getSelectBox().show();
                                game.soundManager.playDialogueOpen();

                            } else if (player.getClosestObject().get("text") != null) {
                                // Otherwise, if it is a text object, just display its text
                                dialogueBox.show();
                                dialogueBox.setText((String) player.getClosestObject().get("text"));
                            }
                        }
                        return true;
                    }
                }

                // If an option dialogue is open it should soak up all keypresses
                if (dialogueBox.isVisible() && dialogueBox.getSelectBox().isVisible() && !escapeMenu.isVisible()) {
                    // Up or down
                    if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
                        dialogueBox.getSelectBox().choiceUp();
                    } else if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
                        dialogueBox.getSelectBox().choiceDown();
                    }

                    return true;

                }


                return false;
            }
        };
    }


    /**
     * Sets the player's energy level and updates the onscreen bar
     *
     * @param energy An int between 0 and 100
     */
    public void setEnergy(int energy) {
        this.energy = energy;
        if (this.energy > 100) {
            this.energy = 100;
        }
        energyBar.setScaleY(this.energy / 100f);
    }

    /**
     * @return The player's energy out of 100
     */
    public int getEnergy() {
        return this.energy;
    }

    /**
     * Decreases the player's energy by a certain amount
     *
     * @param energy The energy to decrement
     */
    public void decreaseEnergy(int energy) {
        this.energy = this.energy - energy;
        if (this.energy < 0) {
            this.energy = 0;
        }
        energyBar.setScaleY(this.energy / 100f);
    }

    // Functions related to game score and requirements

    /**
     * Adds an amount of hours studied to the total hours studied
     * @param hours The amount of hours to add
     */
    public void addStudyHours(int hours) {
        hoursStudied += hours;
    }

    /**
     * Adds an amount of recreational hours to the total amount for the current day
     * @param hours The amount of hours to add
     */
    public void addRecreationalHours(int hours) {
        hoursRecreational += hours;
    }

    /**
     * @return Returns 'breakfast', 'lunch' or 'dinner' depending on the time of day
     */
    public String getMeal() {
        int hours = Math.floorDiv((int) daySeconds, 60);
        if (hours >= 7 && hours <= 10) {
            //Breakfast between 7:00-10:59am
            return "breakfast";
        } else if (hours > 10 && hours <= 16) {
            // Lunch between 10:00am and 4:59pm
            return "lunch";
        } else if (hours > 16 && hours <= 21) {
            // Dinner served between 4:00pm and 9:59pm
            return "dinner";
        } else {
            // Nothing is served between 10:00pm and 6:59am
            return "food";
        }
    }

    /**
     * @return A wake up message based on the time left until the exam
     */
    public String getWakeUpMessage() {
        int daysLeft = 8 - day;
        if (daysLeft != 1) {
            return String.format("You have %d days left until your exam!\nRemember to eat, study and have fun, but don't overwork yourself!", daysLeft);
        } else {
            return "Your exam is tomorrow! I hope you've been studying! Remember not to overwork yourself and get enough sleep!";
        }
    }


    /**
     * @param sleeping Sets the value of sleeping
     */
    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }

    /**
     * @return true if the player is sleeping
     */
    public boolean getSleeping() {
        return sleeping;
    }

    /**
     * @param hours Add this amount of hours to the total hours slept
     */
    public void addSleptHours(int hours) {
        hoursSlept += hours;
    }

    /**
     * @return The number of seconds elapsed in the day
     */
    public float getSeconds() {
        return daySeconds;
    }

    /**
     * Ends the game, called at the end of the 7th day, switches to a screen that displays a score
     */
    public void GameOver() {
        game.setScreen(new GameOverScreen(game, hoursStudied, hoursRecreational, hoursSlept));
    }
}
