package com.javahis.device;

import com.javahis.device.provobj.CardInfo;
import com.javahis.device.provobj.FeeDetail;
import com.javahis.device.provobj.OutpatReg;
import com.javahis.device.provobj.RecCode;

/**
 * ����ʡҽ�Ʊ��սӿ�
 * �������ʡҽ�Ʊ�����ϢϵͳҽԺ(ҩ��)�ӿڹ淶�ĵ�
 * @author lixiang
 *
 */
public class JSProvInwDriver {
	static {
		System.loadLibrary("JSProvInwDriver"); // ����dll
	}

	public JSProvInwDriver() {
	}

	/**
	 * ��ʼ������dll
	 * 
	 * @param type
	 *            0ʡҽ��|1�ɲ�����;
	 * @return
	 */
	public native static int init(int type);

	/**
	 * ע��dll
	 * 
	 * @param type
	 *            0ʡҽ��|1�ɲ�����;
	 * @return
	 */
	public native static int close(int type);

	/**
	 * ȡ�����
	 * 
	 * ����FGetRecCode������HT.HISȡ�þ���š�ҽԺHISϵͳͨ������ˮ�Ž�����ҽ���շ�ϵͳ����ϵ��
	 * ҽԺ����Ҫ����������룬����ҽԺHISϵͳ��¼��ҽ���շѼ�¼��Ӧ�� ��������ҽ���շ��йصĽӿں����У�������������psRecCode��
	 * �������óɹ�����C:��Ŀ¼������һ���ı��ļ����ļ���ΪFGetRC0.txt��FGetRC1.txt,�ֱ��Ӧ�������סԺ��
	 * 
	 * @param piRecType
	 *            0���1סԺ
	 * @param pChar
	 *            �����(���������շ�ʱΪ12λ������סԺ����ʱΪ6λ)
	 * @return
	 */
	public native static int FGetRecCode(int piRecType, RecCode psRecCode);
	
	/**
	 * ����ȡ������Ϣ  
	 * @param piRecType  ����[N1]��������룩 0����/1סԺ
	 * @param psRecCode   �����
	 * @param psVoucherID   ����֤��[C16]
	 * @param CardInfo   ���صĿ���Ϣ;
	 * @return
	 */
	public native static int FGetCardInfo(int piRecType, String psRecCode,
			String psVoucherID,CardInfo cardInfo);

	/**
	 * ����Һ�
	 * @param psRecCode  //�����[C12]<YYYYMMDD****>	
	 * @param psRegName   //�Һ����ר�Һţ���ͨ��...��
	 * @param psDepartName   //��������
	 * @param psRegFeeCode   //�Һŷ���Ŀ�Ա���
	 * @param psRegFeeName   //�Һŷ���Ŀ����(C50)
	 * @param RegFee			//�Һŷѽ��
	 * @param psDiagFeeCode     //���Ʒ���Ŀ�Ա���
	 * @param psDiagFeeName   //���Ʒ���Ŀ����(C50)
	 * @param DiagFee         //���Ʒѽ��
	 * @param psFeeType		   //�ѱ�
	 * @param psOpCode			//����Ա
	 * @param psRegDate			//����
	 * @param pRegMode			//����ģʽ(T(�俨��)/F��ˢ����
	 * @param outpatReg
	 * @return
	 */
	public native static int FOutpatReg(String psRecCode, String psRegName,
			String psDepartName, String psRegFeeCode, String psRegFeeName,
			float RegFee, String psDiagFeeCode, String psDiagFeeName,
			float DiagFee, String psFeeType, String psOpCode, String psRegDate,
			String pRegMode,OutpatReg outpatReg);

	/**
	 * ȡ������Һ�
	 * @param psRecCode  //�����[C12]
	 * @param psOpCode	 //����Ա����
	 * @return
	 */
	public native static int FCancleOutpatReg(String psRecCode, String psOpCode);

	/**
	 * ȡ����������
	 * @param psRecCod  //���ﴦ����
	 * @param psOpCode   //����Ա����
	 * @return
	 */
	public native static int FCancelTryOutpatBalance(String psRecCod,
			String psOpCode);

