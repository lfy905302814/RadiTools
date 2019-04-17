package com.radi.entity;

/**
 * 某个点的信息
 * @author leo
 *
 */
public class PointCharacte {
	
	/**电平值 mv*/
	int V;      
	/**位置，与nBbegin的相对位置，单位5ms*/
    int i_V;                    
    
	public int getV() {
		return V;
	}
	public void setV(int v) {
		V = v;
	}
	public int getI_V() {
		return i_V;
	}
	public void setI_V(int i_V) {
		this.i_V = i_V;
	}
    
}
