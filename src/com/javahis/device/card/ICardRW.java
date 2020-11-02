package com.javahis.device.card;

/**
 * 读写卡工具类接口
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
	 * @param seq  序号(2位)
	 * @param mrNo  病案号(位数决定于数据库)
	 * @param cType 卡类别(2位)
	 * @param balance 余额(10,2)
	 */
	void writeMedicalCard(String seq, String mrNo, String cType, double balance);


	/**
	 *
	 * @param balance 余额(10,2)
	 */
	void writeMedicalCardBalance(double balance);

}
