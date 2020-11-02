package jdo.ins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.FileTool;

public class insDatatestTool extends TJDOTool {
	public static boolean DEBUG = true;
	/**
	 * ʵ��
	 */
	public static insDatatestTool instanceObject;

	/**
	 * �õ�ʵ��
	 *
	 * @return INSOpdApproveTool
	 */
	public static insDatatestTool getInstance() {
		if (instanceObject == null)
			instanceObject = new insDatatestTool();
		return instanceObject;
	}

	public insDatatestTool() {
		super();
		this.onInit();
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

	public TParm safe(TParm parm) {
		// log("safe()", "start input TParm");
		TParm result = new TParm();
		//System.out.println("INSInterface������Ρ���������������������������������������-" + parm);
//		if (!IsinsIodata(parm.getValue("PIPELINE"), parm.getValue("PLOT_TYPE"),
//				"IN", parm.getInt("PARM_COUNT", 0)))
//			return result;
                    if (!isInit())
                        if (!init()) {
                            result.setErr( -1, "ҽ��������û��׼����!");
                            return result;
                        }
		// System.out.println("���ж�@@@@@@@@@@");
		//System.out.println("�ӿڷ������parm" + parm);
		//System.out.println("�ӿڷ������result" + result);
		result.setData("PIPELINE", parm.getData("PIPELINE"));
		result.setData("PLOT_TYPE", parm.getData("PLOT_TYPE"));
		result.setData("HOSP_AREA", parm.getData("HOSP_AREA"));
		// ������������
		if (parseIn(parm, result))
			return result;
		// ��ӿڴ�������
		result = linkFunction(parm);
		if (result.getErrCode() < 0)
			return result;

		// ������ֵ
		// String returnValue = result.getReturnValue("_RETURN_");

		// if(!"0".equals(returnValue))
		// {
		// result.setErr(-1,"ҽ���������ؽ���쳣=" + returnValue);
		// return result;
		// }

		// ������������
		result = parseOut(parm, result);
		String returnValue = result.getValue("PROGRAM_STATE", 0);
		if (returnValue == null || returnValue.length() == 0)
			return result;
		try {
			if (Integer.parseInt(returnValue) < 0) {
				result.setErr(-1, result.getValue("PROGRAM_MESSAGE", 0));
				return result;
			}
		} catch (Exception e) {
			result.setErr(-1, e.getMessage());
			return result;
		}
		return result;
	}

	/**
	 * �����������
	 *
	 * @param parm
	 *            TParm
	 * @param result
	 *            TParm
	 * @return boolean
	 */
	private boolean parseIn(TParm parm, TParm result) {
		//System.out.println("�����������" + parm);
		// �������ݼ��
		if (parm.checkEmpty("PIPELINE,PLOT_TYPE,HOSP_AREA", result)) {
			return false;
		}
		// �õ�ҽ����������
		TParm sysParm = getTool().getSysParm(parm);
		if (sysParm.getErrCode() < 0) {
			result.setErr(-1, sysParm.getErrText(), sysParm.getErrName());
			return false;
		}
		if (sysParm.getErrCode() > 0) {
			result.setErr(-1, "INS_SYSPARM ����û���ҵ�Ĭ�ϵ�ҽ������!");
			return false;
		}

		parm.setData("sysParm", sysParm);
		parm.setData("CITY", sysParm.getData("CITY", 0));
		// ��ȡҽ���ӿں�����Ϣ
		TParm ioParm = getTool().getIOParm(parm);
		//System.out.println("-----------0000--------------" + ioParm);
		if (ioParm.getErrCode() < 0) {
			result.setErr(-1, ioParm.getErrText(), ioParm.getErrName());
			return false;
		}
		if (ioParm.getErrCode() > 0) {
			result.setErr(-1,
					"INS_IO_PARM ����û���ҵ�[CITY=" + parm.getValue("CITY")
							+ " PIPELINE=" + parm.getValue("PIPELINE")
							+ " PLOT_TYPE=" + parm.getValue("PLOT_TYPE")
							+ "]���������!");
			return false;
		}
		parm.setData("ioParm", ioParm);

		// �õ�����ӿ���������
		parm.setData("IN_OUT", "IN");
		TParm ioData = getIOData(parm);
		//System.out.println("-----------11111--------------" + ioData);
		if (ioData.getErrCode() < 0) {
			result.setErr(-1, ioData.getErrText(), ioData.getErrName());
			return false;
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
			result.setErr(-1, "InsInterface.parse()parm ȱ�� " + cn + " "
					+ ioData.getData("COLUMN_DESC", 0) + " ��Ҫ����");
			return false;
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
					result.setErr(
							-1,
							"INS_IO_�������ݷǷ�[CITY="
									+ parm.getValue("CITY")
									+ " PIPELINE="
									+ parm.getValue("PIPELINE")
									+ " PLOT_TYPE="
									+ parm.getValue("PLOT_TYPE")
									+ " IN_OUT=IN] �� PRECISION,SCALE,LENGTH ���д���null!");
					return false;
				}
				String data = parm.getValue(columnName, row);
				//System.out.println("-----------5555--------------" + i);
				//System.out.println("-----------5555--------------" + parm);
				// ����Ҫ�����Ƿ����
				if (need && (data.length() == 0)) {
					result.setErr(-1, "InsInterface.parse()parm ȱ�� "
							+ columnName + " " + columnDesc + " ��Ҫ�����ڵ� " + row
							+ " ��!");
					return false;
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

		parm.setData("PARAM", sb.toString());
//		// ����start
//		try {
//			String fileName = parm.getValue("PIPELINE") + "_"
//					+ parm.getValue("PLOT_TYPE");
//			String subValue = sb.toString();
//			FileTool.setString("D:\\log\\INS_IN\\" + fileName + ".txt",
//					subValue, false);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		// ����end
		// �õ������ӿ���������
		parm.setData("IN_OUT", "OUT");
		ioData = getIOData(parm);
		if (ioData.getErrCode() < 0) {
			result.setErr(-1, ioData.getErrText(), ioData.getErrName());
			return false;
		}
		// ���㷵�����ݳ���
		// ���ݿ��趨��󻺳�����
		int row = ioParm.getInt("ROW_COUNT", 0);
		int dataSize = 0;
		// ����и���
		count = ioData.getCount("ID");
		// �ۼ�����������ֽ�
		for (int i = 0; i < count; i++)
			dataSize += ioData.getInt("LENGTH", i);
		// �ۼӷָ���ͻ��з�
		dataSize += count;
		// ��Ԥ���趨����
		dataSize *= row;
		// �ӽ�����
		dataSize += 1;
		parm.setData("ioOutData", ioData);
		parm.setData("RETURN_DATA_COUNT", dataSize);
		return true;
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
	public TParm parseOut(TParm parm, TParm resultParm) {
		//System.out.println("������������111111111111" + parm);
		//System.out.println("������������222222222222" + resultParm);
		TParm result = new TParm();
		// �õ�ҽ����������
		TParm sysParm = (TParm) parm.getData("sysParm");
		// TParm ioParm = (TParm) parm.getData("ioParm");
		TParm ioData = (TParm) parm.getData("ioOutData");
		// Ĭ�ϵķָ��
		String separator = sysParm.getValue("SEPARATOR", 0);
		// Ĭ�ϵ��н�����
		String newline = initNewline(sysParm.getValue("NEWLINE", 0));
		// Ĭ�ϵĽ�����
		String finish = initNewline(sysParm.getValue("FINISH", 0));
		byte[] byteData = (byte[]) resultParm.getData("RETURN_DATA");
		String data = new String(byteData);
		if (data.length() == 0) {
			result.setErr(-1, resultParm.getValue("_RETURN_"));
			return result;
		}
		/*
		 * System.out.println(" finish = " + finish); int index =
		 * data.lastIndexOf(finish); System.out.println("# index = " + index);
		 * if(index >= 0) data = data.substring(0,index);
		 * if(!data.substring(data.length() - newline.length()).equals(newline))
		 * data += newline;
		 */

		data = data.trim();
		if (data.endsWith(finish)) {
			data = data.substring(0, data.length() - finish.length());
		}
		if (!data.endsWith(newline))
			data += newline;
		int count = ioData.getCount("ID");
		String[] sData = parseNewLine(data, newline, false);
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
				// cData.length = 2Ϊ���з��ز���
				// count != cData.length + 2 ���ز���������char(6)�ָ���
				if (cData.length == 3) {
					result.setErr(-1, trim(cData[0]) + "," + trim(cData[1])
							+ "," + trim(cData[2]));
					return result;
				}
				result.setErr(-1, "InsInterface.parseOut()���ز�����Ԥ���趨������������ "
						+ " [row=" + i + "][Data=" + count + " DB="
						+ cData.length + "] " + trim(data));
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

	public String dateFormat(String s, String format) {
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
	private String[] parseNewLine(String s, String newline, boolean b) {
		List list = new ArrayList();
		if (s.startsWith(newline))
			s = s.substring(newline.length(), s.length());
		int index = s.indexOf(newline);
		while (index >= 0) {
			list.add(s.substring(0, index));
			s = s.substring(index + newline.length(), s.length());
			index = s.indexOf(newline);
		}
		if (b)
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
	public String initNewline(String s) {
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
	 * ��ʼ��ҽ���ӿ�
	 *
	 * @return boolean<BR>
	 *         true ��ʼ���ɹ�<BR>
	 *         false ��ʼ��ʧ��
	 */
	private boolean init() {
		TParm parm = new TParm();
		// �õ� TConfig.x
		TConfig prop = getProp();
		if (prop == null) {
			System.out.println("TConfig.x �ļ�û���ҵ�:");
			return false;
		}
		// �ӿ���־���Ŀ¼�ļ�
		String insLogPath = prop.getString("", "InsLogPath");
		// ҽ������ 0 ��ʽҽ���ӿ� 1 ����ҽ���ӿ�
		String insType = prop.getString("", "InsType");

		// �Ƿ�����ӿ���־
		String insDebug = prop.getString("", "InsDebug");

		if (insLogPath == null || insLogPath.length() == 0) {
			System.out.println("TConfig.x insLogPath ����û�����");
			insDebug = "0";
		}
		if (insType == null || insType.length() == 0)
			System.out.println("TConfig.x insType ����û�����");
		if (insDebug == null || insDebug.length() == 0)
			System.out.println("TConfig.x insDebug ����û�����");

		int type = 0;
		int debug = 0;

		try {
			type = Integer.parseInt(insType);
		} catch (Exception e) {
			System.out.println("TConfig.x insType ����û�����");
		}
		try {
			debug = Integer.parseInt(insDebug);
		} catch (Exception e) {
			System.out.println("TConfig.x insDebug ����û�����");
		}

		// ���� InsServer ���ò���
		parm.setData("_ACTION_", 0x2);
		parm.setData("_TYPE_", type);
		parm.setData("_PATH_", insLogPath);
		parm.setData("_ISDEBUG_", debug);
		//System.out.println("doSocket11111111111");
		TParm result = doSocket(parm);
		if (result.getErrCode() == -1)
			return false;

		return result.getBoolean("_IS_INIT_");
	}

	/**
	 * InsServer �Ƿ�ɹ���ʼ��ҽ���ӿڵ�DLL
	 *
	 * @return boolean true �Ѿ���ʼ�� false û�г�ʼ��
	 */
	private boolean isInit() {
		TParm parm = new TParm();
		parm.setData("_ACTION_", 0x1);
		//System.out.println("doSocket222222222222");
		//System.out.println("isInit>>parm" + parm);
		TParm result = doSocket(parm);
		//System.out.println("isInit>>result" + result);
		if (result.getErrCode() == -1)
			return false;
		return result.getBoolean("_IS_INIT_");
	}

	public TParm linkFunction(Object parm) {
		TParm result = new TParm();
		// ��˲����Ƿ�Ϊ��
		if (parm == null) {
			result.setErr(-1, "action.ins.InsInterface.linkFunction->Err:����Ϊ��");
			return result;
		}
		// ��˲��������Ƿ���ȷ
		if (!(parm instanceof TParm)) {
			result.setErr(-1,
					"action.ins.InsInterface.linkFunction->Err:�������ʹ���");
			return result;
		}
		TParm actionParm = (TParm) parm;
		return linkFunction(actionParm);
	}

	/**
	 * ���� InsServer �Ľӿں���
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm linkFunction(TParm parm) {
		parm.setData("_ACTION_", 0x3);
		//System.out.println("doSocket3333333333   " + parm);
		// parm.setData("ROW_COUNT",parm.getParm("ioParm").getData("ROW_COUNT",0));
		TParm parmm = new TParm();
		parmm.setData("sysParm", ((TParm) parm.getData("sysParm")).getData());
		parmm.setData("ioOutData",
				((TParm) parm.getData("ioOutData")).getData());
		parmm.setData("ioParm", ((TParm) parm.getData("ioParm")).getData());
		parmm.setData("_ACTION_", 0x3);
		parmm.setData("RETURN_DATA_COUNT", parm.getData("RETURN_DATA_COUNT"));
		parmm.setData("PIPELINE", parm.getData("PIPELINE"));
		parmm.setData("PLOT_TYPE", parm.getData("PLOT_TYPE"));
		parmm.setData("IN_OUT", parm.getData("IN_OUT"));
		parmm.setData("CITY", parm.getData("CITY"));
		parmm.setData("HOSP_AREA", parm.getData("HOSP_AREA"));
		parmm.setData("TEXT", parm.getData("TEXT"));
		parmm.setData("PARAM", parm.getData("PARAM"));

		TParm result = doSocket(parmm);
		if (result.getErrCode() == -1)
			return result;

		return result;
	}

	/**
	 * ��ȡ TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		// TConfig config = new TConfig(
		// "%ROOT%\\WEB-INF\\config\\system\\TConfig.x",
		// TIOM_AppServer.SOCKET);
		return config;
	}

	/**
	 * Socket ���� InsServer
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm doSocket(TParm parm) {
		//System.out.println("Socket ���� InsServer");
		TParm result = new TParm();

		// �õ� TConfig.x
		TConfig prop = getProp();
		if (prop == null) {
			result.setErr(-1, "TConfig.x �ļ�û���ҵ�:");
			return result;
		}

		// InsServer IP
		String insHost = prop.getString("", "InsHost");
		// InsServer Port
		String insPort = prop.getString("", "InsPort");

		// check Ins Parm from TConfig.x
		if (insHost == null || insHost.length() == 0) {
			result.setErr(-1, "TConfig.x InsHost����û�����");
			return result;
		}
		if (insPort == null || insPort.length() == 0) {
			result.setErr(-1, "TConfig.x InsPort����û�����");
			return result;
		}
		int port = 0;
		try {
			port = Integer.parseInt(insPort);
		} catch (Exception e) {
			result.setErr(-1, "TConfig.x InsPort��������");
			return result;
		}
		// call socket
		// Object obj = DtAction.doSocket(insHost,port,parm);
		TSocket socket;
		socket = new TSocket(insHost, port);
		//System.out.println("socketIP" + insHost + "PORT" + port);
		//System.out.println("����socket����111" + parm);
		//System.out.println("����socket����222" + parm.getData());
		Object obj = socket.doSocket(parm.getData());
		//System.out.println("socket����" + obj);
		if (obj == null) {
			result.setErr(-1, "ҽ��������û������!");
			return result;
		}
		result = new TParm((Map) obj);
		return result;
	}

	public String trim(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ((int) c != 0)
				sb.append(c);
		}
		return sb.toString();
	}

//	public void log(String function, String text) {
//		if (DEBUG)
//			System.out.println(getClass().getName() + "." + function + "->"
//					+ text);
//	}

	/**
	 * ���鷽���в�����ȷ�ԣ��������ã�
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
		if (!in_outType.equals("")) {
			Str.append(" IN_OUT='" + in_outType + "'");
		}
		String sql = " SELECT COLUMN_NAME,COLUMN_DESC,DATA_TYPE,LENGTH,PRECISION,SCALE,NEED,DEFAULT_VALUE,"
				+ " FORMAT,REMARK "
				+ " FROM INS_IO"
				+ " WHERE "
				+ Str.toString();
		//System.out.println("�������1111SQL___---------"+sql);
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
	 * getDBTool ���ݿ⹤��ʵ��
	 *
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
}
