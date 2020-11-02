package jdo.bms.ws;

import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 血库webService
 * </p>
 * 
 * <p>
 * Description: 血库webService
 * </p>
 * 
 * <p>
 * Copyright: BLUECORE
 * </p>
 * 
 * <p>
 * Company:BLUECORE
 * </p>
 * 
 * @author SHIBL
 * @version 1.0
 */
@WebService
public class BmsServiceImpl implements IBmsService {
	/*
	 * 测试标识
	 */
	private boolean IsDebug = true;

	/**
	 * 备血申请数据
	 */
	@Override
	public List<String> getBmsApplyData(String ApplyNo) {
		List<String> list = new ArrayList<String>();
		if (ApplyNo.equals("")) {
			list.add("-1");
			list.add("申请单号不能为空");
			return list;
		}
		if (IsDebug) {
			System.out.println("备血申请数据入参:" + ApplyNo);
		}
		String sql = "SELECT ADM_TYPE FROM BMS_APPLYM WHERE APPLY_NO='"
				+ ApplyNo + "'";
		TParm applyParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (applyParm.getCount() <= 0) {
			list.add("-1");
			list.add("未查询到申请单信息");
			return list;
		}
		String admType = applyParm.getValue("ADM_TYPE", 0);
		String reline = "";
		try {
			TParm inparm = BmsTool.getInstance().getBmsApplyData(ApplyNo,
					admType);
			if (inparm.getErrCode() < 0) {
				list.add("-1");
				list.add(inparm.getErrText());
				return list;
			}
			XMLUtil xu = new XMLUtil();
			reline = xu.getApplyXMLString(inparm,admType);
		} catch (Exception e) {
			e.printStackTrace();
			list.add("-1");
			list.add("生成申请xml异常失败");
			return list;
		}
		list.add("0");
		list.add(reline);
		return list;
	}

	/**
	 * 输血反应数据
	 */
	@Override
	public List<String> getBmsTranReacData(String ApplyNo) {
		List<String> list = new ArrayList<String>();
		if (ApplyNo.equals("")) {
			list.add("-1");
			list.add("反应单号不能为空");
			return list;
		}
		if (IsDebug) {
			System.out.println("输血反应数据入参:" + ApplyNo);
		}
		String reline = "";
		try {
			TParm inparm = BmsTool.getInstance().getBmsReacData(ApplyNo);
			if (inparm.getErrCode() < 0) {
				list.add("-1");
				list.add(inparm.getErrText());
				return list;
			}
			XMLUtil xu = new XMLUtil();
			reline = xu.getReactXmlString(inparm);
		} catch (Exception e) {
			e.printStackTrace();
			list.add("-1");
			list.add("生成反应xml异常失败");
			return list;
		}
		list.add("0");
		list.add(reline);
		return list;
	}

