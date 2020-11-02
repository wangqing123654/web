package com.javahis.ui.pes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.odo.OpdRxSheetTool;
import jdo.pes.PESTool;
import jdo.sys.Operator;
import jdo.sys.Pat;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:��������ѡ��
 * </p>
 * 
 * <p>
 * Description:��������ѡ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author zhangp 2012.8.2
 * @version 1.0
 */
public class PESRxSelectControl extends TControl {
	private static TTable tableL;
	private static TTable tableR;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", date.toString().substring(0, 10).replace(
				'-', '/')
				+ " 00:00:00");
		setValue("EVA_DATE", date.toString().substring(0, 4)
				+ date.toString().substring(5, 7));
		setValue("EVAL_CODE", Operator.getID());
		this.setValue("ADM_TYPE","OE");
		setValue("REGION_CODE", Operator.getRegion());
		tableL = (TTable) getComponent("TABLE1");
		tableR = (TTable) getComponent("TABLE2");
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		onClearI();
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		TParm parm = new TParm();
		parm.setData("START_DATE", date_s);
		parm.setData("END_DATE", date_e);
		if (!getValueString("DEPT").equals("")) {
			parm.setData("DEPT_CODE", getValueString("DEPT"));
		}
		if (!getValueString("DOCTOR").equals("")) {
			parm.setData("DR_CODE", getValueString("DOCTOR"));
		}
		if (!getValueString("ANTIBIOTIC_CODE").equals("")) {
			parm.setData("ANTIBIOTIC_CODE", getValueString("ANTIBIOTIC_CODE"));
		}
		//$--- modify caoyong 20140213 start
		 if("".equals(getValueString("ADM_TYPE"))){
			 this.messageBox("�ż�ס����Ϊ��");
			 return;
		  } else if("OE".equals(getValueString("ADM_TYPE"))){
        	 parm.setData("ADM_TYPE",getValueString("ADM_TYPE"));
          }else if("O".equals(getValueString("ADM_TYPE"))){
        	 parm.setData("ADM_TYPE_O",getValueString("ADM_TYPE"));
          }else if("E".equals(getValueString("ADM_TYPE"))){
        	 parm.setData("ADM_TYPE_E",getValueString("ADM_TYPE"));
          }
			//$--- modify caoyong 20140213 end
		if (getValueString("PES_TYPE").equals("")) {
			messageBox("��ѡ��������");
			return;
		} else if (getValueString("PES_TYPE").equals("02")) {
			parm.setData("PES_TYPE", getValueString("PES_TYPE"));
		}
		TParm result =new TParm();
		if ("I".equals(getValueString("ADM_TYPE"))) {
			result =PESTool.getInstance().selectinpNo(parm);
			
		  }else{
			  
			  result = PESTool.getInstance().selectRxNo(parm);
	           
		  }
		 gridBind(result,getValueString("ADM_TYPE"));
		
	}

	/**
	 * ���Ұ�ť�¼�
	 */
	public void onRight() {
		tableL.acceptText();
		tableR.acceptText();
		int countR = tableR.getRowCount();
		
		TParm tmpParm = tableR.getParmValue();
		
		
		if (tmpParm == null) {
			tmpParm = new TParm();
		}
		TParm leftParm = tableL.getParmValue();
		String inpNo=leftParm.getValue("IPD_NO",0);
		//this.messageBox(""+inpNo);
		List list = new ArrayList();
		for (int i = 0; i < leftParm.getCount("FLG"); i++) {
			if (leftParm.getValue("FLG", i).equals("Y")) {
				tmpParm.addData("FLG", "N");
				tmpParm.addData("SEQ_NO", countR + 1);
				//$-----------MODIFY caoyong 20130213 start
				if(!"".equals(inpNo)&&inpNo!=null){
					tmpParm.addData("IN_DATE", leftParm.getValue("IN_DATE", i)
							.substring(0, 4)
							+ "/"
							+ leftParm.getValue("IN_DATE", i).substring(5, 7)
							+ "/"
							+ leftParm.getValue("IN_DATE", i).substring(8, 10));
					tmpParm.addData("VS_DR_CODE", leftParm.getValue("VS_DR_CODE", i));
					tmpParm.addData("IPD_NO", leftParm.getValue("IPD_NO", i));
				}else{
					
				tmpParm.addData("ADM_DATE", leftParm.getValue("ADM_DATE", i)
						.substring(0, 4)
						+ "/"
						+ leftParm.getValue("ADM_DATE", i).substring(5, 7)
						+ "/"
						+ leftParm.getValue("ADM_DATE", i).substring(8, 10));
				tmpParm.addData("DR_CODE", leftParm.getValue("DR_CODE", i));
				tmpParm.addData("RX_NO", leftParm.getValue("RX_NO", i));
				
				}
				//$-----------MODIFY caoyong 20130213 end
				tmpParm.addData("DEPT_CODE", leftParm.getValue("DEPT_CODE", i));
				tmpParm.addData("MR_NO", leftParm.getValue("MR_NO", i));
				tmpParm.addData("PAT_NAME", leftParm.getValue("PAT_NAME", i));
				tmpParm.addData("CASE_NO", leftParm.getValue("CASE_NO", i));
				
				countR++;
				list.add(i);
			}
		}
		for (int i = 0; i < list.size(); i++) {
			leftParm.removeRow(Integer.valueOf("" + list.get(i)) - i);
		}
		for (int i = 0; i < tmpParm.getCount("FLG"); i++) {

		}
		tableR.setParmValue(tmpParm);
		tableL.setParmValue(leftParm);
		setValue("COUNT", tmpParm.getCount("FLG"));
	}

	/**
	 * ����ť�¼�
	 */
	public void onLeft() {
		tableR.acceptText();
		tableL.acceptText();
		TParm tmpParm = tableL.getParmValue();
		if (tmpParm == null) {
			tmpParm = new TParm();
		}
		TParm rightParm = tableR.getParmValue();
		String inpNo=rightParm.getValue("IPD_NO",0);
		//this.messageBox(""+inpNo);
		List list = new ArrayList();
		for (int i = 0; i < rightParm.getCount("FLG"); i++) {
			if (rightParm.getValue("FLG", i).equals("Y")) {
				tmpParm.addData("FLG", "N");
				//$-----------MODIFY caoyong 20130213 start
				if(!"".equals(inpNo)&&inpNo!=null){
				tmpParm.addData("IN_DATE", rightParm.getValue("IN_DATE", i)
						.substring(0, 4)
						+ "/"
						+ rightParm.getValue("IN_DATE", i).substring(5, 7)
						+ "/"
						+ rightParm.getValue("IN_DATE", i).substring(8, 10));
				tmpParm.addData("VS_DR_CODE", rightParm.getValue("VS_DR_CODE", i));
				tmpParm.addData("IPD_NO", rightParm.getValue("IPD_NO", i));
				}else{
					tmpParm.addData("ADM_DATE", rightParm.getValue("ADM_DATE", i)
							.substring(0, 4)
							+ "/"
							+ rightParm.getValue("ADM_DATE", i).substring(5, 7)
							+ "/"
							+ rightParm.getValue("ADM_DATE", i).substring(8, 10));
					tmpParm.addData("RX_NO", rightParm.getValue("RX_NO", i));
					tmpParm.addData("DR_CODE", rightParm.getValue("DR_CODE", i));
				}
				//$-----------MODIFY caoyong 20130213 end
				tmpParm.addData("CASE_NO", rightParm.getValue("CASE_NO", i));
				tmpParm.addData("DEPT_CODE", rightParm.getValue("DEPT_CODE", i));
				tmpParm.addData("MR_NO", rightParm.getValue("MR_NO", i));
				tmpParm.addData("PAT_NAME", rightParm.getValue("PAT_NAME", i));
				
				list.add(i);
			}
		}
		for (int i = 0; i < list.size(); i++) {
			rightParm.removeRow(Integer.valueOf("" + list.get(i)) - i);
		}
		int seq = 1;
		for (int i = 0; i < rightParm.getCount("FLG"); i++) {
			rightParm.setData("SEQ_NO", i, seq);
			seq++;
		}
		tableL.setParmValue(tmpParm);
		tableR.setParmValue(rightParm);
		setValue("COUNT", rightParm.getCount("FLG"));
	}

	/**
	 * ����
	 */
	public void onSave() {
		tableR.acceptText();
		if (tableR.getRowCount() < 1) {
			messageBox("�޼�¼����ѡ��");
			return;
		}
		if (getValueString("PES_TYPE").equals("")) {
			messageBox("��ѡ��������");
			return;
		}
		if (getValueString("EVA_DATE").equals("")) {
			messageBox("����������ڼ�");
			return;
		}
		if (getValueString("EVAL_CODE").equals("")) {
			messageBox("��ѡ�������Ա");
			return;
		}
		TParm parm = new TParm();
		int seq_no = 1;
		boolean success = true;
		TParm result =new TParm();
		String inpNo=tableR.getParmValue().getValue("IPD_NO",0);
		
		for (int i = 0; i < tableR.getRowCount(); i++) {
			parm = tableR.getParmValue().getRow(i);
			parm.setData("TYPE_CODE", getValue("PES_TYPE"));
			parm.setData("PES_NO", getValue("EVA_DATE"));
			parm.setData("EVAL_CODE", getValue("EVAL_CODE"));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("SEQ_NO", seq_no);
			Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO"));
			parm.setData("PAT_NAME", pat.getName());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy��MM��dd��");
			Timestamp time = pat.getBirthday();
			parm.setData("AGE", sdf.format(time));
			
			parm.setData("WEIGHT", this.getWeight(parm.getValue("CASE_NO")));
			parm.setData("SEX_CODE", pat.getSexString());
			
			
			if("".equals(inpNo)||inpNo==null){
			     result = TIOM_AppServer.executeAction("action.pes.PESAction",
					"saveSelectRx", parm);//����
			}else{//סԺ
				result = TIOM_AppServer.executeAction("action.pes.PESAction",
						"saveSelectIpdRx", parm);//����
			}
			if (result.getErrCode() < 0) {
				messageBox("����ʧ��");
				success = false;
				return;
			}
			seq_no = result.getInt("SEQ_NO");
		}
		if (success) {
			if("".equals(inpNo)||inpNo==null){
			    result = TIOM_AppServer.executeAction("action.pes.PESAction","insertPESResult", parm);
			}else{
				 result = TIOM_AppServer.executeAction("action.pes.PESAction","insertIDPPESResult", parm);
			}
					
			if (result.getErrCode() < 0) {
				messageBox("����ʧ��");
				return;
			}
			messageBox("����ɹ�");
			
		}
		if("".equals(inpNo)||inpNo==null){
		 result = TIOM_AppServer.executeAction("action.pes.PESAction",
				"updatePESOPDMD", parm);
		}
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		tableR.acceptText();
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = tableR.getParmValue();
		if (null == parm || parm.getCount("FLG") <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(tableR, "�������������б�");
	}

	/**
	 * ���
	 */
	public void onClear() {
		TParm parm = new TParm();
		tableL.setParmValue(parm);
		tableR.setParmValue(parm);
		this.setValue("ADM_TYPE","OE");
		this.clearValue("COUNT;NUM");
	}
	
	
	public void onClearI() {
		TParm parm = new TParm();
		tableL.setParmValue(parm);
		tableR.setParmValue(parm);
		this.clearValue("COUNT;NUM");
	}

	public void onRam() {
		int num = getValueInt("NUM");
		num++;
		int count = 0;
		tableL.acceptText();
		TParm parmL = tableL.getParmValue();
		for (int i = 0; i < parmL.getCount("FLG"); i++) {
			parmL.setData("FLG", i, "N");
			if (i % num == 0) {
				parmL.setData("FLG", i, "Y");
				count++;
			}
		}
		tableL.setParmValue(parmL);
		messageBox("ѡ����" + count + "������");
	}
	/**
	 * �ı�GRID
	 * add caoyong 20140213
	 *            
	 */
	public void SelectItem(){
		TParm result =new TParm();
			 gridBind(result,this.getValueString("ADM_TYPE"));
			
	}
	/**
	 * ��GRID
	 * add caoyong 20140213
	 * @param data
	 *            TParm ����
	 * @param type
	 *            
	 */
	private void gridBind( TParm data,String type) {
		TTable table = (TTable) this.getComponent("Table");
		if (!"I".equals(type)) {
			// ���ñ�ͷ
			tableL.setHeader("ѡ,30,boolean;��������,100;����,100,DEPT;ҽ��,100,DOCTOR;������,100;����,100");
			tableL.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12");// ���������в��ɱ༭
			// ���ý���˳��
			tableL.setFocusIndexList("0,1,2,3,4,5,64");
			// ���ö��䷽ʽ
			tableL.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;");
			// �趨���ݶ�Ӧ�� parmMap
			tableL.setParmMap("FLG;ADM_DATE;DEPT_CODE;DR_CODE;MR_NO;PAT_NAME;RX_NO;CASE_NO");
			
			tableR.setHeader("ѡ,30,boolean;���,60;��������,100;����,100,DEPT;ҽ��,100,DOCTOR;������,100;����,100;�������� ,100");
			tableR.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12");// ���������в��ɱ༭
			// ���ý���˳��
			tableR.setFocusIndexList("0,1,2,3,4,5,6,7,8");
			// ���ö��䷽ʽ
			tableR.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,left;7,left");
			// �趨���ݶ�Ӧ�� parmMap
			tableR.setParmMap("FLG;SEQ_NO;ADM_DATE;DEPT_CODE;DR_CODE;MR_NO;PAT_NAME;RX_NO");
			tableL.setParmValue(data);// ���ݰ�
			tableL.retrieve();
		} else{
			tableL.setHeader("ѡ,30,boolean;��Ժ����,100;����,100,DEPT;ҽ��,100,DOCTOR;סԺ��,100;������,100;����,100");
			tableL.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12");// ���������в��ɱ༭
			// ���ý���˳��
			tableL.setFocusIndexList("0,1,2,3,4,5,6");
			// ���ö��䷽ʽ
			tableL.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;");
			// �趨���ݶ�Ӧ�� parmMap
			tableL.setParmMap("FLG;IN_DATE;DEPT_CODE;VS_DR_CODE;IPD_NO;MR_NO;PAT_NAME;CASE_NO");
			
			tableR.setHeader("ѡ,30,boolean;���,60;��Ժ����,100;����,100,DEPT;ҽ��,100,DOCTOR;סԺ��,100;������,100;����,100");
			tableR.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12");// ���������в��ɱ༭
			// ���ý���˳��
			tableR.setFocusIndexList("0,1,2,3,4,5,6,7,8");
			// ���ö��䷽ʽ
			tableR.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,left;7,left");
			// �趨���ݶ�Ӧ�� parmMap
			tableR.setParmMap("FLG;SEQ_NO;IN_DATE;DEPT_CODE;VS_DR_CODE;IPD_NO;MR_NO;PAT_NAME;CASE_NO");
			tableL.setParmValue(data);// ���ݰ�
			
			tableL.retrieve();
		}
	}
	
	public String getWeight(String caseNo){
		String sql = "SELECT WEIGHT FROM REG_PATADM WHERE CASE_NO='"+caseNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("WEIGHT", 0);
	}
	
	public String getSex(String sexCode){
		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX' AND ID='"+sexCode+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("CHN_DESC", 0);
	}
	
}
