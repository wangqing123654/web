package action.med;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.med.MedSmsTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.XmlUtil;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title:����action </p>
 *
 * <p>Description:����action </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company:javahis </p>
 *
 * @author ԬС��
 * @version 1.0
 */
public class MedSmsAction extends TAction {

	/**
	 * �������� 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm){
		 writerLog("\n\r");
	     writerLog("��������----------------------------------------------------BEGION"+SystemTool.getInstance().getDate());
	     writerLog("parm--------:"+parm);
		 TConnection connection = getConnection();	
		 TParm result = null;
		 
    	 for(int i = 0 ;  i < parm.getCount() ; i++ ){
    		 TParm parmRow = parm.getRow(i);
	    	 
    		 /**�õ�Application_No*/
	    	 String applicationNo = parmRow.getValue("APPLICATION_NO");
	    	 TParm  applyParm = MedSmsTool.getInstance().getMedApply(applicationNo);
	    	 String admType = applyParm.getValue("ADM_TYPE",0);
	    	 String caseNo = applyParm.getValue("CASE_NO",0);
	    	
	    	 String testitemCode = parmRow.getValue("TESTITEM_CODE");
	    	 //�Ȳ�ѯһ����ȷ���Ƿ񱣴�
	    	 TParm searchSmsParm = new TParm();
	    	 searchSmsParm.setData("APPLICATION_NO",applicationNo);
	    	 searchSmsParm.setData("CASE_NO",caseNo);
	    	 searchSmsParm.setData("TESTITEM_CODE",testitemCode);
	    	 TParm  medParm = MedSmsTool.getInstance().onQueryMedSms(searchSmsParm);
	    	 
