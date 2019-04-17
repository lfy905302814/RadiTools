package com.radi.entity;

/**
 * 心律失常分析结果
 * @author leo
 *
 */
public class ArrhythmiaInfo {
	
	/**心律失常类别*/  
	private ARRH_TYPE ArrhythmiaType;  
	/**起始时间，当NORMAL时，时间为0*/
	private long nStartTime;           
	/**结束时间, 当NORMAL时，时间为0*/
	private long nEndTime;
	/**状态字 -- 预留*/
	private int nState;                                     
	
	public ARRH_TYPE getArrhythmiaType() {
		return ArrhythmiaType;
	}
	public void setArrhythmiaType(ARRH_TYPE arrhythmiaType) {
		ArrhythmiaType = arrhythmiaType;
	}
	public long getnStartTime() {
		return nStartTime;
	}
	public void setnStartTime(long nStartTime) {
		this.nStartTime = nStartTime;
	}
	public long getnEndTime() {
		return nEndTime;
	}
	public void setnEndTime(long nEndTime) {
		this.nEndTime = nEndTime;
	}
	public void setnEndTime(int nEndTime) {
		this.nEndTime = nEndTime;
	}
	public int getnState() {
		return nState;
	}
	public void setnState(int nState) {
		this.nState = nState;
	}
	
}
