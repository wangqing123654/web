package jdo.hl7.ws;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import sun.misc.BASE64Decoder;
import jdo.hl7.BILJdo;
import jdo.hl7.Hl7Communications;
import jdo.hl7.Hl7Tool;
import jdo.hl7.pojo.Hl7Result;
import jdo.sys.PatTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.exception.HL7Exception;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ҽ��webService
 * </p>
 * 
 * <p>
 * Description: ҽ��webService
 * </p>
 * 
 * <p>
 * Copyright: BLUECORE
 * </p>
 * 
 * <p>
 * Company:BLUECORE
 * </p>
 * 
 * @author SHIBL
 * @version 1.0
 */
@WebService
public class Hl7ServiceImpl implements IHl7Service {

	/**
	 * �س���
	 */
	private String enter = "" + (char) 13 + (char) 10;

	/**
	 * ���ݺ���õ�LISҽ����Ϣ
	 * 
	 * @param ApplyNo
	 * @return
	 */
	@Override
	public List<Hl7Result> getLisOrderData(String ApplyNo) {
		List<Hl7Result> list = new ArrayList<Hl7Result>();
		Hl7Result lisList = new Hl7Result();
		if (StringUtil.isNullString(ApplyNo)) {
			lisList.setCode("-1");
			lisList.setResult("���벻��Ϊ��");
			list.add(lisList);
			return list;
		}
		try {
			TParm medData = Hl7Tool.getInstance().getOrder(ApplyNo, "LIS","");
			if (medData.getCount() <= 0) {
				lisList.setCode("-1");
				lisList.setResult("�޲�ѯҽ������");
				list.add(lisList);
				return list;
			}
//			String status = medData.getValue("STATUS", 0);
//			String orderDesc = medData.getValue("ORDER_DESC", 0);
//			if (!status.equals("0")) {
//				lisList.setCode("-1");
//				lisList.setResult(orderDesc
//						+ "��"
//						+ Status.getStatusMap()
//								.get(medData.getInt("STATUS", 0)));
//				list.add(lisList);
//				return list;
//			}
			StringBuffer sendData = new StringBuffer();
			TParm patP = new TParm();
			patP.setData("MR_NO", medData.getValue("MR_NO", 0));
			TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
			if (patInfoParm.getCount() <= 0) {
				lisList.setCode("-1");
				lisList.setResult("�޲�ѯ��������");
				list.add(lisList);
				return list;
			}
			patInfoParm = patInfoParm.getRow(0);
			patInfoParm.setData("CASE_NO", medData.getRow(0).getValue("CASE_NO"));
			patInfoParm.setData("ADM_TYPE", medData.getRow(0).getValue("ADM_TYPE"));
			String mshStr = "";
			String pidStr = "";
			String pv1Str = "";
			String orcStr = "";
			String obrStr = "";
			String netStr = "";
			String dg1Str = "";
			String orcAndobr = "";
			mshStr = Hl7Communications.getInstance().createMsh(
					medData.getRow(0), "LIS");
			pidStr = Hl7Communications.getInstance().createPid(patInfoParm,
					"LIS");
			pv1Str = Hl7Communications.getInstance().createPv1(
					medData.getRow(0), "LIS");
			int rowCount = medData.getCount();
			for (int i = 0; i < rowCount; i++) {
				TParm orcParm = medData.getRow(i);
				orcParm.setData("TYPE",
						orcParm.getValue("STATUS").equals("9") ? "CA" : "NW");
				orcStr = Hl7Communications.getInstance().createOrc(orcParm,
						"LIS");
				orcAndobr += orcStr + enter;
				obrStr = Hl7Communications.getInstance().createObr(orcParm,
						"LIS");
				orcAndobr += obrStr + enter;
			}
			netStr = Hl7Communications.getInstance().createNet(
					medData.getRow(0), "LIS");
			dg1Str = Hl7Communications.getInstance().createDg1(
					medData.getRow(0), "LIS");
			// ����MSHͷ
			if (mshStr.length() == 0) {
				lisList.setCode("-1");
				lisList.setResult("HL7����MSHͷʧ��");
				list.add(lisList);
				return list;
			}
			sendData.append(mshStr);
			sendData.append(enter);
			// ����PIDͷ
			if (pidStr.length() == 0) {
				lisList.setCode("-1");
				lisList.setResult("HL7����PIDͷʧ��");
				list.add(lisList);
				return list;
			}
			sendData.append(pidStr);
			sendData.append(enter);
			// ����PV1ͷ
			if (pv1Str.length() == 0) {
				lisList.setCode("-1");
				lisList.setResult("HL7����PV1ͷʧ��");
				list.add(lisList);
				return list;
			}
			sendData.append(pv1Str);
			sendData.append(enter);
			// ����ORC��OBR���ͷ
			if (orcAndobr.length() == 0) {
				lisList.setCode("-1");
				lisList.setResult("HL7����ORC��OBR���ͷʧ��");
				list.add(lisList);
				return list;
			}
			sendData.append(orcAndobr);
			// ����NETͷ
			if (netStr.length() == 0) {
				lisList.setCode("-1");
				lisList.setResult("HL7����NETͷʧ��");
				list.add(lisList);
				return list;
			}
			sendData.append(netStr);
			sendData.append(enter);
			// ����DG1ͷ
			if (dg1Str.length() == 0) {
				lisList.setCode("-1");
				lisList.setResult("HL7����DG1ͷʧ��");
				list.add(lisList);
				return list;
			}
			sendData.append(dg1Str);
			lisList.setCode("0");
			lisList.setResult(sendData.toString());
		} catch (HL7Exception ex1) {
			lisList.setCode("-1");
			lisList.setResult("��ȡHL7����ʧ��");
			list.add(lisList);
			return list;
		}
		list.add(lisList);
		return list;
	}