	/**
	 * ������ϸ¼��
	 * @param piRecType  //I����[N1]: 0����/1סԺ
	 * @param psRecCode   //I����ţ�סԺ�����ﲻͬ��
	 * @param psItmFlag   //I��Ŀ����[C1] '0' ��ҩƷ/'1' ҩƷ
	 * @param psItmCode		 //I��Ŀ����(HIS���� [C20])
	 * @param psAliasCode	 //I��ϸ����[C20]
	 * @param psItmName		//I��Ŀ����[C50]
	 * @param psItmUnit		//I��λ[C10]
	 * @param psItmDesc		//I��񡢼��͵�[C50]
	 * @param psFeeCode		//I�ѱ���[C3] �����룩
	 * @param psOTCCode		//I����ҩ��־[C3] �����룩
	 * @param pcQuantity		//I����[N(8, 2)]
	 * @param pcPharPrice		 //IӦ�۵���[N(10, 4)]	
	 * @param pcFactPrice		 //Iʵ�۵���[N(10, 4)]
	 * @param pcDosage			 //Iÿ������[N(9, 3)]
	 * @param psFrequency
	 * @param psUsage
	 * @param pcDays
	 * @param psOpCode
	 * @param psDepCode
	 * @param psDocCode
	 * @param psRecDate
	 * @param feeDetail
	 * @return
	 */
	public native static String FWriteFeeDetail(int piRecType,
			String psRecCode, String psItmFlag, String psItmCode,
			String psAliasCode, String psItmName, String psItmUnit,
			String psItmDesc, String psFeeCode, String psOTCCode,
			float pcQuantity, float pcPharPrice, float pcFactPrice,
			float pcDosage, String psFrequency, String psUsage, float pcDays,
			String psOpCode, String psDepCode, String psDocCode,
			String psRecDate,FeeDetail feeDetail);

	/**
	 * ����סԺ������ϸ��ʱ���䣨���˸�Ԥ��
	 * 
	 * @param piShowMess
	 *            �Ƿ���ʾ��Ϣ(0��ʾ/1����ʾ)
	 * @param piRecType
	 *            ��������[N1]: 0����/1סԺ
	 * @param psRecCode
	 *            �����: ���ﴦ����[C12]<YYYYMMDD****>/סԺ��[C6]<******>
	 * @param psItmFlag
	 *            0' ��Ŀ/'1' ҩƷ
	 * @param psItmCode
	 *            ��Ŀ����(HIS���� C20)
	 * @param psAliasCode
	 *            ��ϸ����
	 * @param psItmName
	 *            ��Ŀ����(C50)
	 * @param psItmUnit
	 *            ��λ[C10]
	 * @param psItmDesc
	 *            ��񡢼��͵�[C50]
	 * @param psFeeCode
	 *            �ѱ���[C3](AKA063 : 01��ҩ/02�г�ҩ/03�в�ҩ/...)
	 * @param psOTCCode
	 *            ����ҩ��־[C3](AKA064 0�Ǵ���ҩ/1����ҩ)
	 * @param pcQuantity
	 *            ����[N(8, 2)]
	 * @param pcPharPrice
	 *            Ӧ�۵���[N(10, 4)]
	 * @param pcFactPrice
	 *            ʵ�۵���[N(10, 4)]
	 * @param pcDosage
	 *            ÿ������[N(9, 3)]
	 * @param psFrequency
	 *            ʹ��Ƶ��[C20]
	 * @param psUsage
	 *            �÷�[C50]
	 * @param pcDays
	 *            ִ������[N(5, 2)]
	 * @param psOpCode
	 *            �շ�Ա����[C8]
	 * @param psDepCode
	 *            ���ұ���
	 * @param psDocCode
	 *            ����ҽ������[C8]
	 * @param psRecDate
	 *            ��������(YYYY-MM-DD HH:NN:SS)<סԺ��>
	 * @param psMess
	 *            ��ʾ��Ϣ
	 * @return Out pcRate����֧������[N(5,4)]|Out pcSelfFee�����Ը����[N(10, 4)] = (���� * ����
	 *         - ����׼����) * ����֧������|Out pcDeduct����׼����[N(10, 4)]
	 */
	public native static String FWriteFeeDetail2(int piShowMess, int piRecType,
			String psRecCode, String psItmFlag, String psItmCode,
			String psAliasCode, String psItmName, String psItmUnit,
			String psItmDesc, String psFeeCode, String psOTCCode,
			float pcQuantity, float pcPharPrice, float pcFactPrice,
			float pcDosage, String psFrequency, String psUsage, float pcDays,
			String psOpCode, String psDepCode, String psDocCode,
			String psRecDate, String psMess);

