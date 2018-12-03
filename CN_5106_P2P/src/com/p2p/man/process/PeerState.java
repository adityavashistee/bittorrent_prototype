package com.p2p.man.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

import com.p2p.man.config.CommonConfig;
import com.p2p.man.config.PeerConfig;
import com.p2p.man.conn.Neighbor;
import com.p2p.man.log.PeerLogs;
import com.p2p.man.server.Server;

public class PeerState {
		int peerId;
		String hostName;
		int port;
		boolean hasFile;
		HashMap<Integer, Neighbor> preferredNeighbors;
		Neighbor optNeighbors;
		String fileName;
		byte[][] filePieces;
		boolean[] chockedList;
		PeerLogs peerLogs;
		HashMap<Integer, Neighbor> otherPeers;
		public HashMap<Integer, Integer> clientIdToPeerId;
		int unchokingTime;
		int optNeighTime;		
		static Timer prefNeighTimer;
		static Timer optNeighTimer;
		public Server server;
		public int numberOfPreferredNeighbour;
		public Server getServer() {
			return server;
		}

		public void setServer(Server server) {
			this.server = server;
		}
		//TODO set piece size, initialise isFileComplete
		int pieceSize;
		boolean transferCompleted = false;
		public boolean isTransferCompleted() {
			return transferCompleted;
		}

		public void setTransferCompleted(boolean transferCompleted) {
			this.transferCompleted = transferCompleted;
		}
		public boolean[] isFileComplete;
		HashMap<Integer, Integer> hasReceivedData;
		
		
		public int getPieceSize() {
			return pieceSize;
		}

		public void setPieceSize(int pieceSize) {
			this.pieceSize = pieceSize;
		}

		public HashMap<Integer, Neighbor> getPreferredNeighbors() {
			return preferredNeighbors;
		}
	
		
		public HashMap<Integer, Integer> getHasReceivedData() {
			return hasReceivedData;
		}

		public void setHasReceivedData(HashMap<Integer, Integer> hasReceivedData) {
			this.hasReceivedData = hasReceivedData;
		}
		//number of bits in ref code
		int numberOfPieces;
		int numberOfByte;
		
		public void setPreferredNeighbors(PeerConfig peerConfig, CommonConfig commonConfig) {
			if (preferredNeighbors.size() > commonConfig.getNumberOfPreferredNeighbors()) {
				return;
			}
			Neighbor neighbor = new Neighbor();
			neighbor.neighborState.setPeerID(peerConfig.getPeerId());
			neighbor.neighborState.setPortNumber(peerConfig.getPortNum());
			neighbor.neighborState.setHasFile(peerConfig.isHasEntireFile());
			neighbor.neighborState.setHostName(peerConfig.getHostName());
			
			preferredNeighbors.put(peerConfig.getPeerId(), neighbor);
		}
		
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
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public boolean isHasFile() {
			return hasFile;
		}
		public void setHasFile(boolean hasFile) {
			this.hasFile = hasFile;
		}
		
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public byte[][] getFilePieces() {
			return filePieces;
		}
		public void setFilePieces(byte[][] filePieces) {
			this.filePieces = filePieces;
		}
		public boolean[] getChockedList() {
			return chockedList;
		}
		public void setChockedList(boolean[] chockedList) {
			this.chockedList = chockedList;
		}

		public PeerLogs getPeerLogs() {
			return peerLogs;
		}
		public void setPeerLogs(PeerLogs peerLogs) {
			this.peerLogs = peerLogs;
		}

		public HashMap<Integer, Neighbor> getOtherPeers() {
			return otherPeers;
		}

		public void setOtherPeers(HashMap<Integer, Neighbor> otherPeers) {
			/*HashMap<Integer, Neighbor>neighbors = new HashMap<Integer, Neighbor>();
			for (Integer key : otherPeers.keySet()) {
				if(this.peerId != key){
				Neighbor neighbor = new Neighbor();
				neighbor.neighborState.setPeerID(otherPeers.get(key).getPeerId());
				neighbor.neighborState.setPortNumber(otherPeers.get(key).getPort());
				neighbor.neighborState.setHasFile(otherPeers.get(key).isHasEntireFile());
				neighbor.neighborState.setHostName(otherPeers.get(key).getHostName());
				neighbors.put(key, neighbor);
			   // ...
				}
			}*/
			this.otherPeers = otherPeers;		
		}

		public void setPreferredNeighbors(HashMap<Integer, Neighbor> preferredNeighbors) {
			this.preferredNeighbors = preferredNeighbors;
		}


		public int getNumberOfPieces() {
			return numberOfPieces;
		}

		public void setNumberOfPieces(int numberOfPieces) {
			this.numberOfPieces = numberOfPieces;
		}

		public int getNumberOfByte() {
			return numberOfByte;
		}

		public void setNumberOfByte(int numberOfByte) {
			this.numberOfByte = numberOfByte;
		}

		public int getUnchokingTimer() {
			return unchokingTime;
		}

		public void setUnchokingTimer(int prefNeighTimer) {
			this.unchokingTime = prefNeighTimer;
		}

		public int getOptNeighTimer() {
			return optNeighTime;
		}

		public void setOptNeighTimer(int optNeighTimer) {
			this.optNeighTime = optNeighTimer;
		}

		public Neighbor getOptNeighbors() {
			return optNeighbors;
		}

		public void setOptNeighbor(Neighbor optNeighbors) {
			this.optNeighbors = optNeighbors;
		}

		public int getNumberOfPreferredNeighbour() {
			return numberOfPreferredNeighbour;
		}

		public void setNumberOfPreferredNeighbour(int numberOfPreferredNeighbour) {
			this.numberOfPreferredNeighbour = numberOfPreferredNeighbour;
		}
		
		
}
