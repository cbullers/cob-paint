package com.cbullers.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.cbullers.config.Config;

public class SplashScreen extends JFrame {
	
	// Some refrence variables
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	// The content
	SplashScreenContent content = new SplashScreenContent();
	
	// Reference from inside timers, etc
	SplashScreen pThis = this;
	
	
	// Timer, and timerlistener
	Timer fadeInTimer;
	ActionListener fadeInListener;

	// Required fonts for the program
	List<String> requiredFonts = new ArrayList<String>();
	
	// Constructor
	public SplashScreen() {
		
		// Move stuffs
		this.setLayout(null);
		
		// So it doesnt like "flash"
		this.getContentPane().setBackground(new Color(0,0,0,0));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		content.setBackground(Config.SPLASH_BACKGROUND);
		//setContentPane(content);
		setBounds(screenSize.width/2-200,screenSize.height/2-100,400,200);
		
		setUndecorated(true);
		//this.setBackground(Config.SPLASH_BACKGROUND);
	
		// Initialize fonts
		initFonts();
		
		// Initialize the listener
		initFadeInListener();
		
		// Cool fade in effect
		fadeIn();

		// Set the normal background
		this.getContentPane().setBackground(Config.SPLASH_BACKGROUND);
		
	}
	
	private void initFadeInListener() {
		fadeInListener = new ActionListener() {
			int alpha = 0;
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(alpha >= 250) {
					pThis.setBackground(Config.SPLASH_BACKGROUND);
					fadeInTimer.stop();
					// Show
					pThis.setContentPane(content);
				}else{
					alpha+=10;
					pThis.setBackground(new Color(Config.SPLASH_BACKGROUND.getRed(), Config.SPLASH_BACKGROUND.getGreen(), Config.SPLASH_BACKGROUND.getBlue() ,alpha));
					try{
						Thread.sleep(5);
					}catch(InterruptedException ie) {
						ie.printStackTrace();
					}
				}
			}
		};
	}
	
	private void initFonts() {
		
		// Add fonts to required
		requiredFonts.add("Roboto");
		
		
		// Check it out
		for(String s : requiredFonts) {
			if(!fontExists(s)) {
				System.err.println("ERROR: FONT " + s + " WAS NOT DETECTED ON YOUR SYSTEM, PLEASE INSTALL!");
			}
		}
		
	}
	
	private boolean fontExists(String name) {
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for(String s : fonts) {
			if(s.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	// Variables
	
	// Serial Version
	public static final long serialVersionUID = 1L;
	
	// Override default paint component
	
	// Just for debugging
	public static void main(String... args) {
		new SplashScreen().setVisible(true);
	}
	
	// Fade in method
	public void fadeIn() {
		fadeInTimer = new Timer(30, fadeInListener);
		fadeInTimer.start();
	}
}

// Dont add this anywhere else pls
class SplashScreenContent extends JPanel {

	// Serial id
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public SplashScreenContent() {
		// Move stuffs
		this.setLayout(null);
		
		// Set size
		this.setBounds(0,0,400,200);
		
		// Add stuff
		add(new SplashCloseButton());
		
		// LABEL
		JLabel welcome = new JLabel("Welcome to " + Config.APP_NAME + "!");
		welcome.setBounds(25, 0, 1000, 50);
		welcome.setFont(new Font("Verdana", Font.PLAIN, 24));
		add(welcome);
		
		// LINE
		SplashScreenLine line = new SplashScreenLine(100, 20, 100);
		//add(line);
		
		JButton debug = new JButton("TEST");
		debug.setBounds(50,100,100,25);
		debug.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				line.actiavte();

			}
			
		});
		//add(debug);
		
		JButton onePlayer = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage("oneplayer.png").getScaledInstance(100, 100, Image.SCALE_FAST)));
		JButton twoPlayer = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage("twoplayer.png").getScaledInstance(100, 100, Image.SCALE_FAST)));
		
		onePlayer.setBounds(10+75,65,100,100);
		twoPlayer.setBounds(120+75,65,100,100);
		
		onePlayer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Config.gameType = 0;
			}
			
		});
		
		twoPlayer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Config.gameType = 1;
				
			}
			
		});
		
		add(onePlayer);
		add(twoPlayer);
	
	}
	
}

class SplashCloseButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	
	
	public SplashCloseButton() {
		super(new ImageIcon(Toolkit.getDefaultToolkit().getImage("close.png").getScaledInstance(32, 32, Image.SCALE_FAST)));
		this.setBounds(335,-5,64,64);
		this.setBackground(new Color(0,0,0,0));
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);			// So it isnt drawn on the back
		this.setFocusPainted(false);
		this.setOpaque(false);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
	}
	
	
}


class SplashScreenLine extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// like when you do the action it moves and stuff

	// timer
	Timer lineTimer;
	ActionListener lineTimerList;
	Timer deactivateTimer;

	int move = 5;
	
	int wantedWidth;
	
	// just pls
	SplashScreenLine pThis = this;
	
	public SplashScreenLine(int x, int y, int width) {
		this.setLocation(x,y);
		this.setSize(10,3);
		this.wantedWidth = width;
		this.setBackground(Color.black);
		lineTimerList = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(pThis.getWidth() >= pThis.wantedWidth) {
					lineTimer.stop();
					return;
				}
				
				//pThis.setLocation(pThis.getX()-move, pThis.getY());
				pThis.setSize((pThis.getWidth()+move), pThis.getHeight());
			}
		};
		
		lineTimer = new Timer(10, lineTimerList);
	}
	
	public void actiavte() {
		lineTimer.start();
	}
	
	public void deactivate() {
		deactivateTimer = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(pThis.getWidth() <= 10) deactivateTimer.stop();
				
				pThis.setSize(pThis.getWidth()-move, pThis.getHeight());
			}
		});
		
		deactivateTimer.start();
	}
	
}