package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.javahis.util.JavaHisDebug;

import jdo.bil.BILComparator;
import jdo.pha.PhaMedSaleStaTool;
import com.dongyang.util.StringTool;
import jdo.sys.PatTool;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.jdo.TJDODBTool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import jdo.util.Manager;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.util.Compare;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

/**
 *
 * <p>Title: �ż���ҩƷ���۷����ѯ����
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH 2009.01.20
 *
 * @version 1.0
 */

public class PhaMedSaleStaControl
    extends TControl {  
  
    private TTextFormat START_DATE; //��ʼʱ�� 
    private TTextFormat END_DATE; //��ֹʱ��

    private TCheckBox OrdCat1_W; //��ҩ
    private TCheckBox OrdCat1_C; //�г�
    private TCheckBox OrdCat1_G; //��ҩ
    private TCheckBox OrdCat1_M; //�龫

    private TTextField MR_NO;
    private TTextField NAME;
    private TNumberTextField TOT; //�ܼ�
    private TTextField ORDER_DESC; //ҩƷ����
    private TTextField ORDER_CODE; //ҩƷ����

    private TComboBox TYPE; //״̬����
    private TComboBox REGION_CODE; //����
    private TTextFormat EXEC_DEPT_CODE; //ִ�п��ң�ҩ����//===zhangp 20120817

    private TRadioButton Master;
    private TRadioButton Detail;

    private TTable table;

    private String MedType;
    
    private String sysPope;
	
	//$$=============add by liyh 2012-07-10 ����������start==================$$//
    //=====modify-begin (by wanglong 20120716)===============================
    //�ɶԱ��������⣬��д
	//private Compare compare = new Compare();
	private BILComparator compare=new BILComparator();
	//======modify-end========================================================
	private boolean ascending = false;
	private int sortColumn = -1;
    //$$=============add by liyh 20120710 ����������end==================$$//    

    public PhaMedSaleStaControl() {
    }

    public void onInit() { //��ʼ������
        super.onInit();
        //===zhangp 201200726 start
        Object obj = getPopedem();
        TParm sysParm = (TParm) obj;
        sysPope = "";
        if(sysParm.getCount() > 0){
        	sysPope = sysParm.getValue("ID", 0);
        }
//		messageBox(""+sysPope);
        //===zhangp 201200726 start
        myInitControler();
        //===========pangben modify 20110511 stop
		//$$=====add by liyh 20120710�������򷽷�start============$$//
		addListener(getTTable("TABLE"));
		//$$=====add by liyh 20120710 �������򷽷�end============$$//

    }

    /**
     * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼�
     * ����
     */
    public void myInitControler() {

        START_DATE = (TTextFormat)this.getComponent("START_DATE");
        END_DATE = (TTextFormat)this.getComponent("END_DATE");

        OrdCat1_W = (TCheckBox)this.getComponent("OrdCat1_W");
        OrdCat1_C = (TCheckBox)this.getComponent("OrdCat1_C");
        OrdCat1_G = (TCheckBox)this.getComponent("OrdCat1_G");
        OrdCat1_M = (TCheckBox)this.getComponent("OrdCat1_M");
        

        MR_NO = (TTextField)this.getComponent("MR_NO");
        NAME = (TTextField)this.getComponent("NAME");
        TOT = (TNumberTextField)this.getComponent("TOT");
        ORDER_DESC = (TTextField)this.getComponent("ORDER_DESC");
        ORDER_CODE = (TTextField)this.getComponent("ORDER_CODE");

        TYPE = (TComboBox)this.getComponent("TYPE");
        REGION_CODE = (TComboBox)this.getComponent("REGION_CODE");
        EXEC_DEPT_CODE = (TTextFormat)this.getComponent("EXEC_DEPT_CODE");//===zhangp 20120817

        Master = (TRadioButton)this.getComponent("Master");
        Detail = (TRadioButton)this.getComponent("Detail");

        table = (TTable)this.getComponent("TABLE");

        //------------------------��ĳһ���ؼ���ע��SYSFeePopup-------------------
        // ע�ἤ��SYSFeePopup�������¼�
        ORDER_CODE.setPopupMenuParameter("TAG", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"));
        // ������ܷ���ֵ����
        ORDER_CODE.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");

        //��ʼ���������
        myInit();

    }
    /**
     * ��ʼ�������ֵ
     */
    public void myInit() {
        //��ʼ��table--ȱʡ�ǻ���
        onVarious("M");
        REGION_CODE.setValue(Operator.getRegion());
        //========pangben modify 20110421 start Ȩ�����
       this.callFunction("UI|REGION_CODE|setEnabled",SYSRegionTool.getInstance().getRegionIsEnabled(this.
              getValueString("REGION_CODE")));
       //========pangben modify 20110421 stop
        EXEC_DEPT_CODE.setValue(Operator.getDept());
        //luhai 2012-04-24 begin �����Ѿ���ҩ���ж�
        TYPE.setValue("04");
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("END_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 23:59:59");
		this.setValue("START_DATE", date.toString().substring(0, 10).replace('-', '/')
				+ " 00:00:00");
        //luhai 2012-04-24 end  
        //=====modify-begin (by wanglong 20120713)===============================
        //������ϸ�鿴��Ȩ�޿��ơ�
        //System.out.println("��ɫ��"+Operator.getRole());//ADMIN
        if(sysPope.equals("PHAADMIN")){
        	this.callFunction("UI|Detail|setEnabled", true);
        }else{
        	this.callFunction("UI|Detail|setEnabled", false);
        }
        //======modify-end========================================================
    }

    /**
     * ��ѯ����
     * 1)��ѯ
     * 2)�������ݵ�ʱ��ı���صĿؼ�״̬
     */
    public void onQuery() {
        //�����ǰ������
        table.setParmValue(new TParm());

        if (!checkCondition())
            return;

        TParm tableDate = getQueryDate();

        if (tableDate.getCount() <= 0) {
        	// ====modify-begin (by wanglong 20120716) =====
            //onClear();
            // ====modify-end===============================
            this.messageBox("�ò�ѯ���������ݣ�");
            return;
        }
        //�ܽ��
        double totalAmt = 0.0;
        int count = tableDate.getCount();
         //==========pangben modify 20110426 start
        int sumDispenseQty=0;//����
        double sumContractPrice=0.00;//�ɱ����
         //==========pangben modify 20110426 stop
        //ѭ���ۼ�
        for (int i = 0; i < count; i++) {
            double temp = tableDate.getDouble("OWN_AMT", i);
            totalAmt += temp;
            //==========pangben modify 20110426 start
            sumDispenseQty+=tableDate.getInt("DISPENSE_QTY", i);
            tableDate.setData("DISPENSE_QTY",i,tableDate.getInt("DISPENSE_QTY", i));
           if (!Detail.isSelected())
            sumContractPrice+=tableDate.getDouble("CONTRACT_PRICE", i);
            //==========pangben modify 20110426 stop
        }
        TOT.setValue(totalAmt);

        //�ֶ�д�뵱ǰ״̬��
        if (Detail.isSelected()) {
            for (int i = 0; i < count; i++) {
                tableDate.setData("TYPE", i, TYPE.getSelectedName());
            }

        }
        //==========pangben modify 20110426 start
        tableDate.setData("REGION_CHN_DESC", count, "�ܼ�:");
        tableDate.setData("ORDER_CODE", count, "");
        tableDate.setData("ORDER_DESC", count, "");
        tableDate.setData("SPECIFICATION", count, "");
        tableDate.setData("DISPENSE_UNIT", count, "");
        tableDate.setData("DISPENSE_QTY", count, sumDispenseQty);
        tableDate.setData("CONTRACT_PRICE", count, sumContractPrice);
        tableDate.setData("OWN_AMT", count, totalAmt);
        //==========pangben modify 20110426 stop
        //����table�ϵ�����
        this.callFunction("UI|TABLE|setParmValue", tableDate);

        }

        /**
         * ��ò�ѯ/��ӡ��Ҫ��ʾ��table�ϵ�����
         * @return TParm
         */
        public TParm getQueryDate() {
            TParm inParm = new TParm();
            //״̬����
            String type = TYPE.getValue();
            //�õ���ǰͳ��ʱ��
            Timestamp startDate = (Timestamp) START_DATE.getValue();
            String tempEnd = END_DATE.getValue().toString();
            Timestamp endDate = StringTool.getTimestamp(tempEnd.substring(0, 4) +
                    tempEnd.substring(5, 7) + tempEnd.substring(8, 10) + "235959",
                    "yyyyMMddHHmmss");
            //��ò�ѯ����
            //===============pangben modify 20110416 strat ʵ�ֿ������ѯ
            if (this.getValueString("REGION_CODE").length() > 0)
                inParm = this.getParmForTag("REGION_CODE");
            if (startDate != null)
                inParm.setData("START_DATE", startDate);
            if (endDate != null)
                inParm.setData("END_DATE", endDate);
            //===============pangben modify 20110416 stop
            String mrNo = MR_NO.getValue();
            if (!"".equals(mrNo))
                inParm.setData("MR_NO", mrNo);

            String execDept = getValueString("EXEC_DEPT_CODE");
            if (!"".equals(execDept))
                inParm.setData("EXEC_DEPT_CODE", execDept);

            String ordCode = ORDER_CODE.getValue();
            if (!"".equals(ordCode))
                inParm.setData("ORDER_CODE", ordCode);
            if(OrdCat1_M.isSelected()) {
            	inParm.setData("CTRLDRUGCLASS_CODE","'01','02'");        
            }
            //�ж�ѡ�е�ҩƷ����
            int x = 0;
            //����ҩ��ѡ�е�ʱ��
            if (OrdCat1_W.isSelected())
                x |= 4; //x=x|4
            //���гɱ�ѡ�е�ʱ��  
            if (OrdCat1_C.isSelected())
                x |= 2;
            //����ҩ��ѡ�е�ʱ��
            if (OrdCat1_G.isSelected())
                x |= 1;

            switch (x) {  
            case 4:
    //                System.out.println("==ֻѡ��ҩ=");
                inParm.setData("ORDER_CAT1_CODE1", "Y");
                inParm.setData("VALUE1", "PHA_W");

                MedType = "��ҩ";
                break;
            case 2:
    //                System.out.println("==ֻѡ�г�ҩ=");
                inParm.setData("ORDER_CAT1_CODE1", "Y");
                inParm.setData("VALUE1", "PHA_C");

                MedType = "�г�ҩ";
                break;
            case 1:
    //                System.out.println("==ֻѡ��ҩ=");
                inParm.setData("ORDER_CAT1_CODE1", "Y");
                inParm.setData("VALUE1", "PHA_G");

                MedType = "�в�ҩ";
                break;
            case 6:
    //                System.out.println("==ѡ��/�г�ҩ=");
                inParm.setData("ORDER_CAT1_CODE2", "Y");  
                inParm.setData("VALUE1", "PHA_W");
                inParm.setData("VALUE2", "PHA_C");

                MedType = "��ҩ���г�ҩ";
                break;
            case 3:
    //                System.out.println("==ֻѡ�г�/��ҩ=");
                inParm.setData("ORDER_CAT1_CODE2", "Y");
                inParm.setData("VALUE1", "PHA_G");
                inParm.setData("VALUE2", "PHA_C");

                MedType = "�г�ҩ���в�ҩ";
                break;
            case 5:
    //                System.out.println("==ֻѡ��/��ҩ=");
                inParm.setData("ORDER_CAT1_CODE2", "Y");
                inParm.setData("VALUE1", "PHA_W");
                inParm.setData("VALUE2", "PHA_G");

                MedType = "��ҩ���в�ҩ";
                break;
            case 7:
    //                System.out.println("==��ѡ=");
                inParm.setData("ORDER_CAT1_CODE3", "Y");
                inParm.setData("VALUE1", "PHA_W");
                inParm.setData("VALUE2", "PHA_G");
                inParm.setData("VALUE3", "PHA_C");

                MedType = "��ҩ���г�ҩ���в�ҩ";
                break;
            }
           /********************��� �����ز�ѯ���� 20120710 by liyh start*************************/ 
           //�����صȼ�	           
           if (getCheckBox("ANTIBIOTIC").isSelected()) {//�����ѡ�� ��û�������ѯ����
        	    String  antiblogticCode=this.getValueString("ANTIBIOTIC_CODE")+"";
				if (null != antiblogticCode &&  !"".equals(antiblogticCode)) {//��ѯ���忹���صȼ�
					inParm.setData("ANTIBIOTIC_CODE", antiblogticCode);
				}else
				{
					inParm.setData("ANTIBIOTIC_CODE1", "A");//��ѯȫ��
				}
           }
           /********************��� �����ز�ѯ���� 20120710 by liyh end***************************/
            inParm.setData("VARIOUS", Master.isSelected() ? "M" : "D");
            if (getValueString("DEPT_CODE").length() != 0)
                inParm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
            if (getValueString("DR_CODE").length() != 0)
                inParm.setData("DR_CODE", getValueString("DR_CODE"));  
            //���ú�̨��ѯ��Ӧ������
            TParm result = PhaMedSaleStaTool.getInstance().getQueryDate(inParm, type);
            return result;
        }
        
    /**
     * �õ�CheckBox����
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }
    
    
    /**
     * �����ѡ��
     */
    public void onChangeCheckBox() {
        //�����صȼ�
        if (getCheckBox("ANTIBIOTIC").isSelected()) {
        	getComBox("ANTIBIOTIC_CODE").setEnabled(true);
        }else
        {
        	  getComBox("ANTIBIOTIC_CODE").setEnabled(false);
              this.clearValue("ANTIBIOTIC_CODE");
        }
    }     
    
    /**
	 * �õ�combox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}    

    /**
     * ��ն���
     */
    public void onClear() {
    	//luhai 2012-04-24 ɾ��״̬���͵����begin
//        this.clearValue(
//            "MR_NO;EXEC_DEPT_CODE;ORDER_CODE;OrdCat1_W;OrdCat1_G;OrdCat1_C;NAME;TOT;ORDER_DESC;ORDER_CODE;EXEC_DEPT_CODE;TYPE");
        this.clearValue(
            "MR_NO;EXEC_DEPT_CODE;ORDER_CODE;OrdCat1_W;OrdCat1_G;OrdCat1_C;NAME;TOT;ORDER_DESC;ORDER_CODE;EXEC_DEPT_CODE");
        //luhai 2012-04-24 ɾ��״̬���͵����end 
        //���table
        table.removeRowAll();
        //ѡ���ܻ㡯
        Master.setSelected(true);
        onVarious("M");
        //========pangben modify 20110416
        REGION_CODE.setValue(Operator.getRegion());

    }

    /**
     * ��⴫���̨�Ĳ����Ƿ����
     * @return boolean
     */
    public boolean checkCondition() {
        //Ҫôѡ��ҩƷ���࣬Ҫô��ҩƷCODE
        if (!OrdCat1_M.isSelected()&&!OrdCat1_W.isSelected() && !OrdCat1_C.isSelected() &&
            !OrdCat1_G.isSelected()&&"".equals(ORDER_CODE.getValue())) {
            this.messageBox("��ѡ��ҩƷ���ͣ�");
            return false;
        }
        if ("".equals(TYPE.getValue())) {
            this.messageBox("������״̬���ͣ�");
            return false;
        }
        //===========pangben modify 20110416 start ʵ�ֿ������ѯ
//        if ("".equals(REGION_CODE.getValue())) {
//            this.messageBox("����������");
//            return false;
//        }
        //===========pangben modify 20110416 stop
        return true;
    }

    /**
     * ����MR_NO
     */
    public void onMrNo() {
        String mrNo = MR_NO.getValue();
        MR_NO.setValue(PatTool.getInstance().checkMrno(mrNo));
        //�õ���������
        getPatName(mrNo);
    }

    /**
     * ��øò��˵�����
     * @param mrNo String
     */
    private void getPatName(String mrNo){

        NAME.setValue(PatTool.getInstance().getNameForMrno(mrNo));

    }

    /**
     * �л���ϸ��
     * ��ʼ����ϸ��
     * @param ms Object
     */
    public void onVarious(Object ms) {
        String MSFlg = ms.toString();
        //�л���ϸ��ʱ���table  
        table.setParmValue(new TParm());
        if ("D".equals(MSFlg)) {
            //=========pangben modify 20110416 start ���������ʾ
            table.setHeader(
                "����,120;��ҩ����,100;������,100;����,100;ҩƷ����,240;���,140;����,60,double,#########0.000;��λ,60,UNIT;��ҩ���,100,double,#########0.00;��ǰ״̬,90;��������,90,DEPT_CODE_VIEW;����ҽ��,90,DR_CODE");
            table.setParmMap("REGION_CHN_DESC;ORDER_DATE;MR_NO;PAT_NAME;ORDER_DESC;SPECIFICATION;DISPENSE_QTY;DISPENSE_UNIT;OWN_AMT;TYPE;DEPT_CODE;DR_CODE");
            table.setLockColumns("0,1,2,3,4,5,6,7,8,9,10,11");
            table.setColumnHorizontalAlignmentData(
                "0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,left;8,right;9,left;10,left;11,left");
            //=========pangben modify 20110416 stop
        }
        if ("M".equals(MSFlg)) {  
            //=========pangben modify 20110416 start ���������ʾ
    		// ==========modify-begin (by wanglong 20120716) ȥ������ʱ�ġ��������ҡ��롰����ҽ������
//            table.setHeader(
//                "����,180;ҩƷ����,100;ҩƷ����,250;���,130;��λ,70,UNIT;����,70;��ҩ���,70,double,#########0.00;�ɱ����,90,double,#########0.00;��������,90,DEPT_CODE;����ҽ��,90,DR_CODE");
//            table.setParmMap(
//                "REGION_CHN_DESC;ORDER_CODE;ORDER_DESC;SPECIFICATION;DISPENSE_UNIT;DISPENSE_QTY;OWN_AMT;CONTRACT_PRICE;DEPT_CODE;DR_CODE");
//            table.setLockColumns("0,1,2,3,4,5,6,7,8,9");
//            table.setColumnHorizontalAlignmentData(
           	//fux modify 20150911 ��� ���۵�����ɹ�����
              table.setHeader(  
              		"����,120;ҩƷ����,100;ҩƷ����,240;���,140;����,60,double,#########0.000;��λ,60,UNIT;���۵���,100,double,#########0.0000;��ҩ���,100,double,#########0.00;�ɹ�����,100,double,#########0.0000;�ɱ����,100,double,#########0.00");
              table.setParmMap(
              		"REGION_CHN_DESC;ORDER_CODE;ORDER_DESC;SPECIFICATION;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE;OWN_AMT;STOCK_PRICE;CONTRACT_PRICE");
              table.setLockColumns("0,1,2,3,4,5,6,7,8,9");
              table.setColumnHorizontalAlignmentData(
              		"0,left;1,left;2,left;3,left;4,right;5,right;6,right;7,right;8,right;9,right");
            // ==========modify-end========================================
            //=========pangben modify 20110416 stop
        }
        TOT.setValue(0);
//        this.messageBox_(ms);

    }

    /**
     * ����EXCEL
     */
    public void onExcel() {
        //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        ExportExcelUtil.getInstance().exportExcel(table, "�ż���ҩƷ���۷���ͳ�Ʊ���");

    }

    /**
     * ��ӡ����
     */
    public void onPrint() {

        if (table.getRowCount() <= 0) {
            this.messageBox("û����ش�ӡ���ݣ�");
            return;
        }
        //�õ���ʵ������(ʡȥformat����)
        TParm printData = table.getShowParmValue();
        int rowCount = table.getRowCount();

        Timestamp nowTime = TJDODBTool.getInstance().getDBTime();

        //��������
        String prtName = "";
        String tableName = "";
        //������
        String proName = "��PhaMedSaleStaControl��";
        //��ӡʱ��
        String prtTime = StringTool.getString(nowTime, "yyyy/MM/dd HH:mm:ss");
        //ҽԺ���
        String HospName = Manager.getOrganization().getHospitalCHNShortName(REGION_CODE.getValue());
        Timestamp startDate=(Timestamp) START_DATE.getValue();
        Timestamp endDate=(Timestamp) END_DATE.getValue();
        //ͳ������
        String staSection = "ͳ������: " + StringTool.getString(startDate,
            "yyyy/MM/dd") + " �� " + StringTool.getString(endDate,
            "yyyy/MM/dd");
        //�Ʊ�ʱ��
        String prtDate = "�Ʊ�ʱ��:" + StringTool.getString(nowTime,"yyyy/MM/dd HH:mm:ss");
        //---------------------��ѯ����-----------------------------------------
        //����
        String region=REGION_CODE.getSelectedName();
        //����
        String dept=this.getValueString("EXEC_DEPT_CODE");//====zhangp 20120817 start
        String prtType=TYPE.getSelectedName();  

        String MDType=Master.isSelected()?"(����)":"(��ϸ)";

        printData.setCount(rowCount);
        if (Master.isSelected()) {
            //7���ֶ�  
            //======pangben modify 20110418 start
            printData.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
            //======pangben modify 20110418 start
            printData.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            printData.addData("SYSTEM", "COLUMNS", "DISPENSE_UNIT");
            printData.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            //fux modify 20150911 ���۵��� 
            printData.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
            printData.addData("SYSTEM", "COLUMNS", "OWN_AMT");
            //�ɹ�����
            printData.addData("SYSTEM", "COLUMNS", "STOCK_PRICE");
            printData.addData("SYSTEM", "COLUMNS", "CONTRACT_PRICE");
    		// ==========modify-begin (by wanglong 20120716) ȥ������ʱ�ġ��������ҡ��롰����ҽ������
            //printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            //printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
            // ==========modify-end========================================
            prtName = "PHAMedSaleStaBySort_M.jhw";
            tableName = "TABLE_M";

        }
        if (Detail.isSelected()) {
            //9���ֶ�
            //======pangben modify 20110418 start
            printData.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
            //======pangben modify 20110418 start
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DATE");
            printData.addData("SYSTEM", "COLUMNS", "MR_NO");  
            printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            printData.addData("SYSTEM", "COLUMNS", "DISPENSE_UNIT");
            printData.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            printData.addData("SYSTEM", "COLUMNS", "OWN_AMT");
            printData.addData("SYSTEM", "COLUMNS", "TYPE");
            printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
            prtName = "PHAMedSaleStaBySort_D.jhw";
            tableName = "TABLE_D";
        }

        if ("".equals(prtName)) {
            this.messageBox("���ݴ���");
            return;
        }

        TParm parm = new TParm();
        //ʵ��-1:���� 2��Map������
        parm.setData(tableName, printData.getData());
        parm.setData("proName", "TEXT", proName);
        parm.setData("prtTime", "TEXT", prtTime);
        parm.setData("HospName", "TEXT", HospName);
        parm.setData("staSection", "TEXT", staSection);
        parm.setData("prtDate", "TEXT", prtDate);
        parm.setData("REGION", "TEXT", region.length()==0?"����ҽԺ":region);
        parm.setData("DEPT", "TEXT", getOrg(dept));  
        parm.setData("MedType", "TEXT", MedType);
        parm.setData("staName", "TEXT", prtType + "ҩƷ���۷���ͳ��"+MDType+"����");

        this.openPrintWindow("%ROOT%\\config\\prt\\pha\\" + prtName, parm);

    }  

    
	private String getOrg(String valueString) {
		String sql = " SELECT ORG_CHN_DESC FROM IND_ORG WHERE ORG_CODE = '"+valueString+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("ORG_CHN_DESC",0);
	}

    /**
     * ���ܷ���ֵ����
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
    }

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 * @author liyh
	 * @date 20120710
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	/**
	 * �����������������
	 * @param table
	 * @author liyh
	 * @date 20120710
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// TParm tableData = getTTable("TABLE").getParmValue();
		 //System.out.println("===tableDate����ǰ==="+tableData);
		 
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				 //System.out.println("----+i:"+i);
				 //System.out.println("----+i:"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
			    // ==========modify-begin (by wanglong 20120716) ȥ������ʱ�ġ��������ҡ��롰����ҽ������
				TParm tableData = getTTable("TABLE").getParmValue();
				//TParm tableData = getTTable("TABLE").getShowParmValue();
				if(Detail.isSelected()){
					tableData.removeData("Data", "ORDER_CODE");
					tableData.removeData("Data", "CONTRACT_PRICE");
					tableData.addData("ORDER_DATE", "");
					tableData.addData("DEPT_CODE", "");
					tableData.addData("PAT_NAME", "");
					tableData.addData("MR_NO", "");
					tableData.addData("DR_CODE", "");
					tableData.addData("TYPE", "");
				}else{
					tableData.removeData("Data", "ORDER_DATE");
					tableData.removeData("Data", "PAT_NAME");
					tableData.removeData("Data", "TYPE");
					tableData.removeData("Data", "MR_NO");
					//tableData.addData("DR_CODE", "MR_NO");
					//tableData.addData("DEPT_CODE", "MR_NO");
				}
				//���ܼơ��� ����������
				TParm totRowParm=new TParm();//��¼���ܼơ���
				tableData.setCount(tableData.getCount("OWN_AMT")-1);
				totRowParm.addRowData(table.getShowParmValue(), tableData.getCount());
				tableData.removeRow(tableData.getCount());//ȥ�����һ��(�ܼ���)
				// ==========modify-end========================================
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = getTTable("TABLE").getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
			    // ==========modify-begin (by wanglong 20120716)===============
				//cloneVectoryParam(vct, new TParm(), strNames);
				TParm lastResultParm=new TParm();//��¼���ս��
				lastResultParm=cloneVectoryParam(vct, new TParm(), strNames);//�����м�����
				if(Detail.isSelected()){
					String colName[] = tableData.getNames("Data");
					for (String tmp : colName) {
						if(tmp.equals("REGION_CHN_DESC")||tmp.equals("OWN_AMT")) continue;
						else if(tmp.equals("DISPENSE_QTY")) totRowParm.setData("DISPENSE_QTY", 0,(int)Double.parseDouble(totRowParm.getValue("DISPENSE_QTY",0)));
						else{
							 totRowParm.setData(tmp, 0, "");
						}
					}
				}else{
					String colName[] = tableData.getNames("Data");
					for (String tmp : colName) {
						if(tmp.equals("REGION_CHN_DESC")||tmp.equals("CONTRACT_PRICE")||tmp.equals("OWN_AMT")) continue;
						else if(tmp.equals("DISPENSE_QTY")) totRowParm.setData("DISPENSE_QTY", 0, (int)Double.parseDouble(totRowParm.getValue("DISPENSE_QTY",0)));
						else{
							 totRowParm.setData(tmp, 0, "");
						}
					}
				}
				lastResultParm.addRowData(totRowParm, 0);//�����ܼ���
				lastResultParm.setCount(tableData.getCount("OWN_AMT")-1);
				table.setParmValue(lastResultParm);
				// ==========modify-end========================================
				// getTMenuItem("save").setEnabled(false);
			}
		});
	}	
	
	/**
	 * ����������
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 * @author liyh
	 * @date 20120710
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 * @author liyh
	 * @date 20120710
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}	

	/**
	 * vectoryת��param
	 * @author liyh
	 * @date 20120710
	 */
	    // ==========modify-begin (by wanglong 20120716)===============
//	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
//			String columnNames) {
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ==========modify-end========================================
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		// ==========modify-begin (by wanglong 20120716)===============
		// getTTable("TABLE").setParmValue(parmTable);
		return parmTable;
		// ==========modify-end========================================
		// System.out.println("�����===="+parmTable);

	}

    public static void main(String[] args) {

        //JavaHisDebug.initClient();
        //JavaHisDebug.initServer();
//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("pha\\PHAMedSaleSta.x");
    }

    public void onClickW() {
    	String flg = this.getValueString("OrdCat1_W");
    	if("Y".equals(flg)) {
    		this.setValue("OrdCat1_M", "N");			
    	}
    }
    
    public void onClickC() {
    	String flg = this.getValueString("OrdCat1_C");
    	if("Y".equals(flg)) {
    		this.setValue("OrdCat1_M", "N");
    	}
    }
    
    public void onClickG() {			
    	String flg = this.getValueString("OrdCat1_G");
    	if("Y".equals(flg)) {
    		this.setValue("OrdCat1_M", "N");
    	}
    }
    
    /**
     * �龫����¼�			
     */
    public void onClick() {
    	String mjFlg = this.getValueString("OrdCat1_M");
    	if("Y".equals(mjFlg)) {
    		this.setValue("OrdCat1_W", "N");
    		this.setValue("OrdCat1_C", "N");
    		this.setValue("OrdCat1_G", "N");
    	}else {
    		this.setValue("OrdCat1_W", "Y");
    		this.setValue("OrdCat1_C", "Y");
    		this.setValue("OrdCat1_G", "Y");
    	}
    }

}
