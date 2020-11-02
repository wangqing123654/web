package com.javahis.device;

import java.util.Hashtable;

import com.dongyang.data.TParm;

/**
 * 
 * �Ž������ӿ��๦��
 * 
 * @author lix
 * 
 */
public class PullDriver {
	public static boolean loadFlg = true; // ����Load Dll
	static Hashtable<Integer, String> h = new Hashtable<Integer, String>(); // ����ѶϢ

	public PullDriver() {

	}

	static {
		try {
			System.loadLibrary("PullDriver"); // ����dll
			loadFlg = true;
			// ���ش�����Ϣ;
			setErrorMessage();
		} catch (Throwable ex) {
			loadFlg = false;
		}
	}

	/**
	 * ��ʼ������dll
	 * 
	 * @return int
	 */
	public native static int init();

	/**
	 * ע��dll
	 * 
	 * @return int
	 */
	public native static int free();

	/**
	 * �����豸�����ӳɹ��󷵻����Ӿ��
	 * 
	 * @param Parameters
	 * @return
	 */
	public native static int Connect(final String Parameters);

	/**
	 * �Ͽ����豸������
	 * 
	 * @param handle
	 */
	public native static void Disconnect(int Handle);

	/**
	 * ���ÿ����������������豸�š��Ŵ����͡�������ʱ�䡢��������ȡ�
	 * 
	 * @param handle
	 * @param itemValues
	 * @return
	 */
	public native static int SetDeviceParam(int Handle, final String ItemValues);

	/**
	 * ��ȡ�����������������豸�š��Ŵ����͡�������ʱ�䡢��������ȡ�
	 * 
	 * @param handle
	 * @param buffer
	 * @param bufferSize
	 * @param items
	 * @return
	 */
	public native static int GetDeviceParam(int Handle, String Buffer, int bufferSize,
			final String items);

	/**
	 * ���ƿ���������
	 * 
	 * @param Handle
	 * @param OperationID
	 * @param Param1
	 * @param Param2
	 * @param Param3
	 * @param Param4
	 * @param Options
	 * @return
	 */
	public native static int ControlDevice(int Handle, int OperationID, int Param1,
			int Param2, int Param3, int Param4, final String Options);

	/**
	 * �������ݵ��豸,��������ʱ��Ρ��û���Ϣ���������á������ݣ����ݿ�����һ����¼��Ҳ�����Ƕ�����¼��
	 * 
	 * @param handle
	 * @param TableName
	 * @param Data
	 * @param Options
	 * @return
	 */
	public native static int SetDeviceData(int handle, final String TableName,
			final String Data, final String Options);

	/**
	 * ���豸��ȡ����,���ڶ�ȡˢ����¼��ʱ��Ρ��û���Ϣ���������á������ݣ����ݿ�����һ����¼��Ҳ�����Ƕ�����¼��
	 * 
	 * @param handle
	 * @param Buffer
	 * @param BufferSize
	 * @param TableName
	 * @param FieldNames
	 * @param Filter
	 * @param Options
	 * @return  
	 */
	public native static int GetDeviceData(int handle, byte[] Buffer,
			int BufferSize, final String TableName, final String FieldNames,
			final String Filter, final String Options);

	/**
	 * ��ȡ�豸�еļ�¼������Ϣ������ָ�����ݵļ�¼������
	 * 
	 * @param Handle
	 * @param TableName
	 * @param Filter
	 * @param Options
	 * @return
	 */
	public native static int GetDeviceDataCount(int Handle, final String TableName,
			final String Filter, final String Options);

	/**
	 * ɾ���豸�е����ݣ������û���Ϣ��ʱ��ε����ݡ�
	 * 
	 * @param handle
	 * @param TableName
	 * @param Data
	 * @param Options
	 * @return
	 */
	public native static int DeleteDeviceData(int handle, final String TableName,
			final String Data, final String Options);

	/**
	 * ʵʱ��ȡ�豸�¼���¼��
	 * 
	 * @param handle
	 * @param Buffer
	 * @param BufferSize
	 * @return
	 */
	public native static int GetRTLog(int handle, byte[] Buffer, int BufferSize);

	/**
	 * �����������ڵ��Ž���������
	 * 
	 * @param CommType
	 * @return
	 */
	public native static int SearchDevice(String CommType, String Address,
			byte[] Buffer);

