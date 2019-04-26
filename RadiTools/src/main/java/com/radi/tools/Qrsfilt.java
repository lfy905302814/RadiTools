package com.radi.tools;

import com.radi.entity.FILTER_TYPE;

public class Qrsfilt {

	public final static int SAMPLE_RATE = 200;
	public final static double MS_PER_SAMPLE = 1000.00 / SAMPLE_RATE;
	public final static int MS10 = (int) (10 / MS_PER_SAMPLE + 0.5);
	public final static int MS20 = (int) (20 / MS_PER_SAMPLE + 0.5);
	public final static int MS25 = (int) (25 / MS_PER_SAMPLE + 0.5);
	public final static int MS40 = (int) (40 / MS_PER_SAMPLE + 0.5);
	public final static int MS80 = (int) (80 / MS_PER_SAMPLE + 0.5);
	public final static int MS95 = (int) (95 / MS_PER_SAMPLE + 0.5);
	public final static int MS100 = (int) (100 / MS_PER_SAMPLE + 0.5);
	public final static int MS125 = (int) (125 / MS_PER_SAMPLE + 0.5);
	public final static int MS150 = (int) (150 / MS_PER_SAMPLE + 0.5);
	public final static int MS195 = (int) (195 / MS_PER_SAMPLE + 0.5);
	public final static int MS200 = (int) (200 / MS_PER_SAMPLE + 0.5);
	public final static int MS220 = (int) (220 / MS_PER_SAMPLE + 0.5);
	public final static int MS360 = (int) (360 / MS_PER_SAMPLE + 0.5);
	public final static int HPBUFFER_LGTH = MS125;
	public final static int LPBUFFER_LGTH = 2 * MS25;
	public final static int WINDOW_WIDTH = MS80;
	public final static int DERIV_LENGTH = MS10;
	public final static int MS1000 = SAMPLE_RATE;
	public final static int MS1500 = (int) (1500/MS_PER_SAMPLE);
	public final static int PRE_BLANK = MS195;
	public final static int FILTER_DELAY = (int) ((DERIV_LENGTH/2.00) + (LPBUFFER_LGTH/2.00 - 1) + ((HPBUFFER_LGTH-1)/2.00) + PRE_BLANK); 
	public final static int DER_DELAY = WINDOW_WIDTH + FILTER_DELAY + MS100;

	static int FilterProcess(FILTER_TYPE filter_type, int datum, int[] pbuf, String deviceid, int index) {
		int fdatum = 0;
		int i = 0;
		int temp;
		if (filter_type == FILTER_TYPE.OLDFILTER)/*use old filter*/
		{
			pbuf[i++]=fdatum=datum;
			pbuf[i++]=fdatum=Filter.iir_filter_005_80hz_bp_I(fdatum);/*Filter.iir_filter_005_80hz_bp_I(fdatum)*/;
			pbuf[i++]=fdatum = deriv2(fdatum, deviceid, index) ;	// Take the derivative.
			pbuf[i++]=temp=(int)(Math.abs(fdatum)*Math.abs(fdatum));		// Take the absolute value.
			pbuf[i++]=fdatum = mvwint(temp, deviceid, index) ;	// Average over an 80 ms window .
		}
		else if (filter_type == FILTER_TYPE.DIAGNOSE)/*诊断模式*/
		{
			pbuf[i++]=fdatum=datum;
			pbuf[i++]=fdatum=Filter_005_99hz.iir_filter_005_99hz_bp_I(fdatum);
			pbuf[i++]=fdatum = deriv2(fdatum, deviceid, index) ;	// Take the derivative.
			pbuf[i++]=temp=(int)(Math.abs(fdatum)*Math.abs(fdatum));				// Take the absolute value.
			pbuf[i++]=fdatum = mvwint(temp, deviceid, index) ;	// Average over an 80 ms window .
		}
		else if (filter_type == FILTER_TYPE.MONITOR)/*监护模式*/
		{
			pbuf[i++]=fdatum=datum;
			pbuf[i++]=fdatum=Filter_05_40hz.iir_filter_05_40hz_bp_I(fdatum);/*Filter_05_40hz.iir_filter_05_40hz_bp_I(fdatum)*/;
			pbuf[i++]=fdatum = deriv2(fdatum, deviceid, index) ;	// Take the derivative.
			pbuf[i++]=temp=(int)(Math.abs(fdatum)*Math.abs(fdatum));				// Take the absolute value.
			pbuf[i++]=fdatum = mvwint(temp, deviceid, index) ;	// Average over an 80 ms window .
		}
		else if (filter_type == FILTER_TYPE.OPRATION)/*手术模式*/
		{
			pbuf[i++]=fdatum=datum;
			pbuf[i++]=fdatum=Filter.iir_filter_1_40hz_bp_DingZong(fdatum, 1);/*iir_filter_1_40hz_bp_I(fdatum);*/
			pbuf[i++]=fdatum = Filter.Derivative(fdatum,deviceid, index)/*deriv2( fdatum, 0 )*/ ;	// Take the derivative.
			pbuf[i++]=temp=(int)(Math.abs(fdatum)*Math.abs(fdatum));			// Take the absolute value.
			pbuf[i++]=fdatum = mvwint(temp,deviceid,index)*4 ;	// Average over an 80 ms window .
		}
		else if (filter_type == FILTER_TYPE.COMPARATOR)/*滤波比较测试*/
		{
			pbuf[i++]=fdatum=datum;
			pbuf[i++]=fdatum=Filter.iir_filter_1_40hz_bp_DingZong(datum, 1);
			pbuf[i++]=fdatum=Filter.iir_filter_005_80hz_bp_I(datum)*10;
			pbuf[i++]=fdatum=Filter_005_99hz.iir_filter_005_99hz_bp_I(datum)*10;
			pbuf[i++]=fdatum=Filter_05_40hz.iir_filter_05_40hz_bp_I(datum)*10;
			pbuf[i++]=fdatum=Filter_1_40hz.iir_filter_1_40hz_bp_I(datum)*10;
		}
		return (fdatum);
	}

