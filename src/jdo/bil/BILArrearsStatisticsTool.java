package jdo.bil;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

import jdo.sys.SystemTool;

import com.dongyang.db.TConnection;

/**
 * <p>Title: �ż���Ƿ��tool��</p>
 *
 * <p>Description: �ż���Ƿ��tool</p>
 *
 * <p>Copyright: Copyright (c) 20170509</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhanglei
 * @version 5.0
 */
public class BILArrearsStatisticsTool extends TJDOTool{

    /**
     * ������
     */
    public BILArrearsStatisticsTool() {
        onInit();
    }

    /**
     * ʵ��
     */
    private static BILArrearsStatisticsTool instanceObject;

    /**
     * �õ�ʵ��
     * @return INFCaseTool
     */
    public static BILArrearsStatisticsTool getInstance() {
        if (instanceObject == null) instanceObject = new BILArrearsStatisticsTool();
        return instanceObject;
    }
    

	/**
	 * ��ý�������
	 */
	public TParm getUiDate(TParm parm){
		TParm result = new TParm();
		TParm ParmKey = new TParm();
		TParm ParmValue = new TParm();
		TParm ParmSet = new TParm();
		String key = "";
		String value = "";
		String set = "";
		//"to_date('"+parm.getData("OPT_DATE")+ "','yyyy-mm-dd hh24:mi:ss'),"
		//ҽԺ��Ⱦ���������ǩ��ʱ��
		if (parm.getValue("INFECTED_DATE").length() > 0){
			//ParmValue.setData("INFECTED_DATE", parm.getValue("INFECTED_DATE"));
			key = key + ",INFECTED_DATE";
			value = value + ",to_date('" + parm.getValue("INFECTED_DATE") + "','yyyy-mm-dd')";
			set = set + ",INFECTED_DATE=to_date('" + parm.getValue("INFECTED_DATE") + "','yyyy-mm-dd')";
			//this.messageBox("ҽԺ��Ⱦ���������ǩ��ʱ��"+ParmValue.getValue("INFECTED_DATE"));
		}
		//��������ʱ��
		if (parm.getValue("REPORT_DATE").length() > 0){
			//ParmValue.setData("INFECTED_DATE", parm.getValue("INFECTED_DATE"));
			key = key + ",REPORT_DATE";
			value = value + ",to_date('" + parm.getValue("REPORT_DATE") + "','yyyy-mm-dd hh24:mi:ss')";
			set = set + ",REPORT_DATE=to_date('" + parm.getValue("REPORT_DATE") + "','yyyy-mm-dd hh24:mi:ss')";
			//this.messageBox("ҽԺ��Ⱦ���������ǩ��ʱ��"+ParmValue.getValue("INFECTED_DATE"));
				}
		//����
		if (parm.getValue("AGE").length() > 0){
			//ParmValue.setData("AGE", parm.getValue("AGE"));
			key = key + ",AGE";
			value = value + ",'" + parm.getValue("AGE") +"'";
			set = set + ",AGE='" + parm.getValue("AGE") +"'";
			//this.messageBox("����"+ParmValue.getValue("AGE"));
		}
		//ʵϰ
		if (parm.getValue("INTERNSHIP").length() > 0){
			//ParmValue.setData("INTERNSHIP", parm.getValue("INTERNSHIP"));
			key = key + ",INTERNSHIP";
			value = value + ",'" + parm.getValue("INTERNSHIP") +"'";
			set = set + ",INTERNSHIP='" + parm.getValue("INTERNSHIP") +"'";
			//this.messageBox("ʵϰ"+ParmValue.getValue("INTERNSHIP"));
		}
		//����
		if (parm.getValue("USER_ID").length() > 0){
			//ParmValue.setData("USER_ID", parm.getValue("USER_ID"));
			key = key + ",USER_ID";
			value = value + ",'" + parm.getValue("USER_ID") +"'";
			set = set + ",USER_ID='" + parm.getValue("USER_ID") +"'";
			//this.messageBox("����"+ParmValue.getValue("USER_ID"));
		}
		//��������
		if (parm.getValue("OCCURRENCE_DATE").length() > 0){
			//ParmValue.setData("OCCURRENCE_DATE", parm.getValue("OCCURRENCE_DATE"));
			//System.out.println("ʱ��::" + ParmValue.getValue("OCCURRENCE_DATE"));
			key = key + ",OCCURRENCE_DATE";
			value = value + ",to_date('" + parm.getValue("OCCURRENCE_DATE") + "','yyyy-mm-dd hh24:mi:ss')";
			set = set + ",OCCURRENCE_DATE=to_date('" + parm.getValue("OCCURRENCE_DATE") + "','yyyy-mm-dd hh24:mi:ss')";
			//this.messageBox("��������"+ParmValue.getValue("OCCURRENCE_DATE"));
		}
		//����
		if (parm.getValue("PAT_NAME").length() > 0){
			//ParmValue.setData("PAT_NAME", parm.getValue("PAT_NAME"));
			key = key + ",PAT_NAME";
			value = value + ",'" + parm.getValue("PAT_NAME") +"'";
			set = set + ",PAT_NAME='" + parm.getValue("PAT_NAME") +"'";
			//this.messageBox("����"+ParmValue.getValue("PAT_NAME"));
		}
		//�Ա�
		if (parm.getValue("SEX_CODE").length() > 0){
			//ParmValue.setData("SEX_CODE", parm.getValue("SEX_CODE"));
			key = key + ",SEX_CODE";
			value = value + ",'" + parm.getValue("SEX_CODE") +"'";
			set = set + ",SEX_CODE='" + parm.getValue("SEX_CODE") +"'";
			//this.messageBox("�Ա�"+ParmValue.getValue("SEX_CODE"));
		}
		//����
		if (parm.getValue("DEPT_CODE").length() > 0){
			//ParmValue.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
			key = key + ",DEPT_CODE";
			value = value + ",'" + parm.getValue("DEPT_CODE") +"'";
			set = set + ",DEPT_CODE='" + parm.getValue("DEPT_CODE") +"'";
			//this.messageBox("����"+ParmValue.getValue("DEPT_CODE"));
		}
		//�绰
		if (parm.getValue("CELL_PHONE").length() > 0){
			//ParmValue.setData("CELL_PHONE", parm.getValue("CELL_PHONE"));
			key = key + ",CELL_PHONE";
			value = value + ",'" + parm.getValue("CELL_PHONE") +"'";
			set = set + ",CELL_PHONE='" + parm.getValue("CELL_PHONE") +"'";
			//this.messageBox("�绰"+ParmValue.getValue("CELL_PHONE"));
		}
		//����
		if (parm.getValue("WORKING_YEARS").length() > 0){
			//ParmValue.setData("WORKING_YEARS", parm.getValue("WORKING_YEARS"));
			key = key + ",WORKING_YEARS";
			value = value + ",'" + parm.getValue("WORKING_YEARS") +"'";
			set = set + ",WORKING_YEARS='" + parm.getValue("WORKING_YEARS") +"'";
			//this.messageBox("����"+ParmValue.getValue("WORKING_YEARS"));
		}
		//���߲�����
		if (parm.getValue("PATIENT_MR_NO").length() > 0){
			//ParmValue.setData("PATIENT_MR_NO", parm.getValue("PATIENT_MR_NO"));
			key = key + ",PATIENT_MR_NO";
			value = value + ",'" + parm.getValue("PATIENT_MR_NO") +"'";
			set = set + ",PATIENT_MR_NO='" + parm.getValue("PATIENT_MR_NO") +"'";
			//this.messageBox("���߲�����"+ParmValue.getValue("PATIENT_MR_NO"));
		}
		//���߿���
		if (parm.getValue("PATIENT_DEPT_CODE").length() > 0){
			//ParmValue.setData("PATIENT_DEPT_CODE", parm.getValue("PATIENT_DEPT_CODE"));
			key = key + ",PATIENT_DEPT_CODE";
			value = value + ",'" + parm.getValue("PATIENT_DEPT_CODE") +"'";
			set = set + ",PATIENT_DEPT_CODE='" + parm.getValue("PATIENT_DEPT_CODE") +"'";
			//this.messageBox("���߿���"+ParmValue.getValue("PATIENT_DEPT_CODE"));
		}
		//�״μ�������
		if (parm.getValue("FIRST_INSPECTION_DATE").length() > 0){
			//ParmValue.setData("FIRST_INSPECTION_DATE", parm.getValue("FIRST_INSPECTION_DATE"));
			key = key + ",FIRST_INSPECTION_DATE";
			value = value + ",to_date('" + parm.getValue("FIRST_INSPECTION_DATE") + "','yyyy-mm-dd')";
			set = set + ",FIRST_INSPECTION_DATE=to_date('" + parm.getValue("FIRST_INSPECTION_DATE") + "','yyyy-mm-dd')";
			//this.messageBox("�״μ�������"+ParmValue.getValue("FIRST_INSPECTION_DATE"));
		}
		//���ߴ�ɸ���
		if (parm.getValue("PATIENT_PASS_SCREENING").length() > 0){
			key = key + ",PATIENT_PASS_SCREENING";
			value = value + ",'" + parm.getValue("PATIENT_PASS_SCREENING") +"'";
			set = set + ",PATIENT_PASS_SCREENING='" + parm.getValue("PATIENT_PASS_SCREENING") +"'";
		}
		//ANTIHIV
		if (parm.getValue("ANTIHIV").length() > 0){
			//ParmValue.setData("ANTIHIV", parm.getValue("ANTIHIV"));
			key = key + ",ANTIHIV";
			value = value + ",'" + parm.getValue("ANTIHIV") +"'";
			set = set + ",ANTIHIV='" + parm.getValue("ANTIHIV") +"'";
			//this.messageBox("ANTIHIV"+ParmValue.getValue("ANTIHIV"));
		}
		//HBSAG
		if (parm.getValue("HBSAG").length() > 0){
			//ParmValue.setData("HBSAG", parm.getValue("HBSAG"));
			key = key + ",HBSAG";
			value = value + ",'" + parm.getValue("HBSAG") +"'";
			set = set + ",HBSAG='" + parm.getValue("HBSAG") +"'";
			//this.messageBox("HBSAG"+ParmValue.getValue("HBSAG"));
		}
		//ANTIHBS
		if (parm.getValue("ANTIHBS").length() > 0){
			//ParmValue.setData("ANTIHBS", parm.getValue("ANTIHBS"));
			key = key + ",ANTIHBS";
			value = value + ",'" + parm.getValue("ANTIHBS") +"'";
			set = set + ",ANTIHBS='" + parm.getValue("ANTIHBS") +"'";
			//this.messageBox("ANTIHBS"+ParmValue.getValue("ANTIHBS"));
		}
		//ANTIHCV
		if (parm.getValue("ANTIHCV").length() > 0){
			//ParmValue.setData("ANTIHCV", parm.getValue("ANTIHCV"));
			key = key + ",ANTIHCV";
			value = value + ",'" + parm.getValue("ANTIHCV") +"'";
			set = set + ",ANTIHCV='" + parm.getValue("ANTIHCV") +"'";
			//this.messageBox("ANTIHCV"+ParmValue.getValue("ANTIHCV"));
		}
		//VDRL
		if (parm.getValue("VDRL").length() > 0){
			//ParmValue.setData("VDRL", parm.getValue("VDRL"));
			key = key + ",VDRL";
			value = value + ",'" + parm.getValue("VDRL") +"'";
			set = set + ",VDRL='" + parm.getValue("VDRL") +"'";
			//this.messageBox("VDRL"+ParmValue.getValue("VDRL"));
		}
		//PATIENT_ANTIHIV
		if (parm.getValue("PATIENT_ANTIHIV").length() > 0){
			//ParmValue.setData("PATIENT_ANTIHIV", parm.getValue("PATIENT_ANTIHIV"));
			key = key + ",PATIENT_ANTIHIV";
			value = value + ",'" + parm.getValue("PATIENT_ANTIHIV") +"'";
			set = set + ",PATIENT_ANTIHIV='" + parm.getValue("PATIENT_ANTIHIV") +"'";
			//this.messageBox("PATIENT_ANTIHIV"+ParmValue.getValue("PATIENT_ANTIHIV"));
		}
		//PATIENT_HBSAG
		if (parm.getValue("PATIENT_HBSAG").length() > 0){
			//ParmValue.setData("PATIENT_HBSAG", parm.getValue("PATIENT_HBSAG"));
			key = key + ",PATIENT_HBSAG";
			value = value + ",'" + parm.getValue("PATIENT_HBSAG") +"'";
			set = set + ",PATIENT_HBSAG='" + parm.getValue("PATIENT_HBSAG") +"'";
			//this.messageBox("PATIENT_HBSAG"+ParmValue.getValue("PATIENT_HBSAG"));
		}
		//PATIENT_ANTIHBS
		if (parm.getValue("PATIENT_ANTIHBS").length() > 0){
			//ParmValue.setData("PATIENT_ANTIHBS", parm.getValue("PATIENT_ANTIHBS"));
			key = key + ",PATIENT_ANTIHBS";
			value = value + ",'" + parm.getValue("PATIENT_ANTIHBS") +"'";
			set = set + ",PATIENT_ANTIHBS='" + parm.getValue("PATIENT_ANTIHBS") +"'";
			//this.messageBox("PATIENT_ANTIHBS"+ParmValue.getValue("PATIENT_ANTIHBS"));
		}
		//PATIENT_ANTIHCV
		if (parm.getValue("PATIENT_ANTIHCV").length() > 0){
			//ParmValue.setData("PATIENT_ANTIHCV", parm.getValue("PATIENT_ANTIHCV"));
			key = key + ",PATIENT_ANTIHCV";
			value = value + ",'" + parm.getValue("PATIENT_ANTIHCV") +"'";
			set = set + ",PATIENT_ANTIHCV='" + parm.getValue("PATIENT_ANTIHCV") +"'";
			//this.messageBox("PATIENT_ANTIHCV"+ParmValue.getValue("PATIENT_ANTIHCV"));
		}
		//PATIENT_VDRL
		if (parm.getValue("PATIENT_VDRL").length() > 0){
			//ParmValue.setData("PATIENT_VDRL", parm.getValue("PATIENT_VDRL"));
			key = key + ",PATIENT_VDRL";
			value = value + ",'" + parm.getValue("PATIENT_VDRL") +"'";
			set = set + ",PATIENT_VDRL='" + parm.getValue("PATIENT_VDRL") +"'";
			//this.messageBox("PATIENT_VDRL"+ParmValue.getValue("PATIENT_VDRL"));
		}
		//������Ʒ�� ��������
		if (parm.getValue("OTHER_TYPE_DESCRIBE").length() >= 0){
			//ParmValue.setData("OTHER_TYPE_DESCRIBE", parm.getValue("OTHER_TYPE_DESCRIBE"));
			key = key + ",OTHER_TYPE_DESCRIBE";
			value = value + ",'" + parm.getValue("OTHER_TYPE_DESCRIBE") +"'";
			set = set + ",OTHER_TYPE_DESCRIBE='" + parm.getValue("OTHER_TYPE_DESCRIBE") +"'";
			//this.messageBox("������Ʒ�� ��������"+Parm.getValue("OTHER_TYPE_DESCRIBE"));
		}
		//������ʱ�Ĳ��� ��������
		if (parm.getValue("OTHER_OPERATION_DESCRIBE").length() >= 0){
			//ParmValue.setData("OTHER_OPERATION_DESCRIBE", parm.getValue("OTHER_OPERATION_DESCRIBE"));
			key = key + ",OTHER_OPERATION_DESCRIBE";
			value = value + ",'" + parm.getValue("OTHER_OPERATION_DESCRIBE") +"'";
			set = set + ",OTHER_OPERATION_DESCRIBE='" + parm.getValue("OTHER_OPERATION_DESCRIBE") +"'";
			//this.messageBox("������ʱ�Ĳ��� ��������"+Parm.getValue("OTHER_OPERATION_DESCRIBE"));
		}
		//������ʱ�Ķ��� ��������
		if (parm.getValue("OTHER_ACTION_DESCRIBE").length() >= 0){
			//ParmValue.setData("OTHER_ACTION_DESCRIBE", parm.getValue("OTHER_ACTION_DESCRIBE"));
			key = key + ",OTHER_ACTION_DESCRIBE";
			value = value + ",'" + parm.getValue("OTHER_ACTION_DESCRIBE") +"'";
			set = set + ",OTHER_ACTION_DESCRIBE='" + parm.getValue("OTHER_ACTION_DESCRIBE") +"'";
			//this.messageBox("������ʱ�Ķ��� ��������"+ParmValue.getValue("OTHER_ACTION_DESCRIBE"));
		}
		//�����˷����ص�
		if (parm.getValue("PLACE_INJURY").length() > 0){
			//Parm.setData("PLACE_INJURY", this.getValue("PLACE_INJURY"));
			key = key + ",PLACE_INJURY";
			value = value + ",'" + parm.getValue("PLACE_INJURY") +"'";
			set = set + ",PLACE_INJURY='" + parm.getValue("PLACE_INJURY") +"'";
			//this.messageBox("�����˷����ص�"+Parm.getValue("PLACE_INJURY"));
		}
		//�����˷�����λ
		if (parm.getValue("POSITION_INJURY").length() > 0){
			//Parm.setData("POSITION_INJURY", this.getValue("POSITION_INJURY"));
			key = key + ",POSITION_INJURY";
			value = value + ",'" + parm.getValue("POSITION_INJURY") +"'";
			set = set + ",POSITION_INJURY='" + parm.getValue("POSITION_INJURY") +"'";
			//this.messageBox("�����˷�����λ"+Parm.getValue("POSITION_INJURY"));
		}
		//��������Ʒ���Ӵ������˵�ѪҺ����Һ��Ⱦ
		if(parm.getValue("CONTACT_POLLUTION").length() > 0){
			key = key + ",CONTACT_POLLUTION";
			value = value + ",'" + parm.getValue("CONTACT_POLLUTION") +"'";
			set = set + ",CONTACT_POLLUTION='" + parm.getValue("CONTACT_POLLUTION") +"'";
		}
		//������ʱ�Ƿ������
		if (parm.getValue("WEAR_GLOVES").length() > 0){
			key = key + ",WEAR_GLOVES";
			value = value + ",'" + parm.getValue("WEAR_GLOVES") +"'";
			set = set + ",WEAR_GLOVES='" + parm.getValue("WEAR_GLOVES") +"'";
		}
		//���˴���
		if (parm.getValue("INJURED").length() > 0){
			key = key + ",INJURED";
			value = value + ",'" + parm.getValue("INJURED") +"'";
			set = set + ",INJURED='" + parm.getValue("INJURED") +"'";
		}
		//���˴����ܹ�
		if (parm.getValue("INJURY_FREQUENCY").length() >= 0){
			//ParmValue.setData("INJURY_FREQUENCY", parm.getValue("INJURY_FREQUENCY"));
			key = key + ",INJURY_FREQUENCY";
			value = value + ",'" + parm.getValue("INJURY_FREQUENCY") +"'";
			set = set + ",INJURY_FREQUENCY='" + parm.getValue("INJURY_FREQUENCY") +"'";
			//this.messageBox("���˴����ܹ�"+ParmValue.getValue("INJURY_FREQUENCY"));
		}
		//�����˺���
		if (parm.getValue("INJURY_TREATMENT").length() > 0){
			key = key + ",INJURY_TREATMENT";
			value = value + ",'" + parm.getValue("INJURY_TREATMENT") +"'";
			set = set + ",INJURY_TREATMENT='" + parm.getValue("INJURY_TREATMENT") +"'";
		}
		//���Ÿ�����
		if (parm.getValue("DEPARTMENT_HEADS").length() > 0){
			//ParmValue.setData("DEPARTMENT_HEADS", parm.getValue("DEPARTMENT_HEADS"));
			key = key + ",DEPARTMENT_HEADS";
			value = value + ",'" + parm.getValue("DEPARTMENT_HEADS") +"'";
			set = set + ",DEPARTMENT_HEADS='" + parm.getValue("DEPARTMENT_HEADS") +"'";
			//this.messageBox("���Ÿ�����"+ParmValue.getValue("DEPARTMENT_HEADS"));
		}
		//���Ÿ�����ǩ��ʱ��
		if (parm.getValue("DEPARTMENT_DATE").length() > 0){
			//ParmValue.setData("DEPARTMENT_DATE", parm.getValue("DEPARTMENT_DATE"));
			key = key + ",DEPARTMENT_DATE";
			value = value + ",to_date('" + parm.getValue("DEPARTMENT_DATE") + "','yyyy-mm-dd')";
			set = set + ",DEPARTMENT_DATE=to_date('" + parm.getValue("DEPARTMENT_DATE") + "','yyyy-mm-dd')";
			//this.messageBox("���Ÿ�����ǩ��ʱ��"+ParmValue.getValue("DEPARTMENT_DATE"));
		}
		//Ԥ������������
		if (parm.getValue("PREVENTION_HEADS").length() > 0){
			//ParmValue.setData("PREVENTION_HEADS", parm.getValue("PREVENTION_HEADS"));
			key = key + ",PREVENTION_HEADS";
			value = value + ",'" + parm.getValue("PREVENTION_HEADS") +"'";
			set = set + ",PREVENTION_HEADS='" + parm.getValue("PREVENTION_HEADS") +"'";
			//this.messageBox("Ԥ������������"+ParmValue.getValue("PREVENTION_HEADS"));
		}
		//Ԥ������������ǩ��ʱ��
		if (parm.getValue("PREVENTION_DATE").length() > 0){
			//ParmValue.setData("PREVENTION_DATE", parm.getValue("PREVENTION_DATE"));
			key = key + ",PREVENTION_DATE";
			value = value + ",to_date('" + parm.getValue("PREVENTION_DATE") + "','yyyy-mm-dd')";
			set = set + ",PREVENTION_DATE=to_date('" + parm.getValue("PREVENTION_DATE") + "','yyyy-mm-dd')";
			//this.messageBox("Ԥ������������ǩ��ʱ��"+ParmValue.getValue("PREVENTION_DATE"));
		}
		//ҽԺ��Ⱦ���������
		if (parm.getValue("INFECTED_HEADS").length() > 0){
			//ParmValue.setData("INFECTED_HEADS", parm.getValue("INFECTED_HEADS"));
			key = key + ",INFECTED_HEADS";
			value = value + ",'" + parm.getValue("INFECTED_HEADS") +"'";
			set = set + ",INFECTED_HEADS='" + parm.getValue("INFECTED_HEADS") +"'";
			//this.messageBox("ҽԺ��Ⱦ���������"+ParmValue.getValue("INFECTED_HEADS"));
		}
		if(parm.getValue("ANTIHIV").equals("1") || parm.getValue("ANTIHBS").equals("1") || 
				parm.getValue("VDRL").equals("1") || parm.getValue("HBSAG").equals("1") || 
				parm.getValue("ANTIHCV").equals("1")){
			//ParmValue.setData("INFECTED_HEADS", parm.getValue("INFECTED_HEADS"));
			key = key + ",INFECTION";
			value = value + ",'Y'";
			set = set + ",INFECTION='Y'";
			//this.messageBox("ҽԺ��Ⱦ���������"+ParmValue.getValue("INFECTED_HEADS"));
		}else{
			key = key + ",INFECTION";
			value = value + ",'N'";
			set = set + ",INFECTION='N'";
		}
		
		
		//��parm��ֵ
		ParmValue.setData("value", value);
		ParmKey.setData("key", key);
		ParmSet.setData("set", set);
		
		//��parm��ֵ
		result.setData("ParmValue", ParmValue.getData());
		result.setData("ParmKey", ParmKey.getData());
		result.setData("ParmSet", ParmSet.getData());
		
		return result;
	}

    
    
