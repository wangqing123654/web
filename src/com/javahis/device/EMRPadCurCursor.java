package com.javahis.device;

/**
 *
 * <p>Title: EMRPad30 GetCurCursorPos�������ض���</p>
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
public class EMRPadCurCursor {
    /**
     * ���к�
     * ���淵�صĻ��кš���һ�����е������0,�������-1,��˵���˺������ص�����ֵ����Ч
     */
    public long nBaseLineIndex;


    /**
     * ��Ԫ���
     * ���淵�صĵ�Ԫ��Ҫ,�����ǰ��겻�ڱ����,�򷵻�-1,����һ����Ԫ��������0
     */
    public long nCellIndex;


    /**
     * �к�
     * ���淵�صĵ�ǰ�е��кŴ�0��ʼ
     */
    public long nLineIndex;


    /**
     * Ԫ�����
     * ���淵�صĵ�ǰԪ����Ŵ�0��ʼ
     */
    public long nFieldElemIndex;


    /**
     * �ַ�λ��
     * ���淵�صĵ�ǰ�ַ�λ�ô�0��ʼ
     */
    public long nCharPos;
    public String toString() {
        return "nBaseLineIndex=" + nBaseLineIndex + ",nCellIndex=" + nCellIndex +
            ",nLineIndex=" + nLineIndex + ",nFieldElemIndex=" + nFieldElemIndex +
            ",nCharPos=" + nCharPos;
    }
}
