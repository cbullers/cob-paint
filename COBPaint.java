package com.cob.paint;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
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
		final Application app = new Application(this);
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
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
	private URL codeBase;
	public Tool currentTool;
	
	JSlider redSlider;
	JSlider greenSlider;
	JSlider blueSlider;
	
	JButton penButton;
	JButton rollerButton;
	
	JPanel canvas;
	
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Image penCursorImg = toolkit.getImage("../pencil.png");
	Cursor penCursor = toolkit.createCustomCursor(penCursorImg, new Point(this.getX(), this.getY()), "penCursor");
	
	Image rollerCursorImg = toolkit.getImage("../roller.png");
	Cursor rollerCursor = toolkit.createCustomCursor(rollerCursorImg, new Point(this.getX(), this.getY()), "rollerCursor");
	
	public Application(JApplet a) { 
		codeBase = a.getCodeBase();
		setLayout(null);
		initSliders();
		initCanvas();
		initButtons();
		add(redSlider);
		add(greenSlider);
		add(blueSlider);
		add(penButton);
		add(rollerButton);
		add(canvas);
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
		g.fillRect(0, 0, 1000, 50);
		
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
		
		// Draw the pen tool image

		
		// Draw the roller tool image
		
	}
	
	private void initButtons() {
		// Pen button
		penButton = new JButton();
		penButton.setSize(32,32);
		penButton.setLocation(7,7);
		try{
			URL penUrl = new URL(codeBase, "../pencil.png");
			Image img = ImageIO.read(penUrl);
			img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
			penButton.setIcon(new ImageIcon(img));
		}catch(IOException e) {
			e.printStackTrace();
		}
		penButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent a) {
				currentTool = new PenTool();
				resetCursor();
			}
			
		});
		
		
		// Roller Button
		rollerButton = new JButton();
		rollerButton.setSize(32,32);
		rollerButton.setLocation(45,7);
		try{
			URL rollerUrl = new URL(codeBase, "../roller.png");
			Image img = ImageIO.read(rollerUrl);
			img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
			rollerButton.setIcon(new ImageIcon(img));
		}catch(IOException e){
			e.printStackTrace();
		}
		rollerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentTool = new RollerTool();
			}
			
		});
		
	}
	
	public void initSliders() {
		redSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
		greenSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
		blueSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
		
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
		
	}
	
	private void resetCursor() {
		if(currentTool instanceof PenTool) {
			this.setCursor(penCursor);
		}else if(currentTool instanceof RollerTool) {
			this.setCursor(rollerCursor);
		}
	}
	
	private void setDefaultCursor() {
		this.setCursor(Cursor.getDefaultCursor());
	}
	
	private void initCanvas() {
		
		boolean pressingDown = false;
		
		canvas = new JPanel();
		canvas.setLocation(0,50);
		canvas.setSize(800,600);
		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("hello");
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				resetCursor();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				setDefaultCursor();
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				pressingDown = true;
				canvasLogic(); // Need to make
			}


			@Override
			public void mouseReleased(MouseEvent arg0) {
				pressingDown = false;
			}
			
		});
	}
}

class Tool {
	
	public int brushSize = 5;
	
	public void setBrushSize(int s) {
		this.brushSize = s;
	}
	
}

class PenTool extends Tool {
	
}

class RollerTool extends Tool {
	
}

class BucketTool extends Tool {
	
	
}
