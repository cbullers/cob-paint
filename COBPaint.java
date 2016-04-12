package com.cob.paint;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
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
	
	List<BufferedImage> undoImages = new ArrayList<BufferedImage>();
	
	JSlider redSlider;//slider for color red
	JSlider greenSlider;//slider for color green
	JSlider blueSlider;//slider for color blue
	
	JSlider toolBrushSize;//slider for tool brush 
	JLabel toolBrushSizeLbl;
	
	JButton saveButton;//button to save your work
	JButton penButton;//button for pen
	JButton rollerButton;//button for roller
	JButton bucketButton;//button for bucket
	JButton rectangleButton;//button for rectangle
	JButton undoButton;//button for undo
	JButton eraserButton;//button for eraser
	JButton lineButton;//button for line
	JButton cursorButton;//button for cursor
	
	JPanel canvas;
	
	CursorTool cursor = new CursorTool();
	PenTool pen = new PenTool();
	RollerTool roller = new RollerTool();
	BucketTool bucket = new BucketTool();
	EraserTool eraser = new EraserTool();
	LineTool line = new LineTool();
	
	static BufferedImage canvasImage;
	Graphics2D bufferGraphics;
	
	PointerInfo p;
	Point b;
	
	int desiredBrushWidth = 10;
	int yOffsetDrawing = -75;
	
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Image penCursorImg = toolkit.getImage("../pencil.png");
	Cursor penCursor = toolkit.createCustomCursor(penCursorImg, new Point(this.getX(), this.getY()), "penCursor");
	
	Image rollerCursorImg = toolkit.getImage("../roller.png");
	Cursor rollerCursor = toolkit.createCustomCursor(rollerCursorImg, new Point(this.getX(), this.getY()), "rollerCursor");
	
	Image bucketCursorImg = toolkit.getImage("../bucket.png");
	Cursor bucketCursor = toolkit.createCustomCursor(bucketCursorImg, new Point(this.getX(), this.getY()), "bucketCursor");
	
	Image eraserImg = toolkit.getImage("../eraser.png");
	Cursor eraserCursor = toolkit.createCustomCursor(eraserImg, new Point(this.getX(), this.getY()), "eraserCursor");
	
	
	boolean triedToResizePenTool = false;
	
	//debug
	int offset = 35;
	
	boolean pressingDown = false;
	
	public Application(JApplet a) { 
		codeBase = a.getCodeBase();
		setLayout(null);
		initSliders();
		initCanvas();
		initButtons();
		addStuff();
		
		canvasImage = new BufferedImage(999,999,BufferedImage.TYPE_INT_ARGB);
		bufferGraphics = (Graphics2D)canvasImage.createGraphics();
	}
	
	private void addStuff() {
		add(saveButton);
		add(redSlider);
		add(greenSlider);//slider for green
		add(blueSlider);//slider for blue
		add(toolBrushSize);//adjusting size for brush
		add(toolBrushSizeLbl);//the label for tool brush
		add(penButton);
		add(rectangleButton);
		add(rollerButton);
		add(bucketButton);
		add(undoButton);
		add(eraserButton);
		add(cursorButton);
		add(lineButton);
		add(canvas);
	}
	
	private void repaintIt(){
		super.repaint();
	}

	public static BufferedImage getCavasImage() { 
		return canvasImage;
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
		g.drawString("RED", 850, 575);//levels for red
		g.drawString("GREEN", 890, 575);// levels for green
		g.drawString("BLUE", 950, 575);// levels for blue
		
		g.setColor(new Color(curR, curG, curB));
		g.fillRect(835,595,150,35);
		
		String hexValue = String.format("#%02X%02X%02X", curR, curG, curB);
		g.setColor(Color.black);
		g.drawString("Hex Value: " + hexValue, 850, 617);
		
		// Draw the pen tool image

		
		// Draw the roller tool image
		
	}
	

	public BufferedImage createImage(JPanel panel) {
		int w = panel.getWidth();
		int h = panel.getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		panel.print(g);
		return bi;
	}
	
	private String askForString(String message) {
		return JOptionPane.showInputDialog(message);
	}
	
	private String askForDirectory(String choosertitle) {
	    JFileChooser chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle(choosertitle);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //
	    // disable the "All files" option.
	    //
	    chooser.setAcceptAllFileFilterUsed(false);
	    //    
	    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
	      	return chooser.getSelectedFile().getAbsolutePath().toString() + "\\";
	      }
	    else {
	    	return "No Selection";
	      }
	}
	
	private void initButtons() {
		
		// Save button
		saveButton = new JButton();
		saveButton.setSize(32,32);
		saveButton.setLocation(7,7);
		try{
			URL saveUrl = new URL(codeBase, "../save.png");
			Image img = ImageIO.read(saveUrl);
			img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
			saveButton.setIcon(new ImageIcon(img));
		}catch(IOException e) {
			e.printStackTrace();
		}
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent a) {
				String directory = askForDirectory("Please go to the directory where you would like to save your drawing.");
				if(directory == "No Selection"){return;}
				String fileName = askForString("What do you want the file to be called (dont add extension)");
				fileName += ".png";
				directory += fileName;
				
				BufferedImage capture = createImage(canvas);
			    File saveOutput = new File(directory);
			    try {
					ImageIO.write(capture, "png", saveOutput);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		// Pen button
		penButton = new JButton();
		penButton.setSize(32,32);
		penButton.setLocation(7+offset,7);
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
				currentTool = pen;
			}
			
		});
		
		
		// Roller Button
		rollerButton = new JButton();
		rollerButton.setSize(32,32);
		rollerButton.setLocation(45+offset,7);
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
				currentTool = roller;
			}
			
		});
		
		// Roller Button
		bucketButton = new JButton();
		bucketButton.setSize(32,32);
		bucketButton.setLocation(85+offset,7);
		try{
			URL bucketUrl = new URL(codeBase, "../bucket.png");
			Image img = ImageIO.read(bucketUrl);
			img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
			bucketButton.setIcon(new ImageIcon(img));
		}catch(IOException e){
			e.printStackTrace();
		}
		bucketButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentTool = bucket;
			}
			
		});
		
		// Rectangle button
		rectangleButton = new JButton();
		rectangleButton.setSize(32,32);
		rectangleButton.setLocation(85+offset+45,7);
		rectangleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentTool = new RectangleTool();
			}
			
		});
		
		// Undo button
		undoButton = new JButton();
		undoButton.setSize(32,32);
		undoButton.setLocation(85+offset+90,7);
		undoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvasImage = undoImages.get(undoImages.size()-1);
				undoImages.remove(undoImages.size()-1);
				bufferGraphics = canvasImage.createGraphics();
				repaintIt();
				((Canvas) canvas).repaintCanvas();
			}
			
		});
		try{
			URL undoUrl = new URL(codeBase, "../undo.png");
			Image img = ImageIO.read(undoUrl);
			img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
			undoButton.setIcon(new ImageIcon(img));
		}catch(IOException e){
			e.printStackTrace();
		}
		
		// Eraser button
		eraserButton = new JButton();
		eraserButton.setSize(32,32);
		eraserButton.setLocation(85+offset+135,7);
		eraserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentTool = eraser;
			}
		});
		try{
			URL eraserUrl = new URL(codeBase, "../eraser.png");
			Image img = ImageIO.read(eraserUrl);
			img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
			eraserButton.setIcon(new ImageIcon(img));
		}catch(IOException e){
			e.printStackTrace();
		}
		
		// Cursor button
		cursorButton = new JButton("Cursor");
		cursorButton.setSize(32,32);
		cursorButton.setLocation(85+offset+180,7);
		cursorButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentTool = cursor;
			}
			
		});
		
		// Line tool button
		lineButton = new JButton("Line");
		lineButton.setSize(32,32);
		lineButton.setLocation(85+offset+225,7);
		lineButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentTool = line;
			}
		
		});
		
	}
	
	public void initSliders() {
		redSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
		greenSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
		blueSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
		
		toolBrushSize = new JSlider(JSlider.HORIZONTAL, 0, 200, 10);
		toolBrushSize.setSize(300,30);
		toolBrushSize.setLocation(695,10);
		toolBrushSize.setMajorTickSpacing(10);
		toolBrushSize.setMinorTickSpacing(1);
		toolBrushSize.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				//if(!(currentTool instanceof PenTool) || !(currentTool instanceof BucketTool) || !(currentTool instanceof RollerTool)) {return;}
				if(currentTool instanceof PenTool && !triedToResizePenTool) {
					JOptionPane.showMessageDialog(
						    canvas, "Cant change the pen tools brush size!",
						    "ERROR",
						    JOptionPane.ERROR_MESSAGE);
					toolBrushSize.setValue(desiredBrushWidth);
					triedToResizePenTool = true;
					return;
				}
				int value = toolBrushSize.getValue();
				desiredBrushWidth = value;
			}
			
		});
		toolBrushSizeLbl = new JLabel("Brush Size: ", JLabel.CENTER);
		toolBrushSizeLbl.setSize(75,25);
		toolBrushSizeLbl.setLocation(615, 13);
		
		redSlider.setSize(SLIDER_DIMENSION);//setting dimensions for red slider
		greenSlider.setSize(SLIDER_DIMENSION);//setting dimensions for green slider
		blueSlider.setSize(SLIDER_DIMENSION);// setting dimensions for blue slider
		
		redSlider.setLocation(850, 55);//setting location for red slider
		greenSlider.setLocation(900, 55);//setting locations for green slider 
		blueSlider.setLocation(950, 55);//setting location for blue slider
		
		redSlider.setMajorTickSpacing(25);//setting the tick spacing for red slider
		greenSlider.setMajorTickSpacing(25);//seting tick for green slider
		blueSlider.setMajorTickSpacing(25);//setting tick for blue slider
		
		redSlider.setMinorTickSpacing(5);//setting the minor tick for red
		greenSlider.setMinorTickSpacing(5);//setting the minor tick for green
		blueSlider.setMinorTickSpacing(5);//setting the minor tick spacing for blue
		
		redSlider.setPaintTicks(true);//setting the ticks for paint
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
	
	private void resetCursor() {//resetting cursor 
		if(currentTool instanceof PenTool) {
			this.setCursor(penCursor);
		}else if(currentTool instanceof RollerTool) {
			this.setCursor(rollerCursor);
		}else if(currentTool instanceof BucketTool) {
			this.setCursor(bucketCursor);
		}else if(currentTool instanceof EraserTool) {
			this.setCursor(eraserCursor);
		}
	}
	
	private void setDefaultCursor() {
		this.setCursor(Cursor.getDefaultCursor());
	}
	
	int newX, newY, oldX, oldY;
	private void canvasLogic(final int initialX, final int initialY, final Color bgColor) {

		p = MouseInfo.getPointerInfo();
		b = p.getLocation();
		newX = b.x;
		newY = b.y;
		
		Thread t = new Thread(new Runnable(){
			public void run() {
				while(pressingDown) {
					p = MouseInfo.getPointerInfo();
					b = p.getLocation();
					oldX = b.x;
					oldY = b.y;
					System.out.print("OLD: ");
					System.out.print(oldX);
					System.out.print(" , ");
					System.out.print(oldY);
					
					System.out.println();
					
					System.out.print("NEW: ");
					System.out.print(newX);
					System.out.print(" , ");
					System.out.print(newX);
					
					System.out.println();
					
					if(currentTool instanceof PenTool) {
						bufferGraphics.setColor(new Color(curR, curG, curB));
						bufferGraphics.drawLine(oldX, oldY-75, newX, newY-75);
						newX = oldX;
						newY = oldY;
						repaintIt();
					}else if(currentTool instanceof RollerTool) {
						bufferGraphics.setColor(new Color(curR, curG, curB));
						
						int dotSize = 4;
						int d = desiredBrushWidth;
						
						bufferGraphics.fillOval(b.x-(d), b.y-75, dotSize, dotSize);
						bufferGraphics.fillOval(b.x+(d), b.y-75, dotSize, dotSize);
						bufferGraphics.fillOval(b.x, b.y-75, dotSize, dotSize);
						
						// To space it out
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						repaintIt();
					}else if(currentTool instanceof RectangleTool) {
						p = MouseInfo.getPointerInfo();
						b = p.getLocation();
						
						bufferGraphics.setColor(new Color(curR, curG, curB));
						bufferGraphics.fillRect(initialX, initialY, b.x-initialX, b.y-initialY);
						
						repaintIt();
					}else if(currentTool instanceof EraserTool) {
						p = MouseInfo.getPointerInfo();
						b = p.getLocation();
						
						bufferGraphics.setColor(bgColor);
						bufferGraphics.fillOval(b.x-5, b.y-80, desiredBrushWidth, desiredBrushWidth);
						repaintIt();
					}else if(currentTool instanceof LineTool) {
//						p = MouseInfo.getPointerInfo();
//						b = p.getLocation();
//						
//						bufferGraphics.setColor(new Color(curR, curG, curB));
//						bufferGraphics.drawLine(initialX, initialY, b.x, b.y);
//						
//						bufferGraphics.setColor(bgColor);
//						bufferGraphics.drawLine(initialX, initialY, b.x, b.y);
//						
//						lineStartX = initialX;
//						lineStartY = initialY;
//						lineEndX = b.x;
//						lineEndY = b.y;
//						repaintIt();
					}
				}
				oldX = 0;
				oldY = 0;
				newX = 0;
				newY = 0;
//				if(currentTool instanceof LineTool) {
//					bufferGraphics.setColor(new Color(curR, curG, curB));
//					bufferGraphics.drawLine(lineStartX, lineStartY, lineEndX, lineEndY);
//				}
			}
		});
		t.start();
	}
	private void initCanvas() {

		canvas = new Canvas();
		canvas.setLocation(0,50);
		canvas.setSize(800,600);
		canvas.setBorder(new EmptyBorder(0, 0, 0, 0));
		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(currentTool instanceof BucketTool) {
					bufferGraphics.setColor(new Color(curR, curG, curB));
					bufferGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
					canvasImage = undoImages.get(undoImages.size()-1);
					bufferGraphics = canvasImage.createGraphics();
					((Canvas) canvas).repaintCanvas();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				resetCursor();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				setDefaultCursor();
				pressingDown = false;
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				
				undoImages.add(createImage(canvas));
				
				pressingDown = true;
				
				p = MouseInfo.getPointerInfo();
				b = p.getLocation();
				
				canvasLogic(b.x, b.y, ((Canvas) canvas).getTheBackgroundColor());
			}


			@Override
			public void mouseReleased(MouseEvent arg0) {
				pressingDown = false;
			}
			
			
		});
	}
}

