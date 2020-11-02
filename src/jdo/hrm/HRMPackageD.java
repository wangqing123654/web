package jdo.hrm;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
*
* <p>Title: ��������ײ�ϸ�����</p>
*
* <p>Description: ��������ײ�ϸ�����</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis</p>
*
* @author ehui 20090922
* @version 1.0
*/
public class HRMPackageD extends TDataStore{
	//ȡ��ҩ��
	public static final String GET_ORDER_DESC="SELECT ORDER_DESC||' ('||ALIAS_DESC||') '||SPECIFICATION ORDER_NAME FROM SYS_FEE WHERE ORDER_CODE='#'";
	//ȡ���ײ���
	private static final String GET_PACKAGE_DESC="SELECT PACKAGE_DESC FROM HRM_PACKAGEM WHERE PACKAGE_CODE='#'";
	//ȡ���ײ������Ӧ��sys_fee����
	//private static final String GET_SYS_FEE_BY_PACK="SELECT B.*,A.DISPENSE_QTY,A.DISPENSE_UNIT,A.ORIGINAL_PRICE,A.PACKAGE_PRICE,A.EXEC_DEPT_CODE HRM_EXE_DEPT,A.DEPT_ATTRIBUTE DEPT_ATTRIBUTE,A.HIDE_FLG FROM HRM_PACKAGED A,SYS_FEE B WHERE A.PACKAGE_CODE='#' AND A.ORDER_CODE=B.ORDER_CODE ORDER BY A.SEQ";
	private static final String GET_SYS_FEE_BY_PACK="SELECT B.*,A.DISPENSE_QTY,A.DISPENSE_UNIT,A.ORIGINAL_PRICE,A.PACKAGE_PRICE,A.EXEC_DEPT_CODE HRM_EXE_DEPT,A.DEPT_ATTRIBUTE DEPT_ATTRIBUTE,A.HIDE_FLG FROM HRM_PACKAGED A,SYS_FEE B WHERE A.PACKAGE_CODE='#' AND A.ORDER_CODE=B.ORDER_CODE ORDER BY A.ORDERSET_CODE, A.SETMAIN_FLG DESC";//������˳���ܱ䣡 modify by wanglong 20130107

    private static final String GET_SYS_FEE_HRMODDER = // ====add by yuanxm
            "SELECT B.*,A.DISPENSE_QTY,A.DISPENSE_UNIT, A.OWN_PRICE, A.OWN_PRICE PACKAGE_PRICE,A.EXEC_DEPT_CODE HRM_EXE_DEPT,A.DEPT_ATTRIBUTE DEPT_ATTRIBUTE,A.HIDE_FLG HIDE_FLG,A.NEW_FLG NEW_FLG,A.DISCOUNT_RATE DISCOUNT_RATE,A.MED_APPLY_NO MED_APPLY_NO,A.DEPT_CODE "
                    + "FROM HRM_ORDER A,SYS_FEE B "
                    + "WHERE A.CASE_NO='#' AND A.ORDER_CODE=B.ORDER_CODE ORDER BY A.SEQ_NO ";
	
    private static final String GET_SYS_FEE_FROM_HRMORDER =//add by wanglong 20130508
            "SELECT B.*,A.DISPENSE_QTY,A.DISPENSE_UNIT,A.OWN_PRICE,A.OWN_PRICE PACKAGE_PRICE,A.EXEC_DEPT_CODE HRM_EXE_DEPT,A.DEPT_ATTRIBUTE,A.HIDE_FLG,A.NEW_FLG,A.DISCOUNT_RATE,A.MED_APPLY_NO,A.DEPT_CODE "
                    + " FROM HRM_ORDER A, SYS_FEE B "
                    + "WHERE A.ORDER_CODE = B.ORDER_CODE "
                    + "  AND A.CAT1_TYPE <> 'PHA' "
                    + "  AND A.CONTRACT_CODE='#' AND A.MR_NO='#' ORDER BY A.SEQ_NO ";
    
