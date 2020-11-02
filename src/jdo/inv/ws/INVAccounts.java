package jdo.inv.ws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
*
* <p>Title: �½�����list</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.14 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD)  
public class INVAccounts {

	private  List<INVAccount> invAccount; //��������
	
	
	/**
	 * �õ��������
	 * @return
	 */
	public List<INVAccount> getInvAccount() {
		if (invAccount == null) {  
			invAccount = new ArrayList<INVAccount>();  
        }  
		return invAccount ;
	}
	/**
	 * ���ý������
	 * @param request
	 */
	public void setInvAccount(List<INVAccount> account) {
		if (account == null) {  
			account = new ArrayList<INVAccount>();  
        }  
		this.invAccount = account;
	}
}
