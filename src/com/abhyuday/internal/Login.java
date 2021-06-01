package com.abhyuday.internal;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Login extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtAddress;
	private JLabel lblIPAdress;
	private JTextField txtPort;
	private JLabel lblPort;
	private JLabel lblAddressDesc;
	private JLabel lblPortDesc;

	/**
	 * Create the frame.
	 */
	public Login() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 420);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setBounds(69, 51, 162, 26);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(119, 34, 61, 16);
		contentPane.add(lblName);
		
		txtAddress = new JTextField();
		txtAddress.setBounds(69, 134, 162, 26);
		contentPane.add(txtAddress);
		txtAddress.setColumns(10);
		
		lblIPAdress = new JLabel("IP Address:");
		lblIPAdress.setBounds(119, 116, 70, 16);
		contentPane.add(lblIPAdress);
		
		txtPort = new JTextField();
		txtPort.setBounds(69, 224, 162, 26);
		contentPane.add(txtPort);
		txtPort.setColumns(10);
		
		lblPort = new JLabel("Port:");
		lblPort.setBounds(135, 207, 30, 16);
		contentPane.add(lblPort);
		
		lblAddressDesc = new JLabel("(e.g 192.168.0.1)");
		lblAddressDesc.setBounds(98, 160, 107, 16);
		contentPane.add(lblAddressDesc);
		
		lblPortDesc = new JLabel("(e.g 3443)");
		lblPortDesc.setBounds(115, 251, 69, 16);
		contentPane.add(lblPortDesc);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				String nameString = txtName.getText();
				String addressString = txtAddress.getText();
				int port = Integer.parseInt(txtPort.getText());
				
				login(nameString, addressString, port);
			}
		});
		
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nameString = txtName.getText();
				String addressString = txtAddress.getText();
				int port = Integer.parseInt(txtPort.getText());
				
				login(nameString,addressString,port);
			}

			
		});
		
		btnLogin.setBounds(91, 327, 117, 29);
		contentPane.add(btnLogin);
	}

	
	/*
	 * Login logic here
	 */
	private void login(String name, String address, int port) {
		dispose();
		new Client(name,address,port);
	}
	
	/**
	 * Launch the application.
	 */	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
