package com.skloch.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class HustleGame extends Game {
	public SpriteBatch batch;
	public BitmapFont infoFont;
	public BitmapFont smallinfoFont;
	public int WIDTH;
	public int HEIGHT;
	public Skin skin;

	// Constructor to grab width and height of the game
	public HustleGame (int width, int height) {
		WIDTH = width;
		HEIGHT = height;
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("Interface/BlockyInterface.json"));
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
	}
}
