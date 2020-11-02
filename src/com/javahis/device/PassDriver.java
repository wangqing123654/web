package com.javahis.device;

public class PassDriver {
  static {
    System.loadLibrary("PassDriver_JavaHis"); //����dll
  }

  public PassDriver() {
	  
  }


  /**
   * PASS�ͻ��˹���Ӧ����ʽ(����)
   * HISϵͳ����վ��ȷ��½֮�󣬵�һ���������õĽӿڡ�
   * @return int PASS�ͻ��˹���Ӧ����ʽע���Ƿ�ɹ�
   * 0 ע��ɹ�������ʧ��
   */
  public native static int RegisterServer();


  /**
   * PASS��ʼ��
   * PASS�ͻ��˹���Ӧ����ʽע��ɹ�֮�󣬵��øýӿ�
   * @param UserName String �û�����/��¼�û� �����봫ֵ��
   * @param DepartmentName String ���Ҵ���/������������ �����봫ֵ��
   * @param WorkStationType int ����վ����(10-ҽ������վ 20-ҩѧ����վ) �����봫ֵ��
   * @return int ��ʼ��PASS�Ƿ���ȷ 0 ��ʼ��PASSʧ�� 1 ��ʼ��PASS����ͨ��
   */
  public native static int PassInit(String UserName, String DepartmentName,
                                    int WorkStationType);


  /**
   * PASSϵͳ������Ч������
   * ��ˢ�½��桢�л����ˡ������Ҽ��˵�����֮����Ҫˢ�²�ѯ����ʱ�������ݴ˺����ķ���ֵ��������Ӧ�ؼ�Enabled���ԡ�
   * @param QueryItemNo String ��ѯ����Ŀ��ʶ����Ŀ��ţ����д���/���Ĳ��֣���ʾ���ϱ�ʶ������������ѯ����Ŀ��״̬(��Ŀ��ʶ�����ִ�Сд)�����±�
   * 0 PASSENABLE PASS�����Ƿ���� ����������״̬
   * 11 SYS-SET ϵͳ��������
   * 12 DISQUISITION ��ҩ�о�
   * 13 MATCH-DRUG ҩƷ�����Ϣ��ѯ
   * 14 MATCH-ROUTE ҩƷ��ҩ;����Ϣ��ѯ ������ҩ��������
   * 24 AlleyEnable ����״̬/����ʷ���� ����ʷ/����״̬����
   * 101 CPRRes/CPR �ٴ���ҩָ�ϲ�ѯ
   * 102 Directions ҩƷ˵�����ѯ
   * 103 CPERes/CPE ������ҩ������ѯ
   * 104 CheckRes/CHECKINFO ҩ�����ֵ��ѯ
   * 105 HisDrugInfo ҽԺҩƷ��Ϣ��ѯ
   * 106 MEDInfo ҩ����Ϣ��ѯ����
   * 107 Chp �й�ҩ��
   * 501 DISPOSE �����������
   * 220 LMIM �ٴ�������Ϣ�ο���ѯ һ���˵���ѯ״̬
   * 201 DDIM ҩ����ҩ���໥���ò�ѯ
   * 202 DFIM ҩ����ʳ���໥���ò�ѯ
   * 203 MatchRes/IV ����ע������������ѯ
   * 204 TriessRes/IVM ����ע������������ѯ
   * 205 DDCM ����֢��ѯ
   * 206 SIDE �����ò�ѯ
   * 207 GERI ��������ҩ�����ѯ
   * 208 PEDI ��ͯ��ҩ�����ѯ
   * 209 PREG ��������ҩ�����ѯ
   * 210 LACT ��������ҩ�����ѯ �����˵���ѯ״̬(ר����Ϣ)
   * 301 HELP Passϵͳ���� ������ҩ����ϵͳ״̬
   * @return int PASSϵͳ�����Ƿ���Ч�� 0 ��Ч 1 ��Ч
   */
  public native static int PassGetState(String QueryItemNo);


