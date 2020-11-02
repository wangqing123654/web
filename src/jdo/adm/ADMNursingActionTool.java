package jdo.adm;

import com.dongyang.action.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ����ȼ��޸������</p>
 *
 * <p>Description: ����ȼ��޸������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-8-26
 * @version 1.0
 */
public class ADMNursingActionTool
    extends IOAction {
    public ADMNursingActionTool() {
    }

    /**
     * onInit
     *
     * @todo Implement this com.dongyang.action.IOAction method
     */
    public void onInit() {
    }

    /**
     * onRun
     *
     * @todo Implement this com.dongyang.action.IOAction method
     */
    public void onRun() {
        TConnection con = getConnection();
        TParm orderParm = this.getParm();
        String caseNo = orderParm.getValue("CASE_NO");
        //ACTION_CODE ��ʽ��  ����;����;ֵ
        String ACTION_CODE = orderParm.getValue("ACTION_CODE");
        String[] code = ACTION_CODE.split(";");
        TParm result = new TParm();
        if(code.length==3){
            String sql = "update " + code[0] + " set " + code[1] + "='" +
                code[2] + "' where case_no='" + caseNo + "'";
            result.setData(TJDODBTool.getInstance().update(sql, con));
            if(result.getErrCode()<0){
                System.out.println("SQL:"+sql);
            }
        }else{
            result.setErr(-1,"��δ���");
        }
        this.setResult(result);
    }
}
