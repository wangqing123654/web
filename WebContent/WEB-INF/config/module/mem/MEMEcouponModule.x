Module.item=isChild;insertCard;updatedata;deletedata

// ²éÑ¯È«×Ö¶Î
isChild.Type=TSQL
isChild.SQL=SELECT MEM_CODE,MEM_DESC,MEM_ENG_DESC,PY1, &
             VALID_DAYS,OPT_DATE,OPT_USER,DESCRIPTION &
	     FROM &
	     MEM_MEMBERSHIP_INFO &
	     WHERE MEM_TYPE ='2' ORDER BY SEQ
isChild.Debug=N




// ÐÞ¸ÄÈ«×Ö¶Î
 
updatedata.Type=TSQL
updatedata.SQL=UPDATE MEM_MEMBERSHIP_INFO SET MEM_DESC=<MEM_DESC>,MEM_ENG_DESC=<MEM_ENG_DESC>,VALID_DAYS=<VALID_DAYS>, &
                      PY1=<PY1>,DESCRIPTION=<DESCRIPTION>,OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>, &
		      MEM_TYPE=<MEM_TYPE>,OPT_TERM=<OPT_TERM> WHERE MEM_CODE=<MEM_CODE>
                   
               
updatedata.Debug=N
//Ìí¼Ó
insertCard.Type=TSQL
insertCard.SQL=INSERT INTO MEM_MEMBERSHIP_INFO &  
               ( MEM_CODE,MEM_DESC,MEM_ENG_DESC,PY1,DESCRIPTION, &
	         MEM_TYPE,VALID_DAYS,OPT_DATE,OPT_USER,OPT_TERM ) &
	       VALUES &
              ( <MEM_CODE>,<MEM_DESC>,<MEM_ENG_DESC>,<PY1>,<DESCRIPTION>, &
	        <MEM_TYPE>,<VALID_DAYS>,<OPT_DATE>,<OPT_USER>,<OPT_TERM> )
               
insertCard.Debug=N

//É¾³ý

deletedata.Type=TSQL
deletedata.SQL=DELETE FROM MEM_MEMBERSHIP_INFO WHERE MEM_CODE=<MEM_CODE>
               
deletedata.Debug=N