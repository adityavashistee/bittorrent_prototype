package com.p2p.man.server;

import com.p2p.man.message.*;

import com.p2p.man.message.MessageTypeEnum;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import com.p2p.man.utils.*;

class Request extends Thread {
	private int clientId;
	private Socket socket;
	private ObjectInputStream inputStream;  
	private MessageHandler messageHandler; 
	private Server server;

	public Request(Socket socket, int clientID, Server server) {
		this.socket = socket;
		this.clientId = clientID;
		this.server = server;
	}
	public void run() {
		try{
			inputStream = new ObjectInputStream(socket.getInputStream());
			try{
				while(true)
				{
					byte[] tmp = (byte[])inputStream.readObject();
					this.messageHandler = bytesToMessage(tmp, clientId);
					synchronized (this.server.receivedMessages) {
						this.server.receivedMessages.add(this.messageHandler);
					}
				}
			}
			catch(ClassNotFoundException io_exception){
				System.err.println("Data received in unknown format");
			}
		}
		catch(IOException io_exception){
			System.err.println("Error in request.java " + io_exception.getMessage());
		}
		finally{
			try{
				this.inputStream.close();
				this.socket.close();
			}
			catch(IOException io_exception){
			}
		}
	}

	private MessageHandler bytesToMessage(byte[] byte_data, int clientID) {
		try {
			Thread.sleep(1);
		} catch (Exception io_exception) {
			System.out.println(io_exception.getMessage());
		}
		if (byte_data.length >= 18) {
			byte[] byte_arr = Arrays.copyOfRange(byte_data, 0, 18);
			String message_header = new String(byte_arr, Charset.forName(CommonConfigConstants.UTF8));
			if (message_header.equalsIgnoreCase(CommonConfigConstants.HEADER)) {
				int new_peerId = ByteBuffer.allocate(4).put(Arrays.copyOfRange(byte_data, 28, 32)).getInt(0);
				return new HandShakeMessage(new_peerId, MessageTypeEnum.HANDSHAKE.getMessageTypeCode(), null, clientID);
			}
		}
		byte[] message_payload = Arrays.copyOfRange(byte_data, 5, byte_data.length);
		int message_length = ByteBuffer.allocate(4).put(Arrays.copyOfRange(byte_data, 0, 4)).getInt(0);
		return getInstanceOfMessage(message_length, byte_data[4], message_payload, clientID);
		//		return new MessageHandler(message_length, byte_data[4], message_payload, clientID);
	}

	private MessageHandler getInstanceOfMessage(int messageLength, byte messageType, byte[] payload, int clientId) {
		MessageHandler message =null;
		MessageTypeEnum type = MessageTypeEnum.getMessageTypeFromCode(messageType);
		ArrayList<Byte> messagePayload = P2PUtils.bytesArrayToArrayList(payload);
		switch(type) {
		case CHOKE:
			message = new ChokeMessage(messageLength, messageType, messagePayload, clientId);
			break;
		case UNCHOKE:
			message = new UnChokeMessage(messageLength, messageType, messagePayload, clientId);
			break;
		case INTERESTED:
			message = new InterestedMessage(messageLength, messageType, messagePayload, clientId);
			break;
		case NOT_INTERESTED:
			message = new UnInterestedMessage(messageLength, messageType, messagePayload, clientId);
			break;
		case HAVE:
			message = new HaveMessage(messageLength, messageType, messagePayload, clientId);
			break;
		case BIT_FIELD:
			message = new BitFieldMessage(messageLength, messageType, messagePayload, clientId);
			break;
		case REQUEST:
			message = new RequestMessage(messageLength, messageType, messagePayload, clientId);
			break;
		case PIECE:
			message = new PieceMessage(messageLength, messageType, messagePayload, clientId);
			break;
		case HANDSHAKE:
			message = new HandShakeMessage(messageLength, messageType, messagePayload, clientId);
			break;
		}
		return message;
	}

}