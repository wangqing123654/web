package jdo.sys;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: ���ɰ�ҩ��/��ҩ��txt�ļ�
 * </p>
 * 
 * <p>
 * Description: ���ɰ�ҩ��/��ҩ��txt�ļ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * bluecore
 * </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class PHATXATCTool {
	/**
	 * �հ�1�ֽ�
	 */
	private String blankSpace = " ";
	/**
	 * �س���
	 */
	private String enter = "" + (char) 13 + (char) 10;
	/**
	 * ʵ��
	 */
	public static PHATXATCTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return OperatorTool
	 */
	public static PHATXATCTool getInstance() {
		if (instanceObject == null)
			instanceObject = new PHATXATCTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public PHATXATCTool() {
	}

	/**
	 * ȡ�ð�ҩ������
	 * 
	 * @return String
	 */
	public String getATCNo() {
		return SystemTool.getInstance().getNo("ALL", "PHA", "ATC_NO", "ATC_NO");
	}

	/**
	 * �����Ͱ�ҩ��txt�ļ�(����)
	 * 
	 * @return boolean
	 */
	public boolean generateOldATCTxtO(TParm parm) {
		TParm drugListParm = parm.getParm("DRUG_LIST_PARM");
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < drugListParm.getCount("ORDER_CODE"); i++) {

			// ��ҩ��
			stringBuffer.append(getPrescriptNo(drugListParm.getValue(
					"PRESCRIPT_NO", i)));
			stringBuffer.append(blankSpace);

			// ��������
			stringBuffer.append(this.getPatName(drugListParm.getValue(
					"PAT_NAME", i)));
			stringBuffer.append(blankSpace);

			// ������
			stringBuffer
					.append(this.getMrNo(drugListParm.getValue("MR_NO", i)));
			stringBuffer.append(blankSpace);

			// ��ҩ����
			String date = StringTool.getString(StringTool.getTimestamp(
					drugListParm.getValue("DATE", i).substring(0, 19),
					"yyyy-MM-dd HH:mm:ss"), "yyyyMMdd");
			stringBuffer.append(this.getAtcDate(date));
			stringBuffer.append(blankSpace);

			// ���
			stringBuffer.append(this.getSeq(drugListParm.getValue("SEQ", i)));
			stringBuffer.append(blankSpace);

			// ҩƷ����
			stringBuffer.append(this.getOrderCode(drugListParm.getValue(
					"ORDER_CODE", i)));
			stringBuffer.append(blankSpace);

			// ҩƷ��Ʒ��
			stringBuffer.append(this.getOrderdesc(drugListParm.getValue(
					"ORDER_GOODS_DESC", i)));
			stringBuffer.append(blankSpace);

			// ����
			stringBuffer.append(this.getQty(drugListParm.getDouble("QTY", i)));
			stringBuffer.append(blankSpace);

			// ��ҩƵ��
			stringBuffer.append(this.getFreqCode(drugListParm.getValue("FREQ",
					i)));
			stringBuffer.append(blankSpace);

			// ��ҩ����
			stringBuffer.append(this.getDay(drugListParm.getValue("DAY", i)));
			stringBuffer.append(blankSpace);

			// �ײ�ʱ��
			stringBuffer.append(this.getStartTime(drugListParm.getValue(
					"START_DTTM", i)));
			stringBuffer.append(blankSpace);

			// �Ͱ�ע��
			stringBuffer.append(this.getFlg(drugListParm.getValue("FLG", i)));
			stringBuffer.append(enter);
		}
		String path = TConfig.getSystemValue("ATCPATH.O");
		String fileServerIP = TConfig.getSystemValue("FILE_SERVER_IP");
		String port = TConfig.getSystemValue("PORT");
		Timestamp timestamp = SystemTool.getInstance().getDate();
		String timestampStr = timestamp.toString().replaceAll("-", "");
		timestampStr = timestampStr.replaceAll(" ", "");
		timestampStr = timestampStr.replaceAll(":", "");
		timestampStr = timestampStr.substring(0, 14);
		TSocket socket = new TSocket(fileServerIP, Integer.parseInt(port));
		return TIOM_FileServer.writeFile(socket, path + getATCNo() + ".txt",
				stringBuffer.toString().getBytes());
	}
	/**
	 * �����Ͱ�ҩ��txt�ļ�(סԺ)
	 * 
	 * @return boolean
	 */
	public boolean generateOldATCTxtI(TParm parm) {
		TParm drugListParm = parm.getParm("DRUG_LIST_PARM");
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < drugListParm.getCount("ORDER_CODE"); i++) {    
			// ��ҩ��
			stringBuffer.append(getPrescriptNo(drugListParm.getValue(
					"PRESCRIPT_NO", i)));
			stringBuffer.append(blankSpace);

			// ��������
			stringBuffer.append(this.getPatName(drugListParm.getValue(
					"PAT_NAME", i)));
			stringBuffer.append(blankSpace);

			// ������
			stringBuffer
					.append(this.getMrNo(drugListParm.getValue("MR_NO", i)));
			stringBuffer.append(blankSpace);

			// ��ҩ����
			String date = StringTool.getString(StringTool.getTimestamp(
					drugListParm.getValue("DATE", i).substring(0, 19),
					"yyyy-MM-dd HH:mm:ss"), "yyyyMMdd");
			stringBuffer.append(this.getAtcDate(date));
			stringBuffer.append(blankSpace);

			// ���
			stringBuffer.append(this.getSeq(drugListParm.getValue("SEQ", i)));
			stringBuffer.append(blankSpace);

			// ҩƷ����
			stringBuffer.append(this.getOrderCode(drugListParm.getValue(
					"ORDER_CODE", i)));
			stringBuffer.append(blankSpace);

			// ҩƷ��Ʒ��
			stringBuffer.append(this.getOrderdesc(drugListParm.getValue(
					"ORDER_GOODS_DESC", i)));
			stringBuffer.append(blankSpace);

			// ����
			stringBuffer.append(this.getQty(drugListParm.getDouble("QTY", i)));
			stringBuffer.append(blankSpace);

			// ��ҩƵ��
			stringBuffer.append(this.getFreqCode(drugListParm.getValue("FREQ",
					i)));
			stringBuffer.append(blankSpace);
			
			if(drugListParm.getValue("DSPN_KIND",i).equals("DS")){
				// ��ҩ����
				stringBuffer.append(this.getDay(drugListParm.getValue("DAY", i)));
				stringBuffer.append(blankSpace);
				
				// �ײ�ʱ��
				stringBuffer.append(this.getStartTime(drugListParm.getValue(
						"START_DTTM", i)));
				stringBuffer.append(blankSpace);

				// �Ͱ�ע��
				stringBuffer.append(this.getFlg(drugListParm.getValue("FLG", i)));
				stringBuffer.append(enter);
			}else{
			// �ײ�ʱ��
			stringBuffer.append(this.getStartTime(drugListParm.getValue(
					"START_DTTM", i)));
			stringBuffer.append(blankSpace);
			// �Ͱ�ע��
			stringBuffer.append(this.getFlg(drugListParm.getValue("FLG", i)));
			stringBuffer.append(enter);
			}
		}
		String path = TConfig.getSystemValue("ATCPATH.I");
		String fileServerIP = TConfig.getSystemValue("FILE_SERVER_IP");
		String port = TConfig.getSystemValue("PORT");
		Timestamp timestamp = SystemTool.getInstance().getDate();
		String timestampStr = timestamp.toString().replaceAll("-", "");
		timestampStr = timestampStr.replaceAll(" ", "");
		timestampStr = timestampStr.replaceAll(":", "");
		timestampStr = timestampStr.substring(0, 14);
		TSocket socket = new TSocket(fileServerIP, Integer.parseInt(port));
		return TIOM_FileServer.writeFile(socket, path + getATCNo() + ".txt",
				stringBuffer.toString().getBytes());
	}

	/**
	 * ��ҩ��(10 Bytes)
	 * 
	 * @param prescriptNo
	 */
	public String getPrescriptNo(String prescriptNo) {
		int lnum=prescriptNo.getBytes().length;
		if (lnum <=10) {
			for (int i = 0; i < (10 - lnum); i++) {
				prescriptNo += blankSpace;
			}
		} else {
			prescriptNo = prescriptNo.substring(0, 10);
		}
		return prescriptNo;
	}

	/**
	 * ��������(10 Bytes)
	 * 
	 * @param patName
	 */
	public String getPatName(String patName) {
		int lnum=patName.getBytes().length;
		if (lnum <=10) {
			for (int i = 0; i < (10 - lnum); i++) {
				patName += blankSpace;
			}
		} else {
			patName= new String(patName.getBytes(), 0, 10);
		}
		return patName;
	}

	/**
	 * ������(12 Bytes)
	 * 
	 * @param mrNo
	 */
	public String getMrNo(String mrNo) {
		int lnum=mrNo.getBytes().length ;
		if (lnum <=12) {
			for (int i = 0; i < (12 - lnum); i++) {
				mrNo += blankSpace;
			}
		} else {
			mrNo = mrNo.substring(0, 12);
		}
		return mrNo;
	}

	/**
	 * ��ҩ����(8 Bytes)
	 * 
	 * @param atcDate
	 */
	public String getAtcDate(String atcDate) {
		int lnum=atcDate.getBytes().length ;
		if (lnum <=8) {
			for (int i = 0; i < (8 - lnum); i++) {
				atcDate += blankSpace;
			}
		} else {
			atcDate = atcDate.substring(0, 8);
		}
		return atcDate;
	}

	/**
	 * ���(4 Bytes)
	 * 
	 * @param seq
	 */
	public String getSeq(String seq) {
		String Str = "";
		int lnum=seq.getBytes().length ;
		if (lnum <=4) {
			for (int i = 0; i < (4 - lnum); i++) {
				Str += "0";
			}
			Str += seq;
		} else {
			seq = seq.substring(0, 4);
		}
		return Str;
	}

	/**
	 * ҩƷ���� (8Bytes)
	 * 
	 * @param orderCode
	 */
	public String getOrderCode(String orderCode) {
		int lnum=orderCode.getBytes().length ;
		if (lnum <=8) {
			for (int i = 0; i < (8 - lnum); i++) {
				orderCode += blankSpace;
			}
		} else {
			orderCode = orderCode.substring(0, 8);
		}
		return orderCode;
	}

	/**
	 * ҩƷ����(30 Bytes)
	 * 
	 * @param orderdesc
	 */
	public String getOrderdesc(String orderdesc) {
		int lnum=orderdesc.getBytes().length ;
		if (lnum <=30) {
			for (int i = 0; i < (30 - lnum); i++) {
				orderdesc += blankSpace;
			}
		} else {
			orderdesc = new String(orderdesc.getBytes(), 0, 30);
		}
		return orderdesc;
	}

	/**
	 * ����(7 Bytes)
	 * 
	 * @param qty
	 */
	public String getQty(double qty) {
		String str = "";
		BigDecimal sf = new BigDecimal(String.valueOf(qty));
		BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
		int lnum=String.valueOf(data).getBytes().length ;
		if (lnum <=7) {
			for (int i = 0; i < (7 - lnum); i++) {
				str += "0";
			}
			str += data;
		} else {
			str = String.valueOf(data).substring(0, 7);
		}
		return str;
	}

	/**
	 * Ƶ��(14 Bytes)
	 * 
	 * @param freqCode
	 */
	public String getFreqCode(String freqCode) {
		int lnum=freqCode.getBytes().length ; ;
		if (lnum <=14) {
			for (int i = 0; i < (14 - lnum); i++) {
				freqCode += blankSpace;
			}
		} else {
			freqCode = freqCode.substring(0, 14);
			
		}
		return freqCode;
	}

	/**
	 * ����(3 Bytes)
	 * 
	 * @param day
	 */
	public String getDay(String day) {
		String str = "";
		int lnum=day.getBytes().length ; ;
		if (lnum <=3) {
			for (int i = 0; i < (3 - lnum); i++) {
				str += "0";
			}
			str += day;
		} else {
			str = day.substring(0, 3);
		}
		return str;
	}

	/**
	 * ����ʱ�䣨12 Bytes��
	 * 
	 * @param startTime
	 */
	public String getStartTime(String startTime) {
		String str = StringTool.getString(
				StringTool.getTimestamp(startTime, "yyyyMMddHHmm"),
				"yyyyMMddHHmm");
		int lnum=str.getBytes().length ;
		if (lnum <=12) {
			for (int i = 0; i < (12 - lnum); i++) {
				startTime += blankSpace;
			}
		} else {
			startTime = startTime.substring(0, 12);
		}
		return startTime;
	}

	/**
	 * ����ע�ǣ�1 Byte��
	 * 
	 * @param flg
	 */
	public String getFlg(String flg) {
		int lnum=flg.getBytes().length ;
		if (lnum <=1) {
			for (int i = 0; i < (1 - lnum); i++) {
				flg += blankSpace;
			}
		} else {
			flg = flg.substring(0, 1);
		}
		return flg;
	}
}
