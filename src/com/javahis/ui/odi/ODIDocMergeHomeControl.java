package com.javahis.ui.odi;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import jdo.pdf.PDFODITool;
import jdo.pdf.PdfTool;
import jdo.pdf.jacobTool;
import jdo.sys.Operator;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRootPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TText;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWord;
import com.dongyang.util.FileTool;
import com.javahis.util.StringUtil;
import com.pdf.server.PDFGetEMRData;
import com.pdf.server.PDFServer;

public class ODIDocMergeHomeControl extends TControl {

	/**
	 * ��ʼ������
	 */
	public void onInit() {

		super.onInit();
		// ��ʼ��ҳ��
		TParm parm = (TParm) this.getParameter();
		this.setTitle("������ҳ�ϲ�_" + parm.getValue("MR_NO") + "_"
				+ parm.getValue("PAT_NAME"));
		initPage();
	}

	private String caseNo = "";
	private String fileServerRoot = TConfig
			.getSystemValue("FileServer.Main.Root");
	/*String tempPath = TConfig.getSystemValue("FileServer.Main.Root")
			+ "\\temp\\pdf";*/
	String tempPath ="C:\\JavaHisFile\\temp\\pdf";
	
	private String serverPath = "";

	private void initPage() {

		TParm parm = (TParm) this.getParameter();
		caseNo = parm.getValue("CASE_NO");
		this.setTitle("������ҳ�ϲ�_" + parm.getValue("MR_NO") + "_"
				+ parm.getValue("PAT_NAME"));

		if (onPrintIndex()) {
			onAllPDFonAddPdf();
		}
	}

