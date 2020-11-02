package com.javahis.system.textFormat;

import com.dongyang.ui.*;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;

import jdo.sys.Operator;

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
public class TextFormatRegClinicArea
    extends TTextFormat {
	
	private String doctorCode;//
	private String admType;//
	
	public void setDrCode(String doctorCode) {
		this.doctorCode = doctorCode;
	}

	private String getDrCode() {
		return this.doctorCode;
	}
	public void setAdmType(String admType) {
		this.admType = admType;
	}

	private String getAdmtype() {
		return this.admType;
	}
    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
            //=============pangben modify 20110420 start ��Ӻű�����ɸѡ
            String operatorCodeAll = Operator.getRegion();
            String regionWhere = "";
            if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
                regionWhere = " WHERE REGION_CODE = '" + operatorCodeAll + "' ";
            }
            String sql =
                " SELECT DISTINCT A.CLINICAREA_CODE AS ID,A.CLINIC_DESC AS NAME,A.ENG_DESC AS ENNAME,A.PY1,A.PY2,A.SEQ " +
                "   FROM REG_CLINICAREA A,SYS_OPERATOR_STATION B " +
                regionWhere +
                " AND A.CLINICAREA_CODE = B.STATION_CLINIC_CODE(+) ";
                
        String doctorCode = TypeTool
		.getString(getTagValue(getDrCode()));
        if (doctorCode != null && doctorCode.length() > 0) {
        	sql+="  AND B.USER_ID= '" + doctorCode + "' ";
		}
        String admType = TypeTool
		.getString(getTagValue(this.getAdmtype()));
        if (admType != null && admType.length() > 0) {
        	sql+="  AND B.TYPE= '" + admType + "' ";
		}
        sql+="  ORDER BY A.CLINICAREA_CODE,A.SEQ ";
        //System.out.println("======1111sql1111============="+sql);
        //=============pangben modify 20110420 stop
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
        object.setValue("Tip", "����");
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
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("doctorCode", "String", "", "Left"));
        data.add(new TAttribute("admType", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("doctorCode".equalsIgnoreCase(name)) {
        	setDrCode(value);
			getTObject().setValue("doctorCode", value);
			return;
		}
        if ("admType".equalsIgnoreCase(name)) {
        	setDrCode(value);
			getTObject().setValue("admType", value);
			return;
		}
        super.setAttribute(name, value);
    }

}
