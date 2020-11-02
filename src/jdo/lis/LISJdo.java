package jdo.lis;

import com.dongyang.jdo.TJDOTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.util.Log;

public class LISJdo extends TJDOTool{
    /**
     * ʵ��
     */
    public static LISJdo instanceObject;
    /**
     * �õ�ʵ��
     * @return TestJDO
     */
    public static LISJdo getInstance()
    {
        if(instanceObject == null)
            instanceObject = new LISJdo();
        return instanceObject;
    }
    /**
     * ������
     */
    public LISJdo()
    {
        setModuleName("lis\\LisTQL.x");
        onInit();
    }
    /**
     * �õ�ϵͳʱ��
     * @return Timestamp ��Ч����
     */
    public Timestamp getDate()
    {
        TParm parm = new TParm();
        return getResultTimestamp(query("getDate",parm),"SYSDATE");
    }
    /**
     * ����LIS��ҽ������״̬
     * @param labnumberNo String �����
     * @return boolean
     */
    public synchronized boolean getUpDateNewLabStust(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labnumberNo);
        //Log.DEBUG = true;
        TParm action = this.update("getUpDateNewLabStust",parm);
        if(action.getErrCode()<0){
            return false;
        }
        return true;
    }
    /**
     * LIS����������
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean upDateLisDJ(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labnumberNo);
        TParm action = this.update("upDateLisDJ",parm);
        if(action.getErrCode()<0){
           return false;
       }
       return true;
    }
    /**
     * LISȡ���������
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean upDateLisQXDJ(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labnumberNo);
        TParm action = this.update("upDateLisQXDJ",parm);
        if(action.getErrCode()<0){
           return false;
       }
       return true;
    }
    /**
     * LIS�������
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean upDateLisJSDJ(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labnumberNo);
        TParm action = this.update("upDateLisJSDJ",parm);
        if(action.getErrCode()<0){
           return false;
       }
       return true;
    }
    /**
     * ���»�ʿ����վ״̬
     * @param labnumberNo String
     * @return TParm
     */
    public synchronized TParm selectNBWStat(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labnumberNo);
        TParm action = this.query("selectNBWStat",parm);
        return action;
    }
    /**
     * ������
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean upDateSHWC(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER", labnumberNo);
        TParm action = this.update("upDateSHWC", parm);
        if (action.getErrCode() < 0) {
            return false;
        }
        return true;
    }
    /**
     * ɾ���ټ������
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean delLAB_GENRPTDTLData(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER", labnumberNo);
        TParm action = this.update("delLAB_GENRPTDTLData", parm);
        if (action.getErrCode() < 0) {
            return false;
        }
        return true;
    }
    /**
     * ɾ��΢����LIS��������
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean delLAB_ANTISENSTESTData(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER", labnumberNo);
        TParm action = this.update("delLAB_ANTISENSTESTData", parm);
        if (action.getErrCode() < 0) {
            return false;
        }
        return true;
    }
    /**
     * ɾ��ϸ�����LIS��������
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean delLAB_CULRPTDTLData(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER", labnumberNo);
        TParm action = this.update("delLAB_CULRPTDTLData", parm);
        if (action.getErrCode() < 0) {
            return false;
        }
        return true;
    }
    /**
     * �����ټ������
     * @param parm TParm
     * @return boolean
     */
    public synchronized boolean insertLAB_GENRPTDTLData(TParm parm){
        TParm action = this.update("insertLAB_GENRPTDTLData",parm);
        if(action.getErrCode()<0){
            return false;
        }
        return true;
    }
    /**
     * �������
     * @return boolean
     */
    public synchronized boolean upDateBGEND(String labNumberNo,String rptDttm,String drUser,String testSeq){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labNumberNo);
        parm.setData("RPT_DTTM",rptDttm);
        parm.setData("DLVRYRPT_USER",drUser);
        parm.setData("TESTSET_SEQ",testSeq);
        TParm action = this.update("upDateBGEND",parm);
        if(action.getErrCode()<0){
            return false;
        }
        return true;
    }
}
