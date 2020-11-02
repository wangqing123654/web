package jdo.ins;

import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p>Title: ҽ��������</p>
 *
 * <p>Description: ҽ��������</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2011.09.20
 * @version 1.0
 */
public class INSTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static INSTool instanceObject;
    /**
     * �õ�ʵ��
     * @return INSTool
     */
    public static INSTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INSTool() {
        onInit();
    }

    /**
     * ҽ���Һ�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onSaveREG(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("ҽ���Һ�" + parm);
        String saveREG =
            " INSERT INTO INS_ORDM " +
            "        (HOSP_AREA,YEAR_MON,CASE_NO,OPT_USER,OPT_DATE,OPT_TERM) " +
            " VALUES ('" + "HIS" + "','" + "201109" + "','" + "20110920000001" +
            "','" + "tiis" + "',SYSDATE,'" + "192.168.1.106" + "' )";
        //System.out.println("saveREG" + saveREG);
        result = new TParm(TJDODBTool.getInstance().update(saveREG,
            connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * ҽ���˹�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onReturnREG(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("ҽ���˹�" + parm);
        String returnREG =
            " INSERT INTO INS_ORDM " +
            "             (HOSP_AREA,YEAR_MON,CASE_NO = '" + "HIS" + "'," +
            "        YEAR_MON = '" + "201109" + "'," +
            "        CASE_NO = '" + "20110920000001" + "'," +
            "        OWN_RATE = '" + "201109" + "'," +
            "        DECREASE_RATE = 0.1," +
            "        REALOWN_RATE = '" + "201109" + "'," +
            "        INSOWN_RATE = '" + "201109" + "'," +
            "        ";
//        System.out.println("returnREG"+returnREG);
        result = new TParm(TJDODBTool.getInstance().update(returnREG,
            connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * ҽ�����λ����
     * ����ҽ������״̬
     * @param parm ActionParm HOSP_AREA
     * @return TParm NHI_DEBUG,REG_NHI_ONLINE,OPB_NHI_ONLINE<BR>
     * NHI_DEBUG <BR>
     * 0 ��׼����������<BR>
     * 1 ���ԣ��������ҽ����̬�������õĲ����ͽ������־�ļ�<BR>
     * REG_NHI_ONLINE<BR>
     * 0 ��ʱ���ӣ��ڹҺű���ͬʱ��ֻ�ǽ��Һ�����д������ҽ����ϸ����Ժ�վ�ҵ���д���ҽ��ҵ��<BR>
     * 1 ʵʱ���ӣ��ڹҺű���ͬʱ���������Һ�����д������ҽ����ϸ����Ҫ��������ʵʱ����<BR>
     * OPB_NHI_ONLINE<BR>
     * 0 ��ʱ���ӣ����շѱ���ͬʱ��ֻ�ǽ���������д������ҽ����ϸ��<BR>
     * 1 ʵʱ���ӣ����շѱ���ͬʱ����������������д������ҽ����ϸ����Ҫ��������ʵʱ����<BR>
     */
    public TParm getNhiFlg(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1,
                          "module.ins.InsModule.getNhiFlg(ActionParm)->Err:��������ΪNULL");
            return result;
        }
        if (parm.checkEmpty("HOSP_AREA", result))
            return result;
        String sql =
            " SELECT ACTIVE_FLG AS NHI_DEBUG,MAIN_FLG AS REG_NHI_ONLINE,MAIN_FLG AS OPB_NHI_ONLINE " +
            "   FROM SYS_REGION" +
            "  WHERE REGION_CODE='" + parm.getValue("HOSP_AREA") + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ�ҽ����������
     * @param parm TParm
     * @return TParm
     */
    public TParm getSysParm(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1,
                          "module.ins.InsModule.getSysparm(ActionParm)->Err:��������ΪNULL");
            return result;
        }
        if (parm.checkEmpty("HOSP_AREA", result))
            return result;
        String sql =
            "SELECT CITY,SEPARATOR,NEWLINE,FINISH" +
            "  FROM INS_SYSPARM" +
            " WHERE REGION_CODE='" + parm.getValue("HOSP_AREA") + "'";
        //System.out.println("�õ�ҽ����������"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ȡҽ���ӿں�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getIOParm(TParm parm) {
        //System.out.println("sql���"+parm);
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1,
                          "module.ins.InsModule.getIOParm(ActionParm)->Err:��������ΪNULL");
            return result;
        }
        if (parm.checkEmpty("CITY,PIPELINE,PLOT_TYPE", result))
            return result;
        String sql =
            "SELECT ROW_COUNT" +
            "  FROM INS_IO_PARM" +
            " WHERE CITY='" + parm.getValue("CITY") + "'" +
            "   AND PIPELINE = '" + parm.getValue("PIPELINE") + "'" +
            "   AND PLOT_TYPE = '" + parm.getValue("PLOT_TYPE") + "'";
        //System.out.println("��ȡҽ���ӿں�����Ϣ"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * ��ȡҽ�����ݲ�����ʽ
     * @param parm TParm CITY ����,PIPELINE �˿�,PLOT_TYPE ����,IN_OUT IO����
     * @return TParm
     */
    public TParm getIOData(TParm parm) {
        //System.out.println("��ȡҽ�����ݲ�����ʽparm"+parm);
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1,
                          "module.ins.InsModule.getIOData(ActionParm)->Err:��������ΪNULL");
            return result;
        }
        if (parm.checkEmpty("CITY,PIPELINE,PLOT_TYPE,IN_OUT", parm))
            return result;
        String sql =
            "SELECT ID,COLUMN_NAME,COLUMN_DESC,DATA_TYPE,LENGTH,PRECISION," +
            "       SCALE,NEED,DEFAULT_VALUE,FORMAT" +
            "  FROM INS_IO" +
            " WHERE CITY='" + parm.getValue("CITY") + "'" +
            "   AND PIPELINE = '" + parm.getValue("PIPELINE") + "'" +
            "   AND PLOT_TYPE = '" + parm.getValue("PLOT_TYPE") + "'" +
            "   AND IN_OUT = '" + parm.getValue("IN_OUT") + "'" +
            " ORDER BY ID";
        //System.out.println("sql"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    /**
     * �õ�ҽ������
     * @param parm TParm
     * @return TParm
     */
    public TParm getInsParm(TParm parm) {
        TParm result = new TParm();
        parm.setData("IN_OUT", "IN");
        result.setData("IN_COLUMN", this.getIOData(parm)); 
        parm.setData("IN_OUT", "OUT");
        result.setData("OUT_COLUMN", this.getIOData(parm));
        return result;

    }
    
    /**
     * 
     * ��ȡSYS_REGION���е���Ϣ.
     * @param regionCode
     * @return TParm
     */
    public TParm getSysRegionInfo(TParm parm){
    	TParm result = new TParm();
    	String sql = "Select REGION_CHN_ABN, NHI_NO from SYS_REGION " +
                " Where REGION_CODE = '" + parm.getValue("REGION_CODE") + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }  
        return result ;
    }

	/**
	 * ��ò�������ҽ������
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm queryPatHistoryInfo(TParm parm) {//add by wanglong 20120921
		TParm result = new TParm();
		String mrNo = parm.getValue("MR_NO") ;
		String startDate = parm.getValue("START_DATE").split(" ")[0];
		String endDate = parm.getValue("END_DATE").split(" ")[0];
    	String sql =
           "SELECT A.MR_NO, A.PAT_NAME, B.CONFIRM_NO, B.INS_DATE,"+
    	         "(CASE WHEN B.UNREIM_AMT IS NULL THEN 0 ELSE B.UNREIM_AMT END) AS UNREIM_AMT,"+
    	         "(CASE WHEN B.OTOT_PAY_AMT IS NULL THEN 0 ELSE B.OTOT_PAY_AMT END) AS OTOT_PAY_AMT,"+
    	         "(CASE WHEN B.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE B.ACCOUNT_PAY_AMT END) AS ACCOUNT_PAY_AMT "+
    	    "FROM SYS_PATINFO A, INS_OPD B "+
    	   "WHERE     A.MR_NO = '" + mrNo + "' "+
    	         "AND A.MR_NO = B.MR_NO "+
    	         "AND B.INS_DATE BETWEEN TO_DATE ('" + startDate + " 00:00:00', 'YYYY/MM/DD hh24:mi:ss') "+
    	                            "AND TO_DATE ('" + endDate + " 23:59:59', 'YYYY/MM/DD hh24:mi:ss') "+
    	"ORDER BY B.INS_DATE";
        result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;
	}
}
