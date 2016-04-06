package com.cob.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
*
*
*	I KNOW THAT IT ISNT ORGANIZED VERY WELL, I WILL GET TO THAT!
*
*
*/

public class COBPaint extends JApplet {

	private static final long serialVersionUID = 1L;

	public void init() {
		
		setSize(1000,650);
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					Application app = new Application();
					setContentPane(app);
				}
			});
		}catch(InvocationTargetException e) {
			e.printStackTrace();
		}catch(InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
}

class Application extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static Dimension SLIDER_DIMENSION = new Dimension(30,500);
	private int curR, curB, curG = 255;
	
	public Application() {
		
		JSlider redSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
		JSlider greenSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
		JSlider blueSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
		
		redSlider.setSize(SLIDER_DIMENSION);
		greenSlider.setSize(SLIDER_DIMENSION);
		blueSlider.setSize(SLIDER_DIMENSION);
		
		redSlider.setLocation(850, 55);
		greenSlider.setLocation(900, 55);
		blueSlider.setLocation(950, 55);
		
		redSlider.setMajorTickSpacing(25);
		greenSlider.setMajorTickSpacing(25);
		blueSlider.setMajorTickSpacing(25);
		
		redSlider.setMinorTickSpacing(5);
		greenSlider.setMinorTickSpacing(5);
		blueSlider.setMinorTickSpacing(5);
		
		redSlider.setPaintTicks(true);
		greenSlider.setPaintTicks(true);
		blueSlider.setPaintTicks(true);
		
		redSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				curR = redSlider.getValue();
				repaintIt();
				redSlider.setToolTipText("Red Value: " + curR);
			}
		});
		greenSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				curG = greenSlider.getValue();
				repaintIt();
				greenSlider.setToolTipText("Green Value: " + curG);
			}
		});
		blueSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				curB = blueSlider.getValue();
				repaintIt();
				blueSlider.setToolTipText("Blue Value: " + curB);
			}
		});
		
		setLayout(null);
		
		add(redSlider);
		add(greenSlider);
		add(blueSlider);
		
	}
	
	private void repaintIt(){
		super.repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawMenu(g);
	}
	
	public void drawMenu(Graphics g) {
		
		g.setColor(new Color(187,187,187));
		g.fillRect(0, 25, 1000, 25);
		
		g.fillRect(800, 25, 25, 625);
		
		g.setColor(Color.BLACK);
		g.drawString("RED", 850, 575);
		g.drawString("GREEN", 890, 575);
		g.drawString("BLUE", 950, 575);
		
		g.setColor(new Color(curR, curG, curB));
		g.fillRect(835,595,150,35);
		
		String hexValue = String.format("#%02X%02X%02X", curR, curG, curB);
		g.setColor(Color.black);
		g.drawString("Hex Value: " + hexValue, 850, 617);
	}
	
}
