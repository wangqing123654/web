package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.clp.CLPManagemTool;
import com.dongyang.ui.TTable;
import java.util.regex.Pattern;
import jdo.sys.Operator;
import java.util.Map;
import com.dongyang.data.TNull;
import java.util.HashMap;
import com.dongyang.ui.TTextField;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import com.dongyang.manager.TIOM_AppServer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.*;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.DateTool;
import java.util.Date;

/**
 * <p>Title:ʵ��ʱ���趨 </p>
 *
 * <p>Description: ʵ��ʱ���趨</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPDurationConfigControl extends TControl {
    public CLPDurationConfigControl() {

    }

    private String case_no = "";
    private String clncPathCode = "";
    private String version="";//=========pangben 2012-5-28 �汾
    private String dept_code="";//=========pangben 2012-5-28 ����
    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * ҳ���ʼ������
     */
    public void onInit() {
        super.onInit();
        initTableEdit();
        initPage();
    }

    /**
     * ��ʼ��
     */
    private void initPage() {
        TParm sendParm = (TParm)this.getParameter();
        String mr_no = (String) sendParm.getData("MR_NO");
        this.setValue("MR_NO", mr_no);
        String patName = (String) sendParm.getData("PAT_NAME");
        this.setValue("PAT_NAME", patName);
        clncPathCode = (String) sendParm.getData("CLNCPATH_CODE");
        this.setValue("CLNCPATH_CODE", clncPathCode);
        case_no = (String) sendParm.getData("CASE_NO");
        version = (String) sendParm.getData("VERSION");//=========pangben 2012-5-28 
        dept_code = (String) sendParm.getData("DEPT_CODE");//=========pangben 2012-5-28 
        this.onQuery();
    }

    /**
     * ��ʼ�����༭ʱ���¼�
     */
    private void initTableEdit() {
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onEditTable");
    }

    /**
     * ���༭ʱ����Component com, int row, int column
     */
    public boolean onEditTable(Object obj) throws ParseException {
        TTableNode node = (TTableNode) obj;
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        int column = node.getColumn();
        if (column == 7 || column == 8) {
            //��֤ʱ��
            if (!validDate(node,tableParm)) {
                return true;
            }
        }
        //��ʼʱ�䲻��С����Ժʱ����֤
        if(column==7){
            if(!startDateIsValid(node)){
                return true;
            }
        }
        //��������-���ƿ�ʼ������֤
        if (column == 5) {
            if (!validStardDay(node)) {
                return true;
            }
        }
        //��������-��������
        if (column == 6) {
            if (!validSchdDay(node)) {
                return true;
            }
        }
        //ʱ��������֤
        //��֤���������Ϳ�ʼ�����Ƿ�Ϸ�
        if(column==5||column==6){
            if(!schdDayAndStardDayValid(node)){
                return true;
            }
        }
        //������������trueʱeditTable ��������false ��֮��Ȼ ,onEditTable ����
        //����true ʱ��ܻ�ѸĶ���ֵ�ָ���ԭ����ֵ��
        modifyTableData(node);
        return false;
    }
    /**
     * ��֤��ʼʱ���Ƿ�Ϸ�
     * @return boolean
     */
    private boolean startDateIsValid(TTableNode node) throws ParseException {
        boolean flag=true;
        Date time= format.parse(node.getValue()+"");
        Calendar nodeTime=Calendar.getInstance();
        nodeTime.setTimeInMillis(time.getTime());
        Calendar inDate=this.getInDate();
        if(nodeTime.getTimeInMillis()<inDate.getTimeInMillis()){
            this.messageBox("ʱ�̿�ʼʱ�䲻��С����Ժ�ռ䣡");
            flag=false;
        }
        return flag;
    }
    /**
     * ��֤���������Ƿ�Ϸ�
     * @param node TTableNode
     * @return boolean
     */
    private boolean schdDayAndStardDayValid(TTableNode node){
        int column = node.getColumn();
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        int selectedRow = node.getRow();
        String sustainedDays=tableParm.getValue("SUSTAINED_DAYS",selectedRow);
        String schdDay=tableParm.getValue("SCHD_DAY",selectedRow);
        //���¼��еõ��ı༭���ֵΪԭʼֵ����Ҫʹ��node.getValue()���ܵõ�ʵ�ʱ༭��ֵ
        if(column==5){
            sustainedDays=node.getValue()+"";
        }
        if(column==6){
             schdDay=node.getValue()+"";
        }
        if(selectedRow<tableParm.getCount()&&sustainedDays.equals(tableParm.getValue("SUSTAINED_DAYS",selectedRow+1))){
            if(!"1".equals(schdDay)){
                this.messageBox("������¼��סԺ��ʼ��������һʱ�̵�סԺ��ʼ������ͬ���ʸ�����¼��������������Ϊ1��");
                return false;
            }
        }
        if (selectedRow > 0 &&
            sustainedDays.equals(
                    tableParm.getValue("SUSTAINED_DAYS", selectedRow - 1))) {
            if (!"1".equals(tableParm.getValue("SCHD_DAY", selectedRow - 1))) {
                this.messageBox("������¼��סԺ��ʼ��������һʱ�̵�סԺ��ʼ������ͬ������һʱ�̵�������������Ϊ1��");
                return false;
            }
        }

        return true;
    }
    /**
     * ��֤ʱ��
     * @return boolean
     */
    public boolean validDate(TTableNode node,TParm tableParm) throws
            ParseException {
        if (!DateTool.checkDate(node.getValue().toString(),
                                "yyyy/MM/dd HH:mm:ss")) {
            //ʱ���ʽ����ȷ��
            this.messageBox("E0160");
            return false;
        }
        int column =  node.getColumn();
        int selectedIndx=node.getRow();
//        Date curDate=format.parse(node.getValue()+"");
//        if(column==7&&selectedIndx!=0){
//            Date beforeDate=format.parse(tableParm.getValue("END_DATE",selectedIndx-1));
//            if(curDate.getTime()<beforeDate.getTime()){
//                this.messageBox("��ʼʱ�䲻��С��ǰһʱ�̵Ľ���ʱ�䣡");
//                return false;
//            }
//        }
        Date startDate=null;
        Date endDate=null;
        if(column==7){
            startDate=format.parse(node.getValue()+"");
            endDate=format.parse(tableParm.getValue("END_DATE",selectedIndx));
        }
        if(column==8){
            startDate=format.parse(tableParm.getValue("START_DATE",selectedIndx));
            endDate=format.parse(node.getValue()+"");
        }
        //System.out.println("startDate:::::"+startDate);
        //System.out.println("endDate:::::"+endDate);
        if(startDate.getTime()>endDate.getTime()&&column==8){//ֻ�н���ʱ����и��жϣ�ʱ�䲻����ǰ������ʼʱ��֮ǰ
            this.messageBox("����ʱ�䲻��С�ڿ�ʼʱ�䣡");
            return false;
        }
//        if(column==8&&selectedIndx!=(tableParm.getCount()-1)){
//            Date afterDate = format.parse(tableParm.getValue("START_DATE",
//                    selectedIndx +1));
//            if (curDate.getTime() > afterDate.getTime()) {
//                this.messageBox("����ʱ�䲻�ܴ��ں�һʱ�̵Ŀ�ʼʱ�䣡");
//                return false;
//            }
//
//        }
        //����һʱ�̵Ŀ�ʼʱ���뱾ʱ�̵Ŀ�ʼʱ����ͬ�������ʱ�����С�ڿ�ʼʱ��+1��
        if(column==8&&selectedIndx!=(tableParm.getCount()-1)&&tableParm.getValue("SUSTAINED_DAYS",(selectedIndx+1)).equals(tableParm.getValue("SUSTAINED_DAYS",(selectedIndx)))){
            if(endDate.getTime()>(startDate.getTime()+1000*60*60*24)){
                this.messageBox("��ǰʱ�̵Ŀ�ʼʱ������һʱ�̵Ŀ�ʼʱ����ͬ����ʼʱ��ͽ���ʱ�����С��1��!");
                return false;
            }
        }
        return true;
    }
    /**
     * �����������
     */
