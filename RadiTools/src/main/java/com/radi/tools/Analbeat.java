package com.radi.tools;

import com.radi.entity.BeatInfo;
import com.radi.entity.Dat;
import com.radi.entity.Tempbeat;
import com.radi.entity.WaveInfo;

public class Analbeat {

	private final static int ISO_LENGTH1 = Qrsfilt.MS20;
	private final static int ISO_LIMIT = 5;

	/**
	 * struct _tempbeat *pbeat 从BP滤波器输出信号中截取的波形数据，pbeat->nBegin中
	 * 记录的是波形数据的起始位置，即pbeat->nOrignal[0]在BP滤波器输出信号中的位置信息。
	 * 而pBeatInfo中的所有位置信息都是相对于pbeat->nOrignal[0]的位置，也就是pbeat->nOrignal[] 数组的下标
	 */
	public static void AnalyzeBeatNew(Tempbeat pbeat, BeatInfo pBeatInfo) {
		// 先获得数据起始时间
		Dat[] dat = pbeat.getiDat();
		pBeatInfo.setT0(dat[0].getTime());
		// 首先先获得R波,由R波位置向两边找其他波
		int s_i = 0, e_i = 0, length = 0;
		length = pbeat.getnWidth();
		// TRACE("nPeakIndex = %d\n", pbeat->nPeakIndex);
		int nPeakIndex = pbeat.getnPeakIndex();
		s_i = nPeakIndex - 10;
		if (s_i < 0)
			s_i = 0;
		e_i = nPeakIndex + 10;
		if (e_i >= length)
			e_i = length;

		WaveInfo qrs = new WaveInfo();
		pBeatInfo.setQrs(qrs);
		// 矫正R波位置
		int[] temp1 = GetPeak(pbeat, s_i, e_i, true);
		qrs.setAm_peak(temp1[0]);
		qrs.setI_peak(temp1[1]);
		
		pbeat.setnPeakIndex(qrs.getI_peak());
		// TRACE("nPeakIndex = %d\n", pbeat->nPeakIndex);

		// 获得qrs波起始位置

		// 先获得q点
		s_i = nPeakIndex - 15;
		if (s_i < 0)
			s_i = 0;
		e_i = nPeakIndex;
		int q_i = 0;
		int[] temp2 = GetPeak(pbeat, s_i, e_i, false);
		q_i = temp2[1];

		s_i = q_i;
		e_i = q_i - Qrsfilt.MS80;
		if (e_i < ISO_LENGTH1)
			e_i = ISO_LENGTH1;
		boolean IsoCheckNewValue = IsoCheckNew(dat, ISO_LENGTH1);
		for (int i = s_i; i > e_i; i--) {
			if (IsoCheckNewValue) {
				// 找到qrs起始波位置
				qrs.setI_start(i - ISO_LENGTH1 / 2);
				break;
			} else {
				qrs.setI_start(s_i - Qrsfilt.MS20);
			}
		}
		// 获得基线
		pBeatInfo.setIsolevel(dat[qrs.getI_start()].getValue());

		// 获得qrs波结束位置
		// 先获得s点
		int st_i = 0;
		s_i = nPeakIndex;
		e_i = nPeakIndex + 15;
		if (e_i >= length)
			e_i = length;
		int[] temp3 = GetPeak(pbeat, s_i, e_i, false);
		st_i = temp3[1];

		s_i = st_i;
		e_i = st_i + Qrsfilt.MS80;
		if (e_i >= length - ISO_LENGTH1)
			e_i = length - ISO_LENGTH1;
		for (int i = s_i; i < e_i; i++) {
			if (IsoCheckNewValue) {
				// 找到qrs结束位置，即J点
				qrs.setI_end(i);
				break;
			} else {
				qrs.setI_end(st_i + Qrsfilt.MS40);
			}
		}
		int iq = qrs.getI_end() + Qrsfilt.MS40;
		pBeatInfo.setSt_point(dat[iq].getValue());
		pBeatInfo.setI_J(qrs.getI_end());
		pBeatInfo.setI_st_point(iq);

		// 获得p波
		// 先查找p波波峰
		int p_am = 0, p_i = 0;
		s_i = 0;
		e_i = qrs.getI_start();
		int[] temp4 = GetPeak(pbeat, s_i, e_i, true);
		p_am = temp4[0];
		p_i = temp4[1];
		WaveInfo p = new WaveInfo();
		p.setAm_peak(p_am);
		p.setI_peak(p_i);
		pBeatInfo.setP(p);
		s_i = p_i;
		e_i = p_i - Qrsfilt.MS80;
		if (e_i < ISO_LENGTH1)
			e_i = ISO_LENGTH1;

		for (int i = s_i; i > e_i; i--) {
			if (IsoCheckNewValue) {
				// 找到p起始波位置
				p.setI_start(i - ISO_LENGTH1 / 2);
				break;
			} else {
				p.setI_start(s_i - Qrsfilt.MS20);
			}
		}
		// 保存beat起始位置
		pBeatInfo.setI_start(p.getI_start());
		// 查找p波结束位置
		s_i = p_i;
		e_i = qrs.getI_start();
		for (int i = s_i; i < e_i; i++) {
			if (IsoCheckNewValue) {
				// 找到p结束位置
				p.setI_end(i);
				break;
			} else {
				p.setI_end(e_i - 1);
			}
		}
		// 查找T波波峰
		s_i = qrs.getI_end() + Qrsfilt.MS80;
		e_i = length;
		WaveInfo t = new WaveInfo();
		pBeatInfo.setT(t);
		GetTPeak(pbeat, s_i, e_i, t.getAm_peak(), t.getI_peak());

		// 查找T波起始
		s_i = qrs.getI_end() + Qrsfilt.MS80;
		e_i = t.getI_peak();
		for (int i = s_i; i < e_i; i++) {
			if (IsoCheckNewValue) {
				// 找到t起始，即J点
				t.setI_start(i);
				break;
			} else {
				t.setI_start(s_i + Qrsfilt.MS80);
			}
		}
		// 查找T波结束
		s_i = t.getI_peak();
		e_i = length;
		for (int i = s_i; i < e_i; i++) {
			if (IsoCheckNewValue) {
				// 找到t结束位置
				t.setI_end(i);
				break;
			} else {
				t.setI_end(length - 1);
			}
		}
		// beat end
		pBeatInfo.setI_end(t.getI_end());
		// QT间期
		pBeatInfo.setQtinterval(t.getI_end() - t.getI_start());
		// st
		pBeatInfo.setStlevel(pBeatInfo.getSt_point() - pBeatInfo.getIsolevel());
		int i_start = pBeatInfo.getI_start();
		int i_end = pBeatInfo.getI_end();
		// 保存beat波形数据
		for (int i = i_start, j = 0; i < i_end; i++, j++) {
			int[] wave = new int[AlgorithmTool.BEAT_ORG_LENGTH_MAX];
			wave[j] = dat[i].getValue();
			pBeatInfo.setWave(wave);
		}
		pBeatInfo.setWidth(i_end - i_start);

		// 更新beat起始时间
		pBeatInfo.setT0(pBeatInfo.getT0() + i_start * 5);
		// 计算各个特征相对i_start的位置
		pBeatInfo.setI_end(i_end - i_start);

		p.setI_start(p.getI_start() - i_start);
		p.setI_peak(p.getI_peak() - i_start);
		p.setI_end(p.getI_end() - i_start);

		qrs.setI_start(qrs.getI_start() - i_start);
		qrs.setI_peak(qrs.getI_peak() - i_start);
		qrs.setI_end(qrs.getI_end() - i_start);

		t.setI_start(t.getI_start() - i_start);
		t.setI_peak(t.getI_peak() - i_start);
		t.setI_end(t.getI_end() - i_start);

		pBeatInfo.setSt_point(pBeatInfo.getSt_point() - i_start);
		pBeatInfo.setI_J(pBeatInfo.getI_J() - i_start);
		
		pBeatInfo.setI_start(0);
	}