	/**
	 * UDP�㲥��ʽ�޸Ŀ�����IP��ַ��
	 * 
	 * @param CommType
	 * @param Address
	 * @param Buffer
	 * @return
	 */
	public native static int ModifyIPAddress(String CommType, String Address,
			String Buffer);

	/**
	 * ��ȡ����ID�� ����ʧ��ʱ��ͨ����������ȡʧ��ID
	 * 
	 * @return
	 */
	public native static int PullLastError();

	/**
	 * ���ļ���PC���͵��豸����ҪӦ���ڽ������ļ������豸�������ļ����ơ�emfw.cfg����
	 * 
	 * @param Handle
	 * @param FileName
	 * @param Buffer
	 * @param BufferSize
	 * @param Options
	 * @return
	 */
	public native static int SetDeviceFileData(int Handle, final String FileName,
			final String Buffer, int BufferSize, final String Options);

	/**
	 * ���豸��ȡ�ļ���PC���ɴ��豸��ȡ�û���Ϣ�ļ����¼���¼�ļ��ȡ�
	 * 
	 * @param Handle
	 * @param Buffer
	 * @param BufferSize
	 * @param FileName
	 * @param Options
	 * @return
	 */
	public native static int GetDeviceFileData(int Handle, String Buffer,
			int BufferSize, final String FileName, final String Options);

	/**
	 * ������Ϣ;
	 */
	static void setErrorMessage() {
		h.put(new Integer(-1), "�����ʧ��");
		h.put(new Integer(-2), "����û�л�Ӧ");
		h.put(new Integer(-3), "��Ҫ�Ļ��治��");
		h.put(new Integer(-4), "��ѹʧ��");
		h.put(new Integer(-5), "��ȡ���ݳ��Ȳ���");
		h.put(new Integer(-6), "��ѹ�ĳ��Ⱥ������ĳ��Ȳ�һ��");
		h.put(new Integer(-7), "�����ظ�");
		h.put(new Integer(-8), "������δ��Ȩ");
		h.put(new Integer(-9), "���ݴ���CRCУ��ʧ��");
		h.put(new Integer(-10), "���ݴ���PullSDK�޷�����");
		h.put(new Integer(-11), "���ݲ�������");
		h.put(new Integer(-12), "����ִ�д���");
		h.put(new Integer(-13), "�������û�д�����");
		h.put(new Integer(-14), "ͨѸ�������");
		h.put(new Integer(-15), "д�ļ�ʧ��");
		h.put(new Integer(-16), "���ļ�ʧ��");
		h.put(new Integer(-17), "�ļ�������");
		h.put(new Integer(-99), "δ֪����");
		h.put(new Integer(-100), "��ṹ������");
		h.put(new Integer(-101), "��ṹ�У������ֶβ�����");
		h.put(new Integer(-102), "�ֶ�������һ��");
		h.put(new Integer(-103), "�ֶ�����һ��");
		h.put(new Integer(-104), "ʵʱ�¼����ݴ���");
		h.put(new Integer(-105), "��������ʱ�����ݴ���");
		h.put(new Integer(-106), "����������·����ݳ���4M");
		h.put(new Integer(-107), "��ȡ��ṹʧ��");
		h.put(new Integer(-108), "��ЧOPTIONSѡ��");
		h.put(new Integer(-201), "LoadLibraryʧ��");
		h.put(new Integer(-202), "���ýӿ�ʧ��");
		h.put(new Integer(-203), "ͨѶ��ʼ��ʧ��");
		h.put(new Integer(-301), "��ȡTCP/IP�汾ʧ��");
		h.put(new Integer(-302), "����汾��");
		h.put(new Integer(-303), "��ȡЭ������ʧ��");
		h.put(new Integer(-304), "��ЧSOCKET");
		h.put(new Integer(-305), "SOCKET����");
		h.put(new Integer(-306), "HOST����");
	}
	
	// ������ ���ձ�
	private static String getErrorMsg(int error) {
		
		//System.out.println("=====error======="+error);
		String msg = "";
		if (h.containsKey(new Integer(error)))
			msg = (String) h.get(new Integer(error));
		else
			msg = "�������";
		return msg;
	}
	
