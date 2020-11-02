package jdo.hl7;

import com.dongyang.config.TConfigParm;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @SHIBL
 * @version 1.0
 */
public class PDFJdo extends TJDOTool {
    /**
     * 实例
     */
    public static PDFJdo instanceObject;
    /**
     * 得到实例
     * @return PDFJdo
     */
    public static PDFJdo getInstance() {
        if (instanceObject == null) {
            instanceObject = new PDFJdo();
        }
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PDFJdo() {
        setModuleName("pdf\\pdfSQL.x");
        onInit();
    }

    /**
     * 得到系统时间
     * @return Timestamp 生效日期
     */
    public Timestamp getDate() {
        TParm parm = new TParm();
        return getResultTimestamp(query("getDate", parm), "SYSDATE");
    }

    /**
     * 得到检验检查数据
     * @param labnumberNo String
     * @param type String
     * @return TParm
     */
    public synchronized TParm QueryMedapply(String labnumberNo, String type) {
        TParm parm=new TParm();
        parm.setData("APPLICATION_NO",labnumberNo);
        parm.setData("CAT1_TYPE",type);
        TParm action = this.query("QueryMedapply",parm);
//        System.out.println("-------------"+action.getCount());
        return action;
    }
    /**
    * 更新得到PDF文件注记
    * @param labnumberNo String
    * @param type String
    * @return TParm
    */
   public synchronized TParm updateMedapplyPdf(String labnumberNo, String type) {
       TParm parm=new TParm();
       parm.setData("APPLICATION_NO",labnumberNo);
       parm.setData("CAT1_TYPE",type);
       parm.setData("PDFRE_FLG","Y");
       TParm action = this.update("updateMedapplyPdf",parm);
//        System.out.println("-------------"+action.getCount());
       return action;
   }


    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
