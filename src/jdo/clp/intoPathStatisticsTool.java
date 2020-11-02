package jdo.clp;

import com.dongyang.jdo.TJDOTool;
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
public class intoPathStatisticsTool extends TJDOTool {

    //ʹ��������ǵ�����,ֻ�ܳ�ʼ��һ������
    private static intoPathStatisticsTool instance = null;
    //�������Ȩ����Ϊ˽��,����ͨ�����췽��ʵ����,ֻ��ͨ����̬���������õ�����Ķ���,���Ҹö���ֻ�ᱻʵ����һ��
    private intoPathStatisticsTool() {
        //����Module�ļ�,�ļ���ʽ������˵��
        this.setModuleName("clp\\intoPathStatisticsModule.x");
        onInit();
    }

    /**
     * ��̬��������ֻ����һ��ʵ��
     * @return MyJDOTool
     */
    public static intoPathStatisticsTool getNewInstance() {
        if (instance == null) {
            instance = new intoPathStatisticsTool();
        }
        return instance;
    }

    public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    public TParm onQuerySum(TParm parm) {
        TParm result = this.query("querySum", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    public TParm onQueryNo2(TParm parm) {
        TParm result = this.query("queryNo2", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    public TParm onQueryNo3(TParm parm) {
        TParm result = this.query("queryNo3", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}