  /**
   * PASSӦ����ʽ����
   * ��PASS��ʼ���ɹ�֮�󣬵��øýӿڡ�
   * ע�������Ƕ��ʱû������Ҫ�������£������û�ʹ��Ĭ��ֵ��Ĭ��ֵΪ��1,2,0,2,1
   * @param SaveCheckResult int �Ƿ���вɼ���ȡֵ�� 0-���ɼ� 1-����ϵͳ���� �����봫ֵ��
   * @param AllowAllegen int �Ƿ�����˹���ʷ/����״̬��ȡֵ��0-������1-���û����� 2-PASS���� 3-PASSǿ�ƹ������봫ֵ��
   * @param CheckMode int ���ģʽ��ȡֵ��0-��ͨģʽ 1-IVģʽ �����봫ֵ��
   * @param DisqMode int ����ҩ�о�ʱ�Ƿ���ҽ����Ϣ��ȡֵ��0-���� 1-Ҫ�� 2-��ʾ �����봫ֵ��
   * @param UseDiposeIdea int �Ƿ�ʹ�ô��������ȡֵ��0-��ʹ�ô��� 1-�������ã����봫ֵ��
   * @return int ����������
   */
  public native static int PassSetControlParam(int SaveCheckResult,
                                               int AllowAllegen, int CheckMode,
                                               int DisqMode, int UseDiposeIdea);


  /**
   * ���벡�˻�����Ϣ
   * ���ã����˵Ļ�����Ϣ�����仯֮�󣬵��øýӿڡ�
   * @param PatientID String ���˲�����ţ����봫ֵ��
   * @param VisitID String ��ǰ������������봫ֵ��
   * @param Name String �������� �����봫ֵ��
   * @param Sex String �����Ա� �����봫ֵ���磺�С�Ů������ֵ����δ֪��
   * @param Birthday String �������� �����봫ֵ����ʽ��2005-09-20
   * @param Weight String ���� �����Բ���ֵ����λ��KG
   * @param Height String ��� �����Բ���ֵ����λ��CM
   * @param DepartmentName String ҽ������ID/ҽ���������� �����Բ���ֵ��
   * @param Doctor String ����ҽ��ID/����ҽ������ �����Բ���ֵ��
   * @param LeaveHospitalDate String ��Ժ���� �����Բ���ֵ��
   * @return int ��������
   */
  public native static int PassSetPatientInfo(String PatientID, String VisitID,
                                              String Name, String Sex,
                                              String Birthday, String Weight,
                                              String Height,
                                              String DepartmentName,
                                              String Doctor,
                                              String LeaveHospitalDate);


  /**
   * ���벡�˹���ʷ
   * ���ã������˹���ʷ��Ϣ��HISϵͳ���������뵽PASSϵͳ�������ʱ�����øýӿڡ�
   * ע����������ǰ�����ж�������ʷ��Ϣʱ��ѭ�����롣
   * @param AllergenIndex String ����ԭ��ҽ���е�˳���ţ����봫ֵ��
   * @param AllergenCode String ����ԭ���루���봫ֵ��
   * @param AllergenDesc String ����ԭ���ƣ����봫ֵ��
   * @param AllergenType String ����ԭ���ͣ����봫ֵ��ȡֵ����(�жϲ��ִ�Сд)��
   * AllergenGroup PASS������
   * USER_Drug �û�ҩƷ
   * DrugName PASSҩ������
   * @param Reaction String ����֢״ �����Բ���ֵ��
   * @return int ����������
   */
  public native static int PassSetAllergenInfo(String AllergenIndex,
                                               String AllergenCode,
                                               String AllergenDesc,
                                               String AllergenType,
                                               String Reaction);


  /**
   * ���벡��״̬
   * ���ã������˲���״̬��Ϣ��HISϵͳ���������뵽PASSϵͳ�������ʱ�����øýӿڡ�
   * ע����������ǰ�����ж�������״̬��Ϣʱ��ѭ�����롣
   * @param MedCondIndex String ҽ��������ҽ���е�˳���ţ����봫ֵ
   * @param MedCondCode String ҽ���������루���봫ֵ��
   * @param MedCondDesc String ҽ���������ƣ����봫ֵ��
   * @param MedCondType String ҽ���������ͣ����봫ֵ����ֵ����(�жϲ��ִ�Сд)��
   * USER_MedCond �û�ҽ������
   * ICD ICD-9CM����
   * @param StartDate String ��ʼ���� ��ʽ��yyyy-mm-dd�����Բ���ֵ��
   * @param EndDate String �������� ��ʽ��yyyy-mm-dd�����Բ���ֵ��
   * @return int ��������
   */
  public native static int PassSetMedCond(String MedCondIndex,
                                          String MedCondCode,
                                          String MedCondDesc,
                                          String MedCondType,
                                          String StartDate,
                                          String EndDate);


