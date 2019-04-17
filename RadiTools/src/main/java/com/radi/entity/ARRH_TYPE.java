package com.radi.entity;

public enum ARRH_TYPE {
	
	/**正常*/
	NORMAL("正常"),                                      
	/**室早*/
	PVC("室早"),                                           
	/**室速*/ 
	VT("室速"),                                              
	/**停博*/
	CA("停博"),                                             
	/**心动过缓*/
	BC("心动过缓"),   
	/**心动停止,即超过10秒无心跳*/
	CARDIAC_ARREST("心动停止"),									
	/*下面是预留*/
	/**室上速*/
	SVT("室上速"),                                            
	 /**阵发性室上速*/
	PSVT("阵发性室上速"),                                           
	/**室扑或室颤*/  
	VF("室扑或室颤"),                                              
	/**房早*/
	APB("房早"),                                             
	/**房颤或房扑*/
	AF("房颤或房扑"),
	UNKNOWN("");
	
	private String name;
	
	private ARRH_TYPE(String name){
		this.name=name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static void main(String[] args) {
		System.out.println(ARRH_TYPE.values()[0]);
		System.out.println(ARRH_TYPE.APB.ordinal());
		System.out.println(ARRH_TYPE.NORMAL.getName());
		System.out.println(ARRH_TYPE.PVC);
	}

}
