package com.p2p.man.conn;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.p2p.man.message.ChokeMessage;
import com.p2p.man.message.RequestMessage;
import com.p2p.man.message.UnChokeMessage;
import com.p2p.man.process.Peer;
import com.p2p.man.utils.CommonConfigConstants;
import com.p2p.man.utils.P2PUtils;

public class Neighbor implements NeighborInterface {
	public NeighborState neighborState = new NeighborState();
	static ArrayList<Integer> neighPeerIdList = new ArrayList<>();
	public NeighborState getNeighborState() {
		return neighborState;
	}

	public void setNeighborState(NeighborState neighborState) {
		this.neighborState = neighborState;
	}

	public static void getPreferredNeighbors(Peer peer) {
		int totalNeighbors = peer.getPeerState().getOtherPeers().size();
		List<Double> unorderedDownloadRates = new ArrayList<>();
		
		for (int neighId : peer.getPeerState().getOtherPeers().keySet()) {
			neighPeerIdList.add(neighId);
			unorderedDownloadRates.add(getDownloadRate(neighId, peer));			
			//unorderedDownloadRates[i] = getDownloadRate(i, peer);
		}
		
/*	//	List<Double> orderedDownloadRates = new ArrayList<>(); // double[totalNeighbors];
		List<Double> orderedDownloadRates = new ArrayList<>(unorderedDownloadRates);
		//System.arraycopy(unorderedDownloadRates, 0, orderedDownloadRates, 0, totalNeighbors);
		Collections.sort(orderedDownloadRates, new DownloadRateComparator());
		//Arrays.sort(orderedDownloadRates, Collections.reverseOrder());
		*/
		pickRequiredPreferredNeighbors(peer, unorderedDownloadRates);
		
		ArrayList<Integer> prefNeighborIds = new ArrayList<Integer>();
		HashMap<Integer, Neighbor> prefNeigbor = peer.getPeerState().getPreferredNeighbors();
		
		for (int peerID : prefNeigbor.keySet()) {
			prefNeighborIds.add(peerID);
		}
		
		try {
			peer.getPeerLogs().logPeerUpdatedPreferredNeighbour(peer.getPeerState().getPeerId(), prefNeighborIds.toString());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			System.out.println("Security Exception while logging new preferred neigbors");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IO Exception while logging new preferred neigbors");
			e.printStackTrace();
		}
		
		decideChokeUnchokePeer(peer); //Now that we got a list or preferred neighbors, decide which would be the choke/unchoke
		//neighbors.
	}

