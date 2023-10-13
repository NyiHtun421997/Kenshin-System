package com.system.kenshin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

public class CheckMenu {
	
	public static void main(String[] args) {
		String buildingName = "Sample Building C";
		LocalDate readingDate = LocalDate.of(2023, 11, 1);//must follow this ○年○月 pattern
		//will be populated from server
		List<String> floor = new ArrayList<String>(List.of("1F","2F","3F"));
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ReadingOperation operationForAE = new Operation();
					operationForAE.startOperation(buildingName, readingDate, floor);
					CheckMenuFrame f = new CheckMenuFrame(operationForAE,buildingName,readingDate,floor);
					f.setSize(1200,800);
					f.setVisible(true);
					f.setResizable(false);
					f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	} 
				catch (Exception e) {
						e.printStackTrace();
}}});}

	
	public CheckMenu(ReadingOperation operationForAE, String buildingName, LocalDate readingDate, List<String> floor) {
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					CheckMenuFrame f = new CheckMenuFrame(operationForAE,buildingName,readingDate,floor);
					f.setSize(1200,800);
					f.setVisible(true);
					f.setResizable(false);
					f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	} 
				catch (Exception e) {
						e.printStackTrace();
}}});}}
	
	class CheckMenuFrame extends JFrame implements ItemListener,ActionListener,CallBack{
		
		JPanel topPanel,bottomPanel;
		JButton b2,b3,b4,b5,photo;
		JLabel b1,l1,l2,cboxLabel,commentLabel;
		JComboBox cb;
		JFormattedTextField tf,tfOptional;
		JCheckBox newMeterCBox;
		JTextArea commentBox;
		
		private ImageCache imageCache;
		private String buildingName;
		private LocalDate readingDate;
		private List<String> floor;
		
		private String unitType [] = {"電灯","動力","水道","ガス"};
		private ReadingOperation operationForAE;
		
		private static int floorIndex = 0;
		
		CheckMenuFrame(ReadingOperation operationForAE,String buildingName, LocalDate readingDate, List<String> floor){
			super("Check Menu");
			Container contentPane = this.getContentPane();
			contentPane.setLayout(new BorderLayout());
			  
			this.operationForAE = operationForAE;
			this.buildingName = buildingName;
			this.readingDate = readingDate;
			this.floor = floor;
			imageCache = new ImageCache();
			
			JMenuBar menuBar = new JMenuBar();
			JMenu fileMenu = new JMenu("File");
			JMenuItem saveMenu = new JMenuItem("Save");
			fileMenu.add(saveMenu);
			menuBar.add(fileMenu);
			menuBar.setBackground(Color.blue);
			
			setJMenuBar(menuBar);
			
			saveMenu.addActionListener((ActionEvent ae)->{
			
				//call storeToTempMap method for lastetMonth
				if(operationForAE.isLatestMonth())
					HttpService.storeToTempMap(operationForAE.getAllReadings());
				
				//call HttpService method to update DB for non-latestMonths
				else {
					HttpService.updateReadings(buildingName,String.format("%4d-%02d-01", readingDate.getYear(), readingDate.getMonthValue()) , floor.get(floorIndex), operationForAE.getAllReadings());
				}			
				//Creating a confirmation dialog box before moving to CS01
				ImageIcon decorativeIcon = new ImageIcon("resources/images/ask.png");
				Image decorativeImage = decorativeIcon.getImage();
				decorativeIcon = new ImageIcon(decorativeImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
		
		        int choice = JOptionPane.showConfirmDialog(null,"Do you want to proceed?", "Confirmation", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,decorativeIcon);
				
		        if(choice == JOptionPane.YES_OPTION) {
		        	//will save all the image files inside app's directory to server and delete them
		        	try{
		        		//calls HttpService method to update images
		        		HttpService.updateImages();
		        	}
		        	catch(IOException e) {
		        		e.printStackTrace();
		        		throw new CustomException(e);
		        		//Shows error page
		        	}
		        	catch(CustomException e) {
		        		new CustomException(e.getMessage());
		        	}
		        	//after saving close this window and jump to CS01
					new CompareScreen(buildingName,readingDate);
					this.dispose();
		        }
		        else {}
				
			});
			
			//Label for buildings
			b1 = new JLabel(buildingName);
			b1.setFont(new Font("Ariel",Font.BOLD,18));
			b1.setBounds(400,5,200,40);
			
			//Button for Floors
			b2 = new JButton(floor.get(floorIndex));
			b2.setBounds(540,45,120,80);
			b2.setOpaque(true);
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
						b5.doClick();
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
			
			//Labels for comment boxes
			 commentLabel = new JLabel("Comment");
			 commentLabel.setBounds(510,210,180,50);
			 commentLabel.setFont(new Font("Ariel",Font.PLAIN,14));
			 commentLabel.setHorizontalAlignment(SwingConstants.CENTER);
			
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
			topPanel.add(tfOptional);
			topPanel.add(newMeterCBox);
			topPanel.add(l1);
			topPanel.add(l2);
			topPanel.add(commentLabel);
			
			topPanel.setLayout(null);
			
			//For comment box
			commentBox = new JTextArea(10,20);
			commentBox.setFont(new Font(null,Font.PLAIN,16));
			JScrollPane oldScrollPane = new JScrollPane(commentBox);
			oldScrollPane.setBounds(400, 0, 400, 100);
			
			//For Photo
			photo = new JButton();
			photo.setBounds(425,115,350,310);
			photo.setOpaque(true);
			photo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			photo.setBackground(new Color(237, 244, 255));
			photo.setHorizontalAlignment(SwingConstants.CENTER);
			//Get resized image as return value by calling customized rescaleImage()method and pass it as arg to icon
			ImageIcon imageIcon = new ImageIcon(rescaleImage("resources/images/default_photo.png",photo.getWidth(),photo.getHeight()));
			photo.setIcon(imageIcon);
			photo.addActionListener(this);
			
			contentPane.add(topPanel,BorderLayout.NORTH);
			
			//Button for Confirm
			b5 = new JButton("Confirm");
			b5.setHorizontalAlignment(SwingConstants.CENTER);
			b5.addActionListener(this);
			
			bottomPanel = new JPanel();
			bottomPanel.setLayout(null);
			bottomPanel.add(photo);
			bottomPanel.add(b5).setBounds(510,435,180,50);
			bottomPanel.add(oldScrollPane);
			bottomPanel.setBackground(new Color(237, 244, 255));
			
			
			contentPane.add(bottomPanel,BorderLayout.CENTER);
			this.getRootPane().setDefaultButton(b5);
			//when window is close,make sure cache is cleared
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					imageCache.getCacheManager().close();
				}
			});
			refreshPage(floor.get(floorIndex));
		}
		@Override
		public void itemStateChanged(ItemEvent ie) {
			
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
			String reading="";
			if(!tf.getText().isEmpty()) reading = tf.getText();
			String readingBeforeChange="";
			if(!tfOptional.getText().isEmpty()) readingBeforeChange = tfOptional.getText();
			
			//If floor button is pressed
			if(ae.getSource()==b2) {
				ChoiceMenu FM01 = new ChoiceMenu(floor,this,b2);
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
			if(ae.getSource()==b5 ) {
				//Check current state of ComboBox
				String comboBoxState = cb.getSelectedItem().toString();
				if(comboBoxState != null) {
					switch(comboBoxState) {
					case "電灯" : operationForAE.setReadings(floorName, reading, readingBeforeChange, 0);
					operationForAE.setComments(floorName, commentBox.getText());
					cb.setSelectedItem(unitType[1]);refreshPage(floor.get(floorIndex));
					newMeterCBox.setSelected(false);
					tf.requestFocus();
					break;
					
					case "動力" : operationForAE.setReadings(floorName, reading, readingBeforeChange, 1);
					operationForAE.setComments(floorName, commentBox.getText());
					cb.setSelectedItem(unitType[2]);refreshPage(floor.get(floorIndex));
					newMeterCBox.setSelected(false);
					tf.requestFocus();
					break;
					
					case "水道" : operationForAE.setReadings(floorName, reading, readingBeforeChange, 2);
					operationForAE.setComments(floorName, commentBox.getText());
					cb.setSelectedItem(unitType[3]);refreshPage(floor.get(floorIndex));
					newMeterCBox.setSelected(false);
					tf.requestFocus();
					break;
					
					case "ガス" : operationForAE.setReadings(floorName, reading, readingBeforeChange, 3);
					operationForAE.setComments(floorName, commentBox.getText());
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
			if(ae.getSource() == photo) {
				JFileChooser fileChooser = new JFileChooser();
				int option = fileChooser.showOpenDialog(null);
				
				if(option == JFileChooser.APPROVE_OPTION) {
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
		public void copyImage(File selectedFile) {	
			
			//File will be named in bldName_readingdate_floorname_readingtype
			String newFileName = imageFileNameBuilder();
			String destinationFolder = "resources/user_input";

	        // Construct the full path of the destination file
	        String destinationPath = destinationFolder + File.separator + newFileName;
			
			System.out.println(newFileName);
			
			try(FileInputStream fis = new FileInputStream(selectedFile.getAbsolutePath());
				BufferedInputStream bis = new BufferedInputStream(fis);
				FileOutputStream fos = new FileOutputStream(destinationPath);
				BufferedOutputStream bos = new BufferedOutputStream(fos);) {
				
				int b;
				while((b=bis.read())!=-1) {
					bos.write(b);
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
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
			//look for the image in cache
			if(!imageCache.getImageCache().containsKey(fileName)) {
				//call HttpService method to look for the image with this unique file name in DB
				try {
					System.out.println("**********");
					byte[] imageData = HttpService.getImages(fileName);
					System.out.println(imageData);
					if(imageData!=null) {
						imageCache.getImageCache().put(fileName, imageData);
					}
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			
			byte[] imageData = imageCache.getImageCache().get(fileName);
			System.out.println(fileName+"\n"+imageData);
			
	        if(imageData!=null) {
	        	ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
	        	try {
					BufferedImage bufferedImage = ImageIO.read(bis);
					ImageIcon imageIcon = new ImageIcon(bufferedImage);
					Image image = imageIcon.getImage();
					Image scaledImage = image.getScaledInstance(photo.getWidth(), photo.getHeight(), Image.SCALE_SMOOTH);
					ImageIcon newImageIcon = new ImageIcon(scaledImage);
					photo.setIcon(newImageIcon);
				} catch (IOException e) {
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
				b2.setText(componentText);
				//resetting floorIndex
				floorIndex = floor.indexOf(componentText);
				refreshPage(componentText);	
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
			
			String comment = operationForAE.getComments(floorName);
			if(comment!=null) {
				commentBox.setText(comment);
			}
			else commentBox.setText("");
			
			updatePhoto();
		}
	}



