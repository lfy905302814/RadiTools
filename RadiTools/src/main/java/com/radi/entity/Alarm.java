package com.radi.entity;

/**
 * 报警
 * @author leo
 */
public class Alarm {
	
	/**通道*/
	private int chanel;
	/**报警类型*/
	private int type;
	/**报警级别*/
	private int level;
	
	public int getChanel() {
		return chanel;
	}
	public void setChanel(int chanel) {
		this.chanel = chanel;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
}