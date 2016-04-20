private void initButtons() {
		
		// Save button
		// saveButton = new JButton();
		// saveButton.setSize(32,32);
		// saveButton.setLocation(7,7);
		// try{
		// 	URL saveUrl = new URL(codeBase, "../save.png");
		// 	Image img = ImageIO.read(saveUrl);
		// 	img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		// 	saveButton.setIcon(new ImageIcon(img));
		// }catch(IOException e) {
		// 	e.printStackTrace();
		// }

		saveButton = but(32,32,7,7,"../save.png",saveButtonListener);
		
		// Pen button
		// penButton = new JButton();
		// penButton.setSize(32,32);
		// penButton.setLocation(7+offset,7);
		// try{
		// 	URL penUrl = new URL(codeBase, "../pencil.png");
		// 	Image img = ImageIO.read(penUrl);
		// 	img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		// 	penButton.setIcon(new ImageIcon(img));
		// }catch(IOException e) {
		// 	e.printStackTrace();
		// }
		// penButton.addActionListener(new ActionListener() {

		// 	@Override
		// 	public void actionPerformed(ActionEvent a) {
		// 		currentTool = pen;
		// 	}
			
		// });

		penButton = but(32,32,7+offset,7,"../pencil.png",penButtonListener);
		
		
		// Roller Button
		// rollerButton = new JButton();
		// rollerButton.setSize(32,32);
		// rollerButton.setLocation(42+offset,7);
		// try{
		// 	URL rollerUrl = new URL(codeBase, "../roller.png");
		// 	Image img = ImageIO.read(rollerUrl);
		// 	img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		// 	rollerButton.setIcon(new ImageIcon(img));
		// }catch(IOException e){
		// 	e.printStackTrace();
		// }
		// rollerButton.addActionListener(new ActionListener() {

		// 	@Override
		// 	public void actionPerformed(ActionEvent e) {
		// 		currentTool = roller;
		// 	}
			
		// });

		rollerButton = but(32,32,42+offset,7,"../roller.png",rollerButtonListener);
		
		// Roller Button
		// bucketButton = new JButton();
		// bucketButton.setSize(32,32);
		// bucketButton.setLocation(77+offset,7);
		// try{
		// 	URL bucketUrl = new URL(codeBase, "../bucket.png");
		// 	Image img = ImageIO.read(bucketUrl);
		// 	img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		// 	bucketButton.setIcon(new ImageIcon(img));
		// }catch(IOException e){
		// 	e.printStackTrace();
		// }
		// bucketButton.addActionListener(new ActionListener() {

		// 	@Override
		// 	public void actionPerformed(ActionEvent e) {
		// 		currentTool = bucket;
		// 	}
			
		// });
		bucketButton = but(32,32,77+offset,7,"../bucket.png",bucketButtonListener);
		
		// Rectangle button
		// rectangleButton = new JButton();
		// rectangleButton.setSize(32,32);
		// rectangleButton.setLocation(85+offset+27,7);
		// rectangleButton.addActionListener(new ActionListener() {

		// 	@Override
		// 	public void actionPerformed(ActionEvent arg0) {
		// 		currentTool = new RectangleTool();
		// 		resetCursor();
		// 	}
			
		// });
		// try{
		// 	URL rectangleUrl = new URL(codeBase, "../rectangle.png");
		// 	Image img = ImageIO.read(rectangleUrl);
		// 	img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		// 	rectangleButton.setIcon(new ImageIcon(img));
		// }catch(IOException e){
		// 	e.printStackTrace();
		// }
		rectangleButton = but(32,32,85+offset+27,7,"../rectangle.png",rectangleButtonListener);
		
		// Undo button
		// undoButton = new JButton();
		// undoButton.setSize(32,32);
		// undoButton.setLocation(85+offset+62,7);
		// undoButton.addActionListener(new ActionListener() {

		// 	@Override
		// 	public void actionPerformed(ActionEvent arg0) {
		// 		canvasImage = undoImages.get(undoImages.size()-1);
		// 		undoImages.remove(undoImages.size()-1);
		// 		bufferGraphics = canvasImage.createGraphics();
		// 		repaintIt();
		// 		((Canvas) canvas).repaintCanvas();
		// 	}
			
		// });
		// try{
		// 	URL undoUrl = new URL(codeBase, "../undo.png");
		// 	Image img = ImageIO.read(undoUrl);
		// 	img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		// 	undoButton.setIcon(new ImageIcon(img));
		// }catch(IOException e){
		// 	e.printStackTrace();
		// }
		undoButton = but(32,32,85+offset+62,7,"../undo.png",undoButtonListener);
		
		// Eraser button
		// eraserButton = new JButton();
		// eraserButton.setSize(32,32);
		// eraserButton.setLocation(85+offset+97,7);
		// eraserButton.addActionListener(new ActionListener() {
		// 	@Override
		// 	public void actionPerformed(ActionEvent e) {
		// 		currentTool = eraser;
		// 	}
		// });
		// try{
		// 	URL eraserUrl = new URL(codeBase, "../eraser.png");
		// 	Image img = ImageIO.read(eraserUrl);
		// 	img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		// 	eraserButton.setIcon(new ImageIcon(img));
		// }catch(IOException e){
		// 	e.printStackTrace();
		// }
		eraserButton = but(32,32,85+offset+92,7,"../eraser.png",eraserButtonListener);
		
		// Cursor button
		// cursorButton = new JButton("Cursor");
		// cursorButton.setSize(32,32);
		// cursorButton.setLocation(85+offset+132,7);
		// cursorButton.addActionListener(new ActionListener() {

		// 	@Override
		// 	public void actionPerformed(ActionEvent arg0) {
		// 		currentTool = cursor;
		// 	}
			
		// });
		cursorButton = but(32,32,85+offset+132,7,"../eraser.png",cursorButtonListener);
		
		// Line tool button
		// lineButton = new JButton("Line");
		// lineButton.setSize(32,32);
		// lineButton.setLocation(85+offset+167,7);
		// lineButton.addActionListener(new ActionListener() {

		// 	@Override
		// 	public void actionPerformed(ActionEvent arg0) {
		// 		currentTool = line;
		// 	}
		
		// });
		// try{
		// 	URL u = new URL(codeBase, "../line.png");
		// 	Image img = ImageIO.read(u);
		// 	img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		// 	lineButton.setIcon(new ImageIcon(img));
		// }catch(IOException e) {
		// 	e.printStackTrace();
		// }
		lineButton = but(32,32,85+offset+167,7,"../line.png",lineButtonListener);
		
		// multiToolButton = new JButton();
		// multiToolButton.setSize(32,32);
		// multiToolButton.setLocation(85+offset+202,7);
		// multiToolButton.addActionListener(new ActionListener() {
			
		// 	@Override
		// 	public void actionPerformed(ActionEvent e) {
		// 		currentTool = new MultiTool();
		// 		askForNumberSides();
		// 		askForMultiRadius();
		// 	}
		// });
		// try{
		// 	URL u = new URL(codeBase, "../shapes.png");
		// 	Image img = ImageIO.read(u);
		// 	img = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
		// 	multiToolButton.setIcon(new ImageIcon(img));
		// }catch(IOException e){
		// 	e.printStackTrace();
		// }
		multiToolButton = but(32,32,85+offset+202,7,"../shapes.png",multiToolButtonListener);
		
		// waveToolButton = new JButton();
		// waveToolButton.setSize(32,32);
		// waveToolButton.setLocation(85+offset+247,7);
		// waveToolButton.addActionListener(new ActionListener() {
			
		// 	@Override
		// 	public void actionPerformed(ActionEvent e) {
		// 		currentTool = new WaveTool();
		// 		askForWaveEquation();
		// 	}
		// });
		waveToolButton = but(32,32,85+offset+247,7,"../shapes.png",waveToolButtonListener);
		
	}
