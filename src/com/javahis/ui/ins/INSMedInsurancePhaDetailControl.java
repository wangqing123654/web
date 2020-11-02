package com.javahis.ui.ins;

import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: ����л���ҽ�Ʊ���סԺҩƷ��ϸ��</p>
 *
 * <p>Description:����л���ҽ�Ʊ���סԺҩƷ��ϸ��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lim  
 * @version 1.0
 */
public class INSMedInsurancePhaDetailControl extends TControl {
	
	/**
	 * ��ʼ��
	 */
	 public void onInit(){
		 onClear();
	}
	
	/**
	 * 
	 * �����û��Ƿ�����.
	 * @param mrNo
	 * @param outDate
	 * @return
	 */
	private boolean checkInput(String mrNo,String outDate){
		if("".equals(mrNo)){
			this.messageBox("�����Ų���Ϊ��");
			((TTextField) this.getComponent("MR_NO")).grabFocus() ;
			return false ;
		}
		if("".equals(outDate)){
			this.messageBox("��Ժ���ڲ���Ϊ��");
			((TTextField) this.getComponent("OUT_DATE")).grabFocus() ;
			return false ;			
		}
		return true ;
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		String mrNo = this.getValueString("MR_NO") ;
		String outDate = this.getValueString("OUT_DATE") ;
		if(!checkInput(mrNo, outDate)){
			return ;
		}
		
		String[] outDateArray = outDate.split(" ") ;
		String[] newOutDateArray = outDateArray[0].split("-") ;
		String newOutDate = newOutDateArray[0]+"/"+newOutDateArray[1]+"/"+newOutDateArray[2] ;
		
		String sqlMaster = "Select REGION_CODE,YEAR_MON,MR_NO,PAT_NAME,IDNO,ADM_SEQ " +
				     "from INS_IBS " +
				     "where  mr_no='"+mrNo+"' and to_char(ds_date,'yyyy/mm/dd') ='"+newOutDate+"'" ;


		TParm result = new TParm(TJDODBTool.getInstance().select(sqlMaster));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			messageBox("��������");
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			return;
		}
		String admSeq = result.getValue("ADM_SEQ", 0) ;
		String sqlDetail = "Select d.NHI_ORDER_CODE,d.ORDER_DESC,d.SEQ_NO,d.QTY,d.PRICE,d.TOTAL_AMT ,d.TOTAL_NHI_AMT, (d.TOTAL_NHI_AMT- d.REFUSE_AMT) as APPROVAL_AMT, d.REFUSE_AMT , d.REFUSE_REASON_NOTE " +
				           "from INS_IBSORDER_DOWNLOAD d ,sys_fee f " +
				           "where d.adm_seq='"+admSeq+"' and   d.order_code = f.order_code " +
				           "and  f.ACTIVE_FLG='Y' and  f.order_cat1_code='PHA' " +
				           "order by  seq_no" ;
		
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sqlDetail));
		if (result1.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result1.getCount() <= 0) {
			messageBox("��������");
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			return;
		}
		this.callFunction("UI|TTABLE|setParmValue", result1);
	}

    /**
     * �����Ų�ѯ(�س��¼�)
     */
	public void onMrNoAction(){
		String mrNo = this.getValueString("MR_NO") ;
		this.setValue("MR_NO", PatTool.getInstance().checkMrno(mrNo)) ;
	}

	/**
	 * ���
	 */
	public void onClear(){
		this.setValue("MR_NO", "") ;//סԺ����
		this.setValue("OUT_DATE", "");
		this.callFunction("UI|TTABLE|setParmValue", new TParm());	
		((TTextField) this.getComponent("MR_NO")).grabFocus() ;
	}
	
	/**
	 * ���
	 */
    public void onExport(){
    	TTable table = (TTable)this.getComponent("TTABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        } 
        ExportExcelUtil.getInstance().exportExcel(table, "����л���ҽ�Ʊ���סԺҩƷ��ϸ��");   	
    }
}
