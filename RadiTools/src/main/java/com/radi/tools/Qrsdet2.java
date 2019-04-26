package com.radi.tools;

public class Qrsdet2 {

	private final static int MIN_PEAK_AMP = 7;
	private final static double TH = 0.625;

	static int QRSDet(int datum, boolean init, String deviceid, int index) {
		CacheMap cacheMap = CacheMap.getCacheMap();
		String prefix = cacheMap.getPrefix(deviceid, index, "QRSDet");
		int det_thresh = cacheMap.getInteger(prefix + "det_thresh");
		int qpkcnt = cacheMap.getInteger(prefix + "qpkcnt");
		int rsetCount = cacheMap.getInteger(prefix + "rsetCount");
		int nmean = cacheMap.getInteger(prefix + "nmean");
		int qmean = cacheMap.getInteger(prefix + "qmean");
		int rrmean = cacheMap.getInteger(prefix + "rrmean");
		int count = cacheMap.getInteger(prefix + "count");
		int sbpeak = cacheMap.getInteger(prefix + "sbpeak");
		int sbloc = cacheMap.getInteger(prefix + "sbloc");
		int sbcount = cacheMap.getInteger(prefix + "sbcount");
		if(sbcount == 0) sbcount = Qrsfilt.MS1500;
		int initBlank = cacheMap.getInteger(prefix + "initBlank");
		int initMax = cacheMap.getInteger(prefix + "initMax");
		int preBlankCnt = cacheMap.getInteger(prefix + "preBlankCnt");
		int tempPeak = cacheMap.getInteger(prefix + "tempPeak");
		int[] qrsbuf = cacheMap.getObject(prefix + "qrsbuf", int[].class);
		if(qrsbuf==null) qrsbuf = new int[8];
		int[] noise = cacheMap.getObject(prefix + "noise", int[].class);
		if(noise==null) noise = new int[8];
		int[] rrbuf = cacheMap.getObject(prefix + "rrbuf", int[].class);
		if(rrbuf==null){
			rrbuf = new int[8];
			for (int i = 0; i < 8; i++) {
				rrbuf[i] = Qrsfilt.MS1000;
			}
		}
		int[] rsetBuff = cacheMap.getObject(prefix + "rsetBuff", int[].class);
		if(rsetBuff==null) rsetBuff = new int[8];
		
		int fdatum, QrsDelay = 0;
		int i, newPeak, aPeak;

		fdatum = datum;
		/*
		 * Wait until normal detector is ready before calling early detections.
		 */
		aPeak = Peak(fdatum, deviceid ,index);// 返回0时无峰值出现
		if (aPeak < MIN_PEAK_AMP)
			aPeak = 0;
		// Hold any peak that is detected for 200 ms
		// in case a bigger one comes along. There
		// can only be one QRS complex in any 200 ms window.
		newPeak = 0;
		if (aPeak != 0 && preBlankCnt == 0) {// If there has been no peak for 200
			// save this one and start counting.
			// 出现峰值，设置不应期计时，将此峰值加入考察
			tempPeak = aPeak;
			preBlankCnt = Qrsfilt.PRE_BLANK; // MS200
		} else if (aPeak == 0 && preBlankCnt != 0){ // If we have held onto a
			// 200 ms pass it on for evaluation.
			// 在不应期内，且没有其他峰值
			if (--preBlankCnt == 0) {
				// 不应期内没有峰值出现，考察峰值为新峰值
				newPeak = tempPeak;
				//System.out.println("=========get a new peak"+newPeak);
			}
		}else if (aPeak != 0){ // If we were holding a peak, but
			// this ones bigger, save it and
			// 在不应期内有峰值出现
			if (aPeak > tempPeak){ // start counting to 200 ms again.
				// 选择较大的峰值，并重新复位不应期计时
				tempPeak = aPeak;
				preBlankCnt = Qrsfilt.PRE_BLANK; // MS200
			} else if (--preBlankCnt == 0){
				// 考察峰值即为最大峰值
				newPeak = tempPeak;
				//System.out.println("=========get a new peak"+newPeak);
			}
		}
		/*
		 * Save derivative of raw signal for T-wave and baseline shift
		 * discrimination.
		 */
		// 保存第n和第n-2点之间的差值
		// DDBuffer[DDPtr] = Xn - Xn-2;
		/*DDBuffer[DDPtr] = Qrsfilt.deriv1(datum,deviceid, index);
		if (++DDPtr == Qrsfilt.DER_DELAY)
			DDPtr = 0;*/
		/* Initialize the qrs peak buffer with the first eight */
		/* local maximum peaks detected. */
		if (qpkcnt < 8) {
			// 前8秒内
			++count;
			if (newPeak > 0)
				count = Qrsfilt.WINDOW_WIDTH;
			if (++initBlank == Qrsfilt.MS1000) {
				// 每1000ms执行一次，即200个采样点
				initBlank = 0;
				qrsbuf[qpkcnt] = initMax;// 保存每秒内的最大峰值
				initMax = 0;
				++qpkcnt;
				if (qpkcnt == 8) {
					// 8秒
					qmean = mean(qrsbuf, 8);// 求每8秒内的峰值平均值
					nmean = 0;
					rrmean = Qrsfilt.MS1000;
					sbcount = Qrsfilt.MS1500 + Qrsfilt.MS150;
					det_thresh = thresh(qmean, nmean);// 获得栅值
				}
			}
			if (newPeak > initMax)
				// 获得最大峰值
				initMax = newPeak;
		}else{ /* Else test for a qrs. */
			++count; // 心电波形数据计数器，每个采样点+1
			// 新峰值
			if (newPeak > 0) {
				/*
				 * Check for maximum derivative and matching minima and maxima
				 * for T-wave and baseline shift rejection. Only consider this
				 * peak if it doesn't seem to be a base line shift.
				 */
				// 查看基线漂移，无基线漂移时，峰值有效
				//if (!BLSCheck(DDBuffer, DDPtr, maxder)) {
					// 无基线漂移
					// Classify（分类） the beat as a QRS complex（QRS波群）
					// if the peak is larger than the detection threshold.
					if (newPeak > det_thresh) {
						// memmove(qrsbuf[1], qrsbuf, MEMMOVELEN) ;
						// //滑动窗口，向后平移一个峰值
						System.arraycopy(qrsbuf, 0, qrsbuf, 1, qrsbuf.length - 1);
						qrsbuf[0] = newPeak;
						qmean = mean(qrsbuf, 8); // 平均峰值
						det_thresh = thresh(qmean, nmean); // 栅值
						// memmove(rrbuf[1], rrbuf, MEMMOVELEN) ; //
						System.arraycopy(rrbuf, 0, rrbuf, 1, rrbuf.length - 1);
						rrbuf[0] = count - Qrsfilt.WINDOW_WIDTH; // 保存R-R间期
						rrmean = mean(rrbuf, 8); // 平均R-R间期
						sbcount = rrmean + (rrmean >> 1) + Qrsfilt.WINDOW_WIDTH;// 进行回溯的时间界限
						count = Qrsfilt.WINDOW_WIDTH; // 复位计数器

						sbpeak = 0;
						QrsDelay = Qrsfilt.WINDOW_WIDTH + Qrsfilt.FILTER_DELAY;
						initBlank = initMax = rsetCount = 0;
						System.out.println("@@@@@@@@@@@@@get a real new peak"+newPeak);
					}else {// 出现基线漂移，认为无效QRS波，更新噪声
							// memmove(noise[1],noise,MEMMOVELEN) ;
						System.out.println("+++++++++++++++baseline shift rejestion"+newPeak+","+det_thresh);
						System.arraycopy(noise, 0, noise, 1, noise.length - 1);
						noise[0] = newPeak;
						nmean = mean(noise, 8);
						det_thresh = thresh(qmean, nmean);// 更新阀值
						if ((newPeak > sbpeak) && ((count - Qrsfilt.WINDOW_WIDTH) >= Qrsfilt.MS360)) {// 在距离上个有效QRS波360ms内，一直没有有效QRS波，则将回溯峰值更新为在此期间出现的最大newPeak
							sbpeak = newPeak;// 保存回溯峰值
							sbloc = count - Qrsfilt.WINDOW_WIDTH;// 保存峰值点
						}
					}
				}
			//}

			/* Test for search back condition. If a QRS is found in */
			/* search back update the QRS buffer and det_thresh. */

			if ((count > sbcount) && (sbpeak > (det_thresh >> 1))) {// 距离上个有效QRS波sbcount时间内，没有新的有效QRS波，且sbpeak会大于阀值的一半，则进行回溯
																	// sbpeak作为有效QRS波
																	// memmove(qrsbuf[1],qrsbuf,MEMMOVELEN)
																	// ;
				System.arraycopy(qrsbuf, 0, qrsbuf, 1, qrsbuf.length - 1);
				qrsbuf[0] = sbpeak;
				qmean = mean(qrsbuf, 8);
				det_thresh = thresh(qmean, nmean);
				// memmove(rrbuf[1],rrbuf,MEMMOVELEN) ;
				System.arraycopy(rrbuf, 0, rrbuf, 1, rrbuf.length - 1);
				rrbuf[0] = sbloc;
				rrmean = mean(rrbuf, 8);
				sbcount = rrmean + (rrmean >> 1) + Qrsfilt.WINDOW_WIDTH;
				QrsDelay = count = count - sbloc;
				QrsDelay += Qrsfilt.FILTER_DELAY;

				sbpeak = 0;
				// LocationThePoint(PRE_BLANK+MS95, 0, RGB(255,0,0),sbpeak,
				// det_thresh, 0x7FFFFFFF,0x7FFFFFFF);
				initBlank = initMax = rsetCount = 0;
			}
		}

		// In the background estimate threshold to replace adaptive threshold
		// if eight seconds elapses without a QRS detection.

		if (qpkcnt == 8) {
			if (++initBlank == Qrsfilt.MS1000) {
				initBlank = 0;
				rsetBuff[rsetCount] = initMax;
				initMax = 0;
				++rsetCount;

				// Reset threshold if it has been 8 seconds without
				// a detection.

				if (rsetCount == 8) {// 连续8秒无任何有效峰值，重新更新阀值
					for (i = 0; i < 8; ++i) {
						qrsbuf[i] = rsetBuff[i];
						noise[i] = 0;
					}
					qmean = mean(rsetBuff, 8);
					nmean = 0;
					rrmean = Qrsfilt.MS1000;
					sbcount = Qrsfilt.MS1500 + Qrsfilt.MS150;
					det_thresh = thresh(qmean, nmean);
					initBlank = initMax = rsetCount = 0;
				}
			}
			if (newPeak > initMax)
				initMax = newPeak;
		}
		
		cacheMap.set(prefix + "det_thresh", det_thresh);
		cacheMap.set(prefix + "qpkcnt", qpkcnt);
		cacheMap.set(prefix + "rsetCount", rsetCount);
		cacheMap.set(prefix + "nmean", nmean);
		cacheMap.set(prefix + "qmean", qmean);
		cacheMap.set(prefix + "rrmean", rrmean);
		cacheMap.set(prefix + "count", count);
		cacheMap.set(prefix + "sbpeak", sbpeak);
		cacheMap.set(prefix + "sbloc", sbloc);
		cacheMap.set(prefix + "sbcount", sbcount);
		cacheMap.set(prefix + "initBlank", initBlank);
		cacheMap.set(prefix + "initMax", initMax);
		cacheMap.set(prefix + "preBlankCnt", preBlankCnt);
		cacheMap.set(prefix + "tempPeak", tempPeak);
		cacheMap.set(prefix + "qrsbuf", qrsbuf);
		cacheMap.set(prefix + "noise", noise);
		cacheMap.set(prefix + "rrbuf", rrbuf);
		cacheMap.set(prefix + "rsetBuff", rsetBuff);
		return (QrsDelay);
	}

