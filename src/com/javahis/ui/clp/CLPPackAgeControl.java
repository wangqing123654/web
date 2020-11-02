package com.javahis.ui.clp;

import jdo.clp.CLPPackAgeTool;
import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: �ٴ�·���ײ��ֵ� </p>
 *
 * <p>Description: �ٴ�·���ײ��ֵ�</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben 2015-8-10
 * @version 1.0
 */
public class CLPPackAgeControl extends TControl {
	TTable table;
	/**
     * ҳ���ʼ������
     */
    public void onInit() {
        super.onInit();
        initPage();
        onClear();
        onQuery();
    }
    /**
     * 
    * @Title: initPage
    * @Description: TODO(Ĭ������)
    * @author pangben
    * @throws
     */
    public void initPage(){
    	table=(TTable)this.getComponent("TABLE");
    	
    }
    /**
     * 
    * @Title: onClear
    * @Description: TODO(���)
    * @author pangben
    * @throws
     */
    public void onClear(){
    	this.setValue("PACKAGE_CODE", "");
    	this.setValue("PACKAGE_DESC", "");
    	this.setValue("PY", "");
    	//String sql="SELECT MAX(SEQ_NO) SEQ_NO FROM CLP_PACKAGE ";
    	TParm result = CLPPackAgeTool.getInstance().getMaxSeqNo(new TParm());
    	if (result.getCount()>0&&result.getValue("SEQ_NO",0).length()>0) {
    		this.setValue("SEQ_NO", result.getInt("SEQ_NO",0)+1);
		}else{
			this.setValue("SEQ_NO", 0);
		}
    	TCheckBox chk=(TCheckBox)this.getComponent("CLP_PACK_FLG");
    	chk.setSelected(false);
    }
    /**
     * 
    * @Title: onQuery
    * @Description: TODO(��ѯ)
    * @author pangben
    * @throws
     */
    public void onQuery(){
    	String packageCode=this.getConfigString("PACKAGE_CODE");
    	TParm parm=new TParm();
    	if (packageCode.length()>0) {
    		parm.setData("PACKAGE_CODE",packageCode);
		}
    	TParm result=CLPPackAgeTool.getInstance().queryPackAge(parm);
    	if (result.getErrCode()<0) {
			this.messageBox("��ѯ����");
			return;
		}
    	if (result.getCount()<=0) {
			this.messageBox("û����Ҫ��ѯ������");
			table.setParmValue(new TParm());
			return;
		}else{
			table.setParmValue(result);
		}
    }
    /**
     * 
    * @Title: onSave
    * @Description: TODO(����)
    * @author pangben
    * @throws
     */
    public void onSave(){
    	TParm parm=new TParm();
    	if (this.getValueString("PACKAGE_CODE").trim().length()<=0) {
			this.messageBox("�ײʹ��벻����Ϊ��");
			return;
		}
    	if (this.getValueString("PACKAGE_DESC").trim().length()<=0) {
			this.messageBox("�ײ����Ʋ�����Ϊ��");
			return;
		}
    	parm.setData("PACKAGE_CODE",this.getValueString("PACKAGE_CODE").trim());
    	parm.setData("PACKAGE_DESC",this.getValueString("PACKAGE_DESC").trim());
    	parm.setData("PY",this.getValueString("PY").trim());
    	parm.setData("CLP_PACK_FLG",this.getValueString("CLP_PACK_FLG"));
    	parm.setData("PACK_NOTE",this.getValueString("PACK_NOTE"));
    	parm.setData("SEQ_NO",this.getValueInt("SEQ_NO"));
    	parm.setData("OPT_TERM",Operator.getIP());
    	parm.setData("OPT_USER",Operator.getID());
    	//this.setValueForParm("PACKAGE_CODE;PACKAGE_DESC;PY;CLP_PACK_FLG;SEQ_NO;PACK_NOTE", parm);
    	//String packageCode=this.getConfigString("PACKAGE_CODE");
    	TParm result=CLPPackAgeTool.getInstance().queryPackAge(parm);
    	if (result.getErrCode()<0) {
			this.messageBox("��ѯ��������");
			return;
		}
    	TParm onExeParm=new TParm();
    	if (result.getCount()>0) {
    		onExeParm=CLPPackAgeTool.getInstance().onUpdate(parm);
		}else{
			onExeParm=CLPPackAgeTool.getInstance().onInsert(parm);
		}
    	if (onExeParm.getErrCode()<0) {
    		System.out.println("����ʧ��:"+onExeParm);
			this.messageBox("����ʧ��");
			return;
		}
    	if (result.getCount()>0) {
			this.messageBox("�޸ĳɹ�");
		}else{
			this.messageBox("��ӳɹ�");
		}
    	onClear();
    	onQuery();
    }
    /**
     * 
    * @Title: onPy
    * @Description: TODO(ƴ��)
    * @author pangben
    * @throws
     */
    public void onPy(){
    	this.setValue("PY", SYSHzpyTool.getInstance().charToCode(
			TypeTool.getString(getValue("PACKAGE_DESC"))));// ��ƴ
    }
    /**
     * 
    * @Title: onTableClicked
    * @Description: TODO(��������¼�)
    * @author pangben
    * @throws
     */
    public void onTableClicked(){
    	int row=table.getSelectedRow();
    	if (row<0) {
			return;
		}
    	TParm tableParm=table.getParmValue();
    	this.setValueForParm("PACKAGE_CODE;PACKAGE_DESC;PY;CLP_PACK_FLG;SEQ_NO;PACK_NOTE",
    			tableParm, row);
    }
    /**
     * 
    * @Title: onDelete
    * @Description: TODO(ɾ������)
    * @author pangben
    * @throws
     */
    public void onDelete(){
    	TParm parm=new TParm();
    	if (this.getValueString("PACKAGE_CODE").trim().length()<=0) {
			this.messageBox("�ײʹ��벻����Ϊ��");
			return;
		}
    	String sql="SELECT ORDER_CODE FROM CLP_PACK WHERE PACK_CODE='"+this.getValueString("PACKAGE_CODE").trim()+"'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getCount()>0) {
			this.messageBox("���ײʹ�����ʹ�ã����޸�·��ҽ��");
			return;
		}
    	parm.setData("PACKAGE_CODE",this.getValueString("PACKAGE_CODE").trim());
    	result=CLPPackAgeTool.getInstance().onDelete(parm);
    	if (result.getErrCode()<0) {
			this.messageBox("ɾ����������");
			return;
		}
    	this.messageBox("P0003");
    	onClear();
    	onQuery();
    }
}
