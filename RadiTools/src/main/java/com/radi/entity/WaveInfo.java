package com.radi.entity;

/**
 * 算法返回： 单个心跳节拍信息
 */
public class WaveInfo {
	
	private int i_start;			
	private int i_end;
	private int	i_peak;
	private int am_peak;
	
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
	public int getI_peak() {
		return i_peak;
	}
	public void setI_peak(int i_peak) {
		this.i_peak = i_peak;
	}
	public int getAm_peak() {
		return am_peak;
	}
	public void setAm_peak(int am_peak) {
		this.am_peak = am_peak;
	}
	
}
