package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import jdo.sys.SYSOrderSetDetailTool;

import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TTableEvent;
import jdo.ins.INSRuleTXTool;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TNull;

/**
 * <p>Title: ҽ������Ŀ�ֵ�:֧����׼��</p>
 *
 * <p>Description:ҽ������Ŀ�ֵ�:֧����׼�� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author pangben 2011-11-08
 * @version 1.0
 */
public class INSRuleTXControl extends TControl {
    private static String TABLE = "TABLE";
    private int rowOnly = -1;
    private TParm parmReturn ;//��Ŀ��������
    private TParm parmDetail;//����ҽ��ϸ��
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
       callFunction("UI|" + TABLE + "|addEventListener",
                    TABLE + "->" + TTableEvent.CLICKED, this, "onTableClicked");
       //���ó����ص�����
        callFunction("UI|SFXMBM|setPopupMenuParameter", "aaa",
                     "%ROOT%\\config\\sys\\SYSFeePopupToINS.x");
        //textfield���ܻش�ֵ
        callFunction("UI|SFXMBM|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        this.setValue("REGION_CODE",Operator.getRegion());

       onClear();
    }
    /**
   *���Ӷ�Table�ļ���
   * @param row int
   */
  public void onTableClicked(int row) {
      if(row < 0)
            return;
        callFunction("UI|REGION_CODE|setEnabled", false);//����
        //callFunction("UI|XMBM|setEnabled", false);//������Ŀ����
        callFunction("UI|CTZ_CODE|setEnabled", false);
        callFunction("UI|delete|setEnabled",true);
        TParm parm = (TParm) callFunction("UI|" + TABLE + "|getParmValue");
        setValueForParm(
            "SFXMBM;XMBM;XMMC;XMLB;BZJG;SJJG;XMRJ;TXBZ;BZ",
            parm, row);
        this.setValue("REGION_CODE",parm.getValue("SFDLBM",row));
        //TParm parm = this.getSelectRowData(TABLE);
        rowOnly = row;
  }
  /**
  * �õ�ѡ��������
  * @param tableTag String
  * @return TParm
  */
 public TParm getSelectRowData(String tableTag){
     int selectRow = (Integer) callFunction("UI|" + tableTag +"|getSelectedRow");
     out("�к�" + selectRow);
     TParm parm = (TParm) callFunction("UI|" + tableTag + "|getParmValue");
     out("GRID����" + parm);
     TParm parmRow = parm.getRow(selectRow);
     parmRow.setData("OPT_USER",Operator.getID());
     parmRow.setData("OPT_TERM",Operator.getIP());
     return parmRow;
 }
  /**
   * ��ѯ
   */
  public void onQuery() {
//      out("��ѯbegin");
//      callFunction("UI|" + TABLE + "|onQuery");
//      int row = (Integer) callFunction("UI|" + TABLE + "|getRowCount");
//      if(row < 0)
//          this.onClear();
//      out("��ѯend");
      TParm parm=new TParm();
      if(this.getValue("REGION_CODE").toString().length()>0){
          parm.setData("SFDLBM",this.getValue("REGION_CODE").toString());
      }
      if (this.getValue("SFXMBM").toString().length() > 0) {
          //������Ŀ����
          parm.setData("SFXMBM", this.getValue("SFXMBM").toString());
      }
      if(this.getValue("XMBM").toString().length() > 0){
          //������Ŀ����
          parm.setData("XMBM", this.getValue("XMBM").toString());
      }
      TParm data=INSRuleTXTool.getInstance().selectINSRule(parm);
      if(data.getCount()<=0){
          this.messageBox("û����Ҫ��ѯ������");
          return;
      }
      ((TTable) getComponent(TABLE)).setParmValue(data);
  }
  /**
   * ����
   */
  public void onSave() {
      TParm parm = new TParm();
      parm.setData("OPT_USER", Operator.getID());
      parm.setData("OPT_TERM", Operator.getIP());
      TParm result=null;
      if (rowOnly >=0) {
          if (!emptyTextCheck("REGION_CODE,SFXMBM,XMBM,BZJG,SJJG,XMLB"))
              return;
          result=INSRuleTXTool.getInstance().updateINSRule(getTemp(parm));
          if (result.getErrCode()<0) {
              messageBox_("����ʧ��");
              return;
          }
          messageBox_("����ɹ�");

      } else {
          if (!emptyTextCheck("REGION_CODE,SFXMBM,XMBM,BZJG,SJJG,XMLB"))
              return;
          if(parmDetail.getCount("ORDER_CODE")>0){
        	  addParmDetail(parmDetail.getCount());
          }else{
        	  parmDetail=new TParm();
        	  addParmDetail(0);
          }
          result = TIOM_AppServer.executeAction("action.ins.INSRuleTXAction",
					"insertINSRule", parmDetail);
          //result=INSRuleTXTool.getInstance().insertINSRule(getTemp(parm));
          if (result.getErrCode()<0) {
              messageBox_("����ʧ��");
              return;
          }
          messageBox_("�����ɹ�");

      }
      this.onClear();
      onQuery();
      out("����end");
  }
  /**
   * ���һ��ҽ��
   * @param i
   */
  private void addParmDetail(int i){
	  parmDetail.setData("SFXMBM",i,this.getValue("SFXMBM"));// ��������
	  parmDetail.setData("ORDER_CODE", parmReturn.getValue("ORDER_CODE", i));// ��Ŀ����
	  parmDetail.setData("ORDER_DESC",i,parmReturn.getValue("ORDER_DESC"));// ��������
	  parmDetail.setData("PY1",i,parmReturn.getValue("PY1"));// ƴ��
	  parmDetail.setData("OWN_PRICE",i,parmReturn.getDouble("OWN_PRICE"));// ʵ�ս��
	  parmDetail.setData("DOSE_DESC",i,parmReturn.getValue("DOSE_DESC"));// ����
	  parmDetail.setData("SPECIFICATION",i,parmReturn.getValue("SPECIFICATION"));// ���
	  parmDetail.setData("MEDI_UNIT_DESC",i,parmReturn.getValue("MEDI_UNIT_DESC"));// ��λ:��ҩ��λ
	  parmDetail.setData("ROUTE_DESC",i,parmReturn.getValue("ROUTE_DESC"));// �÷�
	  parmDetail.setData("BZ", this.getValue("BZ"));
  }
  /**
   * �޸Ĳ���
   * @param parm TParm
   * @return TParm
   */
  private TParm getTemp(TParm parm){
      parm.setData("SFDLBM",Operator.getRegion());//����
      parm.setData("SFXMBM",this.getValue("SFXMBM"));//��Ŀ����
      parm.setData("XMBM",this.getValue("XMBM"));//�������
      parm.setData("XMMC",this.getValue("XMMC"));//��������
      parm.setData("XMRJ",this.getValue("XMRJ"));//ƴ��
      parm.setData("BZJG",this.getValueDouble("BZJG"));//��׼���
      parm.setData("SJJG",this.getValueDouble("SJJG"));//ʵ�ս��
      parm.setData("XMLB",this.getValue("XMLB"));//��Ŀ���
      if(null!=parmReturn && null!=parmReturn.getValue("ORDER_CODE")){
        parm.setData("JX",updateNull(parmReturn.getValue("DOSE_DESC")));  //����
        parm.setData("GG",updateNull(parmReturn.getValue("SPECIFICATION")));  //���
        parm.setData("DW",updateNull(parmReturn.getValue("MEDI_UNIT_DESC")));  //��λ:��ҩ��λ
        parm.setData("YF",updateNull(parmReturn.getValue("ROUTE_DESC")));  //�÷�
       // result.setData("YL",updateNull(parmReturn.getValue("DOSE_CODE")));  //����
        parm.setData("SL",1);  //����:��ҩ����
       // result.setData("PZWH",updateNull(parmReturn.getValue("DOSE_CODE")));  //��׼�ĺ�
      }else{
          TParm tableParm = ((TTable) getComponent(TABLE)).getParmValue().
                            getRow(rowOnly);
          parm.setData("JX", updateNull(tableParm.getValue("JX"))); //����
          parm.setData("GG", updateNull(tableParm.getValue("GG"))); //���
          parm.setData("DW", updateNull(tableParm.getValue("DW"))); //��λ
          parm.setData("YF", updateNull(tableParm.getValue("YF"))); //�÷�
          //  result.setData("YL", updateNull(parm.getValue("YL"))); //����
          parm.setData("SL", updateNull(tableParm.getValue("SL"))); //����
          // result.setData("PZWH", updateNull(parm.getValue("PZWH"))); //��׼�ĺ�
      }
      parm.setData("BZ",this.getValue("BZ"));
      return parm;
  }
  private Object updateNull(String name){
      if(null==name){
          return "";
      }else
          return name;
  }
  /**
   * ɾ��
   */
  public void onDelete() {
      if (messageBox("��ʾ��Ϣ", "�Ƿ�ɾ��?", this.YES_NO_OPTION) != 0)
          return;
      int row = (Integer) callFunction("UI|" + TABLE + "|getSelectedRow");
      if (row < 0)
          return;
      TParm parm= ((TTable) getComponent(TABLE)).getParmValue().getRow(row);
      TParm result=INSRuleTXTool.getInstance().deleteINSRule(parm);
      if (result.getErrCode()<0) {
          messageBox_("ɾ��ʧ��");
          return;
      }
      messageBox_("ɾ���ɹ�");
      this.onClear();
      onQuery();
//      int rows = (Integer) callFunction("UI|" + TABLE + "|getSelectedRow");
//      if (rows < 0) {
//          this.onClear();
//      }
//      out("ɾ��end");
  }

