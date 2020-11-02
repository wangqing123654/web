package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ������˱�׼</p>
 *
 * <p>Description: ������˱�׼</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-4
 * @version 1.0
 */
public class MROChrtvetstdTool
    extends TJDOTool {
    /**
     * ʵ��
     *///��Ŀ��׼��ֵ���˷�ֵ���ղ�������������׼�е�����˳����ʾ������ڱ��������˳����ҷ�ֵ����ʾ����
    public static MROChrtvetstdTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MROChrtvetstdTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROChrtvetstdTool();
        return instanceObject;
    }

    public MROChrtvetstdTool() {
        this.setModuleName("mro\\MROChrtvetstdModule.x");
        this.onInit();
    }
    /**
     * ��ѯ����
     * @param parm TParm ����˵����TYPE_CODE,TYPE_DESC������������ ���п���
     * @return TParm
     */
    public TParm selectdata(String EXAMINE_CODE){
        TParm pm = new TParm();
        pm.setData("EXAMINE_CODE",EXAMINE_CODE+"%");
        TParm result = query("selectdata",pm);
        // �жϴ���ֵ
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ����
     * @param EXAMINE_CODE String
     * @param METHOD_CODE String
     * @return TParm
     */
    public TParm selectdata(String EXAMINE_CODE, String CHECK_RANGE,
                            String METHOD_CODE) {
        TParm pm = new TParm();
        if (!"".equals(EXAMINE_CODE)) {
            pm.setData("EXAMINE_CODE", EXAMINE_CODE + "%");
        }
        pm.setData("CHECK_RANGE", CHECK_RANGE);
        if (!"".equals(METHOD_CODE)) {
            pm.setData("METHOD_CODE", METHOD_CODE);
        }
        TParm result = query("selectdata", pm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = update("insertdata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��������
     * @param regMethod String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ������
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(String code){
        TParm parm = new TParm();
        parm.setData("EXAMINE_CODE",code);
        TParm result = update("deletedata",parm);
        // �жϴ���ֵ
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