	/**
	 * 血库收费
	 */
	@Override
	public List<String> onBmsFee(String XmlData) {
		List<String> list = new ArrayList<String>();
		if (XmlData.equals("")) {
			list.add("-1");
			list.add("xml字符串不能为空");
			return list;
		}
		if (IsDebug) {
			System.out.println("血库收费数据入参:" + XmlData);
		}
		XMLUtil xu = new XMLUtil();
		TParm parm = new TParm();
		try {
			parm = xu.getFeeParserXml(XmlData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			list.add("-1");
			list.add("解析计费xml字符串异常");
			return list;
		}
		String applyNo = parm.getValue("ApplyNo");
		String sql = "SELECT ADM_TYPE FROM BMS_APPLYM WHERE APPLY_NO='"
				+ applyNo + "'";
		TParm applyParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (applyParm.getCount() <= 0) {
			list.add("-1");
			list.add("未查询到申请单信息");
			return list;
		}
		try {
			if (applyParm.getValue("ADM_TYPE", 0).equals("I")) {
				TParm result = BmsFeeTool.getInstance().onBmsIFee(parm);
				if (result.getErrCode() < 0) {
					list.add("-1");
					list.add(result.getErrText());
					return list;
				}
			} else {
				TParm result = BmsFeeTool.getInstance().onBmsOEFee(parm);
				if (result.getErrCode() < 0) {
					list.add("-1");
					list.add(result.getErrText());
					return list;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			list.add("-1");
			list.add("血库计费操作异常");
			return list;
		}
		list.add("0");
		list.add("成功");
		return list;
	}

	/**
	 * 血品出库
	 */
	@Override
	public List<String> onBmsOutBound(String XmlData) {
		List<String> list = new ArrayList<String>();
		if (XmlData.equals("")) {
			list.add("-1");
			list.add("xml字符串不能为空");
			return list;
		}
		if (IsDebug) {
			System.out.println("血品出库数据入参:" + XmlData);
		}
		XMLUtil xu = new XMLUtil();
		TParm parm = new TParm();
		try {
			parm = xu.getBloodParserXml(XmlData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			list.add("-1");
			list.add("解析出库xml字符串异常");
			return list;
		}
		String applyNo = parm.getValue("ApplyNo");
		String sql = "SELECT ADM_TYPE FROM BMS_APPLYM WHERE APPLY_NO='"
				+ applyNo + "'";
		TParm applyParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (applyParm.getCount() <= 0) {
			list.add("-1");
			list.add("未查询到申请单信息");
			return list;
		}
		String admType = applyParm.getValue("ADM_TYPE", 0);
		TParm result = new TParm();
		try {
			result = BmsTool.getInstance().onBmsOut(parm, admType);
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, "血库出库操作异常");
		}
		if (result.getErrCode() < 0) {
			list.add("-1");
			list.add(result.getErrText());
		}
		list.add("0");
		list.add("成功");
		return list;
	}

	/**
	 * 血型鉴定
	 */
	@Override
	public List<String> onBmsConfirmData(String XmlData) {
		List<String> list = new ArrayList<String>();
		if (XmlData.equals("")) {
			list.add("-1");
			list.add("xml字符串不能为空");
			return list;
		}
		if (IsDebug) {
			System.out.println("血型鉴定数据入参:" + XmlData);
		}
		XMLUtil xu = new XMLUtil();
		TParm parm = new TParm();
		try {
			parm = xu.getConfirmParserXml(XmlData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			list.add("-1");
			list.add("解析血型鉴定xml字符串异常");
			return list;
		}
		if (!parm.getValue("PBloodType").equals("")
				&& !(parm.getValue("PBloodType").toUpperCase().equals("NULL"))) {
			TParm patParm = new TParm();
			patParm.setData("MR_NO", parm.getValue("MrNo"));
			patParm.setData("BLOOD_TYPE", parm.getValue("PBloodType"));
			patParm.setData("BLOOD_RH_TYPE", parm.getValue("PRhType"));
			TParm reParm = new TParm();
			;
			try {
				reParm = new TParm(TJDODBTool.getInstance().update(
						BmsTool.getInstance().onUpdatePatSql(patParm)));
			} catch (Exception e) {
				e.printStackTrace();
				reParm.setErr(-1, "更新病患血型操作异常");
			}
			if (reParm.getErrCode() < 0) {
				list.add("-1");
				list.add("更新病患血型错误");
				return list;
			}
		}
		list.add("0");
		list.add("成功");
		return list;
	}

	/**
	 * 血品回退
	 * 
	 * @param XmlData
	 * @return
	 */
	@Override
	public List<String> onBmsRefund(String XmlData) {
		List<String> list = new ArrayList<String>();
		if (XmlData.equals("")) {
			list.add("-1");
			list.add("xml字符串不能为空");
			return list;
		}
		if (IsDebug) {
			System.out.println("血品回退数据入参:" + XmlData);
		}
		XMLUtil xu = new XMLUtil();
		TParm parm = new TParm();
		try {
			parm = xu.getRefundParserXml(XmlData);
		} catch (Exception e) {
			e.printStackTrace();
			list.add("-1");
			list.add("解析血品回退xml字符串异常");
			return list;
		}
		TParm result = new TParm();
		try {
			result = BmsTool.getInstance().onDelOutBlood(parm);
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, "血库出库操作异常");
		}
		if (result.getErrCode() < 0) {
			list.add("-1");
			list.add(result.getErrText());
			return list;
		}
		list.add("0");
		list.add("成功");
		return list;
	}

	/**
	 * 审核备血申请
	 */
	@Override
	public List<String> onBmsApplyCheckData(String XmlData) {

		return null;
	}

	/**
	 * 审核输血反应
	 */
	@Override
	public List<String> onBmsTranReacCheckData(String XmlData) {

		return null;
	}

}