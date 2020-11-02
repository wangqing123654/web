package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;

/**
 *
 * <p>Title:����ۿ������б� </p>
 *
 * <p>Description:����ۿ������б� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.26
 * @version 1.0
 */
public class TComboSysCtz
    extends TComboBox {

    /**
     * �����ע��
     */
    private String mainCtzFlg;
    /**
     * ҽ�����ע��
     */
    private String nhiCtzFlg;
    
    /**
     * ��������ע��
     */
    private String opdFitFlg    ; //OPD_FIT_FLG
    
    /**
     * ��������ע��
     */
    private String emgFitFlg    ; //EMG_FIT_FLG
    
	/**
     * סԺ����ע��
     */
    private String ipdFitFlg    ; //IPD_FIT_FLG
    
    /**
     * ��������ע��
     */
    private String hrmFitFlg    ; //HRM_FIT_FLG
    
    
    public String getOpdFitFlg() {
		return opdFitFlg;
	}

	public void setOpdFitFlg(String opdFitFlg) {
		this.opdFitFlg = opdFitFlg;
	}

	public String getEmgFitFlg() {
		return emgFitFlg;
	}

	public void setEmgFitFlg(String emgFitFlg) {
		this.emgFitFlg = emgFitFlg;
	}

	public String getIpdFitFlg() {
		return ipdFitFlg;
	}

	public void setIpdFitFlg(String ipdFitFlg) {
		this.ipdFitFlg = ipdFitFlg;
	}

	public String getHrmFitFlg() {
		return hrmFitFlg;
	}

	public void setHrmFitFlg(String hrmFitFlg) {
		this.hrmFitFlg = hrmFitFlg;
	}

	/**
     * ����ҽ�����ע��
     * @param nhiCtzFlg String
     */
    public void setNhiCtzFlg(String nhiCtzFlg) {
        this.nhiCtzFlg = nhiCtzFlg;
    }

    /**
     * ���������
     * @param mainCtzFlg String
     */
    public void setMainCtzFlg(String mainCtzFlg) {
        this.mainCtzFlg = mainCtzFlg;
    }

    /**
     * �õ�ҽ�����ע��
     * @return String
     */
    public String getNhiCtzFlg() {
        return nhiCtzFlg;
    }

    /**
     * �õ������
     * @return String
     */
    public String getMainCtzFlg() {
        return mainCtzFlg;
    }

    /**
     * ִ��Module����
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("MAIN_CTZ_FLG", getTagValue(getMainCtzFlg()));
        parm.setDataN("NHI_CTZ_FLG", getTagValue(getNhiCtzFlg()));
        parm.setDataN("OPD_FIT_FLG", getTagValue(getOpdFitFlg()));
        parm.setDataN("EMG_FIT_FLG", getTagValue(getEmgFitFlg()));
        parm.setDataN("IPD_FIT_FLG", getTagValue(getIpdFitFlg()));
        parm.setDataN("HRM_FIT_FLG", getTagValue(getHrmFitFlg()));
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
        object.setValue("Tip", "���");
        object.setValue("TableShowList", "id,name");
    }

    public String getModuleName() {
        return "sys\\SYSCTZModule.x";
    }

    public String getModuleMethodName() {
        return "initCtzCode";
    }

    public String getParmMap() {
        return "id:ID;text:TEXT;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("MainCtzFlg", "String", "", "Left"));
        data.add(new TAttribute("NhiCtzFlg", "String", "", "Left"));
        data.add(new TAttribute("opdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("emgFitFlg", "String", "", "Left"));
        data.add(new TAttribute("ipdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("hrmFitFlg", "String", "", "Left"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("MainCtzFlg".equalsIgnoreCase(name)) {
            setMainCtzFlg(value);
            getTObject().setValue("MainCtzFlg", value);
            return;
        }
        if ("NhiCtzFlg".equalsIgnoreCase(name)) {
            setNhiCtzFlg(value);
            getTObject().setValue("NhiCtzFlg", value);
            return;
        }
        if("opdFitFlg".equalsIgnoreCase(name)){
        	setOpdFitFlg(value);
        	getTObject().setValue("opdFitFlg", value);
            return;
        }
        if("emgFitFlg".equalsIgnoreCase(name)){
        	setEmgFitFlg(value);
        	getTObject().setValue("emgFitFlg", value);
            return;
        }
        if("ipdFitFlg".equalsIgnoreCase(name)){
        	setIpdFitFlg(value);
        	getTObject().setValue("ipdFitFlg", value);
            return;
        }
        if("hrmFitFlg".equalsIgnoreCase(name)){
        	setHrmFitFlg(value);
        	getTObject().setValue("hrmFitFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