//    private boolean modifyTableData(TTableNode node) throws ParseException {
//
////        //��������-���ƿ�ʼ������֤
////        if (column == 5) {
////            if (!validStardDay(node)) {
////                return false;
////            }
////            modifyTableStartDayAndStartData(node, tableParm, inDate);
////        }
////        //��������-��������
////        if (column == 6) {
////            if (!validSchdDay(node)) {
////                return false;
////            }
////            //������������
////            modifyTableEndDate(node, tableParm, inDate, format);
////        }
////        if(column==7||column==8){
////            //����ʱ��
//            modifyTableDataWithStartAndEndDate(node);
////        }
//        return true;
//    }

    /**
     * �õ���Ժ����
     * @return Calendar
     */
    private Calendar getInDate(){
        SimpleDateFormat inDateformat = new SimpleDateFormat("yyyy/MM/dd");
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        Calendar inDate = Calendar.getInstance();
        inDate.set(Calendar.HOUR_OF_DAY, 0);
        inDate.set(Calendar.MINUTE, 0);
        inDate.set(Calendar.SECOND, 0);
        if (tableParm.getCount() > 0) {
            if (tableParm.getRow(0).getValue("IN_DATE").length() > 0) {
                String currentDateStr = tableParm.getRow(0).getValue("IN_DATE").
                                        substring(0, 10);
                try {
                    inDate.setTime(inDateformat.parse(currentDateStr));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return inDate;
    }
    private void modifyTableData(TTableNode node
        ) throws ParseException {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        int column = node.getColumn();
        //��Ժʱ��
        Calendar inDate = getInDate();
        int selectedRow = node.getRow();
        int selectedColumn=node.getColumn();
        long difDay=0;
        long difDayTime=0;//ͬһ���ʱ�� ,��ʼʱ�� ����ʱ����ʾ�����¼
        long millisecond=0;
        if(selectedColumn==7||selectedColumn==8){
            Date oldDate=format.parse(node.getOldValue()+"");
            Date newDate=format.parse( node.getValue()+ "");
            difDay = (newDate.getTime() - oldDate.getTime()) / 1000 / 60 /
                          60 / 24;
            //=======pangben 2012-6-12 �޸Ŀ�ʼʱ���00��00��00 ����ʱ��Ϊ 23��59��59 ����Ϊ����
            difDayTime=difDay==0?1:difDay;//������������Ϊ0ʱ���1
            millisecond=(newDate.getTime() - oldDate.getTime())%(1000*60*60*24);
        }else if(selectedColumn==5||selectedColumn==6){
            int newValue=Integer.parseInt(node.getValue()+"");
            int oldValue=Integer.parseInt(node.getOldValue()+"");
            difDay=newValue-oldValue;
            difDayTime=newValue-oldValue;
        }
        for (int i = selectedRow; i < tableParm.getCount(); i++) {
//            TParm rowData = tableParm.getRow(i);
            Calendar startDay=Calendar.getInstance();
            startDay.setTimeInMillis(format.parse(tableParm.getValue("START_DATE",i)).getTime());
            Calendar endDay=Calendar.getInstance();
            endDay.setTimeInMillis(format.parse(tableParm.getValue("END_DATE",i)).getTime());
            //����ǵ�ǰ�п�ʼʱ��ͽ���ʱ��Ҫ���޸ĵ�Ϊ׼
            if(selectedRow==i&&selectedColumn==7){
               startDay.setTimeInMillis(format.parse(node.getValue()+"").getTime());
            }
            if(selectedRow==i&&selectedColumn==8){
                endDay.setTimeInMillis(format.parse(node.getValue()+"").getTime());
            }
            //����ǵ�ǰ�п�ʼʱ��ͽ���ʱ��Ҫ���޸ĵ�Ϊ׼end
            //�����ǰ����һ��ʱ�̵Ŀ�ʼ�����뱾����¼�Ŀ�ʼ������ͬ��Ϊͬһ�쿪ʼ��ʱ�̲����и��²���
            if(selectedRow==i&&selectedRow!=(tableParm.getCount()-1)
               &&(tableParm.getValue("SUSTAINED_DAYS",(i+1)).equals(tableParm.getValue("SUSTAINED_DAYS",(i)))
               &&selectedColumn==8
               &&((endDay.getTimeInMillis()-startDay.getTimeInMillis())>1000*60*60*24)
               )){
                break;
            }
            //��ʼʱ�����Ķ�������//
            if(!(selectedRow==i&&selectedColumn==7||selectedRow==i&&selectedColumn==6||selectedRow==i&&selectedColumn==8)){//���е�ǰֵ�Ķ����ò����׼�����
            	startDay.add(Calendar.DAY_OF_YEAR, (int) difDayTime);//======pangben 2012-6-26 
            	//startDay.add(Calendar.DAY_OF_YEAR, (int) difDay+1);//======pangben 2012-6-26 
                //��������ĺ���
                startDay.add(Calendar.MILLISECOND,(int)millisecond);
                tableParm.setData("START_DATE",i,format.format(startDay.getTime()));
            }
            //����ʱ�����Ķ�������||selectedRow==i&&selectedColumn==5||selectedRow==i&&selectedColumn==7
            if(!(selectedRow==i&&selectedColumn==8)){//���е�ǰֵ�Ķ����ò����׼�����
                endDay.add(Calendar.DAY_OF_YEAR, (int) difDayTime);
                //��������ĺ���
                endDay.add(Calendar.MILLISECOND,(int)millisecond);
                tableParm.setData("END_DATE",i,format.format(endDay.getTime()));
            }
            //��ʼ��������
//
            if(!(selectedRow==i&&selectedColumn==5||selectedRow==i&&selectedColumn==6||selectedRow==i&&selectedColumn==8)){//���е�ǰֵ�Ķ����ò����׼�����
                tableParm.setData("SUSTAINED_DAYS", i,
                                  tableParm.getInt("SUSTAINED_DAYS", i) +
                                  difDay);
            }
            //ʱ����������
            //ֻ�е�ǰ�вſ��Խ���ʱ�̵���
            //selectedRow==i&&selectedColumn==7||
            if(selectedRow==i&&selectedColumn==8){
                tableParm.setData("SCHD_DAY", i,
                                  tableParm.getInt("SCHD_DAY", i) + difDay);
            }
        }
        table.setParmValue(tableParm);
    }

    /**
     * ��ʼ������֤����
     * @param node TTableNode
     * @return boolean
     */
    private boolean validStardDay(TTableNode node) {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        //��ʼ������֤
        boolean flag = true;
        int currentRow = node.getRow();
        int curStartDay = Integer.parseInt(node.getValue() + "");
        if (currentRow == 0) {
            if (curStartDay <= 0) {
                flag = false;
            }
        } else {
        	//System.out.println("SUSTAINED_DAYS:::"+tableParm.getRow(currentRow - 1).
             //                                  getValue("SUSTAINED_DAYS"));
            int preStartDay = Integer.parseInt(tableParm.getRow(currentRow - 1).
                                               getValue("SUSTAINED_DAYS"));
            int preSchdDay=Integer.parseInt(tableParm.getRow(currentRow - 1).
                                               getValue("SCHD_DAY"));
            //System.out.println("preStartDay:::"+preStartDay);
            //System.out.println("preSchdDay:::"+preSchdDay);
            //System.out.println("curStartDay:::"+curStartDay);
            if (curStartDay < (preStartDay+preSchdDay-1)){
                flag = false;
            }
        }
        if (!flag) {
            this.messageBox("��ʼ�����������");
            return false;
        }
        return true;
    }

    /**
     * ��ִ֤�������Ƿ���ȷ
     * @param node TTableNode
     * @return boolean
     */
    public boolean validSchdDay(TTableNode node) {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        //��ʼ��֤
        boolean flag = true;
        int curschdDay = Integer.parseInt(node.getValue() + "");
        if (curschdDay <= 0) {
            flag = false;
        }
        if (!flag) {
            this.messageBox("���������������");
            return false;
        }
        return true;
    }

//    //��ʼ�����Ϳ�ʼ����ʱ�䴦����
//    private void modifyTableStartDayAndStartData(TTableNode node,
//                                                 TParm tableParm,
//                                                 Calendar inDate) {
//        int selectedRow = node.getRow();
//        int differPre = Integer.parseInt("" + node.getValue()) -
//                        Integer.parseInt(node.getOldValue() + "");
//        for (int i = selectedRow; i < tableParm.getCount(); i++) {
//            TParm rowData = tableParm.getRow(i);
//            //��ʼ�ڼ���ִ��
//            int susTainedDays = 0;
//            //ִ������
//            int schdDay = 0;
//            if (i == selectedRow) {
//                susTainedDays = Integer.parseInt(node.getValue() + "");
//            } else {
//                susTainedDays = Integer.parseInt(rowData.getValue(
//                        "SUSTAINED_DAYS")) + differPre;
//            }
//            schdDay = Integer.parseInt(rowData.getValue("SCHD_DAY"));
//            Calendar startDay = (Calendar) inDate.clone();
//            startDay.add(Calendar.DAY_OF_YEAR, susTainedDays);
//            Calendar endDay = (Calendar) startDay.clone();
//            endDay.add(Calendar.DAY_OF_YEAR, schdDay);
//            tableParm.setData("SUSTAINED_DAYS", i, susTainedDays);
//            //������ʼʱ��ͽ���ʱ��
//            modifyStartAndEndDate(startDay, endDay);
//            //ʵ�ʴ����ʱ����Ҫ���¼����ʱ��ȡ������Ȼ�����ԭ��ʱ���Time
//            tableParm.setData("START_DATE", i,getStartDayStrWithOldTime(startDay,tableParm.getValue("START_DATE",i)));
//            tableParm.setData("END_DATE", i,getEndDayStrWithOldTime(endDay,tableParm.getValue("END_DATE",i)));
//        }
//
//    }
    /**
     * �õ���ʼʱ�����ԭ�е�time
     * @param startDay Calendar
     * @param oldStartDay String
     * @return String
     */
    private String getStartDayStrWithOldTime(Calendar startDay,String oldStartDay){
        //���봦�� begin
        //д���ʱ����ԭ����ʱ�������£�ʱ���ǲ��ܱ仯�ģ��������ڵı䶯
        //�µ�ʱ����õ��������
        String newStartDay = format.format(startDay.getTime());
        newStartDay = newStartDay.substring(0, 11);
        //��ԭ�е�ʱ�����
        newStartDay += oldStartDay.length() >= 19 ?
                oldStartDay.substring(11, 19) : "00:00:00";
        //���봦��end
        return newStartDay;
    }
//    /**
//     * �õ�����ʱ�����ԭ�е�time
//     * @param startDay Calendar
//     * @param oldEndDay String
//     * @return String
//     */
//    private String getEndDayStrWithOldTime(Calendar endDay,String oldEndDay){
//    //���봦�� begin
//    //д���ʱ����ԭ����ʱ�������£�ʱ���ǲ��ܱ仯�ģ��������ڵı䶯
//    //�µ�ʱ����õ��������
//    String newEndDay = format.format(endDay.getTime());
//    newEndDay = newEndDay.substring(0, 11);
//    //��ԭ�е�ʱ�����
//    newEndDay += oldEndDay.length() >= 19 ?
//            oldEndDay.substring(11, 19) : "23:59:59";
//    //���봦��end
//    return newEndDay;
//}

//    /**
//     * ����ʱ��������
//     * @param node TTableNode
//     * @param tableParm TParm
//     * @param inDate Calendar
//     * @param format SimpleDateFormat
//     */
//    private void modifyTableEndDate(TTableNode node,
//                                    TParm tableParm,
//                                    Calendar inDate,
//                                    SimpleDateFormat format
//            ) {
//        int selectedRow = node.getRow();
//        int differPre = Integer.parseInt("" + node.getValue()) -
//                        Integer.parseInt(node.getOldValue() + "");
//        for (int i = selectedRow; i < tableParm.getCount(); i++) {
//            TParm rowData = tableParm.getRow(i);
//            int susTainedDays = 0;
//            int schdDay = 0;
//            //���������� begin
//            if (i == selectedRow) {
//                susTainedDays = Integer.parseInt(rowData.getValue(
//                        "SUSTAINED_DAYS"));
//                schdDay = Integer.parseInt(node.getValue() + "");
//            } else {
//                susTainedDays = Integer.parseInt(rowData.getValue(
//                        "SUSTAINED_DAYS")) + differPre;
//                schdDay = Integer.parseInt(rowData.getValue(
//                        "SCHD_DAY"));
//            }
//            //���������� end
//            Calendar startDay = (Calendar) inDate.clone();
//            startDay.add(Calendar.DAY_OF_YEAR, susTainedDays);
//            Calendar endDay = (Calendar) startDay.clone();
//            endDay.add(Calendar.DAY_OF_YEAR, schdDay);
//            tableParm.setData("SUSTAINED_DAYS", i, susTainedDays);
//            //������ʼʱ��ͽ���ʱ��
//            modifyStartAndEndDate(startDay, endDay);
////            tableParm.setData("START_DATE", i, format.format(startDay.getTime()));
////            tableParm.setData("END_DATE", i, format.format(endDay.getTime()));
//            //ʵ�ʴ����ʱ����Ҫ���¼����ʱ��ȡ������Ȼ�����ԭ��ʱ���Time
//            tableParm.setData("START_DATE", i,
//                              getStartDayStrWithOldTime(startDay,
//                    tableParm.getValue("START_DATE", i)));
//            tableParm.setData("END_DATE", i,
//                              getEndDayStrWithOldTime(endDay,
//                    tableParm.getValue("END_DATE", i)));
//
//        }
//
//    }

//    /**
//     * ������ʼʱ��ͽ���ʱ��
//     * @param startDay Calendar
//     * @param endDay Calendar
//     */
//    private void modifyStartAndEndDate(Calendar startDay, Calendar endDay) {
//        //ʵ�ʿ�ʼʱ��ͽ���ʱ����20110101 �����Ŀ�ʼʱ��Ϊ20110101 ����ʹ��ʼʱ��+סԺ�ڼ��� 20110629 modify
//        //ӦΪ��ʼʱ��+סԺ�ڼ���-1 �����������  20110629 modify
//        startDay.add(Calendar.DAY_OF_YEAR, -1);
//        endDay.add(Calendar.DAY_OF_YEAR, -1);
//        //���һ��ļ��㹫ʽΪ ��ʼʱ��+ִ������-1 ����Ҫ������ʱ��-1
//        endDay.add(Calendar.DAY_OF_YEAR, -1);
//        //����ʱ��һ����23��59��59 ����
//        endDay.add(Calendar.HOUR_OF_DAY, 24);
//        endDay.add(Calendar.SECOND, -1);
//        //����ʱ��һ����23��59��59 ����end
//        //end
//    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm result = getSelectDuringInfo();
        this.callFunction("UI|TABLE|setParmValue", result);
    }

    /**
     * ����case_no and clncPathCode ��ѯduration
     * @return TParm
     */
    private TParm getSelectDuringInfo() {
        TParm selectParm = new TParm();
        selectParm.setData("CASE_NO", case_no);
        selectParm.setData("CLNCPATH_CODE", clncPathCode);
        selectParm.setData("REGION_CODE",
                           this.getBasicOperatorMap().get("REGION_CODE"));
        TParm result = CLPManagemTool.getInstance().selectDurationData(
                selectParm);
        return result;
    }

    /**
     * ����
     */
    public void onSave() {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm parm = table.getParmValue();
        if (!validSaveData()) {
            return;
        }
        TParm saveParm = new TParm();
        saveParm.setData("tableParm", parm.getData());
        saveParm.setData("basicMap", this.getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagemAction",
                "setDuring", saveParm);
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");
            this.setReturnValue("FAILURE");
        } else {
            this.messageBox("P0001");
            this.setReturnValue("SUCCESS");
        }
        this.onQuery();

    }

    /**
     * ��֤��ѯ��Ϣ
     * @return boolean
     */
    public boolean validSaveData() {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm parm = table.getParmValue();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm rowParm = parm.getRow(i); //4567
            String SCHD_DAY = rowParm.getValue("SCHD_DAY");
            String SUSTAINED_DAYS = rowParm.getValue("SUSTAINED_DAYS");
            if (!this.validNumber(SUSTAINED_DAYS)) {
                this.messageBox("������Ϸ�סԺ����");
                return false;
            }
            //¬�� 2011-07-20 modify,������֤begin
            int sustainedDays = rowParm.getInt("SUSTAINED_DAYS");
            if (sustainedDays <= 0) {
                this.messageBox("��ʼ�����������");
                return false;
            }
            //¬�� 2011-07-20 modify ������֤end
            if (!this.validNumber(SCHD_DAY)) {
                this.messageBox("������Ϸ���ʼ����");
                return false;
            }
            //¬�� 2011-07-20 �޸ģ�������֤ begin
            int schdDay=rowParm.getInt("SCHD_DAY");
            if(schdDay<=0){
                this.messageBox("���������������");
                return false;
            }
            //¬�� 2011-07-20 �޸�,������֤  end
        }
        return true;
    }

    /**
     * չ��ʱ��
     */
    public void openDuraction() {
        TParm parm = getSelectDuringInfo();
        TParm saveParm = new TParm();
        saveParm.setData("tableParm", parm.getData());
        saveParm.setData("basicMap", this.getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagemAction",
                "openDuraction", saveParm);
        if (result.getErrCode() < 0) {
            this.messageBox("չ��ʧ��");
        } else {
            this.messageBox("չ���ɹ�");
        }
    }

    /**
     * չ��ʵ��
     */
    public void openPracticeDuration() {
        TParm inputParm = new TParm();
        TParm patientTParm = this.getPatientParm();
        inputParm.setData("patientTParm", patientTParm.getData());
        inputParm.setData("deployDate", this.getDeployDate());
        inputParm.setData("operator", getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagedAction",
                "deployPractice", inputParm);
        if (result.getErrCode() < 0) {
            this.messageBox("չ��ʧ��");
        } else {
            this.messageBox("չ���ɹ�");
        }
    }

    /**
     * �õ�Ҫչ���Ĳ�����Ϣ
     * @return TParm
     */
    private TParm getPatientParm() {
        TParm patientTParm = new TParm();
        patientTParm.addData("REGION_CODE",
                             this.getBasicOperatorMap().get("REGION_CODE"));
        patientTParm.addData("CASE_NO", this.case_no);
        patientTParm.addData("CLNCPATH_CODE", this.clncPathCode);
      //======pangben 2012-5-28 start
        patientTParm.addData("VERSION", version);
        patientTParm.addData("DEPT_CODE", dept_code);
      //======pangben 2012-5-28 stop
        patientTParm.setCount(1);
        return patientTParm;
    }

    /**
     * �õ�չ��ʱ��
     * @return String
     */
    private String getDeployDate() {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        String datestrtmp = this.getValueString("deployDate");
        if (!this.checkNullAndEmpty(datestrtmp)) {
            datestrtmp = datestr;
        } else {
            datestrtmp = datestrtmp.substring(0, 10).replace("-", "");
        }
        return datestrtmp;
    }

    /**
     * ���ÿؼ��Ƿ���÷���
     * @param flag boolean
     */
    private void setPrimaryKeyEnabled(boolean flag) {
        TTextField tTextField = (TTextField)this.getComponent("TYPE_CODE");
        tTextField.setEnabled(flag);
    }

    /**
     * ��ҳ��õ���ѯ��������
     */
    private TParm getSelectedCondition() {
        TParm selectedCondition = new TParm();
        putParamLikeWithObjName("TYPE_CODE", selectedCondition);
        putParamLikeWithObjName("PY2", selectedCondition);
        putParamLikeWithObjName("TYPE_CHN_DESC", selectedCondition);
        putParamLikeWithObjName("TYPE_ENG_DESC", selectedCondition);
        putParamWithObjNameForQuery("CLASS_FLG", selectedCondition);
        putParamLikeWithObjName("DESCRIPTION", selectedCondition);
        putParamLikeWithObjName("SEQ", selectedCondition);
        selectedCondition.setData("REGION_CODE", Operator.getRegion());
        return selectedCondition;
    }

    /**
     * �����Ķ�Ӧ��Ԫ�����óɿ�д�����������óɲ���д
     * @param tableName String
     * @param rowNum int
     * @param columnNum int
     */
    private void setTableEnabled(String tableName, int rowNum, int columnNum) {
        TTable table = (TTable)this.getComponent(tableName);
        int totalColumnMaxLength = table.getColumnCount();
        int totalRowMaxLength = table.getRowCount();
        //����
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
        table.setLockColumns(lockColumnStr);
        //����
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
        if (lockRowStr.length() > 0) {
            table.setLockRows(lockRowStr);
        }

    }

    /**
     * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
     * @param objName String
     */
    private void putParamWithObjName(String objName, TParm parm,
                                     String paramName) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        parm.setData(paramName, objstr);
    }

    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        //����ֵ��ؼ�����ͬ
        parm.setData(objName, objstr);
    }

    /**
     * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
     * @param objName String
     */
    private void putParamLikeWithObjName(String objName, TParm parm,
                                         String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr + "%";
            parm.setData(paramName, objstr);
        }

    }

    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamLikeWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr.trim() + "%";
            //����ֵ��ؼ�����ͬ
            parm.setData(objName, objstr);
        }
    }

    /**
     * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm) {
        putParamWithObjNameForQuery(objName, parm, objName);
    }

    /**
     * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
     * @param objName String
     * @param parm TParm
     * @param paramName String
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm,
                                             String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            //����ֵ��ؼ�����ͬ
            parm.setData(paramName, objstr.trim());
        }
    }

    /**
     * ���ؼ��Ƿ�Ϊ��
     * @param componentName String
     * @return boolean
     */
    private boolean checkComponentNullOrEmpty(String componentName) {
        if (componentName == null || "".equals(componentName)) {
            return false;
        }
        String valueStr = this.getValueString(componentName);
        if (valueStr == null || "".equals(valueStr)) {
            return false;
        }
        return true;
    }

    /**
     * �õ�ָ��table��ѡ����
     * @param tableName String
     * @return int
     */
    private int getSelectedRow(String tableName) {
        int selectedIndex = -1;
        if (tableName == null || tableName.length() <= 0) {
            return -1;
        }
        Object componentObj = this.getComponent(tableName);
        if (!(componentObj instanceof TTable)) {
            return -1;
        }
        TTable table = (TTable) componentObj;
        selectedIndex = table.getSelectedRow();
        return selectedIndex;
    }

    /**
     * ������֤����
     * @param validData String
     * @return boolean
     */
    private boolean validNumber(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,}");
        Matcher match = p.matcher(validData);
        if (!match.matches()) {
            return false;
        }
        return true;
    }

    /**
     * ��TParm�м���ϵͳĬ����Ϣ
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM",  Operator.getIP());
    }

    /**
     * ����Operator�õ�map
     * @return Map
     */
    private Map getBasicOperatorMap() {
        Map map = new HashMap();
        map.put("REGION_CODE", Operator.getRegion());
        map.put("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        map.put("OPT_DATE", datestr);
        map.put("OPT_TERM", Operator.getIP());
        return map;
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @param dataFormatStr String
     * @return String
     */
    private String getCurrentDateStr(String dataFormatStr) {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, dataFormatStr);
        return datestr;
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @return String
     */
    private String getCurrentDateStr() {
        return getCurrentDateStr("yyyyMMdd");
    }

    /**
     * ����Ƿ�Ϊ�ջ�մ�
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }

    /**
     * ����TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from, TParm to, int row) {
        for (int i = 0; i < from.getNames().length; i++) {
            to.addData(from.getNames()[i],
                       from.getValue(from.getNames()[i], row));
        }
    }

    /**
     * ��¡����
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getValue(from.getNames()[i]));
        }
        return returnTParm;
    }

    /**
     * ����TParm ���null�ķ���
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNullVector(TParm parm, String keyStr, Class type) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData(keyStr, i) == null) {
                TNull tnull = new TNull(type);
                parm.setData(keyStr, i, tnull);
            }
        }
    }

    /**
     * ����TParm ��nullֵ����
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNull(TParm parm, String keyStr, Class type) {
        if (parm.getData(keyStr) == null) {
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }

}
