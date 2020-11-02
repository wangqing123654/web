package com.javahis.ui.ind;

import java.sql.Timestamp;

import com.dongyang.control.TControl;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPrintListCombo;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import com.javahis.system.combo.TComboINDMaterialloc;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.ind.IndStockDTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;
import jdo.util.Manager;
import jdo.sys.SystemTool;
import com.javahis.util.ExportExcelUtil;

/**    
 * <p>
 * Title: ���ſ���ѯ
 * </p>
 *
 * <p>
 * Description: ���ſ���ѯ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.09.22
 * @version 1.0
 */
public class INDOrgStockQueryControl
    extends TControl {
    public INDOrgStockQueryControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
    	setValue("ACTIVE_FLG", "Y");
    	setValue("STOP_FLG", "N");    
//    	if ("Y".equalsIgnoreCase(this.getValueString("ACTIVE_FLG"))){
//    		callFunction("UI|STOP_FLG|setEnabled", false);
//    	}  
//    	else{
//    		callFunction("UI|ACTIVE_FLG|setEnabled", false);
//    	}
        // ��ʾȫԺҩ�ⲿ��   
        if (!this.getPopedem("deptAll")) {
            //((TextFormatINDOrg)this.getComponent("ORG_CODE")).o
            //getTextFormat("ORG_CODE")
//            if (parm.getCount("NAME") > 0) {
//                getComboBox("ORG_CODE").setSelectedIndex(1);
//            }
        }

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        String role = Operator.getRole();
        System.out.println("--ROLE:"+role);
        if(null != role && "YKJZZR".equals(role)){//�����ҩ�������οɼ�
    		// ��ʼ������״̬
        	this.callFunction("UI|save|setEnabled", true);
        }else {
        	// ��ʼ������״̬
    		  this.callFunction("UI|save|setEnabled", false);
		}
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
    	//�������ú�ͣ������
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("��ѯ���Ų���Ϊ��");
            return;
        }
        this.getTable("TABLE").removeRowAll();
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("ORG_CODE"))) {
            parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        }
        if (!"".equals(this.getValueString("ORDER_CODE"))) {
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        }
        if (!"".equals(this.getValueString("BATCH_NO"))) {
            parm.setData("BATCH_NO", this.getValueString("BATCH_NO"));
        }
        if (!"".equals(this.getValueString("MAN_CODE"))) {
            parm.setData("MATERIAL_LOC_CODE", this.getValueString("MAN_CODE"));
        }
        if (!"".equals(this.getValueString("TYPE_CODE"))) {
            parm.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
        }
        if ("Y".equals(this.getValueString("SAFE_QTY"))) {
            parm.setData("SAFE_QTY", "SAFE_QTY");
        }
        if ("Y".equals(this.getValueString("CHECK_NOT_ZERO"))) {
            parm.setData("STOCK_QTY", "CHECK_NOT_ZERO");
        }

        if ("Y".equals(this.getValueString("ORDER_TYPE_W")) &&
            "Y".equals(this.getValueString("ORDER_TYPE_C")) &&
            "Y".equals(this.getValueString("ORDER_TYPE_G"))) {
            parm.setData("PHA_TYPE", "('W', 'C', 'G')");
        }
        if ("Y".equals(this.getValueString("ACTIVE_FLG"))){
        	parm.setData("ACTIVE_FLG",'Y');
        }   
        if  ("Y".equals(this.getValueString("STOP_FLG"))){
        	parm.setData("ACTIVE_FLG",'N');
        }   
        String mjFlg = this.getValueString("MJ_FLG");
        parm.setData("MJ_FLG",mjFlg);
        TParm result = new TParm();
        if (!"Y".equals(this.getValueString("SHOW_BATCH"))) {
          /*  result = IndStockDTool.getInstance().onQueryOrgStockQueryNotBatch(
                parm);*/
        	//�޸� ��ѯ ������Ϳ���� by liyh 20120808����ʾ����Ч��
        	//�޸Ľ���ѯ�龫ҩƷ���� by fuwj 20130422
        	if("Y".equals(mjFlg)) {
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQLNew(parm)));
        	}else {
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQL(parm)));
        	}											
        }
        else {
        	//��ʾ����Ч��
        	if("Y".equals(mjFlg)) {
        		result = IndStockDTool.getInstance().getOrgStockDrugQuery(parm);
        	}else {
        		result = IndStockDTool.getInstance().onQueryOrgStockQuery(parm);
        	}
        }
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
//        Map map = new HashMap();
//        map.put("01", "��ҩ");
//        map.put("02", "�г�ҩ");
//        map.put("03", "�в�ҩ");
//        map.put("04", "������");
//        map.put("05", "��Һ");
//        map.put("06", "�Ƽ�");
//        map.put("07", "�Լ�");
//        map.put("08", "ԭ��");
        double stock_amt = 0;
        double own_amt = 0;
        for (int i = result.getCount() - 1; i >= 0; i--) {
            if ("N".equals(this.getValueString("ORDER_TYPE_W")) &&
                "W".equals(result.getValue("PHA_TYPE", i))&&!"Y".equals(result.getValue("CTRL_FLG", i))) {							
                result.removeRow(i);
                continue;
            }
            if ("N".equals(this.getValueString("ORDER_TYPE_C")) &&
                "C".equals(result.getValue("PHA_TYPE", i))) {
                result.removeRow(i);
                continue;
            }
            if ("N".equals(this.getValueString("ORDER_TYPE_G")) &&
                "G".equals(result.getValue("PHA_TYPE", i))) {
                result.removeRow(i);
                continue;
            }
/*            if ("N".equals(this.getValueString("MJ_FLG")) &&
                    "Y".equals(result.getValue("CTRL_FLG", i))) {
                    result.removeRow(i);					
                    continue;								
            }*/
//            result.setData("TYPE_CODE", i,
//                           map.get(result.getData("TYPE_CODE", i)));
            stock_amt += result.getDouble("STOCK_AMT", i);
            own_amt += result.getDouble("OWN_AMT", i);
        }
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }

        this.getTable("TABLE").setParmValue(result);
        this.setValue("STOCK_AMT", StringTool.round(stock_amt, 2));
        this.setValue("OWN_AMT", StringTool.round(own_amt, 2));
        this.setValue("DIFF_AMT", StringTool.round(own_amt - stock_amt, 2));
        this.setValue("SUM_TOT", this.getTable("TABLE").getRowCount());
    }
    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "ORG_CODE;ORDER_CODE;ORDER_DESC;BATCH_NO;TYPE_CODE;"
            + "SAFE_QTY;CHECK_NOT_ZERO;SHOW_BATCH;MAN_CODE;STOCK_AMT;OWN_AMT;"
            + "DIFF_AMT;SUM_TOT";
        this.clearValue(clearStr);
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setOrgCode("");
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).onQuery();
        this.setValue("ORDER_TYPE_W", "Y");
        this.setValue("ORDER_TYPE_C", "Y");
        this.setValue("ORDER_TYPE_G", "Y");
        this.getTable("TABLE").removeRowAll();
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        TTable table = getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "ҩƷ���ͳ�Ʊ�");
        date.setData("ORG_CODE", "TEXT",
                     "ͳ�Ʋ���: " +
                     this.getTextFormat("ORG_CODE").getText());
        date.setData("DATE", "TEXT",
                     "ͳ��ʱ��: " +
                     SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace('-', '/'));
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        // �������
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();
        for (int i = 0; i < table.getRowCount(); i++) {
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("SPECIFICATION", tableParm.getValue("SPECIFICATION", i));
            parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
            parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
            parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
            parm.addData("OWN_AMT", tableParm.getValue("OWN_AMT", i));
            parm.addData("MATERIAL_CHN_DESC",
                         tableParm.getValue("MATERIAL_CHN_DESC", i));
        }
        parm.setCount(parm.getCount("ORDER_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
        parm.addData("SYSTEM", "COLUMNS", "MATERIAL_CHN_DESC");
        date.setData("TABLE", parm.getData());
        // ��β����
        date.setData("TOT", "TEXT", "�ϼƣ� "+getValueDouble("OWN_AMT"));
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDOrgStockQuery.jhw",
                             date);
    }

    /**
     * ���Excel
     */
    public void onExport() {
        TTable table = this.getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "���ſ���ѯ");
    }

    /**
     * ����ѡ���¼�
     */
    public void onOrgAction() {
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setOrgCode(
            TypeTool.getString(getTextFormat("ORG_CODE").getValue()));
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).onQuery();
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setSelectedIndex(
            0);
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    public void updateStockQty(){
    	String orgCode = getValueString("ORG_CODE");
        if ("".equals(orgCode)) {
            this.messageBox("��ѯ���Ų���Ϊ��");
            return;
        }
        Timestamp date = SystemTool.getInstance().getDate();
        String updateDate =  date.toString().substring(0, 7).replace("-", "");
//        System.out.println("-------------�鿴�Ƿ��Ѿ�����sql:"+INDSQL.queryStockUpdate(updateDate,orgCode));
        //��ѯ�Ƿ��Ѿ�����
        TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockUpdate(updateDate,orgCode)));
        if (parm.getCount()>0) {
			this.messageBox("�����Ѿ����㣬�����ظ�����");
			return ;
		}
        if (this.messageBox("��ʾ", "�Ƿ������", 2) == 0) {
        	//���ݿ��
//        	System.out.println("---------����sql"+INDSQL.saveStockHistory(updateDate,orgCode));
        	TParm backParm = new TParm(TJDODBTool.getInstance().update(INDSQL.saveStockHistory(updateDate,orgCode)));
        }else {
			return;
		}        
