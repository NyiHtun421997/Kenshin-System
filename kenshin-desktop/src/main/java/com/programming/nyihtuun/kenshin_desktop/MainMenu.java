package com.programming.nyihtuun.kenshin_desktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;


public class MainMenu extends JFrame implements ActionListener,CallBack{
	
	JButton inputButton,checkButton,buildingButton;
	JLabel 	dateJLabel,inputLabel,checkLabel,buildingLabel1,buildingLabel2;

	//these will be inside main menu which will call constructor of InputScreen
	private LocalDate readingDate;
	//will be populated from server
	private List<String> buildingName = new ArrayList<String>();
	private List<String> floor = new ArrayList<String>();
	private LinkedHashMap<String,FloorReading> floorReadingsMap;
	private ReadingOperation operationForAE;
	private final HttpService httpService;
	
	MainMenu(HttpService httpService){
		
		super("Main Menu");
		this.httpService = httpService;
		setLayout(null);
		Container contentPane = this.getContentPane();
		buildingName = httpService.getBuildings();
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem logout = new JMenuItem("Logout");
		fileMenu.add(logout);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		logout.addActionListener((ActionEvent ae)->{
			
		    //Creating a confirmation dialog box before moving to CS01
			ImageIcon decorativeIcon = new ImageIcon("resources/images/ask.png");
			Image decorativeImage = decorativeIcon.getImage();
			decorativeIcon = new ImageIcon(decorativeImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
			
			int choice = JOptionPane.showConfirmDialog(null,"Do you want to logout?", "Logout", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,decorativeIcon);
					
			if(choice == JOptionPane.YES_OPTION) {
			   httpService.logoutMethod();
			   new LoginPage();
			   this.dispose();
			}
	});
		
		dateJLabel = new JLabel(dateConverter(readingDate));//must follow this ○年○月 pattern
		dateJLabel.setBounds(405,20,120,40);
		dateJLabel.setFont(new Font("Ariel",Font.BOLD,18));
		
		inputButton = new JButton();
		inputButton.setBounds(210,70,200,200);
		inputButton.setOpaque(true);
		inputButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		inputButton.setBackground(new Color(237, 244, 255));
		ImageIcon imageIcon = new ImageIcon(rescaleImage("resources/images/input.png",inputButton.getWidth(),inputButton.getHeight()));
		inputButton.setIcon(imageIcon);
		inputButton.addActionListener(this);
		
		inputLabel = new JLabel("入力");
		inputLabel.setBounds(270,260,80,40);
		inputLabel.setFont(new Font("Ariel",Font.BOLD,18));
		inputLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		checkButton = new JButton();
		checkButton.setBounds(500,70,200,200);
		checkButton.setOpaque(true);
		checkButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		checkButton.setBackground(new Color(237, 244, 255));
		imageIcon = new ImageIcon(rescaleImage("resources/images/check.png",checkButton.getWidth(),checkButton.getHeight()));
		checkButton.setIcon(imageIcon);
		checkButton.addActionListener(this);

		checkLabel = new JLabel("チェック");
		checkLabel.setBounds(560,260,80,40);
		checkLabel.setFont(new Font("Ariel",Font.BOLD,20));
		checkLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		buildingLabel1 = new JLabel("建物名");
		buildingLabel1.setBounds(385,280,140,40);
		buildingLabel1.setFont(new Font("Ariel",Font.ITALIC,17));
		buildingLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		
		buildingButton = new JButton();
		buildingButton.setBounds(410,330,90,90);
		buildingButton.setOpaque(true);
		buildingButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buildingButton.setBackground(new Color(237, 244, 255));
		imageIcon = new ImageIcon(rescaleImage("resources/images/buildings.png",buildingButton.getWidth(),buildingButton.getHeight()));
		buildingButton.setIcon(imageIcon);
		buildingButton.addActionListener(this);
		
		//Lable for Building Name chosen by user
		buildingLabel2 = new JLabel();
        buildingLabel2.setBounds(385, 420, 140, 40);
		buildingLabel2.setFont(new Font("Ariel",Font.BOLD,14));
		buildingLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		
		contentPane.add(dateJLabel);
		contentPane.add(buildingLabel1);
		contentPane.add(inputButton);
		contentPane.add(inputLabel);
		contentPane.add(checkButton);
		contentPane.add(checkLabel);
		contentPane.add(buildingButton);
		contentPane.add(buildingLabel2);
		contentPane.setBackground(new Color(237, 244, 255));
		
		setSize(900,500);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		String currentBuildingLabel = buildingLabel2.getText();
		if(ae.getSource()==inputButton) {
			if(currentBuildingLabel!="") {
				if(!httpService.checkForBuilding(currentBuildingLabel)) {

					floor = httpService.getFloorListForBld(currentBuildingLabel);
					new InputScreen(currentBuildingLabel,readingDate,floor,httpService);
					this.dispose();
				}
				else throw new CustomException("Data for this building is already being created.");
			}
			else throw new CustomException("Choose a building.");
			
		}
		if(ae.getSource()==buildingButton) {
			
			new BuildingMenu(buildingName,this,buildingButton);
			//this = an instance of input screen frame who implements CallBack interface and acts as observer and will be observing it's subject,buildingMenu
		}
		
		if(ae.getSource() == checkButton) {
			
			if(currentBuildingLabel!="") {
				
				//will ask user to check data for latest month or other months
				ImageIcon decorativeIcon = new ImageIcon("resources/images/ask.png");
				Image decorativeImage = decorativeIcon.getImage();
				decorativeIcon = new ImageIcon(decorativeImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
		
		        int choice = JOptionPane.showConfirmDialog(null,"Do you want to check data for Latest Month?", "Confirmation", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,decorativeIcon);	
		        if(choice == JOptionPane.YES_OPTION) {
		        	//call HttpService method to get LinkedHashMap data for readings
		        	floorReadingsMap = httpService.getFloorReadingsFromTempo(currentBuildingLabel);
		        	operationForAE = new Operation(floorReadingsMap,true);
		        	floor = httpService.getFloorListForBld(currentBuildingLabel);
		        	//move to CH01
		        	new CheckMenu(operationForAE,currentBuildingLabel,readingDate,floor,httpService);
		        	this.dispose();
		        }else {
		        	//let user chooses Month
		        	//call HttpService method to get a list of reading dates for a building
					List<String> readingDateList = httpService.getReadingDatesForBuilding(currentBuildingLabel);
					new ChoiceMenu(readingDateList,this,checkButton);
					//this = an instance of input screen frame who implements CallBack interface and acts as observer and will be observing it's subject,choiceMenu
		        }
				
			}	
			else throw new CustomException("Choose a building.");
		}
		
	}
	public static void main(String[] arg) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
			try {
				MainMenu mMenu = new MainMenu(new HttpService(new TokenManager(""),"192.168.11.6"));
} 
			catch (Exception e) {
					e.printStackTrace();

}}});}
			
	//Sub-program for resizing of images
		public Image rescaleImage(String path,int width,int height) {
			BufferedImage img = null;
			try{
				img = ImageIO.read(new File(path));
				Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
				return resizedImage;
			}
			catch(IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		//Sub-program for converting LocalDate to String
		public String dateConverter(LocalDate readingDate) {
			if(readingDate == null) {
				return "";
			}
			return String.format("%4d年%1d月", readingDate.getYear(), readingDate.getMonthValue());
		}
	@Override
	public void onButtonClicked(String componentText, JButton b) {
		//componentText will accept the text of the button which was clicked on BM01
		if(b == buildingButton) {
			buildingLabel2.setText(componentText);
			readingDate = httpService.getLatestDate(componentText);
			dateJLabel.setText(dateConverter(readingDate));
		}
		if(b == checkButton) {
			//User will finish choosing a month to check and then will ask user to continue to CheckMenu
			//Creating a confirmation dialog box before moving to CH01
			ImageIcon decorativeIcon = new ImageIcon("resources/images/ask.png");
			Image decorativeImage = decorativeIcon.getImage();
			decorativeIcon = new ImageIcon(decorativeImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
	
	        int choice = JOptionPane.showConfirmDialog(null,"Do you want to check data for "+componentText+"?", "Confirmation", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,decorativeIcon);	
	        if(choice == JOptionPane.YES_OPTION) {
	        	String currentBuildingLabel = buildingLabel2.getText();
	        	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	             // Parse the string into a LocalDate using the defined formatter
	             LocalDate date = LocalDate.parse(componentText, formatter);
	             
	        	//call HttpService method to get LinkedHashMap data for readings
	        	floorReadingsMap = httpService.getFloorReadings(currentBuildingLabel, componentText);
	        	operationForAE = new Operation(floorReadingsMap,false);
	        	floor = httpService.getFloorListForBld(currentBuildingLabel);
	        	//move to CH01
	        	new CheckMenu(operationForAE,currentBuildingLabel,date,floor,httpService);
	        	this.dispose();
	        }
	        else {}		
		}
	}	
}