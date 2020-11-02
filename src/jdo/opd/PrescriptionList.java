package jdo.opd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import jdo.opb.OPBReceipt;

/**
 *
 * <p>
 * Title: ����list
 * </p>
 *
 * <p>
 * Description:����list
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 200800911
 * @version 1.0
 */
public class PrescriptionList {
    /**
     * ����
     */
    private Map prescription = new HashMap();

    /**
     * ODO����
     */
    private ODO odo;
    /**
     * �ҺŶ���
     */
    private Reg reg;
    /**
     * ��������
     */
    /**
     * ���ùҺŶ���
     * @param reg Reg
     */
    public void setReg(Reg reg){
    	this.reg=reg;
    }
    /**
     * �õ��ҺŶ���
     * @return Reg
     */
    public Reg getReg(){
    	return this.reg;
    }
    /**
     *
     */
    public PrescriptionList(){


    }
    public PrescriptionList(ODO odo){
    	setODO(odo);
    }
    public void setODO(ODO odo){
    	this.odo=odo;
    }
    public ODO getODO(){
    	return this.odo;
    }

    /**
     *
     * @param groupName String
     * @return Vector��������ȡVector
     */
    public Vector getGroup(String groupName) {
        Vector data = (Vector) prescription.get(groupName);

        if (data == null) {

            data = new Vector();
            prescription.put(groupName, data);
        }
        return data;

    }

    /**
     * ��ĸ���
     * @return int
     */
    public int getGroupSize() {
        return prescription.size();
    }

    /**
     * ��������
     * @return String[]
     */

    public String[] getGroupNames() {
        String[] names = new String[getGroupSize()];
        Iterator it = prescription.keySet().iterator();
        int index = 0;
        while (it.hasNext()) {
            names[index] = (String) it.next();
            index++;
        }
        sort(names);
        return names;
    }
    
    /**
     * ð������
     * ====zhangp
     * @param names
     */
	private void sort(String[] names) {
		String temp = "0";
		for (int i = 0; i < names.length; i++) {
			for (int j = i; j < names.length; j++) {
				if (Integer.valueOf(names[i]) < Integer.valueOf(names[j])) {
					temp = names[i];
					names[i] = names[j];
					names[j] = temp;
				}
			}
		}
	}
	
    public boolean isExistGroup(String groupName) {
        if (groupName == null || groupName.trim().length() == 0)
            return false;
        return prescription.get(groupName) != null;
    }

    /**
     * ����ָ����ĳ���
     * @param groupName String
     * @return int
     */
    public int getGroupPrsptSize(String groupName) {
        if (!isExistGroup(groupName))
            return 0;
        return getGroup(groupName).size();
    }

    /**
     * �����ض����ָ���е�ҽ��
     * @param groupName String
     * @param index int
     * @return OrderList
     */
    public OrderList getOrderList(String groupName, int index) {
        if (!isExistGroup(groupName))
            return null;
        if (index < 0 || index >= getGroupPrsptSize(groupName))
            return null;
        OrderList ol=(OrderList) getGroup(groupName).get(index);
        ol.setPat(odo.getPat());
        return ol;
    }
    /**
     * �����ض����ָ���е�ҽ��opb��
     * @param groupName String
     * @param index int
     * @return OrderList
     */
    public OrderList getOrderListOob(String groupName, int index) {
       if (!isExistGroup(groupName))
           return null;
       if (index < 0 || index >= getGroupPrsptSize(groupName))
           return null;
       OrderList ol=(OrderList) getGroup(groupName).get(index);
       ol.setPat(reg.getPat());
       return ol;
   }


