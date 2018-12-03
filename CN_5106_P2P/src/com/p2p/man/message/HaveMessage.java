package com.p2p.man.message;

import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

//import org.apache.commons.lang3.ArrayUtils;

import com.p2p.man.process.Peer;
import com.p2p.man.utils.P2PUtils;

public class HaveMessage extends MessageHandler {
	
	public HaveMessage(int messagegLength, byte messageType, ArrayList<Byte> messagePayload, int recieverId){
		this(messagegLength, messageType, messagePayload);
		this.receiverId=recieverId;
	}
	
	public HaveMessage(int messageLength, byte messageType, ArrayList<Byte> messagePayload){
		Byte[] temp = P2PUtils.toObjects(ByteBuffer.allocate(4).putInt(messageLength).array());
		//ArrayUtils.toObject(ByteBuffer.allocate(4).putInt(msgLength).array());
		this.lengthBytes = new ArrayList<Byte>(Arrays.asList(temp));
		this.messageLength=messageLength;
		this.messagePayload=messagePayload;
		this.messageType=messageType;
	}
	
	public static void handleMessage(Peer peer ,MessageHandler incomingMessage, int messageIndex) {
//		BigInteger tempField = new BigInteger(peer.peer_neighbours[msg_index].bit_field_map);
		System.out.println(incomingMessage.toString());
		BigInteger tempField = new BigInteger(P2PUtils.byteListToArray(peer.getBitField()));
		ByteBuffer buffer = ByteBuffer.wrap((P2PUtils.byteListToArray(incomingMessage.messagePayload)));
		

		int this_indx = buffer.getInt();
		tempField = tempField.setBit(this_indx);

	//	peer.setBitField((ArrayList<Byte>)Arrays.asList(ArrayUtils.toObject(tempField.toByteArray())));
		peer.setBitField(incomingMessage.messagePayload);
//		peer.peer_neighbours[msg_index].bit_field_map = ;

		boolean neighborHasFile = true;
		for (int i = 0; i < peer.getPeerState().getNumberOfByte(); i++) {
		    if (!tempField.testBit(i)) {
		        neighborHasFile = false;
		        break;
		    }
		}

		peer.getPeerState().isFileComplete[messageIndex] = neighborHasFile;
		
		boolean temp = true;
		for (int i = 0; i < peer.getPeerState().getOtherPeers().size(); i++) {
		    if (!peer.getPeerState().isFileComplete[i]) {
		        temp = false;
		        break;
		    }
		}

		peer.getPeerState().setHasFile(temp);
		//TODO update logs
//		peer.writelogs.haveMsgType(peer.other_peer_Ids[ps.my_clID], peer.peer_neighbours[msg_index].peerId, this_indx);
//		peer.getPeerState().getPeerLogs().log
		
		
		BigInteger myField = new BigInteger(P2PUtils.byteListToArray(peer.getBitField()));
		if (!myField.testBit(this_indx))
		    InterestedMessage.sendMessage(messageIndex,peer);
	}
	
	public static void sendMessage(int index, int pieceNo, Peer peer) {
		try {
			byte[] pieceIndex = ByteBuffer.allocate(4).putInt(pieceNo).array();
			Byte[] pieceIndexInByte = P2PUtils.toObjects(pieceIndex);
			MessageHandler haveMessage = new HaveMessage(4, MessageTypeEnum.HAVE.getMessageTypeCode(),  (ArrayList<Byte>) Arrays.asList(pieceIndexInByte));
			ObjectOutputStream objectOutputStream = peer.getObjectOutput().get(index);
			objectOutputStream.writeObject(haveMessage.getMessageBytes());
			objectOutputStream.flush();
		}
		catch(Exception Exception){
			System.err.println("HaveMessage Message not sent error.");
		}
	}
}