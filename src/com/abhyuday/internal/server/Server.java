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
	private List<Integer> clientResponse = new ArrayList<Integer> ();
	
	private final int MAX_ATTEMPTS = 10;
	
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
			@SuppressWarnings("deprecation")
			public void run() {
				while(running) {
					String conn = "/i/server";
					broadcast(conn);
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					for(int i=0;i<clients.size();i++) {
						ServerClient c = clients.get(i);
						if(!clientResponse.contains(c.getID())) {
							if((int)c.attempt > MAX_ATTEMPTS) {
								disconnect(c.getID(), false);
							}else {
								c.attempt++;							
							}
						} else {
							clientResponse.remove(new Integer(c.getID()));
							c.attempt = 0;
						}
					}
				}
			}
		};
		manage.start();
	}
	

	private void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while(running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		};
		
		receive.start();
	}
	
	private void broadcast(String messageString) {
		for(int i=0;i<clients.size();i++) {
			ServerClient client = clients.get(i);
			send(messageString, client.ipAddress, client.port);
		}
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
			broadcast(string);
		} else if(string.startsWith("/d/")){
			System.out.println("Disconnection request received");
			System.out.println("int the process func: " + string);
			String id = string.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(id),true);
		} else if(string.startsWith("/i/")) {
			clientResponse.add(Integer.parseInt(string.split("/i/|/e/")[1]));
		}
	}

	private void disconnect(int id, boolean status) {
		int i=0;
		ServerClient exp = null;
		for(i=0;i<clients.size();i++) {
			if(clients.get(i).getID() == id) {
				exp = clients.get(i);
				clients.remove(i);
				break;
			}
		}
		String message ="";
		
		if(status) {
			message = "/d/Client " + exp.name + " disconnected./e/"; 
			broadcast(message);
		} else {
			message = "/d/Client " + exp.name + " timed out./e/"; 
			broadcast(message);
		}
		System.out.println("this is the braodcast message: " + message);
		
	}
}
