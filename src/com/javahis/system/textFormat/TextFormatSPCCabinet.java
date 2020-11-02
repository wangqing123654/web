package com.javahis.system.textFormat;

import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 * 
 * <p>
 * Title:���������ܹ������б�
 * </p>
 * 
 * <p>
 * Description: ���������ܹ������б�
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author shendr 20131017
 * @version 1.0
 */

public class TextFormatSPCCabinet extends TTextFormat {

	/**
	 * ִ��Module����
	 * 
	 * @return String
	 */
	public String getPopupMenuSQL() {
		String sql = "SELECT A.CABINET_ID AS ID,A.CABINET_DESC AS NAME,'' AS PY1 "
				+ "FROM IND_CABINET A,IND_ORG B "
				+ "WHERE A.CABINET_TYPE='01' "
				+ "AND B.STATION_FLG='Y' "
				+ "AND A.ORG_CODE = B.ORG_CODE";
		return sql;
	}

	/**
	 * �½�����ĳ�ʼֵ
	 * 
	 * @param object
	 *            TObject
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
		object.setValue("Tip", "���ܹ���Ϣ");
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
	 * 
	 * @return String
	 */
	public String getPopupMenuHeader() {

		return "����,100;����,200";
	}

	/**
	 * ������չ����
	 * 
	 * @param data
	 *            TAttributeList
	 */
	public void getEnlargeAttributes(TAttributeList data) {
		data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
		data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
		data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
	}

	/**
	 * ��������
	 * 
	 * @param name
	 *            String ������
	 * @param value
	 *            String ����ֵ
	 */
	public void setAttribute(String name, String value) {

		super.setAttribute(name, value);
	}

}