  /**
   *���
   */
  public void onClear() {
      clearValue("REGION_CODE;SFXMBM;XMBM;XMMC;XMLB;BZJG;SJJG;XMRJ;TXBZ;BZ");
      callFunction("UI|" + TABLE + "|clearSelection");
       ((TTable) getComponent(TABLE)).removeRowAll();
      callFunction("UI|save|setEnabled", true);
      callFunction("UI|delete|setEnabled", false);
      this.setValue("REGION_CODE",Operator.getRegion());
      parmReturn=null;
//      callFunction("UI|NHI_COMPANY|setEnabled",true);
//      callFunction("UI|FEE_TYPE|setEnabled",true);
//      callFunction("UI|CTZ_CODE|setEnabled",true);
      rowOnly = -1;
  }
  /**
   * �½�������������
   */
  public void onNew() {
    if(this.getValue("XMBM").toString().length()<=0 && !((TTextField)this.getComponent("XMBM")).isEnabled()){
        this.setValue("XMBM", SystemTool.getInstance().getNo("ALL", "INS",
                "INSRULE_NO", "INSRULE_NO"));
    }
    callFunction("UI|XMBM|setEnabled", false);
  }
  /**
     *
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
         parmReturn= (TParm) obj;
//         parm.getValue("HOMEPLACE_DESC");
        //System.out.println("parm::::"+parmReturn);
        //��ѯ����ҽ����Ӽ���ҽ��ϸ��
      // ��ѯϸ�����ݣ����ϸ��
 		parmDetail = SYSOrderSetDetailTool.getInstance()
 				.selectByOrderSetCode(parmReturn.getValue("ORDER_CODE"));
 		if (parmDetail.getErrCode() != 0) {
 			this.messageBox("E0050");
 			return;
 		}
 		//���ڼ���ҽ��ϸ��
 		double sum=parmReturn.getDouble("OWN_PRICE");
 		if (parmDetail.getCount("ORDER_CODE")>0) {
			for (int i = 0; i < parmDetail.getCount("ORDER_CODE"); i++) {
				sum+=parmDetail.getDouble("OWN_PRICE",i);
			}
		}
        this.setValue("SFXMBM", parmReturn.getValue("ORDER_CODE"));
        this.setValue("XMMC", parmReturn.getValue("ORDER_DESC"));
        this.setValue("XMRJ", parmReturn.getValue("PY1"));
        this.setValue("BZJG", sum);
        this.setValue("SJJG", sum);
        this.setValue("BZ", parmReturn.getValue("DESCRIPTION"));

//         this.setValue("DIAG_DESC",parm.getValue("ICD_CHN_DESC"));
//         this.grabFocus("DIAG_REMARK");
    }

}
