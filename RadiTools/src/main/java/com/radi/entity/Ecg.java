package com.radi.entity;

public class Ecg {
	
	private int rRCount;
	
	private Beat beat = new Beat();
	
	private int meanIndex;

	public int getrRCount() {
		return rRCount;
	}

	public void setrRCount(int rRCount) {
		this.rRCount = rRCount;
	}

	public Beat getBeat() {
		return beat;
	}

	public void setBeat(Beat beat) {
		this.beat = beat;
	}

	public int getMeanIndex() {
		return meanIndex;
	}

	public void setMeanIndex(int meanIndex) {
		this.meanIndex = meanIndex;
	}

}
