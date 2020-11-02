package action.bil;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILInvrcptTool;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.data.TNull;

/**
 * ��ӡƱ��Action
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
public class BILRecipientsAction
    extends TAction {
    /**
     * ��ӡ����
     * ���Σ�BIL_InvrcptƱ�����ͣ���ӡƱ�ţ���ˮ�ţ�����Ա���ܽ�����ע�ǣ�����Ա������ʱ�䣬����ĩ��
     * RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,TOT_AMT,CANCEL_FLG,CANCEL_USER,CANCEL_DATE,OPT_USER,OPT_DATEOPT_TERM
     * ���Σ�BIL_Invoice,Ʊ�����ͣ���ʼƱ�ţ�����Ʊ�ţ���һƱ�ţ�������Ա������Ա������ʱ�䣬�����ն�
     * RECP_TYPE,START_INVNO,END_INVNO,UPDATE_NO,CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM
     * @param parm TParm
     * @return TParm
     */
    public TParm print(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        //���µ�ǰƱ��BIL_Invoice
        result = BILInvoiceTool.getInstance().updataData(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //�����ӡ��Ʊ��BIL_Invrcpt
        parm.setData("CANCEL_FLG", new TNull(String.class));
        parm.setData("STATUS", "0");
        result = BILInvrcptTool.getInstance().insertData(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ����Ʊ�ŷ���
     * ���Σ�BIL_InvrcptƱ�����ͣ���ӡƱ�ţ���ˮ�ţ�����Ա���ܽ�����ע�ǣ�����Ա������ʱ�䣬����ĩ��
     * RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,TOT_AMT,CANCEL_FLG,OPT_USER,OPT_DATE,OPT_TERM
     * ���Σ�BIL_Invoice,Ʊ�����ͣ���ʼƱ�ţ���һƱ�ţ�����Ʊ�ţ�������Ա������Ա������ʱ�䣬�����ն�
     * RECP_TYPE,START_INVNO,END_INVNO,UPDATE_NO,CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM
     * @param parm TParm
     * @return TParm
     */
    public TParm Adjustment(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        String updateno = (String) parm.getData("UPDATE_NO");
        parm.setData("TOT_AMT", 0);
        //����Ʊ��
        parm.setData("CANCEL_FLG", new TNull(String.class));
        parm.setData("STATUS", "2");
        //Invrcptѭ�����������Ʊ��
        for (int i = 0; i < (Integer) parm.getData("NUMBER"); i++) {
            //ȡ��ԭ��
            parm.setData("INV_NO",
                         SystemTool.getInstance().getNo("ALL", "BIL", "INV_NO",
                "INV_NO"));
            parm.setData("RECEIPT_NO", updateno);
            err(" StringTool.addString((String)parm.getData(UPDATE_NO)):" +
                StringTool.addString( (String) parm.getData("UPDATE_NO")));
            //���ò��뷽��
            result = BILInvrcptTool.getInstance().insertData(parm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                connection.close();
                return result;
            }
            //Ʊ���Լ�һ
            updateno = StringTool.addString(updateno);
        }
        //������ǰƱ��Invoice
        parm.setData("UPDATE_NO", parm.getData("NOWNUMBER"));
        result = BILInvoiceTool.getInstance().updataData(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }

        connection.commit();
        connection.close();
        return result;
    }


}
