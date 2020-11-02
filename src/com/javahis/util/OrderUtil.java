package com.javahis.util;

import jdo.odi.OdiMainTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import java.util.ArrayList;
import jdo.sys.Operator;

/**
 * 
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 * 
 *
 * @author WangM
 * 
 * @version 1.0
 */
public class OrderUtil {
    private static OrderUtil instanceObject;
    /**
     * ȡ��SYS_ORDER_CAT1����
     */
    private static final String GET_DEAL_SYSTEM_SQL =
            "SELECT * FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE='#'";
    public OrderUtil() {
    }

    public static synchronized OrderUtil getInstance() {
        if (instanceObject == null) {
            instanceObject = new OrderUtil();
        }
        return instanceObject;
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * ���֤�մ���
     * @param userId String
     * @param lcsClassCode String
     * @return boolean
     */
    public boolean checkLcsClassCode(String userId, String lcsClassCode) {
        boolean falg = false;
        //�õ�ʹ���ߵ�֤���б�
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '" +
                userId + "' AND TRUNC(SYSDATE) BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
        int rowCount = parm.getCount("LCS_CLASS_CODE");
        for (int i = 0; i < rowCount; i++) {
            if (lcsClassCode.equals(parm.getValue("LCS_CLASS_CODE", i))) {
                return true;
            }
        }
        return falg;
    }
    /**
     * ���֤�մ���
     * @param userId String
     * @param lcsClassCode String
     * @return boolean
     */
    public boolean checkLcsClassCode(TParm parm,String userId, String lcsClassCode){
    	boolean falg = false; 
    	int rowCount = parm.getCount("LCS_CLASS_CODE");
    	for (int i = 0; i < rowCount; i++) {
             if (lcsClassCode.equals(parm.getValue("LCS_CLASS_CODE", i))) {
                 return true;
             }
         }
         return falg;
    }
    /**
     * ���⿹��ҩƷҽ��վ����У��
     * ����������� ���߷����Ƶ�ҽ�� �Ϳ��Դ���
     * @param parm
     * @param userId
     * @param lcsClassCode
     * @return
     */
    public int checkTsLcsClassCode(TParm parm,String userId, String lcsClassCode){
    	//boolean falg = false; 
    	int index=-1;
    	int rowCount = parm.getCount("LCS_CLASS_CODE");//LCS_CLASS_CODE =3 ����  4 ��������
    	for (int i = 0; i < rowCount; i++) {
    		 if (lcsClassCode.equals(parm.getValue("LCS_CLASS_CODE", i))) {
                 return 1;
             }
    		 if (null!=parm.getValue("LCS_CLASS_CODE", i) && 
           		 parm.getValue("LCS_CLASS_CODE", i).equals("3") || 
           		 null!=parm.getValue("LCS_CLASS_CODE", i) &&
           		 	parm.getValue("LCS_CLASS_CODE", i).equals("4") || 
           		 null!=parm.getValue("LCS_CLASS_CODE", i) &&
      		 	parm.getValue("LCS_CLASS_CODE", i).equals("2")) {
    			index = 2;
            }
         }
    	 if (index!=-1) {//û��֤�յĲ����Կ����� ������ ���� ���߷����� ��֤�� ���Կ���������ҪԽ��
			return index;
		 }
         return -1;
    }
    /**
     * ����������
     * @param ctrlDrugClass String
     * @return boolean
     */
    public int checkAntibioticDay(String antibioticNo) {
//      System.out.println("ctrlDrugClass"+antibioticNo);
        int day = -1;
        TParm action = new TParm(this.getDBTool().select(
                "SELECT TAKE_DAYS FROM SYS_ANTIBIOTIC WHERE ANTIBIOTIC_CODE='" +
                antibioticNo + "'"));
//      System.out.println("ctrlDrugClassaction"+action);
        if (action.getInt("TAKE_DAYS", 0) != 0) {
            day = action.getInt("TAKE_DAYS", 0);
        }
        return day;
    }

    /**
     * �ж���ʱƵ��
     * @param freqCode String
     * @return boolean
     */
    public boolean isSTFreq(String freqCode) {
        boolean falg = false;
        TParm action = new TParm(this.getDBTool().select(
                "SELECT STAT_FLG FROM SYS_PHAFREQ WHERE FREQ_CODE = '" +
                freqCode + "'"));
        if (action.getCount() > 0) {
            falg = action.getBoolean("STAT_FLG", 0);
        }
        return falg;
    }

    /**
     * �õ�ת����
     * @param OrderCode String
     * @return TParm
     */
    public TParm getPhaTransUnit(String orderCode) {
        TParm result = new TParm();
        result = new TParm(this.getDBTool().select("SELECT DOSAGE_QTY,DOSAGE_UNIT,MEDI_QTY,MEDI_UNIT FROM PHA_TRANSUNIT WHERE ORDER_CODE='" +
                orderCode + "'"));
//      System.out.println("ת����:"+result);
        return result;
    }

    /**
     * �õ�ҩƷ����
     * @param effDate String
     * @param orderCode String
     * @return double
     */
    public double getOrderOwnPrice(String effDate, String orderCode) {
        double ownPrice = 0.0;
        TParm result = new TParm();
        result = new TParm(this.getDBTool().select(
                "SELECT OWN_PRICE FROM SYS_FEE_HISTORY WHERE ORDER_CODE='" +
                orderCode + "' AND TO_DATE('" + effDate + "','YYYYMMDDHH24MISS') BETWEEN TO_DATE(START_DATE,'YYYYMMDDHH24MISS') AND TO_DATE(END_DATE,'YYYYMMDDHH24MISS')"));
//      System.out.println("�۸񷵻�ֵ:"+result);
        ownPrice = result.getDouble("OWN_PRICE", 0);
        return ownPrice;
    }

    /**
     * �õ�����ҽ��ϸ��۸�
     * @param parm TParm
     * @param type String
     * @param exDeptCode String
     * @return TParm
     */
    public double getOrderSetList(String orderCode) {
        double ownPrice = 0.0;
        TParm result = new TParm();
        result = new TParm(this.getDBTool().select("SELECT OWN_PRICE " +
                " FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDERSET_CODE='" +
                orderCode + "' AND A.ORDER_CODE=B.ORDER_CODE"));
        int rowCount = result.getCount("OWN_PRICE");
//       System.out.println("����:"+rowCount);
//       System.out.println("����ֵ:"+result);
        for (int i = 0; i < rowCount; i++) {
            ownPrice += result.getDouble("OWN_PRICE", i);
        }
        return ownPrice;
    }

    /**
     * �õ��ۿ۱���
     * @param ctzCode String
     * @return double
     */
    public double getOwnRate(String ctzCode, String orderCode) {
        double ownRate = 1;
        TParm action = new TParm(this.getDBTool().select(
                "SELECT CHARGE_HOSP_CODE FROM SYS_FEE WHERE ORDER_CODE='" +
                orderCode + "'"));
        TParm actionParm = new TParm(this.getDBTool().select(
                "SELECT DISCOUNT_RATE FROM SYS_CHARGE_DETAIL WHERE CTZ_CODE='" +
                ctzCode + "' AND CHARGE_HOSP_CODE='" +
                action.getValue("CHARGE_HOSP_CODE", 0) + "'"));
        if (actionParm.getDouble("DISCOUNT_RATE", 0) == 0)
            return ownRate;
        else
            ownRate = actionParm.getDouble("DISCOUNT_RATE", 0);
        return ownRate;
    }

    /**
     * �Ƿ���ҽ������
     * @param caseNo String
     * @return boolean
     */
    public boolean isNhiPat(String caseNo) {
        boolean falg = false;
        TParm result = new TParm();
        result = new TParm(this.getDBTool().select(
                "SELECT CTZ1_CODE FROM ADM_INP WHERE CASE_NO='" + caseNo + "'"));
        String ctzCode = result.getValue("CTZ1_CODE", 0);
        result = new TParm(this.getDBTool().select(
                "SELECT NHI_CTZ_FLG FROM SYS_CTZ WHERE CTZ_CODE='" + ctzCode +
                "' AND MAIN_CTZ_FLG='Y'"));
        if (result.getBoolean("NHI_CTZ_FLG", 0))
            falg = true;
        return falg;
    }

    /**
     * �ж��Ƿ��ǿ�����
     * @param parm TParm
     * @return boolean
     */
    public boolean checkKssPhaQty(TParm parm) {
        //�Ƿ��ǹ���ҩƷ
//    	System.out.println("ANTIBIOTIC_CODE"+parm.getValue("ANTIBIOTIC_CODE"));
        if (parm.getValue("ANTIBIOTIC_CODE").length() != 0) {
            double mediQty = getPhaTransUnit(parm.
                                             getValue("ORDER_CODE")).getDouble(
                    "MEDI_QTY", 0);
//            System.out.println("�õ�ת��������" + mediQty);
            double onlyMediQty = parm.getDouble("MEDI_QTY");
//            System.out.println("��ǰ��ֵ:" + onlyMediQty);
            
            if (onlyMediQty > mediQty) {
                    //if (messageBox("��ʾ��Ϣ", "�ѳ����˿�������ҩƷ������,�Ƿ��������?", this.YES_NO_OPTION) != 0)
                return false;
            }
        }
        return true;
    }

    /**
     * �жϸ���ҽ���Ŀ����صȼ���Ӧ��Ĭ�����Ƿ�<=�������
     * @param parm TParm
     * @return boolean true:���Լ�������,false:����Ĭ����
     */
    public static boolean checkKssPhaQty(String ctrlDrugCode, String orderCode,
                                         double mediQty) {
        if (StringUtil.isNullString(ctrlDrugCode)) {
            return true;
        }
        double sysQty = OrderUtil.getInstance().getPhaTransUnit(orderCode).
                        getDouble("MEDI_QTY", 0);
//        System.out.println("�õ�ת��������" + mediQty);
//        System.out.println("��ǰ��ֵ:" + sysQty);
        //�Ƿ��ǹ���ҩƷ
        return sysQty <= mediQty;
    }

    /**
     * ���ظ����ORDER_CODE�Ŀ����ش���
     * @param orderCode
     * @return
     */
    public String getAntiBioCode(String orderCode) {
        String antiBio = "";
        if (StringUtil.isNullString(orderCode))
            return antiBio;
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = OdiMainTool.getInstance().queryPhaBase(parm);
//    	System.out.println("RESULT=============="+result);
        return result.getValue("ANTIBIOTIC_CODE", 0);
    }

    /**
     * ���������б�
     * @param ds TDataStore
     * @param type String
     * @return TParm
     */
    public TParm getOrderPasEMR(TDataStore ds, String type) {
//        System.out.println("���������б�");
//        ds.showDebug();
        TParm result = new TParm();
        String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
        int rowCount = 0;
        if ("ODI".equals(type)) {
            int newRow[] = ds.getNewRows(buff);
            for (int i : newRow) {
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
                if (temp.getValue("ORDERSET_CODE").length() != 0 &&
                    !temp.getBoolean("SETMAIN_FLG"))
                    continue;
                if (!temp.getValue("CAT1_TYPE").equals("RIS")&&
                		!temp.getValue("CAT1_TYPE").equals("LIS"))//====20140731  yanjing ���LIS
                    continue;
                result.addData("ORDER_DATE", temp.getData("EFF_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                rowCount++;
            }
        }
        if ("ODO".equals(type)) {
            int newRow[] = ds.getNewRows(buff);
            for (int i : newRow) {
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
                if (temp.getValue("ORDERSET_CODE").length() != 0 &&
                    !temp.getBoolean("SETMAIN_FLG"))
                    continue;
                result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                rowCount++;
            }
        }
        if ("HRM".equals(type)) {
            int newRow[] = ds.getNewRows(buff);
            for (int i : newRow) {
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
                if (temp.getValue("ORDERSET_CODE").length() != 0 &&
                    !temp.getBoolean("SETMAIN_FLG"))
                    continue;
                result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                rowCount++;
            }
        }
        if ("ODI_PHA".equals(type)) {//====pangben 2013-7-30 ҩƷ����ҩ�������ݣ��������뵥��סԺҽ��վ�������
        	 int newRow[] = ds.getNewRows(buff);
             for (int i : newRow) {
                 if (!ds.isActive(i, buff))
                      continue;
                      TParm temp = ds.getRowParm(i, buff);
                      String linkNo = temp.getData("LINK_NO").toString();
                    if(temp.getData("LINKMAIN_FLG").toString().equals("Y")){//���������forѭ��У�����е�����д��pha_anti��
                	boolean antiFlg = false;//false:�������Ӧ��ϸ���в����п���ҩ��
                	for(int j : newRow){ 
                		for(int m : newRow){//ѭ��У��ϸ���Ƿ��п���ҩ��(�����������ͬ������)
                			TParm meTemp = ds.getRowParm(m, buff);
                			String mLinkNo = meTemp.getData("LINK_NO").toString();
                			if(linkNo.equals(mLinkNo)&&(!"".equals(meTemp.getData("ANTIBIOTIC_CODE").toString())
                					&&!meTemp.getData("ANTIBIOTIC_CODE").toString().equals(null))){
                				antiFlg = true;//ϸ���к��п���ҩ����
                			}               			
                		}
                		if(antiFlg){//ϸ���к��п���ҩ���ǣ��������ϸ���д��pha_anti��
                			TParm reTemp = ds.getRowParm(j, buff);
                    		String LlinkNo = reTemp.getData("LINK_NO").toString();
                    		 String dcDate = reTemp.getValue("DC_DATE");
                    		 String effDate = reTemp.getValue("EFF_DATE");
//                    		System.out.println("000+++9999 is AA is :"+dcDate);
                    		if(LlinkNo.equals(linkNo)){
                    			long takeDays = 0;
                    			
                    			String mrCode = "";
                    			String antiCode = "";
                    			TParm orderParm = new TParm(this.getDBTool().select(
                                        "SELECT A.ANTIBIOTIC_CODE,A.ORDER_CODE,B.MR_CODE,A.SPECIFICATION,B.TAKE_DAYS FROM PHA_BASE A,SYS_ANTIBIOTIC B " +
                                        "WHERE A.ANTIBIOTIC_CODE=B.ANTIBIOTIC_CODE AND A.ORDER_CODE='" +
                                        reTemp.getData("ORDER_CODE") + "' AND A.ANTIBIOTIC_CODE IS NOT NULL "));
                                if (orderParm.getValue("MR_CODE",0).length() != 0){
                                	mrCode = orderParm.getValue("MR_CODE", 0);
                                }
                                if("".equals(dcDate)||dcDate.equals(null)){
                                	takeDays = orderParm.getInt("TAKE_DAYS",0);
                    			}else{
                    				takeDays = this.diffDays(dcDate, effDate);
                    			}
                    			
                                //��ѯҩ���Ƿ�Ϊ�����࿹��ҩ�20131023 yanjing
                                if(orderParm.getCount()>0){
                                	antiCode = orderParm.getValue("ANTIBIOTIC_CODE", 0);
                                }
                    			result.addData("TAKE_DAYS", takeDays);
                    			result.addData("ANTIBIOTIC_CODE", antiCode);//�����ش��룬�ǿ���ҩ��Ϊ��
                                result.addData("ORDER_DATE", reTemp.getData("ORDER_DATE"));
                                result.addData("LINKMAIN_FLG", reTemp.getData("LINKMAIN_FLG"));
                                result.addData("LINK_NO", reTemp.getData("LINK_NO"));
                                result.addData("ORDER_CODE", reTemp.getData("ORDER_CODE"));
                                result.addData("ORDER_DESC", reTemp.getData("ORDER_DESC"));
                                result.addData("MR_CODE", mrCode);
                                result.addData("OPTITEM_CODE", reTemp.getData("OPTITEM_CODE"));
                                result.addData("REQUEST_NO", reTemp.getData("REQUEST_NO"));
                                result.addData("SPECIFICATION", reTemp.getData("SPECIFICATION"));//���
                                result.addData("MEDI_UNIT", reTemp.getData("MEDI_UNIT"));//��ҩ��λ
                                result.addData("MEDI_QTY", reTemp.getData("MEDI_QTY"));//��ҩ��
                                result.addData("FREQ_CODE", reTemp.getData("FREQ_CODE"));//Ƶ��
                                result.addData("ROUTE_CODE", reTemp.getData("ROUTE_CODE"));//�÷�
                                result.addData("DC_DATE", reTemp.getData("DC_DATE"));//ͣ��ʱ��
                                result.addData("RX_KIND", reTemp.getData("RX_KIND"));//��ʱ  ���� ���
                                result.addData("INFLUTION_RATE", reTemp.getData("INFLUTION_RATE"));//����
                                rowCount++;
                    		}
                		}
                	}
                }else if((linkNo.equals(null)||"".equals(linkNo))){//���������Ŀ���ҩ��д��pha_anti����
                	TParm orderParm = new TParm(this.getDBTool().select(
                            "SELECT A.ANTIBIOTIC_CODE,A.ORDER_CODE,B.MR_CODE,A.SPECIFICATION,B.TAKE_DAYS FROM PHA_BASE A,SYS_ANTIBIOTIC B " +
                            "WHERE A.ANTIBIOTIC_CODE=B.ANTIBIOTIC_CODE AND A.ORDER_CODE='" +
                            temp.getData("ORDER_CODE") + "' AND A.ANTIBIOTIC_CODE IS NOT NULL "));
                    if (orderParm.getCount() <= 0)
                        continue;
                    if (orderParm.getValue("MR_CODE",0).length() == 0)
                        continue;
                    //��ѯҩ���Ƿ�Ϊ�����࿹��ҩ�20131023 yanjing
                    String dcDate = temp.getValue("DC_DATE");
           		    String effDate = temp.getValue("EFF_DATE");
           		    String antiCode = "";
           		    long takeDays = 0;
                    if("".equals(dcDate)||dcDate.equals(null)){
                    	if(orderParm.getCount()>0){
                        	takeDays = orderParm.getInt("TAKE_DAYS",0);
                        	antiCode = orderParm.getValue("ANTIBIOTIC_CODE", 0);
                        }
                    }else{
                    	   takeDays = this.diffDays(dcDate, effDate);
                         if(orderParm.getCount()>0){
//                         	takeDays = orderParm.getInt("TAKE_DAYS",0);
                         	antiCode = orderParm.getValue("ANTIBIOTIC_CODE", 0);
                         }
                    }
                    result.addData("ANTI_FLG", "Y");//����ҩ����
                    result.addData("ANTIBIOTIC_CODE", antiCode);//�����ش��룬�ǿ���ҩ��Ϊ��
                    result.addData("TAKE_DAYS", takeDays);
                    result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                    result.addData("LINKMAIN_FLG", temp.getData("LINKMAIN_FLG"));
                    result.addData("LINK_NO", temp.getData("LINK_NO"));
                    result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                    result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                    result.addData("MR_CODE", orderParm.getData("MR_CODE",0));
                    result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                    result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                    result.addData("SPECIFICATION", orderParm.getData("SPECIFICATION",0));//���
                    result.addData("MEDI_UNIT", temp.getData("MEDI_UNIT"));//��ҩ��λ
                    result.addData("MEDI_QTY", temp.getData("MEDI_QTY"));//��ҩ��
                    result.addData("FREQ_CODE", temp.getData("FREQ_CODE"));//Ƶ��
                    result.addData("ROUTE_CODE", temp.getData("ROUTE_CODE"));//�÷�
                    result.addData("DC_DATE", temp.getData("DC_DATE"));//ͣ��ʱ��
                    result.addData("RX_KIND", temp.getData("RX_KIND"));//��ʱ  ���� ���
                    result.addData("INFLUTION_RATE", temp.getData("INFLUTION_RATE"));//����
                    rowCount++;
                	
                }
                }
		}
        result.setData("ACTION", "COUNT", rowCount);
        return result;
    }
    /**
     * �ۼƿ���ҩƷ����
     * @param caseNo
     * @return
     * =======pangben 2013-9-10
     */
    private TParm sumPhaAntibiotic(String caseNo,Timestamp sysDate1){
    	String sysDate = StringTool.getString(sysDate1, "yyyyMMddHHmmss");
		String sql="SELECT A.ORDER_CODE FROM ODI_ORDER A,PHA_BASE B  WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CAT1_TYPE='PHA' AND CASE_NO='"
			+caseNo+"' AND B.ANTIBIOTIC_CODE IS NOT NULL  AND A.RX_KIND = 'UD' AND A.DC_DATE>TO_DATE('"+ sysDate +"','YYYYMMDDHH24MISS')";
		TParm orderParm = new TParm(this.getDBTool().select(sql));
		return orderParm;
		
    }
    /**
     * ����pha_anti���п���ҩ��ĸ���
     */
    private int sumAntibioticPha(TParm parm){
//		TParm orderParm = new TParm();
    	int count = 0;
		if(parm.getInt("ACTION", "COUNT")>0){
			for(int i = 0;i<parm.getInt("ACTION", "COUNT");i++){
				String sql = "SELECT ANTIBIOTIC_CODE FROM PHA_BASE WHERE ORDER_CODE = '"+parm.getValue("ORDER_CODE", i)+"' ";
				TParm antiParm = new TParm(this.getDBTool().select(sql));
				if(!"".equals(antiParm.getValue("ANTIBIOTIC_CODE", 0))&&
						!antiParm.getValue("ANTIBIOTIC_CODE", 0).equals(null)){//kangjun
					count++;
				}
			}
			
		}
		return count;
    }
    /**
     * add caoyong 20140320 
     * ���м������뵥�������뵥̯�� �������
     */
    
    public TParm getOrderPasEMRAllM(TDataStore ds, String type) {
    	 TParm result = new TParm();
         String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
         int rowCount = ds.rowCountFilter();
         int rowCounts = 0;
         TParm TOparm =new TParm();
         if ("ODO".equals(type)) {
         	
         	 for (int i = rowCount-1; i >= 0; i--){
                 if (!ds.isActive(i, buff))
                     continue;
                 TParm temp = ds.getRowParm(i, buff);
                 //System.out.println("-------111temp111----------"+temp);
                 if (temp.getValue("MR_CODE").length() == 0)
                     continue;
//                 if(temp.getValue("ORDERSET_CODE").length()!=0&&!temp.getBoolean("SETMAIN_FLG"))
//                     continue;
//                 if(temp.getValue("MED_APPLY_NO")==null||temp.getValue("MED_APPLY_NO").length()==0)
//                     continue;
                 if (temp.getBoolean("HIDE_FLG"))
                     continue;
                  TOparm = new TParm(this.getDBTool().select(
                         "SELECT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO='" +
                         temp.getValue("CASE_NO") + "' AND RX_NO='" +
                         temp.getValue("RX_NO") + "' AND (CAT1_TYPE='RIS' OR CAT1_TYPE='LIS') AND SEQ_NO='" +//ԭ����ORDER_CAT1_CODE='RIS' caoyong  modify (ORDER_CAT1_CODE='RIS' OR ORDER_CAT1_CODE='LIS') 
                         temp.getValue("SEQ_NO") + "'  "));
                 
                 TParm countParm = new TParm(this.getDBTool().select(
                 		"SELECT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO='" +
                 		temp.getValue("CASE_NO") + "' AND RX_NO='" +
                 		temp.getValue("RX_NO") + "' AND CAT1_TYPE='RIS' AND SEQ_NO='" +//ԭ����ORDER_CAT1_CODE='RIS' caoyong  modify (ORDER_CAT1_CODE='RIS' OR ORDER_CAT1_CODE='LIS') 
                 		temp.getValue("SEQ_NO") + "'  "));
                 /**System.out.println("ODO-------"+"SELECT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO='" +
                         temp.getValue("CASE_NO") + "' AND RX_NO='" +
                         temp.getValue("RX_NO") + "' AND ORDER_CAT1_CODE='RIS' AND SEQ_NO='" +
                         temp.getValue("SEQ_NO") + "'  ");**/
                 if (countParm.getCount() <= 0)
                     continue;
                 //$-----add liling 2014/8/7 ��Ӽ�����뵥ִ�еص�----  start
               //String sql=" SELECT CHN_DESC FROM SYS_FEE A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE=B.ID AND A.ORDER_CODE='"+temp.getData("ORDER_CODE")+"' ";
               String sql="SELECT A.USER_NAME,C.CHN_DESC FROM SYS_OPERATOR A, SYS_COST_CENTER B, SYS_DICTIONARY C"
            	   +" WHERE B.COST_CENTER_CODE='"+temp.getValue("COST_CENTER_CODE")+"' " 
            	   +" AND C.GROUP_ID = 'EXAADDRESS' AND B.EXE_PLACE=C.ID "//������뵥 ִ�еص�  �ӳɱ����� ��ȡ modify by huangjw 20140912
               	   +" AND A.USER_ID='"+temp.getData("DR_CODE")+"'";//��ȡ����ҽ�� modify by huangjw 20140915
               TParm exeaParm = new TParm(this.getDBTool().select(sql));
               if(exeaParm.getCount()>0){//==liling 20140810 add �ǿ��ж�
                   result.addData("EXEA_PLACE",exeaParm.getValue("CHN_DESC", 0));
                   result.addData("DR_CODE", exeaParm.getData("USER_NAME",0));
                   }
                   else{
                   	result.addData("EXEA_PLACE","");
                   	result.addData("USER_NAME","");
                   }
               if("Y".equals(temp.getData("MEM_PACKAGE_FLG"))){//add by kangy 20170522
                   result.addData("MEM_PACKAGE_FLG","�ײ���Ŀ");
               }else{
                   result.addData("MEM_PACKAGE_FLG","");
               }
               //$-----add liling 2014/8/7  ��Ӽ�����뵥ִ�еص�----  end
               	 result.addData("DR_NOTE", temp.getData("DR_NOTE"));//������뵥����ҽʦ��ע add by huangjw20141015
               	 result.addData("DEPT_CODE", temp.getData("DEPT_CODE"));//����������� modify by huangjw 20140916
                 result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                 result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                 result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                 result.addData("ORDER_ENG_DESC", temp.getData("TRADE_ENG_DESC"));
                 result.addData("MR_CODE", temp.getData("MR_CODE"));
                 result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                 result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                 result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 �����ʾ������
                 result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO"));
                 result.addData("DR_NOTE", temp.getData("DR_NOTE"));//liling 20140826 add ҽʦ��ע
                 //����ִ�п���
                 result.addData("EXEC_DEPT_CODE", temp.getData("EXEC_DEPT_CODE"));
                 //
                 //���ӱ�������
				String rptTypesql = "SELECT CATEGORY_CHN_DESC FROM SYS_CATEGORY WHERE CATEGORY_CODE='"
						+ temp.getData("RPTTYPE_CODE")
						+ "' AND RULE_TYPE = 'EXM_RULE'";
				TParm rptTypeParm = new TParm(this.getDBTool().select(rptTypesql));
				if (rptTypeParm.getCount() > 0) {
					result.addData("RPTTYPE_DESC", rptTypeParm.getValue(
							"CATEGORY_CHN_DESC", 0));
				} else {
					result.addData("RPTTYPE_DESC", "");
				}
                 
                 //
                 //��ѯ��û��FILE_NO
                 /**System.out.println("SQL===" +
                         "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                                    temp.getData("CASE_NO") + "' AND RX_NO='" +
                                    temp.getData("ORDER_NO") + "' AND SEQ_NO='" +
                                    temp.getData("SEQ_NO") + "'");**/
				//
                 TParm fileNoParm = new TParm(this.getDBTool().select(
                         "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                         temp.getData("CASE_NO") + "' AND RX_NO='" +
                         temp.getData("RX_NO") + "' AND CAT1_TYPE='RIS' AND SEQ_NO='" +
                         temp.getData("SEQ_NO") + "'"));
                 /**System.out.println(
                         "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                         temp.getData("CASE_NO") + "' AND RX_NO='" +
                         temp.getData("ORDER_NO") + "' AND SEQ_NO='" +
                         temp.getData("SEQ_NO") + "'");
                 System.out.println("fileNoParmfileNoParmfileNoParm" +
                                    fileNoParm);**/
                 if (fileNoParm.getCount() <= 0) {
                     result.addData("FILE_NO", "0");
                 } else {
                     result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                 }
                 result.addData("ORDER_NO", temp.getData("RX_NO"));
                 result.addData("ORDER_SEQ", temp.getData("SEQ_NO"));
                 rowCounts++;
             }
         	 
         }
         
         if ("EMG".equals(type)) {
             for (int i = 0; i < rowCount; i++) {
                 if (!ds.isActive(i, buff))
                     continue;
                 TParm temp = ds.getRowParm(i, buff);
                 if (temp.getValue("MR_CODE").length() == 0)
                     continue;
//                 if(temp.getValue("ORDERSET_CODE").length()!=0&&!temp.getBoolean("SETMAIN_FLG"))
//                     continue;
//                 if(temp.getValue("MED_APPLY_NO")==null||temp.getValue("MED_APPLY_NO").length()==0)
//                     continue;
                 if (temp.getBoolean("HIDE_FLG"))
                     continue;
                 TOparm = new TParm(this.getDBTool().select(
                         "SELECT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO='" +
                         temp.getValue("CASE_NO") + "' AND RX_NO='" +
                         temp.getValue("RX_NO") + "' AND (CAT1_TYPE='RIS' OR CAT1_TYPE='LIS') AND SEQ_NO='" +
                         temp.getValue("SEQ_NO") + "'"));
                 
                 TParm countParm = new TParm(this.getDBTool().select(
                         "SELECT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO='" +
                         temp.getValue("CASE_NO") + "' AND RX_NO='" +
                         temp.getValue("RX_NO") + "' AND CAT1_TYPE='RIS' AND SEQ_NO='" +
                         temp.getValue("SEQ_NO") + "'"));
                 if (countParm.getCount() <= 0)
                     continue;
                 //$-----add liling 2014/8/7 ��Ӽ�����뵥ִ�еص�----  start
                 //String sql=" SELECT CHN_DESC FROM SYS_FEE A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE=B.ID AND A.ORDER_CODE='"+temp.getData("ORDER_CODE")+"' ";
                 String sql="SELECT A.USER_NAME,C.CHN_DESC FROM SYS_OPERATOR A, SYS_COST_CENTER B, SYS_DICTIONARY C"
              	   +" WHERE B.COST_CENTER_CODE='"+temp.getValue("COST_CENTER_CODE")+"' " 
              	   +" AND C.GROUP_ID = 'EXAADDRESS' AND B.EXE_PLACE=C.ID "//������뵥 ִ�еص�  �ӳɱ����� ��ȡ modify by huangjw 20140912
                 	   +" AND A.USER_ID='"+temp.getData("DR_CODE")+"'";//��ȡ����ҽ�� modify by huangjw 20140915
                 TParm exeaParm = new TParm(this.getDBTool().select(sql));
                 if(exeaParm.getCount()>0){//==liling 20140810 add �ǿ��ж�
                     result.addData("EXEA_PLACE",exeaParm.getValue("CHN_DESC", 0));
                     result.addData("DR_CODE", exeaParm.getData("USER_NAME",0));
                 }else{
                 	 result.addData("EXEA_PLACE","");
                 	 result.addData("USER_NAME","");
                     }
                 //$-----add liling 2014/8/7  ��Ӽ�����뵥ִ�еص�----  end
                 result.addData("DR_NOTE", temp.getData("DR_NOTE"));//������뵥����ҽʦ��ע add by huangjw20141015
                 result.addData("DEPT_CODE", temp.getData("DEPT_CODE"));//����������� modify by huangjw 20140916
                 result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                 result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                 result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                 result.addData("ORDER_ENG_DESC", temp.getData("TRADE_ENG_DESC"));
                 result.addData("MR_CODE", temp.getData("MR_CODE"));
                 result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                 result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                 result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 �����ʾ������
                 result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO")); 
                 result.addData("DR_NOTE", temp.getData("DR_NOTE"));//liling 20140826 add ҽʦ��ע
                 //����ִ�п���
                 result.addData("EXEC_DEPT_CODE", temp.getData("EXEC_DEPT_CODE"));
                 
                 //��ѯ��û��FILE_NO
                 TParm fileNoParm = new TParm(this.getDBTool().select(
                         "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                         temp.getData("CASE_NO") + "' AND RX_NO='" +
                         temp.getData("RX_NO") + "' AND SEQ_NO='" +
                         temp.getData("SEQ_NO") + "'"));
                 if (fileNoParm.getCount() <= 0) {
                     result.addData("FILE_NO", "0");
                 } else {
                     result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                 }
                 result.addData("ORDER_NO", temp.getData("RX_NO"));
                 result.addData("ORDER_SEQ", temp.getData("SEQ_NO"));
                 rowCounts++;
             }
         }
         result.setData("ACTION", "COUNT", rowCounts);
         result.setData("MED_APPLY",TOparm.getValue("MED_APPLY_NO",0) );
         //System.out.println("���뵥===" + result);
         return result;
         
         
         
         
    }
    /**
     * ����ȫ��ҽ�������б�
     * @param ds TDataStore
     * @param type String
     * @return TParm
     */
    public TParm getOrderPasEMRAll(TDataStore ds, String type) {
    	//System.out.println("========getOrderPasEMRAll==========");
        TParm result = new TParm();
        String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
        int rowCount = ds.rowCountFilter();
        int rowCounts = 0;
        System.out.println("type----"+type);
        if ("ODI".equals(type)) {
            for (int i = rowCount-1; i >= 0; i--) {
            	System.out.println(i);
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
//                if(temp.getValue("ORDERSET_CODE").length()!=0&&!temp.getBoolean("SETMAIN_FLG"))
//                    continue;
//                if(temp.getValue("MED_APPLY_NO")==null||temp.getValue("MED_APPLY_NO").length()==0)
//                    continue;
                if (temp.getBoolean("HIDE_FLG"))
                    continue;
              //============xueyf modify 20120306 start
                //��ѯ��û��FILE_NO
               /**System.out.println(
                        "SELECT FILE_NO FROM ODI_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND CAT1_TYPE='RIS' AND ORDER_NO='" +
                        temp.getData("ORDER_NO") + "' AND ORDER_SEQ='" +
                        temp.getData("ORDER_SEQ") + "'");**/
                //System.out.println("======temp========"+temp);
                
                TParm fileNoParm = new TParm(this.getDBTool().select(
                        "SELECT FILE_NO FROM ODI_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND CAT1_TYPE = 'RIS' AND ORDER_NO='" + //============xueyf modify 20120306 ����ҽ������ʾ
                        temp.getData("ORDER_NO") + "' AND ORDER_SEQ='" +
                        temp.getData("ORDER_SEQ") + "'"));
                
                /**System.out.println(
                "SELECT FILE_NO FROM ODI_ORDER WHERE CASE_NO='" +
                temp.getData("CASE_NO") + "' AND CAT1_TYPE='RIS' AND ORDER_NO='" +
                temp.getData("ORDER_NO") + "' AND ORDER_SEQ='" +
                temp.getData("ORDER_SEQ") + "'");**/
                //System.out.println("========fileNoParm=========="+fileNoParm);
                if (fileNoParm.getCount() <= 0)
                    continue;
              //$-----add caoyong 2014/6/4 ��Ӽ�����뵥ִ�еص�----  start
//                String sql=" SELECT CHN_DESC FROM SYS_COST_CENTER A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE=B.ID AND COST_CENTER_CODE='"+temp.getData("EXEC_DEPT_CODE")+"' ";//==liling 20140807 modify 
                String sql=" SELECT CHN_DESC FROM SYS_FEE A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE=B.ID AND A.ORDER_CODE='"+temp.getData("ORDER_CODE")+"' ";
                TParm exeaParm = new TParm(this.getDBTool().select(sql));
                if(exeaParm.getCount()>0){//==liling 20140810 add �ǿ��ж�
                result.addData("EXEA_PLACE",exeaParm.getValue("CHN_DESC", 0));}
                else{
                	result.addData("EXEA_PLACE","");
                }
                //$-----add caoyong 2014/6/4 ��Ӽ�����뵥ִ�еص�----  end
                
               
              //============xueyf modify 20620301 stop
                result.addData("ORDER_DATE", temp.getData("EFF_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("ORDER_ENG_DESC", temp.getData("ORDER_ENG_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 �����ʾ������
                result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO"));
              //����ִ�п���
                result.addData("EXEC_DEPT_CODE", temp.getData("EXEC_DEPT_CODE"));
                
                String deptCodeSql = "SELECT DEPT_CODE FROM ADM_INP WHERE CASE_NO = '"+temp.getData("CASE_NO")+"'";
                System.out.println("deptCodeSql---"+deptCodeSql);
                TParm deptCodeParm = new TParm(this.getDBTool().select(deptCodeSql));
                if(deptCodeParm.getCount()>0){//==liling 20140810 add �ǿ��ж�
                	result.addData("DEPT_CODE",deptCodeParm.getValue("DEPT_CODE", 0));}
                else{
                	result.addData("DEPT_CODE","");
                }
                
                if (fileNoParm.getCount() <= 0) {
                    result.addData("FILE_NO", "0");
                } else {
                    result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                }
                result.addData("ORDER_NO", temp.getData("ORDER_NO"));
                result.addData("ORDER_SEQ", temp.getData("ORDER_SEQ"));
                rowCounts++;
            }
        }
        if ("ODO".equals(type)) {//ֻ�м���̯�������
        	 for (int i = rowCount-1; i >= 0; i--){
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
//                System.out.println("-----ODO temp------"+temp);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
//                if(temp.getValue("ORDERSET_CODE").length()!=0&&!temp.getBoolean("SETMAIN_FLG"))
//                    continue;
//                if(temp.getValue("MED_APPLY_NO")==null||temp.getValue("MED_APPLY_NO").length()==0)
//                    continue;
                if (temp.getBoolean("HIDE_FLG"))
                    continue;
                
                TParm countParm = new TParm(this.getDBTool().select(
                		"SELECT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO='" +
                		temp.getValue("CASE_NO") + "' AND RX_NO='" +
                		//��ѯ����Ϊ(ORDER_CAT1_CODE='RIS' OR ORDER_CAT1_CODE='LIS')��ҽ��Ϊ�������
                		temp.getValue("RX_NO") + "' AND (ORDER_CAT1_CODE='RIS' OR ORDER_CAT1_CODE='LIS') AND SEQ_NO='" +//ԭ����ORDER_CAT1_CODE='RIS' caoyong  modify (ORDER_CAT1_CODE='RIS' OR ORDER_CAT1_CODE='LIS') 
                		temp.getValue("SEQ_NO") + "'  "));
             /*  System.out.println("ODO-------"+"SELECT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO='" +
                		temp.getValue("CASE_NO") + "' AND RX_NO='" +
                		temp.getValue("RX_NO") + "' AND (ORDER_CAT1_CODE='RIS' OR ORDER_CAT1_CODE='LIS') AND SEQ_NO='" +//ԭ����ORDER_CAT1_CODE='RIS' caoyong  modify (ORDER_CAT1_CODE='RIS' OR ORDER_CAT1_CODE='LIS') 
                		temp.getValue("SEQ_NO") + "'  ");                
               System.out.println("EXEC_DEPT_CODE==="+temp.getData("EXEC_DEPT_CODE"));*/
                if (countParm.getCount() <= 0)
                    continue;
                //$-----add caoyong 2014/6/4 ��Ӽ�����뵥ִ�еص�----  start
//                String sql=" SELECT CHN_DESC FROM SYS_COST_CENTER A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE=B.ID AND COST_CENTER_CODE='"+temp.getData("EXEC_DEPT_CODE")+"' ";//==liling 20140807 modify 
                String sql=" SELECT CHN_DESC FROM SYS_FEE A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE=B.ID AND A.ORDER_CODE='"+temp.getData("ORDER_CODE")+"' ";
                TParm exeaParm = new TParm(this.getDBTool().select(sql));
                //$-----add caoyong 2014/6/4 ��Ӽ�����뵥ִ�еص�----  end
                
                if(exeaParm.getCount()>0){//==liling 20140810 add �ǿ��ж�
                    result.addData("EXEA_PLACE",exeaParm.getValue("CHN_DESC", 0));}
                    else{
                    	result.addData("EXEA_PLACE","");
                    }
                result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("ORDER_ENG_DESC", temp.getData("TRADE_ENG_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 �����ʾ������
                result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO"));
              //����ִ�п���
                result.addData("EXEC_DEPT_CODE", temp.getData("EXEC_DEPT_CODE"));
                
                //��ѯ��û��FILE_NO
                /**System.out.println("SQL===" +
                        "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                                   temp.getData("CASE_NO") + "' AND RX_NO='" +
                                   temp.getData("ORDER_NO") + "' AND SEQ_NO='" +
                                   temp.getData("SEQ_NO") + "'");**/
                TParm fileNoParm = new TParm(this.getDBTool().select(
                        "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND RX_NO='" +
                        temp.getData("RX_NO") + "' AND CAT1_TYPE='RIS' AND SEQ_NO='" +
                        temp.getData("SEQ_NO") + "'"));
                /**System.out.println(
                        "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND RX_NO='" +
                        temp.getData("ORDER_NO") + "' AND SEQ_NO='" +
                        temp.getData("SEQ_NO") + "'");
                System.out.println("fileNoParmfileNoParmfileNoParm" +
                                   fileNoParm);**/
                if (fileNoParm.getCount() <= 0) {
                    result.addData("FILE_NO", "0");
                } else {
                    result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                }
                result.addData("ORDER_NO", temp.getData("RX_NO"));
                result.addData("ORDER_SEQ", temp.getData("SEQ_NO"));
                rowCounts++;
            }
        	 
        }
        if ("EMG".equals(type)) {
            for (int i = 0; i < rowCount; i++) {
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
//                if(temp.getValue("ORDERSET_CODE").length()!=0&&!temp.getBoolean("SETMAIN_FLG"))
//                    continue;
//                if(temp.getValue("MED_APPLY_NO")==null||temp.getValue("MED_APPLY_NO").length()==0)
//                    continue;
                if (temp.getBoolean("HIDE_FLG"))
                    continue;
                TParm countParm = new TParm(this.getDBTool().select(
                        "SELECT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO='" +
                        temp.getValue("CASE_NO") + "' AND RX_NO='" +
                        temp.getValue("RX_NO") + "' AND CAT1_TYPE='RIS' AND SEQ_NO='" +
                        temp.getValue("SEQ_NO") + "'"));
                if (countParm.getCount() <= 0)
                    continue;
                result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("ORDER_ENG_DESC", temp.getData("TRADE_ENG_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 �����ʾ������
                result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO"));    
              //����ִ�п���
                result.addData("EXEC_DEPT_CODE", temp.getData("EXEC_DEPT_CODE"));
                
                //��ѯ��û��FILE_NO
                TParm fileNoParm = new TParm(this.getDBTool().select(
                        "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND RX_NO='" +
                        temp.getData("RX_NO") + "' AND SEQ_NO='" +
                        temp.getData("SEQ_NO") + "'"));
                if (fileNoParm.getCount() <= 0) {
                    result.addData("FILE_NO", "0");
                } else {
                    result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                }
                result.addData("ORDER_NO", temp.getData("RX_NO"));
                result.addData("ORDER_SEQ", temp.getData("SEQ_NO"));
                rowCounts++;
            }
        }
        if ("HRM".equals(type)) {
            for (int i = 0; i < rowCount; i++) {
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
//                if(temp.getValue("ORDERSET_CODE").length()!=0&&!temp.getBoolean("SETMAIN_FLG"))
//                    continue;
//                if(temp.getValue("MED_APPLY_NO")==null||temp.getValue("MED_APPLY_NO").length()==0)
//                    continue;
                if (temp.getBoolean("HIDE_FLG"))
                    continue;
                TParm countParm = new TParm(this.getDBTool().select(
                        "SELECT MED_APPLY_NO FROM HRM_ORDER WHERE CASE_NO='" +
                        temp.getValue("CASE_NO") + "' AND CAT1_TYPE='RIS' AND SEQ_NO='" +
                        temp.getValue("SEQ_NO") + "'"));
                if (countParm.getCount() <= 0)
                    continue;
                result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("ORDER_ENG_DESC", temp.getData("TRADE_ENG_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 �����ʾ������
                result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO"));
              //����ִ�п���
                result.addData("EXEC_DEPT_CODE", temp.getData("EXEC_DEPT_CODE"));
               
                //��ѯ��û��FILE_NO
                TParm fileNoParm = new TParm(this.getDBTool().select(
                        "SELECT FILE_NO FROM HRM_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND SEQ_NO='" +
                        temp.getData("SEQ_NO") + "'"));
                if (fileNoParm.getCount() <= 0) {
                    result.addData("FILE_NO", "0");
                } else {
                    result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                }
                result.addData("ORDER_NO", temp.getData("CASE_NO"));
                result.addData("ORDER_SEQ", temp.getData("SEQ_NO"));
                rowCounts++;
            }
        }
        if ("ODI_PHA".equals(type)) {//====pangben 2013-7-30 ҩƷ����ҩ�������ݣ��������뵥��סԺҽ��վ�������
        	  for (int i = rowCount-1; i >= 0; i--) {
                  if (!ds.isActive(i, buff))
                      continue;
                  TParm temp = ds.getRowParm(i, buff);
                  if (temp.getBoolean("HIDE_FLG"))
                      continue;
                  TParm fileNoParm = new TParm(this.getDBTool().select(
                          "SELECT A.FILE_NO,C.MR_CODE FROM ODI_ORDER A ,PHA_BASE B,SYS_ANTIBIOTIC C  WHERE A.ORDER_CODE=B.ORDER_CODE AND " +
                          "B.ANTIBIOTIC_CODE=C.ANTIBIOTIC_CODE AND A.CASE_NO='" +
                          temp.getData("CASE_NO") + "' AND A.ORDER_NO='" +
                          temp.getData("ORDER_NO") + "' AND A.ORDER_SEQ='" +
                          temp.getData("ORDER_SEQ") + "' AND A.CAT1_TYPE='PHA' AND B.ANTIBIOTIC_CODE IS NOT NULL "));
                  if (fileNoParm.getCount() <= 0)
                      continue;
                  if (fileNoParm.getValue("MR_CODE",0).length() == 0)
                      continue;
                //============xueyf modify 20620301 stop
                  result.addData("ORDER_DATE", temp.getData("EFF_DATE"));
                  result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                  result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                  result.addData("ORDER_ENG_DESC", temp.getData("ORDER_ENG_DESC"));
                  result.addData("MR_CODE", fileNoParm.getData("MR_CODE",0));
                  result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                  result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                  result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 �����ʾ������
                  result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO"));
              
                  if (fileNoParm.getCount() <= 0) {
                      result.addData("FILE_NO", "0");
                  } else {
                      result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                  }
                  result.addData("ORDER_NO", temp.getData("ORDER_NO"));
                  result.addData("ORDER_SEQ", temp.getData("ORDER_SEQ"));
                  rowCounts++;
              }
		}
        
        result.setData("ACTION", "COUNT", rowCounts);
        //System.out.println("���뵥===" + result);
        return result;
    }

    /**
     * ��ѯ��������
     * @param iptCode String
     * @return String
     */
    public String queryOptItem(String iptCode) {
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OPTITEM' AND ID = '" +
                iptCode + "'"));
        return parm.getValue("CHN_DESC", 0);
    }

    /**
     * �õ�������
     * @param mainDiag String
     * @return String
     */
    public String queryDiagType(String mainDiag) {
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT ICD_TYPE FROM SYS_DIAGNOSIS WHERE ICD_CODE='" +
                mainDiag + "'"));
        return parm.getValue("ICD_TYPE", 0);
    }

    /**
     * �ж�ҽ���Ƿ��Ѿ����
     * @param parm TParm
     * @return boolean
     */
    public boolean checkOrderNSCheck(TParm parm, boolean ncCheckFlg) {
        boolean falg = false;
        if (ncCheckFlg) {
            TParm result = new TParm(this.getDBTool().select(
                    "SELECT NS_CHECK_DATE FROM ODI_ORDER WHERE CASE_NO='" +
                    parm.getValue("CASE_NO") + "' AND ORDER_NO='" +
                    parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='" +
                    parm.getValue("ORDER_SEQ") + "'"));
            if (result.getErrCode() < 0)
                falg = true;
            if (result.getTimestamp("NS_CHECK_DATE", 0) != null)
                falg = true;
        } else {
            String startDttm = StringTool.getString(parm.getTimestamp(
                    "START_DTTM"), "yyyyMMddHHmmss");
//            System.out.println("startDttm"+startDttm);
            TParm resultExe = new TParm(this.getDBTool().select(
                    "SELECT NS_EXEC_DATE FROM ODI_DSPNM WHERE CASE_NO='" +
                    parm.getValue("CASE_NO") + "' AND ORDER_NO='" +
                    parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='" +
                    parm.getValue("ORDER_SEQ") + "' AND START_DTTM='" +
                    startDttm + "'"));
            if (resultExe.getErrCode() < 0)
                falg = true;
            if (resultExe.getTimestamp("NS_EXEC_DATE", 0) != null)
                falg = true;
        }
        return falg;
    }

    /**
     * �õ���λ
     * @param unitCode String
     * @return String
     */
    public String getUnit(String unitCode) {
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT UNIT_CODE,UNIT_CHN_DESC FROM SYS_UNIT WHERE UNIT_CODE='" +
                unitCode + "'"));
        return parm.getValue("UNIT_CHN_DESC", 0);
    }

    /**
     * �õ�Ƶ��
     * @param freqCode String
     * @return String
     */
    public String getFreq(String freqCode) {
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT CYCLE,FREQ_TIMES FROM SYS_PHAFREQ WHERE FREQ_CODE='" +
                freqCode + "'"));
        return parm.getInt("FREQ_TIMES", 0) + "/" + parm.getInt("CYCLE", 0);
    }

    /**
     * �õ��÷�
     * @param routeCode String
     * @return String
     */
    public String getRoute(String routeCode) {
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT ROUTE_CODE,ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE='" +
                routeCode + "'"));
        return parm.getValue("ROUTE_CHN_DESC", 0);
    }

    /**
     * �õ�������ҩҩƷ
     * @param caseNo String
     * @return List
     */
    public List getPassOrder(String caseNo) {
        List result = new ArrayList();
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String starDate = StringTool.getString(sysDate, "yyyyMMdd") + "000000";
        String sql = "SELECT * FROM ODI_ORDER WHERE CASE_NO='" + caseNo + "' AND RX_KIND IN ('ST','DS','IG') AND CAT1_TYPE='PHA' AND  DC_DATE IS NULL AND ORDER_DATE BETWEEN TO_DATE('" +
                     starDate + "','YYYYMMDDHH24MISS') AND SYSDATE " +
                     "UNION " +
                     " SELECT * FROM ODI_ORDER WHERE CASE_NO='" + caseNo +
                "' AND RX_KIND='UD' AND CAT1_TYPE='PHA' AND  DC_DATE IS NULL";
        TParm parm = new TParm(this.getDBTool().select(sql));
        if (parm.getErrCode() != 0) {
            return result;
        }
        int countP = parm.getCount();
        String[] orderInfo;
        for (int i = 0; i < countP; i++) {
            TParm temp = parm.getRow(i);
            if (StringUtil.isNullString(temp.getValue("ORDER_DESC")))
                continue;
            orderInfo = this.getOrderInfo(temp);
            result.add(orderInfo);
        }
        return result;
    }

    /**
     * �õ�������ҩ��Ϣ
     * @param parm TParm
     * @return String[]
     */
    public String[] getOrderInfo(TParm parm) {
        String[] result = new String[12];
        //ҩƷΨһ�룺RX_NO+FILL0(SEQ,3)+ORDER_CODE
        result[0] = parm.getValue("RX_NO") +
                    StringTool.fill0(parm.getValue("SEQ_NO") + "", 3) +
                    parm.getValue("ORDER_CODE");
        result[1] = parm.getValue("ORDER_CODE");
        result[2] = parm.getValue("ORDER_DESC");
        result[3] = parm.getValue("MEDI_QTY");
        result[4] = getUnit(parm.getValue("MEDI_UNIT"));
        result[5] = getFreq(parm.getValue("FREQ_CODE"));
        String date = StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                           "yyyy-MM-dd");
        result[6] = date;
        result[7] = date;
        result[8] = getRoute(parm.getValue("ROUTE_CODE"));
        result[9] = "1";
        String type = "UD".equals(parm.getValue("RX_KIND")) ? "0" : "1";
        result[10] = type;
        result[11] = Operator.getName();
        return result;
    }

