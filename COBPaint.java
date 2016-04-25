package com.cob.paint;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class COBPaint extends JFrame {
	
	private static final long serialVersionUID = 1L;

	public COBPaint() {
		super("COBPaint");
		setSize(1015,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		final Application app = new Application(this);
		setContentPane(app);
		repaint();
	}
	
	public static void main(String[] args) {

		new COBPaint();
		
	}
	
}

class Application extends JPanel {
	
	private static final long serialVersionUID = 1L; // It gets mad if I dont put this

	private static Dimension SLIDER_DIMENSION = new Dimension(30,500); // How big the RGB sliders are (w,h)

	private int curR = 255; // White default color
	private int curG = 255;
	private int curB = 255;
	
	List<BufferedImage> undoImages = new ArrayList<BufferedImage>(); // Store the bufferedimages for undo

	public Tool currentTool; // The selected tool
	
	static int multiToolSides; // Variables for the multi-tool
	static int multiToolRadius;
	
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
	JButton textToolButton;
	JButton fountainPenButton;
	JButton openButton;
	JButton friendDraw;
	
	static JPanel canvas; // Where to draw on
	
	PenTool pen = new PenTool();
	RollerTool roller = new RollerTool();
	BucketTool bucket = new BucketTool();
	EraserTool eraser = new EraserTool();	// Make new tool object to save memory
	LineTool line = new LineTool();
	RectangleTool rect = new RectangleTool();
	MultiTool multitool = new MultiTool();
	TextTool text = new TextTool();
	FountainPen fountain = new FountainPen();
	OpenTool open = new OpenTool();
	
	Color backgroundColor = new Color(238,238,238); // The default background color of the canvas
	
	static BufferedImage canvasImage; // What to show on the canvas
	Graphics bufferGraphics; // Where to draw to put it on the image
	
	PointerInfo p; // For the drawing
	Point b;
	
	Point locationOnScreen; // The location of the window relative to the screen/monitor
	
	JFrame ap; // So I can get the main frame, to run tests
	
	int desiredBrushWidth = 10; // Starting brush width
	
	Toolkit toolkit = Toolkit.getDefaultToolkit(); // So I can get the images easier

	Image openToolImg;
	
	Image penCursorImg = toolkit.getImage("pencil.png");
	Image rollerCursorImg = toolkit.getImage("roller.png");
	Image bucketCursorImg = toolkit.getImage("bucket.png");
	Image eraserImg = toolkit.getImage("eraser.png");
	Image rectangleImg = toolkit.getImage("rectangle.png");
	Image multiToolImg = toolkit.getImage("shapes.png");
	Image lineToolImg = toolkit.getImage("line.png");
	Image saveImg = toolkit.getImage("save.png");
	Image rectImage = toolkit.getImage("rectangle.png");
	Image undoImg = toolkit.getImage("undo.png");
	Image textImg = toolkit.getImage("text.png");
	Image fountainImg = toolkit.getImage(".fountain.png");
	Image openImg = toolkit.getImage("open.png");

	Cursor eraserCursor = toolkit.createCustomCursor(eraserImg, new Point(this.getX(), this.getY()), "eraserCursor");
	Cursor bucketCursor = toolkit.createCustomCursor(bucketCursorImg, new Point(this.getX(), this.getY()), "bucketCursor");
	Cursor rollerCursor = toolkit.createCustomCursor(rollerCursorImg, new Point(this.getX(), this.getY()), "rollerCursor");
	Cursor penCursor = toolkit.createCustomCursor(penCursorImg, new Point(this.getX(), this.getY()), "penCursor");
	Cursor fountainCursor = toolkit.createCustomCursor(fountainImg, new Point(this.getX(), this.getY()), "fountainCursor");
	
	boolean triedToResizePenTool = false;
	
	// Drawing with friends variables
	boolean drawingWithFriend;
	String friendIP;
	ServerSocket serverSocket;
	Socket socket; // Just for listening purposes
	Socket friend; // This will be where data will be sent to and fro
	int DEFAULT_PORT = 34125;
	ObjectOutputStream out; // When we want to send to our friend
	ObjectInputStream in; // When we receive from our friend
	
	private void defineServerSocket() {
		try{
			serverSocket = new ServerSocket(DEFAULT_PORT);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void listenForIncomingConnections() {
		new Thread(new Runnable() {
			public void run() {
				while(true) {
					try {
						socket = serverSocket.accept();
						if(socket==null) continue;
						if(askForString("INCOMING REQUEST TO PAINT FROM " + socket.getInetAddress().toString().substring(1) + " IF YOU WISH TO PAINT TYPE YES, IF NOT, TYPE NO") == "NO") continue;
						friend = socket;
						out = new ObjectOutputStream(friend.getOutputStream());
						in = new ObjectInputStream(friend.getInputStream());
						drawingWithFriend = true;
						break;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	int testNewX, testNewY;
	private void listenForFriendDrawing() throws EOFException {
		new Thread(new Runnable() {
			public void run() {
				
				while(true) {
					if(!drawingWithFriend || in == null || out == null) continue;
					DrawingInformation d = null;
					
					try {
						if(null != in.readObject()) {
							d = (DrawingInformation)in.readObject();
							if(d.currentTool instanceof PenTool) {
								bufferGraphics.setColor(d.color);
								bufferGraphics.drawLine(d.oldX, d.oldY-75, testNewX, testNewY-75);
								testNewX=d.oldX;
								testNewY=d.oldY;
								repaintIt();
							}
						}
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
					
				}
			}
		}).start();
	}
	
	//debug
	int offset = 35;
	
	boolean pressingDown = false;
	
	public Application(final JFrame a) {
		setSize(1000,650);
		setLayout(null);
		initSliders();
		initCanvas();
		initButtons();
		addStuff();
		setDoubleBuffered(false);
		
		ap = a;
		
		canvasImage = new BufferedImage(999,999,BufferedImage.TYPE_INT_ARGB);
		bufferGraphics = (Graphics)canvasImage.createGraphics();
		
		defineServerSocket();
		listenForIncomingConnections();
		try {
			listenForFriendDrawing();
		} catch (EOFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		add(lineButton);
		add(canvas);
		add(multiToolButton);
		add(textToolButton);
		add(fountainPenButton);
		add(openButton);
		add(friendDraw);
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
	
	private int askForInt(String message) {
		return Integer.parseInt(askForString(message));
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
	
	private String askForFile(String title) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath().toString();
		}else{
			return "No Selection";
		}
	}
	
	private void askForFriendsIP() {
		friendIP = askForString("Your friend's IP or hostname");
	}
	
	private void askForImage() {
		String file = askForFile("What file?");
		if(file.equals("No Selection")) return;
		openToolImg = toolkit.getImage(file);
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

	
	ActionListener undoButtonListener = new ActionListener() {
	 	@Override
	 	public void actionPerformed(ActionEvent arg0) {
	 		if(undoImages.get(undoImages.size()-1) == null) return;
	 		canvasImage = undoImages.get(undoImages.size()-1);
	 		undoImages.remove(undoImages.size()-1);
	 		bufferGraphics = canvasImage.createGraphics();
	 		repaintIt();
	 		((Canvas) canvas).repaintCanvas();
	 	}
	};
	
	ActionListener textToolListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentTool = text;
		}
	};
	
	ActionListener fountainListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			currentTool = fountain;
		}
	};
	
	ActionListener openListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			currentTool = open;
			askForImage();
		}
	};
	
	ActionListener friendDrawListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			askForFriendsIP();
			try {
				friend = new Socket(friendIP, DEFAULT_PORT);
				out = new ObjectOutputStream(friend.getOutputStream());
				in = new ObjectInputStream(friend.getInputStream());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
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
		lineButton = but(32,32,85+offset+132,7,lineToolImg,lineButtonListener,"Line tool");
		multiToolButton = but(32,32,85+offset+167,7,multiToolImg,multiToolButtonListener,"Multi-tool");
		textToolButton = but(32,32,85+offset+202,7,textImg,textToolListener,"Text tool");
		fountainPenButton = but(32,32,85+offset+237,7,fountainImg,fountainListener,"Fountain pen");
		openButton = but(32,32,85+offset+272,7,openImg,openListener,"Open image");
		friendDraw = but(32,32,85+offset+304,7,openImg,friendDrawListener,"Draw with friends!");
		
	}
	

	ChangeListener toolBrushSizeListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
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
	};
	
	ChangeListener redSliderListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			curR = redSlider.getValue();
			repaintIt();
			redSlider.setToolTipText("Red Value: " + curR);
		}
	};
	
	ChangeListener greenSliderListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			curG = greenSlider.getValue();
			repaintIt();
			greenSlider.setToolTipText("Green Value: " + curG);
		}
	};
	
	ChangeListener blueSliderListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			curB = blueSlider.getValue();
			repaintIt();
			blueSlider.setToolTipText("Blue Value: " + curB);
		}
	};
	
	private JSlider slider(int type, int a, int b, int c, int x, int y, int w, int h, ChangeListener act) {
		
		JSlider retSlider = new JSlider(type,a,b,c);
		retSlider.setSize(w,h);
		retSlider.setLocation(x,y);
		retSlider.setMajorTickSpacing(10);
		retSlider.setMinorTickSpacing(1);
		retSlider.addChangeListener(act);
		return retSlider;
		
	}
	
	private JLabel label(String title, int type, int x, int y, int w, int h) {
		JLabel retLabel = new JLabel(title, type);
		retLabel.setSize(w,h);
		retLabel.setLocation(x,y);
		return retLabel;
	}

	public void initSliders() {
		toolBrushSize = slider(JSlider.HORIZONTAL, 0, 200, 10, 695, 10, 300, 30, toolBrushSizeListener);
		redSlider = slider(JSlider.VERTICAL, 0, 255, 255, 850, 55, SLIDER_DIMENSION.width, SLIDER_DIMENSION.height, redSliderListener);
		greenSlider = slider(JSlider.VERTICAL, 0, 255, 255, 900, 55, SLIDER_DIMENSION.width, SLIDER_DIMENSION.height, greenSliderListener);
		blueSlider = slider(JSlider.VERTICAL, 0, 255, 255, 950, 55, SLIDER_DIMENSION.width, SLIDER_DIMENSION.height, blueSliderListener);
		
		toolBrushSizeLbl = label("Brush Size: ", JLabel.CENTER, 615, 13, 75, 25);
		
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
		}else if(currentTool instanceof TextTool) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		}else if(currentTool instanceof FountainPen) {
			this.setCursor(fountainCursor);
		}else if(currentTool instanceof OpenTool) {
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
	
	private static void fillImageRect(Graphics g, Image i,int x1, int y1, int x2, int y2) {
		// Resize the image
		//i = i.getScaledInstance(x1+x2, y1+y2, Image.SCALE_DEFAULT);
		
		int temp;
		if (x1 > x2)
			{ temp = x1; x1 = x2; x2 = temp; }
		if (y1 > y2)
			{ temp = y1; y1 = y2; y2 = temp; }
		int width  = x2 - x1 + 1;
		int height = y2 - y1 + 1;
		g.drawImage(i,x1,y1,width,height,null);
	}
	
	private void setDefaultCursor() {
		this.setCursor(Cursor.getDefaultCursor());
	}
	
	int newX, newY, oldX, oldY;
	Rectangle rectangle;
	Rectangle openedImage;
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
					
					if(drawingWithFriend) {
						try {
							out.writeObject(new DrawingInformation(currentTool, initialX, initialY, oldX, oldY, b.x, b.y, new Color(curR, curG, curB)));
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}

					if(currentTool instanceof PenTool) {
						
						bufferGraphics.setColor(new Color(curR, curG, curB));
						bufferGraphics.drawLine(oldX, oldY-75, newX, newY-75);
						//bufferGraphics.fillOval(b.x, b.y-75, desiredBrushWidth, desiredBrushWidth);
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
					}else if(currentTool instanceof FountainPen) {
						
						bufferGraphics.setColor(new Color(curR, curG, curB));
						//bufferGraphics.fillRect(oldX, oldY-75, desiredBrushWidth, desiredBrushWidth);
						bufferGraphics.drawLine(oldX+20,(oldY+15)-75,newX-20,(newY-15)-75);
						newX = oldX;
						newY = oldY;
						repaintIt();
						
					}else if(currentTool instanceof OpenTool) {
						if(openImg == null) return;
						fillImageRect(getGraphics(), openToolImg, initialX, initialY, b.x, b.y-75);
						openedImage = new Rectangle(initialX, initialY, b.x, b.y-75);
						repaintIt();
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
					
				}else if(currentTool instanceof OpenTool) {
					if(openedImage == null) return;
					fillImageRect(bufferGraphics, openToolImg, openedImage.x+50, openedImage.y-50, openedImage.width, openedImage.height);
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
				}else if(currentTool instanceof TextTool) {
					String txt = askForString("What do you want it to say?");
					if(txt == null) return;
					int size = askForInt("How large should the text be?");
					
					
					Font toUse = new Font("Trebuchet MS", Font.PLAIN, size);
					bufferGraphics.setFont(toUse);
					bufferGraphics.setColor(new Color(curR, curG, curB));
					bufferGraphics.drawString(txt, b.x, b.y-75);
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
	
	public Canvas() {
		setDoubleBuffered(false);
	}

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

class Tool implements Serializable {private static final long serialVersionUID = 1L;}
class PenTool extends Tool {private static final long serialVersionUID = 1L;}
class FountainPen extends Tool {private static final long serialVersionUID = 1L;}
class RollerTool extends Tool {private static final long serialVersionUID = 1L;}
class BucketTool extends Tool {private static final long serialVersionUID = 1L;}
class RectangleTool extends Tool {private static final long serialVersionUID = 1L;}
class LineTool extends Tool {private static final long serialVersionUID = 1L;}
class EraserTool extends Tool {private static final long serialVersionUID = 1L;}
class MultiTool extends Tool {private static final long serialVersionUID = 1L;}
class TextTool extends Tool {private static final long serialVersionUID = 1L;}
class OpenTool extends Tool {private static final long serialVersionUID = 1L;}

class DrawingInformation implements Serializable {

	/*
	 * This class will be sent to the ServerSocket, or the socket, depending
	 * on the connection.
	 */
	
	private static final long serialVersionUID = 1L;
	
	Tool currentTool;
	int initialX, initialY, mouseX, mouseY, oldX, oldY;
	Color color;
	
	public DrawingInformation(Tool currentTool, int initialX, int initialY, int oldX, int oldY, int mouseX, int mouseY, Color color) {
		this.currentTool = currentTool;
		this.initialX = initialX;
		this.initialY = initialY;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.color = color;
		this.oldX = oldX;
		this.oldY = oldY;
	}
	
}
