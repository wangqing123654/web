package jdo.med;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MEDApplyTool
    extends TJDODBTool {
    /**
     * 实例
     */
    public static MEDApplyTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MEDApplyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEDApplyTool();
        return instanceObject;
    }

    /**
     * 保存住院医嘱
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm saveMedApply(TParm parm, TConnection con) {
        String sqlStr[] = (String[]) parm.getData("SQL");
        TParm result = new TParm(this.update(sqlStr, con));
        return result;
    }
    
    /**
     * 修改打印标记
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm saveMedApplyPrtFlg(TParm parm, TConnection con) {
        String sqlStr[] = (String[]) parm.getData("PRTFLGSQL");
        for(int i = 0;i<sqlStr.length;i++){
        }
        
        TParm result = new TParm(this.update(sqlStr, con));
        return result;
    }
    
    /**
     * 查询医技状态
     * @param parm TParm
     * @return TParm
     */
    public TParm queryMedApply(String caseNo){
        TParm result = new TParm(this.select("SELECT * FROM MED_APPLY WHERE CASE_NO='"+caseNo+"'"));
        return result;
    }
    /**
     * 更新状态
     * @param caseNo String
     * @param applyNo String
     * @return TParm
     */
    public TParm updateStauts(String caseNo,String applyNo,String orderNo,String seqNo,String billFlg,TConnection con){
//        System.out.println("UPDATE MED_APPLY SET BILL_FLG='"+billFlg+"' WHERE CASE_NO='"+caseNo+"' AND APPLICATION_NO='"+applyNo+"' AND ORDER_NO='"+orderNo+"' AND SEQ_NO='"+seqNo+"'");
        TParm result = new TParm(this.update("UPDATE MED_APPLY SET BILL_FLG='"+billFlg+"' WHERE CASE_NO='"+caseNo+"' AND APPLICATION_NO='"+applyNo+"' AND ORDER_NO='"+orderNo+"' AND SEQ_NO='"+seqNo+"'",con));
        return result;
    }
    /**
     * 插入MED_APPLY表数据补充计价
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm insertMedApply(TParm parm,TConnection con){
        TParm result = new TParm();
        if(parm==null){
            parm.setErrCode(-1);
            parm.setErrText("无数据！");
            return parm;
        }
        int count = parm.getCount("CASE_NO");
//        System.out.println("数量=="+count);
        for(int i=0;i<count;i++){
            if(("Y".equals(parm.getValue("SETMAIN_FLG",i))&&"LIS".equals(parm.getValue("CAT1_TYPE",i)))||("Y".equals(parm.getValue("SETMAIN_FLG",i))&&"RIS".equals(parm.getValue("CAT1_TYPE",i)))){
                String sql = createSql(parm.getRow(i));
//                System.out.println("插入MED"+sql);
                result = new TParm(this.update(sql,con));
                if(result.getErrCode()!=0)
                    return result;
            }
        }
        return result;
    }
    /**
     * 查询检体名称
     * @param iptCode String
     * @return String
     */
    public String queryOptItem(String iptCode){
        TParm parm = new TParm(this.select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OPTITEM' AND ID = '"+iptCode+"'"));
        return parm.getValue("CHN_DESC",0);
    }
    /**
    * 得到集合医嘱细项价格
    * @param parm TParm
    * @param type String
    * @param exDeptCode String
    * @return TParm
    */
   public double getOrderSetList(String orderCode){
       double ownPrice=0.0;
       TParm result = new TParm();
       result = new TParm(this.select("SELECT OWN_PRICE "+
           " FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDERSET_CODE='"+orderCode+"' AND A.ORDER_CODE=B.ORDER_CODE"));
       int rowCount = result.getCount("OWN_PRICE");
//       System.out.println("个数:"+rowCount);
//       System.out.println("返回值:"+result);
       for(int i=0;i<rowCount;i++){
           ownPrice+=result.getDouble("OWN_PRICE",i);
       }
       return ownPrice;
   }
   /**
     * 根據給入orderCat1Code得到deal_system
     * @param orderCat1Code
     * @return
     */
    public  String getDealSystem(String orderCat1Code){
//        System.out.println("SQL"+"SELECT * FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE='"+orderCat1Code+"'");
        TParm result=new TParm(this.select("SELECT * FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE='"+orderCat1Code+"'"));
        return result.getValue("DEAL_SYSTEM",0);
    }
    /**
    * 得到折扣比率
    * @param ctzCode String
    * @return double
    */
   public double getOwnRate(String ctzCode,String orderCode){
       double ownRate = 1;
       TParm action = new TParm(this.select("SELECT CHARGE_HOSP_CODE FROM SYS_FEE WHERE ORDER_CODE='"+orderCode+"'"));
       TParm actionParm = new TParm(this.select("SELECT DISCOUNT_RATE FROM SYS_CHARGE_DETAIL WHERE CTZ_CODE='"+ctzCode+"' AND CHARGE_HOSP_CODE='"+action.getValue("CHARGE_HOSP_CODE",0)+"'"));
       if(actionParm.getDouble("DISCOUNT_RATE",0)==0)
           return ownRate;
       else
           ownRate = actionParm.getDouble("DISCOUNT_RATE",0);
       return ownRate;
   }
   /**
    * 查询挂号科室
    * @param caseNo String
    * @return TParm
    */
   public TParm getRegPatAdmDate(String caseNo){
       TParm result = new TParm(this.select("SELECT * FROM REG_PATADM WHERE CASE_NO='"+caseNo+"'"));
       return result.getRow(0);
   }
    /**
     * 生成SQL
     * @param parm TParm
     * @return String[]
     */
    public String createSql(TParm parm){
        //查询检体名称
        String optItemDesc = queryOptItem(parm.getValue("OPTITEM_CODE"));
        //得到诊断类别
        TParm icdParm = new TParm();
        if("O".equals(parm.getValue("ADM_TYPE"))||"E".equals(parm.getValue("ADM_TYPE"))){
            icdParm = new TParm(this.select("SELECT ICD_TYPE,ICD_CODE FROM OPD_DIAGREC WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND MAIN_DIAG_FLG = 'Y'"));
        }
        String mainDiag = "";
        String icdType = "";
        if(icdParm.getCount()>0){
            mainDiag = icdParm.getValue("ICD_CODE",0);
            icdType = icdParm.getValue("ICD_TYPE",0);
        }
        //得到自费总价格
        double ownAmt = getOrderSetList(parm.getValue("ORDER_CODE"));
        //开立时间
        String startDttm = StringTool.getString(parm.getTimestamp("ORDER_DATE"),"yyyyMMddHHmmss");
        //接口厂商名称
        String dealSystem=getDealSystem(parm.getValue("ORDER_CAT1_CODE"));
//        System.out.println("接口厂商名称"+dealSystem);
        TParm pat = getRegPatAdmDate(parm.getValue("CASE_NO"));
        String regCr = pat.getValue("CLINICAREA_CODE");
        String regCno = pat.getValue("CLINICROOM_NO");
        //应付金额
        double arAmt = ownAmt*getOwnRate(parm.getValue("CTZ1_CODE"),parm.getValue("ORDER_CODE"));
        TParm patParm = new TParm(this.select("SELECT * FROM SYS_PATINFO WHERE MR_NO='"+parm.getValue("MR_NO")+"'"));
        String patName = "";
        String patName1 = "";
        String birthDate = "";
        String sexCode = "";
        String postCode = "";
        String address = "";
        String companyCode = "";
        String tel = "";
        String idNo = "";
//        System.out.println("patPamr==基本信息"+patParm);
        patName = patParm.getValue("PAT_NAME", 0);
        patName1 = patParm.getValue("PAT_NAME1", 0);
        birthDate = StringTool.getString(patParm.getTimestamp("BIRTH_DATE", 0),"yyyyMMddHHmmss");
        sexCode = patParm.getValue("SEX_CODE", 0);
        postCode = patParm.getValue("POST_CODE", 0);
        address = patParm.getValue("ADDRESS", 0);
        companyCode = patParm.getValue("COMPANY_DESC", 0);
        tel = patParm.getValue("TEL_HOME", 0);
        idNo = patParm.getValue("IDNO", 0);
        String sql = "INSERT INTO MED_APPLY"+
                        " (CAT1_TYPE, APPLICATION_NO, CASE_NO, IPD_NO, MR_NO,"+
                        "  ADM_TYPE, PAT_NAME, PAT_NAME1, BIRTH_DATE, SEX_CODE,"+
                        "  POST_CODE, ADDRESS,COMPANY,TEL,IDNO,"+
                        "  DEPT_CODE, REGION_CODE, CLINICROOM_NO, STATION_CODE, BED_NO,"+
                        " ORDER_NO,SEQ_NO,ORDER_CODE,ORDER_DESC, ORDER_DR_CODE,"+
                        " ORDER_DATE, ORDER_DEPT_CODE, START_DTTM, EXEC_DEPT_CODE,EXEC_DR_CODE,"+
                        " OPTITEM_CODE, OPTITEM_CHN_DESC, ORDER_CAT1_CODE, DEAL_SYSTEM, RPTTYPE_CODE,"+
                        " DEV_CODE, REMARK, ICD_TYPE, ICD_CODE, STATUS,"+
                        " XML_DATE, NEW_READ_FLG, DC_DR_CODE, DC_ORDER_DATE, DC_DEPT_CODE,"+
                        " DC_READ_FLG,REJECTRSN_CODE, RESERVED_DATE, REGISTER_DATE, INSPECT_DATE,"+
                        " INSPECT_TOTTIME,WAIT_TOTTIME, REPORT_DR, REPORT_DATE, EXAMINE_DR,"+
                        " EXAMINE_DATE,DIAGNOSIS_QUALITY,TECHNOLOGY_QUALITY, SERVICE_QUALITY, SEND_FLG,"+
                        " BILL_FLG,OWN_AMT, AR_AMT, OPT_USER, OPT_DATE,"+
                        " OPT_TERM,ORDER_ENG_DESC,URGENT_FLG,CLINICAREA_CODE)"+
                        " VALUES"+
                        "('"+parm.getValue("CAT1_TYPE")+"', '"+parm.getValue("MED_APPLY_NO")+"', '"+parm.getValue("CASE_NO")+"', '"+parm.getValue("IPD_NO")+"', '"+parm.getValue("MR_NO")+"',"+
                        " '"+parm.getValue("ADM_TYPE")+"', '"+patName+"', '"+patName1+"', TO_DATE('"+birthDate+"','YYYYMMDDHH24MISS'), '"+sexCode+"',"+
                        " '"+postCode+"', '"+address+"', '"+companyCode+"', '"+tel+"', '"+idNo+"',"+
                        " '"+parm.getValue("DEPT_CODE")+"', '"+parm.getValue("REGION_CODE")+"', '"+regCno+"', '"+parm.getValue("STATION_CODE")+"', '"+parm.getValue("BED_NO")+"',"+
                        " '"+parm.getValue("RX_NO")+"', '"+parm.getValue("SEQ_NO")+"', '"+parm.getValue("ORDER_CODE")+"', '"+parm.getValue("ORDER_DESC")+"', '"+parm.getValue("DR_CODE")+"',"+
                        " SYSDATE, '"+parm.getValue("DEPT_CODE")+"',TO_DATE('"+startDttm+"','YYYYMMDDHH24MISS'), '"+parm.getValue("EXEC_DEPT_CODE")+"', '"+parm.getValue("EXEC_DR_CODE")+"',"+
                        " '"+parm.getValue("OPTITEM_CODE")+"', '"+optItemDesc+"', '"+parm.getValue("ORDER_CAT1_CODE")+"', '"+dealSystem+"', '"+parm.getValue("RPTTYPE_CODE")+"',"+
                        " '"+parm.getValue("DEV_CODE")+"', '"+parm.getValue("REMARK")+"', '"+icdType+"', '"+mainDiag+"', '0',"+
                        " '', 'N', '', '', '',"+
                        " 'N', '', '', '', '',"+
                        " '', '', '', '', '',"+
                        " '', '', '', '', '0',"+
                        " '"+parm.getValue("BILL_FLG")+"', '"+StringTool.round(ownAmt,2)+"', '"+StringTool.round(arAmt,2)+"', '"+parm.getValue("OPT_USER")+"', SYSDATE,"+
                        " '"+parm.getValue("OPT_TERM")+"','"+parm.getValue("TRADE_ENG_DESC")+"','"+parm.getValue("URGENT_FLG")+"','"+regCr+"')";
                    return sql;

    }
//    /**
//     * 健康检查使用：删除检验数据
//     * @param parm
//     * @param conn
//     * @return
//     * ========pangben 2013-3-10 根据就诊号码删除
//     */
//    public TParm deleteMedApply(TParm parm ,TConnection conn){
//    	String sql="DELETE FROM MED_APPLY WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'";
//    	TParm result = new TParm(this.update(sql,conn));
//    	return result;
//    }
//    
//    /**
//     * 健康检查使用：使检验数据失效
//     * @param parm
//     * @param conn
//     * @return
//     * ========wanglong 2013-3-14 根据就诊号码使失效
//     */
//    public TParm disableMedApply(TParm parm ,TConnection conn){
//        String sql="UPDATE MED_APPLY SET STATUS = 9,SEND_FLG = '1' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'";
//        TParm result = new TParm(this.update(sql,conn));
//        return result;
//    }
}
