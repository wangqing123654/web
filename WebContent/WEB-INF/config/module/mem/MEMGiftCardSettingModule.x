Module.item=isChild;insertCard;updatedata;deletedata

// ��ѯȫ�ֶ�
isChild.Type=TSQL
isChild.SQL=SELECT GIFTCARD_CODE,GIFTCARD_DESC,GIFTCARD_ENG_DESC,PY1,CARD_TYPE, &
             FACE_VALUE,RETAIL_PRICE,DESCRIPTION,SEQ,OPT_DATE,OPT_USER &
	     FROM &
	     MEM_CASH_CARD_INFO &
	     ORDER BY SEQ
isChild.Debug=N




// �޸�ȫ�ֶ�
 
updatedata.Type=TSQL
updatedata.SQL=UPDATE MEM_CASH_CARD_INFO SET GIFTCARD_DESC=<GIFTCARD_DESC>,GIFTCARD_ENG_DESC=<GIFTCARD_ENG_DESC>,RETAIL_PRICE=<RETAIL_PRICE>, &
                      CARD_TYPE=<CARD_TYPE>,PY1=<PY1>,DESCRIPTION=<DESCRIPTION>,OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>, &
		      SEQ=<SEQ>,FACE_VALUE=<FACE_VALUE>,OPT_TERM=<OPT_TERM> WHERE GIFTCARD_CODE=<GIFTCARD_CODE>
                   
               
updatedata.Debug=N
//����
insertCard.Type=TSQL
insertCard.SQL=INSERT INTO MEM_CASH_CARD_INFO &  
               ( GIFTCARD_CODE,GIFTCARD_DESC,GIFTCARD_ENG_DESC,PY1,DESCRIPTION,SEQ, &
	         CARD_TYPE,RETAIL_PRICE,FACE_VALUE,OPT_DATE,OPT_USER,OPT_TERM ) &
	       VALUES &
              ( <GIFTCARD_CODE>,<GIFTCARD_DESC>,<GIFTCARD_ENG_DESC>,<PY1>,<DESCRIPTION>,<SEQ>, &
	        <CARD_TYPE>,<RETAIL_PRICE>,<FACE_VALUE>,<OPT_DATE>,<OPT_USER>,<OPT_TERM> )
               
insertCard.Debug=N

//ɾ��

deletedata.Type=TSQL
deletedata.SQL=DELETE FROM MEM_CASH_CARD_INFO WHERE GIFTCARD_CODE=<GIFTCARD_CODE>
               
deletedata.Debug=N