	private static int[] GetPeak(Tempbeat pdata, int start, int end, boolean flag) {
		int[] tempInt = new int[2];
		int temp = 0;
		int n = 0, i = 0;
		Dat[] iDat = pdata.getiDat();
		if (flag) {
			// 获得波峰
			temp = -32768;
			n = start;
			for (i = start; i < end; i++) {
				if (temp < (iDat[i].getValue())) {
					temp = iDat[i].getValue();
					n = i;
				}
			}
			tempInt[0] = temp;
			tempInt[1] = n;

		} else {
			// 获得波谷
			temp = 32768;
			n = start;
			for (i = start; i < end; i++) {
				if (temp > (iDat[i].getValue())) {
					temp = iDat[i].getValue();
					n = i;
				}
			}
			tempInt[0] = temp;
			tempInt[1] = n;
		}
		return tempInt;
	}
	
	private static boolean IsoCheckNew(Dat[] pdat, int isoLength) {
		int i, max, min;
		// 通过查找isolength长度内的极值之差，定位isoelectric
		for (i = 1, max = min = pdat[0].getValue(); i < isoLength; i++) {
			if (pdat[i].getValue() > max)
				max = pdat[i].getValue();
			if (pdat[i].getValue() < min)
				min = pdat[i].getValue();
		}
		if (max - min < ISO_LIMIT)// <20
			return true;
		return false;
	}

	private static void GetTPeak(Tempbeat pdata, int start, int end, int am, int i_am) {
		int temp = 0;
		int n = 0;

		// 获得波峰
		temp = -32768;
		n = start;
		for (int i = start; i < end; i++) {
			if (temp < Math.abs((pdata.getiDat())[i].getValue())) {
				temp = (pdata.getiDat())[i].getValue();
				n = i;
			}
		}
		am = (pdata.getiDat())[n].getValue();
		i_am = n;
	}

}
