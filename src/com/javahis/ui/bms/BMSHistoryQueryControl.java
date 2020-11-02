package com.javahis.ui.bms;
import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;

import javax.swing.table.TableModel;

import jdo.bms.BMSSQL;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.util.Compare;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ѪҺ��ʷ��ѯ(��ѯ����գ��ر�)
 * </p>
 *
 * <p>
 * Description: ѪҺ��ʷ��ѯ 
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author fux 2012.04.19
 * @version 2.0
 */

public class BMSHistoryQueryControl extends TControl {

	private TTable table;
	/**
     * ��ʼ������
     */
    public void onInit() {
        initPage();
    }

    
	 /**
     * ��ʼ��������
     */ 
    private void initPage() {
        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("E_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("S_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // ��ʼ��TABLE
        table = getTable("TABLE");
       
    }
    
    /**
     * ��ӡ���ⵥ
     */
    public void onPrint() {
    	String outNo1 = this.getValueString("OUT_NO") ;//��ȡ���뵥��
    	String sql = "SELECT A.*,B.DEPT_CHN_DESC,C.STATION_DESC,D.BED_NO FROM BMS_BLOOD A,SYS_DEPT B,SYS_STATION C,ADM_INP D WHERE A.DEPT_CODE = B.DEPT_CODE AND A.STATION_CODE = C.STATION_CODE AND A.CASE_NO = D.CASE_NO AND A.OUT_NO = '"+outNo1+"'" ;
    	TParm outData = new TParm(TJDODBTool.getInstance().select(sql));
    	if(outData.getErrCode()<0){
    		messageBox("û�д����뵥��!") ;
    		return ;
    	}
    	if(outData.getCount()<=0){
    		messageBox("����û�г�����Ϣ!") ;
    		return ;
    	}
    	
    	if(!"2".equals(outData.getValue("STATE_CODE", 0))){
    		messageBox("�����뵥�е�ѪƷ״̬���ǳ���״̬.") ;
    		return ;
    	}
    	Pat pat = Pat.onQueryByMrNo(outData.getValue("MR_NO", 0));
        TParm date = new TParm();
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "ѪƷ���ⵥ");
        date.setData("APPLY_NO", "TEXT", "��Ѫ����: " + outData.getValue("APPLY_NO", 0));
        date.setData("PAT_NAME", "TEXT", " ��Ѫ��: " + pat.getName());
        date.setData("AGE", "TEXT", "����: " + StringUtil.showAge(pat.getBirthday(),
                SystemTool.getInstance().getDate()));
        date.setData("SEX", "TEXT", "�Ա�: " + pat.getSexString());
        date.setData("BLD_TYPE", "TEXT", " ABOѪ��: " + pat.getBloodType());
        String rh_type = "";
        if ( "+".equals(pat.getBloodRHType())) {
            rh_type = "����";
        }
        else if ( "-".equals(pat.getBloodRHType())) {
            rh_type = "����";
        }
        date.setData("RH_TYPE", "TEXT", "RHѪ��: " + rh_type);
        date.setData("MR_NO", "TEXT", " ������: " + outData.getValue("MR_NO", 0));
   
        date.setData("DEPT_CODE", "TEXT",
                     "�Ʊ�: " + outData.getValue("DEPT_CHN_DESC", 0));

        date.setData("IPD_NO", "TEXT", "סԺ��: " + outData.getValue("IPD_NO", 0));
        date.setData("STATION_CODE", "TEXT",
                     "����: " +
                     outData.getValue("STATION_DESC", 0));
        date.setData("BED_NO", "TEXT",
                     "��λ: " + outData.getValue("BED_NO", 0));
       

        String outNo = "" ;//���ⵥ��
        String reason = "" ;//��Ѫԭ��
        String deptCode = "" ; //��Ѫ����
        String diag = "" ;//���
        String optDate = "" ;//�ռ�����
        String outDate = "" ;//��������
        String caseNo = "" ;
        // �������
        TParm parm = new TParm();
        String blood_no = "";
        TParm inparm = new TParm();
        for (int i = 0; i < outData.getCount(); i++) {
            blood_no = outData.getValue("BLOOD_NO",i);
            parm.addData("BLOOD_NO", blood_no);
            inparm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
                getBMSBloodOut(blood_no)));
            
