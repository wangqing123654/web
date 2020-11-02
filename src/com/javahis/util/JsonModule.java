/**
 * @className JsonModule.java 
 * @author litong
 * @Date 2013-1-23 
 * @version V 1.0 
 */
package com.javahis.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author litong
 * @Date 2013-1-23 
 */
public class JsonModule {
	private String total;//����
	private Map<String, String> title;//����
	private List<Map<String, String>> list;//����
	private String Sdate;  //��ʼʱ��
	private String Edate; //��ʼʱ��
	private String date;//ʱ��
	private String kind;//ͳ������  ����  ������
	private String name;//ģ����
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private String item;//ͳ�����
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public Map<String, String> getTitle() {
		return title;
	}
	public void setTitle(Map<String, String> title) {
		this.title = title;
	}
	public List<Map<String, String>> getList() {
		return list;
	}
	public void setList(List<Map<String, String>> list) {
		this.list = list;
	}
	public String getSdate() {
		return Sdate;
	}
	public void setSdate(String sdate) {
		Sdate = sdate;
	}
	public String getEdate() {
		return Edate;
	}
	public void setEdate(String edate) {
		Edate = edate;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	
}
