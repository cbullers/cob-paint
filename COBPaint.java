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
	
	JSlider redSlider;
	JSlider greenSlider;
	JSlider blueSlider;
	
	JSlider toolBrushSize;
	JLabel toolBrushSizeLbl;
	
	JButton saveButton;
	JButton penButton;
	JButton rollerButton;
	JButton bucketButton;
	JButton rectangleButton;
	
	JPanel canvas;
	
	PenTool pen = new PenTool();
	RollerTool roller = new RollerTool();
	BucketTool bucket = new BucketTool();
	
	static BufferedImage canvasImage;
	Graphics2D bufferGraphics;
	
	PointerInfo p;
	Point b;
	
	int desiredBrushWidth;
	
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Image penCursorImg = toolkit.getImage("../pencil.png");
	Cursor penCursor = toolkit.createCustomCursor(penCursorImg, new Point(this.getX(), this.getY()), "penCursor");
	
	Image rollerCursorImg = toolkit.getImage("../roller.png");
	Cursor rollerCursor = toolkit.createCustomCursor(rollerCursorImg, new Point(this.getX(), this.getY()), "rollerCursor");
	
	Image bucketCursorImg = toolkit.getImage("../bucket.png");
	Cursor bucketCursor = toolkit.createCustomCursor(bucketCursorImg, new Point(this.getX(), this.getY()), "bucketCursor");
	
	//debug
	int offset = 35;
	
	boolean pressingDown = false;
	
	public Application(JApplet a) { 
		codeBase = a.getCodeBase();
		setLayout(null);
		initSliders();
		initCanvas();
		initButtons();
		add(saveButton);
		add(redSlider);
		add(greenSlider);
		add(blueSlider);
		add(toolBrushSize);
		add(toolBrushSizeLbl);
		add(penButton);
		add(rectangleButton);
		add(rollerButton);
		add(bucketButton);
		add(canvas);
		
		canvasImage = new BufferedImage(999,999,BufferedImage.TYPE_INT_ARGB);
		bufferGraphics = (Graphics2D)canvasImage.createGraphics();
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
	

	public BufferedImage createImage(JPanel panel) {

		int w = panel.getWidth();
		int h = panel.getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
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
	      System.out.println("No Selection ");
	      }
	    return " ";
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
				resetCursor();
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
				resetCursor();
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
				resetCursor();
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
				int value = toolBrushSize.getValue();
				desiredBrushWidth = value;
			}
			
		});
		toolBrushSizeLbl = new JLabel("Brush Size: ", JLabel.CENTER);
		toolBrushSizeLbl.setSize(75,25);
		toolBrushSizeLbl.setLocation(615, 13);
		
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
		}else if(currentTool instanceof BucketTool) {
			this.setCursor(bucketCursor);
		}
	}
	
	private void setDefaultCursor() {
		this.setCursor(Cursor.getDefaultCursor());
	}
	
	
	private void canvasLogic(int initialX, int initialY, Color bgColor) {

		Thread t = new Thread(new Runnable(){
			public void run() {
				while(pressingDown) {
					p = MouseInfo.getPointerInfo();
					b = p.getLocation();
					if(currentTool instanceof PenTool) {
						bufferGraphics.setColor(new Color(curR, curG, curB));
						bufferGraphics.fillOval(b.x,b.y, desiredBrushWidth, desiredBrushWidth);
						repaintIt();
					}else if(currentTool instanceof RollerTool) {
						bufferGraphics.setColor(new Color(curR, curG, curB));
						
						int dotSize = 4;
						int d = desiredBrushWidth;
						
						bufferGraphics.fillOval(b.x-(d), b.y, dotSize, dotSize);
						bufferGraphics.fillOval(b.x+(d), b.y, dotSize, dotSize);
						bufferGraphics.fillOval(b.x, b.y, dotSize, dotSize);
						
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
					}
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
					((Canvas) canvas).setTheBackgroundColor(new Color(curR, curG, curB));
					repaintIt();
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
	
	Canvas() {
	}
	
	public void setTheBackgroundColor(Color c) {
		backgroundColor = c;
	}
	
	public Color getTheBackgroundColor() {
		return backgroundColor;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(backgroundColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.drawImage(Application.getCavasImage(),0, -75, null);
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

class BucketTool extends Tool { /* I guess this class isnt really needed, but yolo */ }
class RectangleTool extends Tool {}
