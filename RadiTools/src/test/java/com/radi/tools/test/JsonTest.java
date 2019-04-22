package com.radi.tools.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.radi.tools.AlgorithmTool;

public class JsonTest{
	
	public static void reader(String filePath) {
		BufferedReader bufferedReader = null;
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
				bufferedReader = new BufferedReader(read);
				while (true) {
						String str = bufferedReader.readLine();
						if(str!=null){
							AlgorithmTool.handleWave(str);
							/*BeatInfo[] beatInfo = AlgorithmTool.handleWave(str);
							System.out.println("======================================="+beatInfo);*/
						}else{
							break;
						}
				}
			}
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			System.out.println("Cannot find the file specified!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error reading file content!");
			e.printStackTrace();
		}finally{
			if(bufferedReader != null){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		//JedisUtils.flushDB();
		reader("D:\\jsonnew.txt");
	}

}
