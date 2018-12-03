package com.p2p.man.process;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;

//import org.apache.commons.lang3.ArrayUtils;

import com.p2p.man.config.CommonConfig;
import com.p2p.man.config.PeerConfig;
import com.p2p.man.conn.Neighbor;
import com.p2p.man.conn.NeighborState;
import com.p2p.man.file.FileUtils;
import com.p2p.man.message.BitFieldMessage;
import com.p2p.man.message.ChokeMessage;
import com.p2p.man.message.HandShakeMessage;
import com.p2p.man.message.HaveMessage;
import com.p2p.man.message.InterestedMessage;
import com.p2p.man.message.MessageHandler;
import com.p2p.man.message.MessageTypeEnum;
import com.p2p.man.message.PieceMessage;
import com.p2p.man.message.RequestMessage;
import com.p2p.man.message.UnChokeMessage;
import com.p2p.man.message.UnInterestedMessage;
import com.p2p.man.utils.CommonConfigConstants;
import com.p2p.man.utils.P2PUtils;

public class Process {
	static Peer peer = new Peer();
	public Process(CommonConfig commonConfig, HashMap<Integer, PeerConfig> peerConfigMap, int peerId) throws IOException{
		peer.initPeerState(commonConfig, peerConfigMap, peerId);
		//intialSetup
		connectRemotePeers(peerConfigMap);
		
		boolean temp= true;
		while(!peer.peerState.transferCompleted){
			conditionalTask();
			if(temp) {
				startSchedulars();
				temp=false;
			}
			hanlder(peer);
		}

		FileUtils.combinePiecesOfFile(peer.peerState.peerId, commonConfig, peer.peerState.filePieces);
		peer.OtherPeerSocketList.values().forEach(socket->{
			try {
				socket.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		System.exit(-1);
	}

	public static int getRandomRequiredPiece(Neighbor neighbor){
//		byte[] remoteAvailable = P2PUtils.byteListToArray(neighbor.neighborState.getAvailablePieces());
//		BitSet remoteAvailableBitSet = BitSet.valueOf(remoteAvailable);
//		BitSet selfBitFiled = BitSet.valueOf(P2PUtils.byteListToArray(peer.getBitField()));
//		System.out.println("before: " +selfBitFiled);
//		System.out.println("before: remote " +remoteAvailableBitSet);
//		remoteAvailableBitSet.andNot(selfBitFiled);
//		System.out.println("After: remote" +remoteAvailableBitSet);
		ArrayList<Byte> remoteAvailable = new ArrayList<>(neighbor.neighborState.getAvailablePieces());
		System.out.println("before :"+ remoteAvailable);
		remoteAvailable.removeAll(peer.getBitField());
		System.out.println("After :"+ remoteAvailable);
//		return pickRandom(remoteAvailableBitSet);
		return remoteAvailable.size() > 0 ? remoteAvailable.get(new Random().nextInt(remoteAvailable.size())):-1;
	}

	public void startSchedulars(){
		preferredNeighSchedular();
		optNeighSchedular();
	}
	public static void preferredNeighSchedular(){
		peer.peerState.prefNeighTimer.scheduleAtFixedRate(new TimerTask() {
			public void run(){
				Neighbor.getPreferredNeighbors(peer); 
			}
		}, CommonConfigConstants.SENTINEL, peer.peerState.getUnchokingTimer()*1000);
	}

	public static void connectRemotePeers(HashMap<Integer, PeerConfig> peerConfig) throws SecurityException, IOException{
		int totalConnections = 0;
		int connectionsToBeEstablished = peerConfig.size() - 1;
		HashMap<Integer, Neighbor> neigbourMap = new HashMap<Integer, Neighbor>();
		//Iterator<Map.Entry<Integer, PeerConfig>> itr = peerConfig.entrySet().iterator(); 
		while(totalConnections<connectionsToBeEstablished){
				Socket socket = null;
				for(int remotePeerId : peerConfig.keySet()){
					if(peer.peerState.peerId != remotePeerId){	
						if((peer.peerState.getOtherPeers().containsKey(remotePeerId)) && !peer.peerState.getOtherPeers().get(remotePeerId).neighborState.isHasConn()){
							peer.peerLogs.logPeersConnecting(peer.peerState.peerId, remotePeerId);
							try{
					System.out.println("Trying to establish connection between " +peer.peerState.peerId +" and " + remotePeerId);	
					socket = new Socket(peerConfig.get(remotePeerId).getHostName(), peerConfig.get(remotePeerId).getPortNum());
					if(socket.isConnected()){
						peer.OtherPeerSocketList.put(remotePeerId, socket);
						peer.objectOutput.put(remotePeerId, new ObjectOutputStream(socket.getOutputStream()));
						//int index = (peer.getPeerState().peerId)%1000;
						peer.peerLogs.logGenerics("Socket connection is established between "+peer.peerState.peerId+ " and " +remotePeerId);
						PeerConfig peerconfig = peerConfig.get(remotePeerId);
						Neighbor neighbour = new Neighbor();
						NeighborState neighborState= peer.peerState.getOtherPeers().get(remotePeerId).neighborState;
						neighborState.setHasConn(true);
			
						totalConnections = totalConnections+1;
						}
					}catch(ConnectException ce){
						peer.peerLogs.logPeersConnectionRefused(peer.peerState.peerId, remotePeerId);
						if(socket != null){
							try{
								socket.close();
							}catch(Exception e){
								peer.peerLogs.logGenerics("Exception occured while closing socket between "+peer.peerState.peerId+ " and " +remotePeerId);
							}
						}
					}catch(IOException ex){
						if(socket != null){
							try{
								socket.close();
							}catch(Exception e){
								peer.peerLogs.logGenerics("Exception occured while closing socket between "+peer.peerState.peerId+ " and " +remotePeerId);
								
							}
						}
					}
				}
				
				}
				//peer.peerState.setOtherPeers(neigbourMap);
				//itr=peerConfig.entrySet().iterator(); 
				try{
					Thread.sleep(1000);
					//System.out.println("trying to reconnect");
				}catch(Exception ex){
					
				}
				}
				}
	
		

	}


	public static void optNeighSchedular(){
		peer.peerState.optNeighTimer.scheduleAtFixedRate(new TimerTask() {
			public void run(){
				Neighbor.getOptimisticallyUnchokedNeighbor((peer));  
			}
		}, CommonConfigConstants.SENTINEL, peer.peerState.getOptNeighTimer()*1000);
	}

	public static int pickRandom(BitSet requiredpieces) {
		if(requiredpieces.size()>0){
		int[] piecesArray = new int[requiredpieces.size()];
		//Random randomGenerator = new Random();
		//Random randomGenerator = new Random();
		
		String bitSetString = requiredpieces.toString();

		String[] pieceIndexes = bitSetString.substring(1,bitSetString.length()-1).split(",");
		return Integer.parseInt(pieceIndexes[(int)peer.randGen.nextInt(pieceIndexes.length-1)]);
	}
	else{
		return -1;
	}
	}


	public static void conditionalTask() throws SecurityException, IOException{
		conditionalSendHanshake();
		conditionalSendBitField();
		conditionalRequestRandomPiece();
	}
	public static void conditionalRequestRandomPiece() {
		for(int index : peer.peerState.getOtherPeers().keySet()){
//			int index = i%1000;
			NeighborState ns = peer.peerState.getOtherPeers().get(index).getNeighborState();
			if (ns.isReceivedBitField() &&
					ns.isReceivedHandshake() &&
					ns.isChoked() == false &&
					ns.isWaiting() == false && CommonConfigConstants.CONDITIONAL_CHECK) {

				peer.peerState.getPreferredNeighbors().get(index).getNeighborState().setWaiting(true);

				int rqstedPiece = -1;
				if(!peer.peerState.hasFile) rqstedPiece= getRandomRequiredPiece(peer.peerState.getPreferredNeighbors().get(index));
				if (rqstedPiece != -1) {
					peer.peerState.getPreferredNeighbors().get(index).neighborState.setPieceNumber(rqstedPiece);
					RequestMessage.sendMessage(index, rqstedPiece , peer);
				}
			}
		}
	}

	public static void conditionalSendBitField() {
		for(int index : peer.peerState.getOtherPeers().keySet()){
			//			int index = i%1000;
			NeighborState ns = peer.peerState.getOtherPeers().get(index).getNeighborState();
			if (!ns.isReceivedBitField() && ns.isHasConn() && ns.isReceivedHandshake()) {
				BitFieldMessage.sendMessage(index, peer);
				peer.peerState.getOtherPeers().get(index).neighborState.setSentBitfield(true);
			}
		}
	}

	public static void conditionalSendHanshake() throws SecurityException, IOException {
		for(int index : peer.peerState.getOtherPeers().keySet()){
			if(peer.peerState.peerId != index){	
				//				int index = %1000;
								//System.out.println(peer.peerState.getOtherPeers().get(index).neighborState + " "+index);
				NeighborState ns = peer.peerState.getOtherPeers().get(index).getNeighborState();
				if (!ns.isSentHandshake() && ns.isHasConn() && CommonConfigConstants.CONDITIONAL_CHECK) {
					HandShakeMessage.sendMessage(index,peer);
					peer.peerState.getOtherPeers().get(index).neighborState.setSentHandshake(true);
					peer.peerState.peerLogs.logPeersConnected(peer.peerState.peerId, ns.getPeerID());
				}
			}
		}
	}
	
	private void hanlder(Peer peer) {

		List<MessageHandler> messageToRemove = new ArrayList<MessageHandler>();

		synchronized (peer.getPeerState().server.receivedMessages) {

			Iterator<MessageHandler> it = peer.getPeerState().server.receivedMessages.iterator();
			while (it.hasNext()) {
				MessageHandler incomingMessage = it.next();
				int messageIndex = peer.getPeerState().clientIdToPeerId.get(incomingMessage.receiverId);

				if(checkHandshake(incomingMessage, messageIndex))
					continue;

				messageIndex = getIndex(peer, incomingMessage, messageIndex);
				if (messageIndex != -1) {
					if (((int)incomingMessage.messageType != MessageTypeEnum.BIT_FIELD.getMessageTypeCode()) && 
							!peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().isReceivedBitField() &&
							peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().isReceivedHandshake()) {
						continue;
					}
				}

				handleMessage(peer, incomingMessage, messageIndex);
				messageToRemove.add(incomingMessage);
			}

			for (MessageHandler message : messageToRemove) {
				peer.getPeerState().server.receivedMessages.remove(message);
			}
		}

	}
	
	private boolean checkHandshake(MessageHandler incomingMessage, int messageIndex) {
		return (messageIndex == -1 && !((int)incomingMessage.messageType == MessageTypeEnum.HANDSHAKE.getMessageTypeCode())) ;
	}
	
	private int getIndex(Peer peer, MessageHandler incomingMessage, int messageIndex) {
		if (incomingMessage.messageType != MessageTypeEnum.HANDSHAKE.getMessageTypeCode()) {
			for(int k:peer.getPeerState().getOtherPeers().keySet()) {
				if(k==messageIndex) {
					messageIndex=k;
					break;
				}
			}
		}
		return messageIndex;
	}
	
	private void handleMessage(Peer peer, MessageHandler incomingMessage, int messageIndex) {
		MessageTypeEnum type = MessageTypeEnum.getMessageTypeFromCode(incomingMessage.messageType);
		switch(type) {
		case CHOKE:
			ChokeMessage.handleMessage(peer, incomingMessage, messageIndex);
			break;
		case UNCHOKE:
			UnChokeMessage.handleMessage(peer, incomingMessage, messageIndex);
			break;
		case INTERESTED:
			InterestedMessage.handleMessage(peer, incomingMessage, messageIndex);
			break;
		case NOT_INTERESTED:
			UnInterestedMessage.handleMessage(peer, incomingMessage, messageIndex);
			break;
		case HAVE:
			HaveMessage.handleMessage(peer, incomingMessage, messageIndex);
			break;
		case BIT_FIELD:
			BitFieldMessage.handleMessage(peer, incomingMessage, messageIndex);
			break;
		case REQUEST:
			RequestMessage.handleMessage(peer, incomingMessage, messageIndex);
			break;
		case PIECE:
			PieceMessage.handleMessage(peer, incomingMessage, messageIndex);
			break;
		case HANDSHAKE:
			HandShakeMessage.handleMessage(peer, incomingMessage, messageIndex);
			break;
		}
	}
	
}