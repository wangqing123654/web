package jdo.ind.inf;

import javax.jws.WebService;

/**
 * ����������ӿ�
 * @author liyanhui
 *
 */
@WebService
public interface IndAccountService {

	/**
	 * �������������������
	 * @param xml
	 * @return
	 */
	public String onSaveIndAccount(IndAccounts  indAccounts);
	
}
