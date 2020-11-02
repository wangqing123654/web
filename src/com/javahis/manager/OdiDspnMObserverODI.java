package com.javahis.manager;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

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
public class OdiDspnMObserverODI
    extends TObserverAdapter {
    /**
     * ���������й�ϵ�б�
     */
    Map newRowMap = new HashMap();
    /**
     * ����һ������
     * @param ds TDS
     * @param row int
     * @return int
     */
    public int insertRow(TDS dspdmDs, int row) {
        String type = dspdmDs.getAttributeString("RX_KIND");
        List list = new ArrayList();
        //��ʱ
        if ("ST".equals(type)) {
            //�õ�DSPNM����������DATASTORE�е�ΨһID
//            System.out.println("DSPNM����������"+row);
            int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
//            System.out.println("DSPNMDataStore����"+dspnmNewRowD);
            //��DSPNM�в���һ������Ϊ��������
//            System.out.println("D������=====================");
            int newRow = this.getDS().insertRow();
            int newRowId = (Integer)this.getDS().getItemData(newRow,"#ID#");
            list.add(newRowId);
            //DSPNM�������к�DSPND�������ж��չ�ϵ
            newRowMap.put(dspnmNewRowD,list);
//            this.getDS().setItem(newRow, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));(0907)
            //���ö���֮�����еĶ�Ӧ��ϵ
            //������
            this.getDS().setActive(newRow,false);
//            System.out.println("DSPND������=========");
//            this.getDS().showDebug();
            return newRow;
        }
        //����
        if ("UD".equals(type)) {
            //�õ�DSPNM����������DATASTORE�е�ΨһID
            int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
            //��DSPNM�в���һ������Ϊ��������
            int newRow = this.getDS().insertRow();
            list.add(newRow);
            //DSPNM�������к�DSPND�������ж��չ�ϵ
            newRowMap.put(dspnmNewRowD,list);
            this.getDS().setItem(newRow, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));
            //���ö���֮�����еĶ�Ӧ��ϵ
            //������
            this.getDS().setActive(newRow,false);
//            System.out.println("DSPND������=========");
//            this.getDS().showDebug();
            return newRow;
        }
        //��Ժ��ҩ
        if ("DS".equals(type)) {
            //�õ�DSPNM����������DATASTORE�е�ΨһID
            int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
            //��DSPNM�в���һ������Ϊ��������
            int newRow = this.getDS().insertRow();
            list.add(newRow);
            //DSPNM�������к�DSPND�������ж��չ�ϵ
            newRowMap.put(dspnmNewRowD,list);
            this.getDS().setItem(newRow, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));
            //���ö���֮�����еĶ�Ӧ��ϵ
            //������
            this.getDS().setActive(newRow,false);
//            System.out.println("DSPND������=========");
//            this.getDS().showDebug();
            return newRow;
        }
        //��ҩ��Ƭ
        if ("IG".equals(type)) {
            //�õ�DSPNM����������DATASTORE�е�ΨһID
            int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
            //��DSPNM�в���һ������Ϊ��������
            int newRow = this.getDS().insertRow();
            list.add(newRow);
            //DSPNM�������к�DSPND�������ж��չ�ϵ
            newRowMap.put(dspnmNewRowD,list);
            this.getDS().setItem(newRow, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));
            //���ö���֮�����еĶ�Ӧ��ϵ
            //������
            this.getDS().setActive(newRow,false);
