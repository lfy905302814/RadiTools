package com.radi.tools.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MessageG4Helper {

	private final static int TAGLEN = 2;

	public static void readFile(String filePath) {
		File file = new File(filePath);
		FileInputStream in1 = null;
		DataInputStream data_in = null;
		byte[] itemBuf = null;
		try {
			in1 = new FileInputStream(file);
			data_in = new DataInputStream(in1);
			while (true) {
				itemBuf = new byte[TAGLEN];
				data_in.read(itemBuf, 0, TAGLEN);
				int length = byteToInt(new byte[] { itemBuf[0], itemBuf[1] });
				itemBuf = null;
				if (length == 0) break;
				itemBuf = new byte[length];
				data_in.read(itemBuf, 0, length);
				int itemsum = 0;
				for (int j = 2; j < length - 1; j++) {
					itemsum += itemBuf[j] & 0xff;
				}
				itemsum = byteToInt(new byte[] { (byte) itemsum });
				int checknum = byteToInt(new byte[] { itemBuf[length - 1] });
				if (itemsum != checknum) break;
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("deviceid", byteToInt(new byte[] { itemBuf[13], itemBuf[12], itemBuf[11], itemBuf[10] }));
				jsonobj.put("packagenum", byteToInt(new byte[] { itemBuf[17], itemBuf[16], itemBuf[15], itemBuf[14] }));
				handleData(itemBuf, jsonobj);
				// TODO 保存jsonobj
				System.out.println(jsonobj);
				itemBuf = null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				itemBuf = null;
				data_in.close();
				in1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void handleData(byte[] data, JSONObject jsonobj) {
		int len = data.length;
		int index = 24;
		while (index < len) {
			int dataType = byteToInt(new byte[] { data[index - 3] });
			if (dataType == 0) {
				// 心跳数据
				index++;
			}else if(dataType == 200){
				// 远程升级模式，未定义数据格式
			}else {
				int dataLen = byteToInt(new byte[] { data[index - 1], data[index - 2] });
				// 数据处理
				setData(dataType, Arrays.copyOfRange(data, index, index + dataLen), jsonobj);
				index += dataLen;
			}
		}
	}

	private static void setData(int type, byte[] data, JSONObject jsonobj) {
		if (type == 1) {
			// 心电{ecgwave:[{ecg1:,ecg2:,ecg3:}]}
			JSONArray objarr = EcgData(data, 1);
			jsonobj.put("ecgwave", objarr);
		} else if (type == 2) {
			// 血氧容积波
			JSONArray objarr = PELTHData(data);
			jsonobj.put("pelth", objarr);
		} else if (type == 4) {
			// mmHg), 40Hz,数据顺序：脉搏波(1BYTE)/压力波(1BYTE)
			JSONArray objarr = BPWaveData(data);
			jsonobj.put("bpwave", objarr);
		} else if (type == 5) {
			// 阻抗式呼吸波
			JSONArray objarr = RespData(data);
			jsonobj.put("respwave", objarr);
		} else if (type == 10) {
			// 电池容量（百分比）
			jsonobj.put("battery", byteToInt(data));
		} else if (type == 11) {
			// 信号（百分比)
			jsonobj.put("signal", byteToInt(data));
		} else if (type == 12) {
			// 状态值
			jsonobj.put("state", byteToInt(new byte[] { data[0] }));
		} else if (type == 13) {
			// 呼吸率，范围[0,60]
			jsonobj.put("resprate", byteToInt(data));
		} else if (type == 14) {
			// 心率，范围[0,300]
			jsonobj.put("heartrate", byteToInt(new byte[] { data[1], data[0] }));
		} else if (type == 15) {
			// 脉率，范围[0,255]
			jsonobj.put("pluserate", byteToInt(data));
		} else if (type == 16) {
			// 血氧饱和度，百分比，范围[0,100]
			jsonobj.put("spo2rate", byteToInt(data));
		} else if (type == 17) {
			// 体温，exp：366=36.6度
			jsonobj.put("temp", byteToInt(new byte[] { data[1], data[0] }));
		} else if (type == 18) {
			// 血压测量结果
			jsonobj.put("errorcode", byteToInt(new byte[] { data[0] }));
			jsonobj.put("syspres", byteToInt(new byte[] { data[2], data[1] }));
			jsonobj.put("diapres", byteToInt(new byte[] { data[4], data[3] }));
			jsonobj.put("meanpres", byteToInt(new byte[] { data[6], data[5] }));
		} else if (type == 19) {
			// 病人呼叫（1呼叫，0取消呼叫）
			jsonobj.put("nursereq", byteToInt(data));
		}else if (type == 20) {
			//鼻式呼吸波
			jsonobj.put("boodypost", byteToInt(new byte[] { data[0] }));
			jsonobj.put("nresprate", byteToInt(new byte[] { data[1] }));
			jsonobj.put("nrespflow", byteToInt(new byte[] { data[3], data[2]}));
			JSONArray objarr = NRespWaveData(Arrays.copyOfRange(data, 4, data.length));
			jsonobj.put("nrespwave", objarr);
		}else if (type == 103) {
			// 第1个字节:1单次测压;2关自动测量;3开自动测量;
		}
	}

	/**
	 * 处理心电数据
	 * 
	 * @param bytes
	 */
	private static JSONArray EcgData(byte[] bytes, int ecgtype) {
		JSONArray jsonObj = new JSONArray();
		int len = bytes.length / 2 / 3;
		String[] lead = new String[0];
		switch (ecgtype) {
		case 0:
			lead = new String[] { "ecgx" };
			break;
		case 1:
			lead = new String[] { "ecg1", "ecg2", "ecgv" };
			break;
		case 2:
			lead = new String[] { "ai", "es", "as" };
			break;
		default:
			break;
		}
		for (int j = 0; j < len; j++) {
			JSONObject data = new JSONObject();
			for (int i = 0; i < lead.length; i++) {
				int value = byteToInt(new byte[] { bytes[j * 6 + i * 2], bytes[j * 6 + i * 2 + 1] });
				data.put(lead[i], value);
			}
			// 计算其它导
			if (ecgtype == 1 && data.has("ecg1") && data.has("ecg2")) {
				double ecg1 = data.getDouble("ecg1");
				double ecg2 = data.getDouble("ecg2");
				// 1、III导。
				double IIIValue = ecg2 - ecg1;
				data.put("ecg3", IIIValue);
				double vr = -(ecg1 + ecg2) / 2;
				data.put("ecgvr", vr);
				double vl = ecg1 - (ecg2 / 2);
				data.put("ecgvl", vl);
				double vf = ecg2 - (ecg1 / 2);
				data.put("ecgvf", vf);
			}
			jsonObj.add(data);
		}
		return jsonObj;
	}

	/**
	 * 血氧容积波
	 * 
	 * @param bytes
	 * @param jsonObj
	 */
	private static JSONArray PELTHData(byte[] bytes) {
		JSONArray jsonObj = new JSONArray();
		for (int j = 0; j < bytes.length; j++) {
			JSONObject data = new JSONObject();
			int value = byteToInt(new byte[] { bytes[j] });
			data.put("pelth", value);
			jsonObj.add(data);
		}
		return jsonObj;
	}

	/**
	 * 处理阻抗式呼吸数据
	 * 
	 * @param bytes
	 * @param jsonObj
	 */
	private static JSONArray RespData(byte[] bytes) {
		JSONArray jsonObj = new JSONArray();
		int len = bytes.length / 2;
		for (int j = 0; j < len; j++) {
			JSONObject data = new JSONObject();
			data.put("resp", byteToInt(new byte[] { bytes[2 * j + 1], bytes[2 * j] }));
			jsonObj.add(data);
		}
		return jsonObj;
	}
	
	/**
	 * 处理鼻式呼吸波
	 * @param bytes
	 * @param jsonObj
	 */
	private static JSONArray NRespWaveData(byte[] bytes) {
		JSONArray jsonObj = new JSONArray();
		int len = bytes.length / 2;
		for (int j = 0; j < len; j++) {
			JSONObject data = new JSONObject();
			data.put("nresp", byteToInt(new byte[] { bytes[2 * j + 1], bytes[2 * j] }));
			jsonObj.add(data);
		}
		return jsonObj;
	}

	/**
	 * 血压波形
	 * 
	 * @param bytes
	 * @param jsonObj
	 */
	private static JSONArray BPWaveData(byte[] bytes) {
		JSONArray jsonObj = new JSONArray();
		int len = bytes.length / 2;
		String[] lead = new String[] { "pluse", "press" };
		for (int j = 0; j < len;) {
			JSONObject data = new JSONObject();
			for (int i = 0; i < lead.length; i++) {
				data.put(lead[i], byteToInt(new byte[] { bytes[2 * j + i] }));
			}
			jsonObj.add(data);
		}
		return jsonObj;
	}

	private static int byteToInt(byte[] bytes) {
		String temp = "";
		for (byte b : bytes) {
			temp += String.format("%02x", b);
		}
		return Integer.parseInt(temp, 16);
	}

	public static void main(String[] args) {
		readFile("D:\\20190430115223.dat");
	}

}
