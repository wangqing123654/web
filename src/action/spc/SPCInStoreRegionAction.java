package action.spc;

import jdo.spc.INDTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * 
 * <p>Title: ������ҩ�����ܹ�</p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCInStoreRegionAction   extends TAction {

	 /**
     * �������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        
        
        result = INDTool.getInstance().onInsertDispenseIn(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback() ;
            conn.close();
            return result;
        }
        
        /**
        TParm outM = parm.getParm("OUT_M");
        
       
        String dispenseNo = outM.getValue("DISPENSE_NO");
        TParm cbnParm = parm.getParm("OUT_IN");
        for(int i = 0 ; i < cbnParm.getCount() ; i++ ){
        	TParm rowParm = cbnParm.getRow(i);
        	
        	 
        	//�Ȳ�ѯ���ܹ�����û�����ҩORDER_CODE,BATCH_SEQ ,CABINET_ID ��,���¿����,û,����һ����¼
        	TParm queryParm = SPCInStoreReginTool.getInstance().onQueryIndCbnstock(rowParm);
        	if(queryParm.getCount() <= 0 ){
        		result = SPCInStoreReginTool.getInstance().onSaveIndCbnstock(rowParm, conn);
        	}else{
        		result = SPCInStoreReginTool.getInstance().onUpdateIndCbnstock(rowParm, conn);
        	}
        	
        	if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText()
                     + result.getErrName());
                 result.setErrText("��ҩ�����ܹ�ʧ��");
                 conn.close();
                 return result;
             } 
        	
        	TParm newParm = new TParm();
        	newParm.setData("DISPENSE_NO",dispenseNo);
        	newParm.setData("SEQ_NO", rowParm.getInt("SEQ_NO"));
        	newParm.setData("IS_STORE","Y");
    		result = SPCInStoreReginTool.getInstance().onUpdateDispenseD(newParm);
    		if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                result.setErrText("������ϸ����ʧ��");
                conn.close();
                return result;
            }
        }*/
        
        conn.commit();
        conn.close();
        return result;
    }
}
