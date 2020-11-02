package jdo.label;

import java.util.List;
import java.util.Map;

public interface LabelInf {
	
	/**
	 * ������������վ����
	 * @param map
	 * @return
	 */
	public Map<String, Object> apRegion(List<Map<String, Object>> list,String url);
	
	/**
	 * ������������ǩ�趨
	 * @param map
	 * @return
	 */
	public Map<String, Object> apTag(List<Map<String, Object>> list,String url);
	
	/**
	 * �������������Ҫ�������ݵı�ǩ�����Ƿ��Ӧ
	 * @param map
	 * @return
	 */
	public Map<String, Object> checkRegionLabel(List<Map<String, Object>> list,String url);
	
	/**
	 * �������������ͱ�ǩ��ʾ����
	 * @param map
	 * @return
	 */
	public Map<String, Object> labelData(List<Map<String, Object>> list,String url);
	
	
	/**
	 * ����������ͨ���¼��Ų�ѯ���ͽ��
	 * @param map
	 * @return
	 */
	public Map<String, Object> sendResult(String url);
	
	/**
	 * ������������ѯ�����»�վ�ź�IP
	 * @param map
	 * @return
	 */
	public Map<String, Object> getRegionAP(String url);
	
	
	/**
	 * ������������ѯ������ȫ����ǩ���һ��ͨѶ״̬
	 * @param map
	 * @return
	 */
	public Map<String, Object> getRegionLabel(String url);
	 
	/**
	 * ������������ѯ��ǩ���һ��ͨѶ״̬
	 * @param map
	 * @return
	 */
	public Map<String, Object> getLabel(String labelNo,String url);
	
}
