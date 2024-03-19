package com.skloch.game;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.skloch.game.HustleGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		int WIDTH = 1280;
		int HEIGHT = 720;
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowSizeLimits(1280/2, 720/2, 1920, 1080);
		config.setTitle("Heslington Hustle");
		config.setWindowedMode(WIDTH, HEIGHT);
		config.useVsync(true);
		config.setForegroundFPS(60);

		config.setWindowIcon(Files.FileType.Internal, "Icons/icon_16x16.png");
		config.setWindowIcon(Files.FileType.Internal, "Icons/icon_32x32.png");
		config.setWindowIcon(Files.FileType.Internal, "Icons/icon_128x128.png");

		new Lwjgl3Application(new HustleGame(WIDTH, HEIGHT), config);
	}
}