	private static void pickRequiredPreferredNeighbors(Peer peer, List<Double> unorderedDownloadRates) {
		//List<Double> orderedDownloadRates = new ArrayList<>();
		List<Double> orderedDownloadRates = new ArrayList<>(unorderedDownloadRates);
		//System.arraycopy(unorderedDownloadRates, 0, orderedDownloadRates, 0, totalNeighbors);
		Collections.sort(orderedDownloadRates, new DownloadRateComparator());
		//Arrays.sort(orderedDownloadRates, Collections.reverseOrder());
		
		int randomPick;
		int starrtPos = 0;
		int tie_length = 1;
		int numberOfPreferredNeighbour = peer.getPeerState().getNumberOfPreferredNeighbour();
		boolean[] top_pick = new boolean[peer.getPeerState().getOtherPeers().size()]; 
		List<Double> best_download_rates;
		best_download_rates = new ArrayList<Double>(orderedDownloadRates.subList(0, numberOfPreferredNeighbour));
	    if ((orderedDownloadRates.size() > numberOfPreferredNeighbour) && (orderedDownloadRates.get(numberOfPreferredNeighbour-1) == orderedDownloadRates.get(numberOfPreferredNeighbour))) {
	    		for (int i = numberOfPreferredNeighbour-1; i > 0; i--) { 
	    			if((i != 1) && (orderedDownloadRates.get(i) == orderedDownloadRates.get(i-1))) {
	    				starrtPos = i-1;
	    			} else {
	    				break;
	    			}
	    		}
	    		for (int i = starrtPos; i < orderedDownloadRates.size(); i++) {
	    			if((i != orderedDownloadRates.size()-1) && (orderedDownloadRates.get(i) == orderedDownloadRates.get(i+1))) {
	    				tie_length += 1;
	    			}
	    		}
	   }
	  HashMap<Integer, Neighbor> currentPrefNeigh = peer.getPeerState().getPreferredNeighbors(); 
	  for (int i = 0; i < numberOfPreferredNeighbour; i++) {
		  for(int j = 0; j < unorderedDownloadRates.size(); j++) {
			  if ((tie_length == 1 || i < starrtPos) && (best_download_rates.get(i) == unorderedDownloadRates.get(j)) && !top_pick[j]) {
				  //HashMap<Integer, Neighbor> currentPrefNeigh = peer.getPeerState().getPreferredNeighbors();
				  currentPrefNeigh.put(neighPeerIdList.get(i), peer.getPeerState().getOtherPeers().get(neighPeerIdList.get(i)));
				  peer.getPeerState().setPreferredNeighbors(currentPrefNeigh);
				  top_pick[j] = true;
				  break;
			  }
		  }
		  if (i >= starrtPos && tie_length != 0) {
			  while(true) {
				  randomPick = peer.randGen.nextInt(unorderedDownloadRates.size());
				  //  if(unorderedDownloadRates.get(rnd_indx) == best_download_rates.get(i) && !top_pick[rnd_indx]) {
				  if(unorderedDownloadRates.get(randomPick) == best_download_rates.get(i)) {
					  peer.getPeerState().setPreferredNeighbors(currentPrefNeigh);
					  //peer.pref_neighbours_clIDs[i] = rnd_indx; 
					  top_pick[randomPick] = true;
					  break;
				  }
			  }
		  }
	  }
		
	}

	private static double getDownloadRate(int i, Peer peer) {
		if (i != peer.getPeerState().getPeerId() && peer.getPeerState().getOtherPeers().get(i).getNeighborState().isHasConn() && 
				peer.getPeerState().getOtherPeers().get(i).getNeighborState().isInterested()) {
			int rate;
			rate = peer.getPeerState().getHasReceivedData().get(i)/peer.getPeerState().getUnchokingTimer();
			peer.getPeerState().getHasReceivedData().put(i,0);
			return rate;
		}
		else {
			return 0;
		}
	}
	
	public static void decideChokeUnchokePeer(Peer peer) {
		int optNeighId = 0;
		for(int prefNeighborID : peer.getPeerState().getPreferredNeighbors().keySet()){
			for(int otherPeerID : peer.getPeerState().getOtherPeers().keySet()){
				if((prefNeighborID == otherPeerID) && CommonConfigConstants.CONDITIONAL_CHECK){
					optNeighId = 1;
					break;
				}
			}
			if(prefNeighborID != peer.getPeerState().getPeerId()){
				if(optNeighId ==1){
					UnChokeMessage.sendMessage(prefNeighborID, peer);
				}else{
					ChokeMessage.sendMessage(prefNeighborID, peer);
				}
			}
		}
		
	}

	public static Neighbor getOptimisticallyUnchokedNeighbor(Peer peer) {
		List<Neighbor> values = new ArrayList<Neighbor>();
		int count = 0;
		HashMap<Integer, Neighbor> otherPeers = peer.getPeerState().getOtherPeers();
		
		for (int peerID : otherPeers.keySet()) {
			Neighbor neighbor = otherPeers.get(peerID);
			if (neighbor != null) {
				if (neighbor.getNeighborState().isInterested()) {
					values.add(neighbor);
					count++;
				}
			}			
		}
		
		Neighbor randomNeighbor = null;
		if (count > 0) {
			int randIndex = peer.randGen.nextInt(count);
			randomNeighbor = values.get(randIndex);
			int randomNeigborPeerID = randomNeighbor.getNeighborState().getPeerID();
			UnChokeMessage.sendMessage(randomNeigborPeerID, peer);
		}
		
		if (randomNeighbor != null) {
			try {
				peer.getPeerLogs().logPeerUpdatedOptUnckNeighbour(peer.getPeerState().getPeerId(), randomNeighbor.getNeighborState().getPeerID());
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return randomNeighbor;	
	}

}
