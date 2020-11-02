package com.javahis.ui.odo;

import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

import jdo.odo.OpdOrder;

/**
 * 
 * <p>
 * 
 * Title: ����ҽ������վ����ǩ�ӿ�
 * </p>
 * 
 * <p>
 * Description:����ҽ������վ����ǩ�ӿ�
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public interface IODORx {

	/**
	 * ��ʼ���¼�
	 * @param odoMainOpdOrder
	 * @throws Exception
	 */
	public void onInitEvent(ODOMainOpdOrder odoMainOpdOrder) throws Exception;

	/**
	 * ��ʼ��
	 * @throws Exception
	 */
	public void init() throws Exception;

	/**
	 * ���
	 * @param order
	 * @param row
	 * @param name
	 * @param spcFlg
	 * @return
	 * @throws Exception
	 */
	public boolean check(OpdOrder order, int row, String name, boolean spcFlg)
			throws Exception;

	/**
	 * �ײͲ���
	 * @param parm
	 * @param flg
	 * @throws Exception
	 */
	public void insertPack(TParm parm, boolean flg) throws Exception;

	/**
	 * �л�����ǩ
	 * @throws Exception
	 */
	public void onChangeRx(String rxType) throws Exception;

	/**
	 * ҽ���������
	 * @param order
	 * @param row
	 * @return
	 * @throws Exception
	 */
	public boolean medAppyCheckDate(OpdOrder order, int row) throws Exception;

	/**
	 * ȡ��table�ؼ�
	 * @return
	 * @throws Exception
	 */
	public TTable getTable() throws Exception;

	/**
	 * ��ʼ��table
	 * @param rxNo
	 * @return
	 * @throws Exception
	 */
	public boolean initTable(String rxNo) throws Exception;

	/**
	 * ����ҽ��
	 * @param parm
	 * @throws Exception
	 */
	public void insertOrder(TParm parm) throws Exception;

	/**
	 * У���Ƿ���Կ���ͬһ������ǩ�� ===========pangben 2012-7-12 ��ӹܿ� ����ҳǩ��ǰ�Ĵ���ǩ�ܿ�
	 * 
	 * @return false ������ִ�� true ����ִ��
	 */
	public String getCheckRxNoSum(TParm parm) throws Exception;

	/**
	 * ��rx_type
	 * @param order
	 * @throws Exception
	 */
	public void setopdRecipeInfo(OpdOrder order) throws Exception;

	/**
	 * ȡ���ݴ�����
	 * @throws Exception
	 */
	public void getTempSaveParm() throws Exception;

	/**
	 * ɾ������ǩ
	 * @param rxType
	 * @throws Exception
	 */
	public void onDeleteOrderList(int rxType) throws Exception;

	/**
	 * ɾ����
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public boolean deleteRow(OpdOrder order, int row, TTable table)
			throws Exception;

	/**
	 * ɾ��������
	 * 
	 * @param row
	 */
	public void deleteorderAuto(int row) throws Exception;

	/**
	 * ֵ�ı�
	 * @param inRow
	 * @param execDept
	 * @param orderCodeFinal
	 * @param columnNameFinal
	 * @param oldDosageQty
	 * @throws Exception
	 */
	public void setOnValueChange(final int inRow, final String execDept,
			final String orderCodeFinal, final String columnNameFinal,
			final double oldDosageQty) throws Exception;
	
	/**
	 * ����������
	 * 
	 * @param parm
	 *            SysFee
	 * @param row
	 *            TABLE ѡ����
	 * @param column
	 *            TABLE ѡ����
	 */
	public void insertExa(TParm parm, int row, int column) throws Exception;
	
	/**
	 * �ַ�
	 * @param flg �Ƿ��ǵ����ť����
	 */
	public void onSortRx(boolean flg) throws Exception;
	
	public void onChnChange(String fieldName, String type) throws Exception;
}
