package action.spc;

import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
 
/**
 * <p>
 * Title: 根据结算数据自动生成请领数据
 * </p>
 * 
 * <p>
 * Description: 根据结算数据自动生成请领数据
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author fuwj 2013.07.17
 * @version 1.0
 */
public class INDCreateRequestAutoAction extends TAction {

	/**
	 * 根据结算数据自动生成请领数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm createIndRequsestAuto(TParm parm) {
		TConnection conn = getConnection();
		TParm searchresult = INDTool.getInstance().queryOrgCodeInIndAccout("");
		if (searchresult.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return searchresult;
		}
		int count = searchresult.getCount();
		String optUser = parm.getValue("OPT_USER");
		String optTerm = parm.getValue("OPT_TERM");
		String regionCode = parm.getValue("REGION_CODE");
		TParm result = new TParm();
		if (null != searchresult && count > 0) {				
			for (int i = 0; i < count; i++) {
				String appOrgCode = (String) searchresult.getData("ORG_CODE", i);	
				// String toOrgCode = "040101";  
				// 得到请领单号
				String requestNo = SystemTool.getInstance().getNo("ALL", "IND",
						"IND_REQUEST", "No");
				// 创建请领主档
				TParm requestM = INDTool.getInstance().createRequestM("040101",
						requestNo, appOrgCode, optUser, optTerm, regionCode,
						"1", "1");
				if (requestM.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return requestM;
				}
				// 生成请领主档
				Date d = new Date();  
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		        String dateNowStr = sdf.format(d);   
		        dateNowStr = dateNowStr.substring(0, 7)+"-25 23:59:59";
		        requestM.setData("REQUEST_DATE", dateNowStr);
			    result = new TParm(TJDODBTool.getInstance().update(
						INDSQL.createReRequestM(requestM), conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
				int seqNo = 1;
				// 查询物联网结算数据
				TParm parmD = INDTool.getInstance().queryIndAccout("",
						appOrgCode, "");
				if (parmD.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return parmD;
				}
				// 生成请领明细档
				for (int j = 0; j < parmD.getCount("ORDER_CODE"); j++) {
					String orderCode = parmD.getValue("ORDER_CODE", j);
					double qty = parmD.getDouble("OUT_QTY", j);
					if (qty == 0) {		
						continue;
					}						
					seqNo = seqNo + 1;				
					// 得到请领明细档PARM
					TParm requestD = INDTool.getInstance()
							.getRequestDAutoOfDrug(requestNo, seqNo + "",
									orderCode, qty, optUser, optTerm,
									regionCode);
					if (requestD.getErrCode() < 0) {
						conn.rollback();
						conn.close();
						return requestD;
					}
					// 保存		
					result = new TParm(TJDODBTool.getInstance().update(
							INDSQL.saveRequestDAutoOfDrug(requestD), conn));
					if (result.getErrCode() < 0) {
						conn.rollback();
						conn.close();
						return result;
					}
				}
			}
		}
		conn.commit();
		conn.close();
		return result;
	}

}
