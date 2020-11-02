package com.javahis.device;

/**
 * NJSMCardYYDriver.dll(YYReader.dll)��װ
 * @author lixiang
 *
 */
public class NJSMCardYYDriver {

	static {
		System.loadLibrary("NJSMCardYYDriver"); // ����dll
	}
		
	/**
	 * ��ʼ������DLL
	 * 
	 * @return int 1 �ɹ� 0 ʧ��
	 */
	public native static int init();
	
	/**
	 * ע��DLL
	 * @return
	 */
	public native static int close();
	
	/**
	 * Desfire����дҽ����֤
	 * @param hReader ���������ΪLinkReaderPro�ķ���ֵ
	 * @return ���չ�������ֵ˵��
	 */
	public native static int YYAuthPro(int hReader);
	
	/**
	 * Desfire����ȡҽ����Ϣ
	 * @param hReader  ���������ΪLinkReaderPro�ķ���ֵ
	 * @param yy    ������ҽ����Ϣ
	 * @return
	 */
	public native static int YYReadData(int hReader,NJYY yy);
	
	/**
	 * Desfire��д��ҽ����Ϣ
	 * @param hReader  ���������ΪLinkReaderPro�ķ���ֵ
	 * @param yy		д���ҽ����Ϣ
	 * @return			���չ�������ֵ˵����
	 */
	public native static int YYWriteData(int hReader,NJYY yy);
	
}
