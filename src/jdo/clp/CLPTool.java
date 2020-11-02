package jdo.clp;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.jdo.*;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPTool extends TJDODBTool {
    /**
     * ʵ��
     */
    public static CLPTool instanceObject;
    /**
     * �õ�ʵ��
     * @return IBSTool
     */
    public static CLPTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPTool();
        return instanceObject;
    }
    /**
     * �õ���������
     * @param tableName String
     * @param caseNo String
     * @return TParm
     */
    public TParm getCheckFile(String tableName,String caseNo){
        String sql = "SELECT * FROM " + tableName +
            " WHERE HOSP_AREA='HIS' AND CASE_NO='" + caseNo + "'";
        //system.out.println("sql==" + sql);
        TParm result = new TParm(select(sql));
        return result;
    }
    /**
     * �����ٴ�·������
     * @param tableType String
     * @param parm TParm
     * @return TParm
     */
    public TParm insertCLPData(String tableType,TParm parm){
        TParm result = new TParm();
        String sql = "";
        if ("AMI".equals(tableType)) {
            sql = "INSERT INTO CLP_AMI(HOSP_AREA,CASE_NO,MR_NO,IPD_NO,IN_DATE,DEPT_CODE,DR_CODE,AMI_1,AMI_2,AMI_3,AMI_3_1,AMI_3_2,AMI_3_3,AMI_4,AMI_5,AMI_5_1,AMI_6,AMI_7,AMI_7_1,AMI_7_2,AMI_7_3,TOT_AMT,MED_FEE,INTERVENE_FEE,FILE_NO,FILE_PATH,FILE_NAME,OPT_USER,OPT_DATE,OPT_TERM) VALUES ('" +
                parm.getValue("HOSP_AREA") + "','" + parm.getValue("CASE_NO") +
                "','" + parm.getValue("MR_NO") + "','" + parm.getValue("IPD_NO") +
                "','" + parm.getValue("IN_DATE") + "','" +
                parm.getValue("DEPT_CODE") + "','" + parm.getValue("DR_CODE") +
                "','" + parm.getValue("AMI_1") + "','" + parm.getValue("AMI_2") +
                "','" + parm.getValue("AMI_3") + "','" +
                parm.getValue("AMI_3_1") + "','" + parm.getValue("AMI_3_2") +
                "','" + parm.getValue("AMI_3_3") + "','" +
                parm.getValue("AMI_4") + "','" + parm.getValue("AMI_5") + "','" +
                parm.getValue("AMI_5_1") + "','" + parm.getValue("AMI_6") +
                "','" + parm.getValue("AMI_7") + "','" +
                parm.getValue("AMI_7_1") + "','" + parm.getValue("AMI_7_2") +
                "','" + parm.getValue("AMI_7_3") + "'," +
                parm.getValue("TOT_AMT") + "," + parm.getValue("MED_FEE") +
                "," + parm.getValue("INTERVENE_FEE") + ",'"
                + parm.getValue("FILE_NO") + "','" + parm.getValue("FILE_PATH") +
                "','" + parm.getValue("FILE_NAME") + "','" +
                parm.getValue("OPT_USER") + "','" + parm.getValue("OPT_DATE") +
                "','" + parm.getValue("OPT_TERM") + "')";
        }
        if ("HF".equals(tableType)) {
            sql = "INSERT INTO CLP_HF(HOSP_AREA,CASE_NO,FILE_NO,FILE_PATH,FILE_NAME,MR_NO,IPD_NO,IN_DATE,DEPT_CODE,DR_CODE,HF_1,HF_1_1,HF_2,HF_3,HF_4,HF_5,HF_6,HF_7,HF_8,HF_9,HF_9_1,HF_9_2,HF_9_3,HF_9_4,TOT_AMT,MED_FEE,OPT_USER,OPT_DATE,OPT_TERM) VALUES ('" +
                parm.getValue("HOSP_AREA") + "','" + parm.getValue("CASE_NO") +
                "','" + parm.getValue("FILE_NO") + "','" +
                parm.getValue("FILE_PATH") + "','" + parm.getValue("FILE_NAME") +
                "','" + parm.getValue("MR_NO") + "','" + parm.getValue("IPD_NO") +
                "','" + parm.getValue("IN_DATE") + "','" +
                parm.getValue("DEPT_CODE") + "','" + parm.getValue("DR_CODE") +
                "','" + parm.getValue("HF_1") + "','" + parm.getValue("HF_1_1") +
                "','" + parm.getValue("HF_2") + "','" + parm.getValue("HF_3") +
                "','" + parm.getValue("HF_4") + "','" + parm.getValue("HF_5") +
                "','" + parm.getValue("HF_6") + "','" + parm.getValue("HF_7") +
                "','" + parm.getValue("HF_8") + "','" + parm.getValue("HF_9") +
                "','" + parm.getValue("HF_9_1") + "','" +
                parm.getValue("HF_9_2") + "','" + parm.getValue("HF_9_3") +
                "','" + parm.getValue("HF_9_4") + "'," +
                parm.getValue("TOT_AMT") + "," + parm.getValue("MED_FEE"
                ) + ",'" + parm.getValue("OPT_USER") + "','" +
                parm.getValue("OPT_DATE") + "','" + parm.getValue("OPT_TERM") +
                "')";
        }
        if ("CABG".equals(tableType)) {
            sql = "INSERT INTO CLP_CABG(HOSP_AREA,CASE_NO,FILE_NO,FILE_PATH,FILE_NAME,MR_NO,IPD_NO,IN_DATE,DEPT_CODE,DR_CODE,CABG_1,CABG_1_1,CABG_1_2,CABG_1_3,CABG_2,CABG_2_1,CABG_2_2,CABG_3,CABG_3_1,CABG_3_2,CABG_4,CABG_4_1,CABG_4_2,CABG_4_3,CABG_4_4,CABG_5,CABG_6,CABG_6_1,CABG_6_2,CABG_6_3,CABG_6_4,CABG_6_5,CABG_6_6,CABG_7,CABG_8,CABG_9,TOT_AMT,MED_FEE,OPERATION_FEE,OPT_USER,OPT_DATE,OPT_TERM) VALUES ('" +
                parm.getValue("HOSP_AREA") + "','" + parm.getValue("CASE_NO") +
                "','" + parm.getValue("FILE_NO") + "','" +
                parm.getValue("FILE_PATH") + "','" + parm.getValue("FILE_NAME") +
                "','" + parm.getValue("MR_NO") + "','" + parm.getValue("IPD_NO") +
                "','" + parm.getValue("IN_DATE") + "','" +
                parm.getValue("DEPT_CODE") + "','" + parm.getValue("DR_CODE") +
                "','" + parm.getValue("CABG_1") + "','" +
                parm.getValue("CABG_1_1") + "','" + parm.getValue("CABG_1_2") +
                "','" + parm.getValue("CABG_1_3") + "','" +
                parm.getValue("CABG_2") + "','" + parm.getValue("CABG_2_1") +
                "','" + parm.getValue("CABG_2_2") + "','" +
                parm.getValue("CABG_3") + "','" + parm.getValue("CABG_3_1") +
                "','" + parm.getValue("CABG_3_2"
                ) + "','" + parm.getValue("CABG_4") + "','" +
                parm.getValue("CABG_4_1") + "','" + parm.getValue("CABG_4_2") +
                "','" + parm.getValue("CABG_4_3") + "','" +
                parm.getValue("CABG_4_4") + "','" + parm.getValue("CABG_5") +
                "','" + parm.getValue("CABG_6") + "','" +
                parm.getValue("CABG_6_1") + "','" + parm.getValue("CABG_6_2") +
                "','" + parm.getValue("CABG_6_3") + "','" +
                parm.getValue("CABG_6_4") + "','" + parm.getValue("CABG_6_5") +
                "','" + parm.getValue("CABG_6_6") + "','" +
                parm.getValue("CABG_7") + "','" + parm.getValue("CABG_8") +
                "','" + parm.getValue("CABG_9") + "'," +
                parm.getValue("TOT_AMT") + "," + parm.getValue("MED_FEE") +
                "," + parm.getValue("OPERATION_FEE") + ",'" +
                parm.getValue("OPT_USER") + "','" + parm.getValue("OPT_DATE") +
                "','" + parm.getValue("OPT_TERM") + "')";
        }
        //system.out.println("sql===insert"+sql);
        result.setData(this.update(sql));
        return result;
    }
    /**
     * �����ٴ�·��
     * @param tableType String
     * @param parm TParm
     * @return TParm
     */
    public TParm updateCLPData(String tableType,TParm parm){
        TParm result = new TParm();
        String sql = "";
        if ("AMI".equals(tableType)) {
            sql = "UPDATE CLP_AMI SET AMI_1='"+parm.getValue("AMI_1")+"',AMI_2='"+parm.getValue("AMI_2")+"',AMI_3='"+parm.getValue("AMI_3")+"',AMI_3_1='"+parm.getValue("AMI_3_1")+"',AMI_3_2='"+parm.getValue("AMI_3_2")+"',AMI_3_3='"+parm.getValue("AMI_3_3")+"',AMI_4='"+parm.getValue("AMI_4")+"',AMI_5='"+parm.getValue("AMI_5")+"',AMI_5_1='"+parm.getValue("AMI_5_1")+"',AMI_6='"+parm.getValue("AMI_6")+"',AMI_7='"+parm.getValue("AMI_7")+"',AMI_7_1='"+parm.getValue("AMI_7_1")+"',AMI_7_2='"+parm.getValue("AMI_7_1")+"',AMI_7_3='"+parm.getValue("AMI_7_3")+"',OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_DATE='"+parm.getValue("OPT_DATE")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"' WHERE HOSP_AREA='HIS' AND CASE_NO='"+parm.getValue("CASE_MO")+"'";
        }
        if ("HF".equals(tableType)) {
            sql = "UPDATE CLP_HF SET HF_1='"+parm.getValue("HF_1")+"',HF_1_1='"+parm.getValue("HF_1_1")+"',HF_2='"+parm.getValue("HF_2")+"',HF_3='"+parm.getValue("HF_3")+"',HF_4='"+parm.getValue("HF_4")+"',HF_5='"+parm.getValue("HF_5")+"',HF_6='"+parm.getValue("HF_6")+"',HF_7='"+parm.getValue("HF_7")+"',HF_8='"+parm.getValue("HF_8")+"',HF_9='"+parm.getValue("HF_9")+"',HF_9_1='"+parm.getValue("HF_9_1")+"',HF_9_2='"+parm.getValue("HF_9_2")+"',HF_9_3='"+parm.getValue("HF_9_3")+"',HF_9_4='"+parm.getValue("HF_9_4")+"',OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_DATE='"+parm.getValue("OPT_DATE")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"' WHERE HOSP_AREA='HIS' AND CASE_NO='"+parm.getValue("CASE_NO")+"'";
        }
        if ("CABG".equals(tableType)) {
            sql = "UPDATE CLP_CABG SET CABG_1='"+parm.getValue("CABG_1")+"',CABG_1_1='"+parm.getValue("CABG_1_1")+"',CABG_1_2='"+parm.getValue("CABG_1_2")+"',CABG_1_3='"+parm.getValue("CABG_1_3")+"',CABG_2='"+parm.getValue("CABG_2")+"',CABG_2_1='"+parm.getValue("CABG_2_1")+"',CABG_2_2='"+parm.getValue("CABG_2_2")+"',CABG_3='"+parm.getValue("CABG_3")+"',CABG_3_1='"+parm.getValue("CABG_3_1")+"',CABG_3_2='"+parm.getValue("CABG_3_2")+"',CABG_4='"+parm.getValue("CABG_4")+"',CABG_4_1='"+parm.getValue("CABG_4_1")+"',CABG_4_2='"+parm.getValue("CABG_4_2")+"',CABG_4_3='"+parm.getValue("CABG_4_3")+"',CABG_4_4='"+parm.getValue("CABG_4_4")+"',CABG_5='"+parm.getValue("CABG_5")+"',CABG_6='"+parm.getValue("CABG_6")+"',CABG_6_1='"+parm.getValue("CABG_6_1")+"',CABG_6_2='"+parm.getValue("CABG_6_2")+"',CABG_6_3='"+parm.getValue("CABG_6_3")+"',CABG_6_4='"+parm.getValue("CABG_6_4")+"',CABG_6_5='"+parm.getValue("CABG_6_5")+"',CABG_6_6='"+parm.getValue("CABG_6_6")+"',CABG_7='"+parm.getValue("CABG_7")+"',CABG_8='"+parm.getValue("CABG_8")+"',CABG_9='"+parm.getValue("CABG_9")+"',OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_DATE='"+parm.getValue("OPT_DATE")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"' WHERE HOSP_AREA = 'HIS' AND CASE_NO='"+parm.getValue("CASE_NO")+"'";
        }
        //system.out.println("sql===update"+sql);
        result.setData(this.update(sql));
        return result;
    }
    /**
     * 
    * @Title: checkOdiClpExe
    * @Description: TODO(סԺҽ��վУ���һ�ν���·������)
    * @author pangben
    * @return
    * @throws
     */
    public TParm checkOdiClpExe(TParm parm,TControl control){
    	TParm result=CLPManagemTool.getInstance().queryClpManagem(parm);
    	if (result.getErrCode()<0) {
			return result;
		}
    	if (result.getValue("DS_DATE",0).length()>0) {//��Ժ��������ҪУ��
		}else{
			if (null!=result.getValue("CLNCPATH_CODE",0) &&result.getValue("CLNCPATH_CODE",0).length()>0) {//�Ѿ�����·������Ҫ����	
			}else{
				 String sql="SELECT CASE_NO FROM CLP_CAUSE_HISTORY WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'";//��ѯ·��������ԭ��������������˵�����ǵ�һ�ν���ҽ��վ
				 TParm causeParm=new TParm(select(sql));
				 if (causeParm.getCount()>0) {
					return new TParm();
				 }
				 //����֪ʶ�⣬У�������Ƿ���Ҫ����·��
				 sql="SELECT A.CTZ1_CODE,A.IN_DEPT_CODE,B.ICD_CODE,C.ICD_CHN_DESC DIAG_DESC FROM ADM_INP A,ADM_INPDIAG B,SYS_DIAGNOSIS C " +
				 		"WHERE A.CASE_NO=B.CASE_NO AND A.CANCEL_FLG='N' AND B.ICD_CODE=C.ICD_CODE " +
				 		" AND B.MAINDIAG_FLG='Y' AND A.DS_DATE IS NULL AND B.IO_TYPE='I' AND A.CASE_NO='"
				 +parm.getValue("CASE_NO")+"'";
				 TParm icdParm=new TParm(select(sql));
				 if (icdParm.getCount()<=0) {
					return new TParm();
				 }
				 TParm diagParm=new TParm();
				 diagParm.setData("diagCode",icdParm.getValue("ICD_CODE",0));
				 diagParm.setData("deptCode", icdParm.getValue("IN_DEPT_CODE",0));
				 diagParm.setData("ctzCode", icdParm.getValue("CTZ1_CODE",0));
				 TParm returnParm = TIOM_AppServer.executeAction("action.cdss.CDSSAction",
							"fireRule4", diagParm);
				 if (null==returnParm ||null==returnParm.getValue("clncpathCode", 0)
						 ||returnParm.getValue("clncpathCode", 0).length()<=0) {
					return new TParm();
				 }
				 parm.setData("CLNCPATH_CODE",returnParm.getValue("clncpathCode", 0));//֪ʶ���л��·������
				 parm.setData("DIAG_CODE",icdParm.getValue("ICD_CODE",0));
				 parm.setData("DIAG_DESC",icdParm.getValue("DIAG_DESC",0));
				 parm.setData("CLP_NEW_FLG","Y");//��һ�ν���ҽ��վ����������ת�Ʊ�ADM_TRANS_LOG
				 result = (TParm) control.openDialog(
			                "%ROOT%\\config\\clp\\CLPManageMNew.x", parm);
			}
		}
    	return new TParm();
    }
    /**
     * 
    * @Title: intoClpDuration
    * @Description: TODO(ת�ƺ��ٴ�·��������ҽ��վ������Ҫ�޸�ʱ��)
    * @author pangben 2015-8-13
    * @param parm
    * @throws
     */
    public void intoClpDuration(TParm parm,TControl control){
    	String clncPathCode="";
    	StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("SELECT CLNCPATH_CODE  FROM  ADM_INP WHERE CASE_NO ='"+parm.getValue("CASE_NO")+"' AND DS_DATE IS NULL AND CANCEL_FLG = 'N' ");
        TParm temp = new TParm(TJDODBTool.getInstance().select( sqlbf.toString()));
        if (temp.getCount("CLNCPATH_CODE") > 0) {
            clncPathCode = temp.getValue("CLNCPATH_CODE", 0);
        }
        if (clncPathCode.length() <= 0) {
        	return;
        }
        //��ѯ�Ƿ����ת�����ݣ�CLP_SCHD_FLG=��N�� δ����ҽ��վתʱ��
        String sql="SELECT CASE_NO,OUT_DEPT_CODE,IN_DEPT_CODE,IN_STATION_CODE,OUT_STATION_CODE FROM ADM_TRANS_LOG" +
        		" WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND  CLP_SCHD_FLG='N'";
        temp = new TParm(TJDODBTool.getInstance().select(sql));
        if (temp.getCount()>0) {
        	 parm.setData("CLNCPATH_CODE", clncPathCode);
        	 parm.setData("DEPT_FLG","Y");
             TParm result = (TParm) control.openDialog(
                     "%ROOT%\\config\\odi\\ODIintoDuration.x", parm);
		}
    }
    /**
     * 
    * @Title: onCheckClpOverflow
    * @Description: TODO(�ٴ�·�����У�飺1.��������У�� 2.�������У��)
    * @author pangben 2015-8-13
    * @param parm
    * @return
    * @throws
     */
    public void onCheckClpOverflow(TParm parm,TControl control){
    	String sql="SELECT CASE_NO FROM CLP_MANAGEM_HISTORY WHERE END_DTTM IS NOT NULL AND CASE_NO='"+parm.getValue("CASE_NO")+"'";
    	TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
    	if (temp.getCount()>0) {//�Ѿ��������Ҫ����
			return;
		}
    	//��ѯ��Ժ����У�����
    	sql="SELECT A.IN_DATE,B.AVERAGECOST,B.STAYHOSP_DAYS,A.CLNCPATH_CODE,B.DEPT_TYPE_N,B.DEPT_TYPE_W " +
    			" FROM ADM_INP A,CLP_BSCINFO B WHERE A.CLNCPATH_CODE=B.CLNCPATH_CODE " +
    			" AND A.CLNCPATH_CODE IS NOT NULL AND A.DS_DATE IS NULL AND A.CASE_NO='"+parm.getValue("CASE_NO")+"'";
    	temp = new TParm(TJDODBTool.getInstance().select(sql));
    	if (temp.getCount()>0) {
			String caseNo=parm.getValue("CASE_NO");
			String mrNo=parm.getValue("MR_NO");
			//סԺʱ����STAYHOSP_DAYS�Ƚ� ���У���Ƿ񳬹��ٴ�·������סԺ��30�죬�ڿ�У���Ƿ񳬹�����סԺ��һ��
			if (null!=temp.getValue("DEPT_TYPE_N",0)&&temp.getValue("DEPT_TYPE_W",0).equals("Y")) {
				Timestamp nowTime = SystemTool.getInstance().getDate();
				Timestamp inDate=temp.getTimestamp("IN_DATE",0);
				int day=StringTool.getDateDiffer(nowTime, inDate);
				int days=temp.getInt("STAYHOSP_DAYS",0);
				if (day>days+30) {//���У���Ƿ񳬹��ٴ�·������סԺ��30��
					if(control.messageBox("��ʾ", "����·������סԺ��30��,�Ƿ���·�����������", 2)==0){
						onShowClpManageM(control, caseNo, mrNo);
					}
				}
			}else if(null!=temp.getValue("DEPT_TYPE_W",0)&&temp.getValue("DEPT_TYPE_N",0).equals("Y")){//�ڿ�ѡ��У��
				Timestamp nowTime = SystemTool.getInstance().getDate();
				Timestamp inDate=temp.getTimestamp("IN_DATE",0);
				int day=StringTool.getDateDiffer(nowTime, inDate);
				int days=temp.getInt("STAYHOSP_DAYS",0);
				if (day>days*2) {//�ڿ�У���Ƿ񳬹�����סԺ��һ��
					if(control.messageBox("��ʾ", "��������סԺ��һ��,�Ƿ���·�����������", 2)==0){
						onShowClpManageM(control, caseNo, mrNo);
					}
				}
			}
			//}
		}
    }
    /**
     * 
    * @Title: onShowClpManageM
    * @Description: TODO(��ʾ׼��·������)
    * @author pangben
    * @param control
    * @param caseNo
    * @param mrNo
    * @throws
     */
    private void onShowClpManageM(TControl control,String caseNo,String mrNo){
    	 TParm parm = new TParm();
         parm.setData("CLP", "CASE_NO", caseNo);
         parm.setData("CLP", "MR_NO", mrNo);
         parm.setData("CLP", "FLG", "Y");
         TParm result = (TParm) control.openDialog(
                "%ROOT%\\config\\clp\\CLPManagem.x", parm);
    }
    /**
     * 
    * @Title: onCheckOutAdmOpeBook
    * @Description: TODO(�ٴ�·�����У�飺1.��Ժ֪ͨ��У�������ų�״̬)
    * @author pangben 2015-8-13
    * @param parm
    * @return
    * @throws
     */
	public void onCheckOutAdmOpeBook(TParm parm,TControl control) {
		String clncPathCode = "";
		StringBuffer sqlbf = new StringBuffer();
		//��ѯ�˲�������·������
		sqlbf.append("SELECT A.CLNCPATH_CODE,B.CLP_TYPE FROM ADM_INP A,CLP_BSCINFO B WHERE A.CLNCPATH_CODE=B.CLNCPATH_CODE AND A.CASE_NO ='"
				+ parm.getValue("CASE_NO")
				+ "' AND A.DS_DATE IS NULL AND A.CANCEL_FLG = 'N' ");
		TParm temp = new TParm(TJDODBTool.getInstance()
				.select(sqlbf.toString()));
		if (temp.getCount("CLNCPATH_CODE") > 0) {
			clncPathCode = temp.getValue("CLNCPATH_CODE", 0);
		}
		if (clncPathCode.length() <= 0) {
			return;
		}
		if (null==temp.getValue("CLP_TYPE",0)||!temp.getValue("CLP_TYPE",0).equals("2")) {//�������Ͳſ���У�����
			return;
		}
		String caseNo=parm.getValue("CASE_NO");
		String mrNo=parm.getValue("MR_NO");
		//��ѯ�Ƿ���������ų̻�������¼
		String sql="SELECT CASE_NO FROM OPE_OPBOOK WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND CANCEL_FLG<>'Y' ";
		temp = new TParm(TJDODBTool.getInstance()
				.select(sql));
		if (temp.getCount("CASE_NO") <=0) {//��������������
			if(control.messageBox("��ʾ", "û�в�����������,�Ƿ���·�����������", 2)==0){	
				onShowClpManageM(control, caseNo, mrNo);
			}
		}
	}
	/**
	 * 
	* @Title: onCheckOutAmtIbs
	* @Description: TODO(��Ժ·������У�����)
	* @author pangben 2015-8-26
	* @param parm
	* @param control
	* @throws
	 */
	public void onCheckOutAmtIbs(TParm parm,TControl control){
		String sql="SELECT CASE_NO FROM CLP_MANAGEM_HISTORY WHERE END_DTTM IS NOT NULL AND CASE_NO='"
			+parm.getValue("CASE_NO")+"'";
    	TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
    	if (temp.getCount()>0) {//�Ѿ��������Ҫ����
			return;
		}
    	String caseNo=parm.getValue("CASE_NO");
		String mrNo=parm.getValue("MR_NO");
    	//��ѯ��Ժ����У�����
    	sql="SELECT A.IN_DATE,SUM(C.TOT_AMT) TOT_AMT,B.AVERAGECOST,B.STAYHOSP_DAYS,A.CLNCPATH_CODE,B.DEPT_TYPE_N,B.DEPT_TYPE_W " +
    			" FROM ADM_INP A,CLP_BSCINFO B,IBS_ORDD C WHERE A.CLNCPATH_CODE=B.CLNCPATH_CODE AND A.CASE_NO=C.CASE_NO " +
    			" AND A.CLNCPATH_CODE IS NOT NULL AND A.DS_DATE IS NULL AND A.CASE_NO='"+parm.getValue("CASE_NO")+
    			"' GROUP BY A.IN_DATE,B.AVERAGECOST,B.STAYHOSP_DAYS,A.CLNCPATH_CODE,B.DEPT_TYPE_N,B.DEPT_TYPE_W";
    	temp = new TParm(TJDODBTool.getInstance().select(sql));
    	if (temp.getCount()>0) {
			if (temp.getDouble("TOT_AMT",0)>temp.getDouble("AVERAGECOST",0)*2) {//���Ƚϣ�סԺ�ܷ��ø����ٴ�·����׼����һ��ʱ��ʾ���
				if(control.messageBox("��ʾ", "�ܷ��ó���·����׼����һ��,�Ƿ���·�����������", 2)==0){
					onShowClpManageM(control, caseNo, mrNo);
				}
			}
    	}
	}
}
