package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title:礼品卡设定</p>
*
* <p>Description: 礼品卡设定</p>
*
* <p>Copyright: Copyright (c) caoyong 20131225/p>
*
* <p>Company: BlueCore</p>
*
* @author caoyong
* @version 1.0
*/

public class MEMGiftCardSettingTool extends TJDOTool{
	/**
	 * 构造器
	 */
	public MEMGiftCardSettingTool() {
		setModuleName("mem\\MEMGiftCardSettingModule.x");
		onInit();
	}

	/**
	 * 实例
	 */
	private static MEMGiftCardSettingTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PESTool
	 */
	public static MEMGiftCardSettingTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MEMGiftCardSettingTool();
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
		
		String sql="SELECT GIFTCARD_CODE FROM MEM_CASH_CARD_INFO WHERE GIFTCARD_CODE ='"+mem_code+"'";
		
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
		
		String sql="SELECT GIFTCARD_CODE,GIFTCARD_DESC,GIFTCARD_ENG_DESC,PY1,PY2,SEQ,CARD_TYPE,FACE_VALUE, "+
                   "RETAIL_PRICE,DESCRIPTION, "+
	               "OPT_DATE,OPT_USER,OPT_TERM  " +
	               "FROM MEM_CASH_CARD_INFO  ";
		        if(parm.getValue("GIFTCARD_CODE").length()>0){//会员代码精确查询
		        	sql+=" WHERE GIFTCARD_CODE='"+parm.getValue("GIFTCARD_CODE")+"'"; 
		        }else if(parm.getValue("GIFTCARD_DESC").length()>0&&parm.getValue("MEM_ENG_DESC").length()<=0){//汉文会员卡名称模糊查询
		        	sql+=" WHERE GIFTCARD_DESC LIKE'%"+parm.getValue("GIFTCARD_DESC")+"%' ";
		        }else if(parm.getValue("GIFTCARD_DESC").length()<=0&&parm.getValue("GIFTCARD_ENG_DESC").length()>0){//英文会员卡名称模糊查询
		        	sql+=" WHERE GIFTCARD_ENG_DESC LIKE '%"+parm.getValue("GIFTCARD_ENG_DESC")+"%' ";
		        }else if(parm.getValue("GIFTCARD_DESC").length()>0&&parm.getValue("GIFTCARD_ENG_DESC").length()>0){//英文会员卡名称和汉文会员卡名称模糊查询
		        	sql+=" WHERE GIFTCARD_ENG_DESC LIKE '%"+parm.getValue("GIFTCARD_ENG_DESC")+"%' OR GIFTCARD_DESC LIKE'%"+parm.getValue("GIFTCARD_DESC")+"%' ";
		        }
		            sql+=" ORDER BY SEQ";
	              
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println(sql);
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
	

}
