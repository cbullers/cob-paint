package com.cob.paint;

import java.awt.Color;
import java.awt.Dimension;

public class COBConfig {
	
	public static String LANGUAGE = "en";

	// Sizes for the things..?
	public static Dimension APP_DIMENSION = new Dimension(1000,650); // Size of the whole application
	public static Dimension SLIDER_DIMENSION = new Dimension(30,500); // Size of the RGB sliders
	public static Dimension BRUSHSLIDER_DIMENSION = new Dimension(300, 30); // Brush size slider
	
	// Colors
	public static Color STARTING_COLOR = new Color(255,255,255); // The default foreground color
	public static Color BACKGROUND_COLOR = new Color(238,238,238); // The default background color
	
	// Language
	public static String SAVE_TOOLTIP = "Save image";
	public static String PEN_TOOLTIP = "Pen tool";
	public static String FOUNTAIN_TOOLTIP = "Fountain pen tool";
	public static String ROLLER_TOOLTIP = "Roller tool";
	public static String FILL_TOOLTIP = "Bucket tool";
	public static String RECT_TOOLTIP = "Rectangle tool";
	public static String UNDO_TOOLTIP = "Undo last action";
	public static String ERASER_TOOLTIP = "Eraser tool";
	public static String LINE_TOOLTIP = "Line tool";
	public static String MULTI_TOOLTIP = "Multi-tool";
	public static String TEXT_TOOLTIP = "Text tool";
	public static String OPEN_TOOLTIP = "Open image";
}
