package jdo.ins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.FileTool;
import com.javahis.util.JavaHisDebug;

import device.PassDriver;

public class INSDebugTool extends TJDOTool {

	public INSDebugTool() {
	}

	/**
	 * ʵ��
	 */
	public static INSDebugTool instanceObject;

	/**
	 * �õ�ʵ��
	 *
	 * @return INSTJTool
	 */
	public static INSDebugTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSDebugTool();
		return instanceObject;
	}

	/**
	 * INS���ݽӿ�
	 *
	 * @param parm
	 * @return
	 */
	public TParm INSInterface(TParm parm) {
           // System.out.println("��̨���"+parm);
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "jdo.ins.INSDebugTool.INSInterface->Err:���Ϊ�գ�");
			return result;
		}
		// Ŀǰ�̶�
		parm.setData("HOSP_AREA", "HIS");
		// ��������
		String interName = parm.getValue("PIPELINE");
		// ��������
		String method = parm.getValue("PLOT_TYPE");
		// ��������
		String in_outType = "IN";
		// ��������
		int parcount = parm.getInt("PARM_COUNT", 0);

		if (!this.IsinsIodata(interName, method, in_outType, parcount)) {
			result.setErr(-1, "jdo.ins.INSDebugTool.INSInterface->Err:��δ���");
			return result;
		}
		// �������ת�����ַ���
		TParm InStrParm = this.parseIn(parm);
		if (InStrParm.getErrCode() < -1) {
			result.setErr(-1, "jdo.ins.INSDebugTool.INSInterface->Err:�������ʧ�ܣ�");
			return result;
		}
		// ����ҽ���ӿ��ַ���
		String inStr = InStrParm.getValue("InStrParm");
		// debug��� 0:���� 1:��ʽ
		String debug = this.getInsDebug();
		// ��������
		if (debug.equals("0")) {
			// ����start
			try {
				// ���Ե����·��
				String indir = this.getInsInPath();
				File file = new File(indir);
				if (!file.isDirectory()) {

					file.mkdirs();

				}
				String infileName = parm.getValue("PIPELINE") + "("
						+ parm.getValue("PLOT_TYPE") + ").txt";
				FileTool.setString(indir + infileName, inStr, false);
			} catch (IOException e) {
				result.setErr(-1,
						"jdo.ins.INSDebugTool.INSInterface->Err:��������ļ�ʧ�ܣ�");
				return result;
			}
			// ���Եĳ���·��
			String outdir = this.getInsOutPath();
			String outfileName = parm.getValue("PIPELINE") + "("
					+ parm.getValue("PLOT_TYPE") + ").txt";
                              //      System.out.println("���Եĳ���·��"+outdir+outfileName);
			Object object;
			try {
				object = FileTool.getString(outdir + outfileName);
			} catch (IOException e) {
				result.setErr(-1,
						"jdo.ins.INSDebugTool.INSInterface->Err:���ν����ļ�ʧ�ܣ�");
				return result;
			}
			Vector vct = (Vector) object;
			// �����ַ���
			String outStr = vct.get(0).toString();
			parm.setData("OutStrParm", outStr);
			result = parseOut(parm);
                       // System.out.println("����"+result);
			if (result.getErrCode() < 0) {
				result.setErr(-1,
						"jdo.ins.INSDebugTool.INSInterface->Err:���ν���ʧ�ܣ�");
				return result;
			}
		} else if (debug.equals("1")) {
			// ҽ���ӿ�dll
                        //      Object obj = InsManagerTool.getInstance().safe(parm);//��ʽ�ӿ�
                     result=   InsManagerTool.getInstance().safe(parm);
		}
		return result;

	}

	/**
	 * �����������
	 *
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	private TParm parseIn(TParm parm) {
		TParm inParm = new TParm();
		// �������ݼ��
		if (parm.checkEmpty("PIPELINE,PLOT_TYPE,HOSP_AREA", parm)) {
			inParm.setErr(-1, "�����PIPELINE,PLOT_TYPE,HOSP_AREA��Ϊ��");
			return inParm;
		}
		// �õ�ҽ����������
		TParm sysParm = getTool().getSysParm(parm);
		if (sysParm.getErrCode() < 0) {
			inParm.setErr(-1, sysParm.getErrText(), sysParm.getErrName());
			return inParm;
		}
		if (sysParm.getErrCode() > 0) {
			inParm.setErr(-1, "INS_SYSPARM ����û���ҵ�Ĭ�ϵ�ҽ������!");
			return inParm;
		}

		parm.setData("sysParm", sysParm);
		parm.setData("CITY", sysParm.getData("CITY", 0));
		// ��ȡҽ���ӿں�����Ϣ
		TParm ioParm = getTool().getIOParm(parm);
		if (ioParm.getErrCode() < 0) {
			inParm.setErr(-1, ioParm.getErrText(), ioParm.getErrName());
			return inParm;
		}
		if (ioParm.getErrCode() > 0) {
			inParm.setErr(-1,
					"INS_IO_PARM ����û���ҵ�[CITY=" + parm.getValue("CITY")
							+ " PIPELINE=" + parm.getValue("PIPELINE")
							+ " PLOT_TYPE=" + parm.getValue("PLOT_TYPE")
							+ "]���������!");
			return inParm;
		}
		parm.setData("ioParm", ioParm);

		// �õ�����ӿ���������
		parm.setData("IN_OUT", "IN");
		TParm ioData = getIOData(parm);
		if (ioData.getErrCode() < 0) {
			inParm.setErr(-1, ioData.getErrText(), ioData.getErrName());
			return inParm;
		}
		int count = ioData.getCount("ID");
		StringBuffer sb = new StringBuffer();
		// Ĭ�ϵķָ��
		String separator = sysParm.getValue("SEPARATOR", 0);
		// Ĭ�ϵ��н�����
		String newline = initNewline(sysParm.getValue("NEWLINE", 0));
		// Ĭ�ϵĽ�����
		String finish = initNewline(sysParm.getValue("FINISH", 0));
		String cn = ioData.getValue("COLUMN_NAME", 0);
		if (parm.getData(cn) == null) {
			inParm.setErr(-1, "InsInterface.parse()parm ȱ�� " + cn + " "
					+ ioData.getData("COLUMN_DESC", 0) + " ��Ҫ����");
			return inParm;
		}
		int rowCount = parm.getCount(cn);
		for (int row = 0; row < rowCount; row++) {
			for (int i = 0; i < count; i++) {
				if (i > 0)
					sb.append(separator);
				String columnName = ioData.getValue("COLUMN_NAME", i);
				boolean need = ioData.getBoolean("NEED", i);
				String dataType = ioData.getValue("DATA_TYPE", i);
				String columnDesc = ioData.getValue("COLUMN_DESC", i);
				String format = ioData.getValue("FORMAT", i);
				String defaultValue = ioData.getValue("DEFAULT_VALUE", i);
				int precision = 0;
				int scale = 0;
				int length = 0;
				try {
					precision = ioData.getInt("PRECISION", i);
					scale = ioData.getInt("SCALE", i);
					length = ioData.getInt("LENGTH", i);
				} catch (Exception e) {
					inParm.setErr(
							-1,
							"INS_IO_�������ݷǷ�[CITY="
									+ parm.getValue("CITY")
									+ " PIPELINE="
									+ parm.getValue("PIPELINE")
									+ " PLOT_TYPE="
									+ parm.getValue("PLOT_TYPE")
									+ " IN_OUT=IN] �� PRECISION,SCALE,LENGTH ���д���null!");
					return inParm;
				}
				String data = parm.getValue(columnName, row);
				// ����Ҫ�����Ƿ����
				if (need && (data.length() == 0)) {
					inParm.setErr(-1, "InsInterface.parse()parm ȱ�� "
							+ columnName + " " + columnDesc + " ��Ҫ�����ڵ� " + row
							+ " ��!");
					return inParm;
				}
				// ��չ�������
				if (data.length() == 0) {
					if (defaultValue != null && defaultValue.length() > 0)
						data = defaultValue;
					else
						data = " ";
				}
				if (format != null && format.length() > 0) {
					if ("BOOLEAN".equals(format)) {
						if ("Y".equals(data))
							data = "1";
						if ("N".equals(data))
							data = "0";
						if ("".equals(data))
							data = "0";
					}
				}
				// ƴ������
				sb.append(data);
			}
			if (rowCount > 1)
				sb.append(newline);
		}
		sb.append(finish);
		inParm.setData("InStrParm", sb.toString());
		return inParm;
		// ����end
		// �õ������ӿ���������
		// parm.getData("IN_OUT", "OUT");
		// ioData = getIOData(parm);
		// if (ioData.getErrCode() < 0) {
		// System.out.println(ioData.getErrText()+" "+ioData.getErrName());
		// }
		// // ���㷵�����ݳ���
		// // ���ݿ��趨��󻺳�����
		// int row = ioParm.getInt("ROW_COUNT", 0);
		// int dataSize = 0;
		// // ����и���
		// count = ioData.getCount("ID");
		// // �ۼ�����������ֽ�
		// for (int i = 0; i < count; i++)
		// dataSize += ioData.getInt("LENGTH", i);
		// // �ۼӷָ���ͻ��з�
		// dataSize += count;
		// // ��Ԥ���趨����
		// dataSize *= row;
		// // �ӽ�����
		// dataSize += 1;
		// parm.setData("ioOutData", ioData);
		// parm.setData("RETURN_DATA_COUNT", dataSize);
		// return true;
	}

	/**
	 * ������������
	 *
	 * @param parm
	 *            TParm
	 * @param resultParm
	 *            TParm
	 * @return TParm
	 */
	public static TParm parseOut(TParm parm) {
		TParm result = new TParm();
		//System.out.println("------------����-----------"+parm);
		// �õ�ҽ����������
		TParm sysParm = INSTool.getInstance().getSysParm(parm);
		parm.setData("CITY", sysParm.getData("CITY", 0));
                parm.setData("IN_OUT","OUT");
		//System.out.println("------------sysParm��-----------" + sysParm);
		// System.out.println("sysParm" + sysParm);
		TParm ioData = INSTool.getInstance().getIOData(parm);
		//System.out.println("ioData" + ioData);
		// Ĭ�ϵķָ��
		String separator = sysParm.getValue("SEPARATOR", 0);
		// Ĭ�ϵ��н�����
		String newline = initNewline(sysParm.getValue("NEWLINE", 0));
		// Ĭ�ϵĽ�����
		String finish = initNewline(sysParm.getValue("FINISH", 0));
		// �����ַ���
		String outStr = parm.getValue("OutStrParm");

		outStr.replace("[", "");
		outStr.replace("]", "");
		if (outStr.length() == 0) {
			result.setErr(-1, "û�ж����ļ�");
			return result;
		}
		outStr = outStr.trim();
		if (outStr.endsWith(finish)) {
			outStr = outStr.substring(0, outStr.length() - finish.length());
		}
		if (!outStr.endsWith(newline))
			outStr += newline;
		// ��������
		int count = ioData.getCount("ID");
//                System.out.println("��������AAA"+count);
//                System.out.println("�����ַ���"+outStr);
		String[] sData =  parseNewLine(outStr, newline, false);
		//System.out.println("��������BBB"+sData.length);
		int head = -1;
		for (int i = 0; i < sData.length; i++) {
			String[] cData = parseNewLine(sData[i], separator, true);
			if (count == cData.length) {
				for (int j = 0; j < count; j++) {
					String columnName = ioData.getValue("COLUMN_NAME", j);
					String format = ioData.getValue("FORMAT", j);
					String dataType = ioData.getValue("DATA_TYPE", j);
					String value = cData[j];
					if ("DATE".equals(dataType))
						value = dateFormat(value, format);
					result.addData(columnName, value);
				}
				continue;
			} else if (cData.length != 2 && count != cData.length + 2) {
				if (cData.length == 3) {
					result.setErr(-1, "InsInterface.parseOut()���ز�����Ԥ���趨������������ ");
					return result;
				}
				result.setErr(-1, "InsInterface.parseOut()���ز�����Ԥ���趨������������ ");
				return result;
			}
			if (i == 0) {
				for (int j = 0; j < cData.length; j++) {
					String columnName = ioData.getValue("COLUMN_NAME", j);
					String format = ioData.getValue("FORMAT", j);
					String dataType = ioData.getValue("DATA_TYPE", j);
					String value = cData[j];
					if ("DATE".equals(dataType))
						value = dateFormat(value, format);
					result.addData(columnName, value);
					head = j + 1;
				}
				continue;
			}
			if (head > 0) {
				for (int j = 0; j < cData.length; j++) {
					String columnName = ioData
							.getValue("COLUMN_NAME", j + head);
					String format = ioData.getValue("FORMAT", j + head);
					String dataType = ioData.getValue("DATA_TYPE", j + head);
					String value = cData[j];
					if ("DATE".equals(dataType))
						value = dateFormat(value, format);
					result.addData(columnName, value);
				}
				continue;
			}
		}

		return result;
	}

	public static String dateFormat(String s, String format) {
		if (format == null)
			format = "";
		StringBuffer v = new StringBuffer();
		for (int i = 0; i < s.length(); i++)
			switch (s.charAt(i)) {
			case ' ':
			case '/':
			case '-':
			case ':':
				break;
			default:
				v.append(s.charAt(i));
			}
		String value = v.toString();
		if ("DATE".equals(format))
			if (value.length() > 8)
				value = value.substring(0, 8);
		return value;
	}

	/**
	 * �����ݲ�ֳɶ���
	 *
	 * @param s
	 *            String
	 * @param newline
	 *            String
	 * @param b
	 *            boolean
	 * @return String[]
	 */
	private static String[] parseNewLine(String s, String newline, boolean  flg) {
		List list = new ArrayList();
		if (s.startsWith(newline))
		s = s.substring(newline.length(), s.length());
		int index = s.indexOf(newline);
		while (index >= 0) {
			list.add(s.substring(0, index));
			s = s.substring(index + newline.length(), s.length());
			index = s.indexOf(newline);
		}
		if (flg)
			list.add(s);
		return (String[]) list.toArray(new String[] {});
	}
	/**
	 * ��ʼ�����з�
	 *
	 * @param s
	 *            String
	 * @return String
	 */
	public static String initNewline(String s) {
		if (s.startsWith("char(") && s.endsWith(")"))
			return ""
					+ ((char) Integer.parseInt(s.substring(5, s.length() - 1)));
		return s;
	}

	/**
	 * �õ��ӿڲ�������
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getIOData(TParm parm) {
		TParm ioData = getTool().getIOData(parm);
		if (ioData.getErrCode() < 0)
			return ioData;
		if (ioData.getErrCode() > 0) {
			ioData.setErr(-1,
					"INS_IO ����û���ҵ� [CITY=" + parm.getValue("CITY")
							+ " PIPELINE=" + parm.getValue("PIPELINE")
							+ " PLOT_TYPE=" + parm.getValue("PLOT_TYPE")
							+ " IN_OUT=" + parm.getValue("IN_OUT") + "]���������!");
			return ioData;
		}
		return ioData;
	}

	/**
	 * ���鷽������β�����ȷ�ԣ��������ã�
	 *
	 * @param interName
	 *            String ��������
	 * @param method
	 *            String �ӿڷ���
	 * @param in_outType
	 *            String ��������
	 * @param count
	 *            int ��������
	 * @return boolean ����ֵ����
	 */
	private boolean IsinsIodata(String interName, String method,
			String in_outType, int count) {
		boolean flg = true;
		StringBuffer Str = new StringBuffer();
		if (!interName.equals("")) {
			Str.append(" PIPELINE='" + interName + "'");
			Str.append(" AND ");
		}
		if (!method.equals("")) {
			Str.append(" PLOT_TYPE='" + method + "'");
			Str.append(" AND ");
		}
		// �������IN
		if (!in_outType.equals("")) {
			Str.append(" IN_OUT='" + in_outType + "'");
		}
		String sql = " SELECT COLUMN_NAME,COLUMN_DESC,DATA_TYPE,LENGTH,PRECISION,SCALE,NEED,DEFAULT_VALUE,"
				+ " FORMAT,REMARK "
				+ " FROM INS_IO"
				+ " WHERE "
				+ Str.toString();
		TParm parm = new TParm(this.getDBTool().select(sql));
		int Parmcount = parm.getCount();
		if (Parmcount < 0) {
			flg = false;
		}
		if (Parmcount != count) {
			flg = false;
		}
		return flg;
	}

	/**
	 * �������ģ��INSTool
	 *
	 * @alias �������ģ��INSTool
	 * @return INSTool
	 */
	public INSTool getTool() {
		return INSTool.getInstance();
	}

	/**
	 * getDBTool ���ݿ⹤��ʵ��
	 *
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * �õ���־·��
	 *
	 * @return
	 */
	public String getInsLogPath() {
		String path = "";
		path = getProp().getString("InsLogPath");
		if (path == null || path.trim().length() <= 0) {
			System.out.println("�����ļ���־·������");
		}
		return path;
	}

	/**
	 * �õ�debug���
	 *
	 * @return
	 */
	public String getInsDebug() {
		String debug = "";
		debug = getProp().getString("", "InsDebug");
               // System.out.println("�õ�debug���"+debug);
		if (debug == null || debug.trim().length() <= 0) {
			System.out.println("�����ļ�ҽ��debug��Ǵ���");
		}
		return debug;
	}

	/**
	 * �õ�ҽ������
	 *
	 * @return
	 */
	public String getInsType() {
		String type = "";
		type = getProp().getString("", "InsType");
		if (type == null || type.trim().length() <= 0) {
			System.out.println("�����ļ�ҽ�����ʹ���");
		}
		return type;
	}

	/**
	 * �õ�ҽ��������Ip
	 *
	 * @return
	 */
	public String getInsHost() {
		String ip = "";
		ip = getProp().getString("", "InsHost");
		if (ip == null || ip.trim().length() <= 0) {
			System.out.println("�����ļ�ҽ��������Ip����");
		}
		return ip;
	}

	/**
	 * �õ�ҽ���������˿�
	 *
	 * @return
	 */
	public String getInsPort() {
		String port = "";
		port = getProp().getString("", "InsPort");
		if (port == null || port.trim().length() <= 0) {
			System.out.println("�����ļ�ҽ���������˿ڴ���");
		}
		return port;
	}

	/**
	 * �õ��������·��
	 *
	 * @return
	 */
	public String getInsInPath() {
		String inPath = "";
		inPath = getProp().getString("", "InsInPath");
               // System.out.println("·��"+inPath);
		if (inPath == null || inPath.trim().length() <= 0) {
			System.out.println("�����ļ��������·������");
		}
		return inPath;
	}

	/**
	 * �õ����Գ���·��
	 *
	 * @return
	 */
	public static String getInsOutPath() {
		String outPath = "";
		outPath = getProp().getString("", "InsOutPath");
		if (outPath == null || outPath.trim().length() <= 0) {
			System.out.println("�����ļ��õ����Գ���·������");
		}
		return outPath;
	}

	/**
	 * ��ȡ TConfig.x
	 *
	 * @return TConfig
	 */
	public static TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		if (config == null) {
			System.out.println("TConfig.x �ļ�û���ҵ���");
		}
		return config;
	}

	public static void main(String[] args) {
		// System.out.println("------------------"+i);
		String s="-1|SUCCESS|MT05511111259865207|0003654840|6013820200805581213|01|����Ӣ|2" +
				" |120106194704067027|1947-04-06 00:00:00|64|21|00028145|E0134043|����к�ƽ����" +
				"�����������һ��|13|1|105025.93|150000|0|0|.1|0|.1|.2| |01| |0| ";

		JavaHisDebug.initServer();
		TParm parm = new TParm();
		String outdir = "D:\\log\\INS_OUT\\";
		String outfileName = "DataDown_mts(E).txt";
		Object object = null;
		try {
			object = FileTool.getString(outdir + outfileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Vector vct = (Vector) object;
		// �����ַ���
		String outStr = vct.get(0).toString();
		//System.out.println("--------------------------" + outStr);
		parm.setData("OutStrParm", outStr);
		parm.setData("HOSP_AREA", "HIS");
		parm.setData("PIPELINE", "DataDown_mts");
		parm.setData("PLOT_TYPE", "E");
		parm.setData("IN_OUT", "OUT");
		parm = parseOut(parm);
		//System.out.println("--------------------------" + parm);
	}
}
