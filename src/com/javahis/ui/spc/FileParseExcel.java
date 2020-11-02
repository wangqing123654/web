package com.javahis.ui.spc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


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
		String path = "C://expSaleInfo_J9222689_20131101104225.xls";
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
//			System.out.println("22222");
			HSSFRow row = hssfSheet.getRow(0);
			Map<String,Integer> map = new HashMap<String,Integer>();
			for(int i=0;i<70;i++) {
				map.put(getValue(row.getCell(i)), i);
			}
			  					
			for (int rowNum =1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
				if (hssfRow == null) {
					continue;
				}
				parm.setData("ORDER_CODE", rowNum, getValue(hssfRow.getCell( map.get("GOODCODE"))));
				
				parm.setData("ORDER_DESC", rowNum,getValue(hssfRow.getCell( map.get("F_DRUG_NAME"))));
				// ���ռ۸�
				parm.setData("PURORDER_PRICE", "");
				//�����������汾
				//parm.setData("PURORDER_PRICE", getValue(hssfRow.getCell(47)));
				// ��������
				parm.setData("PURORDER_QTY", rowNum, getValue(hssfRow.getCell( map.get("SUM_NUM"))));
				// ���۵���
				parm.setData("PURORDER_NO", rowNum, getValue(hssfRow.getCell( map.get("SALBILLNO"))));				
				// Ч��
				parm.setData("VALID_DATE", rowNum, getValue(hssfRow.getCell( map.get("END_DATE"))));
				// ��Ӧ�̱���SUP_CODE
				parm.setData("SUP_CODE", rowNum, "18");
				// ��Ʊ��
				parm.setData("INVOICE_NO", rowNum, "");
				// ����
				parm.setData("BATCH_NO", rowNum, getValue(hssfRow.getCell( map.get("BATCH_CODE"))));
				// �ɹ�����ID��
//				parm.setData("PURORDER_NO", rowNum, getValue(hssfRow.getCell(1)));
				// ��Ʊ����
				parm.setData("INVOICE_DATE", rowNum, "");						
				// ������
				parm.setData("MAN_CODE", rowNum, getValue(hssfRow.getCell( map.get("MANUFACTORY_NAME"))));
				// ��ת��
				parm.setData("SPC_BOX_BARCODE", rowNum, getValue(hssfRow.getCell( map.get("BAR_CODE"))));
				// ��ҩERP����
				parm.setData("ERP_PACKING_ID", rowNum, getValue(hssfRow.getCell( map.get("ERP_PACKING_ID"))));
				
				parm.setData("PRC", rowNum, getValue(hssfRow.getCell( map.get("PRC"))));						
				/*parm.setData("ORDER_CODE", rowNum, getValue(hssfRow.getCell(59)));

				parm.setData("ORDER_DESC", rowNum, getValue(hssfRow.getCell(62)));
				// ���ռ۸�
				parm.setData("PURORDER_PRICE", "");
				//�����������汾
				//parm.setData("PURORDER_PRICE", getValue(hssfRow.getCell(47)));
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
				
				parm.setData("PRC", rowNum, getValue(hssfRow.getCell(47)));
				// ѭ����Cell   
*/			}
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