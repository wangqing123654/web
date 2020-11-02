package com.javahis.device;

/**
 * �������Ľ���dll��װ;
 * @author lixiang
 *
 */
public class NJEapagentDriver {
	
	static {
		System.loadLibrary("NJEapagentDriver"); // ����dll
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
	 * �������ķ���
	 * @return  ÿ�ε������Ķ˷����һ��������ó�ʼ������
	 */
	public native static int EapAgent_Init();
	
	/**
	 * �������ĳ�ʼ������
	 * @return  ÿ�ε������Ķ˷���ڶ���������ø�λ����
	 */
	public native static int EapAgent_Reset();
	
	/**
	 * ������������Action����
	 * @param action
	 * @return  ָ����Ҫ���õ�action
	 */
	public native static int EapAgent_SetAction(String action);
	
	/**
	 * ������������Action��������
	 * @param actionMethod
	 * @return  ָ�����õ�action��������
	 */
	public native static int EapAgent_SetActionMethod(String actionMethod);
	
	/**
	 * �����������ò���ֵ����
	 * @param paraName
	 * @param paraValue
	 * @return  paraName Ϊ��������paraValueΪ����ֵ����������Ҫ�ͻ��������ķ����Լ�����μ��ӿ��ĵ�������ֵΪ�ַ�����淶Xml
	 */
	public native static int EapAgent_PutParameter(String paraName, String paraValue);
	
	/**
	 * �������ķ���������
	 * @return  ֮ǰ������ɺ󣬵��øú����������󡣷���ֵ��Ϊ0ʱ˵�����������ʧ�ܡ�
	 */
	public native static int EapAgent_SendRequest();
	
	/**
	 * �������Ļ�ȡ����״̬����
	 * @param appCode   ����Ӧ��
	 * @return ��ȡ����״̬�������������0��˵�����óɹ���-1Ϊ����ʧ��;
	 */
	public native static int EapAgent_GetAppCode(EapAppCode appCode);
	
	/**
	 * �������Ļ�ȡ����ֵ����
	 * @param paraName
	 * @param paraValue
	 * @return paraName ����ֵ��������paraValue����ֵ������ֵ������Ҫ�ͻ��������ķ����Լ�����μ��ӿ��ĵ�
	 */
	public native static int EapAgent_GetParameter(String paraName,EapReturnParm paraValue);
	
	/**
	 * �������Ļ�ȡ���ش�����Ϣ����
	 * @param briefErrorMessage
	 * @return  GetAppCode����-1ʱ���ɵ��ø÷�����ȡ������ʾ��Ϣ��
	 */
	public native static int EapAgent_GetBriefErrorMessage(EapErrorMessage briefErrorMessage);
	
	private static void getString(String aa){
		aa="eeeeee";
	}
	
	public static void main(String args[]){
	/*	EapReturnParm c=new EapReturnParm();
		EapAgent_GetParameter("aa",c);
		System.out.println("return para"+c.getParaValue());**/
		/*String bb="abcde";
		getString(bb);
		System.out.println("====bb===="+bb);*/
		
		/**String paraName="����";
		EapAgent_PutParameter(paraName,"bbb");
		System.out.println("paraName"+paraName);**/
		
		
	}
	

}


