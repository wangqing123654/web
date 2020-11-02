package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import jdo.sys.SYSRuleTool;
import java.util.regex.Pattern;
import jdo.clp.CLPORDERTypeTool;
import com.dongyang.data.TNull;
import com.dongyang.ui.TTable;
import java.util.Map;
import com.dongyang.jdo.TDataStore;
import java.util.HashMap;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTree;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import java.text.DecimalFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;

/**
 * <p>Title: �ٴ�·��ҽ������</p>
 *
 * <p>Description: �ٴ�·��ҽ������</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPORDERTypeControl extends TControl{
    public CLPORDERTypeControl() {

    }
    /**
     * ����
     */
    private TTreeNode treeRoot;
    //��
    TTree tree;
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
    private TDataStore treeDataStore = new TDataStore();
    /**
     * ��Ź�����𹤾�
     */
    private SYSRuleTool ruleTool;
    //ҳ������
    private String systemSign;
    //�շѷ���
    private final String feeType="FEE";
    //ҩƷ����
    private final String medType="MED";
    //���������
    private final String exmType="EXM";
    //ҽ���ײͷ���
    private final String packType="PACK";
    /**
     * ҳ���ʼ������
     */
    public void onInit() {
        super.onInit();
        initPage();
        //��ʼ����
        onInitTree();
        //��tree��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        //��ʼ�����
        onInitNode();
        //�������ֵ�䶯
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this,
                         "onChangeTableValue");

    }
    /**
     * �������ֵ�䶯����
     * @param obj Object
     * @return boolean
     */
    public boolean onChangeTableValue(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return true;
        //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        if (node.getValue().equals(node.getOldValue()))
            return true;
        //�ж�Ƶ���Ƿ����Ҫ��
        int selectedColumn = node.getTable().getSelectedColumn();
        int selectedRow = node.getTable().getSelectedRow();
        String nodeValue = (String) node.getValue();
        boolean freqFlag = false;
        if (selectedColumn == 5) {
            if(!validDouble(nodeValue)&&this.checkNullAndEmpty(nodeValue)){
                this.messageBox("�ٴ�·�������������");
                freqFlag=true;
            }
            //����ת����
            setRate(selectedRow,Double.parseDouble(nodeValue));
        }
        return freqFlag;
    }
    private void setRate(int row,double clpQTY){
        TTable table= (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        TParm rowParm = tableParm.getRow(row);
        double medQTY=rowParm.getDouble("MEDI_QTY");
        tableParm.setData("CLP_RATE",row,getRate(medQTY,clpQTY));
        table.setParmValue(tableParm);
    }
    /**
     * �õ�ת����
     * @param mediQty double
     * @param clpQTY double
     * @return double
     */
    private double getRate(double mediQty,double clpQTY){
        double medQTY=mediQty;
        double rate=clpQTY/medQTY;
        DecimalFormat formater = new DecimalFormat("0.##");
        try{
            rate = Double.parseDouble(formater.format(rate));
        }catch(Exception e){
            rate=0.0;
        }
        return rate;
    }
    /**
     * ��ʼ����
     */
    public void onInitTree() {
        //�õ�����
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null)
            return;
        //�����ڵ����������ʾ
        String treeTitle="";
        if(systemSign.equalsIgnoreCase(this.exmType)){
            treeTitle="���������";
        }else if(systemSign.equalsIgnoreCase(this.packType)){
            treeTitle="ҽ���ײͷ���";
        }else if(systemSign.equalsIgnoreCase(this.medType)){
            treeTitle="ҩƷ����";
        }else if(systemSign.equalsIgnoreCase(this.feeType)){
            treeTitle="�շѷ���";
        }
        treeRoot.setText(treeTitle);
        //�����ڵ㸳tag
        treeRoot.setType("Root");
        //���ø��ڵ��id
        treeRoot.setID("");
        //������нڵ������
        treeRoot.removeAllChildren();
        //���������ʼ������
        callMessage("UI|TREE|update");
    }
    /**
     * ��ʼ�����Ľ��
     */

    public void onInitNode() {
        //������
        if (this.systemSign.equalsIgnoreCase(this.exmType)) {
            //��dataStore��ֵ
            treeDataStore.setSQL(
                    "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='EXM_RULE'");
            //�����dataStore���õ�������С��0
            if (treeDataStore.retrieve() <= 0)
                return;
            //��������,�Ǳ�������еĿ�������
            ruleTool = new SYSRuleTool("EXM_RULE");
        }
        //ҽ���ײ�
        if (this.systemSign.equalsIgnoreCase(this.packType)) {
            //��dataStore��ֵ
            treeDataStore.setSQL(
                    "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='ORDPACK_RULE'");
            //�����dataStore���õ�������С��0
            if (treeDataStore.retrieve() <= 0)
                return;
            //��������,�Ǳ�������еĿ�������
            ruleTool = new SYSRuleTool("ORDPACK_RULE");
        }
        //�շѷ���
        if (this.systemSign.equalsIgnoreCase(this.feeType)) {
            //��dataStore��ֵ
            treeDataStore.setSQL(
                    "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='SYS_FEE_RULE'");
            //�����dataStore���õ�������С��0
            if (treeDataStore.retrieve() <= 0)
                return;
            //��������,�Ǳ�������еĿ�������
            ruleTool = new SYSRuleTool("SYS_FEE_RULE");
        }
        //ҩƷ����
        if (this.systemSign.equalsIgnoreCase(this.medType)) {
            //��dataStore��ֵ
            treeDataStore.setSQL(
                    "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='PHA_RULE'");
            //�����dataStore���õ�������С��0
            if (treeDataStore.retrieve() <= 0)
                return;
            //��������,�Ǳ�������еĿ�������
            ruleTool = new SYSRuleTool("PHA_RULE");
        }
        if (ruleTool.isLoad()) { //�����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "CATEGORY_CODE",
                "CATEGORY_CHN_DESC", "Path", "SEQ");
            //ѭ����������ڵ�
            for (int i = 0; i < node.length; i++)
                treeRoot.addSeq(node[i]);
        }
        //�õ������ϵ�������
         this.tree = (TTree) callMessage("UI|TREE|getThis");
        //������
        tree.update();
        //��������Ĭ��ѡ�нڵ�
        tree.setSelectNode(treeRoot);

    }

    /**
     * ������
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        this.onQuery();

    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        TParm result = CLPORDERTypeTool.getInstance().
                       selectOrderCodeFromSysFeeData(getSelectCondition());
//        System.out.println("��ѯ����ҽ��" + result);
        addDefaultValueToTable(result);
        this.callFunction("UI|TABLE|setParmValue", result);
    }
    /**
     * ����Ĭ��ֵ�����ݿ�
     */
    public void addDefaultValueToTable(TParm result){
        for(int i=0;i<result.getCount();i++){
            TParm row=result.getRow(i);
            String unit=row.getValue("CLP_UNIT");
            if(!this.checkNullAndEmpty(unit)){
                result.setData("CLP_UNIT",i,row.getValue("MEDI_UNIT"));
            }
            String qty=row.getValue("CLP_QTY");
            if(!this.checkNullAndEmpty(qty)||"0.0".equals(qty)){
                result.setData("CLP_QTY",i,row.getValue("MEDI_QTY"));
                double rate= getRate(row.getDouble("MEDI_QTY"),result.getDouble("CLP_QTY",i));
                result.setData("CLP_RATE",i,rate);
            }
            if (!systemSign.equalsIgnoreCase(this.medType)) {
            	result.setData("MEDI_UNIT",i,row.getValue("UNIT_CODE"));
            	result.setData("MEDI_QTY",i,1);
			}
        }
    }

    /**
     * ��շ���
     */
    public void onClear(){
        this.clearValue("ORDER_CODE;ORDER_DESC");
    }
    /**
     * ���淽��
     */
    public void onSave(){
        TTable table =(TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm savaParm=new TParm();
        int rownum=0;
        if(!this.validSaveData()){
            return;
        }
        if (parm.getCount() <= 0) {
            return;
        }
        for(int i=0;i<parm.getCount();i++){
            TParm rowParm= parm.getRow(i);
//            if(!"".equals(rowParm.getValue("ORDER_CODE"))&&!"".equals(rowParm.getValue("TYPE_CODE"))){
                savaParm.addData("ORDER_CODE",rowParm.getValue("ORDER_CODE"));
                savaParm.addData("TYPE_CODE",null==rowParm.getValue("TYPE_CODE")?"":rowParm.getValue("TYPE_CODE"));
                savaParm.addData("ORDER_FLG","Y");
                savaParm.addData("CLP_UNIT",rowParm.getValue("CLP_UNIT"));
                savaParm.addData("CLP_RATE","".equals(rowParm.getValue("CLP_RATE"))?"0":rowParm.getValue("CLP_RATE"));
                savaParm.addData("CLP_QTY","".equals(rowParm.getValue("CLP_QTY"))?"0":rowParm.getValue("CLP_QTY"));
                Map basicInfo=getBasicOperatorMap();
                savaParm.addData("OPT_USER",basicInfo.get("OPT_USER"));
                savaParm.addData("OPT_DATE",basicInfo.get("OPT_DATE"));
                savaParm.addData("OPT_TERM",basicInfo.get("OPT_TERM"));
//            }
        }
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPORDERTypeAction", "saveORDERType", savaParm);
        if (result.getErrCode() <= 0) {
            this.messageBox("P0001");
            this.onQuery();
        } else {
            this.messageBox("E0001");
        }

    }
    /**
     * ������֤
     * @return boolean
     */
    private boolean validSaveData(){
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm parm = table.getParmValue();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm rowParm = parm.getRow(i);
            if (!"".equals(rowParm.getValue("ORDER_CODE")) &&
                !"".equals(rowParm.getValue("TYPE_CODE"))) {
                String rate= rowParm.getValue("CLP_RATE");
                if(!"".equals(rate)&&!this.validDouble(rate)){
                    this.messageBox("ת����������Ϸ���ֵ!");
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * ����ʵ������жϱ���ѡ�����Ƿ���Ա༭|
     *
     */
    public void setTableEnabledWithValidColumn() {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        int column=table.getSelectedColumn();
        int row=table.getSelectedRow();
        if(column==2||column==5||column==6){
            setTableEnabled("TABLE", table.getSelectedRow(),
                        table.getSelectedColumn());
        }
    }

    /**
     * �õ���ѯ��TParm
     * @return TParm
     */
    private TParm getSelectCondition(){
        TTreeNode node = tree.getSelectNode();
        TParm selectTParm = new TParm();
        //�жϵ�����Ƿ������ĸ����
        if (!node.getType().equals("Root")&&node!=null) {
            //�õ���ǰѡ�еĽڵ��idֵ
            String id = node.getID();
            selectTParm.setData("ORDER_CODE", id + "%");
        }
        this.putParamLikeWithObjName("ORDER_CODE",selectTParm,"ORDER_CODE_CONDITION");
        this.putParamLikeWithObjName("ORDER_DESC",selectTParm);
        this.putBasicSysInfoIntoParm(selectTParm);
        return selectTParm;
    }
    /**
     * ��ʼ��ҳ��
     */
    private void initPage() {
        //�õ�ҳ�洫�����
        Object obj = this.getParameter();
        if(obj!=null){
            systemSign=this.getParameter().toString();
        }
        //��ʼ��ҽ��ѡ��ؼ�ORDER_CODE
        TTextField orderDesc=(TTextField)this.getComponent("ORDER_DESC");
        orderDesc.setPopupMenuParameter("UD",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"));
        //����ҽ�����ܷ���ֵ����
        orderDesc.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
    }
    /**
     * ҽ�����ܷ���ֵ����
     * @param tag String
     * @param obj Object
     */
    public void popOrderReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("ORDER_CODE");
        String orderDesc = parm.getValue("ORDER_DESC");
        this.setValue("ORDER_CODE", orderCode);
        this.setValue("ORDER_DESC", orderDesc);
    }

    /**
     * �����Ķ�Ӧ��Ԫ�����óɿ�д�����������óɲ���д
     * @param tableName String
     * @param rowNum int
     * @param columnNum int
     */
    private void setTableEnabled(String tableName, int rowNum, int columnNum) {
        TTable table = (TTable)this.getComponent(tableName);
        int totalColumnMaxLength = table.getColumnCount();
        int totalRowMaxLength = table.getRowCount();
        //System.out.println("��������" + totalColumnMaxLength + "������:" +
          //                 totalRowMaxLength);
        //����
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
        //System.out.println("���д���" + lockColumnStr);
        table.setLockColumns(lockColumnStr);
        //����
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
        //System.out.println("���д�ǰ��" + lockRowStr + "����" + totalRowMaxLength);
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
        //System.out.println("���д���" + lockRowStr);
        if (lockRowStr.length() > 0) {
            table.setLockRows(lockRowStr);
        }

    }

    /**
     * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
     * @param objName String
     */
    private void putParamWithObjName(String objName, TParm parm,
                                     String paramName) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        parm.setData(paramName, objstr);
    }


    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        //System.out.println(objstr);
        objstr = objstr;
        //����ֵ��ؼ�����ͬ
        parm.setData(objName, objstr);
    }

    /**
     * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
     * @param objName String
     */
    private void putParamLikeWithObjName(String objName, TParm parm,
                                         String paramName) {
        String objstr = this.getValueString(objName).trim();
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr + "%";
            parm.setData(paramName, objstr);
        }

    }

    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamLikeWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr.trim() + "%";
            //����ֵ��ؼ�����ͬ
            parm.setData(objName, objstr);
        }
    }

    /**
     * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm) {
        putParamWithObjNameForQuery(objName, parm, objName);
    }

    /**
     * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
     * @param objName String
     * @param parm TParm
     * @param paramName String
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm,
                                             String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            //����ֵ��ؼ�����ͬ
            parm.setData(paramName, objstr.trim());
        }
    }

    /**
     * ���ؼ��Ƿ�Ϊ��
     * @param componentName String
     * @return boolean
     */
    private boolean checkComponentNullOrEmpty(String componentName) {
        if (componentName == null || "".equals(componentName)) {
            return false;
        }
        String valueStr = this.getValueString(componentName);
        if (valueStr == null || "".equals(valueStr)) {
            return false;
        }
        return true;
    }

    /**
     * �õ�ָ��table��ѡ����
     * @param tableName String
     * @return int
     */
    private int getSelectedRow(String tableName) {
        int selectedIndex = -1;
        if (tableName == null || tableName.length() <= 0) {
            return -1;
        }
        Object componentObj = this.getComponent(tableName);
        if (!(componentObj instanceof TTable)) {
            return -1;
        }
        TTable table = (TTable) componentObj;
        selectedIndex = table.getSelectedRow();
        return selectedIndex;
    }

    /**
     * ������֤����
     * @param validData String
     * @return boolean
     */
    private boolean validNumber(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,}");
        Matcher match = p.matcher(validData);
        if (!match.matches()) {
            return false;
        }
        return true;
    }

    /**
     * ������֤����
     * @param validData String
     * @return boolean
     */
    private boolean validDouble(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,2}([.][0-9]{1,2}){0,1}");
        Matcher match = p.matcher(validData);
        if (!match.matches()) {
            return false;
        }
        return true;
    }


    /**
     * ��TParm�м���ϵͳĬ����Ϣ
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
        //System.out.println("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM",  Operator.getIP());
    }

    /**
     * ����Operator�õ�map
     * @return Map
     */
    private Map getBasicOperatorMap() {
        Map map = new HashMap();
        map.put("REGION_CODE", Operator.getRegion());
        map.put("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        map.put("OPT_DATE", datestr);
        map.put("OPT_TERM", Operator.getIP());
        return map;
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @param dataFormatStr String
     * @return String
     */
    private String getCurrentDateStr(String dataFormatStr) {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, dataFormatStr);
        return datestr;
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @return String
     */
    private String getCurrentDateStr() {
        return getCurrentDateStr("yyyyMMdd");
    }

    /**
     * ����Ƿ�Ϊ�ջ�մ�
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }

    /**
     * ����TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from, TParm to, int row) {
        for (int i = 0; i < from.getNames().length; i++) {
            to.addData(from.getNames()[i],
                       from.getValue(from.getNames()[i], row));
        }
    }

    /**
     * ��¡����
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getValue(from.getNames()[i]));
        }
        return returnTParm;
    }

    /**
     * ����TParm ���null�ķ���
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNullVector(TParm parm, String keyStr, Class type) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData(keyStr, i) == null) {
                //System.out.println("����Ϊ�����");
                TNull tnull = new TNull(type);
                parm.setData(keyStr, i, tnull);
            }
        }
    }

    /**
     * ����TParm ��nullֵ����
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNull(TParm parm, String keyStr, Class type) {
        if (parm.getData(keyStr) == null) {
            //System.out.println("����Ϊ�����");
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }




}
