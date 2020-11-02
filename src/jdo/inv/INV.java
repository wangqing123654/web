package jdo.inv;

import com.dongyang.data.TParm;
/**
 *
 * <p>Title: ���ʴ�������</p>
 *
 * <p>Description:���ʴ������� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw 2009-09-21
 * @version 1.0
 */
public class INV {
    /**
     * ����������
     * @return SYSFeeTool
     */
    public static InvStockMTool InvStockMTool() {
        return InvStockMTool.getInstance();
    }
    /**
     * ��������ϸ��
     * @return SYSFeeTool
     */
    public static InvStockDTool InvStockDTool() {
        return InvStockDTool.getInstance();
    }

    /**
     * ���ҿ��������������ŵ�������ϸ����ĳһ���ҵĿ��������
     * @param orgCode String(���Ҵ���)
     * @param invCode String(���ʴ���)
     * @param batchSeq int(�������)
     * @return double(�����)
     */
    public static double getStockQty(String orgCode, String invCode,int batchSeq) {
        double qty = 0.0;
        return qty;
    }

    /**
     *  �������п�����ʣ���������ŵ�ĳһ���ҵĿ�����ʣ�
     * @param orgCode String(����)(����)
     * @param invCode String(���ʴ���)
     * @param batchSeq int
     * @return TParm
     */
    public static TParm getAllStockQty(String orgCode, String invCode,
                                       int batchSeq) {
        TParm result = new TParm();
        return result;
    }
    /**
     *  ���ҿ�����������������ĳ����ĳ����������
     * @param orgCode String(���Ҵ���)
     * @param invCode String(���ʴ���)
     * @return double
     */
    public static double getStockQty(String orgCode, String invCode) {
        if(invCode == null ||invCode.length() == 0)
              return -1;
          TParm parm=new TParm();
          parm.setData("INV_CODE",invCode);
          parm.setData("ORG_CODE",orgCode);
          parm=InvStockMTool().getStockQty(parm);   //wm2013-06-04  INVStockMTool()
          if (parm.getErrCode() < 0){
              System.out.println(parm.getErrCode() + " " + parm.getErrText());
              return -1;
          }
          return parm.getDouble("SUM(STOCK_QTY)",0);
    }
    /**
     * �������п�����ʣ����������ĳ���ҵ�ȫ�����ŵĿ�棩
     * @param orgCode String
     * @param invCode String
     * @return TParm
     */
    public static TParm getAllStockQty(String orgCode, String invCode) {
    	System.out.println("INV.java");
        if (orgCode == null || orgCode.length() == 0 || invCode == null ||
            invCode.length() == 0)
            return null;
        TParm parm = new TParm();
        parm.setData("INV_CODE", invCode);
        parm.setData("ORG_CODE",orgCode);
        parm=InvStockDTool().getStockBatchSeq(parm);			//wm2013-06-03
        if (parm.getErrCode() < 0)
           System.out.println(parm.getErrCode() + " " + parm.getErrText());
        return parm;
    }
    /**
     * ���ҿ�����������������ĳ����ȫԺ������
     * @param invCode String
     * @return double
     */
    public static double getStockQty(String invCode){
        if(invCode == null ||invCode.length() == 0)
            return -1;
        TParm parm=new TParm();
        parm.setData("INV_CODE",invCode);
        parm=InvStockMTool().getStockQty(parm);
        if (parm.getErrCode() < 0){
            System.out.println(parm.getErrCode() + " " + parm.getErrText());
            return -1;
        }
        return parm.getDouble("STOCK_QTY",0);
    }
    /**
     * �Զ�������
     * @param parm TParm
     * @return String
     */
    public String autoRePack(TParm parm){



        return "";
    }




}
