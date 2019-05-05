package com.radi.tools.test;

import com.radi.tools.CacheMap;

public class CacheMapTest {
	
	public static void main(String[] args) {
		CacheMap cache = CacheMap.getCacheMap();
		String prefix = cache.getPrefix("005", 1, "test");
		cache.set(prefix+"test", 555);
		System.out.println(cache.get(prefix+"test"));
		try {
			Thread.sleep(35000);
			System.out.println(cache.get(prefix+"test"));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
