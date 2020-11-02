package jdo.odi;

import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.util.*;
import com.javahis.util.*;

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
public class OdiDrugAllergy extends TDataStore{
    private String caseNo;
    private String mrNo;
    private static final String GET_A_SQL="SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='PHA_INGREDIENT'";
    private static final String GET_C_SQL="SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ALLERGYTYPE'";
    public OdiDrugAllergy() {
    }
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
    	//============xueyf modify 20120217 start
    	//TO_CHAR(TO_DATE(ADM_DATE,'YYYYMMDDHH24MISS'),'YYYYMMDD') AS ADM_DATE
        return "SELECT ADM_DATE,DRUG_TYPE,DRUGORINGRD_CODE,ALLERGY_NOTE,DEPT_CODE,DR_CODE,ADM_TYPE,CASE_NO,MR_NO,OPT_USER,OPT_DATE,OPT_TERM FROM OPD_DRUGALLERGY WHERE MR_NO='"+this.getMrNo()+"' ORDER BY ADM_DATE";
      //============xueyf modify 20120217 stop
    }
    /**
    * ��ѯ
    * @return boolean
    */
   public boolean onQuery(){
       if(!setSQL(getQuerySQL()))
           return false;
       if(retrieve()==-1)
           return false;
       return true;
   }
   String code="";
    /**
     * �õ�δ֪������
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm, int row, String column) {
//        System.out.println("getOtherColumnValue");
//        System.out.println("����:"+parm);
//        System.out.println("��:"+row);
//        System.out.println("��:"+column);
        if("ORDER_DESC".equals(column)){
            String drugType = parm.getValue("DRUG_TYPE",row);
//            System.out.println("DRUG_TYPE"+drugType);
            String codeStr = parm.getValue("DRUGORINGRD_CODE",row);
            //�ɷֹ���
            if("A".equals(drugType)){
                TParm parmA  = new TParm(TJDODBTool.getInstance().select(GET_A_SQL+" AND ID='"+codeStr+"'"));
                return parmA.getData("CHN_DESC",0);
            }
            //ҩƷ����
            if("B".equals(drugType)){
                return OdiUtil.getInstance().getSysOrderDesc(codeStr);
            }
            //��������
            if("C".equals(drugType)){
                TParm parmC  = new TParm(TJDODBTool.getInstance().select(GET_C_SQL+" AND ID='"+codeStr+"'"));
                return parmC.getData("CHN_DESC",0);
            }
        }
        //����ʱ��
        if("ADM_DATEFORMAT".equals(column)){
            return StringTool.getTimestamp(parm.getValue("ADM_DATE",row),"yyyyMMdd");
        }
        return null;
    }
//    /**
//     * ���˷���
//     * @param parm TParm
//     * @param row int
//     * @return boolean
//     */
//    public boolean filter(TParm parm, int row) {
//        return parm.getValue("ORDER_CODE", row).equals(this.code);
//    }
    public String getCaseNo() {
        return caseNo;
    }

    public String getMrNo() {
        return mrNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }
}
