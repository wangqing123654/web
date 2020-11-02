package jdo.hrm;

import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: ��������ײ��趨Tool</p>
*
* <p>Description: ��������ײ��趨Tool</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis</p>
*
* @author ehui 20090922
* @version 1.0
*/
public class HRMFeePackTool  extends TJDOTool{
	//��ʼ�������趨������ʼ��SQL
	public static final String INIT_PACK_MAIN="SELECT * FROM HRM_PACKAGEM";
	//��ʼ�������趨ϸ����ʼ��SQL
	public static final String INIT_PACK_DETAIL="SELECT * FROM HRM_PACKAGED WHERE PACKAGE_CODE ='#' ORDER BY SEQ";
	//ȡ�ü���ҽ��ϸ��

	public static final String QUERY_ORDERSET=
		"SELECT B.ORDER_CODE,A.ORDER_DESC,A.OWN_PRICE  OWN_PRICE,A.OWN_PRICE*B.DOSAGE_QTY TOT_OWN_PRICE," +
		"  B.DOSAGE_QTY,A.UNIT_CODE,A.OPTITEM_CODE,B.ORDERSET_CODE,A.GOODS_DESC," +
        "  A.SPECIFICATION,A.NHI_PRICE,A.CAT1_TYPE,A.ORDER_CAT1_CODE " + // modify by wanglong 20130423
		"FROM SYS_FEE A, SYS_ORDERSETDETAIL B  " +
		"WHERE B.ORDERSET_CODE='#' AND B.ORDER_CODE=A.ORDER_CODE";
	 /**
     * ʵ��
     */
    public static HRMFeePackTool instanceObject;
    /**
     * �õ�ʵ��
     * @return HRMFeePackTool
     */
    public static HRMFeePackTool getInstance()
    {
        if(instanceObject == null){
        	instanceObject = new HRMFeePackTool();
        }

        return instanceObject;
    }
    /**
     * ����ȡ��ԭ��ȡ����ģ���
     * @return
     */
    public String getNewPackCode(){
		String packCode = SystemTool.getInstance().getNo("ALL", "ODO", "PACKAGE_NO","PACKAGE_NO");
		return packCode;
    }
}
