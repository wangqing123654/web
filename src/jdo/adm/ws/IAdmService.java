package jdo.adm.ws;

import java.util.List;

import javax.jws.WebService;

/**
 * 
 * 住院管理服务接口
 * @author shibl
 *
 */
@WebService
public interface IAdmService {
	
	public List<String> getAdmPatInfo(Object MrNo);

}
