package com.abhyuday.internal.server;

import java.io.Console;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import java.util.List;
import java.util.UUID;

public class Server implements Runnable {
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	
	private DatagramSocket socket;
	private int port;

	private Thread run, manage, send, receive;
	public boolean running = false;
	
	Server(int port){
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
	
	
	private void process(DatagramPacket packet) {
		String string = new String(packet.getData());
		
		if(string.startsWith("/c/")) {
			
			// Creating random ids: Method 1 =>
			// UUID id = UUID.randomUUID();
			
			// Creating random ids: Method 2 =>
			// using the uniqueIdentifier class that we made
			
			
			String name = string.substring(3,string.length());
			int id = UniqueIdentifiers.getIdentifiers();
			clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(),id));
			System.out.println("Connection packet: " + name + " with id: " + id);
		} else {
			System.out.println(string);
		}
	}
	
	

}
