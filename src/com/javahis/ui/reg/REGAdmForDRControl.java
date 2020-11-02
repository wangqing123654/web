package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

import jdo.sys.Pat;
import jdo.sys.SystemTool;

import java.sql.Timestamp;
import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;
import jdo.reg.PanelRoomTool;
import jdo.reg.REGAdmForDRTool;
import jdo.reg.Reg;

import com.dongyang.util.*;

/**
 * <p>Title: ҽ��ԤԼ�Һ�</p>
 *
 * <p>Description:ҽ��ԤԼ�Һ� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author zhouGC attributable
 * @version 1.0
 */
public class REGAdmForDRControl
    extends TControl {
    /**
     * ���table
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    private static String caseNO; //�����
    private static String MR_NO; //������
    private static String ADM_TYPE; //�ż������   =====huangtt 20131203
    private static String sessionCode; //����ʱ��
    private static String ADM_DATE; //��������===huangtt 20131204
    private static String CLINICROOM_NO; //���===huangtt 20131204
    private static String QUE_NO;//���==huangtt 20131204
    private static String Action = "P"; //����
    private static String HB1; //�ű�
    private static String KS1; //����
    private static String ZJ1; //���
    private static String YS1; //ҽ��
    private static String ZH1; //���
    private static String SJ1; //ʱ��
    private static String SD1; //ʱ��
    private static String H1; //vip���
    private static String CTZ1_CODE1; //���
    private static String flg = "N";
    private static String NHI_NO=""; //ҽ������===pangben modify 20110809
    private static String YSZ; //ҽ������===huangtt modify 20131101
    private static String SDZ; //ʱ������===huangtt modify 20131101
    private static String RS;  //ҽ���Һ�����==huangtt modifty 20131108
    private static boolean VIP; //����ҽ������е�VIP
    private static  String preOrderFlg = "";//Ԥ�������
    private static  String oldCaseNo = "";//ԭ���ľ����   yanjing 20131230
    /**
     * ��ʼ��
     */
    public void onInit() {
        //��������
        Object obj = getParameter();
        TParm t;
        MR_NO = "";
        if (obj != null) {
            t = (TParm) obj;
            MR_NO = t.getValue("MR_NO");
            NHI_NO=t.getValue("NHI_NO");//===pangben modify 20110809
            ADM_TYPE="O";//======huangtt add by 20131203

        }
        // �õ���ǰʱ��
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("YY_START_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("YY_END_DATE", "9999/12/31");
        this.setValue("start_Date",
                      StringTool.rollDate(date, +7).toString().substring(0, 10).
                      replace('-', '/'));
        
        //add by huangtt 20131205 start
        this.callFunction("UI|SESSION_CODE|AdmType", ADM_TYPE);
        this.callFunction("UI|SESSION_CODE|Region", Operator.getRegion());
        callFunction("UI|SESSION_CODE|onQuery");
      //add by huangtt 20131205 end
        callFunction("UI|DEPT_CODE|onQuery");


        //zhangyong20110421 begin
//        String sql =
//            "SELECT SESSION_CODE, SESSION_DESC FROM REG_SESSION  WHERE  REGION_CODE = '" +
//            Operator.getRegion() + "' AND ADM_TYPE = 'O' ORDER BY SEQ, SESSION_CODE ";
//        TParm parmSession = new TParm(TJDODBTool.getInstance().select(sql));
//        this.getComboBox("Session").setParmValue(parmSession);
        //zhangyong20110421 end

        sessionCode = this.getSession_code(date);
        this.setValue("SESSION_CODE", sessionCode);
        this.setValue("KS", Operator.getDept());
        String w = this.getValue("start_Date").toString().
            substring(0, 10).replace("-", "");
        TParm parm = QueryDRName(this.getValueString("SESSION_CODE"),
                Operator.getDept(), w);
        this.getComboBox("cbx_DRName").setParmValue(parm);
        TParm Tparm = this.QuseryPatMess(MR_NO);
        this.setValue("MR_NO", MR_NO);
        this.setValue("PAT_NAME", Tparm.getValue("PAT_NAME", 0));
        this.setValue("PY1", Tparm.getValue("PY1", 0));
        this.setValue("BIRTH_DATE",
                      Tparm.getValue("BIRTH_DATE", 0).toString().
                      substring(0, 10).replace('-', '/'));
        this.setValue("SEX_CODE", Tparm.getValue("SEX_CODE", 0));
        this.setValue("FOREIGNER_FLG", Tparm.getValue("FOREIGNER_FLG", 0));
        this.setValue("IDNO", Tparm.getValue("IDNO", 0));
        this.setValue("TEL_HOME", Tparm.getValue("TEL_HOME", 0));
        this.setValue("POST_CODE", Tparm.getValue("POST_CODE", 0));
        this.setValue("ADDRESS", Tparm.getValue("ADDRESS", 0));
        CTZ1_CODE1 = Tparm.getValue("CTZ1_CODE", 0);
        this.onInitTable3();//��ʼ���Һ���Ϣ
        this.onInitSchdayTable();//��ʼ�������Ϣ
        this.setValue("cbx_DRName", Operator.getID());
    }
    /**
    * ��ʼ���Һ���Ϣ��
    * yanjing 20131230
    */
    private void onInitTable3(){
    	// �õ���ǰʱ��
        Timestamp date = SystemTool.getInstance().getDate();
        TParm tparm = QueryREGPatMess(MR_NO, Operator.getID(),
                date.toString().substring(0, 10).
                replace("-", "") + "000000",
                date.toString().substring(0, 10).
                replace("-", "") + "235959",
                Operator.getDept(),
                this.getValueString("SESSION_CODE"));
          this.getTTable(" Table3").setParmValue(tparm);
    }
    /**
     * ��ʼ�������Ϣ����ͨ��VIP��
     * yanjing 20131230
     */
    private void onInitSchdayTable(){
    	 TParm tpram = this.QueryDRCount(this.getValue("start_Date").toString().
                 substring(0, 10).replace("-", ""),
                 Operator.getDept(),
                 this.getValueString("SESSION_CODE"),ADM_TYPE); 
    	 this.getTTable("Table1").setParmValue(tpram); //��ͨ���
         TParm p = this.QueryVip(this.getValue("start_Date").toString().
                 substring(0, 10).replace("-", ""),
                 Operator.getStation(),this.getValueString("cbx_DRName"),
                 this.getValueString("SESSION_CODE"),this.getValue("KS").toString(),ADM_TYPE); 
         this.getTTable(" Table2").setParmValue(p);//VIP���
         
    }
    /**
     * ���TComboBox
     * @param tagName String
     * @return TComboBox
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * ��ѯ����ҽ����Ϣ�����ظ�������
     * @param sessionCode String
     * @param dept_code String
     * @param w int
     * @return TParm
     */
    public TParm QueryDRName(String sessionCode, String dept_code, String w) {

        String sql =
           "SELECT REG_SCHDAY.DR_CODE AS DR_CODE,SYS_OPERATOR.USER_NAME AS USER_NAME"+
           "  FROM   REG_SCHDAY,SYS_OPERATOR"+
           "  WHERE   ADM_DATE = '"+w+"' AND SESSION_CODE = '"+sessionCode+"' AND DEPT_CODE = '"+dept_code+"' AND SYS_OPERATOR.USER_ID=REG_SCHDAY.DR_CODE";
       //System.out.println("sql----"+sql);

        TParm parm = new TParm(TJDODBTool.getInstance().select(REGAdmForDRTool.
            QueryDRNameSql(sessionCode, dept_code, w)));
        return parm;
    }

    /**
     * ҽ���Һ���Ϣ
     * @param stateDate String
     * @param dept String
     * @param endDate String
     * @return TParm
     */
    public TParm QueryDRCount(String stateDate, String dept,String sessionCode,String admType) {
        TParm tparm = new TParm();
        tparm = new TParm(TJDODBTool.getInstance().select(REGAdmForDRTool.
            QueryDRCountSql(stateDate, dept,sessionCode,admType)));

        return tparm;
    }

    /**
     * ���߻�����Ϣ
     * @param MR_NO String
     * @return TParm
     */
    public TParm QuseryPatMess(String MR_NO) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(REGAdmForDRTool.
            QuseryPatMessSql(MR_NO)));
        return parm;
    }

    /**
     * ��ѯ���߹Һ���Ϣ
     * @param MR_NO String
     * @param DR_CODE String
     * @param state_date String
     * @param end_date String
     * @param dept_code String
     * @param session_code String
     * @return TParm
     */
    public TParm QueryREGPatMess(String MR_NO, String DR_CODE,
                                 String state_date, String end_date,
                                 String dept_code, String session_code) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(REGAdmForDRTool.
            QueryREGPatMessSql(MR_NO, DR_CODE, state_date, end_date, dept_code,
                               session_code)));
        return parm;
    }

    /**
     * �˹Ҹ��·���
     * @param CaseNO String
     */
    public void upDate(String CaseNO) {
        Timestamp date = SystemTool.getInstance().getDate();
        TParm t = new TParm();
        t.setData("CASE_NO", CaseNO);
        t.setData("REGCAN_USER", Operator.getID());
        t.setData("REGCAN_DATE", date);
        t.setData("ADM_STATUS", "2"); // �ѹҺ�
        TParm delResultParm = TIOM_AppServer.executeAction("action.reg.REGAction",
	            "delOpdOrder", t);
        this.onInitTable3();
//        REGAdmForDRTool.getInstance().onUpdate(t);
    }

    /**
     * �˹��¼�
     */
    public void onUnReg() {
        upDate(caseNO);
        String sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = '"
			+ ADM_TYPE
			+ "'AND ADM_DATE = '"
			+ ADM_DATE
			+ "' AND "
			+ "SESSION_CODE = '"
			+ sessionCode
			+ "' AND "
			+ "CLINICROOM_NO = '"
			+ CLINICROOM_NO
			+ "' AND "
			+ "QUE_NO = '"
			+ QUE_NO + "'";
	    TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
	    if (updateParm.getErrCode() < 0) {
		    messageBox("�˹�ʧ��");
		    return;
	    }
        this.messageBox("�˹ҳɹ�");
        //huangtt start 20121105 
        onInitTable3();
        onChangeDR();
      //huangtt end 20121105

    }

    /**
     * Table1������¼�
     * @param date Timestamp
     * @return int
     */
    public void onTableClick() {
        TTable table = getTTable("Table3");
        int row = getTTable("Table3").getSelectedRow();
        TParm parm = table.getParmValue().getRow(row);
        caseNO = parm.getData("CASE_NO").toString();
        preOrderFlg = parm.getData("IS_PRE_ORDER").toString();//Ԥ�����Һű��,yanjing 20131227
        oldCaseNo = parm.getData("OLD_CASE_NO").toString();//��ȡԭ���ľ����,yanjing 20131227
//        String preAdmDate = parm.getData("ADM_DATE").toString();
        String preAdmDate = parm.getValue("ADM_DATE",0);
        if(preOrderFlg.equals("Y")){//Ԥ�����ʱʱ���Զ���ֵ  
        	if(preAdmDate.equals("")){
//        		this.setValue("start_Date","");
        	}else{
        	this.setValue("start_Date",preAdmDate.substring(0, 10).replace('-', '/'));
        	}
        }
        
        if(parm.getBoolean("APPT_CODE")){
        	this.callFunction("UI|unreg|setEnabled", true);
//        	caseNO = parm.getData("CASE_NO").toString();
        	ADM_TYPE= parm.getData("ADM_TYPE").toString();
        	sessionCode=parm.getData("SESSION_CODE").toString();
        	CLINICROOM_NO = parm.getData("CLINICROOM_NO").toString();
        	ADM_DATE = parm.getData("ADM_DATE").toString().substring(0, 10).replace("-", "");
        	QUE_NO = parm.getData("QUE_NO").toString();
        }else{
        	this.messageBox("��ǰ�ҺŲ������˹�");
        	this.callFunction("UI|unreg|setEnabled", false);
        	return;
        }

        
    }

    /**
     * Table3������¼�
     * @param date Timestamp
     * @return int
     */
    public void onTableClick3() {
        Action = "P";
        // this.messageBox(Action);
        TTable table = getTTable("Table1");
        int row = getTTable("Table1").getSelectedRow();
        TParm parm1 = table.getParmValue().getRow(row);
        SD1 = parm1.getData("SESSION_CODE").toString();
        HB1 = parm1.getData("CLINICTYPE_CODE").toString();
        KS1 = parm1.getData("DEPT_CODE").toString();
        ZJ1 = parm1.getData("CLINICROOM_NO").toString();
        YS1 = parm1.getData("DR_CODE").toString();
        ZH1 = parm1.getData("QUE_NO").toString();
        //  this.messageBox(HB1 + KS1 + ZJ1 + YS1 + ZH1);
        RS =  parm1.getData("QUE_NO").toString();  
        VIP = parm1.getBoolean("VIP_FLG"); 
        this.setValue("cbx_DRName", YS1); 
        TComboBox cbx = (TComboBox) getComponent("cbx_DRName");  
        YSZ = cbx.getSelectedName();
        if(VIP){
        	this.getTTable("Table2").removeRowAll();
            TParm v = this.QueryVip(this.getValue("start_Date").toString().
                    substring(0, 10).replace("-", ""),
                    Operator.getStation(),YS1,this.getValueString("SESSION_CODE"),KS1,ADM_TYPE);
            this.getTTable("Table2").setParmValue(v);
            
            TParm tempV = new TParm();
            tempV.setData("CLINICROOM_NO",ZJ1);
            tempV.setData("ADM_DATE",StringTool.getString(
					(Timestamp) getValue("start_Date"), "yyyyMMdd"));
            this.queryQueNo(tempV);

        }
        this.callFunction("UI|SAVE|setEnabled", true);


    }
	/**
	 * ����������ռ�� ====huangtt 20131108
	 * 
	 * @param temp
	 */
	private void queryQueNo(TParm temp) {
		String vipSql = "SELECT MIN(QUE_NO) QUE_NO FROM REG_CLINICQUE "
				+ "WHERE ADM_TYPE='"+ADM_TYPE+"' AND  ADM_DATE='"
				+ temp.getValue("ADM_DATE") + "'" + " AND SESSION_CODE='"
				+ this.getValueString("SESSION_CODE") + "' AND CLINICROOM_NO='"
				+ temp.getValue("CLINICROOM_NO") + "' AND  QUE_STATUS='N'";
		TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
		if (result.getErrCode() < 0) {
			messageBox("���ʧ��");
			return;
		}
		if (result.getCount() <= 0) {
			messageBox("�޾����");
			return;
		}
		ZH1 = result.getValue("QUE_NO", 0);
		
	}


    /**
     * Table2����¼�
      @param date Timestamp
     * @return int
     */

    public void onTableClick2() {
        Action = "V";
        TTable table = getTTable("Table2");
        int row = getTTable("Table2").getSelectedRow();
        TParm parm2 = table.getParmValue().getRow(row);
        //=====huangtt 20131013 start
        if(parm2.getBoolean("QUE_STATUS")){
        	this.messageBox("�ú���ռ�ã�������ѡ��");
        	this.callFunction("UI|SAVE|setEnabled", false);
        	return;
        	
        }
        this.callFunction("UI|SAVE|setEnabled",true );
        //=====huangtt 20131013 end
        SD1 = parm2.getData("SESSION_CODE").toString();
        KS1 = parm2.getData("DEPT_CODE").toString();
        YS1 = parm2.getData("DR_CODE").toString();
        ZH1 = parm2.getData("QUE_NO").toString();
        SJ1 = parm2.getData("START_TIME").toString();
        H1 = parm2.getData("QUE_STATUS").toString();
        HB1 = parm2.getData("CLINICTYPE_CODE").toString();
        ZJ1 = parm2.getData("CLINICROOM_NO").toString();
        TParm parmz = table.getShowParmValue().getRow(row); //add by huangtt 20131101
        YSZ = parmz.getData("DR_CODE").toString();  //add by huangtt 20131101

    }

    /**
     * ���淽��
     * @param date Timestamp
     * @return int
     */
    public void onSave() {
    	String newCaseNo = "";
//    	String oldCaseNo = "";
    	if(preOrderFlg.equals("Y")){
    		newCaseNo = caseNO;
    		//��ѯreg_patadm��ɾ��������Ϣ
    		String regSql = "DELETE REG_PATADM WHERE CASE_NO = '"+caseNO+"'";
        	TParm deleteParm = new TParm(TJDODBTool.getInstance().update(regSql));
    	}else{
            newCaseNo = SystemTool.getInstance().getNo("ALL", "REG",
            "CASE_NO", "CASE_NO");
    	}
        String ADM_TYPE = "O"; //
        String mr_no = MR_NO;
        String REGION_CODE = Operator.getRegion();
        String SESSION_CODE = this.getValueString("SESSION_CODE");
        String CLINICAREA_CODE = ZJ1;
        String tt = HB1;
        String DEPT_CODE = KS1;
        String DR_CODE = YS1;
        //===zhangp 20120628 start
        String APPT_CODE = "Y";
        //===zhangp 20120628 end
        String VISIT_CODE = "1";
        String REGMETHOD_CODE = "D";
        String CTZ1_CODE = CTZ1_CODE1;
        String ARRIVE_FLG = "N";
        String ADM_REGION = Operator.getRegion();
        String HEAT_FLG = "N";
        String ADM_STATUS = "1";
        String REPORT_STATUS = "1";
        String OPT_USER = Operator.getID();
        Timestamp OPT_DATE = SystemTool.getInstance().getDate();
        String ADM_DATE = this.getValue("start_Date").toString().substring(0,
            10).replace("-", "") + "000000"; //������ؼ��ϵ����ݽ�ȡ�Ժ󸳸�����
        Timestamp REG_DATE = SystemTool.getInstance().getDate();  //add by huangtt 20131211

//        String REG_DATE = this.getValue("YY_START_DATE").toString().substring(0,
//            10).replace("-", "") + "000000"; //������ؼ��ϵ����ݽ�ȡ�Ժ󸳸�����
        String OPT_TERM = Operator.getIP();
        //add by huangtt 20131113 start ����ҽʦͬʱ��������غ�
        if("V".equals(Action)){
        	
        	TParm parmZHV= this.QueryVip(this.getValue("start_Date").toString().
                    substring(0, 10).replace("-", ""), CLINICAREA_CODE, DR_CODE, SD1, DEPT_CODE,ADM_TYPE);
        	for(int i=0;i<parmZHV.getCount();i++){
        		if(ZH1.equals(parmZHV.getValue("QUE_NO", i))){
        			if(parmZHV.getBoolean("QUE_STATUS", i)){
        				this.messageBox("�ú��Ѿ�������ҽʦԤԼ��������ѡ����ţ�");
        				onChangeDR();
        				return;
        			}
        		}
        	}
        }else{

        	TParm parmZH = this.QueryDRP(this.getValue("start_Date").toString().
                    substring(0, 10).replace("-", ""),CLINICAREA_CODE , DR_CODE, SD1,ADM_TYPE);
//        	System.out.println("parmZH=="+parmZH);
        	if(VIP){
        		TParm tempV = new TParm();
                tempV.setData("CLINICROOM_NO",ZJ1);
                tempV.setData("ADM_DATE",StringTool.getString(
    					(Timestamp) getValue("start_Date"), "yyyyMMdd"));
                this.queryQueNo(tempV);
        	}else{
//        		this.messageBox("zh=="+parmZH.getValue("QUE_NO", 0));
                if(!ZH1.equals(parmZH.getValue("QUE_NO", 0))){
                	ZH1 = parmZH.getValue("QUE_NO", 0);
                	
                }
        	}
        	RS = parmZH.getValue("QUE_NO", 0);
        }
        
        //add by huangtt 20131113 end
        TParm tp = new TParm();
        tp.setData("CASE_NO", newCaseNo);
        tp.setData("ADM_TYPE", ADM_TYPE);
        tp.setData("MR_NO", mr_no);
        tp.setData("REGION_CODE", REGION_CODE);
        tp.setData("ADM_DATE",
                   StringTool.getTimestamp(ADM_DATE, "yyyyMMddHHmmss"));
        tp.setData("REG_DATE",
        				REG_DATE);
        tp.setData("SESSION_CODE", SD1);
        tp.setData("CLINICAREA_CODE", (PanelRoomTool.getInstance()
				.getAreaByRoom(TypeTool.getString(CLINICAREA_CODE)))
				.getValue("CLINICAREA_CODE", 0));  //add by huangtt 20131108
        tp.setData("CLINICROOM_NO", CLINICAREA_CODE);
        tp.setData("CLINICTYPE_CODE", tt);
        tp.setData("DEPT_CODE", DEPT_CODE);
        tp.setData("DR_CODE", DR_CODE);
        //===zhangp 20120628 start
        tp.setData("REALDEPT_CODE", DEPT_CODE);
        tp.setData("REALDR_CODE", DR_CODE);
//        ===zhangp 20120628 end
        tp.setData("APPT_CODE", APPT_CODE);
        tp.setData("VISIT_CODE", VISIT_CODE);
        tp.setData("REGMETHOD_CODE", REGMETHOD_CODE);
        tp.setData("CTZ1_CODE", CTZ1_CODE);
        tp.setData("ARRIVE_FLG", ARRIVE_FLG);
        tp.setData("ADM_REGION", ADM_REGION);
        tp.setData("HEAT_FLG", HEAT_FLG);
        tp.setData("ADM_STATUS", ADM_STATUS);
        tp.setData("ERD_LEVEL","");
        tp.setData("REPORT_STATUS", REPORT_STATUS);
        tp.setData("OPT_USER", OPT_USER);
        tp.setData("OPT_DATE", OPT_DATE);
        tp.setData("OPT_TERM", OPT_TERM);
        tp.setData("QUE_NO", ZH1);
        tp.setData("NHI_NO", NHI_NO);//===========panben  20110809
        tp.setData("IS_PRE_ORDER", preOrderFlg);//===========yanjing 20131230
        tp.setData("OLD_CASE_NO", oldCaseNo);//============yanjing 20131230
        TParm tpv = new TParm();
        tpv.setData("IS_PRE_ORDER", preOrderFlg);//===========yanjing 20131230
        tpv.setData("OLD_CASE_NO", oldCaseNo);//============yanjing 20131230
        tpv.setData("CASE_NO", newCaseNo);
        tpv.setData("ADM_TYPE", ADM_TYPE);
        tpv.setData("MR_NO", mr_no);
        tpv.setData("REGION_CODE", REGION_CODE);
        tpv.setData("ADM_DATE",
                    StringTool.getTimestamp(ADM_DATE, "yyyyMMddHHmmss"));
        tpv.setData("REG_DATE",
        		REG_DATE);
        tpv.setData("SESSION_CODE", SD1);
        tpv.setData("CLINICAREA_CODE", (PanelRoomTool.getInstance()
				.getAreaByRoom(TypeTool.getString(CLINICAREA_CODE)))
				.getValue("CLINICAREA_CODE", 0)); //add by huangtt 20131108
        tpv.setData("CLINICROOM_NO", CLINICAREA_CODE);
        tpv.setData("CLINICTYPE_CODE", tt);
        tpv.setData("DEPT_CODE", DEPT_CODE);
        tpv.setData("DR_CODE", DR_CODE);
        //===zhangp 20120628 start
        tpv.setData("REALDEPT_CODE", DEPT_CODE);
        tpv.setData("REALDR_CODE", DR_CODE);
        //===zhangp 20120628 end
        tpv.setData("APPT_CODE", APPT_CODE);
        tpv.setData("VISIT_CODE", VISIT_CODE);
        tpv.setData("REGMETHOD_CODE", REGMETHOD_CODE);
        tpv.setData("CTZ1_CODE", CTZ1_CODE);
        tpv.setData("ARRIVE_FLG", ARRIVE_FLG);
        tpv.setData("ADM_REGION", ADM_REGION);
        tpv.setData("HEAT_FLG", HEAT_FLG);
        tpv.setData("ADM_STATUS", ADM_STATUS);
        tpv.setData("REPORT_STATUS", REPORT_STATUS);
        tpv.setData("ERD_LEVEL","");
        tpv.setData("OPT_USER", OPT_USER);
        tpv.setData("OPT_DATE", OPT_DATE);
        tpv.setData("OPT_TERM", OPT_TERM);
        tpv.setData("VIP_FLG", "Y");
        tpv.setData("REG_ADM_TIME", SJ1);
        tpv.setData("QUE_NO", ZH1);
        tpv.setData("NHI_NO", NHI_NO);//===========panben  20110809
        if ("V".equals(Action)) {
            //this.messageBox_(tpv);
            REGAdmForDRTool.getInstance().onSaveV(tpv);
//            this.messageBox("P0001");
        }
        else {
            //this.messageBox_(tp);
            REGAdmForDRTool.getInstance().onSaveP(tp);
            if(VIP){
            	TParm VipSeqNo = new TParm( TJDODBTool.getInstance().select(
            			REGAdmForDRTool.selectVIP(this.getValue("start_Date").
            					toString().substring(0, 10).replace("-", ""), SD1, CLINICAREA_CODE)));
            	String seqNo = VipSeqNo.getValue("QUE_NO", 0);
            	TJDODBTool.getInstance().update(REGAdmForDRTool.updateVIP(this.getValue("start_Date").
            					toString().substring(0, 10).replace("-", ""), SD1, CLINICAREA_CODE, seqNo));
            }

        }
        int q = 0;
        if(RS!=null&&!"".equals(RS))    
            q = Integer.parseInt(RS);
        q += 1;
        String qu = "" + q;
        TJDODBTool.getInstance().update(REGAdmForDRTool.updateDept(qu,
            this.getValue("start_Date").toString().
            substring(0, 10).replace("-", ""), KS1, YS1,SD1, CLINICAREA_CODE,ADM_TYPE,DR_CODE));
        //===huangtt 2013/11/01 start 
        TParm parm = new TParm();
        parm.addData("MrNo", this.getValueString("MR_NO"));
        parm.addData("Name", this.getValueString("PAT_NAME"));
//        TComboBox sessionCode = (TComboBox) getComponent("SESSION_CODE");
//        String sc = sessionCode.getSelectedName();
        String sessionSql = "SELECT SESSION_DESC FROM REG_SESSION WHERE SESSION_CODE = '"+SD1+"' AND ADM_TYPE='"+ADM_TYPE+"'";
        TParm sc = new TParm(TJDODBTool.getInstance().select(sessionSql));
        
        String content = "����ԤԼ�ɹ�"+
        				this.getValue("start_Date").toString().substring(0, 10).replace("-", "/")+" "+
        				sc.getValue("SESSION_DESC", 0)+
        				"��"+ZH1+"��"+
        				YSZ+"ҽ��������";
        
        this.messageBox(content);
        content += "������"+this.getValueString("PAT_NAME")+"���ˣ�����ȡ��������ǰһ�첦�����绰4001568568��Ϊ�˱�֤��׼ʱ���������ǰ����Һ�����";
        parm.addData("Content", content);
        parm.addData("TEL1", this.getValueString("TEL_HOME"));
        TIOM_AppServer.executeAction(
    			"action.reg.REGAction", "orderMessage", parm);
        //===huangtt 2013/11/01 end
        
        //huangtt start 20121105 
        this.onInitTable3();
        onChangeDR();
        this.callFunction("UI|SAVE|setEnabled", false);
      //huangtt end 20121105
    
    }

    /**
     * VIP�Һ���Ϣ
     * @param date Timestamp
     * @return int
     */
    public TParm QueryVip(String ADM_DATE, String CLINICROOM_NO, String DR_CODE,String SESSION_CODE,String DEPT_CODE,String ADM_TYPE) {
        TParm tpVip = new TParm(TJDODBTool.getInstance().select(REGAdmForDRTool.
                QueryVip(ADM_DATE,DR_CODE,SESSION_CODE,DEPT_CODE,ADM_TYPE)));
       // this.messageBox_(tpVip);
        return tpVip;
    }

    /**
     * ���ݵ�ǰ���ڣ���ý��������ڼ�
     * @param date Timestamp
     * @return int
     */
    public int getWeek(Timestamp date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * ���ݵ�ǰ���ڣ���õ�ǰʱ��
     * @param date Timestamp
     * @return int
     */
    public String getSession_code(Timestamp date) {
        String time = date.toString().substring(11, 19);
        String sql = "SELECT SESSION_CODE, START_REG_TIME, END_REG_TIME "
            + " FROM REG_SESSION WHERE REGION_CODE = '" +
            Operator.getRegion() + "' AND ADM_TYPE = 'O'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

        for (int i = 0; i < parm.getCount("SESSION_CODE"); i++) {
            if (time.compareTo(parm.getValue("START_REG_TIME", i)) >= 0 &&
                time.compareTo(parm.getValue("END_REG_TIME", i)) <= 0) {
                return parm.getValue("SESSION_CODE", i);
            }
        }

        return "";
    }

    /**
     * ���������˵��ĵ���¼�
     */
    public void onChange() {
        TParm parm = QueryDRName(this.getValueString("SESSION_CODE"),
                                 this.getValue("KS").toString(),
                                 this.getValue("start_Date").toString().
                                 substring(0, 10).replace("-", ""));
        //System.out.println("-----------"+parm);
        this.getComboBox("cbx_DRName").setParmValue(parm);
    }

    /**
     * ҽ�������˵�����¼�
     */
    public void onChangeDR() {
    	this.getTTable("Table2").removeRowAll(); //add by huangtt 20131108
    	this.getTTable("Table1").removeRowAll(); //add by huangtt 20131108
        String DR_CODE = this.getValue("cbx_DRName").toString();
        if (DR_CODE.length() > 0) {
            TParm tpram = this.QueryDR(this.getValue("start_Date").toString().
                                       substring(0, 10).replace("-", ""),
                                       this.getValue("KS").toString(), DR_CODE,this.getValueString("SESSION_CODE"),ADM_TYPE); //modify by huangtt 20131101  this.getValue("KS").toString()
            this.getTTable("Table1").setParmValue(tpram);
            RS = tpram.getValue("QUE_NO", 0); //add by huangtt ��������
            //add by huangtt start 2013/10/30
            TParm p = this.QueryVip(this.getValue("start_Date").toString().
                    substring(0, 10).replace("-", ""),
                    Operator.getStation(),this.getValueString("cbx_DRName"),
                    this.getValueString("SESSION_CODE"),this.getValue("KS").toString(),ADM_TYPE);
            
            this.getTTable(" Table2").setParmValue(p);
            // add by huangtt end 2013/10/30

        }
        else {
            TParm tpram = this.QueryDRCount(this.getValue("start_Date").
                                            toString().
                                            substring(0, 10).replace("-", ""),
                                            this.getValue("KS").toString(),   //modify by huangtt 20131101  this.getValue("KS").toString()
                                            this.getValueString("SESSION_CODE"),ADM_TYPE); //add by huangtt 20131105
            this.getTTable("Table1").setParmValue(tpram);
            TParm p = this.QueryVip(this.getValue("start_Date").toString().
                                    substring(0, 10).replace("-", ""),
                                    Operator.getStation(),this.getValueString("cbx_DRName"),
                                    this.getValueString("SESSION_CODE"),this.getValue("KS").toString(),ADM_TYPE); //add by huangtt 2013/10/30
            this.getTTable(" Table2").setParmValue(p);
        }
    }


    /**
     * ��ѯҽ�� ��ͨ�Һ���Ϣ�����ˣ�
     */
    public TParm QueryDR(String stateDate, String dept, String DR_CODE, String sessionCode,String admType) {
        TParm tparm = new TParm();
        tparm = new TParm(TJDODBTool.getInstance().select(REGAdmForDRTool.
                QueryDRSql(stateDate, dept, DR_CODE, sessionCode,admType)));
        return tparm;

    }
    /**
     * ��ѯҽ�� ��ͨ�Һ���Ϣ�����ˣ�
     */
    public TParm QueryDRP(String stateDate, String clinicroomNo, String drCode, String sessionCode,String admType) {
        TParm tparm = new TParm();
        tparm = new TParm(TJDODBTool.getInstance().select(REGAdmForDRTool.
           QueryDRPSql(stateDate, clinicroomNo, drCode, sessionCode,admType)));
        return tparm;

    }


    /**
     * ��ѯҽ��VIP�Һ���Ϣ�����ˣ�
     */
    public TParm QueryDRVIP(String stateDate, String dept, String DR_CODE, String sessionCode,String admType) {
        TParm tparm = new TParm();
        tparm = new TParm(TJDODBTool.getInstance().select(REGAdmForDRTool.
                QueryDRSql(stateDate, dept, DR_CODE, sessionCode,admType)));

        return tparm;

    }
    /**
     * ��ӡ
     * @param caseNo
     */
    private void onPrint(String caseNo)
    {
      Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
      Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
  
      TTabbedPane tp = (TTabbedPane)getComponent("tTabbedPane_3");
      int que = reg.getQueNo();
      String sql1 = "SELECT B.CHN_DESC NAME FROM REG_CLINICROOM A, SYS_DICTIONARY B WHERE A.LOCATION = B.ID AND B.GROUP_ID = 'SYS_LOCATION'  AND A.CLINICROOM_NO='" + 
        getValueString("CLINICROOM_NO") + "'";
      TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
      String dept = "";
      if (!parm1.getValue("NAME", 0).equals("")) {
        dept = "(" + parm1.getValue("NAME", 0) + ")";
      }
      TParm data = new TParm();
      data.setData("TITLE", "TEXT", "");
      //������
      data.setData("MR_NO", "TEXT", getValueString("MR_NO"));
      //����
      data.setData("PAT_NAME", "TEXT", getValueString("PAT_NAME"));
      //�Ա�
      data.setData("PAT_SEX", "TEXT", pat.getSexString());
      //ԤԼʱ��
      TComboBox ts = (TComboBox)getComponent("SESSION_CODE");
      data.setData("APPOINTMENT", "TEXT", getText("start_Date").toString().replace('/', '-') + " " + ts.getSelectedName());
      //���
      data.setData("QUE_NO", "TEXT", Integer.valueOf(que));
      //����
      data.setData("DEPT", "TEXT", getText("KS") + dept);
      //����
      String area = reg.getAdmRegion();
      String sql2 = "SElECT CLINIC_DESC FROM REG_CLINICAREA WHERE CLINICAREA_CODE='" + area + "'";
      TParm areaParm = new TParm(TJDODBTool.getInstance().select(sql2));
      data.setData("AREA", "TEXT", areaParm.getValue("CLINIC_DESC", 0));
      if (tp.getSelectedIndex() == 1) {
        TTable table2 = (TTable)getComponent("Table2");
        int row2 = table2.getSelectedRow();
        TParm parm2 = table2.getShowParmValue();
        String clinicroom_no = parm2.getValue("CLINICROOM_NO", row2);
        //ҽ��
        data.setData("DR", "TEXT", parm2.getValue("DR_CODE", row2));
        //����
        data.setData("ORG_CODE", "TEXT", clinicroom_no);
        StringBuffer time = new StringBuffer("");
        time.append(parm2.getValue("START_TIME", row2).substring(0, 2));
        time.append(":");
        time.append(parm2.getValue("START_TIME", row2).substring(2, 4));
        //Ԥ�ƾ���ʱ��
        data.setData("TIME", "TEXT", time);
      } else {
        TTable table1 = (TTable)getComponent("Table1");
        int row1 = table1.getSelectedRow();
        TParm parm3 = table1.getShowParmValue();
        String clinicroom_no = parm3.getValue("CLINICROOM_NO", row1);
        //ҽ��
        data.setData("DR", "TEXT", parm3.getValue("DR_CODE", row1));
        //����
        data.setData("ORG_CODE", "TEXT", clinicroom_no);
        //Ԥ�ƾ���ʱ��
        data.setData("TIME", "TEXT", "");
      }
      //˵��
      data.setData("DESCRIPTION", "TEXT", "");
      //��ӡ����
      data.setData("DATE", "TEXT", StringTool.getTimestamp(new Date()).toString().substring(0, 19));
      openPrintWindow("%ROOT%\\config\\prt\\REG\\NEWREG.jhw", data);
    }
    /**
     * ��ӡ
     * 
     */
    public void onDoublePrint()
    {
      TTable table = (TTable)getComponent("Table3");
      int row = table.getSelectedRow();
      TParm result = table.getParmValue();
      String case_no = result.getValue("CASE_NO", row);
      Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
      Reg reg = Reg.onQueryByCaseNo(pat, case_no);
      int que = reg.getQueNo();
      String sql1 = "SELECT B.CHN_DESC NAME FROM REG_CLINICROOM A, SYS_DICTIONARY B WHERE A.LOCATION = B.ID AND B.GROUP_ID = 'SYS_LOCATION'  AND A.CLINICROOM_NO='" + 
        getValueString("CLINICROOM_NO") + "'";
      TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
      String dept = "";
      if (!parm1.getValue("NAME", 0).equals("")) {
        dept = "(" + parm1.getValue("NAME", 0) + ")";
      }
      TParm data = new TParm();
      //������
      data.setData("MR_NO", "TEXT", getValueString("MR_NO"));
      //����
      data.setData("PAT_NAME", "TEXT", getValueString("PAT_NAME"));
      //�Ա�
      data.setData("PAT_SEX", "TEXT", pat.getSexString());
      //ԤԼʱ��
      TComboBox ts = (TComboBox)getComponent("SESSION_CODE");
      data.setData("APPOINTMENT", "TEXT", getText("start_Date").toString().replace('/', '-') + " " + ts.getSelectedName());
      //���
      data.setData("QUE_NO", "TEXT", Integer.valueOf(que));
      //����
      data.setData("DEPT", "TEXT", getText("KS") + dept);
      
      String area = reg.getAdmRegion();
      String sql2 = "SElECT CLINIC_DESC FROM REG_CLINICAREA WHERE CLINICAREA_CODE='" + area + "'";
      TParm areaParm = new TParm(TJDODBTool.getInstance().select(sql2));
      //����
      data.setData("AREA", "TEXT", areaParm.getValue("CLINIC_DESC", 0));
      //ҽ��
      String doctor = result.getValue("DR_CODE", row);
      String doctor_sql = "SELECT B.USER_NAME FROM REG_PATADM A,SYS_OPERATOR B WHERE A.CASE_NO='" + case_no + "' AND A.DR_CODE=B.USER_ID";
      TParm doctorParm = new TParm(TJDODBTool.getInstance().select(doctor_sql));
      data.setData("DR", "TEXT", doctorParm.getValue("USER_NAME", 0));
      //����
      String room = "SELECT A.CLINICROOM_DESC FROM REG_CLINICROOM A,REG_PATADM B  WHERE B.CASE_NO='" + 
        case_no + "' AND B.CLINICROOM_NO=A.CLINICROOM_NO ";
      TParm roomParm = new TParm(TJDODBTool.getInstance().select(room));
      System.out.println("CLINICROOM_DESC+++" + roomParm.getValue("CLINICROOM_DESC", 0));
      data.setData("ORG_CODE", "TEXT", roomParm.getValue("CLINICROOM_DESC", 0));
      //�ж�VIP���VIP
      String vipsql = "SELECT B.PROF_FLG FROM REG_PATADM A,REG_CLINICTYPE B WHERE A.CASE_NO='" + case_no + "'" + 
        " AND A.ADM_TYPE=B.ADM_TYPE AND A.CLINICTYPE_CODE=B.CLINICTYPE_CODE";
      TParm parm = new TParm(TJDODBTool.getInstance().select(vipsql));
      System.out.println("VIP_FLG+++" + parm.getValue("PROF_FLG", 0));
      String vip_flg = parm.getValue("PROF_FLG", 0);
      if (vip_flg.equals("Y"))//vip
      {
        String timesql = "SELECT B.START_TIME FROM REG_PATADM A,REG_CLINICQUE B WHERE A.CASE_NO='" + case_no + "'" + 
          " AND A.ADM_TYPE=B.ADM_TYPE" + 
          " AND TO_CHAR(A.ADM_DATE,'YYYYMMdd')=B.ADM_DATE" + 
          " AND A.SESSION_CODE=B.SESSION_CODE" + 
          " AND A.CLINICROOM_NO=B.CLINICROOM_NO" + 
          " AND A.QUE_NO=B.QUE_NO";
        //Ԥ�ƾ���ʱ��
        TParm timeParm = new TParm(TJDODBTool.getInstance().select(timesql));
        StringBuffer time = new StringBuffer("");
        time.append(timeParm.getValue("START_TIME", 0).substring(0, 2));
        time.append(":");
        time.append(timeParm.getValue("START_TIME", 0).substring(2, 4));
        data.setData("TIME", "TEXT", time);
      }
      else {//��VIP
    	  //Ԥ�ƾ���ʱ��
        data.setData("TIME", "TEXT", "");
      }
      //˵��
      data.setData("DESCRIPTION", "TEXT", " ");
      //��ӡ����
      data.setData("DATE", "TEXT", StringTool.getTimestamp(new Date()).toString().substring(0, 19));
      openPrintWindow("%ROOT%\\config\\prt\\REG\\NEWREG.jhw", data);
    }
}
