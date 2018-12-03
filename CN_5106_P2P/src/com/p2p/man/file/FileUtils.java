package com.p2p.man.file;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.p2p.man.config.CommonConfig;

public class FileUtils {

	public static byte[][] splitFile(int peerId, boolean hasFile, CommonConfig commonConfig) throws IOException{
		int noOfPieces =  (int) Math.ceil(commonConfig.getFileSize()/commonConfig.getPieceSize());
		int pieceSize = commonConfig.getPieceSize();
		byte[][] filePieces = new byte[noOfPieces][pieceSize];
		
		if(hasFile == true){
			FileInputStream fileInputStream = new FileInputStream(commonConfig.getFileName());
			for(int i = 0; i<noOfPieces; i++ ){
				fileInputStream.read(filePieces[i], 0, pieceSize);
			}
			fileInputStream.close();
		}
		
		return filePieces;
	}
	
	  public static void combinePiecesOfFile(int peerId, CommonConfig commonConfig, byte[][] filePieces) {

	        try {
	        	int noOfPieces = (int) Math.ceil(commonConfig.getFileSize()/commonConfig.getPieceSize());
	            FileOutputStream outputStream = new FileOutputStream("peer_" + peerId + "//" + commonConfig.getFileName());

	            for (int i = 0; i <noOfPieces-1; i++) {
	            	outputStream.write(filePieces[i]);
	            }
	            //processing the last piece
	            int k = filePieces.length-1;
	            while (k >= 0 && filePieces[noOfPieces-1][k] == 0)
	                k=k-1;
	            outputStream.write(Arrays.copyOf(filePieces[noOfPieces-1], k + 1));
	            outputStream.close();
	        } catch (Exception e) {
	            //Error assembling file pieces
	            System.exit(0);
	        }

	    }
}
