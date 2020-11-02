package jdo.sys;

import com.dongyang.config.TConfig;
import javax.mail.Session;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.NoSuchProviderException;
import com.dongyang.data.TParm;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Part;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import java.util.Date;
import java.security.Security;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import javax.mail.internet.MimeUtility;

/**
 * <p>Title: �ʼ�������</p>
 *
 * <p>Description: ʵ���ʼ����͹���;</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author li.xiang790130@gmail.com
 * @version 1.0
 */
public class MailUtil {
    /**
     * �����������ͳ���:TEXT
     */
    public static final String CONTENT_TYPE_TEXT = "text/plain";

    /**
     * �����������ͳ���:HTML
     */
    public static final String CONTENT_TYPE_HTML = "text/html";

    private static MailUtil instanceObject;
    //�ʼ�������������ַ��
    private String host;
    //SMTP �˿ں�
    private String port;
    //�Ƿ���֤
    private boolean auth;
    //�Ƿ�ȫ���ӣ�
    private boolean smtpSSL;
    //�����ż���������
    private String adminAccount;
    //��������
    private String adminPassword;
    //��־����true��|false�ر�
    private boolean isDebug = false;

    private String isMailDebug="false";

    private MailUtil() {
        host = TConfig.getSystemValue("mail.smtp.host");
        port = TConfig.getSystemValue("mail.smtp.port");
        auth = TConfig.getSystemValue("mail.smtp.auth").equalsIgnoreCase("Yes") ? true : false;
        smtpSSL = TConfig.getSystemValue("mail.smtp.smtpSSL").equalsIgnoreCase(
            "Yes") ? true : false;
        adminAccount = TConfig.getSystemValue("mail.admin.account");
        adminPassword = TConfig.getSystemValue("mail.admin.password");
//        if(isDebug){
//            System.out.println("=======host=========" + host);
//            System.out.println("=======port=========" + port);
//            System.out.println("=======auth=========" + auth);
//            System.out.println("=======smtpSSL=========" + smtpSSL);
//            System.out.println("=======adminAccount=========" + adminAccount);
//            System.out.println("=======adminPassword=========" + adminPassword);
//        }
    }

    public static synchronized MailUtil getInstance() {
        if (instanceObject == null) {
            instanceObject = new MailUtil();
        }
        return instanceObject;
    }

