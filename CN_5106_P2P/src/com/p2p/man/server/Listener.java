package com.p2p.man.server;

import java.net.ServerSocket;
import java.net.SocketException;

public class Listener extends Thread {
	Server server;
	int clientCount;
	ServerSocket socket = null;
	
	public Listener(Server server) {
		this.server = server;
	}
	
	public void run() {
		this.clientCount = 0;
		
		try {
			while (true) {
				try {
					socket = new ServerSocket(this.server.getPortNum());
					new Request(socket.accept(), clientCount, this.server).start();
					this.clientCount++;
				}
				catch(SocketException socket_exception) {
					Thread.sleep(50);				
				}
				finally {
					socket.close();
				}
			}
		}
		catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}	

}