//        System.out.println("--------------update sql:"+INDSQL.updateStockQty(orgCode));
        //��������
       TParm reultParm = updateStockQty(orgCode);//new TParm(TJDODBTool.getInstance().update(INDSQL.updateStockQty(orgCode)));
//       System.out.println("----------getErrCode: "+reultParm.getErrCode());
       if(reultParm.getErrCode()<0){
    	   this.messageBox("����ʧ�ܣ������¸���");
    	   return;
       }
       this.messageBox("���³ɹ�");
//       System.out.println("--------------insert sql:"+INDSQL.insertStockUpdate(updateDate, orgCode));
       //��������¼
       TParm saveTParm = new TParm(TJDODBTool.getInstance().update(INDSQL.insertStockUpdate(updateDate, orgCode)));
       
    }
    
    /**
     * �����ۿ�
     * @param orgCode
     */
    public TParm updateStockQty(String orgCode) {
    	TParm parm = new TParm();
    	parm.setData("OPT_USER", Operator.getID());
    	parm.setData("OPT_TERM", Operator.getIP());
    	String sql = INDSQL.getIndAccountForRed("", "", "");
    //	System.out.println("----------sql:"+sql);
		TParm accountParm = new TParm(TJDODBTool.getInstance().select(sql));//INDTool.getInstance().queryIndAccout("",orgCode,"");
	//	System.out.println("----------------accountparm:"+accountParm);
		parm.setData("data", accountParm.getData());
		TParm result = new TParm();
		if (null != accountParm && accountParm.getCount()>0) {
	        // ִ����������
	        result = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
	                                            "onUpdateStockQty", parm);
		}
		return result;
	}
