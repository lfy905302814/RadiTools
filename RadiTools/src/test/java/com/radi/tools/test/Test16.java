package com.radi.tools.test;

public class Test16 {

	public static void main(String[] args) {
		//定义一个十进制值 
		int valueTen = 328; 
		//将其转换为十六进制并输出 
		String strHex = Integer.toHexString(valueTen); 
		System.out.println(valueTen + "[十进制]---->[十六进制] " + strHex); 
		//将十六进制格式化输出 
		String strHex2 =String.format("%08x",valueTen);
		System.out.println(valueTen + "[十进制]---->[十六进制] " + strHex2);
		System.out.println("==========================================================");
		//定义一个十六进制值 
		String strHex3 = "00001322"; 
		//将十六进制转化成十进制 
		int valueTen2 = Integer.parseInt(strHex3,16); 
		System.out.println(strHex3 + "[十六进制]---->[十进制] " + valueTen2);
		// LOGGER.info("==========================================================");
		//可以在声明十进制时，自动完成十六进制到十进制的转换 
		int valueHex = 0x00001322;
		System.out.println("int valueHex = 0x00001322 --> " + valueHex);

	}

}
