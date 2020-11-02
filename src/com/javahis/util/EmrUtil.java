package com.javahis.util;

import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.IBlock;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.dongyang.util.TList;

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
public class EmrUtil {
    private static EmrUtil instanceObject;
    public EmrUtil() {
    }

    public static synchronized EmrUtil getInstance() {
        if (instanceObject == null) {
            instanceObject = new EmrUtil();
        }
        return instanceObject;
    }
    /**
     * �õ�ģ���ļ�
     * @return String[]
     */
    public String[] getGSTemplet(String deptCode,String admType)
    {
        String sql = "";
        String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
        if(admType.equals("O")){
            sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
            "' AND OPD_FLG='Y' AND DEPT_CODE='"+deptCode+"' ORDER BY SEQ";
        }

        if(admType.equals("E")){
            sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
            "' AND EMG_FLG='Y' AND DEPT_CODE='"+deptCode+"' ORDER BY SEQ";
        }
//        System.out.println("SQL~~~~~~~~~~~~~:"+sql);

        String s[];
        TParm result = new TParm(this.getDBTool().select(sql));
            if(result.getCount("CLASS_CODE")>0){
                s = new String[] {result.getValue("TEMPLET_PATH",0),result.getValue("SUBCLASS_DESC",0),result.getValue("SUBCLASS_CODE",0)};
            }else{
                s = new String[]{
                    TIOM_FileServer.getPath("ODOEmrTempletZSPath"),
                    TConfig.getSystemValue("ODOEmrTempletZSFileName"),TConfig.getSystemValue("ODOEmrTempletZSSUBCLASSCODE")};
            }

        return s;
    }

