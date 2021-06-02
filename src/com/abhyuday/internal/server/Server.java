package com.abhyuday.internal.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	
	private DatagramSocket socket;
	private int port;

	private Thread run, manage, send, receive;
	public boolean running = false;
	
	public Server(int port){
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		run = new Thread(this, "Server");
		run.start();
	}
	
	public void run() {
		running = true;
		System.out.println("Running! On port: "+ port);
		managerClients();
		receive();
	}

	private void managerClients() {
		// TODO Auto-generated method stub
		manage = new Thread("Manage") {
			public void run() {
				while(running) {
					
				}
			}
		};
		manage.start();
	}
	

	private void receive() {
		// TODO Auto-generated method stub
		receive = new Thread("Receive") {
			public void run() {
				while(running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					
					try {
						socket.receive(packet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					process(packet);
					
					// Sending it to the console
					// clients.add(new ServerClient("droidlakhera", packet.getAddress(), packet.getPort(),50));
					// System.out.println(clients.get(0).ipAddress.toString() + ":" + clients.get(0).port);
				}
			}
		};
		
		receive.start();
	}
	
	private void sendToAll(String messageString) {
		for(int i=0;i<clients.size();i++) {
			ServerClient client = clients.get(i);
			send(messageString, client.ipAddress, client.port);
		}
		System.out.println(messageString);
	}
	
	private void send(final byte[] data, final InetAddress ipAddress, final int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data,data.length,ipAddress, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		send.start();
	}
	
	private void send(String message, InetAddress address, int port) {
		message+= "/e/";
		send(message.getBytes(),address, port);
	}
	
	private void process(DatagramPacket packet) {
		String string = new String(packet.getData());
		
		if(string.startsWith("/c/")) {
	
			String name = string.substring(3,string.length());
			int id = UniqueIdentifiers.getIdentifiers();
			clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(),id));
			System.out.println("Connection packet: " + name + " with id: " + id);
			
			String confirmationString = "/c/"+id;
			send(confirmationString, packet.getAddress(), packet.getPort());
			
		} else if(string.startsWith("/m/")){
			sendToAll(string);
		} else {
			
		}
	}
}
