package com.javahis.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSON;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * @author litong
 * @Date 2013-1-24
 */
public class ChartUtils {
	
	/** ��״ͼ */
	public static String PIE_CHART = "1";
	
	/** ��״ͼ */
	public static String BAR_CHART = "2";
	
	/** ����ͼ */
	public static String LINE_CHART = "3";
	
	/**
	 * @param table
	 *            TTable����
	 * @param sDate
	 *            ��ʼ���ڻ���ͳ���Ǹ�����
	 * @param eDate
	 *            �������� ������´��롰������
	 * @param title
	 *            ģ������
	 */
	@SuppressWarnings("unchecked")
	public static void name(TTable table, String sDate, String eDate,
			String title) {
		TParm parm = table.getShowParmValue();
		List<Map<String, String>> lt = new ArrayList<Map<String, String>>();
		Map map = parm.getData();
		String nameArray[] = StringTool.parseLine(table.getParmMap(), ";");
		Map<String, Vector<String>> map2 = (Map<String, Vector<String>>) map
				.get("Data");
		for (int i = 0; i < table.getRowCount(); i++) {
			Map<String, String> mainMap = new HashMap<String, String>();
			for (int j = 0; j < nameArray.length; j++) {
				mainMap.put(nameArray[j], map2.get(nameArray[j]).get(i));
			}
			lt.add(mainMap);
		}
		JsonModule jsonModule = new JsonModule();
		jsonModule.setTitle(getHead(table));
		jsonModule.setList(lt);

		if ("".equals(eDate)) {
			jsonModule.setKind("0");
			jsonModule.setDate(sDate);
		} else {
			jsonModule.setKind("1");
			jsonModule.setSdate(sDate);
			jsonModule.setEdate(eDate);
		}
		jsonModule.setName(title);
		String cString = JSON.toJSONString(jsonModule);
		String fileName = UUID.randomUUID().toString();
		File file = new File("////C://" + fileName + ".txt");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter resultFile = null;
		try {
			resultFile = new FileWriter(file);
			PrintWriter myNewFile = new PrintWriter(resultFile);
			myNewFile.println(cString);
			resultFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Runtime rt = Runtime.getRuntime();

		try {
			Process proc = rt
					.exec("C:\\Program Files\\Internet Explorer\\iexplore.exe http://127.0.0.1:8080/web/FlexServlet?fileName="
							+ fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Map getHead(TTable table) {
		String[] tStrings = table.getHeader().split(";");
		String nameArray[] = StringTool.parseLine(table.getParmMap(), ";");
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < tStrings.length; i++) {
			map.put(nameArray[i], tStrings[i].split(",")[0]);
		}
		return map;
	}

	/**
	 * ����ͳ��ͼ��
	 * 
	 * @param parm ��װ����
	 * @param type ͼ������
	 */
	public static void createChart(TParm parm, String type) {
		parm.setData("TYPE", type);
		// ���û�ͼAction����ͳ��ͼ��
		TParm result = TIOM_AppServer.executeAction("action.sys.SYSChartAction", "createChart",
				parm);
		
		// �õ���ǰʹ�õ�ip��ַ
		String ip = TIOM_AppServer.SOCKET.getServletPath("ChartWebInitServlet");
		String title = parm.getValue("TITLE");
		title = (new BASE64Encoder()).encode(title.getBytes());
		ip = ip + "?fileName=" + result.getValue("FILE_NAME") + "&title="
				+ title;
		try {
			Runtime.getRuntime().exec(
					"rundll32 url.dll,FileProtocolHandler " + ip);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
