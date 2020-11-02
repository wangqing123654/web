package jdo.pha;

import java.sql.*;
import com.dongyang.jdo.TJDOObject;
import java.util.Vector;
import jdo.opd.OrderList;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TModifiedList;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import jdo.opd.Order;



/**
 *
 * <p>Title: ����ҩ�����ṹ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS (c)</p>
 *
 * @author ZangJH 2008.09.26
 *
 * @version 1.0
 */

public class Pha
    extends TJDOObject {

    /**
     * ���ڽ���ı�ǣ����䷢��
     */
    private String type = "";
    /**
     * �Ƿ���ȡ����ҩ����
     */
    private boolean isUnDisp=false;
    /**
     * ���ڽ����״̬��Y���/Nδ���
     */
    private String finishFlg = "";

    /**
     *��ǰѡ�е�ĳ��OrderList���к�
     */
    private int ordListRow = -1;

    /**
     * ���OrderList(���Բ��ֵȼ��Ĵ��)
     */
    private Vector orderListVector = new Vector();

    /**
     * OrderList���󣬹������е�Order(��ǰ��OrderList)
     */
    private OrderList nowOrdList = new OrderList();

    private PhaUtil phaUtil = new PhaUtil();

    private String lockFlg ;
    /**
     * ��ʼ��
     */
    public Pha() {

    }

    /**
     * ����parm��ʼ��pha
     * @param parm TParm
     * @return boolean �棺�ɹ����٣�ʧ��
     */
    public boolean initParm(TParm parm) {

        if (parm == null)
            return false;

        //���ù�����ĳ�ʼ��
        if (parm.existData("LIST")) {
            orderListVector = phaUtil.initParm(parm);
        }

        return true;
    }

    /**
     * ���ݶ�̬where ������ѯPha��������DR_CODE,DEPT_CODE,DSCHECK_DATE,DSDGT_DATE,DSDLVRY_Date,DSRETURN_DATE,RX_NO,CASE_NO,MR_NO,SEQ_NO
     * @param parm
     * @return Pha
     */
    public static Pha onQueryByTParm(TParm parm) {
        //����PHAAction��ѯ
        TParm result = TIOM_AppServer.executeAction("action.pha.PHAAction",
            "onQuery", parm);
        if (result.getErrCode() < 0) {
            return null;
        }
        Pha pha = new Pha();
        //��ʼ��pha
        if (!pha.initParm(result)) {
            return null;
        }
        return pha;
    }


    /**
     * ����
     * @return TParm
     */
    public TParm onSave() {

        //����һ��TParm�����ڴ�jdo����action��Ĵ������
        TParm saveDate = new TParm();

        //�õ���ǰѡ�е�orderList
        nowOrdList = phaUtil.getCertainOrdListByRow(this.getOrdListRow(),
            orderListVector);

        //����ִ�з�������Parm��EXE_FLGΪ�������д���Ӧ�ڽ��������
        this.onExecute(nowOrdList);
        String viewFlg = this.getType();
        
        //������ҩ�����ʱ������һ��orderHistoryList���������治�ᣩ
        if (viewFlg.equals("Return")) {
        	
            //��õ�ǰorderList��parm
            TParm parmOrd = nowOrdList.getParm();
            //��õ�ǰorderHistoryList��parm
            TParm parmHis = phaUtil.orderParmTransPhaHistoryParm(parmOrd.
                getParm(
                    TModifiedList.MODIFIED));

            saveDate.setData("ORDER", parmOrd.getData());
            //ֻ�е���δ��ɡ�״̬��ʱ�򱣴����HISTORY�в�������
            if(!"Y".equals(getFinishFlag()))
                saveDate.setData("HISTORY", parmHis.getData());

        }else {
        	
            //��õ�ǰorderList��parm
            TParm parm = nowOrdList.getParm();

            //ע�⣺ͨ��parm.getData()����һ��TParm�����·��ѵ�����Ϊһ��MAP����Ȼ���ٰ����map����set����һ��TParm�У�����action�У�
            saveDate.setData("ORDER", parm.getData());
            //�Ƿ���ȡ����ҩ���ۿⷴ�������==��ҩ��--->��ҩ����������ɱ�ѡ��
            if("Dispense".equals(getType())&&"Y".equals(getFinishFlag()))
                this.setIsUnDisp(true);
            saveDate.setData("ISUNDISP_FLG",this.isIsUnDisp());
        }

        //�Ƿ���Ҫ���ÿ��ӿ�
        if("Examine".equals(getType())||"Send".equals(getType())){
            //��ˡ���ҩ����Ҫ���ÿ��ӿ�
            saveDate.setData("forIND",false);
        }else {
        	//��ҩ����ҩ��Ҫ���ÿ��ӿ�
        	saveDate.setData("forIND",true);
        }
          
        saveDate.setData("LOCK_FLG",getLockFlg());
        saveDate.setData("finishFlag",getFinishFlag());
        
        //���ú�̨��action
        TParm result = TIOM_AppServer.executeAction("action.pha.PHAAction",
            "onSave", saveDate);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }


    /**
     * ��onSaveǰ���õ�һ����������ÿ��order(��TParm)��д��Ӧ�ġ����䷢�ˡ�ʱ��
     * ordListParm ��Ҫ�����orderList��TParm
     * @param String ���ڸý���ı��
     */

    public void onExecute(OrderList ordList) {

        //���ϵͳ��ǰʱ��
        Timestamp optTime = TJDODBTool.getInstance().getDBTime();
        String optUser = Operator.getID();
        //��õ�ǰҳ������ͣ����䷢��
        String viewFlg = this.getType();
        //�õ�order����
        int count = ordList.size();
        //�޸ĸ�order�����ʱ��
        if (viewFlg.equals("Examine")) {
            //����ÿһ��order
            for (int i = 0; i < count; i++) {
                Order ord = ordList.getOrder(i);
                //����ȡ����orderList��ÿһ��order��ִ�б��,���ִ�еı���С��̡�
                if (ord.getExeFlg()) {
                    //δ��ɵ�ʱ�����ʱ�����Ա
                    if (finishFlg.equals("N")) {
                        ord.modifyPhaCheckCode(optUser);
                        ord.modifyPhaCheckDate(optTime);
                    }
                    else { //��ɵ�ʱ�����ʱ�����Ա
                        ord.modifyPhaCheckCode(null);
                        ord.modifyPhaCheckDate(null);
                    }
                }
            }
        }
        //�޸ĸ�order����ҩʱ��
        if (viewFlg.equals("Dispense")) {
            //����ÿһ��order
            for (int i = 0; i < count; i++) {
                Order ord = ordList.getOrder(i);
                //����ȡ����orderList��ÿһ��order,���ִ�еı���С��̡�
                if (ord.getExeFlg()) {
                    if (finishFlg.equals("N")) {
                        ord.modifyPhaDosageCode(optUser);
                        ord.modifyPhaDosageDate(optTime);
                        ord.modifyExecDrCode(optUser);
                        ord.modifyExecFlg("Y");
                        ord.modifyExecDate(optTime);
                        
                    }
                    else { //��ɵ�ʱ�����ʱ�����Ա
                        ord.modifyPhaDosageCode(null);
                        ord.modifyPhaDosageDate(null);
                        ord.modifyExecDrCode(optUser);
                        ord.modifyExecFlg("N");                
                    }
                }
            }
        }
        //�޸ĸ�order�ķ�ҩʱ��
        if (viewFlg.equals("Send")) {
            //����ÿһ��order
            for (int i = 0; i < count; i++) {
                Order ord = ordList.getOrder(i);
                //����ȡ����orderList��ÿһ��order,���ִ�еı���С��̡�
                if (ord.getExeFlg()) {
                    if (finishFlg.equals("N")) {
                        ord.modifyPhaDispenseCode(optUser);
                        ord.modifyPhaDispenseDate(optTime);
                        ord.modifyExecFlg("Y");
                    }
                    else { //��ɵ�ʱ�����ʱ�����Ա
                        ord.modifyPhaDispenseCode(null);
                        ord.modifyPhaDispenseDate(null);
                        ord.modifyExecFlg("N");
                    }
                }
            }
        }
        //��ҩ��ʱ�����DSCHECK_DATE DSDGT_DATE DSDLVRY_DATE������Ҫ����ʷ���в�������
        if (viewFlg.equals("Return")) {
            for (int i = 0; i < count; i++) {
                Order ord = ordList.getOrder(i);
                //����ȡ����orderList��ÿһ��order,���ִ�еı���С��̡�
                if (ord.getExeFlg()) {
                    if (finishFlg.equals("N")) {
                        ord.modifyPhaRetnCode(optUser);
                        ord.modifyPhaRetnDate(optTime);
                        ord.modifyExecDrCode(optUser);
                        ord.modifyExecFlg("N");
                        // modify by wangbin 20140808 ��ҩʱ���ִ��ʱ��
                        ord.modifyExecDate(null);
                    }
                    else { //��ɵ�ʱ�����ʱ�����Ա
                        ord.modifyPhaRetnCode(null);
                        ord.modifyPhaRetnDate(null);
                        ord.modifyExecDrCode(optUser);
                        ord.modifyExecFlg("Y");
                    }
                    //Ȼ����������䷢��ʱ�䣨����Ҫ���OPD_ORDER�е����䣬�����ϣ�
//                    ord.modifyPhaCheckCode(null);
//                    ord.modifyPhaCheckDate(null);
//                    ord.modifyPhaDosageCode(null);
//                    ord.modifyPhaDosageDate(null);
//                    ord.modifyPhaDispenseCode(null);
//                    ord.modifyPhaDispenseDate(null);
                }
            }
        }
    }

    //�õ���ѡ����Ǹ�orderList
    public OrderList getCertainOrdListByRow(int row) {

        OrderList ordList = new OrderList();
        ordList = phaUtil.getCertainOrdListByRow(row, orderListVector);
        return ordList;

    }

    public TParm getAllOrderListParm() {

        TParm result = new TParm();
        result = phaUtil.getAllOrderListParm(orderListVector);
        return result;
    }


    /**
     * �õ�ȫ������
     * ע�⣺һ�����TParm����Ҫװ��3��Сparm��newParm��modifiedParm��deletedParm����Ȼ�󴫸���̨action
     * @return TParm
     */
    public TParm getParm() {
        TParm result = new TParm();
        if (orderListVector == null || orderListVector.size() == 0)
            return result;
        int count = phaUtil.getOrderListCount(orderListVector);
        //����3��Сparm
        TParm newParm, modifiedParm, deletedParm;
        newParm = new TParm();
        modifiedParm = new TParm();
        deletedParm = new TParm();
        //����ҽ������
        for (int i = 0; i < count; i++) {
            OrderList ol = phaUtil.getCertainOrdListByRow(i, orderListVector);

            newParm = new TParm();
            //װ������ҽ��
            ol.getParm(OrderList.NEW, newParm);

            modifiedParm = new TParm();
            //װ���޸�ҽ��
            ol.getParm(OrderList.MODIFIED, modifiedParm);

            deletedParm = new TParm();
            //װ��ɾ��ҽ��
            ol.getParm(OrderList.DELETED, deletedParm);

        }
        newParm.setData("ACTION", "COUNT", newParm.getCount("CASE_NO"));
        modifiedParm.setData("ACTION", "COUNT", modifiedParm.getCount("CASE_NO"));
        deletedParm.setData("ACTION", "COUNT", deletedParm.getCount("CASE_NO"));
        //����������ݰ�
        result.setData(OrderList.NEW, newParm.getData());
        result.setData(OrderList.MODIFIED, modifiedParm.getData());
        result.setData(OrderList.DELETED, deletedParm.getData());
        result.setData("ACTION", "COUNT", count);
        return result;
    }


    /**
     * �������Ա��
     */
    public void setType(String pageType) {
        //���õ�ǰҳ�������
        this.type = pageType;
    }

    public String getType() {
        //���ص�ǰҳ�������
        return this.type;
    }

    /**
     * �����ѯ״̬���
     */
    public void setFinishFlag(String finishFlg) {
        //���õ�ǰҳ��Ĳ�ѯ״̬���
        this.finishFlg = finishFlg;
    }

    public String getFinishFlag() {
        //���ص�ǰҳ��Ĳ�ѯ״̬���
        return this.finishFlg;
    }

    /**
     * ��ǰѡ�е�ĳ��orderList
     */
    public void setOrdListRow(int row) {
        //���õ�ǰѡ�е�ĳ��orderList�к�
        this.ordListRow = row;
    }

    public void setIsUnDisp(boolean isUnDisp) {
        this.isUnDisp = isUnDisp;
    }

    public int getOrdListRow() {
        //���ص�ǰѡ�е�ĳ��orderList�к�
        return this.ordListRow;
    }

    public boolean isIsUnDisp() {
        return isUnDisp;
    }

	public String getLockFlg() {
		return lockFlg;
	}

	public void setLockFlg(String lockFlg) {
		this.lockFlg = lockFlg;
	}
    
    


}
