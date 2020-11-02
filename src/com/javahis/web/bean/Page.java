
package com.javahis.web.bean;

import java.io.Serializable;
import java.util.List;

public class Page implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ��ת���ڼ�ҳ
	 */
	private int toPage;

	/**
	 * �ܹ���¼��
	 */
	private int totalRecords;

	/**
	 * ÿҳ��ʾ��¼��
	 */
	private int pageSize;

	/**
	 * �ܹ�ҳ��
	 */
	private int totalPage;

	/**
	 * ��ʼ��¼��
	 */
	private int beginIndex;

	/**
	 * �Ƿ�����ҳ
	 */
	@SuppressWarnings("unused")
	private boolean nextPage;

	/**
	 * �Ƿ�������ҳ
	 */
	@SuppressWarnings("unused")
	private boolean prevPage;
	
	/**
	 * Ҫ���ص�ĳһҳ�ļ�¼�б� 
	 */
	@SuppressWarnings("unchecked")
	private List list;   
 

	/**
	 * ���������Ĺ��캯�����캯��
	 */
	public Page() {
	}

	
	public Page(int toPage,int pageSize){
		if (toPage == 0) {
			this.toPage = 1;
		} else {
			this.toPage = toPage;
		}
		this.pageSize = pageSize;
	}
	
	/**
	 * ˵���� ������ʼ��¼�� 
	 * 
	 * @return
	 */
	public int getBeginIndex() {
		beginIndex = (toPage - 1) * pageSize;
		return beginIndex;
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	/**
	 * ˵���� �ж��Ƿ�����һҳ  
	 * 
	 * @return
	 */
	public boolean isNextPage() {
		if (this.getToPage() > 0) {
			if (this.getToPage() >= getTotalPage()) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * ˵���� �ж��Ƿ�����ҳ  
	 * 
	 * @return
	 */
	public boolean isPrevPage() {
		if (this.getToPage() > 1) {
			return true;
		}
		return false;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getToPage() {
		return toPage;
	}

	public void setToPage(int toPage) {
		this.toPage = toPage;
	}

	/**
	 * ˵���� ����ҳ�� 
	 * 
	 * @return
	 */
	public int getTotalPage() {
		totalPage = totalRecords / pageSize;
		if (totalRecords % pageSize != 0) {
			totalPage++;
		}
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	@SuppressWarnings("unchecked")
	public List getList() {
		return list;
	}

	@SuppressWarnings("unchecked")
	public void setList(List list) {
		this.list = list;
	}
	 
}