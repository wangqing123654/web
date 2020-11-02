package action.sys;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.sys.OperatorTool;
import javax.servlet.http.HttpServletRequest;
import jdo.sys.SYSOperatorTool;
import jdo.device.WindowsDriver;
/**
 *
 * <p>Title: ��¼������</p>
 *
 * <p>Description: ��¼������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.9.3
 * @version 1.0
 */
public class LoginAction extends TAction{
    /**
     * ע��
     * @param parm TParm
     * @return TParm
     */
    public TParm getRegistry(TParm parm)
    {
        WindowsDriver.setConame(parm.getValue("CONAME"));
        WindowsDriver.setKey(parm.getValue("KEY"));
        String key = WindowsDriver.checkKey();
        TParm result = new TParm();
        result.setData("KEY",key);
        return result;
    }
    /**
     * �õ�ע����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getInfo(TParm parm)
    {
        String computer = WindowsDriver.getComputer();
        String coname = WindowsDriver.getConame();
        String key = WindowsDriver.getKey();
        TParm result = new TParm();
        result.setData("COMPUTER",computer);
        result.setData("CONAME",coname);
        result.setData("KEY",key);
        return result;
    }
    /**
     * �õ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm getOutNo(TParm parm)
    {
        String key = WindowsDriver.getOutNo(parm.getValue("CONAME"));
        TParm result = new TParm();
        result.setData("KEY",key);
        return result;
    }
    /**
     * ��¼��֤
     * @param parm TParm
     * @return TParm
     */
    public TParm login(TParm parm)
    {
        TParm result = new TParm();
//        String key = WindowsDriver.checkKey();
//        if(!"OK".equals(key))
//        {
//        	//������   ע�ᵽ����ʾ��Ϣ���Ե���ע�������ҽԺע��
//        	if(key.indexOf("service@bluecore.com.cn")!=-1){
//        		//System.out.println("-----key come in.-----");
//        		result.setErr(-3,key);
//        		result.setData("IP",((HttpServletRequest)parm.getData("SYSTEM","REQUEST")).getRemoteAddr());
//	            return result;
//        	}else{
//	            result.setErr(-2,key);
//	            result.setData("IP",((HttpServletRequest)parm.getData("SYSTEM","REQUEST")).getRemoteAddr());
//	            return result;
//        	}
//        	//
//        }
        //
        String userID = parm.getValue("USER_ID");
        String password = parm.getValue("USER_PASSWORD");
        if (!OperatorTool.getInstance().existsOperator(userID))
        {
            result.setErr(-1,"İ���û�" + userID + "!");
            return result;
        }
        if (!password.equals(OperatorTool.getInstance().decrypt(OperatorTool.getInstance().getOperatorPassword(userID)))) {
                SYSOperatorTool.getInstance().updateAbnormaiTimes(
                                userID);
                result.setErr(-1,"�������!");
                return result;
        }
        if (!OperatorTool.getInstance().affecttedTimeOperator(userID))
        {
            result.setErr(-1,"�û�ʱЧ����");
            return result;
        }
        
        /**if (OperatorTool.getInstance().getAbnormaiTimes(userID) >= 3)
        {
            result.setErr(-1,"�쳣��½����3�Σ�����ϵ����Ա����");
            return result;
        }**/
        SYSOperatorTool.getInstance().resetAbnormaiTimes(userID);
        OperatorTool.getInstance().updateRcntLoginDate(userID);
        result.setData("IP",((HttpServletRequest)parm.getData("SYSTEM","REQUEST")).getRemoteAddr());
        return result;
    }
    /**
     * ��¼����
     * @param parm TParm
     * @return TParm
     */
    public TParm logincheck(TParm parm)
    {
        TParm result = new TParm();
        String userID = parm.getValue("USER_ID");
        String password = parm.getValue("PASSWORD");
        if (!OperatorTool.getInstance().existsOperator(userID))
        {
            result.setErr(-1,"İ���û�" + userID + "!");
            return result;
        }
        if (!password.equals(OperatorTool.getInstance().decrypt(OperatorTool.getInstance().getOperatorPassword(userID)))) {
            result.setErr(-2,"�������!");
            return result;
        }
        if (!OperatorTool.getInstance().affecttedTimeOperator(userID))
        {
            result.setErr(-3,"�û�ʱЧ����");
            return result;
        }
        OperatorTool.getInstance().updateRcntLoginDate(parm.getValue("USER_ID"));
        return result;
    }
    /**
     * �õ��û�IP
     * @param parm TParm
     * @return TParm
     */
    public TParm getIP(TParm parm)
    {
        TParm result = new TParm();
        result.setData("IP",((HttpServletRequest)parm.getData("SYSTEM","REQUEST")).getRemoteAddr());
        return result;
    }
}