	private static int Peak(int datum, String deviceid, int index) {
		CacheMap cacheMap = CacheMap.getCacheMap();
		String prefix = cacheMap.getPrefix(deviceid, index, "Peak");
		int max = cacheMap.getInteger(prefix + "max");
		int timeSinceMax = cacheMap.getInteger(prefix + "timeSinceMax");
		int lastDatum = cacheMap.getInteger(prefix + "lastDatum");
		int pk = 0;
		if (timeSinceMax > 0)
			++timeSinceMax;
		if ((datum > lastDatum) && (datum > max)) {
			// 上升沿不停进入此处，定位峰值位置，起始下降沿持续时间计时
			max = datum;
			if (max > 2)
				timeSinceMax = 1;
		}else if (timeSinceMax > Qrsfilt.MS95) {
			// max连续MS95时间最大，确认峰值
			pk = max;
			max = 0;
			timeSinceMax = 0;
			//System.out.println("========================max="+max+","+pk);
		}
		//System.out.println("========================max="+max);
		lastDatum = datum;
		cacheMap.set(prefix + "max", max);
		cacheMap.set(prefix + "timeSinceMax", timeSinceMax);
		cacheMap.set(prefix + "lastDatum", lastDatum);
		return (pk);
	}

	private static int mean(int[] array, int datnum) {
		long sum;
		int i;
		for (i = 0, sum = 0; i < datnum; ++i){
			sum += array[i];
		}
		sum /= datnum;
		return (int) sum;
	}
	