    /**
     * �����o��orderCat1Code�õ�deal_system
     * @param orderCat1Code
     * @return
     */
	public String getDealSystem(String orderCat1Code) {
		if (StringUtil.isNullString(orderCat1Code)) {
			return "";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(
				GET_DEAL_SYSTEM_SQL.replace("#", orderCat1Code)));
		return result.getValue("DEAL_SYSTEM", 0);
	}
	/**
	 * ���㵱ǰ���ں�����������������
	 * @throws ParseException 
	 * 
	 */
	public long diffDays(String SDate, String MDate) {
		 long quot = 0;
		try{
//		 long quot = 0;
			SDate = SDate.substring(0, 10);
			MDate = MDate.substring(0, 10);
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		Date sysDate = ft.parse( SDate );
		   Date maxDate = ft.parse( MDate );
		   quot = sysDate.getTime() - maxDate.getTime();
		   quot = quot / 1000 / 60 / 60 / 24;
//		   return quot;
		   }catch (Exception e) {
				e.printStackTrace();
			}
		   return quot;
	}
	/**
	 * ������ʱ�����
	 * 
	 */
	public TParm getAntiTimeControl(String caseNo){
        TParm phaEmrParm = new TParm();//���ؽ��
        TParm result = new TParm();//��ѯ���
        boolean flg = false;//������ߵ������뵥У��
		//��ѯҽ�����д���������ʱ�����С�Ŀ�ʼʱ��
		String odiOrderSql="SELECT MAX(DC_DATE) AS DC_DATE,MIN(ORDER_DATE) AS ORDER_DATE FROM ODI_ORDER WHERE CASE_NO='"+caseNo+
		"' AND ANTIBIOTIC_CODE IS NOT NULL AND RX_KIND ='UD'";
		TParm odiOrderParm=new TParm(TJDODBTool.getInstance().select(odiOrderSql));
		 long diffDays = 100;//��pha_anti���в����ڸò�����Ϣʱ���ʹ���31���������ͬ
		if (odiOrderParm.getCount("DC_DATE")<=0) {//û��ʹ�ÿ���ҩƷ
			phaEmrParm.setData("NODE_FLG", "N");
		}else{
		    String maxDate = odiOrderParm.getValue("DC_DATE",0).toString();//����������
			if (null != maxDate && maxDate.length() > 0) {
				String sysDate = SystemTool.getInstance().getDate().toString();// ��ȡϵͳ��ǰʱ��
				diffDays = this.diffDays(sysDate, maxDate);// �Ƚ�������ʱ���뵱ǰʱ��Ƚ�
				if (diffDays < 31) {// ���ܳ���14��ʹ�õ����
					String minDate = odiOrderParm.getValue("ORDER_DATE", 0)
							.toString();// ��С��ʼ����
					if (null != minDate && minDate.length() > 0) {
						diffDays = this.diffDays(sysDate, minDate);// �Ƚ�������ʱ���뵱ǰʱ��Ƚ�
						if (diffDays >= 14) {// ����ʹ��14������
							String sql = "SELECT SUM(DAYS) SUMDAY FROM (SELECT TRUNC(DC_DATE-ORDER_DATE) DAYS FROM ODI_ORDER WHERE CASE_NO='"
									+ caseNo
									+ "' AND ANTIBIOTIC_CODE IS NOT NULL AND RX_KIND ='UD')";// pangben
																								// 2013-12-23��ѯODI_ORDER����ͣ��ʱ�����14������
							odiOrderParm = new TParm(TJDODBTool.getInstance()
									.select(sql));
							if (odiOrderParm.getInt("SUMDAY", 0) >= 14) {
								result.setErr(-1, "����ҩƷ��������14�죬��д���뵥");
								flg = true;
								phaEmrParm.setData("PHA_USE_FLG", "N");// ҽ��û��ʹ��״̬
							}
						}
					}
					phaEmrParm.setData("NODE_FLG", "N");
				} else {
					phaEmrParm.setData("NODE_FLG", "Y");// 31��ڵ��������
				}
			} else {
				phaEmrParm.setData("NODE_FLG", "N");
			}
		}
	    phaEmrParm.setData("FLG", flg);
//	    phaEmrParm.addParm(result);
	    return phaEmrParm;
	}
	/**
	 * ������ҩƷ����
	 * @return
	 */
	public TParm getPhaAntiCreate(TParm phaEmrParm,String caseNo,String mrNo,int index,TControl control) {
		Timestamp sysDate = SystemTool.getInstance().getDate();		
		TParm phaParm = sumPhaAntibiotic(caseNo,sysDate);
		int newPhaEmrParm = sumAntibioticPha(phaEmrParm);
//		int count = phaParm.getInt("COUNT", 0)
//				+ phaEmrParm.getInt("ACTION", "COUNT");// �ۼ�������
		int count=0;
		if (phaParm.getCount("ORDER_CODE")>0) {
			count = phaParm.getCount("ORDER_CODE")//pha_anti���м���ǿ���ҩ��ʱ��������
		            + newPhaEmrParm;// �ۼ�������
		}else{
			count=newPhaEmrParm;
		}
//		System.out.println("=====yeqina is ::"+index);
		String pagIndex = String.valueOf(index);
		phaEmrParm.addData("INDEX", pagIndex);
//		phaEmrParm.addData("INDEX", index);
		phaEmrParm.setData("CASE_NO", caseNo);
		phaEmrParm.setData("MR_NO", mrNo);
		phaEmrParm.setData("OPT_USER", Operator.getID());
		phaEmrParm.setData("OPT_TERM", Operator.getIP());
//		System.out.println("phaEmrParm=====yeqina is ::"+phaEmrParm);
		boolean flg = false;//������ߵ������뵥У��
		TParm result=new TParm();
		switch (index) {
		case 0://��ʱ
			for (int i = 0; i < phaEmrParm.getCount("ORDER_CODE"); i++) {
				if (null!=phaEmrParm.getValue("ROUTE_CODE",i)&&!phaEmrParm.getValue("ROUTE_CODE",i).equals("PS")) {
					//====��ѯ��ҩƷ�Ƿ���Ƥ����ҩ
					String  psPhaSql = "SELECT SKINTEST_FLG FROM PHA_BASE WHERE ORDER_CODE = '"+phaEmrParm.getValue("ORDER_CODE",i)+"' ";
					TParm psPharesult = new TParm(TJDODBTool.getInstance().select(psPhaSql));//��ѯ����ҽ���Ƿ���	
					if ("Y".equals(psPharesult.getValue("SKINTEST_FLG", 0))) {
						//=====��ѯ���˸�ҩƷ�Ƿ�����Ƥ��
						String psSql = "SELECT COUNT(A.CASE_NO) AS SUM  FROM ODI_ORDER A WHERE " +
								" A.CASE_NO = '"+caseNo+"' AND a.RX_KIND='ST' " +
								"AND A.ORDER_CODE='"+phaEmrParm.getValue("ORDER_CODE",i)+"'  ";
						TParm ps_result = new TParm(TJDODBTool.getInstance().select(psSql));
						if(ps_result.getInt("SUM",0)<=0){
							//pangben 20150602ѡ��ȷ�ϰ�ť�����ݱ��浽PHA_ANTI��
							 if(control.messageBox("��ʾ",phaEmrParm.getValue("ORDER_DESC", i)+"Ƥ�Ե��÷�����ȷ���Ƿ�������棿", 2) != 0){
								 result.setErr(-1, "Ƥ�Ե��÷�����ȷ");
								 return result;
		                     }
						}
					}
					
					String sql="SELECT A.CASE_NO,A.ORDER_DATE,B.ANTIBIOTIC_CODE FROM ODI_ORDER A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"+caseNo+
					"' AND A.RX_KIND='UD' AND A.ORDER_CODE='"+phaEmrParm.getValue("ORDER_CODE",i)+"'";
					TParm ope_result = new TParm(TJDODBTool.getInstance().select(sql));//��ѯ����ҽ���Ƿ���	
					if (ope_result.getCount()>0) {
						if (null==ope_result.getValue("ANTIBIOTIC_CODE",0)||
								ope_result.getValue("ANTIBIOTIC_CODE",0).length()<=0) {
							continue;
						}
						sql = "SELECT A.CASE_NO,A.ORDER_DESC,B.ANTIBIOTIC_CODE FROM ODI_ORDER A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"
							+ caseNo
							+ "' AND A.RX_KIND='ST' AND A.ORDER_CODE='"
							+ phaEmrParm.getValue("ORDER_CODE",i)
							+ "' AND A.ROUTE_CODE<>'PS' AND A.ORDER_DATE>TO_DATE('"
							+ SystemTool.getInstance().getDateReplace(ope_result.getValue("ORDER_DATE",0), true)+"','YYYYMMDDHH24MISS')";
					}else{
						
						sql="SELECT A.CASE_NO,B.ORDER_DESC,B.ANTIBIOTIC_CODE FROM ODI_ORDER A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"+caseNo+
						"' AND A.RX_KIND='ST' AND A.ORDER_CODE='"+phaEmrParm.getValue("ORDER_CODE",i)+"' AND A.ROUTE_CODE<>'PS' ";
					}
					ope_result = new TParm(TJDODBTool.getInstance().select(sql));//У����ʱҽ���Ƿ��Ѿ�����
					//�������ҽ���Ѿ�������ʱҽ��������һ��
					if (ope_result.getCount()>0) {
						if (null==ope_result.getValue("ANTIBIOTIC_CODE",0)||
								ope_result.getValue("ANTIBIOTIC_CODE",0).length()<=0) {
							continue;
						}
						//result.setErr(-1,ope_result.getValue("ORDER_DESC",0)+"ҩƷ�Ѿ������������Բ���");
						return result;
					}
				}
			}
			
//			System.out.println("��ʱ��ʱ��ʱ����ҩƷ����   phaEmrParm is :::"+phaEmrParm);
			TParm result1 = TIOM_AppServer.executeAction(
					"action.pha.PHAAntiAction", "onSavePhaAntiOne", phaEmrParm);
			if(result1.getErrCode() < 0){
				result.setErr(-1,result1.getErrText());
				return result;
			}
			break;
		case 1://����
//			System.out.println("����ҩƷ����   count is :::"+count);
			if (count >= 3) {// ������������������
				result.setErr(-1,"����ҩƷ����������������д���뵥");
				flg = true;
				phaEmrParm.setData("PHA_USE_FLG", "N");// ҽ��û��ʹ��״̬
			}else{
				TParm parm = new TParm();//����ʱ����Ʒ�����������ֵ
				parm = this.getAntiTimeControl(caseNo);
				phaEmrParm.setData("FLG", parm.getBoolean("FLG"));
				phaEmrParm.setData("PHA_USE_FLG", parm.getValue("PHA_USE_FLG"));
				phaEmrParm.setData("NODE_FLG", parm.getValue("NODE_FLG"));
				flg = phaEmrParm.getBoolean("FLG");
				if(flg){
//					result.setErr(-1,phaEmrParm.getValue("RESULT",0));
					result.setErr(-1,"����ҩƷ��������14�죬��д���뵥");
				}else{
//					System.out.println("11111orderUtilphaEmrParm phaEmrParm is����"+phaEmrParm);
					TParm result2 = TIOM_AppServer.executeAction(
							"action.pha.PHAAntiAction", "onSavePhaAntiOne", phaEmrParm);
					if (result2.getErrCode() < 0) {
						result.setErr(-1,"���濹��ҩƷʧ��");
						return result;
					}
				}
			}	
			break;
		default:
			
			TParm result3 = TIOM_AppServer.executeAction(
					"action.pha.PHAAntiAction", "onSavePhaAntiOne", phaEmrParm);
			if (result3.getErrCode() < 0) {
				//result.setErr(-1,"���濹��ҩƷʧ��");
				return result;
			}
			break;
		}
//		TParm result1 = TIOM_AppServer.executeAction(
//				"action.pha.PHAAntiAction", "onSavePhaAntiOne", phaEmrParm);
//		if (result1.getErrCode() < 0) {
//			result.setErr(-1,"���濹��ҩƷʧ��");
//			return result;
//		}
		if (flg) {
			result.setData("MESSAGE","Y");
			//====pangben 2014-2-25 ����ҩƷ��ʾ����ҽ����14������Ҳ���PHA_ANTI���߼�
			TParm result3 = TIOM_AppServer.executeAction(
					"action.pha.PHAAntiAction", "onSavePhaAntiOne", phaEmrParm);
			if (result3.getErrCode() < 0) {
				result.setErr(-1,"���濹��ҩƷʧ��");
			}
			return result;
		}
		return result;
	}
}
