package com.p2p.man.utils;

import java.util.ArrayList;

public class P2PUtils {
	public static byte[] byteListToArray(ArrayList<Byte> list){ 
		byte[] data = new byte[list.size()];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) list.get(i);
		}
		return data;
	}	
	
	
	public static Byte[] toObjects(byte[] bytesPrim) {

	    Byte[] bytes = new Byte[bytesPrim.length];
	    int i = 0;
	    for (byte b : bytesPrim) bytes[i++] = b; //Autoboxing
	    return bytes;

	}
	public static ArrayList<Byte> bytesArrayToArrayList(byte[] bytes) {
		ArrayList<Byte> al = new ArrayList<>();
	    int i = 0;
	    for (byte b : bytes) al.add(b);
	    return al;

	}
}
