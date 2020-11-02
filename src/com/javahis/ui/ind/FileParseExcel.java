package com.javahis.ui.ind;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jdo.ind.INDTool;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.dongyang.data.TParm;

public class FileParseExcel {
    /**
     * ʵ��
     */
    public static FileParseExcel instanceObject;

    private static final String SHEET_NAME = "ERP_PACKING_INFO";
    /**
     * �õ�ʵ��
     *
     * @return INDTool
     */
    public static FileParseExcel getInstance() {
        if (instanceObject == null)
            instanceObject = new FileParseExcel();
        return instanceObject;
    }


	public static void main(String[] args) throws IOException {
		FileParseExcel aExcel = new FileParseExcel();
		String path = "C:/Users/liyanhui/Desktop/28.xls";
		aExcel.readXls(path);
	}

	public  TParm readXls(String filePath) throws IOException {
//		System.out.println("--------");
		InputStream is = new FileInputStream(filePath);
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
		TParm parm = new TParm();
		// ѭ��������Sheet
		// for(int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets();
		// numSheet++){
		for (int numSheet =0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
			String sheetName = hssfSheet.getSheetName();
//			System.out.println("hssfSheet: " + hssfSheet.getSheetName());
			if (hssfSheet == null) {
				continue;
			}

			if(!SHEET_NAME.equals(sheetName)){
				continue;
			}
			// ѭ����Row
			for (int rowNum =1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
				if (hssfRow == null) {
					continue;
				}
				parm.setData("ORDER_CODE", rowNum, getValue(hssfRow.getCell(59)));
				// ���ռ۸�
				parm.setData("PURORDER_PRICE", "");
				// ��������
				parm.setData("PURORDER_QTY", rowNum, getValue(hssfRow.getCell(8)));
				// ���۵���
				parm.setData("PURORDER_NO", rowNum, getValue(hssfRow.getCell(9)));				
				// Ч��
				parm.setData("VALID_DATE", rowNum, getValue(hssfRow.getCell(6)));
				// ��Ӧ�̱���SUP_CODE
				parm.setData("SUP_CODE", rowNum, "18");
				// ��Ʊ��
				parm.setData("INVOICE_NO", rowNum, "");
				// ����
				parm.setData("BATCH_NO", rowNum, getValue(hssfRow.getCell(2)));
				// �ɹ�����ID��
//				parm.setData("PURORDER_NO", rowNum, getValue(hssfRow.getCell(1)));
				// ��Ʊ����
				parm.setData("INVOICE_DATE", rowNum, "");
				// ������
				parm.setData("MAN_CODE", rowNum, getValue(hssfRow.getCell(15)));
				// ��ת��
				parm.setData("SPC_BOX_BARCODE", rowNum, getValue(hssfRow.getCell(3)));
				// ��ҩERP����
				parm.setData("ERP_PACKING_ID", rowNum, getValue(hssfRow.getCell(0)));
				// ѭ����Cell
/*				int i = 0;
				for (int cellNum = 0; cellNum <= hssfRow.getLastCellNum(); cellNum++) {
					HSSFCell hssfCell = hssfRow.getCell(cellNum);
					if (hssfCell == null) {
						continue;
					}

					System.out.print(i + "    " + getValue(hssfCell) + "; ");
					i++;
				}*/
//				if (rowNum == 3)
//					break;
				System.out.println();
			}
//			System.out.println("-----------parm: " + parm);
		}
		return parm;
	}

	@SuppressWarnings("static-access")
	private static String getValue(HSSFCell hssfCell) {
		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
			return String.valueOf(hssfCell.getNumericCellValue());
		} else {
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}

}