   /**
    * ��ʼ��List
     * Ҫ��parm��ֻ��һ����Ϊ��GROUP������������
    * @param parm TParm
    * @return �棺�ɹ����٣�ʧ��
    */
   public boolean initParm(TParm parm) {
        if (parm == null)
            return false;
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm group = new TParm();

            group.setData( (Map) parm.getData("GROUP", i));

            if (!initGroup(group))
                return false;
        }
        return true;
    }
    /**
     * װ������Ϣ
     * @param parm TParm
     * @return boolean
     */
    private boolean initGroup(TParm parm) {
        String groupName = parm.getValue("NAME");
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm list = new TParm();
            list.setData( (Map) parm.getData("LIST", i));
            OrderList orderlist = new OrderList(this);
            if (!orderlist.initParm(list))
                return false;
            getGroup(groupName).add(orderlist);
        }
        return true;
    }
    /**
     * �õ�����
     * @return TParm
     */
    public TParm getParmShow()
    {
        return getParmShow(new TParm());
    }
    /**
     * �õ�����
     * @param result TParm
     * @return TParm
     */
    public TParm getParmShow(TParm result)
    {
        return getParmShow(OrderList.PRIMARY,result);
    }
    /**
     * �õ�����
     * @param storeName String ������
     * @param result TParm
     * @return TParm
     */
    public TParm getParmShow(String storeName, TParm result)
    {
        //�õ�ȫ������
        String groupNames[] = getGroupNames();
        for(int i = 0;i < groupNames.length;i++)
        {
            //�õ�����ǩ�б�
            getParmShow(groupNames[i],storeName,result);
        }
        return result;
    }
    /**
     * �õ�����
     * @param groupName String ����ǩ��
     * @return TParm
     */
    public TParm getParmShow(String groupName)
    {
        return getParmShow(groupName,OrderList.PRIMARY);
    }
    /**
     * �õ�����
     * @param groupName String ����ǩ��
     * @param storeName String ������
     * @return TParm
     */
    public TParm getParmShow(String groupName,String storeName)
    {
        return getParmShow(groupName,storeName,new TParm());
    }
    /**
     * �õ�����
     * @param groupName String ����ǩ��
     * @param storeName String ������
     * @param result TParm ����
     * @return TParm
     */
    public TParm getParmShow(String groupName,String storeName, TParm result)
    {
        Vector group = getGroup(groupName);
        for(int j = 0;j < group.size();j++)
        {
            //�õ���һ����ǩ
            OrderList orderlist = (OrderList)group.get(j);
            orderlist.getParm(storeName,result,true);
        }
        result.setData("ACTION","COUNT",result.getCount("CASE_NO"));
        return result;
    }
    public List getOrder()
    {
        List list = new ArrayList();
        getOrder(list);
        return list;
    }
    public void getOrder(List list)
    {
        //�õ�ȫ������
        String groupNames[] = getGroupNames();
        for(int i = 0;i < groupNames.length;i++)
        {
            //�õ�����ǩ�б�
            getOrder(groupNames[i],list);
        }
    }
    public void getOrder(String groupName,List list)
    {
        Vector group = getGroup(groupName);
        for(int i = 0;i < group.size();i++)
        {
            //�õ���һ����ǩ
            OrderList orderlist = (OrderList)group.get(i);
            for(int j = 0;j < orderlist.size();j++)
                list.add(orderlist.get(j));
        }
    }
    public TParm getParm() {
        TParm parm = new TParm();
        TParm newParm = new TParm();
        TParm modifiedParm = new TParm();
        TParm deletedParm = new TParm();
        //�õ�ȫ������
        String groupNames[] = getGroupNames();
        for(int i = 0;i < groupNames.length;i++)
        {
            //�õ�����ǩ�б�
            Vector group = getGroup(groupNames[i]);
            for(int j = 0;j < group.size();j++)
            {
                //�õ���һ����ǩ
                OrderList orderlist = (OrderList)group.get(j);
                //װ������ҽ��
                orderlist.getParm(OrderList.NEW,newParm);
                //װ���޸�ҽ��
                orderlist.getParm(OrderList.MODIFIED,modifiedParm);
                //װ��ɾ��ҽ��
                orderlist.getParm(OrderList.DELETED,deletedParm);
            }
        }
        newParm.setData("ACTION","COUNT",newParm.getCount("CASE_NO"));
        modifiedParm.setData("ACTION","COUNT",modifiedParm.getCount("CASE_NO"));
        deletedParm.setData("ACTION","COUNT",deletedParm.getCount("CASE_NO"));
        //����������ݰ�
        parm.setData(OrderList.NEW,newParm.getData());
        parm.setData(OrderList.MODIFIED,modifiedParm.getData());
        parm.setData(OrderList.DELETED,deletedParm.getData());
        return parm;
    }
    /**
     * �½�����
     * @param groupName String
     * @return OrderList
     */
    public OrderList newOrderList(String groupName){
    	Vector v=this.getGroup(groupName);
    	OrderList ol=new OrderList(this);
    	OrderList oldol=this.getOrderList(groupName, this.getGroupPrsptSize(groupName)-1);
    	//System.out.println("prescriptionlist---------------"+(this.getGroupPrsptSize(groupName)-1));
    	if(oldol==null){
    		ol.setPresrtNo(1);
    	}else{
    		ol.setPresrtNo(this.getGroupPrsptSize(groupName)+1);
    	}
    	String rxNo=SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO", "RX_NO");
    	if(rxNo.length()==0)
    	{
    		//err("ȡ������ʧ��");
    		//System.out.println("ȡ������ʧ��");
    		return null;
    	}
    	ol.setPat(reg.getPat());
    	ol.setRxNo(rxNo);
    	ol.setRxType(groupName);
    	ol.setDrCode(Operator.getID());
    	ol.setDeptCode(Operator.getDept());
//    	ol.setRbOrderDeptCode(Operator.getDept());
    	ol.setAdmType(odo.getAdmType());
    	if(odo.getPat()==null)
    		//System.out.println("odo's pat=null");
    	ol.setPat(odo.getPat());

    	v.add(ol);
    	return ol;
    }
    /**
     * �½�����
     * @param groupName String
     * @param admType String
     * @param pat Pat
     * @return OrderList
     */
    public OrderList newOrderList(String groupName,String admType,Pat pat){
    	Vector v=this.getGroup(groupName);
    	OrderList ol=new OrderList(this);
    	OrderList oldol=this.getOrderListOob(groupName, this.getGroupPrsptSize(groupName)-1);
    	//System.out.println("prescriptionlist---------------"+(this.getGroupPrsptSize(groupName)-1));
    	if(oldol==null){
    		ol.setPresrtNo(1);
    	}else{
    		ol.setPresrtNo(this.getGroupPrsptSize(groupName)+1);
    	}
    	String rxNo=SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO", "RX_NO");
    	if(rxNo.length()==0)
    	{
    		//err("ȡ������ʧ��");
    		//System.out.println("ȡ������ʧ��");
    		return null;
    	}

    	ol.setRxNo(rxNo);
    	ol.setRxType(groupName);
    	ol.setDrCode(Operator.getID());
    	ol.setDeptCode(Operator.getDept());
//    	ol.setRbOrderDeptCode(Operator.getDept());
    	ol.setAdmType(admType);
    	ol.setPat(pat);
    	ol.setChargeFlg(true);
    	v.add(ol);
    	return ol;
    }
    public boolean removeOrderList(String groupName,int index){
    	Vector v=this.getGroup(groupName);
    	if(v.size()==0||index>=v.size()){
    		return false;
    	}
    	v.remove(index);
    	return true;
    }
