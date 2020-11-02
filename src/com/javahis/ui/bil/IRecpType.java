package com.javahis.ui.bil;


import com.dongyang.data.TParm;

/**
 * <p>Title:�վ����ͽӿ�</p>
 *
 * <p>Description:�վ����ͽӿ� </p>
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company:bluecore </p>
 *
 * @author zhangp
 * @version 1.0
 */
public interface IRecpType {
	
	/**
	 * ȡ�ñ���
	 * @return
	 * @throws Exception
	 */
	public String getHeader() throws Exception;
	
	/**
	 * ȡ��parmMap
	 * @return
	 * @throws Exception
	 */
	public String getParmMap() throws Exception;
	
	/**
	 * ȡ��columnHorizontalAlignmentData
	 * @return
	 * @throws Exception
	 */
	public String getColumnHorizontalAlignmentData() throws Exception;
	
	/**
	 *  ȡ��LockColumns
	 * @return
	 * @throws Exception
	 */
	public String getLockColumns() throws Exception;
	
	/**
	 * ��ѯ
	 * @param mrNo
	 * @param patName
	 * @param recpStDate
	 * @param recpEdDate
	 * @return
	 * @throws Exception
	 */
	public TParm onQuery(String mrNo, String patName, String recpStDate, String recpEdDate) throws Exception;
	
	/**
	 * ��ѯ
	 * @param mrNo
	 * @param patName
	 * @param taxStDate
	 * @param taxEdDate
	 * @return
	 * @throws Exception
	 */
	public TParm onQueryCompeleted(String mrNo, String patName, String taxStDate, String taxEdDate) throws Exception;
	
	/**
	 * ����
	 * @param recpParm
	 * @param taxUser
	 * @return
	 * @throws Exception
	 */
	public TParm onSave(TParm recpParm, String taxUser) throws Exception;
	
	/**
	 * Ʊ�ű���
	 * @param recpParm
	 * @param taxUser
	 * @return
	 * @throws Exception
	 */
	public TParm onSaveInvNo(TParm recpParm) throws Exception;
	
	/**
	 * ȡ��
	 * @param recpParm
	 * @param taxUser
	 * @return
	 * @throws Exception
	 */
	public TParm onCancle(TParm recpParm) throws Exception;

}
