package com.skloch.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class HustleGame extends Game {
	public SpriteBatch batch;
	public int WIDTH;
	public int HEIGHT;
	public Skin skin;
	public TiledMap map;
	public String credits, tutorialText;
	public GameScreen gameScreen;
	public MenuScreen menuScreen;
	public ShapeRenderer shapeRenderer;
	public SoundManager soundManager;
	public Stage blueBackground;


	// Constructor to grab width and height of the game
	public HustleGame (int width, int height) {
		WIDTH = width;
		HEIGHT = height;
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("Interface/BlockyInterface.json"));
		map = new TmxMapLoader().load("Test Map/testmap.tmx");
		shapeRenderer = new ShapeRenderer();
		soundManager = new SoundManager();

		// Make a stage with a blue background that any screen can draw
		Image blueImage = new Image(new Texture(Gdx.files.internal("Sprites/white_square.png")));
		blueImage.setColor(0.53f, 0.81f, 0.92f, 1);
		blueImage.setName("blue image");
		blueBackground = new Stage();
		blueBackground.addActor(blueImage);

		credits = readTextFile("Text/credits.txt");
		tutorialText = readTextFile("Text/tutorial_text.txt");

		this.setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		skin.dispose();
		map.dispose();
		shapeRenderer.dispose();
		soundManager.dispose();
	}

	public String readTextFile(String filepath) {
		FileHandle file = Gdx.files.internal(filepath);

		if (!file.exists()) {
			System.out.println("WARNING: Couldn't load file " + filepath);
			return "Couldn't load " + filepath;
		} else {
			return file.readString();
		}

	}
}
