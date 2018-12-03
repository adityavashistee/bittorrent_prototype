package com.p2p.man.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.p2p.man.config.CommonConfig;
import com.p2p.man.config.PeerConfig;

public class ParseConfig {
	public static CommonConfig parseCommonConfig(String commonConfigfile) throws IOException {
		FileReader commonConfigFileReader = null;
		BufferedReader configBufferedReader = null;
		CommonConfig commonConfig;	
		
		int numberOfPreferredNeighbors = 0, unchokingInterval = 0, optimisticUnchokingInterval=0, fileSize = 0, pieceSize = 0;
		
		String fileName= "";
		try {
			commonConfigFileReader = new FileReader(commonConfigfile);
			configBufferedReader = new BufferedReader(commonConfigFileReader);
			
			for (String configLine = configBufferedReader.readLine(); configLine != null; configLine = configBufferedReader.readLine()) {
				configLine = configLine.trim();
				String[] lineSplit = configLine.split(" ");
				String name = lineSplit[0];
				String val = lineSplit[1];
				
				if (name.equals(CommonConfigConstants.NUM_PREF_NEIGHBOURS)) {
					numberOfPreferredNeighbors= Integer.parseInt(val);
				}
				else if (name.equals(CommonConfigConstants.OPTIMISTIC_UNCHOKING_INT)) {
					optimisticUnchokingInterval = Integer.parseInt(val);
				}
				else if (name.equals(CommonConfigConstants.FILE_NAME)) {
					fileName = val;
				}
				else if (name.equals(CommonConfigConstants.FILE_SIZE)) {
					fileSize = Integer.parseInt(val);
				}
				else if (name.equals(CommonConfigConstants.PIECE_SIZE)) {
					pieceSize = Integer.parseInt(val);;
				}
				else if (name.equals(CommonConfigConstants.UNCHOKING_INT)) {
					unchokingInterval = (Integer.parseInt(val));
				}
				
			}	
			commonConfig = new CommonConfig( numberOfPreferredNeighbors, unchokingInterval, optimisticUnchokingInterval, fileName, fileSize, pieceSize);
		} 
		catch (IOException e) {
            throw e;
		}
		finally {
			try {
				if (configBufferedReader != null) {
					configBufferedReader.close();
				}
				
				if (commonConfigFileReader != null) {
					commonConfigFileReader.close();
				}
			}
			catch (IOException e) {
				throw e;
			}
		}
		
		return commonConfig;
	}
	
	public static HashMap<Integer, PeerConfig> parsePeerConfig(String fileName) throws IOException {
		FileReader peerFileReader = null;
		BufferedReader peerConfigBufferedReader = null;
		
		HashMap<Integer, PeerConfig> peerConfigMap = new HashMap<Integer, PeerConfig>();
		
		try {
			peerFileReader = new FileReader(fileName);
			peerConfigBufferedReader = new BufferedReader(peerFileReader);
			
			for (String peerConfigLine = peerConfigBufferedReader.readLine(); peerConfigLine != null; peerConfigLine = peerConfigBufferedReader.readLine()) {
				PeerConfig peerConfig = new PeerConfig();
				
				peerConfigLine = peerConfigLine.trim();
				String[] lineSplit = peerConfigLine.split(" ");
				int peerId = Integer.parseInt(lineSplit[0]);
				String hostName = lineSplit[1];
				int port = Integer.parseInt(lineSplit[2]);
				Boolean hasEntireFile = null;
				
				if (lineSplit[3].equals("0")) {
					hasEntireFile = Boolean.FALSE;
				} else {
					hasEntireFile = Boolean.TRUE;
				}
				
				peerConfig.setHasEntireFile(hasEntireFile);
				peerConfig.setHostName(hostName);
				peerConfig.setPeerId(peerId);
				peerConfig.setPortNum(port);
				
				peerConfigMap.put(peerId, peerConfig);			
			}		
		} 
		catch (IOException e) {
            throw e;
		}
		finally {
			try {
				if (peerConfigBufferedReader != null) {
					peerConfigBufferedReader.close();
				}
				
				if (peerFileReader != null) {
					peerFileReader.close();
				}
			}
			catch (IOException e) {
				throw e;
			}
		}
		
		return peerConfigMap;
		
	}
}