	/**
	 * ��������
	 * 
	 * ˵���� 1�� �������¼����Ϻ���Խ��㣻����ʱ�ϴ����ü�¼�� 2��
	 * ���Ե������ô˺�������������õ����㣬�˽�������뱾��ҽ�����ݿ�HT.INS�� 3�� �����������ǰ������������Խ��㣻 4��
	 * �������ط��ù�����������ʻ�֧����ͳ��֧�����ֽ�֧������Ϣ 5�� �������óɹ�����C:��Ŀ¼������һ���ı��ļ����ļ���ΪFTryBal0.txt
	 * 
	 * @param psRecCode
	 *            �����[C12]
	 * @param psOpCode
	 *            ����Ա��[C20]
	 * @param psUseAcc
	 *            �Ƿ�ʹ���ʻ�[C2](��/��)
	 * @param psDepCode
	 *            ���ұ���[C20]
	 * @param psDocCode
	 *            ҽ������[C20]
	 * @param psMedMode
	 *            ҽ�Ʒ�ʽ[C3]
	 * @param psRecClass
	 *            ҽ�����[C3]
	 * @param psICDMode
	 *            [C1](��A��)
	 * @param psICD
	 *            ��������
	 * @param pcOther1
	 *            ����1
	 * @param pcOther2
	 *            ����2
	 * @param psMemo
	 *            ��ע
	 * 
	 *            ������ 1�� ����psMedModeҽ�Ʒ�ʽ������룺��������1��ͨ���2��ͨסԺ��3�������4�������ȣ�5��� 2��
	 *            ����psRecClassҽ����������
	 *            ����������11��ͨ���12�������13ת���������14����ҩ�깺ҩ��21��ͨסԺ
	 *            ��22���ⲡ��סԺ��23ת������סԺ��24��ͥ������ 3��
	 *            ����psICD�������룺OA01���ﱨ��/OB01������ر���/OC01����תԺ����/
	 *            IA01סԺ����/IB01סԺ��ر���/IC01סԺתԺ���� 4�� ���˸������ = pcMedAccPay +
	 *            pcBankAccPay + pcCashPay 5�� ����ƽ���ϵ�� pcSumFee = pcPubPay +
	 *            pcHelpPay + pcSupplyPay + pcOtrPay + pcMedAccPay +
	 *            pcBankAccPay + pcCashPay
	 * 
	 * 
	 * 
	 * @return psBillCode ����UnKnown����|Out pcSumFee �����ܷ���[N10, 2]|Out pcGenFee
	 *         3����Χ�ڷ���[N10, 2]|Out pcFirstPay�Ը�����[N10, 2]|Out pcSelfFee
	 *         O�Էѷ���[N10, 2]|Out pcPayLevel O�𸶱�׼[N10, 2]|Out pcPubPayͳ��֧��[N10,
	 *         2]|Out pcPubSelf |Out pcHelpPay �󲡾�������֧��[N10, 2]|Out pcHelpSelf
	 *         |Out pcSupplyPay ����Ա/��ҵ����֧��[N10, 2]|Out pcSupplySelf |Out
	 *         pcOtrPay ��������֧��[N10, 2]|Out pcMedAccPay ����ҽ���ʻ�֧��[N10, 2]|Out
	 *         pcBankAccPay ���˴����ʻ�֧��[N10, 2]|Out pcCashPay �ֽ�֧��[N10, 2]
	 */
	public native static String FTryOutpatBalance(String psRecCode,
			String psOpCode, String psUseAcc, String psDepCode,
			String psDocCode, String psMedMode, String psRecClass,
			String psICDMode, String psICD, float pcOther1, float pcOther2,
			String psMemo);

