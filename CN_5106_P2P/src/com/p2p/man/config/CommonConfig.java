package com.p2p.man.config;
public class CommonConfig {
	int numberOfPreferredNeighbors;
	int unchokingInterval;
	int optimisticUnchokingInterval;
	String fileName;
	int fileSize;
	int pieceSize;
	
	public CommonConfig(int numberOfPreferredNeighbors, int unchokingInterval, int optimisticUnchokingInterval,String fileName, int fileSize, int pieceSize){
		this.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
		this.unchokingInterval = unchokingInterval;
		this.optimisticUnchokingInterval = optimisticUnchokingInterval;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.pieceSize = pieceSize;
	}
	public int getNumberOfPreferredNeighbors() {
		return numberOfPreferredNeighbors;
	}
	
	public int getUnchokingInterval() {
		return unchokingInterval;
	}
	
	public int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}

	public String getFileName() {
		return fileName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public int getPieceSize() {
		return pieceSize;
	}	

}
