package com.radi.tools.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.radi.entity.InputWave;
import com.radi.tools.AlgorithmTool;

public class Test3{
	
	public static void main(String[] args) {
		//File file = new File("D:\\20180405.hst");
		//File file = new File("D:\\20180408.hst");
		//File file = new File("D:\\20180409.hst");
		File file = new File("D:\\20180410.hst");
		FileInputStream in1 = null;
		DataInputStream data_in = null;
		try {
			in1 = new FileInputStream(file);
			data_in = new DataInputStream(in1);
			int len = 72004;
			int num = 200*60;
			int k = 6;
			byte[] itemBuf = new byte[num*k+4];
			int[] data = new int[num];
			long time = System.currentTimeMillis();
			while(true){
				data_in.read(itemBuf, 0, len);
				for (int i = 0; i < num; i++) {
					Integer ecg2 = Integer.parseInt(String.format("%02x",itemBuf[i*6+5])+String.format("%02x",itemBuf[i*6+4]),16);
					data[i] = ecg2;
				}
				int datLen = 1;
				int[] temp = new int[datLen];
				int tempIndex = 0;
				for (int i = 0; i < data.length; i++) {
					temp[tempIndex++] = data[i];
					if(tempIndex % datLen == 0 || i == data.length-1){
						InputWave pEcgSample = new InputWave();
						pEcgSample.setT0(time);
						pEcgSample.setDat(temp);
						pEcgSample.setLen(tempIndex);
						AlgorithmTool.beatAnalysis(pEcgSample, "005", 1);
						tempIndex = 0;
						time += 5;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				data_in.close();
				in1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
