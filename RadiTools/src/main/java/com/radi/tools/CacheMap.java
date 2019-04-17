package com.radi.tools;

import java.util.HashMap;
import java.util.Map;

public class CacheMap {
	
	private static CacheMap cacheMap;
	private static Map<String,Object> map = new HashMap<>();
	
	private CacheMap(){}
	
	public static CacheMap getCacheMap(){
		if(cacheMap == null){
			synchronized(CacheMap.class){
				if(cacheMap == null){
					cacheMap = new CacheMap();
					map = new HashMap<>();
				}
			}
		}
		return cacheMap;
	}
	
	public void set(String key, Object obj){
		map.put(key, obj);
	}
	
	public Object get(String key){
		return map.get(key);
	}
	
	public int getInteger(String key){
		return map.get(key) == null ?  0 : (int)map.get(key);
	}
	
	public long getLong(String key){
		return map.get(key) == null ?  0 : (long)map.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getObject(String key, Class<T> c){
		return (T) map.get(key);
	}
	
	public String getPrefix(String deviceid,int index,String methodName){
		return deviceid + "_" + index + "_" + methodName+"_";
	}
	
}
