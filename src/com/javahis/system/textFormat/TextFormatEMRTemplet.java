package com.javahis.system.textFormat;

import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: �ṹ���������뵥��������</p>
 *
 * <p>Description: �ṹ���������뵥��������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatEMRTemplet
    extends TTextFormat {
    /**
     * ģ�����
     */
    private String classCode;
    /**
     * ���Ҵ���
     */
    private String deptCode; //DEPT_CODE
    /**
     * ��������
     */
    private String opdFlg; //OPD_FLG
    /**
     * סԺע��
     */
    private String ipdFlg; //IPD_FLG
    /**
     * ����ע��
     */
    private String emgFlg; //EMG_FLG
    /**
     * ����ע��
     */
    private String hrmFlg; //HRM_FLG
    
    
    /**
     * ���ע��
     */
    private String seqFlg; //HRM_FLG
    
    /**
     * �ӷ���
     */
    private String subClassCode;
    
    /**
     * ����ģ�����
     * @param classCode String
     */
    public void setClassCode(String classCode) {

        this.classCode = classCode;
        setModifySQL(true);
    }

    /**
     * ������������ע��
     * @param opdFlg String
     */
    public void setOpdFlg(String opdFlg) {
        this.opdFlg = opdFlg;
        setModifySQL(true);
    }

    /**
     * ���ÿ��Ҵ���
     * @param deptCode String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
        setModifySQL(true);
    }

    /**
     * ���ü���ע��
     * @param emgFlg String
     */
    public void setEmgFlg(String emgFlg) {
        this.emgFlg = emgFlg;
        setModifySQL(true);
    }

    /**
     * ����סԺע��
     * @param ipdFlg String
     */
    public void setIpdFlg(String ipdFlg) {
        this.ipdFlg = ipdFlg;
        setModifySQL(true);
    }

    /**
     * ���ý���ע��
     * @param hrmFlg String
     */
    public void setHrmFlg(String hrmFlg) {
        this.hrmFlg = hrmFlg;
        setModifySQL(true);
    }

    /**
     * ģ�����
     * @return String
     */
    public String getClassCode() {
        return classCode;
    }

    /**
     * �õ����ض�ע��
     * @return String
     */
    public String getOpdFlg() {
        return opdFlg;
    }

    /**
     * �õ����Ҵ���
     * @return String
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getEmgFlg() {
        return emgFlg;
    }

    /**
     * �õ�סԺע��
     * @return String
     */
    public String getIpdFlg() {
        return ipdFlg;
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getHrmFlg() {
        return hrmFlg;
    }

    public String getSubClassCode() {
		return subClassCode;
	}

	public void setSubClassCode(String subClassCode) {
		this.subClassCode = subClassCode;
		setModifySQL(true);
	}

	public String getSeqFlg() {
		return seqFlg;
	}

	public void setSeqFlg(String seqFlg) {
		this.seqFlg = seqFlg;
		setModifySQL(true);
	}

	/**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
    	
    	String seqFlg = TypeTool.getString(getTagValue(getSeqFlg()));
    	//System.out.println("---seqFlg---"+seqFlg);
    	
    	String sql =" SELECT SUBCLASS_CODE AS ID,SUBCLASS_DESC AS NAME,ENNAME,PY1,SEQ" +
        "   FROM EMR_TEMPLET ";
    	 if (seqFlg != null && seqFlg.length() > 0) {
    		 sql =
    	            " SELECT SUBCLASS_CODE||'_'||SEQ AS ID,SUBCLASS_DESC AS NAME,ENNAME,PY1,SEQ" +
    	            "   FROM EMR_TEMPLET ";
    	 }
    	
        
        String sql1 = " ORDER BY SUBCLASS_CODE,SEQ ";
        StringBuffer sb = new StringBuffer();

        String classCode = TypeTool.getString(getTagValue(getClassCode()));
        if (classCode != null && classCode.length() > 0)
            sb.append(" CLASS_CODE = '" + classCode + "' ");

        String deptCode = TypeTool.getString(getTagValue(getDeptCode()));
        if (deptCode != null && deptCode.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DEPT_CODE = '" + deptCode + "' ");
        }

        String opdFlg = TypeTool.getString(getTagValue(getOpdFlg()));
        if (opdFlg != null && opdFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" OPD_FLG = '" + opdFlg + "' ");
        }

        String ipdFlg = TypeTool.getString(getTagValue(getIpdFlg()));
        if (ipdFlg != null && ipdFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" IPD_FLG = '" + ipdFlg + "' ");
        }

        String emgFlg = TypeTool.getString(getTagValue(
            getEmgFlg()));
        if (emgFlg != null && emgFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" EMG_FLG = '" + emgFlg + "' ");
        }

        String hrmFlg = TypeTool.getString(getTagValue(getHrmFlg()));
        if (hrmFlg != null && hrmFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" HRM_FLG = '" + hrmFlg + "' ");
        }
        
        String subClassCode = TypeTool.getString(getTagValue(getSubClassCode()));
        if (subClassCode != null && subClassCode.length() > 0){
        	if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" SUBCLASS_CODE = '" + subClassCode + "' ");
        }
        
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
        
        //System.out.println("-------SQL------"+sql);
     //  this.setPopupMenuSQL(sql);
//        super.onQuery();
//    System.out.println("sql"+sql);
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
        object.setValue("PopupMenuHeader", "����,130;����,200");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "�ṹ���������뵥");
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

        return "����,130;����,200";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("ClassCode", "String", "", "Left"));
        data.add(new TAttribute("DeptCode", "String", "", "Left"));
        data.add(new TAttribute("OpdFlg", "String", "", "Left"));
        data.add(new TAttribute("IpdFlg", "String", "", "Left"));
        data.add(new TAttribute("EmgFlg", "String", "", "Left"));
        data.add(new TAttribute("TransplantFlg", "String", "", "Left"));
        data.add(new TAttribute("HrmFlg", "boolean", "N", "Center"));
        data.add(new TAttribute("SubClassCode", "String", "", "Left"));
        data.add(new TAttribute("SeqFlg", "boolean", "N", "Center"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("ClassCode".equalsIgnoreCase(name)) {
            setClassCode(value);
            getTObject().setValue("ClassCode", value);
            return;
        }
        if ("DeptCode".equalsIgnoreCase(name)) {
            setDeptCode(value);
            getTObject().setValue("DeptCode", value);
            return;
        }
        if ("OpdFlg".equalsIgnoreCase(name)) {
            setOpdFlg(value);
            getTObject().setValue("OpdFlg", value);
            return;
        }
        if ("IpdFlg".equalsIgnoreCase(name)) {
            setIpdFlg(value);
            getTObject().setValue("IpdFlg", value);
            return;
        }
        if ("EmgFlg".equalsIgnoreCase(name)) {
            setEmgFlg(value);
            getTObject().setValue("EmgFlg", value);
            return;
        }
        
        if ("SubClassCode".equalsIgnoreCase(name)) {
            setSubClassCode(value);
            getTObject().setValue("SubClassCode", value);
            return;
        }
        
        if ("SeqFlg".equalsIgnoreCase(name)) {
            setEmgFlg(value);
            getTObject().setValue("SeqFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
