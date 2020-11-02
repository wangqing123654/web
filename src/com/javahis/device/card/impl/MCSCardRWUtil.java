package com.javahis.device.card.impl;


import jdo.sys.PatTool;
import com.javahis.device.card.CardDTO;
import com.javahis.device.card.ICardRW;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;



/**
 *
 * MCS RF-2008U 智能卡读写器
 * @author whaosoft
 *
 */
public class MCSCardRWUtil implements ICardRW{

	/** 4扇区 - MrNo */
    private final int PART_1 = 4;
    /** 5扇区 - seq,ctype */
    private final int PART_2 = 5;
    /** 6扇区 - balance */
    private final int PART_3 = 6;

	/**
	 *
	 */
	static {
		IMCSCardRW.mcs.MCS_InitComm(10, 115200);
		IMCSCardRW.mcs.MCS_Config(198, 14);
	}

	@Override
	public CardDTO readMedicalCard() {

        IMCSCardRW.mcs.MCS_Buzzer(1);
		IMCSCardRW.mcs.MCS_LED(1);
        //

		String rwType = "R";
		byte[] data = new byte[16];

		//
		this.doProcessResult( IMCSCardRW.mcs.MCS_ReadCard(PART_1,data) , rwType);
		String result1 = bytes2HexString(data,16);

		//
		data = new byte[16];
		this.doProcessResult(  IMCSCardRW.mcs.MCS_ReadCard(PART_2,data) , rwType);
		String result2 = bytes2HexString(data,16);

		//
		data = new byte[16];
		this.doProcessResult( IMCSCardRW.mcs.MCS_ReadCard(PART_3,data) , rwType);
		String result3 = bytes2HexString(data,16);

		return this.doReadHandle(result1, result2, result3);
	}

	@Override
	public void writeMedicalCard(String seq, String mrNo, String type,
			double balance) {

        IMCSCardRW.mcs.MCS_Buzzer(1);
		IMCSCardRW.mcs.MCS_LED(1);
        //

		String rwType = "W";
		byte[] data = null;

		//
		String f_mrno = this.doProcessFormatStr(mrNo);
		//System.out.println( "======= f_mrno ======" + f_mrno );
		String f_balance = this.doProcessFormatBalance(balance);

		//
		data = 	hexString2Bytes( f_mrno, f_mrno.length() );
		this.doProcessResult( IMCSCardRW.mcs.MCS_WriteCard( PART_1,data ) , rwType);

		//
		if( null==seq || seq.length()!=3 ){
            throw new RuntimeException(" 序号,不可以为空且长度为3! ");
		}
		if( null==type || type.length()!=2 ){
			throw new RuntimeException(" 卡类别,不可以为空且长度为2! ");
		}
		String f_sc = this.doProcessFormatStr(seq+type);
		data = 	hexString2Bytes( f_sc, f_sc.length() );
		this.doProcessResult( IMCSCardRW.mcs.MCS_WriteCard( PART_2,data ) , rwType);

        //
		data = 	hexString2Bytes( f_balance, f_balance.length() );
		this.doProcessResult( IMCSCardRW.mcs.MCS_WriteCard( PART_3,data ), rwType);

		//
		IMCSCardRW.mcs.MCS_Buzzer(0);
		IMCSCardRW.mcs.MCS_LED(2);

		//
		IMCSCardRW.mcs.MCS_Halt();
	}

	@Override
	public void writeMedicalCardBalance(double balance) {

        IMCSCardRW.mcs.MCS_Buzzer(1);
		IMCSCardRW.mcs.MCS_LED(1);
        //

		String f_balance = this.doProcessFormatBalance(balance);
		byte[] data = hexString2Bytes( f_balance, f_balance.length() );
		this.doProcessResult( IMCSCardRW.mcs.MCS_WriteCard( PART_3,data ), "W");

		//
		IMCSCardRW.mcs.MCS_Buzzer(0);
		IMCSCardRW.mcs.MCS_LED(2);

		//
		IMCSCardRW.mcs.MCS_Halt();
	}

	/**
	 *
	 * @return
	 */
    private String doProcessFormatStr(String str){

        StringBuilder sb = new StringBuilder(str);
        sb.insert(0,"0");
        if( str.length()<10 ){
        	sb.append("0000000");
        }else{
        	sb.append("00");
        }

    	return sb.toString();
    }

	/**
	 *
	 * @return
	 */
	private String doProcessFormatBalance(double balance) {

        String balanceStr = String.valueOf(balance);

        int point = balanceStr.indexOf(".");

        String integerStr = balanceStr.substring(0, point) ;
        String decimalStr = balanceStr.substring(point+1, balanceStr.length()) ;

        if( decimalStr.length()==1 ){
        	decimalStr += "0";
        }
        if( decimalStr.length()>2 ){
        	decimalStr= decimalStr.substring(0,2);
        }

        //System.out.println( " ========== decimalStr ==========  "+decimalStr );

        String allStr = integerStr.length()+decimalStr+integerStr;

        return this.doProcessFormatStr(allStr);
	}