  /**
   * ���뵱ǰ��ѯҩƷ��Ϣ
   * ���ã�����Ҫʵ��ҩ����Ϣ��ѯ�򸡶����ڹ��ܣ����øýӿڡ�
   * @param DrugCode String ҩƷ���루���봫ֵ��
   * @param DrugName String ҩƷ���ƣ����봫ֵ��
   * @param DoseUnit String ������λ�����봫ֵ��
   * @param RouteName String ��ҩ;���������ƣ����봫ֵ��
   * @return int ��������
   */
  public native static int PassSetQueryDrug(String DrugCode,
                                            String DrugName,
                                            String DoseUnit,
                                            String RouteName);


  /**
   * ���뵱ǰҩƷ����������ʾλ��
   * ���ã����뵱ǰҩƷ����������ʾλ��ʱ�����ñ��ӿڣ����ǵ��ñ��ӿ�֮ǰ���ȵ���PassSetQueryDrug() ���������뵱ǰҩƷ��ѯ��Ϣ��
   * @param left int
   * @param top int
   * @param right int
   * @param bottome int
   * ��ʾ��ǰ�༭�����Ļ��Χ(Ҫȡ��Ļ�ľ���ֵ)���ֱ�Ϊ��ǰ�༭������Ͻ�x,y�����½�x,y������width,height��
   * @return int ��������
   */
  public native static int PassSetFloatWinPos(int left,
                                              int top,
                                              int right,
                                              int bottome);


  /**
   * ���벡����ҩ��Ϣ
   * ���ã�����Ҫ������ҩҽ�����ʱ�����øýӿڡ�
   * ע����������ǰ�����ж�����ҩҽ��ʱ��ѭ�����롣�����ҽ��Ϊ��ҩҽ����
   * ���ڹ���վ����Ϊ10��ʱ�����ʱ(�磺סԺҽ��վ������ҽ��վ)��
   * ��ҩ�������ڿ��Բ��ô�ֵ��Ĭ��Ϊ���죻�����ڹ���վ����Ϊ20�ع������ʱ(�磺�ٴ�ҩѧ�������ѯͳ��)��
   * ��ҩ�������ڱ��봫ֵ��
   * @param OrderUniqueCode String ҽ��Ψһ�루���봫ֵ��
   * @param DrugCode String ҩƷ���� �����봫ֵ��
   * @param DrugName String ҩƷ���� �����봫ֵ��
   * @param SingleDose String ÿ������ �����봫ֵ��
   * @param DoseUnit String ������λ �����봫ֵ��
   * @param Frequency String ��ҩƵ��(��/��)�����봫ֵ��
   * @param StartOrderDate String ��ҩ��ʼ���ڣ���ʽ��yyyy-mm-dd �����봫ֵ��
   * @param StopOrderDate String ��ҩ�������ڣ���ʽ��yyyy-mm-dd �����Բ���ֵ����Ĭ��ֵΪ����
   * @param RouteName String ��ҩ;���������� �����봫ֵ��
   * @param GroupTag String ����ҽ����־ �����봫ֵ��
   * @param OrderType String �Ƿ�Ϊ��ʱҽ�� 1-����ʱҽ�� 0��� ����ҽ�� �����봫ֵ��
   * @param OrderDoctor String ����ҽ��ID/����ҽ������ �����봫ֵ��
   * @return int ��������
   */
  public native static int PassSetRecipeInfo(String OrderUniqueCode,
                                             String DrugCode,
                                             String DrugName,
                                             String SingleDose,
                                             String DoseUnit,
                                             String Frequency,
                                             String StartOrderDate,
                                             String StopOrderDate,
                                             String RouteName,
                                             String GroupTag,
                                             String OrderType,
                                             String OrderDoctor);


