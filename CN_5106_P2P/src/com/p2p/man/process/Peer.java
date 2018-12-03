package com.p2p.man.process;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

import com.p2p.man.config.CommonConfig;
import com.p2p.man.config.PeerConfig;
import com.p2p.man.conn.Neighbor;
import com.p2p.man.file.FileUtils;
import com.p2p.man.log.PeerLogs;
import com.p2p.man.server.Server;

public class Peer{
	PeerState peerState = new PeerState();
	PeerLogs peerLogs = new PeerLogs();
	public static HashMap<Integer, Socket> OtherPeerSocketList = new HashMap<Integer, Socket>(); 
	public static HashMap<Integer, ObjectOutputStream> objectOutput = new HashMap<Integer, ObjectOutputStream>();
	static byte[][] filePieces;
	static BitSet receivedPeices;
	ArrayList<Byte>bitField = new ArrayList<Byte>();
	public Random randGen;
	
	
	public void initPeerState(CommonConfig commonConfig, HashMap<Integer, PeerConfig> peerConfigMap, int id) throws IOException{
		PeerConfig peerConfig = peerConfigMap.get(id);
		peerState.setPeerId(id); 
		peerState.setHostName(peerConfig.getHostName());
		peerState.setPort(peerConfig.getPortNum());
		peerState.setNumberOfPreferredNeighbour(commonConfig.getNumberOfPreferredNeighbors());
		peerState.setPreferredNeighbors(new HashMap<Integer, Neighbor>());
		peerState.isFileComplete = new boolean[peerConfigMap.size()];
		//peerState.setNeighbors(peerConfig);
		//other peers means all remote neighbour... this is different from preferred neighbour
		HashMap<Integer, Neighbor> otherPeer = new HashMap<Integer, Neighbor>();
		Neighbor neighbour = null;
		peerState.clientIdToPeerId = new HashMap<Integer, Integer>();
		int count =0;
		HashMap<Integer, Integer> hasRecivedMap = new HashMap();
		for(int i : peerConfigMap.keySet()){
			if(i !=id){
				neighbour = new Neighbor();
				PeerConfig peerConfig1 = peerConfigMap.get(i);
				neighbour.neighborState.setPeerID(peerConfig1.getPeerId());
				neighbour.neighborState.setHostName(peerConfig1.getHostName());
				neighbour.neighborState.setPortNumber(peerConfig1.getPortNum());
				//neighbor.neighborState.setHasFile(otherPeers.get(key).isHasEntireFile());
				neighbour.neighborState.setHasFile(peerConfig1.isHasEntireFile());
				otherPeer.put(i,neighbour);
				peerState.clientIdToPeerId.put(count++, -1);
				hasRecivedMap.put(i, 0);
				peerState.isFileComplete[i%1001]=peerConfig.isHasEntireFile();
				
			}
			
		}
		peerState.setHasReceivedData(hasRecivedMap);
		peerState.setOtherPeers(otherPeer);
		peerState.setHasFile(peerConfig.isHasEntireFile());
//		peerState.isFileComplete[peerState.getPeerId()%1001]=peerState.isHasFile();
		//peerState.setOtherPeers(peerConfigMap);
		peerState.setFilePieces(FileUtils.splitFile(id, peerConfig.isHasEntireFile(), commonConfig));
		peerState.setUnchokingTimer(commonConfig.getUnchokingInterval());
		peerState.setOptNeighTimer(commonConfig.getOptimisticUnchokingInterval());
		peerLogs.createLogger(id);
		peerState.setPeerLogs(peerLogs);
		int noOfPieces =(int) Math.ceil(commonConfig.getFileSize()/commonConfig.getPieceSize());
		peerState.setNumberOfPieces(noOfPieces);
		for( int i : peerConfigMap.keySet()) {
			if(peerConfigMap.get(i).isHasEntireFile()) {
				peerState.isFileComplete[i%1001] = true;
			}
			else
				peerState.isFileComplete[i%1001] = false;
		}
		peerState.setNumberOfByte((int) Math.ceil(noOfPieces/8));
		
			if(peerConfig.isHasEntireFile()){
				for(int i=0; i<noOfPieces; i++){
					bitField.add((byte) i);
				}
		}
		filePieces = FileUtils.splitFile(id, peerConfig.isHasEntireFile(), commonConfig);
		
		peerState.optNeighTimer = new Timer();
		peerState.prefNeighTimer = new Timer();
		randGen = new Random();
		
		PeerLogs peerLogs = new PeerLogs();
		peerLogs.createLogger(id);
		peerState.setPeerLogs(peerLogs);
		Server server = new Server(peerState.getPort());
//		server.setPortNum(peerState.getPort());
		peerState.setServer(server);
		
	}
	
	public PeerState getPeerState() {
		return peerState;
	}
	
	public PeerLogs getPeerLogs() {
		return peerLogs;
	}

	public static HashMap<Integer, Socket> getOtherPeerSocketList() {
		return OtherPeerSocketList;
	}

	public static void setOtherPeerSocketList(HashMap<Integer, Socket> otherPeerSocketList) {
		OtherPeerSocketList = otherPeerSocketList;
	}

	public static HashMap<Integer, ObjectOutputStream> getObjectOutput() {
		return objectOutput;
	}

	public static void setObjectOutput(HashMap<Integer, ObjectOutputStream> objectOutput) {
		Peer.objectOutput = objectOutput;
	}

	public static byte[][] getFilePieces() {
		return filePieces;
	}

	public static void setFilePieces(byte[][] filePieces) {
		Peer.filePieces = filePieces;
	}

	public static BitSet getReceivedPeices() {
		return receivedPeices;
	}

	public static void setReceivedPeices(BitSet receivedPeices) {
		Peer.receivedPeices = receivedPeices;
	}

	public ArrayList<Byte> getBitField() {
		return bitField;
	}

	public void setBitField(ArrayList<Byte> bitField) {
		this.bitField = bitField;
	}

	public void setPeerState(PeerState peerState) {
		this.peerState = peerState;
	}

	public void setPeerLogs(PeerLogs peerLogs) {
		this.peerLogs = peerLogs;
	}
	
}
