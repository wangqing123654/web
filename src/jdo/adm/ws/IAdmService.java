package jdo.adm.ws;

import java.util.List;

import javax.jws.WebService;

/**
 * 
 * סԺ�������ӿ�
 * @author shibl
 *
 */
@WebService
public interface IAdmService {
	
	public List<String> getAdmPatInfo(Object MrNo);

}
