package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * <p>Title: ҽ���渶����ϸ�ʲ�ƽ��Ϣ </p>
 *
 * <p>Description:  ҽ���渶����ϸ�ʲ�ƽ��Ϣ</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: </p>
 *
 * @author zhangp 20120211
 * @version 1.0
 */
public class INSCheckAccount_DFDetailControl extends TControl{

	TParm acceptData = new TParm(); //�Ӳ�
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		//�Ӳ�
    	Object obj = this.getParameter();
        if (obj instanceof TParm) {
            acceptData = (TParm) obj;
//          �����뱾�����ݲ���,120;�����м�¼���Ķ�û��,140;���Ķ��м�¼����û��,140;
//          �渶˳���,130;����,90;���ط������,160,double,#########0.00;
//          ���Ķ˷������,160,double,#########0.00;�����걨���,160,double,#########0.00;
//          ���Ķ��걨���,160,double,#########0.00;����ȫ�Էѽ��,160,double,#########0.00;
//          ���Ķ�ȫ�Էѽ��,160,double,#########0.00;�����������,160,double,#########0.00;
//          ���Ķ��������,160,double,#########0.00
//          0,left;1,left;2,left;3,left;4,left;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right
//          STATUS_ONE;STATUS_TWO;STATUS_THREE;ADM_SEQ;NAME;TOT_AMT_LOCAL;TOT_AMT_CENTER;NHI_AMT_LOCAL;
//          NHI_AMT_CENTER;OWN_AMT_LOCAL;OWN_AMT_CENTER;ADD_AMT_LOCAL;ADD_AMT_CENTER
            this.callFunction("UI|TABLE|setParmValue", acceptData);
        }
	}
}
