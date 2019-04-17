package com.radi.entity;

import com.radi.tools.AlgorithmTool;

public class Beat {
	
	private int[] que = new int[AlgorithmTool.BEAT_QUE_LENGTH];
	private int QueCount;
	
	public int[] getQue() {
		return que;
	}
	public void setQue(int[] que) {
		this.que = que;
	}
	public int getQueCount() {
		return QueCount;
	}
	public void setQueCount(int queCount) {
		QueCount = queCount;
	}
	
}