	    	 if(medParm.getCount() > 0 ) {
	    		 writerLog("�ظ���������--------:"+searchSmsParm);
	    	 }else {
		    	 /**����ҽ��*/
		    	 TParm telParm = null ;
		    	 TParm directorParm = null;
		    	 String deptCode = "";
		    	 if("O".equals(admType) || "E".equals(admType)){
		    		 telParm = MedSmsTool.getInstance().getRealdrCode(admType,caseNo);
		    		 parmRow.setData("BILLING_DOCTORS",telParm.getValue("REALDR_CODE",0));
		    		 deptCode =  telParm.getValue("DEPT_CODE",0);
		    		 directorParm = MedSmsTool.getInstance().getDeanOrCompementTel(deptCode, "3");
		    		 parmRow.setData("DIRECTOR_DR_CODE",directorParm.getValue("USER_ID",0));
		    	 }else if("I".equals(admType)) {
		    		 telParm = MedSmsTool.getInstance().getVsDrCode(caseNo);
		    		 parmRow.setData("BILLING_DOCTORS",telParm.getValue("VS_DR_CODE",0));
		    		 deptCode =  telParm.getValue("DEPT_CODE",0);
		    		 directorParm = MedSmsTool.getInstance().getDirectorTel(caseNo);
		    		 parmRow.setData("DIRECTOR_DR_CODE",directorParm.getValue("USER_ID",0));
				 }
		    	 
		    	 String deptChnDesc =  "" ;
		    	 if(deptCode != null && !deptCode.equals("")){
		    		 TParm searchParm = new TParm();
		    		 searchParm.setData("DEPT_CODE",deptCode);
		    		 TParm dutyParm = MedSmsTool.getInstance().getDutyTel(searchParm);
		    		 if(dutyParm.getCount()> 0){
		    			 int telParmCount = telParm.getCount() ;
		    			 telParm.setData("TEL1",telParmCount+1,dutyParm.getRow(0).getValue("DUTY_TEL"));
		    			 telParm.setCount(telParmCount+1);
		    			 
		    			 //������������ 
		    			 deptChnDesc = dutyParm.getRow(0).getValue("DEPT_CHN_DESC");
		    			 parmRow.setData("DEPT_CHN_DESC",deptChnDesc);
		    		 }
		    	 }
		    	
		    	 parmRow.setData("STATION_CODE",applyParm.getValue("STATION_CODE",0));
		    	 parmRow.setData("BED_NO",applyParm.getValue("BED_NO",0));
		    	 parmRow.setData("MR_NO", telParm.getValue("MR_NO",0));
		    	 parmRow.setData("DEPT_CODE",deptCode);
		    	 parmRow.setData("IPD_NO",applyParm.getValue("IPD_NO",0));
		    	 parmRow.setData("CASE_NO",caseNo);
		    	 parmRow.setData("ADM_TYPE",admType);
		    	 parmRow.setData("STATE","1");
		    	 parmRow.setData("OPT_USER",Operator.getID());
		    	 parmRow.setData("OPT_TERM",Operator.getIP());
		    	 
		    	 String content = 
		    	 				  parmRow.getValue("TESTITEM_CHN_DESC")+","+parmRow.getValue("TEST_VALUE")+","+parmRow.getValue("CRTCLLWLMT");
		    	 parmRow.setData("SMS_CONTENT",content);
		    	 
		    	 /**ҽ������ܣ�����Ժ��*/
		    	 TParm competentParm = MedSmsTool.getInstance().getDeanOrCompementTel(deptCode, "2");
		    	 TParm deanParm = MedSmsTool.getInstance().getDeanOrCompementTel(deptCode, "1");
		    	 parmRow.setData("COMPETENT_CODE",competentParm.getValue("USER_ID",0));
		    	 parmRow.setData("DEAN_CODE",deanParm.getValue("USER_ID",0));
		    	 
			     result = MedSmsTool.getInstance().insertMedSms(parmRow,connection);
	 
			     if (result.getErrCode() < 0) {
			    	 connection.rollback();
			    	 connection.close();
			            return result;
			    	 /**������ɹ�����д��һ��*/
			     }
			     
			     writeXml(parmRow, telParm, content);
	    	 }
    	 }
    	 writerLog("��������----------------------------------------------------END"+SystemTool.getInstance().getDate());
	     connection.commit();
	     connection.close();
	     return result;
	}

	private void writeXml(TParm parmRow, TParm telParm, String content) {
		//д�ļ�
		 TParm xmlParm = new TParm();
		
		 xmlParm.setData("Content",content);
		 xmlParm.setData("MrNo",telParm.getValue("MR_NO",0));
		 
		 //�õ�����,�ż�ס���
		 String deptChnCode =  parmRow.getValue("DEPT_CHN_DESC");
		 String admType = parmRow.getValue("ADM_TYPE");
		 String admTypeChn = "";
		 admTypeChn = getAdmType(admType);
		 
		 xmlParm.setData("Name",parmRow.getValue("PAT_NAME")+","+deptChnCode+","+admTypeChn);
		// System.out.println("xmlParm:"+xmlParm);
		 //System.out.println("telParm:"+telParm);
		 XmlUtil.createSmsFile(xmlParm, telParm);
	}

	private String getAdmType(String admType) {
		String admTypeChn = "";
		if(admType != null ){
			 if(admType.equals("O")){
				 admTypeChn = "����";
			 }else if(admType.equals("I")) {
				 admTypeChn = "סԺ";
			}else if(admType.equals("E")){
				 admTypeChn = "����";
			}else if(admType.equals("H")){
				 admTypeChn = "�������";
			}
		 }
		return admTypeChn;
	}
	
	
	public static void writerLog(String msg) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String name = "MED_SMS";
	    name +=format.format(new Date());
		File f = new File("C:\\JavaHis\\logs\\"+name+".log");
		BufferedWriter out = null;
		try {
			if (!f.exists()) {
				f.createNewFile();// ���MED_SMS.log�����ڣ��򴴽�һ�����ļ�
			}
			out = new BufferedWriter(new FileWriter(f, true));// ����true��ʾ�����׷�ӵ��ļ����ݵ�ĩβ��������ԭ��������
			out.write(msg);
			out.newLine(); // ����
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// commented by wangqing 20171116
	// connectionû����
	/**
	 * �������� ----- ���ֹ�¼��
	 * @param parm
	 * @return
	 */
	public TParm onSaveBySelf(TParm parm){
		
		TConnection connection = getConnection();
		
		String mrNo = parm.getValue("MR_NO");
		String deptCode = parm.getValue("DEPT_CODE");
		String billDoc = parm.getValue("BILLING_DOCTORS");
		
		parm.setData("APPLICATION_NO", "");
		parm.setData("TESTITEM_CODE", "");
		parm.setData("STATE", "1");
		
		
		TParm telParm = MedSmsTool.getInstance().getDrPhone(billDoc);
		telParm.setData("REALDR_CODE", billDoc);
		telParm.setData("DEPT_CODE", deptCode);
		telParm.setData("MR_NO", mrNo);
		
		String deptChnDesc = "";
		if (deptCode != null && !deptCode.equals("")) {
			TParm searchParm = new TParm();
			searchParm.setData("DEPT_CODE", deptCode);
			TParm dutyParm = MedSmsTool.getInstance()
					.getDutyTel(searchParm);
			if (dutyParm.getCount() > 0) {
				// ������������
				deptChnDesc = dutyParm.getRow(0).getValue("DEPT_CHN_DESC");
				parm.setData("DEPT_CHN_DESC", deptChnDesc);
			}
		}
		
		//���ݺ�̨����ȡ�ÿ�������
		String sql = " SELECT PERSON_CODE FROM MED_SMSDEPT_SETUP " +
					 " WHERE COMPETENT_TYPE = '3' AND DEPT_CODE = '" + deptCode + "'";
		TParm directorParm = new TParm(TJDODBTool.getInstance().select(sql));
		String director = directorParm.getValue("PERSON_CODE", 0);
		parm.setData("DIRECTOR_DR_CODE", director);
		
		String content = parm.getValue("TESTITEM_CHN_DESC") + ","
				+ parm.getValue("TEST_VALUE") + ","
				+ parm.getValue("CRTCLLWLMT");
		parm.setData("SMS_CONTENT", content);

		/** ҽ������ܣ�����Ժ�� */
		TParm competentParm = MedSmsTool.getInstance()
				.getDeanOrCompementTel(deptCode, "2");
		TParm deanParm = MedSmsTool.getInstance().getDeanOrCompementTel(
				deptCode, "1");

		parm.setData("COMPETENT_CODE", competentParm.getValue("USER_ID", 0));
		parm.setData("DEAN_CODE", deanParm.getValue("USER_ID", 0));
		// modified by wangqing 20171116 ���126�汾��������
		TParm result = MedSmsTool.getInstance().insertMedSms(parm, connection); 
		if (result.getErrCode() < 0) {
//			connection.rollback();
			connection.close();
			return result;
		}

		//writeXml(parm, telParm, content);
		
		connection.commit();
	    connection.close();
	    return result;
	}

	/**
	 * ���¶���
	 * @param parm
	 * @return
	 */
	public TParm updateMedSms(TParm parm){
		TConnection conn = getConnection();
		TParm result = MedSmsTool.getInstance().updateMedSms(parm, conn);
		if(result.getErrCode() < 0){
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
	    return result;		
	}











}
