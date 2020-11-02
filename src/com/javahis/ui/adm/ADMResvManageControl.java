package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.Pat;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;

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
public class ADMResvManageControl extends TControl {
    TParm recptype = new TParm();//��ԤԼסԺ�Ӳ�
    Pat pat ;
    public void onInit() {
        super.onInit();
        init();
        setValue("APP_DATE", SystemTool.getInstance().getDate());
        setValue("RESV_DATE", SystemTool.getInstance().getDate());
        recptype = (TParm)this.getParameter();
        this.onSelect();
    }
    /**
     * ��ԤԼסԺ���θ�ֵ
     */
    public void onSelect(){
        String mrNo = recptype.getValue("MR_NO");
        pat = pat.onQueryByMrNo(mrNo);
        setValue("MR_NO",pat.getMrNo());
        setValue("CTZ1_CODE",pat.getCtz1Code());
        setValue("SEX_CODE",pat.getSexCode());
    }

    public void onSave(){
        TParm reData = new TParm();
        reData.addData("res","����");
//        reData.setData("RESV_DATE",StringTool.getString((Timestamp)this.getValue(""),"yyyymmdd"));//������Դ
        reData.setData("RESV_DATE",this.getValue("RESV_DATE"));
        reData.setData("ADM_SOURCE",getValue("ADM_SOURCE"));//������Դ
        reData.setData("PR_DEPT_CODE",getValue("PR_DEPT_CODE"));//�ż���Ʊ�
        reData.setData("DR_CODE",getValue("DR_CODE"));//ҽʦ
        reData.setData("PATIENT_CONDITION",getValue("PATIENT_CONDITION"));//����״��
        reData.setData("STATION_CODE",getValue("STATION_CODE"));//Ԥ��סԺ����
        reData.setData("DEPT_CODE",getValue("DEPT_CODE"));//Ԥ��סԺ����
        reData.setData("CLNCPATH_CODE",getValue("CLNCPATH_CODE"));//�ٴ�·��
        this.setReturnValue(reData);

    }




}