class Canvas extends JPanel {
	
	
	private static final long serialVersionUID = 1L;
	private Color backgroundColor = new Color(255,255,255);

	public void setTheBackgroundColor(Color c) {
		backgroundColor = c;
	}
	
	public Color getTheBackgroundColor() {
		return backgroundColor;
	}
	
	public void repaintCanvas() {
		this.repaint();
	}
	
	Runnable r = new Runnable() {
		public void run() {
			while(true) {
				getGraphics().drawImage(Application.getCavasImage(), 0, 0, null);
			}
		}
	};
	
	Thread cool = new Thread(r);
	
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(backgroundColor);
		g.drawImage(Application.getCavasImage(),0, 0, null);
	}

	
}

class Tool {
	
	public int brushSize = 5;
	
	public void setBrushSize(int s) {
		this.brushSize = s;
	}
	
}

class PenTool extends Tool {
	
	public int brushSize = 5;
	
	@Override
	public void setBrushSize(int s) {
		this.brushSize = s;
	}
	
}

class RollerTool extends Tool {
	
	public int brushSize = 10;
	
	@Override
	public void setBrushSize(int s) {
		this.brushSize = s;
	}
	
}

class CursorTool extends Tool{}
class BucketTool extends Tool { /* I guess this class isn't really needed, but yolo */ }
class RectangleTool extends Tool {}
class LineTool extends Tool {
	public int startX, startY, endX, endY;
}
class EraserTool extends Tool {}
