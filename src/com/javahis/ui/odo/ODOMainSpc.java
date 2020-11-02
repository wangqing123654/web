package com.javahis.ui.odo;

import jdo.odo.OpdOrder;
import jdo.opd.OrderTool;
import jdo.pha.PhaSysParmTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站物联网对象
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站物联网对象
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODOMainSpc {
	public OdoMainControl odoMainControl;
	private SocketLink client1;//Socket传送门诊药房工具
	private String phaRxNo;//===pangben 2013-5-17 药品审核界面添加跑马灯处方签数据 
	
	private static final String NULLSTR = "";
	
	public ODOMainSpc(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
	}
	
	public void onInit() throws Exception{
		
	}
	/**
	 * 校验物联网是否已经审核
	 * @param order
	 * @return
	 */
	public boolean checkSpcPha(OpdOrder order) throws Exception{
		for (int i = 0; i < order.rowCount(); i++) {
			if (i - 1 >= 0) {// 已经审配发的处方签，不可以添加医嘱
				//===pangben 2013-7-23 修改物联网提示消息
				String caseNo = order.getCaseNo();
				String rxNo = order.getRowParm(i - 1).getValue("RX_NO");
				String seqNo = order.getRowParm(i - 1).getValue("SEQ_NO");
				TParm spcParm = new TParm();
				spcParm.setData("CASE_NO", caseNo);
				spcParm.setData("RX_NO", rxNo);
				spcParm.setData("SEQ_NO", seqNo);
				TParm spcReturn = TIOM_AppServer.executeAction(
						"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
				if (!this.checkDrugCanUpdate(order, "MED", i - 1, true,spcReturn)) { // 判断是否可以新增医嘱
					if(spcReturn.getValue("PhaRetnCode").length()>0)
						odoMainControl.messageBox("已经退药,请删除处方签操作");
					else
						odoMainControl.messageBox("此处方已经审核,不可修改操作");
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 检核药品是否已经审配发 是否可以退药
	 * 
	 * @param type
	 *            String "EXA":西药 "CHN":中药
	 * @param row
	 *            int
	 *            flg =true检验当前操作是修改医嘱还是删除医嘱，修改医嘱操作退药不可以修改但是可以删除
	 * @return boolean
	 */
	public boolean checkDrugCanUpdate(OpdOrder order, String type, int row,
			boolean flg,TParm spcParm) throws Exception{
		boolean needExamineFlg = false;
		// 如果是西药 审核或配药后就不可以再进行修改或者删除
		if ("MED".equals(type)) {
			// 判断是否审核
			needExamineFlg = PhaSysParmTool.getInstance().needExamine();
		}
		// 如果是中药 审核或配药后就不可以再进行修改或者删除
		if ("CHN".equals(type)) {
			// 判断是否审核
			needExamineFlg = PhaSysParmTool.getInstance().needExamineD();
		}
		TParm spcReturn=new TParm();
		if (null==spcParm) {
			String caseNo = order.getCaseNo();
			String rxNo = order.getRowParm(row).getValue("RX_NO");
			String seqNo = order.getRowParm(row).getValue("SEQ_NO");
			spcParm = new TParm();
			spcParm.setData("CASE_NO", caseNo);
			spcParm.setData("RX_NO", rxNo);
			spcParm.setData("SEQ_NO", seqNo);
			spcReturn = TIOM_AppServer.executeAction(
					"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
		}else{
			spcReturn=spcParm;
		}
		if(spcReturn.getErrCode()==-2){
			return true;
		}
		// 如果有审核流程 那么判断审核医师是否为空
		if (needExamineFlg) {
			// 如果审核人员存在 不存在退药人员 那么表示药品已审核 不能再做修改
			if(flg){//============pangben 2013-4-17 添加修改医嘱交验
//				if (spcOpdOrderReturnDto.getPhaCheckCode().length() > 0) {
//					return false;
//				}
				if (spcReturn.getValue("PhaCheckCode").length() > 0) {
					return false;
				}
			} else {
				if (spcReturn.getValue("PhaCheckCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					return false;
				}
			}
		} else {// 没有审核流程 直接配药
			// 判断是否有配药药师
			if (flg) {// ============pangben 2013-4-17 添加修改医嘱交验
				if (spcReturn.getValue("PhaDosageCode").length() > 0) {
					return false;
				}
			} else {
				if (spcReturn.getValue("PhaDosageCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					return false;// 已经配药不可以做修改
				}
			}
		}
		return true;
	}
	
	/**
	 * 物联网保存医嘱
	 * @param pha_rx_no
	 * @throws Exception
	 */
	public void saveSpcOpdOrder(String pha_rx_no) throws Exception{
		TParm spcParm = new TParm();
		spcParm.setData("RX_NO", pha_rx_no);
		spcParm.setData("CASE_NO", odoMainControl.odoMainReg.reg.caseNo());
		spcParm.setData("CAT1_TYPE", "PHA");
		spcParm.setData("RX_TYPE", "7");
		// 物联网获得此次操作的医嘱，通过处方签获得
		TParm spcResult = OrderTool.getInstance().getSumOpdOrderByRxNo(
				spcParm);
		if (spcResult.getErrCode() < 0) {
			odoMainControl.messageBox("物联网操作：医嘱查询出现错误");
		} else {
			spcResult.setData("SUM_RX_NO", pha_rx_no);
			spcResult = TIOM_AppServer.executeAction(
					"action.opd.OpdOrderSpcCAction", "saveSpcOpdOrder",
					spcResult);
			if (spcResult.getErrCode() < 0) {
				System.out.println("物联网操作:" + spcResult.getErrText());
				odoMainControl.messageBox("物联网操作：医嘱添加出现错误,"
						+ spcResult.getErrText());
			} else {
				phaRxNo = pha_rx_no;// =pangben2013-5-15添加药房审药显示跑马灯数据
				sendMedMessages();
			}
		}
	}
	
	/**
	 * 向对应的门诊药房发送消息
	 * =======pangben 2013-5-13 
	 */
	public void sendMedMessages() throws Exception{
		//System.out.println("------sendMedMessages come in-----");
		client1 = SocketLink.running(NULLSTR, "ODO", "ODO");
		if (client1.isClose()) {
			odoMainControl.out(client1.getErrText());
			return;
		}
		String[] phaArray = new String[0];
		if (phaRxNo.length() > 0) {// 获得所有操作的处方签号码 发送数据
			phaArray = phaRxNo.split(",");
		}
		String sql="";
		TParm result=null;
		String sktUser="";
		for (int i = 0; i < phaArray.length; i++) { 
			sql="SELECT CASE_NO,EXEC_DEPT_CODE FROM OPD_ORDER WHERE CASE_NO='"+odoMainControl.caseNo+"' AND RX_NO='"+phaArray[i]+"'";			
			//System.out.println("----sql----"+sql);			
			result=new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount()<=0) {
				continue;
			}
			//药品 按执行科室，发送消息
			sktUser=result.getValue("EXEC_DEPT_CODE", 0);
			//
			//"PHAMAIN"
			client1.sendMessage(sktUser, "RX_NO:"// PHAMAIN :SKT_USER 表添加数据
					+ phaArray[i] + "|MR_NO:" + odoMainControl.odoMainPat.pat.getMrNo()
					+ "|PAT_NAME:"
					+ odoMainControl.odoMainPat.pat.getName());
		}
		if (client1 == null)
			return;
		client1.close();
	}
	
	/**
	 * 校验已发药品，已到检
	 * 
	 * @param order
	 * @param row
	 */
	public boolean checkSendPah(OpdOrder order, int row) throws Exception{
		if ("PHA".equals(order.getItemData(row, "CAT1_TYPE"))) {
			String caseNo = order.getCaseNo();
			String rxNo = order.getRowParm(row).getValue("RX_NO");
			String seqNo = order.getRowParm(row).getValue("SEQ_NO");
			TParm spcParm = new TParm();
			spcParm.setData("CASE_NO", caseNo);
			spcParm.setData("RX_NO", rxNo);
			spcParm.setData("SEQ_NO", seqNo);
			TParm spcReturn = TIOM_AppServer.executeAction(
	                "action.opb.OPBSPCAction",
	                "getPhaStateReturn", spcParm);
			if(spcReturn.getErrCode()==-2){
				return true;
			}
			boolean needExamineFlg = false;
			// 如果是西药 审核或配药后就不可以再进行修改或者删除
			if ("W".equals(order.getRowParm(row).getValue("PHA_TYPE"))
					|| "C".equals(order.getRowParm(row)
							.getValue("PHA_TYPE"))) {
				// 判断是否审核
				needExamineFlg = PhaSysParmTool.getInstance().needExamine();
			}
			// 如果有审核流程 那么判断审核医师是否为空
			if (needExamineFlg) {
				// System.out.println("有审核");
				// 如果审核人员存在 不存在退药人员 那么表示药品已审核 不能再做修改
				if (spcReturn.getValue("PhaCheckCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					odoMainControl.messageBox("药品已审核,不能退费!");
					return false;
				}
			} else {// 没有审核流程 直接配药
				// 判断是否有配药药师
				if (spcReturn.getValue("PhaDosageCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					odoMainControl.messageBox("药品已发药,不能退费!");
					return false;// 已经配药不可以做修改
				}
			}
		}
		return true;
	}
	
	/**
	 * 发送消息
	 * =====pangben 2013-12-18 爱育华版本 药房发送消息
	 * @param ektSumExeParm
	 * @throws Exception 
	 */
	public void onSendInw(TParm ektSumExeParm, boolean flg) throws Exception {
		if (ektSumExeParm.getValue("PHA_RX_NO").length() > 0) {
			// ==pangben 2013-5-21 添加预审功能
			if (flg) {
				saveSpcOpdOrder(ektSumExeParm.getValue("PHA_RX_NO"));
			} else {
				phaRxNo = ektSumExeParm.getValue("PHA_RX_NO");// =pangben2013-5-15添加药房审药显示跑马灯数据
				sendMedMessages();
			}
		}
	}
}
