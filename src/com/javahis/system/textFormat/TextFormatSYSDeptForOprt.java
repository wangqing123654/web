package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;
import jdo.sys.Operator;

/**
 * <p>Title: ����(����ר��)��������</p>
 *
 * <p>Description: ����(����ר��)��������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.27
 * @version 1.0
 */
public class TextFormatSYSDeptForOprt
    extends TTextFormat {
    /**
     * ���ҵȼ�
     */
    private String grade;
	/**
     * ���ҹ���
     */
    private String classIfy;
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
     * ��Ա
     */
    private String userID;
	/**
     * ���Ҳ�������
     */
    private String StationCode;
    /**
     * ���ÿ��ҵȼ�
     * @param grade String
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * �õ����ҵȼ�
     * @return String
     */
    public String getGrade() {
        return grade;
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
     * ������Ա
     * @param userID String
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * �õ���Ա
     * @return String
     */
    public String getUserID() {
        return userID;
    }
    /**
	 * �õ����ҹ���
	 */
	public String getClassIfy() {
		return classIfy;
	}

	/**
	 * ���ÿ��ҹ���
	 */
	public void setClassIfy(String classIfy) {
		this.classIfy = classIfy;
	}
	 /**
	 * �õ����ղ���
	 */
	public String getStationCode() {
		return StationCode;
	}

	/**
	 * ���ö��ղ���
	 */
	public void setStationCode(String stationCode) {
		StationCode = stationCode;
	}
    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT DEPT_CODE AS ID,DEPT_ABS_DESC AS NAME,DEPT_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM SYS_DEPT " +
            "  WHERE ACTIVE_FLG='Y' AND CLASSIFY IN ('0','1') ";
        String sql1 = " ORDER BY DEPT_CODE";

        StringBuffer sb = new StringBuffer();

        String grade = TypeTool.getString(getTagValue(getGrade()));
        if (grade != null && grade.length() > 0)
            sb.append(" AND DEPT_GRADE = '" + grade + "' ");

        String finalFlg = TypeTool.getString(getTagValue(getFinalFlg()));
        if (finalFlg != null && finalFlg.length() > 0) {
            sb.append(" AND FINAL_FLG = '" + finalFlg + "' ");
        }

        String opdFitFlg = TypeTool.getString(getTagValue(getOpdFitFlg()));
        if (opdFitFlg != null && opdFitFlg.length() > 0) {
            sb.append(" AND OPD_FIT_FLG = '" + opdFitFlg + "' ");
        }

        String emgFitFlg = TypeTool.getString(getTagValue(getEmgFitFlg()));
        if (emgFitFlg != null && emgFitFlg.length() > 0) {
            sb.append(" AND EMG_FIT_FLG = '" + emgFitFlg + "' ");
        }
        String classIfyTemp = TypeTool.getString(getTagValue(getClassIfy()));
        if (classIfyTemp != null && classIfyTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" CLASSIFY = '" + classIfyTemp + "' ");
        }
        String ipdFitFlg = TypeTool.getString(getTagValue(getIpdFitFlg()));
        if (ipdFitFlg != null && ipdFitFlg.length() > 0) {
            sb.append(" AND IPD_FIT_FLG = '" + ipdFitFlg + "' ");
        }

        String hrmFitFlg = TypeTool.getString(getTagValue(getHrmFitFlg()));
        if (hrmFitFlg != null && hrmFitFlg.length() > 0) {
            sb.append(" AND HRM_FIT_FLG = '" + hrmFitFlg + "' ");
        }

        String userID = TypeTool.getString(getTagValue(getUserID()));
        if (userID != null && userID.length() > 0) {
            sb.append(
                " AND DEPT_CODE in (SELECT DEPT_CODE FROM SYS_OPERATOR_DEPT WHERE USER_ID= '" +
                userID + "' )");
        }
        String stationCode = TypeTool.getString(getTagValue(this.getStationCode()));
        if (stationCode != null && stationCode.length() > 0) {
            sb.append(
                " AND DEPT_CODE in (SELECT DEPT_CODE FROM SYS_STADEP_LIST WHERE STATION_CODE= '" +
                stationCode + "' )");
        }
        //=============pangben modify 20110420 start ��Ӻű�����ɸѡ
              String operatorCodeAll = Operator.getRegion();
              if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
//                  if (sb.length() > 0)
//                      sb.append(" AND ");
                  sb.append(" AND REGION_CODE = '" + operatorCodeAll + "' ");
              }
       //=============pangben modify 20110420 stop
        if (sb.length() > 0)
            sql += sb.toString() + sql1;
        else
            sql = sql + sql1;
//        this.setPopupMenuSQL(sql);
//        super.onQuery();
    //System.out.println("sql!!!!"+sql);
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
        object.setValue("Tip", "����(����ר��)");
        object.setValue("ShowColumnList", "ID;NAME");
    }
    public void onInit() {
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
        data.add(new TAttribute("Grade", "String", "", "Left"));
        data.add(new TAttribute("ClassIfy", "String", "", "Left"));
        data.add(new TAttribute("FinalFlg", "String", "", "Left"));
        data.add(new TAttribute("OpdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("EmgFitFlg", "String", "", "Left"));
        data.add(new TAttribute("IpdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("HrmFitFlg", "String", "", "Left"));
        data.add(new TAttribute("UserID", "String", "", "Left"));
        data.add(new TAttribute("StationCode", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("Grade".equalsIgnoreCase(name)) {
            setGrade(value);
            getTObject().setValue("Grade", value);
            return;
        }
        if ("ClassIfy".equalsIgnoreCase(name)) {
            setClassIfy(value);
            getTObject().setValue("ClassIfy", value);
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
        if ("UserID".equalsIgnoreCase(name)) {
            setUserID(value);
            getTObject().setValue("UserID", value);
            return;
        }
        if ("StationCode".equalsIgnoreCase(name)) {
            setStationCode(value);
            getTObject().setValue("StationCode", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
