package com.radi.entity;

import java.util.List;
import java.util.Map;

/**
 * 传入参数
 * @author leo
 *
 */
public class Parameters{
	
	private Long datetime;
	/**0为心跳数据,1为正常数据*/
	private short datatype;
	
	private String deviceid;
	private String patienid;
	private String drxrssi;
	private String erxrssi;
	private String wrxrssi;
	/**ecgtype导联类型;lead脱落标识*/
	private Ecglead ecglead;
	/**心电*/
	private List<Ecgwave> ecgwave;
	/**resp阻抗式呼吸波。40hz, 0.05-0.8hz的滤波*/
	private List<Map<String,Object>> respwave;
	/**nresp鼻式呼吸波，不用滤波*/
	private List<Map<String,Object>> nrespwave;
	/**pelth血氧容积波，不用滤波*/
	private List<Map<String,Object>> pelth;
	/**pluse:,press血压波形，不用滤波*/
	private List<Map<String,Object>> bpwave;
	/**心率*/
	private String heartrate;
	/**阻抗呼吸率*/
	private String resprate;
	/**鼻式呼吸率*/
	private String nresprate;
	/**鼻式流量*/
	private String nrespflow;
	/**鼻式呼吸气流量*/
	private String gasflow;
	/**血氧饱和度*/
	private String spo2rate;
	/**脉率*/
	private String pluserate;
	/**体温*/
	private String temp;
	/**电池容量*/
	private String battery;
	/**收缩压*/
	private String syspres;
	/**平均压*/
	private String meanpres;
	/**舒张压*/
	private String diapres;
	/**错误代码*/
	private String errorcode;
	/**体位*/
	private String boodypost;
	/**呼吸氧气含量*/
	private String o2content;
	/**呼吸二氧化碳含量*/
	private String co2content;
	/**打标*/
	private String sleepstatus;
	/**护士呼叫*/
	private String nursereq;
	
	private String cmdack;
	/**报警*/
	private List<Alarm> alarm;
	
	public Long getDatetime() {
		return datetime;
	}
	public void setDatetime(Long datetime) {
		this.datetime = datetime;
	}
	public short getDatatype() {
		return datatype;
	}
	public void setDatatype(short datatype) {
		this.datatype = datatype;
	}
	public String getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}
	public String getPatienid() {
		return patienid;
	}
	public void setPatienid(String patienid) {
		this.patienid = patienid;
	}
	public String getDrxrssi() {
		return drxrssi;
	}
	public void setDrxrssi(String drxrssi) {
		this.drxrssi = drxrssi;
	}
	public String getErxrssi() {
		return erxrssi;
	}
	public void setErxrssi(String erxrssi) {
		this.erxrssi = erxrssi;
	}
	public String getWrxrssi() {
		return wrxrssi;
	}
	public void setWrxrssi(String wrxrssi) {
		this.wrxrssi = wrxrssi;
	}
	public Ecglead getEcglead() {
		return ecglead;
	}
	public void setEcglead(Ecglead ecglead) {
		this.ecglead = ecglead;
	}
	public List<Ecgwave> getEcgwave() {
		return ecgwave;
	}
	public void setEcgwave(List<Ecgwave> ecgwave) {
		this.ecgwave = ecgwave;
	}
	public List<Map<String, Object>> getRespwave() {
		return respwave;
	}
	public void setRespwave(List<Map<String, Object>> respwave) {
		this.respwave = respwave;
	}
	public List<Map<String, Object>> getNrespwave() {
		return nrespwave;
	}
	public void setNrespwave(List<Map<String, Object>> nrespwave) {
		this.nrespwave = nrespwave;
	}
	public List<Map<String, Object>> getPelth() {
		return pelth;
	}
	public void setPelth(List<Map<String, Object>> pelth) {
		this.pelth = pelth;
	}
	
	public List<Map<String, Object>> getBpwave() {
		return bpwave;
	}
	public void setBpwave(List<Map<String, Object>> bpwave) {
		this.bpwave = bpwave;
	}
	public String getHeartrate() {
		return heartrate;
	}
	public void setHeartrate(String heartrate) {
		this.heartrate = heartrate;
	}
	public String getResprate() {
		return resprate;
	}
	public void setResprate(String resprate) {
		this.resprate = resprate;
	}
	public String getNresprate() {
		return nresprate;
	}
	public void setNresprate(String nresprate) {
		this.nresprate = nresprate;
	}
	public String getNrespflow() {
		return nrespflow;
	}
	public void setNrespflow(String nrespflow) {
		this.nrespflow = nrespflow;
	}
	public String getGasflow() {
		return gasflow;
	}
	public void setGasflow(String gasflow) {
		this.gasflow = gasflow;
	}
	public String getSpo2rate() {
		return spo2rate;
	}
	public void setSpo2rate(String spo2rate) {
		this.spo2rate = spo2rate;
	}
	public String getPluserate() {
		return pluserate;
	}
	public void setPluserate(String pluserate) {
		this.pluserate = pluserate;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
	public String getSyspres() {
		return syspres;
	}
	public void setSyspres(String syspres) {
		this.syspres = syspres;
	}
	public String getMeanpres() {
		return meanpres;
	}
	public void setMeanpres(String meanpres) {
		this.meanpres = meanpres;
	}
	public String getDiapres() {
		return diapres;
	}
	public void setDiapres(String diapres) {
		this.diapres = diapres;
	}
	public String getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
	public String getBoodypost() {
		return boodypost;
	}
	public void setBoodypost(String boodypost) {
		this.boodypost = boodypost;
	}
	public String getO2content() {
		return o2content;
	}
	public void setO2content(String o2content) {
		this.o2content = o2content;
	}
	public String getCo2content() {
		return co2content;
	}
	public void setCo2content(String co2content) {
		this.co2content = co2content;
	}
	public String getSleepstatus() {
		return sleepstatus;
	}
	public void setSleepstatus(String sleepstatus) {
		this.sleepstatus = sleepstatus;
	}
	public String getNursereq() {
		return nursereq;
	}
	public void setNursereq(String nursereq) {
		this.nursereq = nursereq;
	}
	public String getCmdack() {
		return cmdack;
	}
	public void setCmdack(String cmdack) {
		this.cmdack = cmdack;
	}
	public List<Alarm> getAlarm() {
		return alarm;
	}
	public void setAlarm(List<Alarm> alarm) {
		this.alarm = alarm;
	}
	
}