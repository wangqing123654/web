package action.ins;
/**
 *
 * <p>Title: ���ҽ���ӿ���(�ڲ�)</p>
 *
 * <p>Description: ���ҽ���ӿ���(�ڲ�)</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2011.12.07
 * @version 1.0
 */
public class TJSafeDriver {
    static {
        try {
            //����TJSafeDriver.dll
            System.loadLibrary("TJSafeDriver");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TJSafeDriver() {
    }

    //========================================//
    // ҽ���ӿڷ���
    //========================================//
    //yhybReckoning.dll
    public native static int init(int type, String path, int isDebug);

    public native static String DataDown_rs(String Param, String Type,
                                            byte[] returndata);

    public native static String DataDown_sp(String Param, String Type,
                                            byte[] returndata);

    public native static String DataUpload(String Param, String Type,
                                           byte[] returndata);

    public native static String DataDown_yb(String Param, String Type,
                                            byte[] returndata);

    public native static String DataDown_czys(String Param, String Type,
                                              byte[] returndata);

    public native static String DataDown_czyd(String Param, String Type,
                                              byte[] returndata);

    public native static String DataDown_cmts(String Param, String Type,
                                              byte[] returndata);

    public native static String DataDown_cmtd(String Param, String Type,
                                              byte[] returndata);

    public native static String DataDown_mts(String Param, String Type,
                                             byte[] returndata);

    public native static String DataDown_mtd(String Param, String Type,
                                             byte[] returndata);

    public native static String DataDown_jk(String Param, String Type,
                                            byte[] returndata);

    //yhybReckoning_his.dll
    public native static String DataDown_sp1(String Param, String Type,
                                             byte[] returndata);

    public static void main(String args[]) {
        TJSafeDriver driver = new TJSafeDriver();
        //System.out.println(driver.init(1, "D:\\����\\NEW\\log\\aaa.txt", 1));
        byte[] b = new byte[397];
        //System.out.println(driver.DataUpload("AAA���", "A", b));
        //System.out.println(new String(b));
    }

}
