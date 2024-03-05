package com.skloch.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class HustleGame extends Game {
	public SpriteBatch batch;
	public BitmapFont infoFont;
	public BitmapFont smallinfoFont;
	public int WIDTH;
	public int HEIGHT;
	public Skin skin;
	public TiledMap map;
	public GameScreen gameScreen;
	public MenuScreen menuScreen;


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
}