	//ȡ���ײ��кϼ��ܼ�
	private static final String GET_PACK_AMT="SELECT SUM(TO_NUMBER(DISPENSE_QTY*PACKAGE_PRICE)) AMT FROM HRM_PACKAGED WHERE PACKAGE_CODE='#'";
	//��ѯ�Ƿ�����ʹ�õ��ײ�
    // private static final String IS_PACK_IN_USE="SELECT COUNT(*) COUNT_USE FROM HRM_CONTRACTD WHERE PACKAGE_CODE='#' AND RECEIPT_NO IS NULL";

	/**
	 * �õ�����������
	 * @param parm TParm
	 * @param row int
	 * @param column String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
        // ����ҽ����Ǽ���ҽ��(Y:����ҽ��)
        String orderCat1Code = parm.getValue("ORDER_CAT1_CODE", row);
        String orderSetCode = parm.getValue("ORDERSET_CODE", row);
        int goupNo = parm.getInt("ORDERSET_GROUP_NO", row);
        String orderCode = parm.getValue("ORDER_CODE", row);
        String packCode = parm.getValue("PACKAGE_CODE", row);
        // ���ҽ������Ϊ��˵����������������հ�һ�У�ֻ�����ײ����ƣ������Ĳ���Ҫ��
        if (orderCode == null || orderCode.equals("")) {
            if ("PACKAGE_DESC".equalsIgnoreCase(column)) {
                TParm result = new TParm(TJDODBTool.getInstance().select(GET_PACKAGE_DESC.replace("#", packCode)));
                return result.getValue("PACKAGE_DESC", 0);
            }
        } else {
            if ("PACKAGE_DESC".equalsIgnoreCase(column)) {
                TParm result = new TParm(TJDODBTool.getInstance().select(GET_PACKAGE_DESC.replace("#", packCode)));
                return result.getValue("PACKAGE_DESC", 0);
            } else if ("ORIGINAL_PRICE_MAIN".equalsIgnoreCase(column)) {
                double totAmt = 0.0;
                String filterString = this.getFilter();
                String filter = "";
                // caowl 20130226 ���ۺ��ײͼ۴�������
                if ("PHA_W".equals(orderCat1Code) || "MAT".equals(orderCat1Code)) {
                    // System.out.println("�ߵ���if");
                    filter =
                            "PACKAGE_CODE='" + packCode + "' AND ORDER_CODE='" + parm.getValue("ORDER_CODE", row) + "'  AND SETMAIN_FLG='Y' ";
                    this.setFilter(filter);
                    if (!this.filter()) {
                        return totAmt;
                    }
                    int count = this.rowCount();
                    // this.showDebug();
                    for (int i = 0; i < count; i++) {
                        totAmt = totAmt + StringTool.round(this.getItemDouble(i, "ORIGINAL_PRICE"), 2);
                    }
                    if ("PHA_W".equals(orderCat1Code)) {
                        this.setItem(row, "MEDI_QTY", parm.getValue("DISPENSE_QTY", row));
                    }
                    this.setFilter(filterString);
                    this.filter();
                    return totAmt;
                } else {
                    // System.out.println("�ߵ���else");
                    filter =
                            "PACKAGE_CODE='" + packCode + "' AND ORDERSET_CODE='" + orderSetCode + "' AND ORDERSET_GROUP_NO=" + goupNo;
                    this.setFilter(filter);
                    if (!this.filter()) {
                        return totAmt;
                    }
                    int count = this.rowCount();
                    // this.showDebug();
                    for (int i = 0; i < count; i++) {
                        totAmt = totAmt + StringTool.round(this.getItemDouble(i, "DISPENSE_QTY") * this.getItemDouble(i, "ORIGINAL_PRICE"), 2);
                    }
                    if ("PHA_W".equals(orderCat1Code)) {
                        this.setItem(row, "MEDI_QTY", parm.getValue("DISPENSE_QTY", row));
                    }
                    this.setFilter(filterString);
                    this.filter();
                    return totAmt;
                }
            } else if ("PACKAGE_PRICE_MAIN".equalsIgnoreCase(column)) {
                double totAmt = 0.0;
                String filterString = this.getFilter();
                String filter = "";
                if ("PHA_W".equals(orderCat1Code) || "MAT".equals(orderCat1Code)) {
                    filter =
                            "PACKAGE_CODE='" + packCode + "' AND ORDER_CODE='" + parm.getValue("ORDER_CODE", row) + "' AND SETMAIN_FLG='Y' ";
                    this.setFilter(filter);
                    if (!this.filter()) {
                        // System.out.println("PACKAGE_PRICE_MAIN.filter is wrong");
                        return totAmt;
                    }
                    int count = this.rowCount();
                    for (int i = 0; i < count; i++) {
                        totAmt = totAmt + StringTool.round(this.getItemDouble(i, "PACKAGE_PRICE"), 2);
                    }
                    this.setFilter(filterString);
                    this.filter();
                    // System.out.println("PACKAGE_PRICE_MAIN.totAmt="+totAmt);
                    return totAmt;
                } else {
                    filter =
                            "PACKAGE_CODE='" + packCode + "' AND ORDERSET_CODE='" + orderSetCode + "' AND ORDERSET_GROUP_NO=" + goupNo;
                    this.setFilter(filter);
                    if (!this.filter()) {
                        // System.out.println("PACKAGE_PRICE_MAIN.filter is wrong");
                        return totAmt;
                    }
                    int count = this.rowCount();
                    for (int i = 0; i < count; i++) {
                        totAmt =
                                totAmt + StringTool.round(this.getItemDouble(i, "DISPENSE_QTY") * this.getItemDouble(i, "PACKAGE_PRICE"), 2);
                    }
                    this.setFilter(filterString);
                    this.filter();
                    // System.out.println("PACKAGE_PRICE_MAIN.totAmt="+totAmt);
                    return totAmt;
                }
                // caowl 20130226 end
            } else if ("AR_AMT".equalsIgnoreCase(column)) {
                double packPrice = parm.getDouble("PACKAGE_PRICE", row);
                double qty = parm.getDouble("DISPENSE_QTY", row);
                return StringTool.round(qty * packPrice, 2);
            }
        }
        return "";
    }
	
    /**
     * ��������������
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setOtherColumnValue(TParm parm, int row, String column, Object value) {
        if ("PACKAGE_PRICE_MAIN".equalsIgnoreCase(column)) {
            this.setItem(row, "PACKAGE_PRICE", value);
            this.setActive(row, true);
        }
        return true;
    }
    
	/**
	 * ����������
	 * @param packCode
	 * @return int
	 */
    public int getMaxSeq(String packCode) {
        int seq = -1;
        if (StringUtil.isNullString(packCode)) {
            return seq;
        }
        this.setFilter("PACKAGE_CODE='" + packCode + "'");
        this.filter();
        for (int i = 0; i < this.rowCount(); i++) {
            Long temp = TypeTool.getLong(this.getItemData(i, "SEQ"));
            int tempInt = temp.intValue();
            if (tempInt > seq) seq = tempInt;
        }
        return seq;
    }
	
