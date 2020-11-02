package action.pha;

import jdo.pha.PhaAntiTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;

/**
 * 
 * <p>
 * Title: סԺҽ��վ����ҩƷ������
 * </p>
 * 
 * <p>
 * Description: סԺҽ��վ����ҩƷ������
 * </p>
 * 
 * <p>
 * Copyright: JavaHis (c) 2013
 * </p>
 * 
 * @author pangben 2013-09-10
 * @version 1.0
 */
public class PHAAntiAction extends TAction {

	/**
	 * �����������������
	 * 
	 * @param parm
	 * @param newParm
	 * @param newCount
	 * @param ds
	 * @param antiFechFlg
	 * @return
	 */
	public TParm onSavePhaAnti(TParm parm) {
		String phaSeq = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace("/", "").replace("-", "");

		String sql = "SELECT MAX(SEQ_NO) AS SEQ_NO FROM PHA_ANTI WHERE CASE_NO = '"
				+ parm.getValue("CASE_NO", 0)
				+ "' "
				+ "AND PHA_SEQ = '"
				+ phaSeq + "' ";
		TParm seqResult = new TParm(TJDODBTool.getInstance().select(sql));
		if (seqResult.getErrCode() < 0) {
			return seqResult;
		}
		int seq_no = seqResult.getInt("SEQ_NO", 0) + 1;
		TParm inParm = new TParm();
		TConnection connection = getConnection();
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			inParm = parm.getRow(i);
			if (parm.getValue("DATA_FLG", i).equals("N")) {// �޸�
				inParm.setData("APPROVE_FLG", "Y");
				result = PhaAntiTool.getInstance().updatePhaAntiApproveFlg(inParm, connection);
			} else {// ����
				String antiReason = "";
				int days = TypeTool.getInt(parm.getInt("ANTI_TAKE_DAYS", i));
				antiReason = parm.getValue("ANTI_REASON_CODE", i);
				inParm.setData("ANTI_REASON", antiReason);
				inParm.setData("ANTI_MAX_DAYS", days);
				inParm.setData("CASE_NO", parm.getValue("CASE_NO", i));
				inParm.setData("PHA_SEQ", phaSeq);
				inParm.setData("SEQ_NO", Integer.valueOf(seq_no));
				inParm.setData("MR_NO", parm.getValue("MR_NO", i));
				inParm.setData("NODE_FLG", "N");
				inParm.setData("APPROVE_FLG", parm.getValue("APPROVE_FLG", i));
				inParm.setData("USE_FLG", (parm.getValue("USE_FLG", i) != null)
						&& (parm.getValue("USE_FLG", i).equals("N")) ? "N"
						: "Y");
				inParm
						.setData("ORDER_DATE", SystemTool.getInstance()
								.getDate());
				inParm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				inParm.setData("SPECIFICATION", parm.getValue("SPECIFICATION",
						i));
				inParm.setData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
				inParm.setData("DESCRIPTION", parm.getValue("DESCRIPTION", i));
				inParm.setData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				inParm.setData("MEDI_QTY", parm.getValue("MEDI_QTY", i));
				inParm.setData("FREQ_CODE", parm.getValue("FREQ_CODE", i));
				inParm.setData("OPT_USER", parm.getValue("OPT_USER", i));
				inParm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
				inParm.setData("OVERRIDE_FLG", "N");
				inParm.setData("CHECK_FLG", "N");
				inParm.setData("LINK_NO", "");//=====�����ţ�yanjing 20140619
				inParm.setData("LINKMAIN_FLG", "");
				inParm.setData("RX_KIND", "UD");
				inParm.setData("INFLUTION_RATE", 0);
				result = PhaAntiTool.getInstance().insertPhaAnti(inParm,
						connection);
				seq_no++;
			}
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}

		}

		connection.commit();
		connection.close();
		return result;
	}

	public TParm onSavePhaAntiOne(TParm parm) {
//		System.out.println("====+++=== PHAANTIACTION parm parm IS ::"+parm);
		String phaSeq = SystemTool.getInstance().getDate().toString()
				.substring(0,10).replace("/", "").replace("-", "");
		// �������
		String sql = "SELECT MAX(SEQ_NO) AS SEQ_NO FROM PHA_ANTI "
				+ "WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "' "
				+ "AND PHA_SEQ = '" + phaSeq + "'  ";
		//System.out.println("------------------sql sql sql is ::"+sql);
		
		TParm seqResult = new TParm(TJDODBTool.getInstance().select(sql));
		//System.out.println("seqResult seqResult------seqResult is ::"+seqResult);
		if (seqResult.getErrCode() < 0) {
			return seqResult;
		}
		//System.out.println("++++====+++++seq_no seq_no seq_no is ::"+seqResult.getInt("SEQ_NO", 0));
		int seq_no = seqResult.getInt("SEQ_NO", 0) + 1;// ������
		//System.out.println("++++====+++++seq_no seq_no seq_no is ::"+seq_no);
		TParm inParm = new TParm();
		TConnection connection = getConnection();
		TParm result = new TParm();
		String linkNo = "9999";
		String linkNoSql = "SELECT LINK_NO FROM PHA_ANTI "
			+ "WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "' AND LINK_NO IS NOT NULL "
			+ " ORDER BY LINK_NO DESC  ";
		
//		System.out.println("action sql is :"+linkNoSql);
		
	    TParm linkNoResult = new TParm(TJDODBTool.getInstance().select(linkNoSql));
	    if (linkNoResult.getErrCode() < 0) {
		    return linkNoResult;
	    }
	    linkNoSql = "SELECT MAX(LINK_NO) LINK_NO FROM ODI_ORDER "
				+ "WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "' AND LINK_NO IS NOT NULL ";
			
//			System.out.println("action sql is :"+linkNoSql);
			
	    TParm odiLinkNoResult = new TParm(TJDODBTool.getInstance().select(linkNoSql));
	    if (odiLinkNoResult.getErrCode() < 0) {
		    return odiLinkNoResult;
	    }
	    if(parm.getValue("LINK_NO", 0).equals(null)||
	    		"".equals(parm.getValue("LINK_NO", 0))){
	    	linkNo = "";
	    }else{
	    	if(linkNoResult.getCount()<=0){
		    	linkNo = "0";
		    	
		    }else{
//		    	 linkNo =  String.valueOf(Integer.parseInt(linkNoResult.getValue("LINK_NO",0))+1);
		    	linkNo = linkNoResult.getValue("LINK_NO",0);
		    }
	    }
	    
	    //====�����Ƿ��޸ĵı��
	    for(int m = 0;m<parm.getCount("ORDER_CODE");m++){
	    	parm.setData("MODIFY_FLG", m, "N");
	    }
	    String tempLinkNo = linkNo;
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			inParm = parm.getRow(i);
			String rLinkNo = parm.getValue("LINK_NO", i);
			if("".equals(rLinkNo)||rLinkNo.equals(null)){
				tempLinkNo = linkNo;
				linkNo = "";
			}else if(!parm.getValue("MODIFY_FLG",i).equals("Y")){
				linkNo = tempLinkNo;
			}
			//=====����������
//			if(parm.getValue("ANTI_FLG",i).equals("Y")&&parm.getValue("LINKMAIN_FLG",i).equals("Y")){
			if(!parm.getValue("LINK_NO",i).equals(null)&&!"".equals(parm.getValue("LINK_NO",i))
					&&!parm.getValue("MODIFY_FLG",i).equals("Y")){
				if (linkNo.trim().length()<=0) {
					linkNo ="0";
				}
				if(odiLinkNoResult.getCount()>0 && null!=odiLinkNoResult.getValue("LINK_NO", 0)
			    		&& !"".equals(odiLinkNoResult.getValue("LINK_NO", 0))){
			    	if(odiLinkNoResult.getInt("LINK_NO", 0)>Integer.parseInt(linkNo)){
			    		linkNo =odiLinkNoResult.getValue("LINK_NO", 0);
			    	}
			    }
				linkNo =  String.valueOf(Integer.parseInt(linkNo)+1);//����ʱ�����ż�1
				tempLinkNo = linkNo;
			    for(int j = 0;j<parm.getCount("ORDER_CODE");j++){//�޸������ͬ��ҽ�������
			    	String tLinkNo = parm.getValue("LINK_NO", j);
			    	if(rLinkNo.equals(tLinkNo)){//��������ͬ
			    		parm.setData("LINK_NO", j, linkNo);
			    		parm.setData("MODIFY_FLG", j, "Y");
			    		
			    	}
			    }
			   
			}
		//======yanjing modify end
//			if (inParm.getValue("INDEX").equals("0")) {
//				// �õ��÷��ֶ�
//				String routeCode = inParm.getValue("ROUTE_CODE");
//				// ��ѯ��ҩ���Ƿ�ΪƤ��ҩ��
//				String sql1 = "SELECT SKINTEST_FLG, ANTIBIOTIC_CODE"
//						+ " FROM PHA_BASE  WHERE ORDER_CODE = '"
//						+ inParm.getValue("ORDER_CODE") + "' ";
//				TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
//				// ��ѯ�÷���Ƥ�Ա��
//				String psFlgSql = "SELECT PS_FLG FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"
//						+ routeCode + "' ";
//				TParm psFlgResult = new TParm(TJDODBTool.getInstance().select(
//						psFlgSql));
//				if ((!(result1.getValue("ANTIBIOTIC_CODE").equals(null)) || !(""
//						.equals(result1.getValue("ANTIBIOTIC_CODE"))))
//						&& result1.getValue("SKINTEST_FLG", 0).equals("Y")) {
//					if (psFlgResult.getValue("PS_FLG", 0).equals("N")) {
//						result.setErr(-3, "Ƥ��ҩƷ�÷�����");
//						return result;
//					}
//				}
//			}

			// �����ƿ���ҩƷ���Ʊ�PHA_ANTI���������

			int days = TypeTool.getInt(parm.getInt("ANTI_TAKE_DAYS", i));
			inParm.setData("LINK_NO", linkNo);//=====�����ţ�yanjing 20140619
			inParm.setData("ANTI_MAX_DAYS", days);
			inParm.setData("ANTI_REASON", "");
			inParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			inParm.setData("PHA_SEQ", phaSeq);
			inParm.setData("SEQ_NO", seq_no);
			inParm.setData("MR_NO", parm.getValue("MR_NO"));
			inParm.setData("NODE_FLG", null != parm.getValue("NODE_FLG")
					&& parm.getValue("NODE_FLG").equals("Y") ? "Y" : "N");// 31��ע��
			inParm.setData("APPROVE_FLG", "N");// ����ע��
			inParm.setData("USE_FLG", null != parm.getValue("PHA_USE_FLG")
					&& parm.getValue("PHA_USE_FLG").equals("N") ? "N" : "Y");// ʹ��ע��
			inParm.setData("ORDER_DATE", SystemTool.getInstance().getDate());
			inParm.setData("OPT_USER", parm.getValue("OPT_USER"));
			inParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
//			inParm.setData("OVERRIDE_FLG", "Y");// Խ�����
			inParm.setData("CHECK_FLG", "N");// ��˱��
//			System.out.println("Action++++++inParm inParm inParm is ::"+inParm);
			result = PhaAntiTool.getInstance()
					.insertPhaAnti(inParm, connection);
			seq_no++;
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ԽȨʱ��pha_anti����д������ yanjing 20140218
	 */
	public TParm onSavePha(TParm parm) {
		String phaSeq = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace("/", "").replace("-", "");
		// �������
		String sql = "SELECT MAX(SEQ_NO) AS SEQ_NO FROM PHA_ANTI "
				+ "WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "' "
				+ "AND PHA_SEQ = '" + phaSeq + "' ";
		TParm seqResult = new TParm(TJDODBTool.getInstance().select(sql));
		if (seqResult.getErrCode() < 0) {
			return seqResult;
		}
		int seq_no = seqResult.getInt("SEQ_NO", 0) + 1;// ������
		TConnection connection = getConnection();
		TParm result = new TParm();
		// �����ƿ���ҩƷ���Ʊ�PHA_ANTI���������
		int days = TypeTool.getInt(parm.getInt("ANTI_TAKE_DAYS"));
		parm.setData("ANTI_MAX_DAYS", days);
		parm.setData("ANTI_REASON", "");
		parm.setData("CASE_NO", parm.getValue("CASE_NO"));
		parm.setData("PHA_SEQ", phaSeq);
		parm.setData("SEQ_NO", seq_no);
		parm.setData("MR_NO", parm.getValue("MR_NO"));
		parm.setData("NODE_FLG", null != parm.getValue("NODE_FLG")
				&& parm.getValue("NODE_FLG").equals("Y") ? "Y" : "N");// 31��ע��
		parm.setData("APPROVE_FLG", "N");// ����ע��
		parm.setData("USE_FLG", null != parm.getValue("PHA_USE_FLG")
				&& parm.getValue("PHA_USE_FLG").equals("N") ? "N" : "Y");// ʹ��ע��
		parm.setData("ORDER_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_USER", parm.getValue("OPT_USER"));
		parm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		parm.setData("OVERRIDE_FLG", "Y");
		parm.setData("CHECK_FLG", "N");
		result = PhaAntiTool.getInstance().insertPhaAnti(parm, connection);
		seq_no++;
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		// }
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * �޸�ҽ��ʹ��ע��
	 * 
	 * @return
	 */
	public TParm onUpdateStatePhaAnti(TParm parm) {
		TParm inParm = new TParm();
		TConnection connection = getConnection();
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			inParm = parm.getRow(i);
			inParm.setData("USE_FLG", "Y");
			inParm.setData("OPT_USER", parm.getValue("OPT_USER"));
			inParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			result = PhaAntiTool.getInstance().updatePhaAntiUseFlg(inParm,
					connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
}
