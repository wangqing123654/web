/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: ���򹤾߰�</p>
 *
 * <p>Description:���򹤾߰� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author ehui 2008.9.3
 * @version 1.0
 */
public class SYSRegionTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static SYSRegionTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SYSRegionTool
     */
    public static SYSRegionTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSRegionTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SYSRegionTool() {
        setModuleName("sys\\SYSRegionModule.x");
        onInit();
    }

    /**
     * �õ�ҽԺ��Ժ���ļ��
     * @return String
     */
    public String selRegionABN() {
        String regionABN = "";
        TParm result = query("selectRegionABN");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return regionABN;
        }
        regionABN=result.getValue("REGION_CHN_ABN",0);
        return regionABN;
    }

    /**
     * ������������ѯ����
     * @param REGION_CODE String �������
     * @return TParm
     */
    public TParm selectdata(String REGION_CODE) {
        TParm parm = new TParm();
        parm.setData("REGION_CODE", REGION_CODE);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯҳ��flag��˳��,һ��ƴ��20λ�����硰yyyyynnnnn����������Ϊstate_list���ֶ�ֵ
     * @param GROUP_ID
     * @return
     */
    public TParm getflagorder(String GROUP_ID) {
        TParm parm = new TParm();
        parm.setData("GROUP_ID", GROUP_ID);
        TParm result = query("getflagorder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public TParm selectall()
    {
        TParm result = query("selectall");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �õ�ip��������
     * @param ip String
     * @return String
     */
    public String getRegionByIP(String ip)
    {
        TParm result = selectall();
        for(int i = 0;i < result.getCount();i++)
        {
            String ipStart = result.getValue("IP_RANGE_START",i);
            String ipEnd = result.getValue("IP_RANGE_END",i);
            if(StringTool.isIPBetween(ipStart,ipEnd,ip))
                return result.getValue("REGION_CODE",i);
        }
        return "";
    }
    public static void main(String args[])
    {
        SYSRegionTool.getInstance().selectall();
    }

    /**
     * ����Ȩ�޹�����¼����Ϊ��ֵʱ,��ʾ�������ѡ��
     * ====pangben modify 20110422
     */
    public boolean getRegionIsEnabled(String regionCode) {
        if (!"".equals(regionCode) && null != regionCode)
            return false;
        else
            return true;
    }
}
