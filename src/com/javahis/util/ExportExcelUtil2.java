package com.javahis.util;

import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import jxl.Workbook;
import jxl.write.*;

import java.sql.Timestamp;

import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;

import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;
import com.dongyang.control.TControl;
/**
 * 手术室排程查询界面JCI核查表单打印
 * 
 * @author wangqing 20171127
 *
 */
public class ExportExcelUtil2 extends TControl {
	private static final ExportExcelUtil2 INSTANCE = new ExportExcelUtil2();
	private TTable table;
	private String parmMap = "";

	/**
	 * 无参构造器
	 */
	private ExportExcelUtil2() {
	}

	/**
	 * 获取单例
	 * @return
	 */
	public static ExportExcelUtil2 getInstance() {

		return INSTANCE;
	}

	/**
	 * 导出EXCEL的方法的接口
	 * 
	 * @param table
	 *            TTable table对象
	 * @param defName
	 *            String 默认文件的名字
	 */
	public void exportExcel(TTable mainTable, String defName) {
		//		System.out.println("111111111111111");
		table = mainTable;
		// 得到talble的头
		String header = mainTable.getHeader();
		// 只得到table上显示的数据（PS：隐藏数据得不到）
		TParm mainDate = mainTable.getShowParmValue();
		if (mainDate.getCount() <= 0) {
			this.messageBox("无导出EXCEL数据！");
			return;
		}
		// 调用导出excel方法（标题头，主数据，文件名字）
		exeSaveExcel(header, mainDate, defName);
	}

	/**
	 * 导出excel文件
	 * 
	 * @param headerDate
	 *            String
	 * @param mainDate
	 *            TParm
	 * @param fileDefName
	 *            默认文件名字
	 */
	public void exeSaveExcel(String header, TParm mainDate, String fileDefName) {
		//		System.out.println("222222222");
		// 重新整理标题头数据（只留下标题）
		Vector arrHeader = arrHeader(header);
		System.out.println("222zhongde " + arrHeader);
		// 标准SWING
		JFileChooser chooser = new JFileChooser();
		// 设置当前的目录（目前写死）
		File dir = new File("C:\\JavaHis\\Excel");
		if (!dir.exists())
			dir.mkdirs();
		chooser.setCurrentDirectory(dir);
		// 利用当前时间附加到默认文件名上
		Timestamp optTime = SystemTool.getInstance().getDate();
		// String opttime=StringTool.getString(optTime, "yyyyMMddHHmmss");
		// String a = opttime.substring(4,6);
		// System.out.println("字符串时间截取:"+a);
		fileDefName = fileDefName
				+ StringTool.getString(optTime, "yyyyMMddHHmmss");
		// 默认的文件保存名字
		chooser.setSelectedFile(new File(fileDefName));// 提供默认名
		// 设置对话框的标题
		chooser.setDialogTitle("导出EXCEL界面");
		// 设置过滤（扩展名）
		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().toUpperCase().endsWith(".XLS");
			}

