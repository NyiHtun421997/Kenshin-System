package com.system.kenshin;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

public class CompareScreen{
	//will accept as arg from IS01/01
	String buildingName,dateLabel;
	//will accept as arg from server
	List<String> floor_Tenant = new ArrayList<>();
	
	//For Testing
	public static void main(String[] args) {
		//will accept as arg from IS01/01
		String buildingName = "BRAVI北浜",dateLabel = "2023"+"年"+"2"+"月";
		//will accept as arg from server
		List<String> floor_Tenant = new ArrayList<>(List.of("1F・ShopA","2F・ShopB","2F・ShopC","3F・ShopD"));
	    EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
			try {
				CompareScreenFrame compareScreen = new CompareScreenFrame(buildingName,dateLabel,floor_Tenant);
				compareScreen.setSize(1200,800);
				compareScreen.setVisible(true);
				compareScreen.setResizable(false);
			    compareScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	} 
			catch (Exception e) {
					e.printStackTrace();

	}}});}
	
	public CompareScreen(String buildingName,String dateLabel){
		
		this.buildingName = buildingName;
		this.dateLabel = dateLabel;
		floor_Tenant = HttpService.getTenantListForBld(buildingName);
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					CompareScreenFrame compareScreen = new CompareScreenFrame(buildingName,dateLabel,floor_Tenant);
					compareScreen.setSize(1200,800);
					compareScreen.setVisible(true);
					compareScreen.setResizable(false);
				    compareScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
class CompareScreenFrame extends JFrame implements ActionListener{
  
JLabel buildingLabel,dateJLabel,usageLabel,curMonthUsage,prevMonthUsage,prevMonthCompare,prevYearUsage,prevYearCompare;
JLabel[] readingTypes = new JLabel[4];
JLabel[] lb = new JLabel[20];
JButton locationButton,nextButton,prevButton;
JPanel headerPanel,tablePanel;
String buildingName,dateLabel;
List<String> floor_Tenant;
static int floor_TenantIndex = 0;

LinkedHashMap<String,FloorReading> prevMonthData;
LinkedHashMap<String,FloorReading> twoMonthBeforeData;
LinkedHashMap<String,FloorReading> prevYearData;
    
  public CompareScreenFrame(String buildingName, String dateLabel,  List<String> floor_Tenant){
    
  super("Compare Menu");
  this.buildingName = buildingName;
  this.dateLabel = dateLabel;
  this.floor_Tenant = floor_Tenant;
  
//converting dateLabel String to last month String
int thisMonth = Integer.valueOf(dateLabel.substring(dateLabel.indexOf("年")+1,dateLabel.indexOf("月")));

String prevMonth = dateLabel.substring(0,dateLabel.indexOf("年")+1)+Integer.toString(thisMonth-1)+"月";
prevMonthData = HttpService.getTenantReadings(buildingName, prevMonth);

//converting dateLabel String to twoMonthBefore String
String twoMonthBefore = dateLabel.substring(0,dateLabel.indexOf("年")+1)+Integer.toString(thisMonth-2)+"月";
twoMonthBeforeData = HttpService.getTenantReadings(buildingName, twoMonthBefore);

//converting dateLabel String to prevYear String
int thisYear = Integer.valueOf(dateLabel.substring(0,dateLabel.indexOf("年")));
String prevYear = Integer.toString(thisYear-1)+dateLabel.substring(dateLabel.indexOf("年"));
prevYearData = HttpService.getTenantReadings(buildingName, prevYear);


  Container contentPane = this.getContentPane();
  contentPane.setLayout(null);

buildingLabel = new JLabel(buildingName);
buildingLabel.setBounds(500,20,200,30);
buildingLabel.setFont(new Font("Ariel",Font.BOLD,18));
buildingLabel.setHorizontalAlignment(SwingConstants.CENTER);

dateJLabel = new JLabel(dateLabel);
dateJLabel.setBounds(500,60,200,30);
dateJLabel.setFont(new Font(null,Font.BOLD,18));
dateJLabel.setHorizontalAlignment(SwingConstants.CENTER);

nextButton = new JButton();
nextButton.setBounds(690,120,80,80);
nextButton.setOpaque(true);
nextButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
nextButton.setBackground(new Color(237, 244, 255));
ImageIcon imageIcon = new ImageIcon(rescaleImage("resources/images/next.png",nextButton));
nextButton.setIcon(imageIcon);
nextButton.addActionListener(this);

locationButton = new JButton(floor_Tenant.get(floor_TenantIndex));//floor + tenant
locationButton.setVerticalTextPosition(JButton.BOTTOM);
locationButton.setHorizontalTextPosition(JButton.CENTER);
locationButton.setFont(new Font(null,Font.PLAIN,11));
locationButton.setBounds(500,120,200,80);
locationButton.setOpaque(true);
locationButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
locationButton.setBackground(new Color(237, 244, 255));
imageIcon = new ImageIcon(rescaleImage("resources/images/destination.png",locationButton));
locationButton.setIcon(imageIcon);
locationButton.addActionListener(this);

prevButton = new JButton();
prevButton.setBounds(430,120,80,80);
prevButton.setOpaque(true);
prevButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
prevButton.setBackground(new Color(237, 244, 255));
imageIcon = new ImageIcon(rescaleImage("resources/images/previous.png",prevButton));
prevButton.setIcon(imageIcon);
prevButton.addActionListener(this);

//building table
//headers
usageLabel = new JLabel("使用");
usageLabel.setHorizontalAlignment(SwingConstants.CENTER);
usageLabel.setOpaque(true);
usageLabel.setBackground(new Color(244, 250, 230));
usageLabel.setFont(new Font(null,Font.BOLD,16));
usageLabel.setBorder(BorderFactory.createLineBorder(Color.black, 3));

curMonthUsage = new JLabel(dateLabel);
curMonthUsage.setHorizontalAlignment(SwingConstants.CENTER);
curMonthUsage.setOpaque(true);
curMonthUsage.setBackground(new Color(244, 250, 230));
curMonthUsage.setFont(new Font(null,Font.BOLD,16));
curMonthUsage.setBorder(BorderFactory.createLineBorder(Color.black, 3));

prevMonthUsage = new JLabel(prevMonth);
prevMonthUsage.setHorizontalAlignment(SwingConstants.CENTER);
prevMonthUsage.setOpaque(true);
prevMonthUsage.setBackground(new Color(244, 250, 230));
prevMonthUsage.setFont(new Font(null,Font.BOLD,16));
prevMonthUsage.setBorder(BorderFactory.createLineBorder(Color.black, 3));

prevMonthCompare = new JLabel("前月対比");
prevMonthCompare.setHorizontalAlignment(SwingConstants.CENTER);
prevMonthCompare.setOpaque(true);
prevMonthCompare.setBackground(new Color(244, 250, 230));
prevMonthCompare.setFont(new Font(null,Font.BOLD,16));
prevMonthCompare.setBorder(BorderFactory.createLineBorder(Color.black, 3));

prevYearUsage = new JLabel(prevYear);
prevYearUsage.setHorizontalAlignment(SwingConstants.CENTER);
prevYearUsage.setOpaque(true);
prevYearUsage.setBackground(new Color(244, 250, 230));
prevYearUsage.setFont(new Font(null,Font.BOLD,16));
prevYearUsage.setBorder(BorderFactory.createLineBorder(Color.black, 3));

prevYearCompare = new JLabel("前年対比");
prevYearCompare.setHorizontalAlignment(SwingConstants.CENTER);
prevYearCompare.setOpaque(true);
prevYearCompare.setBackground(new Color(244, 250, 230));
prevYearCompare.setFont(new Font(null,Font.BOLD,16));
prevYearCompare.setBorder(BorderFactory.createLineBorder(Color.black, 3));

headerPanel = new JPanel();
headerPanel.setLayout(new GridLayout(1,6,10,10));
headerPanel.setBounds(150,220,900,60);
headerPanel.setBackground(new Color(237, 244, 255));
headerPanel.add(usageLabel);
headerPanel.add(curMonthUsage);
headerPanel.add(prevMonthUsage);
headerPanel.add(prevMonthCompare);
headerPanel.add(prevYearUsage);
headerPanel.add(prevYearCompare);

//Table Body

tablePanel = new JPanel();
tablePanel.setLayout(new GridLayout(4,6,10,10));
tablePanel.setBounds(150,290,900,280);
tablePanel.setBackground(new Color(237, 244, 255));

readingTypes[0] = new JLabel("電灯");
readingTypes[1] = new JLabel("動力");
readingTypes[2] = new JLabel("水道");
readingTypes[3] = new JLabel("ガス");

for(int i = 0; i<4; i++) {
	
	readingTypes[i].setHorizontalAlignment(SwingConstants.CENTER);
	readingTypes[i].setOpaque(true);
	readingTypes[i].setBackground(new Color(205, 230, 250));
	readingTypes[i].setFont(new Font(null,Font.BOLD,20));
	readingTypes[i].setBorder(BorderFactory.createLineBorder(Color.black, 2));
	tablePanel.add(readingTypes[i]);
	
	for(int j = 0; j<5; j++) {
		
		lb[j] = new JLabel();
		lb[j].setHorizontalAlignment(SwingConstants.CENTER);
		lb[j].setOpaque(true);
		lb[j].setBackground(new Color(205, 230, 250));
		lb[j].setFont(new Font(null,Font.PLAIN,16));
		lb[j].setBorder(BorderFactory.createLineBorder(Color.black, 2));
		tablePanel.add(lb[j]);
	}
	
}

contentPane.add(buildingLabel);
contentPane.add(dateJLabel);
contentPane.add(nextButton);
contentPane.add(locationButton);
contentPane.add(prevButton);
contentPane.add(headerPanel);
contentPane.add(tablePanel);

contentPane.setBackground(new Color(237, 244, 255));
		
}
  @Override
	public void actionPerformed(ActionEvent ae) {
	//if up button is pressed
			if(ae.getSource()==nextButton) {
				if(floor_TenantIndex<floor_Tenant.size()-1) {
					floor_TenantIndex++;
					locationButton.setText(floor_Tenant.get(floor_TenantIndex));
				}
		
			}
			
			//if down button is pressed
			if(ae.getSource()==prevButton) {
				if(floor_TenantIndex>0) {
					floor_TenantIndex--;
					locationButton.setText(floor_Tenant.get(floor_TenantIndex));
				}
				
			}
		
	}
//Sub-program for resizing of images
		public Image rescaleImage(String path,Component component) {
			BufferedImage img = null;
			try{
				img = ImageIO.read(new File(path));
				Image resizedImage = img.getScaledInstance(component.getWidth()-140, component.getHeight()-20, Image.SCALE_SMOOTH);
				return resizedImage;
			}
			catch(IOException e) {
				e.printStackTrace();
				return null;
			}
		}
    
}