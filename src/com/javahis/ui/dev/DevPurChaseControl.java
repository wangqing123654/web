package com.javahis.ui.dev;

import com.dongyang.config.TConfigParm;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.base.JTableBase;
import com.dongyang.ui.base.TTableColumnModel;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import java.awt.Component;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date; 

import jdo.dev.DEVStockCheckTool;
import jdo.dev.DevBaseDataStore;
import jdo.dev.DevCheckTool;
import jdo.dev.DevPurChaseDTool;
import jdo.dev.DevPurChaseMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import jdo.util.Organization;


/**  
 * <p>Title: �빺��ҵ</p>  
 *
 * <p>Description: �빺��ҵ</p>  
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: javahis</p>
 *
 * @author  fux
 * @version 1.0      
 */
public class DevPurChaseControl extends TControl
{
 /**
  * ����������
  */
  private String actionName = "action.dev.DevAction";
  /**
   * ��
   */
  private static String TABLE1 = "TABLE1";
  /**
   * ϸ
   */
  private static String TABLE2 = "TABLE2";
  /**
   * �빺����
   */
  private String rateofproCode = "";
  /**
   * ���״̬
   */
  private boolean checkFlg = false;
  /**
   * ��ʼ������
   */
  public void onInitParameter()
  {
      /**
       * 1��һ��Ȩ�ޣ�������Ա��:�������ҿ�����Ա,�빺����Ĭ��Ϊ����������������׼ GeneralPermissions
       * 2����ˣ��豸�Ƴ��� AuditPermissions
       */
//      //һ��Ȩ��
//      this.setPopedem("GeneralPermissions",true);
//      //�ϲ��빺��
//      this.setPopedem("ClerksPermissions",true);
      //��� 
//      this.setPopedem("AuditPermissions",true);
  }
  /**
   * ��ʼ��
   */
  public void onInit()
  {
    /**
     * ��ʼ��ҳ��
     */	  
    onInitPage();
    /**
     * ��ʼ��Ȩ��
     */
    onInitPopeDem();
    /**
     * ��ʼ���¼�
     */
    initEven();
  }
  /**
   * ��ʼ��ҳ��
   */	  
  public void onInitPage()
  {
	//���ҡ���Ա
    setValue("QDEPT_CODE", Operator.getDept());
    setValue("QOPERATOR", Operator.getID());
    setValue("REQUEST_DEPT", Operator.getDept());
    setValue("REQUEST_USER", Operator.getID());
    //��������
    setValue("RATEOFPRO_CODE", "A");
    setValue("QRATEOFPRO_CODE", "A");
    //����
    Timestamp startDate = StringTool.getTimestamp(StringTool.getString(StringTool.getTimestamp(new Date()), "yyyy/MM/dd"), "yyyy/MM/dd");
    //����
    Timestamp endDate = StringTool.getTimestamp("9999/12/31", "yyyy/MM/dd");
    //��ѯ�빺��������
    setValue("QSTART_DATE", startDate);
    //��ѯ�빺��������  
    setValue("QEND_DATE", endDate);
    //�빺����
    setValue("REQUEST_DATE", startDate);
    //Ԥ���������� 
    setValue("QUSERSTART_DATE", startDate);
    //Ԥ����������  
    setValue("QUSEEND_DATE", endDate);
    //Ԥ��������
    setValue("USE_DATE", startDate);
    //��ʼ��TABLE1
    getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
    getTTable(TABLE1).setDSValue();
    //��ʼ��TABLE2
    getTTable(TABLE2).setDataStore(getTableTDataStore("TABLE2"));
    getTTable(TABLE2).setDSValue();
    //����һ��
    insertRow(TABLE2);
    getTTable(TABLE2).setDSValue();
  }
  /** 
   * �¼���ʼ��
   */
  public void initEven()
  {
	//����TABLE1�����¼�  
    callFunction("UI|" + TABLE1 + "|addEventListener", new Object[] { 
      TABLE1 + "->" + "table.clicked", this, "onTableClicked" });
    //ϸ��TABLE2ֵ�ı����
    addEventListener(TABLE2 + "->" + "table.changeValue", this, 
      "onChangeTableValue");     
    //ϸ��TABLE2�����¼�
    getTTable(TABLE2).addEventListener("table.createEditComponent", this, 
      "onCreateEditComoponent");
    //ϸ��checkBox�����¼� 
    getTTable(TABLE2).addEventListener("table.checkBoxClicked", this, "onCheckBoxValue");
  }
  /**
   * checkBox����¼� 
   * @param obj Object
   */
  public void onCheckBoxValue(Object obj)
  {
    TTable table = (TTable)obj;
    table.acceptText();
    int col = table.getSelectedColumn();
    String columnName = getTTable(TABLE2).getDataStoreColumnName(col);
    if ("#ACTIVE#".equals(columnName)) {
      double totAmt = getTotAmt(table.getDataStore());
      setValue("TOT_AMT", Double.valueOf(totAmt));
    }
  }
  /**
   * ֵ�ı��¼�����   
   * @param obj Object 
   */  
  public boolean onChangeTableValue(Object obj)
  {
	//�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ  
    TTableNode node = (TTableNode)obj;
    if (node == null) { 
      return true;
    } 
    //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
    if (node.getValue().equals(node.getOldValue())) {
      return true;
    }
    //�õ�table�ϵ�parmmap������
    String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
    if ("#ACTIVE#".equals(columnName)) {
      return false;
    }
    if ("UNIT_PRICE".equals(columnName)) {
      node.getTable().getDataStore().setItem(node.getRow(), "UNIT_PRICE", node.getValue());
      node.getTable().setDSValue(node.getRow());
    }
    if ("QTY".equals(columnName)) {
      node.getTable().getDataStore().setItem(node.getRow(), "QTY", node.getValue());
      node.getTable().setDSValue(node.getRow());
    }
    double totAmt = getTotAmt(node.getTable().getDataStore());
    setValue("TOT_AMT", Double.valueOf(totAmt));
    return false;
  }  
  /**
   * ����¼� 
   */
  public void onTableClicked(int row)
  {
    TParm parm = getTTable(TABLE1).getDataStore().getRowParm(row);
    callFunction("UI|setSysStatus", new Object[] { parm.getValue("DEV_CODE") + ":" + parm.getValue("DEV_CHN_DESC") + parm.getValue("SPECIFICATION") });
    //�빺����
    this.rateofproCode = parm.getValue("RATEOFPRO_CODE");
    if ((parm.getData("CHK_DATE") != null) && (parm.getValue("CHK_USER").length() != 0))
      this.checkFlg = true;
    else {
      this.checkFlg = false;
    }
    setValue("REQUEST_NO", parm.getData("REQUEST_NO"));
    setValue("REQUEST_DEPT", parm.getData("REQUEST_DEPT"));
    setValue("REQUEST_USER", parm.getData("REQUEST_USER"));
    setValue("RATEOFPRO_CODE", parm.getData("RATEOFPRO_CODE"));
    setValue("REQUEST_DATE", parm.getData("REQUEST_DATE"));
    setValue("USE_DATE", parm.getData("USE_DATE"));
    setValue("TOT_AMT", parm.getData("TOT_AMT"));
    setValue("CHECK_FLG", Boolean.valueOf(this.checkFlg));
    setValue("FUNDSOU_CODE", parm.getData("FUNDSOU_CODE"));
    setValue("PURTYPE_CODE", parm.getData("PURTYPE_CODE"));
    setValue("DEVUSE_CODE", parm.getData("DEVUSE_CODE"));
    setValue("DETAILED_USE", parm.getData("DETAILED_USE"));
    setValue("REQUEST_REASON", parm.getData("REQUEST_REASON"));
    setValue("BENEFIT_PROVE", parm.getData("BENEFIT_PROVE"));
    setValue("REMARK", parm.getData("REMARK"));
    //�ж��Ƿ��������µ���Ŀ
    if ((this.rateofproCode.equals("C")) || (this.rateofproCode.equals("D")) || (this.rateofproCode.equals("E")) || (this.checkFlg)) {
      getTTextFormat("REQUEST_DEPT").setEnabled(false);
      getTTextFormat("REQUEST_USER").setEnabled(false);
      getTTextFormat("REQUEST_DATE").setEnabled(false);
      getTTextFormat("USE_DATE").setEnabled(false);
      getTNumberTextField("TOT_AMT").setEnabled(false);
      getTComboBox("FUNDSOU_CODE").setEnabled(false);
      getTComboBox("PURTYPE_CODE").setEnabled(false);
      getTComboBox("DEVUSE_CODE").setEnabled(false);
    } else {
      getTTextFormat("REQUEST_DEPT").setEnabled(true);
      getTTextFormat("REQUEST_USER").setEnabled(true);
      getTTextFormat("REQUEST_DATE").setEnabled(true);
      getTTextFormat("USE_DATE").setEnabled(true);
      getTNumberTextField("TOT_AMT").setEnabled(true);
      getTComboBox("FUNDSOU_CODE").setEnabled(true);
      getTComboBox("PURTYPE_CODE").setEnabled(true);
      getTComboBox("DEVUSE_CODE").setEnabled(true);
    }

    getTTable(TABLE2).setDataStore(getRequestDData(parm.getValue("REQUEST_NO")));
    getTTable(TABLE2).setDSValue();
    //��ʼ��TABLE2
    if (!(this.checkFlg)) {
      //���һ��
      insertRow(TABLE2);
      getTTable(TABLE2).setLockColumns("");
    }
    else {
      getTTable(TABLE2).setLockColumns("all");
    }
    getTTable(TABLE2).setDSValue();
  }
  /**
   * �õ����ֿؼ�
   * @param tag String
   * @return TNumberTextField
   */
  public TNumberTextField getTNumberTextField(String tag)
  {
    return ((TNumberTextField)getComponent(tag));
  }
  /**
   * �õ�����֮ǰ���к�
   * @param column int
   * @return int
   */
  public int getThisColumnIndex(int column)
  {
    return getTTable(TABLE2).getColumnModel().getColumnIndex(column);
  }
  /**
   * ����ʵ������
   * @param column String
   * @param column int
   * @return String
   */
  public String getFactColumnName(String tableTag, int column)
  {
    int col = getThisColumnIndex(column);
    return getTTable(tableTag).getDataStoreColumnName(col);
  }
  /**
   * ��TABLE�����༭�ؼ�ʱ��ʱ
   * @param com Component
   * @param row int
   * @param column int
   */
  public void onCreateEditComoponent(Component com, int row, int column)
  { 
	//�豸����
    String devProCode = getTTable(TABLE2).getDataStore().getItemString(row, "DEVPRO_CODE");
    //״̬����ʾ
    callFunction("UI|setSysStatus",""); 
    //�õ�����
    String columnName = getFactColumnName(TABLE2, column);
    if (!("DEV_CHN_DESC".equals(columnName)))
      return;
    if (!(com instanceof TTextField))
      return;
    TTextField textFilter = (TTextField)com;
    textFilter.onInit();
    TParm parm = new TParm(); 
    parm.setData("DEVPRO_CODE", devProCode);
    parm.setData("ACTIVE", "Y");
    //���õ����˵� 
    textFilter.setPopupMenuParameter("DEVBASE", getConfigParm().newConfig("%ROOT%" +
    		"\\config\\sys\\DEVBASEPopupUI.x"), parm);
//    //������ܷ���ֵ����
//    textFilter.addEventListener("popupMenu.ReturnValue", this, "popReturn");
    //������ܷ���ֵ����         
    textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
  }   
  
