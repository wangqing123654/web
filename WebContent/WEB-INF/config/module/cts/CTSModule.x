# 
#  Title:CTSmodule
# 
#  Description:CTSmodule
# 
#  Copyright: Copyright (c) Bluecore 2012
# 
#  author zhangp 2012.10.10
#  version 1.0
#
Module.item=selectCloth;insertCTSWASHM;insertCTSWASHD;selectWASHM;selectWASHD;updateWASHM;updateWASHD;selectCTSMD;updateWASHMEndDate;insertCTSOUTD;insertCTSOUTM;selectOUTM;selectOUTD;selectOUTMD;insertCTSANT;updateCTSANT;deleteCTSANT;updateStockDD;insertCTSANTLIST;updateCTSANTLIST;deleteCTSANTLIST;updateOutWashNo;updateInWashNo;deleteOUTM;deleteOUTD;deleteWASHM;deleteWASHD

//����stockdd
updateStockDD.Type=TSQL
updateStockDD.SQL=UPDATE INV_STOCKDD SET STATE=<STATE> WHERE RFID=<RFID>
updateStockDD.Debug=Y


//д��ANT
insertCTSANT.Type=TSQL
insertCTSANT.SQL= INSERT INTO CTS_ANT &
			(ANT_ID, ANT_CHN_DESC,PY1,PY2,OPT_USER, OPT_DATE, OPT_TERM, SEQ) &
			VALUES (<ANT_ID>,<ANT_CHN_DESC>,<PY1>,<PY2>,<OPT_USER>,SYSDATE,<OPT_TERM>,<SEQ>)
insertCTSANT.Debug=Y

//����ANT 
updateCTSANT.Type=TSQL
updateCTSANT.SQL= UPDATE CTS_ANT SET ANT_CHN_DESC=<ANT_CHN_DESC>, &
				PY1=<PY1>,PY2=<PY2>,OPT_USER = <OPT_USER>, &
				OPT_DATE = SYSDATE, OPT_TERM = <OPT_TERM> &
					WHERE ANT_ID=<ANT_ID>
			
updateCTSANT.Debug=Y

//ɾ��ant
deleteCTSANT.Type=TSQL
deleteCTSANT.SQL= DELETE FROM CTS_ANT WHERE ANT_ID=<ANT_ID>
deleteCTSANT.Debug=Y


//д��ANT_LIST
insertCTSANTLIST.Type=TSQL
insertCTSANTLIST.SQL= INSERT INTO CTS_ANT_LIST &
			(CTS_ANT_IP, ANT_CHN_DESC, ANT_ID, OPT_USER, OPT_DATE, OPT_TERM, RFID_URL,MQ_DESC, RFID_IP, RFID_ID) &
			VALUES (<CTS_ANT_IP>,<ANT_CHN_DESC>,<ANT_ID>,<OPT_USER>,SYSDATE,<OPT_TERM>,<RFID_URL>,<MQ_DESC>,<RFID_IP>,<RFID_ID>)
insertCTSANTLIST.Debug=Y

//����ANT_LIST 
updateCTSANTLIST.Type=TSQL
updateCTSANTLIST.SQL= UPDATE CTS_ANT_LIST SET CTS_ANT_IP=<CTS_ANT_IP>, &
				ANT_CHN_DESC=<ANT_CHN_DESC>,ANT_ID=<ANT_ID>,RFID_URL = <RFID_URL>, &
				MQ_DESC = <MQ_DESC>, RFID_IP = <RFID_IP>, RFID_ID = <RFID_ID> &
					WHERE CTS_ANT_IP=<CTS_ANT_IP> AND ANT_CHN_DESC=<ANT_CHN_DESC> AND ANT_ID=<ANT_ID>
			
updateCTSANTLIST.Debug=Y

//ɾ��ANT_LIST
deleteCTSANTLIST.Type=TSQL
deleteCTSANTLIST.SQL= DELETE FROM CTS_ANT_LIST WHERE CTS_ANT_IP=<CTS_ANT_IP> AND ANT_CHN_DESC=<ANT_CHN_DESC> AND ANT_ID=<ANT_ID>
deleteCTSANTLIST.Debug=Y