	/**
	 * ���ݺ���õ�RISҽ����Ϣ
	 * 
	 * @param ApplyNo
	 * @return
	 */
	@Override
	public List<Hl7Result> getRisOrderData(String ApplyNo,String ExecDept) {
		List<Hl7Result> list = new ArrayList<Hl7Result>();
		Hl7Result risList = new Hl7Result();
		if (ApplyNo.equals("")) {
			risList.setCode("-1");
			risList.setResult("���벻��Ϊ��");
			list.add(risList);
			return list;
		}
		try {
			TParm medData = Hl7Tool.getInstance().getOrder(ApplyNo, "RIS",ExecDept);
			if (medData.getCount() <= 0) {
				risList.setCode("-1");
				risList.setResult("�޲�ѯҽ������");
				list.add(risList);
				return list;
			}
//			String status = medData.getValue("STATUS", 0);
//			String orderDesc = medData.getValue("ORDER_DESC", 0);
//			if (!status.equals("0")) {
//				risList.setCode("-1");
//				risList.setResult(orderDesc
//						+ "��"
//						+ Status.getStatusMap()
//								.get(medData.getInt("STATUS", 0)));
//				list.add(risList);
//				return list;
//			}
			StringBuffer sendData = new StringBuffer();
			TParm patP = new TParm();
			patP.setData("MR_NO", medData.getValue("MR_NO", 0));
			TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
			if (patInfoParm.getCount() <= 0) {
				risList.setCode("-1");
				risList.setResult("�޲�ѯ��������");
				list.add(risList);
				return list;
			}
			patInfoParm = patInfoParm.getRow(0);
			patInfoParm.setData("CASE_NO", medData.getRow(0).getValue("CASE_NO"));
			patInfoParm.setData("ADM_TYPE", medData.getRow(0).getValue("ADM_TYPE"));
			String mshStr = "";
			String pidStr = "";
			String pv1Str = "";
			String orcStr = "";
			String obrStr = "";
			String netStr = "";
			String dg1Str = "";
			String orcAndobr = "";
			mshStr = Hl7Communications.getInstance().createMsh(
					medData.getRow(0), "RIS");
			pidStr = Hl7Communications.getInstance().createPid(patInfoParm,
					"RIS");
			pv1Str = Hl7Communications.getInstance().createPv1(
					medData.getRow(0), "RIS");
			int rowCount = medData.getCount();
			for (int i = 0; i < rowCount; i++) {
				TParm orcParm = medData.getRow(i);
				orcParm.setData("TYPE",
						orcParm.getValue("STATUS").equals("9") ? "CA" : "NW");
				orcStr = Hl7Communications.getInstance().createOrc(orcParm,
						"RIS");
				orcAndobr += orcStr + enter;
				obrStr = Hl7Communications.getInstance().createObr(orcParm,
						"RIS");
				orcAndobr += obrStr + enter;
			}
			netStr = Hl7Communications.getInstance().createNet(
					medData.getRow(0), "RIS");
			dg1Str = Hl7Communications.getInstance().createDg1(
					medData.getRow(0), "RIS");
			// ����MSHͷ
			if (mshStr.length() == 0) {
				risList.setCode("-1");
				risList.setResult("HL7����MSHͷʧ��");
				list.add(risList);
				return list;
			}
			sendData.append(mshStr);
			sendData.append(enter);
			// ����PIDͷ
			if (pidStr.length() == 0) {
				risList.setCode("-1");
				risList.setResult("HL7����PIDͷʧ��");
				list.add(risList);
				return list;
			}
			sendData.append(pidStr);
			sendData.append(enter);
			// ����PV1ͷ
			if (pv1Str.length() == 0) {
				risList.setCode("-1");
				risList.setResult("HL7����PV1ͷʧ��");
				list.add(risList);
				return list;
			}
			sendData.append(pv1Str);
			sendData.append(enter);
			// ����ORC��OBR���ͷ
			if (orcAndobr.length() == 0) {
				risList.setCode("-1");
				risList.setResult("HL7����ORC��OBR���ͷʧ��");
				list.add(risList);
				return list;
			}
			sendData.append(orcAndobr);
			// ����NETͷ
			if (netStr.length() == 0) {
				risList.setCode("-1");
				risList.setResult("HL7����NETͷʧ��");
				list.add(risList);
				return list;
			}
			sendData.append(netStr);
			sendData.append(enter);
			// ����DG1ͷ
			if (dg1Str.length() == 0) {
				risList.setCode("-1");
				risList.setResult("HL7����DG1ͷʧ��");
				list.add(risList);
				return list;
			}
			sendData.append(dg1Str);
			risList.setCode("0");
			risList.setResult(sendData.toString());
		} catch (HL7Exception ex1) {
			risList.setCode("-1");
			risList.setResult("��ȡHL7����ʧ��");
			list.add(risList);
			return list;
		}
		list.add(risList);
		return list;
	}

