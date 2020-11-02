package action.inw;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

import jdo.inw.InwForOdiTool;
import jdo.inw.InwOrderExecTool;
import jdo.ibs.IBSTool;
import jdo.udd.UddChnCheckTool;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * <p>
 * Title: סԺ��ʿվִ��Action
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class InwOrderExecAction extends TAction {
	public InwOrderExecAction() {
	}

	/**
	 * ִ�����
	 * 
	 * @param parm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) throws SQLException {
		TParm result = new TParm();
		// ����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();
		try {
			// shibl 20121024 modify ���ݾ���Ž�ҽ������
			TParm Noteparm = parm.getParm("EXECNOTE");// ��ʿ��ע����
			TParm Execparm = parm.getParm("EXECDATA");// ִ������
			//			System.out.println("-=-=----Execparm--------------" + Execparm);
			Map execpat = InwOrderExecTool.getInstance().groupByPatParm(
					Execparm);
			Map notepat = InwOrderExecTool.getInstance().groupByPatParm(
					Noteparm);
			//			System.out.println("-=-=-----notepat-------------" + notepat);
			Iterator it = execpat.values().iterator();
			while (it.hasNext()) {
				TParm patParm = (TParm) it.next();
				TParm noteParm = new TParm();
				//				System.out.println("-=-=------patParm------------" + patParm);
				if (notepat != null) {
					noteParm = notepat.get(patParm.getValue("CASE_NO", 0)) == null ? null
							: (TParm) notepat.get(patParm
									.getValue("CASE_NO", 0));
					if (noteParm != null) {
						noteParm.setCount(noteParm.getCount("CASE_NO"));
						patParm.setData("EXECNOTE", noteParm.getData());
					}
				}
				//				System.out.println("-=-=-2-----patParm------------" + patParm);
				patParm.setCount(patParm.getCount("CASE_NO"));
				// ���ó��ڴ��õ�չ������
				result = InwOrderExecTool.getInstance().onExec(patParm,
						connection);
				if (result.getErrCode() < 0) {
					System.out.println(result.getErrText());
					connection.rollback();
					// modified by WangQing at 20170209 -start
					// connection û��close
					connection.close();
					// modified by WangQing at 20170209 -end
					return result;
				}
				TParm toIBS = ((TParm) result.getData("FORIBS"));
				if (toIBS != null) {
					for (int i = 0; i < toIBS.getCount("CASE_NO"); i++) {
						if (toIBS.getValue("DC_DATE", i) == null
								|| toIBS.getValue("DC_DATE", i).length() == 0
								|| toIBS.getValue("DC_DATE", i)
								.equalsIgnoreCase("null"))
							continue;
						TParm dCQty = InwOrderExecTool.getInstance()
								.getDCOrder(toIBS.getRow(i));
						if (dCQty.getInt("DC_QYT", 0) == 0)
							continue;
						toIBS.setData("DOSAGE_QTY", i, -1
								* dCQty.getInt("DC_QYT", 0));
					}
					// ---------------����IBS�ӿ�---------------------------------------------
					TParm forIBSParm1 = new TParm();
					String ctz1Code = "";
					String ctz2Code = "";
					String ctz3Code = "";
					String SelSql = "";
					String checkcaseStr = "";
					int count1 = 0;
					// System.out.println("-=-=------------------"+patParm);
					SelSql = "SELECT *" + " FROM ADM_INP WHERE CASE_NO='"
							+ patParm.getValue("CASE_NO", 0) + "'";
					// �õ��ò������и�ִ��չ���Ĵ���
					TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
							SelSql));
					// ����IBS�ӿڷ��ط�������
					forIBSParm1.setData("M", toIBS.getData());
					forIBSParm1.setData("CTZ1_CODE", ctzParm.getData(
							"CTZ1_CODE", 0));
					forIBSParm1.setData("CTZ2_CODE", ctzParm.getData(
							"CTZ2_CODE", 0));
					forIBSParm1.setData("CTZ3_CODE", ctzParm.getData(
							"CTZ3_CODE", 0));
					forIBSParm1.setData("FLG", parm.getData("FLG"));
					// System.out.println("====patParm after===="+patParm);
					// System.out.println("===forIBSParm1===="+forIBSParm1);
					// ������̨�����ݵõ�IBS���ص�����
					TParm resultFromIBS = IBSTool.getInstance()
							.getIBSOrderData(forIBSParm1);
					// System.out.println("===resultFromIBS===="+resultFromIBS);
					if (resultFromIBS.getErrCode() < 0) {
						System.out.println(resultFromIBS.getErrText());
						connection.rollback();
						// modified by WangQing at 20170209 -start
						// connection û��close
						connection.close();
						// modified by WangQing at 20170209 -end
						return resultFromIBS;
					}
					if (resultFromIBS.getCount() <= 0) {
					}
					TParm backWriteOdiParm = new TParm();
					// �ǵ�����
					int count = resultFromIBS.getCount("ORDER_DATE");
					int rows = 0;
					for (int i = 0; i < count; i++) {
						backWriteOdiParm.addData("CASE_NO", resultFromIBS
								.getData("CASE_NO", i));
						backWriteOdiParm.addData("ORDER_NO", resultFromIBS
								.getData("ORDER_NO", i));
						backWriteOdiParm.addData("ORDER_SEQ", resultFromIBS
								.getData("ORDER_SEQ", i));
						/******************* SHIBL ADD*** 20120424 ***************************/
						backWriteOdiParm.addData("START_DTTM", resultFromIBS
								.getData("START_DTTM", i));
						backWriteOdiParm.addData("END_DTTM", resultFromIBS
								.getData("END_DTTM", i));
						/******************** SHIBL ADD *****************************/
						backWriteOdiParm.addData("OPT_DATE", patParm.getData(
								"OPT_DATE", 0));
						backWriteOdiParm.addData("OPT_USER", patParm.getData(
								"OPT_USER", 0));
						backWriteOdiParm.addData("OPT_TERM", patParm.getData(
								"OPT_TERM", 0));
						// ������Ա���ǼƷ���Ա M/D�����ֶ�
						backWriteOdiParm.addData("CASHIER_CODE", patParm
								.getData("OPT_USER", 0));
						backWriteOdiParm.addData("CASHIER_USER", patParm
								.getData("OPT_USER", 0));
						backWriteOdiParm.addData("CASHIER_DATE", patParm
								.getData("OPT_DATE", 0));
						backWriteOdiParm.addData("BILL_FLG", resultFromIBS
								.getData("BILL_FLG", i));
						backWriteOdiParm.addData("IBS_CASE_NO_SEQ",
								resultFromIBS.getData("CASE_NO_SEQ", i));
						// M/D�����ֶ�
						backWriteOdiParm.addData("IBS_SEQ_NO", resultFromIBS
								.getData("SEQ_NO", i));
						backWriteOdiParm.addData("IBS_CASE_NO", resultFromIBS
								.getData("SEQ_NO", i));
						rows++;
					}
					backWriteOdiParm.setCount(rows);
					// ��дODI��M/D����
					result = InwOrderExecTool.getInstance()
							.updateOdiDspndByIBS(backWriteOdiParm, connection);
					if (result.getErrCode() < 0) {
						System.out.println(result.getErrText());
						connection.rollback();
						// modified by WangQing at 20170209 -start
						// connection û��close
						connection.close();
						// modified by WangQing at 20170209 -end
						return result;
					}
					result = InwOrderExecTool.getInstance()
							.updateOdiDspnmByIBS(backWriteOdiParm, connection);
					if (result.getErrCode() < 0) {
						System.out.println(result.getErrText());
						connection.rollback();
						// modified by WangQing at 20170209 -start
						// connection û��close
						connection.close();
						// modified by WangQing at 20170209 -end
						return result;
					}
					// �����̨ʹ�õ�����
					TParm forIBSParm2 = new TParm();
					forIBSParm2.setData("DATA_TYPE", "3"); // ��ʿվ���ñ�ǣ�ҩ��������2��
					forIBSParm2.setData("M", resultFromIBS.getData());
					forIBSParm2.setData("FLG", parm.getData("FLG"));
					// System.out.println("===========forIBSParm2==============="+forIBSParm2);
					// ����IBS�ṩ��Tool����ִ��
					result = IBSTool.getInstance().insertIBSOrder(forIBSParm2,
							connection);
					// System.out.println("--------------------------------------------------4");
					if (result.getErrCode() < 0) {
						System.out.println(result.getErrText());
						connection.rollback();
						// modified by WangQing at 20170209 -start
						// connection û��close
						connection.close();
						// modified by WangQing at 20170209 -end
						return result;
					}
				}
				connection.commit();
				// modified by WangQing at 20170209 -start
				// connection �]��close
				//connection.close();
				// modified by WangQing at 20170209 -end
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (connection != null){
				//System.out.println("====onSave�ͷ�����====");
				connection.close();
				connection = null;
			}
			// modified by WangQing at 20170209 -start
			// ע������һ�䱣֤finally���﷨��ȷ
			// return result;
			// modified by WangQing at 20170209 -end
		}
		// modified by WangQing at 20170209 -start
		// �����ﷵ��ֵ
		return result;
		// modified by WangQing at 20170209 -end
		// -------------------------end------------------------------------------
	}

	/**
	 * ȡ��ִ�����
	 * 
	 * @param parm
	 * @return TParm
	 */
	public TParm onUndoSave(TParm parm) {
		TParm result = new TParm();
		// ����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();
		// ���ó��ڴ��õ�չ������
		result = InwOrderExecTool.getInstance().onUndoExec(parm, connection);
		if (result.getErrCode() < 0) {
			System.out.println(result.getErrText());
			connection.rollback();
			connection.close();
			return result;
		}
		TParm toIBS = ((TParm) result.getData("FORIBS"));
		if (toIBS != null) {
			// ---------------����IBS�ӿ�---------------------------------------------
			TParm forIBSParm1 = new TParm();
			String ctz1Code = "";
			String ctz2Code = "";
			String ctz3Code = "";
			String SelSql = "";
			Map pat = InwOrderExecTool.getInstance().groupByPatParm(toIBS);
			Iterator it = pat.values().iterator();
			while (it.hasNext()) {
				TParm patParm = (TParm) it.next();
				SelSql = "SELECT * FROM ADM_INP WHERE CASE_NO='"
						+ patParm.getValue("CASE_NO", 0) + "'";
				// �õ��ò������и�ִ��չ���Ĵ���
				TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
						SelSql));
				// ����IBS�ӿڷ��ط�������
				forIBSParm1.setData("M", patParm.getData());
				forIBSParm1.setData("CTZ1_CODE", ctzParm
						.getData("CTZ1_CODE", 0));
				forIBSParm1.setData("CTZ2_CODE", ctzParm
						.getData("CTZ2_CODE", 0));
				forIBSParm1.setData("CTZ3_CODE", ctzParm
						.getData("CTZ3_CODE", 0));
				forIBSParm1.setData("FLG", parm.getData("FLG"));
				// ������̨�����ݵõ�IBS���ص�����
				TParm resultFromIBS = IBSTool.getInstance().getIBSOrderData(
						forIBSParm1);
				if (resultFromIBS.getCount() <= 0) {
					System.out.println("IBS���ز����ǿ�");
					connection.rollback();
					connection.close();
					return result;
				}
				TParm backWriteOdiParm = new TParm();
				// �ǵ�����
				int count = resultFromIBS.getCount("ORDER_DATE");
				int rows = 0;
				for (int i = 0; i < count; i++) {
					backWriteOdiParm.addData("CASE_NO", resultFromIBS.getData(
							"CASE_NO", i));
					backWriteOdiParm.addData("ORDER_NO", resultFromIBS.getData(
							"ORDER_NO", i));
					backWriteOdiParm.addData("ORDER_SEQ", resultFromIBS
							.getData("ORDER_SEQ", i));
					/******************* SHIBL ADD*** 20120424 ***************************/
					backWriteOdiParm.addData("START_DTTM", resultFromIBS
							.getData("START_DTTM", i));
					backWriteOdiParm.addData("END_DTTM", resultFromIBS.getData(
							"END_DTTM", i));
					/******************** SHIBL ADD *****************************/
					backWriteOdiParm.addData("OPT_DATE", parm.getData(
							"OPT_DATE", 0));
					backWriteOdiParm.addData("OPT_USER", parm.getData(
							"OPT_USER", 0));
					backWriteOdiParm.addData("OPT_TERM", parm.getData(
							"OPT_TERM", 0));
					// ������Ա���ǼƷ���Ա M/D�����ֶ�
					backWriteOdiParm.addData("CASHIER_CODE", parm.getData(
							"OPT_USER", 0));
					backWriteOdiParm.addData("CASHIER_USER", parm.getData(
							"OPT_USER", 0));

					backWriteOdiParm.addData("CASHIER_DATE", parm.getData(
							"OPT_DATE", 0));

					backWriteOdiParm.addData("BILL_FLG", resultFromIBS.getData(
							"BILL_FLG", i));
					backWriteOdiParm.addData("IBS_CASE_NO_SEQ", resultFromIBS
							.getData("CASE_NO_SEQ", i));
					// M/D�����ֶ�
					backWriteOdiParm.addData("IBS_SEQ_NO", resultFromIBS
							.getData("SEQ_NO", i));
					backWriteOdiParm.addData("IBS_CASE_NO", resultFromIBS
							.getData("SEQ_NO", i));
					rows++;
				}
				backWriteOdiParm.setCount(rows);
				// ��дODI��M/D����
				result = InwOrderExecTool.getInstance().updateOdiDspndByIBS(
						backWriteOdiParm, connection);
				if (result.getErrCode() < 0) {
					System.out.println(result.getErrText());
					connection.rollback();
					connection.close();
					return result;
				}
				result = InwOrderExecTool.getInstance().updateOdiDspnmByIBS(
						backWriteOdiParm, connection);
				if (result.getErrCode() < 0) {
					System.out.println(result.getErrText());
					connection.rollback();
					connection.close();
					return result;
				}

				// �����̨ʹ�õ�����
				TParm forIBSParm2 = new TParm();
				forIBSParm2.setData("DATA_TYPE", "3"); // ��ʿվ���ñ�ǣ�ҩ��������2��
				forIBSParm2.setData("M", resultFromIBS.getData());
				forIBSParm2.setData("FLG", parm.getData("FLG"));
				// ����IBS�ṩ��Tool����ִ��
				result = IBSTool.getInstance().insertIBSOrder(forIBSParm2,
						connection);
				if (result.getErrCode() < 0) {
					System.out.println("IBSִ�д���");
					connection.rollback();
					connection.close();
					return result;
				}
			}
		}
		// -------------------------end------------------------------------------
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * ִ�����
	 * 
	 * @param parm
	 * @return TParm
	 */
	public TParm insertSkinNote(TParm inParm) {
		TParm result = new TParm();
		// ����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();
		String selectSql = "SELECT ORDER_DATE FROM PHA_ANTI WHERE CASE_NO= '"+inParm.getValue("CASE_NO")+"' AND ORDER_CODE='"+inParm.getValue("ORDER_CODE")+"' " +
				" AND ROUTE_CODE = 'PS' ORDER BY ORDER_DATE DESC";
		TParm selectparm = new TParm(TJDODBTool.getInstance().select(selectSql));
		if(selectparm.getCount()>0){
			String optDate = selectparm.getValue("ORDER_DATE",0).substring(0, 19).replace("-","").replace("/","").replace(" ", "").replace(":","");
			String updatePhaAnti = " UPDATE PHA_ANTI SET BATCH_NO = '"+inParm.getValue("BATCH_NO")+"',SKINTEST_NOTE= '"+inParm.getValue("SKINTEST_NOTE")+"' " +
					"WHERE CASE_NO= '"+inParm.getValue("CASE_NO")+"' AND ORDER_CODE='"+inParm.getValue("ORDER_CODE")+"' AND ROUTE_CODE = 'PS' " +
					"AND ORDER_DATE = TO_DATE('"+optDate+"','YYYYMMDDHH24MISS') ";
			TParm parm = new TParm(TJDODBTool.getInstance().update(updatePhaAnti,connection));
			if(parm.getErrCode()<0){
				connection.rollback();
				connection.close();
				return parm;
			}
			//===��med_nodify����д����
			//====Ƥ��ҩƷ��MED_NODIFY�������һ������ yanjing 20140714
			String admInpSql = "SELECT A.IPD_NO,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.BED_NO,A.IPD_NO,A.STATION_CODE,C.ORDER_CODE,C.ORDER_DEPT_CODE,C.ORDER_DR_CODE  " +
					"FROM ADM_INP A,SYS_PATINFO B ,ODI_ORDER C " +
					"WHERE A.MR_NO = B.MR_NO AND A.CASE_NO = C.CASE_NO  AND A.DS_DATE IS NULL AND " +
					"C.ORDER_NO = '"+inParm.getValue("ORDER_NO")+"' AND C.ORDER_SEQ = '"+inParm.getValue("ORDER_SEQ")+"' ";
			TParm admInpParm =  new TParm(TJDODBTool.getInstance().select(admInpSql));
			//��ѯѡ��ҩ���Ƿ�ΪƤ��ҩƷ
			String sql = "SELECT A.SKINTEST_FLG, A.ANTIBIOTIC_CODE,MAX(B.OPT_DATE)," +
					"B.BATCH_NO,B.SKINTEST_NOTE" +
					" FROM PHA_BASE A,PHA_ANTI B  WHERE A.ORDER_CODE = B.ORDER_CODE " +
					"AND A.ORDER_CODE = '"+admInpParm.getValue("ORDER_CODE", 0)+"' AND B.CASE_NO = '"+inParm.getValue("CASE_NO")+"' " +
					"GROUP BY B.BATCH_NO ,B.SKINTEST_NOTE,B.OPT_DATE,A.SKINTEST_FLG, A.ANTIBIOTIC_CODE " +
					"ORDER BY B.OPT_DATE DESC";
			//		System.out.println("ACTION ACTION Ƥ��ҩƷ sql is����"+sql);
			TParm result1= new TParm(TJDODBTool.getInstance().select(sql));
			//		System.out.println("Ƥ��ҩƷresult1 is����"+result1);
			if(result1.getCount() >0&&result1.getValue("SKINTEST_FLG",0).equals("Y")){
				//����Ҫ����Ĳ���
				TParm medNodifyParm = new TParm ();
				//��ǰ������ʱ��
				//ת��Ƥ�Խ��
				String skinResult = "";
				if("0".equals(inParm.getValue("SKINTEST_NOTE"))){//����
					skinResult = "(-)����";
				}else if("1".equals(inParm.getValue("SKINTEST_NOTE"))){//����
					skinResult = "(+)����";
				}else{
					skinResult = "";
				}
				Timestamp date = TJDODBTool.getInstance().getDBTime();
				medNodifyParm.setData("MED_NOTIFY_CODE",java.util.UUID.randomUUID().toString());//��������֤Ψһ����
				medNodifyParm.setData("SEQ",1);//����
				medNodifyParm.setData("CASE_NO",inParm.getValue("CASE_NO"));//�����
				medNodifyParm.setData("ADM_TYPE","I");//�ż�ס���
				medNodifyParm.setData("MR_NO",admInpParm.getValue("MR_NO", 0));//������
				medNodifyParm.setData("PAT_NAME",admInpParm.getValue("PAT_NAME", 0));//��������
				medNodifyParm.setData("SEX_CODE",admInpParm.getValue("SEX_CODE", 0));//�Ա𣺱���
				medNodifyParm.setData("BED_NO",admInpParm.getValue("BED_NO", 0));//���ţ�����
				medNodifyParm.setData("IPD_NO",admInpParm.getValue("IPD_NO", 0));//סԺ��
				medNodifyParm.setData("DEPT_CODE",admInpParm.getValue("ORDER_DEPT_CODE", 0));//��������
				medNodifyParm.setData("BILLING_DOCTORS",admInpParm.getValue("ORDER_DR_CODE", 0));//����ҽ��
				medNodifyParm.setData("CAT1_TYPE","PS");//Ĭ�ϣ�PS
				medNodifyParm.setData("SYSTEM_CODE","");//Ĭ��Ϊ��
				medNodifyParm.setData("APPLICATION_NO","");//Ĭ��Ϊ��
				medNodifyParm.setData("ORDER_CODE",admInpParm.getValue("ORDER_CODE", 0));//ҽ������
				medNodifyParm.setData("ORDER_NO", inParm.getValue("ORDER_NO"));//ҽ����
				medNodifyParm.setData("ORDER_SEQ", inParm.getValue("ORDER_SEQ"));//˳���
				medNodifyParm.setData("SEND_STAT","1");//״̬��ǣ�Ĭ��Ϊ1
				medNodifyParm.setData("CRTCLLWLMT","N");//Σ��ֵ��Ĭ�ϣ�N
				medNodifyParm.setData("STATION_CODE",admInpParm.getValue("STATION_CODE", 0));//����
				medNodifyParm.setData("CLINICAREA_CODE","");//����
				medNodifyParm.setData("SEND_DATE",date);//�������ڣ�Ĭ�ϵ�ǰ��������
				medNodifyParm.setData("REMARK", "Ƥ�Խ����"+skinResult +"    ���ţ�"+inParm.getValue("BATCH_NO"));//Ƥ�Խ��
				medNodifyParm.setData("OPT_USER", inParm.getValue("OPT_USER"));//������Ա
				medNodifyParm.setData("OPT_DATE", date);//��������
				medNodifyParm.setData("OPT_TERM", inParm.getValue("OPT_TERM"));//����ip
				result = InwForOdiTool.getInstance().insertIntoMedNodify(
						medNodifyParm, connection);
				if(result.getErrCode()<0){
					connection.rollback();
					connection.close();
					return result;
				}

			}
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * ����Ƥ�Դ�����med_nodify�����������
	 * @param inParm
	 * @return
	 */
	public TParm insertSkinNotePHL(TParm inParm) {
		TParm result = new TParm();
		// ����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();
		//Ƥ��ҩƷ
		//����Ҫ����Ĳ���
		TParm medNodifyParm = new TParm ();
		//��ǰ������ʱ��
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		//ת��Ƥ�Խ��
		String skinResult = "";
		if("0".equals(inParm.getValue("SKINTEST_FLG"))){//����
			skinResult = "(-)����";
		}else if("1".equals(inParm.getValue("SKINTEST_FLG"))){//����
			skinResult = "(+)����";
		}else{
			skinResult = "";
		}
		String admInpSql = "SELECT A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.CLINICAREA_CODE,C.ORDER_CODE,C.DEPT_CODE,C.DR_CODE,C.ADM_TYPE  " +
				"FROM REG_PATADM A,SYS_PATINFO B ,OPD_ORDER C " +
				"WHERE A.MR_NO = B.MR_NO AND A.CASE_NO = C.CASE_NO  AND A.REGCAN_USER IS NULL AND " +
				"C.RX_NO = '"+inParm.getValue("RX_NO")+"' AND C.SEQ_NO = '"+inParm.getValue("SEQ_NO")+"' ";
		TParm admInpParm =  new TParm(TJDODBTool.getInstance().select(admInpSql));
		medNodifyParm.setData("CASE_NO", inParm.getData("CASE_NO"));
		medNodifyParm.setData("MED_NOTIFY_CODE",java.util.UUID.randomUUID().toString());//��������֤Ψһ����
		medNodifyParm.setData("SEQ",1);//����
		medNodifyParm.setData("ADM_TYPE",admInpParm.getValue("ADM_TYPE", 0));//�ż�ס���
		medNodifyParm.setData("MR_NO",admInpParm.getValue("MR_NO", 0));//������
		medNodifyParm.setData("PAT_NAME",admInpParm.getValue("PAT_NAME", 0));//��������
		medNodifyParm.setData("SEX_CODE",admInpParm.getValue("SEX_CODE", 0));//�Ա𣺱���
		medNodifyParm.setData("BED_NO","");//���ţ�����
		medNodifyParm.setData("IPD_NO","");//סԺ��
		medNodifyParm.setData("DEPT_CODE",admInpParm.getValue("DEPT_CODE", 0));//��������
		medNodifyParm.setData("BILLING_DOCTORS",admInpParm.getValue("DR_CODE", 0));//����ҽ��
		medNodifyParm.setData("CAT1_TYPE","PS");//Ĭ�ϣ�PS
		medNodifyParm.setData("SYSTEM_CODE","");//Ĭ��Ϊ��
		medNodifyParm.setData("APPLICATION_NO","");//Ĭ��Ϊ��
		medNodifyParm.setData("ORDER_CODE",admInpParm.getValue("ORDER_CODE", 0));//ҽ������
		medNodifyParm.setData("ORDER_NO", inParm.getValue("ORDER_NO"));//ҽ����
		medNodifyParm.setData("ORDER_SEQ", inParm.getValue("SEQ_NO"));//˳���
		medNodifyParm.setData("SEND_STAT","1");//״̬��ǣ�Ĭ��Ϊ1
		medNodifyParm.setData("CRTCLLWLMT","N");//Σ��ֵ��Ĭ�ϣ�N
		medNodifyParm.setData("STATION_CODE","");//����
		medNodifyParm.setData("CLINICAREA_CODE",admInpParm.getValue("CLINICAREA_CODE", 0));//����
		medNodifyParm.setData("SEND_DATE",date);//�������ڣ�Ĭ�ϵ�ǰ��������
		medNodifyParm.setData("REMARK", "Ƥ�Խ����"+skinResult+"    ���ţ�"+inParm.getValue("BATCH_NO"));//Ƥ�Խ��
		medNodifyParm.setData("OPT_USER", inParm.getValue("OPT_USER"));//������Ա
		medNodifyParm.setData("OPT_DATE", date);//��������
		medNodifyParm.setData("OPT_TERM", inParm.getValue("OPT_TERM"));//����ip
		result = InwForOdiTool.getInstance().insertIntoMedNodify(
				medNodifyParm, connection);
		if(result.getErrCode()<0){
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 
	 * @Title: insterUddSkinNote @Description: TODO(סԺҩ����ҩ�޸�Ƥ�����ţ���¼��Ϣ) @author
	 * pangben @param inParm @return @throws
	 */
	public TParm insterUddSkinNote(TParm inParm) {
		TParm result = new TParm();
		// ����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();
		String selectSql = "SELECT ORDER_DATE FROM PHA_ANTI WHERE CASE_NO= '" + inParm.getValue("CASE_NO")
				+ "' AND ORDER_CODE='" + inParm.getValue("ORDER_CODE") + "' "
				+ " AND ROUTE_CODE = 'PS' ORDER BY ORDER_DATE DESC";
		TParm selectparm = new TParm(TJDODBTool.getInstance().select(selectSql));
		if (selectparm.getCount() > 0) {
			String optDate = selectparm.getValue("ORDER_DATE", 0).substring(0, 19).replace("-", "").replace("/", "")
					.replace(" ", "").replace(":", "");
			String updatePhaAnti = " UPDATE PHA_ANTI SET BATCH_NO = '" + inParm.getValue("BATCH_NO") + "',BATCH_USER='"
					+ inParm.getValue("OPT_USER") + "'" + " WHERE CASE_NO= '" + inParm.getValue("CASE_NO")
					+ "' AND ORDER_CODE='" + inParm.getValue("ORDER_CODE") + "' AND ROUTE_CODE = 'PS' "
					+ "AND ORDER_DATE = TO_DATE('" + optDate + "','YYYYMMDDHH24MISS') ";
			result = new TParm(TJDODBTool.getInstance().update(updatePhaAnti, connection));
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}

			// ===��med_nodify����д����
			// ====Ƥ��ҩƷ��MED_NODIFY�������һ������ yanjing 20140714
			String admInpSql = "SELECT A.IPD_NO,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.BED_NO,A.IPD_NO,A.STATION_CODE,C.ORDER_CODE,C.ORDER_DEPT_CODE,C.ORDER_DR_CODE  "
					+ "FROM ADM_INP A,SYS_PATINFO B ,ODI_ORDER C "
					+ "WHERE A.MR_NO = B.MR_NO AND A.CASE_NO = C.CASE_NO  AND A.DS_DATE IS NULL AND " + "C.ORDER_NO = '"
					+ inParm.getValue("ORDER_NO") + "' AND C.ORDER_SEQ = '" + inParm.getValue("ORDER_SEQ") + "' ";
			TParm admInpParm = new TParm(TJDODBTool.getInstance().select(admInpSql));
			// ��ѯѡ��ҩ���Ƿ�ΪƤ��ҩƷ
			String sql = "SELECT A.SKINTEST_FLG, A.ANTIBIOTIC_CODE,MAX(B.OPT_DATE)," + "B.BATCH_NO,B.SKINTEST_NOTE, B.PHA_SEQ "
					+ " FROM PHA_BASE A,PHA_ANTI B  WHERE A.ORDER_CODE = B.ORDER_CODE " + "AND A.ORDER_CODE = '"
					+ admInpParm.getValue("ORDER_CODE", 0) + "' AND B.CASE_NO = '" + inParm.getValue("CASE_NO") + "' "
					+ "GROUP BY B.BATCH_NO ,B.SKINTEST_NOTE,B.OPT_DATE,A.SKINTEST_FLG, A.ANTIBIOTIC_CODE "
					+ "ORDER BY B.OPT_DATE DESC";
			// System.out.println("ACTION ACTION Ƥ��ҩƷ sql is����"+sql);
			TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
			// System.out.println("Ƥ��ҩƷresult1 is����"+result1);
			if (result1.getCount() > 0 && result1.getValue("SKINTEST_FLG", 0).equals("Y")) {
				// ����Ҫ����Ĳ���
				TParm medNodifyParm = new TParm();
				// ��ǰ������ʱ��
				// ת��Ƥ�Խ��
				String skinResult = "";
				Timestamp date = TJDODBTool.getInstance().getDBTime();
				medNodifyParm.setData("MED_NOTIFY_CODE", java.util.UUID.randomUUID().toString());// ��������֤Ψһ����
				medNodifyParm.setData("SEQ", 1);// ����
				medNodifyParm.setData("CASE_NO", inParm.getValue("CASE_NO"));// �����
				medNodifyParm.setData("ADM_TYPE", "I");// �ż�ס���
				medNodifyParm.setData("MR_NO", admInpParm.getValue("MR_NO", 0));// ������
				medNodifyParm.setData("PAT_NAME", admInpParm.getValue("PAT_NAME", 0));// ��������
				medNodifyParm.setData("SEX_CODE", admInpParm.getValue("SEX_CODE", 0));// �Ա𣺱���
				medNodifyParm.setData("BED_NO", admInpParm.getValue("BED_NO", 0));// ���ţ�����
				medNodifyParm.setData("IPD_NO", admInpParm.getValue("IPD_NO", 0));// סԺ��
				medNodifyParm.setData("DEPT_CODE", admInpParm.getValue("ORDER_DEPT_CODE", 0));// ��������
				medNodifyParm.setData("BILLING_DOCTORS", admInpParm.getValue("ORDER_DR_CODE", 0));// ����ҽ��
				medNodifyParm.setData("CAT1_TYPE", "PS");// Ĭ�ϣ�PS
				medNodifyParm.setData("SYSTEM_CODE", "");// Ĭ��Ϊ��
				medNodifyParm.setData("APPLICATION_NO", "");// Ĭ��Ϊ��
				medNodifyParm.setData("ORDER_CODE", admInpParm.getValue("ORDER_CODE", 0));// ҽ������
				medNodifyParm.setData("ORDER_NO", inParm.getValue("ORDER_NO"));// ҽ����
				medNodifyParm.setData("ORDER_SEQ", inParm.getValue("ORDER_SEQ"));// ˳���
				medNodifyParm.setData("SEND_STAT", "1");// ״̬��ǣ�Ĭ��Ϊ1
				medNodifyParm.setData("CRTCLLWLMT", "N");// Σ��ֵ��Ĭ�ϣ�N
				medNodifyParm.setData("STATION_CODE", admInpParm.getValue("STATION_CODE", 0));// ����
				medNodifyParm.setData("CLINICAREA_CODE", "");// ����
				medNodifyParm.setData("SEND_DATE", date);// �������ڣ�Ĭ�ϵ�ǰ��������
				medNodifyParm.setData("REMARK", "Ƥ�����ţ�" + inParm.getValue("BATCH_NO"));// Ƥ�Խ��
				medNodifyParm.setData("OPT_USER", inParm.getValue("OPT_USER"));// ������Ա
				medNodifyParm.setData("OPT_DATE", date);// ��������
				medNodifyParm.setData("OPT_TERM", inParm.getValue("OPT_TERM"));// ����ip
				result = InwForOdiTool.getInstance().insertIntoMedNodify(medNodifyParm, connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				
				//add by wukai on 20170412 start == סԺҩ�������޸����� ��ͬ������PHA_ANTI
				String PHA_SEQ = result1.getValue("PHA_SEQ",0);
				result = UddChnCheckTool.getInstance().onUpdatePhaAnti(inParm, PHA_SEQ, 0, connection);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				//add by wukai on 20170412 end == סԺҩ�������޸����� ��ͬ������PHA_ANTI
				
				
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

}