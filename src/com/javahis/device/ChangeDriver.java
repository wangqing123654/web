package com.javahis.device;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ChangeDriver {
    static {
      System.loadLibrary("ChangeDriver");
    }
    /**
     * �򿪶˿�
     * @param port int 4,57600
     * @param baud long
     * @return int
     */
    public native int OpenCom(int port, long baud);
    /**
     * �رն˿�
     * @return int
     */
    public native int CloseCom();
    /**
     * Ѱ��
     * @return int
     */
    public native int CapNBQueryCard();
    /**
     * ����
     * @return String
     * long	lCustomerID;	//3			//�˺����
     * long	lCardNO;	//2			//����
     * long	lStatus;	//1			//��״̬ F1H=���� F3H=��ʧ
     * long	lKlb;		//1			//�����
     * long	strPWD;		//2			//��������
     * long	lZe;		//3			//�ܶ�	��λ����
     * long	lYe;		//3			//�Է����	��λ����
     * long	lOpCount;	//2			//��������
     * long	lCardSN;	//1			//���˳ֿ����
     * long	lSubYe;		//3			//���˲������	��λ����
     */
    public native String CapGetNBCardInfo();
    public static void main(String args[])
    {
        ChangeDriver d = new ChangeDriver();
        //�򿪶˿�
        System.out.println(d.OpenCom(4,57600));
        //Ѱ��
        while(d.CapNBQueryCard()!=0)
            System.out.println("Ѱ��");
        System.out.println("Ѱ���ɹ�");
        System.out.println(d.CapGetNBCardInfo());
        //�رն˿�
        System.out.println(d.CloseCom());
    }
}
