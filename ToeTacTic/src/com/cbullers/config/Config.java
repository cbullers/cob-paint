package com.cbullers.config;

import java.awt.Color;

public class Config {

	/*
	 * Variable class, with all the crap
	 */
	
	// Splash screen
	public static Color SPLASH_BACKGROUND = Color.BLUE;
	
	public static String APP_NAME = "ToeTacTic";
	
	// Changing variables
	public static volatile int gameType = 0; // 0 = single player
										// 1 = multiplayer
	
}
