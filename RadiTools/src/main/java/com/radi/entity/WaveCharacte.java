package com.radi.entity;

/**
 * 某段波形的信息
 * @author leo
 *
 */
public class WaveCharacte {
	
	/**是否存在		1-存在，0不存在,只有存在才有特征，不存在则忽略*/
	private char ucExist;				
	/**幅度: 与基线的差值，正值表示在基线上-正向，负值表示在基线下-反向*/
	private int nAm;					
	/**所在位置，与nBbegin的相对位置，单位5ms*/
	private int nAm_i;					
	/**宽度*/
	private int nWidth;					
	/**起始点，与nBbegin的相对位置，单位5ms*/
	private int nBegin;			
	/**结束点，与nBbegin的相对位置，单位5ms*/
	private int nEnd;					
	/**经过BP滤波后的原始数据200ms*/
	private int[] nOrigal = new int[40];            
	
	public char getUcExist() {
		return ucExist;
	}
	public void setUcExist(char ucExist) {
		this.ucExist = ucExist;
	}
	public int getnAm() {
		return nAm;
	}
	public void setnAm(int nAm) {
		this.nAm = nAm;
	}
	public int getnAm_i() {
		return nAm_i;
	}
	public void setnAm_i(int nAm_i) {
		this.nAm_i = nAm_i;
	}
	public int getnWidth() {
		return nWidth;
	}
	public void setnWidth(int nWidth) {
		this.nWidth = nWidth;
	}
	public int getnBegin() {
		return nBegin;
	}
	public void setnBegin(int nBegin) {
		this.nBegin = nBegin;
	}
	public int getnEnd() {
		return nEnd;
	}
	public void setnEnd(int nEnd) {
		this.nEnd = nEnd;
	}
	public int[] getnOrigal() {
		return nOrigal;
	}
	public void setnOrigal(int[] nOrigal) {
		this.nOrigal = nOrigal;
	}
	
}