    /**
     * �����ʼ�
     * @param mailVO MailVO �ʼ�ʵ��
     * @return boolean
     */
    public TParm sendMail(MailVO mailVO) {
        TParm result = new TParm();
        //��ȡSMTP�Ự
        Session smtpSession = this.getSmtpSession();
        Transport smtpTransport = null;
        try {
            smtpTransport = this.getSmtpTransport(smtpSession);
            //����SMTP
            this.connectSmtpTransport(smtpTransport);
            //�����ʼ�����
            Multipart multipart = new MimeMultipart();
            BodyPart textPart = new MimeBodyPart();
            textPart.setDisposition(Part.INLINE);
            //�������
            //html���ͣ�CONTENT_TYPE_HTML ���� ���ı�����"text/plain";
            textPart.setContent(mailVO.getContent(),
                                mailVO.getContentType() + "; charset=" +
                                mailVO.getCharset());
            multipart.addBodyPart(textPart);
            //��Ӹ���
            //�����и�����ѭ�����븽��(url��������byte[]),��ֻ֧��byte[]���͸�����
            if (mailVO.getAttachByteArrays() != null &&
                mailVO.getAttachByteArrays().size() > 0) {
                DataSource dataSource = null;
                //ѭ����ȡ����Ӧ�ĸ������뵽�ʼ��壻
                for (MailAttachment mailAttachment : mailVO.getAttachByteArrays()) {
                    BodyPart attachmentPart = new MimeBodyPart();
                    //���總�������ݣ����뵽�ʼ��壻
                    if (mailAttachment.getData() != null &&
                        mailAttachment.getData().length > 0) {
                        dataSource = new MailByteArrayDataSource(mailAttachment.
                            getData(), "application/octet-stream");
                        attachmentPart.setDataHandler(new DataHandler(
                            dataSource));
                        attachmentPart.setFileName(MimeUtility.encodeText(
                            mailAttachment.getName()));
                        multipart.addBodyPart(attachmentPart);

                    }
                }
            }

            Message message = new MimeMessage(smtpSession);
            //���÷�����;
            if (mailVO.getFrom() != null && !mailVO.getFrom().equals("")) {
                message.setFrom(new InternetAddress(mailVO.getFrom()));

            }
            else {
                //���緢����Ϊ�����ʼ������˺ţ������ʼ���
                message.setFrom(new InternetAddress(adminAccount));
            }

            //�����ռ��ˣ�
            if (mailVO.getToAddress() != null &&
                mailVO.getToAddress().size() > 0) {
                InternetAddress[] toMailAddress = new InternetAddress[mailVO.
                    getToAddress().size()];
                for (int i = 0; i < mailVO.getToAddress().size(); i++) {
                    toMailAddress[i] = this.createInternetAddress(mailVO.
                        getToAddress().get(i));
                    //System.out.println("toMailAddress["+i+"]"+toMailAddress[i]);
                }
                message.setRecipients(Message.RecipientType.TO, toMailAddress);

            }
            //���ó����ˣ�
            if (mailVO.getCcAddress() != null &&
               mailVO.getCcAddress().size() > 0) {
               InternetAddress[] ccMailAddress = new InternetAddress[mailVO.
                   getCcAddress().size()];
               for (int i = 0; i < mailVO.getCcAddress().size(); i++) {
                   ccMailAddress[i] = this.createInternetAddress(mailVO.
                       getCcAddress().get(i));
                   //System.out.println("toMailAddress["+i+"]"+toMailAddress[i]);
               }
               message.setRecipients(Message.RecipientType.CC, ccMailAddress);

           }
            //������ڽ������䣬������ʼ�����;
            if (message.getAllRecipients() != null &&
                message.getAllRecipients().length > 0) {
                //��������;
                message.setSubject(mailVO.getSubject());
                message.setContent(multipart);
                message.setSentDate(new Date());
                message.saveChanges();
                smtpTransport.sendMessage(message, message.getAllRecipients());

            }

        }
        catch (NoSuchProviderException ex) {
            if (isDebug) {
                ex.printStackTrace();
            }
            result.setErrCode( -1);
            return result;
        }
        catch (MessagingException ex) {
            if (isDebug) {
                ex.printStackTrace();
            }
            result.setErrCode( -1);
            return result;
        }
        catch (Exception ex) {
            if (isDebug) {
                ex.printStackTrace();
            }
            result.setErrCode( -1);
            return result;

        }
        finally {
            //�ر����ӣ�
            try {
                this.closeSmtpTransport(smtpTransport);
            }
            catch (MessagingException ex) {
                if (isDebug) {
                    ex.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * �����ʼ���ַ
     * @param email String
     * @return InternetAddress
     */
    private InternetAddress createInternetAddress(String email) throws
        AddressException {
        InternetAddress address = new InternetAddress(email);
        // ����address
        return address;
    }

    /**
     * ��ȡSMTP����
     * @return Properties
     */
    private Properties getSmtpProperties() {
        Properties smtpProperties = System.getProperties();
        smtpProperties.put("mail.smtp.host", host);
        smtpProperties.put("mail.smtp.port", port);
        smtpProperties.put("mail.smtp.auth", auth ? "true" : "false");
        //trueΪ����ģʽ����ʽ����ʱ�ر�false;
        smtpProperties.put("mail.debug", isMailDebug);
        //�Ƿ�ȫ���ӣ�
        if (smtpSSL) {
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            smtpProperties.setProperty("mail.smtp.socketFactory.class",
                                       "javax.net.ssl.SSLSocketFactory");
            smtpProperties.setProperty("mail.smtp.socketFactory.fallback",
                                       "false");
            smtpProperties.setProperty("mail.smtp.socketFactory.port", port);
        }

        return smtpProperties;
    }

    /**
     * ��ȡSMTP�Ự��
     * @return Session
     */
    private Session getSmtpSession() {
        Session smtpSession = null;
        Properties smtpProperties = this.getSmtpProperties();

        if (auth) {
            smtpSession = Session.getInstance(smtpProperties,
                                              new
                                              MyAuthenticator(adminAccount,
                adminPassword));
        }
        else {
            smtpSession = Session.getInstance(smtpProperties, null);
        }
        return smtpSession;
    }

    /**
     * ��ȡsmtp����
     * @param smtpSession Session
     * @return Transport
     * @throws NoSuchProviderException
     */
    private Transport getSmtpTransport(Session smtpSession) throws
        NoSuchProviderException {

        Transport smtpTransport = null;
        //ͨ��smtpЭ������
        smtpTransport = smtpSession.getTransport("smtp");
        return smtpTransport;
    }

    /**
     *  ����SMTP
     * @param smtpTransport Transport
     */
    private void connectSmtpTransport(Transport smtpTransport) throws
        MessagingException {
        if (!smtpTransport.isConnected()) {
            if (auth) {
                smtpTransport.connect();
            }
            else {
                smtpTransport.connect(host, Integer.valueOf(port), adminAccount,
                                      adminPassword);
            }
        }
    }

    /**
     * �ر�SMTP����
     * @param smtpTransport Transport
     * @throws MessagingException
     */
    private void closeSmtpTransport(Transport smtpTransport) throws
        MessagingException {
        if (smtpTransport.isConnected()) {
            smtpTransport.close();
        }
    }


    /**
     * �̳�javaMail����֤��,�ڹ���mail��Sessionʱʹ��
     *
     * ��д����getPasswordAuthentication()
     */
    class MyAuthenticator
        extends Authenticator {

        private String userName;

        private String passWord;

        public MyAuthenticator(String userName, String passWord) {
            this.userName = userName;
            this.passWord = passWord;
        }

        /**
         * ��д�����getPasswordAuthentication����
         *
         * @Override
         */
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, passWord);
        }
    }


}
