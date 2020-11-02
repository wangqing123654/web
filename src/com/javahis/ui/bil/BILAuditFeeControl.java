package com.javahis.ui.bil;

import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import jdo.ibs.IBSBillmTool;
import jdo.adm.ADMInpTool;
import jdo.bil.BILInvoiceTool;
import jdo.ibs.IBSOrderdTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.OdiUtil;

import jdo.ibs.IBSTool;

/**
 * <p>Title: �������</p>
 *
 * <p>Description: �������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl  2010.06.12
 * @version 1.0
 */
public class BILAuditFeeControl extends TControl {
    TParm data;
    int selectRow = -1;
    TParm endParm;
    //�������
    
    String caseNo = "";
    public void onInit() {
    	
        super.onInit();
        //�˵�����tableר�õļ���
        getTTable("TableM").addEventListener(TTableEvent.
                                             CHECK_BOX_CLICKED, this,
                                             "onTableMComponent");
        callFunction("UI|newBill|setEnabled", false);
        
        onClear();
    }

    /**
     * �õ�TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
    	callFunction("UI|newBill|setEnabled", false);
        if (getValue("MR_NO").equals(null) ||
            getValue("MR_NO").toString().length() == 0) {
            this.messageBox("�����벡����");
            return;
        }
        setValue("MR_NO",
                 PatTool.getInstance().checkMrno(TCM_Transform.
                                                 getString(getValue("MR_NO"))));
        TParm parm = new TParm();
        parm.setData("MR_NO", this.getValueString("MR_NO"));
        TParm selPatInfo = PatTool.getInstance().getInfoForMrno(getValueString(
                "MR_NO"));
        if (selPatInfo.getErrCode() < 0) {
            err(selPatInfo.getErrName() + " " + selPatInfo.getErrText());
            return;
        }
        setValue("PAT_NAME", selPatInfo.getValue("PAT_NAME", 0));
        //===zhangp 20120815 start
        String sql =
        	" SELECT CASE_NO, IPD_NO" +
        	" FROM ADM_INP" +
        	" WHERE MR_NO = '" + this.getValueString("MR_NO")+"'" ;//==modify by caowl 20120911
        TParm admInpParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (admInpParm.getErrCode() < 0) {
            err(admInpParm.getErrName() + " " + admInpParm.getErrText());
            return;
        }
        if(admInpParm.getCount() == 1){
        	caseNo = admInpParm.getValue("CASE_NO", 0);
        }
        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate());
        if(admInpParm.getCount() > 1 ){//==modify by caowl 20120911 ȥ��admInpParm.getCount() < 1
    		TParm inparm = new TParm();
    		inparm.setData("MR_NO", pat.getMrNo());
    		inparm.setData("PAT_NAME", pat.getName());
    		inparm.setData("SEX_CODE", pat.getSexCode());
    		inparm.setData("AGE", age);
    		// �ж��Ƿ����ϸ�㿪�ľ����ѡ��
    		inparm.setData("count", "0");
    		TParm caseNoParm = (TParm) openDialog(
    				"%ROOT%\\config\\bil\\BILChooseVisit.x", inparm);
    		caseNo = caseNoParm.getValue("CASE_NO");
        }
        parm.setData("CASE_NO",caseNo);
        
       
           
        //===zhangp 20120815 end
        setValue("IPD_NO", admInpParm.getValue("IPD_NO", 0));
        TParm billmParm = IBSBillmTool.getInstance().selAuditFee(parm);
        
        
        
        
        if (billmParm.getErrCode() < 0) {
            err(billmParm.getErrName() + " " + billmParm.getErrText());
            return;
        }
        if (billmParm.getCount() <= 0) {
            this.messageBox("���������!");
            return;
        }
       
        
        //$--- add caoyng 20145/23 ����������ʾ---start---
        TParm newBorn=getNewBorn(caseNo);//���ҸĲ����Ƿ���������
        if(newBorn.getCount()>0&&newBorn!=null){
        	TParm newBparm= getnewTParm(newBorn);//���ӵķ���
        	if(newBparm.getCount("MR_NO")>0){
        		billmParm.addParm(newBparm);
              }
        	
        }
        //$--- add caoyng 20145/23 ����������ʾ---end---  
        
        
        //===modify by caowl 20120911 start
        
//        for(int i =0;i<billmParm.getCount();i++){
//        	billmParm.setData("UPDFLG",i,this.getValueBoolean("UPDFLG"));
//        	UPDFLG
//        }  //===liling
        //====modify by caowl 20120911 end
      
      
        this.getTTable("TableM").setParmValue(billmParm);
    }


    /**
     * ��˱���
     */
    public void onSave() {
    	TTable tableM = (TTable) getComponent("TableM");//δ���˵����
    	TParm parmM = tableM.getParmValue();
//    	System.out.println("parmM::"+parmM);
		for (int a = 0; a < parmM.getCount("CASE_NO"); a++) {
			String caseNo = parmM.getValue("CASE_NO", a);//�������������Ҫ�������þֲ�����--xiongwg20150401
			// System.out.println("caseNo::"+caseNo);
			boolean flg = IBSTool.getInstance().checkData(caseNo);

			if (!flg) {
				this.messageBox("����δ�����˵���ҽ����Ϣ��������˵�����");
				callFunction("UI|newBill|setEnabled", true);
				
				return;
				// System.out.println("����δ�����˵���ҽ����Ϣ");
			}
			TParm actionParm = new TParm();
			TParm result = new TParm();

			if (endParm == null) {
				this.messageBox("û��Ҫ���µ��˵�");
				// System.out.println("û��Ҫ���µ��˵�  endParm==null");
				return;
			}

			if ((endParm.getCount("BILL_NO") == -1)) {
				this.messageBox("û��Ҫ���µ��˵�");
				// System.out.println("û��Ҫ���µ��˵� endParm.getCount() == -1");
				return;
			}
			int count = endParm.getCount("BILL_NO");
			// System.out.println("endParm:::"+endParm);
			// System.out.println("ѭ����ʼ");
			for (int i = 0; i < count; i++) {
				// System.out.println("��"+i+"��ѭ��ִ��");
				String billNo = "";
				billNo = endParm.getValue("BILL_NO", i);
				actionParm.setData("BILL_NO", billNo);
				// ======modify by caowl 20120911 start
				boolean approve_flg = endParm.getBoolean("APPROVE_FLG", i);
				// System.out.println("approve_flg:::"+approve_flg);
				if (approve_flg) {
					// ���
					// System.out.println("���ִ��");

					actionParm.setData("APPROVE_FLG", "Y");
					result = TIOM_AppServer.executeAction(
							"action.bil.BILAction", "onSaveAuditFee",
							actionParm);
					// System.out.println("���ִ�н��result:::"+result);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						return;
					}
				} else {

					// ȡ�����
					// �ж��Ƿ��Ѿ��걨
					// System.out.println("ȡ�����");
					String sql = "SELECT IN_STATUS FROM INS_ADM_CONFIRM WHERE CASE_NO = '"
							+ caseNo + "'";

					TParm selParm = new TParm(TJDODBTool.getInstance().select(
							sql));
					if (selParm != null
							&& selParm.getData("IN_STATUS", 0) != null
							&& selParm.getData("IN_STATUS", 0).equals("2")) {
						this.messageBox("��δȡ���걨��");
						return;
					}
					actionParm.setData("APPROVE_FLG", "N");
					result = TIOM_AppServer.executeAction(
							"action.bil.BILAction", "onSaveAuditFee",
							actionParm);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						return;
					}
				}
				// =====modify by caowl 20120911 end
				// System.out.println("ѭ������");
			}
			String sql = "SELECT BILL_STATUS FROM ADM_INP WHERE CASE_NO ='"
					+ caseNo + "'";
			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
			String bill_status = selParm.getValue("BILL_STATUS", 0);
			if (bill_status.equals("0")) {

			} else {
				TParm acParm = new TParm();
				acParm.setData("CASE_NO", caseNo);
				result = TIOM_AppServer.executeAction("action.bil.BILAction",
						"onAuditFeeCheck", acParm);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					// ִ��ʧ��
					this.messageBox("E0005");
					return;
				}
			}
		}	
      
        //ִ�гɹ�
        this.messageBox("P0005");
        this.onClear();
    }

    /**
     * ȡ�����
     */
    public void onReturn() {

        boolean flg = IBSTool.getInstance().checkData(caseNo);
        if (!flg) {
            this.messageBox("��δ�����˵���ҽ����Ϣ,�����²����˵�");
            return;
        }
        TParm actionParm = new TParm();
        TParm result = new TParm();
        int count = endParm.getCount("BILL_NO");
        for (int i = 0; i < count; i++) {
            String billNo = "";
            billNo = endParm.getValue("BILL_NO", i);
            actionParm.setData("BILL_NO", billNo);
            actionParm.setData("APPROVE_FLG", "N");
            result = TIOM_AppServer.executeAction("action.bil.BILAction",
                                                  "onSaveAuditFee", actionParm);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return;
            }
        }
        TParm acParm = new TParm();
        acParm.setData("CASE_NO", caseNo);
        result = TIOM_AppServer.executeAction("action.bil.BILAction",
                                              "onAuditFeeCheck", acParm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            //ִ��ʧ��
            this.messageBox("E0005");
            return;
        }
        //ִ�гɹ�
        this.messageBox("P0005");
        this.onClear();

    }

    /**
     * ��������¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableMComponent(Object obj) {
        TTable tableM = (TTable) obj;
        tableM.acceptText();
        TParm tableParm = tableM.getParmValue();
        endParm = new TParm();
        int count = tableParm.getCount("BILL_NO");
        //System.out.println("count::"+count);
        for (int i = 0; i < count; i++) {
        	//System.out.println("tableParm.getData(,i)::"+tableParm.getData("UPDFLG",i));
//        	if(tableParm.getData("UPDFLG",i).equals("Y")){//modify by caowl 20120911  
//        		UPDFLG//==liling
        		endParm.addData("APPROVE_FLG", tableParm.getValue("APPROVE_FLG",i));//modify by caowl 20120911
                endParm.addData("BILL_NO",
                                tableParm.getValue("BILL_NO", i));
                endParm.addData("AR_AMT",
                                tableParm.getValue("AR_AMT", i));
//            }
        }
        int feeCount = endParm.getCount("AR_AMT");
        double totAmt = 0.00;
        for (int j = 0; j < feeCount; j++) {
            totAmt = totAmt + endParm.getDouble("AR_AMT", j);
        }
        return true;
    }

    /**
     * ����ĵ����¼�
     */
    public void onTableMClicked() {
        TTable TableM = getTTable("TableM");
        TTable TableD = getTTable("TableD");
        int row = TableM.getSelectedRow();
        if (row < 0)
            return;
        TableD.removeRowAll();//add caoyong 2014/5/23
        TParm parm = new TParm();
        TParm regionParm = TableM.getParmValue();
        parm.setData("BILL_NO", regionParm.getData("BILL_NO", row));
        parm.setData("CASE_NO", regionParm.getData("CASE_NO", row));//=====caowl 20120927
        TParm data = IBSOrderdTool.getInstance().selAuditFeeData(parm);
        TableD.setParmValue(data);
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("REDUCE_REASON;IPD_NO;MR_NO;PAT_NAME");
        this.callFunction("UI|TableM|removeRowAll");
        this.callFunction("UI|TableD|removeRowAll");
        selectRow = -1;
        caseNo="";
        endParm=new TParm();
    }

    /**
     * У������
     * @param flg String
     */
    public void checkData(String flg) {
    }
    /**
     * add caoyong  2014��5��23
     * ĸ�׺ͺ��ӵķ���һ�����
     * @return
     */
    public TParm getnewTParm(TParm newBorn){
    	 TParm newParm=new TParm();
         TParm newResult=new TParm();
         TParm newBparm=new TParm();
       
     	   this.messageBox("�˲�����������");
     	   
     	   for(int i=0;i<newBorn.getCount();i++){//һ�����߶�����ӵķ���
     		   newParm.setData("MR_NO", newBorn.getValue("MR_NO", i));
     		   newParm.setData("CASE_NO", newBorn.getValue("CASE_NO", i));
     		   newResult=IBSBillmTool.getInstance().selAuditFee(newParm);//ÿ�����ӵķ��ò�ѯ
     		   if(newResult.getCount()>0){
     			   for(int j=0;j<newResult.getCount();j++){
     				    newBparm.addRowData(newResult, j) ; 
     			   }
     		   }
     	   }
           return newBparm;
    }
    
    
    /**
     * add caoyong  2014��5��23
     * @param ��ѯ�Ƿ���������
     * @return
     */
    public TParm getNewBorn(String caseno){
    	String sql="SELECT MR_NO,CASE_NO FROM ADM_INP WHERE M_CASE_NO='"+caseno+"' " +
    			   "AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' AND NEW_BORN_FLG='Y'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	
    	  return result;
    }
    /**
     * �����ɷ���ҵ
     *  =====pangben 2014-7-7 
     */
    public void onBilIBSRecp(){
//    	this.setTitle("�ɷ���ҵ");
    	if (null==caseNo||caseNo.length()<=0) {
			this.messageBox("���ò�����Ϣ");
			return;
		}
		if (!checkNo()) {
			// ��δ�������ȿ���!
			this.messageBox("E0014");
			return;
		}
		
		this.openWindow("%ROOT%\\config\\bil\\BilIBSRecp.x", runPane());
		
		
		//getTPanel().addItem(tag, "%ROOT%\\config\\bil\\BilIBSRecp.x", runPane(), false);
		//this.runPane("IBSCUTBILLSTATION", "bil\\BilIBSRecp.x");
    }
    /**
     * У���Ƿ��Ѿ�����
     * @return
     *  =====pangben 2014-7-7 
     */
    public boolean checkNo() {
		TParm parm = new TParm();
		parm.setData("RECP_TYPE", "IBS");
		parm.setData("CASHIER_CODE", Operator.getID());
		parm.setData("STATUS", "0");
		parm.setData("TERM_IP", Operator.getIP());
		TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parm);
		String updateNo = noParm.getValue("UPDATE_NO", 0);
		if (updateNo == null || updateNo.length() == 0) {
			return false;
		}
		return true;
	}
    /**
     * �ɷ���ҵ�������
     * @return
     * =====pangben 2014-7-7 
     */
    private TParm runPane(){
    	
    	TParm action=new TParm();
    	String sql="SELECT A.BED_NO,A.LUMPWORK_CODE,A.VS_DR_CODE AS VC_CODE,A.STATION_CODE," +//==liling 20140813 add AND A.DS_DATE IS NOT NULL  
    			"A.IN_DATE,A.DEPT_CODE,A.CLNCPATH_CODE,B.SEX_CODE,B.BIRTH_DATE,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE FROM ADM_INP A,SYS_PATINFO B   WHERE A.MR_NO=B.MR_NO AND A.CASE_NO='"+caseNo+"' AND A.DS_DATE IS NOT NULL";
    	TParm actionParm=new TParm(TJDODBTool.getInstance().select(sql));
     	if(actionParm.getCount()<=0){  
     		this.messageBox("������Ժ");
     		return null;
     	}
    	action.setData("IBS", "CASE_NO", caseNo);
		action.setData("IBS", "VS_DR_CODE", actionParm.getValue("VC_CODE",0));
		action.setData("ODI", "IPD_NO", this.getValue("IPD_NO"));
		action.setData("ODI", "MR_NO", this.getValue("MR_NO"));
		action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAME"));
		action.setData("ODI", "SEX_CODE", actionParm.getValue("SEX_CODE",0));
		action.setData("IBS", "LUMPWORK_CODE", null!=actionParm.getData("LUMPWORK_CODE",0)?actionParm.getData("LUMPWORK_CODE",0):"");
		action.setData("ODI", "STATION_CODE", actionParm.getValue("STATION_CODE",0));
		action.setData("ODI", "DEPT_CODE", actionParm.getValue("DEPT_CODE",0));
		// ����
		action.setData("ODI", "BED_NO", null!=actionParm.getData("BED_NO",0)?actionParm.getData("BED_NO",0):"");
		// ����Ȩ��ע��
		action.setData("IBS", "SAVE_FLG", true);
		//�ٴ�·��������ʾ
		action.setData("IBS", "CLNCPATH_CODE", actionParm.getData("CLNCPATH_CODE",0));
		// ���1
		action.setData("IBS", "CTZ1_CODE", actionParm.getData("CTZ1_CODE",0));
		// ���2
		action.setData("IBS", "CTZ2_CODE", actionParm.getData("CTZ2_CODE",0));
		// ���3
		action.setData("IBS", "CTZ3_CODE", actionParm.getData("CTZ3_CODE",0));
		action.setData("ODI", "ADM_DATE", actionParm.getData("IN_DATE",0));
		action.setData("ODI", "BIRTH_DATE",actionParm.getData("BIRTH_DATE",0));
//		action.setData("IBS", "BIL_AUDIT_FLG", "Y");//==20140716 ADD 
		return action;
    }
    
    //�����˵�--�����ʾ��δ�����˵���ҽ��ʱ����
    public void onNewBill(){
    	int Count = this.messageBox("��ʾ��Ϣ", "�Ƿ�����˵�", 0);
		if (Count ==0) {
			TParm parm=new TParm();
			parm.setData("CASE_NO",caseNo);
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
                     "onNewBill", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("�����˵���������");
				err(result.getErrName() + " " + result.getErrText());
				return;
				}
			this.messageBox("�����ɹ���");
			     this.callFunction("UI|TableM|removeRowAll");
			     this.callFunction("UI|TableD|removeRowAll");
			  onQuery();
		}else{
			return;
		}
    }
}