            if("".equals(outNo)){
            	outNo = inparm.getValue("OUT_NO", 0) ;
            }
            if("".equals(reason)){
            	reason += this.getTransReason(inparm.getValue("TRANRSN_CODE1", 0)) +" ";
            	reason += this.getTransReason(inparm.getValue("TRANRSN_CODE2", 0)) +" ";
            	reason += this.getTransReason(inparm.getValue("TRANRSN_CODE3", 0)) +" ";
            }
            if("".equals(deptCode)){
            	deptCode = inparm.getValue("DEPT_CHN_DESC", 0) ;
            }
            if("".equals(diag)){
            	diag += this.getDiagDesc(inparm.getValue("DIAG_CODE1", 0)) +" ";
            	diag += this.getDiagDesc(inparm.getValue("DIAG_CODE2", 0)) +" ";
            	diag += this.getDiagDesc(inparm.getValue("DIAG_CODE3", 0)) +" ";
            }
            if("".equals(optDate)){
            	optDate = inparm.getValue("OPT_DATE", 0) ;
            }
            if("".equals(outDate)){
            	outDate = inparm.getValue("OUT_DATE", 0) ;
            }
            
            if("".equals(caseNo)){
            	caseNo = inparm.getValue("CASE_NO", 0) ;
            }
            
            parm.addData("ORG_BARCODE", inparm.getValue("ORG_BARCODE",0)) ;//Ժ������
            
            //modify by lim 2012/05/06 begin
            parm.addData("BLD_CODE", inparm.getValue("SUBCAT_DESC", 0));//Ժ������
            parm.addData("SUBCAT_CODE", "");//���
            //modify by lim 2012/05/06 end
            
            parm.addData("BLD_TYPE", pat.getBloodType());//Ѫ��
            parm.addData("RH_FLG", pat.getBloodRHType());//RHѪ��
            String crossL = " " ;
            if("0".equals(outData.getValue("CROSS_MATCH_L",i))){
            	crossL = "������,����Ѫ" ;
            }else if("1".equals(outData.getValue("CROSS_MATCH_L",i))){
            	crossL = "����" ;
            }
            parm.addData("CROSS_MATCH_L",crossL);
            
