package com.p2p.man.message;

import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import org.apache.commons.lang3.ArrayUtils;

import com.p2p.man.process.Peer;
import com.p2p.man.utils.P2PUtils;

public class PieceMessage extends MessageHandler {

	public PieceMessage(int messagegLength, byte messageType, ArrayList<Byte> messagePayload, int recieverId){
		this(messagegLength, messageType, messagePayload);
		this.receiverId=recieverId;
	}

	public PieceMessage(int messageLength, byte messageType, ArrayList<Byte> messagePayload){
		Byte[] temp = P2PUtils.toObjects(ByteBuffer.allocate(4).putInt(messageLength).array());
		//ArrayUtils.toObject(ByteBuffer.allocate(4).putInt(msgLength).array());
		this.lengthBytes = new ArrayList<Byte>(Arrays.asList(temp));
		this.messageLength=messageLength;
		this.messagePayload=messagePayload;
		this.messageType=messageType;
	}


	public static void handleMessage(Peer peer ,MessageHandler incomingMessage, int messageIndex) {
		try {
			System.out.println(incomingMessage.toString());
			peer.getPeerState().getFilePieces()[peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().getPieceNumber()] = 
					P2PUtils.byteListToArray(incomingMessage.messagePayload);

			peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().setWaiting(false);

//			BigInteger tempField = new BigInteger(P2PUtils.byteListToArray(peer.getBitField()));


//			tempField = tempField.setBit(peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().getPieceNumber());

//			peer.setBitField((ArrayList<Byte>) Arrays.asList(P2PUtils.toObjects(tempField.toByteArray())));
			peer.getBitField().add((byte)peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().getPieceNumber());
			int temp = peer.getPeerState().getHasReceivedData().get(messageIndex);
			temp +=peer.getPeerState().getPieceSize();// .size_of_piece;
			peer.getPeerState().getHasReceivedData().put(messageIndex, temp);
//			peer.getPeerState().getPeerLogs().logPieceDownload(peer.getPeerState().getOtherPeers().get(peer.getPeerState().getPeerId()).neighborState.getPeerID(), 
//					peer.getPeerState().getNumberOfPieces(), peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().getPieceNumber(), peer.getPeerState().getNumberOfPieces()+1); 
			boolean haveFile = (peer.getBitField().size() == peer.getPeerState().getNumberOfPieces());
//			ArrayList<Byte> al = new ArrayList<>();
//			for(int i : peer.getPeerState().getOtherPeers().keySet()) {
//				if(peer.getPeerState().getOtherPeers().get(i).getNeighborState().isHasFile())
//			}
//			for (int i = 0; i < peer.getPeerState().getNumberOfByte(); i++) {
//				if (!tempField.testBit(i)) {
//					haveFile = false;
//					break;
//				}
//			}

			peer.getPeerState().isFileComplete[peer.getPeerState().getPeerId()%1001] = haveFile;

			if (haveFile){
				peer.getPeerState().getPeerLogs().logCompleteDownLoad(peer.getPeerState().getPeerId());
			}
			for (int i : peer.getPeerState().getOtherPeers().keySet()) {
				if (i == peer.getPeerState().getPeerId())
					continue;
				HaveMessage.sendMessage(i, peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().getPieceNumber(), peer);
			}

			peer.getPeerState().getOtherPeers().get(messageIndex).getNeighborState().setPieceNumber(-1);// = -1;
			for (int i :peer.getPeerState().getOtherPeers().keySet()) {
				if (i == peer.getPeerState().getPeerId())
					continue;

				boolean interested = false;

				if (peer.getPeerState().getOtherPeers().get(i).getNeighborState().getAvailablePieces().size()>=0)
					interested = checkNeededPieces(peer.getPeerState().getOtherPeers().get(i),peer);

				if (!interested)
					UnInterestedMessage.sendMessage(i, peer);
			}
			
			if (haveFile) {
			boolean everyPeerHaveFile = true;
			for (int i = 0; i < peer.getPeerState().getOtherPeers().size(); i++) {
				if (!peer.getPeerState().isFileComplete[i]) {
					everyPeerHaveFile = false;
					break;
				}
			}
			peer.getPeerState().setTransferCompleted(everyPeerHaveFile);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("Error while procesing Piece Message");
		}
	}

	public static void sendMessage(int index, int pieceNumber, Peer peer) {
		try {
			List<Byte> temp = Arrays.asList(P2PUtils.toObjects((peer.getFilePieces()[pieceNumber])));
			MessageHandler pieceMessage = new PieceMessage(peer.getPeerState().getPieceSize()+ temp.size(), MessageTypeEnum.PIECE.getMessageTypeCode(), 
					new ArrayList<>(temp));
			ObjectOutputStream objectOutputStream = Peer.objectOutput.get(index);
			objectOutputStream.writeObject(pieceMessage.getMessageBytes(peer.getFilePieces()[pieceNumber]));
			objectOutputStream.flush();
		}
		catch(Exception Exception){
			Exception.printStackTrace();
			System.err.println("PieceMessage Message not sent error.");
		}
	}
}