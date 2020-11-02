package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 药品销售报表 </p>
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
     * 实例
     */
    public static PhaMedSaleStaTool instanceObject;

    /**
     * 得到实例
     * @return OrderTool
     */
    public static PhaMedSaleStaTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaMedSaleStaTool();
        return instanceObject;
    }

    public PhaMedSaleStaTool() {

        //加载Module文件
        this.setModuleName("pha\\PhaMedSaleStaModule.x");                    

        onInit();

    }

    /**
     * 获得‘门急诊药房分类销售报表’的主数据
     * @return TParm
     */
    public TParm getQueryDate(TParm parm, String type) {

        String various=(String) parm.getData("VARIOUS");

        TParm result = new TParm();
        //未审核查询
        if (type.equals("01")) {
            if ("D".equals(various))
                result = query("queryNOTExamine_D", parm);
            if ("M".equals(various))  
                result = query("queryNOTExamine_M", parm); 
        } //未配药查询
        else if (type.equals("02")) {
            if ("D".equals(various))
                result = query("queryNOTDosage_D", parm);
            if ("M".equals(various))
                result = query("queryNOTDosage_M", parm);
        } //未发药查询
        else if (type.equals("03")) {
            if ("D".equals(various))
                result = query("queryNOTDispense_D", parm);
            if ("M".equals(various))
                result = query("queryNOTDispense_M", parm);
        } //已发药查询
        else if (type.equals("04")) {
            if ("D".equals(various))
                result = query("queryDispenseed_D", parm);
            if ("M".equals(various))
                result = query("queryDispenseed_M", parm);
        } //已退药查询
        else if (type.equals("05")) {
            if ("D".equals(various))
                result = query("queryReturned_D", parm);
            if ("M".equals(various))
                result = query("queryReturned_M", parm);
        }
        else if (type.equals("07")) {//已配药
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
