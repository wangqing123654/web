package jdo.spc.accountinf;

import javax.jws.WebService;

@WebService
public interface IndService {

	
	
	/**
	 * ҩ����Ᵽ��DispensenM,D ��
	 * @param indDispensem
	 * @return
	 */
	/**
	public String onSaveDispenseDM(IndDispensem indDispensem);
	*/
	/**
	 * ҩ����ֹ����
	 * @param indDispensem
	 * @return
	 */
	//public String onStop(IndDispensem indDispensem);
	

	public String onSaveDispense(String xml);
	
}
