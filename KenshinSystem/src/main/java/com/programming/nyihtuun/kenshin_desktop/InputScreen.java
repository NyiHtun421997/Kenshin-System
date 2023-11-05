package main.java.com.programming.nyihtuun.kenshin_desktop;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

import org.ehcache.Cache;

public class InputScreen {
	
	public InputScreen(String buildingName, LocalDate readingDate, List<String> floor, HttpService httpService) {
	  
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
			try {
				ReadingOperation operationForAE = new Operation();	
				InputScreenFrame f = new InputScreenFrame(operationForAE,buildingName,readingDate,floor,httpService);
				f.setSize(1200,800);
				f.setVisible(true);
				f.setResizable(false);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
} 
			catch (Exception e) {
					e.printStackTrace();

}}});}
}
class InputScreenFrame extends JFrame implements ItemListener,ActionListener,CallBack{
	
	JPanel topPanel,bottomPanel1,bottomPanel2,mainBottomPanel,imageCboxPanel;
	JButton b2,b3,b4,b5,b6,b7;
	JLabel b1,l1,l2,photo,cboxLabel,unitLabel;
	JComboBox cb;
	JFormattedTextField tf,tfOptional;
	JCheckBox cbox,newMeterCBox;
	CardLayout cl;
	
	private ImageCache imageCache;
	private String buildingName;
	private LocalDate readingDate;
	private List<String> floor;
	private final HttpService httpService;
	
	private String unitType [] = {"電灯","動力","水道","ガス"};
	private String electricUnit = "kWh", volumeUnit = "m3";
	private ReadingOperation operationForAE;
	
	private int floorIndex = 0;
	
