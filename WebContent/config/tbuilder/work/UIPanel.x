<Type=TPanel>

UI.ControlClassName=com.tbuilder.work.WorkPanelControl
UI.Item=Design|Center
UI.Layout=BorderLayout
UI.X=10
UI.Y=10
UI.Width=200
UI.Height=200
UI.MenuConfig=%ROOT%\config\tbuilder\work\UIPanelMenu.x
UI.TopMenu=Y
UI.TopToolBar=Y

Design.type=TSplitPane
Design.dividerLocation=<i>100
Design.item=TComponentSelection|Left;DesignBack|Right


TComponentSelection.type=TTree
//TComponentSelection.bkcolor=��


DesignBack.type=TSplitPane
DesignBack.dividerLocation=<i>580
DesignBack.item=UIDesign|Left;UIProperty|Right


UIDesign.type=TScrollPane
UIDesign.ControlClassName=com.tbuilder.work.UIDesugbControl
//UIDesign.bkcolor=161,192,245

UIProperty.type=TTable
UIProperty.header=name,100;value,80
UIProperty.autoResizeMode=1
//UIProperty.value=[[x,0],[y,0],[width,0],[height,0]]
UIProperty.lockColumns=0
UIProperty.ColumnHorizontalAlignmentData=0,Left;1,��
//UIProperty.HorizontalAlignmentData=0,1,��;1,1,��
UIProperty.RowHeight=20
UIProperty.focusIndexJump=N
UIProperty.Item=HorizontalAlignmentComboBox;VerticalAlignmentComboBox;FocusTypeComboBox;AutoResizeModeComboBox;SelectionModeComboBox;MoveTypeComboBox

HorizontalAlignmentComboBox.Type=TComboBox
HorizontalAlignmentComboBox.Text=ComboBox
HorizontalAlignmentComboBox.VectorData=[[id,name,py1],[0,center,c],[2,left,l],[4,right,r]]
HorizontalAlignmentComboBox.ShowName=Y
HorizontalAlignmentComboBox.SelectedID=0
HorizontalAlignmentComboBox.Editable=true
HorizontalAlignmentComboBox.TableShowList=id,name

VerticalAlignmentComboBox.Type=TComboBox
VerticalAlignmentComboBox.Text=ComboBox
VerticalAlignmentComboBox.VectorData=[[id,name,py1],[0,center,c],[1,up,u],[3,bottom,r]]
VerticalAlignmentComboBox.ShowName=Y
VerticalAlignmentComboBox.SelectedID=0
VerticalAlignmentComboBox.Editable=true
VerticalAlignmentComboBox.TableShowList=id,name

FocusTypeComboBox.Type=TComboBox
FocusTypeComboBox.Text=ComboBox
FocusTypeComboBox.VectorData=[[id,name,py1],[0,none,n],[1,up,u],[2,right,r]]
FocusTypeComboBox.ShowName=Y
FocusTypeComboBox.SelectedID=0
FocusTypeComboBox.Editable=true
FocusTypeComboBox.TableShowList=id,name

AutoResizeModeComboBox.Type=TComboBox
AutoResizeModeComboBox.Text=ComboBox
AutoResizeModeComboBox.VectorData=[[id,name,py1],[0,AUTO_RESIZE_OFF,o],[1,AUTO_RESIZE_NEXT_COLUMN,n],[2,AUTO_RESIZE_SUBSEQUENT_COLUMNS,s],[3,AUTO_RESIZE_LAST_COLUMN,r],[4,AUTO_RESIZE_ALL_COLUMNS,a]]
AutoResizeModeComboBox.ShowName=Y
AutoResizeModeComboBox.SelectedID=0
AutoResizeModeComboBox.Editable=true
AutoResizeModeComboBox.TableShowList=id,name

SelectionModeComboBox.Type=TComboBox
SelectionModeComboBox.Text=ComboBox
SelectionModeComboBox.VectorData=[[id,name,py1],[0,SINGLE_SELECTION,s],[1,SINGLE_INTERVAL_SELECTION,i],[2,MULTIPLE_INTERVAL_SELECTION,m]]
SelectionModeComboBox.ShowName=Y
SelectionModeComboBox.SelectedID=0
SelectionModeComboBox.Editable=true
SelectionModeComboBox.TableShowList=id,name

MoveTypeComboBox.Type=TComboBox
MoveTypeComboBox.Text=ComboBox
MoveTypeComboBox.VectorData=[[id,name,py1],[0,NO_MOVE,n],[1,WIDTH_MOVE,w],[2,HEIGHT_MOVE,h],[3,MOVE,m]]
MoveTypeComboBox.ShowName=Y
MoveTypeComboBox.SelectedID=0
MoveTypeComboBox.Editable=true
MoveTypeComboBox.TableShowList=id,name

TComponentList=TButton;TLabel;TTextField;TNumberTextField;TDateField;TPasswordField;TCheckBox;TRadioButton;TComboBox;TPanel;TTable;TTree;TTabbedPane;&
		TLayoutAdapter;TMovePane;TDataWindow;TTextArea;TTextFormat;TDPanel;TRootPanel;TWord;TLanguageCombo
