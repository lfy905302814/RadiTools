package com.radi.tools;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.radi.entity.ARRH_TYPE;
import com.radi.entity.ArrhythmiaInfo;
import com.radi.entity.BeatInfo;
import com.radi.entity.CHANNEL;
import com.radi.entity.DAT_TYPE;
import com.radi.entity.Dat;
import com.radi.entity.Ecg;
import com.radi.entity.Ecgwave;
import com.radi.entity.FILTER_TYPE;
import com.radi.entity.InputWave;
import com.radi.entity.Parameters;
import com.radi.entity.Tempbeat;
import com.radi.entity.Wave;

public class AlgorithmTool {

	public final static int MS2000 = 400;
	public final static int MS2400 = 480;
	public final static int WAVE_LEN = MS2400 * 6;
	public final static int CHN_SUM = CHANNEL.Vx.ordinal() + 1;
	public final static int CHN_SUM_NEW = 7;
	public final static int BEAT_QUE_LENGTH = 10;
	public final static int BEAT_SAMPLE_RATE = 100;
	public final static double BEAT_MS_PER_SAMPLE = 1000.00 / BEAT_SAMPLE_RATE;
	public final static int BEAT_MS400 = (int) (400 / BEAT_MS_PER_SAMPLE + 0.5);
	public final static int BEAT_MS1000 = BEAT_SAMPLE_RATE;
	public final static int BEATLGTH = BEAT_MS1000;
	public final static int FIDMARK = BEAT_MS400;
	public final static int BEAT_DELAY_COUNTER = (BEATLGTH - FIDMARK) * (Qrsfilt.SAMPLE_RATE / BEAT_SAMPLE_RATE);
	public final static int BEAT_ORG_LENGTH_MAX = 600;
	public final static int BEAT_SUM = 12;
	public final static int DIF = Qrsfilt.PRE_BLANK + Qrsfilt.MS95 + BEAT_DELAY_COUNTER
			- (Qrsfilt.WINDOW_WIDTH + Qrsfilt.FILTER_DELAY) + Qrsfilt.WINDOW_WIDTH
			- 3; /* 如果有-3则是Bandpass中的信号，没有则是移动积分后的信号 */
	private static FILTER_TYPE enumFilterType = FILTER_TYPE.OPRATION;
	
	/*
	 * struct _beat_info { LONG64 t0; int i_start; 在BP滤波器输出缓冲区的位置 int i_end;
	 * 相对i_start的位置 int width; struct _wave_info p; struct _wave_info qrs;
	 * struct _wave_info t;
	 * 
	 * int i_J; J点位置 int i_st_point; st点位置 int isolevel; 基线电平 int st_point;
	 * st点电平 int qtinterval; qt间期 int stlevel; st诊断值 >0表示抬高多少，<0表示降低多少 stlevel =
	 * st_point - isolevel. 单位：(mv) int wave[500];
	 * 
	 * int RealHeartRate; 实时心率 int MeanHeartRate; 平均心率，6点平均心率
	 * 
	 * struct _arrhythmia_info Arrhythmia; 心律失常信息 };
	 */

	private static int GET_BACK_INDEX(int a, int b) {
		a = a - b;
		if (a < 0)
			a += WAVE_LEN;
		return a;
	}

