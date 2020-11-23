package com.javahis.ui.ibs;

import java.sql.Timestamp;

import jdo.adm.ADMInpTool;
import jdo.bil.BILLumpWorkTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 套餐重新计算控制类
 * </p>
 * 
 * <p>
 * Description: 住院套餐病患已经计费的病人重新计算费用
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20160905
 * @version 4.5
 */
public class IBSLumpworkFeeCountControl extends TControl {
	private TTable table;

	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		onPage();
	}

	public void onPage() {
		Timestamp rollDay = StringTool.rollDate(SystemTool.getInstance().getDate(), -7);
		String startDate = StringTool.getString(rollDay, "yyyy/MM/dd");
        // System.out.println("startDate"+startDate);
        setValue("START_DATE", startDate+" 00:00:00");
        setValue("END_DATE",StringTool.getString(SystemTool.
                getInstance().getDate(), "yyyy/MM/dd")+" 23:59:59");
		this.clearText("MR_NO;DEPT_CODE;STATION_CODE");
		table.setParmValue(new TParm());
	}
	/**
	 * 查詢
	 */
	public void onQuery() {
		String sDate =SystemTool.getInstance().getDateReplace(getValue("START_DATE").toString(), true).toString();
		String eDate = SystemTool.getInstance().getDateReplace(getValue("END_DATE").toString(), true).toString();
		TParm rexpParm = new TParm();
		rexpParm.setData("START_DATE", sDate);// 查询开始起时间
		rexpParm.setData("END_DATE", eDate);// 查询结束时间
		if (null != this.getValue("MR_NO")
				&& this.getValue("MR_NO").toString().length() > 0) {
			rexpParm.setData("MR_NO", this.getValue("MR_NO"));
		}
		if (null != this.getValue("DEPT_CODE")
				&& this.getValue("DEPT_CODE").toString().length() > 0) {
			rexpParm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		}
		if (null != this.getValue("LUMPWORK_CODE")
				&& this.getValue("LUMPWORK_CODE").toString().length() > 0) {
			rexpParm.setData("LUMPWORK_CODE", this.getValue("LUMPWORK_CODE"));
		}
		if (null != this.getValue("STATION_CODE")
				&& this.getValue("STATION_CODE").toString().length() > 0) {
			rexpParm.setData("STATION_CODE", this.getValue("STATION_CODE"));
		}
		table.setParmValue(new TParm());
		rexpParm.setData("DS_DATE","N");
		TParm result=ADMInpTool.getInstance().selectLumpWork(rexpParm);
		if(result.getErrCode()<0){
			this.messageBox("查询出现问题");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("没有查询的数据");
			return;
		}
		table.setParmValue(result);
	}
	/**
	 * 保存
	 */
	public void onSave() {
		int index = table.getSelectedRow();
		if (index < 0) {
			this.messageBox("请选择需要操作的数据");
			return;
		}
		TParm tableParm = table.getParmValue();
		TParm lumpParm = BILLumpWorkTool.getInstance().getLumpRate(
				tableParm.getValue("CASE_NO", index),
				tableParm.getValue("MR_NO", index),
				tableParm.getValue("LUMPWORK_CODE",index).toString());
		if (lumpParm.getErrCode() <0) {
			this.messageBox("计算折扣出现问题:"+lumpParm.getErrText());
			return;
		}
		if (null == lumpParm.getValue("RATE")
				|| lumpParm.getValue("RATE").length() <= 0) {
			this.messageBox("没有获得套餐折扣");
			return;
		}
		double rate = lumpParm.getDouble("RATE");
//		if (rate <= 0) {
//			this.messageBox("此病患套餐折扣存在问题，套餐折扣为:" + rate + ",不可以操作");
//			return;
//		}
		if(this.messageBox("提示", "套餐折扣为:"+rate+",是否继续", 2)!=0){
			return;
		}
		TParm parm =new TParm();
		parm.setData("LUMPWORK_RATE", rate);//套餐折扣
		parm.setData("CASE_NO",tableParm.getValue("CASE_NO", index));
		parm.setData("CTZ1_CODE",tableParm.getValue("CTZ1_CODE", index));
		parm.setData("CTZ2_CODE",tableParm.getValue("CTZ2_CODE", index));
		parm.setData("CTZ3_CODE",tableParm.getValue("CTZ3_CODE", index));
		parm.setData("OPT_USER",Operator.getID());
		parm.setData("OPT_TERM",Operator.getIP());
	    TParm result = TIOM_AppServer.executeAction("action.ibs.IBSAction",
	                "onExeLumpWorkFeeCount", parm); // 住院登记保存===liing
	    if (result.getErrCode() < 0) {
			this.messageBox("操作失败," + result.getErrText());
			return;
		}
		this.messageBox("P0005");
		onQuery();
	}
	/**
	 * 
	* @Title: onExeIncludeBatch
	* @Description: TODO(套餐修改身份执行批次)
	* @author pangben 2015-10-20
	* @throws
	 */
	public void onExeIncludeBatch() {
		int index=table.getSelectedRow();
		if(index<0){
			this.messageBox("请选择需要操作的数据");
			return;
		}
		String caseNo=table.getParmValue().getValue("CASE_NO",index);
		TParm selParm = new TParm();
		String sql = "SELECT MR_NO,LUMPWORK_RATE FROM ADM_INP WHERE CASE_NO='"
				+ caseNo
				+ "' AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' AND LUMPWORK_CODE IS NOT NULL ";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount() <= 0) {
			this.messageBox("此病患不是套餐患者,不可以操作");
			return;
		}
		if(null==selParm.getValue("LUMPWORK_RATE",0)||
				selParm.getValue("LUMPWORK_RATE",0).length()<=0||selParm.getDouble("LUMPWORK_RATE",0)==0.00){
			this.messageBox("此病患未设置套餐折扣,不可以操作");
    		return ;
    	}
		if (null != caseNo && caseNo.length() > 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			TParm result = TIOM_AppServer.executeAction("action.ibs.IBSAction",
					"onExeIbsLumpWorkBatch", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("操作失败," + result.getErrText());
				return;
			}
			if (null != result.getValue("MESSAGE")
					&& result.getValue("MESSAGE").length() > 0) {
				this.messageBox(result.getValue("MESSAGE"));
				return;
			}
			this.messageBox("P0005");
			onQuery();
		} else {
			this.messageBox("请选择病患");
		}
	}
	/**
	 * 查询病案号
	 */
	public void onMrNo() {
		Pat pat = new Pat();
		String mrno = getValue("MR_NO").toString().trim();
		if (!this.queryPat(mrno))
			return;
		pat = pat.onQueryByMrNo(mrno);
		// System.out.println("------------------>pat"+pat);//==liling 20140513
		// 屏蔽
		if (pat == null || "".equals(getValueString("MR_NO"))) {
			this.messageBox_("查无病患! ");
			this.onClear(); // 清空
			return;
		}else{
			this.setValue("MR_NO", pat.getMrNo());
		}
	}

	/**
	 * 查询病患信息
	 * 
	 * @param mrNo
	 *            String
	 * @return boolean
	 */
	public boolean queryPat(String mrNo) {
		Pat pat = new Pat();
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("E0081");
			return false;
		}
		String allMrNo = PatTool.getInstance().checkMrno(mrNo);
		if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
			// ============xueyf modify 20120307 start
			messageBox("病案号" + allMrNo + " 已合并至" + pat.getMrNo());
			// ============xueyf modify 20120307 stop
		}

		return true;
	}
	/**
	 * 清空
	 */
	public void onClear() {
		onPage();
	}
}

