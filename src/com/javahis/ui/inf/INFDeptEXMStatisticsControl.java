package com.javahis.ui.inf;

import jdo.inf.INFReportTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: ���Ҽ��ͳ�Ʊ�</p>
 *
 * <p>Description: ���Ҽ��ͳ�Ʊ�</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFDeptEXMStatisticsControl  extends TControl {

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        initUI();
    }


    /**
     * ��ʼ������
     */
    public void initUI(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("START_DATE",timestamp);
        setValue("END_DATE",timestamp);
        setValue("YEAR",timestamp);
        setValue("DEPT_CODE",Operator.getDept());
        setValue("YEAR_MONTH",timestamp);
    }

    /**
     * ��ӡ�¼�
     */
    public void onQuery(){
        if(getValue("DEPT_RESULT").equals("Y"))
            deptEXMStatisticsDate();
        if(getValue("DEPT_YEAR").equals("Y"))
            selectDeptMonCount();
        if(getValue("DEPT_MONTH").equals("Y"))
            selectYearMonEXMStatistics();
    }

    /**
     * ����ͳ�Ʋ�ѯ������
     */
    private void deptEXMStatisticsDate(){
        if(getValueString("START_DATE").length() == 0 ||
           getValueString("END_DATE").length() == 0){
            messageBox("�����뿪ʼ���ڼ���������");
            return;
        }
        if(getValueString("START_DATE").compareTo(getValueString("END_DATE"))>0){
            messageBox("����Ŀ�ʼ���ڼ�������������");
            return;
        }
        TParm parm = new TParm();
        parm.setData("START_DATE",getValue("START_DATE"));
        parm.setData("END_DATE",getValue("END_DATE"));
        //=============pangben modify 20110629 start
        if(null!=Operator.getRegion() && Operator.getRegion().length()>0){
            parm.setData("REGION_CODE",Operator.getRegion());
        }
        //=============pangben modify 20110629 stop
        parm = INFReportTool.getInstance().deptEXMStatisticsDate(parm);
        getTable("TABLE").setHeader("����,120;�������,100;�����Ŀ,200;�����,80;�ܷ�,66;��ע,200");
        getTable("TABLE").setLockColumns("0,1,2,3,4,5");
        getTable("TABLE").setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,right;5,left");
        getTable("TABLE").setParmMap("DEPT_CHN_DESC;EXAM_DATE;CHN_DESC;PASS_FLG;ITEM_GAINPOINT;REMARK");
        getTable("TABLE").setParmValue(parm);
    }

    /**
     * ȡ�ÿ�����ȼ����Ϣ
     */
    private void selectDeptMonCount(){
       if(getValueString("YEAR").length() == 0){
           messageBox("���������");
           return;
       }
       if(getValueString("DEPT_CODE").length() == 0){
           messageBox("���������");
           return;
       }
       TParm parm = new TParm();
       parm.setData("YEAR",getValueString("YEAR").substring(0,4));
       parm.setData("DEPT_CODE",getValue("DEPT_CODE"));
       //=============pangben modify 20110629 start
        if(null!=Operator.getRegion() && Operator.getRegion().length()>0){
            parm.setData("REGION_CODE",Operator.getRegion());
        }
        //=============pangben modify 20110629 stop
       parm = INFReportTool.getInstance().selectDeptMonCount(parm);
       getTable("TABLE").setHeader("�·�,80;�������,90;�ܷ�,66;ƽ��,80");
       getTable("TABLE").setLockColumns("0,1,2,3");
       getTable("TABLE").setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right");
       getTable("TABLE").setParmMap("EXAM_MONTH;EXAM_DATE;ITEM_GAINPOINT;ITEM_GAINPOINT_AVERAGE");
       getTable("TABLE").setParmValue(parm);
   }

   /**
    * ȡ��ҽԺ�����ͳ����Ϣ
    */
   private void selectYearMonEXMStatistics(){
      if(getValueString("YEAR_MONTH").length() == 0){
          messageBox("����������");
          return;
      }
      TParm parm = new TParm();
      parm.setData("YEAR_MONTH",getValueString("YEAR_MONTH").replace("-","").substring(0,6));
      //=============pangben modify 20110629 start
        if(null!=Operator.getRegion() && Operator.getRegion().length()>0){
            parm.setData("REGION_CODE",Operator.getRegion());
        }
        //=============pangben modify 20110629 stop
      parm = INFReportTool.getInstance().selectYearMonEXMStatistics(parm);
      getTable("TABLE").setHeader("����,120;�������,160;ƽ����,90;��ע,80");
      getTable("TABLE").setLockColumns("0,1,2");
      getTable("TABLE").setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,left");
      getTable("TABLE").setParmMap("DEPT_CHN_DESC;DATE_ALL;ITEM_GAINPOINT;REMARK");
      getTable("TABLE").setParmValue(parm);
   }


   /**
    * ����Excel���
    */
   public void onExcel(){
      TTable mainTable = getTable("TABLE");
      if(mainTable.getRowCount() <= 0){
          messageBox("�޵�������");
          return;
      }
      String tag = "";
      if(getValueString("DEPT_RESULT").equals("Y"))
          tag = "���Ҽ��ͳ�Ʊ�-" + "���Ҽ����ͳ��";
      else if(getValueString("DEPT_YEAR").equals("Y"))
          tag = "���Ҽ��ͳ�Ʊ�-" + "���Ҽ����ͳ��";
      else if(getValueString("DEPT_MONTH").equals("Y"))
          tag = "���Ҽ��ͳ�Ʊ�-" + "ҽԺ�����ͳ��";
      ExportExcelUtil.getInstance().exportExcel(mainTable, tag);
   }
    /**
     * �õ�������
     * @param tag String
     * @return TTable
     */
    private TTable getTable(String tag){
        return (TTable)getComponent(tag);
    }

    /**
     * ��շ���
     */
    public void onClear(){
        onInit();
        setValue("DEPT_RESULT","Y");
        getTable("TABLE").removeRowAll();
    }
}
