package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import jdo.dev.DevTypeTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTree;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: �豸������</p>
 *
 * <p>Description: �豸������</p>
 * 
 * <p>Copyright: Copyright (c) 2009</p>
 * 
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class DevTypeControl extends TControl {

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        onInitTree(); 
        //fux modify 
//		if (selVisitCode.getValue("DEFAULT_VISIT_CODE", 0).equals("1")) {
//			setValue("VISIT_CODE_F", "Y");
//			callFunction("UI|MR_NO|setEnabled", true);
//		}  
//        if(this.getValue("DEVPRO_CODE").equals("B")){
//            this.callFunction("UI|DEVSET_CODE|setEnabled", true);
//            this.callFunction("UI|DEVSET_CHN_DESC|setEnabled", true); 
//        }
//        else{ 
//        	 this.callFunction("UI|DEVSET_CODE|setEnabled", false);
//             this.callFunction("UI|DEVSET_CHN_DESC|setEnabled", false); 
//        } 
        //��TREE��Ӽ����¼� 
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        //������豸�¼� 
        //�󶨿ؼ�      
        callFunction("UI|DEVSET_CODE|setPopupMenuParameter", "aaa",
        "%ROOT%\\config\\sys\\DEVBASEPopupUI.x");
        //textfield���ܻش�ֵ   
        callFunction("UI|DEVSET_CODE|addEventListener", 
        TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        //Ĭ��Ϊ�й�
        this.setValue("MAN_NATION", "86");  
    	((TTextField)getComponent("DEVSET_CODE")).setEnabled(true);
    	((TTextField)getComponent("DEVSET_CHN_DESC")).setEnabled(false);
         
    }    

    /**
     * ������ܷ���ֵ����
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj; 
        String dev_code = parm.getValue("DEV_CODE");
        if (!dev_code.equals("")) {
            getTextField("DEVSET_CODE").setValue(dev_code); 
        }                                                                        
        String dev_desc = parm.getValue("DEV_CHN_DESC");
        if (!dev_desc.equals("")) {
            getTextField("DEVSET_CHN_DESC").setValue(dev_desc);
        } 
    }
    /**
     * ��ʼ���� 
     */
    public void onInitTree(){
        TParm parmRule = new TParm();
        parmRule = DevTypeTool.getInstance().getDevRule(); 
        //�õ������ϵ�������
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        tree.getRoot().removeAllChildren();
        TTreeNode treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        treeRoot.setText("�豸���");
        treeRoot.setType("Root");
        TParm parm = DevTypeTool.getInstance().analyzeDevType();
        if(parm.getCount("DEVTYPE_GROUP") <= 0)
            return;
        for(int i = 0;i < parm.getCount("DEVTYPE_GROUP");i++){
            TParm parmI = (TParm)parm.getData("DEVTYPE_GROUP",i);
            for(int j = 0;j < parmI.getCount("CATEGORY_CODE");j++){
                TTreeNode treeNode = new TTreeNode();
                treeNode.setText(parmI.getValue("CATEGORY_CHN_DESC",j));
                treeNode.setID(parmI.getValue("CATEGORY_CODE",j));
                if(parmI.getValue("DETAIL_FLG",j).equals("Y"))
                    treeNode.setType("UI");
                else
                    treeNode.setType("Path");
               if(getParentCode(treeNode.getID(),parmRule).length() == 0)
                   treeRoot.addSeq(treeNode);
               else
                   treeRoot.findNodeForID(getParentCode(treeNode.getID(),parmRule)).addSeq(treeNode);
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
    private String getParentCode(String code,TParm parm){
        int classify1 = parm.getInt("CLASSIFY1",0);
        int classify2 = parm.getInt("CLASSIFY2",0);
        int classify3 = parm.getInt("CLASSIFY3",0);
        int classify4 = parm.getInt("CLASSIFY4",0);
        int classify5 = parm.getInt("CLASSIFY5",0);
        int serialNumber = parm.getInt("SERIAL_NUMBER",0);
        if(code.length() == classify1)
            return "";
        if(code.length() == classify1 + classify2)
            return code.substring(0,classify1);
        if(code.length() == classify1 + classify2 + classify3)
            return code.substring(0,classify1 + classify2);
        if(code.length() == classify1 + classify2 + classify3 + classify4)
            return code.substring(0,classify1 + classify2 + classify3);
        if(code.length() == classify1 + classify2 + classify3 + classify4 + classify5)
            return code.substring(0,classify1 + classify2 + classify3 + classify4);
        if(code.length() == classify1 + classify2 + classify3 + classify4 + classify5 + serialNumber)
            return code.substring(0,classify1 + classify2 + classify3 + classify4 + classify5);
        return "";
    }
    /**
     * �õ����˳���
     * @return int
     */
    public int getMaxSeq(){
        TParm parm= DevTypeTool.getInstance().selectDevBaseMaxSeq();
        if(parm.getCount("SEQ") <= 0)
            return 1;
        if(parm.getValue("SEQ",0).length() == 0 ||
           parm.getInt("SEQ",0) == 0)
            return 1;
        return parm.getInt("SEQ",0) + 1;
    }
    //SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5,
    //SERIAL_NUMBER,TOT_NUMBER,RULE_TYPE 
    //FROM   SYS_RULE 
    //WHERE RULE_TYPE = 'DEV_RULE'  
    //DEV_CODE    68         22         10         00          0001  (682210000002)
    //DEV_CODE    CLASSIFY1  CLASSIFY2  CLASSIFY3  CLASSIFY4   SERIAL_NUMBER
    //RFID        131214�������գ�                 +3λ��ˮ��    (SERIAL_NUMBER����������ĳ�5λ) 
    /**
     * �õ������ˮ��
     * @param typeCode String
     * @return String
     */
    public String getDevMaxSerialNumber(String typeCode){
        TParm parmRule = new TParm();
        parmRule = DevTypeTool.getInstance().getDevRule();
        int serialNumber = parmRule.getInt("SERIAL_NUMBER",0);
        int totNumber = parmRule.getInt("TOT_NUMBER",0);         
        //SELECT MAX(DEV_CODE) DEV_CODE
        TParm parm = DevTypeTool.getInstance().getDevMaxSerialNumber(typeCode);
        String numString = "";
        if(parm.getCount("DEV_CODE")<=0 ||
           parm.getValue("DEV_CODE",0).length() == 0)
            numString = "0";
        else    
            numString = parm.getValue("DEV_CODE",0).
            substring(typeCode.length(),parm.getValue("DEV_CODE",0).length());
        int num = Integer.parseInt(numString) + 1;
        numString = num + "";
        for(int i = 0;numString.length()<serialNumber;i++)
           numString = "0" + numString;
        String zeroType = "";
        for(int i = 0;zeroType.length()<(totNumber - typeCode.length() - numString.length());i++){
            zeroType += "0";
        } 
        return typeCode + zeroType + numString;
    }
    /**
     * ���涯��
     */
    public void onSave(){
        if(getValueString("DEVKIND_CODE").length() == 0){
            messageBox("�豸��𲻿�Ϊ��");
            return;
        }
        if(getValueString("DEVPRO_CODE").length() == 0){
            messageBox("�豸���Բ���Ϊ��");
            return;
        }
        if(getValueString("USE_DEADLINE").length() == 0){
            messageBox("ʹ�����޲���Ϊ��");  
            return;        
        }
        //fux modify  20130814  ����Ҫ��Щ����Ϊ�գ�
        if(getValueString("DEP_DEADLINE").length() == 0){
            messageBox("�۾����޲���Ϊ��");  
            return;         
        }
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        if(getValueString("DEV_CODE").length() == 0 &&
           (tree.getSelectNode() == null||
            !tree.getSelectNode().isLeaf())){
            messageBox("��ѡ����С����");
            return;  
        }  

        String devCode = "";  
       if(getValueString("DEV_CODE").length() == 0)
       {
       devCode = getDevMaxSerialNumber(tree.getSelectNode().getID());
       } 
       else  
       {
       devCode = getValueString("DEV_CODE");
       }
       TParm parm = new TParm();
       parm.setData("DEVKIND_CODE",getValueString("DEVKIND_CODE"));
       parm.setData("DEVPRO_CODE",getValueString("DEVPRO_CODE"));
       parm.setData("ACTIVE_FLG",getValueString("ACTIVE_FLG"));
       parm.setData("DEV_CODE",devCode);
       //�޸ľ���type,����ѡȡtree��Ӧ�Ľڵ� 
       parm.setData("DEVTYPE_CODE",getValueString("DEVTYPE_CODE"));
       parm.setData("DEV_CHN_DESC",getValueString("DEV_CHN_DESC"));
       parm.setData("PY1",getValueString("PY1"));  
       parm.setData("DEV_ENG_DESC",getValueString("DEV_ENG_DESC"));
       parm.setData("DEV_ABS_DESC",getValueString("DEV_ABS_DESC"));
       parm.setData("DESCRIPTION",getValueString("DESCRIPTION"));
       parm.setData("SPECIFICATION",getValueString("SPECIFICATION"));
       parm.setData("UNIT_CODE",getValueString("UNIT_CODE"));
       parm.setData("DEPR_METHOD",getValueString("DEPR_METHOD"));
       parm.setData("MAN_CODE",getValueString("MAN_CODE"));
       parm.setData("MAN_NATION",getValueString("MAN_NATION")); 
       parm.setData("BUYWAY_CODE",getValueString("BUYWAY_CODE"));
       parm.setData("USE_DEADLINE",getValueDouble("USE_DEADLINE"));
       parm.setData("DEV_CLASS",getValueString("DEV_CLASS"));
       parm.setData("SEQMAN_FLG",getValueString("SEQMAN_FLG"));
       parm.setData("MEASURE_FLG",getValueString("MEASURE_FLG"));
       parm.setData("BENEFIT_FLG",getValueString("BENEFIT_FLG"));
       parm.setData("MEASURE_ITEMDESC",getValueString("MEASURE_ITEMDESC"));
       parm.setData("MEASURE_FREQ",getValueInt("MEASURE_FREQ"));
       parm.setData("SEQ",getMaxSeq());
       parm.setData("OPT_USER",Operator.getID());
       parm.setData("OPT_DATE",SystemTool.getInstance().getDate());
       parm.setData("OPT_TERM",Operator.getIP());
       //��������,��д�������豸 
       if("B".equals(getValueString("DEVPRO_CODE"))){    
       //�������豸     
       parm.setData("SETDEV_CODE",getValueString("DEVSET_CODE"));
       //�������豸���� 
       parm.setData("DEVSET_CHN_DESC",getValueString("DEVSET_CHN_DESC"));
       }     
       else{ 
       //�������豸   
       parm.setData("SETDEV_CODE","");  
       //�������豸����    
       parm.setData("DEVSET_CHN_DESC",""); 
       }
       parm.setData("DEP_DEADLINE",getValueDouble("DEP_DEADLINE")); 
       parm.setData("FINAN_KIND",getValueString("FINAN_KIND")); 
       parm.setData("FUNDSOU_CODE",getValueString("FUNDSOU_CODE")); 
       //�����ʲ����
       parm.setData("INTANGIBLE_FLG",getValueString("INTANGIBLE_FLG"));
       System.out.println("parm"+parm); 
       TParm result = DevTypeTool.getInstance().selectDevBase(devCode); 
       //������ݿ⣨DEV_BASE����ѯ����DEV_CODE�ҽ����ϵ�DEV_CODEΪ��
       if(result.getCount("DEV_CODE")>0 ){ 
    	   parm = DevTypeTool.getInstance().updateDevBase(parm);   
       //������ݿ�����DEV_CODE��ִ�и���
    	   if(this.getValueString("DEVPRO_CODE")=="B" && 
    	    	      parm.getValue("SETDEV_CODE") == null &&
    	    	      parm.getValue("DEVSET_CHN_DESC") == null){
    		   messageBox("�豸����Ϊ����,����д�����豸����������豸����");  
    		   return;
    	    	   }                
       if (parm.getErrCode() < 0) {
           messageBox("����ʧ��");
           return;
       }
       messageBox("����ɹ�");
       setValue("DEV_CODE",devCode);
       queryDevInf(getValueString("DEV_CODE"));
       onTreeClicked();
       return; 
       }
    }
    
    /**
     * ��������
     */
    public void onAdd(){
        if(getValueString("DEVKIND_CODE").length() == 0){
            messageBox("�豸��𲻿�Ϊ��");
            return;
        }
        if(getValueString("DEVPRO_CODE").length() == 0){
            messageBox("�豸���Բ���Ϊ��");
            return;
        }
        if(getValueString("USE_DEADLINE").length() == 0){
            messageBox("ʹ�����޲���Ϊ��");  
            return;
        }
        //fux modify   
        if(this.getValue("DEVPRO_CODE").equals("B")){
            this.callFunction("UI|DEVSET_CODE|setEnabled", true);
            this.callFunction("UI|DEVSET_CHN_DESC|setEnabled", true); 
        }
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        if(getValueString("DEV_CODE").length() == 0 &&
           (tree.getSelectNode() == null||
            !tree.getSelectNode().isLeaf())){
            messageBox("��ѡ����С����");
            return;  
        }  

        String devCode = "";  
       if(getValueString("DEV_CODE").length() == 0)
       {
       devCode = getDevMaxSerialNumber(tree.getSelectNode().getID());
       } 
       else 
       {
       devCode = getValueString("DEV_CODE");
       }
       TParm parm = new TParm();
       parm.setData("DEVKIND_CODE",getValueString("DEVKIND_CODE"));
       parm.setData("DEVPRO_CODE",getValueString("DEVPRO_CODE"));
       parm.setData("ACTIVE_FLG",getValueString("ACTIVE_FLG"));
       parm.setData("DEV_CODE",devCode);
       //����һ��Ҫѡȡ�ڵ�,��ȷ���豸������
       parm.setData("DEVTYPE_CODE",tree.getSelectNode().getID());
       parm.setData("DEV_CHN_DESC",getValueString("DEV_CHN_DESC"));
       //�������豸 
       parm.setData("SETDEV_CODE",getValueString("SETDEV_CODE"));
       parm.setData("PY1",getValueString("PY1"));  
       parm.setData("DEV_ENG_DESC",getValueString("DEV_ENG_DESC"));
       parm.setData("DEV_ABS_DESC",getValueString("DEV_ABS_DESC"));
       parm.setData("DESCRIPTION",getValueString("DESCRIPTION"));
       parm.setData("SPECIFICATION",getValueString("SPECIFICATION"));
       parm.setData("UNIT_CODE",getValueString("UNIT_CODE"));
       parm.setData("DEPR_METHOD",getValueString("DEPR_METHOD"));
       parm.setData("MAN_CODE",getValueString("MAN_CODE"));
       parm.setData("MAN_NATION",getValueString("MAN_NATION"));
       parm.setData("BUYWAY_CODE",getValueString("BUYWAY_CODE"));
       parm.setData("USE_DEADLINE",getValueDouble("USE_DEADLINE"));
       parm.setData("DEV_CLASS",getValueString("DEV_CLASS"));
       parm.setData("SEQMAN_FLG",getValueString("SEQMAN_FLG"));
       parm.setData("MEASURE_FLG",getValueString("MEASURE_FLG"));
       parm.setData("BENEFIT_FLG",getValueString("BENEFIT_FLG"));
       parm.setData("MEASURE_ITEMDESC",getValueString("MEASURE_ITEMDESC"));
       parm.setData("MEASURE_FREQ",getValueInt("MEASURE_FREQ"));
       parm.setData("SEQ",getMaxSeq());
       parm.setData("OPT_USER",Operator.getID());
       parm.setData("OPT_DATE",SystemTool.getInstance().getDate());
       parm.setData("OPT_TERM",Operator.getIP());
       //��������,��д�������豸
       if(this.getValueString("DEVPRO_CODE")=="B"){   
       //�������豸    
       parm.setData("SETDEV_CODE",getValueString("DEVSET_CODE"));
       //�������豸���� 
       parm.setData("DEVSET_CHN_DESC",getValueString("DEVSET_CHN_DESC"));
       }  
       else{
       //�������豸  
       parm.setData("SETDEV_CODE","");  
       //�������豸���� 
       parm.setData("DEVSET_CHN_DESC",""); 
       }
       parm.setData("DEP_DEADLINE",getValueDouble("DEP_DEADLINE")); 
       parm.setData("FINAN_KIND",getValueString("FINAN_KIND")); 
       parm.setData("FUNDSOU_CODE",getValueString("FUNDSOU_CODE")); 
       //�����ʲ����
       parm.setData("INTANGIBLE_FLG",getValueString("INTANGIBLE_FLG"));
       TParm result = DevTypeTool.getInstance().selectDevBase(devCode); 
       //������ݿ⣨DEV_BASE����ѯ����DEV_CODE�ҽ����ϵ�DEV_CODEΪ��
       if(result.getCount("DEV_CODE")<=0 ){ 
           parm = DevTypeTool.getInstance().insertDevBase(parm);
           if (parm.getErrCode() < 0) {
               messageBox("����ʧ��");
               return;
           }  
           if("B".equals(getValueString("DEVPRO_CODE")) && 
               this.getValueString("DEVSET_CODE").length()==0)
           {
               messageBox("��¼���������豸");  
               return;
           }
           messageBox("����ɹ�");
           //����ɹ���    ��ѯ�豸��Ϣ
           setValue("DEV_CODE",devCode);
           queryDevInf(getValueString("DEV_CODE"));
           onTreeClicked();  
           return;    
       }
    }

    /**
     * ����˫���¼�
     */
    public void onTreeClicked(){
       //����Ƿ�õ����Ľڵ�
       if(((TTree)getComponent("TREE")).getSelectNode() == null)
           return;
       if(((TTree)getComponent("TREE")).getSelectNode().isRoot())
           return;
       queryDevTableInfo(((TTree)getComponent("TREE")).getSelectNode().getID());
    } 
    /**
     * �豸���붯��
     */
    public void onDevCode(){
        if(getValueString("DEV_CODE").length() != 0)  
        	onSave();                   
//        else{
//            queryDevInf(getValueString("DEV_CODE"));
//            queryDevTableInfo(getValueString("DEV_CODE"));
//            ((TComboBox)getComponent("DEVKIND_CODE")).grabFocus();
//        }

    }
    /**
     * �豸���Ͷ���
     */
    public void onDevKindCode(){
        ((TComboBox)getComponent("DEVPRO_CODE")).grabFocus();
    }

    /**
     * �豸���ƶ���
     */
    public void onDevChnDesc(){
        setValue("PY1",TMessage.getPy(getValueString("DEV_CHN_DESC")));
        ((TTextField)getComponent("DEV_ENG_DESC")).grabFocus();
    }
    /**
     * �豸Ӣ�����ƶ���
     */
    public void onDevEngDesc(){
        ((TTextField)getComponent("DEV_ABS_DESC")).grabFocus();
    }
    /**
     * �豸��������
     */
    public void onDevAbsDesc(){
        ((TTextField)getComponent("DESCRIPTION")).grabFocus();
    }
    /**
     * �豸��ע����
     */
    public void onDescription(){
        ((TTextField)getComponent("SPECIFICATION")).grabFocus();
    }
    /**
     * �豸�����
     */
    public void onSpeccification(){
        ((TTextFormat)getComponent("UNIT_CODE")).grabFocus();
    }
    /**
     * �豸��λ����
     */
    public void onUnitCode(){
        ((TComboBox)getComponent("DEPR_METHOD")).grabFocus();
    }
    /**
     * �豸�۾ɷ�������
     */
    public void onDeprMethod(){
        ((TTextFormat)getComponent("MAN_CODE")).grabFocus();
    }

    /**
     * �豸��������
     */
    public void onManCode(){
        ((TTextFormat)getComponent("MAN_NATION")).grabFocus();
    }
    /**
     * �豸����������
     */
    public void onManNation(){
        ((TNumberTextField)getComponent("FUNDSOU_CODE")).grabFocus();
    }
    
    /**
     * �豸�ʽ���ԴFUNDSOU_CODE 
     */ 
    public void onFundsouCode(){
        ((TComboBox)getComponent("BUYWAY_CODE")).grabFocus();
    }
     
    /**
     * �豸����;������
     */
    public void onBuyWayCode(){
        ((TTextFormat)getComponent("DEV_CLASS")).grabFocus();
    }
    /**
     * �豸���ද��
     */
    public void onDevClass(){
        ((TNumberTextField)getComponent("USE_DEADLINE")).grabFocus();
    }
    /**
     * �豸ʹ�����޶��� 
     */
    public void onUseDeadLine(){
        ((TNumberTextField)getComponent("DEP_DEADLINE")).grabFocus();
    }
    /**
     * �豸�۾����޶���
     */
    public void onDepDeadLine(){ 
        ((TTextFormat)getComponent("FINAN_KIND")).grabFocus();
    }
    /**
     * �豸�������FINAN_KIND
     */
    public void onFinanKind(){ 
    	String finanKind = this.getValueString("FINAN_KIND");
    	messageBox("finanKind"+finanKind);
        String sql =  
            " SELECT  DEP_DEADLINE " + 
            " FROM DEV_FINANKIND " + 
            " WHERE FINAN_KIND = '"+finanKind+"'" + 
            " ORDER BY CLASSIFY,FINAN_KIND ";    
       
        TParm parm = new TParm(getDBTool().select(sql));             
        Double depDeadline = Double.parseDouble(parm.getData("DEP_DEADLINE",0).toString());
        this.setValue("DEP_DEADLINE",depDeadline); 
		((TTextField)getComponent("DEVSET_CODE")).grabFocus();
    } 
    /**
     * �������ݿ�������� 
     * @return TJDODBTool 
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
     * �������豸����DEVSET_CODE  
     */   
    public void onDevSetCode(){   
        ((TTextField)getComponent("DEVSET_CHN_DESC")).grabFocus();
    }
    /**
     * �豸�����¼�,Ϊ�����ſ�����д 
     */   
    public void onDevProCode(){    
        ((TTextField)getComponent("DEV_CHN_DESC")).grabFocus();
//        messageBox(""+getValueString("DEVPRO_CODE")); 
//        if("B".equals(getValueString("DEVPRO_CODE"))) {
//        	messageBox("����"); 
//        	((TTextField)getComponent("DEVSET_CODE")).setEnabled(true);
//        	((TTextField)getComponent("DEVSET_CHN_DESC")).setEnabled(false);
//        } 
    } 
    
    /**
     * ������Ƕ���
     */
    public void onMeasureFlg(){
        if(getValue("MEASURE_FLG").equals("N")){
            setValue("MEASURE_ITEMDESC","");
            setValue("MEASURE_FREQ","");
            ((TTextField) getComponent("MEASURE_ITEMDESC")).setEnabled(false);
            ((TNumberTextField) getComponent("MEASURE_FREQ")).setEnabled(false);
        }
        else{
            ((TTextField) getComponent("MEASURE_ITEMDESC")).setEnabled(true);
            ((TNumberTextField) getComponent("MEASURE_FREQ")).setEnabled(true);
        }
        ((TTextField)getComponent("MEASURE_ITEMDESC")).grabFocus();
    }
    /**
     * ������������
     */
    public void onMeasureItemDesc(){
        ((TNumberTextField)getComponent("MEASURE_FREQ")).grabFocus();
    }
    /**
     * ��񵥻��¼�
     */
    public void onTableClick(){
        String devCode = ((TTable)getComponent("TABLE")).getDataStore().getItemString(((TTable)getComponent("TABLE")).getSelectedRow(),0);
        if(devCode == null || devCode.length() == 0)
            return;
        queryDevInf(devCode);   
    }
    /**
     * ��ѯ����  
     * @param devType String
     */    
    public void onQuery(String devCode){ 
    	devCode = this.getValueString("DEV_CODE");
        //������ݿ⣨DEV_BASE����ѯ����DEV_CODE�ҽ����ϵ�DEV_CODE��Ϊ��
    	   String SQL = " SELECT * "+
           " FROM   DEV_BASE "+ 
           " WHERE  DEV_CODE LIKE '"+devCode+"'||'%'"+
           " ORDER BY SEQ";        
        TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
        if(result.getCount("DEV_CODE")<=0 && devCode.length() != 0 ){   
            messageBox("��������");
            return;    
        }  
        //������е���Ϣ(д��TABLE)     
        ((TTable)getComponent("TABLE")).removeRowAll();
        ((TTable)getComponent("TABLE")).setSQL(SQL);
        ((TTable)getComponent("TABLE")).retrieve();  
    }
    
    /**
     * ��ѯ������ݶ���
     * @param devType String
     */
    private void queryDevTableInfo(String devType){
        //������е���Ϣ(д��TABLE)  
        ((TTable)getComponent("TABLE")).removeRowAll();
        String SQL = " SELECT DEV_CODE,DEV_CHN_DESC,ACTIVE_FLG,DEVKIND_CODE,DEVPRO_CODE,"+
                     "        SPECIFICATION"+
                     " FROM   DEV_BASE "+
                     " WHERE  DEV_CODE LIKE '"+devType+"'||'%'"+
                     " ORDER BY SEQ";
        ((TTable)getComponent("TABLE")).setSQL(SQL);
        ((TTable)getComponent("TABLE")).retrieve();
    }
    /**
     * ��ѯ�豸��Ϣ
     * @param devCode String
     */ 
    private void queryDevInf(String devCode){
        TParm result = DevTypeTool.getInstance().selectDevBase(devCode);
        if(result.getErrCode() < 0)
            return; 
        if(result.getCount("DEV_CODE") <= 0) 
            return; 
        setValue("DEV_CODE",result.getValue("DEV_CODE",0));
        //fux modify 20130808 �����豸����DEVTYPE_CODE
        setValue("DEVTYPE_CODE",result.getValue("DEVTYPE_CODE",0));
        setValue("DEVKIND_CODE",result.getValue("DEVKIND_CODE",0));
        setValue("DEVPRO_CODE",result.getValue("DEVPRO_CODE",0));
        setValue("ACTIVE_FLG",result.getValue("ACTIVE_FLG",0));
        setValue("DEV_CHN_DESC",result.getValue("DEV_CHN_DESC",0));
        setValue("PY1",result.getValue("PY1",0));
        setValue("DEV_ENG_DESC",result.getValue("DEV_ENG_DESC",0));
        setValue("DEV_ABS_DESC",result.getValue("DEV_ABS_DESC",0));
        setValue("DESCRIPTION",result.getValue("DESCRIPTION",0));
        setValue("SPECIFICATION",result.getValue("SPECIFICATION",0));
        setValue("UNIT_CODE",result.getValue("UNIT_CODE",0));
        setValue("DEPR_METHOD",result.getValue("DEPR_METHOD",0));
        setValue("MAN_CODE",result.getValue("MAN_CODE",0));
        setValue("MAN_NATION",result.getValue("MAN_NATION",0));
        setValue("BUYWAY_CODE",result.getValue("BUYWAY_CODE",0));
        setValue("USE_DEADLINE",result.getValue("USE_DEADLINE",0));
        setValue("DEV_CLASS",result.getValue("DEV_CLASS",0));
        setValue("SEQMAN_FLG",result.getValue("SEQMAN_FLG",0));
        setValue("MEASURE_FLG",result.getValue("MEASURE_FLG",0));
        setValue("BENEFIT_FLG",result.getValue("BENEFIT_FLG",0));
        setValue("MEASURE_ITEMDESC",result.getValue("MEASURE_ITEMDESC",0));
        setValue("MEASURE_FREQ",result.getValue("MEASURE_FREQ",0));
        if(getValue("MEASURE_FLG").equals("N")){
            setValue("MEASURE_ITEMDESC","");
            setValue("MEASURE_FREQ","");
            ((TTextField) getComponent("MEASURE_ITEMDESC")).setEnabled(false);
            ((TNumberTextField) getComponent("MEASURE_FREQ")).setEnabled(false);
        } 
        else{
            ((TTextField) getComponent("MEASURE_ITEMDESC")).setEnabled(true);
            ((TNumberTextField) getComponent("MEASURE_FREQ")).setEnabled(true);
        }     
        setValue("DEVSET_CODE",result.getValue("SETDEV_CODE",0));
        setValue("DEVSET_CHN_DESC",result.getValue("DEVSET_CHN_DESC",0));
        //�½���������
        setValue("DEP_DEADLINE",result.getValue("DEP_DEADLINE",0));
        setValue("FINAN_KIND",result.getValue("FINAN_KIND",0));
        setValue("FUNDSOU_CODE",result.getValue("FUNDSOU_CODE",0));
        //�����ʲ����
        setValue("INTANGIBLE_FLG",result.getValue("INTANGIBLE_FLG",0));
    }     
    /**
     * ɾ������ 
     */
    public void onDelete(){
        if(getValueString("DEV_CODE").length() == 0)
            return;
        TParm parm = DevTypeTool.getInstance().deleteDevBase(getValueString("DEV_CODE"));
        if(parm.getErrCode() < 0){
            messageBox("ɾ��ʧ��");
            return;
        }
        messageBox("ɾ���ɹ�"); 
        onClear();
        onTreeClicked();
    }
    /**
     * ��ն���
     */
    public void onClear(){
        setValue("DEV_CODE","");
        setValue("DEVKIND_CODE","");
        setValue("DEVPRO_CODE",""); 
        setValue("ACTIVE_FLG","Y");
        setValue("DEV_CHN_DESC","");
        setValue("PY1","");
        setValue("DEV_ENG_DESC","");
        setValue("DEV_ABS_DESC","");
        setValue("DESCRIPTION","");
        setValue("SPECIFICATION","");
        setValue("UNIT_CODE","");
        setValue("DEPR_METHOD","");
        setValue("MAN_CODE","");
        setValue("MAN_NATION","86"); 
        setValue("BUYWAY_CODE","");
        setValue("USE_DEADLINE","");
        setValue("DEV_CLASS","");
        setValue("SEQMAN_FLG","N");
        setValue("MEASURE_FLG","N");
        setValue("BENEFIT_FLG","N");
        setValue("MEASURE_ITEMDESC","");
        setValue("MEASURE_FREQ","");
        setValue("DEVSET_CODE","");  
        setValue("DEVSET_CHN_DESC","");
//        MDEP_PRICE:���۾ɽ��
//        DEP_PRICE:�ۼ��۾ɽ��
//        CURR_PRICE:��ֵ  
//        DEP_DEADLINE:�۾�����
//        FINAN_KIND:�������
//        FUNDSOU_CODE:�ʽ���Դ
        setValue("DEP_DEADLINE","");
        setValue("FINAN_KIND","");
        setValue("FUNDSOU_CODE",""); 
        setValue("INTANGIBLE_FLG","");
        ((TTextField) getComponent("MEASURE_ITEMDESC")).setEnabled(false);
        ((TNumberTextField) getComponent("MEASURE_FREQ")).setEnabled(false);
       ((TTable)getComponent("TABLE")).removeRowAll();
    }
    /**
     * �õ�TTable
     * @param tag String
     * @return TTable 
     */
    public TTable getTTable(String tag){
        return (TTable)getComponent(tag);
     }     
    
    /**
     * �õ��ı��ؼ�
     * @param tagName String
     * @return TTextField
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

}
