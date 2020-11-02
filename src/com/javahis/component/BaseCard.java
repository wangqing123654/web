package com.javahis.component;

import java.util.ArrayList;
import java.util.List;

import com.tiis.ui.TiMultiPanel;


/**
 *
 * ��ͷ������
 * @author whaosoft
 *
 */
public class BaseCard extends TiMultiPanel{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

    /** ���� */
	public String sBed_no = "";
	/** ������ */
	public String sMr_no = "";

	/** ����ֶα���Ӧ���� ���ݿ��������� bed_no ��ʵ���е�����bed_no��ų��� bed_desc�� */
	public String sCode = "";

    /** ��λ״̬ */
	public String sBedStatus = "";

	/** ����ҽʦ */
	public String sVs_dr_code = null;
	/** ����ҽʦ */
	public String sAttend_dr_code = null;
	/** ������ */
	public String sDirector_dr_code = null;
	/** ���ܻ�ʿ */
	public String sVs_nurse_code = null;
	/** ��Ժ״̬ */
	public String sPatient_condition = null;

	//
	public String sIpdNo = "";
	public String sCase_no = "";
	public String sNURSING_CLASS = "";
	public String sName = "";
	public String sStation_code = "";
	public String sDeptCode = "";
	public String occupy_bed_flg = "";
	public String sSex_code = "";// �Ա����
	public String sAge = "";
	public String sInDate = "";

	/** ������ */
	public String new_born_flg = null;
	/** Ӥ����λ */
	public String baby_bed_flg = null;
	/** ��������ʾ���� */
	public String new_age = "";


	/**
	 *
	 * @param array
	 * @return
	 */
	public List<String> getDataList(String[] array){

		List<String> list = new ArrayList<String>();
		for(  String str:array ){
			list.add(str);
		}
		array = null;

		return list;
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public int getColorValue( String value ){

		return Integer.parseInt( value.equals("") ? "255": value.trim() );
	}

	/**
	 *
	 * @param list
	 * @param index
	 * @return
	 */
	public String getData(List<String> list,int index){

		if( list.size()>index ){
			return list.get(index);
		}else{
			return "";
		}
	}
}
