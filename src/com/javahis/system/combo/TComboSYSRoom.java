package com.javahis.system.combo;

import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;

/**
 *
 * <p>Title: ���������б�</p>
 *
 * <p>Description: ���������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSRoom extends TComboBox {
    /**
     * ����
     */
    private String stationCode;
    /**
     * ����
     */
    private String regionCode;
    /**
     * �Ա����
     */
    private String sexLimitFlg;
    /**
     * ��ɫ����
     */
    private int redSign;
    /**
     * ��ɫ����
     */
    private int yellowSign;
    /**
     * ���ò���
     * @param stationCode String
     */
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    /**
     * �õ�����
     * @return String
     */
    public String getStationCode() {
        return stationCode;
    }

    /**
     * ��������
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * �õ�����
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * �����Ա����
     * @param sexLimitFlg String
     */
    public void setSexLimitFlg(String sexLimitFlg) {
        this.sexLimitFlg = sexLimitFlg;
    }

    /**
     * �õ��Ա����
     * @return String
     */
    public String getSexLimitFlg() {
        return sexLimitFlg;
    }

    /**
     * ���ú�ɫ����
     * @param redSign int
     */
    public void setRedSign(int redSign) {
        this.redSign = redSign;
    }

    /**
     * �õ���ɫ����
     * @return int
     */
    public int getRedSign() {
        return redSign;
    }

    /**
     * ���û�ɫ����
     * @param yellowSign int
     */
    public void setYellowSign(int yellowSign) {
        this.yellowSign = yellowSign;
    }

    /**
     * �õ���ɫ����
     * @return int
     */
    public int getYellowSign() {
        return yellowSign;
    }

    /**
     * ִ��Module����
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("STATION_CODE", getTagValue(getStationCode()));
        parm.setDataN("REGION_CODE", getTagValue(getRegionCode()));
        parm.setDataN("SEX_LIMIT_FLG", getTagValue(getSexLimitFlg()));
        parm.setDataN("RED_SIGN", getRedSign());
        parm.setDataN("YELLOW_SIGN", getYellowSign());
        String optRegion = Operator.getRegion();
        if(!"".equals(optRegion))
            parm.setData("REGION_CODE_ALL",optRegion);
        setModuleParm(parm);
        super.onQuery();
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
        object.setValue("Text", "TButton");
        object.setValue("showID", "Y");
        object.setValue("showName", "Y");
        object.setValue("showText", "N");
        object.setValue("showValue", "N");
        object.setValue("showPy1", "Y");
        object.setValue("showPy2", "Y");
        object.setValue("Editable", "Y");
        object.setValue("Tip", "����");
        object.setValue("TableShowList", "id,name");
    }

    public String getModuleName() {
        return "sys\\SYSRoomModule.x";
    }

    public String getModuleMethodName() {
        return "initroomcode";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("StationCode", "String", "", "Left"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        data.add(new TAttribute("SexLimitFlg", "String", "", "Left"));
        data.add(new TAttribute("RedSign", "int", "", "Left"));
        data.add(new TAttribute("YellowSign", "int", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("StationCode".equalsIgnoreCase(name)) {
            setStationCode(value);
            getTObject().setValue("StationCode", value);
            return;
        }
        if ("RegionCode".equalsIgnoreCase(name)) {
            setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        if ("SexLimitFlg".equalsIgnoreCase(name)) {
            setSexLimitFlg(value);
            getTObject().setValue("SexLimitFlg", value);
            return;
        }
        if ("RedSign".equalsIgnoreCase(name)) {
            setRedSign(TypeTool.getInt(value));
            getTObject().setValue("RedSign", value);
            return;
        }
        if ("YellowSign".equalsIgnoreCase(name)) {
            setYellowSign(TypeTool.getInt(value));
            getTObject().setValue("YellowSign", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
