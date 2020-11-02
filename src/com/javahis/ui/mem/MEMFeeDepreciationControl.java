package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jdo.ekt.EKTTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
/**
*
* <p>Title:会员费折旧</p>
*
* <p>Description: 会员费折旧</p>
*
* <p>Copyright: Copyright (c) /p>
*
* <p>Company: BlueCore</p>
*
* @author huangtt 2013140220
* @version 1.0
*/
public class MEMFeeDepreciationControl extends TControl{
	private static TTable table1;
	private static TTable table2;
	private static String date_s;
	private static String date_e;
	
	 /**
     * 初始化
     */
    public void onInit(){
    	table1 = (TTable) getComponent("TABLE1"); 
    	table2 = (TTable) getComponent("TABLE2"); 
    	//初始化查询时间
    	Timestamp date = StringTool.getTimestamp(new Date());
		 this.setValue("START_DATE",
				 StringTool.rollDate(date, -1).toString().substring(0, 10).replace('-', '/'));
		 this.setValue("END_DATE", date.toString()
					.substring(0, 10).replace('-', '/'));
		 String sql = "SELECT MONTH_CYCLE FROM BIL_SYSPARM WHERE ADM_TYPE='O'";
		 TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		 this.setValue("DEPRECIATION_DATE", date.toString()
					.substring(0, 8).replace('-', '/')+parm.getValue("MONTH_CYCLE", 0));
		 onTable1();
		 this.callFunction("UI|TABLE1|addEventListener",
					TTableEvent.CHECK_BOX_CLICKED, this,
					"onTable1Clicked"); 

    }
    
    public void onTable1Clicked(Object obj){
    	table2.removeRowAll();
    	table1 = (TTable) obj;
    	table1.acceptText();
    	TParm parm1 = table1.getParmValue();
    	String memCodes = "";
    	for(int i=0;i<parm1.getCount("FLG");i++){
    		if(parm1.getBoolean("FLG", i)){
    			memCodes += "'" + parm1.getValue("MEM_CODE", i) + "',";
			}
    	}
    	if(memCodes.length()>0){
    		memCodes = memCodes.substring(0, memCodes.length() - 1);
		}
    	onTable2(memCodes);
    }
    
    public void onTable1(){
    	date_s = getValueString("START_DATE");
		date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "").substring(0, 8);
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "").substring(0, 8);
    	String sql = "SELECT 'N' FLG, B.MEM_CODE,COUNT(B.MEM_CODE) MEM_COUNT" +
    			" FROM MEM_TRADE A,MEM_PATINFO B" +
    			" WHERE A.MR_NO=B.MR_NO" +
    			" AND A.STATUS=1" +
    			" AND A.REMOVE_FLG='N'" +
    			" AND B.END_DATE > SYSDATE" +
    			" AND A.START_DATE BETWEEN TO_DATE ('"+date_s+"', 'YYYYMMDD') " +
				" AND  TO_DATE ('"+date_e+"', 'YYYYMMDD')" +
    			" GROUP BY B.MEM_CODE";
