package jdo.device;

/**
 * �кų���
 * @author lixiang
 *
 */
public class CallNoTx {
	
	/**
	   * �๹����
	   * @alias �๹����
	   */
	  static{
	    System.loadLibrary("CallNo");
	    init();
	  }

	  /**
	   * ��ʼ������
	   * @alias  ��ʼ������
	   * @return int
	   */
	  public native static int init();
	  /**
	   *
	   * @param MsgType int
	   * @param Msg String
	   */
	  public native static void SendMsg(int MsgType, String Msg);
	  /**
	   *
	   * @param MsgType int
	   * @param Msg String
	   * @return int
	   */
	  public native static int SyncSendMsg(int MsgType, String Msg);


	  /**
	   * ������
	 * @return 
	   * @alias ������
	   */
	  public  CallNoTx() {
	  }


	  /**
	   * ������
	   */
	  public static void main(String[] args) {
		  
	  }

}
