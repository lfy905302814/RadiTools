package com.radi.tools;

public class Filter {

	private final static int MWSPT_NSEC = 5;
	private final static float[][] IIR_A = { { 1, 0, 0 }, { 1, -1.997778535f, 0.9977810383f }, { 1, 0, 0 },
			{ 1, 1.142872334f, 0.4128219187f }, { 1, 0, 0 } };
	private final static float[][] IIR_B = { { 0.7988830805f, 0, 0 }, { 1, 0, -1 }, { 0.7988830805f, 0, 0 },
			{ 1, 0, -1 }, { 1, 0, 0 } };
	private final static int IIR_NSEC = MWSPT_NSEC;
	private final static int IIR_ODER = 3;
	private final static int IIR_BUF_LEN = (IIR_ODER * 2 + 1);
	private final static double[] b = { 0.0930632088652284, 0.0, -0.2791896265956852, 0.0, 0.2791896265956852, 0.0,
			-0.0930632088652284 };
	private final static double[] a = { 1.0, -3.5532126053259954, 5.136069365083911, -4.100147431481218, 2.069371532568915,
			-0.6149101943256272, 0.06285391989554877 };

	public static int iir_filter_005_80hz_bp_I(int in) {
		int i;
		float y[][] = new float[IIR_NSEC][3];
		float x[][] = new float[IIR_NSEC + 1][3];
		x[0][0] = in;
		for (i = 0; i < IIR_NSEC; i++) {
			y[i][0] = x[i][0] * IIR_B[i][0] + x[i][1] * IIR_B[i][1] + x[i][2] * IIR_B[i][2] - y[i][1] * IIR_A[i][1]
					- y[i][2] * IIR_A[i][2];
			y[i][0] /= IIR_A[i][0];

			y[i][2] = y[i][1];
			y[i][1] = y[i][0];
			x[i][2] = x[i][1];
			x[i][1] = x[i][0];

			x[i + 1][0] = y[i][0];
		}

		if (x[IIR_NSEC][0] > 32767)
			x[IIR_NSEC][0] = 32767;
		if (x[IIR_NSEC][0] < -32768)
			x[IIR_NSEC][0] = -32768;
		return ((int) x[IIR_NSEC][0]);
	}

	public static int iir_filter_1_40hz_bp_DingZong(int pdata, int datalen) {
		double[] prev_x = new double[IIR_BUF_LEN], prev_y = new double[IIR_BUF_LEN];
		int Nback = IIR_BUF_LEN;
		int outdat = 0;
		for (int i = 0; i < datalen; i++) {/* 对输入数据块进行滤波 */
			// 滤波函数主体
			// moving window
			for (int j = Nback - 1; j > 0; j--) {
				prev_x[j] = prev_x[j - 1];
				prev_y[j] = prev_y[j - 1];
			}
			prev_x[0] = (double) pdata;
			// calculate y based on a and b coefficients
			// and in and out.
			double y = 0;
			for (int j = 0; j < Nback; j++) {
				y += (b[j] * prev_x[j]);
				if (j > 0) {
					y -= (a[j] * prev_y[j]);
				}
			}
			prev_y[0] = y;

			pdata = (int) y;
			outdat = (int) y;

		}
		if (datalen == 1) {
			return outdat/** 12 */
			;
		} else {
			return 0;
		}

	};
	
	/**微分*/
	public static int Derivative(int data, String deviceid, int index){
		int y,i;
		CacheMap cacheMap = CacheMap.getCacheMap();
		String prefix = cacheMap.getPrefix(deviceid, index, "Derivative");
		int[] x_derv = cacheMap.getObject(prefix + "x_derv", int[].class);
		if(x_derv == null) x_derv = new int[4];
		/*y=1/8(2x(nT)+x(nT-T)-x(nT-3T)-2x(nT-4T))*/
		y=(data<<1)+x_derv[3]-x_derv[1]-(x_derv[0]<<1);
		y>>=3;
		for(i=0;i<3;i++)
			x_derv[i]=x_derv[i+1];
		x_derv[3]=data;
		cacheMap.set(prefix + "x_derv", x_derv);
		return(y);
	}

}
