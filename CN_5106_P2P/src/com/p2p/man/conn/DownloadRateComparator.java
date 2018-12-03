package com.p2p.man.conn;

import java.util.Comparator;

public class DownloadRateComparator implements Comparator<Double> {

	@Override
	public int compare(Double input1, Double input2) {
		return input1 < input2 ? 1 : input1 == input2 ? 0 : -1;
	}

}