TComboList=ϵͳ����;����;סԺ;����;ҩ��;����

TComboList.ϵͳ����=�������;ҽ�����;��֯���;ҩƷ���;�������;��Ա���;ԭ�����;�������


TComboList.ϵͳ����.�������=��ʾ��Ϣ�����б�;�ż�ס�������б�;ת��ҽԺ�����б�;�ݼ���������б�;���д��������б�;����ѭ�����������б�;������λ�����б�
TComboList.ϵͳ����.ҽ�����=�й������ϰ����������б�;������������б�;�����������б�;�������濨��������б�;��ʳ״̬�����б�;���±�����̬�����б�;&
			    ���±仯������������б�;����δ��ԭ�������б�;��λ���������б�;���ϵȼ������б�;ҽ�����������б�;�������������б�;����ȼ�;&
			    ����״̬;�û��Զ��������Ŀ;����ʽ;�����ȼ�;�������б�;��������;��������
TComboList.ϵͳ����.��֯���=�շѹ�̨�����б�;��ͳ���������б�;λ���ֵ������б�;ҽԺ�ȼ������б�;��λ�ȼ������б�;��λ״̬�����б�;���ҹ��������б�;&
			    ���ҵȼ������б�;ʡ�����б�;�������б�;������������б�;���������б�;���������б�;��λ;��λ;���������б�(����ר��);&
			    ���۲���;��������;���㴲λ
TComboList.ϵͳ����.ҩƷ���=���ʹ���������б�;ҩƷ�ɷ������б�;ҩƷ���������б�;����ҩƷ�ȼ����������б�;��Ӧ���������б�;;�������б�;Ƶ�������б�;&
			    �����صȼ�;��������;����;ѪҺ��Դ;ѪƷ;��Ѫԭ��;ѪƷ���;���������
TComboList.ϵͳ����.�������=�վݷ��������б�;ҽ����λ�����б�;�վ�״̬�����б�;�վ�����״̬�����б�;֧����ʽ�����б�
TComboList.ϵͳ����.��Ա���=�ڽ������б�;������ϵ�����б�;���������б�;ְҵ�����б�;ְ�������б�;Ѫ�������б�;���������б�;�����̶������б�;���������б�;&
			    ���������б�;֤����������б�;�Ա������б�;��Ա�����б�
TComboList.ϵͳ����.ԭ�����=DC ԭ�������б�;��ҩԭ�������б�;ҩ��ԭ��;�빺��������б�
TComboList.ϵͳ����.�������=����״̬�����б�;���������б�;ʱ�������б�;MDC��Ŀ�����б�;��ӡ�������б�;������������б�;ϵͳ��������б�;&
			    ������ϴ��������б�;������˱�׼;���������Ŀ;��������ԭ��;�¹ʲ��ȼ�;�¹ʲ��ԭ��;�¹ʲ����;�¹ʲ������;�豸��;�����б�;&
			    ��Ⱦ������Ӱ����������;��ԭ������



TComboList.����=����ԭ�������б�;�Һŷ�����������б�;����;�������б�;����״̬�����б�;�ż��������б�;�Һ��շ���������б�;&
		     ����ҽ��������б�;�ű������б�;���������б�;���������б�;�ż������ÿ��������б�;�ż���������Ա�����б�;&
		     �ż����������������б�;�Һŷ�ʽ�����б�;���������б�;��ҩ��ʽ�����б�;����巨�����б�;���˵ȼ������б�

TComboList.סԺ=ȡ��ԤԼסԺԭ�������б�;��̬��¼�ֵ�(ϵͳ�Զ���)�����б�;ת�������б�;סԺ������Դ�������б�;��Ժ״̬�����б�;סԺ�����������б�;���������б�;&
		���������б�
TComboList.����=�վ���������б�;Ʊ��״̬�����б�;�ʽ���Դ�����б�

TComboList.ҩ��=ҩ�������б�;�龫��������б�;������ҩ�������б�

TComboList.����=ͳ�Ʒ��������б�;��ҳ���������б�;Ժ�ڷ��������б�;����ۿ������б�;��ɫ�����б�;�м���ղ��������б�;�������COMBO;������ʽCOMBO;&
		�����������COMBO;�ṹ���������뵥;KPIָ��;��������;��Ⱦ��λ;��Ⱦ��������Ӱ��;��ԭ������;������������б�;�����������������������;&�������������������б�

TComboList.����ƽ̨=BPEL�������COMBO






TTextFormatList=ϵͳ����;����;סԺ;����;ҩ��;����


TTextFormatList.ϵͳ����=�������;ҽ�����;��֯���;ҩƷ���;�������;��Ա���;ԭ�����;�������


