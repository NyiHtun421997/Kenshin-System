package com.programming.nyihtuun.kenshin_desktop;

import java.awt.Image;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CustomException extends RuntimeException {
	
	public CustomException(String message) {
		super(message);
		new ErrorMessageFrame(message);
	}
	
	public CustomException(Exception e) {
		if(e instanceof ConnectException) {
			new ErrorMessageFrame("Not connected to server. Check you connection.");
		}
		else if(e instanceof UnknownHostException) {
			new ErrorMessageFrame("Server Not Found.");
		}
		else {
			new ErrorMessageFrame("An error occurred: "+e.getMessage());
		}
	}

}
class ErrorMessageFrame extends JFrame{
	
	String message;
	public ErrorMessageFrame(String message) {
		this.message = message;
		
		ImageIcon decorativeIcon = new ImageIcon("resources/images/ask.png");
		Image decorativeImage = decorativeIcon.getImage();
		decorativeIcon = new ImageIcon(decorativeImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

	    JOptionPane.showMessageDialog(null, "Error: "+message,"Error",JOptionPane.ERROR_MESSAGE);
	}
	
}