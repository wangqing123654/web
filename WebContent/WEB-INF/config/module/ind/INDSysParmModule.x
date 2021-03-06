 #
   # Title:药库参数设定
   #
   # Description:药库参数设定
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/04/27

Module.item=insertData;updateData

//添加数据
insertData.Type=TSQL
insertData.SQL=INSERT INTO IND_SYSPARM ( &
		 FIXEDAMOUNT_FLG,REUPRICE_FLG,DISCHECK_FLG,GPRICE_FLG,GOWN_COSTRATE, &
		 GNHI_COSTRATE,GOV_COSTRATE,UPDATE_GRETAIL_FLG,WOWN_COSTRATE,WNHI_COSTRATE, &
		 WGOV_COSTRATE,UPDATE_WRETAIL_FLG,MANUAL_TYPE,UNIT_TYPE,OPT_USER, &
		 OPT_DATE,OPT_TERM, PHA_PRICE_FLG) &
		VALUES( &
		 <FIXEDAMOUNT_FLG>,<REUPRICE_FLG>,<DISCHECK_FLG>,<GPRICE_FLG>,<GOWN_COSTRATE>, &
		 <GNHI_COSTRATE>,<GOV_COSTRATE>,<UPDATE_GRETAIL_FLG>,<WOWN_COSTRATE>,<WNHI_COSTRATE>, &
		 <WGOV_COSTRATE>,<UPDATE_WRETAIL_FLG>,<MANUAL_TYPE>,<UNIT_TYPE>,<OPT_USER>, &
		 <OPT_DATE>,<OPT_TERM>, <PHA_PRICE_FLG>)
insertData.Debug=N

//更新数据
updateData.Type=TSQL
updateData.SQL=UPDATE IND_SYSPARM &
		SET FIXEDAMOUNT_FLG=<FIXEDAMOUNT_FLG>, &
		    REUPRICE_FLG=<REUPRICE_FLG>, &
		    DISCHECK_FLG=<DISCHECK_FLG>, &
		    GPRICE_FLG=<GPRICE_FLG>, &
		    GOWN_COSTRATE=<GOWN_COSTRATE>, &
		    GNHI_COSTRATE=<GNHI_COSTRATE>, &
		    GOV_COSTRATE=<GOV_COSTRATE>, &
		    UPDATE_GRETAIL_FLG=<UPDATE_GRETAIL_FLG>, &
		    WOWN_COSTRATE=<WOWN_COSTRATE>, &
		    WNHI_COSTRATE=<WNHI_COSTRATE>, &
		    WGOV_COSTRATE=<WGOV_COSTRATE>, &
		    UPDATE_WRETAIL_FLG=<UPDATE_WRETAIL_FLG>, &
		    MANUAL_TYPE=<MANUAL_TYPE>, &
		    UNIT_TYPE=<UNIT_TYPE>, &
		    OPT_USER=<OPT_USER>, &
		    OPT_DATE=<OPT_DATE>, &
		    OPT_TERM=<OPT_TERM>, &
		    PHA_PRICE_FLG=<PHA_PRICE_FLG>		   
updateData.Debug=N

