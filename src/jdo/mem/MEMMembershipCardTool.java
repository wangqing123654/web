package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title:会员卡种类设定</p>
*
* <p>Description: 会员卡种类设定</p>
*
* <p>Copyright: Copyright (c) caoyong 20131225/p>
*
* <p>Company: JavaHis</p>
*
* @author caoyong
* @version 1.0
*/

public class MEMMembershipCardTool extends TJDOTool{
	/**
	 * 构造器
	 */
	public MEMMembershipCardTool() {
		setModuleName("mem\\MEMMembershipCardModule.x");
		onInit();
	}

	/**
	 * 实例
	 */
	private static MEMMembershipCardTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PESTool
	 */
	public static MEMMembershipCardTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MEMMembershipCardTool();
		return instanceObject;
	}
	/**
	 * 添加
	 * @param parm
	 * @return
	 */
	public TParm insertCard(TParm parm) {
		TParm result = new TParm();
		result = update("insertCard", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 添加
	 * @param parm
	 * @return
	 */
	public TParm updatedata(TParm parm) {
		TParm result = new TParm();
		result = update("updatedata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询询全部
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectCardall() {
		TParm result = query("isChild");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		return result;
	}
	/**
	 * 修改前查询
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectCard(String mem_code) {
		
		String sql="SELECT MEM_CODE FROM MEM_MEMBERSHIP_INFO WHERE MEM_CODE ='"+mem_code+"'";
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		return result;
	}
	/**
	 * 修改前查询
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdata(TParm  parm) {
		
//		String sql="SELECT MEM_CODE,MEM_DESC,MEM_ENG_DESC,PY1,PY2,SEQ,MEM_FEE, "+
//                   "VALID_DAYS,DESCRIPTION,OVERDRAFT, "+
//	               "OPT_DATE,OPT_USER,OPT_TERM  " +
//	               "FROM MEM_MEMBERSHIP_INFO WHERE 1=1 ";
//		        if(parm.getValue("MEM_CODE").length()>0){//会员代码精确查询
//		        	sql+=" AND MEM_CODE='"+parm.getValue("MEM_CODE")+"'"; 
//		        }else if(parm.getValue("MEM_DESC").length()>0&&parm.getValue("MEM_ENG_DESC").length()<=0){//汉文会员卡名称模糊查询
//		        	sql+=" AND MEM_DESC LIKE'%"+parm.getValue("MEM_DESC")+"%' ";
//		        }else if(parm.getValue("MEM_DESC").length()<=0&&parm.getValue("MEM_ENG_DESC").length()>0){//英文会员卡名称模糊查询
//		        	sql+=" AND MEM_ENG_DESC LIKE '%"+parm.getValue("MEM_ENG_DESC")+"%' ";
//		        }else if(parm.getValue("MEM_DESC").length()>0&&parm.getValue("MEM_ENG_DESC").length()>0){//英文会员卡名称和汉文会员卡名称模糊查询
//		        	sql+=" AND MEM_ENG_DESC LIKE '%"+parm.getValue("MEM_ENG_DESC")+"%' OR MEM_DESC LIKE'%"+parm.getValue("MEM_DESC")+"%' ";
//		        }
//		            sql+=" ORDER BY SEQ";
		String sql = "SELECT MEM_CODE,MEM_DESC,MEM_ENG_DESC,PY1,PY2,SEQ,MEM_FEE,VALID_DAYS,DESCRIPTION,OVERDRAFT," +
				"  OPT_DATE,OPT_USER,OPT_TERM,MEM_IN_REASON,MEM_CARD FROM MEM_MEMBERSHIP_INFO WHERE 1=1 ";
		if(parm.getValue("MEM_CARD").length()>0){//会员代码精确查询
        	sql+=" AND MEM_CARD='"+parm.getValue("MEM_CARD")+"' "; 
        }else if(parm.getValue("VALID_DAYS").length()>0){
        	sql+=" AND VALID_DAYS='"+parm.getValue("VALID_DAYS")+"' "; 
        }else if(parm.getValue("REASON").length()>0){
        	sql+=" AND MEM_IN_REASON='"+parm.getValue("REASON")+"' "; 
        }
		sql+=" ORDER BY SEQ ";         
	              
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		return result;
	}
	
	
	/**
     * 删除
     * @param admType String
     * @param clinicTypeCode String
     * @param orderCode String
     * @return TParm
     */
    public TParm deletedata(TParm parm) {
       
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
	 * 查询该类型
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectMemCard(TParm parm) {
		TParm result = query("selectMemCard", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		return result;
	}
	

}
