package com.javahis.ui.mem;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.bil.PaymentTool;
import jdo.mem.MEMInsureQueryTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>
 * Title: 保险患者信息查询
 * </p>
 * <p>
 * Company:Javahis
 * </p> 
 * @author liling
 * 2014-07-09
 */
public class MEMInsureQueryControl extends TControl{
	private static TTable table;
	PaymentTool paymentTool;
	private Pat  pat;//  病患信息
	int selectRow = -1;
	
	private BILComparator comparator=new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	
	public MEMInsureQueryControl(){
		super();
		selectRow = -1;
	}
	 /**
     * 初始化
     */
    public void onInit(){
    	 super.onInit();
    	table = (TTable) getComponent("TABLE");     	
    	onInitTable();    	
    	table.addEventListener("TABLE->"
	            + TTableEvent.CLICKED, this, "onTABLEClicked");
    	addListener(table);
    	TPanel p = (TPanel) getComponent("tPanel_1");
    	try {
			paymentTool = new PaymentTool(p, this);
			paymentTool.setHasAmt(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//onQuery();
    }
    
    /**
     * 初始化表格  add by huangtt 20150521
     */
    public void onInitTable(){
    	this.setValue("ADM_TYPE", "O");
    	admTypeAction();
		
		
		
	}
    
    public void admTypeAction(){
    	table.removeRowAll();
    	String admType=this.getValueString("ADM_TYPE");
    	String header="";
    	String column="";
    	String parmMap="";
    	TLabel tLabel = (TLabel) this.getComponent("tLabel_6");
    	if(!admType.equals("I")){
    		tLabel.setText("就诊日期:");
    		header = "欠费,50,boolean;欠费金额,100,double,#########0.00;病案号,80;姓名,100;" +
			"性别,40,SEX_CODE;出生日期,120,Timestamp,yyyy/MM/dd;保险公司,100,CONTRACTOR_CODE;保险卡号,100;保险单号,100;" +
			"支付类型,90,INSURE_PAY_TYPE;生效日,90,Timestamp,yyyy/MM/dd;失效日,90,Timestamp,yyyy/MM/dd;" +
			"就诊日期,90,Timestamp,yyyy/MM/dd;就诊科室,100,REALDEPT_CODE;看诊医生,100,REALDR_CODE;主诊断,150,ICD_CODE;" +
			"结算日期,150,Timestamp,yyyy/MM/dd HH:mm:ss;结算金额,100,double,#########0.00";
	    	column="1,right;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left;17,right";
	    	parmMap ="DEPT_FLG;AMT;MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;CONTRACTOR_CODE;INSURANCE_NUMBER;" +
					"INSURANCE_BILL_NUMBER;INSURE_PAY_TYPE;START_DATE;END_DATE;ADM_DATE;REALDEPT_CODE;REALDR_CODE;ICD_CODE;" +
					"BILL_DATE;AR_AMT";
			
			String sql = "SELECT A.GATHER_TYPE,B.CHN_DESC,A.PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE A,SYS_DICTIONARY B" +
					" WHERE B.GROUP_ID = 'GATHER_TYPE' AND A.GATHER_TYPE = B.ID";
			TParm payType = new TParm(TJDODBTool.getInstance().select(sql));
			for (int i = 0; i < payType.getCount(); i++) {
				header =header + ";" +payType.getValue("CHN_DESC", i)+",100,double,#########0.00";
				column = column + ";"+(18+i)+",right";
				parmMap = parmMap + ";" + payType.getValue("PAYTYPE", i);
			}
    	}else{
    		tLabel.setText("入院日期:");
    		header = "欠费,50,boolean;欠费金额,100,double,#########0.00;病案号,80;姓名,100;" +
			"性别,40,SEX_CODE;出生日期,120,Timestamp,yyyy/MM/dd;保险公司,100,CONTRACTOR_CODE;保险卡号,100;保险单号,100;" +
			"支付类型,90,INSURE_PAY_TYPE;生效日,90,Timestamp,yyyy/MM/dd;失效日,90,Timestamp,yyyy/MM/dd;" +
			"入院日期,90,Timestamp,yyyy/MM/dd;入院科室,100,REALDEPT_CODE;经治医生,100,REALDR_CODE;出院诊断,150,ICD_CODE;" +
			"结算日期,150,Timestamp,yyyy/MM/dd HH:mm:ss;住院天数,60;结算金额,100,double,#########0.00;现金,100,double,#########0.00;" +
			"医疗卡,100,double,#########0.00;银行卡,100,double,#########0.00;医保卡,100,double,#########0.00;" +
			"支票,100,double,#########0.00;应收金额,100,double,#########0.00;预交金,100,double,#########0.00;" +
			"套内金额,100,double,#########0.00;套外金额,100,double,#########0.00;减免金额,100,double,#########0.00";
			column="1,right;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left;" +
					"17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right;26,right;27,right;28,right";
			parmMap ="DEPT_FLG;AMT;MR_NO;PAT_NAME;" +
					"SEX_CODE;BIRTH_DATE;CONTRACTOR_CODE;INSURANCE_NUMBER;INSURANCE_BILL_NUMBER;" +
					"INSURE_PAY_TYPE;START_DATE;END_DATE;" +
					"IN_DATE;REALDEPT_CODE;REALDR_CODE;ICD_CODE;" +
					"BILL_DATE;ADM_DAYS;AR_AMT;PAY_CASH;" +
					"PAY_MEDICAL_CARD;PAY_BANK_CARD;PAY_INS_CARD;" +
					"PAY_CHECK;PAY_DEBIT;PAY_BILPAY;" +
					"LUMPWORK_AMT;LUMPWORK_OUT_AMT;REDUCE_AMT";
    	}
    	
    	table.setHeader(header);
		table.setItem("CONTRACTOR_CODE;SEX_CODE;INSURE_PAY_TYPE;REALDEPT_CODE;REALDR_CODE");
		table.setParmMap(parmMap);
		table.setColumnHorizontalAlignmentData(column);
		
		onQuery();
    }
    
  
    /**
	 * 查询
	 */
	public void onQuery(){
		if(this.getValue("ADM_TYPE").toString().equals("")){
			this.messageBox("门急住别不可为空");
			return;
		}
		table.acceptText();
		 TParm parm = getParmForTag("START_DATE;END_DATE;ADM_TYPE;CONTRACTOR_CODE;INSURANCE_NUMBER;MR_NO;DEPT_FLG", true);
//		 TParm data = MEMInsureQueryTool.getInstance().selectdata(parm);
//		 System.out.println("parm=="+parm);
		 TParm data=new TParm();
		 if(!this.getValue("ADM_TYPE").toString().equals("I")){
	     	 data = select(parm);
		 }else{
			 data = selectI(parm);
		 }
	        if (data.getErrCode() < 0) {
	            messageBox(data.getErrText());
	            return;
	        }
	        
	     table.setParmValue(data);
	       
//	     ((TTextField) getComponent("LUMPWORK_CODE")).setEnabled(true);	
	}
	public void onQueryNO(){
    	pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
        if (pat == null) {
            this.messageBox("无此病案号!");
            this.grabFocus("PAT_NAME");
            return;
        }
        String mrNo=PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
        this.setValue("MR_NO", mrNo);
        onQuery();
        }
	/**
	 * 保存
	 */
	public void onSave(){
		TParm parm = getParmForTag("CONTRACTOR_CODE;INSURANCE_NUMBER;MR_NO;DEPT_FLG");		
		if(selectRow == -1){
			this.messageBox("没有选中数据");
			return;
		}
		//System.out.println(selectRow );
		TParm parm11= new TParm();
		try {
			parm11 = paymentTool.getAmts();
//			System.out.println("parm11======="+parm11);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int j=1;j<11;j++){
				parm.setData("PAY_TYPE0"+j, "");
				parm.setData("MEMO"+j, "");
		}		
		for(int i=0;i<parm11.getCount();i++){
			parm.setData(parm11.getValue("PAY_TYPE", i), parm11.getData("AMT", i));
			String dex=parm11.getValue("PAY_TYPE", i).substring(9, parm11.getValue("PAY_TYPE", i).length());
			parm.setData("MEMO"+dex, parm11.getData("REMARKS", i));
		}
		TParm payParm = paymentTool.table.getParmValue();
    	int payCount = payParm.getCount();
    	String payType = "";
    	String memo ="";
    	double amt = 0.00;
    	double tot =0.00;
    	double totamt= 0.00;  	
    	for (int i = 0; i <= payCount; i++) {
    		payType = paymentTool.table.getItemString(i, "PAY_TYPE");
			amt = paymentTool.table.getItemDouble(i, "AMT");
			if(amt > 0 && ("".equals(payType)||payType==null)){
				this.messageBox("存在未设定支付方式的金额,请填写！");
				return;
			}
			tot += paymentTool.table.getItemDouble(i, "AMT");
//			totamt= this.getValueDouble("AMT")-tot;
//			 if(totamt<0){
//	        	 this.messageBox("超出应收金额");
//	        	 paymentTool.table.setItem(i, "AMT", amt+totamt);
//	        	 return;
//	         }
//			for(int j=0;j<parm0.getCount();j++){
//				payType0=parm0.getValue("GATHER_TYPE", j);
//				if(payType.equals(payType0)){
//					parm.setData(parm0.getValue("PAYTYPE", j),amt);
//					parm.setData("MEMO"+j,memo);					
//			}
//		}   
			 }
//    	if(tot<=0){
//    		this.messageBox("没有缴费");
//    		return;
//    	}
    	 totamt= this.getValueDouble("AMT")-tot;    	
    	 TParm parm1 = new TParm(TJDODBTool.getInstance().select("SELECT COUNT(*) AS COUNT FROM MEM_INSURANCE_BILL_LOG"));
    	 parm.setData("ID",Integer.parseInt(parm1.getValue("COUNT", 0)));
    	 parm.setData("OPT_USER", Operator.getID());
         parm.setData("OPT_DATE", SystemTool.getInstance().getDate().toString().substring(0, 19));
         parm.setData("OPT_TERM", Operator.getIP());
         parm.setData("BILL_AMT",tot);        
         TParm result = MEMInsureQueryTool.getInstance().insertdata(parm);
         if (result.getErrCode() < 0) {
             messageBox(result.getErrText());
             return;
         }        
         this.messageBox("P0001");
         memo=this.getValueString("MEMO");
         String sql1="";
         String sql2="";
         if(totamt>0){
	          sql1="UPDATE MEM_INSURE_INFO SET DEPT_FLG='"+parm.getValue("DEPT_FLG")+"', MEMO='"
				 +memo+"',AMT="+totamt+" WHERE MR_NO='"+parm.getValue("MR_NO")+
				 "' AND CONTRACTOR_CODE='"+parm.getValue("CONTRACTOR_CODE")+"' " +
				 "AND INSURANCE_NUMBER='"+parm.getValue("INSURANCE_NUMBER")+"'";
	          sql2="UPDATE SYS_PATINFO SET  DEPT_FLG='"+parm.getValue("DEPT_FLG")+"' WHERE MR_NO='"+parm.getValue("MR_NO")+"'";
         }else{
        	  sql1="UPDATE MEM_INSURE_INFO SET DEPT_FLG='N', MEMO='"
			 +memo+"',AMT="+totamt+" WHERE MR_NO='"+parm.getValue("MR_NO")+
			 "' AND CONTRACTOR_CODE='"+parm.getValue("CONTRACTOR_CODE")+"' " +
			 "AND INSURANCE_NUMBER='"+parm.getValue("INSURANCE_NUMBER")+"'";
          sql2="UPDATE SYS_PATINFO SET  DEPT_FLG='N' WHERE MR_NO='"+parm.getValue("MR_NO")+"'";
         }
         TJDODBTool.getInstance().update(sql1);
         TJDODBTool.getInstance().update(sql2);
         onClear();
         this.setValue("ADM_TYPE", "O");
         onQuery();
	}
	/**
	 * 汇出Excel
	 */
	public void onExport() {
        if(table.getRowCount()<=0){
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "保险患者信息表");
    }
	/**
	 * 清空
	 */
	public void onClear() {//;DEPT_FLG
		this.clearValue("CONTRACTOR_CODE;INSURANCE_NUMBER;MR_NO;PAT_NAME;AMT;MEMO;GATHER_TYPE;START_DATE;END_DATE;ADM_TYPE");
		paymentTool.onClear();
		((TTable)this.getComponent("TABLE")).removeRowAll();
		getCheckBox("DEPT_FLG").setSelected(true);
		selectRow = -1;
	}
	 /**
     * 获得TCheckBox
     */
    private TCheckBox getCheckBox(String tagName){
    	return (TCheckBox)getComponent(tagName);
    }
    /**
	 * 表格单击事件
	 */
	public void onTABLEClicked(int row) {
		 // 选中行
        if (row < 0)
            return;
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        setValueForParm(
            "CONTRACTOR_CODE;INSURANCE_NUMBER;MR_NO;PAT_NAME;DEPT_FLG;AMT;MEMO",
            data, row);    
        paymentTool.setAmt(this.getValueDouble("AMT"));
        selectRow = row;
	}
	
	/**
	 * 门急诊
	 * @param parm
	 * @return
	 */
	public TParm select(TParm parm){
		String sql = "SELECT M.DEPT_FLG,M.AMT,M.MR_NO,S.PAT_NAME,S.SEX_CODE,S.BIRTH_DATE, M.CONTRACTOR_NAME," +
				" M.INSURANCE_NUMBER,M.INSURANCE_BILL_NUMBER,M.INSURE_PAY_TYPE,M.START_DATE," +
				" M.END_DATE,M.MEMO, M.CONTRACTOR_CODE,M.OPT_USER,M.OPT_TERM,M.OPT_DATE,A.ADM_DATE,A.REALDEPT_CODE,A.REALDR_CODE,A.ICD_CHN_DESC ICD_CODE,A.BILL_DATE," +
				" A.PAY_TYPE01,A.PAY_TYPE02,A.PAY_TYPE03,A.PAY_TYPE04,A.PAY_TYPE05,A.PAY_TYPE06," +
				" A.PAY_TYPE07,A.PAY_TYPE08,A.PAY_TYPE09,A.PAY_TYPE10,A.AR_AMT " +
				" FROM MEM_INSURE_INFO M,SYS_PATINFO S," +
				" (  SELECT A.INSURE_INFO CONTRACTOR_CODE, A.MR_NO, A.ADM_DATE,A.REALDEPT_CODE,A.REALDR_CODE,E.ICD_CHN_DESC,B.BILL_DATE,SUM (B.AR_AMT) AR_AMT,SUM (B.PAY_TYPE01) PAY_TYPE01," +
				" SUM (B.PAY_TYPE02) PAY_TYPE02,SUM (B.PAY_TYPE03) PAY_TYPE03,SUM (B.PAY_TYPE04) PAY_TYPE04," +
				" SUM (B.PAY_TYPE05) PAY_TYPE05, SUM (B.PAY_TYPE06) PAY_TYPE06, SUM (B.PAY_TYPE07) PAY_TYPE07," +
				" SUM (B.PAY_TYPE08) PAY_TYPE08, SUM (B.PAY_TYPE09) PAY_TYPE09, SUM (B.PAY_TYPE10) PAY_TYPE10" +
				" FROM REG_PATADM A, BIL_OPB_RECP B,OPD_DIAGREC D,SYS_DIAGNOSIS E" +
				" WHERE     A.CASE_NO = B.CASE_NO(+) AND A.REGCAN_USER IS NULL   " +
				" AND A.CASE_NO = D.CASE_NO AND A.SEEN_DR_TIME IS NOT NULL AND D.MAIN_DIAG_FLG='Y' " +
				" AND D.ICD_CODE=E.ICD_CODE AND D.ICD_TYPE=E.ICD_TYPE  " ;
		if(!("".equals(parm.getValue("MR_NO")) || "null".equals(parm.getValue("MR_NO")))){
			sql += " AND A.MR_NO = '"+parm.getValue("MR_NO")+"'";
		}
		//=========================添加查询条件 add by huangjw 20150803 start
		if(!(("".equals(parm.getValue("START_DATE")) || "null".equals(parm.getValue("START_DATE"))) && ("".equals(parm.getValue("END_DATE")) || "null".equals(parm.getValue("END_DATE"))))){
			sql += " AND A.ADM_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE").toString().substring(0,10).replaceAll("-", "/")+"','yyyy/MM/dd') " +
					" AND TO_DATE('"+parm.getValue("END_DATE").toString().substring(0,10).replaceAll("-", "/")+"','yyyy/MM/dd') ";
		}
		if(!("".equals(parm.getValue("ADM_TYPE")) || "null".equals(parm.getValue("ADM_TYPE")))){
			sql += " AND A.ADM_TYPE = '"+parm.getValue("ADM_TYPE")+"'";
		}
		
		//=========================添加查询条件 add by huangjw 20150803 end 
		sql +=	" GROUP BY A.MR_NO, A.ADM_DATE,A.REALDEPT_CODE,A.REALDR_CODE,E.ICD_CHN_DESC,B.BILL_DATE,A.INSURE_INFO ORDER BY A.ADM_DATE) A " +
				" WHERE M.CONTRACTOR_CODE = A.CONTRACTOR_CODE AND M.MR_NO = S.MR_NO" +
				//" AND M.VALID_FLG = 'Y'" +				
				" AND M.MR_NO = A.MR_NO" ;
		if(!("".equals(parm.getValue("MR_NO")) || "null".equals(parm.getValue("MR_NO")))){
			sql += " AND M.MR_NO = '"+parm.getValue("MR_NO")+"'";
		}
		if(!("".equals(parm.getValue("DEPT_FLG")) || "null".equals(parm.getValue("DEPT_FLG")))){
			sql += " AND M.DEPT_FLG = '"+parm.getValue("DEPT_FLG")+"'";//欠费
		}
		if(!("".equals(parm.getValue("CONTRACTOR_CODE")) || "null".equals(parm.getValue("CONTRACTOR_CODE")))){
			sql += " AND M.CONTRACTOR_CODE = '"+parm.getValue("CONTRACTOR_CODE")+"'";
		}
		if(!("".equals(parm.getValue("INSURANCE_NUMBER")) || "null".equals(parm.getValue("INSURANCE_NUMBER")))){
			sql += " AND M.INSURANCE_NUMBER = '"+parm.getValue("INSURANCE_NUMBER")+"'";
		}
		
		sql +=	" ORDER BY M.START_DATE";
		//System.out.println("sql===="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm selectI(TParm parm){
		String sql="SELECT M.DEPT_FLG,M.AMT,M.MR_NO,S.PAT_NAME,S.SEX_CODE,S.BIRTH_DATE, M.CONTRACTOR_NAME," +
				" M.INSURANCE_NUMBER,M.INSURANCE_BILL_NUMBER,M.INSURE_PAY_TYPE,M.START_DATE," +
				" M.END_DATE,M.MEMO, M.CONTRACTOR_CODE,M.OPT_USER,M.OPT_TERM,M.OPT_DATE,A.IN_DATE,A.REALDEPT_CODE,A.REALDR_CODE,A.ICD_CHN_DESC ICD_CODE,A.BILL_DATE,A.ADM_DAYS," +
				" A.PAY_CASH,A.PAY_MEDICAL_CARD,A.PAY_BANK_CARD,A.PAY_INS_CARD,A.PAY_CHECK,A.PAY_DEBIT," +
				" A.PAY_BILPAY,A.LUMPWORK_AMT,A.LUMPWORK_OUT_AMT,A.REDUCE_AMT,A.AR_AMT " +
				" FROM MEM_INSURE_INFO M,SYS_PATINFO S," +
				" (SELECT A.INSURE_INFO CONTRACTOR_CODE,A.MR_NO, A.IN_DATE,A.VS_DR_CODE REALDR_CODE,A.IN_DEPT_CODE REALDEPT_CODE,E.ICD_CHN_DESC,A.BILL_DATE,A.ADM_DAYS, SUM (B.AR_AMT) AR_AMT,SUM (B.PAY_CASH) PAY_CASH," +
				" SUM (B.PAY_MEDICAL_CARD) PAY_MEDICAL_CARD,SUM (B.PAY_BANK_CARD) PAY_BANK_CARD,SUM (B.PAY_INS_CARD) PAY_INS_CARD," +
				" SUM (B.PAY_CHECK) PAY_CHECK, SUM (B.PAY_DEBIT) PAY_DEBIT, SUM (B.PAY_BILPAY) PAY_BILPAY," +
				" SUM (B.LUMPWORK_AMT) LUMPWORK_AMT, SUM (B.LUMPWORK_OUT_AMT) LUMPWORK_OUT_AMT, SUM (B.REDUCE_AMT) REDUCE_AMT" +
				" FROM ADM_INP A, BIL_IBS_RECPM B,ADM_INPDIAG D,SYS_DIAGNOSIS E" +
				" WHERE     A.CASE_NO = B.CASE_NO(+) AND A.DS_DATE IS NOT NULL  " +
				" AND A.CASE_NO=D.CASE_NO AND D.MAINDIAG_FLG='Y' AND D.IO_TYPE='I' " +
				" AND D.ICD_CODE=E.ICD_CODE AND D.ICD_TYPE=E.ICD_TYPE";
		
		if(!("".equals(parm.getValue("MR_NO")) || "null".equals(parm.getValue("MR_NO")))){
			sql += " AND A.MR_NO = '"+parm.getValue("MR_NO")+"'";
		}
		
		if(!(("".equals(parm.getValue("START_DATE")) || "null".equals(parm.getValue("START_DATE"))) && ("".equals(parm.getValue("END_DATE")) || "null".equals(parm.getValue("END_DATE"))))){
			sql += " AND A.IN_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE").toString().substring(0,10).replaceAll("-", "/")+"','yyyy/MM/dd') " +
					" AND TO_DATE('"+parm.getValue("END_DATE").toString().substring(0,10).replaceAll("-", "/")+"','yyyy/MM/dd') ";
		}
		
		sql +=	" GROUP BY A.MR_NO, A.IN_DATE ,A.IN_DEPT_CODE,A.VS_DR_CODE,E.ICD_CHN_DESC,A.BILL_DATE,A.ADM_DAYS,A.INSURE_INFO ORDER BY A.IN_DATE) A " +
		" WHERE M.CONTRACTOR_CODE = A.CONTRACTOR_CODE AND M.MR_NO = S.MR_NO" +
		//" AND M.VALID_FLG = 'Y'" +				
		" AND M.MR_NO = A.MR_NO" ;
		if(!("".equals(parm.getValue("MR_NO")) || "null".equals(parm.getValue("MR_NO")))){
			sql += " AND M.MR_NO = '"+parm.getValue("MR_NO")+"'";
		}
		if(!("".equals(parm.getValue("DEPT_FLG")) || "null".equals(parm.getValue("DEPT_FLG")))){
			sql += " AND M.DEPT_FLG = '"+parm.getValue("DEPT_FLG")+"'";//欠费
		}
		if(!("".equals(parm.getValue("CONTRACTOR_CODE")) || "null".equals(parm.getValue("CONTRACTOR_CODE")))){
			sql += " AND M.CONTRACTOR_CODE = '"+parm.getValue("CONTRACTOR_CODE")+"'";
		}
		if(!("".equals(parm.getValue("INSURANCE_NUMBER")) || "null".equals(parm.getValue("INSURANCE_NUMBER")))){
			sql += " AND M.INSURANCE_NUMBER = '"+parm.getValue("INSURANCE_NUMBER")+"'";
		}
		sql +=	" ORDER BY M.START_DATE";
		//System.out.println(""+sql);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 * 加入表格排序监听方法
	 * @param table TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				int i = table.getTable().columnAtPoint(me.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = table.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				comparator.setDes(ascending);
				comparator.setCol(col);
				java.util.Collections.sort(vct, comparator);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				//getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * 得到 Vector 值
	 * @param parm TParm
	 * @param group String
	 * @param names String
	 * @param size int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	/**
	 * 转换parm中的列
	 * @param columnName String[]
	 * @param tblColumnName String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}
		return index;
	}
	
	/**
	 * vectory转成param
	 * @param vectorTable Vector
	 * @param parmTable TParm
	 * @param columnNames String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
}
