package com.javahis.ui.spc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import javax.swing.JLabel;

import jdo.spc.SPCSQL;
import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDSObject;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.JMFRegistry;
import com.javahis.device.JMStudio;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.StringUtil;


/**
 * <p>Title:ҩƷ���û����� </p>
 *
 * <p>Description: </p>
 *  
 * <p>Copyright: JAVAHIS 1.0</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 * @version 1.0 
 */
public class SYSFee_PhaControl
    extends TControl {

    /**
     * �����������ֵ������иı���SYS_FEE�������µ�һ������
     */
    private double oldOwnPrice = 0.0;
    private double oldNhiPrice = 0.0;
    private double oldGovPrice = 0.0;
    private String orderCode = "";
    //$$ add by lx ����ѯ��SQL
	private String querySQL="";
    

    /**
     * �����
     */
    private TDSObject bigObject = new TDSObject();

    private TDS phaTransUnitDS = new TDS();
    private TDS phaBaseDS = new TDS();
    /**
     * SYS_FEE���TDS
     */
    TDS sysFeeDS = new TDS();

    //private TDS indStockMDS = new TDS();
    /**
     * SYS_FEE_HISTORY���TDS
     */
    TDS sysFeeHisDS = new TDS();

    /**
     * ����
     */
    private TTreeNode treeRoot;
    /**
     * ��Ź�����𹤾�
     */
    private SYSRuleTool ruleTool;
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
    private TDataStore treeDataStore = new TDataStore();

    /**
     * ���ǰѡ�е��к�
     */
    int selRow = -1;
    /**
     * ���пؼ�������
     */
    private String controlNameTab1 =
        "LET_KEYIN_FLG;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;" +
        "HRM_FIT_FLG;DR_ORDER_FLG;TRANS_OUT_FLG;EXEC_ORDER_FLG;START_DATE;" +
        "END_DATE;ORDER_DESC;PY1;NHI_FEE_DESC;" +
        "TRADE_ENG_DESC;GOODS_DESC;GOODS_PYCODE;ALIAS_DESC;SPECIFICATION;ALIAS_PYCODE;DESCRIPTION;" +
        "HYGIENE_TRADE_CODE;NHI_CODE_I;NHI_CODE_O;NHI_CODE_E;HABITAT_TYPE;" +
        "MAN_CODE;UNIT_CODE;MR_CODE;CHARGE_HOSP_CODE;ORDER_CAT1_CODE;LCS_CLASS_CODE;" +
        "EXEC_DEPT_CODE;INSPAY_TYPE;OWN_PRICE;OWN_PRICE2;" +
        "OWN_PRICE3;ADDPAY_RATE;ADDPAY_AMT;ORD_SUPERVISION";//shibl 20121127 add

    private String controlNameTab2 =
        "MEDI_QTY1;DOSAGE_QTY1;DOSAGE_QTY;STOCK_QTY;STOCK_QTY1;PURCH_QTY;" +
        "MEDI_UNIT;DOSAGE_UNIT1;DOSAGE_UNIT;STOCK_UNIT;STOCK_UNIT1;PURCH_UNIT;PACK_UNIT;" +
        "TYPE_CODE;HALF_USE_FLG;REFUND_FLG;" +
        //============pangben modify 20110428 ���SKINTEST_FLG��Ƥ�ԣ��ֶ�PRESCRIPTION_FLG���������ֶ�
        "DSPNSTOTDOSE_FLG;GIVEBOX_FLG;SKINTEST_FLG;PRESCRIPTION_FLG;REUSE_FLG;ODD_FLG;UDCARRY_FLG;CTRLDRUGCLASS_CODE;" +
        "ANTIBIOTIC_CODE;SUP_CODE;DOSE_CODE;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;" +
        //=========pangben modify 20110817 ���SYS_GRUG_CLASS(ҩ������)�ֶ� NOADDTION_FLG (����)�ֶ�
        "MEDI_QTY;DEFAULT_TOTQTY;ATC_FLG;IS_REMARK,ATC_FLG_I"+";SYS_GRUG_CLASS;NOADDTION_FLG;OPD_ATC_ORG;ODI_ATC_ORG;" +
        //��DRUG_NOTES_PATIENT��������ҩע����� DRUG_NOTES_DR��ҽʦ��ҩע����� DRUG_NOTE��ҩ��˵���� DRUG_FORM������˵����
        //DDD 
        "DRUG_NOTES_PATIENT;DRUG_NOTES_DR;DRUG_NOTE;DRUG_FORM;DDD";//shibl 20121127 add


    /**
     * ����Ŀؼ�
     */
    //��
    TTree tree;
    //����
    TTable upTable;

    //ȫ��/����/ͣ�ñ��
    TRadioButton ALL;
    TRadioButton ACTIVE_Y;
    TRadioButton ACTIVE_N;


    /**
     * ********************��һ����ǩҳ�Ŀؼ�*************************************
     */
    //����ע��
    TCheckBox ACTIVE_FLG;
    //�����ֶ��Ǽ�ע��
    TCheckBox LET_KEYIN_FLG;
    //��,��,ס,��,��ҽ���ñ��
    TCheckBox OPD_FIT_FLG;
    TCheckBox EMG_FIT_FLG;
    TCheckBox IPD_FIT_FLG;
    TCheckBox HRM_FIT_FLG;
    TCheckBox DR_ORDER_FLG;
    //ת��ע��
    TCheckBox TRANS_OUT_FLG;
    TCheckBox EXEC_ORDER_FLG;

    TTextFormat START_DATE;
    TTextFormat END_DATE;
    TTextFormat ORD_SUPERVISION;

    TTextField QUERY;
    TTextField ORDER_CODE;
    TTextField ORDER_DESC;
    TTextField PY1;
    TTextField NHI_FEE_DESC;
    TTextField TRADE_ENG_DESC;
    TTextField GOODS_DESC;
    TTextField GOODS_PYCODE;
    TTextField ALIAS_DESC;
    TTextField SPECIFICATION;
    TTextField ALIAS_PYCODE;
    TTextField DESCRIPTION;
    TTextField HYGIENE_TRADE_CODE;
    TTextField NHI_CODE_I;
    TTextField NHI_CODE_O;
    TTextField NHI_CODE_E;
    
    TComboBox HABITAT_TYPE;
    //===zhangp 20120706 start
//    TTextFormat MAN_CODE;
    TTextArea MAN_CODE;
    //===zhangp 20120706 end
    TTextFormat UNIT_CODE;
    TTextFormat MR_CODE;
    TTextFormat CHARGE_HOSP_CODE;
    TTextFormat ORDER_CAT1_CODE;
    TComboBox LCS_CLASS_CODE;
    TComboBox TRANS_HOSP_CODE;
    TTextFormat EXEC_DEPT_CODE;
    TComboBox INSPAY_TYPE;

    TNumberTextField OWN_PRICE;
    TNumberTextField NHI_PRICE;
    TNumberTextField GOV_PRICE;
    TNumberTextField ADDPAY_RATE;
    TNumberTextField ADDPAY_AMT;

    /**
     * ********************�ڶ�����ǩҳ�Ŀؼ�*************************************
     */
    //���浽PHA_TRANSUNIT������
    TTextField DRUG_NOTES_PATIENT;
    TTextField DRUG_NOTES_DR;
    TTextField DRUG_NOTE;
    TTextField DRUG_FORM;
    TTextField DDD;

    TNumberTextField MEDI_QTY1;
    TNumberTextField DOSAGE_QTY1;
    TNumberTextField DOSAGE_QTY;
    TNumberTextField STOCK_QTY;
    TNumberTextField STOCK_QTY1;
    TNumberTextField PURCH_QTY;
    TTextFormat MEDI_UNIT;
    TTextFormat DOSAGE_UNIT1;
    TTextFormat DOSAGE_UNIT;
    TTextFormat STOCK_UNIT;
    TTextFormat STOCK_UNIT1;
    TTextFormat PURCH_UNIT;
    TTextField PACK_UNIT;
    TComboBox copyMEDI_UNIT;
    TComboBox copyDOSAGE_UNIT1;
    
    TTextFormat OPD_ATC_ORG;
    TTextFormat ODI_ATC_ORG;

    //���浽PHA_BASE������
    TRadioButton PHA_TYPE1;
    TRadioButton PHA_TYPE2;
    TRadioButton PHA_TYPE3;

    TComboBox TYPE_CODE;

    TCheckBox HALF_USE_FLG;
    TCheckBox REFUND_FLG;
    /*
     * ҩƷ����ʱ,Ĭ������ֻ��,��ѡ��������ҩʱ.��Ĭ������
     * ��ʼ��ʱ,��ѡ��ҩƷ��������ҩʱ,��Ĭ������,��������Ĭ������ֻ��
     * Modify ZhenQin 2011-06-01
     */
    TCheckBox DSPNSTOTDOSE_FLG;
    TCheckBox GIVEBOX_FLG;
    TCheckBox SKINTEST_FLG;//=======pangben modify 20110428 Ƥ��
    TCheckBox PRESCRIPTION_FLG;//=======pangben modify 20110428 ����
    TCheckBox NOADDTION_FLG;//=======pangben modify 20110817 ����
    TCheckBox REUSE_FLG;
    TCheckBox ODD_FLG;
    TCheckBox UDCARRY_FLG;

    TComboBox CTRLDRUGCLASS_CODE;
    TComboBox ANTIBIOTIC_CODE;
    TTextFormat SUP_CODE;
    TTextFormat DOSE_CODE;
    TTextFormat FREQ_CODE;
    TTextFormat ROUTE_CODE;
    TTextFormat SYS_GRUG_CLASS;//=======pangben modify 20110817 ҩ������

    TNumberTextField TAKE_DAYS;
    TNumberTextField MEDI_QTY;
    TNumberTextField DEFAULT_TOTQTY;

    //�ⲿ���ñ��
    boolean outShow = false;
    
    // �ж�onNew����ִ�����
    boolean isOnNew = false;
    
    // ���ã�ͣ��
    String isActive = "";

    /**
     * ��ʼ��
     */
    public SYSFee_PhaControl() {
    }

    /**
     * ��ʼ��
     */
    public void onInit() { //��ʼ������
        super.onInit();
        myInitControler();

        //��ʼ����
        onInitTree();
        //��tree��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        //��ʼ�����
        onInitNode();
        //���������TParm���͵ľ����ⲿ����
        if (this.getParameter() instanceof TParm)
            initParmFromOutside();

        canSave();

        //========pangben modify 20110426 start Ȩ�����
        //��ʼ��Ժ��
        setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110426 stop
        // �ж��Ƿ������������Ƿ�disable��λ����λת���� <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        isActive = ACTIVE_FLG.getValue();
        // ---------->

    }


    private void canSave(){
        Object obj = this.getParameter();
        if(obj==null)
            return;
        if(obj.equals("QUERY")){
        	callFunction("UI|save|setEnabled", false);
        	callFunction("UI|PHOTO|setEnabled", false);
        	callFunction("UI|regist|setEnabled", false);
        }
    }

    /**
     * ��ʼ���������caseNo/stationCode�����ⲿ����������洫���Ĳ�����
     */
    public void initParmFromOutside() {

        //�Ӳ�����������õ�����TParm
        TParm outsideParm = (TParm)this.getParameter();
        if (outsideParm != null) {
            //������Ų�ѯ��caseNo
            String ordcode = outsideParm.getData("ORDER_CODE").toString();
            //��������ѯ��stationCode
            String flg = outsideParm.getData("FLG").toString();
            //�ⲿ������ס���пؼ�
            setEnabledForCtl(false);
            showByOutside(ordcode, flg);
        }
        return;
    }

    /**
     *
     * @param orderByout String
     * @param fromFlg String
     */
    public void showByOutside(String orderByout, String fromFlg) {
        //�ж��Ƿ����ⲿ�ӿڵ���
        if (fromFlg.equals("OPD")) {
            orderCode = orderByout;
            ORDER_CODE.setValue(orderCode);
            outShow = true;
        }
        onQuery();

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
        treeRoot.setText("ҩƷ����");
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
        //��dataStore��ֵ
        treeDataStore.setSQL(
            "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='PHA_RULE'");
        //�����dataStore���õ�������С��0
        if (treeDataStore.retrieve() <= 0)
            return;
        //��������,�Ǳ�������еĿ�������
        ruleTool = new SYSRuleTool("PHA_RULE");
        if (ruleTool.isLoad()) { //�����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "CATEGORY_CODE",
                "CATEGORY_CHN_DESC", "Path", "SEQ");
            //ѭ����������ڵ�
            for (int i = 0; i < node.length; i++)
                treeRoot.addSeq(node[i]);
        }
        //�õ������ϵ�������
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
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
        //���
        onClear();

        //������ť������
        callFunction("UI|new|setEnabled", false);
        //�õ�������Ľڵ����
        //TTreeNode node = (TTreeNode) parm;
        TTreeNode node = tree.getSelectNode();
        if (node == null)
            return;
        //�õ�table����
        TTable table = (TTable)this.callFunction("UI|upTable|getThis");
        //table�������иı�ֵ
        table.acceptText();
        //�жϵ�����Ƿ������ĸ����
        if (node.getType().equals("Root")) {
            //��������ĸ��ӵ�table�ϲ���ʾ����
            table.removeRowAll();
        }
        else { //�����Ĳ��Ǹ����
            //�õ���ǰѡ�еĽڵ��idֵ
            String id = node.getID();
            //�õ���ѯTDS��SQL��䣨ͨ����һ���IDȥlike���е�orderCode��
            String sql = getSQL(id,Operator.getRegion());//======pangben modify 20110426 ��Ӳ���
          //$$===add by lx 2013/01/06====$$//
			querySQL=sql;
            //��ʼ��table��TDS
            initTblAndTDS(sql);

        }
        //��table���ݼ���������
//        table.setSort("ORDER_CODE");
        //table��������¸�ֵ
//        table.sort();
        //�õ���ǰ�������ID
        String nowID = node.getID();

        int classify = 1;
        if (nowID.length() > 0)
            classify = ruleTool.getNumberClass(nowID) + 1;
        //�������С�ڵ�,��������һ��(ʹ������ť����)&&�����ⲿ����
        if (classify > ruleTool.getClassifyCurrent() && !outShow) {
            this.callFunction("UI|new|setEnabled", true);
        }

    }

    /**
     * �õ���ʼ��TDS��SQL���(����Ŀǰ�������õ���Ŀ�б�����START_DATE/END_DATE ֻ��ACTIVE_FLGΪ��Y��)
     * @return String
     * =========pangben modify 20110426 ����������
     */
    private String getSQL(String orderCode,String regionCode) {
        String active = "";

        if (ACTIVE_Y.isSelected()) { //����
            active = " AND ACTIVE_FLG='Y'";
            setEnabledForCtl(true);
        }
        else if (ACTIVE_N.isSelected()) { //ͣ��
            active = " AND ACTIVE_FLG='N'";
            setEnabledForCtl(false);
        }
        else { //ȫ��
            setEnabledForCtl(false);
        }
        //======pangben modify 20110426 start
        String region = "";
        if (null != regionCode && !"".equals(regionCode))
            region = " AND (REGION_CODE='" + regionCode + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        //======pangben modify 20110426 stop
        String sql = "";
        //���������
        if (orderCode != null && orderCode.length() > 0)
            sql = " SELECT * " +
            	" FROM SYS_FEE_HISTORY" +
            	" WHERE " +
                " ORDER_CODE like '" + orderCode + "%'" +
                active +region+
                " ORDER BY ORDER_CODE";

        return sql;
    }

    /**
     * ��ʼ������ı�������еĿؼ�����
     * @param sql String
     */
    public void initTblAndTDS(String sql) {
        sysFeeHisDS.setSQL(sql);
        sysFeeHisDS.retrieve();
        //���û��������ձ���ϵ�����
        if (sysFeeHisDS.rowCount() <= 0) {
            upTable.removeRowAll();
        }
        sysFeeHisDS.showDebug();
        
        upTable.setDataStore(sysFeeHisDS);
        //modify by liming  2012/02/18 begin
        upTable.setDSValue();
      //modify by liming end
       

    }

    /**
     * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼�
     * ����
     */
    public void myInitControler() {

        tree = (TTree) callFunction("UI|TREE|getThis");

        //�õ�table�ؼ�
        upTable = (TTable)this.getComponent("upTable");

        ALL = (TRadioButton)this.getComponent("ALL");
        ACTIVE_Y = (TRadioButton)this.getComponent("ACTIVE_Y");
        ACTIVE_N = (TRadioButton)this.getComponent("ACTIVE_N");

        ACTIVE_FLG = (TCheckBox)this.getComponent("ACTIVE_FLG");
        LET_KEYIN_FLG = (TCheckBox)this.getComponent("LET_KEYIN_FLG");
        OPD_FIT_FLG = (TCheckBox)this.getComponent("OPD_FIT_FLG");
        EMG_FIT_FLG = (TCheckBox)this.getComponent("EMG_FIT_FLG");
        IPD_FIT_FLG = (TCheckBox)this.getComponent("IPD_FIT_FLG");
        HRM_FIT_FLG = (TCheckBox)this.getComponent("HRM_FIT_FLG");
        DR_ORDER_FLG = (TCheckBox)this.getComponent("DR_ORDER_FLG");
        TRANS_OUT_FLG = (TCheckBox)this.getComponent("TRANS_OUT_FLG");
        EXEC_ORDER_FLG = (TCheckBox)this.getComponent("EXEC_ORDER_FLG");

        START_DATE = (TTextFormat)this.getComponent("START_DATE");
        END_DATE = (TTextFormat)this.getComponent("END_DATE");
        ORD_SUPERVISION=(TTextFormat)this.getComponent("ORD_SUPERVISION");
        
        QUERY= (TTextField)this.getComponent("QUERY");
        ORDER_CODE = (TTextField)this.getComponent("ORDER_CODE");
        ORDER_DESC = (TTextField)this.getComponent("ORDER_DESC");
        PY1 = (TTextField)this.getComponent("PY1");
        NHI_FEE_DESC = (TTextField)this.getComponent("NHI_FEE_DESC");
        TRADE_ENG_DESC = (TTextField)this.getComponent("TRADE_ENG_DESC");

        GOODS_DESC = (TTextField)this.getComponent("GOODS_DESC");
        GOODS_PYCODE = (TTextField)this.getComponent("GOODS_PYCODE");

        ALIAS_DESC = (TTextField)this.getComponent("ALIAS_DESC");
        SPECIFICATION = (TTextField)this.getComponent("SPECIFICATION");
        ALIAS_PYCODE = (TTextField)this.getComponent("ALIAS_PYCODE");
        DESCRIPTION = (TTextField)this.getComponent("DESCRIPTION");
        HYGIENE_TRADE_CODE = (TTextField)this.getComponent("HYGIENE_TRADE_CODE");

        NHI_CODE_I = (TTextField)this.getComponent("NHI_CODE_I");
        NHI_CODE_O = (TTextField)this.getComponent("NHI_CODE_O");
        NHI_CODE_E = (TTextField)this.getComponent("NHI_CODE_E");

        HABITAT_TYPE = (TComboBox)this.getComponent("HABITAT_TYPE");
        //===zhangp 20120706 start
//        MAN_CODE = (TTextFormat)this.getComponent("MAN_CODE");
        MAN_CODE = (TTextArea)this.getComponent("MAN_CODE");
        //===zhangp 20120706 end
        UNIT_CODE = (TTextFormat)this.getComponent("UNIT_CODE");
        MR_CODE= (TTextFormat)this.getComponent("MR_CODE");
        CHARGE_HOSP_CODE = (TTextFormat)this.getComponent("CHARGE_HOSP_CODE");
        ORDER_CAT1_CODE = (TTextFormat)this.getComponent("ORDER_CAT1_CODE");
        
        LCS_CLASS_CODE = (TComboBox)this.getComponent("LCS_CLASS_CODE");
        TRANS_HOSP_CODE = (TComboBox)this.getComponent("TRANS_HOSP_CODE");
        EXEC_DEPT_CODE = (TTextFormat)this.getComponent("EXEC_DEPT_CODE");
        INSPAY_TYPE = (TComboBox)this.getComponent("INSPAY_TYPE");

        NHI_PRICE = (TNumberTextField)this.getComponent("OWN_PRICE2");
        GOV_PRICE = (TNumberTextField)this.getComponent("OWN_PRICE3");
        OWN_PRICE = (TNumberTextField)this.getComponent("OWN_PRICE");
        ADDPAY_RATE = (TNumberTextField)this.getComponent("ADDPAY_RATE");
        ADDPAY_AMT = (TNumberTextField)this.getComponent("ADDPAY_AMT");

        //�ڶ�����ǩҳ
        MEDI_QTY1 = (TNumberTextField)this.getComponent("MEDI_QTY1");
        DOSAGE_QTY1 = (TNumberTextField)this.getComponent("DOSAGE_QTY1");
        DOSAGE_QTY = (TNumberTextField)this.getComponent("DOSAGE_QTY");
        STOCK_QTY = (TNumberTextField)this.getComponent("STOCK_QTY");
        STOCK_QTY1 = (TNumberTextField)this.getComponent("STOCK_QTY1");
        PURCH_QTY = (TNumberTextField)this.getComponent("PURCH_QTY");

        MEDI_UNIT = (TTextFormat)this.getComponent("MEDI_UNIT"); ;
        DOSAGE_UNIT1 = (TTextFormat)this.getComponent("DOSAGE_UNIT1"); ;
        DOSAGE_UNIT = (TTextFormat)this.getComponent("DOSAGE_UNIT"); ;
        STOCK_UNIT = (TTextFormat)this.getComponent("STOCK_UNIT"); ;
        STOCK_UNIT1 = (TTextFormat)this.getComponent("STOCK_UNIT1"); ;
        PURCH_UNIT = (TTextFormat)this.getComponent("PURCH_UNIT"); ;
        PACK_UNIT = (TTextField)this.getComponent("PACK_UNIT"); ;

        PHA_TYPE1 = (TRadioButton)this.getComponent("PHA_TYPE1");
        PHA_TYPE2 = (TRadioButton)this.getComponent("PHA_TYPE2");
        PHA_TYPE3 = (TRadioButton)this.getComponent("PHA_TYPE3");

        TYPE_CODE = (TComboBox)this.getComponent("TYPE_CODE");

        HALF_USE_FLG = (TCheckBox)this.getComponent("HALF_USE_FLG");
        REFUND_FLG = (TCheckBox)this.getComponent("REFUND_FLG");
        DSPNSTOTDOSE_FLG = (TCheckBox)this.getComponent("DSPNSTOTDOSE_FLG");
        GIVEBOX_FLG = (TCheckBox)this.getComponent("GIVEBOX_FLG");
        //========pangben modify 20110428 start Ƥ���ֶ�
        SKINTEST_FLG = (TCheckBox)this.getComponent("SKINTEST_FLG");
        //�����ֶ�
        PRESCRIPTION_FLG = (TCheckBox)this.getComponent("PRESCRIPTION_FLG");
        //����
        NOADDTION_FLG=(TCheckBox)this.getComponent("NOADDTION_FLG");
        //========pangben modify 20110428 stop
        REUSE_FLG = (TCheckBox)this.getComponent("REUSE_FLG");
        ODD_FLG = (TCheckBox)this.getComponent("ODD_FLG");
        UDCARRY_FLG = (TCheckBox)this.getComponent("UDCARRY_FLG");

        CTRLDRUGCLASS_CODE = (TComboBox)this.getComponent("CTRLDRUGCLASS_CODE");
        ANTIBIOTIC_CODE = (TComboBox)this.getComponent("ANTIBIOTIC_CODE");
        SUP_CODE = (TTextFormat)this.getComponent("SUP_CODE");
        DOSE_CODE = (TTextFormat)this.getComponent("DOSE_CODE");
        FREQ_CODE = (TTextFormat)this.getComponent("FREQ_CODE");
        ROUTE_CODE = (TTextFormat)this.getComponent("ROUTE_CODE");
        //========pangben modify 20110817 start ҩ������
        SYS_GRUG_CLASS=(TTextFormat)this.getComponent("SYS_GRUG_CLASS");
        //========pangben modify 20110817 stop
        TAKE_DAYS = (TNumberTextField)this.getComponent("TAKE_DAYS");
        MEDI_QTY = (TNumberTextField)this.getComponent("MEDI_QTY");
        DEFAULT_TOTQTY = (TNumberTextField)this.getComponent("DEFAULT_TOTQTY");
        
        OPD_ATC_ORG = (TTextFormat)this.getComponent("OPD_ATC_ORG");
        ODI_ATC_ORG = (TTextFormat)this.getComponent("ODI_ATC_ORG");

        copyMEDI_UNIT = (TComboBox)this.getComponent("copyMEDI_UNIT"); ;
        copyDOSAGE_UNIT1 = (TComboBox)this.getComponent("copyDOSAGE_UNIT1"); ;
        
        DRUG_NOTES_PATIENT = (TTextField)this.getComponent("DRUG_NOTES_PATIENT	");
        DRUG_NOTES_DR = (TTextField)this.getComponent("DRUG_NOTES_DR");
        DRUG_NOTE = (TTextField)this.getComponent("DRUG_NOTE");
        DRUG_FORM = (TTextField)this.getComponent("DRUG_FORM");
        this.DDD=(TTextField)this.getComponent("DDD");
        
        

        //������tableע�ᵥ���¼�����
        this.callFunction("UI|upTable|addEventListener",
                          "upTable->" + TTableEvent.CLICKED, this,
                          "onUpTableClicked");
        TParm parm =new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        QUERY.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        QUERY.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        //������ť������
        callFunction("UI|new|setEnabled", false);
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
            ORDER_CODE.setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            ORDER_DESC.setValue(order_desc);
        //��ղ�ѯ�ؼ�
        QUERY.setValue("");
        onQuery();
    }


    /**
     * ���������table�¼�
     */
    public void onUpTableClicked(int row) {

        //��ǰѡ�е��к�
        selRow = row;
        //�������ؼ���ֵ
        clearCtl();
        //�õ����parm
        TParm tableDate = ( (TDS) upTable.getDataStore()).getBuffer(TDS.
            PRIMARY).getRow(selRow);
        //�����еĿؼ�ֵ
        //setValueForDownCtl(tableDate, row);
        setValueForDownCtl(tableDate);
        //������ʾ��ȫ������ʱ��Ĭ�ϲ��ɱ༭(����״̬�ָ��༭/ͣ��״̬���ɱ༭)
        if (ALL.isSelected()) {
            boolean activeFlg = ACTIVE_FLG.isSelected();
            setEnabledForCtl(activeFlg);
        }

        //��¼��������������SYSFEE���ݵĹؼ�����(��������ʱ��orderCode���䣬ֻ�������۸���˵��Ҫ���£�����)
        oldOwnPrice = TypeTool.getDouble(OWN_PRICE.getText());
        oldNhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
        oldGovPrice = TypeTool.getDouble(GOV_PRICE.getText());
        orderCode = ORDER_CODE.getValue();

        //��ѯPHA_TRANSUNIT����TDS
        qeuryPhaTransUnit(orderCode);
        qeuryPhaBase(orderCode);

        //��ʼ����ǩ2
        onTabPanel2();
        this.viewPhoto(orderCode);
    }

    /**
     * ��ʼ��PHA_TRANSUNIT��TDS
     * @param orderCode String
     */
    public void qeuryPhaTransUnit(String orderCode) {
        String sql = "SELECT * FROM PHA_TRANSUNIT WHERE ORDER_CODE = '" +
            orderCode + "'";
        phaTransUnitDS.setSQL(sql);
        phaTransUnitDS.retrieve();

    }

    /**
     * ��ʼ��PHA_BASE��TDS
     * @param orderCode String
     *
     */
    public void qeuryPhaBase(String orderCode) {
        String sql = "SELECT * FROM PHA_BASE WHERE ORDER_CODE = '" + orderCode +
            "'";
        phaBaseDS.setSQL(sql);
        phaBaseDS.retrieve();

    }

    /**
     * ��ʼ����2����ǩҳ
     */
    public void onTabPanel2() {

        TParm phaTransUnit = phaTransUnitDS.getBuffer(TDS.PRIMARY);
        double mediQty = phaTransUnit.getDouble("MEDI_QTY", 0);
        double dosageQty = phaTransUnit.getDouble("DOSAGE_QTY", 0);
        double stockQty = phaTransUnit.getDouble("STOCK_QTY", 0);
        double purchQty = phaTransUnit.getDouble("PURCH_QTY", 0);
        String mediUnit = phaTransUnit.getValue("MEDI_UNIT", 0);
        String dosageUnit = phaTransUnit.getValue("DOSAGE_UNIT", 0);
        String stockUnit = phaTransUnit.getValue("STOCK_UNIT", 0);
        String purchUnit = phaTransUnit.getValue("PURCH_UNIT", 0);
      //  String packUnit = phaTransUnit.getValue("PACK_UNIT", 0);

        MEDI_QTY1.setValue(mediQty);
        DOSAGE_QTY.setValue(dosageQty);
        DOSAGE_QTY1.setValue(1);
        STOCK_QTY.setValue(1);
        STOCK_QTY1.setValue(stockQty);
        PURCH_QTY.setValue(purchQty);
        MEDI_UNIT.setValue(mediUnit);
        DOSAGE_UNIT1.setValue(dosageUnit);
        DOSAGE_UNIT.setValue(dosageUnit);
        STOCK_UNIT.setValue(stockUnit);
        STOCK_UNIT1.setValue(stockUnit);
        PURCH_UNIT.setValue(purchUnit);
      //  PACK_UNIT.setValue(packUnit);

        TParm phaBase = phaBaseDS.getBuffer(TDS.PRIMARY);
        String phaType = phaBase.getValue("PHA_TYPE", 0);
        PHA_TYPE1.setSelected("W".equals(phaType));
        PHA_TYPE2.setSelected("C".equals(phaType));
        PHA_TYPE3.setSelected("G".equals(phaType));
        TYPE_CODE.setValue(phaBase.getValue("TYPE_CODE", 0));

        HALF_USE_FLG.setSelected(phaBase.getBoolean("HALF_USE_FLG", 0));
        REFUND_FLG.setSelected(phaBase.getBoolean("REFUND_FLG", 0));
        DSPNSTOTDOSE_FLG.setSelected(phaBase.getBoolean("DSPNSTOTDOSE_FLG", 0));
        GIVEBOX_FLG.setSelected(phaBase.getBoolean("GIVEBOX_FLG", 0));
        REUSE_FLG.setSelected(phaBase.getBoolean("REUSE_FLG", 0));
        ODD_FLG.setSelected(phaBase.getBoolean("ODD_FLG", 0));
        UDCARRY_FLG.setSelected(phaBase.getBoolean("UDCARRY_FLG", 0));
        //=======pangben modify 20110428 start Ƥ��combo
        SKINTEST_FLG.setSelected(phaBase.getBoolean("SKINTEST_FLG", 0));
        // ����combo
        PRESCRIPTION_FLG.setSelected(phaBase.getBoolean("PRESCRIPTION_FLG", 0));
        //=======pangben modify 20110428 stop
        CTRLDRUGCLASS_CODE.setValue(phaBase.getValue("CTRLDRUGCLASS_CODE", 0));
        ANTIBIOTIC_CODE.setValue(phaBase.getValue("ANTIBIOTIC_CODE", 0));
        SUP_CODE.setValue(phaBase.getValue("SUP_CODE", 0));
        OPD_ATC_ORG.setValue(phaBase.getValue("OPD_ATC_ORG", 0));
        ODI_ATC_ORG.setValue(phaBase.getValue("ODI_ATC_ORG", 0));
        PACK_UNIT.setValue(phaBase.getValue("PACK_UNIT",0));			   
        DOSE_CODE.setValue(phaBase.getValue("DOSE_CODE", 0));
        TAKE_DAYS.setValue(phaBase.getValue("TAKE_DAYS", 0));
        FREQ_CODE.setValue(phaBase.getValue("FREQ_CODE", 0));
        MEDI_QTY.setValue(phaBase.getValue("MEDI_QTY", 0));
        copyMEDI_UNIT.setValue(mediUnit);
        copyDOSAGE_UNIT1.setValue(dosageUnit);
        ROUTE_CODE.setValue(phaBase.getValue("ROUTE_CODE", 0));
        DEFAULT_TOTQTY.setValue(phaBase.getValue("DEFAULT_TOTQTY", 0));

        //ֻ��������ҩѡ��,���Ĭ������ Modify ZhenQin 2011-06-01
        DEFAULT_TOTQTY.setEnabled(DSPNSTOTDOSE_FLG.isSelected());
        this.DRUG_NOTES_PATIENT.setValue(phaBase.getValue("DRUG_NOTES_PATIENT", 0));
        this.DRUG_NOTES_DR.setValue(phaBase.getValue("DRUG_NOTES_DR", 0));
        this.DRUG_NOTE.setValue(phaBase.getValue("DRUG_NOTE", 0));
        this.DRUG_FORM.setValue(phaBase.getValue("DRUG_FORM", 0));
        this.DDD.setValue(phaBase.getValue("DDD", 0));

    }

    public void getTabPanel2Vaule() {

        //��ǰ���ݿ�ʱ��
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        int phaTransUnitRow = 0;
        phaTransUnitDS.showDebug();
        if (phaTransUnitDS.rowCount() == 0) {
            phaTransUnitRow = phaTransUnitDS.insertRow();
        }
        //���������
        phaTransUnitDS.setItem(phaTransUnitRow, "OPT_USER", Operator.getID());
        phaTransUnitDS.setItem(phaTransUnitRow, "OPT_DATE", date);
        phaTransUnitDS.setItem(phaTransUnitRow, "OPT_TERM", Operator.getIP());
        phaTransUnitDS.setItem(phaTransUnitRow, "ORDER_CODE",
                               ORDER_CODE.getValue());
        phaTransUnitDS.setItem(phaTransUnitRow, "MEDI_QTY", MEDI_QTY1.getText());
        phaTransUnitDS.setItem(phaTransUnitRow, "DOSAGE_QTY",
                               DOSAGE_QTY.getText());
        phaTransUnitDS.setItem(phaTransUnitRow, "STOCK_QTY", STOCK_QTY1.getText());
        phaTransUnitDS.setItem(phaTransUnitRow, "PURCH_QTY", PURCH_QTY.getText());
        phaTransUnitDS.setItem(phaTransUnitRow, "MEDI_UNIT", MEDI_UNIT.getValue());
        phaTransUnitDS.setItem(phaTransUnitRow, "DOSAGE_UNIT",
                               DOSAGE_UNIT.getValue());
        phaTransUnitDS.setItem(phaTransUnitRow, "STOCK_UNIT",
                               STOCK_UNIT.getValue());
        phaTransUnitDS.setItem(phaTransUnitRow, "PURCH_UNIT",
                               PURCH_UNIT.getValue());
        //============pangben modify 20110427 start
        phaTransUnitDS.setItem(phaTransUnitRow, "REGION_CODE",
                               this.getValueString("REGION_CODE"));
         //============pangben modify 20110427 stop
        phaTransUnitDS.showDebug();

        int phaBaseRow = 0;
        if (phaBaseDS.rowCount() == 0) {
            phaBaseRow = phaBaseDS.insertRow();
        }

        phaBaseDS.setItem(phaBaseRow, "OPT_USER", Operator.getID());
        phaBaseDS.setItem(phaBaseRow, "OPT_DATE", date);
        phaBaseDS.setItem(phaBaseRow, "OPT_TERM", Operator.getIP());
        phaBaseDS.setItem(phaBaseRow, "ORDER_CODE", ORDER_CODE.getValue());
        String phaType = "";
        if (PHA_TYPE1.isSelected()) {
            phaType = "W";
        }
        else if (PHA_TYPE2.isSelected()) {
            phaType = "C";
        }
        else {
            phaType = "G";
        }
        phaBaseDS.setItem(phaBaseRow, "ALIAS_DESC", ALIAS_DESC.getText());
        phaBaseDS.setItem(phaBaseRow, "ORDER_DESC", ORDER_DESC.getText());
        phaBaseDS.setItem(phaBaseRow, "GOODS_DESC", GOODS_DESC.getText());
        phaBaseDS.setItem(phaBaseRow, "SPECIFICATION", SPECIFICATION.getText());
        phaBaseDS.setItem(phaBaseRow, "DEFAULT_TOTQTY", DEFAULT_TOTQTY.getText());

        phaBaseDS.setItem(phaBaseRow, "DOSAGE_UNIT", DOSAGE_UNIT.getValue());
        phaBaseDS.setItem(phaBaseRow, "STOCK_UNIT", STOCK_UNIT.getValue());
        phaBaseDS.setItem(phaBaseRow, "PURCH_UNIT", PURCH_UNIT.getValue());

        phaBaseDS.setItem(phaBaseRow, "PACK_UNIT", PACK_UNIT.getValue());
        phaBaseDS.setItem(phaBaseRow, "PHA_TYPE", phaType);
        phaBaseDS.setItem(phaBaseRow, "TYPE_CODE", TYPE_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "HALF_USE_FLG", HALF_USE_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "REFUND_FLG", REFUND_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "DSPNSTOTDOSE_FLG",
                          DSPNSTOTDOSE_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "GIVEBOX_FLG", GIVEBOX_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "REUSE_FLG", REUSE_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "ODD_FLG", ODD_FLG.getValue());
        phaBaseDS.setItem(phaBaseRow, "UDCARRY_FLG", UDCARRY_FLG.getValue());
        //=======pangben modify 20110428 start  Ƥ��combo
        phaBaseDS.setItem(phaBaseRow, "SKINTEST_FLG", SKINTEST_FLG.getValue());
        // ����combo
        phaBaseDS.setItem(phaBaseRow, "PRESCRIPTION_FLG", PRESCRIPTION_FLG.getValue());
        //=======pangben modify 20110428 stop

        phaBaseDS.setItem(phaBaseRow, "CTRLDRUGCLASS_CODE",
                          CTRLDRUGCLASS_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "ANTIBIOTIC_CODE",
                          ANTIBIOTIC_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "MAN_CHN_DESC", MAN_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "SUP_CODE", SUP_CODE.getValue());
        
        phaBaseDS.setItem(phaBaseRow, "OPD_ATC_ORG", OPD_ATC_ORG.getValue());
        phaBaseDS.setItem(phaBaseRow, "ODI_ATC_ORG", ODI_ATC_ORG.getValue());

        phaBaseDS.setItem(phaBaseRow, "DOSE_CODE", DOSE_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "TAKE_DAYS", TAKE_DAYS.getValue());
        phaBaseDS.setItem(phaBaseRow, "FREQ_CODE", FREQ_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "MEDI_QTY", MEDI_QTY.getText());
        phaBaseDS.setItem(phaBaseRow, "MEDI_UNIT", MEDI_UNIT.getValue());
        phaBaseDS.setItem(phaBaseRow, "ROUTE_CODE", ROUTE_CODE.getValue());
        phaBaseDS.setItem(phaBaseRow, "DEFAULT_TOTQTY", DEFAULT_TOTQTY.getValue());
        //�ɹ���λ
        phaBaseDS.setItem(phaBaseRow, "PURCH_QTY", PURCH_QTY.getValue());
        //===========pangben modify 20110426 start
        phaBaseDS.setItem(phaBaseRow, "REGION_CODE",this.getValueString("REGION_CODE"));
         //===========pangben modify 20110426 stop
        phaBaseDS.setItem(phaBaseRow, "DRUG_NOTES_PATIENT", DRUG_NOTES_PATIENT.getValue());
        phaBaseDS.setItem(phaBaseRow, "DRUG_NOTES_DR", DRUG_NOTES_DR.getValue());
        phaBaseDS.setItem(phaBaseRow, "DRUG_NOTE", DRUG_NOTE.getValue());
        phaBaseDS.setItem(phaBaseRow, "DRUG_FORM", DRUG_FORM.getValue());
        phaBaseDS.setItem(phaBaseRow, "DDD", DDD.getValue());
    }


    /**
     * ��ճ���table����Ŀؼ���ֵ
     */
    public void clearCtl() {

        this.clearValue(controlNameTab1 + ";ACTIVE_FLG;ORDER_CODE;REFUND_FLG");
        this.clearValue(controlNameTab2 + ";copyMEDI_UNIT;copyDOSAGE_UNIT1");
        StringBuffer cbos=new StringBuffer();
        for(int i=1;i<=20;i++){
            cbos.append("cbo"+i+";");
        }
        //=========pangben modify 20110816 start ���cboֵ
        String cbo=  cbos.toString().substring(0,cbos.toString().lastIndexOf(";"));
        this.clearValue(cbo);
        //=========pangben modify 20110426 start
        this.setValue("REGION_CODE",Operator.getRegion());
         //=========pangben modify 20110426 stop
        PHA_TYPE1.setSelected(true);
    }

    /**
     * ��ղ���
     */
    public void onClear() {
        clearCtl();
        upTable.removeRowAll();
        // �ж��Ƿ������������Ƿ�disable��λ����λת���� <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        // ---------->
    }

    /**
     * �½�
     */
    public void onNew() {
    	// ֻ��������ʱ��ſ��Ա༭��λ�Լ���λת����
    	isOnNew = true;
    	// �ж��Ƿ������������Ƿ�disable��λ����λת���� <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        // ---------->
        if (outShow) {
            this.messageBox("��û��Ȩ�ޣ�");
            return;
        }
//        if(!ALL.isSelected()){
//            this.messageBox("����'ȫ��'״̬���½���Ŀ��");
//        }

        //��ǰ���ݿ�ʱ��
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        clearCtl();
        cerateNewDate();

        //Ĭ�ϸ���ǰʱ��
        START_DATE.setValue(date);
        END_DATE.setValue(date);
        //'�����ֶ��Ǽ�ע��'Ĭ��ѡ��
        LET_KEYIN_FLG.setSelected(true);
        //�ָ��༭״̬
        setEnabledForCtl(true);
        isOnNew = false;
    }

    /**
     * ����
     */
    public boolean onSave() {
    	// �ж��Ƿ������������Ƿ�disable��λ����λת���� <----------  identify by shendr 2013.09.03
        checkIsOnNew();
        String newIsActive = ACTIVE_FLG.getValue();
        // ---------->
        if (outShow) {
            this.messageBox("��û��Ȩ�ޣ�");
            return false;
        }
        if(onSaveCheck())
            return false;
        double newOwnPrice = TypeTool.getDouble(OWN_PRICE.getText());
        double newNhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
        double newGovPrice = TypeTool.getDouble(GOV_PRICE.getText());
        String nowOrdCode = ORDER_CODE.getValue();
        // ���Ҫͣ�ø�ҩƷ
        if(isActive != newIsActive && isActive == "Y"){
	        // �жϸ�ҩƷ�Ƿ��п��
	        TParm stockQty = SPCSQL.getStockQtyByOrderCode(nowOrdCode);
	        // ����п��Ͳ���ͣ��
	        if(stockQty.getCount()>0){
	        	messageBox("��ҩƷ���п�桢����ͣ��");
	        	return false;
	        }
	        TParm notInStock = SPCSQL.getNotInStock(nowOrdCode);
	        if(notInStock.getInt("COUNTS")>0){
	        	messageBox("ͣ�ñ�ҩƷ�������쵥δ���,�봦��");
	        	return false;
	        }
        }
        //�ж�ʱ��
        String stDate = START_DATE.getText();
        String endDate = END_DATE.getText();
        Timestamp start = (Timestamp) StringTool.getTimestamp(stDate,
            "yyyy/MM/dd HH:mm:ss");
        Timestamp end = (Timestamp) StringTool.getTimestamp(endDate,
            "yyyy/MM/dd HH:mm:ss");
        if (start.getTime() > end.getTime()) {
            this.messageBox("��Ч���ڲ����Դ���ʧЧ���ڣ�");
            return false;
        }

        //�õ���ǰѡ���е����ݣ�Ҫ���ĺ��½����У�
        selRow = upTable.getSelectedRow();
        //ȡtable����
        TDataStore dataStore = upTable.getDataStore();
        //���õ�������λһ�У���Ĭ�ϱ�����Ǹ��С���0
        if(selRow==-1&&dataStore.rowCount()==1)
            selRow=0;
        //����ڴ���û�иı������¸ı��������۸���һ��Ҫ����������һ��
        if (orderCode.equals(nowOrdCode) && ( (newOwnPrice != oldOwnPrice) ||
                                             (newNhiPrice != oldNhiPrice) ||
                                             (newGovPrice != oldGovPrice))) {
        	
        	//add by lx 
			//$$ add by lx 2012/01/06 ûѡ��Ҫ�޸ĵ�ҽ��
			if(selRow==-1){
				this.messageBox("��ѡ����Ҫ�޸ĵ�ҽ����");
				return false;
			}
        	
            //��Ҫ�ı����ڣ�true��
            setDataInTDS(dataStore, selRow, true);
            //����һ���µ����ݣ��õ��к�
            int insertRowNumber = dataStore.insertRow();
            setDataInTDS(dataStore, insertRowNumber, true);
        }
        else {
            //�ӽ������õ����ݣ��ŵ�TDS�У����ڸ��±���
            setDataInTDS(dataStore, selRow, false);                        	

        }
        //���ӱ���SYS_FEE��TDS
//        System.out.println("--------da---------"+setDataInSysFeeTds());
        if (setDataInSysFeeTds())
            bigObject.addDS("SYS_FEE", sysFeeDS);
        //�ô���󱣴棬�γ�һ������
        bigObject.addDS("SYS_FEE_HISTORY", (TDS) dataStore);
        getTabPanel2Vaule();
        bigObject.addDS("PHA_TRANSUNIT", phaTransUnitDS);
        bigObject.addDS("PHA_BASE", phaBaseDS);
        if (!bigObject.update()) {
            messageBox("E0001");
            return false;
        }
        
       //add by lx 2013/01/06 ˢ��һ��ҽ�����
		initTblAndTDS(querySQL);
		
        messageBox("P0001");
        // <------- ִ��ͬ�� identify by shendr 2013.09.03
        updatePhaSync();
        // ------->
        //ǰ̨ˢ��
        TIOM_Database.logTableAction("SYS_FEE");
        return true;

    }
    
    /**
     * ͬ��PHA_BASE��PHA_TRANSUNIT
     * @author shendr 2013.08.14
     */
    public void updatePhaSync(){
    	Timestamp opt_date = SystemTool.getInstance().getDate();
    	TParm parm = new TParm();
    	// ��װparm,������
    	parm.setData("PURCH_UNIT", PURCH_UNIT.getValue());
    	parm.setData("STOCK_UNIT", STOCK_UNIT.getValue());
    	parm.setData("DOSAGE_UNIT", DOSAGE_UNIT.getValue());
    	parm.setData("MEDI_UNIT", MEDI_UNIT.getValue());
    	// PHA_TRANSUNIT
    	parm.setData("PURCH_QTY", PURCH_QTY.getValue());
    	parm.setData("STOCK_QTY", STOCK_QTY.getValue());
    	parm.setData("DOSAGE_QTY", DOSAGE_QTY.getValue());
    	parm.setData("MEDI_QTY", MEDI_QTY.getValue());
    	parm.setData("OPT_USER", Operator.getID());
    	parm.setData("OPT_DATE", opt_date);
    	parm.setData("OPT_TERM", Operator.getIP());
    	parm.setData("REGION_CODE", Operator.getRegion());
    	String flg = "";
    	int row = upTable.getSelectedRow();
    	TParm tableParm = upTable.getParmValue().getRow(row);
    	String specification = tableParm.getValue("SPECIFICATION");
    	if(StringUtil.isNullString(specification))
    		flg = "insert";
    	else
    		flg = "update";
    	parm.setData("FLG", flg);
    	String orderCode = ORDER_CODE.getValue();
    	TParm hisParm = SPCSQL.getOrderCode(orderCode);
    	// ���û��HIS���룬����ͬ��
    	if(hisParm.getCount()<=0)
    		return;
    	else{
    		String hisOrderCode =hisParm.getValue("HIS_ORDER_CODE");
        	TParm isExist = SPCSQL.queryOrderCodeIsExistPha(hisOrderCode);
        	// ���HIS������PHA_BASE��PHA_TRANSUNIT��û�У�����ͬ��
        	if(isExist.getCount()<=0)
        		return;
        	else{
        		parm.setData("ORDER_CODE", hisOrderCode);
        		// ����HIS�ӿ�ִ��ͬ������
            	TParm result = TIOM_AppServer.executeAction(
                        "action.spc.SPCPhaBaseSyncExecute", "executeSync", parm);
            	this.messageBox("ͬ��ִ�����!");
            	System.out.println(result.getData("RESULT_MEG"));
        	}
    	}
    }

    /**
     * �õ�SYS_FEE��TDS
     */
    private boolean setDataInSysFeeTds() {

        String sql = "";
        String orderCode = ORDER_CODE.getValue();
        //=============pangben modify 20110426 start
        String regionCode=this.getValueString("REGION_CODE");
        String region="";
        if(null!=regionCode&&!"".equals(regionCode))
            region = " AND (REGION_CODE='" + regionCode + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'"+region;
        //=============pangben modify 20110426 stop
//        System.out.println("-===--=-----2131------------="+sql);
        sysFeeDS.setSQL(sql);
        if (sysFeeDS.retrieve() <= 0)
            return false;
//        System.out.println("-===--=-----------------=");
        //��ǰ���ݿ�ʱ��
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //���������
        sysFeeDS.setItem(0, "OPT_USER", Operator.getID());
        sysFeeDS.setItem(0, "OPT_DATE", date);
        sysFeeDS.setItem(0, "OPT_TERM", Operator.getIP());
        sysFeeDS.setItem(0, "ORDER_CODE", ORDER_CODE.getValue());
        sysFeeDS.setItem(0, "ORDER_DESC", ORDER_DESC.getValue());
        sysFeeDS.setItem(0, "PY1", PY1.getValue());
        sysFeeDS.setItem(0, "NHI_FEE_DESC", NHI_FEE_DESC.getValue());
        sysFeeDS.setItem(0, "TRADE_ENG_DESC", TRADE_ENG_DESC.getValue());
        sysFeeDS.setItem(0, "GOODS_DESC", GOODS_DESC.getValue());
        sysFeeDS.setItem(0, "GOODS_PYCODE", GOODS_PYCODE.getValue());
        sysFeeDS.setItem(0, "ALIAS_DESC", ALIAS_DESC.getValue());
        sysFeeDS.setItem(0, "SPECIFICATION", SPECIFICATION.getValue());
        sysFeeDS.setItem(0, "DESCRIPTION", DESCRIPTION.getValue());
        sysFeeDS.setItem(0, "ALIAS_PYCODE", ALIAS_PYCODE.getValue());
        sysFeeDS.setItem(0, "HABITAT_TYPE", HABITAT_TYPE.getValue());
        sysFeeDS.setItem(0, "MAN_CODE", MAN_CODE.getValue());
        sysFeeDS.setItem(0, "UNIT_CODE", UNIT_CODE.getValue());

        sysFeeDS.setItem(0, "MR_CODE", MR_CODE.getValue());
        sysFeeDS.setItem(0, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
        sysFeeDS.setItem(0, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());

        sysFeeDS.setItem(0, "HYGIENE_TRADE_CODE",
                          HYGIENE_TRADE_CODE.getValue());
        sysFeeDS.setItem(0, "LET_KEYIN_FLG", LET_KEYIN_FLG.getValue());

        sysFeeDS.setItem(0, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "DR_ORDER_FLG", DR_ORDER_FLG.getValue());
        sysFeeDS.setItem(0, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "LCS_CLASS_CODE", LCS_CLASS_CODE.getValue());
        sysFeeDS.setItem(0, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
        sysFeeDS.setItem(0, "TRANS_HOSP_CODE", TRANS_HOSP_CODE.getValue());
        sysFeeDS.setItem(0, "EXEC_ORDER_FLG", EXEC_ORDER_FLG.getValue());
        sysFeeDS.setItem(0, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());

        sysFeeDS.setItem(0, "INSPAY_TYPE", INSPAY_TYPE.getValue());
        sysFeeDS.setItem(0, "ADDPAY_RATE", ADDPAY_RATE.getValue());
        sysFeeDS.setItem(0, "ADDPAY_AMT", ADDPAY_AMT.getValue());
        sysFeeDS.setItem(0, "NHI_CODE_I", NHI_CODE_I.getValue());
        sysFeeDS.setItem(0, "NHI_CODE_O", NHI_CODE_O.getValue());
        sysFeeDS.setItem(0, "NHI_CODE_E", NHI_CODE_E.getValue());
        if(!"".equals(CTRLDRUGCLASS_CODE.getValue()))
            sysFeeDS.setItem(0, "CTRL_FLG", "Y");
        else
            sysFeeDS.setItem(0, "CTRL_FLG", "N");
        sysFeeDS.setItem(0, "ATC_FLG",getValue("ATC_FLG"));
        sysFeeDS.setItem(0, "ATC_FLG_I",getValue("ATC_FLG_I"));
        sysFeeDS.setItem(0, "CAT1_TYPE",getCta1Type("" + ORDER_CAT1_CODE.getValue()));
        sysFeeDS.setItem(0, "ACTIVE_FLG",getValue("ACTIVE_FLG"));
        sysFeeDS.setItem(0, "IS_REMARK",getValue("IS_REMARK"));
//        //============pangben modify 20110426 start
        sysFeeDS.setItem(0, "REGION_CODE",getValue("REGION_CODE"));
//         //============pangben modify 20110426 stop
        //============pangben modify 20110816 start ҩ�����࣬����
        sysFeeDS.setItem(0, "SYS_GRUG_CLASS",getValue("SYS_GRUG_CLASS"));
        sysFeeDS.setItem(0, "NOADDTION_FLG",getValue("NOADDTION_FLG"));
        StringBuffer cbos=new StringBuffer();
        for (int i = 1; i <= 20; i++) {
            if (this.getValue("cbo" + i).equals("Y")) {
                cbos.append(this.getText("cbo" + i) + ";");
            }
        }
        String cbo=cbos.toString().trim().length()>0?cbos.substring(0,cbos.lastIndexOf(";")):"";
        sysFeeDS.setItem(0, "SYS_PHA_CLASS", cbo);
        //============pangben modify 20110816 start
        sysFeeDS.setItem(0, "DRUG_NOTES_PATIENT", DRUG_NOTES_PATIENT.getValue());
        sysFeeDS.setItem(0, "DRUG_NOTES_DR", DRUG_NOTES_DR.getValue());
        sysFeeDS.setItem(0, "DRUG_NOTE", DRUG_NOTE.getValue());
        sysFeeDS.setItem(0, "DRUG_FORM", DRUG_FORM.getValue());
        sysFeeDS.setItem(0, "DDD", DDD.getValue());
      //  sysFeeDS.setItem(0, "ORD_SUPERVISION", ORD_SUPERVISION.getValue());
        return true;           
    }

    /**
     * ȡ��ҽ��ϸ����
     * @param orderCat1Type String
     * @return String
     */
    private String getCta1Type(String orderCat1Type){
        String SQL = "SELECT CAT1_TYPE FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE = '"+orderCat1Type+"'";
        TParm parm = new TParm(getDBTool().select(SQL));
        if(parm.getErrCode() != 0)
            return "";
        return parm.getValue("CAT1_TYPE",0);
    }

    /**
     * ȡ�����ݿ������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
       return TJDODBTool.getInstance();
    }

    /**
     * �ռ������ϵ�ֵ����ʹ�ã����±��棩
     * @param dataStore TDataStore
     */
    public void setDataInTDS(TDataStore dataStore, int row, boolean dateFlg) {
    	
        //��ǰ���ݿ�ʱ��
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //���������
        dataStore.setItem(row, "OPT_USER", Operator.getID());
        dataStore.setItem(row, "OPT_DATE", date);
        dataStore.setItem(row, "OPT_TERM", Operator.getIP());

        //����޸����ڱ��Ϊ�ٵ�ʱ���������ڰ���������ʾ�ı���
        String tempStartDate;
        String tempEndDate;
        if (!dateFlg) {
            tempStartDate = START_DATE.getText();
            String startDate = tempStartDate.substring(0, 4) +
                tempStartDate.substring(5, 7) + tempStartDate.substring(8, 10) +
                tempStartDate.substring(11, 13) +
                tempStartDate.substring(14, 16) +
                tempStartDate.substring(17, 19);
            dataStore.setItem(row, "START_DATE", startDate);

            tempEndDate = END_DATE.getText();
            String endDate = tempEndDate.substring(0, 4) +
                tempEndDate.substring(5, 7) + tempEndDate.substring(8, 10) +
                tempEndDate.substring(11, 13) +
                tempEndDate.substring(14, 16) +
                tempEndDate.substring(17, 19);
            dataStore.setItem(row, "END_DATE", endDate);

            //ִ�б�ǲ���
            dataStore.setItem(row, "ACTIVE_FLG", ACTIVE_FLG.getValue());
            dataStore.setItem(row, "OWN_PRICE", OWN_PRICE.getText());
            dataStore.setItem(row, "OWN_PRICE2", NHI_PRICE.getText());
            dataStore.setItem(row, "OWN_PRICE3", GOV_PRICE.getText());

        }
        else { //���Ϊ��START_DATE
            Timestamp nowTime = TJDODBTool.getInstance().getDBTime();
            if (row == selRow) { //���������кŵ��ڱ�ѡ�е��к�˵������Ҫ���µ�����
                tempEndDate = StringTool.getString(nowTime,
                    "yyyy/MM/dd HH:mm:ss");
                String endDate = tempEndDate.substring(0, 4) +
                    tempEndDate.substring(5, 7) + tempEndDate.substring(8, 10) +
                    tempEndDate.substring(11, 13) +
                    tempEndDate.substring(14, 16) +
                    tempEndDate.substring(17, 19);
                dataStore.setItem(row, "END_DATE", endDate);

                //ִ�б�Ǳ�Ϊ��
                dataStore.setItem(row, "ACTIVE_FLG", 'N');
                //���ϵ��������ݼ�¼�ϵļ�Ǯ�������Ķ����µ�ֵ��
                dataStore.setItem(row, "OWN_PRICE", oldOwnPrice);
                dataStore.setItem(row, "OWN_PRICE2", oldNhiPrice);
                dataStore.setItem(row, "OWN_PRICE3", oldGovPrice);
            }
            else {
                tempStartDate = StringTool.getString(nowTime,
                    "yyyy/MM/dd HH:mm:ss");
                String startDate = tempStartDate.substring(0, 4) +
                    tempStartDate.substring(5, 7) +
                    tempStartDate.substring(8, 10) +
                    tempStartDate.substring(11, 13) +
                    tempStartDate.substring(14, 16) +
                    tempStartDate.substring(17, 19);
                dataStore.setItem(row, "START_DATE", startDate);

                tempEndDate = END_DATE.getText();
                String endDate = tempEndDate.substring(0, 4) +
                    tempEndDate.substring(5, 7) + tempEndDate.substring(8, 10) +
                    tempEndDate.substring(11, 13) +
                    tempEndDate.substring(14, 16) +
                    tempEndDate.substring(17, 19);
                dataStore.setItem(row, "END_DATE", endDate);

                //ִ�б�Ǳ�Ϊ��
                dataStore.setItem(row, "ACTIVE_FLG", 'Y');
                dataStore.setItem(row, "OWN_PRICE", OWN_PRICE.getText());
                dataStore.setItem(row, "OWN_PRICE2", NHI_PRICE.getText());
                dataStore.setItem(row, "OWN_PRICE3", GOV_PRICE.getText());
            }
        }

        dataStore.setItem(row, "ORDER_CODE", ORDER_CODE.getValue());
        dataStore.setItem(row, "ORDER_DESC", ORDER_DESC.getValue());
        dataStore.setItem(row, "PY1", PY1.getValue());
        dataStore.setItem(row, "NHI_FEE_DESC", NHI_FEE_DESC.getValue());
        dataStore.setItem(row, "TRADE_ENG_DESC", TRADE_ENG_DESC.getValue());
        dataStore.setItem(row, "GOODS_DESC", GOODS_DESC.getValue());
        dataStore.setItem(row, "GOODS_PYCODE", GOODS_PYCODE.getValue());
        dataStore.setItem(row, "ALIAS_DESC", ALIAS_DESC.getValue());
        dataStore.setItem(row, "SPECIFICATION", SPECIFICATION.getValue());
        dataStore.setItem(row, "DESCRIPTION", DESCRIPTION.getValue());
        dataStore.setItem(row, "ALIAS_PYCODE", ALIAS_PYCODE.getValue());
        dataStore.setItem(row, "HABITAT_TYPE", HABITAT_TYPE.getValue());
        dataStore.setItem(row, "MAN_CODE", MAN_CODE.getValue());
        dataStore.setItem(row, "UNIT_CODE", UNIT_CODE.getValue());

        dataStore.setItem(row, "MR_CODE", MR_CODE.getValue());
        dataStore.setItem(row, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
        dataStore.setItem(row, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());

        dataStore.setItem(row, "HYGIENE_TRADE_CODE",
                          HYGIENE_TRADE_CODE.getValue());
        dataStore.setItem(row, "LET_KEYIN_FLG", LET_KEYIN_FLG.getValue());

        dataStore.setItem(row, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
        dataStore.setItem(row, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
        dataStore.setItem(row, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
        dataStore.setItem(row, "DR_ORDER_FLG", DR_ORDER_FLG.getValue());
        dataStore.setItem(row, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
        dataStore.setItem(row, "LCS_CLASS_CODE", LCS_CLASS_CODE.getValue());
        dataStore.setItem(row, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
        dataStore.setItem(row, "TRANS_HOSP_CODE", TRANS_HOSP_CODE.getValue());
        dataStore.setItem(row, "EXEC_ORDER_FLG", EXEC_ORDER_FLG.getValue());
        dataStore.setItem(row, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());

        dataStore.setItem(row, "INSPAY_TYPE", INSPAY_TYPE.getValue());
        dataStore.setItem(row, "ADDPAY_RATE", ADDPAY_RATE.getValue());
        dataStore.setItem(row, "ADDPAY_AMT", ADDPAY_AMT.getValue());
        dataStore.setItem(row, "NHI_CODE_I", NHI_CODE_I.getValue());
        dataStore.setItem(row, "NHI_CODE_O", NHI_CODE_O.getValue());
        dataStore.setItem(row, "NHI_CODE_E", NHI_CODE_E.getValue());
        if(!"".equals(CTRLDRUGCLASS_CODE.getValue()))
            dataStore.setItem(row, "CTRL_FLG", "Y");
        else
            dataStore.setItem(row, "CTRL_FLG", "N");
        dataStore.setItem(row, "ATC_FLG", getValue("ATC_FLG"));
        dataStore.setItem(row, "ATC_FLG_I", getValue("ATC_FLG_I"));
        dataStore.setItem(row, "IS_REMARK",getValue("IS_REMARK"));
        dataStore.setItem(row, "CAT1_TYPE",getCta1Type("" + getValue("ORDER_CAT1_CODE")));
        //=============pangben modify 20110426 start
        dataStore.setItem(row, "REGION_CODE",getValue("REGION_CODE"));
        //=============pangben modify 20110426 stop
        //============pangben modify 20110816 start ҩ�����࣬����
       dataStore.setItem(row, "SYS_GRUG_CLASS",getValue("SYS_GRUG_CLASS"));
       dataStore.setItem(row, "NOADDTION_FLG",getValue("NOADDTION_FLG"));
       dataStore.setItem(row, "DRUG_NOTES_PATIENT",getValue("DRUG_NOTES_PATIENT"));
       dataStore.setItem(row, "DRUG_NOTES_DR",getValue("DRUG_NOTES_DR"));
       dataStore.setItem(row, "DRUG_NOTE",getValue("DRUG_NOTE"));
       dataStore.setItem(row, "DRUG_FORM",getValue("DRUG_FORM"));
       dataStore.setItem(row, "DDD",getValue("DDD"));
   //    dataStore.setItem(row, "ORD_SUPERVISION", ORD_SUPERVISION.getValue());
       StringBuffer cbos=new StringBuffer();
       for(int i=1;i<=20;i++){
         if(this.getValue("cbo"+i).equals("Y")){
             cbos.append(this.getText("cbo"+i)+";");
         }
       }

       String cbo=cbos.toString().trim().length()>0?cbos.substring(0,cbos.lastIndexOf(";")):"";
       dataStore.setItem(row, "SYS_PHA_CLASS",cbo);
       //============pangben modify 20110816 start

    }

    /**
     * ���½���ʱ���Զ����ɱ���ŵ�������
     */
    public void cerateNewDate() {
        String newCode = "";
        //�����ı�
        upTable.acceptText();
        //ȡtable����
        //========pangben modify 20110427 start ע��ȥ������Ҫ��ѯ���е�����ţ�ͨ����ѯ���ݿ��еĵ���ֵ��ʾ�����
        // TDataStore dataStore = upTable.getDataStore();
        // String maxCode = getMaxCode(dataStore, "ORDER_CODE");
        //System.out.println("maxCode:"+maxCode);
        //========pangben modify 20110427 start
        //�ҵ�ѡ�е����ڵ�
        TTreeNode node = tree.getSelectionNode();
        //���û��ѡ�еĽڵ�
        if (node == null)
            return;
        //�õ���ǰID
        String nowID = node.getID();
        int classify = 1;
        //�����������ĸ��ڵ����,�õ���ǰ�������
        if (nowID.length() > 0)
            classify = ruleTool.getNumberClass(nowID) + 1;
        //�������С�ڵ�,��������һ��
        if (classify > ruleTool.getClassifyCurrent()) {
            //�õ�Ĭ�ϵ��Զ���ӵ�ҽ������
            //===pangben modify 20110427 start
            //============���Ҵ˱�Ź����б�����ֵ
            String sql =
                    "SELECT MAX(ORDER_CODE) AS ORDER_CODE FROM PHA_BASE WHERE ORDER_CODE LIKE '" +
                    nowID + "%'";
            TParm parm = new TParm(getDBTool().select(sql));
            String maxCode = parm.getValue("ORDER_CODE", 0);
            //===pangben modify 20110427 start
            //�ҵ������Ҵ���(dataStore,����)
            String no = ruleTool.getNewCode(maxCode, classify);
            newCode = nowID + no;
            //�õ�����ӵ�table�����к�(�൱��TD�е�insertRow()����)
            int row = upTable.addRow();
            //���õ�ǰѡ����Ϊ��ӵ���
            upTable.setSelectedRow(row);
            //�����Ҵ������Ĭ��ֵ
            upTable.setItem(row, "ORDER_CODE", newCode);
            //Ĭ�Ͽ�������
            upTable.setItem(row, "ORDER_DESC", "(�½�����)");
            //Ĭ�Ͽ��Ҽ��
            upTable.setItem(row, "SPECIFICATION", null);
            //Ĭ�Ͽ���
            upTable.setItem(row, "OWN_PRICE", null);
            //Ĭ����С����ע��
            upTable.setItem(row, "UNIT_CODE", null);
            //========pangben modify 20110512 start
            //����
            upTable.setItem(row, "REGION_CODE", Operator.getRegion());
            //========pangben modify 20110512 start
        }
        //���Զ����ɵ�orderCode���õ�ORDER_CODE��
        ORDER_CODE.setText(newCode);

        //��ѯPHA_TRANSUNIT����TDS
        qeuryPhaTransUnit(newCode);
        qeuryPhaBase(newCode);

    }


    /**
     * �õ����ı��
     * @param dataStore TDataStore
     * @param columnName String
     * @return String
     */
    public String getMaxCode(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return "";
        String s = "";
        if (dataStore.isFilter()) {
            TParm a = dataStore.getBuffer(TDataStore.FILTER);
            int count = a.getCount();
            for (int i = 0; i < count; i++) {
                String value = a.getValue(columnName, i);
                if (StringTool.compareTo(s, value) < 0)
                    s = value;
            }
        }
        else {
            int count = dataStore.rowCount();
            for (int i = 0; i < count; i++) {
                String value = dataStore.getItemString(i, columnName);
                if (StringTool.compareTo(s, value) < 0)
                    s = value;
            }
        }
        return s;
    }


    /**
     * ����table�ϵ�ĳһ�����ݸ�����Ŀؼ���ʼ��ֵ
     * @param date TParm
     */
    public void setValueForDownCtl(TParm date) {
        clearCtl();
        this.setValue("ACTIVE_FLG", date.getValue("ACTIVE_FLG"));
        Timestamp start = StringTool.getTimestamp(date.getValue("START_DATE"),
                                                  "yyyyMMddHHmmss");
        this.setValue("START_DATE", start);
        Timestamp end = StringTool.getTimestamp(date.getValue("END_DATE"),
                                                "yyyyMMddHHmmss");
        this.setValue("END_DATE", end);

        this.setValue("ORDER_CODE", date.getValue("ORDER_CODE"));
        this.setValue("ORDER_DESC", date.getValue("ORDER_DESC"));
        this.setValue("PY1", date.getValue("PY1"));
        this.setValue("NHI_FEE_DESC", date.getValue("NHI_FEE_DESC"));
        this.setValue("TRADE_ENG_DESC", date.getValue("TRADE_ENG_DESC"));
        this.setValue("GOODS_DESC", date.getValue("GOODS_DESC"));
        this.setValue("GOODS_PYCODE", date.getValue("GOODS_PYCODE"));
        this.setValue("ALIAS_DESC", date.getValue("ALIAS_DESC"));
        this.setValue("SPECIFICATION", date.getValue("SPECIFICATION"));
        this.setValue("DESCRIPTION", date.getValue("DESCRIPTION"));
        this.setValue("HABITAT_TYPE", date.getValue("HABITAT_TYPE"));
        this.setValue("MAN_CODE", date.getValue("MAN_CODE"));
        this.setValue("ALIAS_PYCODE", date.getValue("ALIAS_PYCODE"));
        this.setValue("UNIT_CODE", date.getValue("UNIT_CODE"));
        this.setValue("MR_CODE", date.getValue("MR_CODE"));

        this.setValue("CHARGE_HOSP_CODE", date.getValue("CHARGE_HOSP_CODE"));
        this.setValue("ORDER_CAT1_CODE", date.getValue("ORDER_CAT1_CODE"));
        this.setValue("OWN_PRICE", date.getValue("OWN_PRICE"));
        this.setValue("OWN_PRICE2", date.getValue("OWN_PRICE2"));
        this.setValue("OWN_PRICE3", date.getValue("OWN_PRICE3"));
        this.setValue("HYGIENE_TRADE_CODE",
                      date.getValue("HYGIENE_TRADE_CODE"));
        this.setValue("LET_KEYIN_FLG", date.getValue("LET_KEYIN_FLG"));
        this.setValue("OPD_FIT_FLG", date.getValue("OPD_FIT_FLG"));
        this.setValue("EMG_FIT_FLG", date.getValue("EMG_FIT_FLG"));
        this.setValue("IPD_FIT_FLG", date.getValue("IPD_FIT_FLG"));
        this.setValue("HRM_FIT_FLG", date.getValue("HRM_FIT_FLG"));
        this.setValue("DR_ORDER_FLG", date.getValue("DR_ORDER_FLG"));
        this.setValue("TRANS_OUT_FLG", date.getValue("TRANS_OUT_FLG"));
        this.setValue("EXEC_ORDER_FLG", date.getValue("EXEC_ORDER_FLG"));
        this.setValue("LCS_CLASS_CODE", date.getValue("LCS_CLASS_CODE"));
        this.setValue("TRANS_HOSP_CODE", date.getValue("TRANS_HOSP_CODE"));
        this.setValue("EXEC_DEPT_CODE", date.getValue("EXEC_DEPT_CODE"));

        this.setValue("INSPAY_TYPE", date.getValue("INSPAY_TYPE"));
        this.setValue("ADDPAY_RATE", date.getValue("ADDPAY_RATE"));
        this.setValue("ADDPAY_AMT", date.getValue("ADDPAY_AMT"));
        this.setValue("NHI_CODE_I", date.getValue("NHI_CODE_I"));
        this.setValue("NHI_CODE_O", date.getValue("NHI_CODE_O"));
        this.setValue("NHI_CODE_E", date.getValue("NHI_CODE_E"));
        this.setValue("ATC_FLG", date.getValue("ATC_FLG"));
        this.setValue("ATC_FLG_I", date.getValue("ATC_FLG_I"));
        this.setValue("IS_REMARK", date.getValue("IS_REMARK"));
        //===========pangben modify 20110426 start
        this.setValue("REGION_CODE", date.getValue("REGION_CODE"));
        //===========pangben modify 20110816
        this.setValue("SYS_GRUG_CLASS", date.getValue("SYS_GRUG_CLASS"));
        this.setValue("NOADDTION_FLG", date.getValue("NOADDTION_FLG"));
        String[] cbos = date.getValue("SYS_PHA_CLASS").split(";");
        for (int index = 1; index <= 20; index++) {
            for (int i = 0; i < cbos.length; i++) {
                if (cbos[i].equals(this.getText("cbo" + index))){
                    this.setValue("cbo" + index, "Y");
                    break;
                }
            }
        }
        this.setValue("DRUG_NOTES_PATIENT",date.getValue("DRUG_NOTES_PATIENT"));
        this.setValue("DRUG_NOTES_DR",date.getValue("DRUG_NOTES_DR"));
        this.setValue("DRUG_NOTE",date.getValue("DRUG_NOTE"));
        this.setValue("DRUG_FORM",date.getValue("DRUG_FORM"));
        this.setValue("DDD",date.getValue("DDD"));
        this.setValue("ORD_SUPERVISION",date.getValue("ORD_SUPERVISION"));
    }

    /**
     * ѡ��TRANS_OUT_FLG���
     */
    public void onOutHosp() {
        String value = TRANS_OUT_FLG.getValue();
        TRANS_HOSP_CODE.setEnabled(TypeTool.getBoolean(value));
        if (!TypeTool.getBoolean(value)) {
            TRANS_HOSP_CODE.setText("");
        }
    }

    /**
     * ѡ�񿪵���ִ�б��
     */
    public void onExrOrd() {
        String value = EXEC_ORDER_FLG.getValue();
        //��������ʱ��
        EXEC_DEPT_CODE.setEnabled(TypeTool.getBoolean(value));
        //���ֵ
        if (!TypeTool.getBoolean(value)) {
            EXEC_DEPT_CODE.setText("");
        }

    }

    /**
     * �����ԷѼ��¼�
     */
    public void onOwnPrice() {
        //�õ���ǰ�ԷѼ۸�
        double ownPrice = TypeTool.getDouble(OWN_PRICE.getText());
        double nhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
        double govPrice = TypeTool.getDouble(GOV_PRICE.getText());
        //Ĭ��ҽ��/������߼۵����ԷѼ۸�
//        if (nhiPrice > ownPrice || nhiPrice == 0)
            NHI_PRICE.setValue(ownPrice * 2);
//        if (govPrice < ownPrice || govPrice == 0)
            GOV_PRICE.setValue(ownPrice * 2.5);
        //�ƶ����
        NHI_PRICE.grabFocus();
    }

    /**
     * ����ҽ�����¼�
     */
    public void onNhiPrice() {/*
        //�õ���ǰҽ���۸�
        double nhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
        double ownPrice = TypeTool.getDouble(OWN_PRICE.getText());
        double govPrice = TypeTool.getDouble(GOV_PRICE.getText());

        //���ҽ���۸�����ԷѼ۸�
//        if (ownPrice < nhiPrice || nhiPrice == 0)
            OWN_PRICE.setValue(nhiPrice*0.5);
        //�����������۸�С��ҽ���۸�
//        if (govPrice < nhiPrice || govPrice == 0)
            GOV_PRICE.setValue(nhiPrice*1.25);
        //�ƶ����
        GOV_PRICE.grabFocus();*/
    }

    /**
     * ����������߼��¼�
     */
    public void onGovPrice() {/*
        //�õ���ǰ�ԷѼ۸�
        double govPrice = TypeTool.getDouble(GOV_PRICE.getText());
        double ownPrice = TypeTool.getDouble(OWN_PRICE.getText());
        double nhiPrice = TypeTool.getDouble(NHI_PRICE.getText());

        //����ԷѼ۸����������߼۸�
//        if (ownPrice > govPrice || ownPrice == 0)
            OWN_PRICE.setValue(govPrice*0.4);
        //���ҽ���۸����������߼۸�
//        if (nhiPrice > govPrice || govPrice == 0)
            NHI_PRICE.setValue(govPrice*0.8);
        //�ƶ����
        HYGIENE_TRADE_CODE.grabFocus();*/
    }

    /**
     * �õ�����ƴ��
     */
    public void onPY1() {
        String orderDesc = ORDER_DESC.getText();
        String orderPy = SYSHzpyTool.getInstance().charToCode(orderDesc);
        PY1.setText(orderPy);
        NHI_FEE_DESC.grabFocus();
    }

    /**
     * �õ�����ƴ��
     */
    public void onPY2() {
        String aliasDesc = ALIAS_DESC.getText();
        String aliasPy = SYSHzpyTool.getInstance().charToCode(aliasDesc);
        ALIAS_PYCODE.setText(aliasPy);
        SPECIFICATION.grabFocus();
    }

    /**
     * ��ѧ��ƴ��
     */
    public void onPY3() {
        String orderDesc = GOODS_DESC.getText();
        String orderPy = SYSHzpyTool.getInstance().charToCode(orderDesc);
        GOODS_PYCODE.setText(orderPy);
        ALIAS_DESC.grabFocus();
    }


    /**
     * ��ʷ��ϸ��ť
     * @param args String[]
     */
    public void onHistory() {

        String orderCode = ORDER_CODE.getText();
        if ("".equals(orderCode)) {
            this.messageBox("��ѡ��Ҫ�鿴����Ŀ��");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        //======pangben modify 20110426 start ��Ӳ���
         parm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
        //======pangben modify 20110426 stop
        //��ģ̬����ʽ�򿪴��ڣ� ·�� ����
        TParm returnData = (TParm)this.openDialog(
            "%ROOT%\\config\\sys\\SYS_FEE\\SYSFEE_HISTORY.x", parm);

        if (returnData != null) {
            setValueForDownCtl(returnData);
            //����ʷ�е��������ݲ������޸�
            setEnabledForCtl(false);
        }
    }

    /**
     * �������еĿؼ��Ƿ���ã���ʷ���ݲ������޸ģ�
     * @param flg boolean
     */
    public void setEnabledForCtl(boolean flg) {
        //����Ǵ��ⲿ�ӿڵ���
        if (outShow) {
            //���ӹرա�������
            ACTIVE_FLG.setEnabled(false);
            return;
        }
        //�õ����пؼ���Tag����
        String allCtlTag = controlNameTab1 + ";" + controlNameTab2;
        String tag[] = allCtlTag.split(";");
        int count = tag.length;
        for (int i = 0; i < count; i++) {
            this.callFunction("UI|" + tag[i] + "|setEnabled", flg);
        }
        //===========pangben modify 20110816
        for(int i=1;i<=20;i++ ){
            this.callFunction("UI|cbo" + i + "|setEnabled", flg);
        }
        ACTIVE_FLG.setEnabled(true);
    }


    public void onExe() {

        Timestamp time = TJDODBTool.getInstance().getDBTime();
        //�����ʼʱ��Ϊ�գ���Ĭ�ϸ���ǰʱ��
        if ("".equals(TypeTool.getString(START_DATE.getValue()))) {
            START_DATE.setValue(time);
        }

        //�������õ�ʱ��Ĭ�ϡ���ʧЧ����дΪ"9999/12/31 23:59:59"
        if (TypeTool.getBoolean(ACTIVE_FLG.getValue())) {
            END_DATE.setText("9999/12/31 23:59:59");

        }
        else {
            String date = StringTool.getString(time, "yyyy/MM/dd HH:mm:ss");
            END_DATE.setText(date);
        }

        //�ָ��༭״̬
        setEnabledForCtl(true);
    }

    public void onQuery() {
        String selCode = ORDER_CODE.getValue();
        if ("".equals(selCode)) {
            this.messageBox("������Ҫ��ѯ��Ŀ�ı��룡");
        }

        //�õ���ѯcode��SQL���
        String sql = getSQL(selCode,this.getValueString("REGION_CODE"));//======pangben modify 20110426 ��Ӳ���
      //add by lx 2013/01/06
		querySQL=sql;
        //��ʼ��table��TDS
        initTblAndTDS(sql);
        //����ѯ���ֻ��һ�����ݵ�ʱ��ֱ����ʾ����ϸ��Ϣ
        if (upTable.getRowCount() == 1) {
            onUpTableClicked(0);
        }
      this.viewPhoto(selCode);
      // �ж��Ƿ������������Ƿ�disable��λ����λת���� <----------  identify by shendr 2013.09.03
      checkIsOnNew();
      // ---------->
    }


    /**
     * table˫��ѡ����
     * @param row int
     */
    public void onTableDoubleCleck() {

        upTable.acceptText();
        int row = upTable.getSelectedRow();
        String value = upTable.getItemString(row, 0);
        //�õ��ϲ����
        String partentID = ruleTool.getNumberParent(value);
        TTreeNode node = treeRoot.findNodeForID(partentID);
        if (node == null)
            return;
        //�õ������ϵ�������
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        //��������ѡ�нڵ�
        tree.setSelectNode(node);
        tree.update();
        //���ò�ѯ�¼�
        //======pangben modify 20110426 start ��Ӳ���
        onCleckClassifyNode(partentID, upTable,this.getValueString("REGION_CODE"));
        //======pangben modify 20110426 stop
        //table������ѡ����
        int count = upTable.getRowCount(); //table������
        for (int i = 0; i < count; i++) {
            //�õ����ʴ���
            String invCode = upTable.getItemString(i, 0);
            if (value.equals(invCode)) {
                //����ѡ����
                upTable.setSelectedRow(i);
                return;
            }
        }
    }

    /**
     * ѡ�ж�Ӧ���ڵ���¼�
     * @param parentID String
     * @param table TTable
     */
    public void onCleckClassifyNode(String parentID, TTable table,String regionCode) {
        //========pangben modify 20110426 start
        String region="";
        if(null!=regionCode&&!"".equals(regionCode))
             region = " AND (REGION_CODE='" + regionCode + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        //table�е�datastore�в�ѯ����sql
        table.setSQL("select * from SYS_FEE_HISTORY where ORDER_CODE like '" +
                     parentID +
                     "%'"+region);
        //========pangben modify 20110426 stop
        //��������
        table.retrieve();
        //�������ݵ�table��
        table.setDSValue();
        //����������ť
        callFunction("UI|new|setEnabled", true);

    }

    /**
     * ���õ�λ
     * @param flg Object
     */

    public void onCopy(Object flg) {
        String tag = TypeTool.getString(flg);
        if ("MEDI_UNIT".equals(tag)) {
            copyMEDI_UNIT.setValue(MEDI_UNIT.getValue());
        }
        else if ("DOSAGE_UNIT1".equals(tag)) {
            copyDOSAGE_UNIT1.setValue(DOSAGE_UNIT1.getValue());
        }
    }

    /**
     * ����
     * @param ob Object
     */
    public void onFilter(Object ob) {
        if ("ALL".equals(ob.toString())) {
            upTable.setFilter("");
            upTable.filter();
            //�������ݵ�table��
            upTable.setDSValue();
            //����������ť
            callFunction("UI|new|setEnabled", true);
        }
        else if ("Y".equals(ob.toString())) {
            upTable.setFilter("ACTIVE_FLG='Y'");
            upTable.filter();
            //�������ݵ�table��
            upTable.setDSValue();
            //����������ť
            callFunction("UI|new|setEnabled", true);
        }
        else {
            upTable.setFilter("ACTIVE_FLG='N'");
            upTable.filter();
            //�������ݵ�table��
            upTable.setDSValue();
            //����������ť
            callFunction("UI|new|setEnabled", false);
        }
    }


    /**
     * ���ؿ�������SQL������������
     * @return String[]
     */
    public String[] getSql() {

        return StringTool.copyArray(StringTool.copyArray(bigObject.getDS(
            "SYS_FEE").getUpdateSQL(),
            bigObject.getDS("PHA_TRANSUNIT").getUpdateSQL()),
                                    bigObject.getDS("PHA_BASE").getUpdateSQL());
    }

    public boolean onSaveCheck(){
        if(getValueString("START_DATE").length() == 0){
            messageBox("��Ч���ڲ���Ϊ��");
            return true;
        }
        if(getValueString("END_DATE").length() == 0){
            messageBox("ʧЧ���ڲ���Ϊ��");
            return true;
        }
        if(getValueString("ORDER_CODE").length() == 0){
            messageBox("ҩƷ���벻��Ϊ��");
            return true;
        }
        if(getValueString("ORDER_DESC").length() == 0){
            messageBox("ҩƷ���Ʋ���Ϊ��");
            return true;
        }
        if(getValueString("PY1").length() == 0){
            messageBox("����ƴ������Ϊ��");
            return true;
        }
        if(getValueString("UNIT_CODE").length() == 0){
            messageBox("��λ����Ϊ��");
            return true;
        }
        if(getValueString("CHARGE_HOSP_CODE").length() == 0){
            messageBox("Ժ�ڴ��벻��Ϊ��");
            return true;
        }
        if(getValueString("ORDER_CAT1_CODE").length() == 0){
            messageBox("ϸ���಻��Ϊ��");
            return true;
        }
        if(getValueString("OWN_PRICE").length() == 0){
            messageBox("�ԷѼ۲���Ϊ��");
            return true;
        }
        if(getValueString("OWN_PRICE2").length() == 0){
            messageBox("����۲���Ϊ��");
            return true;
        }
        if(getValueString("OWN_PRICE3").length() == 0){
            messageBox("����ҽ�Ƽ۲���Ϊ��");
            return true;
        }
//        if(getValueString("EXEC_DEPT_CODE").length() == 0){
//            messageBox("�����Ų���Ϊ��");
//            return true;
//        }
        if(getValueString("TYPE_CODE").length() == 0){
            messageBox("ҩƷ���಻��Ϊ��");
            return true;
        }
        if(getValueString("MEDI_QTY1").length() == 0){
            messageBox("���ʵ��ҩ��λ�������ҩ��λ��Ϣ");
            return true;
        }
        if(getValueString("MEDI_UNIT").length() == 0){
            messageBox("���ʵ��ҩ��λ�������ҩ��λ��Ϣ");
            return true;
        }
        if(getValueString("DOSAGE_QTY1").length() == 0){
            messageBox("���ʵ��ҩ��λ�������ҩ��λ��Ϣ");
            return true;
        }
        if(getValueString("DOSAGE_UNIT1").length() == 0){
            messageBox("���ʵ��ҩ��λ�������ҩ��λ��Ϣ");
            return true;
        }
        if(getValueString("DOSAGE_QTY").length() == 0){
            messageBox("���ʵ��ҩ��λ����ɿ�浥λ��Ϣ");
            return true;
        }
        if(getValueString("DOSAGE_UNIT").length() == 0){
            messageBox("���ʵ��ҩ��λ����ɿ�浥λ��Ϣ");
            return true;
        }
        if(getValueString("STOCK_QTY").length() == 0){
            messageBox("���ʵ��ҩ��λ����ɿ�浥λ��Ϣ");
            return true;
        }
        if(getValueString("STOCK_UNIT").length() == 0){
            messageBox("���ʵ��ҩ��λ����ɿ�浥λ��Ϣ");
            return true;
        }
        if(getValueString("STOCK_QTY1").length() == 0){
            messageBox("���ʵ��浥λ����ɽ�����λ��Ϣ");
            return true;
        }
        if(getValueString("STOCK_UNIT1").length() == 0){
            messageBox("���ʵ��浥λ����ɽ�����λ��Ϣ");
            return true;
        }
        if(getValueString("PURCH_QTY").length() == 0){
            messageBox("���ʵ��浥λ����ɽ�����λ��Ϣ");
            return true;
        }
        if(getValueString("PURCH_UNIT").length() == 0){
            messageBox("���ʵ��浥λ����ɽ�����λ��Ϣ");
            return true;
        }
        if(getValueString("DOSE_CODE").length() == 0){
            messageBox("���Ͳ���Ϊ��");
            return true;
        }
        if(getValueString("FREQ_CODE").length() == 0){
            messageBox("Ƶ�β���Ϊ��");
            return true;
        }
        if(getValueString("ROUTE_CODE").length() == 0){
            messageBox("�÷�����Ϊ��");
            return true;
        }
        if(getValueString("TAKE_DAYS").length() == 0){
            messageBox("������������Ϊ��");
            return true;
        }
        if(getValueString("MEDI_QTY").length() == 0){
            messageBox("���ü�������Ϊ��");
            return true;
        }
        if(getValueString("DEFAULT_TOTQTY").length() == 0){
            messageBox("Ĭ����������Ϊ��");
            return true;
        }
       return false;
    }

    /**
     * ����������ҩע���Ϸ������ʱ,ѡ��������ҩ,���Ĭ������,��������Ĭ������Ϊֻ��
     */
    public void dspnstotdoseSelect(){
        if(DSPNSTOTDOSE_FLG.isSelected()){
            DEFAULT_TOTQTY.setEnabled(true);
        } else {
            DEFAULT_TOTQTY.setValue(0);
            DEFAULT_TOTQTY.setEnabled(false);
        }
    }
    /**
	 * ����
	 * 
	 * @throws IOException
	 */
	public void onPhoto() throws IOException {

		String orderCode = getValue("ORDER_CODE").toString();
		String photoName = orderCode + ".jpg";
		String dir = TIOM_FileServer.getPath("PHAInfoPic.LocalPath");
		new File(dir).mkdirs();
		JMStudio jms = JMStudio.openCamera(dir + photoName);
		jms.addListener("onCameraed", this, "sendpic");
	}

	/**
	 * //ע���������
	 */
	public void onRegist() {
		// ע���������
		JMFRegistry jmfr = new JMFRegistry();
		jmfr.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				event.getWindow().dispose();
				System.exit(0);
			}

		});
		jmfr.setVisible(true);
	}

	/**
	 * ������Ƭ
	 * 
	 * @param image
	 *            Image
	 */
	public void sendpic(Image image) {
		String orderCode = getValue("ORDER_CODE").toString();
		String photoName = orderCode + ".jpg";
		String dir = TIOM_FileServer.getPath("PHAInfoPic.LocalPath");
		String localFileName = dir + photoName;
		try {
			byte[] data = FileTool.getByte(localFileName);
			new File(localFileName).delete();

			String root = TIOM_FileServer.getRoot();
			dir = TIOM_FileServer.getPath("PHAInfoPic.ServerPath");
			dir = root + dir;

			TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir
					+ photoName, data);
		} catch (Exception e) {
		}
		this.viewPhoto(orderCode);
	}

	/**
	 * ��ʾphoto
	 * 
	 * @param mrNo
	 *            String ������
	 */
	public void viewPhoto(String orderCode) {

		String photoName = orderCode + ".jpg";
		String fileName = photoName;
		try {
			TPanel viewPanel = (TPanel) getComponent("PHOTO_PANEL");
			String root = TIOM_FileServer.getRoot();
			String dir = TIOM_FileServer.getPath("PHAInfoPic.ServerPath");
			dir = root + dir;

			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					dir + fileName);
			if (data == null){
				viewPanel.removeAll();
				return;
			}
			double scale = 0.5;
			boolean flag = true;
			Image image = ImageTool.scale(data, scale, flag);
			// Image image = ImageTool.getImage(data);
			Pic pic = new Pic(image);
			pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
			pic.setLocation(0, 0);
			viewPanel.removeAll();
			viewPanel.add(pic);
			pic.repaint();
		} catch (Exception e) {
		}
	}

	class Pic extends JLabel {
		Image image;

		public Pic(Image image) {
			this.image = image;
		}

		public void paint(Graphics g) {
			g.setColor(new Color(161, 220, 230));
			g.fillRect(4, 15, 100, 100);
			if (image != null) {
				g.drawImage(image, 4, 15, null);

			}
		}
	}
    //��������
    public static void main(String[] args) {
        JavaHisDebug.initClient();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_PHA.x");
    }
   
    /**
     * �����Ͱ�ҩ���¼�
     */
    public void onAtcClick() {
    	boolean flg = this.getValueBoolean("ATC_FLG");
    	TTextFormat t = (TTextFormat) getComponent("OPD_ATC_ORG");
    	if(flg) {
    		t.setEnabled(true);
    	}else {
    		t.setEnabled(false); 
    		t.setValue("");               
    	}	
    }
    
    public void onAtcFlgClick(){
    	boolean flg = this.getValueBoolean("ATC_FLG_I");
    	TTextFormat t = (TTextFormat) getComponent("ODI_ATC_ORG");
    	if(flg) {
    		t.setEnabled(true);
    	}else {
    		t.setEnabled(false); 
    		t.setValue("");               
    	}
    }                      
    
    /**
     * �ж��Ƿ�����
     * @author shendr 2013.08.09
     */
    public void checkIsOnNew(){
    	if(isOnNew){
    		MEDI_QTY1.setEnabled(true);
    		DOSAGE_QTY1.setEnabled(true);
    		DOSAGE_QTY.setEnabled(true);
    		STOCK_QTY.setEnabled(true);
    		STOCK_QTY1.setEnabled(true);
    		PURCH_QTY.setEnabled(true);
    		MEDI_UNIT.setEnabled(true);
    		DOSAGE_UNIT1.setEnabled(true);
    		DOSAGE_UNIT.setEnabled(true);
    		STOCK_UNIT.setEnabled(true);
    		STOCK_UNIT1.setEnabled(true);
    		PURCH_UNIT.setEnabled(true);
    	}else{
    		MEDI_QTY1.setEnabled(false);
    		DOSAGE_QTY1.setEnabled(false);
    		DOSAGE_QTY.setEnabled(false);
    		STOCK_QTY.setEnabled(false);
    		STOCK_QTY1.setEnabled(false);
    		PURCH_QTY.setEnabled(false);
    		MEDI_UNIT.setEnabled(false);
    		DOSAGE_UNIT1.setEnabled(false);
    		DOSAGE_UNIT.setEnabled(false);
    		STOCK_UNIT.setEnabled(false);
    		STOCK_UNIT1.setEnabled(false);
    		PURCH_UNIT.setEnabled(false);
    	}
    }

}
