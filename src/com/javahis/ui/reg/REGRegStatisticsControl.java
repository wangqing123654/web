package com.javahis.ui.reg;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.reg.PanelTypeTool;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import jdo.reg.REGRegStatisticsTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.text.DecimalFormat;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TRadioButton;

/**
 * <p>Title: �Һ���Ϣͳ��</p>
 *
 * <p>Description: �Һ���Ϣͳ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-03-18
 * @version 4.0
 */
public class REGRegStatisticsControl
    extends TControl {
private TTable TABLE;
  private TParm CLINICTYPE;//��¼�ű�
  private String ParmMap = "";//��¼�������ݵ�˳��
  private String STA_DATE = "";//��¼��ѯʱ�� ������ʾ�ڴ�ӡ��
  private TParm result;//��¼�����
  DecimalFormat df = new DecimalFormat();//�涨��ʽ ����������ѯ���ô�С���㣬�����á���ѯ����2λС��
  public void onInit(){
//      ID,1;NAME,1;PY1,1

              super.onInit();
     String title = (String) this.getParameter();
     this.setTitle(title);
      TABLE = (TTable)this.getComponent("Table");
      pageInit();
      tableInit();
      //=========pangben modify 20110422 start
      TTextFormat  tftDept =(TTextFormat)this.getComponent("DEPT_CODE");
      String sql="SELECT DEPT_CODE AS ID,DEPT_CHN_DESC AS NAME,DEPT_ENG_DESC,PY1 FROM SYS_DEPT WHERE (OPD_FIT_FLG='Y' OR EMG_FIT_FLG='Y') AND DEPT_GRADE='3'";
      if(null!=Operator.getRegion()&&!"".equals(Operator.getRegion()))
          sql+=" AND REGION_CODE ='"+Operator.getRegion()+"'";
      tftDept.setPopupMenuSQL(sql);
      tftDept.setPopupMenuFilter("ID,1;NAME,1;PY1,1");
      //==========pangben modify 20110422 stop
      //========pangben modify 20110421 start Ȩ�����
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

  }
  /**
   * ����ҳ����ʼ״̬
   */
  private void pageInit(){
      //this.setValue("DEPT_LEAVE","3");//Ĭ��ѡ����������
      //Ĭ������
      //===========pangben modify 20110325
      setValue("REGION_CODE", Operator.getRegion());
      //������ֹ����
      Timestamp now = SystemTool.getInstance().getDate();
      this.setValue("DATE_S",now);
      this.setValue("DATE_E",now);
  }
  /**
   *  ����ʼ��
   */
  private void tableInit(){
      CLINICTYPE = PanelTypeTool.getInstance().queryTree();
      String tableHead = "";
      //ѭ���ű� ָ�����ı�ͷ �� �������ݵ� �ֶ���
      for (int i = 0; i < CLINICTYPE.getCount(); i++) {
          tableHead += CLINICTYPE.getValue("CLINICTYPE_DESC",i) + ",100;";
          ParmMap += CLINICTYPE.getValue("CLINICTYPE_CODE",i) + ";";
      }
      ParmMap += "SUM";
      ParmMap = "DEPT_DESC;"+ParmMap;
      ParmMap = "REGION_CHN_ABN;"+ParmMap;//========pangben modify 20110410,  modify 20120305
      TABLE.setHeader("����,120;��������,100;"+tableHead+"�ϼ�,100");
  }
  /**
   * ���ҵȼ��������¼�
   */
  public void onDEPT_LEAVE(){
      String leave = this.getValueString("DEPT_LEAVE");
      if("2".equals(leave)){
          this.clearValue("DEPT_CODE;USER_ID");
          this.callFunction("UI|DEPT_CODE|setEnabled",false);
          this.callFunction("UI|USER_ID|setEnabled",false);
      }else if("3".equals(leave)){
          this.callFunction("UI|DEPT_CODE|setEnabled",true);
          this.callFunction("UI|USER_ID|setEnabled",true);
      }
  }
  /**
   * ����¼�
   */
  public void onClear(){
      pageInit();
      this.clearValue("DEPT_CODE;USER_ID");
      TABLE.removeRowAll();
  }
  /**
   * ��ѯ
   */
  public void onQuery(){
      TParm data = new TParm();
      TParm parm = new TParm();
      parm.setData("DATE_S",StringTool.getString((Timestamp)this.getValue("DATE_S"),"yyyyMMdd")+"000000");
      parm.setData("DATE_E",StringTool.getString((Timestamp)this.getValue("DATE_E"),"yyyyMMdd")+"235959");
      //��Ӳ�ѯ����
      //========pangben modify 20110325 start
      if(this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals(""))
          parm.setData("REGION_CODE",this.getValue("REGION_CODE"));
       //========pangben modify 20110325 stop
      //����ͳ��
      //===========pangben modify 20110413 start �޸Ĳ���Ҫ����������ѯ
      if (this.getValueString("DEPT_CODE").length() > 0)
          parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
      if (this.getValueString("DR_CODE").length() > 0)
          parm.setData("DR_CODE", this.getValueString("DR_CODE"));
      if ("Y".equals(this.getValueString("type1"))) {
          data = REGRegStatisticsTool.getInstance().selectNumForDept3(parm);
          df = new DecimalFormat("0");
//          //�ж� ��������
//          if("2".equals(getValueString("DEPT_LEAVE"))){
//              data = REGRegStatisticsTool.getInstance().selectNumForDept2(parm);
//          }
//          else if("3".equals(getValueString("DEPT_LEAVE"))){
//              if(this.getValueString("DEPT_CODE").length()>0)
//                  parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
//              if(this.getValueString("DR_CODE").length()>0)
//                  parm.setData("DR_CODE",this.getValueString("DR_CODE"));
//              data = REGRegStatisticsTool.getInstance().selectNumForDept3(parm);
//          }
      } else if ("Y".equals(this.getValueString("type2"))) {
          df = new DecimalFormat("0.00");

          data = REGRegStatisticsTool.getInstance().selectNumForDept3_M(parm);
//          //�ж� ��������
//          if("2".equals(getValueString("DEPT_LEAVE"))){
//              data = REGRegStatisticsTool.getInstance().selectNumForDept2_M(parm);
//          }
//          else if("3".equals(getValueString("DEPT_LEAVE"))){
//              if(this.getValueString("DEPT_CODE").length()>0)
//                  parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
//              if(this.getValueString("DR_CODE").length()>0)
//                  parm.setData("DR_CODE",this.getValueString("DR_CODE"));
//              data = REGRegStatisticsTool.getInstance().selectNumForDept3_M(parm);
//          }
      }
      //===========pangben modify 20110413 stop
      if ( null==data ||data.getCount()==0){
          this.messageBox("û����Ҫ��ѯ������");
          return;
      }
      //������������
      result = getResult(data);
      //�����ܼ���
      String[] code = ParmMap.split(";");
      TParm sumRow = new TParm();
      for(int i=2;i<code.length;i++){
          String CLINICTYPE_CODE = code[i];
          double num = 0;
          for (int j = 0; j < result.getCount("DEPT_DESC"); j++) {
              num += result.getDouble(CLINICTYPE_CODE,j);
              sumRow.setData(CLINICTYPE_CODE,0,df.format(num));
          }
      }
      sumRow.setData("DEPT_DESC",0,"��  ��");
      //=============pangben modify 20110426 start
      int count=result.getCount("DEPT_DESC");
      result.setRowData(count,sumRow,0);
      result.setData("DEPT_DESC", count, "");
      result.setData("REGION_CHN_ABN", count, "�ܼ�:");               //======fuxin  modify 20120305
      if (((TRadioButton)this.getComponent("type1")).isSelected()) {
          getValueFormat("N", result, count);//��ʽ��������ʾ��ͬ����

      } else if(((TRadioButton)this.getComponent("type2")).isSelected()){
          getValueFormat("Y", result, count);//��ʽ��������ʾ��ͬ����
      }
      TABLE.setParmValue(result, ParmMap);
      STA_DATE = StringTool.getString((Timestamp)this.getValue("DATE_S"),
                                      "yyyy/MM/dd") + " " +
                 this.getValue("S_TIME") + "��" +
          StringTool.getString( (Timestamp)this.getValue("DATE_E"), "yyyy/MM/dd")+" "+this.getValue("E_TIME");
      //=============pangben modify 20110410 stop
  }
  /**
   * ��ʽ����ʾ���ݣ�ͨ��ѡ��ѡ��ť������ʾ��ͬ�ĸ�ʽ
   * @param type String
   * @param result TParm
   * @return TParm
   * ========pangben modify 20110426
   */
  public TParm getValueFormat(String type, TParm result, int count) {

      if(type.equals("Y")){
          double sumZRYS = 0.00; //����ҽʦ
          double sumFZRYS = 0.00; //������ҽʦ
          double sumZZYS = 0.00; //����ҽʦ
          double sumClinic = 0.00; //����
          double sumEmergency = 0.00; //����
          double sumExpert = 0.00; //ר��
          double sumAdvClinic = 0.00; //��������
          double sumCount = 0.00; //�ܼ�

          for (int i = 0; i < count; i++) {
              sumZRYS += StringTool.round(result.getDouble("1", i), 2);
              sumFZRYS += StringTool.round(result.getDouble("2", i), 2);
              sumZZYS += StringTool.round(result.getDouble("3", i), 2);
              sumClinic += StringTool.round(result.getDouble("4", i), 2);
              sumEmergency += StringTool.round(result.getDouble("5", i), 2);
              sumExpert += StringTool.round(result.getDouble("6", i), 2);
              sumAdvClinic += StringTool.round(result.getDouble("7", i), 2);
              sumCount += StringTool.round(result.getDouble("SUM", i), 2);
          }

          df = new DecimalFormat("0.00");
          result.setData("1", count, df.format(StringTool.round(sumZRYS, 2)));
          result.setData("2", count, df.format(StringTool.round(sumFZRYS, 2)));
          result.setData("3", count, df.format(StringTool.round(sumZZYS, 2)));
          result.setData("4", count, df.format(StringTool.round(sumClinic, 2)));
          result.setData("5", count, df.format(StringTool.round(sumEmergency, 2)));
          result.setData("6", count, df.format(StringTool.round(sumExpert, 2)));
          result.setData("7", count, df.format(StringTool.round(sumAdvClinic, 2)));
          result.setData("SUM", count, df.format(StringTool.round(sumCount, 2)));
      }else{
         int sumZRYS = 0; //����ҽʦ
         int sumFZRYS = 0; //������ҽʦ
         int sumZZYS = 0; //����ҽʦ
         int sumClinic = 0; //����
         int sumEmergency = 0; //����
         int sumExpert = 0; //ר��
         int sumAdvClinic = 0; //��������
         int sumCount = 0; //�ܼ�
          for (int i = 0; i < count; i++) {
              sumZRYS += result.getInt("1", i);
              sumFZRYS += result.getInt("2", i);
              sumZZYS += result.getInt("3", i);
              sumClinic += result.getInt("4", i);
              sumEmergency += result.getInt("5", i);
              sumExpert += result.getInt("6", i);
              sumAdvClinic += result.getInt("7", i);
              sumCount += result.getInt("SUM", i);
              for(int j=1;j<8;j++){
                  result.setData(j+"", i, result.getInt(j+"", i));
              }
              result.setData("SUM", i, result.getInt("SUM", i));
          }
          result.setData("1", count, sumZRYS);
          result.setData("2", count, sumFZRYS);
          result.setData("3", count, sumZZYS);
          result.setData("4", count,sumClinic);
          result.setData("5", count, sumEmergency);
          result.setData("6", count,sumExpert);
          result.setData("7", count, sumAdvClinic);
          result.setData("SUM", count, sumCount);
      }
      return result;
  }
  /**
   * �������ڰ󶨱�������
   * @param parm TParm
   * @return TParm
   */
  private TParm getResult(TParm parm){
      //����Ҫ��ѯ�Ŀ�����Ϣ

      TParm dept = new TParm();
      //========pangben modify 20110325 start
      TParm parm1 = new TParm();
      if (this.getValue("REGION_CODE") != null &&
          !this.getValue("REGION_CODE").equals(""))
          parm1.setData("REGION_CODE", this.getValue("REGION_CODE"));
      //========pangben modify 20110325 stop
      //===============pangben modify 20110413 �޸Ĳ���Ҫ����������ѯ
//      if ("2".equals(getValueString("DEPT_LEAVE"))) {
//          dept = REGRegStatisticsTool.getInstance().selectDept2();//��ȡ���������б�
//      }else if ("3".equals(getValueString("DEPT_LEAVE"))) {
//          if("".equals(this.getValueString("DEPT_CODE")))
//              //========pangben modify 20110325 start ��Ӳ���
//              dept = REGRegStatisticsTool.getInstance().selectOEDpet(parm1);//��ȡ�����ż��ﲿ���б�
//              //========pangben modify 20110325 stop
//          else if(this.getValueString("DEPT_CODE").length()>0){
//              dept.addData("ID",this.getValue("DEPT_CODE"));
//              dept.addData("NAME",this.getText("DEPT_CODE"));
//              dept.setCount(1);
//          }
//      }
      //===============pangben modify 20110413 stop
       dept = REGRegStatisticsTool.getInstance().selectOEDpet(parm1);//��ȡ�����ż��ﲿ���б�
      TParm re = new TParm();
      int row = 0;
      //ѭ����ѯÿһ�����ҵ�����
      for(int i=0;i<dept.getCount();i++){
          TParm rowData = getRowParm(parm,dept.getValue("ID",i),dept.getValue("REGION_CHN_ABN",i)); //======fuxin  modify 20120305
          if(rowData.getValue("DEPT_DESC",0).length()>0){
              re.setRowData(row,rowData,0);
              row++;
          }
      }

      return re;
  }
  /**
   * ��ȡÿ�����ҵ�����
   * @param parm TParm ȫ������
   * @param DEPT_CODE String  ����CODE
   * @return TParm
   * ========pangben modify 20110413 ����������
   */
  private TParm getRowParm(TParm parm,String DEPT_CODE,String REGION_CODE){
      int row = 0;
      TParm deptData = new TParm();
      for(int i=0;i<parm.getCount();i++){
          if(DEPT_CODE.equals(parm.getValue("DEPT_CODE",i))&&parm.getValue("REGION_CHN_ABN",i).equals(REGION_CODE)){ //======fuxin  modify 20120305
              deptData.setRowData(row,parm.getRow(i));
              row++;
          }
      }
      TParm result = new TParm();
      boolean flg = false;//��¼�Ƿ��иÿ��ҵ�����
      double sum = 0.00;
      //�ӻ��ܵ�һ�����ҵ������� �����ÿ�ֺű������
      for(int i=0;i<CLINICTYPE.getCount();i++){
          boolean flg2 = false;//��¼�ÿ����Ƿ�����˺ű����Ϣ
          for(int j=0;j<deptData.getCount("DEPT_CODE");j++){
              if(CLINICTYPE.getValue("CLINICTYPE_CODE",i).equals(deptData.getValue("CLINICTYPE_CODE",j))){
                  result.setData(CLINICTYPE.getValue("CLINICTYPE_CODE",i), 0,df.format(deptData.getData("NUM", j)));
                  sum +=StringTool.round(deptData.getDouble("NUM", j),2);
                  flg = true;
                  flg2 = true;
              }
          }
          //����������úű�ĹҺ���Ϣ ��ô��Ĭ��ֵ0
          if(!flg2){
              result.setData(CLINICTYPE.getValue("CLINICTYPE_CODE",i), 0,df.format(0));
          }
      }

      //����иÿ��ҵ����� ��ôΪ��������д��������� д��ϼ���
      if(flg){
          //===============pangben modify 20110410 start
          result.setData("REGION_CHN_ABN", 0, deptData.getValue("REGION_CHN_ABN", 0)); //======fuxin  modify 20120305
          //===============pangben modify 20110410 stop
          result.setData("DEPT_DESC", 0, deptData.getValue("DEPT_CHN_DESC", 0));
          result.setData("SUM", 0,df.format(sum));
      }
      return result;
  }
  /**
   * ����ѡ���¼�
   */
  public void onDEPT_CODE(){
      this.clearValue("DR_CODE");
      this.callFunction("UI|DR_CODE|onQuery");
  }
  /**
   * ��ӡ
   */
  public void onPrint(){
      if(result.getCount("DEPT_DESC")<=0){
          return;
      }
      TParm t1 = new TParm();
      TParm headerT=new TParm();
      String header=TABLE.getHeader().replace(",100","");
      String[] titleValue =  (header.replace(",120","")).split(";");
      String[] title = ParmMap.split(";");
      for (int j = 0; j < 10; j++) {

          if (j < title.length)
              headerT.addData(title[j], titleValue[j]);
          else
              headerT.addData(""+(j-2), "");
      }
      headerT.setCount(1);
      headerT.addData("SYSTEM","COLUMNS","REGION_CHN_ABN");//======pangben modify 20110410,fuxin  modify 20120305
      headerT.addData("SYSTEM","COLUMNS","DEPT_DESC");

//      for (int i = 1; i < 8; i++) {
//          if (i < result.getCount("DEPT_DESC") + 1){
//               System.out.println("t1:"+i+":"+t1);
//              t1.setRowData(i, result.getRow(i - 1));
//          }
//          else{
////              TParm parm=new TParm();
////              parm.setData("","");
//              System.out.println("i:"+i);
//              for(int k=0;k<result.getCount();k++){
//                  t1.addData(""+i,"");
//              }
//            //  t1.setRowData(i, parm);
//            System.out.println("t1:"+i+":"+t1);
//          }
//      }
      t1.setCount(result.getCount("DEPT_DESC"));
      t1.addData("SYSTEM","COLUMNS","REGION_CHN_ABN");//======pangben modify 20110410,fuxin  modify 20120305
      t1.addData("SYSTEM","COLUMNS","DEPT_DESC");
      //==============����������д����
      for(int i=0;i<8;i++){
          if(i==CLINICTYPE.getCount()){
             headerT.addData("SYSTEM","COLUMNS","SUM");
             t1.addData("SYSTEM","COLUMNS","SUM");
             continue;
         }
          if(i<CLINICTYPE.getCount()){
              t1.addData("SYSTEM", "COLUMNS",
                         CLINICTYPE.getValue("CLINICTYPE_CODE", i));
              headerT.addData("SYSTEM", "COLUMNS",
                              CLINICTYPE.getValue("CLINICTYPE_CODE", i));
          }else{
              t1.addData("SYSTEM", "COLUMNS",
                         ""+i);
              headerT.addData("SYSTEM", "COLUMNS",
                              ""+i);
          }
      }
      //===========pangben modify 20110701 start ��û�����ݵ�����ʾ����
      for (int i = 0; i < result.getCount("DEPT_DESC"); i++) {
          for (int j = 0; j < 8; j++) {
              if(j<CLINICTYPE.getCount()){
                  t1.addData(CLINICTYPE.getValue("CLINICTYPE_CODE", j),
                             result.getValue(CLINICTYPE.getValue(
                                     "CLINICTYPE_CODE", j), i));
              }else
                  t1.addData(""+j,"");

          }
          t1.addData("SUM", result.getValue("SUM", i));
          t1.addData("REGION_CHN_ABN",
                     result.getValue("REGION_CHN_ABN", i)); //======fuxin  modify 20120305
          t1.addData("DEPT_DESC", result.getValue("DEPT_DESC", i));
      }
      //===========pangben modify 20110701 stop
      t1.setData("REGION_CHN_ABN", result.getCount("DEPT_DESC"), "�ܼ�:"); //======pangben modify 20110410
      TParm printData = new TParm();
      printData.setData("headerT",headerT.getData());
      printData.setData("T1",t1.getData());
      //========pangben modify 20110329 start
      TTable table=  ((TTable)this.getComponent("Table"));
      String region=table.getParmValue().getRow(0).getValue("REGION_CHN_ABN");//======fuxin modify 20120305
      printData.setData("TITLE1", "TEXT",
                  (this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals("")?region:"����ҽԺ" ) );
      printData.setData("TITLE2", "TEXT","�Һ���Ϣͳ�Ʊ�");
     //========pangben modify 20110329 stop
      printData.setData("date","TEXT","��ѯ���ڣ�"+StringTool.getString((Timestamp)this.getValue("DATE_S"),"yyyy/MM/dd")+" 00:00:00"+" �� "+StringTool.getString((Timestamp)this.getValue("DATE_E")," yyyy/MM/dd")+"23:59:59");
      printData.setData("printDate","TEXT","��ӡ����:"+StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd"));
      printData.setData("printUser","TEXT",Operator.getName());
      this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRegStatistics.jhw",printData);
  }
  /**
   * ����Excel���
   */
  public void onExport() {
      TTable table = (TTable) callFunction("UI|Table|getThis");
      if (table.getRowCount() <= 0) {
          messageBox("�޵�������");
          return;
      }
      ExportExcelUtil.getInstance().exportExcel(table, "�Һ���Ϣͳ�Ʊ���");
  }

}