	/**
	 * ���ݺ���õ�RISҽ����Ϣ
	 * 
	 * @param ApplyNo
	 * @return
	 */
	@Override
	public List<Hl7Result> getRisOrderList(String ExecDept, String MrNo,
			String StartDate, String EndDate) {
		List<Hl7Result> list = new ArrayList<Hl7Result>();
		List<String> resultList = new ArrayList<String>();
		Hl7Result risList = new Hl7Result();
		try {
			if (StartDate.equals("")) {
				risList.setCode("-1");
				risList.setResult("��ʼʱ�䲻��Ϊ��");
				list.add(risList);
				return list;
			}
			if (EndDate.equals("")) {
				risList.setCode("-1");
				risList.setResult("����ʱ�䲻��Ϊ��");
				list.add(risList);
				return list;
			}
			TParm medData = Hl7Tool.getInstance().getOrderList(MrNo, "RIS",
					ExecDept, StartDate, EndDate);
			if (medData.getCount() <= 0) {
				risList.setCode("0");
				risList.setResultList(resultList);
				list.add(risList);
				return list;
			}
			StringBuffer errorlog=new StringBuffer();
			Map map = new HashMap();
			for (int j = 0; j < medData.getCount(); j++) {
				String status = medData.getValue("STATUS", j);
				String ApplyNo = medData.getRow(j).getValue("APPLICATION_NO");
//				if (!status.equals("0"))
//					continue;
				if (map.get(ApplyNo) == null) {
					StringBuffer sendData = new StringBuffer();
					TParm patP = new TParm();
					patP.setData("MR_NO", medData.getValue("MR_NO", j));
					TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
					if (patInfoParm.getCount() <= 0) {
						continue;
					}
					patInfoParm = patInfoParm.getRow(0);
					patInfoParm.setData("CASE_NO", medData.getRow(0).getValue("CASE_NO"));
					patInfoParm.setData("ADM_TYPE", medData.getRow(0).getValue("ADM_TYPE"));
					String mshStr = "";
					String pidStr = "";
					String pv1Str = "";
					String orcStr = "";
					String obrStr = "";
					String netStr = "";
					String dg1Str = "";
					String orcAndobr = "";
					mshStr = Hl7Communications.getInstance().createMsh(
							medData.getRow(j), "RIS");
					pidStr = Hl7Communications.getInstance().createPid(
							patInfoParm, "RIS");					
					pv1Str = Hl7Communications.getInstance().createPv1(
							medData.getRow(j), "RIS");
					TParm PmedData = Hl7Tool.getInstance().getOrder(ApplyNo,
							"RIS","");
					int rowCount = PmedData.getCount();
					for (int i = 0; i < rowCount; i++) {
						TParm orcParm = PmedData.getRow(i);
						orcParm.setData("TYPE", orcParm.getValue("STATUS")
								.equals("9") ? "CA" : "NW");
						orcStr = Hl7Communications.getInstance().createOrc(
								orcParm, "RIS");
						orcAndobr += orcStr + enter;
						obrStr = Hl7Communications.getInstance().createObr(
								orcParm, "RIS");
						orcAndobr += obrStr + enter;
					}
					netStr = Hl7Communications.getInstance().createNet(
							medData.getRow(j), "RIS");
					dg1Str = Hl7Communications.getInstance().createDg1(
							medData.getRow(j), "RIS");
					// ����MSHͷ
					if (mshStr.length() == 0) {
						errorlog.append("���뵥"+ApplyNo+":HL7����MSHͷʧ��");
						continue;
					}
					sendData.append(mshStr);
					sendData.append(enter);
					// ����PIDͷ
					if (pidStr.length() == 0) {
						errorlog.append("���뵥"+ApplyNo+":HL7����PIDͷʧ��;");
						continue;
					}
					sendData.append(pidStr);
					sendData.append(enter);
					// ����PV1ͷ
					if (pv1Str.length() == 0) {
						errorlog.append("���뵥"+ApplyNo+":HL7����PV1ͷʧ��;");
						continue;
					}
					sendData.append(pv1Str);
					sendData.append(enter);
					// ����ORC��OBR���ͷ
					if (orcAndobr.length() == 0) {
						errorlog.append("���뵥"+ApplyNo+":HL7����ORC��OBR���ͷʧ��;");
						continue;
					}
					sendData.append(orcAndobr);
					// ����NETͷ
					if (netStr.length() == 0) {
						errorlog.append("���뵥"+ApplyNo+":HL7����NETͷʧ��;");
						continue;
					}
					sendData.append(netStr);
					sendData.append(enter);
					// ����DG1ͷ
					if (dg1Str.length() == 0) {
						errorlog.append("���뵥"+ApplyNo+":HL7����DG1ͷʧ��;");
						continue;
					}
					sendData.append(dg1Str);
					resultList.add(sendData.toString());
				} else {
					map.put(ApplyNo, ApplyNo);
				}
			}
			if(errorlog.toString().length()>0){
				risList.setCode("-1");
				risList.setResult(errorlog.toString());
			}else{
				risList.setResultList(resultList);
				risList.setCode("0");
			}
		} catch (HL7Exception ex1) {
			risList.setCode("-1");
			risList.setResult("��ȡHL7����ʧ��");
			list.add(risList);
			return list;
		}
		list.add(risList);
		return list;
	}

