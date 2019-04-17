package com.radi.entity;

public class Ecglead {
	
	/**导联类型*/
	private int ecgtype; 
	/**脱落标识*/
	private int lead;	
	
	public int getEcgtype() {
		return ecgtype;
	}
	public void setEcgtype(int ecgtype) {
		this.ecgtype = ecgtype;
	}
	public int getLead() {
		return lead;
	}
	public void setLead(int lead) {
		this.lead = lead;
	}
	
	@Override
	public String toString() {
		return super.toString()+"{"+this.ecgtype+"-"+this.lead+"}";
	}
	
}