	private static boolean StoreBeatCharacte_roughly(Tempbeat tempBeat, Wave bandPassSignal, int rr) {
		int mid_x = 0, start_i = 0;
		/*
		 * 对于移动积分窗口的输出信号，当获得一个新R波时，R波已经推迟了PRE_BLANK+MS95，此时没有立即进行RR间期计算，
		 * 还需再等待（BEAT_DELAY_COUNTER-qrsdelay）= （BEAT_DELAY_COUNTER -
		 * (WINDOW_WIDTH + FILTER_DELAY)）延时 再进入RR间期计算。 所以在RR间期计算后去寻找新R波的位置时，需往回推
		 * PRE_BLANK+MS95 + （BEAT_DELAY_COUNTER - (WINDOW_WIDTH + FILTER_DELAY)）
		 * 的长度。
		 */
		if ((rr / 2 + DIF) > (BEAT_ORG_LENGTH_MAX))
			return true;

		mid_x = bandPassSignal.getiWritePoint();
		mid_x = GET_BACK_INDEX(mid_x, DIF); /* 获得Bandpass输出信号中的新R波位置 */

		start_i = mid_x;
		start_i = GET_BACK_INDEX(start_i, (rr / 2)); /* 获得起始位置 */

		tempBeat.setnBegin(start_i);
		tempBeat.setnWidth(rr/2+DIF); /* 获得宽度 */

		for (int j = 0; j < tempBeat.getnWidth(); j++) { /* 保存BP滤波后的新beat数据 */
			if (start_i == mid_x) {
				tempBeat.setnPeakIndex(j);
			}
			Dat[] iDat = bandPassSignal.getiDat();
			Dat[] iData = tempBeat.getiDat();
			iData[j].setValue(iDat[start_i].getValue());
			iData[j].setTime(iDat[start_i++].getTime());
			if (start_i >= WAVE_LEN)
				start_i = 0;
		}
		return false;
	}

	/**
	 * @param len:输入InputWave对象有效数据长度len
	 * @param pEcg
	 * @param index:波形序列CHANNEL(I(0),II(1),Vx(2));
	 * @return
	 */
	private static int beatDetection(Wave heartRateSignal, int len,Ecg pEcg, String deviceid, int index) {
		int sDat = 0, sWp = 0, rr = 0;
		int qrsdelay = 0;
		int QRSIndex = 0;
		Dat[] iDat = heartRateSignal.getiDat();
		int iWritePoint = heartRateSignal.getiWritePoint();
		sWp = iWritePoint - len;
		//System.out.println("iWritePoint="+iWritePoint);
		if (sWp < 0)
			sWp += WAVE_LEN;
		//for (int i = 0; i < len; i++) {
			
			sDat = iDat[sWp++].getValue();
			sWp = (sWp >= WAVE_LEN) ? 0 : sWp;
			//System.out.println("QRSDet input ="+sDat);
			/* increment RRInterval count */
			int rRCount = pEcg.getrRCount() + 1;
			pEcg.setrRCount(rRCount);
			int[] que = pEcg.getBeat().getQue();
			int queCount = pEcg.getBeat().getQueCount();
			for (int j = 0; j < queCount; j++) {
				que[j]++;
			}
			if (sDat == 100000){
				return 0;
			}
			/* Run the sample through the QRS detector */
			qrsdelay = Qrsdet2.QRSDet(sDat, false, deviceid, index); /* finding R wave delay */
			if (qrsdelay != 0) {
				que[queCount++] = qrsdelay;
				pEcg.getBeat().setQueCount(queCount);
			}
			/* Return if no beat is ready for classification */
			if (que[0] < BEAT_DELAY_COUNTER || pEcg.getBeat().getQueCount() == 0) {
				return 0;
			}
			/*
			 * otherwise classify the beat at the head of the que /*calcute the
			 * delay in pSoure buffer
			 */
			QRSIndex = sWp - Qrsfilt.MS200 - Qrsfilt.MS95/* qrsdelay */;
			if (QRSIndex < 0)
				QRSIndex += WAVE_LEN;

			/*
			 * calculate the R-to-R interval, unfortunately it's not accurately,
			 * has little error
			 */
			rr = rRCount - que[0];
			qrsdelay = rRCount = que[0];
			pEcg.setrRCount(rRCount);
			System.out.println("=====================HR = " + (12000/rr));
			
			int queCountTemp = pEcg.getBeat().getQueCount() - 1;
			for (int j = 0; j < queCountTemp; j++) {
				que[j] = que[j + 1];
			}
			pEcg.getBeat().setQueCount(queCountTemp);
		//}
		return rr;
	}
	
	private static long TIME_POINT(long t0, int index){
		return t0+5*index;
	}
	
