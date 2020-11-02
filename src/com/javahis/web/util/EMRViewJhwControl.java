package com.javahis.web.util;

import jdo.sys.MessageTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.DMessageIO;
import com.dongyang.tui.DText;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.util.StringTool;
import com.dongyang.util.TSystem;

/**
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMRViewJhwControl extends TControl implements DMessageIO   {
	/**
	 * WORD对象
	 */
	private TWord word;
	
	private String WORD_NAME = "WORD";
	/**
	 * 就诊序号
	 */
	private String caseNo;
	/**
	 * 病历文件序号
	 */
	private String fileSeq;
	/**
	 * 病历文件路径
	 */
	private String filePath;
	/**
	 * 病历文件名称
	 */
	private String fileName;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		TSystem.setObject("MessageObject", new MessageTool());
		callFunction("UI|changeLanguage", "zh");
		TSystem.setObject("ZhFontSizeProportion", StringTool.getDouble(TConfig
				.getSystemValue("ZhFontSizeProportion")));
		TSystem.setObject("EnFontSizeProportion", StringTool.getDouble(TConfig
				.getSystemValue("EnFontSizeProportion")));

		this.word = (TWord) this.getComponent("WORD");
	    addEventListener(WORD_NAME+"->" + TKeyListener.KEY_PRESSED, "onKeyPressed");
		initWord();
	}

	/**
	 * 
	 * @param word
	 *            TWord
	 */
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}

	/**
	 * 打开病历
	 */
	public void initWord() {
		//目录表第一个根目录FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //模板路径服务器
        String templetPathSer = TIOM_FileServer.getPath("EmrData");
        //临时文件地址
        String tmpFilePath = rootName + templetPathSer + "tmpFile.x";
        //拿到Socket通讯工具
        TSocket socket = TIOM_FileServer.getSocket();
        //获取临时文件
		String content = new String(TIOM_FileServer.readFile(socket, tmpFilePath));
		//删除临时文件
		TIOM_FileServer.deleteFile(socket, tmpFilePath);
		//获取病历文件信息
		String tmpFile[] = content.split(",");
		if (tmpFile.length < 4) {
			this.messageBox("病历文件信息不正确！");
			return;
		}
		this.caseNo = tmpFile[0];
		this.fileSeq = tmpFile[1];
		this.filePath = tmpFile[2];
		this.fileName = tmpFile[3];
		//System.out.println("-------caseNo------"+caseNo);
		//System.out.println("-------fileSeq------"+fileSeq);
		//System.out.println("-------fileSeq------"+filePath);
		//System.out.println("-------fileName------"+fileName);
		
//		if (!CommonUtil.checkInputString(this.caseNo)) {
//			this.messageBox("就诊序号不能为空！");
//			return;
//		}
//		if (!CommonUtil.checkInputString(this.fileSeq)) {
//			this.messageBox("病历文件序号不能为空！");
//			return;
//		}
//		if (!CommonUtil.checkInputString(this.filePath)) {
//			this.messageBox("病历文件路径不能为空！");
//			return;
//		}
//		if (!CommonUtil.checkInputString(this.fileName)) {
//			this.messageBox("病历文件名称不能为空！");
//			return;
//		}
//		if (!this.word.onOpen(this.filePath, this.fileName, 3, false)) {
//			this.messageBox("病历打开失败！");
//			return;
//		}

		word.onOpen(this.filePath, this.fileName,3, true);
		//word.onPreviewWord();
		// 设置不可编辑
		this.getWord().setCanEdit(false);
		TParm allParm = new TParm();
		this.getWord().setWordParameter(allParm);

		//
		word.onOpen(this.filePath, this.fileName,3, true);
		//word.onPreviewWord();
		
		// 设置不可编辑
		this.getWord().setCanEdit(false);
		TParm allParm1 = new TParm();
		this.getWord().setWordParameter(allParm1);		
		
        //设置可编辑
        //word.setCanEdit(true);
        //编辑状态(非整洁)
        //word.onEditWord();
         
	}
	
	
	
	
	
}
            
