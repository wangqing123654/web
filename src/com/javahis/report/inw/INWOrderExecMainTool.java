package com.javahis.report.inw;
import com.dongyang.data.TParm;
import com.javahis.util.IReport;
/**
 * <p> Title: 预交金收据合并报表数据准备类 </p>
 * 
 * <p> Description: 预交金收据合并报表数据准备类  </p>
 * 
 * <p> Copyright: Bluecore 20130730 </p>
 * 
 * <p> Company: Bluecore </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class INWOrderExecMainTool implements IReport {
	/**
     * 准备报表传参
     * @param parm
     * @return
     */
    public TParm getReportParm(TParm parm) {
        return parm;
    }
}
