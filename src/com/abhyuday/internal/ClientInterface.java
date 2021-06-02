package com.abhyuday.internal;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientInterface extends JFrame implements Runnable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private JTextField txtMessage;
	private DefaultCaret caret;
	private JTextArea history;
	private Thread listen, run;
	
	private boolean running = false;
	
	private Client client;

	
	
	public ClientInterface(String name, String address, int port) {

		setTitle("The Internal");
	
		client = new Client(name,address,port);
		createWindow();
		this.setMinimumSize(getMinimumSize());
		

		boolean connect = client.openConnection(address);
		if(!connect) {
			System.err.println("Connection failed");
			console("Connection failed");
			return;
		}
		
		running = true;
		console("Successfully connected!");
		console("For the user:"+ name + " on the address: " + address + ":" + port + "!");
		
		run = new Thread(this, "Running");
		run.start();
		String connection = "/c/"+name;
		client.send(connection.getBytes());
	}

	private void createWindow() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{34, 726, 33, 7};
		gbl_contentPane.rowHeights = new int[]{50, 450, 50};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextArea();
		history.setEditable(false);		
		JScrollPane scrollPane = new JScrollPane(history);
		caret = (DefaultCaret)history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		// gbc_txtrHistory.insets = new Insets(0, 20, 0, 0);
		contentPane.add(scrollPane, scrollConstraints);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String message = txtMessage.getText();
					send(message);
				}
			}
		});
		
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2; 
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = txtMessage.getText();
				send(message);
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 2;
		contentPane.add(btnNewButton, gbc_btnNewButton);
		setVisible(true);
		
		txtMessage.requestFocusInWindow();
	}
	
	
	
	public void run() {
		listen();
	}
	
	public void listen() {
		listen = new Thread("Listen"){
			public void run() {
				while(running) {
					String message = client.receive();
					if(message.startsWith("/c/")) {
						client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
						console("Successfully connected to server! Id: "+ client.getId());
					}
				}
			}
		};
		listen.start();
	}
	
	private void send(String message) {
		if(message.equals("")) {
			return;
		}
		String msgString = client.getName() + ": " +message; 
		console(msgString);
		msgString = "/m/" + msgString;
		client.send(msgString.getBytes());
		txtMessage.setText("");		
	}
	
	public void console(String message) {
		history.append(message + "\n\r");
		history.setCaretPosition(history.getDocument().getLength());
	}
	

}
