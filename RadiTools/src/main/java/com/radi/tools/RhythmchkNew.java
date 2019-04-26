package com.radi.tools;

import com.radi.entity.ARRH_TYPE;
import com.radi.entity.BeatInfo;

public class RhythmchkNew {
	
	private final static int QQ = 0;
	private final static int NN = 1;
	private final static int NV = 2;
	private final static int VN = 3;
	private final static int VV = 4;
	private final static int RBB_LENGTH = 8;
	private final static int LEARNING = 0;
	private final static int READY = 1;
	private final static int BRADY_LIMIT = Qrsfilt.MS1500;
	
	static ARRH_TYPE RhythmChk_New(BeatInfo[] pBeatInfo, String deviceid, int index){
		ARRH_TYPE classify= ARRH_TYPE.NORMAL;
		int[] rr_buf = new int[AlgorithmTool.BEAT_SUM];
		int max_rr=0,min_rr=500;
		int rr_temp=0;
		for (int i=AlgorithmTool.BEAT_SUM-6; i<AlgorithmTool.BEAT_SUM; i++){
			if (pBeatInfo[i].getRealHeartRate()<20){
				continue;
			}
			rr_buf[i] = rr_temp = pBeatInfo[i].getRealHeartRate();
			if (max_rr < rr_temp){
				max_rr = rr_temp;
			}
			if (min_rr > rr_temp && rr_temp>0){
				min_rr = rr_temp;
			}
		}

		if (max_rr > 2*min_rr){
			//窦性停搏
			System.out.println("********窦性停搏**********\n");
			return ARRH_TYPE.CA;
		}else{	
			for(int i=AlgorithmTool.BEAT_SUM-4; i<AlgorithmTool.BEAT_SUM; i++){
				int rr=0;
				rr = rr_buf[i];
				if (rr < 160){
					classify = ARRH_TYPE.NORMAL;
					break;
				}else{/*连续几个心率大于160认为是：室上速*/
					classify = ARRH_TYPE.SVT;
				}
			}
			if(classify == ARRH_TYPE.SVT) System.out.println("********室上速**********\n");

			if (classify == ARRH_TYPE.NORMAL){
				for (int i=AlgorithmTool.BEAT_SUM-4; i<AlgorithmTool.BEAT_SUM; i++){
					int rr=0;
					rr = rr_buf[i];
					if (rr > 60){
						classify = ARRH_TYPE.NORMAL;
						break;
					}else{/*连续几个心率小于60认为是：心动过缓*/
						classify = ARRH_TYPE.BC;
					}
				}
			}
			if(classify == ARRH_TYPE.BC) System.out.println("********心动过缓**********\n");

			if (classify == ARRH_TYPE.NORMAL){
				//判断室早
				ARRH_TYPE temp;
				temp =  RhythmChk(rr_buf[AlgorithmTool.BEAT_SUM-1],deviceid,index);
				if (temp == ARRH_TYPE.PVC){
					classify = ARRH_TYPE.PVC;
					System.out.println("********室早**********\n");
				}
			}
		}
		return classify;
	}
	
