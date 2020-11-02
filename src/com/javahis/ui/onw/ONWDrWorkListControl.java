package com.javahis.ui.onw;

import com.dongyang.config.TConfig;
import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.onw.ONWDrWorkListTool;

import com.javahis.util.DateUtil;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: �ż���ҽʦ������ͳ��</p>
 *
 * <p>Description: �ż���ҽʦ������ͳ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-2-4
 * @version 1.0
 */
public class ONWDrWorkListControl
    extends TControl {
    TParm DATA;//��¼��ѯ����
    TTable table;
    String DEPT_DESC ="";//��¼��ѯ�Ŀ���
    String QUERY_DATE="";//��¼��ѯ����
    public void onInit(){
        super.onInit();
        //================pangben modify 20110406 start ��������
        setValue("REGION_CODE", Operator.getRegion());
        //================pangben modify 20110406 stop
        //========pangben modify 20110421 start Ȩ�����
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

        table = (TTable)this.getComponent("Table");
        this.setValue("ADM_DATE",SystemTool.getInstance().getDate());
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        if(this.getValue("ADM_DATE")==null){
            this.messageBox_("��ѡ���ѯ����");
            this.grabFocus("ADM_DATE");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ADM_DATE",this.getValueString("ADM_DATE").substring(0,10).replace("-",""));
        if(this.getValue("ADM_TYPE")!=null&&!this.getValueString("ADM_TYPE").equals("")){
            parm.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
        }
        if(this.getValue("SESSION_CODE")!=null&&!this.getValueString("SESSION_CODE").equals("")){
            parm.setData("SESSION_CODE",this.getValue("SESSION_CODE"));
        }
        if(this.getValue("DEPT_CODE")!=null&&!this.getValueString("DEPT_CODE").equals("")){
            parm.setData("DEPT_CODE",this.getValue("DEPT_CODE"));
        }
        if(this.getValue("DR_CODE")!=null&&!this.getValueString("DR_CODE").equals("")){
            parm.setData("DR_CODE",this.getValue("DR_CODE"));
        }
        //=================pangben modify 20110406 start ��������ѯ�������
        if(this.getValueString("REGION_CODE")!=null&&!this.getValueString("REGION_CODE").equals("")){
            parm.setData("REGION_CODE",this.getValue("REGION_CODE"));
        }
        //=================pangben modify 20110406 stop
        DATA = ONWDrWorkListTool.getInstance().selectData(parm);
        if(DATA.getCount()<=0){
            this.messageBox("E0008");
            table.removeRowAll();
            return;
        }

        DEPT_DESC = this.getText("DEPT_CODE").equals("")?"ȫԺ":this.getText("DEPT_CODE");
        QUERY_DATE =StringTool.getString((Timestamp)this.getValue("ADM_DATE"),"yyyy��MM��dd��");
        TParm showData = new TParm();
        //�������� ��������
        for(int i=0;i<DATA.getCount();i++){
            //======pangben modify 20110418 start
            showData.addData("REGION_CHN_DESC",DATA.getData("REGION_CHN_DESC",i));
            //======pangben modify 20110418 stop
            showData.addData("USER_NAME",DATA.getData("USER_NAME",i));
            showData.addData("PAT_NAME",DATA.getData("PAT_NAME",i));
            showData.addData("CHN_DESC",DATA.getData("CHN_DESC",i));
            showData.addData("AGE",DateUtil.showAge(DATA.getTimestamp("BIRTH_DATE",i),DATA.getTimestamp("ADM_DATE",i)));
            showData.addData("ADDRESS",DATA.getData("ADDRESS",i));
            showData.addData("ICD_CHN_DESC",DATA.getData("ICD_CHN_DESC",i));
            showData.addData("DIAG_NOTE",DATA.getData("DIAG_NOTE",i));
            showData.addData("ILLNESS_DATE",DATA.getData("ILLNESS_DATE",i));
            showData.addData("ADM_DATE",DATA.getData("ADM_DATE",i));
            showData.addData("VISIT_CODE",DATA.getValue("VISIT_CODE",i).equals("0")?"Y":"N");
            showData.addData("VISIT_CODE1",DATA.getValue("VISIT_CODE",i).equals("0")?"N":"Y");
            showData.addData("PAD_DATE",DATA.getData("PAD_DATE",i));
            showData.addData("REALDEPT_CODE",DATA.getData("REALDEPT_CODE",i));
            showData.addData("IDNO",DATA.getData("IDNO",i));
            showData.addData("OCC_CODE",DATA.getData("OCC_CODE",i));
            if(isExsitEmrFileData(DATA.getData("MR_NO",i).toString(),DATA.getData("REALDR_CODE",i).toString(),DATA.getData("REALDEPT_CODE",i).toString())){
            	showData.addData("EMR_FLG","��");
            }else{
            	showData.addData("EMR_FLG","��");
            }
            
        }
        table.setParmValue(showData);
    }
    /**
     * ���ݾ���� �ж��Ƿ���סԺ֤
     * @param caseNo
     */
    public boolean isExsitEmrFileData(String mrNo,String realDrCode,String realDeptCode){
    	
    	String sql=" SELECT RESV_NO FROM ADM_RESV WHERE MR_NO='"+mrNo+"' AND OPD_DEPT_CODE='"+realDeptCode+"' AND OPD_DR_CODE='"+realDrCode+"'" +
    			" AND ADM_SOURCE='01' ";
    	
    	//System.out.println("sql::"+sql);
    	TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
    	int count=0;
    	String  subClassCode=TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
    	for(int i=0;i<parm.getCount();i++){
    		sql="SELECT FILE_NAME FROM EMR_FILE_INDEX  WHERE CASE_NO='"+parm.getValue("RESV_NO",i)+"' AND SUBCLASS_CODE='"+subClassCode+"' ";
    		parm=new TParm(TJDODBTool.getInstance().select(sql));
    		if(parm.getCount()>0){
    			count++;
    			return true;
    		}
    	}
    	return false;
    }
    /**
     * ���
     */
    public void onClear(){
        table.removeRowAll();
        this.clearValue("ADM_TYPE;SESSION_CODE;DEPT_CODE;DR_CODE");
        this.setValue("ADM_DATE",SystemTool.getInstance().getDate());
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
        if(DATA.getCount()<=0){
            return;
        }
        TParm printData = new TParm();
        TParm showTableParm = table.getShowParmValue();
        //�������� ��������
        for(int i=0;i<DATA.getCount();i++){
            //=======pangben modify 20110418 start
            printData.addData("REGION_CHN_DESC",DATA.getValue("REGION_CHN_DESC",i));
             //=======pangben modify 20110418 stop
            printData.addData("REALDEPT_CODE",showTableParm.getValue("REALDEPT_CODE",i));//add  by huangjw 20150911
            printData.addData("USER_NAME",DATA.getValue("USER_NAME",i));
            printData.addData("PAT_NAME",DATA.getValue("PAT_NAME",i));
            printData.addData("CHN_DESC",DATA.getValue("CHN_DESC",i));
            printData.addData("AGE",DateUtil.showAge(DATA.getTimestamp("BIRTH_DATE",i),DATA.getTimestamp("ADM_DATE",i)));
            printData.addData("ADDRESS",DATA.getValue("ADDRESS",i));
            printData.addData("ICD_CHN_DESC",DATA.getValue("ICD_CHN_DESC",i));
            printData.addData("ILLNESS_DATE",DATA.getValue("ILLNESS_DATE",i));
            printData.addData("ADM_DATE",StringTool.getString(DATA.getTimestamp("ADM_DATE",i),"yyyy-MM-dd"));
            printData.addData("VISIT_CODE0",DATA.getValue("VISIT_CODE",i).equals("0")?"��":"");
            printData.addData("VISIT_CODE1",DATA.getValue("VISIT_CODE",i).equals("1")?"��":"");
            printData.addData("PAD_DATE",StringTool.getString(DATA.getTimestamp("PAD_DATE",i),"yyyy-MM-dd"));
            //printData.addData("DEPT",DATA.getValue("DEPT_CHN_DESC",i));
            if(isExsitEmrFileData(DATA.getData("MR_NO",i).toString(),DATA.getData("REALDR_CODE",i).toString(),DATA.getData("REALDEPT_CODE",i).toString())){
            	printData.addData("EMR_FLG","��");
            }else{
            	printData.addData("EMR_FLG","��");
            }
        }
        printData.setCount(DATA.getCount());
        //=======pangben modify 20110418 start
        printData.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=======pangben modify 20110418 stop
        printData.addData("SYSTEM", "COLUMNS", "REALDEPT_CODE");//add  by huangjw 20150911
        printData.addData("SYSTEM", "COLUMNS", "USER_NAME");
        printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        printData.addData("SYSTEM", "COLUMNS", "CHN_DESC");
        printData.addData("SYSTEM", "COLUMNS", "AGE");
        printData.addData("SYSTEM", "COLUMNS", "ADDRESS");
        printData.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
        printData.addData("SYSTEM", "COLUMNS", "ILLNESS_DATE");
        printData.addData("SYSTEM", "COLUMNS", "ADM_DATE");
        printData.addData("SYSTEM", "COLUMNS", "VISIT_CODE0");
        printData.addData("SYSTEM", "COLUMNS", "VISIT_CODE1");
        printData.addData("SYSTEM", "COLUMNS", "PAD_DATE");
        //printData.addData("SYSTEM", "COLUMNS", "DEPT");
        printData.addData("SYSTEM", "COLUMNS", "EMR_FLG");
        TParm parm = new TParm();
        parm.setData("T1",printData.getData());
        parm.setData("DEPT","TEXT",DEPT_DESC);
        parm.setData("DATE","TEXT",QUERY_DATE);
        parm.setData("printUser","TEXT",Operator.getName());
        //========pangben modify 20110418 start
        String region= ((TTable)this.getComponent("Table")).getParmValue().getRow(0).getValue("REGION_CHN_DESC");
        parm.setData("title","TEXT",( this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals("")?region:"����ҽԺ")+"�ż���ҽʦ������־��");
       //========pangben modify 20110418 stop
        this.openPrintDialog("%ROOT%\\config\\prt\\ONW\\ONWDrWorkList.jhw",parm);
    }
    
    
    /**
     * �ż�ס���¼�
     */
    public void onADM_TYPE(){
        this.clearValue("DEPT_CODE;DR_CODE");
    }
    /**
     * �����¼�
     */
    public void onDEPT_CODE(){
        this.clearValue("DR_CODE");
    }
    
    
    
    /**
     * ���add by huangjw 20150722
     */
    public void onExcel(){
    	if(table.getRowCount()<=0){
    		this.messageBox("û�л������");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "�ż���ҽʦ������ͳ��");
    }
}
