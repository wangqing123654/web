package com.javahis.ui.inv;

import javax.print.DocFlavor.STRING;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTree;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import jdo.inv.InvBaseTool;

import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.TMessage;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

/**    
 * <p>Title: �����ֵ�</p>
 *
 * <p>Description: </p>
 *  
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy
 * @version 1.0
 */
public class INVBaseControl
    extends TControl {
    public INVBaseControl() {
    }

    private TTable table;

    private TTree tree;

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        // ��ʼ�����ṹ
        onInitTree();
        table = this.getTable("TABLE");
        tree = (TTree) callMessage("UI|TREE|getThis");
        //��TREE��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }         
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    /**
     * ��ʼ���� 
     */
    public void onInitTree() {
        TParm parmRule = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvBaseRule()));
        //System.out.println(parmRule);
        //�õ������ϵ�������
        tree = (TTree) callMessage("UI|TREE|getThis");
        tree.getRoot().removeAllChildren();
        TTreeNode treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        treeRoot.setText("�������");
        treeRoot.setType("Root");
        TParm parm = analyzeINVType();
        if (parm.getCount("INVTYPE_GROUP") <= 0)
            return;
        for (int i = 0; i < parm.getCount("INVTYPE_GROUP"); i++) {
            TParm parmI = (TParm) parm.getData("INVTYPE_GROUP", i);
            for (int j = 0; j < parmI.getCount("CATEGORY_CODE"); j++) {
                TTreeNode treeNode = new TTreeNode();
                treeNode.setText(parmI.getValue("CATEGORY_CHN_DESC", j));
                treeNode.setID(parmI.getValue("CATEGORY_CODE", j));
                if (parmI.getValue("DETAIL_FLG", j).equals("Y"))
                    treeNode.setType("UI");
                else
                    treeNode.setType("Path");
                if (getParentCode(treeNode.getID(), parmRule).length() == 0)
                    treeRoot.addSeq(treeNode);
                else
                    treeRoot.findNodeForID(getParentCode(treeNode.getID(),
                        parmRule)).addSeq(treeNode);
            }
        }
        tree.update();
    }

    /**
     * �õ����ڵ����
     * @param code String     
     * @param parm TParm
     * @return String
     */
    private String getParentCode(String code, TParm parm) {
        int classify1 = parm.getInt("CLASSIFY1", 0);
        int classify2 = parm.getInt("CLASSIFY2", 0);
        int classify3 = parm.getInt("CLASSIFY3", 0);
        int classify4 = parm.getInt("CLASSIFY4", 0);
        int classify5 = parm.getInt("CLASSIFY5", 0);
        int serialNumber = parm.getInt("SERIAL_NUMBER", 0);
        if (code.length() == classify1)
            return "";
        if (code.length() == classify1 + classify2)
            return code.substring(0, classify1);
        if (code.length() == classify1 + classify2 + classify3)
            return code.substring(0, classify1 + classify2);
        if (code.length() == classify1 + classify2 + classify3 + classify4)
            return code.substring(0, classify1 + classify2 + classify3);
        if (code.length() ==
            classify1 + classify2 + classify3 + classify4 + classify5)
            return code.substring(0,
                                  classify1 + classify2 + classify3 + classify4);
        if (code.length() ==
            classify1 + classify2 + classify3 + classify4 + classify5 +
            serialNumber)
            return code.substring(0,
                                  classify1 + classify2 + classify3 + classify4 +
                                  classify5);
        return "";
    }

    /**
     * �������ʷ�����Ϣ
     * @return TParm
     */
    public TParm analyzeINVType() {
        TParm parm = new TParm();
        TParm parmInf = selectINVType();
        //System.out.println(parmInf);
        TParm parmLength = getINVTypeLength();
        //System.out.println(parmLength);
        for (int i = 0; i < parmLength.getCount(); i++) {
            int lenght = parmLength.getInt("CATEGORY_LENGTH", i);
            TParm parmI = new TParm();
            for (int j = 0; j < parmInf.getCount(); j++) {
                if (lenght == parmInf.getValue("CATEGORY_CODE", j).length()) {
                    copyTParm(parmInf, parmI, j);
                }
            }
            parm.addData("INVTYPE_GROUP", parmI);
        }
        return parm;
    }

    /**
     * �õ����ʷ�����Ϣ
     * @return TParm
     */
    public TParm selectINVType() {
        return new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvBaseCategory()));
    }

    /**
     * �õ����ʷ�����볤����Ϣ
     * @return TParm
     */
    public TParm getINVTypeLength() {
        return new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvBaseCategoryLength()));
    }

    /**
     * ����������
     * @param fromTParm TParm
     * @param toTParm TParm
     * @param row int
     */
    public void copyTParm(TParm fromTParm, TParm toTParm, int row) {
        for (int i = 0; i < fromTParm.getNames().length; i++) {
            toTParm.addData(fromTParm.getNames()[i],
                            fromTParm.getValue(fromTParm.getNames()[i], row));
        }
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        table.removeRowAll();
        String sql = InvBaseTool.getInstance().queryInvBase(this.getSerch()) ;
//        System.out.println("sql=====sql======="+sql);
        table.setSQL(sql);
        table.retrieve();
        if (table.getRowCount() < 0) {
            this.messageBox("û�в�ѯ����");
        }
    }
    /**
     * ��ȡ��ѯ��������
     */
    public TParm  getSerch(){
    	TParm result  = new TParm() ;
    	//���ʱ���
    	if(this.getValueString("INV_CODE").length()>0){
    		result.setData("INV_CODE", this.getValueString("INV_CODE")) ;	
    	}
    	//��������
    	if(this.getValueString("INV_CHN_DESC").length()>0){
    		result.setData("INV_CHN_DESC", this.getValueString("INV_CHN_DESC")+"%") ;
    	}
    	//��Ӧ����
    	if(this.getValueString("SUP_CODE").length()>0){
    		result.setData("SUP_CODE", this.getValueString("SUP_CODE")) ;
    	}
    	//ƴ����
    	if(this.getValueString("PY1").length()>0){
    		result.setData("PY1", "%"+this.getValueString("PY1")+"%") ;
    	}
    	//��������
    	if(this.getValueString("INVKIND_CODE").length()>0){
    		result.setData("INVKIND_CODE", this.getValueString("INVKIND_CODE")) ;
    	}
    	return result ;
    }
    /** 
     * ��������
     */
    public void onNew() {
        if (tree.getSelectNode().isRoot()){
            return;
        }
        if (tree.getSelectNode() == null) {
            this.messageBox("��ѡ���������");
            return;
        }   
        String cString=tree.getSelectNode().getID();
       String e= this.getInvMaxNumber(cString).getValue("INV_CODE",0);
       if (e==null||"".equals(e)) {
		e="0000000";
       }else {
		e=e.substring(6,e.length());
       }
       
       String a=cString.substring(0,2);
       String b=cString.substring(2,4);
        this.setValue("INV_CODE",  a+"."+b+"."+e);
        
        //����������Լ��޸�
        //getTextField("INV_CODE").setEnabled(false);  
    }

    /**  
     * �õ������ˮ��  
     * @param typeCode String
     * @return String
     */
    public String getInvMaxSerialNumber(String typeCode){
        TParm parmRule = getINVRule();
        int serialNumber = parmRule.getInt("SERIAL_NUMBER",0);
        int totNumber = parmRule.getInt("TOT_NUMBER",0);
        TParm parm = getInvMaxNumber(typeCode);
        System.out.println("�õ������ˮ��parm"+parm);
        String numString = ""; 
        if (parm.getCount("INV_CODE") <= 0 || 
            parm.getValue("INV_CODE", 0).length() == 0)
            numString = "0"; 
        else                  
            numString = parm.getValue("INV_CODE", 0).substring(typeCode.length(),
            parm.getValue("INV_CODE", 0).length()).substring(2, 9);
//            numString = parm.getValue("INV_CODE", 0).substring(typeCode.length(),
//            parm.getValue("INV_CODE", 0).length()); 
        //1.0370627             
        int num = Integer.parseInt(numString) + 1;      
        numString = num + "";  
        for (int i = 0; numString.length() < serialNumber; i++)
            numString = "0" + numString;
        String zeroType = "";
        for (int i = 0;
             zeroType.length() < (totNumber - typeCode.length() - numString.length());
             i++) {
            zeroType += "0";
        }
        return typeCode + zeroType + numString;
    }

    /**
     * �õ����ʷ�����볤����Ϣ
     * @return TParm
     */
    public TParm getINVRule() {
        return new TParm(TJDODBTool.getInstance().select(INVSQL.getInvRule()));
    }

    /**
     * ȡ������
     * @param invtype_code String
     * @return TParm
     */
    public TParm getInvMaxNumber(String invtype_code) {
        return new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvMaxSerialNumber(invtype_code)));
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        // INV_BASE��������
        TParm parmBase = new TParm();
        parmBase = getParmBase(parmBase);
        // INV_TRANSUNIT��������
        TParm parmUnit = new TParm();
        parmUnit = getParmUnit(parmUnit);
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("INV_BASE", parmBase.getData());
        parm.setData("INV_TRANSUNIT", parmUnit.getData());
        if (table.getSelectedRow() < 0) {
            // ��������
            result = TIOM_AppServer.executeAction(
                "action.inv.INVBaseAction", "onInsert", parm);
        }
        else {   
            // ��������
            result = TIOM_AppServer.executeAction(
                "action.inv.INVBaseAction", "onUpdate", parm);
        }
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        this.onClear();
    }  

    /**
     * ���ݼ��
     * @return boolean
     */
    public boolean checkData() {
        if ("".equals(this.getValueString("INV_CHN_DESC"))) {
            this.messageBox("�������Ʋ���Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("INVKIND_CODE"))) {
            this.messageBox("�������಻��Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("MAN_CODE"))) {
            this.messageBox("�������̲���Ϊ��");
            return false;
        }
        if ("".equals(this.getValue("COST_PRICE"))) {
            this.messageBox("�ɱ��۸���Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("PURCH_UNIT"))) {
            this.messageBox("������λ����Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("STOCK_UNIT"))) {
            this.messageBox("��浥λ����Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("DISPENSE_UNIT"))) {
            this.messageBox("���ⵥλ����Ϊ��");
            return false;
        }
        if ("".equals(this.getValue("STOCK_QTY"))) {
            this.messageBox("���������Ϊ��");
            return false;
        }
        if ("".equals(this.getValue("DISPENSE_QTY"))) {
            this.messageBox("����������Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * INV_BASE��������
     * @param parmBase TParm 
     * @return TParm  
     */  
    public TParm getParmBase(TParm parmBase) {
    	parmBase.setData("ACCOUNTS_CODE", this.getValueString("ACCOUNTS_CODE"));
        parmBase.setData("INV_CODE", this.getValueString("INV_CODE"));
        parmBase.setData("INVTYPE_CODE", tree.getSelectNode().getID());
        parmBase.setData("INV_CHN_DESC", this.getValueString("INV_CHN_DESC"));
        parmBase.setData("INV_ABS_DESC", this.getValueString("INV_ABS_DESC"));
        parmBase.setData("INV_ENG_DESC", this.getValueString("INV_ENG_DESC"));
        parmBase.setData("PY1", this.getValueString("PY1"));
        parmBase.setData("PY2", this.getValueString("PY2"));
        parmBase.setData("INVKIND_CODE", this.getValueString("INVKIND_CODE"));
        parmBase.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parmBase.setData("MAN_CODE", this.getValueString("MAN_CODE"));
        parmBase.setData("MAN_NATION", this.getValueString("MAN_NATION"));
        parmBase.setData("BUYWAY_CODE",
                         this.getRadioButton("BUYWAY_CODE_1").isSelected() ? "1" :
                         "2");
        parmBase.setData("USE_DEADLINE", this.getValueDouble("USE_DEADLINE"));
        parmBase.setData("COST_PRICE", this.getValueDouble("COST_PRICE"));
        parmBase.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        parmBase.setData("HYGIENE_TRADE_CODE",
                         this.getValueString("HYGIENE_TRADE_CODE"));
        parmBase.setData("STOCK_UNIT", this.getValueString("STOCK_UNIT"));
        parmBase.setData("DISPENSE_UNIT", this.getValueString("DISPENSE_UNIT"));
        parmBase.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG"));
        parmBase.setData("STOPBUY_FLG", this.getValueString("STOPBUY_FLG"));
        parmBase.setData("NORMALDRUG_FLG", this.getValueString("NORMALDRUG_FLG"));
        parmBase.setData("REQUEST_FLG", this.getValueString("REQUEST_FLG"));
        parmBase.setData("SEQMAN_FLG", this.getValueString("SEQMAN_FLG"));
        parmBase.setData("VALIDATE_FLG", this.getValueString("VALIDATE_FLG"));
        parmBase.setData("EXPENSIVE_FLG", this.getValueString("EXPENSIVE_FLG"));
        parmBase.setData("OPT_USER", Operator.getID());
        parmBase.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parmBase.setData("OPT_TERM", Operator.getIP());    
        parmBase.setData("CONSIGN_FLG", this.getValueBoolean("CONSIGN_FLG"));
        parmBase.setData("CONSIGN_MAN_CODE", this.getValueString("CONSIGN_MAN_CODE"));
         
        parmBase.setData("SUP_CODE", this.getValueString("SUP_CODE")); 
        parmBase.setData("UP_SUP_CODE", this.getValueString("UP_SUP_CODE"));
        parmBase.setData("INV_KIND", this.getValueString("KIND"));  
        parmBase.setData("INV_ACCOUNT", this.getValueString("ACCOUNT"));  
        System.out.println("parmBase"+parmBase);
        return parmBase;       
    }

    /**
     * INV_TRANSUNIT��������
     * @param parmUnit TParm
     * @return TParm
     */
    public TParm getParmUnit(TParm parmUnit) {
        parmUnit.setData("INV_CODE", this.getValueString("INV_CODE"));
        parmUnit.setData("PURCH_UNIT", this.getValueString("PURCH_UNIT"));
        parmUnit.setData("PURCH_QTY", 1);
        parmUnit.setData("STOCK_UNIT", this.getValueString("STOCK_UNIT"));
        parmUnit.setData("STOCK_QTY", this.getValueDouble("STOCK_QTY"));
        parmUnit.setData("DISPENSE_UNIT", this.getValueString("DISPENSE_UNIT"));
        parmUnit.setData("DISPENSE_QTY", this.getValueDouble("DISPENSE_QTY"));
        parmUnit.setData("OPT_USER", Operator.getID());
        parmUnit.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parmUnit.setData("OPT_TERM", Operator.getIP());
        return parmUnit;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        table.removeRowAll(); 
        String clearStr = "INV_CODE;INV_CHN_DESC;INV_ABS_DESC;INV_ENG_DESC;"
            + "PY1;PY2;INVKIND_CODE;DESCRIPTION;MAN_CODE;MAN_NATION;"
            + "USE_DEADLINE;COST_PRICE;ORDER_CODE;ORDER_DESC;"
            + "HYGIENE_TRADE_CODE;ACTIVE_FLG;STOPBUY_FLG;NORMALDRUG_FLG;"
            + "REQUEST_FLG;SEQMAN_FLG;VALIDATE_FLG;EXPENSIVE_FLG;PURCH_UNIT;"
            + "STOCK_QTY;STOCK_UNIT;DISPENSE_QTY;DISPENSE_UNIT;UNIT;" + 
              "SUP_CODE;UP_SUP_CODE;KIND;ACCOUNT;CONSIGN_FLG;CONSIGN_MAN_CODE";  
        this.clearValue(clearStr); 
        getTextField("INV_CODE").setEnabled(true);
    }

    /** 
     * ɾ������
     */
    public void onDelete() {
        if (table.getSelectedRow() < 0) {
            this.messageBox("��ѡ��ɾ����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("INV_CODE", this.getValueString("INV_CODE"));
        TParm result = TIOM_AppServer.executeAction(
            "action.inv.INVBaseAction", "onDelete", parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("ɾ��ʧ��");
            return;
        }
        this.messageBox("ɾ���ɹ�");
        this.onClear();
    }

    /**
     * ���ĵ����¼�
     */
    public void onTreeClicked(){
       //����Ƿ�õ����Ľڵ�
       if(((TTree)getComponent("TREE")).getSelectNode() == null)
           return;
       if(((TTree)getComponent("TREE")).getSelectNode().isRoot())
           return;
       onClear();
       queryInvTableInfo(((TTree)getComponent("TREE")).getSelectNode().getID());
    }

    /**
     * ��ѯ������ݶ���
     * @param invType String
     */
    private void queryInvTableInfo(String type_code) {
        //������е���Ϣ���
        ( (TTable) getComponent("TABLE")).removeRowAll();
        ( (TTable) getComponent("TABLE")).setSQL(INVSQL.getInvBaseByTypeCode(
            type_code));
        ( (TTable) getComponent("TABLE")).retrieve();
    }

    /**
     * INV_CHN_DESC�س��¼�
     */
    public void onInvDescAction() {
        String py = TMessage.getPy(this.getValueString("INV_CHN_DESC"));
        setValue("PY1", py);
    }

    /**
     * STOCK_UNIT�س��¼� 
     */
    public void onStockUnitAction() {
        setValue("UNIT", this.getValue("STOCK_UNIT"));
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableClick() {
    	//messageBox("111"); 
        String inv_code = table.getDataStore().getRowParm(table.getSelectedRow()).
            getValue("INV_CODE");
        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvBaseInfo(inv_code)));
        String parmStr = "INV_CODE;ACTIVE_FLG;INVTYPE_CODE;INVKIND_CODE;"
            + "INV_CHN_DESC;PY1;INV_ENG_DESC;INV_ABS_DESC;DESCRIPTION;MAN_CODE;"
            + "MAN_NATION;PURCH_UNIT;STOCK_UNIT;" 
            + "ORDER_CODE;HYGIENE_TRADE_CODE;STOPBUY_FLG;NORMALDRUG_FLG;"
            + "REQUEST_FLG;SEQMAN_FLG;VALIDATE_FLG;EXPENSIVE_FLG;PY2;"     
            + "DISPENSE_UNIT;ORDER_DESC;SUP_CODE;UP_SUP_CODE;KIND;ACCOUNT;" +
              "CONSIGN_MAN_CODE;CONSIGN_FLG";        
        TParm baseParm = parm.getRow(0);      
        this.setValueForParm(parmStr, baseParm);  
        //messageBox("aaaaaaa2222");
        if ("1".equals(baseParm.getValue("BUYWAY_CODE"))) {
            this.getRadioButton("BUYWAY_CODE_1").setSelected(true);
        }
        else {
            this.getRadioButton("BUYWAY_CODE_2").setSelected(true);
        }
        this.setValue("USE_DEADLINE", baseParm.getDouble("USE_DEADLINE"));
        this.setValue("COST_PRICE", baseParm.getDouble("COST_PRICE"));
        this.setValue("STOCK_QTY", baseParm.getDouble("STOCK_QTY"));
        this.setValue("DISPENSE_QTY", baseParm.getDouble("DISPENSE_QTY"));
        this.setValue("UNIT", baseParm.getValue("STOCK_UNIT"));
        this.getTextField("INV_CODE").setEnabled(false);
        //messageBox("2222");
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
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
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

}