  /**
   * ���ܷ���ֵ����
   * @param tag String
   * @param obj Object
   */
  public void popReturn(String tag, Object obj)  
  {
	//�ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
    if ((obj == null) && (!(obj instanceof TParm))) {
      return;
    } 
    //����ת����TParm
    TParm action = (TParm)obj;
    callFunction("UI|setSysStatus", new Object[] { action.getValue("DEV_CODE") + ":" + action.getValue("DEV_CHN_DESC") + action.getValue("SPECIFICATION") });
    getTTable(TABLE2).acceptText();  
    int rowNum = getTTable(TABLE2).getSelectedRow(); 
    //�ж��Ƿ��������µ���Ŀ
    if ((this.rateofproCode.equals("C")) || (this.rateofproCode.equals("D")) || (this.rateofproCode.equals("E"))) {
      messageBox("�빺���빺����״̬���������");
      getTTable(TABLE2).getDataStore().setItem(rowNum, "DEV_CHN_DESC", "");
      getTTable(TABLE2).setDSValue(rowNum);
      return;
    }
    if (this.checkFlg) {
      messageBox("�빺���Ѿ���˲��������");
      getTTable(TABLE2).getDataStore().setItem(rowNum, "DEV_CHN_DESC", "");
      getTTable(TABLE2).setDSValue(rowNum);
      return;
    }
    //�ж��Ƿ����ظ���
    if (isRepeatItem(action.getValue("DEV_CODE"), rowNum)) {
      messageBox("����������ͬ�豸���������豸���޸�������");
      getTTable(TABLE2).getDataStore().setItem(rowNum, "DEV_CHN_DESC", "");
      getTTable(TABLE2).setDSValue(rowNum);
      return;
    }
    String[] columnArr = getTTable(TABLE2).getDataStore().getColumns(); 
    for (String temp : columnArr) {
//    	tempREQUEST_NO
//    	tempSEQMAN_FLG
//    	tempDEVPRO_CODE
//    	tempDEV_CODE
//    	tempDEV_CHN_DESC
//    	tempDESCRIPTION
//    	tempQTY
//    	tempSTORGE_QTY
//    	tempSETDEV_CODE
//    	tempUNIT_CODE
//    	tempUNIT_PRICE
//    	tempTOT_VALUE
//    	tempUPDATE_FLG
//    	tempOPT_USER
//    	tempOPT_DATE
//    	tempOPT_TERM
      if (action.getValue(temp).length() == 0)
        continue;
      if ("OPT_DATE".equals(temp))
        continue;
      if ("OPT_USER".equals(temp))
        continue;
      if ("OPT_TERM".equals(temp))
        continue;  
//      ����Y
//      ����A
//      ����211AA0001
//      ����̨ʽѪѹ�����豸
//      ����121
//      ����80.0    
      //System.out.println("����"+action.getData(temp));
      getTTable(TABLE2).getDataStore().setItem(rowNum, temp, action.getData(temp));
    } 
    getTTable(TABLE2).getDataStore().setActive(rowNum, true);
    insertRow(TABLE2);
    getTTable(TABLE2).setDSValue();
    getTTable(TABLE2).getTable().grabFocus();
    getTTable(TABLE2).setSelectedRow(rowNum);
    getTTable(TABLE2).setSelectedColumn(4);
  }
  
  
  /**
   * �Ƿ����ظ��� 
   * @return boolean
   */
  public boolean isRepeatItem(String devOrder, int selCount)
  {
    boolean falg = false;
    TDataStore dataStore = getTTable(TABLE2).getDataStore();
    int rowCount = dataStore.rowCount();
    for (int i = 0; i < rowCount; ++i) {
      if (!(dataStore.isActive(i)))
        continue;
      if (i == selCount)
        continue;
      if (devOrder.equals(dataStore.getItemString(i, "DEV_CODE")))
        falg = true;
    }
    return falg;
  }
  /**
   * ����
   * @return boolean
   */
  public boolean onSave()
  {
	//���ϸ��
    if (!(emptyTextCheck("REQUEST_DEPT,REQUEST_USER,RATEOFPRO_CODE,REQUEST_DATE,USE_DATE,TOT_AMT,FUNDSOU_CODE,PURTYPE_CODE,DEVUSE_CODE"))) {
      return false;
    }
    TParm checkParm = isCheckMItem(); 
    if (checkParm.getErrCode() < 0) {
      messageBox(checkParm.getErrText());
      return false; 
    }  
    //����
    if (getValueString("REQUEST_NO").length() == 0)
    {
      //�빺�� 
      String requestNo = SystemTool.getInstance().getNo("ALL", "DEV", 
        "REQUEST_NO", "REQUEST_NO"); 
      TParm requestMParm = getRequestM(requestNo); 
      //requestMParm.setData("TYPE","INSERT"); 
      TParm resultM = DevPurChaseMTool.getInstance().insertDevPurChaseM(requestMParm,"INSERT");
//      String[] sqlRequestM = { creatRequestMSQL(requestMParm, "INSERT") };
//      for (String temp : sqlRequestM) {
//        System.out.println("temp:" + temp); 
//      }     
          
      TParm requestDParm = getRequestD(requestNo); 
      //requestDParm.setData("TYPE","INSERT"); 
      TParm resultD = DevPurChaseDTool.getInstance().insertDevPurChaseD(requestDParm,"INSERT");
//      String[] sqlRequestD = {creatRequestDSQL(requestDParm, "INSERT")};
//      for(String temp:sqlRequestD){ 
//          System.out.println("temp:"+temp);
//      }   
      if (resultM.getErrCode() < 0||resultD.getErrCode() < 0) {
        messageBox("����ʧ�ܣ�");
        return false; 
      }
      messageBox("����ɹ���");
      setValue("REQUEST_NO", requestNo);
    }
    //����
    else {
      if ((this.rateofproCode.equals("B")) || (this.rateofproCode.equals("C")) || 
        (this.rateofproCode.equals("D")) || (this.rateofproCode.equals("E"))) {
        messageBox("�빺���빺����״̬�������޸ģ�");
        return false;
      } 
      getTTable(TABLE2).acceptText(); 
      String requestNo = this.getValueString("REQUEST_NO"); 
      TParm requestMParm = getRequestM(requestNo);  
      //requestMParm.setData("TYPE","UPDATE");  
      TParm resultM = DevPurChaseMTool.getInstance().insertDevPurChaseM(requestMParm,"UPDATE");
//      String[] sqlRequestM = { creatRequestMSQL(requestMParm, "UPDATE") };
//      for (String temp : sqlRequestM) {
//          System.out.println("temp:" + temp); 
//        }         
      TParm requestDParm = getRequestM(requestNo);  
      //requestDParm.setData("TYPE","UPDATE"); 
      TParm resultD = DevPurChaseDTool.getInstance().insertDevPurChaseD(requestDParm,"UPDATE");
//      String[] sqlRequestD = {creatRequestDSQL(requestDParm, "UPDATE")};
//      for (String temp : sqlRequestD) { 
//          System.out.println("temp:" + temp); 
//        }   
      if (resultM.getErrCode() < 0||resultD.getErrCode() < 0) {
        messageBox("����ʧ�ܣ�"); 
        return false;
      } 
      messageBox("����ɹ���");
    }
    onClear();
    onQuery();
    return true;
  }
  /**
   * �õ��빺��������
   * @return TParm
   */
  public TParm getRequestM(String requestNo)
  { 
    TParm result = new TParm();  
    //�빺����
    result.setData("REQUEST_NO", requestNo);
    //�빺����REQUEST_DATE=2013-06-27 00:00:00.0
	String requestDate = this.getValueString("REQUEST_DATE").substring(0, 10).replace(":", "")
	.replace("-", "").replace(" ", ""); 
    result.setData("REQUEST_DATE", requestDate);
    //�빺���� 
    result.setData("REQUEST_DEPT", getValue("REQUEST_DEPT"));
    //�빺��Ա
    result.setData("REQUEST_USER", getValue("REQUEST_USER"));
    //��������
    result.setData("RATEOFPRO_CODE", getValue("RATEOFPRO_CODE"));
    //Ԥ��������
	String useDate = this.getValueString("USE_DATE").substring(0, 10).replace(":", "")
	.replace("-", "").replace(" ", "");
    result.setData("USE_DATE", useDate); 
    //�ܼ� 
    result.setData("TOT_AMT", getValue("TOT_AMT"));
    //�ʽ���Դ
    result.setData("FUNDSOU_CODE", getValue("FUNDSOU_CODE"));
    //�빺���
    result.setData("PURTYPE_CODE", getValue("PURTYPE_CODE"));
    //�豸��;
    result.setData("DEVUSE_CODE", getValue("DEVUSE_CODE"));
    //��ϸ��;
    result.setData("DETAILED_USE", getValue("DETAILED_USE"));
    //�빺ԭ��
    result.setData("REQUEST_REASON", getValue("REQUEST_REASON"));
    //Ч����֤ 
    result.setData("BENEFIT_PROVE", getValue("BENEFIT_PROVE"));
    //��ע
    result.setData("REMARK", getValue("REMARK"));
    Timestamp timestamp = SystemTool.getInstance().getDate();
    String sysDate = StringTool.getString(timestamp,
    "yyyyMMdd");    
    if (getTCheckBox("CHECK_FLG").isSelected())
    {   
      result.setData("CHK_USER", Operator.getID());
      result.setData("CHK_DATE", sysDate); 
    } 
    else {  
      result.setData("CHK_USER", getValue("CHK_USER"));
      //û��CHK_DATE....
  	  String chkDate = this.getValueString("CHK_DATE").substring(0, 10).replace(":", "")
	  .replace("-", "").replace(" ", "");; 
      result.setData("CHK_DATE", chkDate); 
    }   
    //����Ա  
    result.setData("OPT_USER", Operator.getName());  
    //����ʱ��
    result.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
    //����IP  
    result.setData("OPT_TERM",  sysDate);  
    System.out.println("�õ��빺��������result"+result); 
    return result; 
  }
  /**
   * �õ��빺ϸ������   
   * @return TParm
   */ 
  public TParm getRequestD(String requestNo)
  { 
    TParm result = new TParm(); 
    TTable tableD = getTTable(TABLE2);
    //#ACTIVE#;DEVPRO_CODE;DEV_CHN_DESC;
    //SPECIFICATION;UNIT_PRICE;QTY;UNIT_CODE;XJ;DEV_CODE;REMARK
    for(int i=0;i<tableD.getRowCount()-1;i++){
    	System.out.println("i"+i);   
    //�빺����
    result.setData("REQUEST_NO", requestNo);
    //��� 
    result.setData("SEQ_NO", i+1);    
    //�豸���� 
    result.setData("DEV_CHN_DESC", tableD.getItemData(i, "DEV_CHN_DESC"));
    //���
    result.setData("SPECIFICATION", tableD.getItemData(i, "SPECIFICATION")); 
    //��λ
    result.setData("UNIT_CODE", tableD.getItemData(i, "UNIT_CODE")); 
    //�豸����
    result.setData("DEVPRO_CODE", tableD.getItemData(i, "DEVPRO_CODE")); 
    //�豸����
    result.setData("DEV_CODE", tableD.getItemData(i, "DEV_CODE"));   
    //���� 
    result.setData("UNIT_PRICE", tableD.getItemData(i, "UNIT_PRICE"));  
    //����
    result.setData("QTY", tableD.getItemData(i, "QTY")); 
    //���� 
    result.setData("SUM_QTY", "");  
    //��ע
    result.setData("REMARK", tableD.getItemData(i, "REMARK"));
    //����Ա
    result.setData("OPT_USER", Operator.getName());  
    //����ʱ��
    result.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
    //����IP  
    result.setData("OPT_TERM", Operator.getIP());
    }
    System.out.println("�õ��빺ϸ������result"+result);  
    return result;
  }
   
