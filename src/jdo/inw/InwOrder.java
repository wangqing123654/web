package jdo.inw;

import com.dongyang.jdo.TDataStore;
import java.sql.Timestamp;
import java.util.Date;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

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
public class InwOrder extends TDataStore{

    //��Ҫִ�е�SQL
    private String exeSQL = "";
    //��ǰʱ��
    Date nowTime;

    public InwOrder() {
    }

    /**
     * ��ѯ
     * @param CaseNo String
     * @return boolean
     */
    public boolean onQuery() {
        //����Ҫִ�е�SQL
        if (!setSQL(getExeSQL()))
            return false;
        //ִ��
        if (retrieve() == -1)
            return false;
        return true;
    }

    public boolean onSave(){
        //�õ���ǰʱ��
        nowTime=SystemTool.getInstance().getDate();
        setCheck();
        setOperator();

        if(!this.update()){
            return false;
        }
        return true;
    }

    /**
     * ������˻�ʿ�����ʱ��
     * @return boolean
     */
    public boolean setCheck() {

        String storeName = isFilter() ? FILTER : PRIMARY;
        int rows[] = getModifiedRows(storeName);
        for (int i = 0; i < rows.length; i++) {
            setItem(rows[i], "NS_CHECK_CODE", Operator.getID(), storeName);
            setItem(rows[i], "NS_CHECK_DATE", nowTime, storeName);
        }
        return true;
    }


    /**
     * ���ò����û�
     * @param optUser String �����û�
     * @param optDate Timestamp ����ʱ��
     * @param optTerm String ����IP
     * @return boolean true �ɹ� false ʧ��
     */
    public boolean setOperator() {

        String storeName = isFilter() ? FILTER : PRIMARY;
        int rows[] = getModifiedRows(storeName);
        for (int i = 0; i < rows.length; i++) {
            setItem(rows[i], "OPT_USER", Operator.getID(), storeName);
            setItem(rows[i], "OPT_DATE", nowTime, storeName);
            setItem(rows[i], "OPT_TERM", Operator.getIP(), storeName);
        }
        return true;
    }



    public void setExeSQL(String sql){
        this.exeSQL=sql;
    }
    public String getExeSQL(){
        return this.exeSQL;
    }
    /**
     * �õ�����������
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm,int row,String column)
    {
        //������������ݿ��б���û�е�ʵ����
        if("NS_CHECK_DATE_DAY".equals(column))
        {
            Timestamp date = parm.getTimestamp("NS_CHECK_DATE",row);
            if(date == null)
                return "";
            //�������һ��ֵ
            return StringTool.getString(date,"yyyy/MM/dd");
        }
        if("NS_CHECK_DATE_TIME".equals(column))
        {
            Timestamp date = parm.getTimestamp("NS_CHECK_DATE",row);
            if(date == null)
                return "";
            return StringTool.getString(date,"HH:mm:ss");
        }
        return "";
    }
    /**
     * ��������������
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setOtherColumnValue(TParm parm,int row,String column,Object value)
    {
        if("NS_CHECK_DATE_DAY".equals(column))
        {
            Timestamp date = parm.getTimestamp("NS_CHECK_DATE",row);
            if(date == null)
                setItem(row,"NS_CHECK_DATE",StringTool.getTimestamp(value.toString(),"yyyy/MM/dd"));
            return true;
        }
        if("NS_CHECK_DATE_TIME".equals(column))
        {
            /*Timestamp date = parm.getTimestamp("NS_CHECK_DATE",row);
            if(date == null)
                return "";
            return StringTool.getString(date,"HH:mm:ss");*/
        }
//        System.out.println(row + " " + column + " " + value);
        return true;
    }

}