            String crossS = " " ;
            if("0".equals(outData.getValue("CROSS_MATCH_S",i))){
            	crossS = "������,����Ѫ" ;
            }else if("1".equals(outData.getValue("CROSS_MATCH_S",i))){
            	crossS = "����" ;
            }            
            parm.addData("CROSS_MATCH_S",crossS);
            parm.addData("RESULT",
                         "1".equals(outData.getValue("RESULT",i)) ? "���" :
                         "���");
            parm.addData("TEST_DATE",outData.getValue("TEST_DATE",i).substring(0, 16).replace('-', '/'));
        }
        
        parm.setCount(parm.getCount("BLOOD_NO"));
        parm.addData("SYSTEM", "COLUMNS", "ORG_BARCODE");
        parm.addData("SYSTEM", "COLUMNS", "BLOOD_NO");
        parm.addData("SYSTEM", "COLUMNS", "BLD_CODE");
        parm.addData("SYSTEM", "COLUMNS", "SUBCAT_CODE");
        parm.addData("SYSTEM", "COLUMNS", "BLD_TYPE");
        parm.addData("SYSTEM", "COLUMNS", "RH_FLG");
        parm.addData("SYSTEM", "COLUMNS", "CROSS_MATCH_L");
        parm.addData("SYSTEM", "COLUMNS", "CROSS_MATCH_S");
        parm.addData("SYSTEM", "COLUMNS", "RESULT");
        parm.addData("SYSTEM", "COLUMNS", "TEST_DATE");

        date.setData("TABLE", parm.getData());
        
        //modify by lim 2012/05/17 begin
        String deptDesc = "" ;
        String sql1 = "SELECT B.DEPT_CHN_DESC FROM ADM_INP A,SYS_DEPT B WHERE A.DEPT_CODE = B.DEPT_CODE AND A.CASE_NO='"+outData.getValue("CASE_NO", 0)+"'" ;
        System.out.println("sql1::::::::::::::::::"+sql1);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql1)) ;
        
        if(result.getErrCode()<0 || result.getCount()<=0){
        	deptDesc = "" ;
        }else{
        	deptDesc = result.getValue("DEPT_CHN_DESC", 0) ;
        }
        //modify by lim 2012/05/17 end
        
        
        TParm inparm2 = SYSOperatorTool.getInstance().selectdata(outData.getValue("TEST_USER",0 ))  ;      
        date.setData("PEIXUEZHE","TEXT","��Ѫ��:"+inparm2.getData("USER_NAME", 0)) ;
        date.setData("OUT_NO", "TEXT", "���ⵥ��: " + outNo);
        date.setData("REASON", "TEXT","��Ѫԭ��:"+reason) ;
        date.setData("YXKS", "TEXT", "��Ѫ����:"+deptDesc) ;
        String recpDate = "" ;
        if(null!=optDate && !"".equals(optDate)){
        	recpDate =optDate.substring(0, 16).replace('-', '/') ;
        }
        date.setData("TEST_DATE","TEXT"," �ռ�����:"+recpDate) ;
        date.setData("JWSH","TEXT","���: "+diag) ;
        if(!"".equals(outDate)&& outDate!=null){
        	outDate = outDate.substring(0, 19).replace("-", "/") ;
        }
        date.setData("OUT_DATE","TEXT",outDate) ;
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSBloodOut.jhw", date);
    }
    
    private String getDiagDesc(String diagCode){
    	String desc = "" ;
    	if(diagCode!=null && !"".equals(diagCode)){
    		String sql = "SELECT A.ICD_CHN_DESC FROM SYS_DIAGNOSIS A WHERE A.ICD_CODE='"+diagCode+"'" ;
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result.getErrCode()<0){
    			return desc ;
    		}
    		if(result.getCount() <= 0){
    			return desc ;
    		}
    		desc = result.getValue("ICD_CHN_DESC", 0) ; 
    	}
    	return desc ;
    }
    
    private String getTransReason(String transReasonCode){
    	String desc = "" ;
    	if(transReasonCode!=null && !"".equals(transReasonCode)){
    		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='BMS_TRANRSN' AND ID='"+transReasonCode+"'" ;
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result.getErrCode()<0){
    			return desc ;
    		}
    		if(result.getCount() <= 0){
    			return desc ;
    		}
    		desc = result.getValue("CHN_DESC", 0) ; 
    	}
    	return desc ;
    }
    
    /**
     * ��ѯ���� 
     */
    public  void  onQuery(){
    	String bldcode = this.getValueString("BLD_CODE");//ѪƷ
    	String subcatcode = this.getValueString("SUBCAT_CODE");//ѪƷ���
    	String statecode = this.getValueString("STATE_CODE");//ѪҺ״��
    	String deptcode = this.getValueString("DEPT_CODE");//��Ѫ����
    	
    	String sDate = this.getValueString("S_DATE") ;//����ʱ�� begin
    	String eDate = this.getValueString("E_DATE") ;//����ʱ�� end
    	String indate = this.getValueString("IN_DATE");//�������
    	String bldtype = this.getValueString("BLD_TYPE");//Ѫ��
    	String mrno  = this.getValueString("MR_NO");//������
    	String outNO = this.getValueString("OUT_NO") ;//���ⵥ��
    	
    	
//    	String sql=" SELECT A.BLOOD_NO, A.RH_FLG, A.BLD_CODE, A.SUBCAT_CODE,"+
//    	  	       "        A.IN_DATE, A.BLD_TYPE, A.SHIT_FLG, A.END_DATE, A.IN_PRICE, A.BLOOD_VOL,"+ 
//    	  	       "        A.ORG_BARCODE, A.STATE_CODE, A.APPLY_NO, A.MR_NO, A.IPD_NO,"+ 
//    	  	       "        A.CASE_NO, A.ID_NO, A.USE_DATE, A.CROSS_MATCH_L, A.CROSS_MATCH_S,"+ 
//    	   	       "        A.ANTI_A, A.ANTI_B, A.RESULT, A.TEST_DATE, A.TEST_USER,"+ 
//    	   	       "        A.PRE_U, A.PRE_D, A.T, A.P, A.R,"+ 
//    	  	       "        A.WORK_USER, A.OUT_NO, A.OUT_DATE, A.OUT_USER,"+ 
//    	  	       "        A.TRAN_RESN,A.TRAN_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.DEPT_CODE ,B.PAT_NAME "+
//    	  	       " FROM BMS_BLOOD A ,SYS_PATINFO B "+ 
//    	  	       " WHERE A.MR_NO = B.MR_NO(+) " ; 
    	String sql=" SELECT A.BLOOD_NO, A.RH_FLG, A.BLD_CODE, A.SUBCAT_CODE,"+
	       "        A.IN_DATE, A.BLD_TYPE, A.SHIT_FLG, A.END_DATE, A.IN_PRICE, A.BLOOD_VOL,"+ 
	       "        A.ORG_BARCODE, A.STATE_CODE, A.APPLY_NO, A.MR_NO, A.IPD_NO,"+ 
	       "        A.CASE_NO, A.ID_NO, A.USE_DATE, A.CROSS_MATCH_L, A.CROSS_MATCH_S,"+ 
	       "        A.ANTI_A, A.ANTI_B, A.RESULT, A.TEST_DATE, A.TEST_USER,"+ 
	       "        A.PRE_U, A.PRE_D, A.T, A.P, A.R,"+ 
	       "        A.WORK_USER, A.OUT_NO, A.OUT_DATE, A.OUT_USER,"+ 
	       "        A.TRAN_RESN,A.TRAN_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.DEPT_CODE ,B.PAT_NAME,D.UNIT_CHN_DESC,C.UNIT_CODE "+
	       " FROM BMS_BLOOD A ,SYS_PATINFO B ,BMS_BLDSUBCAT C,SYS_UNIT D "+ 
	       " WHERE A.MR_NO = B.MR_NO(+) " +
	       "   AND A.BLD_CODE = C.BLD_CODE " +
	       "   AND A.SUBCAT_CODE = C.SUBCAT_CODE " +
	       "   AND C.UNIT_CODE = D.UNIT_CODE " ;     
   
    	StringBuilder sbuilder = new StringBuilder(sql) ;
    	if(bldcode!=null && !"".equals(bldcode)){//ѪƷ
    		sbuilder.append(" AND A.BLD_CODE= '"+bldcode+"'") ;
    	}
    	if(subcatcode!=null && !"".equals(subcatcode)){//ѪƷ���
    		sbuilder.append(" AND A.SUBCAT_CODE= '"+subcatcode+"'") ;
    	}
    	if(statecode!=null && !"".equals(statecode)){//ѪҺ״̬
    		sbuilder.append(" AND A.STATE_CODE= '"+statecode+"'") ;
    	} 
    	if(deptcode!=null && !"".equals(deptcode)){//��Ѫ����
    		sbuilder.append(" AND A.DEPT_CODE='"+deptcode+"'") ;
    	}
    	
    	if((sDate!=null && !"".equals(sDate)) ){
    		sDate = sDate.substring(0, 19) ;
    		sbuilder.append(" AND TO_CHAR(OUT_DATE,'YYYY-MM-DD HH:mm:ss') >= '"+sDate+"'") ;
    	} 
    	if((eDate!=null && !"".equals(eDate)) ){
    		eDate = eDate.substring(0, 19) ;
    		sbuilder.append(" AND TO_CHAR(OUT_DATE,'YYYY-MM-DD HH:mm:ss') <= '"+eDate+"'") ;
    	} 
    	
    	if(indate!=null && !"".equals(indate)){//���ʱ��
    		String[] inDateArray = indate.split(" ") ;
    		sbuilder.append(" AND TO_CHAR(IN_DATE,'YYYY-MM-dd')= '"+inDateArray[0]+"' ") ;
    	}    	
    	if(bldtype!=null && !"".equals(bldtype)){//Ѫ��
    		sbuilder.append(" AND A.BLD_TYPE= '"+bldtype+"' ") ;
    	}
    	if(mrno!=null && !"".equals(mrno)){//Ѫ��
    		sbuilder.append(" AND A.MR_NO= '"+mrno+"' ") ;
    	}  
    	if(outNO!=null && !"".equals(outNO)){//��Ѫ����
    		sbuilder.append(" AND A.OUT_NO='"+outNO+"'") ;
    	}

    	sbuilder.append(" ORDER BY BLOOD_NO ") ;

    	TParm result = new TParm(TJDODBTool.getInstance().select(sbuilder.toString()));
    	
    	
    	//   ��ô�����Ϣ��Ϣ
    	if (result.getErrCode() < 0) {
    	    messageBox(result.getErrText());
    	    return;      
    	}
        if (result.getCount() <= 0) {
            messageBox("��������");
            this.callFunction("UI|TABLE|setParmValue", new TParm());
            return;
        }       	
    	
    	Set<String>  unitSet = new HashSet<String>() ;
    	double bldVol = 0.0 ;
    	for (int i = 0; i < result.getCount(); i++) {
    		unitSet.add(result.getValue("UNIT_CHN_DESC", i)) ;
    		bldVol+=result.getDouble("BLOOD_VOL", i) ;
			String crossMatchL = result.getValue("CROSS_MATCH_L", i) ;
			if("0".equals(crossMatchL)){
				result.addData("crossMatchL","����������Ѫ") ;
			}else if("1".equals(crossMatchL)){
				result.addData("crossMatchL", "����") ;
			}else{
				result.addData("crossMatchL", " ") ;
			}
			
			String crossMatchS = result.getValue("CROSS_MATCH_S", i) ;
			if("0".equals(crossMatchS)){
				result.addData("crossMatchS","����������Ѫ") ;
			}else if("1".equals(crossMatchS)){
				result.addData("crossMatchS", "����") ;
			}else{
				result.addData("crossMatchS", " ") ;
			}
			//��Ѫ���
			String crossResult = result.getValue("RESULT", i) ;
			if("1".equals(crossResult)){
				result.addData("crossResult", "���") ;
			}else if("2".equals(crossResult)){
				result.addData("crossResult", "���") ;
			}else{
				result.addData("crossResult", " ") ;
			}
		}
    	
    	if(unitSet.size()==1){
    		this.setValue("BLOOD_VOL", bldVol) ;
    		this.setValue("tLabel_14", unitSet.iterator().next() ) ;
    	}


    	//   ���й���parm��ֵ       
        this.callFunction("UI|TABLE|setParmValue", result);
};
    
    
     
    /**
     * ��շ���   
     */
    public void onClear() {
        String clearString =
            "BLD_CODE;SUBCAT_CODE;IN_DATE;DEPT_CODE;MR_NO;" +
            "BLD_TYPE;STATE_CODE;OUT_DATE;BLOOD_VOL;OUT_NO";
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ���� 
        this.setValue("E_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("S_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        table.removeRowAll(); 
        }
 /**
  * ��񵥻��¼�
  */ 
  public void onTableClicked() {
     int row = table.getSelectedRow();
     if (row != -1) {
        this.setValue("IN_DATE", table.getItemData(row, "IN_DATE"));
        this.setValue("STATE_CODE", table.getItemData(row, "STATE_CODE"));
        this.setValue("ORG_BARCODE", table.getItemData(row, "ORG_BARCODE"));
        this.setValue("BLOOD_NO", table.getItemData(row, "BLOOD_NO"));
        this.setValue("BLD_CODE", table.getItemData(row, "BLD_CODE"));
        this.setValue("SUBCAT_CODE", table.getItemData(row, "SUBCAT_CODE"));
        this.setValue("BLD_TYPE", table.getItemData(row, "BLD_TYPE"));
        this.setValue("DEPT_CODE", table.getItemData(row, "DEPT_CODE"));
        this.setValue("MR_NO", table.getItemData(row, "MR_NO"));
        this.setValue("OUT_NO", table.getItemData(row, "OUT_NO")) ;
        }
    }   
    
	/**
	 * �õ�Table����
	 *
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
	    return (TTable) getComponent(tagName);
	}
	
	public void onMrNoAction(){
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        this.setValue("MR_NO", mr_no) ;
	}
	
	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}	
}





















