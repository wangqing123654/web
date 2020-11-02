package action.sum;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.ibs.IBSTool;
import jdo.sum.SUMNewArrivalTool;
import jdo.sum.SUMVitalSignTool;

/**
 * <p>Title: 新生儿体温单</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class SUMNewArrivalAction
    extends TAction {
    public SUMNewArrivalAction() {
    }


    /**
     * 保存入口
     * @param parm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
        TParm masterParm= parm.getParm("MASET");
        TParm detailParm= parm.getParm("DETAIL");
        boolean insertFlg=parm.getBoolean("I");

        TParm result = new TParm();
        //创建一个连接，在多事物的时候连接各个操作使用
        TConnection connection = getConnection();
        if(insertFlg){//插入
            result = SUMNewArrivalTool.getInstance().insertNewArrival(
                masterParm, connection);
            if (result.getErrCode() < 0) {
                System.out.println(result.getErrText());
                connection.close();
                return result;
            }
            result = SUMNewArrivalTool.getInstance().insertNewArrivalDtl(
                detailParm, connection);
            if (result.getErrCode() < 0) {
                System.out.println(result.getErrText());
                connection.close();
                return result;
            }
        }else{//更新
            result = SUMNewArrivalTool.getInstance().updateNewArrival(
                masterParm, connection);
            if (result.getErrCode() < 0) {
                System.out.println(result.getErrText());
                connection.close();
                return result;
            }
            result = SUMNewArrivalTool.getInstance().updateNewArrivalDtl(
                detailParm, connection);
            if (result.getErrCode() < 0) {
                System.out.println(result.getErrText());
                connection.close();
                return result;
            }

        }
//        System.out.println("detailParm.count:"+detailParm.getCount());
        if(detailParm.getCount() > 0){
            TParm parmWeight = new TParm();
            int row = detailParm.getCount() - 1;
            parmWeight.setData("CASE_NO",((TParm)detailParm.getParm(row + "PARM")).getData("CASE_NO"));
            
            //add by yangjj 20150319
            double weight = 0.000;
            for(int i = row ; i >= 0 ; i--){
            	weight = Double.parseDouble(((TParm)detailParm.getParm(i + "PARM")).getData("WEIGHT")+"")/1000.000;
            	if(weight != 0.0){
            		break;
            	}
            }
            
            
            parmWeight.setData("WEIGHT",weight);
            parmWeight.setData("HEIGHT","");
            //更新ADM_INP的身高体重
            //modify by yangjj 20150320 如果体重为0.0的时候不更新数据库
            if(weight != 0.0){
            	result = SUMVitalSignTool.getInstance().updateHeightAndWeight(
                        parmWeight, connection);
            }
            
            if (result.getErrCode() < 0) {
                System.out.println(result.getErrText());
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

}
