package com.javahis.device;

/**
 * 
 * @author lixiang
 * 
 */
public class NJSMCardDriver {


	static {
		System.loadLibrary("NJSMCardDriver"); // ����dll
	}

	/**
	 * ��ʼ������DLL
	 * 
	 * @return int 1 �ɹ� 0 ʧ��
	 */
	public native static int init();

	/**
	 * ע��DLL
	 * 
	 * @return
	 */
	public native static int close();

	/**
	 * ���Ӷ�������ÿ�ν����ʱ����ʼ��һ�ξͿ���
	 * 
	 * @param ComPort
	 *            ���ںţ�����com1��ComPortΪ1��
	 * @param Baud
	 *            �����ʡ��̶�ֵ��115200��
	 * @return >=0 �ɹ�����ֵ��Ϊ�������������Ĳ�������� <0 ʧ�ܡ�
	 */
	public native static int LinkReaderPro(int ComPort, int Baud);

	/**
	 * ��ȡ�����ͼ����ں�
	 * 
	 * ������ȡ�����ͼ����ں�
	 * 
	 * @param hReader
	 *            //���������ΪLinkReaderPro�ķ���ֵ
	 * @param pCardInfo
	 *            //���صĿ����ͼ����ں���Ϣ���ܳ���Ϊ137��16���ƴ洢��
	 * @return ���չ�������ֵ˵��
	 */
	public native static int FoundCardPro(int hReader, STCardInfo pCardInfo);

	/**
	 * �ͷŶ�����
	 * 
	 * @param hReader
	 *            ���������ΪLinkReaderPro�ķ���ֵ
	 * @return ���չ�������ֵ˵��
	 */
	public native static int FreeReader(int hReader);

	/**
	 * ���ý����ļ�����Ŀ¼��ÿ������ʱ����ָ��Ŀ¼
	 * 
	 * 
	 * @param LogDir
	 *            LogDir�������Ŀ¼��������·����\\Ϊ�ָ�����������C:\\temp\\��
	 * @return ���չ�������ֵ˵��
	 */
	public native static int SetLogDir(String LogDir);

	/**
	 * ��ɶ�M1����д����֤��ÿ����ֻ֤�ܶ�дȡһ��������
	 * 
	 * @param hReader
	 *            ���������ΪLinkReaderPro�ķ���ֵ
	 * @param BlockNo
	 *            ��ţ���������Ͼ��о��￨���ṹ��¼
	 * @return ���չ�������ֵ˵��
	 * 
	 * 
	 */
	public native static int M1_AuthPro(int hReader, int BlockNo);

	/**
	 * M1���麯��
	 * 
	 * @param hReader
	 *            ���������ΪLinkReaderPro�ķ���ֵ
	 * @param BlockNo
	 *            ��ţ���������Ͼ��о��￨���ṹ��¼��
	 * @param pData
	 *            ���ص���Ϣ��16λ�ֽڡ�
	 * @return ���չ�������ֵ˵��
	 */
	public native static int M1_ReadData(int hReader, int BlockNo, M1Data m1Data);

	/**
	 * ��ɶ�M1��ĳһ���д����
	 * 
	 * @param hReader
	 *            ���������ΪLinkReaderPro�ķ���ֵ
	 * @param BlockNo
	 *            ��ţ���������Ͼ��о��￨���ṹ��¼
	 * @param pData
	 *            д�����Ϣ��16λ
	 * @return ���չ�������ֵ˵��
	 */
	public native static int M1_WriteData(int hReader, int BlockNo, byte[] pData);

	/**
	 * Desfire��֤
	 * 
	 * ���ܣ���ɶ�Desfire������д���ۿ�Ȳ�������֤
	 * 
	 * @param hReader
	 *            ���������ΪLinkReaderPro�ķ���ֵ
	 * @param pDFAuth
	 *            ��֤��Ϣ
	 * @return
	 */
	public native static int DF_AuthPro(int hReader, STDFAuth pDFAuth);

	/**
	 * Desfire��������Ϣ���� ���ܣ���ɶ�Desfire��������Ϣ�Ķ�ȡ������
	 * 
	 * @param hReader
	 *            ���������ΪLinkReaderPro�ķ���ֵ
	 * @param pDFInfo
	 *            ������Ϣ���أ�85�ֽڣ�16���Ʒ���
	 * @return ���չ�������ֵ˵��
	 */
	public native static int DF_ReadCardInfo(int hReader, STDFInfo pDFInfo);

	/**
	 * ����Ǯ���ۿ��
	 * 
	 *���ܣ���ɶ�Desfire������Ǯ���ۿ�Ĳ�������Ŀǰֻ֧�������ף�
	 * 
	 * @param hReader
	 *            ���������ΪLinkReaderPro�ķ���ֵ
	 * @param Amount
	 *            ���ѽ�100����1Ԫ����ң�
	 * @param pBalance
	 *            ��100����1Ԫ����ң�
	 * @return ���չ�������ֵ˵��
	 */
	public native static int DF_CardDebit(int hReader, int Amount,
			Balance pBalance);
	
	/**
	 * ��������������;
	 * @param iNums
	 * @return
	 */
	public native static int TK_PCD_Beep(int iNums);
	
}
