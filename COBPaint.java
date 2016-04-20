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
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
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

	public Point getLocationScreen() {
		if(this.isShowing()) {
			return getLocationOnScreen();
		}else{
			return new Point(0,0);
		}
	}
	
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
	
	private static final long serialVersionUID = 1L; // It gets mad if I dont put this

	private static Dimension SLIDER_DIMENSION = new Dimension(30,500); // How big the RGB sliders are (x,y)

	private int curR = 255; // White default color
	private int curG = 255;
	private int curB = 255;
	
	List<BufferedImage> undoImages = new ArrayList<BufferedImage>(); // Store the bufferedimages for undo

	public Tool currentTool; // The selected tool
	
	static int multiToolSides; // Variables for the multi-tool
	static int multiToolRadius;

	static String waveEquation; // For the wave tool
	
	JSlider redSlider;
	JSlider greenSlider; // The sliders to select rgb color
	JSlider blueSlider;
	
	JSlider toolBrushSize;// Slider for how big you want your current tools brush size to be
	
	JLabel toolBrushSizeLbl;
	
	JButton saveButton;
	JButton penButton;
	JButton rollerButton;
	JButton bucketButton;
	JButton rectangleButton;
	JButton undoButton;			// All buttons in the toolbar
	JButton eraserButton;
	JButton lineButton;
	JButton cursorButton;
	JButton multiToolButton;
	JButton waveToolButton;
	
	static JPanel canvas; // Where to draw on
	
	CursorTool cursor = new CursorTool();
	PenTool pen = new PenTool();
	RollerTool roller = new RollerTool();
	BucketTool bucket = new BucketTool();
	EraserTool eraser = new EraserTool();	// Make new tool object to save memory
	LineTool line = new LineTool();
	RectangleTool rect = new RectangleTool();
	MultiTool multitool = new MultiTool();
	WaveTool wavetool = new WaveTool();
	
	Color backgroundColor = new Color(238,238,238); // The default background color of the canvas
	
	static BufferedImage canvasImage; // What to show on the canvas
	Graphics bufferGraphics; // Where to draw to put it on the image
	
	PointerInfo p; // For the drawing
	Point b;
	
	Point locationOnScreen; // The location of the window relative to the screen/monitor
	
	boolean isInCanvas; // Boolean to check if the mouse is in the canvas
	
	JApplet ap; // So I can get the main applet, to run tests
	
	int desiredBrushWidth = 10; // Starting brush width
	
	Toolkit toolkit = Toolkit.getDefaultToolkit(); // So I can get the images easier

	Image penCursorImg = toolkit.getImage("../pencil.png");
	Image rollerCursorImg = toolkit.getImage("../roller.png");
	Image bucketCursorImg = toolkit.getImage("../bucket.png");
	Image eraserImg = toolkit.getImage("../eraser.png");
	Image rectangleImg = toolkit.getImage("../rectangle.png");
	Image multiToolImg = toolkit.getImage("../shapes.png");
	Image lineToolImg = toolkit.getImage("../line.png");
	Image saveImg = toolkit.getImage("../save.png");
	Image rectImage = toolkit.getImage("../rectangle.png");
	Image undoImg = toolkit.getImage("../undo.png");

	Cursor eraserCursor = toolkit.createCustomCursor(eraserImg, new Point(this.getX(), this.getY()), "eraserCursor");
	Cursor bucketCursor = toolkit.createCustomCursor(bucketCursorImg, new Point(this.getX(), this.getY()), "bucketCursor");
	Cursor rollerCursor = toolkit.createCustomCursor(rollerCursorImg, new Point(this.getX(), this.getY()), "rollerCursor");
	Cursor penCursor = toolkit.createCustomCursor(penCursorImg, new Point(this.getX(), this.getY()), "penCursor");
	
	boolean triedToResizePenTool = false;
	
	//debug
	int offset = 35;
	
	boolean pressingDown = false;
	
	public Application(final JApplet a) { 
		a.getCodeBase();
		setLayout(null);
		initSliders();
		initCanvas();
		initButtons();
		addStuff();
		
		ap = a;
		
		canvasImage = new BufferedImage(999,999,BufferedImage.TYPE_INT_ARGB);
		bufferGraphics = (Graphics)canvasImage.createGraphics();
		
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
		add(multiToolButton);
		add(waveToolButton);
		//askForNumberSides();
	}
	
	static double halfPI = Math.PI/2;
	static double twoPI = Math.PI*2;
	public static void fillRegularPolygon(Graphics g, int centerX, int centerY, int radius, int sides)
	{
		int xCoord[] = new int[sides];
		int yCoord[] = new int[sides];
	 
	 	double rotate;
	    if (sides % 2 == 1)
	    	rotate = halfPI;
	    else
	    	rotate = halfPI + Math.PI/sides;
	    	
		for (int k = 0; k < sides; k++)
		{
			xCoord[k] = (int) Math.round(Math.cos(twoPI * k/sides - rotate) * radius) + centerX;
			yCoord[k] = (int) Math.round(Math.sin(twoPI * k/sides - rotate) * radius) + centerY;
		}
		g.fillPolygon(xCoord,yCoord,sides);
	}
	
	public static void askForNumberSides() {
		
		multiToolSides = Integer.parseInt( (String) JOptionPane.showInputDialog(canvas,
		        "How many sides for the multi-tool",
		        "Multi-Tool", JOptionPane.INFORMATION_MESSAGE,
		        null,
		        null,
		        "[number of sides]"));
		
	}
	
	public static void askForMultiRadius() {
		
		multiToolRadius = Integer.parseInt( (String) JOptionPane.showInputDialog(canvas,
		        "What radius for the multi-tool",
		        "Multi-Tool", JOptionPane.INFORMATION_MESSAGE,
		        null,
		        null,
		        "[radius in px]"));
		
	}
	
	public static void askForWaveEquation() {

		waveEquation = (String) JOptionPane.showInputDialog(canvas,
		        "Enter your wave equation",
		        "Wave Tool", JOptionPane.INFORMATION_MESSAGE,
		        null,
		        null,
		        "[equation using x]");
		
	}
	
	  static int eval(String infix) {        
	        ScriptEngineManager mgr = new ScriptEngineManager();
	        ScriptEngine engine = mgr.getEngineByName("JavaScript");    
	        String stringResult;
	        try {
	            stringResult = engine.eval(infix).toString();
	            double doubleResult = Double.parseDouble(stringResult);
	            int result = (int) doubleResult;        
	            return result;
	        } catch (ScriptException ex) {
	            ex.printStackTrace();
	        }
	        return(1);

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

	private JButton but(int w, int h, int x, int y, Image icon, ActionListener a, String toolTip) {
		JButton retButton = new JButton();
		retButton.setSize(w,h);
		retButton.setLocation(x,y);
		retButton.addActionListener(a);
		icon = icon.getScaledInstance(w, h, Image.SCALE_DEFAULT);
		retButton.setIcon(new ImageIcon(icon));
		retButton.setToolTipText(toolTip);
		return retButton;
	}

	// Action listeners for buttons
	ActionListener saveButtonListener = new ActionListener() {
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
	};

	ActionListener penButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent a) {
			currentTool = pen;
		}
	};

	ActionListener rollerButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentTool = roller;
		}
	};

	ActionListener bucketButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentTool = bucket;
		}
	};

	ActionListener rectangleButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentTool = rect;
		}
	};

	ActionListener eraserButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentTool = eraser;
		}
	};

	ActionListener cursorButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentTool = cursor;
		}
	};

	ActionListener lineButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentTool = line;
		}
	};

	ActionListener multiToolButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentTool = multitool;
			askForNumberSides();
			askForMultiRadius();
		}
	};

	ActionListener waveToolButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentTool = wavetool;
			askForWaveEquation();
		}
	};
	
	ActionListener undoButtonListener = new ActionListener() {
	 	@Override
	 	public void actionPerformed(ActionEvent arg0) {
	 		canvasImage = undoImages.get(undoImages.size()-1);
	 		undoImages.remove(undoImages.size()-1);
	 		bufferGraphics = canvasImage.createGraphics();
	 		repaintIt();
	 		((Canvas) canvas).repaintCanvas();
	 	}
	};
	
	private void initButtons() { // Intitialize all the toolbar buttons

		saveButton = but(32,32,7,7,saveImg,saveButtonListener,"Save to a file");
		penButton = but(32,32,7+offset,7,penCursorImg,penButtonListener,"Pen tool");
		rollerButton = but(32,32,42+offset,7,rollerCursorImg,rollerButtonListener,"Roller tool");
		bucketButton = but(32,32,77+offset,7,bucketCursorImg,bucketButtonListener,"Fill canvas");
		rectangleButton = but(32,32,85+offset+27,7,rectangleImg,rectangleButtonListener,"Rectangle tool");
		undoButton = but(32,32,85+offset+62,7,undoImg,undoButtonListener,"Undo");
		eraserButton = but(32,32,85+offset+97,7,eraserImg,eraserButtonListener,"Eraser tool");
		cursorButton = but(32,32,85+offset+132,7,eraserImg,cursorButtonListener,"Cursor tool");
		lineButton = but(32,32,85+offset+167,7,lineToolImg,lineButtonListener,"Line tool");
		multiToolButton = but(32,32,85+offset+202,7,multiToolImg,multiToolButtonListener,"Multi-tool");
		waveToolButton = but(32,32,85+offset+247,7,multiToolImg,waveToolButtonListener,"Equation tool");
		
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
		}else if(currentTool instanceof RectangleTool) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
	}
	
	private static void fillRectangle(Graphics g, int x1, int y1, int x2, int y2)
	{
		int temp;
		if (x1 > x2)
			{ temp = x1; x1 = x2; x2 = temp; }
		if (y1 > y2)
			{ temp = y1; y1 = y2; y2 = temp; }
		int width  = x2 - x1 + 1;
		int height = y2 - y1 + 1;
		g.fillRect(x1,y1,width,height);
	}
	
	private void setDefaultCursor() {
		this.setCursor(Cursor.getDefaultCursor());
	}
	
	int newX, newY, oldX, oldY;
	Rectangle rectangle;
	int lineX, lineY, lineX2, lineY2;
	int initX, initY;
	private void canvasLogic(final int initialX, final int initialY) {

		locationOnScreen = ap.getLocationOnScreen();
		
		newX = b.x - locationOnScreen.x;
		newY = b.y - locationOnScreen.y + 50;

		Thread t = new Thread(new Runnable(){
			public void run() {
				while(pressingDown) {
					
					if(!ap.isShowing()) return;
					
					p = MouseInfo.getPointerInfo();
					b = p.getLocation();
					
					b.x -= locationOnScreen.x;
					b.y -= locationOnScreen.y;
					
					//b.x-=50;
					b.y+=50;
					
					oldX = b.x;
					oldY = b.y;

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
						
						
						repaintIt();
					}else if(currentTool instanceof RectangleTool) {
						Rectangle rekt = new Rectangle(initialX, initialY, b.x-initialX, b.y-initialY-50);
						
						getGraphics().setColor(new Color(255,255,255));
						fillRectangle(getGraphics(), (int)rekt.getX(), (int)rekt.getY(), (int)(rekt.getX()+rekt.getWidth()), (int)(rekt.getY()+rekt.getHeight()));
						
						rectangle = rekt;
						
						repaintIt();
					}else if(currentTool instanceof EraserTool) {
						
						bufferGraphics.setColor(backgroundColor);
						bufferGraphics.fillOval(b.x-5, b.y-80, desiredBrushWidth, desiredBrushWidth);
						
						repaintIt();
					}else if(currentTool instanceof LineTool) {
						
						getGraphics().setColor(new Color(curR, curG, curB));
						getGraphics().drawLine(initialX, initialY, b.x, b.y-50);
						
						lineX = initialX;
						lineY = initialY-50;
						lineX2 = b.x;
						lineY2 = b.y-100;
						
						repaintIt();
					}else if(currentTool instanceof WaveTool) {
						
					}
				}
				
				if(currentTool instanceof RectangleTool) {
					
					bufferGraphics.setColor(new Color(curR, curG, curB));
					fillRectangle(bufferGraphics, (int)rectangle.getX(), (int)rectangle.getY() - 50, (int)(rectangle.getX()+rectangle.getWidth()), (int)(rectangle.getY()+rectangle.getHeight()) - 50);
					repaintIt();
					
				}else if(currentTool instanceof LineTool) {
					
					bufferGraphics.setColor(new Color(curR, curG, curB));
					bufferGraphics.drawLine(lineX, lineY, lineX2, lineY2);
					repaintIt();
					
				}
				
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
					backgroundColor = new Color(curR, curG, curB);
					bufferGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
					repaintIt();
				}else if(currentTool instanceof MultiTool) {				
					bufferGraphics.setColor(new Color(curR, curG, curB));
					fillRegularPolygon(bufferGraphics, b.x-locationOnScreen.x, b.y-75-locationOnScreen.y, multiToolRadius, multiToolSides);
					repaintIt();
				}else if(currentTool instanceof WaveTool) {
					bufferGraphics.setColor(Color.red);
					int oldX = -10000;
					int oldY = -10000;
					locationOnScreen = ap.getLocationOnScreen();
					System.out.println(locationOnScreen);
			       for(int x = -500; x < 500; x++) {
			    	   bufferGraphics.drawLine(x+(b.x), (int)(Math.pow(x,2) * (.05)), oldX+(b.x), (int)(oldY*(.05)));
			    	   oldX = x;
			    	   oldY = (int)Math.pow(x, 2);
			    	   repaintIt();
			       }
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				isInCanvas = true;
				resetCursor();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				setDefaultCursor();
				pressingDown = false;
				isInCanvas = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				undoImages.add(createImage(canvas));
				
				pressingDown = true;
				
				p = MouseInfo.getPointerInfo();
				b = p.getLocation();
				locationOnScreen = ap.getLocationOnScreen();
				
				canvasLogic(b.x-locationOnScreen.x, b.y-locationOnScreen.y);
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
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(backgroundColor);
		g.drawImage(Application.getCavasImage(),0, 0, null);
	}

	
}

class Tool {}
class PenTool extends Tool {}
class RollerTool extends Tool {}
class CursorTool extends Tool {}
class BucketTool extends Tool {}
class RectangleTool extends Tool {}
class LineTool extends Tool {}
class EraserTool extends Tool {}
class MultiTool extends Tool {}
class WaveTool extends Tool {}
