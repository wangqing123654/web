package jdo.odi;

import com.dongyang.data.TParm;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not WangM
 * @version 1.0
 */
public class OdiSqlObject {
    private static OdiSqlObject instanceObject;
    public OdiSqlObject(){
    }
    /**
     * ���ص�ǰʵ��
     * @return OdiSqlObject
     */
    public static synchronized OdiSqlObject getInstance(){
        if (instanceObject == null)
           instanceObject = new OdiSqlObject();
       return instanceObject;
    }
    /**
     * ����ODI_ORDER��SQL
     * @param parm TParm
     * @return String
     */
    public String creatSQL(String columnName,TParm parm){
//        System.out.println("����ODI_ORDER��SQL"+parm);
//        System.out.println("����ODI_ORDER��SQL"+parm.getCount());
        if(parm.getInt("ACTION","COUNT")<=0){
            return "";
        }
        String sql="";
        String columnNames[] = parm.getNames();
        for(int i=0;i<columnNames.length;i++){
            sql+=columnNames[i]+"='"+parm.getValue(columnNames[i])+"'";
            if(i==columnNames.length-1)
                break;
            sql+=" AND ";
        }
        return "SELECT * FROM "+columnName+" WHERE "+sql;
    }
    //$$=====add by lx 2013/01/11 Ϊ�����ʱҽ���ٶ�=====$$//
    /**
     * ��ʼ������ʱ����ʱҽ��ֻ���ص���
     * @param columnName
     * @param parm
     * @param stStartDate
     * @param stEndDate
     * @return
     */
    public String creatSQL(String columnName, TParm parm, String stStartDate, String stEndDate) {//wanglong modify 20150106
        return creatSQL(columnName, parm, stStartDate, stEndDate, false);
    }
    
    /**
     * ��ʼ������ʱ����ʱҽ��ֻ���ص���
     * @param columnName
     * @param parm
     * @param stStartDate
     * @param stEndDate
     * @param opeFlg
     * @return
     */
    public String creatSQL(String columnName,TParm parm,String stStartDate,String stEndDate,boolean opeFlg){//wanglong add 20150106
        
        if(parm.getInt("ACTION","COUNT")<=0){
            return "";
        }
        String sql="";
        String columnNames[] = parm.getNames();
        for(int i=0;i<columnNames.length;i++){
            sql+=columnNames[i]+"='"+parm.getValue(columnNames[i])+"'";
            if(i==columnNames.length-1)
                break;
            sql+=" AND ";
        }
        //��ʱ(�϶��ʱ���)
        String stSQL="SELECT * FROM "+columnName+" WHERE "+sql;
               stSQL+=" AND RX_KIND = 'ST'";
               stSQL+=" AND (ORDER_DATE>= TO_DATE ('"+stStartDate+"', 'YYYY-MM-DD HH24:MI:SS')";
               stSQL+=" AND ORDER_DATE<= TO_DATE ('"+stEndDate+"', 'YYYY-MM-DD HH24:MI:SS'))";
               
        //����ʱ(����)     
        String notSTSql="SELECT * FROM "+columnName+" WHERE "+sql;
               notSTSql+=" AND RX_KIND != 'ST'";
             
        if (opeFlg) { // ����ҽ�� wanglong add 20150106
            stSQL += " AND OPBOOK_SEQ IS NOT NULL ";
            notSTSql += " AND OPBOOK_SEQ IS NOT NULL ";
        } else {
            stSQL += " AND OPBOOK_SEQ IS NULL ";
            notSTSql += " AND OPBOOK_SEQ IS NULL ";
        }
//        System.out.println("---odi sql----"+stSQL+" UNION "+notSTSql);
        return stSQL+" UNION ALL "+notSTSql;
        
    }
}