//��ѯ�·�
selectCloth.Type=TSQL
selectCloth.SQL=SELECT   A.RFID CLOTH_NO, A.INV_CODE, A.OWNER, A.OWNER_CODE,&
         			A.CTS_COST_CENTRE STATION_CODE, A.PAT_FLG, 'N' NEW_FLG, A.TURN_POINT &
    			FROM INV_STOCKDD A, INV_BASE B &
   				WHERE A.INV_CODE = B.INV_CODE &
                ORDER BY A.RFID
selectCloth.item=CLOTH_NO
selectCloth.CLOTH_NO=A.RFID=<CLOTH_NO>
selectCloth.Debug=Y


//д��CTS����
insertCTSWASHM.Type=TSQL
insertCTSWASHM.SQL=INSERT INTO CTS_WASHM &
            			(WASH_NO, DEPT_CODE, STATION_CODE, QTY, &
             			START_DATE, &
             			PAT_FLG, &
             			STATE, WASH_CODE, OPT_USER, &
             			OPT_DATE, OPT_TERM, NEW_FLG, TURN_POINT &
            			) &
     				VALUES (<WASH_NO>, <DEPT_CODE>, <STATION_CODE>, <QTY>, &
             			<START_DATE>, &
             			<PAT_FLG>, &
             			<STATE>, <WASH_CODE>, <OPT_USER>, &
            		 	<OPT_DATE>, <OPT_TERM>, <NEW_FLG>, <TURN_POINT> &
            			)
insertCTSWASHM.Debug=Y


//д��CTS��ϸ
insertCTSWASHD.Type=TSQL
insertCTSWASHD.SQL=INSERT INTO CTS_WASHD &
            			(WASH_NO, SEQ_NO, CLOTH_NO, OWNER, PAT_FLG, OPT_USER, &
             			OPT_DATE, OPT_TERM , OUT_FLG, NEW_FLG, TURN_POINT &
            			) &
     				VALUES (<WASH_NO>, <SEQ_NO>, <CLOTH_NO>, <OWNER>, <PAT_FLG>, <OPT_USER>, &
            		 	<OPT_DATE>, <OPT_TERM> , <OUT_FLG>, <NEW_FLG>, <TURN_POINT> &
            			)
insertCTSWASHD.Debug=Y


//д��CTS����
insertCTSOUTM.Type=TSQL
insertCTSOUTM.SQL=INSERT INTO CTS_OUTM &
            			(WASH_NO, STATION_CODE, &
             			PAT_FLG, END_DATE,&
             			STATE, WASH_CODE, OPT_USER, &
             			OPT_DATE, OPT_TERM, QTY, TURN_POINT &
            			) &
     				VALUES (<WASH_NO>, <STATION_CODE>, &
             			<PAT_FLG>, <END_DATE>, &
             			<STATE>, <WASH_CODE>, <OPT_USER>, &
            		 	<OPT_DATE>, <OPT_TERM>, <QTY>, <TURN_POINT> &
            			)
insertCTSOUTM.Debug=Y


//д��CTS��ϸ
insertCTSOUTD.Type=TSQL
insertCTSOUTD.SQL=INSERT INTO CTS_OUTD &
            			(WASH_NO, CLOTH_NO, OWNER, PAT_FLG, OPT_USER, &
             			OPT_DATE, OPT_TERM, NEW_FLG, TURN_POINT &
            			) &
     				VALUES (<WASH_NO>, <CLOTH_NO>, <OWNER>, <PAT_FLG>, <OPT_USER>, &
            		 	<OPT_DATE>, <OPT_TERM>, <NEW_FLG>, <TURN_POINT> &
            			)
insertCTSOUTD.Debug=Y


//��ѯOPDM
selectWASHM.Type=TSQL
selectWASHM.SQL=SELECT WASH_NO, DEPT_CODE, STATION_CODE, QTY, START_DATE, END_DATE, PAT_FLG, &
       					STATE, WASH_CODE, OPT_USER, OPT_DATE, OPT_TERM &
  				FROM CTS_WASHM &
 				ORDER BY WASH_NO
