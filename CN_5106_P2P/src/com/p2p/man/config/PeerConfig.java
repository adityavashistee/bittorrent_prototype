package com.p2p.man.config;
public class PeerConfig {
	private int peerId;
	private String hostName;
	private int portNum;
	private boolean hasEntireFile;
	
	public int getPeerId() {
		return peerId;
	}
	public void setPeerId(int peerId) {
		this.peerId = peerId;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public int getPortNum() {
		return portNum;
	}
	public void setPortNum(int port) {
		this.portNum = port;
	}
	public boolean isHasEntireFile() {
		return hasEntireFile;
	}
	public void setHasEntireFile(boolean hasEntireFile) {
		this.hasEntireFile = hasEntireFile;
	}
	
	
	
}