TTextFormatList.ϵͳ����.�������=���д���;��ʾ��Ϣ;ȡ��ԭ��;�������;�����̶�;�ݼ����;������λ
TTextFormatList.ϵͳ����.ҽ�����=�����Ŀ;�ײ�ģ��;���ϵȼ�;����ʽ��������;������;ҽ������;���������������;��Ŀʹ�÷�����������;ҽ���ÿ��ҷ�����������;����ÿ��ҷ�����������
TTextFormatList.ϵͳ����.��֯���=��λ���;ϵͳ���;����;����;���;����;ҽ����λ;���۲�����������;�û���������;������;���㴲λ��������;&
				 ���տ���;��λ��������;��;����(����ר��)��������;λ��;����;ʡ;���ʲ�����������;�豸������������;���۴�λ��������;&
				����������������;��ҩ������������;�ɱ�������������;���˵�λ��������;���ÿ���(̩��ר��);���ܷ�����������
TTextFormatList.ϵͳ����.ҩƷ���=��Ӧ����;��λ��������;������������;Ƶ��;�÷���������;�����������������;����������������;ҩ�������������;&
				 ҩƷ���Է�����������
TTextFormatList.ϵͳ����.�������=ͳ�Ʒ���;������;����ί���������б�
TTextFormatList.ϵͳ����.��Ա���=Ȩ��;������ϵ;����;ְҵ;����;̥����ԡ��ʽ;̥�����״̬;̥��������ʽ;̥����Ѫ;ְ������;����;�ڽ�;��Ա;��ҵ�����������;&
				 ְ����������;������Ա(̩��ר��);�����������;�Ա���������;������������;RH����������������
TTextFormatList.ϵͳ����.ԭ�����=����ԭ��;����ԭ����������
TTextFormatList.ϵͳ����.�������=MDC��Ŀ;��Ⱦ�����;������ϴ���;��λ�ȼ�;�豸������������;�豸������������;����;����������;��Ч�����������;&
				 �豸������������;��Ҷ�����������;��Ⱦ��λ��������;����������������;������λ�����б�;�걾����;��Ӧ�����������;&
				 ͳһ���볧����������;����ͬ���ֵ���������;��ҩ����������;���뷽ʽ��������;������ʽ��������;����ȼ���������;&
				 �Թܴ�����������;��޷�ʽ��������;������㷽ʽ��������;����ҩ����������;��ʳ������������;��ʳ�ײ���������;�˵�������������;&
				 �ʹ�������������;��ʳ�˵���������;��������������;��ҩ��̨����������;��ҩ��������������;����״̬������������;ҽ��ܿز�����������;&
				 ������ص������б�;֤��������������;��������;����ҽ��;����ȡ��ԭ��;����ԭ��;ҩƷҽ������ʹ��˵��������������;��Ʒ��������������;&
				 ��Ա��������������;��Ա���������������� ;�ײ�����������������;��ͬ��λ��������;������ʳ��������;�쿨ԭ��;��֪��ʽ;ͣ��ԭ��;��Ա���;&
				 �����ײ���������;����֧������;����ָ�������б�;Χ�����������б�;���䷽ʽ�����б�;����������������;�����ײ���������;��ײ�����������������





TTextFormatList.����=������������;�ű�;�������ÿ���;����������Ա;���������������
TTextFormatList.סԺ=����;��Ժ��ʽ��������;��Ժ������������;סԺ���ҷ�����������;סԺ�ط��ȼ���������;��תԺ����������
TTextFormatList.����=�վ����;��ҳ����;Ժ�ڷ���;֧����ʽ��������(������);�ײ������ۿ�ԭ����������;�ײ������ۿ�������������;����ӿ�֧�����;HRPҩƷ�������
TTextFormatList.ҩ��=ҩ��;ҩ��(����ר��)��������
TTextFormatList.������=������ҩ�������б�;������ҩ��ҩ�����������б�;���������ܹ������б�;�������龫Ĭ����������б�;������ҩ�����첿��A�����б�;������ҩ�����첿��B�����б�;������ҩ�����첿��C�����б�;������ҩ�����첿��AB�����б�
TTextFormatList.����=�ṹ���������뵥��������;�����Ŀ��������;����׼��������;��������ײ���������;�������������������;��������ͬ��������;&
		     ����˳�����;�����ӡ������������;��������������;�ٴ�·����Ŀ��������;�ٴ�·��ִ����Ա��������;�����ٴ�·����������;&
		     ����������������;�ٴ�·�����ԭ����������;�ٴ�·������״̬��������;����������������;�ٴ�·��ʱ����������;����ģ��Ԫ��;����ģ�����ݷ���;&
		     �ٴ�·�����ԭ����������;����ԭ����������;֧����ʽ��������(ҽ�ƿ�);����ԭ����������;����ģ��������;������ģ����������;����������������;&
		     ֪ʶ�ⲡ����������;�����ڼ���������;�����������������������;Ƥ�Խ��;�ײ����;�����������������б�;�����շ�Ƿ��ԭ��;�ͻ���Դ



editDialog.TextArea=%ROOT%\config\tbuilder\edit\TextAreaEdit.x
InputParmDialogConfig=%ROOT%\config\tbuilder\dialog\InputParmDialog.x

TTextFormatList.����=�ײͷ���