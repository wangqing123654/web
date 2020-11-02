package jdo.sys;

import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * wuxy 2017/1/19
 * @author his
 *
 */
public class SYSPositonTool extends TJDOTool{
	 /**
     * 实例
     */
    private static SYSPositonTool instanceObject;
    /**
     * 得到实例
     * @return PatTool
     */
    public static SYSPositonTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSPositonTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SYSPositonTool() {
        setModuleName("sys\\SYSPositionModule.x");
        onInit();
    }
    
    public Map getPosCodeMap(){
    	TParm parm = getPosCodeParm();
    	Map map = new HashMap();
    	for(int i=0 ; i < parm.getCount("POS_CODE"); i ++) {
    		map.put(parm.getData("POS_CODE", i), parm.getData("POS_CHN_DESC", i));
    	}
    	return map;
    }
    
    /**
     * parm
     * @return
     */
    public TParm getPosCodeParm(){
    	TParm parm = new TParm();
    	String sql= "SELECT POS_CODE, POS_CHN_DESC FROM SYS_POSITION";
    	parm = new TParm( TJDODBTool.getInstance().select(sql));
    	return parm;
    }
    
    
}
