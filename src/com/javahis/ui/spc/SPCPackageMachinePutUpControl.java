package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.INDTool;
import jdo.spc.IndSysParmTool;
import jdo.spc.SPCPackageMachinePutUpTool;
import jdo.spc.SPCInStoreReginTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:��ҩ�����control
 * </p>
 * 
 * <p>
 * Description: ��ҩ�����control
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author liuzhen 20121015
 * @version 1.0
 */
public class SPCPackageMachinePutUpControl extends TControl {

	TTable table_N;// δ���
	TTable table_Y;// �����

	TPanel N_PANEL;
	TPanel Y_PANEL;
	
	TParm parmData = new TParm();
	int countResult = 0;// ��¼�Ƴ�ҩƷ������
	
	 // ��������
    private String request_type;
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

    // ȫԺҩ�ⲿ����ҵ����
    private boolean request_all_flg = true;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		
		Timestamp sysDate = SystemTool.getInstance().getDate();
		setValue("END_DATE",sysDate.toString().substring(0, 10).replace('-', '/'));
		setValue("START_DATE",StringTool.rollDate(sysDate, -2).toString().substring(0, 10).replace('-', '/') );
		
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
	}


	public void onQuery(){
		
		//��ת��
		String boxEslId = getValueString("BOX_ESL_ID");
		
		//���ⵥ��
		String dispenseNo = getValueString("DISPENSE_NO");
		
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
		N_PANEL = (TPanel)getComponent("N_PANEL");
		Y_PANEL = (TPanel)getComponent("Y_PANEL");
		
		TParm parm = new TParm();
		
		parm.setData("DISPENSE_NO",dispenseNo);
		parm.setData("BOX_ESL_ID",boxEslId);
		
		String startDate = getValueString("START_DATE");
		if(startDate != null && !startDate.equals("")){
			startDate = startDate.substring(0,10);
			parm.setData("START_DATE",startDate);
		}
		
		String endDate = getValueString("END_DATE");
		if(endDate != null && !endDate.equals("")){
			endDate = endDate.substring(0,10);
			parm.setData("END_DATE",endDate);
		}
		
		if(N_PANEL.isShowing()){
			parm.setData("IS_PUTAWAY","N");
			parm.setData("UPDATE_FLG","1");
		}else if(Y_PANEL.isShowing()){
			parm.setData("IS_PUTAWAY","Y");
			parm.setData("UPDATE_FLG","3");
		}
		
		TParm result = SPCPackageMachinePutUpTool.getInstance().onQuery(parm);
		this.setValue("BOX_ESL_ID", "");
		this.setValue("DISPENSE_NO", "");

		if(result.getCount() <  0 ){
			 
			if(N_PANEL.isShowing()){
				table_N.setParmValue(new TParm());
			}
			if(Y_PANEL.isShowing()){
				table_Y.setParmValue(new TParm());
			}
			return ;
		}
		
		String orgChnDesc = (String)result.getData( "ORG_CHN_DESC",0);
		setValue("ORG_CHN_DESC", orgChnDesc);

		if(N_PANEL.isShowing()){
			table_N.setParmValue(result);
			//���õ��ӱ�ǩ�ӿ�
//			for(int i = 0 ; i < result.getCount() ; i++ ){
//				TParm rowParm = (TParm)result.getRow(i);
//				String orderDesc = rowParm.getValue("ORDER_DESC");
//				EleTagControl.getInstance().login();
//				EleTagControl.getInstance().sendEleTag(rowParm.getValue("ELETAG_CODE"), orderDesc, rowParm.getValue("SPECIFICATION"), "", 50);
//			}
		}else if(Y_PANEL.isShowing()){
			table_Y.setParmValue(result);
		}
		
			
	}

	/**
	 * ��ת��س��¼�
	 * */
	public void onMonZHOUZX() {		
		onQuery();
		this.getTextField("ELETAG_CODE").grabFocus();		
	}
	
	/**
	 * ���ⵥ�Żس��¼�
	 */
	public void onDispenseNo(){
		onQuery();
		this.getTextField("ELETAG_CODE").grabFocus();
	}

	/**
	 * ��λ���ӱ�ǩ�Ļس��¼�
	 */
	public void onClickByElectTags() {
			
		//��λ���ӱ�ǩ
		String medCaseCode =  getValueString("ELETAG_CODE");
		if (StringUtil.isNullString(medCaseCode)){
	        return;
		}
		
		String boxEslId = getValueString("BOX_ESL_ID");	//��ת�� 
		String dispenseNo = getValueString("DISPENSE_NO");//���ⵥ��

		if(!StringUtil.isNullString(dispenseNo)){
			this.setValue("DISPENSE_NO", dispenseNo);
		}
		
		table_N.acceptText();
		
		//����IND_MATERIALLOC,�õ�ORDER_CODE
		TParm tabParm = table_N.getParmValue();
			
		String labelNo = "";
		String productName = "";
		String orgCode = "";
		String orderCode = "";
		String spec = "";
		int count = table_N.getParmValue().getCount();
		
		//�ж��Ƿ��ҵ�
		boolean b = false; 
    	TParm mRowParm = new TParm();
    	
		for (int i = 0; i < count; i++) {
			TParm dParm = tabParm.getRow(i);
			if(medCaseCode.equals(dParm.getValue("MATERIAL_LOC_CODE"))){
				//���ݳ��ⵥ�Ų�ѯIND_DISPENSEM����Ϣ
				TParm inParmSec = new TParm() ;
				dispenseNo = dParm.getValue("DISPENSE_NO");
				inParmSec.setData("DISPENSE_NO",dispenseNo);
				
		    	TParm mParm = SPCInStoreReginTool.getInstance().onQueryDispenseM(inParmSec);
		    	mRowParm = mParm.getRow(0);
				
		    	//��ʼ��ֵ 
				setValueAll(mRowParm);
				labelNo = medCaseCode;
				
				productName = dParm.getValue("ORDER_DESC");
				orgCode = dParm.getValue("ORG_CODE");
				orderCode= dParm.getValue("ORDER_CODE");
				//SPCPackageMachinePutUpTool.getInstance().updateDispensed(rowParm);
				onSave(mRowParm, dParm);
				b = true;
				
				table_N.removeRow(i);
				 
			}
			 
		}
		
		this.setValue("BOX_ESL_ID", boxEslId);
		this.clearValue("ELETAG_CODE");
		
		//�ǵģ����õ��ӱ�ǩ�ӿ���ʾ�������˸
//		if(b){
//			TParm inParm = new TParm();
//			inParm.setData("ORDER_CODE",orderCode);
//			inParm.setData("ORG_CODE",orgCode);
//			inParm.setData("ELETAG_CODE",eletagCode);
//			TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
//			spec = outParm.getValue("SPECIFICATION",0);
//			 
//			EleTagControl.getInstance().login();
//			EleTagControl.getInstance().sendEleTag(labelNo, productName, spec, outParm.getValue("QTY",0), 0);
//		}
		
		//onQuery() ;
		return;
	}
	
	
	/**
     * ���淽��
     */
    public void onSave(TParm mParm,TParm dParm) {
        
        /** ���±���(��ⵥ���ϸ��) */
        // 2.���������ж�
        String batchvalid = getBatchValid(mParm.getValue("REQTYPE_CODE"));
        // 3.��ⲿ�ſ���Ƿ���춯
        if (!getOrgBatchFlg(in_org_code)) {
            return;
        }
        if (!"THI".equals(request_type)) {
            // �����ҵ
            getDispenseOutIn(out_org_code, in_org_code, batchvalid,
                             out_flg, in_flg,mParm,dParm);
        }else {
            // ���������ҵ
            getDispenseOtherIn(in_org_code, in_flg,mParm,dParm);
        }
        
    }
    
    /**
     * �����ҵ
     *
     * @param out_org_code
     * @param in_org_code
     * @param batchvalid
     */
    private void getDispenseOutIn(String out_org_code, String in_org_code,
                                  String batchvalid, boolean out_flg,
                                  boolean in_flg,TParm mParm,TParm dParm) {

        TParm parm = new TParm();
        
        // ������Ϣ(OUT_M)
        parm = getDispenseMParm(parm, "3",mParm);
        
        // ϸ����Ϣ(OUT_D)
        if (!CheckDataD(dParm)) {
            return;
        }
        
        parm = getDispenseDParm(parm,dParm);
       
        if (!"".equals(in_org_code)) {
            // ��ⲿ��(IN_ORG)
            parm.setData("IN_ORG", in_org_code);
            // �Ƿ����(IN_FLG)
            parm.setData("IN_FLG", in_flg);
        }
        
        // ִ����������
        TParm result = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                            "onInsertIn", parm);

        // �����ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
               
    }
    
    /**
     * ���������ҵ
     * @param in_org_code String
     * @param flg boolean
     */
    private void getDispenseOtherIn(String in_org_code, boolean in_flg,TParm mParm,TParm dParm) {
        
        TParm parm = new TParm();
        parm.setData("ORG_CODE", in_org_code);
        // ʹ�õ�λ
        parm.setData("UNIT_TYPE", u_type);
        // ���뵥����
        parm.setData("REQTYPE_CODE", request_type);
         
        parm = getDispenseMParm(parm, "3",mParm);
        // ϸ����Ϣ(OUT_D)
        if (!CheckDataD(dParm)) {
            return;
        }
        parm = getDispenseDParm(parm,dParm);
        

        if (!"".equals(in_org_code)) {
            // ��ⲿ��(IN_ORG)
            parm.setData("IN_ORG", in_org_code);
            // �Ƿ����(IN_FLG)
            parm.setData("IN_FLG", in_flg);
        }
        // ִ����������
        parm = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                            "onInsertOtherIn", parm);
        // �����ж�
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }
    
	
	/**
     * ���������Ϣ
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm, String update_flg,TParm mParm ) {
        // ҩ�������Ϣ
        TParm sysParm = getSysParm();
        // �Ƿ��д����۸�
        String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
        parm.setData("REUPRICE_FLG", reuprice_flg);

        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
       
        parmM.setData("DISPENSE_NO", mParm.getValue("DISPENSE_NO"));
        
        //���뷽ʽ--ȫ��:APP_ALL,�˹�:APP_ARTIFICIAL,���콨��:APP_PLE,�Զ��β�:APP_AUTO
        parmM.setData("REQTYPE_CODE", mParm.getValue("REQTYPE_CODE"));
        parmM.setData("REQUEST_NO", mParm.getValue("REQUEST_NO"));
        parmM.setData("REQUEST_DATE", mParm.getValue("REQUEST_DATE"));
        parmM.setData("APP_ORG_CODE", mParm.getValue("APP_ORG_CODE"));
        parmM.setData("TO_ORG_CODE", mParm.getValue("TO_ORG_CODE"));
        parmM.setData("URGENT_FLG", mParm.getValue("URGENT_FLG"));
        parmM.setData("DESCRIPTION", mParm.getValue("DESCRIPTION"));
        parmM.setData("DISPENSE_DATE", mParm.getValue("WAREHOUSING_DATE"));
        parmM.setData("DISPENSE_USER", Operator.getID());
       
        if (!"1".equals(update_flg)) {
            parmM.setData("WAREHOUSING_DATE", SystemTool.getInstance().getDate());
            parmM.setData("WAREHOUSING_USER", Operator.getID());
        }else {
            parmM.setData("WAREHOUSING_DATE", tnull);
            parmM.setData("WAREHOUSING_USER", "");
        }
        
        //ҩƷ����--��ҩ:1,�龫��2
        parmM.setData("DRUG_CATEGORY","1");

        parmM.setData("REASON_CHN_DESC", mParm.getValue("REASON_CHN_DESC"));
        parmM.setData("UNIT_TYPE", mParm.getValue("UNIT_TYPE"));
        parmM.setData("UPDATE_FLG", update_flg);
        parmM.setData("OPT_USER", Operator.getID());
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        parmM.setData("REGION_CODE", Operator.getRegion());
        
        parmM.setData("CHECK_PUTAWAY","Y");
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
    private TParm getDispenseDParm(TParm parm,TParm rowParm) {
        TParm parmD = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        String batch_no = "";
        int count = 0;
        String order_code = "";
        String valid_date = "";
     
        parmD.setData("DISPENSE_NO", count,rowParm.getValue("DISPENSE_NO"));
        parmD.setData("SEQ_NO", count, rowParm.getValue("SEQ_NO"));
        parmD.setData("REQUEST_SEQ", count,rowParm.getInt("REQUEST_SEQ"));
        order_code = rowParm.getValue("ORDER_CODE");
        parmD.setData("ORDER_CODE", count, order_code);
        parmD.setData("QTY", count, rowParm.getDouble( "QTY"));
        parmD.setData("UNIT_CODE", count, rowParm.getValue("UNIT_CODE"));
        parmD.setData("RETAIL_PRICE", count, rowParm.getDouble("RETAIL_PRICE"));
        parmD.setData("STOCK_PRICE", count, rowParm.getDouble("STOCK_PRICE"));
        parmD.setData("ACTUAL_QTY", count, rowParm.getDouble("OUT_QTY"));
        parmD.setData("PHA_TYPE", count, rowParm.getValue("PHA_TYPE"));
      
        parmD.setData("BATCH_SEQ",count,rowParm.getInt("BATCH_SEQ"));
        
        //��Ӧ��
        parmD.setData("SUP_CODE",count,rowParm.getValue("SUP_CODE"));
        
        batch_no = rowParm.getValue("BATCH_NO");
        parmD.setData("BATCH_NO", count, batch_no);
        valid_date = rowParm.getValue("VALID_DATE");
        
        if ("".equals(valid_date)) {
            parmD.setData("VALID_DATE", count, tnull);
        }else {
            parmD.setData("VALID_DATE", count,rowParm.getTimestamp("VALID_DATE"));
        }
        
        parmD.setData("DOSAGE_QTY", count, rowParm.getDouble("DOSAGE_QTY"));
        parmD.setData("STOCK_QTY", count, rowParm.getDouble("STOCK_QTY"));
        parmD.setData("OPT_USER", count, Operator.getID());
        parmD.setData("OPT_DATE", count, date);
        parmD.setData("OPT_TERM", count, Operator.getIP());
        parmD.setData("REGION_CODE", count, Operator.getRegion());
        parmD.setData("PUTAWAY_USER",count,Operator.getID());
        parmD.setData("PUTAWAY_DATE",count,SystemTool.getInstance().getDate());
        parmD.setData("IS_PUTAWAY",count,"Y");
        
        //���ռ۸�
        parmD.setData("VERIFYIN_PRICE",count,rowParm.getDouble("VERIFYIN_PRICE"));
        //���м۸�
        parmD.setData("INVENT_PRICE",count,rowParm.getDouble("INVENT_PRICE"));
        
        parmD.setData("SUP_ORDER_CODE",count,rowParm.getValue("SUP_ORDER_CODE"));
        count++;
        
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        
        return parm;
    }
    
    public void setValueAll(TParm mParm){
    	request_type = mParm.getValue("REQTYPE_CODE");
    	if ("TEC".equals(request_type) || "EXM".equals(request_type) || "COS".equals(request_type)) {
                u_type = "1";
            }else if ("DEP".equals(request_type)) {
                u_type = IndSysParmTool.getInstance().onQuery().getValue(
                    "UNIT_TYPE", 0);
            }else {
                u_type = "0";
            }
    	
            if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
                out_org_code = mParm.getValue("TO_ORG_CODE");
                out_flg = true;
                in_org_code = mParm.getValue("APP_ORG_CODE");
                in_flg = true;
            }else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
                out_org_code = mParm.getValue("APP_ORG_CODE");
                out_flg = true;
                in_org_code = mParm.getValue("TO_ORG_CODE");
                in_flg = true;
            }else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
                out_org_code = mParm.getValue("TO_ORG_CODE");
                out_flg = true;
                in_org_code = mParm.getValue("APP_ORG_CODE");
                in_flg = false;
            }else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
                out_org_code = mParm.getValue("APP_ORG_CODE");
                out_flg = true;
                in_org_code = mParm.getValue("TO_ORG_CODE");
                in_flg = false;
            }else if ("THI".equals(request_type)) {
                out_org_code = mParm.getValue("TO_ORG_CODE");
                out_flg = false;
                in_org_code = mParm.getValue("APP_ORG_CODE");
                in_flg = true;
            }
    }
    
    
    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataD(TParm dParm) {
        // �ж�������ȷ��
        double qty = dParm.getDouble( "OUT_QTY");
        if (qty <= 0) {
            this.messageBox("�������������С�ڻ����0");
            return false;
        }
        double price = dParm.getDouble("STOCK_PRICE");
        if (price <= 0) {
            this.messageBox("�ɱ��۲���С�ڻ����0");
            return false;
        }
       
        return true;
    }
    
    
    /**
     * ����Ƿ���춯״̬�ж�
     *
     * @param org_code
     * @return
     */
    private boolean getOrgBatchFlg(String org_code) {
        // ����Ƿ���춯״̬�ж�
        if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
            this.messageBox("ҩ�����ι���������ʾ�����ֶ����ս�");
            return false;
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
	 * TPanel�ı��¼�
	 * */
	public void onTPanlClick() {
		onQuery();
	}
	
	

	/**
	 * ��ղ���
	 * */
	public void onClear() {
		String controlName = "DISPENSE_NO;BOX_ESL_ID;ORG_CHN_DESC;ELETAG_CODE";
		this.clearValue(controlName);
		this.getTextField("BOX_ESL_ID").setEditable(true);
		this.getTextField("DISPENSE_NO").setEditable(true);
		this.getTextField("ORG_CHN_DESC").setEditable(true);
		table_N.removeRowAll();
		table_Y.removeRowAll();
		this.getTextField("BOX_ESL_ID").grabFocus();
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

    /**
     * ҩ�������Ϣ
     * @return TParm
     */
    private TParm getSysParm(){
        return IndSysParmTool.getInstance().onQuery();
    }
    
	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	
	
		
}
