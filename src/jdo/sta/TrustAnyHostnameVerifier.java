package jdo.sta;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 
 *������������������֤�Ļ��ӿڡ�
 * 
 *�������ڼ䣬��� URL ���������ͷ������ı�ʶ��������ƥ�䣬����֤���ƿ��Իص��˽ӿڵ�ʵ�ֳ�����ȷ���Ƿ�Ӧ����������ӡ�
 * 
 *���Կ����ǻ���֤��Ļ����������������֤������
 * 
 *����֤ URL ������ʹ�õ�Ĭ�Ϲ���ʧ��ʱʹ����Щ�ص�
 * 
 */
public class TrustAnyHostnameVerifier implements HostnameVerifier {
	/**
	 * ��֤�������ͷ����������֤������ƥ���ǿɽ��ܵ�
	 */
	public boolean verify(String hostname, SSLSession session) {
		return true;
	}
}
