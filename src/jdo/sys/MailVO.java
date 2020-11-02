package jdo.sys;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>Title: �ʼ�ʵ����</p>
 *
 * <p>Description: �����ʼ�ʱ�����ʼ�ʵ��</p>
 *
 * <p>Copyright: Copyright (c) 2010-04-25</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author li.xiang@gmail.com
 * @version 1.0
 */
public class MailVO
    implements Serializable {
    private static final long serialVersionUID = -2537535129464080231L;

    public MailVO() {
    }
    //�ʼ����ı���
    private String charset="GB2312";
    //�ʼ����ݵ�����
    private String contentType="text/plain";
    /** ���ͷ� */
    private String from = null;
    /** ���շ� */
    private List<String> toAddress = new ArrayList<String> ();
    /** ���� */
    private List<String> ccAddress = new ArrayList<String> ();
    /** ���� */
    private String subject = null;
    /** ���� */
    private String content = null;
    /** URL���� */
    /** byte[]����*/
   private List<MailAttachment> attachByteArrays=new ArrayList<MailAttachment>();


    public String getCharset(){
        return charset;
    }
    public void setCharset(){
        this.charset=charset;
    }

    public String getContentType(){
        return contentType;
    }
    public void setContentType(String contentType){
        this.contentType=contentType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getToAddress() {
        return toAddress;
    }

    public List<String> getCcAddress() {
        return ccAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    /**
     * ���븽����
     * @return List<MailAttachment>
     */
    public List<MailAttachment> getAttachByteArrays(){
        return attachByteArrays;
    }

}
