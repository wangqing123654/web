package jdo.sys;

import java.sql.Timestamp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.TDebug;
import com.dongyang.util.StringTool;
/**
 *
 * <p>
 * Title: ������Ա���ݶ���
 * </p>
 *
 * <p>
 * Description: �����йز�����Ա��ȫ�����ݴ���
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author lzk 2008-08-11
 * @version 1.0
 */
public class OperatorTool extends TJDOTool {
        /**
         * ʵ��
         */
        public static OperatorTool instanceObject;

        /**
         * �õ�ʵ��
         *
         * @return OperatorTool
         */
        public static OperatorTool getInstance() {
                if (instanceObject == null)
                        instanceObject = new OperatorTool();
                return instanceObject;
        }

        /**
         * ������
         */
        public OperatorTool() {
                setModuleName("sys\\SYSOperatorModule.x");
                onInit();
        }

        /**
         * �õ��û�����
         *
         * @param userID
         *            String �û����
         * @return String ����
         */
        public String getOperatorName(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultString(query("getOperatorName", parm), "USER_NAME");
        }
        /**
         * ��ȡ�û�Ӣ����
         * @param userID String
         * @return String
         */
        public String getOperatorEngName(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultString(query("getOperatorEngName", parm), "USER_ENG_NAME");
        }
        /**
         * �õ��ɱ����Ĵ���
         * @param userID String
         * @return String
         */
        public String getCostCenterCode(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultString(query("getCostCenterCode", parm), "COST_CENTER_CODE");
        }


        /**
         * �õ��û�����
         *
         * @param userID
         *            String �û����
         * @return String �û�����
         */
        public String getOperatorPassword(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultString(query("getOperatorPassword", parm),
                                "USER_PASSWORD");
        }

