package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import jdo.spc.IndSysParmTool;
import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCInStoreReginTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title: ������ҩ�����ܹ�</p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCInStoreRegionControl extends TControl {
	// δȡҩ
	TTable table ;

	/**
	 * ���ⵥ��
	 */
	String dispenseNo ;
	
	/**
	 * ��ת��
	 */
	String boxEslId ;
	
	/**
	 * δ���⣬�ѳ���
	 */
	String radioN ,radioY;

	String ip;
	
	/**
	 * ���ؽ��ֵ
	 */
	TParm result ;
	
	  // ���ؽ����
    private TParm resultParm;
    
	/**
	 * ���ܹ񱾻�����IP
	 */
	String guardIp;
	
	String userId ;

	/**��ʼ������*/
	public void onInit() {
		super.onInit();
		table = (TTable) getComponent("TABLE");
		onInitData() ;
		
	}
	
	/**
	 * ��ʼ������
	 */
	public void onInitData(){
		 
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip=addr.getHostAddress();//��ñ���IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		TParm parm = new TParm();
		parm.setData("IP",ip);
		TParm result = SPCCabinetTool.getInstance().onQuery(parm);
		guardIp = result.getValue("GUARD_IP",0);
		setValue("CABINET_ID", result.getValue("CABINET_ID", 0));
		setValue("CABINET_DESC", result.getValue("CABINET_DESC", 0));
		setValue("ORG_CHN_DESC", result.getValue("ORG_CHN_DESC", 0));
		setValue("OPT_USER", Operator.getName());
		
	}
    
	
    /**-----------------------------------------------------*/

	/**���ⵥ�س��¼�*/
	public void onDispenseNo() {
		 
    	dispenseNo = getValueString("DISPENSE_NO");
		onQuery();
    	//��ת����ת��
    	this.getTextField("BOX_ESL_ID").grabFocus();
	}
	
	/**��ת��س��¼�*/
	public void onBoxEslId() {
		boxEslId = getValueString("BOX_ESL_ID");
		onQuery() ;
		
	}
	
	/**
	 * ��ѡ��ť ������δ�����¼�
	 */
	public void inOut(){
		onQuery();
	}
    
    /**��ѯ*/
    public void onQuery() {
    	
    	table = (TTable) getComponent("TABLE");
    	table.acceptText() ;
    	
    	dispenseNo = getValueString("DISPENSE_NO");
    	boxEslId = getValueString("BOX_ESL_ID");
    	
    	TParm inParm = new TParm() ;
    	inParm.setData("DISPENSE_NO",dispenseNo);
    	inParm.setData("BOX_ESL_ID",boxEslId);
    	inParm.setData("DRUG_CATEGORY","1");
    	
    	if(this.getRadioButton("RADIO_N").isSelected()){
    		inParm.setData("IS_STORE","N");
    		result = SPCInStoreReginTool.getInstance().onQuery(inParm);
    		resultParm = result ;
    	}else if(this.getRadioButton("RADIO_Y").isSelected()){
    		//inParm.setData("UPDATE_FLG","3");
    		inParm.setData("IS_STORE","Y");
    		result = SPCInStoreReginTool.getInstance().onQuery(inParm);
    	}
    	if(result.getCount() < 0 ){
    		this.messageBox("û�в�ѯ���������");
    		setValue("DISPENSE_NO", "");
    		table.setParmValue(new TParm());
    		return ;
    	}
    	
    	setValue("OUT_CHN_DESC", result.getValue("ORG_CHN_DESC",0));
		setValue("REQTYPE_CODE", getReqtype(result.getValue("REQTYPE_CODE",0)));
    	table.setParmValue(result);
    }
    
    /**
     * ������D��Ϊ���״̬��M��UPDATE_FLG=3 IND_STOCK���¿����
     */
    public boolean onSave(){
    	dispenseNo = getValueString("DISPENSE_NO");
    	if(dispenseNo == null || dispenseNo.equals("")){
    		this.messageBox("���ⵥ�Ų���Ϊ��");
    		return false;
    	}
    	table.acceptText() ;
    	 
    	if(table.getRowCount()  <=0 ){
    		this.messageBox("û���������");
    		return false;
    	}
    	
    	// �����ж�
		if (!checkPW()) {
			return false;
		}
		
    	
    	TParm inParm = new TParm();
    	inParm.setData("DISPENSE_NO",dispenseNo);
    	
    	//���ݳ��ⵥ�Ų�ѯIND_DISPENSEM����Ϣ
    	TParm mParm = SPCInStoreReginTool.getInstance().onQueryDispenseM(inParm);
    	
    	if(mParm.getCount() <  0){
    		this.messageBox("û���������");
    		return false;
    	}
    	
    	
    	//����
        TParm parm  = onSave(mParm, resultParm, table);
        
        //��װIND_CBNSTOCK��Ҫ�Ĳ���
        TParm cbnParm = new TParm();
        int count = 0;
        Timestamp date = SystemTool.getInstance().getDate();
        for (int i = 0; i < table_d.getRowCount(); i++) {
        	cbnParm.setData("CABINET_ID", count, getValue("CABINET_ID"));
        	cbnParm.setData("ORDER_CODE", count,
                          resultParm.getValue("ORDER_CODE", i));
        	cbnParm.setData("BATCH_SEQ", count, resultParm.getValue(
                     "BATCH_SEQ",i));
        	cbnParm.setData("BATCH_NO", count,
                     table_d.getItemString(i, "BATCH_NO"));  
        	cbnParm.setData("VALID_DATE", count, table_d.getItemData(i,
             "VALID_DATE"));
        	cbnParm.setData("VERIFYIN_PRICE", count, resultParm.getDouble( 
             "STOCK_PRICE",i));
        	
        	String unitCode = resultParm.getValue("UNIT_CODE",i);
        	String dosageUnit = resultParm.getValue("DOSAGE_UNIT",i);
        	String stockUnit = resultParm.getValue("STOCK_UNIT",i);
        	double outQty = table_d.getItemDouble(i,"OUT_QTY" );
      	    double stockQty = resultParm.getDouble("STOCK_QTY",i);
      	    double dosageQty = resultParm.getDouble("DOSAGE_QTY",i);
      	    if(unitCode != null && unitCode.equals(stockUnit)){
      	    	outQty = outQty*stockQty*dosageQty;
      	    } 
      	   
        	cbnParm.setData("OUT_QTY", count, outQty);
        	cbnParm.setData("STOCK_QTY",count,resultParm.getDouble("STOCK_QTY",i));
        	cbnParm.setData("DOSAGE_QTY",count,resultParm.getDouble("DOSAGE_QTY",i));
        	
        	cbnParm.setData("SEQ_NO",count,resultParm.getInt("SEQ_NO",i));
        	
        	//��ind_cabstock ���stock_unit
        	cbnParm.setData("UNIT_CODE", count, dosageUnit);
            cbnParm.setData("OPT_USER", count, userId);
            cbnParm.setData("OPT_DATE", count, date);
            cbnParm.setData("OPT_TERM", count, Operator.getIP());
          
            count++;
        }
        
        if (cbnParm != null) {
        	cbnParm.setCount(count);
            parm.setData("OUT_IN", cbnParm.getData());
        }
        
        // ִ����������
        TParm result = TIOM_AppServer.executeAction("action.spc.SPCInStoreRegionAction",
                                            "onInsertIn", parm);
    	if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return false;
        }
    	         
        this.messageBox("���ɹ�");
        onClear() ;
        return true;
    
    }
    
    /**
	 * ����������֤
	 * 
	 * @return boolean
	 */
	public boolean checkPW() {
		String inwCheck = "station";
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\spc\\passWordCheck.x", inwCheck);
		if (parm == null) {
			return false;
		}
		String value = parm.getValue("OK");
		userId = parm.getValue("USER_ID");
		return value.equals("OK");
	}
    
    
   
    
    /**��շ���*/
    public void onClear() {
        table.removeRowAll();
               
        this.setValue("DISPENSE_NO", "");
		this.setValue("BOX_ESL_ID", "");
        this.setValue("OUT_CHN_DESC", "");
		this.setValue("REQTYPE_CODE", "");
		
         
    }
    
    /**
	 * �õ��������͡�
	 * @param reqtype
	 * @return
	 */
	private String getReqtype(String reqtype){
		if(StringUtil.isNullString(reqtype)){
			return "";
		}
		if(reqtype.equals("DEP")){
			return "��������";
		}else if(reqtype.equals("TEC")){
			return "��ҩ����";
		}else if(reqtype.equals("EXM")){
			return "����Ʒ�";
		}else if(reqtype.equals("GIF")){
			return "ҩ������";
		}else if(reqtype.equals("RET")){
			return "�˿�";
		}else if(reqtype.equals("WAS")){
			return "���";
		}else if(reqtype.equals("THO")){
			return "��������";
		}else if(reqtype.equals("COS")){
			return "���Ĳ�����";
		}else if(reqtype.equals("THI")){
			return "�������";
		}else if(reqtype.equals("EXM")){
			return "���ұ�ҩ";
			
		}
		return "";
	}
	

    
	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
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
    
    
    /**---------------��⹫�÷���-------------------------------------------**/
    
    // ϸ����
    private TTable table_d;
    
	// ��������
	private String request_type;

	// ���뵥��
	private String request_no;

	// ʹ�õ�λ
	private String u_type;

	// ���ⲿ��
	private String out_org_code;

	// ��ⲿ��
	private String in_org_code;

	// �Ƿ����
	private boolean out_flg;

	// �Ƿ����
	private boolean in_flg;

	// ��ⵥ��
	private String dispense_no;
	
	int  seq = 1;
    
	/**
	 * �õ��������ӱ�����в���
	 * @param parm    ���صĲ���
	 * @param mParm   �����Ӧ����
	 * @param resultParm   �ӱ��Ӧ������
	 * @param updateFlg    ����״̬
	 * @param table        �ӱ�UI table����
	 * @return
	 */
	public TParm onSave(TParm mParm,TParm resultParm,TTable table){
		/**
		 * ��ʼ������
		 */
		initParm(mParm,table) ;
		
		TParm parm = new TParm();
		
		 // 2.���������ж�
        String batchvalid = getBatchValid(request_type);
		parm = getDispenseOutIn(out_org_code, in_org_code, batchvalid, out_flg, in_flg, parm, mParm, resultParm);
		return parm;
	}
	
	 /**
     * �����ҵ
     *
     * @param out_org_code
     * @param in_org_code
     * @param batchvalid
     */
    private TParm getDispenseOutIn(String out_org_code, String in_org_code,
                                  String batchvalid, boolean out_flg,
                                  boolean in_flg ,TParm parm,TParm mParm ,TParm resultParm) {
    	
    	 parm = getDispenseMParm(parm, mParm,"3");
    	// ϸ����Ϣ(OUT_D)
         if (!CheckDataD()) {
             return new TParm();
         }
         parm = getDispenseDParm(parm,resultParm);
         if (!"".equals(in_org_code)) {
             // ��ⲿ��(IN_ORG)
             parm.setData("IN_ORG", in_org_code);
             // �Ƿ����(IN_FLG)
             parm.setData("IN_FLG", in_flg);
             parm.setData("OUT_ORG",out_org_code);
         }
        
         
         return parm;
    	 
    	
    }
    /**
     * ���������Ϣ
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm,TParm mParm, String updateFlg) {
        // ҩ�������Ϣ
        TParm sysParm = getSysParm();
        // �Ƿ��д����۸�
        String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
        parm.setData("REUPRICE_FLG", reuprice_flg);

        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        
        // ��ⵥ��
        String  dispense_no = "";
        
        dispense_no = mParm.getValue("DISPENSE_NO",0);
       
        parmM.setData("DISPENSE_NO", dispense_no);
        parmM.setData("REQTYPE_CODE", mParm.getValue("REQTYPE_CODE",0));
        parmM.setData("REQUEST_NO", mParm.getValue("REQUEST_NO",0));
        parmM.setData("REQUEST_DATE", mParm.getValue("REQUEST_DATE",0));
        parmM.setData("APP_ORG_CODE", mParm.getValue("APP_ORG_CODE",0));
        parmM.setData("TO_ORG_CODE", mParm.getValue("TO_ORG_CODE",0));
        parmM.setData("URGENT_FLG", mParm.getValue("URGENT_FLG",0));
        parmM.setData("DESCRIPTION", mParm.getValue("DESCRIPTION",0));
        parmM.setData("DISPENSE_DATE", mParm.getValue("WAREHOUSING_DATE",0));
        parmM.setData("DISPENSE_USER", Operator.getID());
       
        if (!"1".equals(updateFlg)) {
            parmM.setData("WAREHOUSING_DATE", SystemTool.getInstance().getDate());
            parmM.setData("WAREHOUSING_USER", Operator.getID());
        }
        else {
            parmM.setData("WAREHOUSING_DATE", tnull);
            parmM.setData("WAREHOUSING_USER", "");
        }
        
        parmM.setData("DRUG_CATEGORY",mParm.getValue("DRUG_CATEGORY",0));
        
        //���뷽ʽ--ȫ��:APP_ALL,�˹�:APP_ARTIFICIAL,���콨��:APP_PLE,�Զ��β�:APP_AUTO
        parmM.setData("APPLY_TYPE",mParm.getValue("REQTYPE_CODE",0));
        
        parmM.setData("REASON_CHN_DESC", mParm.getValue("REASON_CHN_DESC",0));
        parmM.setData("UNIT_TYPE", u_type);
        parmM.setData("UPDATE_FLG", updateFlg);
        parmM.setData("OPT_USER", userId);
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        //luhai 2012-01-13 add region_code begin
        parmM.setData("REGION_CODE", Operator.getRegion());
        //luhai 2012-01-13 add region_code end
        if (parmM != null) {
            parm.setData("OUT_M", parmM.getData());
        }
        return parm;
    }
    
    /**
     * �����ϸ��Ϣ
     *
     * @param parm
     * @return
     */
    private TParm getDispenseDParm(TParm parm,TParm resultParm) {
        TParm parmD = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        int count = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            
            parmD.setData("DISPENSE_NO", count, getValueString("DISPENSE_NO"));
            if (!"THI".equals(this.getValueString("REQTYPE_CODE"))) {
                parmD.setData("SEQ_NO", count, resultParm.getInt("SEQ_NO", i));
            }
            else {
                parmD.setData("SEQ_NO", count,table_d.getParmValue().getValue("BATCH_SEQ",i));
            }
            parmD.setData("REQUEST_SEQ", count, resultParm.getInt(
                "REQUEST_SEQ", i));
            parmD.setData("ORDER_CODE", count,
                          resultParm.getValue("ORDER_CODE", i));
            parmD.setData("QTY", count, table_d.getItemDouble(i, "QTY"));
            parmD.setData("UNIT_CODE", count, table_d.getItemString(i,
                "UNIT_CODE"));
            parmD.setData("RETAIL_PRICE", count,resultParm.getDouble(
                "RETAIL_PRICE",i));
            parmD.setData("STOCK_PRICE", count, table_d.getParmValue().getDouble(
                "STOCK_PRICE",i));
            parmD.setData("ACTUAL_QTY", count, table_d.getItemDouble(i,
                "OUT_QTY"));
            parmD.setData("PHA_TYPE", count, table_d.getParmValue().getValue(
                "PHA_TYPE",i));
            parmD.setData("BATCH_SEQ", count, table_d.getParmValue().getValue(
                "BATCH_SEQ",i));
            parmD.setData("BATCH_NO", count,
                          table_d.getItemString(i, "BATCH_NO"));
            parmD.setData("VALID_DATE", count, table_d.getItemData(i,
                "VALID_DATE"));
            parmD.setData("DOSAGE_QTY", count, table_d.getParmValue().getDouble(
                "DOSAGE_QTY", i));
            parmD.setData("STOCK_QTY", count, table_d.getParmValue().getDouble(
                "STOCK_QTY", i));
            parmD.setData("REGION_CODE", count, Operator.getRegion());
            parmD.setData("OPT_USER", count, userId);
            parmD.setData("OPT_DATE", count, date);
            parmD.setData("OPT_TERM", count, Operator.getIP());
            
            parmD.setData("SUP_CODE", count,resultParm.getValue("SUP_CODE",i));//liuzhen
            parmD.setData("PUTAWAY_USER",count,userId);
            parmD.setData("IS_PUTAWAY",count,"Y");
            parmD.setData("PUTAWAY_DATE",count,SystemTool.getInstance().getDate());
        	
            //���ռ۸�
            parmD.setData("VERIFYIN_PRICE",count,resultParm.getDouble("VERIFYIN_PRICE",i));
            //���м۸�
            parmD.setData("INVENT_PRICE",count,resultParm.getDouble("INVENT_PRICE",i));
            count++;
        }
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        return parm;
    }
    

	public void initParm(TParm mparm,TTable table) {
		table_d = table ;
		request_type = mparm.getValue("REQTYPE_CODE",0);
		if ("TEC".equals(request_type) || "EXM".equals(request_type)
				|| "COS".equals(request_type)) {
			u_type = "1";
		} else if ("DEP".equals(request_type)) {
			u_type = IndSysParmTool.getInstance().onQuery().getValue(
					"UNIT_TYPE", 0);
		}

		if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
			out_org_code =mparm.getValue("TO_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("APP_ORG_CODE",0);
			in_flg = true;
		} else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
			out_org_code = mparm.getValue("APP_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("TO_ORG_CODE",0);
			in_flg = true;
		} else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
			out_org_code = mparm.getValue("TO_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("APP_ORG_CODE",0);
			in_flg = false;
		} else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
			out_org_code = mparm.getValue("APP_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("TO_ORG_CODE",0);
			in_flg = false;
		} else if ("THI".equals(request_type)) {
			out_org_code = mparm.getValue("TO_ORG_CODE",0);
			out_flg = false;
			in_org_code = mparm.getValue("APP_ORG_CODE",0);
			in_flg = true;
		}
	}
	
	 /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataD() {
        // �ж�ϸ���Ƿ��б�ѡ����
        for (int i = 0; i < table_d.getRowCount(); i++) {
            // �ж�������ȷ��
            double qty = table_d.getItemDouble(i, "OUT_QTY");
            if (qty <= 0) {
                this.messageBox("�������������С�ڻ����0");
                return false;
            }
            double price = table.getParmValue().getDouble("STOCK_PRICE",i);
            if (price <= 0) {
                this.messageBox("�ɱ��۲���С�ڻ����0");
                return false;
            }
        }
        return true;
    }
    
    /**
     * ���������ж�: 1,��ָ�����ź�Ч��;2,ָ�����ź�Ч��
     *
     * @return
     */
    private String getBatchValid(String type) {
        if ("DEP".equals(type) || "TEC".equals(type) || "EXM".equals(type)
            || "GIF".equals(type) || "COS".equals(type)) {
            return "1";
        }
        return "2";
    }

	 /**
     * ҩ�������Ϣ
     * @return TParm
     */
    private TParm getSysParm(){
        return IndSysParmTool.getInstance().onQuery();
    }
    
    public void openSearch(){
    	
    	TParm parm = new TParm();
    	parm.setData("CABINET_ID",getValueString("CABINET_ID"));
    	Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x", parm);
	}
    
}
