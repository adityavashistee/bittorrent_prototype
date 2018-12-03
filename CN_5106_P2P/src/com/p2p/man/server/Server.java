package com.p2p.man.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.p2p.man.message.MessageHandler;

public class Server {
	private int portNum;
	public List<MessageHandler> receivedMessages;
	
	
	public Server(int portNum) {
		this.portNum=portNum;
		this.receivedMessages = Collections.synchronizedList(new ArrayList<MessageHandler>());
		new Listener(this).start();
	}


	public int getPortNum() {
		return portNum;
	}


	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}
			
	
	
	
}
