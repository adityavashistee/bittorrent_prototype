package com.p2p.man.message;

import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//import org.apache.commons.lang3.ArrayUtils;

import com.p2p.man.conn.Neighbor;
import com.p2p.man.process.Peer;
import com.p2p.man.utils.P2PUtils;

public class HandShakeMessage extends MessageHandler {

	public HandShakeMessage(int messageLength, byte messageType, ArrayList<Byte> messagePayload, int receiverId){
		Byte[] temp = P2PUtils.toObjects(ByteBuffer.allocate(4).putInt(messageLength).array());
		//ArrayUtils.toObject(ByteBuffer.allocate(4).putInt(msgLength).array());
		this.lengthBytes = new ArrayList<Byte>(Arrays.asList(temp));
		//this.lengthBytes= (ArrayList<Byte>) Arrays.asList(ArrayUtils.toObject(ByteBuffer.allocate(4).putInt(messageLength).array()));
		this.messageLength=messageLength;
		this.messagePayload=messagePayload;
		this.messageType=messageType;
		this.receiverId=receiverId;
	}


	public static void handleMessage(Peer peer ,MessageHandler incomingMessage, int messageIndex) {
		System.out.println(incomingMessage.toString());
		peer.getPeerState().clientIdToPeerId.put(incomingMessage.receiverId, incomingMessage.messageLength);
		HashMap<Integer, Neighbor> otherPeerIds=peer.getPeerState().getOtherPeers();
		for (int i:otherPeerIds.keySet()) {
			if (otherPeerIds.get(i).getNeighborState().getPeerID() == incomingMessage.messageLength);
			messageIndex = i;
			break;
		}
		peer.getPeerState().getOtherPeers().get(messageIndex).neighborState.setReceivedHandshake(true);
	}

	//	@Override
	public static void sendMessage(int index, Peer peer) {
		byte[] handShakeHeader = new byte[18];
		try {
			handShakeHeader = "P2PFILESHARINGPROJ".getBytes("UTF-8");
		} catch (Exception e) {
			//e.printStackTrace();
		}
		byte[] zeroBits = new byte[10];
		byte[] PeerIdBits = ByteBuffer.allocate(4).putInt(peer.getPeerState().getPeerId()).array();

		ByteBuffer handShakeBuffer = ByteBuffer.allocate(32);

		handShakeBuffer.put(handShakeHeader);
		handShakeBuffer.put(zeroBits);
		handShakeBuffer.put(PeerIdBits);
		byte[] HandShakePayload = handShakeBuffer.array();

		try {
			ObjectOutputStream objectOutputStream = peer.getObjectOutput().get(index);
			objectOutputStream.writeObject(HandShakePayload);
			peer.getObjectOutput().put(index, objectOutputStream);
			objectOutputStream.flush();
			handShakeBuffer.clear();
			peer.getPeerState().getPeerLogs().logPeersConnecting(peer.getPeerState().getPeerId(), peer.getPeerState().getOtherPeers().get(index).getNeighborState().getPeerID()); 	
		}
		catch(Exception Exception){
			System.err.println("HandShakeMessage handshake Message not sent error.");
		}

		//		peer.writelogs.TcpMakeConnection(ps.my_peer_Id, pd.peer_neighbours[index].peerId);
	}
}