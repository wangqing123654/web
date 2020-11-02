package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfig;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ���������������</p>
 *
 * <p>Description: ���������������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.01.18
 * @version 1.0
 */
public class TextFormatREGQuegroup
    extends TTextFormat {
    /**
     * VIPע��
     */
    private String vipFlg;
    
    /**
     * ʱ��
     */
    private String sessionCode;
    private String admType;
    
    public String getAdmType() {
		return admType;
	}
	public void setAdmType(String admType) {
		this.admType = admType;
	}
	/**
     * �õ�ʱ��
     * @return String
     */
    public String getSessionCode() {
		return sessionCode;
	}
    /**
     * ����ʱ��
     * @return String
     */
	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}

	/**
     * �õ�VIPע��
     * @return String
     */
    public String getVipFlg() {
        return vipFlg;
    }

    /**
     * ����VIPע��
     * @param vipFlg String
     */
    public void setVipFlg(String vipFlg) {
        this.vipFlg = vipFlg;
    }

    /**
     * ִ��Module���� 
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT QUEGROUP_CODE AS ID,QUEGROUP_DESC AS NAME ,ENNAME,PY1,PY2 " +
            "   FROM REG_QUEGROUP ";

        String sqlEnd = " ORDER BY QUEGROUP_CODE,SEQ ";
        StringBuffer sb = new StringBuffer();
        String vipFlg = TypeTool.getString(getTagValue(getVipFlg()));
        String admType = TypeTool.getString(getTagValue(getAdmType()));
        String sessionCode = TypeTool.getString(getTagValue(getSessionCode()));
        if (vipFlg != null && vipFlg.length() > 0)
            sb.append(" AND VIP_FLG = '" + vipFlg + "' ");
        
        if(admType != null && admType.length() > 0 ){
        	sb.append(" AND ADM_TYPE = '" + admType + "' ");
        	if (sessionCode != null && sessionCode.length() > 0 && "O".equals(admType)){
        		sb.append(" AND SESSION_CODE = '" + sessionCode + "' ");
            	if(!StringTool.getBoolean(TConfig.getSystemValue("crm.switch"))){
        			sb.append(" OR (SESSION_CODE IS NULL AND ADM_TYPE='O')");
        		}
        	}            
        }
        
        if (sb.length() > 0)
            sql += " WHERE 1=1 " + sb.toString() + sqlEnd;
        else
            sql = sql + sqlEnd;
        
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
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "�������");
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
        data.add(new TAttribute("VipFlg", "String", "", "Left"));
        data.add(new TAttribute("SessionCode", "String", "", "Left"));
        data.add(new TAttribute("AdmType", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("VipFlg".equalsIgnoreCase(name)) {
            setVipFlg(value);
            getTObject().setValue("VipFlg", value);
            return;
        }
        if ("AdmType".equalsIgnoreCase(name)) {
            setVipFlg(value);
            getTObject().setValue("AdmType", value);
            return;
        }
        
        if ("SessionCode".equalsIgnoreCase(name)) {
            setVipFlg(value);
            getTObject().setValue("SessionCode", value);
            return;
        }
        super.setAttribute(name, value);
    }


}