  /**
   * ���ϸ���
   * @return TParm
   */
  public TParm isCheckMItem()
  {
    TParm result = new TParm();
    TDataStore dateStore = getTTable(TABLE2).getDataStore();
    int rowCount = dateStore.rowCount();
    if (rowCount <= 0) {
      result.setErrCode(-1);
      result.setErrText("����д�豸��ϸ���ϣ�");
      return result;
    }
    for (int i = 0; i < rowCount; ++i) {
      if (!(dateStore.isActive(i))) 
        continue;
      if (dateStore.getItemDouble(i, "UNIT_PRICE") <= 0.0D) {
        result.setErrCode(-2);
        result.setErrText("�豸��Ϊ:" + dateStore.getItemString(i, "DEV_CHN_DESC") + "����Ŀ����д�ο��۸�");
        return result;
      }
      if (dateStore.getItemInt(i, "QTY") <= 0) {
        result.setErrCode(-3);
        result.setErrText("�豸��Ϊ:" + dateStore.getItemString(i, "DEV_CHN_DESC") + "����Ŀ����д������");
        return result;
      }
    }
    return result;
  }
  /**
   * �������
   */
  public int insertRow(String tag)
  {
    int rowNum = -1;
    boolean falg = true;
    TTable table = getTTable(tag);
    TDataStore tabDataStore = table.getDataStore();
    int rowCount = tabDataStore.rowCount();
    for (int i = 0; i < rowCount; ++i) {
      if (!(tabDataStore.isActive(i))) {
        falg = false;
        break;
      }
    }
    if (falg) {
      rowNum = tabDataStore.insertRow();
      tabDataStore.setActive(rowNum, false);
    }
    return rowNum;
  }
  /**
   * ����TABLE������
   * @param tag String
   * @param queryParm TParm
   * @return TDataStore
   */
  public TDataStore getTableTDataStore(String tag)
  {
    TDataStore dateStore = new TDataStore();
    if (tag.equals("TABLE1")) {
      String sql = "SELECT * FROM DEV_PURCHASEM";
      TParm queryParm = getTable1QueryParm();
      String[] columnName = queryParm.getNames();
      if (columnName.length > 0)
        sql = sql + " WHERE ";
      int count = 0;
      for (String temp : columnName) {
        if (temp.equals("QEND_DATE"))
          continue;
        if (temp.equals("YEND_DATE")) {
          continue;
        }
        if (temp.equals("QSTART_DATE")) {
          if (count > 0)
            sql = sql + " AND ";
          sql = sql + " REQUEST_DATE BETWEEN TO_DATE('" + queryParm.getValue("QSTART_DATE") + "','YYYYMMDD') AND TO_DATE('" + queryParm.getValue("QEND_DATE") + "','YYYYMMDD')";
          ++count;
        }
        else if (temp.equals("YSTART_DATE")) {
          if (count > 0)
            sql = sql + " AND ";
          sql = sql + " USE_DATE BETWEEN TO_DATE('" + queryParm.getValue("YSTART_DATE") + "','YYYYMMDD') AND TO_DATE('" + queryParm.getValue("YEND_DATE") + "','YYYYMMDD')";
          ++count;
        }
        else {
          if (count > 0)
            sql = sql + " AND ";
          sql = sql + temp + "='" + queryParm.getValue(temp) + "' ";
          ++count; }
      }
      System.out.println("sql:" + sql);
      dateStore.setSQL(sql);
      dateStore.retrieve();
    }
    if (tag.equals("TABLE2")) {
      String qrequestNo = getValueString("QREQUEST_NO");
      DevBaseDataStore devBaseDataStore = new DevBaseDataStore();
      devBaseDataStore.setRequestNo(qrequestNo);
      devBaseDataStore.onQuery();

      double totAmt = getTotAmt(devBaseDataStore);
      setValue("TOT_AMT", Double.valueOf(totAmt));
      return devBaseDataStore;
    }
    return dateStore;
  }
  /**
   * �õ���ϸ������
   * @param requestNo String
   * @return DevBaseDataStore
   */
  public DevBaseDataStore getRequestDData(String requestNo)
  {
    String qrequestNo = requestNo;
    DevBaseDataStore devBaseDataStore = new DevBaseDataStore();
    devBaseDataStore.setRequestNo(qrequestNo);
    devBaseDataStore.onQuery();

    double totAmt = getTotAmt(devBaseDataStore);
    setValue("TOT_AMT", Double.valueOf(totAmt));
    return devBaseDataStore;
  }
  /**
   * �����ܼ۸�
   * @param devBaseDataStore TDataStore
   * @return double
   */
  public double getTotAmt(TDataStore devBaseDataStore)
  {
    int rowCount = devBaseDataStore.rowCount();
    double totAmt = 0.0D;
    for (int i = 0; i < rowCount; ++i) {
      if (!(devBaseDataStore.isActive(i)))
        continue;
      totAmt += devBaseDataStore.getItemDouble(i, "UNIT_PRICE") * devBaseDataStore.getItemDouble(i, "QTY");
    }
    return totAmt;
  }
  /**
   * �õ�TABLE1�Ĳ�ѯ����
   * @return TParm
   */
  public TParm getTable1QueryParm()
  {
    TParm result = new TParm();
    String startDate = StringTool.getString((Timestamp)getValue("QSTART_DATE"), "yyyyMMdd");
    String endDate = StringTool.getString((Timestamp)getValue("QEND_DATE"), "yyyyMMdd");
    String deptCode = getValueString("QDEPT_CODE");
    String operator = getValueString("QOPERATOR");
    String reteoptro = getValueString("QRATEOFPRO_CODE");
    String requestNo = getValueString("QREQUEST_NO");
    String ydDateStart = StringTool.getString((Timestamp)getValue("QUSERSTART_DATE"), "yyyyMMdd");
    String ydDateEnd = StringTool.getString((Timestamp)getValue("QUSEEND_DATE"), "yyyyMMdd");
    String fundsouCode = getValueString("QFUNDSOU_CODE");
    String purtypeCode = getValueString("QPURTYPE_CODE");
    String devUseCode = getValueString("QDEVUSE_CODE");
    if (startDate.length() != 0)
      result.setData("QSTART_DATE", startDate);
    if (endDate.length() != 0)
      result.setData("QEND_DATE", endDate);
    if (ydDateStart.length() != 0)
      result.setData("YSTART_DATE", ydDateStart);
    if (ydDateEnd.length() != 0)
      result.setData("YEND_DATE", ydDateEnd);
    if (deptCode.length() != 0)
      result.setData("REQUEST_DEPT", deptCode);
    if (operator.length() != 0)
      result.setData("REQUEST_USER", operator);
    if (reteoptro.length() != 0)
      result.setData("RATEOFPRO_CODE", reteoptro);
    if (requestNo.length() != 0)
      result.setData("REQUEST_NO", requestNo);
    if (fundsouCode.length() != 0)
      result.setData("FUNDSOU_CODE", fundsouCode);
    if (purtypeCode.length() != 0)
      result.setData("PURTYPE_CODE", purtypeCode);
    if (devUseCode.length() != 0)
      result.setData("DEVUSE_CODE", devUseCode);
    return result;
  }
  /**
   * ���   
   */
  public void onClear()
  { 
    //�ж��Ƿ񱣴�
    //���
    clearValue("QREQUEST_NO;QFUNDSOU_CODE;QPURTYPE_CODE;QDEVUSE_CODE;REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE;DETAILED_USE;REQUEST_REASON;BENEFIT_PROVE;REMARK");
    /**
     * ��ʼ��ҳ��
     */
    onInitPage();
    /**
     * ��ʼ��Ȩ��
     */
    onInitPopeDem();
    this.checkFlg = false;
  }
  /**
   * ��ѯ
   */
  public void onQuery()
  {
	 
    clearValue("REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE;DETAILED_USE;REQUEST_REASON;BENEFIT_PROVE;REMARK");
    //��ʼ��TABLE1
    getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
    getTTable(TABLE1).setDSValue();
    //��ʼ��TABLE2
    getTTable(TABLE2).setDataStore(getRequestDData(""));
    getTTable(TABLE2).setDSValue();
    //���һ��
    insertRow(TABLE2);
    getTTable(TABLE2).setDSValue();
  }
  /**
   * �ر��¼�
   * @return boolean
   */
  public boolean onClosing()
  {
	//�ж��Ƿ񱣴�
    return true;
  }
  /**
   * 1��һ��Ȩ�ޣ�������Ա��:�������ҿ�����Ա,�빺����Ĭ��Ϊ����������������׼ GeneralPermissions
   * 2���ϲ��빺�����豸��Ա�������ҿ�����Ա������׼ ClerksPermissions
   * 3����ˣ��豸�Ƴ��� AuditPermissions
   */
  public void onInitPopeDem()
  {
	//���
    if (getPopedem("AuditPermissions")) {
      getTTextFormat("QDEPT_CODE").setEnabled(true);
      getTTextFormat("QOPERATOR").setEnabled(true);
      getTTextFormat("REQUEST_DEPT").setEnabled(true);
      getTTextFormat("REQUEST_USER").setEnabled(true);
      getTTextFormat("REQUEST_DATE").setEnabled(true);
      getTTextFormat("USE_DATE").setEnabled(true);
      getTNumberTextField("TOT_AMT").setEnabled(true);
      getTComboBox("FUNDSOU_CODE").setEnabled(true);
      getTComboBox("PURTYPE_CODE").setEnabled(true);
      getTComboBox("DEVUSE_CODE").setEnabled(true);
      getTCheckBox("CHECK_FLG").setEnabled(true);
      return;
    } 
    //һ��Ȩ��
    if (getPopedem("GeneralPermissions")) {
      getTTextFormat("QDEPT_CODE").setEnabled(false);
      getTTextFormat("QOPERATOR").setEnabled(false);
      getTTextFormat("REQUEST_DEPT").setEnabled(false);
      getTTextFormat("REQUEST_USER").setEnabled(false);
      getTTextFormat("REQUEST_DATE").setEnabled(true);
      getTTextFormat("USE_DATE").setEnabled(true);
      getTNumberTextField("TOT_AMT").setEnabled(true);
      getTComboBox("FUNDSOU_CODE").setEnabled(true);
      getTComboBox("PURTYPE_CODE").setEnabled(true);
      getTComboBox("DEVUSE_CODE").setEnabled(true);
      getTCheckBox("CHECK_FLG").setEnabled(true);
    }

    if (getPopedem("ClerksPermissions")) {
      getTTextFormat("REQUEST_DEPT").setEnabled(false);
      getTTextFormat("REQUEST_USER").setEnabled(false);
      getTTextFormat("REQUEST_DATE").setEnabled(true);
      getTTextFormat("USE_DATE").setEnabled(true);
      getTNumberTextField("TOT_AMT").setEnabled(true);
      getTComboBox("FUNDSOU_CODE").setEnabled(true);
      getTComboBox("PURTYPE_CODE").setEnabled(true);
      getTComboBox("DEVUSE_CODE").setEnabled(true);
      getTCheckBox("CHECK_FLG").setEnabled(false);
    }
  }
  /**
   * �õ�TTextFormat
   * @return TTextFormat
   */
  public TTextFormat getTTextFormat(String tag)
  {
    return ((TTextFormat)getComponent(tag));
  }
  /**
   * �õ�getTComboBox
   * @return getTComboBox
   */
  public TComboBox getTComboBox(String tag)
  {
    return ((TComboBox)getComponent(tag));
  }
  /**
   * �õ�getTCheckBox
   * @return getTCheckBox
   */
  public TCheckBox getTCheckBox(String tag)
  {
    return ((TCheckBox)getComponent(tag));
  }
  /**
   * �õ�getTTable
   * @return getTTable
   */
  public TTable getTTable(String tag)
  {
    return ((TTable)getComponent(tag));
  }
  /**
   * ɾ��
   */
  public void onDelete()
  {
    int row = getTTable(TABLE1).getSelectedRow();
    if (row < 0) {
      messageBox("��ѡ��Ҫɾ�������ݣ�");
      return;
    }
    if ((this.rateofproCode.equals("B")) || (this.rateofproCode.equals("C")) || (this.rateofproCode.equals("D")) || (this.rateofproCode.equals("E"))) {
      messageBox("�빺���빺����״̬������ɾ����");
      return;
    }
    if (this.checkFlg) {
      messageBox("�빺�����Ѿ���˲�����ɾ����");
      return;
    }
    if (messageBox("��ʾ��Ϣ", "�Ƿ�ɾ��?", 0) != 0)
      return;
    getTTable(TABLE1).getDataStore().deleteRow(row);
    int rowCount = getTTable(TABLE2).getDataStore().rowCount();
    for (int i = rowCount - 1; i >= 0; --i) {
      if ((!(getTTable(TABLE2).getDataStore().isActive(i))) && (getTTable(TABLE2).getDataStore().getItemString(i, "REQUEST_NO").length() == 0)) {
        continue;
      }
      getTTable(TABLE2).getDataStore().deleteRow(i);
    }
    getTTable(TABLE1).setDSValue();
    getTTable(TABLE2).setDSValue();
    String[] arraySqlTable1 = getTTable(TABLE1).getUpdateSQL();
    String[] arraySqlTable2 = getTTable(TABLE2).getUpdateSQL();
    StringUtil.getInstance(); String[] arraySql = StringUtil.copyArray(arraySqlTable1, arraySqlTable2);
    TParm sqlParm = new TParm();
    sqlParm.setData("SQL", arraySql);
    TParm actionParm = TIOM_AppServer.executeAction(this.actionName, "saveDevRequest", sqlParm);
    if (actionParm.getErrCode() < 0) {
      messageBox("ɾ��ʧ�ܣ�");
      return;
    }
    messageBox("ɾ���ɹ���");
    onClear();
  }
  /**
   * ��ӡ
   */
  public void onPrint()
  {
    int row = getTTable(TABLE1).getSelectedRow();
    if (row < 0) {
      messageBox("��ѡ��Ҫ��ӡ�����ݣ�");
      return;
    }  
    TParm parm = getRequestM(getValueString("REQUEST_NO"));
    parm.setData("TITLE_NAME", "TEXT", Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
    parm.setData("FORMS_NAME", "TEXT", "�豸�빺��");
    TParm printDataParm = new TParm();
    TParm tableParm = getTTable(TABLE2).getDataStore().getBuffer("Primary!");
    System.out.println("tableParm" + tableParm);
    int rowCount = tableParm.getCount();
    for (int i = 0; i < rowCount; ++i) {
      if (!(tableParm.getBoolean("#ACTIVE#", i)))
        continue;
      printDataParm.addRowData(tableParm, i, "DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;UNIT_PRICE;QTY;REMARK");
    }
    printDataParm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
    printDataParm.addData("SYSTEM", "COLUMNS", "DEV_CODE");
    printDataParm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
    printDataParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
    printDataParm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
    printDataParm.addData("SYSTEM", "COLUMNS", "QTY");
    printDataParm.addData("SYSTEM", "COLUMNS", "REMARK");
    printDataParm.setCount(printDataParm.getCount("DEV_CODE"));
    parm.setData("DEV_PURCHASED", printDataParm.getData());
    System.out.println("parm" + parm);
    openPrintDialog("%ROOT%\\config\\prt\\DEV\\DevRequestForms.jhw", parm, false);
  } 
  
  //����ʵ��
  public void spilt() {   
	    String name = "�����ץ�τ�������ʹ�ä��Ƥ���ޤ������ä����܇���Фʤɤ�ʹ���Ȑu�����������Ϥ�����ޤ��Τǡ��ܥ��`��䤴���íh���ˤ�ʮ�֤�ע�⤯������";   
	    int length = name.length();   
	    int part = (length + 2 ) / 3;   
	  
	    int from = 0;   
	    int to = 0;   
	    StringBuilder builder = new StringBuilder();   
	    for (int i = 0; i < 3; i++) {   
	        from = to;   
	        to = from + part;   
	        to = to > length ? length : to;   
	        builder.append(name.subSequence(from, to-1)).append("\n");   
	    }   
	       
	    // ���Ǵ�����Ľ��   
	    String result = builder.toString();   
	       
	    System.out.println(result);   
	}  

  
  
  
  
  
  
  
}