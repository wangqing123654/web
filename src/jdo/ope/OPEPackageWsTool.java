package jdo.ope;

import javax.jws.WebService;

/**
 * <p>
 * Title: �����Խ������� werbservice�ӿ�
 * </p>
 * 
 * <p>
 * Description: �����Խ������� werbservice�ӿ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author fux 2014-5-4
 * @version 4.0
 */
@WebService  
public interface OPEPackageWsTool {  
	// ��HIS������ �õ�OPE_REQUEST��Ϣ
	// OPERATION_ICD ; OPT_CHN_DESC ; PACK_CODE ; QTY ; PACK_DESC ; SEQ_FLG
	// ---������ά��������Ӧ����������
	// OPBOOK_SEQ ; OP_CODE ; SUPTYPE_CODE; OP_DATE ; STATE
	// �����ų���Ϣ(������ȡpackage����Ϣ)
	// ԤԼ״̬ 0 ���룬 1 �ų���� ��2�������
	// public String onOpeSeqQuery(String opbookSeq,String opCode,String
	// packCode,String qty,String packDesc,String supTypeCode, String opDate);
	// ֻ��Ҫȫ�������OPE_PACKAGE
	// flg ȫ��Ϊ0 δ�ų�Ϊ1 ���ų�Ϊ2
	public String onSaveOpePackage(String opCode, String supTypeCode,
			String opDateS, String opDateE, String state, String optUser,
			String optTerm);
}
