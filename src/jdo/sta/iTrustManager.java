package jdo.sta;

import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
/**
 * �˽ӿڵ�ʵ������ʹ����һ�� X509 ֤������֤Զ�˵İ�ȫ�׽��֡�
 * �����Ǹ������ε�֤����Ȩ��֤�鳷���б�����״̬����������ʽ�����ġ�
 * 
 */
public class iTrustManager implements X509TrustManager {
	public iTrustManager() {

	}

	// ����ͬλ���ṩ�Ĳ��ֻ�������֤���������������εĸ���֤��·����
	// ���ҷ����Ƿ����ȷ�Ϻ����ν������ڻ��������֤���͵Ŀͻ��� SSL �����֤
	public void checkClientTrusted(X509Certificate chain[], String authType)
			throws CertificateException {
		System.out.println("check client trust status");
	}

	// ����ͬλ���ṩ�Ĳ��ֻ�������֤���������������εĸ���֤��·����
	// ���ҷ����Ƿ����ȷ�Ϻ����ν������ڻ��������֤���͵ķ����� SSL �����֤��
	public void checkServerTrusted(X509Certificate chain[], String authType)
			throws CertificateException {
		System.out.println("check Server trust status");
	}

	// �����������֤ͬλ�����ε���֤���ĵ�����
	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	// ����ͬλ���ṩ�Ĳ��ֻ�������֤���������������εĸ���֤��·����
	// ���ҷ����Ƿ����ȷ�Ϻ����ν������ڻ��������֤���͵Ŀͻ��� SSL �����֤
	public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
			String authType) throws java.security.cert.CertificateException {
		// TODO Auto-generated method stub
	}
	// ����ͬλ���ṩ�Ĳ��ֻ�������֤���������������εĸ���֤��·����
	// ���ҷ����Ƿ����ȷ�Ϻ����ν������ڻ��������֤���͵ķ����� SSL �����֤��
	public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
			String authType) throws java.security.cert.CertificateException {
		// TODO Auto-generated method stub
	}

}