	/**
	 * LISHL7��Ϣ����ʽ
	 * 
	 * @param Hl7Data
	 * @return
	 */
	@Override
	public List<Hl7Result> onLisOrderOperate(String Hl7Data) {
		List<Hl7Result> list = new ArrayList<Hl7Result>();
		Hl7Result lisList = new Hl7Result();
		if (StringUtil.isNullString(Hl7Data)) {
			lisList.setCode("-1");
			lisList.setResult("���ݲ���Ϊ��");
			list.add(lisList);
			return list;
		}
		ILISOperation lis = new ALISOperation();
		String result = "";
		String code = "";
		try {
			TParm parm = onOperateData(Hl7Data);
			lis.mainLisData(Hl7Data, parm);
			result = lis.getReturnValues();
			code = lis.getReturnCode();
		} catch (HL7Exception e) {
			lisList.setCode("-1");
			lisList.setResult("�����쳣");
			list.add(lisList);
			return list;
		}
		lisList.setCode(code);
		lisList.setResult(result);
		list.add(lisList);
		return list;
	}

	/**
	 * RISHL7��Ϣ����ʽ
	 * 
	 * @param Hl7Data
	 * @return
	 */
	@Override
	public List<Hl7Result> onRisOrderOperate(String Hl7Data) {
		List<Hl7Result> list = new ArrayList<Hl7Result>();
		Hl7Result risList = new Hl7Result();
		if (Hl7Data.equals("")) {
			risList.setCode("-1");
			risList.setResult("���ݲ���Ϊ��");
			list.add(risList);
			return list;
		}
		IRISOperation ris = new ARISOperation();
		String result = "";
		String code = "";
		try {
			TParm parm = onOperateData(Hl7Data);
			ris.mainLisData(Hl7Data, parm);
			result = ris.getBytReturnData();
			code = ris.getReturnCode();
		} catch (HL7Exception e) {
			risList.setCode("-1");
			risList.setResult("�����쳣");
			list.add(risList);
			return list;
		}
		risList.setCode(code);
		risList.setResult(result);
		list.add(risList);
		return list;
	}

