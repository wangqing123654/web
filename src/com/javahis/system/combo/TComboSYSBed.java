package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.data.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import jdo.sys.Operator;
/**
 *
 * <p>Title: ��λ�����б�</p>
 *
 * <p>Description: ��λ�����б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSBed extends TComboBox{
    /**
     * ����
     */
    private String roomCode;//ROOM_CODE
    /**
     * ����/��ʿվ
     */
    private String stationCode;//STATION_CODE
    /**
     * �������
     */
    private String regionCode;//REGION_CODE
    /**
     * ��λ�ȼ�
     */
    private String bedClassCode;//BED_CLASS_CODE
    /**
     * ��λ���
     */
    private String bedTypeCode;//BED_TYPE_CODE
    /**
     * ����ע��
     */
    private String activeFlg;//ACTIVE_FLG
    /**
     * ԤԼע��
     */
    private String apptFlg;//APPT_FLG
    /**
     * ռ��ע��
     */
    private String alloFlg;//ALLO_FLG
    /**
     * ����ע��
     */
    private String bedOccuFlg;//BED_OCCU_FLG
    /**
     * ����ע��
     */
    private String reserveBedFlg;//RESERVE_BED_FLG
    /**
     * �Ա����
     */
    private String sexCode;//SEX_CODE
    /**
     * ռ���ʼ���
     */
    private String occuRateFlg;//OCCU_RATE_FLG
    /**
     * ����ҽʦԤԼ
     */
    private String drApproveFlg;//DR_APPROVE_FLG
    /**
     * Ӥ����ע��
     */
    private String babyBedFlg;//BABY_BED_FLG
    /**
     * ��Ƶ����ϵͳ
     */
    private String htlFlg;//HTL_FLG
    /**
     * �ż�ס��
     */
    private String admType;//ADM_TYPE
    /**
     * ��λ״̬
     */
    private String bedStatus;//BED_STATUS

    /**
     * ��ʼ��
     */
    public void onInit()
    {
        super.onInit();
    }
    /**
    * ִ��Module����
    */
   public void onQuery()
   {
       TParm parm = new TParm();
       parm.setDataN("ROOM_CODE",getTagValue(getRoomCode()));
       parm.setDataN("STATION_CODE",getTagValue(getStationCode()));
       parm.setDataN("REGION_CODE",getTagValue(getRegionCode()));
       parm.setDataN("BED_CLASS_CODE",getTagValue(getBedClassCode()));
       parm.setDataN("BED_TYPE_CODE",getTagValue(getBedTypeCode()));
       parm.setDataN("ACTIVE_FLG",getTagValue(getActiveFlg()));
       parm.setDataN("APPT_FLG",getTagValue(getApptFlg()));
       parm.setDataN("ALLO_FLG",getTagValue(getAlloFlg()));
       parm.setDataN("BED_OCCU_FLG",getTagValue(getBedOccuFlg()));
       parm.setDataN("RESERVE_BED_FLG",getTagValue(getReserveBedFlg()));
       parm.setDataN("SEX_CODE",getTagValue(getSexCode()));
       parm.setDataN("OCCU_RATE_FLG",getTagValue(getOccuRateFlg()));
       parm.setDataN("DR_APPROVE_FLG",getTagValue(getDrApproveFlg()));
       parm.setDataN("BABY_BED_FLG",getTagValue(getBabyBedFlg()));
       parm.setDataN("HTL_FLG",getTagValue(getHtlFlg()));
       parm.setDataN("ADM_TYPE",getTagValue(getAdmType()));
       parm.setDataN("BED_STATUS",getTagValue(getBedStatus()));
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
   public void createInit(TObject object)
   {
       if(object == null)
           return;
       object.setValue("Width","81");
       object.setValue("Height","23");
       object.setValue("Text","TButton");
       object.setValue("showID","Y");
       object.setValue("showName","Y");
       object.setValue("showText","N");
       object.setValue("showValue","N");
       object.setValue("showPy1","Y");
       object.setValue("showPy2","Y");
       object.setValue("Editable","Y");
       object.setValue("Tip","��λ");
       object.setValue("TableShowList","id,name");
       object.setValue("ModuleParmString","");
       object.setValue("ModuleParmTag","");
   }
   public String getModuleName()
   {
       return "sys\\SYSBedModule.x";
   }
   public String getModuleMethodName()
   {
       return "initSYSBed";
   }
   public String getParmMap()
   {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
   }
   /**
    * ������չ����
    * @param data TAttributeList
    */
   public void getEnlargeAttributes(TAttributeList data){
       data.add(new TAttribute("RoomCode","String","","Left"));
       data.add(new TAttribute("StationCode","String","","Left"));
       data.add(new TAttribute("RegionCode","String","","Left"));
       data.add(new TAttribute("BedClassCode","String","","Left"));
       data.add(new TAttribute("BedTypeCode","String","","Left"));
       data.add(new TAttribute("ActiveFlg","String","","Left"));
       data.add(new TAttribute("ApptFlg","String","","Left"));
       data.add(new TAttribute("AlloFlg","String","","Left"));
       data.add(new TAttribute("BedOccuFlg","String","","Left"));
       data.add(new TAttribute("ReserveBedFlg","String","","Left"));
       data.add(new TAttribute("SexCode","String","","Left"));
       data.add(new TAttribute("OccuRateFlg","String","","Left"));
       data.add(new TAttribute("DrApproveFlg","String","","Left"));
       data.add(new TAttribute("BabyBedFlg","String","","Left"));
       data.add(new TAttribute("HtlFlg","String","","Left"));
       data.add(new TAttribute("AdmType","String","","Left"));
       data.add(new TAttribute("BedStatus","String","","Left"));
}
   /**
    * ��������
    * @param name String ������
    * @param value String ����ֵ
    */
   public void setAttribute(String name,String value)
   {
       if("RoomCode".equalsIgnoreCase(name))
       {
           setRoomCode(value);
           getTObject().setValue("RoomCode",value);
           return;
       }
       if("StationCode".equalsIgnoreCase(name))
       {
           setStationCode(value);
           getTObject().setValue("StationCode",value);
           return;
       }
       if("RegionCode".equalsIgnoreCase(name))
       {
           setRegionCode(value);
           getTObject().setValue("RegionCode",value);
           return;
       }
       if("BedClassCode".equalsIgnoreCase(name))
       {
           setBedClassCode(value);
           getTObject().setValue("BedClassCode",value);
           return;
       }
       if("BedTypeCode".equalsIgnoreCase(name))
       {
           setBedTypeCode(value);
           getTObject().setValue("BedTypeCode",value);
           return;
       }
       if("ActiveFlg".equalsIgnoreCase(name))
       {
           setActiveFlg(value);
           getTObject().setValue("ActiveFlg",value);
           return;
       }
       if("ApptFlg".equalsIgnoreCase(name))
       {
           setApptFlg(value);
           getTObject().setValue("ApptFlg",value);
           return;
       }
       if("AlloFlg".equalsIgnoreCase(name))
       {
           setAlloFlg(value);
           getTObject().setValue("AlloFlg",value);
           return;
       }
       if("BedOccuFlg".equalsIgnoreCase(name))
       {
           setBedOccuFlg(value);
           getTObject().setValue("BedOccuFlg",value);
           return;
       }
       if("ReserveBedFlg".equalsIgnoreCase(name))
       {
           setReserveBedFlg(value);
           getTObject().setValue("ReserveBedFlg",value);
           return;
       }
       if("SexCode".equalsIgnoreCase(name))
       {
           setSexCode(value);
           getTObject().setValue("SexCode",value);
           return;
       }
       if("OccuRateFlg".equalsIgnoreCase(name))
       {
           setOccuRateFlg(value);
           getTObject().setValue("OccuRateFlg",value);
           return;
       }
       if("DrApproveFlg".equalsIgnoreCase(name))
       {
           setDrApproveFlg(value);
           getTObject().setValue("DrApproveFlg",value);
           return;
       }
       if("BabyBedFlg".equalsIgnoreCase(name))
       {
           setBabyBedFlg(value);
           getTObject().setValue("BabyBedFlg",value);
           return;
       }
       if("HtlFlg".equalsIgnoreCase(name))
       {
           setHtlFlg(value);
           getTObject().setValue("HtlFlg",value);
           return;
       }
       if("AdmType".equalsIgnoreCase(name))
       {
           setAdmType(value);
           getTObject().setValue("AdmType",value);
           return;
       }
       if("BedStatus".equalsIgnoreCase(name))
       {
           setBedStatus(value);
           getTObject().setValue("BedStatus",value);
           return;
       }
       super.setAttribute(name,value);
   }
   /**
    * �õ�����
    * @return String
    */
   public String getRoomCode() {
        return roomCode;
    }
    /**
     * �õ�ԤԼע��
     * @return String
     */
    public String getApptFlg() {
        return apptFlg;
    }
    /**
     * �õ�Ӥ����ע��
     * @return String
     */
    public String getBabyBedFlg() {
        return babyBedFlg;
    }
    /**
     * �õ������ȼ�
     * @return String
     */
    public String getBedClassCode() {
        return bedClassCode;
    }
    /**
     * �õ�����ע��
     * @return String
     */
    public String getBedOccuFlg() {
        return bedOccuFlg;
    }
    /**
     * �õ���λ״̬
     * @return String
     */
    public String getBedStatus() {
        return bedStatus;
    }
    /**
     * �õ���λ���
     * @return String
     */
    public String getBedTypeCode() {
        return bedTypeCode;
    }
    /**
     * �õ�����ҽʦԤԼ
     * @return String
     */
    public String getDrApproveFlg() {
        return drApproveFlg;
    }
    /**
     * �õ���Ƶ����ϵͳ
     * @return String
     */
    public String getHtlFlg() {
        return htlFlg;
    }
    /**
     * �õ�ռ���ʼ���
     * @return String
     */
    public String getOccuRateFlg() {
        return occuRateFlg;
    }
    /**
     * �õ��������
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }
    /**
     * �õ�������
     * @return String
     */
    public String getReserveBedFlg() {
        return reserveBedFlg;
    }
    /**
     * �õ��Ա����
     * @return String
     */
    public String getSexCode() {
        return sexCode;
    }
    /**
     * �õ�����/��ʿվ
     * @return String
     */
    public String getStationCode() {
        return stationCode;
    }
    /**
     * �õ�����ע��
     * @return String
     */
    public String getActiveFlg() {
        return activeFlg;
    }
    /**
     * �õ��ż�ס��
     * @return String
     */
    public String getAdmType() {
        return admType;
    }
    /**
     * �õ�ռ��ע��
     * @return String
     */
    public String getAlloFlg() {
        return alloFlg;
    }

    /**
     * ���ò���
     * @param roomCode String
     */
    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
    /**
     * ����ռ���ʼ���
     * @param occuRateFlg String
     */
    public void setOccuRateFlg(String occuRateFlg) {
        this.occuRateFlg = occuRateFlg;
    }
    /**
     * ������Ƶ����ϵͳ
     * @param htlFlg String
     */
    public void setHtlFlg(String htlFlg) {
        this.htlFlg = htlFlg;
    }
    /**
     * ���ÿ���ҽʦԤԼ
     * @param drApproveFlg String
     */
    public void setDrApproveFlg(String drApproveFlg) {
        this.drApproveFlg = drApproveFlg;
    }
    /**
     * ���ô�λ���
     * @param bedTypeCode String
     */
    public void setBedTypeCode(String bedTypeCode) {
        this.bedTypeCode = bedTypeCode;
    }
    /**
     * ���ð���ע��
     * @param bedOccuFlg String
     */
    public void setBedOccuFlg(String bedOccuFlg) {
        this.bedOccuFlg = bedOccuFlg;
    }
    /**
     * ���ô�λ�ȼ�
     * @param bedClassCode String
     */
    public void setBedClassCode(String bedClassCode) {
        this.bedClassCode = bedClassCode;
    }
    /**
     * ����Ӥ����ע��
     * @param babyBedFlg String
     */
    public void setBabyBedFlg(String babyBedFlg) {
        this.babyBedFlg = babyBedFlg;
    }
    /**
     * ����ԤԼע��
     * @param apptFlg String
     */
    public void setApptFlg(String apptFlg) {
        this.apptFlg = apptFlg;
    }
    /**
     * �����������
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
    /**
     * ���ñ�����
     * @param reserveBedFlg String
     */
    public void setReserveBedFlg(String reserveBedFlg) {
        this.reserveBedFlg = reserveBedFlg;
    }
    /**
     * �����Ա����
     * @param sexCode String
     */
    public void setSexCode(String sexCode) {
        this.sexCode = sexCode;
    }
    /**
     * ���ò���/��ʿվ
     * @param stationCode String
     */
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }
    /**
     * ���ô�λ״̬
     * @param bedStatus String
     */
    public void setBedStatus(String bedStatus) {
        this.bedStatus = bedStatus;
    }
    /**
     * ��������ע��
     * @param activeFlg String
     */
    public void setActiveFlg(String activeFlg) {
        this.activeFlg = activeFlg;
    }
    /**
     * �����ż�ס��
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }
    /**
     * ����ռ��ע��
     * @param alloFlg String
     */
    public void setAlloFlg(String alloFlg) {
        this.alloFlg = alloFlg;
    }
}
