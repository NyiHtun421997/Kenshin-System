package com.system.kenshin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class mainMenu extends JFrame implements ActionListener{
	
	JButton inputButton,checkButton,buildingButton;
	JLabel 	dateJLabel,buildingLabel;

	//these will be inside main menu which will call constructor of InputScreen
	String buildingName = "BRAVI北浜";
	String dateLabel = "2023年9月";//must follow this ○年○月 pattern
	//will be populated from server
	List<String> floor = new ArrayList<String>(List.of("駐車場","1F","2F","3F"));
	
	mainMenu(){
		
		super("Main Menu");
		setLayout(null);
		
		dateJLabel = new JLabel("2023年9月");
		dateJLabel.setBounds(405,20,120,40);
		dateJLabel.setFont(new Font("Ariel",Font.BOLD,18));
		
		inputButton = new JButton("入力");
		inputButton.setFont(new Font("Ariel",Font.BOLD,18));
		inputButton.setBackground(Color.BLUE);
		inputButton.setBounds(210,70,200,200);
		inputButton.addActionListener(this);
		
		checkButton = new JButton("チェック");
		checkButton.setFont(new Font("Ariel",Font.BOLD,18));
		checkButton.setBackground(Color.BLUE);
		checkButton.setBounds(500,70,200,200);
		
		buildingLabel = new JLabel("建物名");
		buildingLabel.setBounds(435,280,140,40);
		buildingLabel.setFont(new Font("Ariel",Font.BOLD,14));
		
		buildingButton = new JButton();
		buildingButton.setFont(new Font("Ariel",Font.BOLD,14));
		buildingButton.setBounds(410,330,90,90);
		
		add(dateJLabel);
		add(buildingLabel);
		add(inputButton);
		add(checkButton);
		add(buildingButton);
		
		
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getSource()==inputButton) {
			
			InputScreen inputScreen = new InputScreen(buildingName,dateLabel,floor);
			this.dispose();
		}
		
	}
	public static void main(String[] arg) {
		
		mainMenu mMenu = new mainMenu();
		mMenu.setSize(900,500);
		mMenu.setVisible(true);
		mMenu.setResizable(false);
		mMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
