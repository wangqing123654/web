package com.javahis.device.card;

/**
 * ��д��������ӿ�
 * @author whaosoft
 *
 */
public interface ICardRW {


	/**
	 *
	 * @return
	 */
	CardDTO readMedicalCard();


	/**
	 *
	 * @param seq  ���(2λ)
	 * @param mrNo  ������(λ�����������ݿ�)
	 * @param cType �����(2λ)
	 * @param balance ���(10,2)
	 */
	void writeMedicalCard(String seq, String mrNo, String cType, double balance);


	/**
	 *
	 * @param balance ���(10,2)
	 */
	void writeMedicalCardBalance(double balance);

}