	/**���淽��*/
	public TParm onNew(TParm parm, TConnection connection){
		TParm result = new TParm();
//		System.out.println("aaaa::" + parm.getCount());
		for(int i=0;i<parm.getCount();i++){
			if(!parm.getValue("CAUSE_ARREARS",i).equals("")){
				 String sql = " UPDATE REG_PATADM " +
						"SET CAUSE_ARREARS='"+parm.getData("CAUSE_ARREARS",i)+"'" +
						" WHERE CASE_NO='"+parm.getData("CASE_NO",i)+"'";
//				 System.out.println("sql" + i + sql + "111111BILArrearsStatisticsTool" + parm);
				 //zhanglei �޸Ĳ��������ݿ��������
				 result = new TParm(TJDODBTool.getInstance().update(sql,
			                connection));
				 if(result.getErrCode()<0)
						return result;
			}
		}
//		System.out.println("1111111result" + result);
		return result;
		
	}
	/**���·���*/
	public TParm onUpDate(TParm parm, TConnection connection){
		//System.out.println("Tool����onOuDate");
		TParm getUiDate = getUiDate(parm);
		//��ȡ���parm
		TParm ParmSet = getUiDate.getParm("ParmSet");
		//
		//System.out.println("Set::" + ParmSet.getValue("set").toString());
		String sql="UPDATE INF_EXPOSURE " +
					"SET LNJECTION_NEEDLE='"+parm.getData("LNJECTION_NEEDLE")+"'," +
				    "INDWELLING_NEEDLE='"+parm.getData("INDWELLING_NEEDLE")+"'," +
					"SCALP_ACUPUNCTURE='"+parm.getData("SCALP_ACUPUNCTURE")+"'," +
					"NEEDLE='"+parm.getData("NEEDLE")+"'," +
					"VACUUM_BLOOD_COLLECTOR='"+parm.getData("VACUUM_BLOOD_COLLECTOR")+"'," +
					"SURGICAL_INSTRUMENTS='"+parm.getData("SURGICAL_INSTRUMENTS")+"'," +
					"GLASS_ITEMS='"+parm.getData("GLASS_ITEMS")+"'," +
					"OTHER_TYPE='"+parm.getData("OTHER_TYPE")+"'," +
					"BLOOD_COLLECTION='"+parm.getData("BLOOD_COLLECTION")+"'," +
					"CATHETER_PLACEMENT='"+parm.getData("CATHETER_PLACEMENT")+"'," +
					"OPERATION='"+parm.getData("OPERATION")+"'," +
					"FORMULATED_REHYDRATION='"+parm.getData("FORMULATED_REHYDRATION")+"'," +
					"INJECTION='"+parm.getData("INJECTION")+"'," +
					"EQUIPMENT='"+parm.getData("EQUIPMENT")+"'," +
					"OTHER_OPERATION='"+parm.getData("OTHER_OPERATION")+"'," +
					"OPEN_NEEDLE='"+parm.getData("OPEN_NEEDLE")+"'," +
					"MISALIGNMENT_PUNCTURE='"+parm.getData("MISALIGNMENT_PUNCTURE")+"'," +
					"DOSING_TIME='"+parm.getData("DOSING_TIME")+"'," +
					"BACK_SLEEVE='"+parm.getData("BACK_SLEEVE")+"'," +
					"BENDING_BREAKING_NEEDLE='"+parm.getData("BENDING_BREAKING_NEEDLE")+"'," +
					"OTHERS_STABBED='"+parm.getData("OTHERS_STABBED")+"'," +
					"PARTING_INSTRUMENT='"+parm.getData("PARTING_INSTRUMENT")+"'," +
					"CLEANING_ITEMS='"+parm.getData("CLEANING_ITEMS")+"'," +
					"PIERCING_BOX='"+parm.getData("PIERCING_BOX")+"'," +
					"HIDE_ITEMS='"+parm.getData("HIDE_ITEMS")+"'," +
					"BREAK_USE='"+parm.getData("BREAK_USE")+"'," +
					"OPT_USER='"+parm.getData("OPT_USER")+"'," +
				//	to_date('"+parm.getData("OPT_DATE")+ "','yyyy-mm-dd hh24:mi:ss')
					"OPT_DATE=to_date('"+parm.getData("OPT_DATE")+ "','yyyy-mm-dd') ," +
					"OPT_TERM='"+parm.getData("OPT_TERM")+"'" +
					ParmSet.getValue("set").toString() +","+
					"OTHER_ACTION='"+parm.getData("OTHER_ACTION")+"'" +
					" WHERE EXPOSURE_NO='"+parm.getData("EXPOSURE_NO")+"'";
		//System.out.println("Tool����onOuDate��SQL:::::" + sql);
		TParm result= new TParm(TJDODBTool.getInstance().update(sql,
                connection));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**ɾ������*/
	public TParm onDelete(TParm parm, TConnection connection){
		String sql="DELETE INV_SUPTITEM WHERE SUPITEM_CODE='"+parm.getData("SUPITEM_CODE")+"'";
		TParm result= new TParm(TJDODBTool.getInstance().update(sql,
                connection));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
    
    

}
