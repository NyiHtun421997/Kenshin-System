package com.system.kenshin;

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
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;


public class mainMenu extends JFrame implements ActionListener,CallBack{
	
	JButton inputButton,checkButton,buildingButton;
	JLabel 	dateJLabel,inputLabel,checkLabel,buildingLabel1,buildingLabel2;

	//these will be inside main menu which will call constructor of InputScreen
	String dateLabel = "2023年9月";//must follow this ○年○月 pattern
	//will be populated from server
	List<String> buildingName = new ArrayList<String>(List.of("BRAVI北浜","A-PLACE","ビル博丈","a","b","c","d","e","f","g","h","a","b","c","d","e","f","g","GTL尼崎新都心"));
	List<String> floor = new ArrayList<String>(List.of("駐車場","1F","2F","3F"));
	
	mainMenu(){
		
		super("Main Menu");
		setLayout(null);
		Container contentPane = this.getContentPane();
		
		dateJLabel = new JLabel("2023年9月");
		dateJLabel.setBounds(405,20,120,40);
		dateJLabel.setFont(new Font("Ariel",Font.BOLD,18));
		
		inputButton = new JButton();
		inputButton.setBounds(210,70,200,200);
		inputButton.setOpaque(true);
		inputButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		inputButton.setBackground(new Color(237, 244, 255));
		ImageIcon imageIcon = new ImageIcon(rescaleImage("resources/images/input.png",inputButton));
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
		imageIcon = new ImageIcon(rescaleImage("resources/images/check.png",inputButton));
		checkButton.setIcon(imageIcon);
		//checkButton.addActionListener(this);

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
		imageIcon = new ImageIcon(rescaleImage("resources/images/buildings.png",buildingButton));
		buildingButton.setIcon(imageIcon);
		buildingButton.addActionListener(this);
		
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
		
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getSource()==inputButton) {
			if(buildingLabel2.getText()!="") {
				InputScreen inputScreen = new InputScreen(buildingLabel2.getText(),dateLabel,floor);
				this.dispose();
			}
			
		}
		if(ae.getSource()==buildingButton) {
			
			BuildingMenu BM01 = new BuildingMenu(buildingName,this,buildingButton);
		}
		
	}
	public static void main(String[] arg) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
			try {
				mainMenu mMenu = new mainMenu();
				mMenu.setSize(900,500);
				mMenu.setVisible(true);
				mMenu.setResizable(false);
				mMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
} 
			catch (Exception e) {
					e.printStackTrace();

}}});}
		
		
	
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
	@Override
	public void onButtonClicked(String componentText, JButton b) {
		
		buildingLabel2.setText(componentText);
		
	}

}
