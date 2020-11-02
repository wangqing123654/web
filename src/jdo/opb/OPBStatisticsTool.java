package jdo.opb;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;


/**
*
* <p>Title: �ţ���������ϸ�����ܱ���</p>
*
* <p>Description: �ţ���������ϸ�����ܱ���</p>
*
* <p>Copyright: Copyright (c)2013</p>
*
* <p>Company: JavaHis</p>
*
* @author wangm 2013.3.14
* @version 1.0
*/
public class OPBStatisticsTool extends TJDOTool {

	/**
     * ʵ��
     */
    public static OPBStatisticsTool instanceObject;
    /**
     * �õ�ʵ��
     * @return InvoiceTool
     */
    public static OPBStatisticsTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPBStatisticsTool();
        return instanceObject;
    }

    public OPBStatisticsTool() {
        setModuleName("opb\\OPBStatisticsModule.x");
        onInit();
    }
	
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
