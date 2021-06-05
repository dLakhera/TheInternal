package com.abhyuday.internal.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
		Scanner scanner = new Scanner(System.in);
		while(running) {
			String input = scanner.nextLine();
			if(!input.startsWith("/")) {
				broadcast("/m/Server: " + input + "/e/");
				continue;
			}
			if(input.startsWith("/kick")) {
				input = input.substring(6,input.length());
				disconnect(Integer.parseInt(input), 2);
			}
			else if(input.startsWith("/show")) {
				System.out.println("Names \t Ids");
				for(int i=0;i<clients.size();i++) {
					System.out.println(clients.get(i).getName() + "\t" + clients.get(i).getID());
				}
			} 
			else if(input.startsWith("/end") || input.startsWith("/exit") || input.startsWith("/destroy")) {
				for(int i=0;i<clients.size();i++) {
					disconnect(clients.get(i).getID(), 3);
				}
				System.out.println("Successfully kicked all clients!");
				System.exit(0);
			}
		}
	}

	private void managerClients() {
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
								disconnect(c.getID(), 1);
							}else {
								c.attempt++;							
							}
						} else {
							clientResponse.remove(new Integer(c.getID()));
							c.attempt = 0;
						}
					}
					
					System.out.println(clients.size());
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
			disconnect(Integer.parseInt(id),0);
		} else if(string.startsWith("/i/")) {
			clientResponse.add(Integer.parseInt(string.split("/i/|/e/")[1]));
		}
	}

	private void disconnect(int id, int status) {
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
		
		if(status == 0) {
			message = "/d/Client " + exp.name + " disconnected./e/"; 
			broadcast(message);
		} else if(status == 1) {
			message = "/d/Client " + exp.name + " timed out./e/"; 
			broadcast(message);
		} else if(status == 2) {
			message = "/d/Client: " + exp.name + " got kicked by Server Admin./e/";
			broadcast(message);
		} else if(status == 3) {
			message = "/d/Server has terminated for now! See you around tho :)/e/";
			broadcast(message);
		}
		System.out.println("this is the braodcast message: " + message);
		
	}
}
