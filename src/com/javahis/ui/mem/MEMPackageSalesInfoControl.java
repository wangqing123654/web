package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jdo.ekt.EKTIO;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title:�ײ͹���/�˷Ѳ�ѯ </p>
 *
 * <p>Description:�ײ͹���/�˷Ѳ�ѯ  </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author duzhw 2014.01.08
 * @version 4.5
 */
public class MEMPackageSalesInfoControl extends TControl {
	
	//�ײͽ��ױ�;ʱ���ײͱ�;�ײ���ϸ��
	private TTable tradeTable,sectionTable,detailTable;
	
	Pat pat;
	
	//���ڸ�ʽ��
	private SimpleDateFormat formateDate=new SimpleDateFormat("yyyy/MM/dd");
	
	//�ѹ���/���˷ѵ�ѡ��ť
	TRadioButton isBuy;
	TRadioButton isReFee;
	
	private TParm parmEKT; // ҽ�ƿ���Ϣ add by sunqy 20140707
	
	 /**
     * ��ʼ��
     */
    public void onInit() { // ��ʼ������
        super.onInit();
        initComponent();
        initData();
    }
    
    /**
	 * ��ʼ���ؼ�
	 */
    private void initComponent() {
    	tradeTable = getTable("TRADE_TABLE");
    	sectionTable = getTable("SECTION_TABLE");
    	detailTable = getTable("DETAIL_TABLE");
    	isBuy = (TRadioButton) this.getComponent("isBuy");
    	isReFee = (TRadioButton) this.getComponent("isReFee");
    }
    
    /**
     * ��ʼ������   
     */
    private void initData(){
    	//��ѯʱ��Ĭ�ϵ���
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
//   	 	this.setValue("START_DATE",
//		 		now.toString().substring(0, 10).replace('-', '/'));
   	 	this.setValue("END_DATE",
		 		now.toString().substring(0, 10).replace('-', '/'));
   	 	//��ʼʱ��Ϊ�ϸ���-Ĭ��
   	 	Calendar cd = Calendar.getInstance();
   	 	cd.setTime(now);
   	 	cd.add(Calendar.YEAR, -1);
   	 	String format = formateDate.format(cd.getTime());
		this.setValue("START_DATE", format);
   	 	//����������Ч
   	 	callFunction("UI|MR_NO|setEnabled", true);  //������
   	 	//��������
   	 	Object obj = this.getParameter();
   	 	if (obj instanceof TParm) {
   	 		TParm acceptData = (TParm) obj;
   	 		String mrNo = acceptData.getData("MR_NO").toString();
   	 		if(mrNo.length() == 0 || mrNo == null)
   	 			return;
   	 		this.setValue("MR_NO", mrNo);
   	 		this.onMrno();
   	 		this.onQuery();
   	 	}
    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {
    	if(isBuy.isSelected()){
    		onClear2();
    		onQuery1();//�����ײͲ�ѯ
    	}
//    	else if(isReFee.isSelected()){
//    		onClear2();
//    		onQuery2();//�˷Ѳ�ѯ
//    	}
    	if(isReFee.isSelected()){
    		onClear2();
    		onQuery2();//�˷Ѳ�ѯ
    	}
    }
    
    /**
     * �ײ͹����ѯ
     */
    public void onQuery1() {
    	//���ݼ��--�������ûҲ���������
    	if(!checkData()){
    		return;
    	}
    	String mrNo = this.getValue("MR_NO").toString();
    	String operator = "";//�տ���add by sunqy 20140724
    	String startDate = this.getValueString("START_DATE");
    	if(startDate.length()>0){
    		startDate = startDate.toString().replaceAll("-", "/").substring(0, 10);
    	}
    	String endDate = this.getValueString("END_DATE");
    	if(endDate.length()>0){
    		endDate = endDate.toString().replaceAll("-", "/").substring(0, 10);
    	}
    	
    	String sql = "SELECT ROWNUM AS ID, A.*,C.PAT_NAME, B.PACKAGE_CODE FROM MEM_PACKAGE_TRADE_M A," +
    			" (SELECT TRADE_NO,PACKAGE_CODE FROM MEM_PAT_PACKAGE_SECTION GROUP BY TRADE_NO ,PACKAGE_CODE) B,SYS_PATINFO C " +
    			" WHERE A.TRADE_NO=B.TRADE_NO AND A.MR_NO = C.MR_NO ";
    	if(null != mrNo && mrNo.length()>0){//add by sunqy 20140724
    		sql += " AND A.MR_NO = '"+mrNo+"' ";
    	}
    	if(null != getValueString("OPERATOR") && getValueString("OPERATOR").length()>0){//add by sunqy 20140724
    		operator = getValueString("OPERATOR");
    		sql += " AND A.CASHIER_CODE = '"+operator+"' ";
    	}
    	if(startDate.length()>0 && endDate.length()>0){
    		sql += " AND A.BILL_DATE >= TO_DATE('"+startDate+" 000000"+"','YYYY/MM/DD HH24MISS') AND " +
    				" A.BILL_DATE <= TO_DATE('"+endDate+" 235959"+"','YYYY/MM/DD HH24MISS')";
    	}
    	
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount()<=0){
    		this.messageBox("û�в�ѯ������,��������д��Ϣ");
    		onClear();
    	}else{
    		tradeTable.setParmValue(result);
    		callFunction("UI|OPERATOR|setEnabled", false);//�������û�add by sunqy 20130724
    	}
    }
    
