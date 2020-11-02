package jdo.ins;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;


/**
 * <p>
 * Title: ҽ�����ϲ�ѯ
 * </p>
 *
 * <p>
 * Description: ҽ�����ϲ�ѯ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author lim 2009.04.22
 * @version 1.0
 */
public class INSMaterialSearchTool extends TJDOTool {

    /**
     * ʵ��
     */
    public static INSMaterialSearchTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return BMSBloodTool
     */
    public static INSMaterialSearchTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSMaterialSearchTool();
        return instanceObject;
    }

    public TParm checkedSelected(TConnection conn,TParm inParm){
		String admSeq = inParm.getValue("ADM_SEQ", 0) ;
		String hospNhiNo= inParm.getValue("HOSP_NHI_NO",0) ;
		//String confirmNo = inParm.getValue("CONFIRM_NO",0) ;
		TParm parm = new TParm() ;
		//============pangben 2012-4-10 start INS_TYPE:1 ��ְ 2.�Ǿ�
		if (inParm.getValue("INS_TYPE").equals("1")) {
			parm.setData("PIPELINE", "DataDown_sp") ;
			parm.setData("PLOT_TYPE", "Q") ;
	        parm.addData("CONFIRM_NO", admSeq);
			parm.addData("HOSP_NHI_NO", hospNhiNo) ;
			parm.addData("PARM_COUNT", 2);
		}else if(inParm.getValue("INS_TYPE").equals("2")){
			parm.setData("PIPELINE", "DataDown_czys") ;
			parm.setData("PLOT_TYPE", "D") ;
	        parm.addData("CONFIRM_NO", admSeq);
			parm.addData("HOSP_NHI_NO", hospNhiNo) ;
			parm.addData("PARM_COUNT", 2);
		}
		//============pangben 2012-4-10 stop 
		TParm resultParm = InsManager.getInstance().safe(parm,null);
		if (resultParm == null) {
			resultParm = new TParm() ;
			resultParm.setErr(-1, "ҽ���ӿ�DataDown_sp��Q��������NULL") ;
			return resultParm;
		}

		int count = resultParm.getCount("ALLOW_FLG_FLG"); 

		if (count <= 0) {
			resultParm = new TParm() ;
			resultParm.setErr(-1, resultParm.getValue("PROGRAM_MESSAGE")) ;
			return resultParm;
		}

		String allowFlgFlg = resultParm.getValue("ALLOW_FLG_FLG", 0) ;

		if(!"0".equals(allowFlgFlg) && !"".equals(allowFlgFlg)){
			Timestamp date = SystemTool.getInstance().getDate() ;
			String formattedDate = StringTool.getString(date, "yyyy-MM-dd HH:mm:ss") ;
			String sql = "update INS_ADM_CONFIRM set IN_STATUS = '7',DOWN_DATE = to_date('"+formattedDate+"','yyyy-mm-dd hh24:mi:ss') WHERE ADM_SEQ='"+admSeq+"'" ;

			TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn)) ;

            if (result.getErrCode() < 0) {
                return result;
            }
            return result ;
		}else{
			TParm result = new TParm() ;
			result.setErr(-1, resultParm.getValue("PROGRAM_MESSAGE")) ;
			return result ;
		}
    }

    public TParm cancelSelected(TConnection conn,TParm inParm)
    {
		String admSeq = inParm.getValue("ADM_SEQ", 0) ;
		String hospNhiNo= inParm.getValue("HOSP_NHI_NO",0) ;
		String confirmNo = inParm.getValue("CONFIRM_NO",0) ;
		
		//============pangben 2012-4-10 start INS_TYPE:1 ��ְ 2.�Ǿ�
		TParm parm = new TParm() ;
		if (inParm.getValue("INS_TYPE").equals("1")) {
			parm.setData("PIPELINE", "DataDown_sp") ;
			parm.setData("PLOT_TYPE", "C") ;
			parm.addData("CONFIRM_NO", admSeq) ;
			parm.addData("HOSP_NHI_NO", hospNhiNo) ;
			parm.addData("PARM_COUNT", 2);
		}else if(inParm.getValue("INS_TYPE").equals("2")){
			parm.setData("PIPELINE", "DataDown_czys") ;
			parm.setData("PLOT_TYPE", "F") ;
			parm.addData("CONFIRM_NO", admSeq) ;
			parm.addData("HOSP_NHI_NO", hospNhiNo) ;
			parm.addData("PARM_COUNT", 2);
		}
		//============pangben 2012-4-10 STOP
		TParm resultParm = InsManager.getInstance().safe(parm);
		if (resultParm == null) {
			resultParm = new TParm() ; 
			resultParm.setErr(-1, "����ʧ��") ;
			return resultParm;
		}
		if (resultParm.getErrCode()<0) {
			return resultParm;
		}
		if (resultParm.getInt("PROGRAM_STATE")<0) {
			resultParm.setErr(-1, resultParm.getValue("PROGRAM_MESSAGE")) ;
			return resultParm;
		}
		String sql = "UPDATE INS_ADM_CONFIRM SET IN_STATUS = '5' WHERE ADM_SEQ='"+admSeq+"'" ; 
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn)) ;
        if (result.getErrCode() < 0) {
           return result;
        }
        return result ;
    }
}
