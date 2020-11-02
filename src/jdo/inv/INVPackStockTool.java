package jdo.inv;

import jdo.inv.InvPackStockDTool;
import jdo.inv.InvPackStockMTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p>Title: ��������洦��</p>
 *
 * <p>Description: ��������洦��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw 2009-5-6
 * @version 1.0
 */
public class INVPackStockTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static INVPackStockTool instanceObject;
    /**
     * �õ�ʵ��
     * @return INVVerifyinTool
     */
    public static INVPackStockTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVPackStockTool();
        return instanceObject;
    }

    /**
     * �������������浵�ı���
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm dealSavePackStock(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //����Ź���ĸ������������������
        TParm packStockMSaveParm = parm.getParm("PACKMSAVEPARM");
        if (packStockMSaveParm == null)
            return result.newErrParm( -1, "");
        String saveType = packStockMSaveParm.getValue("SAVETYPE");
        //˵��������Ź����
        if (saveType.equals("I")) {
            //�����������������
            result = insertValue(parm, connection);
            if (result == null || result.getErrCode() < 0)
                return result;
        }
        //����Ź����
        if (saveType.equals("U")) {
            //��������
            String packCode = packStockMSaveParm.getValue("PACK_CODE");
            //����Ƿ��Ѿ����ڴ���������
            result = InvPackStockMTool.getInstance().checkPackCount(packCode);    
            if (result == null || result.getErrCode() < 0)
                return result;
            //���û��������
            if (result.getCount() == 0) {

                result = insertValue(parm, connection);
                if (result == null || result.getErrCode() < 0)
                    return result;
            }
            //����Ѿ���������¿����
            if (result.getCount() > 0) {
            	result = insertValue(parm, connection);
//              result = updateValue(parm, connection);
                if (result == null || result.getErrCode() < 0)
                    return result;
            }
        }
        return result;
    }

    /**
     * ������������
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertValue(TParm parm, TConnection connection) {
        //����Ź���ĸ������������������
        TParm packStockMSaveParm = parm.getParm("PACKMSAVEPARM");
        //�����������������
        TParm result = InvPackStockMTool.getInstance().insertPack(
                packStockMSaveParm,
                connection);     
        if (result == null || result.getErrCode() < 0)
            return result;
        String packType = packStockMSaveParm.getValue("SAVETYPE");
        
        if(!packType.equals("U")){	//����Ź���
        	//������������ʷ��¼���������
            result = InvPackStockMDHistoryTool.getInstance().onInsertPackMHistory(packStockMSaveParm, connection);
            if (result == null || result.getErrCode() < 0)
                return result;
        }
        
        
        //�����ϸ
        TParm packStockDSaveParm = parm.getParm("PACKDSAVEPARM");

        if (packStockDSaveParm == null)
            return result.newErrParm( -1, "");
        //��ϸ�ĸ���
        int rowCount = packStockDSaveParm.getCount();
        for (int i = 0; i < rowCount; i++) {
            //������ϸ
            result = InvPackStockDTool.getInstance().insertInv(
                    packStockDSaveParm.getRow(i), connection);  
            if (result == null || result.getErrCode() < 0){
            	return result;
            }
            if(!packType.equals("U")){	//����Ź���
            	//����������ϸ��ʷ��¼���������
            	result = InvPackStockMDHistoryTool.getInstance().onInsertPackDHistory(packStockDSaveParm.getRow(i), connection);
            	if (result == null || result.getErrCode() < 0){
            		return result;
            	}
            }
        }
        return result;
    }

    /**
     * ����������
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateValue(TParm parm, TConnection connection) {
        //����Ź���ĸ������������������
        TParm packStockMSaveParm = parm.getParm("PACKMSAVEPARM");
        //�����������������
        TParm result = InvPackStockMTool.getInstance().updateQty(
                packStockMSaveParm,
                connection);   //wm2013-06-05			INVPackStockMTool.
        if (result.getErrCode() < 0)
            return result;
        //�����ϸ
        TParm packStockDSaveParm = parm.getParm("PACKDSAVEPARM");
        if (packStockDSaveParm == null)
            return result.newErrParm( -1, "");
        //��ϸ�ĸ���
        int rowCount = packStockDSaveParm.getCount();
        for (int i = 0; i < rowCount; i++) {
            //������ϸ
            result = InvPackStockDTool.getInstance().updateQty(
                    packStockDSaveParm.getRow(i), connection);   //wm2013-06-05  INVPackStockDTool.
            if (result.getErrCode() < 0)
                return result;
        }
        return result;
    }

//    /**
//     * ���������ⷽ��
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     * ���ư��������ⲻ�����ư��Ŀ��.ֻ�۳���ϸ����
//       �����ư��������⼴�۳�����������.Ҳ�۳���ϸ
//       �������Ź�����������ǿ��Ի��յ�.
//       û����Ź���ķ����ư����������޷����յ�
//     */
//    public TParm outPackSave(TParm parm, TConnection connection) {
//        System.out.println("outPackSave");
//        TParm result = new TParm();
//        //���淽ʽ
//        TParm typeParm = parm.getParm("TYPE");
//        //���ư�ע��
//        String typeFlg = typeParm.getValue("TYPE_FLG");
//        //����ע��
//        String borrowFlg = typeParm.getValue("BORROW_FLG");
//        //ȷ�ϳ���
//        String sure = typeParm.getValue("SURE");
//        //���ȷ�ϳ����򲻴����������
//        if (sure.equals("N")) {
//            //������⵵
//            String[] str = (String[]) parm.getData("SAVESQL");
//            if (str.length != 0) {
//                int rowCount = str.length;
//                //ѭ������sql
//                for (int i = 0; i < rowCount; i++) {
//                    //ִ��һ��sql
//                    result = new TParm(TJDODBTool.getInstance().update(str[i],
//                            connection));
//                    if (result.getErrCode() < 0)
//                        return result;
//                }
//            }
//        }
//
//        //��������ư�
//        System.out.println("dealOutOncePack");
//        if (typeFlg.equals("Y")) {
//            //�����ȷ�ϳ���ֱ�Ӵ������ư���ϸ
//            if (sure.equals("N")) {
//                //����ǽ���(���ò��ۿ�)
//                if (borrowFlg.equals("Y"))
//                    //zhangyong20091208 ���ư�����ֻ��������
//                    return dealOutOncePack(parm, connection);
//                //return result;
//            }
//            //�������ư���������(���ư��������ⲻ�����ư��Ŀ��.ֻ�۳���ϸ����)
//            //�����ư��������⼴�۳�����������.Ҳ�۳���ϸ
//            //�������Ź�����������ǿ��Ի��յ�.
//            //û����Ź���ķ����ư����������޷����յ�
//            return dealOutOncePack(parm, connection);
//        }
//        //ȡ������ȫ������
//        TParm packMParm = parm.getParm("STOCKPARM");
//        //����������
//        int rowCount = packMParm.getCount();
//        //���û�����������ش�����Ϣ
//        if (rowCount <= 0)
//            return result.newErrParm( -1, "����Ϊ��");
//        TParm oneRowParm;
//        //ѭ����������
//        for (int i = 0; i < rowCount; i++) {
//            //һ������
//            oneRowParm = packMParm.getRow(i);
//            //��Ź�����
//            String seqManFlg = oneRowParm.getValue("SEQMANFLG");
//            //����Ź���
//            if (seqManFlg.equals("Y")) {
//                //��������Ź�����⴦����
//                result = dealOutSeqManOutPack(oneRowParm, connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            } else { //����û����Ź���ĳ��⴦����
//                result = dealOutNoSeqManOutPack(oneRowParm, connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            }
//        }
//        return result;
//    }
//
//    /**
//     * �������ư�����
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm dealOutOncePack(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        //ȡ������ȫ������
//        TParm packMParm = parm.getParm("STOCKPARM");
//        System.out.println("packMParm---" + packMParm);
//        //����������
//        int rowCount = packMParm.getCount();
//        //���û�����������ش�����Ϣ
//        if (rowCount <= 0)
//            return result.newErrParm( -1, "����Ϊ��");
//        TParm oneRowParm;
//        //ѭ����������
//        for (int i = 0; i < rowCount; i++) {
//            //һ������
//            oneRowParm = packMParm.getRow(i);
//            System.out.println("oneRowParm---" + oneRowParm);
//            //���ư����ⶼû����Ź���
//            result = dealOutTypeOncePack(oneRowParm, connection);
//            if (result.getErrCode() < 0)
//                return result;
//        }
//
//        return result;
//    }
//
//    /**
//     * ��������Ź��������������
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm dealOutSeqManOutPack(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        //���ô���������������������ڿ�״̬����
//        result = INVPackStockMTool.getInstance().updatePackStatus(parm,
//                connection);
//        if (result.getErrCode() < 0)
//            return result;
//        //����ϸ
//        TParm packD = parm.getParm("PACKD");
//        int rowCount = packD.getCount();
//        TParm oneRowParm;
//        for (int i = 0; i < rowCount; i++) {
//            //ȡ��һ������
//            oneRowParm = packD.getRow(i);
//            //��ϸ�в���һ������
//            result = INVSupDispenseDDTool.getInstance().insertRow(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//            String svaeType = oneRowParm.getValue("SAVETYPE");
//            //һ��������Ҫɾ��,��һ�������ʲ����κ��޸�
//            if (svaeType.equals("D")) {
//                //ɾ���������е�һ��������
//                result = INVPackStockDTool.getInstance().deleteOneRowInv(
//                        oneRowParm,
//                        connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            }
//        }
//        return result;
//    }
//
//    /**
//     * ����û����Ź��������������
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm dealOutNoSeqManOutPack(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        //���ô�������������������¿�������ķ���
//        parm.setData("QTY", parm.getDouble("QTY") * -1);
//        result = INVPackStockMTool.getInstance().updateQty(parm, connection);
//        if (result.getErrCode() < 0)
//            return result;
//        //����ϸ
//        TParm packD = parm.getParm("PACKD");
//        int rowCount = packD.getCount();
//        TParm oneRowParm;
//        for (int i = 0; i < rowCount; i++) {
//            //ȡ��һ������
//            oneRowParm = packD.getRow(i);
//            //������ϸ�в���һ������
//            result = INVSupDispenseDDTool.getInstance().insertRow(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//            //���������������ʿ��
//            oneRowParm.setData("QTY", oneRowParm.getDouble("QTY") * -1);
//            result = INVPackStockDTool.getInstance().updateQty(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//        }
//
//        return result;
//    }
//
//    //zhangyong20091125 begin
//    /**
//     * �޸ģ����ư�����ʱ���ۿ��==�����ư�����ʱ�ۿ��
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm dealOutTypeOncePack(TParm parm, TConnection connection) {
//        System.out.println("---1----");
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        //zhangyong20091125 begin
//        //ȡ��������
//        result = INVPackStockMTool.getInstance().onUpdatePackStockMQty(parm,
//                connection);
//        if (result.getErrCode() < 0) {
//            return result;
//        }
//        //zhangyong20091125 end
//
//        //����ϸ
//        TParm packD = parm.getParm("PACKD");
//        System.out.println("pack---" + packD);
//        int rowCount = packD.getCount();
//        TParm oneRowParm;
//        for (int i = 0; i < rowCount; i++) {
//            //ȡ��һ������
//            oneRowParm = packD.getRow(i);
//            System.out.println("oneRowParm---"+oneRowParm);
//            //������ϸ�в���һ������
//            result = INVSupDispenseDDTool.getInstance().insertRow(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//            //���������������ʿ��
//            oneRowParm.setData("QTY", oneRowParm.getDouble("QTY") * -1);
//            result = INVPackStockDTool.getInstance().updateQty(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//        }
//        return result;
//    }
//
//    //zhangyong20091125 end
//
//    /**
//     * �������˿ⷽ��
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm saveBackPack(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        //���淽ʽ
//        TParm typeParm = parm.getParm("TYPE");
//        //���ư�ע��
//        String typeFlg = typeParm.getValue("TYPE_FLG");
//        //����ע��
//        String borrowFlg = typeParm.getValue("BORROW_FLG");
//
//        //�˿Ᵽ������˿ⵥ���˿���
//        result = updateDispenMBackFlg(parm, connection);
//        if (result.getErrCode() < 0) {
//            System.out.println("���³��ⵥ�˿��Ǵ���:" + result.getErrText());
//            return result;
//        }
//
//        //��������ư�
//        if (typeFlg.equals("Y")) {
//            //����ǽ���(��Ϊ���ó���ʱ���ۿ�..���˿ⲻ����)
//            if (borrowFlg.equals("Y"))
//                //�Ͳ��۳����.....ֻд���ⵥ
//                return result;
//        }
//        //ȡ������ȫ������
//        TParm packMParm = parm.getParm("STOCKPARM");
//        //����������
//        int rowCount = packMParm.getCount();
//        //���û�����������ش�����Ϣ
//        if (rowCount <= 0)
//            return result.newErrParm( -1, "����Ϊ��");
//        TParm oneRowParm;
//        //ѭ����������
//        for (int i = 0; i < rowCount; i++) {
//            //һ������
//            oneRowParm = packMParm.getRow(i);
//            //��Ź�����
//            String seqManFlg = oneRowParm.getValue("SEQMANFLG");
//            //����Ź���
//            if (seqManFlg.equals("Y")) {
//                //��������Ź����˿⴦����
//                result = dealHaveSeqManPackBackStock(oneRowParm, connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            } else { //����û����Ź���ĳ��⴦����
//                result = noSeqManBackStock(oneRowParm, connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            }
//        }
//        return result;
//    }
//
//    /**
//     * ��������Ź�����������˿�
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm dealHaveSeqManPackBackStock(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        //���ô���������������������ڿ�״̬����
//        result = INVPackStockMTool.getInstance().updatePackStatus(parm,
//                connection);
//        if (result.getErrCode() < 0)
//            return result;
//        //����ϸ
//        TParm packD = parm.getParm("PACKD");
//        int rowCount = packD.getCount();
//        TParm oneRowParm;
//        for (int i = 0; i < rowCount; i++) {
//            //ȡ��һ������
//            oneRowParm = packD.getRow(i);
//            String svaeType = oneRowParm.getValue("SAVETYPE");
//            //һ��������Ҫ�ظ�,��һ�������ʲ����κ��޸�
//            if (svaeType.equals("D")) {
////                System.out.println("sssssssssssssss>>>>>>>>>ssss");
//                //����ǳ���ɾ����������ظ�
//                result = INVPackStockDTool.getInstance().insertInvForBackPack(
//                        oneRowParm, connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            }
//        }
//        return result;
//    }
//
//    /**
//     * ����û����Ź�����������˿�
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm noSeqManBackStock(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        //���ô�������������������¿�������ķ���
////        parm.setData("QTY", parm.getDouble("QTY") * -1);
//        //����ʱ�ۿ�..�˿�������
//        parm.setData("QTY", parm.getDouble("QTY"));
//        result = INVPackStockMTool.getInstance().updateQty(parm, connection);
//        if (result.getErrCode() < 0)
//            return result;
//        //����ϸ
//        TParm packD = parm.getParm("PACKD");
//        int rowCount = packD.getCount();
//        TParm oneRowParm;
//        for (int i = 0; i < rowCount; i++) {
//            //ȡ��һ������
//            oneRowParm = packD.getRow(i);
//            //���������������ʿ�棨���⣩
//            oneRowParm.setData("QTY", oneRowParm.getDouble("QTY"));
//            result = INVPackStockDTool.getInstance().updateQty(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//        }
//
//        return result;
//    }
//
//    /**
//     * �������˿�ʱ��ϸ���ⵥ�˿�״̬
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm updateDispenMBackFlg(TParm parm, TConnection connection) {
//        //�˿Ᵽ������˿ⵥ���˿���
//        TParm dispenseBackFlgParm = parm.getParm("BACKFLG");
//        String dispenseNo = dispenseBackFlgParm.getValue("DISPENSE_NO");
//        //======================     chenxi =================
//        String requestNo =parm.getValue("REQUEST_NO", 0) ;
//        String sql1 =
//            "DELETE INV_SUP_DISPENSED  WHERE DISPENSE_NO='" +
//            dispenseNo + "'";
//        //ִ�еڶ���sql
//     TParm   result1 = new TParm(TJDODBTool.getInstance().update(sql1,
//            connection));
//        if (result1.getErrCode() < 0)
//            return result1;
//        String sql2 = "update INV_SUPREQUESTM set UPDATE_FLG = '0'" +
//        		"  where REQUEST_NO = '"+requestNo+"'" ;
//        TParm   result2 = new TParm(TJDODBTool.getInstance().update(sql2,
//                connection));
//            if (result2.getErrCode() < 0)
//                return result2;
//            String sql3 = "update INV_SUPREQUESTD set UPDATE_FLG = '0'" +
//            		" where REQUEST_NO = '"+requestNo+"'" ;
//            TParm   result3 = new TParm(TJDODBTool.getInstance().update(sql3,
//                    connection));
//                if (result3.getErrCode() < 0)
//                    return result3;
//           String sql4 =  "delete INV_SUP_DISPENSEDD WHERE DISPENSE_NO='" +dispenseNo + "'";
//           TParm result4 = new TParm(TJDODBTool.getInstance().update(sql4)) ;
//           if(result4.getErrCode()<0)
//        	   return result4 ;
//        //=================================   chenxi 
//        String sql =
//                "delete INV_SUP_DISPENSEM WHERE DISPENSE_NO='" +
//                dispenseNo + "'";
//        //ִ��һ��sql
//        TParm result = new TParm(TJDODBTool.getInstance().update(sql,
//                connection));
//        if (result.getErrCode() < 0)
//            System.out.println("" + result.getErrText());
//        return result;
//    }
}