    /**
     * �ײ��˷Ѳ�ѯ
     */
    public void onQuery2() {
    	//���ݼ��--�������ûҲ���������
    	if(!checkData()){
    		return;
    	}
    	String mrNo = this.getValueString("MR_NO");//ɾ��toString������ֿ�ָ�� modified by lich
    	String operator = this.getValueString("OPERATOR");//�տ���add by sunqy 20140724
    	String startDate = this.getValueString("START_DATE");
    	if(startDate.length()>0){
    		startDate = startDate.toString().replaceAll("-", "/").substring(0, 10);
    	}
    	String endDate = this.getValueString("END_DATE");
    	if(endDate.length()>0){
    		endDate = endDate.toString().replaceAll("-", "/").substring(0, 10);
    	}
    	
    	String sql = "SELECT ROWNUM AS ID,A.*,C.PAT_NAME, B.PACKAGE_CODE FROM MEM_PACKAGE_TRADE_M A, " +
    			" (SELECT TRADE_NO,PACKAGE_CODE,REST_TRADE_NO FROM MEM_PAT_PACKAGE_SECTION GROUP BY TRADE_NO ,PACKAGE_CODE,REST_TRADE_NO) B,SYS_PATINFO C " +
    			" WHERE A.TRADE_NO=B.REST_TRADE_NO AND A.REST_FLAG = 'Y' AND A.MR_NO=C.MR_NO";
    	if(mrNo.length()>0){//add by sunqy 20140724
    		sql += " AND A.MR_NO = '"+mrNo+"' ";
    	}
    	if(operator.length()>0){//add by sunqy 20140724
    		sql += " AND A.OPT_USER = '"+operator+"' ";
    	}
    	if(startDate.length()>0 && endDate.length()>0){
    		sql += " AND A.OPT_DATE >= TO_DATE('"+startDate+" 000000"+"','YYYY/MM/DD HH24MISS') AND " +
    				" A.OPT_DATE <= TO_DATE('"+endDate+" 235959"+"','YYYY/MM/DD HH24MISS')";
    	}
    	//System.out.println(sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount()<=0){
    		this.messageBox("û�в�ѯ������,��������д��Ϣ");
    		onClear();
    	}else{
    		tradeTable.setParmValue(result);
//    		System.out.println(result);
    		callFunction("UI|OPERATOR|setEnabled", false);//�������û�add by sunqy 20130724
    	}
    }
    
    /**
     * �����ײͱ����ױ������¼�
     */
    public void onClickTradeTable() {
    	if(isBuy.isSelected()){
    		onClickTradeTable1();
    	}else if(isReFee.isSelected()){
    		onClickTradeTable2();
    	}
    	
    }
    /**
     * �ѹ��򣨹����ײͱ����ױ������¼���
     */
    public void onClickTradeTable1() {
    	int selectedIndx=tradeTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	//�����ϸ��
    	TParm parm = new TParm();
    	detailTable.setParmValue(parm);
    	
    	TParm tradeTableParm=tradeTable.getParmValue();
    	String tradeNO = tradeTableParm.getValue("TRADE_NO",selectedIndx);
    	String packageCode = tradeTableParm.getValue("PACKAGE_CODE",selectedIndx);
    	String mrNo = tradeTableParm.getValue("MR_NO",selectedIndx);
    	//ҳ�潻����Ϣ��ֵ
    	String sqlTrade = "SELECT * FROM MEM_PACKAGE_TRADE_M WHERE TRADE_NO = '"+tradeNO+"'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sqlTrade));
    	//TParm newresult=getEndDate(packageCode);
    	if(result.getCount()>0){
    		//ҳ����Ϣ
    		setDataForTrade(result);
    	}
    	//��ѯʱ���ײ�sql
    	String sql = "SELECT CASE USED_FLG WHEN '0' THEN 'N' WHEN '1' THEN 'Y' END USED_FLG,ROWNUM AS ID1," +
    			" ID, TRADE_NO, PACKAGE_CODE, SECTION_CODE," +  //add by huangtt 2014024
    			" CASE_NO, MR_NO, PACKAGE_DESC, SECTION_DESC, ORIGINAL_PRICE," + //add by huangtt 2014024
    			" SECTION_PRICE, AR_AMT, DESCRIPTION, REST_TRADE_NO " + //add by huangtt 2014024
    			" FROM MEM_PAT_PACKAGE_SECTION A " +
    			" WHERE  A.TRADE_NO = '"+tradeNO+"' AND A.MR_NO = '"+mrNo+"' " +
    			" AND A.PACKAGE_CODE = '"+packageCode+"' ";
    	result = new TParm(TJDODBTool.getInstance().select(sql));
    	sectionTable.setParmValue(result);
    }
   /* *//**
     * ��ȡ��Ч�� add by huangjw 20141107
     * @param packageCode
     * @return
     *//*
    public TParm getEndDate(String tradeNo){
    	TParm newresult=new TParm(TJDODBTool.getInstance().select("SELECT START_DATE,END_DATE" +
    			" FROM MEM_PACKAGE_TRADE_M WHERE TRADE_NO='"+tradeNo+"'"));
    	return newresult;
    }
    */
    /**
     * ���˷ѣ��ײͱ����ױ������¼���
     */
    public void onClickTradeTable2() {
    	int selectedIndx=tradeTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	//�����ϸ��
    	TParm parm = new TParm();
    	detailTable.setParmValue(parm);
    	
    	TParm tradeTableParm=tradeTable.getParmValue();
    	String tradeNO = tradeTableParm.getValue("TRADE_NO",selectedIndx);
    	String packageCode = tradeTableParm.getValue("PACKAGE_CODE",selectedIndx);
    	String mrNo = tradeTableParm.getValue("MR_NO",selectedIndx);
    	//ҳ�潻����Ϣ��ֵ
    	String sqlTrade = "SELECT * FROM MEM_PACKAGE_TRADE_M WHERE TRADE_NO = '"+tradeNO+"'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sqlTrade));
    	//TParm newresult=getEndDate(packageCode);
    	if(result.getCount()>0){
    		//ҳ����Ϣ
    		setDataForTrade(result);
    	}
    	//��ѯʱ���ײ�sql
    	String sql = "SELECT CASE USED_FLG WHEN '0' THEN 'N' WHEN '1' THEN 'Y' END USED_FLG,ROWNUM AS ID1," +
    			" A.* FROM MEM_PAT_PACKAGE_SECTION A " +
    			" WHERE  A.REST_TRADE_NO = '"+tradeNO+"' AND A.MR_NO = '"+mrNo+"' " +
    			" AND A.PACKAGE_CODE = '"+packageCode+"' ";
    	result = new TParm(TJDODBTool.getInstance().select(sql));
    	sectionTable.setParmValue(result);
    }
    /**
     * �ײ�ʱ�̱����¼�
     */
    public void onClickSectionTable() {
    	int selectedIndx=sectionTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	TParm sectionTableParm=sectionTable.getParmValue();
    	String tradeNO = sectionTableParm.getValue("TRADE_NO",selectedIndx);
    	String packageCode = sectionTableParm.getValue("PACKAGE_CODE",selectedIndx);
    	String sectionCode = sectionTableParm.getValue("SECTION_CODE", selectedIndx);
    	String mrNo = sectionTableParm.getValue("MR_NO",selectedIndx);
    	//��ѯ��ϸ�ײ�sql
//    	String sql = "SELECT CASE USED_FLG WHEN '0' THEN 'N' WHEN '1' THEN 'Y' END USED_FLG,ROWNUM AS ID1," +
//    			" A.* FROM MEM_PAT_PACKAGE_SECTION_D A " +
//    			" WHERE A.TRADE_NO = '"+tradeNO+"' AND A.PACKAGE_CODE = '"+packageCode+"' " +
//    			" AND A.SECTION_CODE = '"+sectionCode+"' AND A.MR_NO = '"+mrNo+"' AND HIDE_FLG = 'N'";
    	String sql = "SELECT 'N' AS EXEC,CASE USED_FLG WHEN '0' THEN 'N' WHEN '1' THEN 'Y' END USED_FLG,"
    			+ "(CASE C.ACTIVE_FLG WHEN 'N' THEN 'Y' ELSE 'N' END ) AS ACTIVE_FLG,ROWNUM AS ID1," +
		" A.ID,A.TRADE_NO,A.PACKAGE_CODE,A.SECTION_CODE,A.PACKAGE_DESC,A.SECTION_DESC,A.CASE_NO,A.MR_NO,A.SEQ," +
		" A.ORDER_CODE,A.ORDER_DESC,A.ORDER_NUM,A.UNIT_CODE," +
		" B.UNIT_PRICE,B.RETAIL_PRICE,A.RETAIL_PRICE AS PAY_PRICE,A.DESCRIPTION,A.OPT_DATE," +
		" A.OPT_USER,A.OPT_TERM,A.SETMAIN_FLG,A.ORDERSET_CODE,A.ORDERSET_GROUP_NO,A.HIDE_FLG,A.REST_TRADE_NO " +
		" FROM MEM_PAT_PACKAGE_SECTION_D A,MEM_PACKAGE_SECTION_D B ,SYS_FEE C" +
		" WHERE  A.TRADE_NO = '"+tradeNO+"' AND A.PACKAGE_CODE = '"+packageCode+"' " +
		" AND A.PACKAGE_CODE = B.PACKAGE_CODE(+) AND A.SECTION_CODE = B.SECTION_CODE(+) AND A.ORDER_CODE = C.ORDER_CODE(+)" +
		//" AND A.UNIT_PRICE = B.UNIT_PRICE " +
		" AND A.ORDERSET_GROUP_NO=B.ORDERSET_GROUP_NO(+) " +
		" AND A.SECTION_CODE = '"+sectionCode+"' AND A.MR_NO = '"+mrNo+"' " +
		" AND A.HIDE_FLG = 'N' AND B.HIDE_FLG(+) = 'N'" +
		" ORDER BY A.ID ";
    	System.out.println("-�ײ͹���/�˷�-�ײ�ʱ�̱����¼�--sql="+sql);  
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	detailTable.setParmValue(result);
    }
    
    /**
     * ���
     */
    public void onClear() {
    	//����������Ч
   	 	callFunction("UI|MR_NO|setEnabled", true);  //������
   	 	callFunction("UI|OPERATOR|setEnabled", true);//������add by sunqy 20130724
   	 	this.clearValue("MR_NO;PAT_NAME;INTRODUCER1;INTRODUCER2;INTRODUCER3;DISCOUNT_REASON;" +
   	 			"DISCOUNT_APPROVER;DISCOUNT_TYPE;ORIGINAL_PRICE;RETAIL_PRICE;AR_AMT;DESCRIPTION;OPERATOR");
   	 	//��ʼ����ʱ��
   	 	Timestamp now = StringTool.getTimestamp(new Date());
//   	 	this.setValue("START_DATE",
//   	 			now.toString().substring(0, 10).replace('-', '/'));
   	 	this.setValue("END_DATE",
   	 			now.toString().substring(0, 10).replace('-', '/'));
   	 	//��ʼʱ��Ϊ�ϸ���-Ĭ��
   	 	Calendar cd = Calendar.getInstance();
   	 	cd.add(Calendar.MONTH, -1);
   	 	String format = formateDate.format(cd.getTime());
		this.setValue("START_DATE", format);
   	 	//��ձ�
   	 	tradeTable.removeRowAll();
   	 	sectionTable.removeRowAll();
   	 	detailTable.removeRowAll();
    }
    

    /**
     * ���2
     */
    public void onClear2() {
    	//����������Ч
   	 	//callFunction("UI|MR_NO|setEnabled", true);  //������
   	 	this.clearValue("INTRODUCER1;INTRODUCER2;INTRODUCER3;DISCOUNT_REASON;" +
   	 			"DISCOUNT_APPROVER;DISCOUNT_TYPE;ORIGINAL_PRICE;RETAIL_PRICE;AR_AMT;DESCRIPTION");
//   	 	//��ʼ����ʱ��
//   	 	Timestamp now = StringTool.getTimestamp(new Date());
//   	 	this.setValue("START_DATE",
//   	 			now.toString().substring(0, 10).replace('-', '/'));
//   	 	this.setValue("END_DATE",
//   	 			now.toString().substring(0, 10).replace('-', '/'));
   	 	//��ձ�
   	 	tradeTable.removeRowAll();
   	 	sectionTable.removeRowAll();
   	 	detailTable.removeRowAll();
    }
    
    /**
     * �����Żس���ѯ�¼�
     */
    public void onMrno() {
   	 	
        pat = new Pat();
        String mrno = getValue("MR_NO").toString().trim();
        if (!this.queryPat(mrno))
            return;
        pat = Pat.onQueryByMrNo(mrno);
        if (pat == null || "".equals(getValueString("MR_NO"))) {
            this.messageBox_("���޲���! ");
            this.onClear(); //���
            return;
        } else {
            callFunction("UI|MR_NO|setEnabled", false); // ������
            //MR_NO = pat.getMrNo();
        }
        this.setPatForUI(pat);
       
    }
    
    /**
     * ��ѯ������Ϣ
     * @param mrNo String
     * @return boolean
     */
    public boolean queryPat(String mrNo) {
        //this.setMenu(false); //MENU ��ʾ����
        pat = new Pat();
        pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            //this.setMenu(false); //MENU ��ʾ����
            this.messageBox("E0081");
            return false;
        }
        String allMrNo = PatTool.getInstance().checkMrno(mrNo);
        if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
            //============xueyf modify 20120307 start
            messageBox("������" + allMrNo + " �Ѻϲ���" + pat.getMrNo());
            //============xueyf modify 20120307 stop
        }
        																														
        return true;
    }
    
    /**
     * ������Ϣ��ֵ
     *
     * @param patInfo
     *            Pat
     */
    public void setPatForUI(Pat patInfo) {
        // ������,����
        this.setValueForParm(
                "MR_NO;PAT_NAME",patInfo.getParm());
//        //���ò�ѯ����
//        this.onQuery();
    }
    
    /**
     * ���ݼ��
     */
    public boolean checkData() {
    	TTextField mrNo = (TTextField)this.getComponent("MR_NO");
    	String mrNo2 = this.getValue("MR_NO").toString();
    	String op = this.getValueString("OPERATOR");
    	if(mrNo2.length()==0 && op.length()==0){
    		this.messageBox("���������տ��˲��ܶ�Ϊ�գ�");
    		this.grabFocus("MR_NO");
    		return false;
    	}
    	if(mrNo2.length()==0 && op.length()!=0){
    		callFunction("UI|MR_NO|setEnabled",false);
    	}
    	boolean flag = mrNo.isEnabled();
    	if(flag){
    		this.messageBox("���벡��������");
    		this.grabFocus("MR_NO");
    		return false;
    	}
    	return true;
    }
    
    /**
     * ����ҳ�潻����Ϣ
     */
    public void setDataForTrade(TParm parm) {
    	this.setValue("INTRODUCER1", parm.getValue("INTRODUCER1", 0));
    	this.setValue("INTRODUCER2", parm.getValue("INTRODUCER2", 0));
    	this.setValue("INTRODUCER3", parm.getValue("INTRODUCER3", 0));
    	this.setValue("DISCOUNT_REASON", parm.getValue("DISCOUNT_REASON", 0));
    	this.setValue("DISCOUNT_APPROVER", parm.getValue("DISCOUNT_APPROVER", 0));
    	this.setValue("DISCOUNT_TYPE", parm.getValue("DISCOUNT_TYPE", 0));
    	this.setValue("ORIGINAL_PRICE", parm.getValue("ORIGINAL_PRICE", 0));
    	this.setValue("RETAIL_PRICE", parm.getValue("RETAIL_PRICE", 0));
    	this.setValue("AR_AMT", parm.getValue("AR_AMT", 0));
    	this.setValue("DESCRIPTION", parm.getValue("DESCRIPTION", 0));
    	this.setValue("DATE1", parm.getTimestamp("START_DATE",0));//add by huangjw 20141107 ��Ч����
    	this.setValue("DATE2", parm.getTimestamp("END_DATE",0));//add by huangjw 20141107 ʧЧЧ��
    	this.setValue("OPERATOR",parm.getValue("OPT_USER",0));//add by huangjw 20141114�տ���
    }
    
    /**
	 * �õ�ҳ����Table����
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}
	
	/**
	 * ��ҽ�ƿ�  add by sunqy 20140707
	 */
	public void onReadEKT() {
		// ��ȡҽ�ƿ�
		parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			parmEKT = null;
			return;
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		this.onMrno();
		this.onQuery();
	}
	
	
	/**
	 * ��ӡ add by sunqy 20140716
	 */
	public void onRePrint(){
		boolean isDedug=true; //add by huangtt 20160505 ��־���
		try {
			
		
		TParm parm = new TParm();
		String payType = "";
		DecimalFormat df=new DecimalFormat("#0.00");
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("C0", "�ֽ�");
//		map.put("C1", "ˢ��");
//		map.put("C2", "��Ʊ");
//		map.put("T0", "֧Ʊ");
//		map.put("C4", "Ӧ�տ�");
//		map.put("LPK", "��Ʒ��");
//		map.put("XJZKQ", "�ֽ��ۿ�ȯ");
//		map.put("INS", "ҽ����");
		
//		String gatherTypeSql = 
//			" SELECT GATHER_TYPE, PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE";
		String gatherTypeSql ="SELECT A.PAYTYPE, A.GATHER_TYPE,B.CHN_DESC FROM BIL_GATHERTYPE_PAYTYPE A ,SYS_DICTIONARY B" +
		" WHERE A.GATHER_TYPE= B.ID AND B.GROUP_ID = 'GATHER_TYPE'"; 
//		System.out.println(gatherTypeSql);
		TParm gatherParm = new TParm(TJDODBTool.getInstance().select(gatherTypeSql));
		Map<String, String> gatherMap = new HashMap<String, String>();
		for (int i = 0; i < gatherParm.getCount(); i++) {
//			gatherMap.put(gatherParm.getValue("PAYTYPE", i), map.get(gatherParm.getValue("GATHER_TYPE", i)));
			gatherMap.put(gatherParm.getValue("PAYTYPE", i), gatherParm.getValue("CHN_DESC", i));
		}
		//System.out.println("gatherMap=====:"+gatherMap);
		int selectRow = tradeTable.getSelectedRow();//ѡ����
		String mrNo=tradeTable.getParmValue().getValue("MR_NO",selectRow);
		
		if(selectRow<0){
			this.messageBox("û��ѡ��������!");
			return;
		}
		String sql = "SELECT B.PACKAGE_DESC CTZ_CODE,A.PAY_TYPE01,A.PAY_TYPE02,A.PAY_TYPE03,A.PAY_TYPE04,A.PAY_TYPE05," +
					"A.PAY_TYPE06,A.PAY_TYPE07,A.PAY_TYPE08,A.PAY_TYPE09,A.PAY_TYPE10,A.PAY_TYPE11," +
					"A.MEMO1,A.MEMO2,A.MEMO3,A.MEMO4,A.MEMO5,A.MEMO6,A.MEMO7,A.MEMO8,A.MEMO9,A.MEMO10,A.MEMO11,A.PAY_MEDICAL_CARD,A.BILL_TYPE " +
					"FROM MEM_PACKAGE_TRADE_M A,MEM_PACKAGE B WHERE A.TRADE_NO = '" + 
					tradeTable.getItemString(selectRow, "TRADE_NO") + 
					"' AND B.PACKAGE_CODE ='" +
					sectionTable.getItemString(0, "PACKAGE_CODE") +
					"' ORDER BY A.OPT_DATE DESC";
//		System.out.println("----sql:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		parm.setData("TYPE", "TEXT", ""); //���
		parm.setData("MR_NO", "TEXT", mrNo); // ������
		parm.setData("RecNO", "TEXT", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO", "MEM_NO")); //Ʊ�ݺ�
		parm.setData("DEPT_CODE", "TEXT", "");// �Ʊ�
		parm.setData("PAT_NAME", "TEXT", tradeTable.getParmValue().getValue("PAT_NAME",selectRow)); // ����
		parm.setData("MONEY", "TEXT", df.format(this.getValueDouble("AR_AMT"))+"Ԫ"); // ���
		parm.setData("CAPITAL", "TEXT", StringUtil.getInstance().numberToWord(this.getValueDouble("AR_AMT"))); // ��д���
		//�վ�����������շѣ�Ҫ��ʾ�����ͺͿ��� add by huanjw 20150105 start
		String cardtype="";//����ӿ���ȡ��MEMO2�ֶ�ֵ
		cardtype=result.getValue("MEMO2",0);
		if(!"".equals(cardtype)&&!"#".equals(cardtype)){
			String [] str=cardtype.split("#");
			String [] str1=str[0].split(";");
//			String [] str2=str[1].split(";");
			String [] str2=null;
			if(str.length == 2){
				str2=str[1].split(";");
			}
			String cardtypeString="";
			for(int m=0;m<str1.length;m++){
				String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
				TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
//				cardtypeString=cardtypeString+cardParm.getValue("CHN_DESC",0)+" "+str2[m]+" ";
				cardtypeString=cardtypeString+cardParm.getValue("CHN_DESC",0)+" ";
				if(str2 != null){
					if(m < str2.length ){
						cardtypeString=cardtypeString+str2[m]+" ";
					}
				}
			}
			parm.setData("ACOUNT_NO", "TEXT", cardtypeString);// �˺źͿ�����
		}
		//�վ�����������շѣ�Ҫ��ʾ�����ͺͿ��� add by huanjw 20150105 end
//		String bankNo = "";
//		for (int i = 1; i < 11; i++) {
//			if(!"".equals(result.getValue("MEMO"+i,0))){
//				bankNo += result.getValue("MEMO"+i,0)+";";
//			}
//		}
//		if(bankNo.length()>0){
//			bankNo = bankNo.substring(0, bankNo.length()-1);
//		}
//		parm.setData("ACOUNT_NO", "TEXT", bankNo);// �˺�
		//parm.setData("CTZ_CODE", "TEXT", result.getValue("CTZ_CODE",0));// ��Ʒ
		//===================================modify by huangjw 20141110 start
		String ctz_code=result.getValue("CTZ_CODE",0);
		if(ctz_code.length()<=7){
			parm.setData("CTZ_CODE0", "TEXT", ctz_code);// ��Ʒ
		}else{
			parm.setData("CTZ_CODE","TEXT",ctz_code.substring(0,7));
			parm.setData("CTZ_CODE1","TEXT",ctz_code.substring(7,ctz_code.length()));
		}
		//===================================modify by huangjw 20141110 end
		if("1".equals(this.getValue("DISCOUNT_REASON"))){
			parm.setData("REASON", "TEXT", "��ͨ��Ա�ۿ�");// �ۿ�ԭ��
		}
		if("2".equals(this.getValue("DISCOUNT_REASON"))){
			parm.setData("REASON", "TEXT", "������ۿ�");// �ۿ�ԭ��
		}
		String date = StringTool.getTimestamp(new Date()).toString().substring(0, 19).replace('-', '/');
		parm.setData("DATE", "TEXT", date);// ����
		parm.setData("OP_NAME", "TEXT", Operator.getID()); // �տ���
		parm.setData("COPY", "TEXT", "(copy)"); // ��ӡע��
		//if(isBuy.isSelected()){
		String key, value, keyStr;
		if(result.getValue("BILL_TYPE", 0).equals("C")){
			for (int j = 0; j < gatherParm.getCount(); j++) {
				key = gatherParm.getValue("PAYTYPE", j);
				value = result.getValue(key, 0);
				//���Ǯ��Ϊ0����ʾ
				if(Double.parseDouble(value) != 0){
					keyStr = gatherMap.get(key);
					payType += onReturnPayType(keyStr, value) ;
				}
				
			}
			if(payType.length()>0){
				payType = payType.substring(1, payType.length());
				String arr[] = payType.split(";");//���֧����ʽ��3�����������ʾ
				if(arr.length > 2){
					parm.setData("PAY_TYPE2", "TEXT", arr[0]);
					parm.setData("PAY_TYPE3", "TEXT", arr[1]+";"+arr[2]);
				}else{
					parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
				}
			}else{
				parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
			}
		}else{
			payType = "ҽ�ƿ�:"+result.getDouble("PAY_MEDICAL_CARD", 0)+"Ԫ";
			parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
		}
			
			
			
		if(isBuy.isSelected()){
			parm.setData("TITLE", "TEXT", "�ײͽ����վ�");
			parm.setData("RETURN", "TEXT", ""); // ��
			parm.setData("o", "TEXT", "");// ��
			parm.setData("EXPLAIN", "TEXT", "˵�������վݲ�������ƾ֤�������ɵ��ͷ���ӡ��Ʊ"); //˵�� add by huangtt 201500924
		}
		if(isReFee.isSelected()){
			//payType = "�ֽ�"+this.getValue("AR_AMT")+"Ԫ";
			//parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
			parm.setData("TITLE", "TEXT", "�ײ��˷��վ�");
			parm.setData("RETURN", "TEXT", "��"); // ��
			parm.setData("o", "TEXT", "o");// ��
			parm.setData("EXPLAIN", "TEXT", "˵�������ײ��轻��ԭ�ײ��վ�"); //˵�� add by huangtt 201500924
		}
		parm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalCHNFullName() : "����ҽԺ");
        parm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
		this.openPrintDialog(IReportTool.getInstance().getReportPath("MEMPackReceiptV45.jhw"),
				IReportTool.getInstance().getReportParm("MEMPackReceiptV45.class", parm));//����ϲ�
		
		} catch (Exception e) {
			// TODO: handle exception
			if(isDedug){  
				System.out.println(" come in class: MEMPackageSalesInfoControl ��method ��onRePrint");
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * ���ݴ���������֧����ʽadd by sunqy 20140717
	 * @param key
	 * @param value
	 * @return
	 */
	private String onReturnPayType(String key, String value){
		DecimalFormat format = new DecimalFormat("########0.00");
		String str = "";
		if(value.length() > 0 && Math.abs(Double.valueOf(value)) >= 0){
			str = ";" + key + ":" + format.format(Double.valueOf(value)) + "Ԫ";
		}
		return str;
	}
}
