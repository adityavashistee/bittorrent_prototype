package com.p2p.man.message;

import com.p2p.man.conn.Neighbor;
import com.p2p.man.process.Peer;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import com.p2p.man.utils.*;

public abstract class MessageHandler {

	public int messageLength;
	public ArrayList<Byte> lengthBytes;
	public ArrayList<Byte> messagePayload;
	public byte messageType;
	public int receiverId = Integer.MIN_VALUE;

	public byte[] getMessageBytes() {
		ByteBuffer messageBuffer = ByteBuffer.allocate(5 + this.messageLength);

		messageBuffer.put(P2PUtils.byteListToArray(this.lengthBytes));
		messageBuffer.put(this.messageType);

		if(hasPayloadMessage((int)this.messageType)) {
			messageBuffer.put(P2PUtils.byteListToArray(this.messagePayload));
		}

		return messageBuffer.array();
	}

	public boolean hasPayloadMessage(int type) {
		return (type == MessageTypeEnum.HAVE.getMessageTypeCode() || type == MessageTypeEnum.BIT_FIELD.getMessageTypeCode()
				|| type == MessageTypeEnum.REQUEST.getMessageTypeCode() || type == MessageTypeEnum.PIECE.getMessageTypeCode());
	}

	public static boolean checkNeededPieces(Neighbor neighbor, Peer peer) {
//		BigInteger field = new BigInteger(P2PUtils.byteListToArray(peer.getBitField()));
//		BigInteger neighbourField = new BigInteger(P2PUtils.byteListToArray(neighbor.neighborState.getAvailablePieces()));
//		//field.andNot(neighbourField);
//		BigInteger interesting_field = neighbourField.and(field.and(neighbourField).not());
		ArrayList<Byte> field = new ArrayList<>(neighbor.neighborState.getAvailablePieces());
		field.removeAll(peer.getBitField());
		if (field.size() > 0) {
			return true;
		}
		return false;
	}

	public byte[] messageToByteArray(){
		int totalBufferLength = 5 + messageLength;
		ByteBuffer byteBuffer = ByteBuffer.allocate(totalBufferLength);

		byteBuffer.put(P2PUtils.byteListToArray(this.lengthBytes));
		byteBuffer.put(messageType);

		if(hasPayloadMessage((int)messageType)) {
			byteBuffer.put(P2PUtils.byteListToArray(this.messagePayload));
		}

		return byteBuffer.array();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Length: ");
		sb.append(this.messageLength);
		sb.append(", Type =");
		sb.append((int)this.messageType);
		sb.append(", payload is : ");
		if(this.messagePayload!=null)sb.append(this.messagePayload.toString());
		return sb.toString();
	}

	public byte[] getMessageBytes(byte[] bs) {
		ByteBuffer messageBuffer = ByteBuffer.allocate(5 + this.messageLength);

		messageBuffer.put(P2PUtils.byteListToArray(this.lengthBytes));
		messageBuffer.put(this.messageType);

		if(hasPayloadMessage((int)this.messageType)) {
			messageBuffer.put(bs);
		}

		return messageBuffer.array();
	}
}