//    /**
//     * �½�������ͨ������ȷ���Ƿ���ȡ�ô�����
//     * @param groupName
//     * @param takeNewRxNo boolean �Ƿ���ȡ�ô����� Y:��ȡ�ô����� N:ʹ�þɴ�����
//     * @param rxNo String �粻ȡ�ô��������봦����
//     * @return OrderList �����½���OrderList
//     */
//    public OrderList newOrderList(String groupName,boolean takeNewRxNo,String rxNo){
//    	//����У�飬���������ȡ�ô������Ҵ��봦����Ϊ��
//    	if((!takeNewRxNo)&&(rxNo==null||rxNo.length()==0)){
//    		return new OrderList();
//    	}
//    	Vector v=this.getGroup(groupName);
//    	OrderList ol=new OrderList(this);
//    	if(takeNewRxNo){
//    		rxNo=SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO", "RX_NO");
//    	}
//    	if(rxNo.length()==0)
//    	{
//    		//err("ȡ������ʧ��");
//    		//System.out.println("ȡ������ʧ��");
//    		return null;
//    	}
//    	ol.setRxNo(rxNo);
//    	ol.setRxType(groupName);
//    	v.add(ol);
//    	return ol;
//    }
    public String getCaseNo(){
    	if(this.getODO()!=null)
    		return this.getODO().getCaseNo();
    	return "";
    }
    public String getAdmType(){
    	if(this.getODO()!=null)
    		return this.getODO().getAdmType();
    	return "";
    }
    public String getDrCode(){
    	if(this.getODO()!=null)
    		return this.getODO().getDrCode();
    	return "";
    }

    public void reset()
    {
    	String groupNames[] = this.getGroupNames();
    	for(int i = 0;i < groupNames.length;i++)
    	{
    		int count = getGroupPrsptSize(groupNames[i]);
    		for(int j = 0;j < count;j++)
    			this.getOrderList(groupNames[i], j).reset();

    	}
    }
    /**
     * ת���ַ�����
     * @return String
     */
    public String toString() {
        return prescription.toString();
    }

    public static void main(String[] args) {
        PrescriptionList p = new PrescriptionList();
        TParm parm = new TParm();
        parm.setData("ACTION", "COUNT", 1);
        TParm xiyaozu = new TParm();

        xiyaozu.setData("NAME", "��ҩ");
        xiyaozu.setData("ACTION", "COUNT", 1);
        parm.addData("GROUP", xiyaozu.getData());

        TParm order = new TParm();
        //icdCode:ICD_CODE;description:DESCRIPTION;icdCode:OLD_CODE:old
        order.addData("CASE_NO", "01");
        order.addData("RX_NO", "01");
        order.setData("ACTION", "COUNT", 1);

        xiyaozu.addData("LIST", order.getData());
        p.initParm(parm);

        OrderList list = p.getOrderList("��ҩ",0);
        Order order1 = list.newOrder();
        order1.setCaseNo("AAA");
        //System.out.println(p);
        //System.out.println(p.getParm());
    }
    /**
   * �õ�����ǩ������comb
   * @return TParm
   * add by fudw
   */
  public Vector getPrescriptionComb(String cat1Type) {
      String rxType = "";
      if("PHA".equals(cat1Type))
          rxType = "1,2,3";
      if("LIS".equals(cat1Type)||"RIS".equals(cat1Type))
          rxType = "5";
      if("TRT".equals(cat1Type)||"PLN".equals(cat1Type))
          rxType = "4";
      if("OTH".equals(cat1Type))
          rxType = "7";
      //����ǩcomb������
      Vector prescriptionComb=new Vector();
      //һ������
      Vector combOneLine=new Vector();
      //��ʾ��ʽ
      combOneLine.add("id");
      combOneLine.add("name");
      //���һ��
      prescriptionComb.add(combOneLine);
      combOneLine=new Vector();
      //����
      combOneLine.add("");
      combOneLine.add("");
      //��ӿ���
      prescriptionComb.add(combOneLine);
      //�õ����д���ǩ��
     String[] prescript = this.getGroupNames();
     //
      for (int i = 0; i < prescript.length; i++) {
          //�õ�����ǩ����
         int prescriptSize=  this.getGroup(prescript[i]).size();
         for(int j=0;j<prescriptSize;j++){
             OrderList orderList=this.getOrderListOob(prescript[i], j);
//             System.out.println("����ǩҽ������"+orderList.getRxType());
//             System.out.println("�����"+rxType);
//             System.out.println("�ȽϽ��"+rxType.indexOf(orderList.getRxType()));
             if(cat1Type==null||cat1Type.length()==0){
             }else{
                 if (rxType.indexOf(orderList.getRxType())<0)
                     continue;
             }
             combOneLine=new Vector();
//             System.out.println("����ǩ��"+orderList.getRxNo());
             combOneLine.add(orderList.getRxNo());
             combOneLine.add(orderList.getDrCode());
             prescriptionComb.add(combOneLine);
         }
      }
      return prescriptionComb;
  }

  public Vector getPrescriptionComb() {

      //����ǩcomb������
      Vector prescriptionComb = new Vector();
      //һ������
      Vector combOneLine = new Vector();
      //��ʾ��ʽ
      combOneLine.add("id");
      combOneLine.add("name");
      //���һ��
      prescriptionComb.add(combOneLine);
      combOneLine = new Vector();
      //����
      combOneLine.add("");
      combOneLine.add("");
      //��ӿ���
      prescriptionComb.add(combOneLine);
      //�õ����д���ǩ��
      String[] prescript = this.getGroupNames();
      //
      for (int i = 0; i < prescript.length; i++) {
          //�õ�����ǩ����
          int prescriptSize = this.getGroup(prescript[i]).size();
          for (int j = 0; j < prescriptSize; j++) {
              OrderList orderList = this.getOrderListOob(prescript[i], j);

              combOneLine = new Vector();

              combOneLine.add(orderList.getRxNo());
              combOneLine.add(orderList.getDrCode());
              prescriptionComb.add(combOneLine);
          }
      }
      return prescriptionComb;
  }
  /**
   * �õ����е�ҽ��orderList
   * @param opbReceipt OPBReceipt
   * @return OrderList
   */
  public OrderList getOrderList(OPBReceipt opbReceipt) {
      OrderList orderList = new OrderList();
      //�õ����д���ǩ��
      String[] prescript = this.getGroupNames();
      //
      for (int i = 0; i < prescript.length; i++) {
          //�õ�����ǩ����
          int prescriptSize = this.getGroup(prescript[i]).size();
          for (int j = 0; j < prescriptSize; j++) {
              //�ó�һ�Ŵ���ǩ�е�order
              OrderList odList = this.getOrderListOob(prescript[i], j);
              int count=odList.size();
              for(int t=0;t<count;t++){
                  //ȡ��һ��order
                  Order order = (Order) odList.getOrder(t);
                  //����������ӵ�Ʊ����
                  dealReceipt(orderList,order,opbReceipt);
              }
          }
      }
      return orderList;
  }

  /**
   * ��Ʊ
   * @param orderList OrderList
   * @param order Order
   * @param opbReceipt OPBReceipt
   */
  public void dealReceipt(OrderList orderList, Order order,
                          OPBReceipt opbReceipt) {
      if(opbReceipt.getReceiptNo().equals(order.getReceiptNo()))
          orderList.add(order);
  }
  /**
   * ȡ�ü���ҽ����parm
   * @param groupNo int
   * @param orderCode String
   * @return TParm
   */
  public TParm  getOrderSetParm(int groupNo,String orderCode){
      OrderList orderList = new OrderList();
     //�õ����д���ǩ��
     String[] prescript = this.getGroupNames();
     for (int i = 0; i < prescript.length; i++) {
         //�õ�����ǩ����
         int prescriptSize = this.getGroup(prescript[i]).size();
         for (int j = 0; j < prescriptSize; j++) {
             //�ó�һ�Ŵ���ǩ�е�order
             OrderList odList = this.getOrderListOob(prescript[i], j);
             int count=odList.size();
             for(int t=0;t<count;t++){
                 //ȡ��һ��order
                 Order order = (Order) odList.getOrder(t);
                 int oneGroupNo=order.getOrderSetGroupNo();
                 String oneOrderSetCode = order.getOrdersetCode();
                 if(oneOrderSetCode==null||oneOrderSetCode.length()==0)
                     continue;
                 if (groupNo == oneGroupNo && orderCode.equals(oneOrderSetCode))
                     orderList.add(order);
             }
         }
     }
      return orderList.getParm(OrderList.PRIMARY);
  }
}
