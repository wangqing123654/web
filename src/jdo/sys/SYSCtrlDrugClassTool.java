package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title: ����ҩƷ����</p>
 *
 * <p>Description: �����й�����ҩƷ�����ȫ�����ݴ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author JiaoY 2008-09-2
 * @version 1.0
 */

public class SYSCtrlDrugClassTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static SYSCtrlDrugClassTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PositionTool
     */
    public static SYSCtrlDrugClassTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSCtrlDrugClassTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SYSCtrlDrugClassTool() {
        setModuleName("sys\\SYSCtrlDrugClassModule.x");
        onInit();
    }

    /**
     * ��ѯ����
     * @param ctrldugclasscode String
     * @return TParm
     */
    public TParm selectdata(String ctrldugclasscode) {
        TParm parm = new TParm();

        if (ctrldugclasscode != null && ctrldugclasscode.length() > 0)
            parm.setData("CTRLDRUGCLASS_CODE", ctrldugclasscode);
        TParm result = query("selectall", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * ��֤SYS_CTRLDRUGCLASS�����Ƿ����CTRLDRUGCLASS_CODE
     * @param parm TParm
     * @return boolean true ������ false û������
     */
    public TParm existsData(TParm parm) {
//       System.out.println("��֤begin");
        TParm result = query("existsCTRLDRUGCLASS_CODE", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
//        System.out.println("��֤end--��"+result);
        return result;

    }

    /**
     * ��֤pha_base�����Ƿ����CTRLDRUGCLASS_CODE����������ɾ��
     * @param parm TParm
     * @return TParm
     */
    public TParm existsPHA(TParm parm) {
        TParm result = query("existsPHA", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;

    }


    /**
     * ����һ������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm) {
//       System.out.println("����begin");
        TParm result = new TParm();
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * ����һ������
     * @param parm TParm
     * @return TParm
     */
    public TParm updataData(TParm parm) {
        TParm result = new TParm();
        result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;

    }

    /**
     * ɾ��һ������
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteData(TParm parm) {
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;

    }

    /**
     * �ж�ҩƷ�Ƿ�Ϊ����ҩ
     * @param order_code String
     * @return boolean
     */
    public boolean getOrderCtrFlg(String order_code) {
        String sql = SYSSQL.getOrderCtrFlg(order_code);
        //System.out.println("getOrderCtrFlg.sql="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return false;
        }
        if (result.getCount("CTRLDRUGCLASS_CODE") < 0) {
            return false;
        }
        return true;
    }

    /**
     * �ж϶���ҩ��ʹ�����Ƿ񳬹������
     * @param order_code String
     * @param qty double
     * @return boolean
     */
    public boolean getCtrOrderMaxDosage(String order_code, double qty) {
        TParm result = new TParm(TJDODBTool.getInstance().select(SYSSQL.
            getPhaTransUnit(order_code)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return false;
        }
        if (result.getCount("MEDI_QTY") < 0) {
            return false;
        }
        if (qty / result.getDouble("MEDI_QTY", 0) > 1) {
            return false;
        }
        return true;
    }

}
