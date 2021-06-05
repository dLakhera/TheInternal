package com.abhyuday.internal.server;

import java.net.InetAddress;

public class ServerClient {
	public String name;
	public InetAddress ipAddress;
	public int port;
	
	private final int ID;
	
	public int attempt = 0;
	
	public ServerClient(String name, InetAddress ip, int port, final int ID) {
		this.ID = ID;
		this.name = name;
		this.ipAddress = ip;
		this.port = port;
	}
	
	public int getID() {
		return ID;
	}
	
	public String getName() {
		return name;
	}
}
