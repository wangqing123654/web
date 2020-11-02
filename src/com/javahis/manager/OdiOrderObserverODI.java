package com.javahis.manager;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
/**
 * <p>Title: סԺ����=>סԺҽ��վϵͳ=>סԺҽ��վ������=ODI_ORDER�۲���</p>
 *
 * <p>Description: ODI_ORDER�۲���</p>
 *
 * <p>Copyright: Copyright JavaHis (c) 2009��1��</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class OdiOrderObserverODI
    extends TObserverAdapter {
    /**
     * ���������й�ϵ�б�
     */
    Map newRowMap = new HashMap();
    /**
    * �Ƿ���δ�༭��
    * @return boolean
    */
   public boolean isNewRow(){
       Boolean falg = false;
       TParm parmBuff = this.getDS().getBuffer(this.getDS().PRIMARY);
//       System.out.println("�Ƿ���δ�༭��parmBuff"+parmBuff);
       int lastRow = parmBuff.getCount("#ACTIVE#");
       Object obj = parmBuff.getData("#ACTIVE#",lastRow-1);
       if(obj!=null){
           falg = (Boolean)parmBuff.getData("#ACTIVE#",lastRow-1);
       }else{
           falg=true;
       }
//       System.out.println("�Ƿ���δ�༭��**********"+falg);
       return falg;
   }
   /**
    * �õ���M����ж�Ӧ
    * @return int
    */
   public int getRowIdDspnm(){
       int row = -1;
       TParm parmBuff = this.getDS().getBuffer(this.getDS().PRIMARY);
       int lastRow = parmBuff.getCount("#ACTIVE#");
       Object obj = parmBuff.getData("#ACTIVE#",lastRow-1);
       if(obj!=null){
           boolean falg = parmBuff.getBoolean("#ACTIVE#",lastRow-1);
           if(!falg){
               row = (Integer)this.getDS().getItemData(lastRow-1,"#ID#");
//               System.out.println("OOOOOOOOOOOOOO========"+row);
               Iterator iter = newRowMap.keySet().iterator();
               List mapList = new ArrayList();
               while(iter.hasNext()){
                   int objIndex = (Integer)iter.next();
                   if(row == Integer.parseInt(newRowMap.get(objIndex).toString())){
//                       System.out.println("MMMMMMMMMMMMM========"+objIndex);
                       /**
                        * Iterator �ǹ�����һ���������߳��У�����ӵ��һ�� mutex ����
                        * Iterator ������֮��Ὠ��һ��ָ��ԭ������ĵ���������
                        * ��ԭ���Ķ������������仯ʱ���������������ݲ���ͬ���ı䣬
                        * ���Ե�����ָ�������ƶ���ʱ����Ҳ���Ҫ�����Ķ���
                        * ���԰��� fail-fast ԭ�� Iterator
                        * �������׳� java.util.ConcurrentModificationException �쳣
                        * ���� Iterator �ڹ�����ʱ���ǲ����������Ķ��󱻸ı�ġ�
                        * �������ʹ�� Iterator ����ķ��� remove() ��ɾ������
                        * Iterator.remove() ��������ɾ����ǰ���������ͬʱά��������һ���ԡ�
                        * ������ Collection / Map ����ʵ��ֻ��һ��Ԫ�ص�ʱ��
                        * ConcurrentModificationException �쳣�����ᱻ�׳�
                        */
                       mapList.add(objIndex);
                   }
               }
               for(int j=0;j<mapList.size();j++){
                   newRowMap.remove(mapList.get(j));
               }
           }else{
               row = -1;
           }
       }
       return row;
   }
    /**
     * ����һ������
     * @param ds TDS
     * @param row int
     * @return int Ϊ������ʾ��ȷ����
     */
    public int insertRow(TDS odiOrderDs, int row) {
        String type = odiOrderDs.getAttributeString("RX_KIND");
//        System.out.println("?????????????????   "+type);
        //��ʱ
        if("ST".equals(type)){
            //�õ�ORDER�����������е�ΨһID��DATESTORE(TDS)�е�
            int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
//            System.out.println("INSERT�������к�:"+row);
//            System.out.println("INSERTDATESTORE�к�:"+dsRow);
            //�鿴M���Ƿ���δ�༭��(ɾ��ʱʹ��)���Ҳ��Ǽ���ҽ��
            if(!this.isNewRow()&&!odiOrderDs.getAttributeBoolean("ORDERJH_FLG")){
//                System.out.println("������====");
                int rowDspnm = getRowIdDspnm();
                if(rowDspnm<0){
                    return row;
                }else{
                    newRowMap.put(dsRow,rowDspnm);
                    odiOrderDs.setItem(row,"RX_KIND", type);
                }
                return row;
            }
//            System.out.println("M������:====");
            //��DSPNM�в���һ������Ϊ��������
            this.getDS().setAttribute("RX_KIND",type);
            int newRow = this.getDS().insertRow();
            int newRowId = (Integer)this.getDS().getItemData(newRow,"#ID#");
//            System.out.println("DSPNM������" + newRow);
            //���ö���֮�����еĶ�Ӧ��ϵ
            newRowMap.put(dsRow,newRowId);
            //����ORDER�е�����
            odiOrderDs.setItem(row,"RX_KIND", type);
            //����DSPNM�е�����
//            this.getDS().setItem(newRow, "DSPN_KIND", type);(0907)
//            //���ÿ����
//            this.getDS().setItem(newRow, "CASE_NO", odiOrderDs.getAttribute("CASE_NO"));(0907)
            //������
            this.getDS().setActive(newRow,false);
//            System.out.println("=======================M������");
//            this.getDS().showDebug();
            return newRow;
        }
        //����
        if("UD".equals(type)){
            //�õ�ORDER�����������е�ΨһID��DATESTORE(TDS)�е�
            int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
            //��DSPNM�в���һ������Ϊ��������
            this.getDS().setAttribute("RX_KIND",odiOrderDs.getAttribute("RX_KIND"));
            int newRow = this.getDS().insertRow();
//            System.out.println("DSPNM������" + newRow);
            //���ö���֮�����еĶ�Ӧ��ϵ
            newRowMap.put(dsRow, newRow);
            //����ORDER�е�����
            odiOrderDs.setItem(row, "RX_KIND", odiOrderDs.getAttribute("RX_KIND"));
            //����DSPNM�е�����
            this.getDS().setItem(newRow, "DSPN_KIND", odiOrderDs.getAttribute("RX_KIND"));
            //���ÿ����
            this.getDS().setItem(newRow, "CASE_NO", odiOrderDs.getAttribute("CASE_NO"));
            //������
            this.getDS().setActive(newRow,false);
//            System.out.println("=======================M������");
//            this.getDS().showDebug();
            return newRow;
        }
        //��Ժ��ҩ
        if("DS".equals(type)){
            //�õ�ORDER�����������е�ΨһID��DATESTORE(TDS)�е�
             int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
//             System.out.println("�õ�ORDER�����������е�ΨһID��DATESTORE(TDS)�е�"+dsRow);
             //��DSPNM�в���һ������Ϊ��������
             this.getDS().setAttribute("RX_KIND",odiOrderDs.getAttribute("RX_KIND"));
             int newRow = this.getDS().insertRow();
//             System.out.println("DSPNM��������DSPNM�в���һ������Ϊ��������" + newRow);
             //���ö���֮�����еĶ�Ӧ��ϵ
             newRowMap.put(dsRow, newRow);
             //����ORDER�е�����
             odiOrderDs.setItem(row, "RX_KIND", odiOrderDs.getAttribute("RX_KIND"));
             //����DSPNM�е�����
             this.getDS().setItem(newRow, "DSPN_KIND", odiOrderDs.getAttribute("RX_KIND"));
             //���ÿ����
             this.getDS().setItem(newRow, "CASE_NO", odiOrderDs.getAttribute("CASE_NO"));
             //������
             this.getDS().setActive(newRow,false);
//             System.out.println("=======================M������");
//             this.getDS().showDebug();
             return newRow;
        }
        //��ҩ��Ƭ
        if("IG".equals(type)){
            //�õ�ORDER�����������е�ΨһID��DATESTORE(TDS)�е�
             int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
             //��DSPNM�в���һ������Ϊ��������
             this.getDS().setAttribute("RX_KIND",odiOrderDs.getAttribute("RX_KIND"));
             int newRow = this.getDS().insertRow();
//             System.out.println("DSPNM������" + newRow);
             //���ö���֮�����еĶ�Ӧ��ϵ
             newRowMap.put(dsRow, newRow);
             //����ORDER�е�����
             odiOrderDs.setItem(row, "RX_KIND", odiOrderDs.getAttribute("RX_KIND"));
             //����DSPNM�е�����
             this.getDS().setItem(newRow, "DSPN_KIND", odiOrderDs.getAttribute("RX_KIND"));
             //���ÿ����
             this.getDS().setItem(newRow, "CASE_NO", odiOrderDs.getAttribute("CASE_NO"));
             //������
             this.getDS().setActive(newRow,false);
//             System.out.println("=======================M������");
//             this.getDS().showDebug();
             return newRow;
        }
        return -1;
    }
    /**
     * ������
     * @param ds TDS
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setItem(TDS odiOrderDs, int row, String column, Object value) {
        //ҽ������
        String type = odiOrderDs.getAttributeString("RX_KIND");
        //�Ƿ񱾶���Ҫ������ֵ(Y��ʾ��ҪN��ʾ����Ҫ��ֹ�ݹ�)
        if(!(Boolean)odiOrderDs.getAttribute("CHANGE_FLG"))
            return false;
//        System.out.println("����ֵ:"+value+"��ֵ:"+column+"ҽ������:"+type);
//        System.out.println("setItem�������к�:"+row);
        String buff = odiOrderDs.isFilter()?odiOrderDs.FILTER:odiOrderDs.PRIMARY;
        //��ʱҽ��
        if("ST".equals(type)){
//            System.out.println("M����:"+newRowMap);
            //����ҽ�����
            if ("RX_KIND".equals(column)||"CASE_NO".equals(column)) {
//                System.out.println("Order��column"+column);
                if("RX_KIND".equals(column)){
                    int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
                    odiOrderDs.setAttribute("CHANGE_FLG", false);
                    odiOrderDs.setItem(row, column, value);
                    odiOrderDs.setAttribute("CHANGE_FLG", true);
                    int odiM = (Integer)newRowMap.get(dsRow);
//                    System.out.println("M����к�:"+odiM);
                    //��ѯ��DSPNM�е�λ��
                    int dspnmRowIdSet = getDspnmSet(odiM,this.getDS());
                    if(dspnmRowIdSet<0){
                        if(buff.equals(odiOrderDs.PRIMARY))
                            return true;
                        else
                            return false;
                    }
                    return this.getDS().setItem(dspnmRowIdSet,"DSPN_KIND",value);
                }
                if("CASE_NO".equals(column)){
                    //��ʼ��ORDER���ݵ�ȫ���ж�Ӧ��
                    if(newRowMap.isEmpty()){
                        createDataStoreTable(type,odiOrderDs,this.getDS());
                    }
                }
//                odiOrderDs.setAttribute("CHANGE_FLG", false);
//                odiOrderDs.setItem(row, column, value);
//                odiOrderDs.setAttribute("CHANGE_FLG", true);
                if (buff.equals(odiOrderDs.PRIMARY))
                    return true;
                else
                    return false;
            }
            if(value instanceof TParm){
                int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
                int odiM = (Integer) newRowMap.get(dsRow);
                TParm parm = (TParm)value;
                //�õ���
                String columnList[] = odiOrderDs.getColumns();
                for(String temp:columnList){
                    if(!"LINK_NO".equals(temp)&&parm.getValue(temp).length()==0)
                        continue;
                    if("RX_KIND".equals(temp)||"CASE_NO".equals(temp))
                        continue;
                    odiOrderDs.setAttribute("CHANGE_FLG", false);
                    odiOrderDs.setItem(row,temp,parm.getData(temp));
                    odiOrderDs.setAttribute("CHANGE_FLG", true);
                    //����M��ʱ�Դ��ֶεĴ���
                    if("START_DTTM".equals(temp)){
//                        System.out.println("START_DTTM"+parm.getData(temp));
                        if(parm.getData(temp) instanceof Timestamp){
                            String startDttm = StringTool.getString(parm.getTimestamp(temp),"yyyyMMddHHmmss");
                            parm.setData(temp,startDttm);
                        }
                    }
                    //��ѯ��DSPNM�е�λ��
                    int dspnmRowIdSet = getDspnmSet(odiM, this.getDS());
                    if (dspnmRowIdSet < 0) {
                        if (buff.equals(odiOrderDs.PRIMARY))
                            return true;
                        else
                            return false;
                    }

                    System.out.println("M����:"+dspnmRowIdSet);
//                    this.getDS().setItem(dspnmRowIdSet,temp,parm.getData(temp));
                    this.getDS().setItem(dspnmRowIdSet,"",parm);
                    this.getDS().setActive(dspnmRowIdSet,true);
                }
                if (buff.equals(odiOrderDs.PRIMARY))
                    return true;
                else
                    return false;
            }

        }
//        //�ж��Ƿ���TPARM���Ͳ��Ҳ��ǿ�ָ������
//        if (value instanceof TParm && column.length()==0) {
//            int id = (Integer) odiOrderDs.getItemData(row, "#ID#");
//            System.out.println("setItemDATESTORE�к�:"+id);
//            //�������е��к�
//            int dspnmNewRow = (Integer) newRowMap.get(id);
//            //ת����TPARM
//            TParm parm = (TParm) value;
//            //��ʼ���и�ֵ
//            Object obj = parm.getData("ACTIVE");
//            if(obj!=null){
//                Boolean falg = (Boolean)parm.getData("ACTIVE");
//                if(!falg){
//                    odiOrderDs.setAttribute("CHANGE_FLG", false);
//                    odiOrderDs.setItem(row, "RX_KIND", parm.getData("RX_KIND"));
//                    return true;
//                }
//            }
//            //������
//            this.getDS().setActive(dspnmNewRow,true);
//            //�õ���ǰODI_ORDER����Ҫ���õ���������һ������
////            System.out.println("M����Ҫ��ֵ������"+value);
//            String columnList[] = odiOrderDs.getColumns();
//            //ѭ����ÿ���и�ֵ(���ݲ�ͬ���������ò�ͬ�ļ��㷽ʽ����SYS_FEE�ж�ӦODI_ORDER�е�������ת��)
//            for (String temp : columnList) {
//                if (parm.getData(temp) == null) {
//                    continue;
//                }
//                odiOrderDs.setAttribute("CHANGE_FLG",false);
//                //��ֵFʱ������
//                if("Y".equals(parm.getData("RX_FLG"))){
//                    if (temp.equals("RX_KIND")) {
//                        this.getDS().setItem(dspnmNewRow, "DSPN_KIND",
//                                             parm.getData(temp));
//                        continue;
//                    }
//                }
//                odiOrderDs.setItem(row,temp,parm.getData(temp));
//                //�ҵ�ODI_DSPNM������������
////                System.out.println("�ж���======================>>>>>"+newRowMap+"������=======>>"+row);
//                if(temp.equals("START_DTTM")){
////                    System.out.println("M������"+parm.getData(temp).getClass()+"==="+parm.getData(temp));
//                    if(parm.getData(temp) instanceof Timestamp){
//                        String startDttm = StringTool.getString(parm.getTimestamp(temp),"yyyyMMddHHmmss");
////                        System.out.println("����������:"+startDttm);
//                        parm.setData(temp,startDttm);
//                    }
//                }
//                this.getDS().setItem(dspnmNewRow, temp, parm.getData(temp));
//            }
//            //����ֵ
//            this.getDS().setItem(dspnmNewRow,"", parm);
//        }
//
//        //����ҽ�����
//        if("RX_KIND".equals(column)){
//            odiOrderDs.setAttribute("CHANGE_FLG",false);
//            odiOrderDs.setItem(row,column,value);
//        }
        if (buff.equals(odiOrderDs.PRIMARY))
            return true;
        else
            return false;
    }
    /**
     * �ҵ�DSPNM�е�λ��
     * @param id int
     * @param dspnmDs TDS
     * @return int
     */
    public int getDspnmSet(int id,TDS dspnmDs){
        String buff = dspnmDs.isFilter()?dspnmDs.FILTER:dspnmDs.PRIMARY;
        int rowCount = dspnmDs.rowCount();
        int result = -1;
        for(int i=0;i<rowCount;i++){
            int setId = (Integer)dspnmDs.getItemData(i,"#ID#",buff);
            if(id==setId){
                result = i;
                break;
            }
        }
        return result;
    }
    /**
     * ɾ��һ��
     * @param ds TDS
     * @param row int
     * @return boolean
     */
    public boolean deleteRow(TDS odiOrderDs, int row) {
        //ҽ������
        String type = odiOrderDs.getAttributeString("RX_KIND");
//        System.out.println("DELҽ������"+type);
        String buff = odiOrderDs.isFilter()?odiOrderDs.FILTER:odiOrderDs.PRIMARY;
        int delRowOrder = odiOrderDs.getAttributeInt("DELROW");
        if("ST".equals(type)){
            int odiM = (Integer) newRowMap.get(delRowOrder);
//            System.out.println("��ӦM��ɾ����ID:"+odiM);
            //��ѯ��DSPNM�е�λ��
            int dspnmRowIdSet = getDspnmSet(odiM, this.getDS());
//            System.out.println("��ѯ��DSPNM�е�λ��:"+dspnmRowIdSet);
            if (dspnmRowIdSet < 0) {
                if (buff.equals(odiOrderDs.PRIMARY))
                    return true;
                else
                    return false;
            }
//            System.out.println("DEL��:"+dspnmRowIdSet);
            this.getDS().setAttribute("DELROW",odiM);
            this.getDS().deleteRow(dspnmRowIdSet);
            newRowMap.remove(delRowOrder);
//            System.out.println("M���Ӧ��:"+newRowMap);
            if (buff.equals(odiOrderDs.PRIMARY))
                return true;
            else
                return false;
        }
//        System.out.println("ɾ��һ��"+row);
//        //�������е�ID
//        Object obj = odiOrderDs.getItemData(row,"#ID#");
////        System.out.println("�õ�objֵ"+obj);
//        Object obj1 = odiOrderDs.getAttribute("DEL_FLG");
////        System.out.println("�õ�obj1��ֵ"+obj1);
//        if(obj1!=null){
//            if (! (Boolean) odiOrderDs.getAttribute("DEL_FLG")) {
//                if (obj != null) {
//                    int OrderNewRow = (Integer) obj;
//                    TParm parm = this.getDS().getRowParm( (Integer) newRowMap.
//                        get(OrderNewRow));
////                    System.out.println("Mɾ������" + parm);
//                    this.getDS().deleteRow( (Integer) newRowMap.get(OrderNewRow));
//                    newRowMap.remove(OrderNewRow);
//                    return true;
//                }
//                this.getDS().deleteRow(row);
//                return false;
//            }
//            odiOrderDs.setAttribute("DEL_FLG", false);
//            odiOrderDs.deleteRow(row);
//        }else{
//           this.getDS().deleteRow(row);
//        }
        if (buff.equals(odiOrderDs.PRIMARY))
            return true;
        else
            return false;

    }
    /**
     * �õ�δ֪��ֵ
     * @param ds TDS
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TDS ds, TParm parm, int row,String column) {
        if("EFF_DATEDAY".equals(column)){
            TParm action = parm.getRow(row);
            String effDate = StringTool.getString(action.getTimestamp("EFF_DATE"),"yyyy/MM/dd HH:mm:ss");
            return effDate;
        }
        if("ORDER_DESCCHN".equals(column)){
            TParm action = parm.getRow(row);
            String orderDescChn = action.getValue("ORDER_DESC")+action.getValue("GOODS_DESC")+action.getValue("DESCRIPTION")+action.getValue("SPECIFICATION");
            return orderDescChn;
        }
        return null;
    }
    /**
     * ����δ֪��ֵ
     * @param ds TDS
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setOtherColumnValue(TDS ds, TParm parm, int row,String column, Object value) {
        if("EFF_DATEDAY".equals(column)){
            ds.setAttribute("CHANGE_FLG",false);
            ds.setItem(row,"EFF_DATE",StringTool.getTimestamp(""+value,"yyyy/MM/dd HH:mm:ss"));
        }
        return false;
    }
    /**
     * ��ʼ������͹۲����ж�Ӧ
     * @param type String
     * @param orderDs TDS
     * @param dspnmDs TDS
     */
    public void createDataStoreTable(String type,TDS orderDs,TDS dspnmDs){
        if("ST".equals(type)){
//            String filterStr = orderDs.getFilter();
//            String sortStr = orderDs.getSort();
//            System.out.println("�������"+filterStr);
//            orderDs.setFilter("");
//            orderDs.filter();
//            System.out.println("���˺�:");
//            orderDs.showDebug();
            String buffOrder = orderDs.isFilter()?orderDs.FILTER:orderDs.PRIMARY;
            String buffDspnm = dspnmDs.isFilter()?dspnmDs.FILTER:dspnmDs.PRIMARY;
            //ORDER���к�
            int rowCountOrder = orderDs.isFilter()?orderDs.rowCountFilter():orderDs.rowCount();
            //DSPNM���к�
            int rowCountDspnm = dspnmDs.isFilter()?dspnmDs.rowCountFilter():dspnmDs.rowCount();
            for(int i=0;i<rowCountOrder;i++){
                TParm temp = orderDs.getRowParm(i,buffOrder);
//                String startDttm = StringTool.getString(temp.getTimestamp("START_DTTM"),"yyyyMMddHHmmss");
                int orderID = (Integer)orderDs.getItemData(i,"#ID#",buffOrder);
                for(int j=0;j<rowCountDspnm;j++){
                    TParm tempDspnm = dspnmDs.getRowParm(j,buffDspnm);
                    if(temp.getValue("CASE_NO").equals(tempDspnm.getValue("CASE_NO"))&&temp.getValue("ORDER_NO").equals(tempDspnm.getValue("ORDER_NO"))&&temp.getValue("ORDER_SEQ").equals(tempDspnm.getValue("ORDER_SEQ"))){
                        int dspnmID = (Integer)dspnmDs.getItemData(j,"#ID#",buffDspnm);
                        newRowMap.put(orderID,dspnmID);
                        continue;
                    }
                }
            }
//            orderDs.setFilter(filterStr);
//            orderDs.setSort(sortStr);
//            orderDs.filter();
//            System.out.println("ORDER��Ӧ�к�:"+newRowMap);
        }
    }
    /**
     * ���MAP
     */
    public void setMap(){
//        System.out.println("���MAP(ORDER)");
        this.newRowMap.clear();
    }
}