	/**
	 * 心率异常返回1，正常返回0
	 * @param tempBeat
	 * @param beatInfo
	 * @return
	 */
	private static int BeatCharacteDetec(BeatInfo BeatInfoTemp, BeatInfo[] pBeatInfo ,Ecg ecg, String deviceid, int index) {
		int meanIndex = ecg.getMeanIndex();
		if (meanIndex<BEAT_SUM){
			pBeatInfo[meanIndex] = BeatInfoTemp;
			ecg.setMeanIndex(++meanIndex);
			if (meanIndex>=2 && index==1){
				BeatInfo pbit0 = new BeatInfo();
				BeatInfo pbit1 = new BeatInfo();
				pbit0 = pBeatInfo[meanIndex-1];
				pbit1 = pBeatInfo[meanIndex-2];
				int hr = (int) (TIME_POINT(pbit0.getT0(), pbit0.getQrs().getI_peak()) - TIME_POINT(pbit1.getT0(), pbit1.getQrs().getI_peak()));/*获得时间差*/
				hr = hr/5;	/*获得采样点差值*/
				hr = 12000/hr;/*获得实时心率*/
				pbit0.setRealHeartRate(hr);
				pbit0.setMeanHeartRate(hr);
			}
		}else{
			for (int i=0; i<BEAT_SUM-1; i++){
				pBeatInfo[i] = pBeatInfo[i+1];
			}
			pBeatInfo[BEAT_SUM-1] = BeatInfoTemp;
			if(index == 1){
				BeatInfo pbit0 = new BeatInfo();
				BeatInfo pbit1 = new BeatInfo();
				pbit0 = pBeatInfo[meanIndex-1];
				pbit1 = pBeatInfo[meanIndex-2];
				int hr = (int) (TIME_POINT(pbit0.getT0(), pbit0.getQrs().getI_peak()) - TIME_POINT(pbit1.getT0(), pbit1.getQrs().getI_peak()));/*获得时间差*/
				hr = hr/5;	/*获得采样点差值*/
				hr = 12000/hr;/*获得实时心率*/
				pbit0.setRealHeartRate(hr);

				/*计算平均心率*/
				hr = 0;
				for(int i=0; i<meanIndex; i++){
					hr += pBeatInfo[i].getRealHeartRate();
				}
				pbit0.setMeanHeartRate(hr/meanIndex);
				System.out.println("real hr = "+pbit0.getRealHeartRate());
				System.out.println("mean hr = "+pbit0.getMeanHeartRate());
				System.out.println("st      = "+pbit0.getStlevel()*2500/4095.00/250.00);
				System.out.println("qt hr   = "+pbit0.getQtinterval()*5);
				/*心律失常分析*/
				ARRH_TYPE rhythmClass = RhythmchkNew.RhythmChk_New(pBeatInfo, deviceid ,index);			// Check the rhythm.分析PVC
				ArrhythmiaInfo pArrhythmia =  new ArrhythmiaInfo();
				if(rhythmClass != ARRH_TYPE.NORMAL){
					//TRACE("***************RhythmCh() return is %d***********  PVC  \n", rhythmClass); 
					pArrhythmia.setArrhythmiaType(rhythmClass);
					pArrhythmia.setnStartTime(pBeatInfo[BEAT_SUM-1].getT0());
					pArrhythmia.setnEndTime(pBeatInfo[BEAT_SUM-1].getT0() + pBeatInfo[BEAT_SUM-1].getWidth()*5);
					BeatInfoTemp.setArrhythmia(pArrhythmia);
					return 1;
				}else{
					pArrhythmia.setArrhythmiaType(ARRH_TYPE.NORMAL);
					BeatInfoTemp.setArrhythmia(pArrhythmia);
				}
			}
		}
		return 0;
	}

