package com.bill.mygitosc.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.bill.mygitosc.R;

/**
 * Created by liaobb on 2015/8/6.
 */
public class ClearEditText extends EditText implements OnFocusChangeListener, TextWatcher {
    //private Context context;
    /**
     * ɾ����ť������
     */
    private Drawable mClearDrawable;
    /**
     * �ؼ��Ƿ��н���
     */
    private boolean hasFoucs;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
        //super(context,attrs);
        //initView();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /*TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText);
        mClearDrawable = typedArray.getDrawable(R.styleable.ClearEditText_clear_style);
        typedArray.recycle();*/
        initView();
    }

    private void initView() {
        //��ȡEditText��DrawableRight,����û���������ԣ���ʹ�����Ǿ�ʹ��delete_selector
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.drawable.delete_selector);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        //Ĭ����������ͼ��
        setClearIconVisible(false);
        //���ý���ı�ļ���
        setOnFocusChangeListener(this);
        //����������������ݷ����ı�ļ���
        addTextChangedListener(this);
    }

    /**
     * ��Ϊ���ǲ���ֱ�Ӹ�EditText���õ���¼������������ü�ס���ǰ��µ�λ����ģ�����¼�
     * �����ǰ��µ�λ�� ��  EditText�Ŀ�� - ͼ�굽�ؼ��ұߵļ�� - ͼ��Ŀ��  ��
     * EditText�Ŀ�� - ͼ�굽�ؼ��ұߵļ��֮�����Ǿ�������ͼ�꣬��ֱ�����û�п���
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mClearDrawable != null) {
                boolean touchable = event.getX() > (getWidth() - getPaddingRight() - mClearDrawable.getIntrinsicWidth())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /**
     * ��������������ݷ����仯��ʱ��ص��ķ���
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count,
                              int after) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }
    }

    /**
     * �������ͼ�����ʾ�����أ�����setCompoundDrawablesΪEditText������ȥ
     *
     * @param visible
     */
    private void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }
}
