package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;
import com.javahis.util.StringUtil;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>Title: ��Ա��������</p>
 *
 * <p>Description: ��Ա��������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.27
 * @version 1.0
 */
public class TextFormatSYSOperator
    extends TTextFormat {
    /**
     * ����
     */
    private String dept;
    /**
     * ְ��
     */
    private String posCode;
	/**
     * ְ�����
     */
    private String posType;
    /**
     * ���ҵȼ�
     */
    private String deptGrade;
    /**
     * ���ҹ���
     */
    private String classify;
    /**
     * ��С����
     */
    private String finalFlg;
    /**
     * ��������
     */
    private String opdFitFlg;
    /**
     * ��������
     */
    private String emgFitFlg;
    /**
     * סԺ����
     */
    private String ipdFitFlg;
    /**
     * ��������
     */
    private String hrmFitFlg;
    /**
     * ����
     * =====pangben modify 20110516
     */
    private String regionCode;
    
    /**
     * ������Ա���
     */
    private String endDateFlg ;
    
    /**
     * 
     * ���������Ա���
     * @return
     */
    public String getEndDateFlg() {
		return endDateFlg;
	}

    /**
     * 
     * ����������Ա���
     * @param endDateFlg
     */
	public void setEndDateFlg(String endDateFlg) {
		this.endDateFlg = endDateFlg;
	}
	/**
	 * ����ְ��
	 * @param posCode
	 */
    public void setPosCode(String posCode){
    	this.posCode=posCode;
    }
    public String getPosCode(){
    	return posCode;
    }
	/**
     * ���ÿ���
     * @param dept String
     */
    public void setDept(String dept) {
        this.dept = dept;
    }

    /**
     * ��������
     * @param regionCode String
     * =====pangben modify 20110516
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
    /**
     * �õ�����
     * @return String
     */
    public String getDept() {
        return dept;
    }

    /**
     * ����ְ�����
     * @param posType String
     */
    public void setPosType(String posType) {
        this.posType = posType;
    }

    /**
     * �õ�ְ�����
     * @return String
     */
    public String getPosType() {
        return posType;
    }

    /** 
     * ���ÿ��ҵȼ�
     * @param deptGrade String
     */
    public void setDeptGrade(String deptGrade) {
        this.deptGrade = deptGrade;
    }

    /**
     * �õ����ҵȼ�
     * @return String
     */
    public String getDeptGrade() {
        return deptGrade;
    }

    /**
     * ���ÿ��ҹ���
     * @param classify String
     */
    public void setClassify(String classify) {
        this.classify = classify;
    }

    /**
     * �õ����ҹ���
     * @return String
     */
    public String getClassify() {
        return classify;
    }

    /**
     * ������С����
     * @param finalFlg boolean '' ȫ�� Y ��С���� N �������¿���
     */
    public void setFinalFlg(String finalFlg) {
        this.finalFlg = finalFlg;
    }

    /**
     * �Ƿ�����С����
     * @return String
     */
    public String getFinalFlg() {
        return finalFlg;
    }

    /**
     * ������������
     * @param opdFitFlg String
     */
    public void setOpdFitFlg(String opdFitFlg) {
        this.opdFitFlg = opdFitFlg;
    }

    /**
     * �õ���������
     * @return String
     */
    public String getOpdFitFlg() {
        return opdFitFlg;
    }

    /**
     * ���ü�������
     * @param emgFitFlg String
     */
    public void setEmgFitFlg(String emgFitFlg) {
        this.emgFitFlg = emgFitFlg;
    }

    /**
     * �õ���������
     * @return String
     */
    public String getEmgFitFlg() {
        return emgFitFlg;
    }

    /**
     * ����סԺ����
     * @param ipdFitFlg String
     */
    public void setIpdFitFlg(String ipdFitFlg) {
        this.ipdFitFlg = ipdFitFlg;
    }

    /**
     * �õ�סԺ����
     * @return String
     */
    public String getIpdFitFlg() {
        return ipdFitFlg;
    }

    /**
     * ���ý�������
     * @param hrmFitFlg String
     */
    public void setHrmFitFlg(String hrmFitFlg) {
        this.hrmFitFlg = hrmFitFlg;
    }

    /**
     * �õ���������
     * @return String
     */
    public String getHrmFitFlg() {
        return hrmFitFlg;
    }
       

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT USER_ID AS ID,USER_NAME AS NAME,USER_ENG_NAME AS ENNAME,PY1,PY2 FROM SYS_OPERATOR ";
        String sql1 = " ORDER BY USER_ID ";

        StringBuffer sb = new StringBuffer();

        String dept = TypeTool.getString(getTagValue(getDept()));
        String posCode=TypeTool.getString(getTagValue(getPosCode()));
        if (dept != null && dept.length() > 0)
            sb.append(
                " USER_ID IN (SELECT USER_ID FROM SYS_OPERATOR_DEPT WHERE DEPT_CODE='" +
                dept + "' )");

        String posType = TypeTool.getString(getTagValue(getPosType()));
        if(posCode!=null){
        	posType=null;
        }
        if (posType != null && posType.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            
            
                sb.append(
                        " POS_CODE IN(SELECT POS_CODE FROM SYS_POSITION WHERE POS_TYPE='" +
                        posType + "')");
            
        }
        String test="1";
        if(test.equals(posCode)){
        	if(sb.length()>0){
        		sb.append(" AND ");
        	}
        	sb.append(
        	          " POS_CODE IN(SELECT POS_CODE FROM SYS_POSITION WHERE POS_CODE='231' "
        	          + "OR POS_CODE='232')");
        }
        String sqlWhere =
            " USER_ID IN (SELECT USER_ID FROM SYS_OPERATOR_DEPT " +
            "              WHERE DEPT_CODE IN (SELECT DEPT_CODE FROM SYS_DEPT " +
            " WHERE ";
        String sqlEnd = "))";
        StringBuffer sbIn = new StringBuffer();

        String deptGrade = TypeTool.getString(getTagValue(getDeptGrade()));
        if (deptGrade != null && deptGrade.length() > 0) {
            sbIn.append(" DEPT_GRADE = '" + deptGrade + "' ");
        }

        String classify = TypeTool.getString(getTagValue(getClassify()));
        if (classify != null && classify.length() > 0) {
            if (sbIn.length() > 0)
                sbIn.append(" AND ");
            sbIn.append(" CLASSIFY = '" + classify + "' ");
        }

        String finalFlg = TypeTool.getString(getTagValue(getFinalFlg()));
        if (finalFlg != null && finalFlg.length() > 0) {
            if (sbIn.length() > 0)
                sbIn.append(" AND ");
            sbIn.append(" FINAL_FLG = '" + finalFlg + "' ");
        }

        String opdFitFlg = TypeTool.getString(getTagValue(
            getOpdFitFlg()));
        if (opdFitFlg != null && opdFitFlg.length() > 0) {
            if (sbIn.length() > 0)
                sbIn.append(" AND ");
            sbIn.append(" OPD_FIT_FLG = '" + opdFitFlg + "' ");
        }

        String emgFitFlg = TypeTool.getString(getTagValue(getEmgFitFlg()));
        if (emgFitFlg != null && emgFitFlg.length() > 0) {
            if (sbIn.length() > 0)
                sbIn.append(" AND ");
            sbIn.append(" EMG_FIT_FLG = '" + emgFitFlg + "' ");
        }

        String ipdFitFlg = TypeTool.getString(getTagValue(getIpdFitFlg()));
        if (ipdFitFlg != null && ipdFitFlg.length() > 0) {
            if (sbIn.length() > 0)
                sbIn.append(" AND ");
            sbIn.append(" IPD_FIT_FLG = '" + ipdFitFlg + "' ");
        }

        String hrmFitFlg = TypeTool.getString(getTagValue(getHrmFitFlg()));
        if (hrmFitFlg != null && hrmFitFlg.length() > 0) {
            if (sbIn.length() > 0)
                sbIn.append(" AND ");
            sbIn.append(" HRM_FIT_FLG = '" + hrmFitFlg + "' ");
        }
        if (sbIn.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sqlWhere += sbIn.toString() + sqlEnd;
            sb.append(sqlWhere);
        }
        //=============pangben modify 20110420 start ��Ӻű�����ɸѡ
              String operatorCodeAll = Operator.getRegion();
              if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
                  if (sb.length() > 0)
                      sb.append(" AND ");
                  sb.append(" REGION_CODE = '" + operatorCodeAll + "' ");
              }
      //=============pangben modify 20110420 stop
         
              //=========lim modify 20120330 start ���������Ա���
              String endDate = TypeTool.getString(getTagValue(this.getEndDateFlg()));
              if(endDate!=null && "1".equals(endDate)){
            	  String nowDate = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss") ;
                  if (sb.length() > 0)
                      sb.append(" AND ");    
                  sb.append(" TO_CHAR(END_DATE,'yyyyMMddHH24miss')>'"+nowDate+"' AND TO_CHAR(ACTIVE_DATE,'yyyyMMddHH24miss')<='"+nowDate+"'") ;
              }
              //=========lim modify 20120330 end 
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
//        this.setPopupMenuSQL(sql);
//        super.onQuery();
        return sql;
    }

    /**
     * �½�����ĳ�ʼֵ
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "����,100;����,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "��Ա");
        object.setValue("ShowColumnList", "ID;NAME");
    }
    public void onInit()
    {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
    }

    /**
     * ��ʾ��������
     * @return String
     */
    public String getPopupMenuHeader() {
        return "����,100;����,200";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("Dept", "String", "", "Left"));
        data.add(new TAttribute("PosType", "String", "", "Left"));
        data.add(new TAttribute("PosCode","String","","Left"));
        data.add(new TAttribute("DeptGrade", "String", "", "Left"));
        data.add(new TAttribute("Classify", "String", "", "Left"));
        data.add(new TAttribute("FinalFlg", "String", "", "Left"));
        data.add(new TAttribute("OpdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("EmgFitFlg", "String", "", "Left"));
        data.add(new TAttribute("IpdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("HrmFitFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        //==========pangben modify 20110516
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        //==========lim modify 20120330
        data.add(new TAttribute("EndDateFlg", "String", "","Left")) ;
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        //=====pangben modify 20110516 start
        if ("RegionCode".equalsIgnoreCase(name)) {
            setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        //=====pangben modify 20110516 stop
        
        //=====lim modify 20120330 start
        if("EndDateFlg".equalsIgnoreCase(name)) {
        	this.setEndDateFlg(value) ;
        	getTObject().setValue("EndDateFlg", value) ;
        }
        //=====lim modify 20120330 end
        if ("Dept".equalsIgnoreCase(name)) {
            setDept(value);
            getTObject().setValue("Dept", value);
            return;
        }
        if ("PosCode".equalsIgnoreCase(name)) {
            setPosCode(value);
            getTObject().setValue("PosCode", value);
            return;
        }
        if ("PosType".equalsIgnoreCase(name)) {
            setPosType(value);
            getTObject().setValue("PosType", value);
            return;
        }
        if ("DeptGrade".equalsIgnoreCase(name)) {
            setDeptGrade(value);
            getTObject().setValue("DeptGrade", value);
            return;
        }
        if ("Classify".equalsIgnoreCase(name)) {
            setClassify(value);
            getTObject().setValue("Classify", value);
            return;
        }
        if ("FinalFlg".equalsIgnoreCase(name)) {
            setFinalFlg(value);
            getTObject().setValue("FinalFlg", value);
            return;
        }
        if ("OpdFitFlg".equalsIgnoreCase(name)) {
            setOpdFitFlg(value);
            getTObject().setValue("OpdFitFlg", value);
            return;
        }
        if ("EmgFitFlg".equalsIgnoreCase(name)) {
            setEmgFitFlg(value);
            getTObject().setValue("EmgFitFlg", value);
            return;
        }
        if ("IpdFitFlg".equalsIgnoreCase(name)) {
            setIpdFitFlg(value);
            getTObject().setValue("IpdFitFlg", value);
            return;
        }
        if ("HrmFitFlg".equalsIgnoreCase(name)) {
            setHrmFitFlg(value);
            getTObject().setValue("HrmFitFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
