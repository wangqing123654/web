package action.inv;

import jdo.inv.INVCodeMapTool;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection; 


/**
 * <p>
 * Title: ��������Ӧ��ҩƷ����ȶ�
 * </p>
 *
 * <p>
 * Description: ��������Ӧ��ҩƷ����ȶ�
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: BLUECORE
 * </p>
 *
 * @author fux 2013.6.6
 * @version 1.0
 */ 
public class INVCodeMapAction extends TAction {
	 
	/**
	 * ���ʱ���ת�� 
	 * @param parm
	 * @return
	 */
	public TParm importMap(TParm parm) {
		TConnection connection = getConnection();
		       
		int count = parm.getCount();
		
		boolean queryflg = true;		
		boolean flg = true;
		
		String tempCode =""; 
		
		int rowNo = 0; 
		 
		for(int i = 0; i < count; i++) {
			TParm inParm = parm.getRow(i);
			tempCode = inParm.getValue("INV_CODE");
			String supCode = inParm.getValue("SUP_CODE");
			String supInvCode = inParm.getValue("SUP_INV_CODE");
			
			if(tempCode == null || "".equals(tempCode.trim())){
				rowNo = i+1;
				break;
			}
			 
			if(supCode == null || "".equals(supCode.trim())){
				rowNo = i+1;
				break;
			} 
			
			if(supInvCode == null || "".equals(supInvCode.trim())){
				rowNo = i+1;
				break;
			}
			
			queryflg = INVCodeMapTool.getInstance().queryBase(inParm);
			if(!queryflg){
				break;
			}
			
			flg = INVCodeMapTool.getInstance().importSave(inParm);
			if(!flg){
				break;
			}
		}		
		connection.commit();
		connection.close();
		
		TParm result = new TParm();
		
		if(rowNo != 0){
			result.setData("FLG", "�� " + rowNo + " �����ݲ�ȫ�����飡");
		}else if(!queryflg){
			result.setData("FLG", "ϵͳ��û�и����ʣ����ʱ��룺"+tempCode);
		}else if(!flg){
			result.setData("FLG", "N");
		}else{
			result.setData("FLG", "Y");
		}
		
		return result;
	}
	
}