	private static int deriv2(int x, String deviceid, int index) {
		CacheMap cacheMap = CacheMap.getCacheMap();
		String prefix = cacheMap.getPrefix(deviceid, index, "deriv2");
		int derI = cacheMap.getInteger(prefix + "derI");
		int[] derBuff = cacheMap.getObject(prefix + "derBuff", int[].class);
		if(derBuff == null) derBuff = new int[DERIV_LENGTH];
		int y;
		y = x - derBuff[derI];
		derBuff[derI] = x;
		if (++derI == DERIV_LENGTH)
			derI = 0;
		cacheMap.set(prefix + "derBuff", derBuff);
		cacheMap.set(prefix + "derI", derI);
		return y;
	}

	static int deriv1(int x, String deviceid, int index) {
		CacheMap cacheMap = CacheMap.getCacheMap();
		String prefix = cacheMap.getPrefix(deviceid, index, "deriv1");
		int derI = cacheMap.getInteger(prefix + "derI");
		int[] derBuff = cacheMap.getObject(prefix + "derBuff", int[].class);
		if(derBuff == null) derBuff = new int[DERIV_LENGTH];
		int y;
		y = x - derBuff[derI];
		derBuff[derI] = x;
		if (++derI == DERIV_LENGTH)
			// 10ms长度
			derI = 0;
		cacheMap.set(prefix + "derBuff", derBuff);
		cacheMap.set(prefix + "derI", derI);
		return y;
	}

	private static int mvwint(int datum, String deviceid, int index) {
		CacheMap cacheMap = CacheMap.getCacheMap();
		String prefix = cacheMap.getPrefix(deviceid, index, "mvwint");
		long sum = cacheMap.getLong(prefix + "sum");
		int ptr = cacheMap.getInteger(prefix + "ptr");
		int[] data = cacheMap.getObject(prefix + "data", int[].class);
		if(data == null) data = new int[WINDOW_WIDTH];
		int output;
		sum += datum;
		sum -= data[ptr];
		data[ptr] = datum;
		if (++ptr == WINDOW_WIDTH)
			ptr = 0;
		if ((sum / WINDOW_WIDTH) > 8388608) {
			output = 8388608;
		} else {
			output = (int) (sum / WINDOW_WIDTH);
		}
		cacheMap.set(prefix + "sum", sum);
		cacheMap.set(prefix + "data", data);
		cacheMap.set(prefix + "ptr", ptr);
		return output;
	}

	/*private static int lpfilt(int datum,String deviceid, int index) {
		CacheMap cacheMap = CacheMap.getCacheMap();
		String prefix = cacheMap.getPrefix(deviceid, index, "lpfilt");
		long y1 = cacheMap.getLong(prefix + "y1");
		long y2 = cacheMap.getLong(prefix + "y2");
		int ptr = cacheMap.getInteger(prefix + "ptr");
		int[] data = cacheMap.getObject(prefix + "data", int[].class);
		if(data == null) data = new int[LPBUFFER_LGTH];
		long y0;
		int output, halfPtr;
		halfPtr = ptr - (LPBUFFER_LGTH / 2);
		if (halfPtr < 0)
			halfPtr += LPBUFFER_LGTH;
		y0 = (y1 << 1) - y2 + datum - (data[halfPtr] << 1) + data[ptr];
		y2 = y1;
		y1 = y0;
		output = (int) (y0 / ((LPBUFFER_LGTH * LPBUFFER_LGTH) / 4));
		data[ptr] = datum;
		if (++ptr == LPBUFFER_LGTH)
			ptr = 0;
		cacheMap.set(prefix + "y1", y1);
		cacheMap.set(prefix + "y2", y2);
		cacheMap.set(prefix + "data", data);
		cacheMap.set(prefix + "ptr", ptr);
		return output;
	}

	private static int hpfilt(int datum, String deviceid, int index) {
		CacheMap cacheMap = CacheMap.getCacheMap();
		String prefix = cacheMap.getPrefix(deviceid, index, "hpfilt");
		long y = cacheMap.getLong(prefix + "y");
		int ptr = cacheMap.getInteger(prefix + "ptr");
		int[] data = cacheMap.getObject(prefix + "data", int[].class);
		if(data == null) data = new int[HPBUFFER_LGTH];
		int z, halfPtr;
		y += datum - data[ptr];
		halfPtr = ptr - (HPBUFFER_LGTH / 2);
		if (halfPtr < 0)
			halfPtr += HPBUFFER_LGTH;
		z = (int) (data[halfPtr] - (y / HPBUFFER_LGTH));
		data[ptr] = datum;
		if (++ptr == HPBUFFER_LGTH)
			ptr = 0;
		cacheMap.set(prefix + "y", y);
		cacheMap.set(prefix + "ptr", ptr);
		cacheMap.set(prefix + "data", data);
		return z;
	}*/
	
}
