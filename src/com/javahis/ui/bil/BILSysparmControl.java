package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>Title: ���������</p>
 *
 * <p>Description: ���������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009-8-11
 * @version 1.0
 */
public class BILSysparmControl extends TControl{
    /**
     * ��
     */
    TTable table;
      /**
       * ��ʼ��
       */
      public void onInit() {
          super.onInit();
          //�õ��������ϵı�
          table = (TTable)this.getComponent("TABLE");
      }
      /**
       * �����¼�
       */
      public void onTableClicked(){
          int row=table.getSelectedRow();
          TParm parm=table.getDataStore().getBuffer(table.getDataStore().PRIMARY).getRow(row);
          //�����Ϸ�
          setTextValue(parm);
          //�Ǽ�ס�𲻿ɱ༭
          setTextEnabled(false);
      }
      /**
       * �����Ϸ�
       * @param parm TParm
       */
      public void setTextValue(TParm parm){
          this.setValueForParm("ADM_TYPE;MONTH_CYCLE;DAY_CYCLE",parm);
      }
      /**
       * �������Ǽ�ס�������
       * @param boo boolean
       */
      public void setTextEnabled(boolean boo){
          this.callFunction("UI|ADM_TYPE|setEnabled",boo);
      }
      /**
       * ���
       */
      public void onClear(){
          setTextEnabled(true);
          this.clearValue("ADM_TYPE;MONTH_CYCLE;DAY_CYCLE");
          table.retrieve();
          table.setDSValue();
      }
      /**
       * ����
       * @return boolean
       */
      public boolean onSave(){
          int row=table.getSelectedRow();
          boolean saveFlg=false;
          //�����ѡ����Ϊ����
          if(row>=0){
              //�����������
              if(!dealUpdate(row,saveFlg))
                  return false;
          }
          else{
              //������������
              if(!dealNewData())
                  return false;
          }
          //�������ݷ���
          return saveData();
      }
      /**
       * ���ñ��淽��
       * @return boolean
       */
      public boolean saveData(){
          if (!table.update()) {
              messageBox("����ʧ��!");
              return false;
          }
          messageBox("����ɹ�!");
          table.resetModify();
          onClear();
          return true;
      }
      /**
       * ��������
       * @return boolean
       */
      public boolean dealNewData(){
          //�õ��ż�ס��
          String admType=getValueString("ADM_TYPE");
          //����ż�ס��
          if(!checkAdmType(admType))
              return false;
          //��������
         String monthCycle=getValueString("MONTH_CYCLE");
         //��˽�������
         if(!checkMonthCycle(monthCycle))
             return false;
         //�õ�����ʱ��
         String dayCycle=getValueString("DAY_CYCLE");
         //��˽���ʱ��
         if(!checkDayCycle(dayCycle))
             return false;
         //������������
         setNewData();
          return true;
      }
      /**
       * ������������
       */
      public void setNewData(){
          int row=table.addRow();
          table.setItem(row,"ADM_TYPE",getValueString("ADM_TYPE"));
          table.setItem(row,"MONTH_CYCLE",getValueString("MONTH_CYCLE"));
          table.setItem(row,"DAY_CYCLE",getValueString("DAY_CYCLE"));
          //��ӹ̶�����
          setTableData(row);
      }
      /**
       * ����ż�ס��
       * @param admType String
       * @return boolean
       */
      public boolean checkAdmType(String admType){
          if(admType==null||admType.length()==0){
             messageBox("�ż�ס����Ϊ��!");
             return false;
         }
         //��������ظ�
         if(table.getDataStore().exist("ADM_TYPE ='"+admType+"'")){
             messageBox("�����Ѵ���,�������!");
             return false;
         }
          return true;
      }
      /**
       * �����������
       * @param row int
       * @param saveFlg boolean
       * @return boolean
       */
      public boolean dealUpdate(int row,boolean saveFlg){
          //��������
          String monthCycle=getValueString("MONTH_CYCLE");
          //��˽�������
          if(!checkMonthCycle(monthCycle))
              return false;
          String month=table.getItemString(row,"MONTH_CYCLE");
          //������޸������
          if(!monthCycle.equals(month)){
              table.setItem(row, "MONTH_CYCLE", monthCycle);
              //��¼�Ƿ����޸�
              saveFlg=true;
          }
          //�õ�����ʱ��
          String dayCycle=getValueString("DAY_CYCLE");
          //��˽���ʱ��
          if(!checkDayCycle(dayCycle))
              return false;
          String day=table.getItemString(row,"DAY_CYCLE");
          if(!day.equals(dayCycle)){
              table.setItem(row, "DAY_CYCLE", dayCycle);
              //��¼�Ƿ����޸�
              saveFlg=true;
          }
          //������޸����޸Ĺ̶�����
          if(saveFlg)
              setTableData(row);
          return true;
      }
      /**
       * �޹̶�����
       * @param row int
       */
      public void setTableData(int row){
          table.setItem(row,"OPT_USER",Operator.getID());
          table.setItem(row,"OPT_DATE",SystemTool.getInstance().getDate());
          table.setItem(row,"OPT_TERM",Operator.getIP());
      }
      /**
       * ��˽���ʱ��
       * @param dayCycle String
       * @return boolean
       */
      public boolean checkDayCycle(String dayCycle){
          if(dayCycle==null||dayCycle.length()==0){
              messageBox("����ʱ�䲻��Ϊ��");
              return false;
          }
          int length=dayCycle.length();
          if(length!=6){
              messageBox("����ʱ���ʽΪ6λ����");
              return false;
          }
          for(int i=0;i<length;i++){
              String s=dayCycle.substring(i,i+1);
              if(!onClickCheck(s)){
                  messageBox("����ʱ�����������!");
                  return false;
              }
          }
          //���û����ĸ
          int monthDay=Integer.parseInt(dayCycle);
          if(monthDay<0||monthDay>235959){
              messageBox("����ʱ�������00000��235959֮�������");
              return false;
          }
          return true;
      }
      /**
       * ��˽������ڵ���Ч��
       * @param monthCycle String
       * @return boolean
       */
      public boolean checkMonthCycle(String monthCycle){
          if(monthCycle==null||monthCycle.length()==0){
              messageBox("�������ڲ���Ϊ��");
              return false;
          }
          int length=monthCycle.length();
          if(length!=2){
              messageBox("�������ڱ�����2λ����");
              return false;
          }

          for(int i=0;i<length;i++){
              String s=monthCycle.substring(i,i+1);
              if(!onClickCheck(s)){
                  messageBox("�������ڱ���������!");
                  return false;
              }
          }
          //���û����ĸ
          int monthDay=Integer.parseInt(monthCycle);
          if(monthDay<0||monthDay>31){
              messageBox("�������ڱ�����0��31֮�������");
              return false;
          }
          return true;
      }

      /**
       * ���������Ƿ�������
       * @param checkText String
       * @return boolean
       * @author pangjx 2007-08-10
       */
      public boolean onClickCheck(String checkText) {
          //������ʽ��ʾ������0��9֮��
          String regex = "[0-9]";
          boolean flag = false;
          if (checkText.matches(regex)) {
              flag = true;
          }
          else {
              flag = false;
          }
          return flag;
      }
















}