        /**
         * �޸��û�����
         *
         * @param userID
         *            String �û����
         * @param password
         *            String ������
         * @return boolean true �ɹ� false ʧ��
         */
        public boolean setOperatorPassword(String userID, String password) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                parm.setData("USER_PASSWORD", password);
                TParm result = update("setOperatorPassword", parm);
                return result.getErrCode() == 0;
        }

        /**
         * �û��Ƿ����
         *
         * @param userID
         *            String �û����
         * @return boolean true ���� false ������
         */
        public boolean existsOperator(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultInt(query("existsOperator", parm), "COUNT") > 0;
        }

        /**
         * �õ��쳣��½����
         * @param userID String
         * @return int
         */
        public int getAbnormaiTimes(String userID) {
                TParm result = new TParm();
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                result = query("queryAbnormaiTimes", parm);
                if (result.getErrCode() < 0) {
                        err("ERR:" + result.getErrCode() + result.getErrText()
                                        + result.getErrName());
                        return 0;
                }
                return result.getInt("ABNORMAL_TIMES", 0);
        }

        /**
         * �õ��û�����Ч����
         *
         * @param userID
         *            String �û����
         * @return Timestamp ��Ч����
         */
        public Timestamp getOperatorActiveDate(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultTimestamp(query("getOperatorActiveDate", parm),
                                "ACTIVE_DATE");
        }

        /**
         * �õ��û��Ľ�������
         *
         * @param userID
         *            String �û����
         * @return Timestamp ��Ч����
         */
        public Timestamp getOperatorEndDate(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultTimestamp(query("getOperatorEndDate", parm), "END_DATE");
        }

        /**
         * ��֤�û�ʱЧ
         *
         * @param userID
         *            String �û����
         * @return boolean true ��Ч false ��Ч
         */
        public boolean affecttedTimeOperator(String userID) {
                Timestamp nowTime = SystemTool.getInstance().getDate();
                Timestamp startTime = getOperatorActiveDate(userID);
                Timestamp endTime = getOperatorEndDate(userID);
                if (startTime == null || endTime == null)
                        return false;
                return nowTime.after(startTime) && nowTime.before(endTime);
        }
        /**
         * �����������Ч���ڷ����Ƿ���Ч==========pangben modify 20110324
         * @param result TParm
         * @return boolean
         */
        public boolean affecttedTimePwd(TParm result) {
            Timestamp oldTime = result.getTimestamp("PWD_ENDDATE",0);
            //��ǰ����
            Timestamp nowTime = SystemTool.getInstance().getDate();
            if(null==oldTime)
                return false;
            //��ǰ���ڴ����������ڷ���true ����false
            return nowTime.after(oldTime);
        }
        /**
         * ����������޸�ʱ���Ƿ��Ѿ�����������,���������������ʾ�޸��޸ĸ�������(��ǰ���ڴ����������ڷ���true ����false)==========pangben modify 20110428
         * @param result TParm
         * @param parm TParm
         * @return boolean
         */
        public boolean afterThreeMonthPwd(TParm result,TParm parm) {
            Timestamp oldTime = result.getTimestamp("PWD_STARTDATE",0);
            //========== �ڶ�������У������ʱ�������
            oldTime= StringTool.rollDate(oldTime,parm.getInt("DETECTPWDTIME",0));
           // oldTime
            //��ǰ����
            Timestamp nowTime = SystemTool.getInstance().getDate();
            if(null==oldTime)
                return false;
            //��ǰ���ڴ����������ڷ���true ����false
            return nowTime.after(oldTime);
        }
        /**
         * ͨ��userID��Ų�ѯ�û��������õ����ڣ��Լ���������ʱ��
         * @param userID String
         * @return TParm  �����������õ����ڣ��Լ���������ʱ��
         * ==========pangben modify 20110428
         */
        public TParm getUserInfo(String userID) {
            TParm parm = new TParm();
            parm.setData("USER_ID", userID);
            TParm result = query("getOperatorObateDate", parm);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return null;
            }
            return result;
        }
        /**
         * �õ��û�IP
         *
         * @return String
         */
        public String getUserIP() {
                TParm parm = new TParm();
                TParm result = TIOM_AppServer.executeAction("action.sys.LoginAction",
                                "getIP", parm);
                if (result.getErrCode() < 0) {
                        err(result.getErrCode() + " " + result.getErrText());
                        return "";
                }
                return result.getValue("IP");
        }

        /**
         * ��¼��֤
         * @param userID String �û���
         * @param password String ����
         * @param region String Ժ��
         * @param dept String ����
         * @param station String ����
         * @return TParm
         */
        public TParm login(String userID, String password, String region,
                        String dept, String station) {
            TParm result = new TParm();
            if (userID == null || password == null)
            {
                result.setErr(-1,"��������!");
                return result;
            }
            TParm parm = new TParm();
            parm.setData("USER_ID", userID);
            parm.setData("USER_PASSWORD",password);
            parm.setData("REGION", region);

            result = TIOM_AppServer.executeAction("action.sys.LoginAction",
                                                     "login", parm);
            
            
            System.out.println("-----result-------"+result);
            //
            if (result.getErrCode() < 0){ 
            	
            	 System.out.println("====error code====="+result.getErrCode());	
            	//�Ǵ�����ʾ��Ϣ
            	if(result.getErrCode()!=-3){
            		return result;
            	}
            }
            //
            //
            String IP = result.getValue("IP");
            // �����û�����
            Operator.setData(userID, region, IP, dept, station);
            //
            System.out.println("-----result11111-------"+result);
            return result;
        }

        /**
         * �õ��û���Ĭ�ϲ���
         *
         * @param userID
         *            String �û����
         * @return String
         */
        public String getMainDept(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return this.getResultString(query("getMainDept", parm), "DEPT_CODE");
        }

        /**
         * �õ��û���Ĭ�ϲ���
         *
         * @param userID
         *            String
         * @return String
         */
        public String getMainStation(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return this.getResultString(query("getMainStation", parm),
                                "STATION_CLINIC_CODE");
        }

        /**
         * �������һ�ε�¼ʱ��
         *
         * @param userID
         *            String �û����
         * @return boolean true �ɹ� false ʧ��
         */
        public boolean updateRcntLoginDate(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                TParm result = update("updateRcntLoginDate", parm);
                if (result.getErrCode() < 0) {
                        err(result.getErrCode() + " " + result.getErrText());
                        return false;
                }
                return true;
        }
        /**
         * ��һ�ε�½�޸�����֮�����PWD_STARTDATE����ֵ=======pangben modify 20110531
         * @param userID String
         * @param parm TParm
         * @return boolean
         */
        public boolean updatePwdstartDate(String userID,TParm parm) {
            TParm temp = new TParm();
            temp.setData("USER_ID", userID);
            //��ǰ����
            Timestamp nowTime = SystemTool.getInstance().getDate();
            //�޸�֮�������
            Timestamp time = StringTool.rollDate(nowTime,
                                                 parm.getInt("DETECTPWDTIME",0));
            temp.setData("PWD_ENDDATE",time);
            TParm result = update("updatePwdobateDate", temp);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return false;
            }
            return true;
        }
        /**
         * �õ��û���Ȩ����
         *
         * @param userID
         *            String
         * @return String
         */
        public String getOperatorRole(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                return getResultString(query("getOperatorRole", parm), "ROLE_ID");
        }

        public TParm selectByWhere(TParm parm) {
                TParm result = query("selectByWhere", parm);
                if (result.getErrCode() < 0)
                        err(result.getErrCode() + " " + result.getErrText());
                return result;
        }

        public static void main(String args[]) {
                // Log.DEBUG=true;
                TDebug.initServer();
                // TDebug.initServer();
                OperatorTool tool = new OperatorTool();
                TParm parm = new TParm();
                parm.setData("USER_NAME", "a%");
                parm.setData("DEPT_CODE", "302");
                //System.out.println(tool.selectByWhere(parm));

                // tool.setOperatorPassword("admin",tool.encrypt("admin"));
                // System.out.println(tool.login("admin","admin",""));
        }

        /**
         * ����ʹ����ID�õ�ʹ���ߵ����п���
         * @param userID String
         * @return TParm
         */
        public TParm getOperatorDept(String userID) {
                TParm parm = new TParm();
                parm.setData("USER_ID", userID);
                TParm result = this.query("getOperatorDept", parm);
                if (result.getErrCode() < 0) {
                        err("ERR:" + result.getErrCode() + result.getErrText()
                                        + result.getErrName());
                        return result;
                }
                return result;
        }
}
