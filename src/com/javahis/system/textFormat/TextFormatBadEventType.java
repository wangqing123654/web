package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p> Title: �����¼�������������  </p>
 * 
 * <p>Description: �����¼�������������  </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author wanglong 20130204
 * @version 1.0
 */
public class TextFormatBadEventType extends TTextFormat {

    /**
     * �¼�����ȼ�����Ҷ�ӽڵ��������ȼ�Ҫ�ߣ�
     */
    private String typeLevel;
    
    /**
     * Ҷ�ӽڵ���
     */
    private String finalFlg;

    /**
     * �����¼�����ȼ�
     * @param typeLevel
     */
    public void setTypeLevel(String typeLevel) {
        this.typeLevel = typeLevel;
        setModifySQL(true);
    }

    /**
     * �õ��¼�����ȼ�
     * @return
     */
    public String getTypeLevel() {
        return typeLevel;
    }
    /**
     * ����Ҷ�ӽڵ���
     * @param finalFlg String
     */
    public void setFinalFlg(String finalFlg) {
        this.finalFlg = finalFlg;
    }

    /**
     * �õ�Ҷ�ӽڵ���
     * @return String
     */
    public String getFinalFlg() {
        return finalFlg;
    }

    /**
     * ִ��Module����
     * 
     * @return String
     */
    public String getPopupMenuSQL() {
        //String sql = " SELECT CATEGORY_CODE AS ID,CATEGORY_CHN_DESC AS NAME,DESCRIPTION,PY1,PY2 FROM SYS_CATEGORY WHERE RULE_TYPE = 'ACI_BADEVENT' ORDER BY CATEGORY_CODE ";
//        String sql =
//                " SELECT * FROM ( "
//                        + "      SELECT DISTINCT A.CATEGORY_CODE AS ID,A.CATEGORY_CHN_DESC AS NAME,A.DESCRIPTION, A.PY1,A.PY2,'N' DETAIL_FLG "
//                        + "        FROM SYS_CATEGORY A, SYS_CATEGORY B "
//                        + "       WHERE A.RULE_TYPE = 'ACI_BADEVENT' "
//                        + "         AND A.RULE_TYPE = B.RULE_TYPE "
//                        + "         AND A.CATEGORY_CODE <> B.CATEGORY_CODE "
//                        + "         AND A.CATEGORY_CODE = SUBSTR(B.CATEGORY_CODE,0,LENGTH(A.CATEGORY_CODE)) "
//                        + "       UNION "
//                        + "      SELECT CATEGORY_CODE AS ID,CATEGORY_CHN_DESC AS NAME,DESCRIPTION,PY1, PY2,'Y' DETAIL_FLG "
//                        + "        FROM SYS_CATEGORY "
//                        + "       WHERE RULE_TYPE = 'ACI_BADEVENT' "
//                        + "         AND CATEGORY_CODE NOT IN ( "
//                        + "                                   SELECT DISTINCT A.CATEGORY_CODE "
//                        + "                                     FROM SYS_CATEGORY A, SYS_CATEGORY B "
//                        + "                                    WHERE A.RULE_TYPE = 'ACI_BADEVENT' "
//                        + "                                      AND A.RULE_TYPE = B.RULE_TYPE "
//                        + "                                      AND A.CATEGORY_CODE <> B.CATEGORY_CODE "
//                        + "                                      AND A.CATEGORY_CODE = SUBSTR(B.CATEGORY_CODE,0,LENGTH(A.CATEGORY_CODE)) "
//                        + "                                   ) "
//                        + "     ) "
//                        + "WHERE 1=1 #"
//                        + " ORDER BY ID";
        String sql =
                "SELECT * FROM (SELECT DISTINCT A.TYPE_CODE AS ID, A.TYPE_DESC AS NAME, A.DESCRIPTION, A.PY, 'N' FINAL_FLG "
                        + "       FROM ACI_EVENTTYPE A, ACI_EVENTTYPE B "
                        + "      WHERE A.TYPE_CODE <> B.TYPE_CODE "
                        + "        AND A.TYPE_CODE = SUBSTR( B.TYPE_CODE, 0, LENGTH(A.TYPE_CODE)) "
                        + "      UNION "
                        + "     SELECT TYPE_CODE AS ID, TYPE_DESC AS NAME, DESCRIPTION, PY, 'Y' FINAL_FLG "
                        + "       FROM ACI_EVENTTYPE "
                        + "      WHERE TYPE_CODE NOT IN (SELECT DISTINCT A.TYPE_CODE "
                        + "                                FROM ACI_EVENTTYPE A, ACI_EVENTTYPE B "
                        + "                               WHERE A.TYPE_CODE <> B.TYPE_CODE "
                        + "                                 AND A.TYPE_CODE = SUBSTR( B.TYPE_CODE, 0, LENGTH(A.TYPE_CODE)))) "
                        + "WHERE 1 = 1 # "
                        + "ORDER BY ID";
        int typeLevel = TypeTool.getInt(getTagValue(getTypeLevel()));
        if (typeLevel > 0) {
            int length = levelCodeLength(typeLevel);// �¼�������볤��
            if (length > 0) {
                sql = sql.replaceFirst("#", " AND LENGTH(ID) <= " + length);
            } else {
                sql = sql.replaceFirst("#", "");
            }
        } else {
            String finalFlg = TypeTool.getString(getTagValue(getFinalFlg()));
            if (finalFlg != null && finalFlg.length() > 0) {
                sql = sql.replaceFirst("#", " AND FINAL_FLG = '" + finalFlg + "'");
            } else {
                sql = sql.replaceFirst("#", "");
            }
        } 
//        System.out.println("-------------sql---------------"+sql);
        return sql;
    }

    /**
     * �½�����ĳ�ʼֵ
     * 
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null) return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "����,100;����,100;��ע,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;DESCRIPTION,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "�����¼�����");
        object.setValue("ShowColumnList", "ID;NAME;DESCRIPTION");
    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name;Remark");
    }

    /**
     * ��ʾ��������
     * 
     * @return String
     */
    // public String getPopupMenuHeader() {
    // return "����,100;����,200";
    // }
    
   
    /**
     * ������չ����
     * 
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("typeLevel", "String", "", "Left"));
        data.add(new TAttribute("detailFlg", "String", "", "Left"));
    }

    /**
     * ��������
     * 
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("typeLevel".equalsIgnoreCase(name)) {
            setTypeLevel(value);
            getTObject().setValue("typeLevel", value);
            return;
        }
        if ("finalFlg".equalsIgnoreCase(name)) {
            setFinalFlg(value);
            getTObject().setValue("finalFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
    
    /**
     * �鿴�¼�����ȼ���Ӧ�ı��볤��
     * @param level
     * @return
     */
    public int levelCodeLength(int level) {
        String sql =
                "SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5 FROM SYS_RULE WHERE RULE_TYPE = 'ACI_BADEVENT'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0 || result.getCount() < 0) {
            return -1;
        }
        if (result.getCount() == 0) {
            return 0;
        }
        int length = 0;
        if (level == 1) {
            length = result.getInt("CLASSIFY1", 0);
        } else if (level == 2) {
            length = result.getInt("CLASSIFY1", 0) + result.getInt("CLASSIFY2", 0);
        } else if (level == 3) {
            length = result.getInt("CLASSIFY1", 0) + result.getInt("CLASSIFY2", 0)
                   + result.getInt("CLASSIFY3", 0);
        }
        return length;
    }
}