/**
     * ͬ���������龫ҩƷ���
     */
    public void onSyn() {
    	TParm parm = new TParm();
    	TParm result = TIOM_AppServer.executeAction(                             
					"action.ind.INDStockSearchAction", "onSearchIndStock",parm);
    	 if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             this.messageBox("ͬ��ʧ��");
             return;
         }
    	 this.messageBox("ͬ���ɹ�");				
    	 this.onQuery();
    }
    
    /**
     * �龫����¼�			
     */
    public void onClick() {
    	String mjFlg = this.getValueString("MJ_FLG");
    	if("Y".equals(mjFlg)) {
    		this.setValue("ORDER_TYPE_W", "N");
    		this.setValue("ORDER_TYPE_C", "N");
    		this.setValue("ORDER_TYPE_G", "N");
    	}else {
    		this.setValue("ORDER_TYPE_W", "Y");
    		this.setValue("ORDER_TYPE_C", "Y");
    		this.setValue("ORDER_TYPE_G", "Y");
    	}
    }
    
    public void onClickW() {
    	String flg = this.getValueString("ORDER_TYPE_W");
    	if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}
    }
    
    public void onClickC() {
    	String flg = this.getValueString("ORDER_TYPE_C");
    	if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}
    }
    
    public void onClickG() {			
    	String flg = this.getValueString("ORDER_TYPE_G");
    	if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
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

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }


}