	/**
	 * ������� ˵����
	 * 
	 * 1���������ﴦ�����á�ʹ��IC�����׹�������Ҫ����þ�����Ա��IC���������޷�������㡣
	 * 2���������¼����Ϻ���㣬�ڵ��ô˺���ǰ������������Խ��㺯�����������ط��ù�������������¼���
	 * 3���������óɹ�����C:��Ŀ¼������һ���ı��ļ����ļ���ΪFBal0.txt
	 * 
	 * @param psRecCode
	 *            �����[C12]
	 * @param psOpCode
	 *            ����Ա��[C20]
	 * @param psUseAcc
	 *            �Ƿ�ʹ���ʻ�[C2](��/��)
	 * @param psDepCode
	 *            ���ұ���[C20]
	 * @param psDocCode
	 *            ҽ������[C20]
	 * @param psMedMode
	 *            ҽ�Ʒ�ʽ
	 * @param psRecClass
	 *            ҽ�����[C3]
	 * @param psICDMode
	 *            [C1](��A��)
	 * @param psICD
	 *            ��������
	 * @param pcOther1
	 *            ����1
	 * @param pcOther2
	 *            ����2
	 * @param psMemo
	 *            I��ע
	 * @return psBillCode O������ˮ��[C7]|Out pcSumFee �ܷ���[N10, 2]|Out pcGenFee
	 *         3����Χ�ڷ���[N10, 2]|Out pcFirstPay �Ը�����[N10, 2]| Out pcSelfFee
	 *         �Էѷ���[N10, 2]|Out pcPayLevel �𸶱�׼[N10, 2]|Out pcPubPay ͳ��֧��[N10,
	 *         2]|Out pcPubSelfͳ������Ը�[N10, 2]|Out pcHelpPay �󲡾�������֧��[N10, 2]|Out
	 *         pcHelpSelf�󲡾�����������Ը�[N10, 2]|Out pcSupplyPay ����Ա����֧��/��ҵ����֧��[N10,
	 *         2]|Out pcSupplySelf ����Ա����/��ҵ��������Ը�[N10, 2]|Out pcOtrPay
	 *         ��������֧��[N10, 2]|Out pcMedAccPay����ҽ���ʻ�֧��[N10, 2]|Out pcBankAccPay
	 *         �ض���Ŀ[N10, 2]|Out pcCashPay �ֽ�֧��[N10, 2]
	 */
	public native static String FOutpatBalance(String psRecCode,
			String psOpCode, String psUseAcc, String psDepCode,
			String psDocCode, String psMedMode, String psRecClass,
			String psICDMode, String psICD, float pcOther1, float pcOther2,
			String psMemo);

	/**
	 * �����˷�
	 * 
	 * ˵���� 1�� ȡ�������շѣ���Ҫ�������š�ʹ��IC�����׹�������Ҫ����þ�����Ա��IC���������޷������˷ѡ� 2��
	 * ֻ��ȫ�ˣ����ܲ����˷ѣ��Զ�ʷ���
	 * ���˷�ʱһ���Ǵ��������һ�ʿ�ʼ�˷ѣ���÷����ķ��ò������˷ѣ���������ء����ǲ����˵ĸ���ԭ�򣬺������Զ���ʾ�� 3��
	 * �����˷Ѳ��ú�巽ʽ��������һ�ʸ���¼�� 4���������óɹ�����C:��Ŀ¼������һ���ı��ļ����ļ���ΪFCBal0.txt
	 * 
	 * 
	 * @param psRecCode
	 *            �����[C12]
	 * @param psBillCode
	 *            ������ˮ��[C7]
	 * @param psOpCode
	 *            ����Ա����[C20]
	 * @return Out pcSumFee�ܷ���[N10, 2]| Out pcGenFee 3����Χ�ڷ���[N10, 2]|Out
	 *         pcFirstPay �Ը�����[N10, 2]|Out pcSelfFee �Էѷ���[N10, 2]|Out pcPayLevel
	 *         �𸶱�׼[N10, 2]|Out pcPubPay ͳ��֧��[N10, 2]|Out pcPubSelf |Out
	 *         pcHelpPay �󲡾�������֧��[N10, 2]|Out pcHelpSelf |Out pcSupplyPay
	 *         ����Ա/��ҵ����֧��[N10, 2]|Out pcSupplySelf |Out pcOtrPay ��������֧��[N10,
	 *         2]|Out pcMedAccPay����ҽ���ʻ�֧��[N10, 2]|Out pcBankAccPay ���˴����ʻ�֧��[N10,
	 *         2]|Out pcCashPay �ֽ�֧��[N10, 2]
	 * 
	 */
	public native static String FCancelOutpatBalance(String psRecCode,
			String psBillCode, String psOpCode);

