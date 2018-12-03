package com.p2p.man.message;

import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.p2p.man.process.Peer;
import com.p2p.man.utils.P2PUtils;

public class UnChokeMessage extends MessageHandler {
	
	public UnChokeMessage(int messagegLength, byte messageType, ArrayList<Byte> messagePayload, int recieverId){
		this(messagegLength, messageType, messagePayload);
		this.receiverId=recieverId;
	}

	public UnChokeMessage(int messageLength, byte messageType, ArrayList<Byte> messagePayload){
		Byte[] temp = P2PUtils.toObjects(ByteBuffer.allocate(4).putInt(messageLength).array());
		this.lengthBytes = new ArrayList<Byte>(Arrays.asList(temp));
		this.messageType=messageType;
	}


	public static void handleMessage(Peer peer ,MessageHandler incomingMessage, int messageIndex) {
		try {
			System.out.println(incomingMessage.toString());
			peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().setChoked(false); 
			peer.getPeerState().getPeerLogs().logPeerInterestedNotInterested(peer.getPeerState().getPeerId(), peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().getPeerID(), "unchokeMessage" );
		}catch(Exception ex) {
			System.err.println("unchoke Message error in handle Message.");
		}
	}

	public static void sendMessage(int index, Peer peer) {
		try {
			MessageHandler unChokeMessage = new UnChokeMessage(0, MessageTypeEnum.UNCHOKE.getMessageTypeCode() , null);
			ObjectOutputStream objectOutputStream  = peer.getObjectOutput().get(index);
			if(objectOutputStream != null) {
				objectOutputStream.writeObject(unChokeMessage.getMessageBytes());
				objectOutputStream.flush();
			}
		}
		catch(Exception Exception){
			System.err.println("UnChokeMessage Message not sent error.");
		}
	}
}