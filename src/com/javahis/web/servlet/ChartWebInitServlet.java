package com.javahis.web.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Decoder;

public class ChartWebInitServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public ChartWebInitServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", -10);
		
		BASE64Decoder decode = new BASE64Decoder();
		String title = request.getParameter("title");
		if (StringUtils.isNotEmpty(title)) {
			title = new String(decode.decodeBuffer(title));
		} else {
			title = "ChartWebInitServlet";
		}
		String fileName = request.getParameter("fileName");
		
		request.setAttribute("title", title);
		request.setAttribute("fileName", fileName);
		request.getRequestDispatcher("/jsp/chart/chart.jsp").forward(request, response);
		
		// ɾ�������ļ�
		this.deleteFiles();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * ɾ�������������ļ�
	 */
	private void deleteFiles() {
		// ���Tomcat����·��
		String tempPath = getServletConfig().getServletContext().getRealPath("");
		tempPath = tempPath.substring(0, tempPath.indexOf("webapps")) + "temp";
		File fileDir = new File(tempPath);
		File file = null;
		long nowTime = SystemTool.getInstance().getDate().getTime();
		long fileTime = 0;
		long days = 0;
		
		if (fileDir.exists()) {
			String[] fileList = fileDir.list();
			for (int i = 0; i < fileList.length; i++) {
				file = new File(tempPath + File.separator + fileList[i]);
				if (file.exists()) {
					// ɾ��һ��֮ǰ�����������ļ�
					fileTime = file.lastModified();
				    days = (nowTime - fileTime) / (1000 * 60 * 60 * 24);
				    if (days > 0) {
				    	file.delete();
				    }
				}
			}
		}
	}

}
