package com.javahis.device;

import java.awt.Canvas;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import com.dongyang.util.Log;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import java.awt.Frame;
import javax.swing.JFrame;

/**
 *
 * <p>Title: EMR�ӿڷ���</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.12.30
 * @version 1.0
 */
public class EMRPad30
    extends Canvas implements ComponentListener {
    /**
     * �õ����� Window ����� HWND
     * @param canvas Canvas
     * @return int HWND
     */
    private native static synchronized int getNativeWindowHandle(Canvas canvas);

    /**
     * ����OCX���
     * @param hwnd int HWND
     */
    private native static synchronized void initialize(int hwnd);

    /**
     * �����齨�ߴ�
     * @param hwnd int HWND
     * @param nWidth int ���
     * @param nHeight int �߶�
     */
    private native static synchronized void resizeControl(int hwnd, int nWidth,
        int nHeight);

    public static String OCX_ID = "G1182P-LNCAZT-XLIZZJ-RAPOJA";
    public static long INIT_ID = 2007110509;
    //=========================================================================//
    //EMRPad30����
    private native static synchronized int closeOCX(int hwnd);

    private native static synchronized int FileNew(int hwnd);

    private native static synchronized int FileOpen(int hwnd, String strFile,
        int nReserved);

    private native static synchronized int FileSave(int hwnd);

    private native static synchronized int FileSaveAs(int hwnd, String strFile,
        int nFileType, int nReserved);

    private native static synchronized int FileInsert(int hwnd, String strFile,
        int nReserved);

    private native static synchronized String GetFileName(int hwnd);

    private native static synchronized int Print(int hwnd, int nPrintType);

    private native static synchronized int PageSetup(int hwnd);

    private native static synchronized void FileSetPageNumber(int hwnd);

    private native static synchronized void EditUnDo(int hwnd);

    private native static synchronized void EditReDo(int hwnd);

    private native static synchronized void EditCut(int hwnd);

    private native static synchronized int EditCopy(int hwnd);

    private native static synchronized int EditPaste(int hwnd);

    private native static synchronized void EditSelectAll(int hwnd);

    private native static synchronized void EditFind(int hwnd);

    private native static synchronized void EditReplace(int hwnd);

    private native static synchronized int EditCanUnDo(int hwnd);

    private native static synchronized int EditCanReDo(int hwnd);

    private native static synchronized int EditCanCopy(int hwnd);

    private native static synchronized int EditCanPaste(int hwnd);

    private native static synchronized void CleanUndoBuffer(int hwnd);

    private native static synchronized void CleanClipboard(int hwnd);

    private native static synchronized void EditDelete(int hwnd);

    private native static synchronized void EditQueryKeyword(int hwnd);

    private native static synchronized int EditAutoRange(int hwnd);

    private native static synchronized int EditClearUnuseField(int hwnd);

    private native static synchronized int EditClearSymtax(int hwnd);

    private native static synchronized int EditLineReadOnly(int hwnd);

    private native static synchronized int EditLineEditMode(int hwnd);

    private native static synchronized int EditImage(int hwnd);

    private native static synchronized void ViewToolbar(int hwnd);

    private native static synchronized void ViewStatusbar(int hwnd);

    private native static synchronized void ViewLineIndex(int hwnd);

    private native static synchronized int FontBold(int hwnd);

    private native static synchronized int FontItalic(int hwnd);

    private native static synchronized int FontUnderline(int hwnd);

    private native static synchronized int FontSubscript(int hwnd);

    private native static synchronized int FontSuperscript(int hwnd);

    private native static synchronized int LineSpaceSingle(int hwnd);

    private native static synchronized int LineSpaceOnehalf(int hwnd);

    private native static synchronized void LineAlignLeft(int hwnd);

    private native static synchronized void LineAlignCenter(int hwnd);

    private native static synchronized void LineAlignRight(int hwnd);

    private native static synchronized void ParagraphBegin(int hwnd);

    private native static synchronized void ParagraphEnd(int hwnd);

    private native static synchronized void ParagraphFormat(int hwnd);

    private native static synchronized void TableInsert(int hwnd);

    private native static synchronized int TableInsertColLeft(int hwnd);

    private native static synchronized int TableInsertColRight(int hwnd);

    private native static synchronized int TableInsertRowTop(int hwnd);

    private native static synchronized int TableInsertRowBottom(int hwnd);

    private native static synchronized int TableDelete(int hwnd);

    private native static synchronized int TableDeleteCol(int hwnd);

    private native static synchronized int TableDeleteRow(int hwnd);

    private native static synchronized int TableSelect(int hwnd);

    private native static synchronized int TableSelectCol(int hwnd);

    private native static synchronized int TableSelectRow(int hwnd);

    private native static synchronized int TableMergeCell(int hwnd);

    private native static synchronized int TableSplitCell(int hwnd);

    private native static synchronized int TableProp(int hwnd);

    private native static synchronized int TableCellProp(int hwnd);

    private native static synchronized int TableColProp(int hwnd);

    private native static synchronized int TableHideBorder(int hwnd);

    private native static synchronized int SetDocumentMode(int hwnd, int nMode);

    private native static synchronized long GetDocumentMode(int hwnd);

    private native static synchronized void ShowRevisionHistory(int hwnd);

    private native static synchronized void InsertPageBreaker(int hwnd);

    private native static synchronized void SetPadRedraw(int hwnd, int bRedraw);

    private native static synchronized int InsertLine(int hwnd,
        int bAfterCurLine);

    private native static synchronized int DeleteLines(int hwnd,
        long nStartLine, long nEndLine);

    private native static synchronized long GetBaseLineCount(int hwnd);

    private native static synchronized long GetLineCount(int hwnd);

    private native static synchronized long GetCurFieldCharCount(int hwnd);

    private native static synchronized int InsertField(int hwnd, String strName,
        String strContent, int nLayerNo, int nFieldType, int nPosition);

    private native static synchronized String GetFieldText(int hwnd,
        long nBaseLineIndex, long nCellIndex, long nLineIndex, long nFieldText,
        int bSelected);

    private native static synchronized int SetFieldText(int hwnd,
        long nBaseLineIndex, long nCellIndex, long nLineIndex, long nFieldIndex,
        String strText);

    private native static synchronized int FindField(int hwnd,
        String strFindText, int nLayerNo, int nFindType, int bFromBegin);

    private native static synchronized int SetMicroField(int hwnd,
        String strName, String strValue);

    private native static synchronized int UpdateMicroField(int hwnd,
        int bOnlyCanEdit);

    private native static synchronized int ReplaceFieldText(int hwnd,
        long nBaseLineIndex, long nCellIndex, long nLineIndex, long nFieldIndex,
        String strOld, String strNew);

    private native static synchronized long SetRevisalInfo(int hwnd,
        long nRevisialID, int nAuthorization, String strUserName,
        String strUserCode, String strExtent);

    private native static synchronized int SelectBaseLineRange(int hwnd,
        long nStartIndex, long nEndIndex);

    private native static synchronized long GetObjectCount(int hwnd,
        long nBaseLineIndex, long nCellIndex, long nLineIndex, long nType);

    private native static synchronized String UniversalStringFunction(int hwnd,
        String strParam1, String strParam2, String strParam3, String strParam4,
        String strParam5, String strParam6);

    private native static synchronized int SetSel(int hwnd, long BaseLineIndex1,
                                                  long CellIndex1,
                                                  long LineIndex1,
                                                  long FieldElemIndex1,
                                                  long CharPos1,
                                                  long BaseLineIndex2,
                                                  long CellIndex2,
                                                  long LineIndex2,
                                                  long FieldElemIndex2,
                                                  long CharPos2);

    private native static synchronized long GetCurObjectIndex(int hwnd,
        long nType);

    //private native static int GetCurCursorPos(int hwnd,long[] nBaseLineIndex, long[] nCellIndex, long[] nLineIndex, long[] nFieldElemIndex, long[] nCharPos, int bCursorBegin);//ʹ��UniversalBoolFunction����
    private native static synchronized int UniversalBoolFunction(int hwnd,
        String strParam1, String strParam2, long nParam1, long nParam2);

    /**
     * Window ����� HWND
     */
    private int m_hWnd = 0;
    /**
     * �����˵����ڵĴ���
     */
    private TWindow popMenuWindow;
    /**
     * ��־ϵͳ
     */
    private Log log;
    private JFrame frame;
    private Thread thread = new Thread(new Eventlistener());
    private JPopupMenu popup;
    boolean threadStart;
    public class TWindow
        extends JWindow {
        public boolean b;
        public TWindow(Frame frame) {
            super(frame);
        }
    }


    /**
     * ������
     */
    public EMRPad30() {
        log = new Log();
        addComponentListener(this);
    }

    /**
     * �õ���� Windows �� HWND
     * @return int
     */
    private int getHWND() {
        return m_hWnd;
    }

    /**
     * ���OCX����Ƿ��ʼ�����
     * ������ʾ��OCX�������̴߳����
     * �����OCXû�д���֮ǰ���÷�����������ش���
     * ������java��������ʱͬ���ȴ�
     */
    private void checkInit() {
        out("begin");
        while (UniversalBoolFunction(getHWND(), "", "", 3002, 0) == 0)
            try {
                Thread.sleep(1);
            }
            catch (Exception e1) {}
        out("end");
    }

    public void resizeControl(int nWidth, int nHeight) {
        out("begin nWidth=" + nWidth + " nHeight" + nHeight);
        resizeControl(getHWND(), nWidth, nHeight);
        out("end");
    }

    /**
     * java �ڲ��������ʹ��
     * �����OCX���
     */
    public void addNotify() {
        out("begin");
        super.addNotify();
        //�õ� Window HWND
        m_hWnd = getNativeWindowHandle(this);
        //��ʼ�� OCX ���
        initialize(m_hWnd);
        out("end");
    }

    public void componentHidden(ComponentEvent e) {

    }

    public void componentMoved(ComponentEvent e) {
        out("begin");
        checkInit();
        resizeControl(getWidth(), getHeight());
        out("end");
    }

    public void componentResized(ComponentEvent e) {
        out("begin");
        checkInit();
        out("getX=" + getX() + " getY=" + getY() + " getWidth=" + getWidth() +
            " getHeight=" + getHeight());
        resizeControl(getHWND(), getWidth(), getHeight());
        out("end");
    }

    public void componentShown(ComponentEvent e) {
    }

    //EMRPad30 Public����
    /**
     * �½��ļ�
     * ������������յ�ǰ�༭���е����ݣ���ʼ���༭����Ĭ�����ã���������һ�����ĵ�����д
     * @return boolean �ɹ�true ʧ��false
     */
    public boolean fileNew() {
        int value;
        out("begin");
        value = FileNew(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ���ļ�
     * ���������ڴ�һ���Ѵ��ڵĵ��Ӳ����ĵ�
     * @param strFile String �ļ���
     * @param nReserved int ����
     * @return boolean �ɹ�true ʧ��false
     */
    public boolean fileOpen(String strFile, int nReserved) {
        int value;
        out("begin strFile=" + strFile + " nReserved=" + nReserved);
        value = FileOpen(getHWND(), strFile, nReserved);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ���ļ�
     * ���������ڰѵ�ǰ�༭���е����ݱ����һ�����Ӳ����ļ�
     * @param strFile String �ļ���
     *  ָ���򿪵��ĵ�����Ӧ������ȫ·���������硰C:\סԺ����.emr��
     * @return boolean �ɹ�true ʧ��false
     */
    public boolean fileOpen(String strFile) {
        boolean value;
        out("begin strFile=" + strFile);
        value = fileOpen(strFile, 0);
        out("end value=" + value);
        return value;
    }

    /**
     * �����ļ�
     * ���������ڰѵ�ǰ�༭���е����ݱ����һ�����Ӳ����ļ�
     * @return boolean �ɹ�true ʧ��false
     */
    public boolean fileSave() {
        int value;
        out("begin");
        value = FileSave(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ����ļ�
     * ���������ڰѵ�ǰ�༭���е����ݱ����һ�����Ӳ����ļ�
     * @param strFile String �ļ���
     * ָ��������ĵ�����Ӧ������ȫ·���������硰C:\סԺ����.emr��
     * @param nFileType int �ļ�����
     * 0 �����ļ�
     * 1 ģ���ļ�
     * 2 �ؼ���
     * 3 ƽ���﷨�ļ�
     * 4 XML�ļ�
     * 5 Text�ļ�
     * 12 ���ܵ�Text�ļ�
     * 13 �ѱ༭����ǰ����׷�ӵ�ָ���ļ���Text�ļ�
     * 14 XML����ļ�
     * @param nReserved int ����
     * @return boolean �ɹ�true ʧ��false
     */
    public boolean fileSaveAs(String strFile, int nFileType, int nReserved) {
        int value;
        out("begin strFile=" + strFile + " nFileType=" + nFileType +
            " nReserved=" + nReserved);
        value = FileSaveAs(getHWND(), strFile, nFileType, nReserved);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ����ļ�
     * ���������ڰѵ�ǰ�༭���е����ݱ����һ�����Ӳ����ļ�
     * @param strFile String �ļ���
     * ָ��������ĵ�����Ӧ������ȫ·���������硰C:\סԺ����.emr��
     * @param nFileType int �ļ�����
     * 0 �����ļ�
     * 1 ģ���ļ�
     * 2 �ؼ���
     * 3 ƽ���﷨�ļ�
     * 4 XML�ļ�
     * 5 Text�ļ�
     * 12 ���ܵ�Text�ļ�
     * 13 �ѱ༭����ǰ����׷�ӵ�ָ���ļ���Text�ļ�
     * 14 XML����ļ�
     * @return boolean �ɹ�true ʧ��false
     */
    public boolean fileSaveAs(String strFile, int nFileType) {
        boolean value;
        out("begin strFile=" + strFile + " nFileType=" + nFileType);
        value = fileSaveAs(strFile, nFileType, 0);
        out("end value=" + value);
        return value;
    }

    /**
     * ����ָ���ļ�
     * �����������ڱ༭����ǰ�Ĺ��λ�ò���ָ���ļ�������
     * @param strFile String �ļ���
     * ָ��������ĵ�����Ӧ������ȫ·����
     * @param nReserved int ������չ��Ŀǰ����0
     * @return boolean �ɹ�true ʧ��false
     */
    public boolean fileInsert(String strFile, int nReserved) {
        int value;
        out("begin strFile=" + strFile + " nReserved=" + nReserved);
        value = FileInsert(getHWND(), strFile, nReserved);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ����ָ���ļ�
     * �����������ڱ༭����ǰ�Ĺ��λ�ò���ָ���ļ�������
     * @param strFile String �ļ���
     * ָ��������ĵ�����Ӧ������ȫ·����
     * @return boolean �ɹ�true ʧ��false
     */
    public boolean fileInsert(String strFile) {
        boolean value;
        out("begin strFile=" + strFile);
        value = fileInsert(strFile, 0);
        out("end value=" + value);
        return value;
    }

    /**
     * ���ص�ǰ�ļ�����
     * ���������ڷ��ص�ǰ�򿪵��ļ�����
     * @return String
     */
    public String getFileName() {
        String value;
        out("begin");
        value = GetFileName(getHWND());
        out("end value=" + value);
        return value;
    }

    /**
     * ��ӡ�ĵ�
     * @param nPrintType int ��ӡ����
     * ����ָ����ӡ�����ͣ�
     * 0 ȫ�Ĵ�ӡ��ʽ���ᵯ��һ����ӡ�Ի���
     * 1 ѡ���ӡ��ֻ��ӡѡ�еĲ���
     * 2 �����ӡ����ӡѡ����з�Χ����
     * @return boolean �ɹ�true ʧ��false
     */
    public boolean print(int nPrintType) {
        int value;
        out("begin nPrintType=" + nPrintType);
        value = Print(getHWND(), nPrintType);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ����ҳ��
     * ����������ҳ�����ô���
     * @return boolean �ɹ�true ʧ��false
     */
    public boolean pageSetup() {
        int value;
        out("begin");
        value = PageSetup(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ����ҳ�����
     * ��������������ҳ�����ʼ���,ͬʱ��������ҳ�Ƿ���ʾҳü��ҳ��
     * (���Ϸ���)
     */
    public void fileSetPageNumber() {
        out("begin");
        FileSetPageNumber(getHWND());
        out("end");
    }

    /**
     * ����
     * ���������ڳ��������Ķ���
     */
    public void editUnDo() {
        out("begin");
        EditUnDo(getHWND());
        out("end");
    }

    /**
     * ������ʾ����������
     * ����������������ʾ����������
     */
    public void editReDo() {
        out("begin");
        EditReDo(getHWND());
        out("end");
    }

    /**
     * ����
     * ���������ڼ��е�ǰѡ�е�����
     */
    public void editCut() {
        out("begin");
        EditCut(getHWND());
        out("end");
    }

    /**
     * ����
     * ���������ڸ��Ƶ�ǰѡ�е�����
     * @return boolean ����ɹ�����TRUE�����򷵻�FALSE
     */
    public boolean editCopy() {
        int value;
        out("begin");
        value = EditCopy(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ճ��
     * ���������ڴӼ����������ճ������ǰλ��
     * @return boolean ����ɹ�����TRUE�����򷵻�FALSE
     */
    public boolean editPaste() {
        int value;
        out("begin");
        value = EditPaste(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ȫ��ѡ�е�ǰ�ĵ�
     * ����������ȫ��ѡ�е�ǰ�ĵ�������
     */
    public void editSelectAll() {
        out("begin");
        EditSelectAll(getHWND());
        out("end");
    }

    /**
     * ������׼�Ĳ��ҽ���
     * ���������ڵ�����׼�Ĳ��ҽ��棬������ʹ��
     */
    public void editFind() {
        out("begin");
        EditFind(getHWND());
        out("end");
    }

    /**
     * ������׼���滻����
     * ���������ڵ�����׼���滻���棬���滻ʹ��
     */
    public void editReplace() {
        out("begin");
        EditReplace(getHWND());
        out("end");
    }

    /**
     * �ж��Ƿ���Գ���
     * �����������жϵ�ǰ�༭�������Ƿ���Գ���
     * @return boolean �����ǰ�༭���������Գ�������TRUE�����򷵻�FALSE
     */
    public boolean editCanUnDo() {
        int value;
        out("begin");
        value = EditCanUnDo(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �ж��Ƿ��������
     * �����������жϵ�ǰ�༭�������Ƿ��������
     * @return boolean �����ǰ�༭������������������TRUE�����򷵻�FALSE
     */
    public boolean editCanReDo() {
        int value;
        out("begin");
        value = EditCanReDo(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �ж��Ƿ���Ը���
     * �����������жϵ�ǰ�༭�������Ƿ���Ը��Ʋ���
     * @return boolean �����ǰ�༭����ѡ�з���TRUE�����򷵻�FALSE
     */
    public boolean editCanCopy() {
        int value;
        out("begin");
        value = EditCanCopy(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �ж��Ƿ����ճ��
     * �����������жϵ�ǰ�Ƿ����ճ������
     * @return boolean �����ǰ�༭���������п���ճ��������ѡ�з���TRUE�����򷵻�FALSE
     */
    public boolean editCanPaste() {
        int value;
        out("begin");
        value = EditCanPaste(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ��ձ༭��������
     * ������������ձ༭���������е����ݣ�ִ�б�������, ճ�����ܽ���������
     */
    public void cleanClipboard() {
        out("begin");
        CleanClipboard(getHWND());
        out("end");
    }

    /**
     * ��ձ༭������
     * ������������ձ༭�����������ݣ�ִ�б�������, Undo��Redo���ܽ���������
     */
    public void cleanUndoBuffer() {
        out("begin");
        CleanUndoBuffer(getHWND());
        out("end");
    }

    /**
     * ɾ��
     * ���������ܲ���Ϊ����Delete��
     */
    public void editDelete() {
        out("begin");
        EditDelete(getHWND());
        out("end");
    }

    /**
     * �򿪹ؼ����ֵ�
     * ���������ڴ򿪹ؼ����ֵ�
     * (���Ϸ���)
     */
    public void editQueryKeyword() {
        out("begin");
        EditQueryKeyword(getHWND());
        out("end");
    }

    /**
     * �Զ��Ű�
     * �����������Զ��Ű�
     * @return boolean Ŀǰʼ�շ���TRUE
     */
    public boolean editAutoRange() {
        int value;
        out("begin");
        value = EditAutoRange(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ���δʹ�õ�Ԫ��
     * �������������δʹ�õ�Ԫ��
     * @return boolean Ŀǰʼ�շ���TRUE
     */
    public boolean editClearUnuseField() {
        int value;
        out("begin");
        value = EditClearUnuseField(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ���Ԫ�ص��﷨
     * �������������ѡ����Ŀ��ע��Ԫ���е��﷨
     * @return boolean Ŀǰʼ�շ���TRUE
     */
    public boolean editClearSymtax() {
        int value;
        out("begin");
        value = EditClearSymtax(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ������ֻ��
     * ����������������ֻ��
     * @return boolean Ŀǰʼ�շ���TRUE
     */
    public boolean editLineReadOnly() {
        int value;
        out("begin");
        value = EditLineReadOnly(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �����пɱ༭
     * ���������������пɱ༭
     * @return boolean Ŀǰʼ�շ���TRUE
     */
    public boolean editLineEditMode() {
        int value;
        out("begin");
        value = EditLineEditMode(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �༭ͼ��
     * ���������ڱ༭��ǰѡ�ֵ�ͼ��
     * @return boolean Ŀǰʼ�շ���TRUE
     */
    public boolean editImage() {
        int value;
        out("begin");
        value = EditImage(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ��ʾ�����ع�����
     * ������������ʾ�����ع�����
     */
    public void viewToolbar() {
        out("begin");
        ViewToolbar(getHWND());
        out("end");
    }

    /**
     * ��ʾ������״̬��
     * ������������ʾ������״̬��
     */
    public void viewStatusbar() {
        out("begin");
        ViewStatusbar(getHWND());
        out("end");
    }

    /**
     * ��ʾ�������к�
     * ������������ʾ�������к�
     */
    public void viewLineIndex() {
        out("begin");
        ViewLineIndex(getHWND());
        out("end");
    }

    /**
     * ���ú���
     * ��������������ѡ�еĲ���Ϊ����,�����ǰ�Ǻ����,������Ϊ������
     * @return boolean
     */
    public boolean fontBold() {
        int value;
        out("begin");
        value = FontBold(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ����б��
     * ��������������ѡ�еĲ���Ϊб��,�����ǰ��б���,������Ϊ��б��
     * @return boolean
     */
    public boolean fontItalic() {
        int value;
        out("begin");
        value = FontItalic(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �����»���
     * ��������������ѡ�еĲ���Ϊ�»���,�����ǰ���»��ߵ�,������Ϊ���»���
     * @return boolean
     */
    public boolean fontUnderline() {
        int value;
        out("begin");
        value = FontUnderline(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �����ϽǱ�
     * ��������������ѡ�еĲ���Ϊ�ϽǱ�,�����ǰ���ϽǱ��,������Ϊ���ϽǱ�
     * @return boolean
     */
    public boolean fontSubscript() {
        int value;
        out("begin");
        value = FontSubscript(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �����½ű�
     * ��������������ѡ�еĲ���Ϊ�½ű�,�����ǰ���½ű��,������Ϊ���½ű�
     * @return boolean
     */
    public boolean fontSuperscript() {
        int value;
        out("begin");
        value = FontSuperscript(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ���õ����о�
     * ��������������ѡ�еĲ��ֵ���Ϊ�����о�
     * @return boolean
     */
    public boolean lineSpaceSingle() {
        int value;
        out("begin");
        value = LineSpaceSingle(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ����1.5���о�
     * ��������������ѡ�еĲ��ֵ���Ϊ1.5���о�
     * @return boolean
     */
    public boolean lineSpaceOnehalf() {
        int value;
        out("begin");
        value = LineSpaceOnehalf(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ���������
     * ��������������ѡ�еĲ��ֵ���Ϊ�����
     */
    public void lineAlignLeft() {
        out("begin");
        LineAlignLeft(getHWND());
        out("end");
    }

    /**
     * ���þ���
     * ��������������ѡ�еĲ��ֵ���Ϊ���ж���
     */
    public void lineAlignCenter() {
        out("begin");
        LineAlignCenter(getHWND());
        out("end");
    }

    /**
     * �����Ҷ���
     * ��������������ѡ�еĲ��ֵ���Ϊ�Ҷ���
     */
    public void lineAlignRight() {
        out("begin");
        LineAlignRight(getHWND());
        out("end");
    }

    /**
     * ���ö���ʼ���ӷ�
     * �������������õ�ǰ��Ϊ����ʼ���ӷ�
     */
    public void paragraphBegin() {
        out("begin");
        ParagraphBegin(getHWND());
        out("end");
    }

    /**
     * ���öν������ӷ�
     * �������������õ�ǰ��Ϊ�ν������ӷ�
     */
    public void paragraphEnd() {
        out("begin");
        ParagraphEnd(getHWND());
        out("end");
    }

    /**
     * ����ĸ�ʽ����
     * ���������ڵ�������ĸ�ʽ����,�����ε��������������ҷ�ʽ,���������Ű�ĸ�ʽ
     */
    public void paragraphFormat() {
        out("begin");
        ParagraphFormat(getHWND());
        out("end");
    }

    /**
     * ������Ի���
     * ���������ڵ���ͨ�õı�����Ի���
     */
    public void tableInsert() {
        out("begin");
        TableInsert(getHWND());
        out("end");
    }

    /**
     * ���е���߲�������
     * �����������ڵ�ǰ���е���߲�������
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableInsertColLeft() {
        int value;
        out("begin");
        value = TableInsertColLeft(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ���е��ұ߲�������
     * �����������ڵ�ǰ���е��ұ߲�������
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableInsertColRight() {
        int value;
        out("begin");
        value = TableInsertColRight(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �����ϱ߲�������
     * �����������ڵ�ǰ���е��ϱ߲�������
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableInsertRowTop() {
        int value;
        out("begin");
        value = TableInsertRowTop(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �����±߲�������
     * �����������ڵ�ǰ���е��±߲�������
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableInsertRowBottom() {
        int value;
        out("begin");
        value = TableInsertRowBottom(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ɾ�����
     * ����������ɾ����ǰ�ı��
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableDelete() {
        int value;
        out("begin");
        value = TableDelete(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ɾ�����ѡ�е���
     * ����������ɾ����ǰ�ı��ѡ�е���
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableDeleteCol() {
        int value;
        out("begin");
        value = TableDeleteCol(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ɾ�����ѡ�е���
     * ����������ɾ����ǰ�ı��ѡ�е���
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableDeleteRow() {
        int value;
        out("begin");
        value = TableDeleteRow(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ѡ�е�ǰ�ı��
     * ����������ѡ�е�ǰ�ı��
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableSelect() {
        int value;
        out("begin");
        value = TableSelect(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ѡ�е�ǰ�ı����
     * ����������ѡ�е�ǰ�ı����
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableSelectCol() {
        int value;
        out("begin");
        value = TableSelectCol(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ѡ�е�ǰ�ı����
     * ����������ѡ�е�ǰ�ı����
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableSelectRow() {
        int value;
        out("begin");
        value = TableSelectRow(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �ϲ���Ԫ��
     * ���������ںϲ���Ԫ�����,�����ǰѡ�еĵ�Ԫ����Ժϲ�,����ɺϲ�����
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableMergeCell() {
        int value;
        out("begin");
        value = TableMergeCell(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ��ֵ�Ԫ��
     * ���������ڲ�ֵ�Ԫ�����,�����ǰѡ�еĵ�Ԫ����Բ��,����ʾ���в�ֻ����в��,��ɲ�ֲ���
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableSplitCell() {
        int value;
        out("begin");
        value = TableSplitCell(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ��ʾ������ԶԻ���
     * ������������ʾ������ԶԻ���,�������ñ��ı߿����ԡ������뷽ʽ�����Ԫ��ı߾ࡢ������ơ��������
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableProp() {
        int value;
        out("begin");
        value = TableProp(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ��ʾ��Ԫ�����ԶԻ���
     * ������������ʾ��Ԫ�����ԶԻ���,�������õ�Ԫ��ı߿�����
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableCellProp() {
        int value;
        out("begin");
        value = TableCellProp(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ��ʾ��������ԶԻ���
     * ������������ʾ��������ԶԻ���,�����ֹ������п��
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableColProp() {
        int value;
        out("begin");
        value = TableColProp(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ��ʾ���������߿�
     * ���������������Ƿ���ʾ���������߿���
     * @return boolean Ŀǰ�ܷ���TRUE
     */
    public boolean tableHideBorder() {
        int value;
        out("begin");
        value = TableHideBorder(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �༭״̬
     */
    public final static int DOCUMENT_MODE_EDIT = 1;
    /**
     * ֻ��״̬
     */
    public final static int DOCUMENT_MODE_READ_ONLY = 2;
    /**
     * �����ʾ״̬
     */
    public final static int DOCUMENT_MODE_CLEAR = 3;
    /**
     * �����ĵ���״̬
     * ���������������ĵ���״̬
     * @param nMode int ����ָ���ĵ���״̬
     * 1 �༭״̬ DOCUMENT_MODE_EDIT
     * 2 ֻ��״̬ DOCUMENT_MODE_READ_ONLY
     * 3 �����ʾ״̬ DOCUMENT_MODE_CLEAR
     * @return boolean ����ɹ�����TRUE��ʧ�ܷ���FALSE
     * �����ʾ״̬��һ��ȫ��ֻ��״̬����ɾ�����ĵ��о��޶�ɾ���������֣�
     * ͬʱ�����Ű棬����ʾ�޸ĺۼ�������������״̬ͨ�����ô˺����Ϳ����໥�л���
     * ����ӡ������Ҫ��������һ�����ڴ�����
     */
    public boolean setDocumentMode(int nMode) {
        int value;
        out("begin nMode=" + nMode);
        value = SetDocumentMode(getHWND(), nMode);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �����ĵ���״̬
     * ���������ڷ����ĵ���״̬
     * @return long �����ֻ��״̬����2 DOCUMENT_MODE_READ_ONLY�������ʾ״̬����3 DOCUMENT_MODE_CLEAR������״̬����1 DOCUMENT_MODE_EDIT��
     */
    public long getDocumentMode() {
        long value;
        out("begin");
        value = GetDocumentMode(getHWND());
        out("end value=" + value);
        return value;
    }

    /**
     * ��ʾ�ĵ����޶���¼
     * ������������ʾ�ĵ����޶���¼���
     */
    public void showRevisionHistory() {
        out("begin");
        ShowRevisionHistory(getHWND());
        out("end");
    }

    /**
     * �����ҳ��
     * �������ڵ�ǰλ�ò����ҳ��
     */
    public void insertPageBreaker() {
        out("begin");
        InsertPageBreaker(getHWND());
        out("end");
    }

    /**
     * ǿ�Ʊ༭���ػ�
     * ����������ǿ�Ʊ༭���ػ�
     * @param bRedraw boolean ���������Ƿ��ػ���Ŀǰʼ��Ĭ��Ϊ�ػ�������TRUE��FALSE
     */
    public void setPadRedraw(boolean bRedraw) {
        out("begin bRedraw=" + bRedraw);
        SetPadRedraw(getHWND(), bRedraw ? 1 : 0);
        out("end");
    }

    /**
     * ǿ�Ʊ༭���ػ�
     * ����������ǿ�Ʊ༭���ػ�
     */
    public void setPadRedraw() {
        out("begin");
        setPadRedraw(true);
        out("end");
    }

    /**
     * �����¿հ���
     * �����������ڵ�ǰ�е�λ�ò���һ���µĿհ���
     * @param bAfterCurLine boolean �������������뵱ǰ�е����λ�ã�TRUE�ڵ�ǰ�е����棬FALSE�ڵ�ǰ�е����档
     * @return boolean
     */
    public boolean insertLine(boolean bAfterCurLine) {
        int value;
        out("begin bAfterCurLine=" + bAfterCurLine);
        value = InsertLine(getHWND(), bAfterCurLine ? 1 : 0);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ɾ��ָ����Χ����
     * ����������ɾ��ָ����Χ����
     * @param nStartLine long ��ʼ�� ��������ɾ���е���ʼλ��
     * @param nEndLine long ������ ��������ɾ���еĽ���λ��
     * @return boolean �ɹ�����TRUE��ʧ�ܷ���FALSE
     */
    public boolean deleteLines(long nStartLine, long nEndLine) {
        int value;
        out("begin nStartLine=" + nStartLine + " nEndLine=" + nEndLine);
        value = DeleteLines(getHWND(), nStartLine, nEndLine);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ���ػ��е�����
     * ���������ڷ��ػ��е�����
     * @return long ���ص�ǰ�ĵ����е�����
     */
    public long getBaseLineCount() {
        long value;
        out("begin");
        value = GetBaseLineCount(getHWND());
        out("end value=" + value);
        return value;
    }

    /**
     * ����ѡ��Χ
     * ��������������ѡ��Χ������ʼλ�úͽ���λ����ͬ������Ϊ���ù��λ��
     * @param BaseLineIndex1 long ��ʼ���к�
     * ��ʼ���кš���һ�����е������0,���Ϊ-1,��˵����ʼλ��Ϊ�ĵ�ĩβ��
     * ϵͳ���Զ������λ�ã�����CellIndex1��LineIndex1��FieldElemIndex1��
     * CharPos1����
     * @param CellIndex1 long ��ʼ��Ԫ��
     * ��ʼ��Ԫ��,������ڱ����,��ֵ-1,������Ҫ��ֵ��Ԫ������
     * @param LineIndex1 long ��ʼ�к�
     * ��ʼ�к�
     * @param FieldElemIndex1 long ��ʼԪ�غ�
     *  ��ʼԪ�غ�
     * @param CharPos1 long ��ʼ�ַ�λ��
     * ��ʼ�ַ�λ��
     * @param BaseLineIndex2 long �������к�
     *  �������кš���һ�����е������0,���Ϊ-1,��˵������λ��Ϊ�ĵ�ĩβ��
     * ϵͳ���Զ������λ�ã�����CellIndex2��LineIndex2��FieldElemIndex2��
     * CharPos2����
     * @param CellIndex2 long ������Ԫ��
     * ������Ԫ��,������ڱ����,��ֵ-1,������Ҫ��ֵ��Ԫ������
     * @param LineIndex2 long �����к�
     *  �����к�
     * @param FieldElemIndex2 long ����Ԫ�غ�
     * ����Ԫ�غ�
     * @param CharPos2 long �����ַ�λ��
     * �����ַ�λ��
     * @return boolean ����ɹ�����TRUE,���򷵻�FALSE
     */
    public boolean setSel(long BaseLineIndex1, long CellIndex1, long LineIndex1,
                          long FieldElemIndex1, long CharPos1,
                          long BaseLineIndex2, long CellIndex2, long LineIndex2,
                          long FieldElemIndex2, long CharPos2) {
        int value;
        out("begin BaseLineIndex1=" + BaseLineIndex1 + " CellIndex1=" +
            CellIndex1 + " LineIndex1=" + LineIndex1 + " FieldElemIndex1=" +
            FieldElemIndex1 + " CharPos1=" + CharPos1 +
            " BaseLineIndex2=" + BaseLineIndex2 + " CellIndex2=" + CellIndex2 +
            " LineIndex2=" + LineIndex2 + " FieldElemIndex2=" + FieldElemIndex2 +
            " CharPos2=" + CharPos2);
        value = SetSel(getHWND(), BaseLineIndex1, CellIndex1, LineIndex1,
                       FieldElemIndex1, CharPos1, BaseLineIndex2, CellIndex2,
                       LineIndex2, FieldElemIndex2, CharPos2);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ���ص�ǰ������������
     * ���������ڷ��ص�ǰ������������
     * @return long ���ص�ǰ��ǰ������������
     */
    public long getLineCount() {
        long value;
        out("begin");
        value = GetLineCount(getHWND());
        out("end value=" + value);
        return value;
    }

    /**
     * ���ص�ǰԪ�ص��ַ���
     * ���������ڷ��ص�ǰԪ�ص��ַ���
     * @return long ���ص�ǰԪ�ص��ַ���
     */
    public long getCurFieldCharCount() {
        long value;
        out("begin");
        value = GetCurFieldCharCount(getHWND());
        out("end value=" + value);
        return value;
    }

    /**
     * ��������
     * �����������ڵ�ǰλ�ò���һ���µ���
     * @param strName String Ԫ������
     * ��������Ԫ�ص����ƣ��Ժ�����ȡֵ��XML�����Ҫʹ�õ�
     * @param strContent String Ԫ������
     * Ԫ�ص�����
     * @param nLayerNo int ��κ�
     * Ԫ�صĲ�κţ�ָ����XML���ʱ�Ĳ�Σ���Ŵ�1��ʼ��1Ϊ���
     * @param nFieldType int Ԫ������
     * Ԫ�ص����͡�0��ͨ���֡�1�ꡢ2�̶��ı����⡢3���ⶨλ��
     * @param nPosition int λ�� Ԫ�صĲ���λ�ã����>0������ڵ�ǰԪ�صĺ��棬������뵽��ǰԪ�ص�ǰ��
     * @return boolean �ɹ�����TRUE��ʧ�ܷ���FALSE
     */
    public boolean insertField(String strName, String strContent, int nLayerNo,
                               int nFieldType, int nPosition) {
        int value;
        out("begin strName=" + strName + " strContent=" + strContent +
            " nLayerNo=" + nLayerNo + " nFieldType=" + nFieldType +
            " nPosition=" + nPosition);
        value = InsertField(getHWND(), strName, strContent, nLayerNo,
                            nFieldType, nPosition);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ����ָ��Ԫ�ص��ı�����
     * ���������ڷ���ָ��Ԫ�ص��ı�����
     * @param nBaseLineIndex long �����к�
     * Ԫ�����ڻ��е��к�
     * @param nCellIndex long ���Ԫ���
     * Ԫ�����ڱ��Ԫ����ţ�������ڱ��Ԫ�У��˲���Ӧ����Ϊ-1
     * @param nLineIndex long �к�
     * �����е��кţ����к���������ڵ�����
     * @param nFieldText long Ԫ�����
     * Ԫ�ص����
     * @param bSelected boolean ѡ���־
     * TRUE���ش�Ԫ��ѡ�в��ֵ��ı���FALSE���ش�Ԫ�ص������ı�
     * ���nBaseLineIndex��nCellIndex��nLineIndex��nFieldIndex��Ϊ-1���򷵻ص�ǰԪ�ص�����
     * @return String �����ַ���
     */
    public String getFieldText(long nBaseLineIndex, long nCellIndex,
                               long nLineIndex, long nFieldText,
                               boolean bSelected) {
        String value;
        out("begin nBaseLineIndex=" + nBaseLineIndex + " nCellIndex=" +
            nCellIndex + " nLineIndex=" + nLineIndex + " nFieldText=" +
            nFieldText + " bSelected=" + bSelected);
        value = GetFieldText(getHWND(), nBaseLineIndex, nCellIndex, nLineIndex,
                             nFieldText, bSelected ? 1 : 0);
        out("end value=" + value);
        return value;
    }

    /**
     * ����ָ��Ԫ�ص��ı�����
     * ��������������ָ��Ԫ�ص��ı�����
     * @param nBaseLineIndex long �����к�
     * Ԫ�����ڻ��е��к�
     * @param nCellIndex long ���Ԫ���
     * Ԫ�����ڱ��Ԫ����ţ�������ڱ��Ԫ�У��˲���Ӧ����Ϊ-1
     * @param nLineIndex long �к�
     * �����е��кţ����к���������ڵ�����
     * @param nFieldIndex long Ԫ�����
     * Ԫ�ص����
     * @param strText String ����
     * ��Ҫ���õ�����
     * @return boolean �ɹ�����TRUE��ʧ�ܷ���FALSE
     */
    public boolean setFieldText(long nBaseLineIndex, long nCellIndex,
                                long nLineIndex, long nFieldIndex,
                                String strText) {
        int value;
        out("begin nBaseLineIndex=" + nBaseLineIndex + " nCellIndex=" +
            nCellIndex + " nLineIndex=" + nLineIndex + " nFieldIndex" +
            nFieldIndex + " strText=" + strText);
        value = SetFieldText(getHWND(), nBaseLineIndex, nCellIndex, nLineIndex,
                             nFieldIndex, strText);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ����Ԫ��
     * ���������ڲ��ҷ���������Ԫ��
     * @param strFindText String Ԫ������
     * �������Ԫ�����ƺ��ã���β,�硰��%����������ԡ��ա�Ϊǰ׺��Ԫ��
     * @param nLayerNo int ���ҵĲ��
     * Ԫ�صĲ�κã����Ϊ-1����������в�ε�Ԫ��
     * @param nFindType int Ԫ������
     * Ԫ�����ͣ�1Ԫ�ء�2���3Ϊ����Ԫ��
     * ���䣺�����Ҫ����λ�����Բ��ң���������ֵ��+9,
     * ���磺����Ԫ���ж�λ�����Եģ���ֵΪ10�����ұ���ж�λ�����Եģ���ֵΪ11
     * @param bFromBegin boolean ����ʼλ�ÿ�ʼ����
     * TRUE����ʼλ�ÿ�ʼ���ң�FALSE�ӵ�ǰλ�ÿ�ʼ����
     * @return boolean �ɹ�����TRUE��ʧ�ܷ���FALSE�����ҹ�궨λ�����ҵ���λ��
     */
    public boolean findField(String strFindText, int nLayerNo, int nFindType,
                             boolean bFromBegin) {
        int value;
        out("begin strFindText=" + strFindText + " nLayerNo=" + nLayerNo +
            " nFindType=" + nFindType + " bFromBegin=" + bFromBegin);
        value = FindField(getHWND(), strFindText, nLayerNo, nFindType,
                          bFromBegin ? 1 : 0);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ���ú�
     * �������������ú��ֵ
     * @param strName String ����
     * @param strValue String ֵ
     * @return boolean �ɹ�����TRUE��ʧ�ܷ���FALSE
     */
    public boolean setMicroField(String strName, String strValue) {
        int value;
        out("begin strName=" + strName + " strValue=" + strValue);
        value = SetMicroField(getHWND(), strName, strValue);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �滻��
     * �����������õ�ǰ���ֵ�滻�ı��к������
     * @param bOnlyCanEdit boolean �滻��Χ
     *  TRUEֻ����ɱ༭״̬�µĺ꣬�������ڵ��в���ֻ��״̬��FALSE�����ĵ����еĺ�
     * @return boolean �ɹ�����TRUE��ʧ�ܷ���FALSE
     */
    public boolean updateMicroField(boolean bOnlyCanEdit) {
        int value;
        out("begin bOnlyCanEdit=" + bOnlyCanEdit);
        value = UpdateMicroField(getHWND(), bOnlyCanEdit ? 1 : 0);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �滻Ԫ������
     * �����������滻ָ��Ԫ�صĲ�������
     * @param nBaseLineIndex long �����к�
     * Ԫ�����ڻ��е��к�
     * @param nCellIndex long ���Ԫ���
     * Ԫ�����ڱ��Ԫ����ţ�������ڱ��Ԫ�У��˲���Ӧ����Ϊ-1
     * @param nLineIndex long �к�
     * �����е��кţ����к���������ڵ�����
     * @param nFieldIndex long Ԫ�����
     * Ԫ�ص����
     * @param strOld String ���滻������
     * ���滻������
     * @param strNew String �滻����
     * �滻������
     * @return boolean �ɹ�����TRUE��ʧ�ܷ���FALSE
     * ���nBaseLineIndex��nCellIndex��nLineIndex��nFieldIndex��Ϊ-1��������Ķ����ǵ�ǰԪ��
     */
    public boolean replaceFieldText(long nBaseLineIndex, long nCellIndex,
                                    long nLineIndex, long nFieldIndex,
                                    String strOld, String strNew) {
        int value;
        out("begin nBaseLineIndex=" + nBaseLineIndex + " nCellIndex=" +
            nCellIndex + " nLineIndex=" + nLineIndex + " nFieldIndex=" +
            nFieldIndex + " strOld=" + strOld + " strNew=" + strNew);
        value = ReplaceFieldText(getHWND(), nBaseLineIndex, nCellIndex,
                                 nLineIndex, nFieldIndex, strOld, strNew);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * �����޶��汾
     * ���������������޶��汾��Ϣ
     * @param nRevisialID long �޶���
     * �޶��ţ�-1,���޶�����ϵͳ�Զ�����,>0����Ϊ�ֹ������޶���,����˺��Ѿ�����,������������,������-1
     * @param nAuthorization int Ȩ�޵ȼ�
     * Ȩ�޵ȼ���0Ϊ��ͨȨ�ޣ��������ֵ��>=0���ȼ���ֵ�������޸ĺ�ɾ���ĺۼ���������Ŀ
     * @param strUserName String �û���
     * ��ǰ�������û���
     * @param strUserCode String �û�����
     * ��ǰ�������û�����
     * @param strExtent String ��ע��Ϣ
     * ��Ҫ�Ǽǵ���չ��Ϣ����nRevisialID>0ʱ,���ݸ�ʽΪ���޶�ʱ��;�ն�����
     * @return long �ɹ����ط��ص�ǰ���ĵ����޶�������ʧ�ܷ���-1
     * ���������������޶��ĺۼ���ϵͳĬ��ȡ���ĵ������һ���޶���Ϣ����������Ҫ�ı��޶���
     * ��Ҫ���û��ܲ���ǰ���趨��ͬһ���޶��ۼ�����Ϣ��ͬ��
     */
    public long setRevisalInfo(long nRevisialID, int nAuthorization,
                               String strUserName, String strUserCode,
                               String strExtent) {
        long value;
        out("begin nRevisialID=" + nRevisialID + " nAuthorization=" +
            nAuthorization + " strUserName=" + strUserName + " strUserCode=" +
            strUserCode + " strExtent=" + strExtent);
        value = SetRevisalInfo(getHWND(), nRevisialID, nAuthorization,
                               strUserName, strUserCode, strExtent);
        out("end value=" + value);
        return value;
    }

    /**
     * ���û���ѡ�з�Χ
     * �������������û��е�ѡ�з�Χ
     * @param nStartIndex long ��ʼ�к�
     * ��ʼ�кţ���0��ʼ
     * @param nEndIndex long �����к�
     * �����кţ����Ϊ��������-1
     * @return boolean
     * �ɹ�����TRUE��ʧ�ܷ���FALSE
     */
    public boolean selectBaseLineRange(long nStartIndex, long nEndIndex) {
        int value;
        out("begin nStartIndex=" + nStartIndex + " nEndIndex=" + nEndIndex);
        value = SelectBaseLineRange(getHWND(), nStartIndex, nEndIndex);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * ��ȡ�������Ŀ
     * ���������ڻ�ȡ�������Ŀ
     * @param nBaseLineIndex long �����к�
     * ���е��к�
     * @param nCellIndex long ���Ԫ�����
     * ���Ԫ����ţ�������Ǳ��Ԫ��������Ϊ-1
     * @param nLineIndex long �к�
     * �к�
     * @param nType long ���
     * ͳ�Ƶ����0ָ���е�Ԫ�ظ�����
     * 1 �����ǰ���Ǳ���򷵻ر��ĵ�Ԫ�������򷵻�-1��
     * 2 �����ǰ���Ǳ���򷵻ر������������򷵻�-1��
     * 3 �����ǰ���Ǳ���򷵻ر������������򷵻�-1��
     * 4 �����ǰ���Ǳ���򷵻ر��ĵ�Ԫ���������������򷵻�-1
     * @return long �ɹ���������>=0��ʧ�ܷ���-1
     */
    public long getObjectCount(long nBaseLineIndex, long nCellIndex,
                               long nLineIndex, long nType) {
        long value;
        out("begin nBaseLineIndex=" + nBaseLineIndex + " nCellIndex=" +
            nCellIndex + " nLineIndex=" + nLineIndex + " nType=" + nType);
        value = GetObjectCount(getHWND(), nBaseLineIndex, nCellIndex,
                               nLineIndex, nType);
        out("end value=" + value);
        return value;
    }

    /**
     * ��ȡ���λ������
     * ���������ڻ�ȡ���λ�ö��������
     * @param nType long ���������
     * 0 ָ����ǰ���е�������
     * 1 ��ǰ���Ԫ������
     * 2 ��ǰ��������
     * 3 ��ǰԪ������
     * @return long �ɹ���������>=0��ʧ�ܷ���-1
     */
    public long getCurObjectIndex(long nType) {
        long value;
        out("begin nType=" + nType);
        value = GetCurObjectIndex(getHWND(), nType);
        out("end value=" + value);
        return value;
    }

    /**
     * ����������Ϊͨ����չ�����ؽ��Ϊ������
     * @param strParam1 String
     * @param strParam2 String
     * @param nParam1 long
     * @param nParam2 long
     * @return boolean
     * strParam1,strParam2,nParam1,nParam2 ����˵������:
     * 2.10.1	�ĵ��޸ı�־ "","",1,0
     * �ĵ�������޸Ĺ��ɹ�����TRUE�����򷵻�FALSE
     * 2.10.2	�ڵ�ǰ���λ�ò����ַ��� "��Ҫ������ַ���","",2,0
     * ����ַ�������ɹ�����TRUE�����򷵻�FALSE
     * �����ǰλ����ѡ��״̬,��ִ�д˺���ʱ����ݵ�ǰ��Ȩ����ִ��ɾ��������
     * 2.10.3	�жϵ�ǰ���λ�õ����Ƿ�ֻ�� "","",3,0
     * ���������ֻ�����ĵ���ֻ��״̬���򷵻�TRUE�����򷵻�FALSE
     * ...
     * ...������������...
     * ��ο�<<���Ӳ����༭��V3.0�����ֲ�.doc>>�ĵ�
     */
    public boolean universalBoolFunction(String strParam1, String strParam2,
                                         long nParam1, long nParam2) {
        boolean value;
        out("begin strParam1=" + strParam1 + " strParam2=" + strParam2 +
            " nParam1=" + nParam1 + " nParam2=" + nParam2);
        value = UniversalBoolFunction(getHWND(), strParam1, strParam2, nParam1,
                                      nParam2) == 1;
        out("end value=" + value);
        return value;
    }

    /**
     * ����������Ϊͨ����չ�����ؽ��Ϊ�ַ���
     * @param strParam1 String
     * @param strParam2 String
     * @param strParam3 String
     * @param strParam4 String
     * @param strParam5 String
     * @param strParam6 String
     * @return String
     * ...
     * ...������������...
     * ��ο�<<���Ӳ����༭��V3.0�����ֲ�.doc>>�ĵ�
     */
    public String universalStringFunction(String strParam1, String strParam2,
                                          String strParam3, String strParam4,
                                          String strParam5, String strParam6) {
        String value;
        out("begin strParam1=" + strParam1 + " strParam2=" + strParam2 +
            " strParam3=" + strParam3 + " strParam4=" + strParam4 +
            " strParam5=" + strParam5 + " " + strParam6);
        value = UniversalStringFunction(getHWND(), strParam1, strParam2,
                                        strParam3, strParam4, strParam5,
                                        strParam6);
        out("end value=" + value);
        return value;
    }

    /**
     * ���ص�ǰ�Ĺ��λ��
     * ���������ڷ��ص�ǰ�Ĺ��λ��
     * @return EMRPadCurCursor
     */
    public EMRPadCurCursor getCurCursorPos() {
        out("begin");
        EMRPadCurCursor value = new EMRPadCurCursor();
        String s = universalStringFunction("1", "", "", "", "", "24");
        StringTokenizer stk = new StringTokenizer(s, "|");
        try {
            value.nBaseLineIndex = Long.parseLong(stk.nextToken());
            value.nCellIndex = Long.parseLong(stk.nextToken());
            value.nLineIndex = Long.parseLong(stk.nextToken());
            value.nFieldElemIndex = Long.parseLong(stk.nextToken());
            value.nCharPos = Long.parseLong(stk.nextToken());
        }
        catch (Exception e) {
            err("Exception:" + e.getMessage());
        }
        out("end value=" + value);
        return value;
    }

    /**
     * ��ʼ��OCX���
     * ע��˷�����Ҫ�����ڸ��ര�ڵ�setVisible(true)����֮�����Ч.
     */
    public void initOCX() {
        out("begin");
        checkInit();
        UniversalBoolFunction(getHWND(), OCX_ID, "", 98, INIT_ID);
        ( (JComponent)this.getParent()).
            addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                hidePopup();
            }

            public void ancestorRemoved(AncestorEvent event) {
                hidePopup();
            }

            public void ancestorMoved(AncestorEvent event) {
                hidePopup();
            }
        });
        thread.start();
        out("end");
    }

    /**
     * �ر�OCX���
     */
    public void closeOCX() {
        out("begin");
        thread = null;
        while (threadStart)
            try {
                Thread.sleep(1);
            }
            catch (Exception e) {}
        out("closeOCX=" + closeOCX(this.getHWND()));
        while (UniversalBoolFunction(getHWND(), "", "", 3002, 0) == 1) {
            try {
                Thread.sleep(1);
            }
            catch (Exception e) {}
            out("1");
        }
        out("end");
    }

    public void popMenu(JPopupMenu popup, int x, int y) {
        out("begin x=" + x + " y=" + y);
        if (frame == null)
            return;
        if (popMenuWindow == null) {
            popMenuWindow = new TWindow(frame);
            popMenuWindow.setVisible(true);
        }

        //Point p = getLocationOnScreen();
        popMenuWindow.setLocation(frame.getX(), frame.getY());
        popMenuWindow.setSize(0, 0);
        popup.show(popMenuWindow, x, y);
        popup.repaint();
        this.popup = popup;
        out("end");
    }

    public void hidePopup() {
        if (popup != null && popup.isVisible()) {
            popup.setVisible(false);
            popup = null;
        }
    }

    class Eventlistener
        implements Runnable {
        public void run() {
            int sleepCount = 1;
            out("begin");
            threadStart = true;
            while (thread != null) {
                try {
                    Thread.sleep(sleepCount);
                    //ѡ��keyword
                    if (thread == null)
                        break;
                    if (UniversalBoolFunction(getHWND(), "", "", 3004, 0) == 1)
                        SelectKeyWordEvent();
                    Thread.sleep(sleepCount);
                    if (thread == null)
                        break;
                    //�Ҽ�����
                    if (UniversalBoolFunction(getHWND(), "", "", 3005, 0) == 1)
                        hidePopup();
                    Thread.sleep(sleepCount);
                    if (thread == null)
                        break;
                    //�������
                    if (UniversalBoolFunction(getHWND(), "", "", 3006, 0) == 1)
                        hidePopup();
                    Thread.sleep(sleepCount);
                    if (thread == null)
                        break;
                    //�Ҽ�̧��
                    if (UniversalBoolFunction(getHWND(), "", "", 3007, 0) == 1)
                        RClickEvent();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            threadStart = false;
            out("end");
        }
    }


    public void SelectKeyWordEvent() {
        out("begin");
        /*if(parentObject == null)
          return;
         parentObject.eventListener(tag,DyEventListener.ValueChanged,this);*/
        out("end");
        //System.out.println("SelectKeyWordEvent()" + GetFieldText(-1,-1,-1,-1,true));
    }

    public void RClickEvent() {
        out("begin");
        /*if(parentObject == null)
          return;
         parentObject.eventListener(tag,DyEventListener.MouseClicked,this);*/
        out("end");
    }

    /**
     * ��̬����� EMRPad30_JavaHis.dll �ļ�
     */
    static {
        System.loadLibrary("EMRPad30_JavaHis");
    }

    /**
     * ��־���
     * @param text String ��־����
     */
    public void out(String text) {
        log.out(text);
    }

    /**
     * ��־���
     * @param text String ��־����
     * @param debug boolean true ǿ����� false ��ǿ�����
     */
    public void out(String text, boolean debug) {
        log.out(text, debug);
    }

    /**
     * ������־���
     * @param text String ��־����
     */
    public void err(String text) {
        log.err(text);
    }
}