	/**
	 * סԺ�Ǽ�
	 * 
	 * ˵���� 1�� �α�������Ժʱ����Ժ�Ǽǣ����Ǽ���Ϣ��¼��⣻ʹ��IC�����׹�������Ҫ����þ�����Ա��IC���������޷�סԺ�Ǽǡ� 2��
	 * �������óɹ�����C:��Ŀ¼������һ���ı��ļ����ļ���ΪFReg1.txt
	 * 
	 * @param psRecCode
	 *            סԺ�ţ�����ţ�[C6]
	 * @param psMedMode
	 *            ҽ�Ʒ�ʽ[C3]�����������㣩
	 * @param psMedClass
	 *            ҽ�����[C3]�����������㣩
	 * @param psRegOpCode
	 *            ����Ա����[C20]
	 * @param psBegDate
	 *            ��Ժ����[C19](YYYY-MM-DD HH:NN:SS)
	 * @param psICDMode
	 *            ICD�������[C1](��A��)
	 * @param psICD
	 *            ��Ժ���[C20](ICD10����)
	 * @param psDepCode
	 *            ���Ҵ���[C20]
	 * @param psSecCode
	 *            ��������[C20]
	 * @param psRegDoc
	 *            ��Ժҽ������[C20]
	 * @return �������ۼ�סԺ����[N9, 4]
	 */
	public native static float FInpatReg(String psRecCode, String psMedMode,
			String psMedClass, String psRegOpCode, String psBegDate,
			String psICDMode, String psICD, String psDepCode, String psSecCode,
			String psRegDoc);

	/**
	 * ȡ��סԺ�Ǽ�
	 * 
	 * ˵���� 1�� �α����˵Ǽ�סԺ�󲻴���סԺ����ȡ���ǼǴ���ʹ��IC�����׹�������Ҫ����þ�����Ա��IC��������ȡ��סԺ�Ǽǡ� 2��
	 * ��������˷�����Ϣ��ȡ���ǼǺ������Ϣ���Զ����ϣ� 3�� �������óɹ�����C:��Ŀ¼������һ���ı��ļ����ļ���ΪFCReg1.txt
	 * 
	 * 
	 * @param psRecCode
	 *            סԺ�ţ�����ţ�[C6]
	 * @param psOpCode
	 *            ����Ա����[C20]
	 * @return ��������
	 */
	public native static int FCancelInpatReg(String psRecCode, String psOpCode);

	/**
	 * �޸�סԺ�Ǽ���Ϣ
	 * 
	 * ˵���� 1���α�����סԺ�еǼ���Ϣ�������ת�Ƶȣ����øú����޸ĵǼ���Ϣ��ʹ��IC�����׹�������Ҫ����þ�����Ա��IC���������޷��޸���Ϣ��
	 * 2���������óɹ�����C:��Ŀ¼������һ���ı��ļ����ļ���ΪFUReg1.txt
	 * 
	 * @param psRecCode
	 *            סԺ��[C6]
	 * @param psMedMode
	 *            ҽ�Ʒ�ʽ[C3]
	 * @param psMedClass
	 *            ҽ�����[C3]
	 * @param psRegOpCode
	 *            ����Ա����[C20]
	 * @param psBegDate
	 *            ���￪ʼ����[C19](YYYY-MM-DD HH:NN:SS)
	 * @param psICDMode
	 *            ICD�������[C1](��A��)
	 * @param psICD
	 *            ��Ժ���[C20](ICD10����)
	 * @param psDepCode
	 *            ���Ҵ���[C20]
	 * @param psSecCode
	 *            ��������[C20]
	 * @param psRegDoc
	 *            ��Ժҽ������[C20]
	 * @return ��������
	 */
	public native static int FChgInpatReg(String psRecCode, String psMedMode,
			String psMedClass, String psRegOpCode, String psBegDate,
			String psICDMode, String psICD, String psDepCode, String psSecCode,
			String psRegDoc);

