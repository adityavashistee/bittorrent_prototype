package com.p2p.man.conn;

import java.util.ArrayList;
import java.util.BitSet;

public class NeighborState {
	private int peerID;
	private String hostName;
	private int portNumber;
	private ArrayList<Byte> availablePieces;
	
	private boolean connRefused;
	private boolean hasConn;
	private int pieceNumber;
	private boolean isWaiting;
	private boolean hasFile;
	private boolean sentHandshake;
	private boolean sentBitfield;
	private boolean receivedHandshake;
	private boolean receivedBitField;
	private boolean isInterested;
	private boolean isChoked;
	
	public NeighborState() {
		super();
		this.hostName = "";
		this.connRefused = false;
		this.hasConn = false;
		this.pieceNumber = -1;
		this.isWaiting = false;
		this.hasFile = false;
		this.sentHandshake = false;
		this.sentBitfield = false;
		this.receivedHandshake = false;
		this.receivedBitField = false;
		this.isInterested = false;
		this.isChoked = true;
	}

	public int getPeerID() {
		return peerID;
	}

	public void setPeerID(int peerID) {
		this.peerID = peerID;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public ArrayList<Byte> getAvailablePieces() {
		return availablePieces;
	}

	public void setAvailablePieces(ArrayList<Byte> bitMap) {
		this.availablePieces = bitMap;
	}

	public boolean isConnRefused() {
		return connRefused;
	}

	public void setConnRefused(boolean connRefused) {
		this.connRefused = connRefused;
	}

	public boolean isHasConn() {
		return hasConn;
	}

	public void setHasConn(boolean hasConn) {
		this.hasConn = hasConn;
	}

	public int getPieceNumber() {
		return pieceNumber;
	}

	public void setPieceNumber(int pieceNumber) {
		this.pieceNumber = pieceNumber;
	}

	public boolean isWaiting() {
		return isWaiting;
	}

	public void setWaiting(boolean isWaiting) {
		this.isWaiting = isWaiting;
	}

	public boolean isHasFile() {
		return hasFile;
	}

	public void setHasFile(boolean hasFile) {
		this.hasFile = hasFile;
	}

	public boolean isSentHandshake() {
		return sentHandshake;
	}

	public void setSentHandshake(boolean sentHandshake) {
		this.sentHandshake = sentHandshake;
	}

	public boolean isSentBitfield() {
		return sentBitfield;
	}

	public void setSentBitfield(boolean sentBitfield) {
		this.sentBitfield = sentBitfield;
	}

	public boolean isReceivedHandshake() {
		return receivedHandshake;
	}

	public void setReceivedHandshake(boolean receivedHandshake) {
		this.receivedHandshake = receivedHandshake;
	}

	public boolean isReceivedBitField() {
		return receivedBitField;
	}

	public void setReceivedBitField(boolean receivedBitField) {
		this.receivedBitField = receivedBitField;
	}

	public boolean isInterested() {
		return isInterested;
	}

	public void setInterested(boolean isInterested) {
		this.isInterested = isInterested;
	}

	public boolean isChoked() {
		return isChoked;
	}

	public void setChoked(boolean isChoked) {
		this.isChoked = isChoked;
	}
	
	
}
