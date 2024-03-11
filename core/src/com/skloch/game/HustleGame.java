package com.skloch.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.logging.FileHandler;

public class HustleGame extends Game {
	public SpriteBatch batch;
	public BitmapFont infoFont;
	public BitmapFont smallinfoFont;
	public int WIDTH;
	public int HEIGHT;
	public Skin skin;
	public TiledMap map;
	public String credits;
	public GameScreen gameScreen;
	public MenuScreen menuScreen;
	public float musicVolume = 0.8f;
	public float sfxVolume = 0.8f;


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

		credits = readCreditsFile();

		this.setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		infoFont.dispose();
		smallinfoFont.dispose();
		skin.dispose();
		map.dispose();
	}

	public String readCreditsFile() {
		FileHandle file = Gdx.files.internal("credits.txt");

		if (!file.exists()) {
			System.out.println("WARNING: Couldn't load credits file");
			return "Error: Couldn't load assets/credits.txt";
		} else {
			return file.readString();
		}

	}
}
