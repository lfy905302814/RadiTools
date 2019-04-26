package com.radi.tools;

public class Filter_1_40hz {

	private final static int MWSPT_NSEC = 11;
	private final static float[][] IIR_A = { { 1, 0, 0 }, { 1, -0.487428695f, 0.5598788261f }, { 1, 0, 0 },
			{ 1, -1.980457425f, 0.9814426899f }, { 1, 0, 0 }, { 1, -1.948840022f, 0.9498579502f }, { 1, 0, 0 },
			{ 1, -0.3820093572f, 0.1427547783f }, { 1, 0, 0 }, { 1, -1.148019671f, 0.1745279431f }, { 1, 0, 0 } };
	private final static float[][] IIR_B = { { 0.5061171651f, 0, 0 }, { 1, 0, -1 }, { 0.5061171651f, 0, 0 }, { 1, 0, -1 },
			{ 0.4332802296f, 0, 0 }, { 1, 0, -1 }, { 0.4332802296f, 0, 0 }, { 1, 0, -1 }, { 0.4127360284f, 0, 0 },
			{ 1, 0, -1 }, { 1, 0, 0 } };
	private final static int IIR_NSEC = MWSPT_NSEC;

	static int iir_filter_1_40hz_bp_I(int in) {
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
