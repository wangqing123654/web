package com.javahis.ui.inv;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title: �������ֵ�۲���</p>
 *
 * <p>Description:�������ֵ�۲��� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw
 * @version 1.0
 */
public class INVBaseTob extends TObserverAdapter{
    /**
         * �ղ�ʵ����
         */
        public INVBaseTob(TDataStore dataStore)
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
            if("INV_CHN_DESC".equals(column))
            {
                String invCode = ds.getItemString(row, "INV_CODE");
                INVBaseList inv=new INVBaseList();
                String invDesc=inv.getTableShowValue(invCode,dataStore);
                if(invDesc != null && invDesc.length() > 0){
                    return invDesc;
                }
                return invCode;
            }
            return null;
        }



}
