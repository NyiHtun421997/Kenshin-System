package com.system.kenshin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

public class InputScreen {
	
	String buildingName;
	String dateLabel;
	List<String> floor;
	
	public InputScreen(String buildingName, String dateLabel, List<String> floor) {
		
	  this.buildingName = buildingName;
	  this.dateLabel = dateLabel;
	  this.floor = floor;
	  
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
			try {
				ReadingOperation operationForAE = new Operation();	
				InputScreenFrame f = new InputScreenFrame(operationForAE,buildingName,dateLabel,floor);
				f.setSize(1200,800);
				f.setVisible(true);
				f.setResizable(false);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
} 
			catch (Exception e) {
					e.printStackTrace();

}}});}

	public static void main(String[] args) {
		//these will be inside main menu which will call constructor of InputScreen
		String buildingName = "BRAVI北浜";
		String dateLabel = "2023年11月";//must follow this ○年○月 pattern
		//will be populated from server
		List<String> floor = new ArrayList<String>(List.of("駐車場","1F","2F","3F"));
		
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
			try {
				ReadingOperation operationForAE = new Operation();	
				InputScreenFrame f = new InputScreenFrame(operationForAE,buildingName,dateLabel,floor);;
				f.setSize(1200,800);
				f.setVisible(true);
				f.setResizable(false);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
} 
			catch (Exception e) {
					e.printStackTrace();

}}});}}
class InputScreenFrame extends JFrame implements ItemListener,ActionListener,CallBack{
	
	JPanel topPanel,bottomPanel1,bottomPanel2,mainBottomPanel,imageCboxPanel;
	JButton b2,b3,b4,b5,b6,b7;
	JLabel b1,l1,l2,photo,cboxLabel;
	JComboBox cb;
	JFormattedTextField tf;
	JCheckBox cbox;
	CardLayout cl;
	String buildingName;
	String dateLabel;
	List<String> floor;
	
	String unitType [] = {"電灯","動力","水道","ガス"};
	private ReadingOperation operationForAE;
	private HttpService httpService = new HttpService();
	
	static int floorIndex = 0;
	
