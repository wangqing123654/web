package com.javahis.ui.odo;

import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

import jdo.odo.OpdOrder;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站处方签接口
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站处方签接口
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
	 * 初始化事件
	 * @param odoMainOpdOrder
	 * @throws Exception
	 */
	public void onInitEvent(ODOMainOpdOrder odoMainOpdOrder) throws Exception;

	/**
	 * 初始化
	 * @throws Exception
	 */
	public void init() throws Exception;

	/**
	 * 检核
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
	 * 套餐插入
	 * @param parm
	 * @param flg
	 * @throws Exception
	 */
	public void insertPack(TParm parm, boolean flg) throws Exception;

	/**
	 * 切换处方签
	 * @throws Exception
	 */
	public void onChangeRx(String rxType) throws Exception;

	/**
	 * 医技检核日期
	 * @param order
	 * @param row
	 * @return
	 * @throws Exception
	 */
	public boolean medAppyCheckDate(OpdOrder order, int row) throws Exception;

	/**
	 * 取得table控件
	 * @return
	 * @throws Exception
	 */
	public TTable getTable() throws Exception;

	/**
	 * 初始化table
	 * @param rxNo
	 * @return
	 * @throws Exception
	 */
	public boolean initTable(String rxNo) throws Exception;

	/**
	 * 插入医嘱
	 * @param parm
	 * @throws Exception
	 */
	public void insertOrder(TParm parm) throws Exception;

	/**
	 * 校验是否可以开在同一个处方签上 ===========pangben 2012-7-12 添加管控 所有页签当前的处方签管控
	 * 
	 * @return false 不可以执行 true 可以执行
	 */
	public String getCheckRxNoSum(TParm parm) throws Exception;

	/**
	 * 置rx_type
	 * @param order
	 * @throws Exception
	 */
	public void setopdRecipeInfo(OpdOrder order) throws Exception;

	/**
	 * 取得暂存数据
	 * @throws Exception
	 */
	public void getTempSaveParm() throws Exception;

	/**
	 * 删除处方签
	 * @param rxType
	 * @throws Exception
	 */
	public void onDeleteOrderList(int rxType) throws Exception;

	/**
	 * 删除行
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public boolean deleteRow(OpdOrder order, int row, TTable table)
			throws Exception;

	/**
	 * 删除行数据
	 * 
	 * @param row
	 */
	public void deleteorderAuto(int row) throws Exception;

	/**
	 * 值改变
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
	 * 新增检验检查
	 * 
	 * @param parm
	 *            SysFee
	 * @param row
	 *            TABLE 选中行
	 * @param column
	 *            TABLE 选中列
	 */
	public void insertExa(TParm parm, int row, int column) throws Exception;
	
	/**
	 * 分方
	 * @param flg 是否是点击按钮操作
	 */
	public void onSortRx(boolean flg) throws Exception;
	
	public void onChnChange(String fieldName, String type) throws Exception;
}