	/**
	 * ��ҳ��ӡ
	 */
	public boolean onPrintIndex() {
		TParm parm = (TParm) this.getParameter();
		String sql = "SELECT  E.FILE_PATH, E.FILE_NAME "
				+ "FROM EMR_FILE_INDEX E WHERE CASE_NO ='"
				+ parm.getValue("CASE_NO") + "' AND MR_NO='"
				+ parm.getValue("MR_NO")
				+ "' AND FILE_NAME like '%������ҳ%' ORDER BY CREATOR_DATE DESC";

		boolean flg = true;

		File fDir = new File(tempPath);
		if (!fDir.exists()) {
			fDir.mkdirs();
		}
		Map fileMap = new HashMap();
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// parm.setData("TEMP_PATH", tempPath);
		if (result.getCount("FILE_NAME") > 0) {
			this.delAllFile(tempPath);
			TWord word = new TWord();
			word.onOpen(result.getValue("FILE_PATH", 0), result.getValue(
					"FILE_NAME", 0)
					+ ".jhw", 3, true);
			try {
				word.getPageManager().setOrientation(1);
				word.getPageManager().print(PrinterJob.getPrinterJob(),
						"0" + result.getValue("FILE_NAME", 0).split("_")[0]);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			fileMap.put(result.getValue("FILE_NAME"), result);
		} else {
			this.messageBox("��������δ���ɡ�");
			((TButton) this.getComponent("tButton_0")).setEnabled(false);
			((TButton) this.getComponent("tButton_1")).setEnabled(false);

			return false;
		}
		TWord word = new TWord();
		word.print();
		flg = onUpdate(fileMap);
		onDonwLoadPDF();
		return true;

	}

	public void onGeneratePDF() {
		boolean b = onPrintIndex();
		if (!b) {
			this.messageBox("����ʧ�ܡ�");
			return;
		}
		this.messageBox("���ɳɹ���");
		onInit();
		// PDFServer server=new PDFServer();
		// server.creatTextPDF();
	}

	/**
	 * PDF�������
	 */
	public boolean openPDF() {
		Runtime runtime = Runtime.getRuntime();

		try {
			// ���ļ�
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + caseNo + ".pdf");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param parmCase
	 * @return
	 */
	private TParm getTextListByPat(TParm parmCase) {
		TParm result = new TParm();
		// PDFGetINWData.getINWTextList(parmCase, result);
		// PDFGetSUMData.getSUMATextList(parmCase, result);
		PDFServer server = new PDFServer();
		server.creatTextPDF();
		// PDFGetEMRData.getEMRTextList(parmCase, result);
		return result;
	}

	/**
	 * ���
	 */
	public void onDonwLoadPDF() {
		TParm parm = (TParm) this.getParameter();
		String bigFilePath = TConfig.getSystemValue("FileServer.Main.Root")
				+ "\\��ʽ����\\" + parm.getValue("MR_NO").substring(0, 7) + "\\"
				+ parm.getValue("MR_NO") + "\\" + caseNo + ".pdf";
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
				.getSocket(),bigFilePath);
		if (data == null) {
			messageBox_("��δ�ύPDF");
			return;
		}

		try {
			FileTool.setByte(tempPath + "\\" + 1 + caseNo + ".pdf", data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Runtime runtime = Runtime.getRuntime();
		// try {
		// // ���ļ�
		// runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath +
		// "\\" + caseNo + ".pdf");
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }

	}

	/**
	 * �ϴ�����
	 */
	public boolean onUpdate(Map fileMap) {
		List fileList = this.getAllFile(tempPath);
		for (int i = 0; i < fileList.size(); i++) {
			File f = (File) fileList.get(i);
			TParm parm = (TParm) fileMap.get(f.getName().split("\\.")[0]);
			if (parm == null) {
				continue;
			}
			String fileName = parm.getValue("FILE_NAME");
			String filePath = TConfig.getSystemValue("FileServer.Main.Root")
					+ "\\" + TConfig.getSystemValue("EmrData") + "\\"
					+ parm.getValue("FILE_PATH").replaceFirst("JHW", "PDF")
					+ "\\" + fileName + ".pdf";
			try {
				TIOM_FileServer.deleteFile(TIOM_FileServer.getSocket(),
						filePath);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				byte data[] = FileTool.getByte(f);

				if (!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
						filePath, data)) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // ɾ�����ļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ɾ��ָ���ļ����������ļ�
	// param path �ļ�����������·��
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]);// ��ɾ�����ļ���
				flag = true;
			}
		}
		return flag;
	}

	public static List getAllFile(String path) {
		List fileList = new ArrayList();
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		if (!file.isDirectory()) {
			return null;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				fileList.add(temp);
			}
		}
		return fileList;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * �ϲ�����
	 */
	public void onAllPDFonAddPdf() {
		File f = new File(tempPath + "");

		if (!f.exists())
			f.mkdirs();
		// ����ִ���ļ�
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				PdfTool.getInstance().getRoot() + "\\pdftk");
		if (data == null) {
			messageBox_("��������û���ҵ��ļ� " + PdfTool.getInstance().getRoot()
					+ "\\pdftk");
			return;
		}
		try {
			FileTool.setByte(tempPath + "\\pdftk.exe", data);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// �����������ļ�
		StringBuffer sb = new StringBuffer();
		sb.append(0 + caseNo + "" + ".pdf ");
		sb.append(1 + caseNo + "" + ".pdf ");
		String s = tempPath.substring(0, 2) + "\r\n" + "cd " + tempPath
				+ "\r\n" + "pdftk.exe " + sb.toString() + " cat output "
				+ caseNo + ".pdf \r\n exit";
		try {
			FileTool.setByte(tempPath + "\\pdf.bat", s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// ִ���������ļ�
		PDFODITool tool = new PDFODITool();
		TParm p = new TParm(tool.exec(tempPath + "\\pdf.bat"));
		if (p.getErrCode() != 0) {
			messageBox_(p.getErrText());
			System.out.println(p.getErrText());
			return;
		}
		// jacobTool.setBookmarks(tempPath + "\\data\\" + caseNo + ".pdf",
		// list);
		try {
			data = FileTool.getByte(tempPath + "\\" + caseNo + ".pdf");
			FileTool.setByte(tempPath + "\\" + caseNo + ".pdf", data);
		} catch (Exception e) {
		}
	}

	/**
	 * ���
	 */
	public void onReader() {
		File file = new File(tempPath + "\\" + caseNo + ".pdf");

		if (!file.exists())
			messageBox_("��δ�������������� ");
		// FileTool.setByte(tempPath+"\\temp.pdf", data);
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + caseNo + ".pdf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void onSubmit() {
		TParm check = new TParm();
		TParm parm = (TParm) this.getParameter();
		check.setData("MR_NO", parm.getValue("MR_NO"));
		String bigFilePath = TConfig.getSystemValue("FileServer.Main.Root")
				+ "\\��ʽ����\\" + parm.getValue("MR_NO").substring(0, 7) + "\\"
				+ parm.getValue("MR_NO") + "\\" + caseNo + ".pdf";
		File file = new File(tempPath + "/" + caseNo + ".pdf");

		byte data[];
		try {
			data = FileTool.getByte(file);

			if (TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
					bigFilePath, data)) {

				// PdfTool.getInstance().insertMRV(check);
				messageBox_("�ύ�ɹ�!");
				return;
			}

			messageBox_("�ύʧ��!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			messageBox_("�ύʧ��!");
		}

	}

	/**
	 * ɾ����ʱ����������
	 */
	public void onDelTempPDF() {

		// �ӱ����ȡѡ�е��ļ���
		TTable table = (TTable) getComponent("serverDocTable");
		int row = table.getSelectedRow();
		if (row < 0) {
			messageBox("��ѡ��Ҫɾ�����ļ�!");
			return;
		}

		if (this.messageBox("ѯ��", "ȷ��Ҫɾ���ļ���", 2) == 0) {
			// ȡ�������ļ�·
			TParm p = table.getParmValue();
			String fileName = p.getValue("FileName", row);
			// Ҫɾ�����ļ�
			String filePath = serverPath + "\\" + fileName;

			// ��������������Ŀ¼;
			boolean isCreateDir = TIOM_FileServer.mkdir(TIOM_FileServer
					.getSocket(), fileServerRoot + "\\pdfbackup");
			if (isCreateDir) {
				// �ƶ��ļ�������Ŀ¼;
				// ȡ����˶�Ӧ�ļ���
				byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
						.getSocket(), filePath);
				if (data == null) {
					messageBox("�����û���ҵ�Ҫɾ�����ļ�!");
					return;
				}
				// д�ļ�������˱���Ŀ¼;
				boolean isBuckupFLG = TIOM_FileServer.writeFile(TIOM_FileServer
						.getSocket(), fileServerRoot + "\\pdfbackup\\"
						+ fileName, data);
				if (!isBuckupFLG) {
					messageBox("����˱����ļ�ʧ��!");
					return;
				}

				// ɾ���ļ�
				boolean isDelFLG = TIOM_FileServer.deleteFile(TIOM_FileServer
						.getSocket(), filePath);
				if (isDelFLG) {
					// ˢ�±��,ͬ��ɾ�������;
					table.removeRow(row);
					messageBox("ɾ���ļ��ɹ�!");

				} else {
					messageBox("ɾ���ļ�ʧ��!");
					return;
				}

			} else {
				messageBox("������������������Ŀ¼ʧ��!");
				return;
			}

		}

	}

}
