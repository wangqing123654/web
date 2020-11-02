package jdo.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INVNewRepackTool extends TJDOTool{

	public static INVNewRepackTool instanceObject;
	
	public INVNewRepackTool() {
        setModuleName("inv\\INVNewRepackModule.x");
        onInit();
    }
	
	/**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static INVNewRepackTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVNewRepackTool();
        return instanceObject;
    }
	
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
	
	

	/**
     * ���ݻ��յ��Ų�ѯ���յ���Ϣ    �������������ɾ
     * */
	public TParm queryDisnfectionByNo(TParm parm){
		
		TParm result = this.query("queryDisinfectionByRecycleNo", parm);
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
     * ����������Ų�ѯ���յ���Ϣ
     * */
	public TParm queryRepackByNo(TParm parm){
		
		TParm result = this.query("queryRepackByRepackNo", parm);
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
     * �½����������   �������������ɾ
     * */
	public TParm insertRepack(TParm parm, TConnection connection){
	
		String newBarcode = "";
		
		TParm repackTable = parm.getParm("REPACK");			//�����
		System.out.println("repackTable"+repackTable);
		TParm packageMTable = parm.getParm("PACKAGEMAINTABLE");			//��������
		TParm HOMaterial = parm.getParm("HOMATERIAL");			//��ǰ�����������Ҫ�������һ������Ʒ
		
		TParm result = new TParm();
		//����������      �޸�������״̬
		TParm insertValue = new TParm();
		insertValue.setData("REPACK_NO",0,repackTable.getData("REPACK_NO", 0));
		insertValue.setData("PACK_SEQ_NO",0,packageMTable.getData("PACK_SEQ_NO"));
		insertValue.setData("PACK_CODE",0,packageMTable.getData("PACK_CODE"));
		insertValue.setData("OLDBARCODE",0,packageMTable.getData("BARCODE"));
		insertValue.setData("QTY",0,packageMTable.getData("QTY"));
		insertValue.setData("AUDIT_DATE",0,packageMTable.getData("AUDIT_DATE").toString().substring(0, 19));
		insertValue.setData("AUDIT_USER",0,packageMTable.getData("AUDIT_USER"));
		insertValue.setData("REPACK_DATE",0,packageMTable.getData("REPACK_DATE").toString().substring(0, 19));
		insertValue.setData("REPACK_USER",0,packageMTable.getData("REPACK_USER"));
		insertValue.setData("OPT_USER",0,repackTable.getData("OPT_USER", 0));
		insertValue.setData("OPT_DATE",0,repackTable.getData("OPT_DATE", 0).toString().substring(0, 19));
		insertValue.setData("OPT_TERM",0,repackTable.getData("OPT_TERM", 0));
		insertValue.setData("ORG_CODE",0,repackTable.getData("ORG_CODE", 0));
		
		//�����޸�״̬��Ϊ�����⡱״̬��������״̬start
		TParm changeStatus = parm.getParm("RECYCLENO");	
		changeStatus.setData("REPACK_NO",0,repackTable.getData("REPACK_NO", 0));
		changeStatus.setData("BARCODE",0,packageMTable.getData("BARCODE"));
		changeStatus.setData("PACK_SEQ_NO",0,packageMTable.getData("PACK_SEQ_NO"));
		changeStatus.setData("PACK_CODE",0,packageMTable.getData("PACK_CODE"));
		changeStatus.setData("ONCE_USE_FLG",0,"Y");
		
		TParm statusParm = INVRepackHelpTool.getInstance().queryPackageStatus(changeStatus.getRow(0), connection);
		
		String statusStr =  statusParm.getData("STATUS").toString();
		if(statusStr.equals("[0]")){//�ڿ�״̬
			//��ѯ������������Ϊһ������Ϊ��ֵ��
			TParm tp = INVRepackHelpTool.getInstance().queryMaterialAttr(changeStatus.getRow(0), connection);	
			
			//���¸�ֵ�Ĳĵ�WAST_FLGΪY
			if(tp!=null){
				for(int i=0; i<tp.getCount("INV_CODE");i++){
					result = INVRepackHelpTool.getInstance().updateStockDDWastFlg(tp.getRow(i), connection);
					if (result.getErrCode() < 0) {
						System.out.println("[0]���¸�ֵ�Ĳĵ�WAST_FLGΪY");
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
				}
			}
			
			result = INVRepackHelpTool.getInstance().deletePackageDetailInfo(changeStatus.getRow(0), connection);//ɾ�����е���������ϸ
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}else if(statusStr.equals("[2]")){//����״̬
			result = INVRepackHelpTool.getInstance().updatePackageDisStatus(changeStatus.getRow(0), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}else if(statusStr.equals("[3]")){//���״̬
			result = INVRepackHelpTool.getInstance().updateRepackStatus(changeStatus.getRow(0), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}else if(statusStr.equals("[4]")){//���״̬
			//��ѯ������������Ϊһ������Ϊ��ֵ��
			TParm tp = INVRepackHelpTool.getInstance().queryMaterialAttr(changeStatus.getRow(0), connection);	
			
			//���¸�ֵ�Ĳĵ�WAST_FLGΪY
			if(tp!=null){
				for(int i=0; i<tp.getCount("INV_CODE");i++){
					result = INVRepackHelpTool.getInstance().updateStockDDWastFlg(tp.getRow(i), connection);
					if (result.getErrCode() < 0) {
						System.out.println("[4]���¸�ֵ�Ĳĵ�WAST_FLGΪY");
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
				}
			}
			
			result = INVRepackHelpTool.getInstance().deletePackageDetailInfo(changeStatus.getRow(0), connection);//ɾ�����е���������ϸ
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			result = INVRepackHelpTool.getInstance().updatePackageSterStatus(changeStatus.getRow(0), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}
		//�����޸�״̬��Ϊ�����⡱״̬��������״̬end

		
		
		
		String orgCode = repackTable.getData("ORG_CODE", 0).toString();
		//�ۿ������
		TParm tp = new TParm();
		tp.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE"));
		//��ѯ���������ʹ���
		TParm stockParm = INVRepackHelpTool.getInstance().queryPackDByPackCode(tp.getRow(0), connection);
		boolean tag = true;	//���������������ʿ���Ƿ���
		//���ÿ��������ÿ�����ʵĿ��
		for(int j=0; j<stockParm.getCount();j++){	//ÿ������
			TParm stock = new TParm();
			stock = stockParm.getRow(j);
			tag = this.checkQty(stock.getData("INV_CODE").toString(),orgCode,Double.parseDouble(stock.getData("QTY").toString()));
			if(!tag){
				break;
			}
		}//ÿ������
			
		//�����湻�����´��
		if(tag){
			newBarcode = SystemTool.getInstance().getNo("ALL", "INV","INV_PACKSTOCKM", "No");
			for(int j=0; j<stockParm.getCount();j++){	//ÿ������
				TParm stock = new TParm();
				stock = stockParm.getRow(j);
					
				//����������
				stock.setData("STOCK_QTY", stock.getDouble("QTY") * -1);
				stock.setData("PACK_SEQ_NO",  packageMTable.getData("PACK_SEQ_NO"));
				stock.setData("ONCE_USE_FLG",  "Y");
				result = InvStockMTool.getInstance().updateStockMQty(stock,connection);
				if (result.getErrCode() < 0) {
					err(result.getErrCode() + " " + result.getErrText());
					return result;
				}
					
				List<TParm> stockDList = new ArrayList<TParm>();
				stockDList = this.chooseBatchSeq(stock, orgCode);	//��ѯϸ������

//				INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(stock, connection);//ɾ�����е���������ϸ
				
				//��������������ϸ��PACKSTOCKD
				for(int n=0;n<stockDList.size();n++){
					
					TParm tempTP = new TParm();
					tempTP = stockDList.get(n);
					
					TParm tt = new TParm();
					tt = stockDList.get(n);
					tt.setData("PACK_CODE", stock.getData("PACK_CODE"));
					tt.setData("PACK_SEQ_NO", stock.getData("PACK_SEQ_NO"));
					tt.setData("ONCE_USE_FLG", stock.getData("ONCE_USE_FLG"));
					tt.setData("BARCODE", newBarcode);
					tt.setData("INVSEQ_NO", 0);
					tt.setData("OPT_USER", repackTable.getData("OPT_USER", 0));
					tt.setData("OPT_DATE", repackTable.getData("OPT_DATE", 0).toString().substring(0,19));
					tt.setData("OPT_TERM", repackTable.getData("OPT_TERM", 0));
					
					tt.setData("QTY", tempTP.getData("STOCK_QTY"));
					tt.setData("USED_QTY", 0);
					tt.setData("NOTUSED_QTY", 0);
					tt.setData("RECOUNT_TIME", 0);
					tt.setData("COST_PRICE", tempTP.getData("COST_PRICE"));
					tt.setData("PACK_BATCH_NO", 0);
					
					if(tempTP.getData("DESCRIPTION") == null){
						tt.setData("DESCRIPTION", "");
					}
					
					//��packstockD���������
					result=INVRepackHelpTool.getInstance().insertPackageDetailInfo(tt, connection);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
					//���� ��history���������
					result=InvPackStockMDHistoryTool.getInstance().onInsertRepackPackDHistory(tt,connection);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
					
				}
				
				
				
				//����ϸ����
				for(int m=0;m<stockDList.size();m++){
					TParm tt = new TParm();
					tt = stockDList.get(m);
					tt.setData("STOCK_QTY", tt.getDouble("STOCK_QTY") * -1);
					result = InvStockDTool.getInstance().updateStockQty(tt,connection);  
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
				}
				
			}
			
			//��ѯpackstockD���з�һ������Ʒ�����뵽history�� start
			TParm bar = new TParm();
			bar.setData("BARCODE", packageMTable.getData("BARCODE"));
			result=INVRepackHelpTool.getInstance().queryPackageDInfoByBarcode(bar);
			System.out.println("result.getCount---"+result.getCount("BARCODE"));
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			for(int m=0;m<result.getCount("BARCODE");m++){
				result.setData("BARCODE", m, newBarcode);
				result.setData("OPT_USER", m, repackTable.getData("OPT_USER", 0));
				result.setData("OPT_DATE", m, repackTable.getData("OPT_DATE", 0).toString().substring(0,19));
				result.setData("OPT_TERM", m, repackTable.getData("OPT_TERM", 0));
				result.setData("USED_QTY", m, 0);
				result.setData("NOTUSED_QTY", m, 0);
				
				if(result.getData("DESCRIPTION", m) == null){
					result.setData("DESCRIPTION", m, "");
				}
				System.out.println("��һ������Ʒ---"+result);
				TParm pm = InvPackStockMDHistoryTool.getInstance().onInsertRepackPackDHistory(result.getRow(m),connection);
				if (pm.getErrCode() < 0) {
					err(pm.getErrCode() + " " + pm.getErrText());
					return pm;
				}
			}
			//��ѯpackstockD���з�һ������Ʒ�����뵽history�� end
			
			//��������������ϸ��PACKSTOCKD�е������һ��������
			if(HOMaterial!=null&&HOMaterial.getCount("RFID")!=-1){
				System.out.println("���뵽 ��ֵ��һ�������ʴ���");
				for(int n=0;n<HOMaterial.getCount("RFID");n++){
					//��ѯrfid������Ϣ
					TParm material = INVRepackHelpTool.getInstance().queryMaterialByRFID(HOMaterial.getRow(n));
					
					material.setData("RFID", 0, HOMaterial.getRow(n).getValue("RFID"));
					material.setData("PACK_CODE", 0, HOMaterial.getRow(n).getValue("PACK_CODE"));
					material.setData("PACK_SEQ_NO", 0, HOMaterial.getRow(n).getValue("PACK_SEQ_NO"));
					material.setData("ONCE_USE_FLG", 0, "Y");
					material.setData("BARCODE", 0, newBarcode);
					material.setData("OPT_USER", 0, repackTable.getData("OPT_USER", 0));
					material.setData("OPT_DATE", 0, repackTable.getData("OPT_DATE", 0).toString().substring(0,19));
					material.setData("OPT_TERM", 0, repackTable.getData("OPT_TERM", 0));
					material.setData("PACK_FLG", 0, "Y");
					
					
					//����stockdd������״̬
					result = InvStockDDTool.getInstance().updatePackAge(material.getRow(0),connection);   
			        if (result.getErrCode() < 0) {
			            err(result.getErrCode() + " " + result.getErrText());
			            return result;
			        }
					
					//����packstockdd
					result=INVRepackHelpTool.getInstance().insertPackageDetailInfo(material.getRow(0), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
					
					material.setData("STOCK_QTY", 0, Integer.parseInt(material.getData("STOCK_QTY",0).toString())*-1);
					
					//����stockm����
					result = InvStockMTool.getInstance().updateStockMQty(material.getRow(0),connection);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
					//����stockd����
					result = InvStockDTool.getInstance().updateStockQty(material.getRow(0),connection);  
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
					
					
				}
			}
			
			TParm qValid = new TParm();
			parm.setData("BARCODE", packageMTable.getData("BARCODE"));
			TParm dParm = INVNewBackDisnfectionTool.getInstance().queryPackageInfoByBarcode(parm);	//��ѯ��������Ϣ���������룩
			int valid = Integer.parseInt(dParm.getData("VALUE_DATE",0).toString()); 	//��Ч����   һ���ǡ��족��λ  
			Timestamp date = SystemTool.getInstance().getDate();
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
	        cal.add(cal.DATE,valid);//��������������N��
	        Date d=cal.getTime(); 
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String dateString = formatter.format(d);
	        
	        insertValue.setData("VALID_DATE", 0,  dateString);  
			insertValue.setData("BARCODE", 0,  newBarcode);  
			insertValue.setData("STATUS", 0, "3" );   
			insertValue.setData("FINISH_FLG",0,"Y");
			
			TParm t = insertValue.getRow(0);
			result = INVRepackHelpTool.getInstance().insertRepack(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			
			result = INVRepackHelpTool.getInstance().updatePackageStatus(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			
			//����ԭ�����ѯpackstockm������
			TParm bars = new TParm();
			bars.setData("BARCODE", packageMTable.getData("BARCODE"));
			result = INVRepackHelpTool.getInstance().queryPackageInfoByBarcode(bars);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			
			if(result.getData("DESCRIPTION", 0) == null){
				result.setData("DESCRIPTION", 0, "");
			}
			result.setData("OPT_DATE", 0, result.getData("OPT_DATE", 0).toString().substring(0,19));
			result.setData("BARCODE", 0, newBarcode);
			//����historyM��
			result = InvPackStockMDHistoryTool.getInstance().onInsertRepackPackMHistory(result.getRow(0), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			
			//����������
			result = INVRepackHelpTool.getInstance().updatePackStockMBarcode(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			result = INVRepackHelpTool.getInstance().updatePackStockDBarcode(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			

		}else{//��治��
			
			insertValue.setData("BARCODE", 0,  packageMTable.getData("BARCODE"));  
			insertValue.setData("STATUS", 0, "3" );   
			insertValue.setData("FINISH_FLG",0,"N");
			TParm t = insertValue.getRow(0);
			
			result = INVRepackHelpTool.getInstance().insertRepack(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			result = INVRepackHelpTool.getInstance().updatePackageStatus(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			
		}
		//�����õĻ��յ���
		TParm recParm =  parm.getParm("RECYCLENO");			
		recParm.setData("FINISH_FLG", 0 ,"Y");
		recParm.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE"));
		recParm.setData("PACK_SEQ_NO", 0, packageMTable.getData("PACK_SEQ_NO"));
		
		
		Object recNo = recParm.getRow(0).getData("RECYCLENO");
		if(!(recNo==null)){		//������½��Ĵ�����������޸Ļ��յ���Ϣ
			//�޸Ļ��յ�״̬ΪY
			result = INVRepackHelpTool.getInstance().updateDisinfectionFinishFlg(recParm.getRow(0), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}
		
		if(!tag){
			result.setData("ENOUGH", "NO");
		}else{
			result.setData("ENOUGH", "YES");
		}
		
		return result;
	}
	
	
	//ѡ��������
	private List<TParm> chooseBatchSeq(TParm parm,String orgCode){

		List<TParm> list = new ArrayList<TParm>();	//�����ϸ���ʿۿ���Ϣ
	
		//��Ҫ����������
        double qty = parm.getDouble("QTY");

        //�õ����д����ʵ��������
        TParm result = this.getBatchSeqInv(parm,orgCode);

        if (result == null || result.getErrCode() < 0)
            return list;
        //������������θ���
        int rowCount = result.getCount();
        //ѭ��ȡ����������
        for (int i = 0; i < rowCount; i++) {
            //�ó�һ��
            TParm oneRow = result.getRow(i);
            double stockQty = oneRow.getDouble("STOCK_QTY");
            //��������㹻(���Ȳ���Ϊ0)
            if (stockQty > 0) {
                if (stockQty >= qty) {
                    oneRow.setData("STOCK_QTY", qty);
                    //���ò���һ�еķ���
                    list.add(oneRow);
                    //���˾���
                    return list;
                }
                //�������
                if (stockQty < qty) {
                    //������ֵ
                    qty = qty - stockQty;
                    //���ò���һ�еķ���
                    list.add(oneRow);
                }
            }
        }
        return list;
	}
	
	/**
     * �õ�����Ź��������
     * @param parm TParm
     * @return TParm
     */
    private TParm getBatchSeqInv(TParm parm,String oCode) {
    	INV inv = new INV();
        //���Ҵ���
        String orgCode = oCode;
        //���ʴ���
        String invCode = parm.getValue("INV_CODE");
        //�õ����д����ʵ��������
        TParm result = inv.getAllStockQty(orgCode, invCode);
        
        return result;
    }
	
	//�������
	private boolean checkQty(String invCode,String orgCode,double qty){
		INV inv = new INV();
		//�ܿ����
        double stockQty = inv.getStockQty(orgCode, invCode);
        if (stockQty < 0 || qty > stockQty) {
            return false;
        }
        return true;
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
     * ��ѯ����������
     * */
	public TParm queryPackList(TParm parm){
		TParm result = this.query("queryPackList", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * ��ѯ���õ���Ҫ��Ϣ
     * */
	public TParm queryPackageInfo(TParm parm){
		TParm result = this.query("queryPackageInfo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
}
