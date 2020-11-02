package com.javahis.ui.bil;


import com.dongyang.data.TParm;

/**
 * <p>Title:收据类型接口</p>
 *
 * <p>Description:收据类型接口 </p>
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
	 * 取得标题
	 * @return
	 * @throws Exception
	 */
	public String getHeader() throws Exception;
	
	/**
	 * 取得parmMap
	 * @return
	 * @throws Exception
	 */
	public String getParmMap() throws Exception;
	
	/**
	 * 取得columnHorizontalAlignmentData
	 * @return
	 * @throws Exception
	 */
	public String getColumnHorizontalAlignmentData() throws Exception;
	
	/**
	 *  取得LockColumns
	 * @return
	 * @throws Exception
	 */
	public String getLockColumns() throws Exception;
	
	/**
	 * 查询
	 * @param mrNo
	 * @param patName
	 * @param recpStDate
	 * @param recpEdDate
	 * @return
	 * @throws Exception
	 */
	public TParm onQuery(String mrNo, String patName, String recpStDate, String recpEdDate) throws Exception;
	
	/**
	 * 查询
	 * @param mrNo
	 * @param patName
	 * @param taxStDate
	 * @param taxEdDate
	 * @return
	 * @throws Exception
	 */
	public TParm onQueryCompeleted(String mrNo, String patName, String taxStDate, String taxEdDate) throws Exception;
	
	/**
	 * 保存
	 * @param recpParm
	 * @param taxUser
	 * @return
	 * @throws Exception
	 */
	public TParm onSave(TParm recpParm, String taxUser) throws Exception;
	
	/**
	 * 票号保存
	 * @param recpParm
	 * @param taxUser
	 * @return
	 * @throws Exception
	 */
	public TParm onSaveInvNo(TParm recpParm) throws Exception;
	
	/**
	 * 取消
	 * @param recpParm
	 * @param taxUser
	 * @return
	 * @throws Exception
	 */
	public TParm onCancle(TParm recpParm) throws Exception;

}
