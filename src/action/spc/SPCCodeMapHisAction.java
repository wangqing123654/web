package action.spc;

import jdo.spc.SPCCodeMapHisTool;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;


/**
 * <p>
 * Title: ������ҽԺ��ҩƷ����ȶ�
 * </p>
 *
 * <p>
 * Description: ������ҽԺ��ҩƷ����ȶ�
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
 * @author liuzhen 2013.1.17
 * @version 1.0
 */
public class SPCCodeMapHisAction extends TAction {
	
	
	/**
	 * ��ҩҩƷ����ת��
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
			tempCode = inParm.getValue("ORDER_CODE");
			
			String regionCode = inParm.getValue("REGION_CODE");
			String hisOrderCode = inParm.getValue("HIS_ORDER_CODE");
			
			if(tempCode == null || "".equals(tempCode.trim())){
				rowNo = i+1;
				break;
			}
			
			if(regionCode == null || "".equals(regionCode.trim())){
				rowNo = i+1;
				break;
			}
			
			if(hisOrderCode == null || "".equals(hisOrderCode.trim())){
				rowNo = i+1;
				break;
			}
			
			queryflg = SPCCodeMapHisTool.getInstance().queryBase(inParm);
			if(!queryflg){
				break;
			}
			
			flg = SPCCodeMapHisTool.getInstance().importSave(inParm);
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
			result.setData("FLG", "ϵͳ��û�и�ҩƷ��ҩƷ���룺"+tempCode);
		}else if(!flg){
			result.setData("FLG", "N");
		}else{
			result.setData("FLG", "Y");
		}
		
		return result;
	}
	
}
