package jdo.bil;


import java.util.List;

import javax.jws.WebService;
/**
 * <p>Title: ���ü�¼�Ʒ� werbservice�ӿ�</p>
 *
 * <p>Description:���ü�¼�Ʒ�  werbservice�ӿ�</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author caowl 2013-7-31
 * @version 4.0
 */
@WebService
public interface BILSPCINVWsTool {
	
		
	//סԺ�Ʒѷ���  ����ֵΪ SEQ_NO,CASE_NO_SEQ ��дSPC_INV_RECORD��	
	//public BILSPCINVDtos insertIBSOrder(BILSPCINVDtos bilspcinvDtos);
	public String insertIBSOrder(String inString1,String inString2,String inStringM);
	//public BILSPCINVDto insertIBSOrder(BILSPCINVDto forIBSParm1);
	//����Ʒѷ���
	public String insertOpdOrder(String inString);
	//��ò�����Ϣ
	public String onMrNo(String mr_no,String adm_type);
	//��ò��˼Ʒ���Ϣ
	public String onFeeData(String inString);
	
}