	/**
	 * ��Ժ
	 * 
	 * ˵���� 1�� �α����˳�Ժ�Ĳ�����������Ժ�Ǽǣ������ǽ��˴���ʹ��IC�����׹�������Ҫ����þ�����Ա��IC���������޷���Ժ�� 2��
	 * ��Ժ����ǰ������ú��������ô����ǣ���Ժ���������㣬��Ժ���㣻 3�� �������óɹ�����C:��Ŀ¼������һ���ı��ļ����ļ���ΪFLeave1.txt
	 * 
	 * @param psRecCode
	 *            סԺ��[C6]
	 * @param psOutOpCode
	 *            ����Ա����[C20]
	 * @param psEndDate
	 *            ��Ժ����[C19](YYYY-MM-DD HH:NN:SS)
	 * @param psOutCause
	 *            ��Ժԭ��[C3]
	 * @param psICDMode
	 *            ICD�������[C1](��A��)
	 * @param psICD
	 *            ��Ժ���[C20](ICD10����)
	 * @param psOutDoc
	 *            ��Ժҽ������[C20]
	 * 
	 *            ������ 1�� ����psOutCause��Ժԭ������룺�������£�1������2��ת��3δ����4������5תԺ��6ת�⣻9����
	 * 
	 * 
	 * @return ��������
	 */
	public native static int FInpatLeave(String psRecCode, String psOutOpCode,
			String psEndDate, String psOutCause, String psICDMode,
			String psICD, String psOutDoc);

	/**
	 * סԺ��������
	 * 
	 * ˵���� 1�� ҽԺ����Ժ���˵ķ��ý���Ԥ���㣬������ȡסԺѺ�𣬴�����¿���ֱ�ӵ���סԺ�������㣺�������������ο�����Ϣ����ҽ�����ؿ⣻
	 * ʹ��IC�����׹�������Ҫ����þ�����Ա��IC���������޷�סԺ�������㡣 2��
	 * ��Ժʱ���ڳ�Ժ����ǰ������÷������㣻�������������ο�����Ϣ����ҽ�����ؿ⣻ 3��
	 * ���÷�������ʱ���Զ��ϴ�������ϸ��������ϸ�϶�ʱ����Ҫ�ȴ�ʱ�䡣 4��
	 * �������óɹ�����C:��Ŀ¼������һ���ı��ļ����ļ���ΪFTryBal1.txt
	 * 
	 * @param psRecCode
	 *            סԺ��[C6]
	 * @param psOpCode
	 *            ����Ա����[C20]
	 * @param psUseAcc
	 *            �Ƿ�ʹ���ʻ�[C2](��/��)
	 * @param piLiquiMode
	 *            ���㷽ʽ[C1]
	 * @param psRefundID
	 *            ��������[C4]
	 * @param pcOther1
	 *            ����1
	 * @param pcOther2
	 *            ����2
	 * @param psMemo
	 *            ��ע
	 * 
	 *            ������ 1�� ����piLiquiMode���㷽ʽ������룺��������0�������㣻1��;���� 2��
	 *            ����psRefundID��������ͬ���������㡱 psICD��������
	 * 
	 * 
	 * @return psBillCode ����UnKnown����|Out pcSumFee �ܷ���[N10, 2]|Out pcGenFee
	 *         3����Χ�ڷ���[N10, 2]|Out pcFirstPay�Ը�����[N10, 2]|Out pcSelfFee�Էѷ���[N10,
	 *         2]|Out pcPayLevel�𸶱�׼[N10, 2]|Out pcPubPay ͳ��֧��[N10, 2]|Out
	 *         pcPubSelf |Out pcHelpPay �󲡾�������֧��[N10, 2]|Out pcHelpSelf |Out
	 *         pcSupplyPay����Ա/��ҵ����֧��[N10, 2]|Out pcSupplySelf |Out
	 *         pcOtrPay��������֧��[N10, 2]|Out pcMedAccPay����ҽ���ʻ�֧��[N10, 2]|Out
	 *         pcBankAccPay ���˴����ʻ�֧��[N10, 2]|Out pcCashPay�ֽ�֧��[N10, 2]
	 */
	public native static String FTryInpatBalance(String psRecCode,
			String psOpCode, String psUseAcc, String piLiquiMode,
			String psRefundID, float pcOther1, float pcOther2, String psMemo);

