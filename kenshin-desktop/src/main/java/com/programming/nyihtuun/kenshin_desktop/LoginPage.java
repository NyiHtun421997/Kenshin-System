package com.programming.nyihtuun.kenshin_desktop;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginPage extends JFrame implements ActionListener{
	
	public static void main(String arg[]) {
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
			try {
				new LoginPage();
} 
			catch (Exception e) {
					e.printStackTrace();

}}});
}
	
	JButton jb1,jb2;
	JTextField tf1,tf3;
	JPasswordField tf2;
	JLabel l1,l2,l3,message_Label;
	private String serverIP;
	
	//Constructor
	LoginPage()
	{
		jb1=new JButton("Login");
		jb2=new JButton("Reset");
		
		//UserID
		tf1=new JTextField();
		l1=new JLabel("User ID: ");
		l1.setBounds(50,100,75,25);
		tf1.setBounds(130,100,200,25);
		
		
		//Password
		tf2=new JPasswordField();
		tf2.setEchoChar('*');
		l2=new JLabel("Password: ");
		l2.setBounds(50,150,75,25);
		tf2.setBounds(130,150,200,25);
		
		//ServerIpAddress
		tf3=new JTextField();
		l3=new JLabel("Server IP: ");
		l3.setBounds(50,200,75,25);
		tf3.setBounds(130,200,200,25);
		
		//Buttons
		jb1.setBounds(130,250,100,25);
		jb1.addActionListener(this);
		jb2.setBounds(230,250,100,25);
		jb2.addActionListener(this);
		
		//Message Text
		message_Label=new JLabel();
		message_Label.setBounds(40,300,300,35);
		message_Label.setHorizontalAlignment(SwingConstants.CENTER);
		message_Label.setFont(new Font(null,Font.ITALIC,14));
		
		add(l1);
		add(l2);
		add(l3);
		add(message_Label);
		add(tf1);
		add(tf2);
		add(tf3);
		add(jb1);
		add(jb2);
		
		setLayout(null);
		setSize(400,400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		//if Reset button is pressed
		if(e.getSource()==jb2)
		{
			tf1.setText("");
			tf2.setText("");
			message_Label.setText("");
		}
		//if Login button is pressed
		if(e.getSource()==jb1)
		{
			String username=tf1.getText();
			String password=String.valueOf(tf2.getText());
			serverIP = tf3.getText();
			String jwt;
			AuthRequest authRequest = new AuthRequest(username,password);
			HttpResponse<String> response = HttpService.loginMethod(authRequest,serverIP);
			
				if(response.statusCode() == 200)
				{
					message_Label.setForeground(Color.green);
					message_Label.setText("Login successful!");
					jwt = response.body();
					System.out.println(jwt);
					TokenManager tokenManager = new TokenManager(jwt);
					HttpService httpService = new HttpService(tokenManager,serverIP);
					new MainMenu(httpService);
					this.dispose();
				}
				if(response.statusCode() == 401)
				{
					message_Label.setForeground(Color.red);
					message_Label.setText(response.body());
				}
			}
			
		
	}

}
