package jdo.sta;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import jdo.sys.SystemTool;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import jdo.sta.TrustAnyHostnameVerifier;
import jdo.sta.iTrustManager;
import com.alibaba.fastjson.JSONObject;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ����dbf�ļ�������
 * </p>
 * 
 * <p>
 * Description: ����dbf�ļ�������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shibl
 * @version 1.0
 * 
 * 
 */
public class StaGenMroDataTran extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static StaGenMroDataTran instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return SYSRegionTool
	 */
	public static StaGenMroDataTran getInstance() {
		if (instanceObject == null)
			instanceObject = new StaGenMroDataTran();
		return instanceObject;
	}

	public StaGenMroDataTran() {
		TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		String url = config.getString("Servcie_Url");
		String userName = config.getString("User");
		String pass = config.getString("Pwd");
		String dataType = config.getString("DataType");
		String debug=config.getString("DEBUG");
		this.setSERVICE_URL(url);
		this.setUSER(userName);
		this.setPWD(pass);
		this.setDATA_TYPE(dataType);
		this.setDEBUG(debug);
		onInit();
	}

	/**
	 * ��������������ַ
	 */
	private String SERVICE_URL = "";
	/**
	 * �û���
	 */
	private String USER = ""; // superuser

	/**
	 * ����
	 */
	private String PWD = ""; // cltr112233

	/**
	 * �������ݵ�����
	 */
	private String DATA_TYPE = "";
	/**
	 * ���Ա��
	 */
	private String DEBUG="";
	/**
	 * @return the dEBUG
	 */
	public String getDEBUG() {
		return DEBUG;
	}

	/**
	 * @param dEBUG the dEBUG to set
	 */
	public void setDEBUG(String dEBUG) {
		DEBUG = dEBUG;
	}

	/**
	 * @return
	 */
	public String getSERVICE_URL() {
		return SERVICE_URL;
	}

	/**
	 * @param
	 */
	public void setSERVICE_URL(String sERVICEURL) {
		SERVICE_URL = sERVICEURL;
	}

	/**
	 * @return the uSER
	 */
	public String getUSER() {
		return USER;
	}

	/**
	 * @param uSER
	 *            the uSER to set
	 */
	public void setUSER(String uSER) {
		USER = uSER;
	}

	/**
	 * @return the pWD
	 */
	public String getPWD() {
		return PWD;
	}

	/**
	 * @param pWD
	 *            the pWD to set
	 */
	public void setPWD(String pWD) {
		PWD = pWD;
	}

	/**
	 * @return the dATA_TYPE
	 */
	public String getDATA_TYPE() {
		return DATA_TYPE;
	}

	/**
	 * @param dATATYPE
	 *            the dATA_TYPE to set
	 */
	public void setDATA_TYPE(String dATATYPE) {
		DATA_TYPE = dATATYPE;
	}

	/**
	 * ��ȡtoken
	 * 
	 */

	public TParm getToken() {
		TParm tokenParm = new TParm();
		String sUrl = this.getSERVICE_URL()
				+ "/docking/auto/gettoken.shtml?loginid=" + this.getUSER()
				+ "&pwd=" + this.getPWD() + "&datatype=" + this.getDATA_TYPE()
				+ "&isDev=1";
		// System.out.println("send getToken request url:" + sUrl);
		String response = "{}";
		String inputLine = "";
		String token = "";
		try {
			// ���� SSLContext ����
			SSLContext sc = SSLContext.getInstance("SSL");
			// ��ʼ��
			sc.init(null, new TrustManager[] { new iTrustManager() },new java.security.SecureRandom());
			URL url = new URL(sUrl);
			HttpURLConnection myUrlcon=null;
			if(DEBUG.equals("0")){//��ʽ����
				HttpsURLConnection	Urlcon = (HttpsURLConnection) url.openConnection();// ��������
				// ���õ���ʵ��Ϊ��ȫ https URL ���Ӵ����׽���ʱʹ�õ� SSLSocketFactory
				Urlcon.setSSLSocketFactory(sc.getSocketFactory());
				// ���õ�Ĭ�Ͼ�̬������У����
				Urlcon.setHostnameVerifier(new TrustAnyHostnameVerifier());
				myUrlcon=Urlcon;
			}else{
				HttpURLConnection Urlcon = (HttpURLConnection) url.openConnection();// ��������
				myUrlcon=Urlcon;
			}
			myUrlcon.setConnectTimeout(60000);// ���ӳ�ʱ,��λ:����
			myUrlcon.setReadTimeout(60000); // ��ȡ��ʱ,��λ:����
			BufferedReader in = new java.io.BufferedReader(new InputStreamReader(myUrlcon.getInputStream(), "UTF-8"));
			if ((inputLine = in.readLine()) == null)
				inputLine = "";
			if (in != null)
				in.close();
			// System.out.println("inputLine=" + inputLine);
			response = inputLine;
			JSONObject obj = JSONObject.parseObject(response);
			boolean succ = obj.getBoolean("succ");// �ɹ�
			if (succ) {
				token = obj.getJSONObject("result").getString("token");
				tokenParm.setData("TOKEN", token);
			} else {// ʧ��
				String errMsg = obj.getString("errMsg");
				String errorCode = obj.getString("errorCode");
				tokenParm.setData("ERRORCODE", errorCode);
				tokenParm.setData("ERRMSG", errMsg);
			}
			// System.out.println("token return: " + token);
		} catch (Exception e) {
			e.printStackTrace();
			tokenParm.setData("ERRORCODE", "HIS01");
			tokenParm.setData("ERRMSG", "���token�쳣");
			return tokenParm;
		}
		return tokenParm;

	}

	/**
	 * �������� ��������ʹ��justHttp��ԭ����Ϊ���Ǹ��û���Demo���û���û��JustHttp
	 */
	public boolean sendData(String token, InputStream is) {
		// �������ݵ���ݡ��·ݡ����ı�š����ݵ�ַ
		// ����ʹ��ʱ������Щֵ
		String sUrl = this.getSERVICE_URL() + "/autoReqTran.shtml?token="
				+ token + "&datatype=" + this.getDATA_TYPE()
				+ "&rc020=1&rc021=1";// rc020��ָ��ICD10��ϱ��룺1=ȫ���棻2=�����棨����Ĭ��Ϊ�����棩��
		// rc021��ָ��ICD10��̬ѧ���룺1=ȫ���棻2=�����棨����Ĭ��Ϊ�����棩��
		// System.out.println("senddata url=" + sUrl);
		try {
			SSLContext sc;
			sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new iTrustManager() },
					new java.security.SecureRandom());
			URL url = new URL(sUrl);
			HttpURLConnection connection=null;
			if(DEBUG.equals("0")){//��ʽ����
				HttpsURLConnection	Urlcon = (HttpsURLConnection) url.openConnection();// ��������
				// ���õ���ʵ��Ϊ��ȫ https URL ���Ӵ����׽���ʱʹ�õ� SSLSocketFactory
				Urlcon.setSSLSocketFactory(sc.getSocketFactory());
				// ���õ�Ĭ�Ͼ�̬������У����
				Urlcon.setHostnameVerifier(new TrustAnyHostnameVerifier());
				connection=Urlcon;
			}else{
				HttpURLConnection Urlcon = (HttpURLConnection) url.openConnection();// ��������
				connection=Urlcon;
			}
			// �������ӳ�ʱʱ����������������С����
			connection.setConnectTimeout(10 * 60 * 1000);

			// ���ö�ȡ��ʱ
			connection.setReadTimeout(50000);

			// �����������
			connection.setDoOutput(true);

			// ������������
			connection.setDoInput(true);

			// ���ò�����
			connection.setUseCaches(false);

			// ����ΪPOST��ʽ�������
			connection.setRequestMethod("POST");

			// ��ȡ����ZIP�ļ�
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			// System.out.println("data_url"+data_url);
			// File file = new File(data_url);
			// InputStream is = new FileInputStream(file);
			IOUtils.copy(is, bout);

			// ----------�������ӵĲ���---------------

			// ����ά�ֳ�����
			connection.setRequestProperty("Connection", "Keep-Alive");

			// �����ַ����뼯
			connection.setRequestProperty("Charset", "UTF-8");

			// ���ó���
			connection.setRequestProperty("Content-Length", String.valueOf(bout
					.toByteArray().length));

			// ��������Ϊ��
			connection
					.setRequestProperty(
							"Accept",
							"image/gif,   image/x-xbitmap,   image/jpeg,   image/pjpeg,   application/vnd.ms-excel,   application/vnd.ms-powerpoint,   application/msword,   application/x-shockwave-flash,   application/x-quickviewplus,   */*");
			connection
					.setRequestProperty("Content-Type",
							"multipart/form-data; boundary=Bounday---------------------------7d318fd100112");

			// ���ò�����
			connection.setRequestProperty("Cache-Control", "no-cache");

			// �������������ѹ���ĸ�ʽ���ؽ������Ϊ����Ҫ���ؽ�ѹ��
			// connection.setRequestProperty("Accept-Encoding",
			// "gzip,   deflate");

			// ���ļ�д�������
			OutputStream os = connection.getOutputStream();

			os.write(bout.toByteArray());
			bout.close();
			os.close();
			is.close();

			InputStream in = connection.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			for (int b = in.read(); b >= 0; b = in.read()) {
				outputStream.write((byte) b);
			}
			String res = new String(outputStream.toByteArray(), "UTF-8").trim();
			outputStream.close();
			in.close();
			// System.out.println("sendData return: " + res);
			return true;
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (NoSuchAlgorithmException e) {
			return false;
		} catch (KeyManagementException e) {
			return false;
		}
	}

	/**
	 * ��ѯ���
	 */
	public String findResult(String token) {
		String response = "";
		try {
			SSLContext sc;
			sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new iTrustManager() },
					new java.security.SecureRandom());
			URL url = new URL(this.getSERVICE_URL()
					+ "/docking/auto/findResult.shtml?token=" + token
					+ "&datatype=" + this.getDATA_TYPE());
			HttpURLConnection connection=null;
			if(DEBUG.equals("0")){//��ʽ����
				HttpsURLConnection	Urlcon = (HttpsURLConnection) url.openConnection();// ��������
				// ���õ���ʵ��Ϊ��ȫ https URL ���Ӵ����׽���ʱʹ�õ� SSLSocketFactory
				Urlcon.setSSLSocketFactory(sc.getSocketFactory());
				// ���õ�Ĭ�Ͼ�̬������У����
				Urlcon.setHostnameVerifier(new TrustAnyHostnameVerifier());
				connection=Urlcon;
			}else{
				HttpURLConnection Urlcon = (HttpURLConnection) url.openConnection();// ��������
				connection=Urlcon;
			}
			connection.setConnectTimeout(50000);
			connection.setReadTimeout(50000);
			// �����������
			connection.setDoOutput(true);
			// ������������
			connection.setDoInput(true);
			// ���ò�����
			connection.setUseCaches(false);
			// �����ַ����뼯
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("contentType", "UTF-8");
			// ��ʼ���Ӳ���ý��
			InputStream in = connection.getInputStream();
			// ��ȡ����
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			for (int b = in.read(); b >= 0; b = in.read()) {
				outputStream.write((byte) b);
			}
			response = new String(outputStream.toByteArray(), "UTF-8").trim();
			outputStream.close();
			in.close();
			return response;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			return response;
		} catch (NoSuchAlgorithmException e) {
			return response;
		} catch (KeyManagementException e) {
			return response;
		}
	}

	/**
	 * ����
	 * 
	 * @param response
	 * @return
	 */
	public boolean onLog(String response, TConnection conn, String log) {
		TParm result = new TParm();
		String flg = "";
		String errMsg = "";
		String errorCode = "";
		String errorStack = "";
		String id = "";
		String date = "";
		String status = "";
		String errormsg = "";
		String hasErrorMsg = "";
		String grade0 = "";
		String grade1 = "";
		String grade2 = "";
		String grade3 = "";
		String stocked = "";
		String total = "";
		String grade = "";
		if (!response.equals("")) {
			JSONObject obj = JSONObject.parseObject(response);
			boolean succ = obj.getBoolean("succ");// �ɹ���ʧ�ܣ��˲���ֻ��Ϊ�˴�����Ľ��
			errMsg = obj.getString("errMsg");// ������Ϣ
			errorCode = obj.getString("errorCode");// �������
			errorStack = obj.getString("errorStack");// �����ջ��JAVA��ʽ
			id = obj.getJSONObject("result").getString("id");
			String loaddate = obj.getJSONObject("result").getString("loaddate");
			date = "TO_DATE('"
					+ loaddate.replace("-", "").replace(":", "").replace(" ",
							"").substring(0, 14) + "','YYYYMMDDHH24MISS')";
			status = obj.getJSONObject("result").getString("status");
			errormsg = obj.getJSONObject("result").getString("errormsg");
			hasErrorMsg = obj.getJSONObject("result").getString("hasErrorMsg");
			grade0 = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("grade0");
			grade1 = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("grade1");
			grade2 = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("grade2");
			grade3 = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("grade3");
			stocked = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("stocked");
			total = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("total");
			grade = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("grade");
			flg = succ ? "Y" : "N";
		} else {
			flg = "N";
		}
		String DateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMddHHmmss");
		if(id.equals(""))
			id=DateStr;
		String sql = "INSERT INTO STA_MRO_LOG (LOG_DATE, RESULT, ERRORCODE, ID, LOADDATE, "
				+ "STATUS, ERRORMSG, HASERRORMSG, GRADE0, GRADE1, "
				+ "GRADE2, GRADE3, STOCKED, TOTAL, GRADE,LOG)"
				+ "VALUES"
				+ "('"
				+ DateStr
				+ "','"
				+ flg
				+ "', '"
				+ errorCode
				+ "', '"
				+ id
				+ "',"
				+ (date.equals("") ? "NULL" : date)
				+ ",'"
				+ status
				+ "', '"
				+ errormsg
				+ "', '"
				+ hasErrorMsg
				+ "', '"
				+ grade0
				+ "', '"
				+ grade1
				+ "', "
				+ "'"
				+ grade2
				+ "', '"
				+ grade3
				+ "', '"
				+ stocked
				+ "', '"
				+ total
				+ "', '"
				+ grade
				+ "', '"
				+ log + "')";
		result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		StaGenMroDataTran demo = new StaGenMroDataTran();
		try {
			TParm tokenParm = demo.getToken();
			String token = tokenParm.getValue("TOKEN");
			// System.out.println(token);
			// String token = "gqntanru28ob8mqvqsltuc5fn";
			if (StringUtils.isNotBlank(token)) {// false
				// demo.sendData(token);
				Thread.sleep(2000);
				// String token = "15097f92d103483fbe02d29d1ee9cd25";
				demo.findResult(token);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
