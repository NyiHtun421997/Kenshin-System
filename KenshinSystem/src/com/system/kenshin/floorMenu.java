package com.system.kenshin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class floorMenu extends JFrame implements ActionListener {
	
	JButton[] floorIcon;
	JLabel[] floorLabel;
	JPanel buttonPanel;
	JScrollPane sp;
	JButton b2;
	private CallBack callBack;
	
	
	floorMenu(List<String> floor,CallBack callBack,JButton b2){
		
		super("Input Menu");
		this.callBack = callBack;
		this.b2 = b2;
		this.setSize(900,500);
		this.setVisible(true);
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		buttonPanel = new JPanel(new GridLayout((floor.size()/4)+1,4,10,10));
		//for adding floorIcon
		BufferedImage img ;
		ImageIcon imageIcon=null;
		try{
			img = ImageIO.read(new File("resources/images/floor_icon.png"));
			Image resizedImage = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(resizedImage);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		//creating buttons
		floorIcon = new JButton[floor.size()];
		for(int i = 0; i < floor.size(); i++) {
			floorIcon[i] = new JButton();
			floorIcon[i].setText(floor.get(i));
			floorIcon[i].setIcon(imageIcon);
			buttonPanel.add(floorIcon[i]);
			floorIcon[i].addActionListener(this);
			
		}
		sp = new JScrollPane(buttonPanel);
		// Set the vertical scrollbar policy to "as needed"
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
     // Set the horizontal scrollbar policy to "never" to turn it off
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        // Set the preferred size of the JScrollPane to enable scrolling
        sp.setPreferredSize(new Dimension(900, 500));
		this.add(sp);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String floorName = ((JButton)e.getSource()).getText();
		System.out.println(floorName);
		//invoking observer method
		callBack.onButtonClicked(floorName,b2);
		this.dispose();
		
	}
}
