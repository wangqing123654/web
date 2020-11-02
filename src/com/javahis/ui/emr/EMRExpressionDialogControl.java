package com.javahis.ui.emr;


import com.dongyang.control.TControl;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TWord;
import com.dongyang.wcomponent.expression.ExpressionUtil;
import com.dongyang.wcomponent.util.TiString;

/**
 *
 * @author whaosoft
 *
 */
public class EMRExpressionDialogControl extends TControl{

	/**  */
	private EFixed ef = null;

	/**  */
	private TWord word = null;


    /**
     * ��ʼ��
     */
    public void onInit() {

    	word = (TWord) ( (Object[]) getParameter() )[0];
		ef = (EFixed) ( (Object[]) getParameter() )[1];

		this.setValue( "NAME", ef.getName() );
		this.setValue( "VALUE", ef.getExpressionDesc() );

	}

    /**
	 * ȷ��
	 */
    public void onSave() {

    	if( !this.doCheck() )
    	   return;

		//
		this.closeWindow();
	}

    /**
     *
     * @return
     */
    private boolean doCheck(){

    	String eName = (String) this.getValue("NAME");
    	String eDesc = (String)this.getValue("VALUE");

        //
    	if( TiString.isEmpty( eName ) ){

    		this.messageBox("���ʽ���붨��һ������!");
    		return false;
    	}

        //
    	if( TiString.isEmpty( eDesc ) ){

    		this.messageBox("���ʽ���ݲ�����Ϊ��!");
    		return false;
    	}

    	//
    	if( ExpressionUtil.isExistExpression(word, ef,eName) ){

    		this.messageBox("�˱��ʽ�����ѱ�ռ��!");
    		return false;
    	}

    	ef.setName(eName);
    	ef.setExpressionDesc(eDesc);

    	//
    	return true;
    }

    /**
     *
     */
    public void onHelp(){

    	StringBuilder sb = new StringBuilder();

    	sb.append("1.���ʽ�ڱ���������ӷ�����'[ ]',����: [����1]+[����2] \n");
    	sb.append("2.���ʽ��������ű���ΪӢ�ķ��� \n");
    	sb.append("3.���ʽ�﷨���󷵻�ֵ'NaN' \n");
    	sb.append("4.֧����ͨ��������,����: ((15+69)-90)*3/2 \n");
    	sb.append("5.֧��ƽ��ֵ����,����: avg(1,2,3,4,5,6) \n");
    	sb.append("6.֧�ֻ��ܺ���,����: sum(1,2,3,4,5,6) \n");
    	sb.append("7.֧�������Сֵ���������Ⱥ��� ");


    	this.messageBox( "ѧϰ�﷨", sb.toString());
    }

}
