package com.javahis.report.bil;

import com.dongyang.data.TParm;
import com.javahis.util.IReport;
/**
 * <p> Title: 每日费用清单数据准备类 </p>
 * 
 * <p> Description: 每日费用清单数据准备类  </p>
 * 
 * <p> Copyright: Bluecore 20130730 </p>
 * 
 * <p> Company: Bluecore </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class BILDailyFeeQueryReportTool implements IReport {

    /**
     * 准备报表传参
     * @param parm
     * @return
     */
    public TParm getReportParm(TParm parm) {
        return parm;
    }
}