	/**
	 * ���ݸ����ײʹ����ѯ�����е�sys_fee������
	 * @param packCode
	 * @return
	 */
    public static TParm getSysFeeByPack(String packCode) {
        TParm result = new TParm();
        if (StringUtil.isNullString(packCode)) {
            return result;
        }
        String sql = GET_SYS_FEE_BY_PACK.replace("#", packCode);
        // System.out.println("getSysFeeByPack.sql======="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
	
	/**
	 * ���ݸ����ײʹ����ѯ�����е�sys_fee������
	 * @param packCode
	 * @return
	 */
    public static TParm getSysFeeByHrmOrder(String caseNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            return result;
        }
        String sql = GET_SYS_FEE_HRMODDER.replace("#", caseNo);
        // System.out.println("getSysFeeByHrmOrder.sql======="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
	
    /**
     * ���ݸ����ͬ�Ͳ����Ų�ѯ����ҽ����Ӧ������sys_fee������
     * @param contractCode
     * @param mrNo
     * @return
     */
    public static TParm getSysFeeByHrmOrder(String contractCode, String mrNo) {// add by wanglong 20130508
        TParm result = new TParm();
        if (StringUtil.isNullString(contractCode) || StringUtil.isNullString(mrNo)) {
            return result;
        }
        String sql = GET_SYS_FEE_FROM_HRMORDER.replaceFirst("#", contractCode);
        sql = sql.replaceFirst("#", mrNo);
        // System.out.println("getSysFeeByHrmOrder.sql=======" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
	
	/**
	 * ȡ��ģ�����ܼ�
	 * @param packCode
	 * @return
	 */
    public static double getPackageAmt(String packCode) {
        double amt = 0.0;
        if (StringUtil.isNullString(packCode)) {
            // System.out.println("packCode is null");
            return amt;
        }
        String sql = GET_PACK_AMT.replace("#", packCode);
        // System.out.println("getPackageAmt.sql======"+sql);
        amt = new TParm(TJDODBTool.getInstance().select(sql)).getDouble("AMT", 0);
        return amt;
    }
	
	/**
	 * ȡ�ö������ܼ�
	 * @param packCode
	 * @return
	 */
    public double getPackgeTotAmt() {
        double amt = 0.0;
        String filterString = this.getFilter();
        String packCode = this.getItemData(0, "PACKAGE_CODE") + "";
        this.setFilter("PACKAGE_CODE='" + packCode + "'");
        this.filter();
        // this.showDebug();
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            amt = amt + StringTool.round(this.getItemDouble(i, "ORIGINAL_PRICE") * this.getItemDouble(i, "DISPENSE_QTY"), 2);
        }
        this.setFilter(filterString);
        this.filter();
        return amt;
    }
	
	/**
	 * �ײͼ�
	 * @return
	 */
    public double getPackgeArAmt() {
        double amt = 0.0;
        String filterString = this.getFilter();
        String packCode = this.getItemData(0, "PACKAGE_CODE") + "";
        this.setFilter("PACKAGE_CODE='" + packCode + "'");
        this.filter();
        // this.showDebug();
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            amt = amt + StringTool.round(this.getItemDouble(i, "PACKAGE_PRICE") * this.getItemDouble(i, "DISPENSE_QTY"), 2);
        }
        this.setFilter(filterString);
        this.filter();
        return amt;
    }
	
