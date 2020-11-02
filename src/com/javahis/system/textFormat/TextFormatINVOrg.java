package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ���ʲ�����������</p>
 *
 * <p>Description: ���ʲ�����������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.29
 * @version 1.0
 */
public class TextFormatINVOrg
    extends TTextFormat {
    /**
     * �ⷿ����
     */
    private String orgType;
    /**
     * ��ʿվע��
     */
    private String stationFlg; //STATION_FLG
    /**
     * �����ⷿ
     */
    private String stockOrgCode; //STOCK_ORG_CODE
    /**
     * ����
     */
    private String regionCode; //REGION_CODE
    /**
     * ҽ������ע��
     */
    private String matFlg; //MAT_FLG
    /**
     * ��������ע��
     */
    private String invFlg; //INV_FLG

    private String operatorId; // OPERATOR_ID
    /**
     * ���ÿⷿ����
     * @param orgType String
     */
    public void setOrgType(String orgType) {

        this.orgType = orgType;
        setModifySQL(true);
    }

    /**
     * �����ⷿ
     * @param stockOrgCode String
     */
    public void setStockOrgCode(String stockOrgCode) {
        this.stockOrgCode = stockOrgCode;
        setModifySQL(true);
    }

    /**
     * ���û�ʿվע��
     * @param stationFlg String
     */
    public void setStationFlg(String stationFlg) {
        this.stationFlg = stationFlg;
        setModifySQL(true);
    }

    /**
     * ҽ������ע��
     * @param matFlg String
     */
    public void setMatFlg(String matFlg) {
        this.matFlg = matFlg;
        setModifySQL(true);
    }

    /**
     * ��������
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
        setModifySQL(true);
    }

    /**
     * ��������ע��
     * @param invFlg String
     */
    public void setInvFlg(String invFlg) {
        this.invFlg = invFlg;
        setModifySQL(true);
    }

    /**
     * �õ�ʹ���߱��
     * @param operatorId String
     */
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
        setModifySQL(true);
    }


    /**
     * �õ��ⷿ����
     * @return String
     */
    public String getOrgType() {
        return orgType;
    }

    /**
     * �����ⷿ
     * @return String
     */
    public String getStockOrgCode() {
        return stockOrgCode;
    }

    /**
     * �õ���ʿվע��
     * @return String
     */
    public String getStationFlg() {
        return stationFlg;
    }

    /**
     * ҽ������ע��
     * @return String
     */
    public String getMatFlg() {
        return matFlg;
    }

    /**
     * �õ�����
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * �õ���������ע��
     * @return String
     */
    public String getInvFlg() {
        return invFlg;
    }

    /**
     * �õ�ʹ���߱��
     * @return String
     */
    public String getOperatorId() {
        return operatorId;
    }


    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String userId = TypeTool.getString(getTagValue(getOperatorId()));
        String sql = "";
        if (userId == null || userId.length() == 0) {
            sql =
                " SELECT ORG_CODE AS ID,ORG_DESC AS NAME,ENNAME,PY1,PY2 " +
                "   FROM INV_ORG ";
            String sql1 = " ORDER BY SEQ,ORG_CODE ";

            StringBuffer sb = new StringBuffer();

            String orgType = TypeTool.getString(getTagValue(getOrgType()));
            if (orgType != null && orgType.length() > 0)
                sb.append(" ORG_TYPE = '" + orgType + "' ");

            String stationFlg = TypeTool.getString(getTagValue(getStationFlg()));
            if (stationFlg != null && stationFlg.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" STATION_FLG = '" + stationFlg + "' ");
            }

            String stockOrgCode = TypeTool.getString(getTagValue(
                getStockOrgCode()));
            if (stockOrgCode != null && stockOrgCode.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" STOCK_ORG_CODE = '" + stockOrgCode + "' ");
            }

            String regionCode = TypeTool.getString(getTagValue(getRegionCode()));
            if (regionCode != null && regionCode.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" REGION_CODE = '" + regionCode + "' ");
            }

            String matFlg = TypeTool.getString(getTagValue(getMatFlg()));
            if (matFlg != null && matFlg.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" MAT_FLG = '" + matFlg + "' ");
            }

            String invFlg = TypeTool.getString(getTagValue(getInvFlg()));
            if (invFlg != null && invFlg.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" INV_FLG = '" + invFlg + "' ");
            }
            if (sb.length() > 0)
                sql += " WHERE " + sb.toString() + sql1;
            else
                sql = sql + sql1;

        }
        else {
            sql =
                " SELECT A.ORG_CODE AS ID, A.ORG_DESC AS NAME,A.ENNAME, A.PY1, A.PY2 " +
                "   FROM INV_ORG A,SYS_OPERATOR_DEPT B " +
                " WHERE A.ORG_CODE=B.DEPT_CODE ";
            String sql1 = " ORDER BY A.SEQ, A.ORG_CODE ";

            StringBuffer sb2 = new StringBuffer();

            String orgType = TypeTool.getString(getTagValue(getOrgType()));
            if (orgType != null && orgType.length() > 0) {
                sb2.append("AND A.ORG_TYPE = '" + orgType + "' ");
            }

            String stationFlg = TypeTool.getString(getTagValue(getStationFlg()));
            if (stationFlg != null && stationFlg.length() > 0) {
                sb2.append("AND A.STATION_FLG = '" + stationFlg + "' ");
            }

            String stockOrgCode = TypeTool.getString(getTagValue(
                getStockOrgCode()));
            if (stockOrgCode != null && stockOrgCode.length() > 0) {
                sb2.append("AND A.STOCK_ORG_CODE = '" + stockOrgCode + "' ");
            }

            String regionCode = TypeTool.getString(getTagValue(getRegionCode()));
            if (regionCode != null && regionCode.length() > 0) {
                sb2.append("AND A.REGION_CODE = '" + regionCode + "' ");
            }

            String matFlg = TypeTool.getString(getTagValue(getMatFlg()));
            if (matFlg != null && matFlg.length() > 0) {
                sb2.append("AND A.MAT_FLG = '" + matFlg + "' ");
            }

            String invFlg = TypeTool.getString(getTagValue(getInvFlg()));
            if (invFlg != null && invFlg.length() > 0) {
                sb2.append("AND A.INV_FLG = '" + invFlg + "' ");
            }

            String operatorId = TypeTool.getString(getTagValue(getOperatorId()));
            if (operatorId != null && operatorId.length() > 0) {
                sb2.append("AND B.USER_ID = '" + operatorId + "' ");
            }

            if (sb2.length() > 0)
                sql = sql + sb2.toString() + sql1;
            else
                sql = sql + sql1;
        }

