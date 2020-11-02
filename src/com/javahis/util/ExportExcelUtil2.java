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
 * �������ų̲�ѯ����JCI�˲����ӡ
 * 
 * @author wangqing 20171127
 *
 */
public class ExportExcelUtil2 extends TControl {
	private static final ExportExcelUtil2 INSTANCE = new ExportExcelUtil2();
	private TTable table;
	private String parmMap = "";

	/**
	 * �޲ι�����
	 */
	private ExportExcelUtil2() {
	}

	/**
	 * ��ȡ����
	 * @return
	 */
	public static ExportExcelUtil2 getInstance() {

		return INSTANCE;
	}

	/**
	 * ����EXCEL�ķ����Ľӿ�
	 * 
	 * @param table
	 *            TTable table����
	 * @param defName
	 *            String Ĭ���ļ�������
	 */
	public void exportExcel(TTable mainTable, String defName) {
		//		System.out.println("111111111111111");
		table = mainTable;
		// �õ�talble��ͷ
		String header = mainTable.getHeader();
		// ֻ�õ�table����ʾ�����ݣ�PS���������ݵò�����
		TParm mainDate = mainTable.getShowParmValue();
		if (mainDate.getCount() <= 0) {
			this.messageBox("�޵���EXCEL���ݣ�");
			return;
		}
		// ���õ���excel����������ͷ�������ݣ��ļ����֣�
		exeSaveExcel(header, mainDate, defName);
	}

