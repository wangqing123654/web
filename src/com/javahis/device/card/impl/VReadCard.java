package com.javahis.device.card.impl;


import java.awt.Toolkit;

import jdo.ekt.VRTool;

import com.javahis.device.card.CardDTO;
import com.javahis.device.card.ICardRW;

public class VReadCard implements ICardRW{

	@Override
	public CardDTO readMedicalCard() {
		// TODO Auto-generated method stub
		VRTool vrTool = new VRTool();
		CardDTO cardDTO = vrTool.read();
		Toolkit.getDefaultToolkit().beep();
		return cardDTO;
	}

	@Override
	public void writeMedicalCard(String seq, String mrNo, String cType,
			double balance) {
		// TODO Auto-generated method stub
		VRTool vrTool = new VRTool();
		vrTool.write(seq, mrNo, cType, balance);
		Toolkit.getDefaultToolkit().beep();
	}

	@Override
	public void writeMedicalCardBalance(double balance) {
		// TODO Auto-generated method stub
		Toolkit.getDefaultToolkit().beep();
	}

}