	/**
	 * @param index:波形序列CHANNEL(I(0),II(1),Vx(2));
	 * @param len:输入InputWave对象有效数据长度len
	 * @param pSout
	 */
	private static void ecgSampleFiltering(Wave orignalSignal, Wave bandPassSignal, Wave heartRateSignal, int len,int[] pSout, String deviceid, int index) {
		int sdat = 0;
		int sWp = 0;
		long timeTemp = 0;
		sWp = orignalSignal.getiWritePoint() - len;
		if (sWp < 0)
			sWp += WAVE_LEN;
		//for (int i = 0; i < len; i++) {
			/* 读取时间 */
			timeTemp = (orignalSignal.getiDat())[sWp].getTime();
			/* 读取波形 */
			sdat = (orignalSignal.getiDat())[sWp++].getValue();
			sWp = (sWp >= WAVE_LEN) ? 0 : sWp;
			sdat = Qrsfilt.FilterProcess(enumFilterType, sdat, pSout, deviceid, index);
			pSout[DAT_TYPE.BPASS.ordinal()] *= 12;
			
			/* save the ecg sample afer bp filter */
			
			Dat[] iDat = bandPassSignal.getiDat();
			int iWritePoint = bandPassSignal.getiWritePoint();
			iDat[iWritePoint].setValue(pSout[DAT_TYPE.BPASS.ordinal()]);
			iDat[iWritePoint++].setTime(timeTemp);
			if (iWritePoint >= WAVE_LEN) {
				iWritePoint = 0;
			}
			bandPassSignal.setiWritePoint(iWritePoint);

			/* save the ecg sample afer mv filter */
			pSout[DAT_TYPE.MV.ordinal()] *= 1;
			
			Dat[] iDat2 = heartRateSignal.getiDat();
			int iWritePoint2 = heartRateSignal.getiWritePoint();
			iDat2[iWritePoint2].setValue(pSout[DAT_TYPE.MV.ordinal()]);
			//System.out.println(iDat2[iWritePoint2].getValue());
			iDat2[iWritePoint2++].setTime(timeTemp);
			if (iWritePoint2 >= WAVE_LEN) {
				iWritePoint2 = 0;
			}
			heartRateSignal.setiWritePoint(iWritePoint2);
		//}
	}

	/**
	 * @param pEcg
	 */
	private static void ecgSampleLoading(Wave orignalSignal, InputWave pEcg) {
		/* iWritePoint是波形数据缓冲区的写指针，指向下个数据要写的位置 */
		//int len = pEcg.getLen();
		int dat = pEcg.getDat();
		long t0 = pEcg.getT0();
		// iDat[WAVE_LEN]; iWritePoint
		/* iWritePoint是波形数据缓冲区的写指针，指向下个数据要写的位置 */
		//for (int i = 0; i < len; i++) {
			Dat[] iDat = orignalSignal.getiDat();
			int iWritePoint = orignalSignal.getiWritePoint();
			/* dat[i] int dat[MS2000]; 心电数据缓冲区 */
			iDat[iWritePoint].setValue(dat);
			/* time0+5*i; 计算数据时间 */
			iDat[iWritePoint++].setTime(t0);
			if (iWritePoint >= WAVE_LEN) {
				iWritePoint = 0;
			}
			orignalSignal.setiWritePoint(iWritePoint);
		//}
	}

