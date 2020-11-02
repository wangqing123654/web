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
 * <p>Title: 费用审核</p>
 *
 * <p>Description: 费用审核</p>
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
    //就诊序号
    
    String caseNo = "";
    public void onInit() {
    	
        super.onInit();
        //账单主档table专用的监听
        getTTable("TableM").addEventListener(TTableEvent.
                                             CHECK_BOX_CLICKED, this,
                                             "onTableMComponent");
        callFunction("UI|newBill|setEnabled", false);
        
        onClear();
    }

    /**
     * 得到TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * 查询
     */
    public void onQuery() {
    	callFunction("UI|newBill|setEnabled", false);
        if (getValue("MR_NO").equals(null) ||
            getValue("MR_NO").toString().length() == 0) {
            this.messageBox("请输入病案号");
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
        if(admInpParm.getCount() > 1 ){//==modify by caowl 20120911 去掉admInpParm.getCount() < 1
    		TParm inparm = new TParm();
    		inparm.setData("MR_NO", pat.getMrNo());
    		inparm.setData("PAT_NAME", pat.getName());
    		inparm.setData("SEX_CODE", pat.getSexCode());
    		inparm.setData("AGE", age);
    		// 判断是否从明细点开的就诊号选择
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
            this.messageBox("无审核数据!");
            return;
        }
       
        
        //$--- add caoyng 20145/23 有新生儿提示---start---
        TParm newBorn=getNewBorn(caseNo);//查找改病患是否有新生儿
        if(newBorn.getCount()>0&&newBorn!=null){
        	TParm newBparm= getnewTParm(newBorn);//孩子的费用
        	if(newBparm.getCount("MR_NO")>0){
        		billmParm.addParm(newBparm);
              }
        	
        }
        //$--- add caoyng 20145/23 有新生儿提示---end---  
        
        
        //===modify by caowl 20120911 start
        
//        for(int i =0;i<billmParm.getCount();i++){
//        	billmParm.setData("UPDFLG",i,this.getValueBoolean("UPDFLG"));
//        	UPDFLG
//        }  //===liling
        //====modify by caowl 20120911 end
      
      
        this.getTTable("TableM").setParmValue(billmParm);
    }


    /**
     * 审核保存
     */
    public void onSave() {
    	TTable tableM = (TTable) getComponent("TableM");//未结账单表格
    	TParm parmM = tableM.getParmValue();
//    	System.out.println("parmM::"+parmM);
		for (int a = 0; a < parmM.getCount("CASE_NO"); a++) {
			String caseNo = parmM.getValue("CASE_NO", a);//多条数据审核需要重新设置局部变量--xiongwg20150401
			// System.out.println("caseNo::"+caseNo);
			boolean flg = IBSTool.getInstance().checkData(caseNo);

			if (!flg) {
				this.messageBox("还有未产生账单的医嘱信息，请操作账单调整");
				callFunction("UI|newBill|setEnabled", true);
				
				return;
				// System.out.println("还有未产生账单的医嘱信息");
			}
			TParm actionParm = new TParm();
			TParm result = new TParm();

			if (endParm == null) {
				this.messageBox("没有要更新的账单");
				// System.out.println("没有要更新的账单  endParm==null");
				return;
			}

			if ((endParm.getCount("BILL_NO") == -1)) {
				this.messageBox("没有要更新的账单");
				// System.out.println("没有要更新的账单 endParm.getCount() == -1");
				return;
			}
			int count = endParm.getCount("BILL_NO");
			// System.out.println("endParm:::"+endParm);
			// System.out.println("循环开始");
			for (int i = 0; i < count; i++) {
				// System.out.println("第"+i+"次循环执行");
				String billNo = "";
				billNo = endParm.getValue("BILL_NO", i);
				actionParm.setData("BILL_NO", billNo);
				// ======modify by caowl 20120911 start
				boolean approve_flg = endParm.getBoolean("APPROVE_FLG", i);
				// System.out.println("approve_flg:::"+approve_flg);
				if (approve_flg) {
					// 审核
					// System.out.println("审核执行");

					actionParm.setData("APPROVE_FLG", "Y");
					result = TIOM_AppServer.executeAction(
							"action.bil.BILAction", "onSaveAuditFee",
							actionParm);
					// System.out.println("审核执行结果result:::"+result);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						return;
					}
				} else {

					// 取消审核
					// 判断是否已经申报
					// System.out.println("取消审核");
					String sql = "SELECT IN_STATUS FROM INS_ADM_CONFIRM WHERE CASE_NO = '"
							+ caseNo + "'";

					TParm selParm = new TParm(TJDODBTool.getInstance().select(
							sql));
					if (selParm != null
							&& selParm.getData("IN_STATUS", 0) != null
							&& selParm.getData("IN_STATUS", 0).equals("2")) {
						this.messageBox("还未取消申报！");
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
				// System.out.println("循环结束");
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
					// 执行失败
					this.messageBox("E0005");
					return;
				}
			}
		}	
      
        //执行成功
        this.messageBox("P0005");
        this.onClear();
    }

    /**
     * 取消审核
     */
    public void onReturn() {

        boolean flg = IBSTool.getInstance().checkData(caseNo);
        if (!flg) {
            this.messageBox("有未产生账单的医嘱信息,请重新产生账单");
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
            //执行失败
            this.messageBox("E0005");
            return;
        }
        //执行成功
        this.messageBox("P0005");
        this.onClear();

    }

    /**
     * 主表监听事件
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
     * 主表的单击事件
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
     * 清空
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
     * 校验数据
     * @param flg String
     */
    public void checkData(String flg) {
    }
    /**
     * add caoyong  2014、5、23
     * 母亲和孩子的费用一起审核
     * @return
     */
    public TParm getnewTParm(TParm newBorn){
    	 TParm newParm=new TParm();
         TParm newResult=new TParm();
         TParm newBparm=new TParm();
       
     	   this.messageBox("此病患有新生儿");
     	   
     	   for(int i=0;i<newBorn.getCount();i++){//一个或者多个孩子的费用
     		   newParm.setData("MR_NO", newBorn.getValue("MR_NO", i));
     		   newParm.setData("CASE_NO", newBorn.getValue("CASE_NO", i));
     		   newResult=IBSBillmTool.getInstance().selAuditFee(newParm);//每个孩子的费用查询
     		   if(newResult.getCount()>0){
     			   for(int j=0;j<newResult.getCount();j++){
     				    newBparm.addRowData(newResult, j) ; 
     			   }
     		   }
     	   }
           return newBparm;
    }
    
    
    /**
     * add caoyong  2014、5、23
     * @param 查询是否有新生儿
     * @return
     */
    public TParm getNewBorn(String caseno){
    	String sql="SELECT MR_NO,CASE_NO FROM ADM_INP WHERE M_CASE_NO='"+caseno+"' " +
    			   "AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' AND NEW_BORN_FLG='Y'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	
    	  return result;
    }
    /**
     * 操作缴费作业
     *  =====pangben 2014-7-7 
     */
    public void onBilIBSRecp(){
//    	this.setTitle("缴费作业");
    	if (null==caseNo||caseNo.length()<=0) {
			this.messageBox("请获得病患信息");
			return;
		}
		if (!checkNo()) {
			// 尚未开账请先开账!
			this.messageBox("E0014");
			return;
		}
		
		this.openWindow("%ROOT%\\config\\bil\\BilIBSRecp.x", runPane());
		
		
		//getTPanel().addItem(tag, "%ROOT%\\config\\bil\\BilIBSRecp.x", runPane(), false);
		//this.runPane("IBSCUTBILLSTATION", "bil\\BilIBSRecp.x");
    }
    /**
     * 校验是否已经开帐
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
     * 缴费作业操作入参
     * @return
     * =====pangben 2014-7-7 
     */
    private TParm runPane(){
    	
    	TParm action=new TParm();
    	String sql="SELECT A.BED_NO,A.LUMPWORK_CODE,A.VS_DR_CODE AS VC_CODE,A.STATION_CODE," +//==liling 20140813 add AND A.DS_DATE IS NOT NULL  
    			"A.IN_DATE,A.DEPT_CODE,A.CLNCPATH_CODE,B.SEX_CODE,B.BIRTH_DATE,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE FROM ADM_INP A,SYS_PATINFO B   WHERE A.MR_NO=B.MR_NO AND A.CASE_NO='"+caseNo+"' AND A.DS_DATE IS NOT NULL";
    	TParm actionParm=new TParm(TJDODBTool.getInstance().select(sql));
     	if(actionParm.getCount()<=0){  
     		this.messageBox("请办理出院");
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
		// 床号
		action.setData("ODI", "BED_NO", null!=actionParm.getData("BED_NO",0)?actionParm.getData("BED_NO",0):"");
		// 保存权限注记
		action.setData("IBS", "SAVE_FLG", true);
		//临床路径代码显示
		action.setData("IBS", "CLNCPATH_CODE", actionParm.getData("CLNCPATH_CODE",0));
		// 身份1
		action.setData("IBS", "CTZ1_CODE", actionParm.getData("CTZ1_CODE",0));
		// 身份2
		action.setData("IBS", "CTZ2_CODE", actionParm.getData("CTZ2_CODE",0));
		// 身份3
		action.setData("IBS", "CTZ3_CODE", actionParm.getData("CTZ3_CODE",0));
		action.setData("ODI", "ADM_DATE", actionParm.getData("IN_DATE",0));
		action.setData("ODI", "BIRTH_DATE",actionParm.getData("BIRTH_DATE",0));
//		action.setData("IBS", "BIL_AUDIT_FLG", "Y");//==20140716 ADD 
		return action;
    }
    
    //调整账单--审核提示有未产生账单的医嘱时操作
    public void onNewBill(){
    	int Count = this.messageBox("提示信息", "是否调整账单", 0);
		if (Count ==0) {
			TParm parm=new TParm();
			parm.setData("CASE_NO",caseNo);
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
                     "onNewBill", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("调整账单出现问题");
				err(result.getErrName() + " " + result.getErrText());
				return;
				}
			this.messageBox("调整成功！");
			     this.callFunction("UI|TableM|removeRowAll");
			     this.callFunction("UI|TableD|removeRowAll");
			  onQuery();
		}else{
			return;
		}
    }
}