	/**
	 * ����������֤
	 * 
	 * @param
	 * @return
	 */
	public List<Hl7Result> onFeeQuery(String ApplyNo,String cat1Type) {
		List<Hl7Result> list = new ArrayList<Hl7Result>();
		Hl7Result risList = new Hl7Result();
		if (ApplyNo.equals("")) {
			risList.setCode("-4");
			risList.setResult("���뵥�Ų���Ϊ��");
			list.add(risList);
			return list;
		}
		TParm medData=new TParm();
		try {
			medData = Hl7Tool.getInstance().getOrder(ApplyNo, cat1Type,"");
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			risList.setCode("-2");
			risList.setResult("δ��⵽���뵥��Ϣ");
			list.add(risList);
			return list;
		}
		if (medData.getCount() <= 0) {
			risList.setCode("-2");
			risList.setResult("δ��⵽���뵥��Ϣ");
			list.add(risList);
			return list;
		}
		if (medData.getValue("ADM_TYPE", 0).equals("O")
				|| medData.getValue("ADM_TYPE", 0).equals("E")) {
			TParm parm = new TParm();
			parm.setData("MED_APPLY_NO", ApplyNo);
			TParm result = BILJdo.getInstance().onOEBilCheck(parm, cat1Type);
			if (result.getErrCode() < 0) {
				risList.setCode(result.getErrCode() + "");
				risList.setResult(result.getErrText());
				list.add(risList);
				return list;
			}
		} else if (medData.getValue("ADM_TYPE", 0).equals("I")) {
			TParm parm = new TParm();
			parm.setData("MED_APPLY_NO", ApplyNo);
			try {
				parm = Hl7Tool.getInstance().getOdiOrder(ApplyNo, cat1Type, "ADD");
			} catch (HL7Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				risList.setCode("-3");
				risList.setResult("δ��⵽ִ��ҽ����Ϣ");
				list.add(risList);
				return list;
			}
			if (parm.getCount() <= 0) {
				risList.setCode("-3");
				risList.setResult("δ��⵽ִ��ҽ����Ϣ");
				list.add(risList);
				return list;
			}
		}
		risList.setCode("0");
		risList.setResult("�ɹ�");
		list.add(risList);
		return list;
	}

