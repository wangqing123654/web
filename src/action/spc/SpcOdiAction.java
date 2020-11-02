package action.spc;

import jdo.inw.InwForOdiTool;
import jdo.spc.INDTool;
import jdo.spc.SPCIndCabdspnTool;
import jdo.udd.UddChnCheckTool;
import jdo.udd.UddRtnRgsTool;
import jdo.util.Medicine;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * ODI���нӿڷ���
 * @author YuanXiaomin
 *
 */
public class SpcOdiAction  extends TAction {

	/**
	 * סԺҩ������
	 * @param parm
	 * @return
	 */
	public TParm onSaveOdiDspnmd(TParm parm) {
		TConnection conn = getConnection();
		TParm mParm = parm.getParm("DSPN_M") ;
		TParm dParm = parm.getParm("DSPN_D") ;
		 
		TParm trimedforIndpParmCheck = parm.getParm("IND_STOCK_CHECK");
		TParm result =  new TParm();
		String message = "";
		for (int i = 0; i < trimedforIndpParmCheck.getCount("ORDER_CODE"); i++) {
            //���һ��ҩƷ�Ĳ���--����IND
            TParm parmToIND = (TParm)trimedforIndpParmCheck.getRow(i);
            
            //IND�ӿ���
            Medicine subInd = new Medicine();
            //System.out.println("parmToIND----:"+parmToIND);
            if (!subInd.checkIndStockQty(parmToIND)) {
            	String orderDesc = trimedforIndpParmCheck.getValue("ORDER_DESC", i) ;
            	if(orderDesc == null || orderDesc.equals("")){
            		orderDesc = trimedforIndpParmCheck.getValue("HIS_ORDER_CODE", i)+"HIS����û�ж�Ӧ��";
            	}
                message += (orderDesc+"�ۿⲿ��:"+ trimedforIndpParmCheck.getData("ORG_CODE", i) + "\n");
            }
        }
		
        if(message.length() != 0){
            conn.rollback();
            conn.close();
            result.setErr( -1, message + "ҩƷ��治��\n���ɿۿ⣡");
            result.setErrCode(-1);
            return result;
        }
 	
		
		TParm returnParm = new TParm() ;
		for(int i = 0 ; i < mParm.getCount("CASE_NO") ; i++ ){
			TParm mRowParm = (TParm)mParm.getRow(i);
			
			String orderCode = mRowParm.getValue("ORDER_CODE") ;
			//�ۿ�
			TParm trimedforIndParm = new TParm();
			trimedforIndParm.setData("ORG_CODE",mRowParm.getValue("EXEC_DEPT_CODE"));
			 
			trimedforIndParm.setData("ORDER_CODE",orderCode);
			trimedforIndParm.setData("DOSAGE_QTY",mRowParm.getDouble("DOSAGE_QTY"));
			trimedforIndParm.setData("OPT_DATE", mRowParm.getTimestamp("OPT_DATE"));
			trimedforIndParm.setData("OPT_USER",mRowParm.getValue("OPT_USER"));
			trimedforIndParm.setData("OPT_TERM",mRowParm.getValue("OPT_TERM"));
            
            //����ȼ�[�Էѣ�����]
			trimedforIndParm.setData("SERVICE_LEVEL",mRowParm.getValue("SERVICE_LEVEL"));
			
			String takemedOrg = mRowParm.getValue("TAKEMED_ORG");
			
			//�Ƿ�ۿ�
			boolean isBuckleStock = true; ;
			//�ж��Ƿ��ǴӲ���ȡҩ���Ƿ�ۿ�
			if(takemedOrg.equals("1")){
				TParm searchParm = new TParm();
				searchParm.setData("ORDER_CODE",orderCode);
				
				//�ж��Ƿ�����ҩ
				result =  SPCIndCabdspnTool.getInstance().onQueryIsDrug(searchParm);
				
				//System.out.println("�ж��Ƿ�����ҩ----:"+result);
				//����0����ҩ  �������龫 ,�龫�����жϲ����Ƿ�ۿ�
				if(result.getCount() >  0 ){
					isBuckleStock = false;
				}else {
			 		String isDrugOutUdd = getIsDrugOutUdd() ;
			 		//System.out.println("isDrugOutUdd----:"+isDrugOutUdd);
					if(isDrugOutUdd.equals("N")){
						isBuckleStock = false;
					}
				}
			}
			
			if(isBuckleStock) {
				//�ۿ⶯��
	       	 	returnParm = INDTool.getInstance().reduceIndStockSpc(trimedforIndParm,mRowParm.getValue("SERVICE_LEVEL"),conn);
		       	if (returnParm.getErrCode() < 0) {
			            err("ERR:" + returnParm.getErrCode() + returnParm.getErrText()
			                + returnParm.getErrName());
			            String orderDesc = mRowParm.getValue("ORDER_DESC") ;
			          	 
			            result.setErr(-1,"�ۿ�ʧ��:"+orderDesc+" �ۿⲿ�ţ�\n"+mRowParm.getValue("EXEC_DEPT_CODE"));
			            result.setErrCode(-1);
			            conn.rollback();
			            conn.close();
			            return result;
			    }
			}
			
	       	
	       	result = InwForOdiTool.getInstance().insertOdiDspnm(mRowParm,conn);
			if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	            
	            String msg = "�ظ����䣺CASE_NO="+mRowParm.getValue("CASE_NO")+":ORDER_NO="+mRowParm.getValue("ORDER_NO")+ "\n"+
	            			 ":ORDER_SEQ="+mRowParm.getValue("ORDER_SEQ")+":START_DTTM="+mRowParm.getValue("START_DTTM")+ "\n";
	            result.setErr(-1,msg);
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	       	
			//�ۿ�
			if(isBuckleStock){
		       	TParm updateDispenseParm = new TParm();
		       	updateDispenseParm.setData("CASE_NO",mRowParm.getValue("CASE_NO"));
		       	updateDispenseParm.setData("ORDER_NO",mRowParm.getValue("ORDER_NO"));
		       	updateDispenseParm.setData("ORDER_SEQ",mRowParm.getValue("ORDER_SEQ"));
		       	updateDispenseParm.setData("START_DTTM",mRowParm.getValue("START_DTTM"));
		        
		       	
		       	//���»�дBATCH_SEQ1-3
		       	int count = returnParm.getCount("ORDER_CODE");
		       	int var = 1;
		       	for(int k = 0 ; k < count ;k++){
		       		updateDispenseParm.setData("BATCH_SEQ"+var,returnParm.getValue("BATCH_SEQ",k));
		       		updateDispenseParm.setData("VERIFYIN_PRICE"+var,returnParm.getValue("PRICE",k));
		       		updateDispenseParm.setData("DISPENSE_QTY"+var,returnParm.getValue("QTY",k));
		       		var++ ;
		       	}
		       	if(var <= 3 ){
					while(var <=3 ){
						updateDispenseParm.setData("BATCH_SEQ"+var,-1);
			       		updateDispenseParm.setData("VERIFYIN_PRICE"+var,0);
			       		updateDispenseParm.setData("DISPENSE_QTY"+var,0);
						var++ ;
					}
				}		   	
		       	
		       	result =UddChnCheckTool.getInstance().updateUDDDispenseDetail(updateDispenseParm,conn);
				
				if (result.getErrCode() < 0) {
		            err("ERR:" + result.getErrCode() + result.getErrText()
			                + result.getErrName());
		            result.setErr(-1,"��дM��ۿ���Ϣʧ�ܣ�"+mRowParm.getValue("CASE_NO")+":ҩƷ���룺"+mRowParm.getValue("ORDER_CODE") + "\n");
			            conn.rollback();
			            conn.close();
			            return result;
			    }
			}
			
		} 
		//System.out.println("------------:�ύ�ɹ�------------2");
		
		for(int j = 0 ; j < dParm.getCount("CASE_NO") ; j++ ){
			TParm dRowParm = (TParm)dParm.getRow(j);
			
			//System.out.println("----------dRowParm:"+dRowParm);
			// ����ODI_DSPND
			result = InwForOdiTool.getInstance().insertOdiDspnd(dRowParm,conn);
			//System.out.println("result----:"+result);
			if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	            result.setErr(-1,"����D��ʧ��"+dRowParm.getValue("CASE_NO") +":"+dRowParm.getValue("ORDER_CODE")+ "\n");
	            conn.rollback();
	            conn.close();
	            return result;
	        }
		}

