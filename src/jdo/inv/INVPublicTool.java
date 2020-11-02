package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

public class INVPublicTool  extends TJDOTool {
    /**
     * ʵ��
     */
    public static INVPublicTool instanceObject;
    /**
     * �õ�ʵ��
     * @return INVVerifyinTool
     */
    public static INVPublicTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVPublicTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INVPublicTool() {
        setModuleName("inv\\PublicModule.x");
        onInit();
    }
    /**
     * �õ����ʿ��ҵ�������Ա
     * @return TParm
     */
    public TParm getInvOperator(){
        TParm result=this.query("getInvOperator");
        if(result.getErrCode()<0)
            out(result.getErrText());
        return result;
    }
    /**
     * �õ���ǰ��Ա��Ĭ�Ͽ���
     * @return TParm
     */
    public TParm getOperatorDept(String userId){
        TParm result=new TParm();
        if(userId==null||userId.length()==0)
            return result.newErrParm(-1,"��������Ϊ��");
        result.setData("USER_ID",userId);
        result=this.query("getOperatorDept",result);
       if(result.getErrCode()<0)
           out(result.getErrText());
       return result;
    }
    /**
     * �������ʵĿ���
     * @param orgType String
     * @return TParm
     */
    public TParm getOrgCode(String orgType) {
        TParm result = new TParm();
        if (orgType == null || orgType.length() == 0)
            return result.newErrParm( -1, "��������Ϊ��");
        result.setData("ORG_TYPE", orgType);
        result = this.query("getOrgCode", result);
        if (result.getErrCode() < 0)
            out(result.getErrText());
        return result;
    }
    /**
     * �õ���Ӧ�ҵ�����
     * @return TParm
     */
    public TParm getOrgDesc(){
        TParm  result = this.query("getOrgDesc");
       if (result.getErrCode() < 0)
           out(result.getErrText());
       return result;

    }
}
