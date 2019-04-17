package com.radi.entity;

import com.radi.tools.AlgorithmTool;

public class Tempbeat {
	
	private Dat[] iDat = new Dat[AlgorithmTool.BEAT_ORG_LENGTH_MAX];
	private int nBegin;
	private int nWidth;
	private int nPeakIndex;
	
	public Tempbeat(){
		super();
		for (int i = 0; i < AlgorithmTool.BEAT_ORG_LENGTH_MAX; i++) {
			iDat[i] = new Dat();
		}
	}
	
	public Dat[] getiDat() {
		return iDat;
	}
	public void setiDat(Dat[] iDat) {
		this.iDat = iDat;
	}
	public int getnBegin() {
		return nBegin;
	}
	public void setnBegin(int nBegin) {
		this.nBegin = nBegin;
	}
	public int getnWidth() {
		return nWidth;
	}
	public void setnWidth(int nWidth) {
		this.nWidth = nWidth;
	}
	public int getnPeakIndex() {
		return nPeakIndex;
	}
	public void setnPeakIndex(int nPeakIndex) {
		this.nPeakIndex = nPeakIndex;
	}
	
}