	/**
	 * סԺ���ý���
	 * 
	 * ˵���� 1�� ��Ժ���ʣ���Ϣ��¼��⣻ʹ��IC�����׹�������Ҫ����þ�����Ա��IC���������޷�סԺ���ý��㡣 2��
	 * �������óɹ�����C:��Ŀ¼������һ���ı��ļ����ļ���ΪFBal1.txt
	 * 
	 * @param psRecCode
	 *            סԺ��[C6]
	 * @param psOpCode
	 *            ����Ա����[C20]
	 * @param psUseAcc
	 *            �Ƿ�ʹ���ʻ�[C2](��/��)
	 * @param piLiquiMode
	 *            ���㷽ʽ[C1]��ͬ���㣩
	 * @param psRefundID
	 *            ��������[C4]
	 * @param pcOther1
	 *            ����1
	 * @param pcOther2
	 *            ����2
	 * @param psMemo
	 *            ��ע
	 * @return psBillCode ������ˮ��[C7]| Out pcSumFee �ܷ���[N10, 2]|Out pcGenFee
	 *         3����Χ�ڷ���[N10, 2]|Out pcFirstPay �Ը�����[N10, 2]|Out pcSelfFee
	 *         �Էѷ���[N10, 2]|Out pcPayLevel�𸶱�׼[N10, 2]|Out pcPubPay ͳ��֧��[N10,
	 *         2]|Out pcPubSelf|Out pcHelpPay �󲡾�������֧��[N10, 2]|Out
	 *         pcHelpSelf|Out pcSupplyPay ����Ա/��ҵ����֧��[N10, 2]|Out pcSupplySelf
	 *         |Out pcOtrPay ��������֧��[N10, 2]|Out pcMedAccPay ����ҽ���ʻ�֧��[N10, 2]|Out
	 *         pcBankAccPay ���˴����ʻ�֧��[N10, 2]|Out pcCashPay �ֽ�֧��[N10, 2]
	 */
	public native static String FInpatBalance(String psRecCode,
			String psOpCode, String psUseAcc, String piLiquiMode,
			String psRefundID, float pcOther1, float pcOther2, String psMemo);

	/**
	 * ȡ��סԺ����
	 * 
	 * ˵���� 1�� ����סԺ������Ϣ���α����˷�����Ժ״̬��ʹ��IC�����׹�������Ҫ����þ�����Ա��IC���������޷�ȡ��סԺ���ˡ� 2��
	 * �������óɹ�����C:��Ŀ¼������һ���ı��ļ����ļ���ΪFCBal1.txt
	 * 
	 * @param psRecCode
	 *            סԺ��[C6]
	 * @param psBillCode
	 *            ������ˮ��[C7]
	 * @param psOpCode
	 *            ����Ա����[C20]
	 * @return Out pcSumFee �ܷ���[N10, 2]|Out pcGenFee 3����Χ�ڷ���[N10, 2]|Out
	 *         pcFirstPay �Ը�����[N10, 2]|Out pcSelfFee �Էѷ���[N10, 2]| Out
	 *         pcPayLevel �𸶱�׼[N10, 2]|Out pcPubPay ͳ��֧��[N10, 2]|Out pcPubSelf
	 *         |Out pcHelpPay �󲡾�������֧��[N10, 2]|Out pcHelpSelf|Out pcSupplyPay
	 *         ����Ա/��ҵ����֧��[N10, 2]|Out pcSupplySelf |Out pcOtrPay ��������֧��[N10,
	 *         2]|Out pcMedAccPay ����ҽ���ʻ�֧��[N10, 2]|Out pcBankAccPay
	 *         ���˴����ʻ�֧��[N10, 2]|Out pcCashPay �ֽ�֧��[N10, 2]
	 */
	public native static String FCancelInpatBalance(String psRecCode,
			String psBillCode, String psOpCode);