	/**
	 *@param pEcgSample：单点数据
	 *@param deviceid：设备编号
	 *@param index：导联号I(0),II(1),III(2),avR(3),avL(4),avF(5),V(6)
	 *@return BeatInfo
	 */
	public static BeatInfo beatAnalysis(InputWave pEcgSample, String deviceid, int index) {
		CacheMap cacheMap = CacheMap.getCacheMap();
		String prefix = cacheMap.getPrefix(deviceid, index, "beatAnalysis");
		Wave orignalSignal = cacheMap.getObject(prefix + "orignalSignal", Wave.class);
		if(orignalSignal == null) orignalSignal = new Wave();
		Wave bandPassSignal =  cacheMap.getObject(prefix + "bandPassSignal", Wave.class);
		if(bandPassSignal == null) bandPassSignal = new Wave();
		Wave heartRateSignal = cacheMap.getObject(prefix + "heartRateSignal", Wave.class);
		if(heartRateSignal == null) heartRateSignal = new Wave();
		BeatInfo[] beatInfo = cacheMap.getObject(prefix + "beatInfo", BeatInfo[].class);
		if(beatInfo == null){
			beatInfo = new BeatInfo[BEAT_SUM];
		}
		Ecg pEcg = cacheMap.getObject(prefix + "pEcg", Ecg.class);
		if(pEcg == null) pEcg = new Ecg();
		int nobeatcounter = cacheMap.getInteger(prefix + "nobeatcounter");
		int[] pSout = new int[7];
		int rr=0;
		int len = pEcgSample.getLen();
		ecgSampleLoading(orignalSignal,pEcgSample);
		ecgSampleFiltering(orignalSignal, bandPassSignal, heartRateSignal, len, pSout, deviceid, index);
		rr = beatDetection(heartRateSignal, len, pEcg, deviceid, index);
		BeatInfo BeatInfoNow = null;
		if (rr!=0){	/*new Beat*/
			//phr = 12000/(rr);/*计算实时心率*/
			nobeatcounter = 0;
			/*beat 分析*/
			Tempbeat tempBeat = new Tempbeat();
			if(!StoreBeatCharacte_roughly(tempBeat, bandPassSignal, rr)){
				BeatInfoNow = new BeatInfo();
				Analbeat.AnalyzeBeatNew(tempBeat, BeatInfoNow);
				BeatCharacteDetec(BeatInfoNow, beatInfo, pEcg, deviceid, index);
				//phr = beatInfo[1].getMeanHeartRate();
			}
		}else{
			/*no beat*/
			if(index == 1){
				nobeatcounter += len;
				if (nobeatcounter >= 200*10){
					/*心动停止*/
					nobeatcounter=0;
					BeatInfoNow = new BeatInfo();
					ArrhythmiaInfo pArrhythmia =  new ArrhythmiaInfo();
					pArrhythmia.setArrhythmiaType(ARRH_TYPE.CARDIAC_ARREST);
					BeatInfoNow.setArrhythmia(pArrhythmia);
					System.out.println("********心动停止**********\n");
				}
			}
		}
		cacheMap.set(prefix + "orignalSignal", orignalSignal);
		cacheMap.set(prefix + "bandPassSignal", bandPassSignal);
		cacheMap.set(prefix + "heartRateSignal", heartRateSignal);
		cacheMap.set(prefix + "beatInfo", beatInfo);
		cacheMap.set(prefix + "pEcg", pEcg);
		cacheMap.set(prefix + "nobeatcounter", nobeatcounter);
		return BeatInfoNow;
	}
	
	/**
	 * @param json:算法输入json数据
	 * @return BeatInfo[]
	 */
	public static BeatInfo[] handleWave(String json){
		Parameters params = JSON.parseObject(json, Parameters.class);
		List<Ecgwave> list = params.getEcgwave();
		String deviceid = params.getDeviceid();
		CacheMap cacheMap = CacheMap.getCacheMap();
		String prefix = cacheMap.getPrefix(deviceid, 0, "handleWave");
		long time = cacheMap.getLong(prefix + "time");
		if(time == 0) time=System.currentTimeMillis();
		int[] temp = new int[CHN_SUM_NEW];
		BeatInfo[] beatInfo = null;
		for (int i = 0; i < list.size(); i++) {
			int ecg1 = list.get(i).getEcg1()*4;
			int ecg2 = list.get(i).getEcg2()*4;
			temp[0] = ecg1;
			temp[1] = ecg2;
			temp[2] = ecg2-ecg1;
			temp[3] = (ecg1+ecg2)/2;
			temp[4] = ecg1-ecg2/2;
			temp[5] = ecg2-ecg1/2;
			temp[6] = list.get(i).getEcgv()*4;
			for (int j = 0; j < CHN_SUM_NEW; j++) {
				InputWave pEcgSample = new InputWave();
				pEcgSample.setT0(time);
				pEcgSample.setDat(temp[j]);
				pEcgSample.setLen(1);
				BeatInfo beatInfoTemp = beatAnalysis(pEcgSample, deviceid, j);
				if(beatInfoTemp != null){
					if(beatInfo == null){
						beatInfo = new BeatInfo[CHN_SUM_NEW];
					}
					beatInfo[j] = beatInfoTemp;
				}
			}
			time += 5;
		}
		cacheMap.set(prefix + "time", time);
		return beatInfo;
	}
	
	public static boolean setFilterType(FILTER_TYPE filterType){
		enumFilterType = filterType;
		return true;
	}
	
}
