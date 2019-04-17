package com.radi.tools.test;

import java.util.HashMap;
import java.util.Map;

import com.radi.entity.Dat;
import com.radi.entity.Wave;
import com.radi.tools.AlgorithmTool;
import com.radi.tools.CacheMap;

public class Test{
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		long time = System.currentTimeMillis();
		CacheMap cacheMap = CacheMap.getCacheMap();
		for (int i = 0; i < 7000; i++) {
			//Wave wave = JedisUtils.getObject("test_", "wave", Wave.class);
			Wave wave = null;
			int test = cacheMap.getInteger("test_" + "int");
			if(wave==null) wave = new Wave();
			Dat[] data = wave.getiDat();
			for (int j = 0; j < AlgorithmTool.WAVE_LEN; j++) {
				Dat iDat = data[j];
				iDat.setTime(time);
				iDat.setValue(iDat.getValue()+1);
			}
			//String s = JSONObject.toJSONString(data);
			//Dat[] d = JSON.parseObject(s, Dat[].class);
			Map<String,Object> map = new HashMap<>();
			map.put("aaa", data);
			//Dat[] temp = (Dat[]) map.get("aaa");
			//String json = JSONObject.toJSONString(wave);
			//Wave w = JSON.parseObject(json, Wave.class);
			wave.setiWritePoint(i);
			cacheMap.set("test_" + "int", test);
			//JedisUtils.setToJson("test_", "wave", wave);
		}
		long end = System.currentTimeMillis();
		System.out.println((end-start)/1000.00);
	}
	
}
