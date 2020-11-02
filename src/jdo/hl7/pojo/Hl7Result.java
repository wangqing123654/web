package jdo.hl7.pojo;

import java.util.List;


public class Hl7Result {
	/**
	 * 结果编码
	 */
	private String code;
	/**
	 * 结果内容
	 */
	private String result;
	/**
	 * 结果组合
	 */
	private List<String> resultList;
	/**
	 * 
	 * @return
	 */
	public List<String> getResultList() {
		return resultList;
	}
	/**
	 * 
	 * @param resultList
	 */
	public void setResultList(List<String> resultList) {
		this.resultList = resultList;
	}
	/**
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 
	 * @return
	 */
	public String getResult() {
		return result;
	}
	/**
	 * 
	 * @param result
	 */
	public void setResult(String result) {
		this.result = result;
	}

}