//            System.out.println("DSPND������=========");
//            this.getDS().showDebug();
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
    public boolean setItem(TDS dspdmDs, int row, String column, Object value) {

        //ҽ�����
        String type = dspdmDs.getAttributeString("RX_KIND");
//        System.out.println("ҽ�����M��:"+type);
        //�Ƿ񱾶���Ҫ������ֵ(Y��ʾ��ҪN��ʾ����Ҫ��ֹ�ݹ�)
        if(!(Boolean)dspdmDs.getAttribute("CHANGE_FLG"))
            return false;
        String buff = dspdmDs.isFilter()?dspdmDs.FILTER:dspdmDs.PRIMARY;
        //����ʱҽ����ʱ��
        if("ST".equals(type)){
//            System.out.println("D����:" + newRowMap);
            //����ҽ�����;������
            if ("DSPN_KIND".equals(column) || "CASE_NO".equals(column)) {
//                System.out.println("M��column"+column);
                if ("DSPN_KIND".equals(column)) {
                    if (buff.equals(dspdmDs.PRIMARY))
                        return true;
                    else
                        return false;
                }
                if ("CASE_NO".equals(column)) {
                    //��ʼ��ORDER���ݵ�ȫ���ж�Ӧ��
                    if (newRowMap.isEmpty()) {
                        createDataStoreTable(type, dspdmDs, this.getDS());
                    }
                }
//                dspdmDs.setAttribute("CHANGE_FLG", false);
//                dspdmDs.setItem(row, column, value);
//                dspdmDs.setAttribute("CHANGE_FLG", true);
                if (buff.equals(dspdmDs.PRIMARY))
                    return true;
                else
                    return false;
            }
//            else {
//                int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
//                List rowList = (List) newRowMap.get(dspnmNewRowD);
//               System.out.println("rowList��ֵ��Ӧ��:"+rowList);
//                for (int i = 0; i < rowList.size(); i++) {
//                    int r = Integer.parseInt(rowList.get(i).toString());
//                    int rSetId = getDspnDSet(r, this.getDS());
//                    this.getDS().setItem(rSetId, column, value);
//                    this.getDS().setActive(rSetId, true);
//                }
//                if (buff.equals(dspdmDs.PRIMARY))
//                    return true;
//                else
//                    return false;
//            }
            if(value instanceof TParm){
                int dsRow = (Integer) dspdmDs.getItemData(row, "#ID#");
                List rowList = (List) newRowMap.get(dsRow);
                TParm parm = (TParm) value;
                //�õ���
                String columnList[] = dspdmDs.getColumns();
                String columnListD[] = this.getDS().getColumns();
                for (String temp : columnList) {
                    if (!"LINK_NO".equals(temp)&&parm.getValue(temp).length() == 0)
                        continue;
                    if ("DSPN_KIND".equals(temp) || "CASE_NO".equals(temp))
                        continue;
                    dspdmDs.setAttribute("CHANGE_FLG", false);
                    dspdmDs.setItem(row, temp, parm.getData(temp));
                    dspdmDs.setAttribute("CHANGE_FLG", true);
                }
                for(String temp:columnListD){
                    if (!"LINK_NO".equals(temp)&&parm.getValue(temp).length() == 0)
                        continue;
                    if ("DSPN_KIND".equals(temp) || "CASE_NO".equals(temp))
                        continue;
                    //��ѯ��DSPNM�е�λ��
                    for (int i = 0; i < rowList.size(); i++) {
                        int r = Integer.parseInt(rowList.get(i).toString());
                        int rSetId = getDspnDSet(r, this.getDS());
                        if(rSetId<0){
                            break;
                        }
                        this.getDS().setItem(rSetId, temp, parm.getData(temp));
                        this.getDS().setActive(rSetId, true);
                    }
                }
                if (buff.equals(dspdmDs.PRIMARY))
                    return true;
                else
                    return false;
            }
        }
//        if(!newRowMap.isEmpty()){
//            //�õ�DSPNM������DATASTORE��ΨһID��
//            int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
//            List rowList = (List) newRowMap.get(dspnmNewRowD);
////            int dspndNewRowD = (Integer) newRowMap.get(dspnmNewRowD);
////             int dspndNewRowD = StringTool.getInt(""+rowList.get(0));
////            this.getDS().setItem(dspndNewRowD, column, value);
//            if (value instanceof TParm && column.length() == 0) {
////                this.getDS().setActive(dspndNewRowD, true);
//                //ת����TPARM
//                TParm parm = (TParm) value;
//                parm.setData("CASE_NO",dspdmDs.getAttribute("CASE_NO"));
//                //�õ���ǰODI_ORDER����Ҫ���õ���������һ������
//                String columnList[] = this.getDS().getColumns();
//                //ѭ����ÿ���и�ֵ(���ݲ�ͬ���������ò�ͬ�ļ��㷽ʽ����SYS_FEE�ж�ӦODI_ORDER�е�������ת��)
//                Object obj = parm.getData("START_DTTM_LIST");
//                if (obj != null) {
//                    List dspndList = (List) parm.getData("START_DTTM_LIST");
//                    int listSize = dspndList.size();
//                    //ɾ��
//                    for (int i = 0; i < rowList.size(); i++) {
//                        this.getDS().deleteRow(StringTool.getInt("" + rowList.get(i)));
//                        //�����ж���
//                        List temps = new ArrayList();
//                        newRowMap.put(dspnmNewRowD, temps);
//                    }
//                    //����
//                    List tempRowList = new ArrayList();
//                    for (int i = 0; i < listSize; i++) {
//                        int rowNew = this.getDS().insertRow();
//                        tempRowList.add(rowNew);
//                        newRowMap.put(dspnmNewRowD, tempRowList);
//                        for (String temp : columnList) {
//                            if (parm.getData(temp) == null) {
//                                continue;
//                            }
//                            if("START_DTTM".equals(temp)){
//                                parm.setData(temp,dspndList.get(i));
//                            }
//                            dspdmDs.setAttribute("CHANGE_FLG",false);
//                            this.getDS().setItem(rowNew, temp,parm.getData(temp));
//                        }
//                    }
//                }else{
//                    for (int i = 0; i < rowList.size(); i++) {
//                        int rowModif = StringTool.getInt("" + rowList.get(i));
//                        this.getDS().setItem(rowModif, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));
//                        this.getDS().setActive(rowModif, true);
//                        for (String temp : columnList) {
//                            if (parm.getData(temp) == null) {
//                                continue;
//                            }
//                            dspdmDs.setAttribute("CHANGE_FLG",false);
//                            this.getDS().setItem(rowModif, temp,parm.getData(temp));
//                        }
//                    }
//                }
//            }else {
//                System.out.println("====================================================");
//                for(int i=0;i<rowList.size();i++){
//                    int rowModif = StringTool.getInt(""+rowList.get(i));
//                    this.getDS().setItem(rowModif, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));
//                    this.getDS().setActive(rowModif, true);
//                    this.getDS().setItem(rowModif, column, value);
//                }
//            }
//        }
        if (buff.equals(dspdmDs.PRIMARY))
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
    public int getDspnDSet(int id,TDS dspndDs){
        String buff = dspndDs.isFilter()?dspndDs.FILTER:dspndDs.PRIMARY;
        int rowCount = dspndDs.rowCount();
        int result = -1;
        for(int i=0;i<rowCount;i++){
            int setId = (Integer)dspndDs.getItemData(i,"#ID#",buff);
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
    public boolean deleteRow(TDS dspnmDs, int row) {
        //ҽ������
       String type = dspnmDs.getAttributeString("RX_KIND");
//       System.out.println("ҽ������"+type);
       String buff = dspnmDs.isFilter()?dspnmDs.FILTER:dspnmDs.PRIMARY;
       int delRowdspnm = dspnmDs.getAttributeInt("DELROW");
       if("ST".equals(type)){
           List delList = (List)newRowMap.get(delRowdspnm);
            for(int i=0;i<delList.size();i++){
                //��ѯ��DSPNM�е�λ��
                int dspnmRowIdSet = getDspnDSet(Integer.parseInt(delList.get(i).toString()), this.getDS());
                if (dspnmRowIdSet < 0) {
                    if (buff.equals(dspnmDs.PRIMARY))
                        return true;
                    else
                        return false;
                }
                this.getDS().deleteRow(dspnmRowIdSet);
            }
           newRowMap.remove(delRowdspnm);
           if (buff.equals(dspnmDs.PRIMARY))
               return true;
           else
               return false;
       }

//        //�������е�ID
//        Object obj = dspnmDs.getItemData(row, "#ID#");
////        System.out.println("MObjcֵ"+obj);
//        if (obj != null) {
//            int dspndNewRow = (Integer) obj;
//            TParm parm = this.getDS().getRowParm((Integer)newRowMap.get(dspndNewRow));
////            System.out.println("Dɾ������"+parm);
//            List delList = (List)newRowMap.get(dspndNewRow);
//            for(int i=0;i<delList.size();i++){
//                this.getDS().deleteRow(StringTool.getInt(""+delList.get(i)));
//            }
//            newRowMap.remove(dspndNewRow);
//            return true;
//        }
//        this.getDS().deleteRow(row);
       if (buff.equals(dspnmDs.PRIMARY))
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
//        System.out.println("�õ�δ֪��ֵ");
//        ds.showDebug();
//        System.out.println("����"+parm);
//        System.out.println("��"+row);
//        System.out.println("��"+column);
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
//        System.out.println("����δ֪��ֵ");
//        ds.showDebug();
//        System.out.println("����"+parm);
//        System.out.println("�к�"+row);
//        System.out.println("��"+column);
//        System.out.println("ֵ"+value);
        return false;
    }
    /**
    * ��ʼ������͹۲����ж�Ӧ
    * @param type String
    * @param orderDs TDS
    * @param dspnmDs TDS
    */
   public void createDataStoreTable(String type,TDS dspnmDs,TDS dspndDs){
       if("ST".equals(type)){
           String buffDspnm = dspnmDs.isFilter()?dspnmDs.FILTER:dspnmDs.PRIMARY;
           String buffDspnd = dspndDs.isFilter()?dspndDs.FILTER:dspndDs.PRIMARY;
           //DSPNM���к�
           int rowCountDspnm = dspnmDs.isFilter()?dspnmDs.rowCountFilter():dspnmDs.rowCount();
           //DSPND���к�
           int rowCountDspnd = dspndDs.isFilter()?dspndDs.rowCountFilter():dspndDs.rowCount();
           for(int i=0;i<rowCountDspnm;i++){
               TParm temp = dspnmDs.getRowParm(i,buffDspnm);
               String orderDate = StringTool.getString(temp.getTimestamp("ORDER_DATE"),"yyyyMMddHHmmss");
               int dspnmID = (Integer)dspnmDs.getItemData(i,"#ID#",buffDspnm);
               List listM = new ArrayList();
               for(int j=0;j<rowCountDspnd;j++){
                   TParm tempDspnd = dspndDs.getRowParm(j,buffDspnd);
                   if(temp.getValue("CASE_NO").equals(tempDspnd.getValue("CASE_NO"))&&temp.getValue("ORDER_NO").equals(tempDspnd.getValue("ORDER_NO"))&&temp.getValue("ORDER_SEQ").equals(tempDspnd.getValue("ORDER_SEQ"))&&orderDate.equals(tempDspnd.getValue("ORDER_DATE"))){
                       int dspndID = (Integer)dspndDs.getItemData(j,"#ID#",buffDspnd);
                       listM.add(dspndID);
                       newRowMap.put(dspnmID,listM);
                       continue;
                   }
               }
           }
//           System.out.println("M��Ӧ�к�:"+newRowMap);
       }
   }
   /**
    * ���MAP
    */
   public void setMap(){
//       System.out.println("���MAP(DSPNM)");
       this.newRowMap.clear();
   }
}
