package com.radi.entity;

public class InputWave {
	
	/**dat[0]的实时时间(unix时间)，单位ms*/
	private long t0;
	/*心电数据缓冲区
	private int[] dat = new int[AlgorithmTool.WAVE_LEN];*/
	/**心电数据*/
	private int dat;
	/**有效数据长度*/
	private int len;
	
	public long getT0() {
		return t0;
	}
	public void setT0(long t0) {
		this.t0 = t0;
	}
	public int getDat() {
		return dat;
	}
	public void setDat(int dat) {
		this.dat = dat;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	
}
