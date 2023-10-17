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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

public class CompareScreen{
	
	//will accept as arg from server
	private List<String> floor_Tenant = new ArrayList<>();
	private boolean isLatestMonth;
	
	//For Testing
	public static void main(String[] args) {
		//will accept as arg from IS01/01
		String buildingName = "Sample Building C";
		LocalDate readingDate = LocalDate.of(2023, 11, 1);
		//will accept as arg from server
		List<String> floor_Tenant = new ArrayList<>(List.of("1F・Dental","1F・Convinienece","2F・ABC＿CompanyLimited","3F・大京"));
		boolean isLatestMonth = true;
	    EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
			try {
				CompareScreenFrame compareScreen = new CompareScreenFrame(buildingName,readingDate,floor_Tenant,isLatestMonth);
				compareScreen.setSize(1200,800);
				compareScreen.setVisible(true);
				compareScreen.setResizable(false);
			    compareScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	} 
			catch (Exception e) {
					e.printStackTrace();

	}}});}
	
	public CompareScreen(String buildingName,LocalDate readingDate,boolean isLatestMonth){
		
		floor_Tenant = HttpService.getTenantListForBld(buildingName);
		this.isLatestMonth = isLatestMonth;
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					CompareScreenFrame compareScreen = new CompareScreenFrame(buildingName,readingDate,floor_Tenant,isLatestMonth);
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
class CompareScreenFrame extends JFrame implements ActionListener,CallBack{
  
JLabel buildingLabel,dateJLabel,usageLabel,curMonthUsage,prevMonthUsage,prevMonthCompare,prevYearUsage,prevYearCompare;
JLabel[] readingTypes = new JLabel[4];
JLabel[] lb = new JLabel[20];
JLabel newCommentLabel,oldCommentLabel;
JButton locationButton,nextButton,prevButton,confirmButton;
JPanel headerPanel,tablePanel;
JTextArea oldCommentBox,newCommentBox;
private String buildingName;
private LocalDate readingDate;
private List<String> floor_Tenant;
private static int floor_TenantIndex = 0;
private boolean isLatestMonth;

LinkedHashMap<String,FloorReading> currentMonthData;
LinkedHashMap<String,FloorReading> prevMonthData;
LinkedHashMap<String,FloorReading> twoMonthBeforeData;
LinkedHashMap<String,FloorReading> prevYearSameMonthData;
LinkedHashMap<String,FloorReading> prevYearPrevMonthData;
//Collection to store comments for each reading
LinkedHashMap<String,String> commentData;
    
  public CompareScreenFrame(String buildingName, LocalDate readingDate,  List<String> floor_Tenant,boolean isLatestMonth){
    
  super("Compare Menu");
  this.buildingName = buildingName;
  this.readingDate = readingDate;
  this.floor_Tenant = floor_Tenant;
  this.isLatestMonth = isLatestMonth;
  
//Readings for current month  
//if it is Latest Month, call this method which gets data from Temporary storage of server
  if(isLatestMonth)
	  currentMonthData = HttpService.getTenantReadingsFromTempo(buildingName);
//if its is not Latest Month,call this method which gets data from DB
  else {
	  String currMonth = String.format("%4d-%02d-01",readingDate.getYear(),readingDate.getMonthValue());
	  currentMonthData = HttpService.getTenantReadings(buildingName, currMonth);
  }
String prevMonth = String.format("%4d-%02d-01",readingDate.getYear(),readingDate.minusMonths(1).getMonthValue());
//Readings for previous month
prevMonthData = HttpService.getTenantReadings(buildingName, prevMonth);

//converting to twoMonthBefore String
String twoMonthBefore = String.format("%4d-%02d-01",readingDate.getYear(),readingDate.minusMonths(2).getMonthValue());
//Readings for two months before
twoMonthBeforeData = HttpService.getTenantReadings(buildingName, twoMonthBefore);

//converting to prevYear String
String prevYear = String.format("%4d-%02d-01",readingDate.minusYears(1).getYear(),readingDate.getMonthValue());
//Readings for previous year same month
prevYearSameMonthData = HttpService.getTenantReadings(buildingName, prevYear);
//Readings for previous year previous month
String prevYearPrevMonth = String.format("%4d-%02d-01",readingDate.minusYears(1).getYear(),readingDate.minusMonths(1).getMonthValue());
prevYearPrevMonthData = HttpService.getTenantReadings(buildingName, prevYearPrevMonth);

commentData = new LinkedHashMap<>();
//*********************Calculation Part*************************************

//*********************GUI Part*********************************************
  Container contentPane = this.getContentPane();
  contentPane.setLayout(null);
  
 JMenuBar menuBar = new JMenuBar();
 JMenu fileMenu = new JMenu("File");
 JMenuItem saveMenu = new JMenuItem("Save");
 JMenuItem approveMenu = new JMenuItem("Approve");
 fileMenu.add(saveMenu);
 fileMenu.add(approveMenu);
 menuBar.add(fileMenu);
 setJMenuBar(menuBar);
 
 saveMenu.addActionListener((ActionEvent ae)->{
	 
	//Creating a confirmation dialog box before moving to MM01
		ImageIcon decorativeIcon = new ImageIcon("resources/images/ask.png");
		Image decorativeImage = decorativeIcon.getImage();
		decorativeIcon = new ImageIcon(decorativeImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

     int choice = JOptionPane.showConfirmDialog(null,"Click Yes if data are correct.", "Confirmation", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,decorativeIcon);
		
     if(choice == JOptionPane.YES_OPTION) {
     	//will save all the comments to TempMap or server
     	
     	HttpService.storeComments(commentData, buildingName);
     	MainMenu.main(new String[0]);
		 this.dispose();
     }
     else {}
	 
 });
 if(!isLatestMonth) approveMenu.setEnabled(false);
 approveMenu.addActionListener((ActionEvent ae)->{
	 
		//Creating a confirmation dialog box before moving to MM01
			ImageIcon decorativeIcon = new ImageIcon("resources/images/ask.png");
			Image decorativeImage = decorativeIcon.getImage();
			decorativeIcon = new ImageIcon(decorativeImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

	     int choice = JOptionPane.showConfirmDialog(null,"Would you like to approve the data?", "Confirmation", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,decorativeIcon);
			
	     if(choice == JOptionPane.YES_OPTION) {
	     	//will invoke a method inside spring controller to save data from TempMap to DB
	     	
	     	HttpService.approve(buildingName,String.format("%4d-%02d-01",readingDate.getYear(),readingDate.getMonthValue()));
	     	MainMenu.main(new String[0]);
			 this.dispose();
	     }
	     else {}
		 
	 });

buildingLabel = new JLabel(buildingName);
buildingLabel.setBounds(500,20,200,30);
buildingLabel.setFont(new Font("Ariel",Font.BOLD,18));
buildingLabel.setHorizontalAlignment(SwingConstants.CENTER);

dateJLabel = new JLabel(dateConverter(readingDate));
dateJLabel.setBounds(500,60,200,30);
dateJLabel.setFont(new Font(null,Font.BOLD,18));
dateJLabel.setHorizontalAlignment(SwingConstants.CENTER);

nextButton = new JButton();
nextButton.setBounds(690,120,80,80);
nextButton.setOpaque(true);
nextButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
nextButton.setBackground(new Color(237, 244, 255));
ImageIcon imageIcon = new ImageIcon(rescaleImage("resources/images/next.png",nextButton.getWidth()-140,nextButton.getHeight()-20));
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
imageIcon = new ImageIcon(rescaleImage("resources/images/destination.png",locationButton.getWidth()-140,locationButton.getHeight()-20));
locationButton.setIcon(imageIcon);
locationButton.addActionListener(this);

prevButton = new JButton();
prevButton.setBounds(430,120,80,80);
prevButton.setOpaque(true);
prevButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
prevButton.setBackground(new Color(237, 244, 255));
imageIcon = new ImageIcon(rescaleImage("resources/images/previous.png",prevButton.getWidth()-140,prevButton.getHeight()-20));
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

curMonthUsage = new JLabel(dateConverter(readingDate));
curMonthUsage.setHorizontalAlignment(SwingConstants.CENTER);
curMonthUsage.setOpaque(true);
curMonthUsage.setBackground(new Color(244, 250, 230));
curMonthUsage.setFont(new Font(null,Font.BOLD,16));
curMonthUsage.setBorder(BorderFactory.createLineBorder(Color.black, 3));

prevMonthUsage = new JLabel(dateConverter(readingDate.minusMonths(1)));
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

prevYearUsage = new JLabel(dateConverter(readingDate.minusYears(1)));
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
//Setting up Table
for(int i = 0; i<4; i++) {
	
	readingTypes[i].setHorizontalAlignment(SwingConstants.CENTER);
	readingTypes[i].setOpaque(true);
	readingTypes[i].setBackground(new Color(205, 230, 250));
	readingTypes[i].setFont(new Font(null,Font.BOLD,20));
	readingTypes[i].setBorder(BorderFactory.createLineBorder(Color.black, 2));
	tablePanel.add(readingTypes[i]);
	
	for(int j = 0; j<5; j++) {
		
		lb[j+(5*i)] = new JLabel();
		lb[j+(5*i)].setHorizontalAlignment(SwingConstants.CENTER);
		lb[j+(5*i)].setOpaque(true);
		lb[j+(5*i)].setBackground(new Color(205, 230, 250));
		lb[j+(5*i)].setFont(new Font(null,Font.PLAIN,16));
		lb[j+(5*i)].setBorder(BorderFactory.createLineBorder(Color.black, 2));
		tablePanel.add(lb[j+(5*i)]);
	}
	
}
oldCommentLabel = new JLabel("Old Comment");
oldCommentLabel.setBounds(300, 575, 140, 18);
oldCommentLabel.setFont(new Font("Ariel",Font.BOLD,14));
oldCommentLabel.setHorizontalAlignment(SwingConstants.CENTER);

oldCommentBox = new JTextArea(10,10);
oldCommentBox.setFont(new Font(null,Font.PLAIN,16));
oldCommentBox.setEditable(false);
JScrollPane scrollPane1 = new JScrollPane(oldCommentBox);
scrollPane1.setBounds(145, 600, 450, 80);

newCommentLabel = new JLabel("New Comment");
newCommentLabel.setBounds(760, 575, 140, 18);
newCommentLabel.setFont(new Font("Ariel",Font.BOLD,14));
newCommentLabel.setHorizontalAlignment(SwingConstants.CENTER);

newCommentBox = new JTextArea(10,10);
newCommentBox.setFont(new Font(null,Font.PLAIN,16));
if(!isLatestMonth) newCommentBox.setEditable(false);
JScrollPane scrollPane2 = new JScrollPane(newCommentBox);
scrollPane2.setBounds(605, 600, 450, 80);

//For Confirm Button
confirmButton = new JButton("Confirm");
confirmButton.setHorizontalAlignment(SwingConstants.CENTER);
confirmButton.setBounds(530,680,140,50);
confirmButton.addActionListener(this);

contentPane.add(buildingLabel);
contentPane.add(dateJLabel);
contentPane.add(nextButton);
contentPane.add(locationButton);
contentPane.add(prevButton);
contentPane.add(headerPanel);
contentPane.add(tablePanel);
contentPane.add(oldCommentLabel);
contentPane.add(scrollPane1);
contentPane.add(newCommentLabel);
contentPane.add(scrollPane2);
contentPane.add(confirmButton);

contentPane.setBackground(new Color(237, 244, 255));
this.getRootPane().setDefaultButton(confirmButton);
//filling up Table
refreshPage(floor_Tenant.get(floor_TenantIndex));
		
}
  @Override
	public void actionPerformed(ActionEvent ae) {
	//if up button is pressed
			if(ae.getSource() == nextButton) {
				if(floor_TenantIndex<floor_Tenant.size()-1) {
					floor_TenantIndex++;
					locationButton.setText(floor_Tenant.get(floor_TenantIndex));
					refreshPage(floor_Tenant.get(floor_TenantIndex));
				}
			}
			
			//if down button is pressed
			if(ae.getSource() == prevButton) {
				if(floor_TenantIndex>0) {
					floor_TenantIndex--;
					locationButton.setText(floor_Tenant.get(floor_TenantIndex));
					refreshPage(floor_Tenant.get(floor_TenantIndex));
				}	
			}
			
			//if Location button is pressed
			if(ae.getSource() == locationButton) {
				new ChoiceMenu(floor_Tenant,this,locationButton);
				//this = an instance of input screen frame who implements CallBack interface and acts as observer and will be observing it's subject,choiceMenu	
			}
			
			//if Confirm button is pressed
			if(ae.getSource() == confirmButton) {
				
				String floor_TenantName = floor_Tenant.get(floor_TenantIndex);
				commentData.put(floor_TenantName, newCommentBox.getText());				
				nextButton.doClick();
			}
	}
//Sub-program for resizing of images
		public Image rescaleImage(String path,int width, int height) {
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
		//after changing tenant name,page needs to be refreshed,get data from HashMap and load it into textfield
		public void refreshPage(String floor_tenantName) {
			
			for(int i = 0; i<4; i++) {
				//i=0(電灯) i=1(動力) i=2(水道) i=3(ガス)
				Double currentMonthReading = currentMonthData.get(floor_tenantName).getReading(i);
				
				Double prevMonthReading = prevMonthData.get(floor_tenantName).getReading(i);
				Double twoMonthBeforeReading = twoMonthBeforeData.get(floor_tenantName).getReading(i);
				Double prevYearSameMonthReadng = prevYearSameMonthData.get(floor_tenantName).getReading(i);
				Double prevYearPrevMonthReading = prevYearPrevMonthData.get(floor_tenantName).getReading(i);
				
				Double currentMonthReadingBeforeChange = currentMonthData.get(floor_tenantName).getReadingBeforeChange(i);
				Double prevMonthReadingBeforeChange = prevMonthData.get(floor_tenantName).getReadingBeforeChange(i);
				Double twoMonthBeforeReadingBeforeChange = twoMonthBeforeData.get(floor_tenantName).getReadingBeforeChange(i);
				Double prevYearSameMonthReadngBeforeChange = prevYearSameMonthData.get(floor_tenantName).getReadingBeforeChange(i);
				Double prevYearPrevMonthReadingBeforeChange = prevYearPrevMonthData.get(floor_tenantName).getReadingBeforeChange(i);
				
				//Setting datas to the table
				Double currentMonthUsage;
				if(currentMonthReadingBeforeChange!=0)
				currentMonthUsage = (currentMonthReading+currentMonthReadingBeforeChange)-(prevMonthReading+prevMonthReadingBeforeChange);
				else 
				currentMonthUsage = (currentMonthReading)-(prevMonthReading);
				currentMonthUsage = Math.ceil(currentMonthUsage * 100)/100;
				
				Double previousMonthUsage;
				if(prevMonthReadingBeforeChange!=0)
				previousMonthUsage = (prevMonthReading+prevMonthReadingBeforeChange)-(twoMonthBeforeReading+twoMonthBeforeReadingBeforeChange);
				else
				previousMonthUsage = (prevMonthReading)-(twoMonthBeforeReading);
				previousMonthUsage = Math.ceil(previousMonthUsage * 100)/100;
				
				Double previousYearUsage;
				if(prevYearSameMonthReadngBeforeChange!=0)
				previousYearUsage = (prevYearSameMonthReadng+prevYearSameMonthReadngBeforeChange)-(prevYearPrevMonthReading+prevYearPrevMonthReadingBeforeChange);
				else
				previousYearUsage = (prevYearSameMonthReadng)-(prevYearPrevMonthReading);
				previousYearUsage = Math.ceil(previousYearUsage * 100)/100;
				Integer prevMonthCompare = (int) Math.round(100*currentMonthUsage/previousMonthUsage);
				Integer prevYearCompare = (int) Math.round(100*currentMonthUsage/previousYearUsage);
				
				lb[0+(5*i)].setText(String.format("%.2f",currentMonthUsage));
				lb[1+(5*i)].setText(String.format("%.2f",previousMonthUsage));
				lb[2+(5*i)].setText(String.format("%d%%",prevMonthCompare));

				if(prevMonthCompare < 50 || prevMonthCompare > 150) {
					
					lb[2+(5*i)].setForeground(Color.red);
				}
				else lb[2+(5*i)].setForeground(Color.black);
				lb[3+(5*i)].setText(String.format("%.2f",previousYearUsage));
				lb[4+(5*i)].setText(String.format("%d%%", prevYearCompare));
				
				if(prevYearCompare < 0 || prevYearCompare > 150) {
					
					lb[4+(5*i)].setForeground(Color.red);
				}
				else lb[4+(5*i)].setForeground(Color.black);
				
			}
			if(isLatestMonth) {
				String comment = currentMonthData.get(floor_tenantName).getComment();
				List<String> list = getCommentForCurrent(comment,floor_tenantName);
				if(list!=null) {
					String commentForCurrent = "";
					for(String c : list) {
						commentForCurrent = commentForCurrent + "\n***************\n" +c;
						oldCommentBox.setText(commentForCurrent);
					}
				}
				else oldCommentBox.setText("");
			}
			else {
				String comment = currentMonthData.get(floor_tenantName).getComment();
				oldCommentBox.setText(comment);
			}
			newCommentBox.setText(commentData.get(floor_tenantName));
		}
		//since comment inside each reading object is for each floor,
		//it contains all tenants of the floor,extract comment for each tenant
		public List<String> getCommentForCurrent(String comment,String floor_tenantName) {
			StringTokenizer  stz=new StringTokenizer(comment,"***************");
			List<String> list = new ArrayList<>();
			while(stz.hasMoreTokens())
			{
				String s = stz.nextToken();
				if(s.contains(floor_tenantName)) {
					list.add(s);
				}
			}
			return list;
		}
		@Override
		public void onButtonClicked(String componentText, JButton b) {
			
			if(b==locationButton) {
				locationButton.setText(componentText);
				//resetting floorIndex
				floor_TenantIndex = floor_Tenant.indexOf(componentText);
				refreshPage(componentText);
				
			}		
		}
    
}