package com.p2p.man.message;

import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.p2p.man.process.Peer;
import com.p2p.man.utils.P2PUtils;

public class UnInterestedMessage extends MessageHandler {

	public UnInterestedMessage(int messagegLength, byte messageType, ArrayList<Byte> messagePayload, int recieverId){
		this(messagegLength, messageType, messagePayload);
		this.receiverId=recieverId;
	}

	public UnInterestedMessage(int messageLength, byte messageType, ArrayList<Byte> messagePayload){
		Byte[] temp = P2PUtils.toObjects(ByteBuffer.allocate(4).putInt(messageLength).array());
		this.lengthBytes = new ArrayList<Byte>(Arrays.asList(temp));
		this.messageType=messageType;
	}


	public static void handleMessage(Peer peer ,MessageHandler incomingMessage, int messageIndex) {
		try {
			System.out.println(incomingMessage.toString());
			peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().setInterested(false); 
			peer.getPeerState().getPeerLogs().logPeerInterestedNotInterested(peer.getPeerState().getPeerId(), peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().getPeerID(), "UnInterestedMessage" ); 	

		}catch(Exception ex) {
			System.err.println("not interested Message not sent error.");
		}
	}

	public static void sendMessage(int index, Peer peer) {
		try {
			MessageHandler unInterestedMessage = new UnInterestedMessage(0, MessageTypeEnum.NOT_INTERESTED.getMessageTypeCode() , null);
			ObjectOutputStream objectOutputStream = peer.getObjectOutput().get(index);
			objectOutputStream.writeObject(unInterestedMessage.getMessageBytes());
			objectOutputStream.flush();
		}
		catch(Exception Exception){
			System.err.println("UnInterestedMessage Message not sent error.");
		}
	}
}