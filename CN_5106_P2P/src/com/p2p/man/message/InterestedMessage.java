package com.p2p.man.message;

import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.p2p.man.process.Peer;
import com.p2p.man.utils.P2PUtils;

public class InterestedMessage extends MessageHandler {

	public InterestedMessage(int messagegLength, byte messageType, ArrayList<Byte> messagePayload, int recieverId){
		this(messagegLength, messageType, messagePayload);
		this.receiverId=recieverId;
	}
	
	public InterestedMessage(int messageLength, byte messageType, ArrayList<Byte> messagePayload){
		Byte[] temp = P2PUtils.toObjects(ByteBuffer.allocate(4).putInt(messageLength).array());
		this.lengthBytes = new ArrayList<Byte>(Arrays.asList(temp));
		this.messageType=messageType;
	}


	public static void handleMessage(Peer peer ,MessageHandler incomingMessage, int messageIndex) {
		try {
			peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().setInterested(true); 
			peer.getPeerState().getPeerLogs().logPeerInterestedNotInterested(peer.getPeerState().getPeerId(), peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().getPeerID(), "InterestedMessage" ); 	

		}catch(Exception ex) {
			System.err.println("interested Message not sent error.");
		}
	}

	public static void sendMessage(int index, Peer peer) {
		try {
			MessageHandler interestedMessage = new InterestedMessage(0, MessageTypeEnum.INTERESTED.getMessageTypeCode() , null);
			ObjectOutputStream objectOutputStream = peer.getObjectOutput().get(index);
			objectOutputStream.writeObject(interestedMessage.getMessageBytes());
			objectOutputStream.flush();
		}
		catch(Exception Exception){
			System.err.println("InterestedMessage Message not sent error.");
		}
	}
}