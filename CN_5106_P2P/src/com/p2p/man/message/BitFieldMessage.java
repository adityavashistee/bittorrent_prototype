package com.p2p.man.message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

//import org.apache.commons.lang3.ArrayUtils;


import com.p2p.man.conn.Neighbor;
import com.p2p.man.process.*;
import com.p2p.man.utils.P2PUtils;

public class BitFieldMessage extends MessageHandler{
	
	public BitFieldMessage(int messagegLength, byte messageType, ArrayList<Byte> messagePayload, int recieverId){
		this(messagegLength, messageType, messagePayload);
		this.receiverId=recieverId;
	}

	public BitFieldMessage(int messageLength, byte messageType, ArrayList<Byte> messagePayload){
		Byte[] temp = P2PUtils.toObjects(ByteBuffer.allocate(4).putInt(messageLength).array());
		this.lengthBytes = new ArrayList<Byte>(Arrays.asList(temp));
		this.messageType = messageType;
		this.messageLength = messageLength;
		this.messagePayload = messagePayload;
	}


//	@Override
	public static void handleMessage(Peer peer ,MessageHandler incomingMessage, int messageIndex) {
		System.out.println(incomingMessage.toString());
		peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().setAvailablePieces(incomingMessage.messagePayload);
		peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().setReceivedBitField(true);
		if (checkNeededPieces(peer.getPeerState().getOtherPeers().get(messageIndex),peer)) {
			InterestedMessage.sendMessage(messageIndex,peer);
		} else {
			UnInterestedMessage.sendMessage(messageIndex,peer);
		}
	}

	//	@Override
	public static void sendMessage(int index, Peer peer) {
		try {
			MessageHandler bitfieldMessage = new BitFieldMessage(peer.getBitField().size(), MessageTypeEnum.BIT_FIELD.getMessageTypeCode() , peer.getBitField());
			ObjectOutputStream objectOutputStream = peer.getObjectOutput().get(index);
			objectOutputStream.writeObject(bitfieldMessage.getMessageBytes());
			objectOutputStream.flush();
		}
		catch(Exception Exception){
			System.err.println("BitFieldMessage Message not sent error.");
		}
	}

	
	
	
}
