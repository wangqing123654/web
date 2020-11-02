package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;

public class InStoreCommonControl extends TControl {


    // ������
    private TTable table_m;

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

	private static final InStoreCommonControl INSTANCE =new InStoreCommonControl();
	
	public InStoreCommonControl() {

	}

	public static InStoreCommonControl getInstance(){
	     return INSTANCE;
	}
	

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
         }
         // ִ����������
         TParm result = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                             "onInsertIn", parm);
         
         return result;
    	 
    	
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
            parmM.setData("WAREHOUSING_DATE", getValue("WAREHOUSING_DATE"));
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
        parmM.setData("OPT_USER", Operator.getID());
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
            
            parmD.setData("DISPENSE_NO", count, dispense_no);
            if (!"THI".equals(this.getValueString("REQTYPE_CODE"))) {
                parmD.setData("SEQ_NO", count, resultParm.getInt("SEQ_NO", i));
            }
            else {
                parmD.setData("SEQ_NO", count, seq + count);
            }
            parmD.setData("REQUEST_SEQ", count, resultParm.getInt(
                "REQUEST_SEQ", i));
            parmD.setData("ORDER_CODE", count,
                          resultParm.getValue("ORDER_CODE", i));
            parmD.setData("QTY", count, table_d.getItemDouble(i, "QTY"));
            parmD.setData("UNIT_CODE", count, table_d.getItemString(i,
                "UNIT_CODE"));
            parmD.setData("RETAIL_PRICE", count, table_d.getItemDouble(i,
                "RETAIL_PRICE"));
            parmD.setData("STOCK_PRICE", count, table_d.getItemDouble(i,
                "STOCK_PRICE"));
            parmD.setData("ACTUAL_QTY", count, table_d.getItemDouble(i,
                "OUT_QTY"));
            parmD.setData("PHA_TYPE", count, table_d.getItemString(i,
                "PHA_TYPE"));
            parmD.setData("BATCH_SEQ", count, table_d.getParmValue().getValue(
                "BATCH_SEQ",i));
            parmD.setData("BATCH_NO", count,
                          table_d.getItemString(i, "BATCH_NO"));
            parmD.setData("VALID_DATE", count, table_d.getItemData(i,
                "VALID_DATE"));
            parmD.setData("DOSAGE_QTY", count, resultParm.getDouble(
                "DOSAGE_QTY", i));
            parmD.setData("STOCK_QTY", count, resultParm.getDouble(
                "STOCK_QTY", i));
            parmD.setData("REGION_CODE", count, Operator.getRegion());
            parmD.setData("OPT_USER", count, Operator.getID());
            parmD.setData("OPT_DATE", count, date);
            parmD.setData("OPT_TERM", count, Operator.getIP());
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
        table_d.acceptText();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            // �ж�������ȷ��
            double qty = table_d.getItemDouble(i, "OUT_QTY");
            if (qty <= 0) {
                this.messageBox("�������������С�ڻ����0");
                return false;
            }
            double price = table_d.getItemDouble(i, "STOCK_PRICE");
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
}
