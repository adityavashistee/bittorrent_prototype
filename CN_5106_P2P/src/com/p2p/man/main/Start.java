package com.p2p.man.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.p2p.man.config.CommonConfig;
import com.p2p.man.config.PeerConfig;
import com.p2p.man.utils.CommonConfigConstants;
import com.p2p.man.utils.ParseConfig;
import com.p2p.man.process.Process;

public class Start {
	private static String COMMON_CONFIG_FILE = "Common.cfg";
	private static String PEER_CONFIG_FILE = "PeerInfo.cfg";
	
	
	public static void main(String[] args) throws IOException {
		int peerID = Integer.parseInt(args[0]);
		
		CommonConfig commonConfig = ParseConfig.parseCommonConfig(COMMON_CONFIG_FILE);
		HashMap<Integer, PeerConfig> peerConfig = ParseConfig.parsePeerConfig(PEER_CONFIG_FILE);
		
		System.out.println("Read config files");
		
		if(!peerConfig.containsKey(peerID)) {
			System.out.println("Peer id not found in the PeerInfo.cfg file");
			return;
		}
		
//		for(int i: peerConfig.keySet()) {
//			System.out.println(i + " "+ peerConfig.get(i).getHostName());
//		}
		
		Process process = new Process(commonConfig, peerConfig, peerID);		
	}
	
	
}
