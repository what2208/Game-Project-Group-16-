package com.skloch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
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




    public GameScreen(final HustleGame game) {
        this.game = game;
        // Set the stage specifically to a new gameStage so buttons from menu aren't interactable
        gameStage =  new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(gameStage);

        // Camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, camera);
        camera.setToOrtho(false, game.WIDTH, game.HEIGHT);

        player = new Player(game);

        // Escape menu
        escapeMenu = new Window("", game.skin);
        escapeMenu.setModal(true);
        escapeMenu.setMovable(true);
        escapeMenu.setResizable(true);
        escapeMenu.setWidth(1000);
        escapeMenu.pack();
        escapeMenu.setPosition(400, 300);
        gameStage.addActor(escapeMenu);

        // Load some textures
        testBuilding = new Texture(Gdx.files.internal("Sprites/testbuilding.png"));
        testBuildingHitBox = new Rectangle(600, 300, 150, 100);

        // Add the building to the list of the player's collidable objects
        player.addCollidable(testBuildingHitBox);

        // Button presses
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown (int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    System.out.println("Escape");
                    return true;
                }
                return false;
            }
        });
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


        // Handles movement based on key presses
        // Also handles the player's collision
        player.move();

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

        // Draw UI elements
//        gameStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
//        gameStage.draw();
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
    }
}