	private static ARRH_TYPE RhythmChk(int rr, String deviceid, int index){
		CacheMap cacheMap = CacheMap.getCacheMap();
		String prefix = cacheMap.getPrefix(deviceid, index, "RhythmChk");
		int[] RRBuffer = cacheMap.getObject(prefix + "RRBuffer", int[].class);
		if(RRBuffer == null) RRBuffer = new int[RBB_LENGTH];
		int[] RRTypes = cacheMap.getObject(prefix + "RRTypes", int[].class);
		if(RRTypes == null) RRTypes = new int[RBB_LENGTH];
		int BeatCount = cacheMap.getInteger(prefix + "BeatCount");
		int ClassifyState = cacheMap.getInteger(prefix + "ClassifyState");
		
		int i, regular = 1 ;
		int NNEst, NVEst ;
		
		if(BeatCount < 4){	// Wait for at least 4 beats before classifying anything.
			if(++BeatCount == 4){
				ClassifyState = READY ;
				cacheMap.set(prefix + "ClassifyState", ClassifyState);
			}
			cacheMap.set(prefix + "BeatCount", BeatCount);
		}
		for(i = RBB_LENGTH-1; i > 0; --i)// Stick the new RR interval into the RR interval Buffer.
		{
			RRBuffer[i] = RRBuffer[i-1] ;
			RRTypes[i] = RRTypes[i-1] ;
		}
		RRBuffer[0] = rr ;
		if(ClassifyState == LEARNING)
		{
			RRTypes[0] = QQ ;
			cacheMap.set(prefix + "RRBuffer", RRBuffer);
			cacheMap.set(prefix + "RRTypes", RRTypes);
			return(ARRH_TYPE.UNKNOWN) ;
		}
		if(RRTypes[1] == QQ)
		{
			for(i = 0, regular = 1; i < 3; ++i)
				if(!RRMatch(RRBuffer[i],RRBuffer[i+1]))
					regular = 0 ;
			if(regular == 1)
			{
				RRTypes[0] = NN ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.NORMAL) ;
			}
			for(i = 0, regular = 1; i < 6; ++i)
				if(!RRMatch(RRBuffer[i],RRBuffer[i+2]))
					regular = 0 ;//>
			for(i = 0; i < 6; ++i)
				if(RRMatch(RRBuffer[i],RRBuffer[i+1]))
					regular = 0 ;//<
			if(regular == 1)
			{
				//BigeminyFlag = 1 ;
				if(RRBuffer[0] < RRBuffer[1])
				{
					RRTypes[0] = NV ;
					RRTypes[1] = VN ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.PVC) ;
				}
				else
				{
					RRTypes[0] = VN ;
					RRTypes[1] = NV ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.NORMAL) ;
				}
			}
			if(RRShort(RRBuffer[0],RRBuffer[1]) && RRMatch(RRBuffer[1],RRBuffer[2])
				&& RRMatch(RRBuffer[2]*2,RRBuffer[3]+RRBuffer[4]) &&
				RRMatch(RRBuffer[4],RRBuffer[0]) && RRMatch(RRBuffer[5],RRBuffer[2]))
			{
				RRTypes[0] = NV ;
				RRTypes[1] = NN ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.PVC) ;
			}
			else
			{
				RRTypes[0] = QQ ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.UNKNOWN) ;
			}
		}
		else if(RRTypes[1] == NN)
		{
			if(RRShort2(RRBuffer,RRTypes))
			{
				if(RRBuffer[1] < BRADY_LIMIT)
				{
					RRTypes[0] = NV ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.PVC) ;
				}
				else RRTypes[0] = QQ ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.UNKNOWN) ;
			}
			else if(RRMatch(RRBuffer[0],RRBuffer[1]))
			{
				RRTypes[0] = NN ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.NORMAL) ;
			}
			else if(RRShort(RRBuffer[0],RRBuffer[1]))
			{
				if(RRMatch(RRBuffer[0],RRBuffer[2]) && (RRTypes[2] == NN))
				{
					RRTypes[0] = NN ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.NORMAL) ;
				}
				else if(RRBuffer[1] < BRADY_LIMIT)
				{
					RRTypes[0] = NV ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.PVC) ;
				}
				else
				{
					RRTypes[0] = QQ ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.UNKNOWN) ;
				}
			}
			else
			{
				RRTypes[0] = QQ ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.NORMAL) ;
			}
		}
		else if(RRTypes[1] == NV)
		{
			if(RRShort2(RRBuffer,RRTypes))
			{
				if(RRMatch(RRBuffer[0],RRBuffer[1]))
				{
					RRTypes[0] = NN ;
					RRTypes[1] = NN ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.NORMAL) ;
				}
				else if(RRBuffer[0] > RRBuffer[1])
				{
					RRTypes[0] = VN ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.NORMAL) ;
				}
				else
				{
					RRTypes[0] = QQ ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.UNKNOWN) ;
				}
			}
			else if(RRMatch(RRBuffer[0],RRBuffer[1]))
			{
				RRTypes[0] = VV ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.PVC) ;
			}
			else if(RRBuffer[0] > RRBuffer[1])
			{
				RRTypes[0] = VN ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.NORMAL) ;
			}
			else
			{
				RRTypes[0] = QQ ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.UNKNOWN) ;
			}
		}
		else if(RRTypes[1] == VN)
		{
			for(i = 2;(i < RBB_LENGTH) && (RRTypes[i] != NN); ++i) ;
			if(i != RBB_LENGTH)
			{
				NNEst = RRBuffer[i] ;
				if(RRMatch(RRBuffer[0],NNEst))
				{
					RRTypes[0] = NN ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.NORMAL) ;
				}
			}
			else NNEst = 0 ;
			for(i = 2;(i < RBB_LENGTH) && (RRTypes[i] != NV); ++i) ;
			if(i != RBB_LENGTH)
				NVEst = RRBuffer[i] ;
			else NVEst = 0 ;
			if((NNEst == 0) && (NVEst != 0))
				NNEst = (RRBuffer[1]+NVEst) >> 1 ;
			if((NVEst != 0) &&
				(Math.abs(NNEst - RRBuffer[0]) < Math.abs(NVEst - RRBuffer[0])) &&
				RRMatch(NNEst,RRBuffer[0]))
			{
				RRTypes[0] = NN ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.NORMAL) ;
			}
			else if((NVEst != 0) &&
				(Math.abs(NNEst - RRBuffer[0]) > Math.abs(NVEst - RRBuffer[0])) &&
				RRMatch(NVEst,RRBuffer[0]))
			{
				RRTypes[0] = NV ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.PVC) ;
			}
			else
			{
				RRTypes[0] = QQ ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.UNKNOWN) ;
			}
		}
		else
		{
			if(RRMatch(RRBuffer[0],RRBuffer[1]))
			{
				RRTypes[0] = VV ;
				cacheMap.set(prefix + "RRBuffer", RRBuffer);
				cacheMap.set(prefix + "RRTypes", RRTypes);
				return(ARRH_TYPE.PVC) ;
			}
			else
			{
				if(RRShort(RRBuffer[0],RRBuffer[1]))
				{
					RRTypes[0] = QQ ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.UNKNOWN) ;
				}
				else
				{
					RRTypes[0] = VN ;
					cacheMap.set(prefix + "RRBuffer", RRBuffer);
					cacheMap.set(prefix + "RRTypes", RRTypes);
					return(ARRH_TYPE.NORMAL) ;
				}
			}
		}
	}
	
	private static boolean RRMatch(int rr0,int rr1)
	{
		if(Math.abs(rr0-rr1) < ((rr0+rr1)>>3))
			return(true) ;
		else return(false) ;
	}

	private static boolean RRShort(int rr0, int rr1)
	{
		if(rr0 < rr1-(rr1>>2))
			return(true) ;
		else return(false) ;
	}
	
	private static boolean RRShort2(int[] rrIntervals, int[] rrTypes)
	{
		int rrMean = 0, i, nnCount ;

		for(i = 1, nnCount = 0; (i < 7) && (nnCount < 4); ++i)
			if(rrTypes[i] == NN)
			{
				++nnCount ;
				rrMean += rrIntervals[i] ;
			}

			if(nnCount != 4)
				return(false) ;
			rrMean >>= 2 ;

			for(i = 1, nnCount = 0; (i < 7) && (nnCount < 4); ++i)
				if(rrTypes[i] == NN)
				{
					if(Math.abs(rrMean-rrIntervals[i]) > (rrMean>>4))
						i = 10 ;
				}

				if((i < 9) && (rrIntervals[0] < (rrMean - (rrMean>>3))))
					return(true) ;
				else
					return(false) ;
	}
	
}