	/****************************************************************************
	 * thresh() calculates the detection threshold from the qrs mean and noise
	 * mean estimates.
	 ****************************************************************************/
	private static int thresh(int qmean, int nmean) {
		int thrsh, dmed;
		double temp;
		dmed = qmean - nmean;
		/* thrsh = nmean + (dmed>>2) + (dmed>>3) + (dmed>>4); */
		temp = dmed;
		temp *= TH;
		dmed = (int) temp;
		thrsh = nmean + dmed; /* dmed * THRESHOLD */
		/* thrsh = nmean + (dmed>>1) + (dmed>>2) + (dmed>>3); */
		/* thrsh = nmean + (dmed>>1) / *+ (dmed>>2)* / + (dmed>>3); */
		return (thrsh);
	}

	/***********************************************************************
	 * BLSCheck() reviews data to see if a baseline shift（基线漂移） has occurred.
	 * This is done by looking for both positive and negative slopes（正负斜率） of
	 * roughly（粗略地） the same magnitude（量级） in a 220 ms window.
	 ***********************************************************************/

	/*private static boolean BLSCheck(int[] dBuf, int dbPtr, int maxder) {
		int max, min, maxt = 0, mint = 0, t, x;
		max = min = 0;
		// 搜索220ms内的极值
		for (t = 0; t < Qrsfilt.MS220; ++t) {
			x = dBuf[dbPtr];
			if (x > max) {
				maxt = t;
				max = x;// 最大值
			} else if (x < min) {
				mint = t;
				min = x;// 最小值
			}
			if (++dbPtr == Qrsfilt.DER_DELAY)
				dbPtr = 0;
		}

		maxder = max;
		min = -min;

		 //Possible beat if a maximum and minimum pair are found where the
		 //interval between them is less than 150 ms.
		 

		if ((max > (min >> 3)) && (min > (max >> 3)) && (Math.abs(maxt - mint) < Qrsfilt.MS150))
			return false;

		else
			// SetFlagLine(0,PRE_BLANK+MS95,4,RGB(255,255,255));
			return false;
	}*/

}
