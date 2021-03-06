package com.javahis.ui.adm;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import jdo.adm.ADMResvTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import jdo.adm.ADMInpTool;
import jdo.bil.BILPayTool;

import com.dongyang.data.TNull;
import java.sql.Timestamp;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import java.util.Vector;

import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;
import com.dongyang.util.StringTool;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

import com.javahis.system.textFormat.TextFormatCLPBscInfo;
import jdo.clp.BscInfoTool;
import jdo.clp.CLPSingleDiseTool;
import jdo.ekt.EKTIO;

 

/**
 * <p>Title: ԤԼסԺ</p>
 *
 * <p>Description: ԤԼסԺ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis </p>
 *
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMResvControl
    extends TControl {
    Pat pat;
    private TParm parmEKT; // ҽ�ƿ���Ϣadd by huangjw 20140728
    TParm recParm = new TParm(); //�Ӳ�
    String type = "NEW"; //���棬���¿���ʹ��
    String resvNo = ""; //ԤԼ����
    String BED_NO = ""; //ԤԼ����
    String McaseNo = ""; //ĸ�׾������
    String adm_type_zyz = "";//��ӡסԺ֤ʱ���ż�ס����yanj��20130820
    boolean save = true;
    Timestamp birthDay=null;//��������
    Timestamp sysDate = SystemTool.getInstance().getDate();
    public void onInit() {
        init();
        //ֻ��text���������������ICD10������
        callFunction("UI|DIAG_CODE|setPopupMenuParameter", "aaa",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        //textfield���ܻش�ֵ
        callFunction("UI|DIAG_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        setValue("APP_DATE", SystemTool.getInstance().getDate()); //Ԥ������
                        setValue("RESV_DATE",StringTool.rollDate(SystemTool.getInstance().getDate(),1)); //ԤԼסԺ��
                    }
                    catch (Exception e) {
                    }
                }
            });
        this.setMenu(false);
        //���ղ���
        //������MR_NO --������;
        //     ADM_SOURCE --������Դ
        //     DEPT_CODE --�ż��ﲿ��
        //     DR_CODE --�ż���ҽʦ
        //     ICD_CODE --���CODE
        //     DESCRIPTION --��ϱ�ע
        // ���Բ���
//        TParm parm = new TParm();
//        parm.setData("MR_NO","000000000092");
//        parm.setData("ADM_SOURCE","01");
//        parm.setData("DEPT_CODE","10101");
//        parm.setData("DR_CODE","D001");
//        parm.setData("ICD_CODE","Q44");
//        parm.setData("DESCRIPTION","��ע");
//        Object obj = parm;
        this.setValue("LUMPWORK_CODE", "");
        Object obj = this.getParameter();
        TParm recptParm = new TParm();
        if (obj instanceof TParm) {
            recptParm = (TParm) obj;
            this.initUI(recptParm);
        }
    }

    /**
     * ����������ó�ʼ��
     * MR_NO,ADM_SOURCE,OPD_DEPT_CODE,OPD_DR_CODE,DIAG_CODE
     * @param parm TParm
     */
    public void initUI(TParm parm) {
    	adm_type_zyz = parm.getValue("ADM_TYPE_ZYZ");//yanj,20130820��ʼ��
    
    	
        setValue("MR_NO", parm.getData("MR_NO"));
        onMRNO();
        if (parm.getValue("ADM_SOURCE").length() > 0) {
            setValue("ADM_SOURCE", parm.getValue("ADM_SOURCE"));
            callFunction("UI|ADM_SOURCE|setEnabled", false);
        }
        if (parm.getValue("ADM_SOURCE").equals("02")) {// ���� add by wanglong 20121025
            TParm param = new TParm();
            param.setData("CASE_NO", parm.getValue("CASE_NO"));
            TParm diseCode = CLPSingleDiseTool.getInstance().queryREGAdmSDInfo(param);
            if (diseCode.getErrCode() < 0) {
                messageBox("��������Ϣ��ȡ����" + diseCode.getErrText());
                return;
            } else {
                setValue("DISE_CODE", diseCode.getValue("DISE_CODE", 0));
            }
        }
        if (!parm.checkEmpty("DEPT_CODE", parm)) { // �ż��ﲿ��
            setValue("OPD_DEPT_CODE", parm.getValue("DEPT_CODE"));
            callFunction("UI|OPD_DEPT_CODE|setEnabled", false);
        }
        if (!parm.checkEmpty("DR_CODE", parm)) { // �ż���ҽʦ
            setValue("OPD_DR_CODE", parm.getValue("DR_CODE"));
            callFunction("UI|OPD_DR_CODE|setEnabled", false);
        }
        // ��ϱ�ע
        setValue("DIAG_REMARK", parm.getValue("DESCRIPTION"));
        // �ż������
        setValue("DIAG_CODE", parm.getValue("ICD_CODE"));
        setValue("DIAG_DESC", this.getName(parm.getValue("ICD_CODE")));
        // callFunction("UI|save|setEnabled", true); //���� chenxi
    }

    /**
     * Menu ���̿���
     */
    public void setMenu(boolean check) {
        if (check) {
            callFunction("UI|save|setEnabled", true); // ����
            callFunction("UI|notify|setEnabled", true); // ԤԼ֪ͨ
            callFunction("UI|stop|setEnabled", true); // ȡ��ԤԼ
            callFunction("UI|child|setEnabled", true); // ������
        } else {
            callFunction("UI|save|setEnabled", false); // ����
            callFunction("UI|notify|setEnabled", false); // ԤԼ֪ͨ
            //callFunction("UI|stop|setEnabled", false); // ȡ��ԤԼ
            callFunction("UI|child|setEnabled", false); // ������
        }
    }

    /**
     * �õ����desc
     * @param code String
     * @return String
     */
    public String getName(String code) {
        if (code == null) return code;
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
        if (dataStore == null) return code;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector v = (Vector) parm.getData("ICD_CODE");
        Vector d = (Vector) parm.getData("ICD_CHN_DESC");
        Vector e = (Vector) parm.getData("ICD_ENG_DESC");
        int count = v.size();
        for (int i = 0; i < count; i++) {
            if (code.equals(v.get(i))) {
                if ("en".equals(this.getLanguage())) {
                    return "" + e.get(i);
                } else {
                    return "" + d.get(i);
                }
            }
        }
        return code;
    }

    /**
     * ����¼�
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if(parm==null){
            this.setValue("DIAG_CODE", "");
            this.setValue("DIAG_DESC", "");
        }else{
            this.setValue("DIAG_CODE", parm.getValue("ICD_CODE"));
            this.setValue("DIAG_DESC", parm.getValue("ICD_CHN_DESC"));
            this.grabFocus("DIAG_REMARK");
        }
    }

    /**
	 * ��ҽ�ƿ�add by huangjw 20140728
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
		this.onQuery();
	}

    /**
     * ��ѯ
     */
    public void onQuery() {
        this.onMRNO();
    }

    /**
     * �����Żس���ѯ�¼�
     * ���û��סԺ�ţ�ͨ��ȡ��ԭ��ȡ��סԺ��
     */
    public void onMRNO() {
        String mrNo = getValueString("MR_NO").trim();
        if (!this.queryPat(mrNo)) return;
        TParm parm = new TParm();
        parm.setData("MR_NO", this.getValue("MR_NO"));
        // ��鲡���Ƿ�סԺ��
        if (this.checkAdmInp()) {
            this.messageBox("E0121");
            this.setMenu(false);
            //
            callFunction("UI|print|setEnabled", false); // סԺ֤
            //
            return;
        }
        parm.setData("IN_CASE_FLG","Y");
        // ���ݲ����� ��ѯ�ò��˵�����ԤԼ��Ϣ
        TParm result = ADMResvTool.getInstance().selectAll(parm);
        //System.out.println("--------ȡ����CAN_CLERK-------"+result.getValue("CAN_CLERK", 0));
        //�ж��Ƿ��Ѿ�ԤԼ ��ԤԼ���Ų���û����Ժû�в���IN_CASE_NO����ԤԼ�еĲ���
        //pangben 2014-7-30 �޸��ײ�ԤԼ���ܣ����Զ��ԤԼ���������ԤԼ�����ݵ����������ѡ��ش�
        if (!"".equals(result.getValue("RESV_NO", 0))
        		&&"".equals(result.getValue("IN_CASE_NO", 0))&&"".equals(result.getValue("CAN_CLERK", 0))) {
            int check;
            if ("en".equals(this.getLanguage())) check =
                    this.messageBox("Message", "The patient has an appointment��Whether to amend��", 0);
            else check = this.messageBox("��Ϣ", "�˲�����ԤԼ���Ƿ��ٴδ���ԤԼ��", 0);
            if (check == 0) {
                type = "NEW";
                this.setMenu(true);
            } else {
            	TParm reParm =new TParm();
				if (result.getCount() > 1) {
					reParm.setData("MR_NO",this.getValue("MR_NO"));
					reParm.setData("PAT_NAME",pat.getName());
					reParm.setData("SEX_CODE",pat.getSexCode());
					reParm = (TParm) this.openDialog(
							"%ROOT%\\config\\adm\\ADMInQuery.x", reParm);
					if (reParm == null) {
						this.setMenu(false);
						this.callFunction("UI|NEW_BORN_FLG|setEnabled", false);
						return;
					}
					
				} else if (result.getCount() == 1) {
					reParm=result.getRow(0);
				}
                this.setValueForParm("RESV_NO;CELL_PHONE;TEL;TEL_NO1;CTZ1_CODE;ADM_SOURCE;OPD_DEPT_CODE;OPD_DR_CODE;PATIENT_CONDITION;DIAG_CODE;DIAG_REMARK;"
                        + "CLNCPATH_CODE;ADM_DAYS;URG_FLG;BED_CLASS_CODE;BILPAY;OPER_DESC;OPER_DATE;REMARK",
                        reParm);
				this.setValue("LUMPWORK_CODE", reParm.getData("LUMPWORK_CODE"));// ==liling 20140512 add LUMPWORK_CODE
				parm.setData("RESV_NO",reParm.getValue("RESV_NO"));
				TParm result1 = CLPSingleDiseTool.getInstance()
						.queryADMResvSDInfo(parm);// add by wanglong 20121025
				this.setValueForParm("DISE_CODE", result1, 0);// add by wanglong
																// 20121025
				if (reParm.getData("RESV_DATE") != null) {
					this.setValue("RESV_DATE", reParm.getTimestamp("RESV_DATE"));
				}
				this.setValue("DEPT_CODE", reParm.getValue("DEPT_CODE"));
				this.setValue("STATION_CODE", reParm
						.getValue("STATION_CODE"));
				this.setValue("DR_CODE", reParm.getValue("DR_CODE"));
				BED_NO = reParm.getValue("BED_NO");
				if (reParm.getValue("BED_NO").length() > 0) {
					this.setValue("BED_NO", StringUtil.getDesc("SYS_BED",
							"BED_NO_DESC", "BED_NO='"
									+ reParm.getValue("BED_NO") + "'"));
				}
				// �õ��������
				String desc = this.getName(getValue("DIAG_CODE").toString());
				this.setValue("DIAG_DESC", desc);
				resvNo = reParm.getValue("RESV_NO");
				this.setValue("NEW_BORN_FLG", reParm.getBoolean("NEW_BORN_FLG"));// ������ע��
				McaseNo = reParm.getValue("M_CASE_NO");// ĸ�׵�case_no
				this.setMenu(false);
				// add by wangbin 20150107 סԺ֤���޸�
				callFunction("UI|save|setEnabled", true); // ����
				this.callFunction("UI|NEW_BORN_FLG|setEnabled", false);
				return;
			}
		} else {
            type = "NEW";
            pat = Pat.onQueryByMrNo(mrNo);
            this.setValue("CTZ1_CODE", pat.getCtz1Code());
            // �����ѡ�� ������ע�� �����ĸӤ��Ӧ����
            if (this.getValueBoolean("NEW_BORN_FLG")) {
                this.onChild();
            }
            callFunction("UI|save|setEnabled", true); // ����
        }
    }
 
    /**
     * ��ѯ������Ϣ
     */
    public boolean queryPat(String mrNo) {
        this.setMenu(false); //MENU ��ʾ����
        pat = new Pat();
        pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            this.setMenu(false); //MENU ��ʾ����
            this.messageBox("E0081");
            return false;
        }
        //modifiy by liming begin.��Ӳ����źϲ���ʾ��Ϣ.
        String allMrNo = PatTool.getInstance().checkMrno(mrNo) ;
        if(mrNo!=null && !allMrNo.equals(pat.getMrNo())){
        	//============xueyf modify 20120307 start
        	messageBox("������"+allMrNo+" �Ѻϲ���"+pat.getMrNo()) ;
        	//============xueyf modify 20120307 stop
        }
        //modify by liming end.
        setValue("MR_NO", pat.getMrNo());
        setValue("IPD_NO", pat.getIpdNo());

        setValue("SEX_CODE", pat.getSexCode());
        setValue("CTZ1_CODE", pat.getCtz1Code());
        setValue("CELL_PHONE",pat.getCellPhone());//add by huangjw 20140729
        setValue("TEL", pat.getTelHome());
        setValue("TEL_NO1", pat.getTelCompany());
        setValueForParm("SPECIAL_DIET", pat.getParm());//������ʳ-duzhw add
        if("en".equals(this.getLanguage())){
            setValue("PAT_NAME", pat.getName1());
            if (pat.getBirthday() != null)
                setValue("AGE",
                         DateUtil.showAge(pat.getBirthday(), sysDate));
/*            setValue("AGE",
            		StringTool.CountAgeByTimestamp(pat.getBirthday(),
            				SystemTool.getInstance().getDate())[0] + " Y");
*/        }else{
            setValue("PAT_NAME", pat.getName());
            if (pat.getBirthday() != null)
                setValue("AGE",DateUtil.showAge(pat.getBirthday(), sysDate));
/*            setValue("AGE",
            		com.javahis.util.StringUtil.showAge(pat.getBirthday(),
            				SystemTool.getInstance().getDate()));
*/        }
        callFunction("UI|save|setEnabled", true); //����
        callFunction("UI|MR_NO|setEnabled", false); //������
        return true;
    }

    /**
     * ����ԤԼ�������
     * ��������
     */
    public void onManage() {
//        TParm parm = pat.getParm();
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\customquery\\QueryView.x",
            new Object[] {"ADMRESV_VIEW"});
        if (reParm == null) {
            return;
        }
        setValue("MR_NO", reParm.getData("MR_NO")); //�ż���Ʊ�
        this.onMRNO();
    }

    /**
     * �ж�updata �� insert
     * @param parm TParm
     * @return boolean
     */
    public void onSave() {
        if ("NEW".equals(type))
            this.insertData();
        else {
            //TParm parm = pat.getParm();
            //TParm result = ADMResvTool.getInstance().selectAll(parm);
            this.upDate();
            //modify by lim 2012/05/31 begin
          
            //modify by lim 2012/05/31 end
        }
        TParm data = this.readData();
        updateSYSPatInfo(data);
    }


    /**
     * ����
     */
    public void insertData() {
        if (!checkData())
            return;
        TParm parm = this.readData(); //�õ�������Ϣ
        resvNo = new String();
        resvNo = SystemTool.getInstance().getNo("ALL", "ADM", "RESV_NO",
                                                "RESV_NO"); //����ȡ��ԭ��
        if (resvNo == null || "".equals(resvNo)) {
            this.messageBox("E0122");
            return;
        } 
        parm.setData("RESV_NO", resvNo); //ԤԼ��
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(
            "action.adm.ADMResvAction","insertdata", parm); // סԺ�ǼǱ���

        //�ж��Ƿ�����ٴ�·��
        TParm inBscParm = new TParm();
        inBscParm.setData("ICD_CODE", this.getValueString("DIAG_CODE"));
        TParm bscParm = BscInfoTool.getInstance().existBscinfo(inBscParm);
        int clpCount = bscParm.getCount("CLNCPATH_CODE");
        String clpPath = "";
        if ("".equals(this.getValueString("CLNCPATH_CODE"))) {
            if (clpCount > 0) {
            clpPath = bscParm.getValue("CLNCPATH_CODE");
            this.messageBox("�������" + clpPath + "�ٴ�·��");
            }
        }
        else {
            if (clpCount > 0) {
                clpPath = bscParm.getValue("CLNCPATH_CODE");
                if (!this.getValueString("CLNCPATH_CODE").equals(clpPath))
                    this.messageBox("����϶�Ӧ�ٴ�·����һ��");
            }
        }
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");
            return;
        }
        else {
            if (!this.getValueString("DISE_CODE").trim().equals("")) {// add by wanglong 20121025
                updateADMResvSDInfo();//���뵥������Ϣ
            }
            type = "UPDATE";
            setValue("RESV_NO", resvNo); //ԤԼ����Ϊ
            this.setMenu(true);
            this.messageBox("P0005");
            onPrint();
        }
    }
      

    /**
     * �޸�ԤԼ��Ϣ
     */
    public void upDate() { 
        if (!checkData())
            return;
        TParm data = this.readData(); //�õ�������Ϣ
        TParm resv = ADMResvTool.getInstance().selectAll(data);
        data.setData("RESV_NO", resv.getData("RESV_NO", 0));
        data.setData("OPT_USER", Operator.getID());
        data.setData("OPT_TERM", Operator.getIP());
        
        TParm result = TIOM_AppServer.executeAction(
            "action.adm.ADMResvAction",
            "upDate", data); // סԺ�ǼǱ���
        if (result.getErrCode() < 0)
            this.messageBox("E0005");
        else
            this.messageBox("P0005");
        
        if (!this.getValueString("DISE_CODE").trim().equals("")) {// add by wanglong 20121025
            updateADMResvSDInfo();//���뵥������Ϣ
        }
        onPrint() ;
    }

    /**
     * סԺ֤��ӡ
     * @param parm TParm
     */
	public void onPrint() {
		if (StringUtils.isEmpty(resvNo)) {
			this.messageBox("���ȱ���");
			return;
		}
		this.print();
	}
    
	/**
	 * ��ӡ
	 */
	private void print() {
		String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
		String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO='" + resvNo + "'";
		sql += " AND CLASS_CODE='" + classCode + "' AND  SUBCLASS_CODE='" + subClassCode + "'";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		String path, fileName, seq;
		boolean flg;// �������Ƿ���ڡ���ʶ
		if (result1.getCount() < 0) {
			path = TConfig.getSystemValue("ADMEmrINHOSPPATH");
			fileName = TConfig.getSystemValue("ADMEmrINHOSPFILENAME");
			seq = "";
			flg = false;
		} else {
			path = result1.getValue("FILE_PATH", 0);
			fileName = result1.getValue("FILE_NAME", 0);
			seq = result1.getValue("FILE_SEQ", 0);
			flg = true;
		}
		//
		TParm p = new TParm();
		p.setData("RESV_NO", resvNo);
		TParm resvPrint = ADMResvTool.getInstance().selectFroPrint(p);
		//
		TParm actionParm = new TParm();
		actionParm.setData("MR_NO", pat.getMrNo());
		actionParm.setData("IPD_NO", pat.getIpdNo());
		actionParm.setData("PAT_NAME", pat.getName());
		actionParm.setData("SEX", pat.getSexString());
		actionParm.setData("AGE", DateUtil.showAge(pat.getBirthday(), sysDate)); // ����
		Timestamp ts = SystemTool.getInstance().getDate();
		actionParm.setData("CASE_NO", resvNo);
		actionParm.setData("ADM_TYPE", "O");
		actionParm.setData("DEPT_CODE", resvPrint.getValue("DEPT_CODE", 0));
		actionParm.setData("STATION_CODE", resvPrint.getValue("STATION_CODE", 0));
		actionParm.setData("ADM_DATE", ts);// ��ӡ����
		actionParm.setData("STYLETYPE", "1");
		actionParm.setData("RULETYPE", "3");
		actionParm.setData("SYSTEM_TYPE", "ODO");
		//
		TParm emrFileData = new TParm();
		emrFileData.setData("TEMPLET_PATH", path);
		emrFileData.setData("EMT_FILENAME", fileName);
		emrFileData.setData("FILE_PATH",path)  ;
		emrFileData.setData("FILE_NAME",fileName) ;
		emrFileData.setData("SUBCLASS_CODE", subClassCode);
		emrFileData.setData("CLASS_CODE", classCode);
		emrFileData.setData("FILE_SEQ", seq);
		emrFileData.setData("FLG", flg);
		//
		actionParm.setData("EMR_FILE_DATA", emrFileData);
		actionParm.setData("ADM_TYPE_ZYZ", adm_type_zyz);// 20130820,yanj
		actionParm.setData("RESV_NO", resvNo);// ԤԼ��
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
		// yanj,20130820,��ӵ�Ϊ�ż���ʱ��ӡ���Զ��رոý���
		if (adm_type_zyz.equals("O") || adm_type_zyz.equals("E")) {
			this.closeWindow();
			return;
		}
	}
    


    /**
     * ��ȡ������Ϣ
     * @return TParm
     */
    public TParm readData() {
        TParm parm = new TParm();
        parm.setData("MR_NO", getValue("MR_NO")); //������
        parm.setData("SEX_CODE", getValue("SEX_CODE")); //�Ա�
        parm.setData("ADM_SOURCE", getValue("ADM_SOURCE")); //������Դ
        parm.setData("APP_DATE", getValue("APP_DATE")); //Ԥ������
        parm.setData("BED_CLASS_CODE", getValue("BED_CLASS_CODE")); //�����ȼ�
        parm.setData("RESV_DATE", getValue("RESV_DATE")); //ԤԼסԺ��
        parm.setData("ADM_DAYS", getValue("ADM_DAYS")); //ԤԼ����
        parm.setData("URG_FLG", getValue("URG_FLG")); //����ע��
        parm.setData("BED_CLASS_CODE", getValue("BED_CLASS_CODE")); //�����ȼ�
        parm.setData("DEPT_CODE", getValue("DEPT_CODE")); //�Ʊ����
        parm.setData("STATION_CODE", getValue("STATION_CODE")); //סԺ����
        parm.setData("DR_CODE", getValue("DR_CODE")); //ҽʦ����
        parm.setData("BED_NO", BED_NO); //��λ��
        parm.setData("DIAG_CODE", getValue("DIAG_CODE")); //�����
        parm.setData("DIAG_REMARK", getValue("DIAG_REMARK")); //����ϱ�ע
        parm.setData("OPD_DEPT_CODE", this.getValueString("OPD_DEPT_CODE")); //�ż������
        parm.setData("OPD_DR_CODE", this.getValueString("OPD_DR_CODE")); //�ż���ҽʦ
        TNull timeNull = new TNull(Timestamp.class);
        if (getValue("OPER_DATE") == null)
            parm.setData("OPER_DATE", timeNull); //Ԥ����������
        else
            parm.setData("OPER_DATE", getValue("OPER_DATE")); //Ԥ����������
        parm.setData("OPER_DESC", getValue("OPER_DESC")); //��������
        parm.setData("TEL", getValue("TEL")); //�绰
        parm.setData("TEL_NO1", getValue("TEL_NO1")); //�绰1
        parm.setData("CELL_PHONE",getValue("CELL_PHONE"));//�ֻ���  add by huangjw 20140729
        parm.setData("BILPAY", getValueDouble("BILPAY")); //Ԥ����
        parm.setData("CTZ1_CODE", getValue("CTZ1_CODE")); //��ݱ�
        parm.setData("CLNCPATH_CODE", getValue("CLNCPATH_CODE")==null?new TNull(String.class ):getValue("CLNCPATH_CODE")); //�ٴ�·������DRG_CODE
        parm.setData("DRG_CODE", getValue("DRG_CODE")); //DRG_CODE
        parm.setData("PATIENT_CONDITION", getValue("PATIENT_CONDITION")); //����״��
        parm.setData("REMARK", getValue("REMARK")); //���ע������ CONFIRM_NO
        parm.setData("CONFIRM_NO", getValue("CONFIRM_NO")); //�ʸ�֤����
        parm.setData("NOTIFY_TIMES", getValueInt("NOTIFY_TIMES")); //֪ͨ����
        parm.setData("NOTIFY_DATE", getValue("NOTIFY_DATE")); //���һ��֪ͨ����
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("RESV_NO", getValue("RESV_NO"));
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("NEW_BORN_FLG",this.getValueString("NEW_BORN_FLG"));//������ע��
        parm.setData("M_CASE_NO",McaseNo);//ĸ�׵Ĳ�����
        parm.setData("SPECIAL_DIET", getValue("SPECIAL_DIET"));//������ʳ-add duzhw
        //
        if (getValue("LUMPWORK_CODE") == null)
            parm.setData("LUMPWORK_CODE", new TNull(String.class)); //�����ײ�
        else
            parm.setData("LUMPWORK_CODE", getValue("LUMPWORK_CODE")); 
       // parm.setData("LUMPWORK_CODE", this.getValue("LUMPWORK_CODE"));//==liling 20140513 �����ײ�
        return parm;
    }

    /**
     * ���ҳ������
     * @return boolean
     */
    public boolean checkData() {
        if(this.getValue("CTZ1_CODE")==null||
            "".equals(this.getValue("CTZ1_CODE"))){
            this.messageBox("E0123");
            this.grabFocus("CTZ1_CODE");
            return false;
        }
        if (this.getValue("ADM_SOURCE") == null ||
            "".equals(this.getValue("ADM_SOURCE"))) {
            this.messageBox("E0124");
            this.grabFocus("ADM_SOURCE");
            return false;
        }
        if (this.getValue("PATIENT_CONDITION") == null ||
            "".equals(this.getValue("PATIENT_CONDITION"))) {
            this.messageBox("E0125");
            this.grabFocus("PATIENT_CONDITION");
            return false;
        }
        if (this.getValue("DIAG_CODE") == null ||
            "".equals(this.getValue("DIAG_CODE"))) {
            this.messageBox("E0126");
            this.grabFocus("DIAG_CODE");
            return false;
        }
        // add by wangbin 20150107 ��������Ŀ�û���������ʱ��ʾ�û�������ȷ�������
		if (StringUtils.isEmpty(this.getValueString("DIAG_DESC"))) {
			this.messageBox("E0126");
			this.grabFocus("DIAG_CODE");
			return false;
		}
        if (this.getValue("RESV_DATE") == null ||
            "".equals(this.getValue("RESV_DATE"))) {
            this.messageBox("E0127");
            this.grabFocus("RESV_DATE");
            return false;
        }
        //begin modify by liming 2012/02/17
        if (this.getValue("DEPT_CODE") == null ||
            "".equals(this.getValue("DEPT_CODE"))) {
            //this.messageBox("E0128");
        	this.messageBox("������Ԥ��סԺ����");
            this.grabFocus("DEPT_CODE");
            return false;
        }
        
       
        if (this.getValue("STATION_CODE") == null ||
                "".equals(this.getValue("STATION_CODE"))) {
                this.messageBox("������Ԥ��סԺ����");
                this.grabFocus("STATION_CODE");
                return false;
            }  
        
        // modify by yangjj 20160526
        /*
        if (this.getValue("DR_CODE") == null ||
                "".equals(this.getValue("DR_CODE"))) {
                this.messageBox("������Ԥ��סԺҽʦ");
                this.grabFocus("DR_CODE");
                return false;
            } 
        */
                    
        //end
        Timestamp time = (Timestamp)this.getValue("RESV_DATE");
        Timestamp now = SystemTool.getInstance().getDate();
        int re = StringTool.getDateDiffer(time, now);
        if (re < 0) {
            this.messageBox("E0129");
            return false;
        }
        return true;
    }

    /**
     * ����Ƿ�סԺ�� true סԺ�� false δסԺ
     * @return boolean
     */
    public boolean checkAdmInp() {
        TParm parm = new TParm();
        parm.setData("MR_NO", this.getValue("MR_NO"));
        TParm result = ADMInpTool.getInstance().checkAdmInp(parm);
        if (result.checkEmpty("IPD_NO", result))
            return false;
        return true;
    }

    /**
     * ���
     */
    public void onClear() {
        resvNo = new String();
        pat = new Pat();
        McaseNo = "";
        this.setMenu(false);
        callFunction("UI|MR_NO|setEnabled", true); //����
        clearValue("RESV_NO;MR_NO;IPD_NO;AGE;SEX_CODE;PAT_NAME;OPD_DEPT_CODE;OPD_DR_CODE;DIAG_CODE;DIAG_DESC;DIAG_REMARK;" +
                   "ADM_DAYS;STATION_CODE;DR_CODE;BED_CLASS_CODE;OPER_DATE;OPER_CODE;OPER_DESC;CAN_CLERK;CAN_DATE;BILPAY;" +
                   "CLNCPATH_CODE;REMARK;NOTIFY_TIMES;NOTIFY_DATE;CAN_REASON_CODE;ADM_SOURCE;CTZ1_CODE;PR_DEPT_CODE;CELL_PHONE;TEL;"+//huangjw 20140729 add CELL_PHONE
                   "TEL_NO1;PATIENT_CONDITION;URG_FLG;DEPT_CODE;BED_NO;DRG_CODE;CONFIRM_NO;OPER_DATE;"+
                   "NEW_BORN_FLG;CLNCPATH_DESC;DISE_CODE;SPECIAL_DIET");//����������ʳSPECIAL_DIET
        clearValue("LUMPWORK_CODE");//==liling 20140513 add �����ײ�
        this.grabFocus("MR_NO");
        setValue("APP_DATE", SystemTool.getInstance().getDate()); //Ԥ������
        setValue("RESV_DATE",StringTool.rollDate(SystemTool.getInstance().getDate(),1)); //ԤԼסԺ��
        this.callFunction("UI|NEW_BORN_FLG|setEnabled",true);
        //
        callFunction("UI|print|setEnabled", true); // סԺ֤
        //
    }

    /**
     * ��λ����
     */
    public void onBedNo() {
        TParm sendParm = new TParm();
        sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
        sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
        sendParm.setData("TYPE", "RESV") ;   //chenxi modify 20130301 
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\adm\\ADMQueryBed.x", sendParm);
        if(reParm==null){
            return;
        }
        this.setValue("BED_NO", reParm.getValue("BED_NO_DESC", 0)); //��ʾ��������
        BED_NO = reParm.getValue("BED_NO", 0); //��¼����
    }

    /**
     * ֪ͨ
     */
    public void onNotify() {
        resvNo = getValue("RESV_NO").toString();
        if (resvNo == null || "".equals(resvNo)) {
            this.messageBox("E0130");
            return;
        }
        if (this.checkAdmInp()) {
            this.messageBox("E0131");
            this.setMenu(false);
            return;
        }
        TParm sendParm = new TParm();
        sendParm.setData("RESV_NO", resvNo);
        sendParm.setData("MR_NO", getValue("MR_NO"));
        sendParm.setData("PAT_NAME", getValue("PAT_NAME"));
        sendParm.setData("OPT_USER", Operator.getID());
        sendParm.setData("OPT_TERM", Operator.getIP());

        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\adm\\ADMResvNotify.x", sendParm);
    }
    /**
     * У��Ԥ�������
     * @return boolean
     */
    private boolean checkBilPay(String caseNo) {
        TParm parm = BILPayTool.getInstance().selBilPayLeft(caseNo);
        if (parm.getErrCode() < 0) {
            return false;
        }
        if (parm.getDouble("PRE_AMT", 0) > 0) {
            return false;
        }

        return true;
    }
    /**
     * ȡ��ԤԼ
     */
    public void onCanResv() {
        TParm sendParm = new TParm();
        if (resvNo.length()<=0) {
			this.messageBox("��ѡ��Ҫȡ����ԤԼ��Ϣ");
			return;
		}
        sendParm.setData("RESV_NO", resvNo);
        sendParm.setData("MR_NO", getValue("MR_NO"));
        sendParm.setData("PAT_NAME", getValue("PAT_NAME"));
        sendParm.setData("OPT_USER", Operator.getID());
        sendParm.setData("OPT_TERM", Operator.getIP());
        sendParm.setData("BED_NO", BED_NO);
        if (this.checkAdmInp()) {
            this.messageBox("E0131");
            this.setMenu(false);
            return;
        }
        TParm result=ADMResvTool.getInstance().selectAll(sendParm);
        if (result.getValue("LUMPWORK_CASE_NO",0).length()>0) {
        	if (!checkBilPay(result.getValue("LUMPWORK_CASE_NO",0))) {
                this.messageBox_("�˲�������Ԥ����δ��,����ȡ��ԤԼ");
                return;
            }
        	sendParm.setData("LUMPWORK_CASE_NO",result.getValue("LUMPWORK_CASE_NO",0));
		}
        TParm can = (TParm)this.openDialog(
            "%ROOT%\\config\\adm\\ADMCanResv.x", sendParm);
        if (can == null) {
            return;
        }
        if (can.getValue("CAN").equals("true")) { //�������ֵΪ��true�� ��ȡ��ԤԼ�����������Ϣ
            this.onClear();
        }
    }

    /**
     * ����Cobbo�¼�
     */
    public void onDeptCode() {
        //���ұ仯����combo���Ӧ�仯����Ҫ��ղ���combo��ѡ��ֵ�ʹ�λ��
        this.clearValue("STATION_CODE;BED_NO;DR_CODE");
        BED_NO = "";
    }

    /**
     * ����combo�¼�
     */
    public void onStation() {
        //��մ�λ
        this.clearValue("BED_NO");
        BED_NO = "";
    }

    /**
     * ������� Ϊ��ʱ ��������Ĳ���ʾ
     */
    public void onDiagLost() {
    	// modify by wangbin 20150107 ��ֹ�û��������������
    	String diagCode = this.getValueString("DIAG_CODE");
        if (diagCode.trim().length() <= 0) {
            this.setValue("DIAG_DESC", "");
        } else {
        	TParm parm = new TParm();
        	parm.setData("ICD_CODE", diagCode);
        	parm = ADMResvTool.getInstance().queryDiagnosisInfo(parm);
        	
    		if (parm.getErrCode() < 0) {
    			this.messageBox("��ѯ����ϴ���");
    			err("ERR:" + parm.getErrCode() + parm.getErrText()
    					+ parm.getErrName());
    		}
    		
    		if (parm.getCount() > 0) {
        		// �����û�������������ϴ��������Ӧ���������
        		this.setValue("DIAG_DESC", parm.getValue("ICD_CHN_DESC", 0));
    		} else {
    			this.setValue("DIAG_DESC", "");
    		}
        }
    }
    /**
     * ����Ʊ�Combo�¼�
     */
    public void onOPD_DEPT_CODE(){
        //�������ҽʦ��ѡ��ֵ
        this.clearValue("OPD_DR_CODE");
    }
    /**
     * �������Ǽ�
     */
    public void onChild() {
        if (pat == null) {
            this.messageBox("û�в�����Ϣ��");
            return;
        }
        TParm sendParm = new TParm();
        sendParm.setData("MR_NO", this.getValue("MR_NO"));
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\adm\\ADMBabyFlg.x", sendParm);
        if (reParm == null){
            setValue("NEW_BORN_FLG", "N");
            return;
        }
        if (reParm.checkEmpty("IPD_NO", reParm)) {
            setValue("NEW_BORN_FLG", "N");
        }
        else {
            setValue("NEW_BORN_FLG", "Y");
            McaseNo = reParm.getData("M_CASE_NO").toString();
        }
    }
    /**
     * ������ע���¼�
     */
    public void onNEW_BORN_FLG(){
        if(this.getValueBoolean("NEW_BORN_FLG")){
            if(!"".equals(this.getValueString("MR_NO"))){
                //�ж��Ƿ��Ѿ���ѯ����,����Ѳ�ѯ��ôֱ�ӵ���ĸӤ��Ӧ����  �����Ȳ�ѯ������Ϣ
                if (pat!=null&&!"".equals(pat.getMrNo()))
                    this.onChild();
                else
                    this.onMRNO();
            }
        }
    }
    /**
     * ���ò�����Ϣ����
     */
    public void onPatInfo() {
        TParm parm = new TParm();
        parm.setData("RESV", "RESV");
        parm.setData("MR_NO", this.getValueString("MR_NO").trim());
        TParm result = (TParm)this.openDialog("%ROOT%\\config\\sys\\SYSPatInfo.x",parm);
        if("".equals(this.getValueString("MR_NO"))){
            this.setValue("MR_NO",result.getValue("MR_NO"));
            this.onMRNO();
        }
    }
    /**
     * �����ٴ�·��
     */
    public void setClncpathDesc(){
        TextFormatCLPBscInfo clncPath = (TextFormatCLPBscInfo)this.getComponent("CLNCPATH_CODE");
//        System.out.println("�ٴ�·��"+clncPath.getValue());
//        System.out.println("�ٴ�·��1111"+this.getText("CLNCPATH_CODE"));
//        System.out.println("�ٴ�·��222"+clncPath.getPopupMenuData());
//        System.out.println("�ٴ�·��33333"+clncPath.getName());
//        System.out.println("�ٴ�·��4444444"+this.getName("CLNCPATH_CODE"));
//        System.out.println("�ٴ�·��55555"+clncPath.getSelectText());
//        System.out.println("�ٴ�·��666666"+clncPath.getShowColumnList());
    setValue("CLNCPATH_DESC",clncPath.getText());
    }
    
    /**
     * ���뵥������Ϣ
     */
    public void updateADMResvSDInfo() {// add by wanglong 20121025
        TParm action = new TParm();
        action.setData("RESV_NO", resvNo);
        action.setData("DISE_CODE", this.getValue("DISE_CODE")+"");
        TParm result = CLPSingleDiseTool.getInstance().updateADMResvSDInfo(action);
        if (result.getErrCode() < 0) {
            messageBox("��������Ϣ����ʧ��");
            return;
        }
    }
    
    public void updateSYSPatInfo(TParm data){
    	String sql="UPDATE SYS_PATINFO SET TEL_HOME='"+data.getValue("TEL")+"',CELL_PHONE='"+data.getValue("CELL_PHONE")+"'" +
    			" WHERE MR_NO='"+data.getValue("MR_NO")+"'";
    	TParm result =new TParm(TJDODBTool.getInstance().update(sql));
    	if(result.getErrCode()<0)
    		this.messageBox(result.getErrText());
    }
    /** 
     * ��������
     */
/*    public String setBirth(Timestamp birthDay) {
    	
    		//birthDay=(Timestamp) getValue("BIRTH_DATE");
    	
        Timestamp sysDate = SystemTool.getInstance().getDate();
        Timestamp temp = birthDay == null ? sysDate : birthDay;
        String age = "0";
        age = DateUtil.showAge(temp, sysDate);
        setValue("AGE", age);
        return age;
    }   */
    
	/**
	 * ��ʷסԺ֤��ӡ
	 */
	public void onPrintHistory() {
		if(StringUtils.isEmpty(this.getValueString("MR_NO"))) {
			this.messageBox("�����벡����");
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		this.openWindow("%ROOT%\\config\\adm\\ADMInpHistory.x", parm);
	}
}
