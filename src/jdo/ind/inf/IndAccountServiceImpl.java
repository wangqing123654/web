package jdo.ind.inf;

import javax.jws.WebService;

/**
 * ����������ӿ�-ʵ����
 * @author liyanhui
 *
 */
@WebService
public class IndAccountServiceImpl implements IndAccountService{

	@Override
	public String onSaveIndAccount(IndAccounts indAccounts) {
		// TODO Auto-generated method stub
		return IndAccountDaoImpl.getInstance().onSave(indAccounts);
	}

	
	

	
}
