package jdo.inv;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INVNewSterilizationTool extends TJDOTool{

	public static INVNewSterilizationTool instanceObject;
	
	public INVNewSterilizationTool() {
        setModuleName("inv\\INVNewSterilizationModule.x");
        onInit();
    }
	
	/**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static INVNewSterilizationTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVNewSterilizationTool();
        return instanceObject;
    }
	
    /**
     * ����������ѯ�������Ϣ
     * */
	public TParm queryRepack(TParm parm){
		
		TParm result = this.query("queryRepack", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	

	/**
     * ���ݴ�����Ų�ѯ�������Ϣ
     * */
	public TParm queryRepackByRepackNo(TParm parm){
		
		TParm result = this.query("queryRepackByRepackNo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * ��ѯ��������ϸ��Ϣ
     * */
	public TParm queryPackageDetailInfo(TParm parm){
		
		TParm result = this.query("queryPackageDetailInfo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * ����������ѯ�������Ϣ
     * */
	public TParm querySterilization(TParm parm){
		
		TParm result = this.query("querySterilization", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	

	

	/**
     * ����������Ų�ѯ���յ���Ϣ
     * */
	public TParm querySterilizationByNo(TParm parm){
		
		TParm result = this.query("querySterilizationBySterilizationNo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	/**
     * ��ѯ��ӡ������Ϣ
     * */
	public TParm queryBarcodeInfo(TParm parm){
		
		TParm result = this.query("queryBarcodeInfo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	
	
	
	
	/**
     * �½����������Sterilization
     * */
	public TParm insertSterilization(TParm parm, TConnection connection){
	
		
		TParm sterTable = parm.getParm("STERILIZATIONTABLE");			//�����
		TParm packageMTable = parm.getParm("PACKAGEMAINTABLE");			//��������
		
		TParm result = new TParm();
		//�����������      �޸�������״̬
		TParm insertValue = new TParm();
		insertValue.setData("STERILIZATION_NO",0,sterTable.getData("STERILIZATION_NO", 0));
		insertValue.setData("SEQ_NO",0,packageMTable.getData("PACK_SEQ_NO"));
		insertValue.setData("PACK_CODE",0,packageMTable.getData("PACK_CODE"));
		insertValue.setData("QTY",0,packageMTable.getData("QTY"));
		insertValue.setData("STERILIZATION_POTSEQ",0,packageMTable.getData("STERILIZATION_POTSEQ"));
		insertValue.setData("STERILLZATION_PROGRAM",0,packageMTable.getData("STERILLZATION_PROGRAM"));
		insertValue.setData("STERILLZATION_DATE",0,packageMTable.getData("STERILLZATION_DATE"));
		insertValue.setData("STERILLZATION_USER",0,packageMTable.getData("STERILLZATION_USER"));
		insertValue.setData("AUDIT_DATE",0,packageMTable.getData("AUDIT_DATE"));
		insertValue.setData("AUDIT_USER",0,packageMTable.getData("AUDIT_USER"));
		insertValue.setData("OPT_USER",0,sterTable.getData("OPT_USER", 0));
		insertValue.setData("OPT_DATE",0,sterTable.getData("OPT_DATE", 0));
		insertValue.setData("OPT_TERM",0,sterTable.getData("OPT_TERM", 0));
		insertValue.setData("ORG_CODE",0,sterTable.getData("ORG_CODE", 0));
		insertValue.setData("BARCODE",0, packageMTable.getData("BARCODE"));
		
//		//�����޸�״̬��Ϊ�����⡱״̬��������״̬start
//		TParm changeStatus = parm.getParm("RECYCLENO");	
//		changeStatus.setData("STERILIZATION_NO",0,sterTable.getData("STERILIZATION_NO", 0));
//		changeStatus.setData("PACK_SEQ_NO",0,packageMTable.getData("PACK_SEQ_NO"));
//		changeStatus.setData("PACK_CODE",0,packageMTable.getData("PACK_CODE"));
//		changeStatus.setData("ONCE_USE_FLG",0,"Y");
//		
//		
//		TParm statusParm = INVSterilizationHelpTool.getInstance().queryPackageStatus(changeStatus.getRow(0), connection);
//		
//		String statusStr =  statusParm.getData("STATUS").toString();
//		if(statusStr.equals("[0]")){
//			INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(changeStatus.getRow(0), connection);//ɾ�����е���������ϸ
//		}else if(statusStr.equals("[2]")){
//			INVSterilizationHelpTool.getInstance().updatePackageDisStatus(changeStatus.getRow(0), connection);
//		}else if(statusStr.equals("[3]")){
//			INVSterilizationHelpTool.getInstance().updatePackageSterStatus(changeStatus.getRow(0), connection);
//		}
//		//�����޸�״̬��Ϊ�����⡱״̬��������״̬end

//		String orgCode = sterTable.getData("ORG_CODE", 0).toString();
//		//�ۿ������
//		TParm tp = new TParm();
//		tp.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE"));
//		//��ѯ���������ʹ���
//		TParm stockParm = INVSterilizationHelpTool.getInstance().queryPackDByPackCode(tp.getRow(0), connection);
//		boolean tag = true;	//���������������ʿ���Ƿ���
//		//���ÿ��������ÿ�����ʵĿ��
//		for(int j=0; j<stockParm.getCount();j++){	//ÿ������
//			TParm stock = new TParm();
//			stock = stockParm.getRow(j);
//			tag = this.checkQty(stock.getData("INV_CODE").toString(),orgCode,Double.parseDouble(stock.getData("QTY").toString()));
//			if(!tag){
//				break;
//			}
//		}//ÿ������
			
//		//�����湻�����´��
//		if(tag){
//			for(int j=0; j<stockParm.getCount();j++){	//ÿ������
//				TParm stock = new TParm();
//				stock = stockParm.getRow(j);
//					
//				//����������
//				stock.setData("STOCK_QTY", stock.getDouble("QTY") * -1);
//				stock.setData("PACK_SEQ_NO",  packageMTable.getData("PACK_SEQ_NO"));
//				stock.setData("ONCE_USE_FLG",  "Y");
//				result = InvStockMTool.getInstance().updateStockMQty(stock,connection);
//					
//				List<TParm> stockDList = new ArrayList<TParm>();
//				stockDList = this.chooseBatchSeq(stock, orgCode);	//��ѯϸ������
//
////				INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(stock, connection);//ɾ�����е���������ϸ
//				
//				//��������������ϸ��PACKSTOCKD
//				for(int n=0;n<stockDList.size();n++){
//					TParm tt = new TParm();
//					tt = stockDList.get(n);
//					tt.setData("PACK_CODE", stock.getData("PACK_CODE"));
//					tt.setData("PACK_SEQ_NO", stock.getData("PACK_SEQ_NO"));
//					tt.setData("ONCE_USE_FLG", stock.getData("ONCE_USE_FLG"));
//					tt.setData("INVSEQ_NO", 0);
//					tt.setData("OPT_USER", sterTable.getData("OPT_USER", 0));
//					tt.setData("OPT_DATE", sterTable.getData("OPT_DATE", 0).toString().substring(0, 10));
//					tt.setData("OPT_TERM", sterTable.getData("OPT_TERM", 0));
//					result=INVSterilizationHelpTool.getInstance().insertPackageDetailInfo(tt, connection);
//					if (result.getErrCode() < 0) {
//						err(result.getErrCode() + " " + result.getErrText());
//						return result;
//					}
//				}
//				//����ϸ����
//				for(int m=0;m<stockDList.size();m++){
//					TParm tt = new TParm();
//					tt = stockDList.get(m);
//					tt.setData("STOCK_QTY", tt.getDouble("STOCK_QTY") * -1);
//					result = InvStockDTool.getInstance().updateStockQty(tt,connection);  
//					if (result.getErrCode() < 0) {
//						err(result.getErrCode() + " " + result.getErrText());
//						return result;
//					}
//				}
//					
//			}
//			insertValue.setData("STATUS", 0, "0" );   
//			insertValue.setData("FINISH_FLG",0,"Y");
//			TParm t = insertValue.getRow(0);
//			
//			result = INVSterilizationHelpTool.getInstance().insertSterilization(t, connection);
//			result = INVSterilizationHelpTool.getInstance().updatePackageStatus(t, connection);
//			
//		}else{//��治��
//			
//			insertValue.setData("STATUS", 0, "3" );   
//			insertValue.setData("FINISH_FLG",0,"N");
//			TParm t = insertValue.getRow(0);
//			
//			result = INVSterilizationHelpTool.getInstance().insertSterilization(t, connection);
//			result = INVSterilizationHelpTool.getInstance().updatePackageStatus(t, connection);
//			
//		}
		insertValue.setData("STATUS", 0, "0" );   
		insertValue.setData("FINISH_FLG",0,"Y");
		TParm t = insertValue.getRow(0);
		
		result = INVSterilizationHelpTool.getInstance().insertSterilization(t, connection);
		result = INVSterilizationHelpTool.getInstance().updatePackageStatus(t, connection);
		
//		//�����õĴ������
//		TParm recParm =  parm.getParm("REPACK_NO");			
//		recParm.setData("FINISH_FLG", 0 ,"Y");
//		recParm.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE"));
//		recParm.setData("PACK_SEQ_NO", 0, packageMTable.getData("PACK_SEQ_NO"));
		
		
//		Object reNo = recParm.getRow(0).getData("REPACK_NO");
//		if(!(reNo==null)){		//������½���������������޸Ļ��յ���Ϣ
//			//�޸Ļ��յ�״̬ΪY
//			result = INVSterilizationHelpTool.getInstance().updateDisinfectionFinishFlg(recParm.getRow(0), connection);
//		}
		
//		if(!tag){
//			result.setData("ENOUGH", "NO");
//		}else{
//			result.setData("ENOUGH", "YES");
//		}
		
		return result;
	}
	
//	
//	//ѡ��������
//	private List<TParm> chooseBatchSeq(TParm parm,String orgCode){
////		System.out.println("parm555---"+parm);
//		List<TParm> list = new ArrayList<TParm>();	//�����ϸ���ʿۿ���Ϣ
////		TParm stockD = new TParm();		
//		//��Ҫ����������
//        double qty = parm.getDouble("QTY");
////        System.out.println("qty---"+qty);
//        //�õ����д����ʵ��������
//        TParm result = this.getBatchSeqInv(parm,orgCode);
// //       System.out.println("result---"+result);
//        if (result == null || result.getErrCode() < 0)
//            return list;
//        //������������θ���
//        int rowCount = result.getCount();
//        //ѭ��ȡ����������
//        for (int i = 0; i < rowCount; i++) {
//            //�ó�һ��
//            TParm oneRow = result.getRow(i);
//            double stockQty = oneRow.getDouble("STOCK_QTY");
//            //��������㹻(���Ȳ���Ϊ0)
//            if (stockQty > 0) {
//                if (stockQty >= qty) {
//                    oneRow.setData("STOCK_QTY", qty);
//                    //���ò���һ�еķ���
//                    list.add(oneRow);
//                    //���˾���
//                    return list;
//                }
//                //�������
//                if (stockQty < qty) {
//                    //������ֵ
//                    qty = qty - stockQty;
//                    //���ò���һ�еķ���
//                    list.add(oneRow);
//                }
//            }
//        }
//        return list;
//	}
//	
//	/**
//     * �õ�����Ź��������
//     * @param parm TParm
//     * @return TParm
//     */
//    private TParm getBatchSeqInv(TParm parm,String oCode) {
//    	INV inv = new INV();
//        //���Ҵ���
//        String orgCode = oCode;
//        //���ʴ���
//        String invCode = parm.getValue("INV_CODE");
//        //�õ����д����ʵ��������
//        TParm result = inv.getAllStockQty(orgCode, invCode);
//        
//        return result;
//    }
//	
//	//�������
//	private boolean checkQty(String invCode,String orgCode,double qty){
//		INV inv = new INV();
//		//�ܿ����
//        double stockQty = inv.getStockQty(orgCode, invCode);
//        if (stockQty < 0 || qty > stockQty) {
//            return false;
//        }
//        return true;
//	}
//	
	/**
     * ����������ѯ���յ���Ϣ
     * */
	public TParm queryBackDisnfection(TParm parm){
		
		TParm result = this.query("queryBackDisinfection", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
}