selectWASHM.item=WASH_NO;START_DATE;END_DATE
selectWASHM.WASH_NO=WASH_NO=<WASH_NO>
selectWASHM.START_DATE=START_DATE>=TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS')
selectWASHM.END_DATE=END_DATE<=TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS')
selectWASHM.Debug=Y

//��ѯOPDM
selectOUTM.Type=TSQL
selectOUTM.SQL=SELECT WASH_NO, STATION_CODE, END_DATE, PAT_FLG, &
       					STATE, WASH_CODE, OPT_USER, OPT_DATE, OPT_TERM, QTY, TURN_POINT &
  				FROM CTS_OUTM &
 				ORDER BY WASH_NO
selectOUTM.item=WASH_NO;START_DATE;END_DATE
selectOUTM.WASH_NO=WASH_NO=<WASH_NO>
selectOUTM.START_DATE=START_DATE>=TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS')
selectOUTM.END_DATE=END_DATE<=TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS')
selectOUTM.Debug=Y


//��ѯOPDD
selectWASHD.Type=TSQL
selectWASHD.SQL=SELECT WASH_NO, SEQ_NO, CLOTH_NO, OWNER, PAT_FLG, OUT_FLG, NEW_FLG, OPT_USER, OPT_DATE, OPT_TERM &
  				FROM CTS_WASHD &
  				ORDER BY SEQ_NO
selectWASHD.item=WASH_NO
selectWASHD.WASH_NO=WASH_NO=<WASH_NO>
selectWASHD.Debug=Y

//��ѯOPDD
selectOUTD.Type=TSQL
selectOUTD.SQL=SELECT A.WASH_NO, A.CLOTH_NO, A.OWNER, B.OWNER_CODE, A.PAT_FLG, A.OPT_USER, A.OPT_DATE, A.OPT_TERM , 'Y' OUT_FLG &
  				FROM CTS_OUTD A, INV_STOCKDD B &
				WHERE A.CLOTH_NO=B.RFID
  				ORDER BY CLOTH_NO
selectOUTD.item=WASH_NO
selectOUTD.WASH_NO=WASH_NO=<WASH_NO>
selectOUTD.Debug=Y


//����OPDM
updateWASHM.Type=TSQL
updateWASHM.SQL=UPDATE CTS_WASHM &
   						SET END_DATE=<END_DATE> , &
   						STATE=<STATE>, &
   						OUT_QTY=<OUT_QTY> &
 					WHERE WASH_NO=<WASH_NO>
updateWASHM.Debug=Y


//����OPDD
updateWASHD.Type=TSQL
updateWASHD.SQL=UPDATE CTS_WASHD &
   					SET OUT_FLG=<OUT_FLG> &
 				WHERE WASH_NO=<WASH_NO> &
 					AND SEQ_NO=<SEQ_NO> &
 					AND CLOTH_NO=<CLOTH_NO>
updateWASHD.Debug=Y

//����OPDM
updateWASHMEndDate.Type=TSQL
updateWASHMEndDate.SQL=UPDATE CTS_OUTM &
   						SET END_DATE=<END_DATE>, STATE=<STATE>, QTY=<QTY> &
 					WHERE WASH_NO=<WASH_NO>
updateWASHMEndDate.Debug=Y


//����PESResult
updatePESResult.Type=TSQL
updatePESResult.SQL=UPDATE PES_RESULT &
						SET PES_A = <PES_A>, &
							PES_B = <PES_B>, &
							PES_C = <PES_C>, &
							PES_D = <PES_D>, &
							PES_E = <PES_E>, &
							PES_F = <PES_F>, &
							PES_G = <PES_G>, &
							PES_H = <PES_H>, &
							PES_I = <PES_I>, &
							PES_J = <PES_J>, &
							PES_K = <PES_K>, &
							PES_L = <PES_L>, &
							PES_O = <PES_O>, &
							PES_P = <PES_P>, &
							OPT_USER = <OPT_USER>, &
							OPT_DATE = <OPT_DATE>, &
							OPT_TERM = <OPT_TERM> &
						WHERE TYPE_CODE = <TYPE_CODE> &
							AND PES_NO = <PES_NO>
updatePESResult.Debug=Y