			public String getDescription() {
				return "Excel (*.xls)";
			}
		});

		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = chooser.getSelectedFile().getAbsolutePath();
			this.writeFile(arrHeader, mainDate, path);
		}
	}

	/**
	 * 整理table的标题头，使之只省下标题
	 * 
	 * @param date
	 *            String 缺身省状态下的头
	 * @return Vector
	 */
	public Vector arrHeader(String date) {
		// 把标题数据按；区间为字符数组
		String[] indate = date.split(";");
		Vector colAndType = getColumnView(indate);
		Vector result = new Vector();
		Vector header = new Vector();
		// 循环去掉--> ，长度，类型等
		for (int j = 0; j < indate.length; j++) {
			// System.out.println("逗号的值:::"+indate[j].substring(0, (int)
			// (indate[j].indexOf(","))));
			// 截取‘第一个’逗号前的文字--标题
			String a = indate[j].substring(0, (int) (indate[j].indexOf(",")));
			header.add(a);
		}
		// 第一组是第一行的题目
		result.add(header);
		// 第二组是列宽
		result.add(colAndType.get(0));
		// 第三行是数据类型
		result.add(colAndType.get(1));

		// System.out.println("header++++"+header);
		return result;
	}

	/**
	 * 获取列宽
	 * @param date
	 * @return
	 */
	public Vector getColumnView(String[] date) {
		//		System.out.println("333333333333");
		Vector result = new Vector();
		// 存储列宽和数据类型
		Vector colView = new Vector();
		Vector dateType = new Vector();
		// 行数
		// System.out.println("date::::"+date);
		int col = date.length;
		for (int i = 0; i < col; i++) {
			// 从第一个逗号后面一位开始
			// System.out.println("date[]::::"+date[i]);
			int start = date[i].indexOf(",") + 1;
			int end;
			String type;
			// 没有第二个逗号的时候说明没有数据类型
			if (date[i].indexOf(",", start) == -1) {
				// 没有第二个逗号取到最后--长度
				end = date[i].length();
				// 没有类型就默认为字符串
				type = "String";
			} else {
				// 有就取到第二个逗号以前--长度
				end = date[i].indexOf(",", start);
				// 取得省下的字符--类型
				type = date[i].substring(end + 1);
			}
			// 存储该列的长度
			String view = date[i].substring(start, end);
			colView.add(view);
			dateType.add(type);
			// System.out.println("colView:::"+colView);

		}
		// System.out.println("dateType:::"+dateType);
		result.add(colView);
		result.add(dateType);
		return result;
	}

	/**
	 * 打开保存对话框选择路径
	 * 
	 * @param header
	 *            Vector
	 * @param date
	 *            TParm
	 * @param FileName
	 *            String
	 */
	public void writeFile(Vector header, TParm date, String FileName) {
		//		System.out.println("4444444444444");
		// 创建一个文件对象
		File file;
		try {
			// 过路径中包含.xls就不加后缀
			if (!FileName.contains(".xls")) {
				file = new File(FileName + ".xls");
			} else
				file = new File(FileName);
			// 判断该文件是否存在，如果已经存在提示是否覆盖（不覆盖就退出操作）
			if (file.exists()
					&& JOptionPane.showConfirmDialog(null, "save",
							"该文件已经存在,是否覆盖？", JOptionPane.YES_NO_OPTION) == 1)
				return;
			// 把文件变成输出流
			FileOutputStream fileOutStream = null;
			fileOutStream = new FileOutputStream(file);
			// 执行数据整理并导出
			exportToExcel(header, date, fileOutStream);
			fileOutStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
	}

	/**
	 * 导出EXCEL
	 * @param headerDate
	 * @param date
	 * @param os
	 */
	public void exportToExcel(Vector headerDate, TParm date, OutputStream os) {
		// 取得需要的数据
		Vector header = (Vector) headerDate.get(0);// 表头字段
		Vector colView = (Vector) headerDate.get(1);// 列宽
		Vector dateType = (Vector) headerDate.get(2);// 表头字段类型
		// 取得列数
		int colNum = header.size();
		// 定义cell格式
		WritableFont cellFont= new WritableFont(
				WritableFont.createFont("楷体 _GB2312"), 14, WritableFont.NO_BOLD);
		WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
		try {
			cellFormat.setWrap(true);// 自动换行
			cellFormat.setAlignment(jxl.format.Alignment.CENTRE);// 对齐方式：居中对齐
		} catch (WriteException e) {
			e.printStackTrace();
		}
		// 构造数据
		Label title = new Label(0, 0, "手术室品质指标核查表                  年           月", cellFormat);
		Label head1_1= new Label(4, 1, "安全核查正确执行率100%", cellFormat);
		Label head1_2 = new Label(10, 1, "麻醉评估正确执行率100%", cellFormat);
		Label head1_3 = new Label(15, 1, "术中体温>36℃", cellFormat);
		
		Label head2_1 = new Label(0, 1, "序号", cellFormat);
		Label head2_2 = new Label(1, 1, "日期", cellFormat);
		Label head2_3 = new Label(2, 1, "病案号", cellFormat);
		Label head2_4 = new Label(3, 1, "姓名", cellFormat);
		
		try {
			// 打开文件
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			// 生成名为“报 表”的工作表，参数0表示这是第一页（sheet的名字）
			WritableSheet sheet1 = workbook.createSheet("报  表", 0);
			// 将定义好的单元格添加到工作表中
			sheet1.addCell(title);
			sheet1.addCell(head1_1);
			sheet1.addCell(head1_2);
			sheet1.addCell(head1_3);
			
			sheet1.addCell(head2_1);
			sheet1.addCell(head2_2);
			sheet1.addCell(head2_3);
			sheet1.addCell(head2_4);
			
			// 添加表头
			for (int i=0; i <colNum; i++) {
				// 添加表头的一行（标题）
				Label cell = new Label(i, 2, (String) header.get(i), cellFormat);
				sheet1.addCell(cell);
			}					
			// 添加主数据
			String map = "";
			if (table != null) {
				map = (String) table.getParmMap();
			} else {
				map = this.parmMap;
			}
			String[] title1 = map.split(";");
			System.out.println("@test by wangqing@---map_length="+map.length());
			System.out.println("@test by wangqing@---title1_length="+title1.length);
			for(int j=0; j<date.getCount(); j++){// 行
				for(int k=0; k<title1.length; k++){// 列
					Label dateCell;
					String type = (String) dateType.get(k);
					// 如果是数字类型就靠右
					if (type.equalsIgnoreCase("int")
							|| type.equalsIgnoreCase("float")
							|| type.equalsIgnoreCase("double")
							|| type.equalsIgnoreCase("long")) {
						// 设置该数据靠右
						dateCell = new Label(k, j+3, date.getValue(title1[k], j), cellFormat);
					} else {
						// 设置该数据靠左
						dateCell = new Label(k, j+3, date.getValue(title1[k], j), cellFormat);
					}
					sheet1.addCell(dateCell);
				}
			}
			
			// 表尾
			Label footer_1_1 = new Label(0, date.getCount()+3, "合计", cellFormat);
			Label footer_1_2 = new Label(4, date.getCount()+3, "分子/分母：", cellFormat);
			Label footer_1_3 = new Label(10, date.getCount()+3, "分子/分母：", cellFormat);
			Label footer_1_4 = new Label(15, date.getCount()+3, "分子/分母：", cellFormat);
			Label footer_2_1 = new Label(4, date.getCount()+3+1, "收集人：", cellFormat);
			Label footer_2_2 = new Label(10, date.getCount()+3+1, "收集人：", cellFormat);
			Label footer_2_3 = new Label(15, date.getCount()+3+1, "收集人", cellFormat);
			sheet1.addCell(footer_1_1);
			sheet1.addCell(footer_1_2);
			sheet1.addCell(footer_1_3);
			sheet1.addCell(footer_1_4);
			sheet1.addCell(footer_2_1);
			sheet1.addCell(footer_2_2);
			sheet1.addCell(footer_2_3);
			
			// 合并单元格
			sheet1.mergeCells(0, 0, 17, 0);
			sheet1.mergeCells(4, 1, 9, 1);
			sheet1.mergeCells(10, 1, 14, 1);			
			sheet1.mergeCells(15, 1, 17, 1);			
			sheet1.mergeCells(0, 1, 0, 2);
			sheet1.mergeCells(1, 1, 1, 2);
			sheet1.mergeCells(2, 1, 2, 2);
			sheet1.mergeCells(3, 1, 3, 2);
			
			// 横向和纵向同时合并
			sheet1.mergeCells(0, date.getCount()+3, 3, date.getCount()+3+1);			
			
			sheet1.mergeCells(4, date.getCount()+3, 9, date.getCount()+3);
			sheet1.mergeCells(10, date.getCount()+3, 14, date.getCount()+3);
			sheet1.mergeCells(15, date.getCount()+3, 17, date.getCount()+3);
			
			sheet1.mergeCells(4, date.getCount()+3+1, 9, date.getCount()+3+1);
			sheet1.mergeCells(10, date.getCount()+3+1, 14, date.getCount()+3+1);
			sheet1.mergeCells(15, date.getCount()+3+1, 17, date.getCount()+3+1);
			
			
			// 设置列宽
			for (int n = 0; n < colNum; n++) {
				int colViwe = Integer.parseInt((String) colView.get(n));
				// 循环设置列宽（是table上面长度的10分之一）
				sheet1.setColumnView(n, colViwe / 10);
			}
			workbook.write();
			workbook.close();
			JOptionPane.showMessageDialog(null, "转档成功");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "转档失败");
		}
	}


}
