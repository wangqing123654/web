package jdo.spc.reqinf;

import javax.jws.WebService;

import jdo.spc.reqinf.dto.SysPatinfo;


@WebService
public interface SpcPatInfoService {
	
	/**
	 * HIS������ҵ
	 * @param requestM ���ݴ������
	 * @return
	 */
	public String onSaveSpcPatInfo(SysPatinfo patinfo);
}