//��ѯOPDM
selectOPDMforPrint.Type=TSQL
selectOPDMforPrint.SQL=SELECT A.SEQ, A.ORDER_DATE, A.AGE, B.ICD_CHN_DESC, A.ORDER_QTY, &
       						A.ANTIBIOTIC_QTY, A.INJECT_QTY, A.BASE_QTY, A.GOODS_QTY, A.RX_TOTAL, &
       						C.USER_NAME DR_CODE, D.USER_NAME PHA_DOSAGE_CODE, &
       						E.USER_NAME PHA_DISPENSE_CODE, &
       						CASE  &
       						WHEN A.REASON_FLG = 'Y' &
       						THEN '1' &
       						ELSE '0' &
       						END REASON_FLG, F.CHN_DESC QUESTION_CODE &
  						FROM PES_OPDM A, &
       						SYS_DIAGNOSIS B, &
       						SYS_OPERATOR C, &
       						SYS_OPERATOR D, &
       						SYS_OPERATOR E, &
       						(SELECT ID, CHN_DESC &
          						FROM SYS_DICTIONARY &
         						WHERE GROUP_ID = 'PES_QUESTION_CODE') F &
 						WHERE A.DIAG = B.ICD_CODE &
   							AND A.DR_CODE = C.USER_ID &
   							AND A.PHA_DOSAGE_CODE = D.USER_ID &
   							AND A.PHA_DISPENSE_CODE = E.USER_ID &
   							AND A.QUESTION_CODE = F.ID(+) &
  						ORDER BY SEQ
selectOPDMforPrint.item=TYPE_CODE;PES_NO
selectOPDMforPrint.TYPE_CODE=TYPE_CODE=<TYPE_CODE>
selectOPDMforPrint.PES_NO=PES_NO=<PES_NO>
selectOPDMforPrint.Debug=Y


//��ѯPESResult
selectCTSMD.Type=TSQL
selectCTSMD.SQL=SELECT A.WASH_NO &
 					FROM CTS_WASHM A, CTS_WASHD B &
 					WHERE A.WASH_NO = B.WASH_NO AND A.STATE<>'3'
selectCTSMD.item=CLOTH_NO
selectCTSMD.CLOTH_NO=B.CLOTH_NO=<CLOTH_NO>
selectCTSMD.Debug=Y


//��ѯPESResult
selectOUTMD.Type=TSQL
selectOUTMD.SQL=SELECT A.WASH_NO &
 					FROM CTS_OUTM A, CTS_OUTD B &
 					WHERE A.WASH_NO = B.WASH_NO &
					AND A.END_DATE IS NULL
selectOUTMD.item=CLOTH_NO
selectOUTMD.CLOTH_NO=B.CLOTH_NO=<CLOTH_NO>
selectOUTMD.Debug=Y


updateInWashNo.Type=TSQL
updateInWashNo.SQL=UPDATE CTS_OUTD &
   					SET IN_WASH_NO = <IN_WASH_NO> &
 					WHERE CLOTH_NO = <CLOTH_NO> AND WASH_NO = <WASH_NO>
updateInWashNo.Debug=Y


updateOutWashNo.Type=TSQL
updateOutWashNo.SQL=UPDATE CTS_WASHD &
   					SET OUT_WASH_NO = <OUT_WASH_NO> &
 					WHERE CLOTH_NO = <CLOTH_NO> AND WASH_NO = <WASH_NO>
updateOutWashNo.Debug=Y

/ɾ��outM
deleteOUTM.Type=TSQL
deleteOUTM.SQL= DELETE FROM CTS_OUTM WHERE WASH_NO=<WASH_NO>
deleteOUTM.Debug=Y

/ɾ��outD
deleteOUTD.Type=TSQL
deleteOUTD.SQL= DELETE FROM CTS_OUTD WHERE WASH_NO=<WASH_NO>
deleteOUTD.Debug=Y

/ɾ��deleteWASHM
deleteWASHM.Type=TSQL
deleteWASHM.SQL= DELETE FROM CTS_WASHM WHERE WASH_NO=<WASH_NO>
deleteWASHM.Debug=Y

/ɾ��deleteWASHD
deleteWASHD.Type=TSQL
deleteWASHD.SQL= DELETE FROM CTS_WASHD WHERE WASH_NO=<WASH_NO>
deleteWASHD.Debug=Y