package jdo.inv.ws;

import javax.jws.WebService;
/**
 * <p>Title:</p>ͬ��his inv_account ����
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author chenxi 20130805
 * @version 4.0
 */
@WebService
public interface AccountServiceTool {

	public String onSaveAccount(INVAccounts invAccounts) ;
}
