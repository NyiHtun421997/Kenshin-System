package com.programming.nyihtuun.kenshin_desktop;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class BuildingMenu extends JFrame implements ActionListener {
	
	//demo Array
	
	JButton[] buildingIcon;
	JLabel[] buildingLabel;
	JPanel buttonPanel;
	JScrollPane sp;
	JButton b1;
	private CallBack callBack;
	
	
	BuildingMenu(List<String> buildingName,CallBack callBack,JButton b1){
		
		super("Input Menu");
		this.callBack = callBack;
		this.b1 = b1;
		this.setSize(900,500);
		this.setVisible(true);
		this.setResizable(true);
		
		buttonPanel = new JPanel(new GridLayout((buildingName.size()/4)+1,4,10,10));
		//for adding BuildingIcon
		BufferedImage img ;
		ImageIcon imageIcon=null;
		try{
			img = ImageIO.read(BuildingMenu.class.getResourceAsStream("/images/building_icon.png"));
			Image resizedImage = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(resizedImage);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		//creating buttons
		buildingIcon = new JButton[buildingName.size()];
		for(int i = 0; i < buildingName.size(); i++) {
			buildingIcon[i] = new JButton();
			buildingIcon[i].setText(buildingName.get(i));
			buildingIcon[i].setIcon(imageIcon);
			buildingIcon[i].setVerticalTextPosition(JButton.BOTTOM);
			buildingIcon[i].setHorizontalTextPosition(JButton.CENTER);
			buttonPanel.add(buildingIcon[i]);
			buildingIcon[i].addActionListener(this);
			
		}
		sp = new JScrollPane(buttonPanel);
		// Set the vertical scrollbar policy to "as needed"
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
     // Set the horizontal scrollbar policy to "never" to turn it off
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


        // Set the preferred size of the JScrollPane to enable scrolling
        sp.setPreferredSize(new Dimension(900, 500));
		this.add(sp);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String bldName = ((JButton)e.getSource()).getText();
		callBack.onButtonClicked(bldName,b1);
		
		this.dispose();
		
	}
}