	/**
	 * ȡ�ö���������ҽ��������
	 * @return
	 */
    public int getMaxGroupNo() {
        int groupNo = -1;
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            if (this.getItemInt(i, "ORDERSET_GROUP_NO") > groupNo) {
                groupNo = this.getItemInt(i, "ORDERSET_GROUP_NO");
            }
        }
        groupNo = groupNo + 1;
        return groupNo;
    }
    
	/**
	 * ���ݸ���DEPT_ATTRIBUTE�޸�
	 * @param deptAttribute
	 * @return
	 */
    public boolean updateDeptAttribute(String deptAttribute, String packCode, String orderCode,
                                       int groupNo) {
        if (StringUtil.isNullString(deptAttribute) || StringUtil.isNullString(packCode)
                || StringUtil.isNullString(orderCode)) {
            return false;
        }
        String filterString = this.getFilter();
        this.setFilter("PACKAGE_CODE='" + packCode + "' AND ORDER_CODE='" + orderCode
                + "' AND ORDERSET_GROUP_NO=" + groupNo);
        this.filter();
        int count = this.rowCount();
        for (int i = 0; i < count; i++) {
            this.setItem(i, "DEPT_ATTRIBUTE", deptAttribute);
        }
        this.setFilter(filterString);
        this.filter();
        return true;
    }

    public boolean updateOrder(String packCode, String orderCode) {
        return true;
    }
}