    //zhangyong20110426
    /**
     * �õ�ģ���ļ�
     * @param deptCode String
     * @param drCode String
     * @param admType String
     * @return String[]
     */
    public String[] getGSTemplet(String deptCode, String drCode, String admType)
    {
        String sql = "";
        String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
        TParm result = new TParm();
        if (admType.equals("O")) {
            // 1.ȡ��ҽ�����Ƶ�ģ��(����)
            sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '2' "
                + " AND DEPTORDR_CODE = '" + drCode +
                "' AND B.OPD_FLG = 'Y' "
                +" AND B.CLASS_CODE = '" + classCode+"'"
                + " ORDER BY MAIN_FLG DESC ";
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result == null || result.getCount() <= 0) {
                // 2.ȡ�ÿ����ƶ���ģ��(����)
                sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                    + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                    + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                    + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                    + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '1' "
                    + " AND DEPTORDR_CODE = '" + deptCode +
                    "' AND B.OPD_FLG = 'Y' "
                    +" AND B.CLASS_CODE = '" + classCode+"'"
                    + " ORDER BY MAIN_FLG DESC ";
                //System.out.println("-----ȡ�ÿ����ƶ���ģ��(����)sql11------"+sql);
                result = new TParm(TJDODBTool.getInstance().select(sql));
            }
        }
        else if (admType.equals("E")) {
            // 1.ȡ��ҽ�����Ƶ�ģ��(����)
            sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                + " B.EMT_FILENAME  FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '2' "
                + " AND DEPTORDR_CODE = '" + drCode +
                "' AND B.EMG_FLG = 'Y' "
                +"AND B.CLASS_CODE = '" +classCode+"'"
                + " ORDER BY MAIN_FLG DESC ";
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result == null || result.getCount() <= 0) {
                // 2.ȡ�ÿ����ƶ���ģ��(����)
                sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                    + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                    + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                    + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                    + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '1' "
                   + " AND DEPTORDR_CODE = '" + deptCode +
                   "' AND B.EMG_FLG = 'Y' "
                   +" AND B.CLASS_CODE = '" + classCode+"'"
                   + " ORDER BY MAIN_FLG DESC ";
                result = new TParm(TJDODBTool.getInstance().select(sql));
            }
        }
        //System.out.println("======δ����  getGSTempletSql========="+sql);
        String s[];
        if (result.getCount("CLASS_CODE") > 0) {
            s = new String[] {
                result.getValue("TEMPLET_PATH", 0),
                result.getValue("SUBCLASS_DESC", 0),
                result.getValue("SUBCLASS_CODE", 0)};
        }
        // 3.ȡ��Ĭ�϶��Ƶ�ģ��
        else {
            s = new String[] {
                TIOM_FileServer.getPath("ODOEmrTempletZSPath"),
                TConfig.getSystemValue("ODOEmrTempletZSFileName"),
                TConfig.getSystemValue("ODOEmrTempletZSSUBCLASSCODE")};
        }
        return s;
    }


    /**
     * �õ�֮ǰ������ļ�
     * @param caseNo String
     * @return String[]
     */
    public String[] getGSFile(String caseNo)
    {
        String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
        String subclassCode = TConfig.getSystemValue("ODOEmrTempletZSSUBCLASSCODE");

        TParm emrParm = new TParm(this.getDBTool().select("SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE,EMR_PRINT_USER FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'"));
        //System.out.println("======�ѿ���  getGSTempletSql========="+"SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'");

        String dir="";
        String file="";
        String subClassCode = "";
        String printuser = ""; //��ӡ��
        if(emrParm.getCount()>0){
            dir = emrParm.getValue("FILE_PATH",0);
            file = emrParm.getValue("FILE_NAME",0);
            subClassCode = emrParm.getValue("SUBCLASS_CODE",0);
            printuser = emrParm.getValue("EMR_PRINT_USER",0);

        //���Ĭ��ģ��û���ҵ�����������ҽ������ģ�档
        }/*else{
        	TParm emrParm1 = new TParm(this.getDBTool().select("SELECT REALDEPT,REALDR_CODE,ADM_TYPE FROM REG_PATADM WHERE CASE_NO='"+caseNo+"'"));
        	String realDept = emrParm.getValue("REALDEPT",0);
        	String realDRCode = emrParm.getValue("REALDR_CODE",0);
        	String  admType = emrParm.getValue("ADM_TYPE",0);
        	//reg_patadm   realdept  realdr_code  adm_type
        	String [] s=getGSTemplet(realDept, realDRCode, admType);
        }*/

        String s[] = {dir,file,subClassCode,printuser};
        return s;
    }
    /**
     * �½�����
     * @param mrNo String
     * @param caseNo String
     * @return String[]
     */
    public TParm saveGSFile(String mrNo,String caseNo,String subclassCode,String name)
    {
        TParm result = new TParm();
        TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"'"));
        String indexStr = "01";
        String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
        //����ʱ  subclass  �� subClassCode ������һ�µ�     ������3������²������ǶԵģ�   1.Ĭ��ģ�������2.����ģ�������3�ֹ�ѡ�������
        subclassCode=TConfig.getSystemValue("ODOEmrTempletZSSUBCLASSCODE");
        //
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
        String fileName = caseNo+"_"+name+"_"+indexStr;
        String filePath = "JHW"+"\\"+caseNo.substring(0,2)+"\\"+caseNo.substring(2,4)+"\\"+mrNo;
        TParm saveParm = new TParm( this.getDBTool().update("INSERT INTO EMR_FILE_INDEX (CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH,FILE_NAME, DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, DISPOSAC_FLG,OPT_USER, OPT_DATE, OPT_TERM,CREATOR_USER) VALUES "+
                                " ('"+caseNo+"', '"+indexStr+"', '"+mrNo+"', '', '"+filePath+"', "+
                                " '"+fileName+"', '"+fileName+"', '"+classCode+"', '"+subclassCode+"', 'N',"+
                                " '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"','"+Operator.getID()+"')"));

        if(saveParm.getErrCode()<0)
            return saveParm;

        //System.out.println("-------filePath----------"+filePath);
        //System.out.println("-------fileName----------"+fileName);
        result.setData("PATH",filePath);
        result.setData("FILENAME",fileName);
        return result;
    }
    /**
     * �õ���ʽ
     * @param classCode String
     * @return String
     */
    public String getClassStyle(String classCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT CLASS_STYLE FROM EMR_CLASS WHERE CLASS_CODE='"+classCode+"'"));
        if(parm.getCount()<0){
            return "";
        }
        return parm.getValue("CLASS_STYLE",0);
    }
    /**
     * �õ�����ģ��
     * @param parm TParm (ADM_TYPE,SYSTEM_CODE,DEPT_CODE)
     * @return TParm
     */
    public TParm getEmrZSData(TParm parm){
        String admType = parm.getValue("ADM_TYPE");
        String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
        String classStyle = getClassStyle(classCode);
        String deptCode = parm.getValue("DEPT_CODE").length()==0?"":" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"'";
        String sql="";
        if(admType.equals("I")){
            sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
            "' AND IPD_FLG='Y' "+deptCode+" ORDER BY SEQ";
        }

        if(admType.equals("O")){
            sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
            "' AND OPD_FLG='Y' "+deptCode+" ORDER BY SEQ";
        }

        if(admType.equals("E")){
            sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
            "' AND EMG_FLG='Y' "+deptCode+" ORDER BY SEQ";
        }

        if(admType.equals("H")){
                    sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
                    "' AND HRM_FLG='Y' "+deptCode+" ORDER BY SEQ";
        }
        //System.out.println("�õ�����sql=========="+sql);


        TParm result = new TParm(this.getDBTool().select(sql));
        result.setData("SYSTEM_CODE",parm.getValue("SYSTEM_CODE"));
        result.setData("NODE_NAME",TConfig.getSystemValue("ODOEmrTempletZSPath"));
        result.setData("TYPE",classStyle);
        result.setData("DEPT_CODE",parm.getValue("DEPT_CODE"));
        return result;
    }
    /**
     * �õ�������ģ��
     * @param parm TParm (CASE_NO�������,TYPE���(ZS�����ֲ�ʷ),MICROFIELD(TParm)�����)
     * @return TWord
     */
    public TParm getTWord(TParm parm){
        TParm result = new TParm();
        String caseNo = parm.getValue("CASE_NO");
        String type = parm.getValue("TYPE");
        if(type.equals("ZS")){
            String classCode = TConfig.getSystemValue("ODOEmrTempletZSCLASSCODE");
            String subclassCode = TConfig.getSystemValue("ODOEmrTempletZSSUBCLASSCODE");
            TParm emrParm = new TParm(this.getDBTool().select("SELECT FILE_PATH,FILE_NAME FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'"));
            if(emrParm.getCount()>0){
                result.setData("PATH",emrParm.getValue("FILE_PATH",0));
                result.setData("FILENAME",emrParm.getValue("FILE_NAME",0));
                result.setData("TYPE",3);
                return result;
            }
            String path = TIOM_FileServer.getPath("ODOEmrTempletZSPath");
            String fileName = TIOM_FileServer.getPath("ODOEmrTempletZSFileName");
            result.setData("PATH",path);
            result.setData("FILENAME",fileName);
            result.setData("TYPE",2);
            return result;
        }
        result.setErrCode(-1);
        result.setErrText("û���ҵ�������ļ���");
        return result;
    }
    /**
     * �Ƿ����ڱ༭״̬
     * @param word TWord
     * @return TParm
     */
    public TParm isEditFlg(TWord word){
        TParm result = new TParm();
        if(word.isFileEditLock()){
            String userName = this.getOperatorName(word.getFileLockUser());
            String deptName = this.getDeptDesc(word.getFileLockDept());
            result.setErrCode(-1);
            result.setErrText("�˲�����"+word.getFileLockDate()+"��IPΪ"+word.getFileLockIP()+"����Ϊ:"+deptName+"�û�Ϊ:"+userName+"���û��༭��");
        }
        result.setErrCode(1);
        return result;
    }
    /**
     * ���ñ༭��
     * @param word TWord
     * @param falg boolean true���� false����
     */
    public TParm setFileEditLock(TWord word,boolean falg){
        TParm result = new TParm();
        //����
        String dateStr = StringTool.getString(StringTool.getTimestamp(new Date()),"yyyy��MM��dd�� HHʱmm��ss��");
        if(falg){
            //������ʾ������
            word.setMessageBoxSwitch(false);
            //�ļ�����
            word.setFileEditLock(true);
            //����ʱ��
            word.setFileLockDate(dateStr);
            //��������
            word.setFileLockDept(Operator.getDept());
            //������
            word.setFileLockUser(Operator.getID());
            //����IP
            word.setFileLockIP(Operator.getIP());
            if(!word.onSave()){
                word.setMessageBoxSwitch(true);
                result.setErrCode(-1);
                result.setErrText("�ļ��������쳣��");
                return result;
            }
            word.setMessageBoxSwitch(true);
            result.setErrCode(1);
        }else{
            //������ʾ������
            word.setMessageBoxSwitch(false);
            //�ļ�����
            word.setFileEditLock(false);
            //����ʱ��
            word.setFileLockDate("");
            //��������
            word.setFileLockDept("");
            //������
            word.setFileLockUser("");
            //����IP
            word.setFileLockIP("");
            if(!word.onSave()){
                word.setMessageBoxSwitch(true);
                result.setErrCode(-1);
                result.setErrText("�ļ��������쳣��");
                return result;
            }
            word.setMessageBoxSwitch(true);
            result.setErrCode(1);
        }
        return result;
    }
    /**
     * �õ��û���
     * @param userID String
     * @return String
     */
    public String getOperatorName(String userID){
        TParm parm = new TParm(this.getDBTool().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+userID+"'"));
        return parm.getValue("USER_NAME",0);
    }
    /**
     * �õ�����
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }

    /**
     * ���ú�
     * @param word TWord
     * @param parm TParm �����
     */
    public void setMicroField(TWord word,TParm parm){
        String names[] = parm.getNames();
        for(String temp:names){
            word.setMicroField(temp,parm.getValue(temp));
        }
    }
    /**
     * ���ñ༭Ȩ��
     * @param word TWord
     * @param falg boolean true �ɱ༭ false ���ɱ༭
     */
    public void setCanEidt(TWord word,boolean falg){
        word.setCanEdit(falg);
    }
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
     * �õ�EMR·��
     * @param parm TParm
     * @return String
     */
    public TParm getEmrFilePath(TParm parm){
    	String caseNo=parm.getValue("CASE_NO");
    	String mrCode=parm.getValue("MR_CODE");
        String admType=parm.getValue("ADM_TYPE");
        String deptCode = parm.getValue("DEPT_CODE").length()==0?"AND DEPT_CODE IS NULL":" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"'";
        //
        String seq=parm.getValue("SEQ");
        
    	TParm result = new TParm();
    	if(caseNo==null||mrCode==null){
    		return result;
    	}
    	if(caseNo.trim().length()<1||mrCode.trim().length()<1){
    		return result;
    	}
    	String sql="SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE,A.TEMPLET_PATH,A.DEPT_CODE,"+
        " B.FILE_NAME,B.FILE_PATH,B.DESIGN_NAME,B.FILE_SEQ,B.DISPOSAC_FLG "+
        " FROM EMR_TEMPLET A,EMR_FILE_INDEX B WHERE A.CLASS_CODE=B.CLASS_CODE AND A.SUBCLASS_CODE = B.SUBCLASS_CODE"+
        " AND A.SUBCLASS_CODE='"+mrCode+"' AND B.CASE_NO='"+caseNo+"' AND B.DISPOSAC_FLG='N' ORDER BY FILE_SEQ DESC";
//    	System.out.println("emr.sql1="+sql);
    	//�޸�
        result = new TParm(this.getDBTool().select(sql));
        if(result.getInt("ACTION","COUNT")>0){
        	result.setData("FLG",true);
        	TParm action = result.getRow(0);
        	action.setData("FLG",result.getData("FLG"));
        	//System.out.println(action);
            return action;
        //����
        }else{
            if("H".equals(admType)){
                sql="SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                    " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+mrCode+"' AND HRM_FLG='Y'";
                if(seq!=null&&seq.length()>0){
                	sql+=" AND SEQ='"+seq+"'";
                }else{
                	sql+=deptCode;
                }
                sql+=" ORDER BY SEQ";
            }
            if("O".equals(admType)){
                sql="SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                    " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+mrCode+"' AND OPD_FLG='Y'";
                if(seq!=null&&seq.length()>0){
                	sql+=" AND SEQ='"+seq+"'";
                }else{
                	sql+=deptCode;
                }
                sql+=" ORDER BY SEQ";
            }
            if("E".equals(admType)){
                sql="SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                    " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+mrCode+"' AND EMG_FLG='Y'";
                if(seq!=null&&seq.length()>0){
                	sql+=" AND SEQ='"+seq+"'";
                }else{
                	sql+=deptCode;
                }
                sql+=" ORDER BY SEQ";
            }
            if("I".equals(admType)){
                sql="SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                    " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+mrCode+"' AND IPD_FLG='Y'";
                    if(seq!=null&&seq.length()>0){
                    	sql+=" AND SEQ='"+seq+"'";
                    }else{
                    	sql+=deptCode;
                    }
                    sql+=" ORDER BY SEQ";
            }
            //Ĭ�Ͻ���
            if(admType==null||admType.length()==0){
                sql="SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                    " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+mrCode+"'";
                
                if(seq!=null&&seq.length()>0){
                	sql+=" AND SEQ='"+seq+"'";
                }else{
                	sql+=deptCode;
                }
                sql+=" ORDER BY SEQ";
            }
            //
            System.out.println("EMRֱ�Ӵ򿪲���="+sql);
            result = new TParm(this.getDBTool().select(sql));
            //ȡĬ�ϵ�һ�� ����ģ��
            if(result.getInt("ACTION","COUNT")<=0){
            	sql="SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+mrCode+"'";
	            sql+=" ORDER BY SEQ";
	            System.out.println("EMR  sqlĬ�ϲ���="+sql);
	            result = new TParm(this.getDBTool().select(sql));
            }            
            
            result.setData("FLG",false);
        }
        TParm action = result.getRow(0);
        action.setData("FLG",result.getData("FLG"));
        return action;
    }
    /**
     * �õ�����ʱ��
     * @param caseNo String
     * @return TParm
     */
    public TParm getReportDate(String caseNo){
        String sql = "SELECT REPORT_DATE FROM HRM_PATADM WHERE CASE_NO='"+caseNo+"'";
        TParm emrParm = new TParm(this.getDBTool().select(sql));
        if(emrParm.getErrCode()!=0){
            return null;
        }
        return emrParm.getRow(0);
    }
    /**
     * ���ݸ���MR_NO�õ�����ʷ������ַ����û�д�MR_NO���򷵻�ģ���ַ
     * @param mrNo String
     * @return file String[]
     */
    public String[] getFamilyHistoryPath(String mrNo,String deptCode,String admType){
    	String[] s=new String[]{};
    	if(StringUtil.isNullString(mrNo)){
    		return null;
    	}
        String classCode = TConfig.getSystemValue("ODOEmrTempletFAMILYHISTORY_CLASSCODE");
        String sql="SELECT MAX(CASE_NO||FILE_SEQ) SEQ, FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE MR_NO='"+mrNo+"' AND CLASS_CODE='"+classCode+"' GROUP BY FILE_PATH,FILE_NAME, SUBCLASS_CODE,FILE_SEQ ORDER BY FILE_SEQ DESC";
//        System.out.println("familyPath.sql="+sql);
        TParm emrParm = new TParm(this.getDBTool().select(sql));
        String dir="";
        String file="";
        if(emrParm.getCount()>0){
            dir = emrParm.getValue("FILE_PATH",0);
            file = emrParm.getValue("FILE_NAME",0);

            s = new String[]{dir,file,"3",emrParm.getValue("SUBCLASS_CODE",0)};
        }else{
            if(admType.equals("O")){
                sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
                    "' AND OPD_FLG='Y' AND DEPT_CODE='"+deptCode+"' ORDER BY SEQ";
            }
            if(admType.equals("E")){
                sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
                    "' AND EMG_FLG='Y' AND DEPT_CODE='"+deptCode+"' ORDER BY SEQ";
            }
            TParm result = new TParm(this.getDBTool().select(sql));
            if(result.getCount("CLASS_CODE")>0){
                s = new String[] {result.getValue("TEMPLET_PATH",0),result.getValue("SUBCLASS_DESC",0),"2",result.getValue("SUBCLASS_CODE",0)};
            }else{
                s = new String[] {
                    TIOM_FileServer.getPath("ODOEmrTempletFAMILYHISTORY_Path"),
                    TConfig.getSystemValue("ODOEmrTempletFAMILYHISTORY_FileName"), "2",TConfig.getSystemValue("ODOEmrTempletFAMILYHISTORY_SUBCLASSCODE")};
            }
        }


    	return s;
    }
    /**
     * ���ݸ����CASE_NO��MR_NO�����浽EMR_FILE_INDEX�У�������и�����¼���������
     * @param caseNo
     * @param mrNo
     * @return
     */
    public TParm getFamilyHistorySavePath(String caseNo,String mrNo,String subclassCode,String name){
        TParm result = new TParm();
        TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"'"));
        String indexStr = "01";
        String classCode = TConfig.getSystemValue("ODOEmrTempletFAMILYHISTORY_CLASSCODE");
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
        String fileName = caseNo+"_"+name+"_"+indexStr;
        String filePath = "JHW"+"\\"+caseNo.substring(0,2)+"\\"+caseNo.substring(2,4)+"\\"+mrNo;
        TParm saveParm = new TParm( this.getDBTool().update("INSERT INTO EMR_FILE_INDEX (CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH,FILE_NAME, DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, DISPOSAC_FLG,OPT_USER, OPT_DATE, OPT_TERM) VALUES "+
                                " ('"+caseNo+"', '"+indexStr+"', '"+mrNo+"', '', '"+filePath+"', "+
                                " '"+fileName+"', '"+fileName+"', '"+classCode+"', '"+subclassCode+"', 'N',"+
                                " '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"')"));

        if(saveParm.getErrCode()<0)
            return saveParm;
        result.setData("PATH",filePath);
        result.setData("FILENAME",fileName);
        return result;
    }
    /**
     * �õ��ļ�������·��
     * @param rootPath String
     * @param fileServerPath String
     * @return String
     */
    public TParm getFileServerEmrName(TParm inParm){
        TParm emrParm =new TParm();
        String emrName = "";
        String indexStr = "01";
        String templetName = inParm.getValue("EMT_FILENAME");
        String caseNo=inParm.getValue("CASE_NO");
        String yy=caseNo.substring(0,2);
        String mm=caseNo.substring(2,4);
        TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+inParm.getValue("CASE_NO")+"'"));
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
        emrName = inParm.getValue("CASE_NO")+"_"+templetName+"_"+indexStr;
        String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ss");
        emrParm.setData("FILE_SEQ",indexStr);
        emrParm.setData("FILE_NAME",emrName);
        emrParm.setData("CLASS_CODE",inParm.getValue("CLASS_CODE"));
        emrParm.setData("SUBCLASS_CODE",inParm.getValue("SUBCLASS_CODE"));
        emrParm.setData("CASE_NO",inParm.getValue("CASE_NO"));
        emrParm.setData("MR_NO",inParm.getValue("MR_NO"));
        emrParm.setData("IPD_NO","");

        emrParm.setData("FILE_PATH",yy+"\\"+mm+"\\"+inParm.getValue("MR_NO"));
        emrParm.setData("DESIGN_NAME",templetName+"("+dateStr+")");
        emrParm.setData("DISPOSAC_FLG","N");
        return emrParm;
    }
    
    /**
     * ���ݾ����ȡ�������ϵļ���ʷ����û���ļ���򿪲���ģ��
     * 
     * @param caseNo String
     * @return file String[]
     */
    public String[] getFamilyHistoryPathByCaseNo(String caseNo,String deptCode,String admType){
    	String[] s=new String[]{};
    	if(StringUtil.isNullString(caseNo)){
    		return null;
    	}
        String classCode = TConfig.getSystemValue("ODOEmrTempletFAMILYHISTORY_CLASSCODE");
        // ʹ�þ���Ŵ��没������Ϊ�˱��⵱����ʷģ�巢���仯ʱ��ȡ�þ��ļ�
        String sql="SELECT MAX(CASE_NO||FILE_SEQ) SEQ, FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' GROUP BY FILE_PATH,FILE_NAME, SUBCLASS_CODE,FILE_SEQ ORDER BY FILE_SEQ DESC";
        TParm emrParm = new TParm(this.getDBTool().select(sql));
        String dir="";
        String file="";
        if(emrParm.getCount()>0){
            dir = emrParm.getValue("FILE_PATH",0);
            file = emrParm.getValue("FILE_NAME",0);

            s = new String[]{dir,file,"3",emrParm.getValue("SUBCLASS_CODE",0)};
        }else{
            if(admType.equals("O")){
                sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
                    "' AND OPD_FLG='Y' AND DEPT_CODE='"+deptCode+"' ORDER BY SEQ";
            }
            if(admType.equals("E")){
                sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + classCode +
                    "' AND EMG_FLG='Y' AND DEPT_CODE='"+deptCode+"' ORDER BY SEQ";
            }
            TParm result = new TParm(this.getDBTool().select(sql));
            if(result.getCount("CLASS_CODE")>0){
                s = new String[] {result.getValue("TEMPLET_PATH",0),result.getValue("SUBCLASS_DESC",0),"2",result.getValue("SUBCLASS_CODE",0)};
            }else{
                s = new String[] {
                    TIOM_FileServer.getPath("ODOEmrTempletFAMILYHISTORY_Path"),
                    TConfig.getSystemValue("ODOEmrTempletFAMILYHISTORY_FileName"), "2",TConfig.getSystemValue("ODOEmrTempletFAMILYHISTORY_SUBCLASSCODE")};
            }
        }

    	return s;
    }

    /**
	 * ͨ����������ץȡ��ֵ
	 * 
	 * @param word
	 *            TWord
	 * @param macroName
	 *            String
	 * @param value
	 *            String
	 */
	public boolean setCaptureValue(TWord word, String macroName, String value) {
		if (word == null) {
			return false;
		}
		boolean isSetValue = false;
		TList components = word.getPageManager().getComponentList();
		int size = components.size();
		for (int i = 0; i < size; i++) {
			EPage ePage = (EPage) components.get(i);
			for (int j = 0; j < ePage.getComponentList().size(); j++) {
				EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
				if (ePanel != null) {
					for (int z = 0; z < ePanel.getBlockSize(); z++) {
						IBlock block = (IBlock) ePanel.get(z);
						// 9Ϊץȡ��;
						if (block != null) {
							if (block.getObjectType() == EComponent.CAPTURE_TYPE) {
								EComponent com = block;
								ECapture capture = (ECapture) com;

								if (capture.getMicroName().equals(macroName)) {
									if (capture.getCaptureType() == 0) {
										capture.setFocusLast();
										capture.clear();
										word.pasteString(value);
										isSetValue = true;
										break;
									}
								}
							}
							//�̶��ı���ֵ
							if(block.getObjectType() == EComponent.FIXED_TYPE){
								EComponent com = block;
								EFixed efix = (EFixed) com;
								if("DAY_OPE_FLG".equals(efix.getName()) && efix.getName().equals(macroName)){
									efix.setText(value);
									isSetValue = true;
									break;
								}
							}

						}
						if (isSetValue) {
							break;
						}
					}
					if (isSetValue) {
						break;
					}
				}
			}
		}
		return isSetValue;
	}
}