	InputScreenFrame(ReadingOperation operationForAE,String buildingName, String dateLabel, List<String> floor){
		super("Input Menu");
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		  
		this.operationForAE = operationForAE;
		this.buildingName = buildingName;
		this.dateLabel = dateLabel;
		this.floor = floor;
		//once the input menu is opened,operation will be constructed
		operationForAE.startOperation(buildingName, dateLabel, floor);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveMenu = new JMenuItem("Save");
		//Action to save all the obj inside HashMap to TempMap inside Server
		saveMenu.addActionListener((ActionEvent ae)->{
			
			httpService.storeToTempMap(operationForAE.getAllReadings());
		});
		
		fileMenu.add(saveMenu);
		menuBar.add(fileMenu);
		menuBar.setBackground(Color.blue);
		
		setJMenuBar(menuBar);
		
		//Label for buildings
		b1 = new JLabel(buildingName);
		b1.setFont(new Font("Ariel",Font.BOLD,18));
		b1.setBounds(400,5,200,40);
		
		//Button for Floors
		b2 = new JButton(floor.get(floorIndex));
		b2.setBounds(540,45,120,80);
		b2.setHorizontalAlignment(SwingConstants.CENTER);
		b2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		b2.setBackground(new Color(237, 244, 255));
		ImageIcon floorIcon = new ImageIcon(rescaleImage2("resources/images/floor_icon.png",b2));
		b2.setIcon(floorIcon);
		b2.setVerticalTextPosition(JButton.BOTTOM);
		b2.setHorizontalTextPosition(JButton.CENTER);
		b2.addActionListener(this);
		
		//Label for date
		l1 = new JLabel(dateLabel);
		l1.setFont(new Font("Ariel",Font.BOLD,18));
		l1.setBounds(700,5,200,40);
		
		//Label for 種別
		l2 = new JLabel("種別");
		l2.setBounds(310,130,180,50);
		l2.setFont(new Font("Ariel",Font.BOLD,18));
		
		//ComboBox for 系統
		cb = new JComboBox(unitType);
		cb.setBounds(280,160,100,50);
		cb.addActionListener(this);
		
		//TextField for unit input
		NumberFormat nf = DecimalFormat.getInstance(Locale.US);
        nf.setMaximumFractionDigits(2); // Set the maximum number of decimal places
        nf.setGroupingUsed(false);//disabling comma thousand separator

		NumberFormatter nft = new NumberFormatter(nf);
		tf = new JFormattedTextField(nft);
		tf.setValue(0.0);
		tf.setColumns(20);
		tf.setBounds(410,130,380,80);
		tf.setHorizontalAlignment(JTextField.CENTER);
		tf.setFont(new Font("Ariel",Font.BOLD,18));
		//Making sure only decimal values or numbers are entered
		tf.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt) {
				char c = evt.getKeyChar();
				if(Character.isLetter(c)) evt.consume();
				else {
					String currentText = tf.getText().replace(",", "");
		            String newText = currentText + evt.getKeyChar();
					try{	
						Double.parseDouble(newText);
					}
					catch(	NumberFormatException e) { evt.consume();}
}}});
		//Buttons for increment/decrement of floors
		b3 = new JButton("UP");
		b3.setBounds(800,130,100,40);
		b3.setBackground(Color.blue);//need fix
		b3.addActionListener(this);
		
		b4 = new JButton("DOWN");
		b4.setBounds(800,170,100,40);
		b4.setBackground(Color.blue);//need fix
		b4.addActionListener(this);
		
		//Panel for above components
		topPanel = new JPanel();
		Dimension panelSize = new Dimension(1200,250);
		topPanel.setPreferredSize(panelSize);
		topPanel.setBackground(new Color(237, 244, 255));
		topPanel.add(b1);
		topPanel.add(b2);
		topPanel.add(b3);
		topPanel.add(b4);
		topPanel.add(cb);
		topPanel.add(tf);
		topPanel.add(l1);
		topPanel.add(l2);
		
		topPanel.setLayout(null);
		
		//For Photo
		photo = new JLabel();
		photo.setBounds(400,55,400,360);
		photo.setHorizontalAlignment(SwingConstants.CENTER);
		//Get resized image as return value by calling customized rescaleImage()method and pass it as arg to icon
		//rescaleImage("resources/images/icon2.png",photo);
		ImageIcon imageIcon = new ImageIcon(rescaleImage("resources/images/icon2.png",photo));
		photo.setIcon(imageIcon);
		
		//CheckBox for whether upload image or not
		cbox = new JCheckBox("Upload Image",false);
		
		cbox.addItemListener(this);
		
		//Panel for CheckBox and its Label
		imageCboxPanel = new JPanel();
		imageCboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		imageCboxPanel.setBackground(new Color(237, 244, 255));
		imageCboxPanel.add(cbox);
		topPanel.add(imageCboxPanel).setBounds(530,220,400,40);
		contentPane.add(topPanel,BorderLayout.NORTH);
		
		//Button for Confirm(for bottomPanel1)
		b5 = new JButton("Confirm");
		b5.setHorizontalAlignment(SwingConstants.CENTER);
		//Button for Confirm(for bottomPanel2)
		b6 = new JButton("Confirm");
		b6.setHorizontalAlignment(SwingConstants.CENTER);
		b5.addActionListener(this);
		b6.addActionListener(this);
		
		//Button for Upload Image
		b7 = new JButton("Upload");
		b7.setHorizontalAlignment(SwingConstants.CENTER);
		
		//Panel for image,"confirm" button,"upload" button
		bottomPanel1 = new JPanel();
		bottomPanel1.setLayout(null);
		bottomPanel1.add(b5).setBounds(510,100,180,50);
		bottomPanel1.setBackground(new Color(237, 244, 255));
		
		bottomPanel2 = new JPanel();
		bottomPanel2.setLayout(null);
		bottomPanel2.add(photo);
		bottomPanel2.add(b6).setBounds(510,415,180,50);
		bottomPanel2.add(b7).setBounds(510,5,180,50);
		bottomPanel2.setBackground(new Color(237, 244, 255));
		
		//Will use card layout to flip between bottomPanel 1 and 2
		mainBottomPanel = new JPanel();
		cl = new CardLayout();
		mainBottomPanel.setLayout(cl);
		mainBottomPanel.add("first",bottomPanel1);
		mainBottomPanel.add("second",bottomPanel2);
		mainBottomPanel.setBackground(new Color(237, 244, 255));
		cl.show(mainBottomPanel, "first");
		
		contentPane.add(mainBottomPanel,BorderLayout.CENTER);
		
		
	}
	@Override
	public void itemStateChanged(ItemEvent ie) {
		if(!cbox.isSelected()) {
			cl.show(mainBottomPanel, "first");
		}
		if(cbox.isSelected()) { 
			cl.show(mainBottomPanel, "second");
		}
		
	}
	@Override
	public void actionPerformed(ActionEvent ae) {

		String buildingName = b1.getText();
		String floorName = b2.getText();
		String date = l1.getText();
		String reading = tf.getText();
		//If floor button is pressed
		if(ae.getSource()==b2) {
			FloorMenu FM01 = new FloorMenu(floor,this,b2);
			//this = an instance of input screen who is observer and will be observing it's subject,floorMenu
			
		}
		//if combobox is changed,text field will be refreshed
		if(ae.getSource()==cb) {
			Double readingDouble = operationForAE.getReading(cb.getSelectedIndex(), floorName);
			if(readingDouble!=null) {
				reading = readingDouble.toString();
				tf.setText(reading);
			}
			else tf.setText("");
		}
		//if up button is pressed
		if(ae.getSource()==b3) {
			if(floorIndex<floor.size()-1) {
				floorIndex++;
				b2.setText(floor.get(floorIndex));
				refreshPage(floor.get(floorIndex));
			}
	
		}
		
		//if down button is pressed
		if(ae.getSource()==b4) {
			if(floorIndex>0) {
				floorIndex--;
				b2.setText(floor.get(floorIndex));
				refreshPage(floor.get(floorIndex));
			}
			
		}
		
		//If Confirm button is pressed
		if(ae.getSource()==b5 || ae.getSource()==b6) {
			//Check current state of ComboBox
			String checkBoxState = cb.getSelectedItem().toString();
			if(checkBoxState != null) {
				switch(checkBoxState) {
				case "電灯" : operationForAE.setReadings(floorName, reading, 0);
				cb.setSelectedItem(unitType[1]);tf.setText("");break;
				case "動力" : operationForAE.setReadings(floorName, reading, 1);
				cb.setSelectedItem(unitType[2]);tf.setText("");break;
				case "水道" : operationForAE.setReadings(floorName, reading, 2);
				cb.setSelectedItem(unitType[3]);tf.setText("");break;
				case "ガス" : operationForAE.setReadings(floorName, reading, 3);
				cb.setSelectedItem(unitType[0]);
				if(floorIndex<floor.size()-1) {
					floorIndex++;
					b2.setText(floor.get(floorIndex));
					refreshPage(floor.get(floorIndex));
				}
				else if(floorIndex == floor.size()-1) {
					//code for submitting objects into temp table of server
					//code for CS01 Menu
					
					
				}
				;break;
				
				}
			
			}
		}
	
	}
	
	//Sub-program for resizing of images
	public Image rescaleImage(String path,Component component) {
		BufferedImage img = null;
		try{
			img = ImageIO.read(new File(path));
			Image resizedImage = img.getScaledInstance(component.getWidth(), component.getHeight(), Image.SCALE_SMOOTH);
			return resizedImage;
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public Image rescaleImage2(String path,Component component) {
		BufferedImage img = null;
		try{
			img = ImageIO.read(new File(path));
			Image resizedImage = img.getScaledInstance(component.getWidth()-65, component.getHeight()-20, Image.SCALE_SMOOTH);
			return resizedImage;
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	//Observer will execute this method,once there is a change in it's subject
	//meaning if a button is clicked in BM01 or FM01,these methods will be invoked
	@Override
	public void onButtonClicked(String componentText,JButton b) {
		
		//componentText will accept the text on the button which was clicked on BM01 or FM01
		if(b==b2) {
			b2.setText(componentText);
			//resetting floorIndex
			floorIndex = floor.indexOf(componentText);
			refreshPage(componentText);
			
		}		
	}
	//after changing floor name,page needs to be refreshed,get data from HashMap and load it into textfield
	public void refreshPage(String floorName) {
		String reading;
		Double readingDouble = operationForAE.getReading(cb.getSelectedIndex(), floorName);
		if(readingDouble!=null) {
			reading = readingDouble.toString();
			tf.setText(reading);
		}
		else tf.setText("");
	}
}