  /**
   * PASS���ܵ���
   * @param CommandNo int ���������CommandNo����ţ���ʾ����PASSϵͳ��������š�����±�
   * �����
   *
   * ����� �������� ����ֵ
   * 1 סԺҽ������վ�����Զ���� 1-�������ͨ�� 0-����δ��������*
   * 2 סԺҽ������վ�ύ�Զ���� 1-�������ͨ�� 0-����δ��������
   * 33 ����ҽ������վ�����Զ���� 1-�������ͨ�� 0-����δ��������*
   * 34 ����ҽ������վ�ύ�Զ���� 1-�������ͨ�� 0-����δ��������
   * 3 �ֹ���� ��������*
   * 4 �ٴ�ҩѧ��������� ��������
   * 5 �ٴ�ҩѧ�ಡ����� ��������
   * 6 ��ҩ���� ��������*
   * 7 �ֶ����,���Խ������ ��������*
   * ��ѯ��
   * ����� ˵�� ����ֵ
   * 13 ҩƷ�����Ϣ��ѯ ��������
   * 14 ҩƷ��ҩ;����Ϣ��ѯ ��������
   * 101 �ٴ���ҩָ�ϲ�ѯ ��������
   * 102 ҩƷ˵�����ѯ ��������
   * 103 ������ҩ������ѯ ��������
   * 104 ҩ�����ֵ��ѯ ��������
   * 105 ҽԺҩƷ��Ϣ��ѯ ��������
   * 106 ҩ����Ϣ��ѯ���� ��������
   * 107 �й�ҩ�� ��������
   * 201 ҩ����ҩ���໥���ò�ѯ ��������
   * 202 ҩ����ʳ���໥���ò�ѯ ��������
   * 203 ����ע������������ѯ ��������
   * 204 ����ע������������ѯ ��������
   * 205 ����֢��ѯ ��������
   * 206 �����ò�ѯ ��������
   * 207 ��������ҩ�����ѯ ��������
   * 208 ��ͯ��ҩ�����ѯ ��������
   * 209 ��������ҩ�����ѯ ��������
   * 210 ��������ҩ�����ѯ ��������
   * 220 �ٴ�������Ϣ�ο���ѯ ��������
   * ���ڿ�����
   * ����� ˵�� ����ֵ
   * 401 ҩƷ��Ϣ��ѯ��ʾ�������� ��������
   * 402 ҩƷ��Ϣ��ѯ�ر����и������� ��������
   * 403 ��ʾ��ҩ���һ�������������ʾ���� ��������
   * �ۺ���
   * ����� ˵�� ����ֵ
   * 11 ϵͳ�������� ��������
   * 12 ��ҩ�о� ��������
   * 21 ����״̬/����ʷ����(ֻ��) ��������
   * 22 ����״̬/����ʷ����(�༭) ���˹���ʷ/����״̬�Ƿ����˱仯��2-�����˱仯��1-δ�����仯��<=0-���ֳ������
   * 23 ����״̬/����ʷ����(ǿ��) ���˹���ʷ/����״̬�Ƿ����˱仯��2-�����˱仯��1-δ�����仯��<=0-���ֳ������
   * 301 Passϵͳ���� ��������
   * 501 ���ô���������ù���
   * @return int
   */
  public native static int PassDoCommand(int CommandNo);


  /**
   * ��ȡPASS�������ʾ����
   * ���ã����֮��ýӿڡ�
   * ע�����Ƕ��ʱ�û����Ը�����鷵�ؾ���ֵ�����ж�ҽ���Ƿ���Ҫ������ύ���ƣ�
   * ͬʱ�����Խ��þ���ֵ���浽HISϵͳ���ݿ��У�������������վ�鿴�ȡ�
   * ���һ����ҩҽ������PASS��鷢�ֿ��ܴ��ڶ��Ǳ����ҩ���⣬ϵͳ�������о�ʾ������ߵľ�ʾɫ��ʾ����ҽ�� ��
   * ��Ҫע����ǣ���鷵�ؾ���ֵ������ߴ��������أ����Ǿ�ʾ������ߵľ�ʾɫ����������ء�
   * @param DrugUniqueCode int ҽ��Ψһ����
   * @return int ��ʾ����ȡֵ����
   * ��鷵�ؾ���ֵ ��ʾɫ ˵��
   * -3 �޵� ��ʾPASS���ִ���δ�������
   * -2 �޵� ��ʾ��ҩƷ�ڴ�������ʱ������
   * -1 �޵� ��ʾδ����PASSϵͳ�ļ��
   * 0 ���� PASS���δ��ʾ�����ҩ����
   * 1 �Ƶ� Σ���ϵͻ��в���ȷ���ʶȹ�ע
   * 2 ��� ���Ƽ��������Σ�����߶ȹ�ע
   * 4 �ȵ� ���û���һ��Σ�����ϸ߶ȹ�ע
   * 3 �ڵ� ���Խ��ɡ������������Σ�������ع�ע
   */
  public native static int PassGetWarn(String DrugUniqueCode);


