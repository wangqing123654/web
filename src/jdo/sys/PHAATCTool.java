package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.config.TConfig;
import com.javahis.util.JavaHisDebug;
import java.sql.Timestamp;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.data.TSocket;

/**
 * <p>Title: ���ɰ�ҩ��/��ҩ��XML�ļ�</p>
 *
 * <p>Description: ���ɰ�ҩ��/��ҩ��XML�ļ�</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company:javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class PHAATCTool {

    //��ˮ�ž�̬����
    static int seqNo = 0;

    /**
     * ������
     */
    public PHAATCTool() {
    }

    /**
     * ȡ����ⵥ��
     * @return String
     */
    static public String getATCNo(){
        return SystemTool.getInstance().getNo("ALL", "PHA",
                "ATC_NO", "ATC_NO");
    }
    /**
     * �����Ͱ�ҩ��XML�ļ�(����)
     * @return boolean
     */
    static public boolean generateATCXMLO(TParm parm){
        TParm  drugListParm = parm.getParm("DRUG_LIST_PARM");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
        stringBuffer.append("<JAVAHIS-DRUG-IO>");
        //��������
        stringBuffer.append("<NAME>");
        stringBuffer.append(parm.getValue("NAME"));
        stringBuffer.append("</NAME>");
        //������
        stringBuffer.append("<MRNO>");
        stringBuffer.append(parm.getValue("MRNO"));
        stringBuffer.append("</MRNO>");
        //סԺ��
        stringBuffer.append("<IPDNO>");
        stringBuffer.append(parm.getValue("IPDNO"));
        stringBuffer.append("</IPDNO>");
        //��ҩ����
        stringBuffer.append("<DATE>");
        stringBuffer.append(parm.getValue("DATE"));
        stringBuffer.append("</DATE>");
        //����
        stringBuffer.append("<DEPT>");
        stringBuffer.append(parm.getValue("DEPT"));
        stringBuffer.append("</DEPT>");
        //��������
        stringBuffer.append("<DEPT_DESC>");
        stringBuffer.append(parm.getValue("DEPT_DESC"));
        stringBuffer.append("</DEPT_DESC>");
        //��ҩ��
        stringBuffer.append("<PRESCRIPT_NO>");
        stringBuffer.append(parm.getValue("PRESCRIPT_NO"));
        stringBuffer.append("</PRESCRIPT_NO>");
        //������
        stringBuffer.append("<RX_NO>");
        stringBuffer.append(parm.getValue("RX_NO"));
        stringBuffer.append("</RX_NO>");
        //��ҩƷ����
        stringBuffer.append("<WINDOW_NO>");
        stringBuffer.append(parm.getValue("WINDOW_NO"));
        stringBuffer.append("</WINDOW_NO>");
        //�ż���
        stringBuffer.append("<ADM_TYPE>");
        stringBuffer.append(parm.getValue("ADM_TYPE"));
        stringBuffer.append("</ADM_TYPE>");
        //����סԺע��
        stringBuffer.append("<TYPE>");
        stringBuffer.append("1");
        stringBuffer.append("</TYPE>");
        //ҩƷ�б�
        stringBuffer.append("<DRUGS>");
        for(int i = 0;i < drugListParm.getCount("SEQ");i++){
            stringBuffer.append("<DRUG>");
            //���
            stringBuffer.append("<SEQ>");
            stringBuffer.append(drugListParm.getValue("SEQ",i));
            stringBuffer.append("</SEQ>");
            //ҩƷ����
            stringBuffer.append("<ORDER_CODE>");
            stringBuffer.append(drugListParm.getValue("ORDER_CODE",i));
            stringBuffer.append("</ORDER_CODE>");
            //ҩƷ��Ʒ��
            stringBuffer.append("<ORDER_GOODS_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_GOODS_DESC",i));
            stringBuffer.append("</ORDER_GOODS_DESC>");
            //ҩƷ��ѧ��
            stringBuffer.append("<ORDER_CHEMICAL_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_CHEMICAL_DESC",i));
            stringBuffer.append("</ORDER_CHEMICAL_DESC>");
            //ҩƷӢ����
            stringBuffer.append("<ORDER_ENG_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_ENG_DESC",i));
            stringBuffer.append("</ORDER_ENG_DESC>");
            //����
            stringBuffer.append("<QTY>");
            stringBuffer.append(drugListParm.getValue("QTY",i));
            stringBuffer.append("</QTY>");
            //��ҩƵ��
            stringBuffer.append("<FREQ>");
            stringBuffer.append(drugListParm.getValue("FREQ",i));
            stringBuffer.append("</FREQ>");
            //���
            stringBuffer.append("<SPECIFICATION>");
            stringBuffer.append(drugListParm.getValue("SPECIFICATION",i));
            stringBuffer.append("</SPECIFICATION>");
            //��ҩ��λ��浥λת����
            stringBuffer.append("<TRANS_RATE>");
            stringBuffer.append(drugListParm.getValue("TRANS_RATE",i));
            stringBuffer.append("</TRANS_RATE>");
            //��ҩ����
            stringBuffer.append("<DAY>");
            stringBuffer.append(drugListParm.getValue("DAY",i));
            stringBuffer.append("</DAY>");
            //�ײ�ʱ��
            stringBuffer.append("<START_DTTM>");
            stringBuffer.append(drugListParm.getValue("START_DTTM",i));
            stringBuffer.append("</START_DTTM>");
            //�Ͱ�ע��
            stringBuffer.append("<FLG>");
            stringBuffer.append(drugListParm.getValue("FLG",i));
            stringBuffer.append("</FLG>");
            stringBuffer.append("</DRUG>");
        }
        stringBuffer.append("</DRUGS>");
        stringBuffer.append("</JAVAHIS-DRUG-IO>");
        String path = TConfig.getSystemValue("ATCPATH.O");
        String fileServerIP = TConfig.getSystemValue("FILE_SERVER_IP");
        String port = TConfig.getSystemValue("PORT");
        Timestamp timestamp = SystemTool.getInstance().getDate();
        String timestampStr = timestamp.toString().replaceAll("-","");
        timestampStr = timestampStr.replaceAll(" ","");
        timestampStr = timestampStr.replaceAll(":","");
        timestampStr = timestampStr.substring(0,14);
        TSocket socket = new TSocket(fileServerIP,Integer.parseInt(port));
        return TIOM_FileServer.writeFile(socket,path + getATCNo() + ".xml",stringBuffer.toString().getBytes());
    }

    /**
     * �����Ͱ�ҩ��XML�ļ�(סԺ)
     * @return boolean
     */
    static public boolean generateATCXMLI(TParm parm){
        TParm  drugListParm = parm.getParm("DRUG_LIST_PARM");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\"  encoding=\"GB2312\"?>");
        stringBuffer.append("<JAVAHIS-DRUG-IO>");
        //��������
        stringBuffer.append("<NAME>");
        stringBuffer.append(parm.getValue("NAME"));
        stringBuffer.append("</NAME>");
        //������
        stringBuffer.append("<MRNO>");
        stringBuffer.append(parm.getValue("MRNO"));
        stringBuffer.append("</MRNO>");
        //סԺ��
        stringBuffer.append("<IPDNO>");
        stringBuffer.append(parm.getValue("IPDNO"));
        stringBuffer.append("</IPDNO>");
        //������
        stringBuffer.append("<BED_NO>");
        stringBuffer.append(parm.getValue("BED_NO"));
        stringBuffer.append("</BED_NO>");
        //����
        stringBuffer.append("<DEPT>");
        stringBuffer.append(parm.getValue("DEPT"));
        stringBuffer.append("</DEPT>");
        //��������
        stringBuffer.append("<DEPT_DESC>");
        stringBuffer.append(parm.getValue("DEPT_DESC"));
        stringBuffer.append("</DEPT_DESC>");
        //����
        stringBuffer.append("<STATION_CODE>");
        stringBuffer.append(parm.getValue("STATION_CODE"));
        stringBuffer.append("</STATION_CODE>");
        //��������
        stringBuffer.append("<STATION_DESC>");
        stringBuffer.append(parm.getValue("STATION_DESC"));
        stringBuffer.append("</STATION_DESC>");
        //��ҩ����
        stringBuffer.append("<DATE>");
        stringBuffer.append(parm.getValue("DATE"));
        stringBuffer.append("</DATE>");
        //����סԺע��
        stringBuffer.append("<TYPE>");
        stringBuffer.append("2");
        stringBuffer.append("</TYPE>");
        //ҩƷ�б�
        stringBuffer.append("<DRUGS>");
        for(int i = 0;i < drugListParm.getCount("SEQ");i++){
            stringBuffer.append("<DRUG>");
            //���
            stringBuffer.append("<SEQ>");
            stringBuffer.append(drugListParm.getValue("SEQ",i));
            stringBuffer.append("</SEQ>");
            //ҩƷ����
            stringBuffer.append("<ORDER_CODE>");
            stringBuffer.append(drugListParm.getValue("ORDER_CODE",i));
            stringBuffer.append("</ORDER_CODE>");
            //ҩƷ��Ʒ��
            stringBuffer.append("<ORDER_GOODS_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_GOODS_DESC",i));
            stringBuffer.append("</ORDER_GOODS_DESC>");
            //ҩƷ��ѧ��
            stringBuffer.append("<ORDER_CHEMICAL_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_CHEMICAL_DESC",i));
            stringBuffer.append("</ORDER_CHEMICAL_DESC>");
            //ҩƷӢ����
            stringBuffer.append("<ORDER_ENG_DESC>");
            stringBuffer.append(drugListParm.getValue("ORDER_ENG_DESC",i));
            stringBuffer.append("</ORDER_ENG_DESC>");
            //����
            stringBuffer.append("<QTY>");
            stringBuffer.append(drugListParm.getValue("QTY",i));
            stringBuffer.append("</QTY>");
            //��ҩƵ��
            stringBuffer.append("<FREQ>");
            stringBuffer.append(drugListParm.getValue("FREQ",i));
            stringBuffer.append("</FREQ>");
            //Ͷҩ����ʱ��
            stringBuffer.append("<DAY>");
            stringBuffer.append(drugListParm.getValue("DAY",i));
            stringBuffer.append("</DAY>");
            //��ҩʱ��
            stringBuffer.append("<DRUG_DATETIME>");
            stringBuffer.append(drugListParm.getValue("DRUG_DATETIME",i));
            stringBuffer.append("</DRUG_DATETIME>");
            //��ҩ;��(�ڷ�/����)
            stringBuffer.append("<ROUTE>");
            stringBuffer.append(drugListParm.getValue("ROUTE",i));
            stringBuffer.append("</ROUTE>");
            //��ǰ����(0:��;1:��ǰ;2:����)
            stringBuffer.append("<MEAL_FLG>");
            stringBuffer.append(drugListParm.getValue("MEAL_FLG",i));
            stringBuffer.append("</MEAL_FLG>");
            //�ײ�ʱ��
            stringBuffer.append("<START_DTTM>");
            stringBuffer.append(drugListParm.getValue("START_DTTM",i));
            stringBuffer.append("</START_DTTM>");
            //�Ͱ�ע��
            stringBuffer.append("<FLG>");
            stringBuffer.append(drugListParm.getValue("FLG",i));
            stringBuffer.append("</FLG>");
            //����ҽ������ʱҽ��������
            stringBuffer.append("<OrderType>");
            stringBuffer.append(drugListParm.getValue("OrderType",i));
            stringBuffer.append("</OrderType>");
            stringBuffer.append("</DRUG>");
        }
        stringBuffer.append("</DRUGS>");
        stringBuffer.append("</JAVAHIS-DRUG-IO>");
        String path = TConfig.getSystemValue("ATCPATH.I");
        String fileServerIP = TConfig.getSystemValue("FILE_SERVER_IP");
        String port = TConfig.getSystemValue("PORT");
        Timestamp timestamp = SystemTool.getInstance().getDate();
        String timestampStr = timestamp.toString().replaceAll("-","");
        timestampStr = timestampStr.replaceAll(" ","");
        timestampStr = timestampStr.replaceAll(":","");
        timestampStr = timestampStr.substring(0,14);
        TSocket socket = new TSocket(fileServerIP,Integer.parseInt(port));
        return TIOM_FileServer.writeFile(socket,path + parm.getValue("STATION_CODE")+"_" + getATCNo() + ".xml",stringBuffer.toString().getBytes());
    }

    public static void main(String[] args) {
        JavaHisDebug.initClient();

        TParm parm = new TParm();
        parm.setData("NAME","����");
        parm.setData("MRNO","000000000744");
        parm.setData("IPDNO","000000000130");
        parm.setData("DATE",("" + SystemTool.getInstance().getDate()).substring(0,19));
        parm.setData("DEPT","10101");
        parm.setData("DEPT_DESC","�����ڿ�");
        parm.setData("PRESCRIPT_NO","0");
        parm.setData("RX_NO","100316000027");
        parm.setData("WINDOW_NO","30");
        TParm drugListParm = new TParm();
            drugListParm.addData("SEQ",0);
            drugListParm.addData("ORDER_CODE","1CCA0001");
            drugListParm.addData("ORDER_GOODS_DESC","��˾ƥ�ֳ���Ƭ");
            drugListParm.addData("ORDER_CHEMICAL_DESC","��˾ƥ�ֳ���Ƭ��ѧ��");
            drugListParm.addData("ORDER_ENG_DESC","Aspirin");
            drugListParm.addData("QTY",1);
            drugListParm.addData("FREQ","BID");
            drugListParm.addData("SPECIFICATION","��˾ƥ�ֳ���Ƭ���");
            drugListParm.addData("TRANS_RATE","12");
            drugListParm.addData("DAY",1);
            drugListParm.addData("START_DTTM","");
            drugListParm.addData("FLG","");

            drugListParm.addData("SEQ",1);
            drugListParm.addData("ORDER_CODE","1CHB0001");
            drugListParm.addData("ORDER_GOODS_DESC","����ऴ�Ƭ");
            drugListParm.addData("ORDER_CHEMICAL_DESC","����ऴ�Ƭ��ѧ��");
            drugListParm.addData("ORDER_ENG_DESC","����ऴ�ƬӢ����");
            drugListParm.addData("QTY",1);
            drugListParm.addData("FREQ","BID");
            drugListParm.addData("SPECIFICATION","����ऴ�Ƭ���");
            drugListParm.addData("TRANS_RATE","12");
            drugListParm.addData("DAY",1);
            drugListParm.addData("START_DTTM","");
            drugListParm.addData("FLG","");

            drugListParm.addData("SEQ",2);
            drugListParm.addData("ORDER_CODE","2AJ00001");
            drugListParm.addData("ORDER_GOODS_DESC","Ѫ������");
            drugListParm.addData("ORDER_CHEMICAL_DESC","Ѫ�����һ�ѧ��");
            drugListParm.addData("ORDER_ENG_DESC","Ѫ������Ӣ����");
            drugListParm.addData("QTY",1);
            drugListParm.addData("FREQ","BID");
            drugListParm.addData("SPECIFICATION","Ѫ�����ҹ��");
            drugListParm.addData("TRANS_RATE","12");
            drugListParm.addData("DAY",1);
            drugListParm.addData("START_DTTM","");
            drugListParm.addData("FLG","");
        parm.setData("DRUG_LIST_PARM",drugListParm.getData());
        generateATCXMLO(parm);

//        TParm parm = new TParm();
//        parm.setData("NAME","����");
//        parm.setData("MRNO","000000000744");
//        parm.setData("IPDNO","000000000130");
//        parm.setData("BED_NO","0010102");
//        parm.setData("DEPT","10101");
//        parm.setData("DEPT_DESC","�����ڿ�");
//        parm.setData("STATION_CODE","001");
//        parm.setData("STATION_DESC","������");
//        parm.setData("DATE","20100318092828");
//        TParm drugListParm = new TParm();
//            drugListParm.addData("SEQ",1);
//            drugListParm.addData("ORDER_CODE","1CCA0001");
//            drugListParm.addData("ORDER_GOODS_DESC","��˾ƥ�ֳ���Ƭ");
//            drugListParm.addData("ORDER_CHEMICAL_DESC","��˾ƥ�ֳ���Ƭ��ѧ��");
//            drugListParm.addData("ORDER_ENG_DESC","Aspirin");
//            drugListParm.addData("QTY",1);
//            drugListParm.addData("FREQ","BID");
//            drugListParm.addData("DAY",1);
//            drugListParm.addData("DRUG_DATETIME","20100319092828");
//            drugListParm.addData("ROUTE","�ڷ�");
//            drugListParm.addData("MEAL_FLG","0");
//            drugListParm.addData("START_DTTM","20100319092828");
//            drugListParm.addData("FLG","");
//
//            drugListParm.addData("SEQ",2);
//            drugListParm.addData("ORDER_CODE","1CHB0001");
//            drugListParm.addData("ORDER_GOODS_DESC","����ऴ�Ƭ");
//            drugListParm.addData("ORDER_CHEMICAL_DESC","����ऴ�Ƭ��ѧ��");
//            drugListParm.addData("ORDER_ENG_DESC","����ऴ�ƬӢ����");
//            drugListParm.addData("QTY",1);
//            drugListParm.addData("FREQ","BID");
//            drugListParm.addData("DAY",1);
//            drugListParm.addData("DRUG_DATETIME","20100319092828");
//            drugListParm.addData("ROUTE","�ڷ�");
//            drugListParm.addData("MEAL_FLG","0");
//            drugListParm.addData("START_DTTM","20100319092828");
//            drugListParm.addData("FLG","");
//
//            drugListParm.addData("SEQ",3);
//            drugListParm.addData("ORDER_CODE","2AJ00001");
//            drugListParm.addData("ORDER_GOODS_DESC","Ѫ������");
//            drugListParm.addData("ORDER_CHEMICAL_DESC","Ѫ�����һ�ѧ��");
//            drugListParm.addData("ORDER_ENG_DESC","Ѫ������Ӣ����");
//            drugListParm.addData("QTY",1);
//            drugListParm.addData("FREQ","BID");
//            drugListParm.addData("DAY",1);
//            drugListParm.addData("DRUG_DATETIME","20100319092828");
//            drugListParm.addData("ROUTE","�ڷ�");
//            drugListParm.addData("MEAL_FLG","0");
//            drugListParm.addData("START_DTTM","20100319092828");
//            drugListParm.addData("FLG","");
//        parm.setData("DRUG_LIST_PARM",drugListParm.getData());
//        generateATCXMLI(parm);

    }

}