	/**
	 * ��Ԫ����
	 * @param args
	 */ 
	public static void main(String args[]) {
		TParm result = new TParm();
		//1.PullDriver��ʼ��

		//2.����dll
		int i=PullDriver.init();
		System.out.println("========load========="+i);				
		
		//3.���ӷ���
		String params = "protocol=TCP,ipaddress=192.168.1.5,port=4370,timeout=2000,passwd=";		
	//	String params = "protocol=TCP,ipaddress=192.168.1.5,port=4370,timeout=2000,passwd=";
	//	String params = "protocol=TCP,ipaddress=192.168.0.201,port=4370,timeout=2000,passwd=";
		int rtn1=PullDriver.Connect(params);					
		System.out.println("=====PullDriver rtn1===="+rtn1);							
		if(rtn1<=0){
			result.setErr(-1, PullDriver.getErrorMsg(rtn1));
			System.out.println("=====�д���====="+PullDriver.getErrorMsg(rtn1));
		}
		
		
		//5.���Ų���     ��־���������豸������  (�ɹ�)
    	int ret = 0;
		int operid = 1;
		int doorid = 1;
		int outputadr = 1;
		int doorstate = 6;
		ret=PullDriver.ControlDevice(rtn1, operid, doorid, outputadr, doorstate, 0, "");
		System.out.println("=======���Ų���ret========"+ret);
		if(ret<0){
			result.setErr(-1, PullDriver.getErrorMsg(ret));		
			System.out.println("=====�д���====="+PullDriver.getErrorMsg(ret));
		}
		
	    //4.ȡ��־  ����ͨ��     (��������������ʱʱȡ��¼)
		//2012-10-23 15:34:08,33685762,33685506,0,255,0,200
		//2012-11-01 11:16:07,33685762,33685506,0,255,0,200
	  /*  byte[] data = new byte[256];
		int rtn2=PullDriver.GetRTLog(rtn1, data, 256);
		if(rtn1<0){
			result.setErr(-1, PullDriver.getErrorMsg(rtn1));
			System.out.println("=====�д���====="+rtn1);
		}
		String strData = byte2Str(data);
		System.out.println("=====strData======="+strData);
			 */
	
		
		//PullDriver.GetDeviceDataCount(Handle, "user", Filter, Options)
		
		//6.GetDeviceData  ���豸��ȡ����,���ڶ�ȡˢ����¼
		//��������ʱ�����ݴ���
	/*	byte[] buffer = new byte[BUFFERSIZE];
		String devtablename = "user";
		String str = "*";
	    String devdatfilter = "";
		String options = "";

		int rtn6=PullDriver.GetDeviceData(rtn1, buffer, BUFFERSIZE, devtablename, str, devdatfilter, options);
		System.out.println("=====��������======"+rtn6);
		System.out.println("============"+buffer);
			if(rtn6<0){
				result.setErr(-1, PullDriver.getErrorMsg(rtn6));
				System.out.println("=====�д���====="+PullDriver.getErrorMsg(rtn6));
			}
		String strData = byte2Str(buffer);
		System.out.println("=====strData======="+strData);	int BUFFERSIZE = 10 * 1024 * 1024;*/
	
		
		
		//7.�����������ڵ��Ž������� (�ɹ�)
		//MAC=00:17:61:10:08:33,IP=192.168.1.3,NetMask=255.255.255.0,GATEIPAddress=192.168.1.255,SN=4602011070099,Device=inBIO460,Ver=AC Ver 5.0.8 Aug  8 2011
		//int ret7 = 0;
/*	    String udp = "UDP";
		String adr = "255.255.255.255";
		byte[] buffer = new byte[64 * 1024];
		int ret7=pullDrv.SearchDevice(udp, adr, buffer);
		
		//System.out.println("=====��������======"+ret7);
		if(ret7<0){
			result.setErr(-1, PullDriver.getErrorMsg(ret7));
			System.out.println("=====�д���====="+ret7);
		}
		String strData = byte2Str(buffer);
		System.out.println("=====strData======="+strData);*/
		
		
		//	
		//5.�ز�����
		PullDriver.Disconnect(rtn1);
		
		//ע��dll
		PullDriver.free();
		
		
	}
	// �� byteת�ɷ���
	private static String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		System.out.println("����Data===>"+Total_Data);
		return Total_Data;
	}

}
