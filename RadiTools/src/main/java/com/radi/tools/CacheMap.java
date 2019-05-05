package com.radi.tools;

import java.util.HashMap;
import java.util.Map;

public class CacheMap {

	private static final long DEFAULT_TIMEOUT = 30000;
	private static CacheMap cacheMap;
	private static Map<String, CacheEntry> map = new HashMap<>();
	private long cacheTimeout;

	private CacheMap() {
	}

	private CacheMap(long timeout) {
		this.cacheTimeout = timeout;
		new ClearThread().start();
	}

	public static CacheMap getCacheMap() {
		if (cacheMap == null) {
			synchronized (CacheMap.class) {
				if (cacheMap == null) {
					cacheMap = new CacheMap(DEFAULT_TIMEOUT);
					map = new HashMap<>();
				}
			}
		}
		return cacheMap;
	}

	public void set(String key, Object obj) {
		CacheEntry cache = new CacheEntry(obj);
		synchronized (map) {
			map.put(key, cache);
		}
	}

	public String get(String key) {
		return map.get(key) == null ? "" : map.get(key).getValue()+"";
	}

	public int getInteger(String key) {
		return map.get(key) == null ? 0 : (int) map.get(key).getValue();
	}

	public long getLong(String key) {
		return map.get(key) == null ? 0 : (long) map.get(key).getValue();
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T getObject(String key, Class<T> c) {
		CacheEntry obj = map.get(key);
		if(obj == null) return null;
		return (T) obj.getValue();
	}

	public String getPrefix(String deviceid, int index, String methodName) {
		return deviceid + "_" + index + "_" + methodName + "_";
	}

	private class CacheEntry {
		long time;
		Object value;

		CacheEntry(Object value) {
			super();
			this.value = value;
			this.time = System.currentTimeMillis();
		}

		public Object getValue() {
			return value;
		}

	}

	private class ClearThread extends Thread {
		ClearThread() {
			setName("clear cache thread");
		}

		public void run() {
			while (true) {
				try {
					long now = System.currentTimeMillis();
					Object[] keys = map.keySet().toArray();
					for (Object key : keys) {
						CacheEntry entry = map.get(key);
						if (now - entry.time >= cacheTimeout) {
							synchronized (map) {
								map.remove(key);
							}
						}
					}
					Thread.sleep(cacheTimeout);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
