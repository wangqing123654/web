package jdo.ind;

import javax.jws.WebService;

@WebService
public interface IIndService {

	/**
	 * ����DispensenM,D ��
	 * @param indDispensem
	 * @return
	 */
	public String onSaveDispenseDM(IndDispensem indDispensem);
	
}