	InputScreenFrame(ReadingOperation operationForAE,String buildingName, LocalDate readingDate, List<String> floor, HttpService httpService){
		super("Input Menu");
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		  
		this.operationForAE = operationForAE;
		this.buildingName = buildingName;
		this.readingDate = readingDate;
		this.floor = floor;
		this.httpService = httpService;
		imageCache = new ImageCache();
		//once the input menu is opened,operation will be constructed
		operationForAE.startOperation(buildingName, readingDate, floor);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem nextMenu = new JMenuItem("Next");
		JMenuItem tempSave = new JMenuItem("Save");
		JMenuItem logout = new JMenuItem("Logout");
		fileMenu.add(tempSave);
		fileMenu.add(nextMenu);
		fileMenu.add(logout);
		menuBar.add(fileMenu);
		menuBar.setBackground(Color.blue);
		
		setJMenuBar(menuBar);
		//Action to save all the obj inside HashMap to TempMap inside Server
		tempSave.addActionListener((ActionEvent ae)->{
			
			//Creating a confirmation dialog box before moving to CS01
			int choice = confirmationMenu("Do you want to save the readings?","Confirmation");
			
	        if(choice == JOptionPane.YES_OPTION) {
	        	httpService.storeToTempMap(operationForAE.getAllReadings());
	        }
		});
	        nextMenu.addActionListener((ActionEvent ae)->{
			//Creating a confirmation dialog box before moving to CS01
	        int choice = confirmationMenu("Do you want to proceed to Compare Screen?", "Confirmation");
			
	        if(choice == JOptionPane.YES_OPTION) {
	        	httpService.storeToTempMap(operationForAE.getAllReadings());
	        	//will save all the image files inside app's directory to server and delete them
	        	try{
	        		Iterator<Cache.Entry<String, byte[]>> iterator = imageCache.getImageCache().iterator();
	        		while(iterator.hasNext()) {
	        			Cache.Entry<String, byte[]> entry = iterator.next();
	        			httpService.storeImages(entry.getKey(),entry.getValue());
	        		}
	        	}
	        	catch(IOException ie) {
	        		ie.printStackTrace();
	        		throw new CustomException(ie);
	        		//Shows error page
	        	}
	        	catch(CustomException ie) {
	        		new CustomException(ie.getMessage());
	        	}
	        	finally {
	        		imageCache.getCacheManager().close();
	        	}
	        	//after saving close this window and jump to CS01
				new CompareScreen(buildingName,readingDate,true,httpService);
				this.dispose();
	        }
	        else {}		
	});
	logout.addActionListener((ActionEvent ae)->{
				
	    //Creating a confirmation dialog box before moving to CS01
		ImageIcon decorativeIcon = new ImageIcon("resources/images/ask.png");
		Image decorativeImage = decorativeIcon.getImage();
		decorativeIcon = new ImageIcon(decorativeImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
		
		int choice = confirmationMenu("Do you want to logout?", "Logout");
				
		if(choice == JOptionPane.YES_OPTION) {
		   httpService.logoutMethod();
		   new LoginPage();
		   this.dispose();
		}
});
			
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
		ImageIcon floorIcon = new ImageIcon(rescaleImage("resources/images/floor_icon.png",b2.getWidth()-65,b2.getHeight()-20));
		b2.setIcon(floorIcon);
		b2.setVerticalTextPosition(JButton.BOTTOM);
		b2.setHorizontalTextPosition(JButton.CENTER);
		b2.addActionListener(this);
		
		//Label for date
		l1 = new JLabel(dateConverter(readingDate));
		l1.setFont(new Font("Ariel",Font.BOLD,18));
		l1.setBounds(700,5,200,40);
		
		//Label for 種別
		l2 = new JLabel("種別");
		l2.setBounds(370,125,180,50);
		l2.setFont(new Font("Ariel",Font.BOLD,18));
		
		//ComboBox for 系統
		cb = new JComboBox(unitType);
		cb.setBounds(340,150,100,60);
		cb.addActionListener(this);
		
		//TextField for meter unit input
		NumberFormat nf = DecimalFormat.getInstance(Locale.US);
        nf.setMaximumFractionDigits(2); // Set the maximum number of decimal places
        nf.setGroupingUsed(false);//disabling comma thousand separator

		NumberFormatter nft = new NumberFormatter(nf);
		tf = new JFormattedTextField(nft);
		tf.setValue(0.0);
		tf.setColumns(20);
		tf.setBounds(445,130,310,80);
		tf.setHorizontalAlignment(JTextField.CENTER);
		tf.setFont(new Font("Ariel",Font.BOLD,18));
		tf.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		//Making sure only decimal values or numbers are entered
		tf.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent evt) {
				char c = evt.getKeyChar();
				if(Character.isLetter(c)) evt.consume();
				else if(c == KeyEvent.VK_ENTER) {
					if(bottomPanel1.isVisible())
					b5.doClick();
					else b6.doClick();
				}
				else {
					String currentText = tf.getText().replace(",", "");
		            String newText = currentText + evt.getKeyChar();
					try{	
						Double.parseDouble(newText);
					}
					catch(	NumberFormatException e) { evt.consume();}
}}});
		//TextField for meter unit just before they are changed
				
				tfOptional = new JFormattedTextField(nft);
				tfOptional.setValue(0.0);
				tfOptional.setEnabled(false);
				tfOptional.setColumns(5);
				tfOptional.setBounds(770,130,80,80);
				tfOptional.setHorizontalAlignment(JTextField.CENTER);
				tfOptional.setFont(new Font("Ariel",Font.BOLD,18));
				tfOptional.setBorder(BorderFactory.createLineBorder(Color.black, 2));
				//Making sure only decimal values or numbers are entered
				tfOptional.addKeyListener(new KeyAdapter() {
					public void keyTyped(KeyEvent evt) {
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
		//kWh label
		unitLabel = new JLabel(electricUnit);
		unitLabel.setBounds(705,95,50,50);
		unitLabel.setHorizontalAlignment(SwingConstants.CENTER);
				
		//CheckBox for whether upload image or not
		newMeterCBox = new JCheckBox("New Meter",false);
		newMeterCBox.setBounds(757,100,200,30);
		newMeterCBox.addItemListener(this);
		
		//Buttons for increment/decrement of floors
		b3 = new JButton("UP");
		b3.setBounds(860,130,100,40);
		b3.setBackground(Color.blue);//need fix
		b3.addActionListener(this);
		
		b4 = new JButton("DOWN");
		b4.setBounds(860,170,100,40);
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
		topPanel.add(unitLabel);
		topPanel.add(tfOptional);
		topPanel.add(newMeterCBox);
		topPanel.add(l1);
		topPanel.add(l2);
		
		topPanel.setLayout(null);
		
		//For Photo
		photo = new JLabel();
		photo.setBounds(400,55,400,360);
		photo.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		photo.setHorizontalAlignment(SwingConstants.CENTER);
		//Get resized image as return value by calling customized rescaleImage()method and pass it as arg to icon
		ImageIcon imageIcon = new ImageIcon(rescaleImage("resources/images/default_photo.png",photo.getWidth(),photo.getHeight()));
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
		b7.addActionListener(this);
		
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
		this.getRootPane().setDefaultButton(b5);
		//when window is close,make sure cache is cleared
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//will save all the image files inside app's directory to server and delete them
				imageCache.getCacheManager().close();
			}
		});
	}
	@Override
	public void itemStateChanged(ItemEvent ie) {
		
		if(ie.getSource() == cbox) {
			if(!cbox.isSelected()) {
				cl.show(mainBottomPanel, "first");
				this.getRootPane().setDefaultButton(b5);
			}
			if(cbox.isSelected()) { 
				cl.show(mainBottomPanel, "second");
				this.getRootPane().setDefaultButton(b6);
			}
		}
		if(ie.getSource() == newMeterCBox) {
			if(!newMeterCBox.isSelected()) {
				tfOptional.setEnabled(false);
			}
			if(newMeterCBox.isSelected()) {
				tfOptional.setEnabled(true);
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent ae) {

		String floorName = b2.getText();
		String reading = "0";
		if(!tf.getText().isEmpty()) reading = tf.getText();
		String readingBeforeChange = "0";
		if(!tfOptional.getText().isEmpty()) readingBeforeChange = tfOptional.getText();
		
		//If floor button is pressed
		if(ae.getSource()==b2) {
			new ChoiceMenu(floor,this,b2);
			//this = an instance of input screen frame who implements CallBack interface and acts as observer and will be observing it's subject,choiceMenu
			
		}
		//if combobox is changed,text field will be refreshed
		if(ae.getSource()==cb) {
			Double readingDouble = operationForAE.getReading(cb.getSelectedIndex(), floorName);
			Double readingDoubleBeforeChange = operationForAE.getReadingBeforeChange(cb.getSelectedIndex(), floorName);
			if(readingDouble!=null) {
				reading = readingDouble.toString();
				readingBeforeChange = readingDoubleBeforeChange.toString();
				tf.setText(reading);
				tfOptional.setText(readingBeforeChange);
			}
			else {
				tf.setText("");
				tfOptional.setText("");
			}
			updatePhoto();
			//Changing display unit
			if(cb.getSelectedIndex()==0 || cb.getSelectedIndex()==1)
				unitLabel.setText(electricUnit);
			else unitLabel.setText(volumeUnit);
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
			String comboBoxState = cb.getSelectedItem().toString();
			if(comboBoxState != null) {
				switch(comboBoxState) {
				case "電灯" : operationForAE.setReadings(floorName, reading, readingBeforeChange, 0);
				cb.setSelectedItem(unitType[1]);refreshPage(floor.get(floorIndex));
				newMeterCBox.setSelected(false);
				tf.requestFocus();
				break;
				
				case "動力" : operationForAE.setReadings(floorName, reading, readingBeforeChange, 1);
				cb.setSelectedItem(unitType[2]);refreshPage(floor.get(floorIndex));
				newMeterCBox.setSelected(false);
				tf.requestFocus();
				break;
				
				case "水道" : operationForAE.setReadings(floorName, reading, readingBeforeChange, 2);
				cb.setSelectedItem(unitType[3]);refreshPage(floor.get(floorIndex));
				newMeterCBox.setSelected(false);
				tf.requestFocus();
				break;
				
				case "ガス" : operationForAE.setReadings(floorName, reading, readingBeforeChange, 3);
				tfOptional.setText("");
				newMeterCBox.setSelected(false);
				cb.setSelectedItem(unitType[0]);
				if(floorIndex<floor.size()-1) {
					floorIndex++;
					b2.setText(floor.get(floorIndex));
					refreshPage(floor.get(floorIndex));
				};
				tf.requestFocus();
				break;
				}
			}
		}
	//if upload button is pressed
		if(ae.getSource() == b7) {
			JFileChooser fileChooser = new JFileChooser();
			int option = fileChooser.showOpenDialog(null);
			
			if(option == JFileChooser.APPROVE_OPTION) {
				//Needs to edit
				File selectedFile = fileChooser.getSelectedFile();
				displayImage(selectedFile.getAbsolutePath());
				copyImage(selectedFile);
			}
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
	//Sub-program for confirmation menu
	public int confirmationMenu(String msg,String title) {
		//Creating a confirmation dialog box before moving to next screens
				ImageIcon decorativeIcon = new ImageIcon("resources/images/ask.png");
				Image decorativeImage = decorativeIcon.getImage();
				decorativeIcon = new ImageIcon(decorativeImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
				
				return JOptionPane.showConfirmDialog(null,msg, title, JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,decorativeIcon);
	}
	
	//Sub-program for converting LocalDate to String
			public String dateConverter(LocalDate readingDate) {
				if(readingDate == null) {
					return "";
				}
				return String.format("%4d年%1d月", readingDate.getYear(), readingDate.getMonthValue());
			}
	//Method to display image selected from file chooser
	public void displayImage(String path) {
		
		Image image = rescaleImage(path,photo.getWidth(),photo.getHeight());
		ImageIcon imageIcon = new ImageIcon(image);
		photo.setIcon(imageIcon);
	}
	//Method to copy image selected from file chooser to app's directory
	public String copyImage(File selectedFile) {	
		
		//File will be named in bldName_readingdate_floorname_readingtype
		String newFileName = imageFileNameBuilder();
		
		try(FileInputStream fis = new FileInputStream(selectedFile.getAbsolutePath());
			BufferedInputStream bis = new BufferedInputStream(fis);) {
			
			byte[] imageData = bis.readAllBytes();
			imageCache.getImageCache().put(newFileName, imageData);
			}
		catch(IOException e) {
			e.printStackTrace();
		}
		return newFileName;
	}
	
	//ImageFileNameBuilder
	public String imageFileNameBuilder() {

		String currMonth = String.format("%4d-%02d-01",readingDate.getYear(),readingDate.getMonthValue());
		String readingType = (String) cb.getSelectedItem();
		//File will be named in bldName_readingdate_floorname_readingtype
		return  buildingName+"_"+currMonth+"_"+floor.get(floorIndex)+"_"+readingType+".jpg";
	}
public void updatePhoto() {
	//Updating photo
	//File will be named in bldName_readingdate_floorname_readingtype
	String fileName = imageFileNameBuilder();
	if(imageCache.getImageCache().containsKey(fileName)) {
		byte[] imageData = imageCache.getImageCache().get(fileName);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
		try {
			BufferedImage bufferedImage = ImageIO.read(bis);
			ImageIcon imageIcon = new ImageIcon(bufferedImage);
			Image image = imageIcon.getImage();
			Image scaledImage = image.getScaledInstance(photo.getWidth(), photo.getHeight(), Image.SCALE_SMOOTH);
			ImageIcon newImageIcon = new ImageIcon(scaledImage);
			photo.setIcon(newImageIcon);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
    else displayImage("resources/images/default_photo.png");
}	
	//Observer will execute this method,once there is a change in it's subject
	//meaning if a button is clicked in BM01 or FM01,these methods will be invoked
	@Override
	public void onButtonClicked(String componentText,JButton b) {
		
		//componentText will accept the text of the button which was clicked on FM01
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
		
		String readingBeforeChange;
		Double readingDoubleBeforeChange = operationForAE.getReadingBeforeChange(cb.getSelectedIndex(), floorName);
		if(readingDoubleBeforeChange!=null) {
			readingBeforeChange = readingDoubleBeforeChange.toString();
			tfOptional.setText(readingBeforeChange);
		}
		else tfOptional.setText("");
		updatePhoto();
	}
}