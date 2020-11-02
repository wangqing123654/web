package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: ҩƷ���۱��� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2008.09.26
 * @version 1.0
 */
public class PhaMedSaleStaTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    public static PhaMedSaleStaTool instanceObject;

    /**
     * �õ�ʵ��
     * @return OrderTool
     */
    public static PhaMedSaleStaTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaMedSaleStaTool();
        return instanceObject;
    }

    public PhaMedSaleStaTool() {

        //����Module�ļ�
        this.setModuleName("pha\\PhaMedSaleStaModule.x");                    

        onInit();

    }

    /**
     * ��á��ż���ҩ���������۱�����������
     * @return TParm
     */
    public TParm getQueryDate(TParm parm, String type) {

        String various=(String) parm.getData("VARIOUS");

        TParm result = new TParm();
        //δ��˲�ѯ
        if (type.equals("01")) {
            if ("D".equals(various))
                result = query("queryNOTExamine_D", parm);
            if ("M".equals(various))  
                result = query("queryNOTExamine_M", parm); 
        } //δ��ҩ��ѯ
        else if (type.equals("02")) {
            if ("D".equals(various))
                result = query("queryNOTDosage_D", parm);
            if ("M".equals(various))
                result = query("queryNOTDosage_M", parm);
        } //δ��ҩ��ѯ
        else if (type.equals("03")) {
            if ("D".equals(various))
                result = query("queryNOTDispense_D", parm);
            if ("M".equals(various))
                result = query("queryNOTDispense_M", parm);
        } //�ѷ�ҩ��ѯ
        else if (type.equals("04")) {
            if ("D".equals(various))
                result = query("queryDispenseed_D", parm);
            if ("M".equals(various))
                result = query("queryDispenseed_M", parm);
        } //����ҩ��ѯ
        else if (type.equals("05")) {
            if ("D".equals(various))
                result = query("queryReturned_D", parm);
            if ("M".equals(various))
                result = query("queryReturned_M", parm);
        }
        else if (type.equals("07")) {//����ҩ
            if ("D".equals(various))  
                result = query("queryDosaged_D", parm);
            if ("M".equals(various))
                result = query("queryDosaged_M", parm);
        }
        else{
            if ("D".equals(various))
                result = query("queryBill_D", parm);
            if ("M".equals(various))
                result = query("queryBill_M", parm);
        }

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;
    }


}
