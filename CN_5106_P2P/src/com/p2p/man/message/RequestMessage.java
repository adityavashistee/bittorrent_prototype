package com.p2p.man.message;

import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import org.apache.commons.lang3.ArrayUtils;

import com.p2p.man.process.Peer;
import com.p2p.man.utils.P2PUtils;

public class RequestMessage extends MessageHandler {
	
	public RequestMessage(int messagegLength, byte messageType, ArrayList<Byte> messagePayload, int recieverId){
		this(messagegLength, messageType, messagePayload);
		this.receiverId=recieverId;
	}
	
	public RequestMessage(int messageLength, byte messageType, ArrayList<Byte> messagePayload){
		Byte[] temp = P2PUtils.toObjects(ByteBuffer.allocate(4).putInt(messageLength).array());
		this.lengthBytes = new ArrayList<Byte>(Arrays.asList(temp));
		this.messageLength=messageLength;
		this.messagePayload= messagePayload;
		this.messageType=messageType;
	}
	
	
	public static void handleMessage(Peer peer ,MessageHandler incomingMessage, int messageIndex) {
		System.out.println(incomingMessage.toString());
		ByteBuffer byteBuffer = ByteBuffer.wrap(P2PUtils.byteListToArray(incomingMessage.messagePayload));
		int pieceNumber = byteBuffer.getInt();
		PieceMessage.sendMessage(messageIndex, pieceNumber, peer);
	}
	
	public static void sendMessage(int index, int pieceNumber,Peer peer) {
		try {
			byte[] pieceIndex = ByteBuffer.allocate(4).putInt(pieceNumber).array();
			List<Byte> temp = Arrays.asList(P2PUtils.toObjects(pieceIndex));
			MessageHandler requestMessage = 
					new RequestMessage(4, MessageTypeEnum.REQUEST.getMessageTypeCode() , new ArrayList<>(temp));
			ObjectOutputStream objectOutputStream = Peer.objectOutput.get(index);
			objectOutputStream.writeObject(requestMessage.getMessageBytes());
			objectOutputStream.flush();
		}
		catch(Exception Exception){
			System.err.println("RequestMessage Message not sent error.");
		}
	}
}