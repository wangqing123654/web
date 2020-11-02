package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class InvPackStockMTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    public static InvPackStockMTool instanceObject;

    /**
     * ������
     */
    public InvPackStockMTool() {
        setModuleName("inv\\INVPackStockMModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static InvPackStockMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvPackStockMTool();
        return instanceObject;
    }

    /**
     * ���������������Ź���ϸ��Ŀ����
     *
     * @param parm
     * @return
     */
    public TParm onInsertStockQtyByPack(TParm parm, TConnection conn) {
        TParm result = this.update("insertStockQtyByPack", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���������������Ź���ϸ��Ŀ����
     *
     * @param parm
     * @return
     */
    public TParm onUpdateStockQtyByPack(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyByPack", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ����������
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryStockM(TParm parm) {
        TParm result = this.query("queryStockM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * û����Ź����������,�۳��������������Ŀ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyBySupReq(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyBySupReq", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����Ź����������,������������״̬Ϊ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateStatusBySupReq(TParm parm, TConnection conn) {
        TParm result = this.update("updateStatusBySupReq", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����������������Ŀ���������״̬
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyAndStatus(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyAndStatus", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("delete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
//------------------��Ӧ������start-------------------------
    
    
    /**
     * ����������������
     * @param packCode String
     * @return TParm
     */
    public TParm getStockSeq(String packCode) {
        if (packCode == null || packCode.length() == 0)
            return null;
        TParm parm = new TParm();
        parm.setData("PACK_CODE", packCode);
        TParm result = query("getStockSeq", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * �������������������ں�Ч��
     * @param packCode String
     * @param packSeqNo int
     * @return TParm
     */
    public TParm getPackDate(String packCode,int packSeqNo){
        TParm result=new TParm();
        if(packCode==null||packCode.length()==0||packSeqNo<=0)
            return result.newErrParm(-1,"��������Ϊ��!");
        result.setData("PACK_CODE",packCode);
        result.setData("PACK_SEQ_NO",packSeqNo);
        result = query("getPackDate",result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    /**
     * ����������Ƿ����
     * @param packCode String
     * @return TParm
     */
    public TParm checkPackCount(String packCode) {
        if (packCode == null || packCode.length() == 0)
            return null;
        TParm parm = new TParm();
        parm.setData("PACK_CODE", packCode);
        TParm result = query("checkPackCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * ����������
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertPack(TParm parm, TConnection connection) {
        TParm result = this.update("insertPack", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    /**
     * ���������������
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateQty(TParm parm, TConnection connection) {
        TParm result = this.update("updateQty", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    public TParm queryPackBatch(TParm parm){
    	TParm result = this.query("queryPackBatch", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * �������������������ں�Ч�ڣ���������
     * @param packCode String
     * @param packSeqNo int
     * @return TParm
     */
    public TParm getPackDateBatch(String packCode,int packSeqNo,int packBatchNo){
        TParm result=new TParm();
        result.setData("PACK_CODE",packCode);
        result.setData("PACK_SEQ_NO",packSeqNo);
        result.setData("PACK_BATCH_NO",packBatchNo);
        result = query("getPackDateBatch",result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
  
    //------------------��Ӧ������end-------------------------


}
