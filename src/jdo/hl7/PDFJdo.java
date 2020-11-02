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
     * ʵ��
     */
    public static PDFJdo instanceObject;
    /**
     * �õ�ʵ��
     * @return PDFJdo
     */
    public static PDFJdo getInstance() {
        if (instanceObject == null) {
            instanceObject = new PDFJdo();
        }
        return instanceObject;
    }

    /**
     * ������
     */
    public PDFJdo() {
        setModuleName("pdf\\pdfSQL.x");
        onInit();
    }

    /**
     * �õ�ϵͳʱ��
     * @return Timestamp ��Ч����
     */
    public Timestamp getDate() {
        TParm parm = new TParm();
        return getResultTimestamp(query("getDate", parm), "SYSDATE");
    }

    /**
     * �õ�����������
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
    * ���µõ�PDF�ļ�ע��
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
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