//    	System.out.println("table1::=="+sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){
    		table1.setParmValue(parm);
    	}
    	
    }
    
    public void onTable2(String memCode){
    	String sql = "SELECT ROW_NUMBER () OVER (ORDER BY B.MR_NO DESC) AS SEQ, B.MEM_CODE, B.MR_NO," +
    			" C.PAT_NAME, A.MEM_CARD_NO, A.MEM_FEE, B.START_DATE, B.END_DATE" +
    			" FROM MEM_TRADE A, MEM_PATINFO B, SYS_PATINFO C" +
    			" WHERE A.MR_NO = B.MR_NO" +
    			" AND A.STATUS = 1" +
    			" AND A.REMOVE_FLG='N'" +
    			" AND B.END_DATE > SYSDATE" +
    			" AND B.MR_NO = C.MR_NO"+
    			" AND B.MEM_CODE IN("+memCode +")" +
    			" AND A.START_DATE BETWEEN TO_DATE ('"+date_s+"', 'YYYYMMDD') " +
				" AND  TO_DATE ('"+date_e+"', 'YYYYMMDD')" ;
//    	System.out.println(sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){
    		table2.setParmValue(parm);
    	}		
    }
    
    public void onQuery(){
    	onTable1();
    }
    
    public void onSave() throws ParseException{
    	if(this.getValueString("DEPRECIATION_DATE").equals("")){
    		this.messageBox("请选择折旧时间");
    		return;
    	}
    	String sql = "SELECT A.MR_NO, B.START_DATE, A.LAST_DEPRECIATION_END_DATE, B.END_DATE," +
    			" C.REMAINING_AMOUNT,C.DEPRECIATION_END_DATE,A.MEM_CARD_NO" +
    			" FROM MEM_TRADE A, MEM_PATINFO B, MEM_FEE_DEPRECIATION C" +
    			" WHERE A.MR_NO = B.MR_NO AND A.MR_NO = C.MR_NO" +
    			" AND A.MEM_CARD_NO = C.MEM_CARD_NO" +
    			" AND A.STATUS = 1" +
    			" AND A.REMOVE_FLG='N'" +
    			" AND B.END_DATE > SYSDATE";
//    			+
//    			" AND B.START_DATE BETWEEN TO_DATE ('"+date_s+"', 'YYYYMMDD') " +
//				" AND  TO_DATE ('"+date_e+"', 'YYYYMMDD')" ;
//    	System.out.println("onsave::=="+sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	TParm parmMemFeeD = new TParm();
    	TParm parmMemTrade = new TParm();
    	for(int i=0;i<parm.getCount();i++){   		
    		String sDate = this.getValueString("DEPRECIATION_DATE").replace("-", "/").substring(0, 10); //当前折旧时间
    		String eDate = parm.getValue("END_DATE", i).toString().replace("-", "/").substring(0, 10); //会员卡结束时间
    		String lDate = parm.getValue("DEPRECIATION_END_DATE", i).toString().replace("-", "/").substring(0,10); //最后一次折旧时间
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
    		if(sdf.parse(lDate).before(sdf.parse(sDate)) && sdf.parse(sDate).before(sdf.parse(eDate))){
    			int month = this.getMonthSpace(sDate, eDate);
        		double memFee = parm.getDouble("REMAINING_AMOUNT", i);
        		double dFee =Math.round(memFee/month*100)/100.0 ;
        		parmMemFeeD.addData("MR_NO",parm.getValue("MR_NO", i));
        		parmMemFeeD.addData("MEM_CARD_NO",parm.getValue("MEM_CARD_NO", i));
        		parmMemFeeD.addData("DEPRECIATION_FEE",dFee); //折旧费
        		parmMemFeeD.addData("DEPRECIATION_START_DATE",parm.getData("DEPRECIATION_END_DATE", i)); //折旧区间开始时间
        		parmMemFeeD.addData("DEPRECIATION_END_DATE",this.getValue("DEPRECIATION_DATE")); //折旧区间结束时间
        		parmMemFeeD.addData("REMAINING_AMOUNT",memFee-dFee); //该会员剩余会费
        		parmMemFeeD.addData("BEFORE_AMOUNT",memFee); //折旧前会费
        		parmMemFeeD.addData("STATUS",1); //状态：1折旧，2折旧抵冲
        		parmMemFeeD.addData("OPT_DATE",TJDODBTool.getInstance().getDBTime());
        		parmMemFeeD.addData("OPT_USER",Operator.getID());
        		parmMemFeeD.addData("OPT_TERM",Operator.getIP());
    		}else{
    			parmMemFeeD.addData("MR_NO",parm.getValue("MR_NO", i));
        		parmMemFeeD.addData("MEM_CARD_NO",parm.getValue("MEM_CARD_NO", i));
        		parmMemFeeD.addData("DEPRECIATION_FEE",parm.getValue("REMAINING_AMOUNT", i)); //折旧费
        		parmMemFeeD.addData("DEPRECIATION_START_DATE",parm.getData("DEPRECIATION_END_DATE", i)); //折旧区间开始时间
        		parmMemFeeD.addData("DEPRECIATION_END_DATE",parm.getData("END_DATE",i)); //折旧区间结束时间
        		parmMemFeeD.addData("REMAINING_AMOUNT",0); //该会员剩余会费
        		parmMemFeeD.addData("BEFORE_AMOUNT",parm.getValue("REMAINING_AMOUNT", i)); //折旧前会费
        		parmMemFeeD.addData("STATUS",1); //状态：1折旧，2折旧抵冲
        		parmMemFeeD.addData("OPT_DATE",TJDODBTool.getInstance().getDBTime());
        		parmMemFeeD.addData("OPT_USER",Operator.getID());
        		parmMemFeeD.addData("OPT_TERM",Operator.getIP());
    		}  
    		
    		parmMemTrade.addData("MR_NO", parm.getValue("MR_NO", i));
    		parmMemTrade.addData("MEM_CARD_NO", parm.getValue("MEM_CARD_NO", i));
    		parmMemTrade.addData("LAST_DEPRECIATION_END_DATE", parmMemFeeD.getData("DEPRECIATION_END_DATE", i));
    	} 
//    	System.out.println("parmMemFeeD::=="+parmMemFeeD);
    	TParm parmMem = new TParm();
    	parmMem.setData("parmMemTrade",parmMemTrade.getData());
    	parmMem.setData("parmMemFeeD",parmMemFeeD.getData());
    	
    	TParm result = TIOM_AppServer.executeAction("action.mem.MEMAction","updateMemFeeDepreciation",parmMem);
    	if(result.getErrCode()<0){
    		this.messageBox("折旧失败！");
    		return;
    	}
    	this.messageBox("折旧成功！");
    }
    
    /**
	 * 计算两个时间内相差的月数
	 * @param begin
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	public static int getMonthSpace(String begin, String end)
	throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date beginDate = df.parse(begin);
		Date endDate = df.parse(end);
		int beginYear = beginDate.getYear();
		int beginMonth = beginDate.getMonth();
		int endYear = endDate.getYear();
		int endMonth = endDate.getMonth();
		int difMonth = (endYear-beginYear)*12+(endMonth-beginMonth);
		return difMonth; 
	}
	
	public void onClear(){
		table1.removeRowAll();
		table2.removeRowAll();
		Timestamp date = StringTool.getTimestamp(new Date());
		 this.setValue("START_DATE",
				 StringTool.rollDate(date, -1).toString().substring(0, 10).replace('-', '/'));
		 this.setValue("END_DATE", date.toString()
					.substring(0, 10).replace('-', '/'));
		 String sql = "SELECT MONTH_CYCLE FROM BIL_SYSPARM WHERE ADM_TYPE='O'";
		 TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		 this.setValue("DEPRECIATION_DATE", date.toString()
					.substring(0, 8).replace('-', '/')+parm.getValue("MONTH_CYCLE", 0));
	}
}