	/**
	 *
	 * @param result1
	 * @param result2
	 * @param result3
	 * @return
	 */
	private CardDTO doReadHandle(String result1,String result2,String result3){

		//mr_no长度
		int mrNoLength = PatTool.getInstance().getMrNoLength();

		//mr_no长度
		//int mrNoLength = 12 ;

		//
		//System.out.println( " =========== result1 =========  "+ result1 );
		//System.out.println( " =========== result2 =========  "+ result2 );
		//System.out.println( " =========== result3 =========  "+ result3 );

		//mr_no
		result1 = result1.substring(1,mrNoLength+1);
		//System.out.println( " =========== mr_no =========  "+ result1 );

		//seq,ctype
		result2 = result2.substring(1,5+1);
		//System.out.println( " =========== result2 =========  "+ result2 );
        String seq = result2.substring(0,3);
        //System.out.println( " =========== seq =========  "+ seq );
        String ctype = result2.substring(3,5);
        //System.out.println( " =========== ctype =========  "+ ctype );

		//balance
		result3 = result3.substring(1,10+1);
		//System.out.println( " =========== result3 =========  "+ result3 );
        String ilength = result3.substring(0,1);
        //System.out.println( " =========== ilength =========  "+ ilength );
        String decimalStr = result3.substring(1,3);
        //System.out.println( " =========== decimalStr =========  "+ decimalStr );
        String integerStr = result3.substring(3,Integer.parseInt(ilength)+3);
        //System.out.println( " =========== integerStr =========  "+ integerStr );
        String balanceStr = integerStr+"."+decimalStr;

		//
		IMCSCardRW.mcs.MCS_Buzzer(0);
		IMCSCardRW.mcs.MCS_LED(2);

		//
		IMCSCardRW.mcs.MCS_Halt();

		//
		return new CardDTO( seq,result1,ctype,balanceStr );
	}

	/**
	 *
	 * @param result
	 */
	private void doProcessResult(int result,String rwType){

		String typeStr = "读卡";
        if( rwType.equals("W") ){
        	typeStr = "写卡";
        }

        //
        if( result!=0 ){

        	String error = null;
        	if( result==50 ){
        		error = "请放卡片!";
        	}

    		//
    		IMCSCardRW.mcs.MCS_Buzzer(0);
    		IMCSCardRW.mcs.MCS_LED(2);

    		//
    		IMCSCardRW.mcs.MCS_Halt();

        	throw new RuntimeException( typeStr+"失败! "+error );
        }
	}

	/**
	 *
	 * @param b
	 * @param readSize
	 * @return
	 */
	private String bytes2HexString(byte[] b, int readSize) {
		String ret = "";
		for (int i = 0; i < readSize; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}


    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式
     *   如："2B44EFD9" –> byte[]{0x2B, 0×44, 0xEF,0xD9}
	 *
     * @param src
     * @param readSize
     * @return
     */
	private byte[] hexString2Bytes(String src, int readSize) {
		byte[] ret = new byte[readSize / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < readSize / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * 将两个ASCII字符合成一个字节；
	 *   如："EF"–> 0xEF
	 *
	 * @param src0
	 * @param src1
	 * @return
	 */
	private byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}



	/**
	 *
	 * @param args
	 */
	public static void main(String args[]) {


		// new MCSCardRWUtil().writeMedicalCard("282","123456789","38",23.43344);
		 new MCSCardRWUtil().writeMedicalCardBalance(34567);
		 new MCSCardRWUtil().readMedicalCard();
	}




}



//####


/**
 *
 * @author whaosoft
 *
 */
interface IMCSCardRW extends Library{

	//
	IMCSCardRW mcs = (IMCSCardRW) Native.loadLibrary(
			(Platform.isWindows() ? ".\\mc_dll\\MCS_SR" : "c"), IMCSCardRW.class);

	/**
	 *
	 * @param com 串口编号10或以上表示USB口
	 * @param baudRate 波特率
	 */
	int MCS_InitComm(int com,int baudRate);

	/**
	 *
	 * @param _bMode 通讯模式: 198
	 * @param _bBaud 通讯速率: 14
	 * @return
	 */
	int MCS_Config(int _bMode, int _bBaud);

	/**
	 * 1:红 / 0:绿
	 * @param _type
	 */
	void MCS_LED(int _type);

	/**
	 * 1:开 / 0:关
	 * @param _type
	 */
	void MCS_Buzzer(int _type);

	/**
	 *
	 * @param _iBlock
	 * @param _bData
	 * @return ( 50:没卡 / 0:成功 )
	 */
	int MCS_ReadCard(int _iBlock,byte[] _bData);

	/**
	 *
	 * @param _iBlock
	 * @param _stData
	 * @return
	 */
	int MCS_WriteCard(int _iBlock, byte[] _stData);

	/**
	 *
	 * @return
	 */
	int MCS_Halt();

}