		conn.commit();
	    conn.close();
	    
		return result;
	}  
	
	/**
	 * ס Ժҩ���˿�
	 * @param parm
	 * @return
	 */
	public TParm  onUpdateRtnCfm(TParm parm){
		TParm result = new TParm() ;
		TConnection conn = getConnection();
		
		TParm mParm = parm.getParm("DSPN_M");
		TParm dParm = parm.getParm("DSPN_D");
		
		//���������������Ҫ�˵����ݣ��������ⷿ
		for(int k = 0 ; k < mParm.getCount("CASE_NO"); k++ ){
			TParm mParmRow = (TParm)mParm.getRow(k);
			
			//System.out.println("mParmRow------------:"+mParmRow);
			String sql = "SELECT * FROM ODI_DSPNM WHERE CASE_NO = '" +
			mParmRow.getValue("CASE_NO") + "' AND ORDER_NO = '" +
			mParmRow.getValue("ORDER_NO") + "' AND ORDER_SEQ = " +
			mParmRow.getInt("ORDER_SEQ") + " AND START_DTTM = '" +
			mParmRow.getValue("START_DTTM") + "'";
			TParm odi_dspnm = new TParm(TJDODBTool.getInstance().select(sql));
	        if (odi_dspnm.getCount("PHA_RETN_CODE") > 0 &&
	            odi_dspnm.getData("PHA_RETN_CODE", 0) != null &&
	            !"".equals(odi_dspnm.getValue("PHA_RETN_CODE", 0))) {
	        	System.out.println("-----------------:дDSPNM�����");
	        	//LogUtil.writerLogErr("-----------------:дDSPNM�����");
	            result.setErrCode(-1);
	            result.setErrText("ҩƷ�Ѿ���ҩ�������²�ѯ");
	            
	            return result;
	        }
	        
	        double thisDosage=mParmRow.getDouble("RTN_DOSAGE_QTY")-mParmRow.getDouble("CANCEL_DOSAGE_QTY");
            //����
            TParm parmInd = new TParm();
            parmInd.setData("ORG_CODE", mParmRow.getValue("EXEC_DEPT_CODE"));
            parmInd.setData("ORDER_CODE", mParmRow.getValue("ORDER_CODE"));
            parmInd.setData("DOSAGE_QTY", thisDosage);
            parmInd.setData("OPT_USER", mParmRow.getValue("OPT_USER"));
            parmInd.setData("OPT_DATE", mParmRow.getTimestamp("OPT_DATE"));
            parmInd.setData("OPT_TERM", mParmRow.getValue("OPT_TERM"));
            
            //System.out.println("parmInd-----:"+parmInd);
            result = INDTool.getInstance().regressIndStock(parmInd,mParmRow.getValue("SERVICE_LEVEL"), conn);
            if (result.getErrCode() < 0) {
            	System.out.println("ҩƷ��ҩ����ʧ��------:ORG_CODE");
            	//LogUtil.writerLogErr("ҩƷ��ҩ����ʧ��------:ORG_CODE="+mParmRow.getValue("EXEC_DEPT_CODE")+":ORDER_CODE="+mParmRow.getValue("ORDER_CODE")+":DOSAGE_QTY="+thisDosage);
            	result.setErr( -1, "ҩƷ��ҩ����ʧ�ܣ�");
                result.setErrCode(-1);
            	conn.rollback();
                conn.close();
                return result;
            }
            
	        String batchSeq = result.getValue("BATCH_SEQ");
	        String verifyinPrice = result.getValue("VERIFYIN_PRICE");
	        String dosageQty = result.getValue("DOSAGE_QTY");
	        mParmRow.setData("BATCH_SEQ",batchSeq);
	        mParmRow.setData("VERIFYIN_PRICE1",verifyinPrice);
	        mParmRow.setData("DISPENSE_QTY1",dosageQty);
	        mParmRow.setData("BATCH_SEQ2",-1);
	        mParmRow.setData("VERIFYIN_PRICE2",0);
	        mParmRow.setData("DISPENSE_QTY2",0);
	        mParmRow.setData("BATCH_SEQ3",-1);
	        mParmRow.setData("VERIFYIN_PRICE3",0);
	        mParmRow.setData("DISPENSE_QTY3",0);
	        System.out.println("mParmRow---:"+mParmRow);
			result = UddRtnRgsTool.getInstance().onInsertM(mParmRow,conn);
	        if (result.getErrCode() < 0) {
	        	System.out.println("-----------------:дDSPNM�����");
	        	//LogUtil.writerLogErr("-----------------:дDSPNM�����");
	        	result.setErrCode(-1);
	            result.setErrText("��ҩ����M��ʧ��");
	         	conn.rollback();
	            conn.close();
	            return result;
	         }
 
	        
		}
		
		int c = dParm.getCount("CASE_NO");
		for (int b = 0; b < c; b++) {
			 
            TParm parmRow = dParm.getRow(b);
           // System.out.println("dParmRow------------:"+parmRow);
            result = UddRtnRgsTool.getInstance().onInsertD(parmRow,
                    conn);
            
            /**result = IBSOrderdTool.getInstance().checkIBSQutryForUDD(parmRow.
                    getValue("CASE_NO"), parmRow.getValue("ORDER_CODE"),
                     parmRow.getValue("EXEC_DEPT_CODE"));*/
           
           
            if (result.getErrCode() < 0) {
            	//luhai add 2012-04-15 add rollback
            	result.setErrCode(-1);
            	System.out.println("ҩƷ��ҩ ����ODI_DSPNMD��ʧ��");
	            result.setErrText("ҩƷ��ҩ ����ODI_DSPNMD��ʧ��");
            	//LogUtil.writerLogErr("ҩƷ��ҩ ����ODI_DSPNMD��ʧ��");
            	conn.rollback();
                conn.close();
                return result;
            }
		}
		
		conn.commit();
	    conn.close();
		return result;
	}
	 
	/**
	 * ��ʿ���
	 * @param parm
	 * @return
	 */
	public TParm onSaveIndCabdspn(TParm parm ){
		TParm result = new TParm();
		int count = parm.getCount("CASE_NO");
		TConnection conn = getConnection();
		/**
		 * ��ʼѭ������IND_CABDSPN��
		 */
		for(int i = 0 ; i < count ; i++){
			TParm rowParm = parm.getRow(i);
			 
			result = SPCIndCabdspnTool.getInstance().insert(rowParm, conn);

            if (result.getErrCode() < 0) {
            	result.setErrCode(-1);
            	System.out.println("���ʧ��");
	            result.setErrText("���ʧ��");
            	conn.rollback();
                conn.close();
                return result;
            }
		}
		conn.commit();
	    conn.close();
		return result ;
	}
	
	/**
	 * ȡ�����
	 * @param parm
	 * @return
	 */
	public TParm onDeleteIndCabdspn(TParm parm){
		TParm result = new TParm();
		int count = parm.getCount("CASE_NO");
		TConnection conn = getConnection();
		/**
		 * ��ʼѭ������IND_CABDSPN��
		 */
		for(int i = 0 ; i < count ; i++){
			TParm rowParm = parm.getRow(i);
			 
			result = SPCIndCabdspnTool.getInstance().delete(rowParm, conn);

            if (result.getErrCode() < 0) {
            	 
            	result.setErrCode(-1);
            	System.out.println("ȡ�����ʧ��");
	            result.setErrText("ȡ�����ʧ��");
            	conn.rollback();
                conn.close();
                return result;
            }
		}
		conn.commit();
	    conn.close();
		return result ;
	}
	
	public TParm onCheckStockQty(TParm parm){
		TParm trimedforIndpParmCheck = parm.getParm("IND_STOCK_CHECK");
		TParm result =  new TParm();
		String message = "";
		String orgCode = "";
		for (int i = 0; i < trimedforIndpParmCheck.getCount("ORDER_CODE"); i++) {
            //���һ��ҩƷ�Ĳ���--����IND
            TParm parmToIND = (TParm)trimedforIndpParmCheck.getRow(i);
            
            //IND�ӿ���
            Medicine subInd = new Medicine();
            //System.out.println("parmToIND----:"+parmToIND);
            if (!subInd.checkIndStockQty(parmToIND)) {
            	String orderDesc = trimedforIndpParmCheck.getValue("ORDER_DESC", i) ;
            	String spec = trimedforIndpParmCheck.getValue("SPECIFICATION",i);
            	if(orderDesc == null || orderDesc.equals("")){
            		orderDesc = trimedforIndpParmCheck.getValue("HIS_ORDER_CODE", i)+"-HIS����û�ж�Ӧ��";
            	}
            	orgCode = parmToIND.getValue("ORG_CODE") ;
            	String orgDesc =  getOrgDesc(orgCode);
                message += (orderDesc+"("+spec+")"+"-�ۿⲿ�ţ�"+ orgDesc + "\n");
            }
        }
		
        if(message.length() != 0){
           
            result.setErr( -1, message + "ҩƷ��治�㣬���ɿۿ⣬�벹���棡");
            result.setErrCode(-1);
            return result;
        }
        return result ;
	}
	
	/**
	 * �龫����ʱ������ȡҩ�Ƿ�ۿ�
	 * @return
	 */
	public String  getIsDrugOutUdd(){
		String isDrugOutUdd = "Y" ;
		String sql = " SELECT A.IS_DRUG_OUT_UDD,A.TOXIC_LENGTH FROM IND_SYSPARM A  " ;
		TParm result  = new TParm(TJDODBTool.getInstance().select(sql));
		if(result != null && result.getCount() > 0 ){
			TParm rowParm = result.getRow(0) ;
			isDrugOutUdd = rowParm.getValue("IS_DRUG_OUT_UDD");
		}
		return isDrugOutUdd ;
	}
	
	private String getOrgDesc(String orgCode){
		String orgDesc = "";
		String sql = " SELECT A.ORG_CHN_DESC FROM IND_ORG A WHERE A.ORG_CODE='"+orgCode+"' " ;
		TParm result  = new TParm(TJDODBTool.getInstance().select(sql));
		if(result != null && result.getCount() > 0 ){
			TParm rowParm = result.getRow(0) ;
			orgDesc = rowParm.getValue("ORG_CHN_DESC");
		}
		if(orgDesc == null || orgDesc.equals("")){
			orgDesc = orgCode ;
		}
		return orgDesc ;
		
	}
}
