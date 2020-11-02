package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dongyang.ui.TTextFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import jdo.sys.SystemTool;

/**
 * <p>Title: STA���⹫�÷���</p>
 *
 * <p>Description: STA���⹫�÷���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-12
 * @version 1.0
 */
public class STATool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STATool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STATool getInstance() {
        if (instanceObject == null)
            instanceObject = new STATool();
        return instanceObject;
    }

    public STATool() {
        setModuleName("sta\\STAModule.x");
        onInit();
    }
    /**
     * ���ݿ��ҵȼ���ѯ�м���տ���
     * @param level String[] �ַ��������� ���ҵȼ� ��ʽ�� {"1","2","3"}
     * @return TParm
     * ===============pangben modify 20110523 ����������
     */
    public TParm getDeptByLevel(String[] level,String regionCode){
        //===============pangben modify 20110523 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = "AND REGION_CODE='" + regionCode + "' ";
         //===============pangben modify 20110523 stop
        String sql = "SELECT DISTINCT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,OE_DEPT_CODE,IPD_DEPT_CODE FROM STA_OEI_DEPT_LIST ";
        if(level.length>0){
            //������鳤�ȴ����� ����where����
            sql += " WHERE DEPT_LEVEL IN (";
            //�����������
            for(int i=0;i<level.length;i++){
                sql += "'"+level[i]+"',";
            }
            //ȥ����β�Ķ���
            if(sql.endsWith(","))
                sql = sql.substring(0,sql.length()-1);
            sql += ") "+region+" ORDER BY TO_NUMBER(DEPT_CODE)";//==========pangben modify 20110523
        }else{
            sql+=" WHERE "+region.substring(region.indexOf("D")+1,region.length()); //==========pangben modify 20110523
        }
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���ݿ��ҵȼ���ѯ�м���տ���
     * @param level String[] �ַ��������� ���ҵȼ� ��ʽ�� {"1","2","3"}
     * @param DEPT_TYPE String  ������� OE���������   IPD��סԺ����
     * @return TParm
     */
    public TParm getDeptByLevel(String[] level,String DEPT_TYPE,String regionCode){
        //==========pangben modify 20110523 start
        String region="";
        if(null!=regionCode&&regionCode.length()>0)
            region="AND  REGION_CODE='"+regionCode+"' ";
        //==========pangben modify 20110523 stop
        String sql = "SELECT DISTINCT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,OE_DEPT_CODE,IPD_DEPT_CODE FROM STA_OEI_DEPT_LIST";
        if(level.length>0){
            //������鳤�ȴ����� ����where����
            sql += " WHERE DEPT_LEVEL IN (";
            //�����������
            for(int i=0;i<level.length;i++){
                sql += "'"+level[i]+"',";
            }
            //ȥ����β�Ķ���
            if(sql.endsWith(","))
                sql = sql.substring(0,sql.length()-1);
            sql += ")";
            if("OE".equals(DEPT_TYPE)){
                sql += " AND OE_DEPT_CODE IS NOT NULL ";
            }else if("IPD".equals(DEPT_TYPE)){
                sql += " AND IPD_DEPT_CODE IS NOT NULL ";
            }
            sql += region + " ORDER BY TO_NUMBER(DEPT_CODE)";
        } else {
            sql += " WHERE " + region.substring(region.indexOf("D")+1,region.length()); //==========pangben modify 20110523
        }
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public Map getIBSChargeName() {
		TParm result = this.query("getIBSChargeName");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return null;
		}
		String seq = "";
		String c_name = "";
		Map map = new HashMap();
		for (int i = 1; i <= 30; i++) {
			c_name = "CHARGE";
			if (i < 10)// IС��10 ����
				seq = "0" + i;
			else
				seq = "" + i;
			c_name += seq;
			map.put(c_name, result.getData(c_name, 0));
		}
		return map;
	}
    /**
     * ��ȡָ���·ݵ����һ�������
     * @param date String ��ʽ YYYYMM
     * @return Timestamp
     */
    public Timestamp getLastDayOfMonth(String date){
        if(date.trim().length()<=0){
            return null;
        }
        Timestamp time = StringTool.getTimestamp(date,"yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // ��ǰ�£�1�����¸���
        cal.add(cal.MONTH, 1);
        // ���¸���1����Ϊ���ڳ�ʼֵ
        cal.set(cal.DATE, 1);
        // �¸���1�ż�ȥһ�죬���õ���ǰ�����һ��
        cal.add(cal.DATE, -1);
        Timestamp result = new Timestamp(cal.getTimeInMillis());
        return result;
    }
    /**
     * ����ָ�����·ݼӼ�������Ҫ���·�
     * @param Month String �ƶ��·� ��ʽ:yyyyMM
     * @param num int �Ӽ������� ����Ϊ��λ
     * @return String
     */
    public String rollMonth(String Month,int num){
        if(Month.trim().length()<=0){
            return "";
        }
        Timestamp time = StringTool.getTimestamp(Month,"yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // ��ǰ�£�num
        cal.add(cal.MONTH, num);
        // ���¸���1����Ϊ���ڳ�ʼֵ
        cal.set(cal.DATE, 1);
        Timestamp month = new Timestamp(cal.getTimeInMillis());
        String result = StringTool.getString(month,"yyyyMM");
        return result;
    }
    /**
     * ���ĳ����ύ״̬
     * @param tablName String ����
     * @param STA_DATE String ��������
     * @param condition String ��������
     * @param regionCode String ��������
     * @return int  0��û�����ݣ�1�������ݵ���û���ύ��2���Ѿ��ύ��-1������
     * =============pangben modify 20110519 �����������
     */
    public int checkCONFIRM_FLG(String tableName,String STA_DATE,String condition,String regionCode){
        int re=-1;
        if(tableName.trim().length()<=0||STA_DATE.trim().length()<=0){
            System.out.println("STATool.checkCONFIRM_FLG====>:��������Ϊ��");
            return -1;
        }
        //==============pangben modify 20110519 start
        String region="";
        if(null!=regionCode && regionCode.length()>0)
            region=" AND REGION_CODE='"+regionCode+"'";
        //==============pangben modify 20110519 stop
        String sql = "select * from "+tableName+" where STA_DATE='"+STA_DATE+"'"+region;
        //��������������� ƴ��SQL���
        if(condition.trim().length()>0){
            sql += " AND " + condition;
        }
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return -1;
        }
        //�ж��Ƿ�������
        if(result.getCount("STA_DATE")>0){
            re = 2;//�������������Ѿ��ύ
            for(int i=0;i<result.getCount("STA_DATE");i++){
                //ѭ������� ��������д���û���ύ������ ����ֵΪ1
                if(!result.getValue("CONFIRM_FLG",i).equals("Y")){
                    re = 1;
                    break;
                }
            }
        }else{//û��������û������
            re = 0;
        }
        return re;
    }
    /**
     * ���ĳ����ύ״̬
     * @param tablName String ����
     * @param STA_DATE String ��������
     * @param regionCode String ��������
     * @return int
     * ============pangben modify 20110519 ����������
     */
    public int checkCONFIRM_FLG(String tableName,String STA_DATE,String regionCode){
        return this.checkCONFIRM_FLG(tableName,STA_DATE,"",regionCode);
    }
    /**
     * ��ȡĳ�µ�����
     * @param date String  ��ʽ��yyyyMM
     * @return int
     */
    public int getDaysOfMonth(String date){
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMM");
        try {
            rightNow.setTime(simpleDate.parse(date));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        int days = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }
    /**
     * ��ȡ���ݿ�ʱ�����һ���·�
     * @param data String
     * @return String
     */
    public Timestamp getLastMonth(){
        String now = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMM");
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMM");
        Calendar rightNow = Calendar.getInstance();
        try {
            rightNow.setTime(simpleDate.parse(now));
            rightNow.add(rightNow.MONTH,-1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Timestamp result = new Timestamp(rightNow.getTimeInMillis());
        return result;
    }
}
