package com.javahis.ui.bil;

import jdo.bil.BILCounteTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILInvrcptTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * 
 * <p>
 * Title: 批量领用票号
 * </p>
 * 
 * <p>
 * <b>Description:</b> <br>
 * 批量领用票号
 * </p>
 * 
 * <p>
 * Copyright:
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author design: pangben 2014-7-9 <br>
 *         coding:
 * @version 4.0
 */
public class BILInvoiceCollarControl extends TControl {
	TTable tableM;
	/**
     * 得到TTable
     *
     * @param tag
     *            String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
	public void onInit() {
		super.onInit();
		tableM = (TTable) this.getComponent("TABLE1");
		// 带出使用ID
		this.callFunction("UI|CASHIER_CODE|setValue", Operator.getID());
		callFunction("UI|CASHIER_CODE|setEnabled", false);
		// 带出使用者
		this.callFunction("UI|OPT_TERM|setValue", Operator.getIP());
		callFunction("UI|OPT_TERM|setEnabled", false);
		getTTable("TABLE1").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
                 this, "onClicked");

        //table1的侦听事件
//        callFunction("UI|TABLE1|addEventListener", "TABLE1->"
//                     + TTableEvent.CLICKED, this, "onTableClicked");
		this.onQuery();
	}
    /**
     * 增加对Table的监听
     */
	public void onClicked(Object obj) {
		 TTable bilTable = (TTable) obj;
		 bilTable.acceptText();
//	     TParm tableParm = bilTable.getParmValue();
//		// 记录当前选中的行
//		// 得到当前选中行数据
//		TParm parm = tableM.getParmValue().getRow(row);
//		// 如果下一票号为空,则说明此票据已经用光
//		if (tableParm.getValue("UPDATE_NO",) == null
//				|| parm.getValue("UPDATE_NO").length() == 0)
//			return;
//		for (int i = 0; i < tableM.getParmValue().getCount(); i++) {
//			
//		}
		// for (int i = 0; i < tableM.getRowCount(); i++) {
		// tableM.setValueAt("N", i, 0);
		// }
		// tableM.setValueAt("Y", row, 0);
//		tableM.setParmValue(parmValue)
//		tableM.acceptText();
	}
    /**
     * 查询数据
     */
    public void onQuery() {
        //得到查询参数
        TParm parm = this.getdata();
        //清空table
        this.callFunction("UI|TABLE1|removeRowAll");
        TParm data = new TParm();
        //查询数据
        data = BILInvoiceTool.getInstance().selectAllData(parm);
        //给table配参
        tableM.setParmValue(data);
        ((TCheckBox)this.getComponent("CHK_SEL")).setSelected(false);
    }
    /**
     * 查询配参
     * @return TParm
     */
    public TParm getdata() {
        TParm parm = new TParm();
        String value = getValueString("RECP_TYPE");
        if (value.length() > 0)
            parm.setData("RECP_TYPE", value);
        value = Operator.getID();
        if (value.length() != 0)
            parm.setData("CASHIER_CODE", value);
        //0表示使用中，1表示状态是没有交回的
        parm.setData("STATUS", "1");
        return parm;
    }
    /**
     * 开账
     */
    public void onSave() {
    	TParm tableParm=tableM.getParmValue();
    	boolean flg=false;
    	TParm tempParm=new TParm();
    	for (int j = 0; j < tableParm.getCount(); j++) {
    		if (tableParm.getValue("FLG",j).equals("Y")) {
				if (tableParm.getValue("UPDATE_NO", j) == null
						|| tableParm.getValue("UPDATE_NO", j).length() == 0) {
					messageBox_(tableParm.getValue("RECP_TYPE_NAME", j)+"结束票号为:"+tableParm.getValue("END_INVNO", j)+"此票据已经用完!");
					return;
				}
				 //01代表使用中
		        if (checkout(tableParm.getRow(j))) {
		            //限制条件....已经开账不能再开
		            this.messageBox(tableParm.getValue("RECP_TYPE_NAME", j)+"结束票号为:"+tableParm.getValue("END_INVNO", j)+"已经开帐");
		            return;
		        }
    			flg=true;
    			tempParm.addRowData(tableParm, j);
			}
    	}
    	if (!flg) {
			this.messageBox("请选择需要操作的数据");
			return;
		}
       
        //datat.setData("START_INVNO", parm.getData("UPDATE_NO", selectrow));
        //状态0代表使用中
    	TParm parm=new TParm();
    	parm.setData("STATUS", "0");
    	parm.setData("OPEN_DATE", StringTool.getString(SystemTool
            .getInstance().getDate(), "yyyyMMdd HHmmss"));
    	parm.setData("OPT_TERM", Operator.getIP());
    	parm.setData("TERM_IP", Operator.getIP());
    	parm.setData("OPT_USER", Operator.getID());
    	parm.setData("BILPARM", tempParm.getData());
//        System.out.println("传入Action数据" + parm);
        //调用开账Action
        TParm result = TIOM_AppServer.executeAction(
            "action.bil.InvoicePersionAction", "sumOpencheck", parm);
        if (result.getErrCode() != 0) {
            this.messageBox("E0005"); //执行失败
        }
        else {
            this.messageBox("P0005"); //执行成功
        }
        onQuery();
    }
    /**
     * 检查是否已经开账状态
     * @param parm TParm
     * @return boolean
     */
    public boolean checkout(TParm parm) {
        //用结束票号作为检查这组票是否正在使用中
        parm.setData("CASHIER_CODE", Operator.getID());
//		System.out.println("check parm="+parm);
        //关账时间
        TParm result = BILCounteTool.getInstance().CheckCounter(parm);
        if (result.getCount("CASHIER_CODE") > 0)
            return true;
        return false;
    }
    /**
     * 全选
     */
    public void onSel(){
    	TParm tableParm=tableM.getParmValue();
    	if (tableParm.getCount()<=0) {
			this.messageBox("没有操作的数据");
		}
    	if (((TCheckBox)this.getComponent("CHK_SEL")).isSelected()) {
    		String []arrayName={"EKT","IBS","OPB","PAY","REG"};
    		boolean flg=true;
        	for (int i = 0; i < arrayName.length; i++) {
        		flg=true;
    			for (int j = 0; j < tableParm.getCount(); j++) {
    				if (arrayName[i].equals(tableParm.getValue("RECP_TYPE", j))) {
						if (flg) {
							tableParm.setData("FLG",j, "Y");
							flg=false;
						}
					}
    			}
    		}
        	tableM.setParmValue(tableParm);
		}else{
			for (int i = 0; i < tableParm.getCount(); i++) {
				tableParm.setData("FLG",i, "N");
			}
			tableM.setParmValue(tableParm);
		}
    	
    }
    /**
     * 清空方法
     */
    public void onClear() {
        clearValue("RECP_TYPE");
        callFunction("UI|TABLE1|clearSelection");
        this.onQuery();
    }
    /**
     * 关帐方法
     */
    public void onClosedata() {
    	TParm tableParm=tableM.getParmValue();
    	boolean flg=false;
    	TParm tempParm=new TParm();
    	for (int j = 0; j < tableParm.getCount(); j++) {
    		if (tableParm.getValue("FLG",j).equals("Y")) {
				if (!tableParm.getValue("STATUS",j).equals("0")) {
		            this.messageBox(tableParm.getValue("RECP_TYPE_NAME", j)+"结束票号为:"+tableParm.getValue("END_INVNO", j)+"此票据不在使用中");
		            return;
		        }
				 //01代表使用中
		        if (!checkout(tableParm.getRow(j))) {
		            //限制条件....已经开账不能再开
		            this.messageBox(tableParm.getValue("RECP_TYPE_NAME", j)+"结束票号为:"+tableParm.getValue("END_INVNO", j)+"尚未开帐");
		            return;
		        }
    			flg=true;
    			tempParm.addRowData(tableParm, j);
			}
    	}
    	if (!flg) {
			this.messageBox("请选择需要操作的数据");
			return;
		}
    	for (int i = 0; i < tempParm.getCount("END_INVNO"); i++) {
    		tempParm.setData("STATUS",i, "1");
    		tempParm.setData("CASHIER_CODE",i, Operator.getID());
    		tempParm.setData("OPT_USER",i, Operator.getID());
    		tempParm.setData("OPT_TERM",i, Operator.getIP());
    		tempParm.setData("TERM_IP",i, Operator.getIP());
    		tempParm.setData("CLS_DATE",i,
                    StringTool.getString(SystemTool.getInstance()
                                         .getDate(), "yyyyMMdd HH:mm:ss"));
    		 //需要减1
            //如果下一票号为空,则结束票号就是结束票号
            if (tempParm.getValue("UPDATE_NO",i) == null ||
            		tempParm.getValue("UPDATE_NO",i).length() == 0) {
            }else {
                //下一票号减一作为结束票号
            	tempParm.setData("END_INVNO",i,
                             StringTool.subString(tempParm.getValue("UPDATE_NO",i)));
            }
            //如果下一票号等于初始票号,结束票号等于起始票号
            if (tempParm.getValue("START_INVNO",i).equals(tempParm.getValue("UPDATE_NO",i)))
            	tempParm.setData("END_INVNO",i, tempParm.getValue("UPDATE_NO",i));
		}
        //调用关账Action
        TParm result = TIOM_AppServer.executeAction(
            "action.bil.InvoicePersionAction", "sumCloseCheck", tempParm);
        if (result.getErrCode() != 0) {
            this.messageBox("E0005"); //执行失败
        }
        else {
            this.messageBox("P0005"); //执行成功
        }
        this.onQuery();
    }
    /**
     * 选择领用类别过滤table
     */
    public void onSelect() {
        this.onQuery();
    }
    /**
     *缴回
     */
    public void returnback() {
    	int selectrow=tableM.getSelectedRow();
        if (selectrow < 0) {
            this.messageBox("E0012");
            return;
        }
        //取table上数据，转成TParm
        TParm parm = (TParm)this.callFunction("UI|TABLE1|getParmValue");
        TParm datat = new TParm();
        datat.setRowData( -1, parm, selectrow);
        if (checkout(datat)) {
            this.messageBox("此柜台尚未关账");
            return;
        }
        this.openDialog("%ROOT%\\config\\bil\\BILRecipientsReturn.x", datat);
        this.onQuery();
    }

    /**
     * 调整票号
     */
    public void onAdjustment() {
    	int selectrow=tableM.getSelectedRow();
        if (selectrow < 0) {
            this.messageBox("E0012");
            return;
        }
        TParm parm = (TParm)this.callFunction("UI|TABLE1|getParmValue");
        TParm datat = new TParm();
        datat.setRowData( -1, parm, selectrow);
        this
            .openDialog("%ROOT%\\config\\bil\\BILAdjustmentRecipients.x",
                        datat);
        this.onQuery();
    }
}
