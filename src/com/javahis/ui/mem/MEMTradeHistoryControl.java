package com.javahis.ui.mem;


import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
*
* <p>Title: ��Ա�������˿���ʷ���ײ�ѯ</p>
*
* <p>Description: ��Ա�������˿���ʷ���ײ�ѯ</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author duzhw 20140418
* @version 4.5
*/
public class MEMTradeHistoryControl extends TControl {
	
	private TTable table;
	Pat pat;
	
	/**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        initData();
        initComponent();
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm acceptData = (TParm) obj;
            String mrNo = acceptData.getData("MR_NO").toString();
            this.setValue("MR_NO", mrNo);
            this.onQuery();
        }
    }
    
    /**
     * ��ʼ������
     */
    public void initData() {
    	/*
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
    	//��ʼ����
    	this.setValue("START_DATE", now.toString().substring(0, 10).replace('-', '/'));
    	//��������
    	this.setValue("END_DATE", StringTool.rollDate(now, +1).toString().substring(0, 10).
                replace('-', '/'));
                */
    	
    }
    
    /**
	 * ��ʼ���ؼ�
	 */
    private void initComponent() {
    	table = getTable("TABLE");
    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {
    	String mrNo = this.getValueString("MR_NO");
    	if(mrNo==null || mrNo.length()<=0){
    		this.messageBox("�����Ų���Ϊ�գ�");
    		this.grabFocus("MR_NO");
    		return;
    	}else{
    		onMrno(); 
    	}
    	
    }
    
    /**
     * ��ѯ��ʷ��Ϣ
     */
    public void onQueryHisInfo(){
    	String mrNo = this.getValueString("MR_NO");
    	String sql = getSql(mrNo);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount()<=0){
    		this.messageBox("���޼�¼��");
    		return;
    	}
    	table.setParmValue(result);
    }
    
    /**
     * �����Żس�
     */
    public void onMrno() {
    	pat = new Pat();
        String mrno = getValue("MR_NO").toString().trim();
        if (!this.queryPat(mrno))
            return;
        pat = pat.onQueryByMrNo(mrno);
        //System.out.println("pat="+pat);
        if (pat == null || "".equals(getValueString("MR_NO"))) {
            this.messageBox_("���޲���! ");
            this.onClear(); // ���
            return;
        }else{
        	this.setValue("MR_NO", pat.getMrNo());//������
        	//this.setValue("PAT_NAME", pat.getName());//����
        	//this.setValue("SEX_CODE", pat.getSexCode());//�Ա�
        	//����������Ч
        	callFunction("UI|MR_NO|setEnabled", false); 
        	//��ѯ����
        	onQueryHisInfo();
        }
        
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
     * ���
     */
    public void onClear() {
    	//����������Ч
    	callFunction("UI|MR_NO|setEnabled", true); 
    	this.clearValue("MR_NO;MEM_CODE");
    	/*
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
    	//��ʼ����
    	this.setValue("START_DATE", now.toString().substring(0, 10).replace('-', '/'));
    	//��������
    	this.setValue("END_DATE", StringTool.rollDate(now, +1).toString().substring(0, 10).
                replace('-', '/'));
    	*/
    	table.removeRowAll();
    }
    
    /**
     * ��ȡsql
     */
    public String getSql(String mrNo){
    	String startDate = this.getValueString("START_DATE");
    	String endDate = this.getValueString("END_DATE");
    	String memCode = this.getValueString("MEM_CODE");
    	
    	String sql = "SELECT ROWNUM AS ID,CASE STATUS WHEN '0' THEN 'Ԥ�쿨' WHEN '1' THEN '�Ѱ쿨' " +
    			" WHEN '2' THEN '�˿�' END STATUS,A.TRADE_NO,A.MR_NO,A.MEM_CODE,A.MEM_DESC,A.MEM_CARD_NO,A.MEM_FEE," +
    			" A.START_DATE,A.END_DATE,A.DESCRIPTION,A.OPT_DATE,A.OPT_USER,A.OPT_TERM,A.REASON," +
    			" A.LAST_DEPRECIATION_END_DATE,A.INTRODUCER1,A.INTRODUCER2,A.INTRODUCER3,A.GATHER_TYPE,B.PAT_NAME,B.IDNO " +
    			" FROM MEM_TRADE A, SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND A.MR_NO = '"+mrNo+"' ";
    	if(startDate.trim().length()>0 || endDate.trim().length()>0){
    		sql += " AND A.START_DATE BETWEEN TO_DATE('"+startDate.substring(0, 10).replaceAll("-", "/")+ "','yyyy/mm/dd') " + 
    			" AND TO_DATE('" + endDate.substring(0, 10).replaceAll("-", "/") + "','yyyy/mm/dd')"; 
    	}
    	if(memCode.length()>0){
    		sql += " AND A.MEM_CODE = '"+memCode+"' ";
    	}
    	//System.out.println("sql="+sql);
    	return sql;
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
 
}