	/**
	 * �ϴ�
	 * 
	 * ˵���� 1�� ���ô˺�����ҽ�����ؿ�HT.HIS����δ�ϴ������ݴ�����ҽ�����ģ� 2��
	 * �ϴ�ǰ��ҽԺHIS���ݱ����Ѿ����÷���¼�뺯�����뵽��ҽ�����ؿ⣻
	 * 
	 * @param piType
	 *            2
	 * @param psRecCode
	 *            סԺ�ţ���Ϊ*��ʾ�ϴ����У�
	 * @return ��������;
	 */
	public native static int FUpLoad(int piType, String psRecCode);

	/**
	 * ͨ�õ��뺯��
	 * 
	 * ˵���� 1�� �ú���Ϊͨ�õ����ݵ��뺯�������ڴ�ҽԺHIS�������ݵ�ҽ�����ص����ݿ⣻ 2�� �������ݱ�Ϊ1����Ա��2ҽ����3ҩƷ�ֵ䡢4������Ŀ
	 * 
	 * @param piType
	 *            1����/2����Ա/ 3ҽ��/4ҩƷ�ֵ�/5��
	 * @param psInfo1
	 *            ������Ϣ[C200]
	 * @param psInfo2
	 *            ������Ϣ[C200]
	 * @param psInfo3
	 *            ������Ϣ[C200]
	 * @param psInfo4
	 *            ������Ϣ[C200]
	 * @param psRemark
	 *            ��ע[C1024]
	 * @param psOpStaus
	 *            ����״̬ I
	 * 
	 *            ����˵���� A�� ���� piType �� 1 psInfo1 �� ���ұ��� psInfo2 �� �������� B�� ����Ա��
	 *            piType �� 2 psInfo1 �� ����Ա���� psInfo2 �� ���� C�� ҽ��: piType �� 3
	 *            psInfo1 �� ҽ������ psInfo2 �� ҽ������ psInfo3 ���������ұ��� psInfo4
	 *            ��ְ��(����ҽʦ/������ҽʦ/����ҽʦ/��) D�� ҩƷ�ֵ� piType �� 4 psInfo1 �� ҩƷ�Ա���
	 *            psInfo2 ����ҽ������ psInfo3 �� ����(��Ʒ��) psInfo4 �����ۼ�
	 *            psRemark������|������λ|���|����|���� ��˵����
	 *            psInfo4��Ӧ�������λ��Ӧ�����������λΪ�У������ۼ�Ϊһ�еļ۸�
	 *            psRemark���������е���������|�ָ���Ϊ��ʱ����ַ����� E�� ������Ŀ�� piType �� 5 psInfo1 ��
	 *            ��Ŀ�Ա��� psInfo2 ����ҽ������ psInfo3 �� ���� psInfo4 �����ۼ� psRemark����λ
	 * 
	 * 
	 * @return
	 */
	public native static int FImpInfo(int piType, String psInfo1,
			String psInfo2, String psInfo3, String psInfo4, String psRemark,
			String psOpStaus);

	/**
	 * ͨ�õ�������
	 * 
	 * ˵���� 1�� ���ڵ���ҽ�����ؿ����ݣ���ҽ��ҩƷ�ֵ䡢���ձ�ȣ� 2��
	 * �������Ϊ����������Լ�����͵����ļ�����ΪTXT�ı����ո�ָ������ļ�������·����·��������ʱ���Զ������ڽӿڶ�̬������·����
	 * 
	 * 
	 * @param psTable
	 *            //I ����
	 * @param psFile
	 *            //I�ļ���
	 * @return
	 */
	public native static int FExpInfo(String psTable, String psFile);

	/**
	 * ����������;
	 * 
	 * @param strResult
	 * @param separator
	 * @return
	 */
	public static String[] splitReust(String strResult, String separator) {
		if (strResult == null || strResult.equals("")) {
			return null;
		}
		String resultArray[] = strResult.split(separator);
		return resultArray;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		JSProvInwDriver test = new JSProvInwDriver();

	}
}
