package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: �豸�������趨</p>
 *
 * <p>Description:�豸�������趨 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company:javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class DevTypeTool  extends TJDOTool{

    /**
     * ������
     */
    public DevTypeTool() {
        setModuleName("dev\\DevTypeModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static DevTypeTool instanceObject;

    /**
     * �õ�ʵ��
     * @return MainStockRoomTool
     */
    public static DevTypeTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevTypeTool();
        return instanceObject;
    }

    /**
     * �õ��豸������Ϣ
     * @return TParm
     */
    public TParm selectDevType(){
        TParm parm = new TParm();
        parm = query("selectDevType",parm);
        return parm;
    }

    /**
     * �õ��豸������볤����Ϣ
     * @return TParm
     */
    public TParm getDevTypeLength(){
        TParm parm = new TParm();
        parm = query("getDevTypeLength",parm);
        return parm;
    }

    /**
     * �õ��豸������볤����Ϣ
     * @return TParm
     */
    public TParm getDevRule(){
        TParm parm = new TParm();
        parm = query("getDevRule",parm);
        return parm;
    }
    /**
     * �����豸������Ϣ
     * @return TParm
     */
    public TParm analyzeDevType(){
        TParm parm = new TParm();
        TParm parmInf = selectDevType();
        TParm parmLength = getDevTypeLength();
        for(int i = 0 ; i < parmLength.getCount();i++){
            int lenght = parmLength.getInt("CATEGORY_LENGTH",i);
            TParm parmI = new TParm();
            for(int j = 0 ; j < parmInf.getCount();j++){
                if(lenght == parmInf.getValue("CATEGORY_CODE",j).length()){
                    copyTParm(parmInf,parmI,j);
                }
            }
            parm.addData("DEVTYPE_GROUP",parmI);
        }
        return parm;
    }

    /**
     * ����������
     * @param fromTParm TParm
     * @param toTParm TParm
     * @param row int
     */
    public void copyTParm(TParm fromTParm, TParm toTParm, int row){
        for(int i = 0;i < fromTParm.getNames().length;i++){
            toTParm.addData(fromTParm.getNames()[i],fromTParm.getValue(fromTParm.getNames()[i],row));
        }
    }


    /**
     * д���豸������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertDevBase(TParm parm){
        parm = update("insertDevBase",parm);
        return parm;
    }

    /**
     * �����豸������
     * @param parm TParm
     * @return TParm
     */
    public TParm updateDevBase(TParm parm){
        parm = update("updateDevBase",parm);
        return parm;
    }

    /**
     * ȡ���豸��������Ϣ
     * @param devCode String
     * @return TParm
     */
    public TParm selectDevBase(String devCode){
        TParm parm = new TParm();
        parm.setData("DEV_CODE",devCode); 
        parm = query("selectDevBase",parm);
        return parm;
    } 

    /** 
     * ȡ���豸�����������ˮ��
     * @param typeCode String
     * @return TParm  
     */
    public TParm getDevMaxSerialNumber(String typeCode){
        TParm parm = new TParm();
        parm.setData("TYPE_CODE",typeCode);
        parm = query("getDevMaxSerialNumber",parm);
        return parm;
    }

    /**
     * ��ȡ�豸������������
     * @return TParm
     */
    public TParm selectDevBaseMaxSeq(){
        TParm parm = new TParm();
        parm = query("selectDevBaseMaxSeq",parm);
        return parm;
    }

    /**
     * �����豸����ȡ���豸��������Ϣ
     * @param devCode String
     * @return TParm
     */
    public TParm getDevBaseByType(String devCode){
        TParm parm = new TParm();
        parm.setData("DEV_CODE",devCode);
        parm = query("getDevBaseByType",parm);
        return parm;
    }

    /**
     * ɾ���豸��������Ӧ����
     * @param devCode String
     * @return TParm
     */
    public TParm deleteDevBase(String devCode){
        TParm parm = new TParm();
        parm.setData("DEV_CODE",devCode);
        parm = update("deleteDevBase",parm);
        return parm;
    }
}