//        this.setPopupMenuSQL(sql);
//        super.onQuery();
//        System.out.println("sql" + sql);
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
        object.setValue("Tip", " ���ʲ���");
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
        data.add(new TAttribute("OrgType", "String", "", "Left"));
        data.add(new TAttribute("StationFlg", "String", "", "Left"));
        data.add(new TAttribute("StockOrgCode", "String", "", "Left"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        data.add(new TAttribute("MatFlg", "String", "", "Left"));
        data.add(new TAttribute("InvFlg", "String", "", "Left"));
        data.add(new TAttribute("OperatorId", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("OrgType".equalsIgnoreCase(name)) {
            setOrgType(value);
            getTObject().setValue("OrgType", value);
            return;
        }
        if ("StationFlg".equalsIgnoreCase(name)) {
            setStationFlg(value);
            getTObject().setValue("StationFlg", value);
            return;
        }
        if ("StockOrgCode".equalsIgnoreCase(name)) {
            setStockOrgCode(value);
            getTObject().setValue("StockOrgCode", value);
            return;
        }
        if ("RegionCode".equalsIgnoreCase(name)) {
            setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        if ("MatFlg".equalsIgnoreCase(name)) {
            setMatFlg(value);
            getTObject().setValue("MatFlg", value);
            return;
        }
        if ("InvFlg".equalsIgnoreCase(name)) {
            setInvFlg(value);
            getTObject().setValue("InvFlg", value);
            return;
        }
        if ("OperatorId".equalsIgnoreCase(name)) {
            setOperatorId(value);
            getTObject().setValue("OperatorId", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
