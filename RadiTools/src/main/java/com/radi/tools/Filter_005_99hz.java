package com.radi.tools;

public class Filter_005_99hz {

	private final static int MWSPT_NSEC = 11;
	private final static float[][] IIR_A = { { 1, 0, 0 }, { 1, -1.999027252f, 0.999029696f }, { 1, 0, 0 },
			{ 1, 1.979796648f, 0.9807741046f }, { 1, 0, 0 }, { 1, -1.997459173f, 0.997461617f }, { 1, 0, 0 },
			{ 1, 1.949473023f, 0.9504354596f }, { 1, 0, 0 }, { 1, -0.02936373092f, -0.9675456285f }, { 1, 0, 0 } };
	private final static float[][] IIR_B = { { 0.9948074818f, 0, 0 }, { 1, 0, -1 }, { 0.9948074818f, 0, 0 }, { 1, 0, -1 },
			{ 0.9867859483f, 0, 0 }, { 1, 0, -1 }, { 0.9867859483f, 0, 0 }, { 1, 0, -1 }, { 0.9837728143f, 0, 0 },
			{ 1, 0, -1 }, { 1, 0, 0 } };
	private final static int IIR_NSEC = MWSPT_NSEC;

	public static int iir_filter_005_99hz_bp_I(int in) {
		int i;
		float[][] y = new float[IIR_NSEC][3];
		float[][] x = new float[IIR_NSEC + 1][3];
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

}
