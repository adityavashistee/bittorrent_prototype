package com.p2p.man.log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;	
public class PeerLogs {
		static String logSuffixForPeer;
		Logger peerLogger;
		int peerId;
		public PeerLogs()
		    {
			 peerLogger = Logger.getLogger(PeerLogs.class.getName());
			 
		    }
		public void createLogger(int peerId) throws SecurityException, IOException	{ 
			File logDirectory = new File("Peer_" + peerId);
		    if (!logDirectory.exists()){
		    	logDirectory.mkdir();
		    }
		    
		    String logFileName =  "Peer_" + peerId + "/Peer" + Integer.toString(peerId) + ".log";
		    Handler peerLogHandler = new FileHandler(logFileName);
		    peerLogHandler.setLevel(Level.parse("INFO"));
		    peerLogger.addHandler(peerLogHandler);
		    logSuffixForPeer = " Peer " + peerId + " ";
		    this.peerId = peerId;
		    }
		
		
		public String getLogger(int peerId) throws SecurityException, IOException{
			String logFileName = "Peer_" + peerId + "/Peer" + Integer.toString(peerId) + ".log";
			File logFile = new File(logFileName);
			if(!logFile.exists()){
				createLogger(peerId);
			}
			return logFileName;
		}
		
		
		public void closeLogHandlers(){
			   Handler[] logHandlers =   peerLogger.getHandlers();
			   for(int i=0; i<logHandlers.length; i++){
				   logHandlers[i].close();
			   }
		}
		
		public void logPeersConnecting(int peerId, int peerId2) throws SecurityException, IOException{
			this.peerId = peerId;
			String fName=getLogger(this.peerId);
			String task = " is trying to make a connection to Peer ";
			String msg = (new Date()).toString() + logSuffixForPeer + task + peerId2+".";
		    peerLogger.log (Level.INFO,msg);
		}
		
		public void logPeersConnected(int peerId, int peerId2) throws SecurityException, IOException{
			this.peerId = peerId;
			String fName=getLogger(this.peerId);
			String task = " is connected from Peer ";
			String msg = (new Date()).toString() + logSuffixForPeer + task + peerId2+".";
			peerLogger.log (Level.INFO,msg);	  
		}
		
		
		public void logPeersConnectionRefused(int peerId, int peerId2) throws SecurityException, IOException{
			this.peerId = peerId;
			String fName=getLogger(this.peerId);
			String task = " connection request refused by";
			String msg = (new Date()).toString() + logSuffixForPeer + task + peerId2+".";
			peerLogger.log (Level.INFO,msg);	  
		} 
		public void logPeerUpdatedOptUnckNeighbour(int peerId, int peerId2) throws SecurityException, IOException{
				this.peerId = peerId;
				String fName=getLogger(this.peerId);
			 	String task = " has the optimistically unchoked neighbour Peer ";
			 	String msg = (new Date()).toString() + logSuffixForPeer + task + peerId2+".";
			 	peerLogger.log (Level.INFO,msg);	
		  }
		
		public void logPeerUpdatedPreferredNeighbour(int peerId, String neighbourList) throws SecurityException, IOException{
			  this.peerId = peerId;
			  String fName=getLogger(this.peerId);
			  String task = " has the Preferred neighbours ";
			  String msg = (new Date()).toString() + logSuffixForPeer + task + neighbourList+".";
			  peerLogger.log (Level.INFO,msg);	
		      
		  }
		
		 public void logPeerChockingUnchoking(int peerId, int peerId2, String type) throws SecurityException, IOException{		
			 this.peerId = peerId;
		  	 String fName=getLogger(this.peerId);
		  	 String task = " is " + type +" by Peer ";
			 String msg = (new Date()).toString() + logSuffixForPeer + task +peerId2+".";
			 peerLogger.log (Level.INFO,msg);	
		  }
		  
		 public void logPeerInterestedNotInterested(int peerId, int peerId2, String type) throws SecurityException, IOException{		
			 this.peerId = peerId;
		  	 String fName=getLogger(this.peerId);
			 String task =  " received the "+ type +" message from Peer"; 
			 String msg = (new Date()).toString() + logSuffixForPeer + task + peerId2+".";
			 peerLogger.log (Level.INFO,msg);	
		  }
		
		  public void logPieceDownload(int peerId, int peerId2, int numberofpieces,  int piece_number) throws SecurityException, IOException
		  {		 this.peerId = peerId;
		  	 	 String fName=getLogger(this.peerId);
				 String task =  " has downloaded the piece "+piece_number+" from Peer "+peerId2+" and now have "+numberofpieces+ " pieces" ;
				 String msg = (new Date()).toString() + logSuffixForPeer + task +".";
				 peerLogger.log (Level.INFO,msg);
			
		  }
		  public void logCompleteDownLoad(int peerId) throws SecurityException, IOException
		  {
			  	 this.peerId = peerId;
			  	 String fName=getLogger(this.peerId);
				 String task =  " file download is completed " ;
				 String msg = (new Date()).toString() + logSuffixForPeer + task +".";
				 peerLogger.log (Level.INFO,msg);
				 
		  }
		  
		  public void haveMsgType(int peerId,int peerId2, int piece_index) throws SecurityException, IOException
		  {		 this.peerId = peerId;
			  	 String fName=getLogger(this.peerId);
				 String task =  " received the 'have' message from Peer "+peerId2+" for the piece "+piece_index+"." ;
				 String msg = (new Date()).toString() + logSuffixForPeer + task +".";
				 peerLogger.log (Level.INFO,msg);
				 
		  }	 
		  public void logGenerics(String message) throws SecurityException, IOException
		  {		
				
				 peerLogger.log (Level.INFO,message);
			
		  }
		  
}
