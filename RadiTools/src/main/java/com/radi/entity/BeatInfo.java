package com.radi.entity;

public class BeatInfo {
	
	private long t0;
	/**在BP滤波器输出缓冲区的位置*/
	private int i_start;		
	/**相对i_start的位置*/
	private int i_end;		
	private int width;
	private WaveInfo p = new WaveInfo();
	private WaveInfo qrs = new WaveInfo();
	private WaveInfo t = new WaveInfo();
	
	/**J点位置*/
	private int i_J;		
	/**st点位置*/
	private int i_st_point;	
	/**基线电平*/
	private int isolevel;
	/**st点电平*/
	private int st_point;		
	/**qt间期*/
	private int qtinterval;			
	/**st诊断值 >0表示抬高多少，<0表示降低多少
	stlevel = st_point - isolevel.  单位：(mv)*/
	private int stlevel;			
	private int[] wave = new int[500];
	/**实时心率*/
	private int RealHeartRate;  
	/**平均心率，6点平均心率*/
	private int MeanHeartRate;
	/**心律失常信息*/   
	private ArrhythmiaInfo  arrhythmia;
	
	public long getT0() {
		return t0;
	}
	public void setT0(long t0) {
		this.t0 = t0;
	}
	public int getI_start() {
		return i_start;
	}
	public void setI_start(int i_start) {
		this.i_start = i_start;
	}
	public int getI_end() {
		return i_end;
	}
	public void setI_end(int i_end) {
		this.i_end = i_end;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public WaveInfo getP() {
		return p;
	}
	public void setP(WaveInfo p) {
		this.p = p;
	}
	public WaveInfo getQrs() {
		return qrs;
	}
	public void setQrs(WaveInfo qrs) {
		this.qrs = qrs;
	}
	public WaveInfo getT() {
		return t;
	}
	public void setT(WaveInfo t) {
		this.t = t;
	}
	public int getI_J() {
		return i_J;
	}
	public void setI_J(int i_J) {
		this.i_J = i_J;
	}
	public int getI_st_point() {
		return i_st_point;
	}
	public void setI_st_point(int i_st_point) {
		this.i_st_point = i_st_point;
	}
	public int getIsolevel() {
		return isolevel;
	}
	public void setIsolevel(int isolevel) {
		this.isolevel = isolevel;
	}
	public int getSt_point() {
		return st_point;
	}
	public void setSt_point(int st_point) {
		this.st_point = st_point;
	}
	public int getQtinterval() {
		return qtinterval;
	}
	public void setQtinterval(int qtinterval) {
		this.qtinterval = qtinterval;
	}
	public int getStlevel() {
		return stlevel;
	}
	public void setStlevel(int stlevel) {
		this.stlevel = stlevel;
	}
	public int[] getWave() {
		return wave;
	}
	public void setWave(int[] wave) {
		this.wave = wave;
	}
	public int getRealHeartRate() {
		return RealHeartRate;
	}
	public void setRealHeartRate(int realHeartRate) {
		RealHeartRate = realHeartRate;
	}
	public int getMeanHeartRate() {
		return MeanHeartRate;
	}
	public void setMeanHeartRate(int meanHeartRate) {
		MeanHeartRate = meanHeartRate;
	}
	public ArrhythmiaInfo getArrhythmia() {
		return arrhythmia;
	}
	public void setArrhythmia(ArrhythmiaInfo arrhythmia) {
		this.arrhythmia = arrhythmia;
	}              
	
}