	/**
	 * ����excel�ļ�
	 * 
	 * @param headerDate
	 *            String
	 * @param mainDate
	 *            TParm
	 * @param fileDefName
	 *            Ĭ���ļ�����
	 */
	public void exeSaveExcel(String header, TParm mainDate, String fileDefName) {
		//		System.out.println("222222222");
		// �����������ͷ���ݣ�ֻ���±��⣩
		Vector arrHeader = arrHeader(header);
		System.out.println("222zhongde " + arrHeader);
		// ��׼SWING
		JFileChooser chooser = new JFileChooser();
		// ���õ�ǰ��Ŀ¼��Ŀǰд����
		File dir = new File("C:\\JavaHis\\Excel");
		if (!dir.exists())
			dir.mkdirs();
		chooser.setCurrentDirectory(dir);
		// ���õ�ǰʱ�丽�ӵ�Ĭ���ļ�����
		Timestamp optTime = SystemTool.getInstance().getDate();
		// String opttime=StringTool.getString(optTime, "yyyyMMddHHmmss");
		// String a = opttime.substring(4,6);
		// System.out.println("�ַ���ʱ���ȡ:"+a);
		fileDefName = fileDefName
				+ StringTool.getString(optTime, "yyyyMMddHHmmss");
		// Ĭ�ϵ��ļ���������
		chooser.setSelectedFile(new File(fileDefName));// �ṩĬ����
		// ���öԻ���ı���
		chooser.setDialogTitle("����EXCEL����");
		// ���ù��ˣ���չ����
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
	 * ����table�ı���ͷ��ʹֻ֮ʡ�±���
	 * 
	 * @param date
	 *            String ȱ��ʡ״̬�µ�ͷ
	 * @return Vector
	 */
	public Vector arrHeader(String date) {
		// �ѱ������ݰ�������Ϊ�ַ�����
		String[] indate = date.split(";");
		Vector colAndType = getColumnView(indate);
		Vector result = new Vector();
		Vector header = new Vector();
		// ѭ��ȥ��--> �����ȣ����͵�
		for (int j = 0; j < indate.length; j++) {
			// System.out.println("���ŵ�ֵ:::"+indate[j].substring(0, (int)
			// (indate[j].indexOf(","))));
			// ��ȡ����һ��������ǰ������--����
			String a = indate[j].substring(0, (int) (indate[j].indexOf(",")));
			header.add(a);
		}
		// ��һ���ǵ�һ�е���Ŀ
		result.add(header);
		// �ڶ������п�
		result.add(colAndType.get(0));
		// ����������������
		result.add(colAndType.get(1));

		// System.out.println("header++++"+header);
		return result;
	}

	/**
	 * ��ȡ�п�
	 * @param date
	 * @return
	 */
	public Vector getColumnView(String[] date) {
		//		System.out.println("333333333333");
		Vector result = new Vector();
		// �洢�п����������
		Vector colView = new Vector();
		Vector dateType = new Vector();
		// ����
		// System.out.println("date::::"+date);
		int col = date.length;
		for (int i = 0; i < col; i++) {
			// �ӵ�һ�����ź���һλ��ʼ
			// System.out.println("date[]::::"+date[i]);
			int start = date[i].indexOf(",") + 1;
			int end;
			String type;
			// û�еڶ������ŵ�ʱ��˵��û����������
			if (date[i].indexOf(",", start) == -1) {
				// û�еڶ�������ȡ�����--����
				end = date[i].length();
				// û�����;�Ĭ��Ϊ�ַ���
				type = "String";
			} else {
				// �о�ȡ���ڶ���������ǰ--����
				end = date[i].indexOf(",", start);
				// ȡ��ʡ�µ��ַ�--����
				type = date[i].substring(end + 1);
			}
			// �洢���еĳ���
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
	 * �򿪱���Ի���ѡ��·��
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
		// ����һ���ļ�����
		File file;
		try {
			// ��·���а���.xls�Ͳ��Ӻ�׺
			if (!FileName.contains(".xls")) {
				file = new File(FileName + ".xls");
			} else
				file = new File(FileName);
			// �жϸ��ļ��Ƿ���ڣ�����Ѿ�������ʾ�Ƿ񸲸ǣ������Ǿ��˳�������
			if (file.exists()
					&& JOptionPane.showConfirmDialog(null, "save",
							"���ļ��Ѿ�����,�Ƿ񸲸ǣ�", JOptionPane.YES_NO_OPTION) == 1)
				return;
			// ���ļ���������
			FileOutputStream fileOutStream = null;
			fileOutStream = new FileOutputStream(file);
			// ִ��������������
			exportToExcel(header, date, fileOutStream);
			fileOutStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "ת��ʧ��");
		}
	}

	/**
	 * ����EXCEL
	 * @param headerDate
	 * @param date
	 * @param os
	 */
	public void exportToExcel(Vector headerDate, TParm date, OutputStream os) {
		// ȡ����Ҫ������
		Vector header = (Vector) headerDate.get(0);// ��ͷ�ֶ�
		Vector colView = (Vector) headerDate.get(1);// �п�
		Vector dateType = (Vector) headerDate.get(2);// ��ͷ�ֶ�����
		// ȡ������
		int colNum = header.size();
		// ����cell��ʽ
		WritableFont cellFont= new WritableFont(
				WritableFont.createFont("���� _GB2312"), 14, WritableFont.NO_BOLD);
		WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
		try {
			cellFormat.setWrap(true);// �Զ�����
			cellFormat.setAlignment(jxl.format.Alignment.CENTRE);// ���뷽ʽ�����ж���
		} catch (WriteException e) {
			e.printStackTrace();
		}
		// ��������
		Label title = new Label(0, 0, "������Ʒ��ָ��˲��                  ��           ��", cellFormat);
		Label head1_1= new Label(4, 1, "��ȫ�˲���ȷִ����100%", cellFormat);
		Label head1_2 = new Label(10, 1, "����������ȷִ����100%", cellFormat);
		Label head1_3 = new Label(15, 1, "��������>36��", cellFormat);
		
		Label head2_1 = new Label(0, 1, "���", cellFormat);
		Label head2_2 = new Label(1, 1, "����", cellFormat);
		Label head2_3 = new Label(2, 1, "������", cellFormat);
		Label head2_4 = new Label(3, 1, "����", cellFormat);
		
		try {
			// ���ļ�
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			// ������Ϊ���� ���Ĺ���������0��ʾ���ǵ�һҳ��sheet�����֣�
			WritableSheet sheet1 = workbook.createSheet("��  ��", 0);
			// ������õĵ�Ԫ����ӵ���������
			sheet1.addCell(title);
			sheet1.addCell(head1_1);
			sheet1.addCell(head1_2);
			sheet1.addCell(head1_3);
			
			sheet1.addCell(head2_1);
			sheet1.addCell(head2_2);
			sheet1.addCell(head2_3);
			sheet1.addCell(head2_4);
			
			// ��ӱ�ͷ
			for (int i=0; i <colNum; i++) {
				// ��ӱ�ͷ��һ�У����⣩
				Label cell = new Label(i, 2, (String) header.get(i), cellFormat);
				sheet1.addCell(cell);
			}					
			// ���������
			String map = "";
			if (table != null) {
				map = (String) table.getParmMap();
			} else {
				map = this.parmMap;
			}
			String[] title1 = map.split(";");
			System.out.println("@test by wangqing@---map_length="+map.length());
			System.out.println("@test by wangqing@---title1_length="+title1.length);
			for(int j=0; j<date.getCount(); j++){// ��
				for(int k=0; k<title1.length; k++){// ��
					Label dateCell;
					String type = (String) dateType.get(k);
					// ������������;Ϳ���
					if (type.equalsIgnoreCase("int")
							|| type.equalsIgnoreCase("float")
							|| type.equalsIgnoreCase("double")
							|| type.equalsIgnoreCase("long")) {
						// ���ø����ݿ���
						dateCell = new Label(k, j+3, date.getValue(title1[k], j), cellFormat);
					} else {
						// ���ø����ݿ���
						dateCell = new Label(k, j+3, date.getValue(title1[k], j), cellFormat);
					}
					sheet1.addCell(dateCell);
				}
			}
			
			// ��β
			Label footer_1_1 = new Label(0, date.getCount()+3, "�ϼ�", cellFormat);
			Label footer_1_2 = new Label(4, date.getCount()+3, "����/��ĸ��", cellFormat);
			Label footer_1_3 = new Label(10, date.getCount()+3, "����/��ĸ��", cellFormat);
			Label footer_1_4 = new Label(15, date.getCount()+3, "����/��ĸ��", cellFormat);
			Label footer_2_1 = new Label(4, date.getCount()+3+1, "�ռ��ˣ�", cellFormat);
			Label footer_2_2 = new Label(10, date.getCount()+3+1, "�ռ��ˣ�", cellFormat);
			Label footer_2_3 = new Label(15, date.getCount()+3+1, "�ռ���", cellFormat);
			sheet1.addCell(footer_1_1);
			sheet1.addCell(footer_1_2);
			sheet1.addCell(footer_1_3);
			sheet1.addCell(footer_1_4);
			sheet1.addCell(footer_2_1);
			sheet1.addCell(footer_2_2);
			sheet1.addCell(footer_2_3);
			
			// �ϲ���Ԫ��
			sheet1.mergeCells(0, 0, 17, 0);
			sheet1.mergeCells(4, 1, 9, 1);
			sheet1.mergeCells(10, 1, 14, 1);			
			sheet1.mergeCells(15, 1, 17, 1);			
			sheet1.mergeCells(0, 1, 0, 2);
			sheet1.mergeCells(1, 1, 1, 2);
			sheet1.mergeCells(2, 1, 2, 2);
			sheet1.mergeCells(3, 1, 3, 2);
			
			// ���������ͬʱ�ϲ�
			sheet1.mergeCells(0, date.getCount()+3, 3, date.getCount()+3+1);			
			
			sheet1.mergeCells(4, date.getCount()+3, 9, date.getCount()+3);
			sheet1.mergeCells(10, date.getCount()+3, 14, date.getCount()+3);
			sheet1.mergeCells(15, date.getCount()+3, 17, date.getCount()+3);
			
			sheet1.mergeCells(4, date.getCount()+3+1, 9, date.getCount()+3+1);
			sheet1.mergeCells(10, date.getCount()+3+1, 14, date.getCount()+3+1);
			sheet1.mergeCells(15, date.getCount()+3+1, 17, date.getCount()+3+1);
			
			
			// �����п�
			for (int n = 0; n < colNum; n++) {
				int colViwe = Integer.parseInt((String) colView.get(n));
				// ѭ�������п���table���泤�ȵ�10��֮һ��
				sheet1.setColumnView(n, colViwe / 10);
			}
			workbook.write();
			workbook.close();
			JOptionPane.showMessageDialog(null, "ת���ɹ�");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "ת��ʧ��");
		}
	}


}
