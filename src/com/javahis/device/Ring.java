package com.javahis.device;

import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;


public class Ring{
	
	public static final String SUCCESS = "C:/s.au";
	public static final String ERROR = "C:/e.au";
	public static final String REPEAT = "C:/r.au";
	
	AudioClip audio;
	
	public Ring(String path){
		// ���������ļ�
		URL mus=null;
		try {
			mus = new File(path).toURI().toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		audio = JApplet.newAudioClip(mus);
	}
	
	/**
	 *<br>
	 * ����˵������������ <br>
	 * ��������� <br>
	 * �������ͣ�
	 */
	public void play() {
		if (audio != null)
			stop();
		audio.play();
	}
	
	/**
	 *<br>
	 * ����˵����ѭ���������� <br>
	 * ��������� <br>
	 * �������ͣ�
	 */
	public void loop() {
		if (audio != null)
			audio.loop();
	}

	/**
	 *<br>
	 * ����˵����ֹͣ�������� <br>
	 * ��������� <br>
	 * �������ͣ�
	 */
	public void stop() {
		if (audio != null)
			audio.stop();
	}
}
