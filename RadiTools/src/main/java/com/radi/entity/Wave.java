package com.radi.entity;

import com.radi.tools.AlgorithmTool;

public class Wave {
	
	private int iWritePoint;
	private Dat[] iDat = new Dat[AlgorithmTool.WAVE_LEN];
	
	public Wave(){
		super();
		for (int i = 0; i < AlgorithmTool.WAVE_LEN; i++) {
			iDat[i] = new Dat();
		}
	}
	
	public int getiWritePoint() {
		return iWritePoint;
	}
	public void setiWritePoint(int iWritePoint) {
		this.iWritePoint = iWritePoint;
	}
	public Dat[] getiDat() {
		return iDat;
	}
	public void setiDat(Dat[] iDat) {
		this.iDat = iDat;
	}
	
}
