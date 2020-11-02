package com.javahis.device;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: ҽ�ƿ��ӿ�</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 * ��������
 * 1 consume�������û���Ų���ʹ��int
 * @author lzk 2010.8.18
 * @version 1.0
 */
public class EktDriver {
    static {
        System.loadLibrary("EktDriver"); //����dll
    }
    /**
     * ����ҽ�ƿ���DLL
     * ���ư� ���� 133-7167-9169 ���������
     * QQ:77051970
     * @return int
     */
    public native static int init();
    /**
     * ���豸
     * ����ֵ����00�� ��ʾ�ɹ����ǡ�00����ʾʧ��
     * @return String
     */
    public native static String open();
    /**
     * ��ѯ�ͻ�����
     * "����״̬|��ƬID|��Ƭ����|�ֿ������|�������|����|�Ա�|���֤��|�绰|��ַ"
     * "00|000000001|00|00|9000.00|������|����|100234191102040217|010-84357689|�����к�����"
     * @return String
     */
    public native static String readUser();
    /**
     * ���ѿۿ�
     * @param sysID int ϵͳID��1000 :HISϵͳ��1001����������
     * @param opt String ������Ա����
     * @param value double ���ѽ��������0
     * @param sdatetime String ��������ʱ�䣨YYYY-MM-DD HH:MM:SS��
     * @return String ����ֵ��������״̬|һ��ͨ������ˮ�š� ��״̬��00�� ��ʾִ�гɹ�; �ǡ�00����ʾʧ��
     */
    public native static String consume(int sysID,String opt,double value,String sdatetime);
    /**
     * ���ѳ���
     * @param sysID int ϵͳID��1000 :HISϵͳ��1001����������
     * @param opt String ������Ա����
     * @param ID String Ҫ���������Ѽ�¼��ˮ��
     * @param sdatetime String ��������ʱ�䣨YYYY-MM-DD HH:MM:SS��
     * @return String ����״̬|һ��ͨ������ˮ�� ��״̬��00�� ��ʾִ�гɹ�; �ǡ�00����ʾʧ��
     */
    public native static String unConsume(int sysID,String opt,String ID,String sdatetime);
    /**
     * ��ѯ��һ�ʽ������
     * @param sysID int ϵͳID��1000 :HISϵͳ��1001����������
     * @return String ���ز�ѯ���ݣ���ʽ��״̬|һ��ͨ������ˮ��|��ƬID|ϵͳID|������ԱID|���Ѷ�|���Ѻ����|��������ʱ�䡱
     */
    public native static String queryLastConusme(int sysID);
    /**
     * ��ѯָ��������ˮ�ŵĽ������
     * @param id String ID :������ˮ��
     * @return String ���ز�ѯ���ݣ���ʽ��״̬|һ��ͨ������ˮ��|��ƬID|ϵͳID|������ԱID|��ˮ��|���Ѷ�|���Ѻ����|��������ʱ�䡱
     */
    public native static String queryConusmeByID(String id);
    /**
     * �ж϶�д�����Ƿ��п�
     * @return String ����ֵ����00����ʾ�п�����01����ʾ�޿���������ʾʧ��
     */
    public native static String hasCard();
    /**
     * ��������
     * @param key String Ҫ���ص����룻���硰FFFFFFFFFFFF��
     * @return String ��00�� ��ʾ�ɹ����ǡ�00����ʾʧ��
     */
    public native static String loadKey(String key);
    /**
     * �������
     * @param index int ���
     * @return String ״̬|���������ݣ��� ״̬��00�� ��ʾ�ɹ����ǡ�00����ʾʧ��
     */
    public native static String readCard(int index);
    /**
     * ����д��
     * @param index int ���
     * @param data String д������ݣ�ע��д���ΪHEX���ַ��������ַ�����ֻ�ܰ�����0��~��9��,��A��~��F��֮����ַ���
     * @return String ��00�� ��ʾ�ɹ����ǡ�00����ʾʧ��
     */
    public native static String writeCard(int index,String data);
    /**
     * �ر��豸
     * @return String
     */
    public native static String close();
    /**
     * ��д��������������
     * @param times int ��������
     * @return String
     */
    public native static String beep(int times);


    public static void main(String args[])
    {
        System.out.println("init -> " + EktDriver.init());
        System.out.println("open -> " + EktDriver.open());
        System.out.println("readUser -> " + EktDriver.readUser());
        System.out.println("consume -> " + EktDriver.consume(1000,"123",100,"2010-08-18 11:18:10"));
        System.out.println("unConsume -> " + EktDriver.unConsume(1000,"123","0123456789012345","2010-08-18 11:18:10"));
        /*System.out.println("queryLastConusme -> " + EktDriver.queryLastConusme(1000));
        System.out.println("queryConusmeByID -> " + EktDriver.queryConusmeByID("0123456789012345"));
        System.out.println("hasCard -> " + EktDriver.hasCard());
        System.out.println("loadKey -> " + EktDriver.loadKey("FFFFFFFFFFFF"));*/
        System.out.println("readCard -> " + EktDriver.readCard(4));
        System.out.println("writeCard -> " + EktDriver.writeCard(4,"0"));
        System.out.println("close -> " + EktDriver.close());
    }
}
