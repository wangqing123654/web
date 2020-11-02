package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;


/**
*
* <p>Title: ���������ͳ��</p>
*
* <p>Description: ���������ͳ��</p>
*
* <p>Copyright: Copyright (c)2013</p>
*
* <p>Company: JavaHis</p>
*
* @author wangm 2013.3.18
* @version 1.0
*/
public class INDRadStatisticsTool extends TJDOTool {
	/**
     * ʵ��
     */
    public static INDRadStatisticsTool instanceObject;
    /**
     * �õ�ʵ��
     * @return InvoiceTool
     */
    public static INDRadStatisticsTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INDRadStatisticsTool();
        return instanceObject;
    }

    public INDRadStatisticsTool() {
        setModuleName("ind\\INDRadStatisticsModule.x");
        onInit();
    }
    
    //��ñ�������
    public TParm selectReportData(TParm parm){
		TParm result = this.query(parm.getValue("REPORTFLG"),parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
		return result; 
	}
    
}
