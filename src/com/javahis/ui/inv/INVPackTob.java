package com.javahis.ui.inv;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
/**
 *
 * <p>Title:�������۲��� </p>
 *
 * <p>Description: �������۲���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009-5-21
 * @version 1.0
 */
public class INVPackTob extends TObserverAdapter{
    /**
         * �ղ�ʵ����
         */
        public INVPackTob(TDataStore dataStore)
       {
           this.dataStore=dataStore;
       }
       //�ӱ���sysfee���õ�����order
       TDataStore dataStore ;
       /**
         * �����õ������¼�
         * @param ds TDS
         * @param parm TParm
         * @param row int
         * @param column String
         * @return Object
         */
        public Object getOtherColumnValue(TDS ds, TParm parm, int row, String column)
        {
            //ȷ��
            if("PACK_DESC".equals(column))
            {
                String invCode = ds.getItemString(row, "PACK_CODE");
                if(invCode==null||invCode.length()==0)
                    invCode = ds.getItemString(row, "INV_CODE");
                if(invCode==null||invCode.length()==0)
                    return "";
                INVPackList pack=new INVPackList();
                String packDesc=pack.getTableShowValue(invCode,dataStore);
                if(packDesc != null && packDesc.length() > 0){
                    return packDesc;
                }
                return invCode;
            }
            return null;
        }



}
