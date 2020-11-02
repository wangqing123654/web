package com.javahis.system.textFormat;

import java.util.HashMap;
import java.util.Map;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * 
 * @author shibl
 * 
 */
public class TextFormatMedLisResult extends TTextFormat {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2405308819949239614L;
	/**
	 * �����
	 */
	private String caseNo;
	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * ����
	 */
	private String emrMicro;
	/**
	 * ���
	 */
	private String groupID = "SYS_YY";
	/**
	 * ����������
	 */
	private String threeMonths;
	
	
	/**
	 * 
	 * ����
	 */
	private Map map;

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		setModifySQL(true);
		this.groupID = groupID;
	}

	/**
	 * ִ��Module����
	 * 
	 * @return String
	 */
	public String getPopupMenuSQL() {
		String ID = TypeTool.getString(getTagValue(getGroupID()));
		if (ID != null && ID.length() > 0) {
			groupID = ID;
		}
		String sql = "  SELECT ID,CHN_DESC AS NAME,PY1,PY2 "
				+ "   FROM SYS_DICTIONARY  WHERE GROUP_ID='" + groupID
				+ "' ORDER BY ID ";
		return sql;
	}

	/**
	 * ִ��Module����
	 * 
	 * @return String
	 */
	public String getQuerySQL() {
		String caseNo = TypeTool.getString(getTagValue(getCaseNo()));
		String emr = TypeTool.getString(getTagValue(getEmrMicro()));
		if (emr == null || emr.length() <= 0)
			return "";
		String sql = "SELECT  TEST_VALUE  FROM MED_LIS_RPT A,MED_APPLY B,MED_LIS_MAP C "
				+ " WHERE A.CAT1_TYPE=B.CAT1_TYPE "
				+ " AND A.APPLICATION_NO=B.APPLICATION_NO "
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.SEQ_NO=B.SEQ_NO "
				+ " AND A.TESTITEM_CODE=C.LIS_ID ";
		String sql1 = " ORDER BY B.ORDER_DATE DESC";
		StringBuffer sb = new StringBuffer();
		String mrNo = TypeTool.getString(getTagValue(getMrNo()));
		boolean threeMonths = TypeTool.getBoolean(getTagValue(getThreeMonths()));
		if (mrNo != null && mrNo.length() > 0) {
			sb.append(" AND  B.MR_NO = '" + mrNo + "' ");
		}

		if (caseNo != null && caseNo.length() > 0) {
			sb.append(" AND  B.CASE_NO = '" + caseNo + "' ");
		}
		if (emr != null && emr.length() > 0) {
			sb.append(" AND  C.MAP_ID = '" + emr + "' ");
		}
		if(threeMonths){
			sb.append(" AND  (SYSDATE-B.ORDER_DATE) <=90 ");
		}
		if (sb.length() > 0)
			sql += sb.toString() + sql1;
		else
			sql = sql + sql1;
		//System.out.println("==================="+sql);
		return sql;
	}

	/**
	 * ��ʼ��������ֵ���ղ���
	 * 
	 * @return
	 */
	public Map<String, String> initMap() {
		Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECT ID,STA3_CODE FROM SYS_DICTIONARY WHERE GROUP_ID='"
				+ groupID + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			return map;
		}
		for (int i = 0; i < result.getCount(); i++) {
			TParm pamrRow = result.getRow(i);
			if (!pamrRow.getValue("STA3_CODE").equals(""))
				map.put(pamrRow.getValue("STA3_CODE"), pamrRow.getValue("ID"));
		}
		return map;
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
		object.setValue("Tip", "������");
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
	 * ��ѯ���ݷ���
	 */
	public void onQueryData() {
		String sql = this.getQuerySQL();
		map = this.initMap();
		if(sql.length()<=0)
			return;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0) {
			this.setValue("");
		} else {
			if (this.getFormatType().toUpperCase().equals("COMBO")) {
				String result="";
				if(getGroupID().equals("SYS_YY")&&parm.getValue("TEST_VALUE", 0).contains("����")){
					result="����";
				}else if(getGroupID().equals("SYS_YY")&&parm.getValue("TEST_VALUE", 0).contains("����")){
					result="����";
				}else{
					result=parm.getValue("TEST_VALUE", 0);
				}
				//System.out.println(parm.getValue("TEST_VALUE", 0)+"================="+result);
				this.setValue(map.get(result));
			} else {
				this.setValue(parm.getValue("TEST_VALUE", 0));
			}
		}
	}

	/**
	 * ������չ����
	 * 
	 * @param data
	 *            TAttributeList
	 */
	public void getEnlargeAttributes(TAttributeList data) {
		data.add(new TAttribute("FormatType", "String", "", "Left"));
		data.add(new TAttribute("Format", "String", "", "Left"));
		data.add(new TAttribute("ShowDownButton", "boolean", "N", "Center"));
		data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
		data.add(new TAttribute("PopupMenuFilter", "String", "", "Left"));
		data.add(new TAttribute("InputPopupMenu", "boolean", "Y", "Center"));
		data.add(new TAttribute("ShowColumnList", "String", "", "Left"));
		data.add(new TAttribute("LanguageMap", "String", "", "Left"));
		data.add(new TAttribute("ValueColumn", "String", "", "Left"));
		data.add(new TAttribute("mrNo", "String", "", "Left"));
		data.add(new TAttribute("caseNo", "String", "", "Left"));
		data.add(new TAttribute("emrMicro", "String", "", "Left"));
		data.add(new TAttribute("groupID", "String", "", "Left"));
		data.add(new TAttribute("threeMonths", "boolean", "N", "Center"));
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
		if ("mrNo".equalsIgnoreCase(name)) {
			setMrNo(value);
			getTObject().setValue("mrNo", value);
			return;
		}
		if ("caseNo".equalsIgnoreCase(name)) {
			setCaseNo(value);
			getTObject().setValue("caseNo", value);
			return;
		}
		if ("emrMicro".equalsIgnoreCase(name)) {
			setEmrMicro(value);
			getTObject().setValue("emrMicro", value);
			return;
		}
		if ("groupID".equalsIgnoreCase(name)) {
			setGroupID(value);
			getTObject().setValue("groupID", value);
			return;
		}
		if ("threeMonths".equalsIgnoreCase(name)) {
			setGroupID(value);
			getTObject().setValue("threeMonths", value);
			return;
		}
		super.setAttribute(name, value);
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getEmrMicro() {
		return emrMicro;
	}

	public void setEmrMicro(String emrMicro) {
		this.emrMicro = emrMicro;
	}

	public String getMrNo() {
		return mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	
	public String getThreeMonths() {
		return threeMonths;
	}

	public void setThreeMonths(String threeMonths) {
		this.threeMonths = threeMonths;
	}
}