	/**
	 * �������ݷ���
	 * 
	 * @param Hl7Data
	 * @return
	 */
	private TParm onOperateData(String Hl7Data)throws HL7Exception{
		TConfig config = TConfig.getConfig("WEB-INF/config/system/THl7.x");
		String line = config.getString("TargetArrayIndex");
		String DEBUG = config.getString("Debug");
		String enter = "" + (char) 10;
		if (DEBUG.equals("Y")) {
			System.out.println("�����ַ���:" + Hl7Data);
		}
		// HL7������;
		String fileData[] = StringTool.parseLine(Hl7Data, enter);
		// MSH:MEUF:4;MSH:MTYPE:8;ORC:LABNO:2
		String targetArrayIndex[] = line.split(";");
		// ��������
		TParm parm = new TParm();
		for (int i = 0; i < fileData.length; i++) {
			if (fileData[i].length() == 0)
				continue;
			// �е�һ��ֵ��
			String massagesData = StringTool.parseLine(fileData[i], "|")[0];
			if (massagesData.length() == 0 || massagesData == null)
				continue;
			for (int j = 0; j < targetArrayIndex.length; j++) {
				// MSH
				if (massagesData.equals(targetArrayIndex[j].split(":")[0])) {
					// hl7�����ݣ�
					String temp[] = StringTool.parseLine(fileData[i], "|");
					// 4
					int arrLen = StringTool.getInt(targetArrayIndex[j]
							.split(":")[2]);
					//
					// MSH:MEUF:4
					String tempData[] = targetArrayIndex[j].split(":");
					// Ŀ��������Hl7��������
					if (arrLen > temp.length - 1) {
						parm.addData(targetArrayIndex[j].split(":")[1], "");
						continue;
					}
					// �����д�С3��
					if (tempData.length > 3) {
						String dataStr = temp[arrLen];
						String dataS[] = dataStr.split(targetArrayIndex[j]
								.split(":")[3].equals("^") ? "\\^"
								: targetArrayIndex[j].split(":")[3]);
						parm.addData(targetArrayIndex[j].split(":")[1],
								dataS[StringTool.getInt(targetArrayIndex[j]
										.split(":")[4])]);
						continue;
					}
					String returnData = temp[arrLen];
					parm.addData(targetArrayIndex[j].split(":")[1], returnData);
				}
			}
		}
		if (parm == null) {
			return null;
		}
		if (DEBUG.equals("Y")) {
			System.out.println("�����������:" + parm);
		}
		return parm;
	}

	/**
	 * �ַ�������ת����ʵ�ַ���
	 * 
	 * @param str
	 *            ��ת��������ַ���
	 * @param newCharset
	 *            Ŀ�����
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String changeCharset(String str, String newCharset)
			throws UnsupportedEncodingException {
		if (str != null) {
			byte[] bs = str.getBytes(Charset.forName(newCharset));
			// ���µ��ַ����������ַ���
			return new String(bs, newCharset);
		}
		return null;
	}

	// �� s ���� BASE64 ����
	public static String getBASE64(String s) {
		if (s == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
	}

	// �� BASE64 ������ַ��� s ���н���
	public static String getFromBASE64(String s) {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	@Override
	public String getRisOrderLine(String ApplyNo, String ExecDept, String MrNo,
			String StartDate, String EndDate) {
		String line = "";
		if (!ApplyNo.equals("") && !ApplyNo.toLowerCase().equals("null")) {
			List<Hl7Result> list = getRisOrderData(ApplyNo,ExecDept);
			String code = list.get(0).getCode();
			String result = list.get(0).getResult();
			line = code + "@#@*%$" + result;
		} else {
			List<Hl7Result> list = this.getRisOrderList(ExecDept, MrNo,
					StartDate, EndDate);
			String code = list.get(0).getCode();
			String result = list.get(0).getResult();
			List<String> hl7list = list.get(0).getResultList();
			if (code.equals("-1")) {
				line = code + "@#@*%$" + result;
			} else {
				if (hl7list.size() <= 0) {
					line = code + "@#@*%$" + result;
				} else {
					line = code;
					for (int i = 0; i < hl7list.size(); i++) {
						String hl7 = hl7list.get(i);
						line += "@#@*%$" + hl7;
					}
				}
			}
		}
		return line;
	}

	@Override
	public String onRisFeeQueryLine(String ApplyNo) {
		List<Hl7Result> list = this.onRisFeeQuery(ApplyNo);
		String code = list.get(0).getCode();
		String result = list.get(0).getResult();
		String line = code + "@#@*%$" + result;
		return line;
	}

	public List<Hl7Result> onRisFeeQuery(String applyNo) {
		// TODO Auto-generated method stub
		return onFeeQuery(applyNo,"RIS");
	}

	@Override
	public String onRisOrderOperateLine(String Hl7Data) {
		List<Hl7Result> list = this.onRisOrderOperate(Hl7Data);
		String code = list.get(0).getCode();
		String result = list.get(0).getResult();
		String line = code + "@#@*%$" + result;
		return line;
	}

	@Override
	public String getMchiszfPatData(String mrNo) {
		TParm patP = new TParm();
		patP.setData("MR_NO", PatTool.getInstance().checkMrno(mrNo));
		TParm patInfoParm;
		String pid="";
		try {
			patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
			if (patInfoParm.getCount() > 0) {
				patInfoParm = patInfoParm.getRow(0);
				pid=Hl7Communications.getInstance().createPidMchiszf(patInfoParm);
			}
	
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} 
		
		return pid;
		
	}
}