  /**
   * ������Ҫ���е�ҩ�����ҩƷ
   * ���ã����֮����Ҫ�鿴���渡�����ڻ�ҩ������ϸ��Ϣʱ�����øýӿڡ�
   * @param DrugUniqueCode int ҽ��Ψһ����
   * @return int ��������
   */
  public native static int PassSetWarnDrug(String DrugUniqueCode);


  /**
   * PASS�˳�
   * @return int ��������
   * ���ã��ڳ����˳�ʱ�����øýӿڡ�
   */
  public native static int PassQuit();

  /**
   * ��ʼ������DLL
   * @return int 1 �ɹ� 0 ʧ��
   */
  public native static int init();
  /**
   * �ͷ�DLL
   * @return int 1 �ɹ� 0 ʧ��
   */
  public native static int close();

  public void test()
  {
//    JFrame f = new JFrame();
//    f.setVisible(true);
    System.out.println("test()");
    //new Thread(new Runnable(){
    //  public void run()
    //  {
    for(int i=0;i<3;i++){
    	 init();
         System.out.println("PassInit()" + PassInit("AAA","BBB",10));
         System.out.println(PassGetState("0")+"������");
         System.out.println("PassSetControlParam()" + PassSetControlParam(1,2,0,2,1));
         PassSetPatientInfo("A1","1","����","��","2001-04-05","","","","","");
         PassSetRecipeInfo("1NCB0005", //ҽ��Ψһ��
                        "1NCB0005", //ҩƷ����
                        "ά����Cע��Һ", //ҩƷ����
                        "1", //ÿ������
                        "֧", //������λ
                        "1/1", //��ҩƵ��
                        "2009-3-26", //��ҩ��ʼ����
                        "2009-3-26", //��ҩ��������
                        "�������", //��ҩ;����������
                        "1", //����ҽ����־(���봫)
                        "1","111"); //�Ƿ�Ϊ��ʱҽ��
         PassSetRecipeInfo("1KA00015", //ҽ��Ψһ��
                        "1KA00015", //ҩƷ����
                        "ά����K1ע��Һ", //ҩƷ����
                        "1", //ÿ������
                        "֧", //������λ
                        "1/1", //��ҩƵ��
                        "2009-3-26", //��ҩ��ʼ����
                        "2009-3-26", //��ҩ��������
                        "�������", //��ҩ;����������
                        "1", //����ҽ����־(���봫)
                        "1","111"); //�Ƿ�Ϊ��ʱҽ��
           PassDoCommand(1);
//         System.out.println("PassSetWarnDrug()" + this.PassSetWarnDrug("1CE00002"));
//         System.out.println("PassSetWarnDrug()" + this.PassSetWarnDrug("1CHB0001"));
         System.out.println("PassGetWarn()aspl"+this.PassGetWarn("1NCB0005"));
//           System.out.println("PassGetWarn()jadl"+this.PassGetWarn("1KA00015"));
           
           
//           System.out.println("PassSetQueryDrug()" + PassSetQueryDrug("1CCA0001","25%�����������","",""));
//           this.PassDoCommand(106);
//           System.out.println("PassGetState()"+this.PassGetState("201"));
//           System.out.println("PassDoCommand()" + PassDoCommand(201));
//         System.out.println("PassGetWarn(1) "+PassGetWarn("1"));
//         System.out.println("PassGetWarn(2) "+PassGetWarn("2"));
//         System.out.println("PassSetQueryDrug()" + PassSetQueryDrug("2C010001","25%�����������","",""));
//         System.out.println("PassDoCommand(401)" + PassDoCommand(401));
     //  }
     //}).start();
     //System.out.println("PassDoCommand(401)" + PassDoCommand(401));
//     System.out.println("PassDoCommand(102)" + PassDoCommand(102));
//     System.out.println("PassDoCommand(12)" + PassDoCommand(12));
     close();
    }
       
  }
  public static void main(String args[]) {
    PassDriver driver = new PassDriver();
    System.out.println(driver.init());
    driver.test();
  }
}
