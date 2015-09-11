package com.vsa.seekbarindicated;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by albertovecinasanchez on 27/8/15.
 */
public class SeekBarIndicated extends FrameLayout implements SeekBar.OnSeekBarChangeListener {

    private ViewGroup mWrapperIndicator;
    private ImageView mImageViewIndicator;
    private TextView mTextViewProgress;
    private SeekBar mSeekBar;
    private RelativeLayout mWrapperSeekBarMaxMinValues;
    private TextView mTextViewMinValue;
    private TextView mTextViewMaxValue;

    private int mSeekBarMarginLeft = 0;
    private int mSeekBarMarginTop = 0;
    private int mSeekBarMarginBottom = 0;
    private int mSeekBarMarginRight = 0;

    private String mIndicatorText;


    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    private TextProvider mTextProviderIndicator;

    private int mMeasuredWidth;

    public SeekBarIndicated(Context context) {
        super(context);
        if(!isInEditMode())
            init(context);
    }

    public SeekBarIndicated(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            init(context, attrs, 0);
    }

    public SeekBarIndicated(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode())
            init(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_seekbar_indicated, this);
        bindViews(view);

        if(attrs != null)
            setAttributes(context, attrs, defStyle);
        mSeekBar.setOnSeekBarChangeListener(this);
        mTextViewProgress.setText(String.valueOf(mSeekBar.getProgress()));

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMeasuredWidth = mSeekBar.getWidth()
                        - mSeekBar.getPaddingLeft()
                        - mSeekBar.getPaddingRight();
                mSeekBar.setPadding(mSeekBar.getPaddingLeft(),
                        mSeekBar.getPaddingTop() + mWrapperIndicator.getHeight(),
                        mSeekBar.getPaddingRight(),
                        mSeekBar.getPaddingBottom());
                setIndicator();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mTextViewMinValue.setText("0");
        mTextViewMaxValue.setText(String.valueOf(mSeekBar.getMax()));
    }

    private void bindViews(View view) {
        mWrapperIndicator = (ViewGroup) view.findViewById(R.id.wrapper_seekbar_indicator);
        mImageViewIndicator = (ImageView) view.findViewById(R.id.img_seekbar_indicator);
        mTextViewProgress = (TextView) view.findViewById(R.id.txt_seekbar_indicated_progress);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mWrapperSeekBarMaxMinValues= (RelativeLayout) view.findViewById(R.id.wrapper_seekbar_max_min_values);
        mTextViewMinValue = (TextView) view.findViewById(R.id.txt_seekbar_min_value);
        mTextViewMaxValue = (TextView) view.findViewById(R.id.txt_seekbar_max_value);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setIndicator();
        if(mOnSeekBarChangeListener != null)
            mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(mOnSeekBarChangeListener != null)
            mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mOnSeekBarChangeListener != null)
            mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
    }

    private void setIndicator() {
        if(mTextProviderIndicator != null) {
            mTextViewProgress.setText(mTextProviderIndicator.provideText(mSeekBar.getProgress()));
        } else {
            if(mIndicatorText != null) {
                try {
                    mTextViewProgress.setText(
                            String.valueOf(String.format(mIndicatorText, mSeekBar.getProgress())));
                } catch (Exception e) {
                    mTextViewProgress.setText(String.valueOf(mSeekBar.getProgress()));
                }
            } else {
                mTextViewProgress.setText(String.valueOf(mSeekBar.getProgress()));
            }
        }
        Rect padding = new Rect();
        mSeekBar.getThumb().getPadding(padding);

        int thumbPos = mSeekBar.getPaddingLeft()
                + mMeasuredWidth
                * mSeekBar.getProgress()
                / mSeekBar.getMax();
        mWrapperIndicator.setX(thumbPos
                - (mWrapperIndicator.getWidth() / 2));
    }

    private void setAttributes(Context context, AttributeSet attrs, int defStyle) {
        //then obtain typed array
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.SeekBarIndicated, defStyle, 0);

        //and get values you need by indexes from your array attributes defined above
        mSeekBarMarginLeft = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_seekbar_marginLeft, 0);
        mSeekBarMarginTop = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_seekbar_marginTop, 0);
        mSeekBarMarginRight = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_seekbar_marginRight, 0);
        mSeekBarMarginBottom = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_seekbar_marginBottom, 0);
        int seekBarThumbId = arr.getResourceId(R.styleable.SeekBarIndicated_seekbar_thumb, 0);
        int seekBarProgressDrawableId = arr.getResourceId(R.styleable.SeekBarIndicated_seekbar_progressDrawable, 0);
        int indicatorPaddingLeft = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_indicator_paddingLeft, 0);
        int indicatorPaddingTop = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_indicator_paddingTop, 0);
        int indicatorPaddingRight = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_indicator_paddingRight, 0);
        int indicatorPaddingBottom = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_indicator_paddingBottom, 0);

        mWrapperIndicator.setPadding(indicatorPaddingLeft, indicatorPaddingTop, indicatorPaddingRight, indicatorPaddingBottom);
        if(seekBarThumbId > 0) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                mSeekBar.setThumb(getResources().getDrawable(seekBarThumbId));
            else
                mSeekBar.setThumb(getResources().getDrawable(seekBarThumbId, null));
        }

        if(seekBarProgressDrawableId > 0) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                mSeekBar.setProgressDrawable(getResources().getDrawable(seekBarProgressDrawableId));
            else
                mSeekBar.setProgressDrawable(getResources().getDrawable(seekBarProgressDrawableId, null));
        }

        mIndicatorText = arr.getString(R.styleable.SeekBarIndicated_indicator_text);
        mWrapperSeekBarMaxMinValues.setPadding(
                mSeekBarMarginLeft + mSeekBar.getPaddingLeft(),
                0,
                mSeekBarMarginRight + mSeekBar.getPaddingRight(),
                0);


        mSeekBar.setPadding(
                mSeekBar.getPaddingLeft() + mSeekBarMarginLeft,
                mSeekBar.getPaddingTop() + mSeekBarMarginTop,
                mSeekBar.getPaddingRight() + mSeekBarMarginRight,
                mSeekBar.getPaddingBottom() + mSeekBarMarginBottom);
        setIndicatorImage(arr);
        setIndicatorTextAttributes(arr);
        arr.recycle();

    }

    private void setIndicatorTextAttributes(TypedArray arr) {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) mTextViewProgress.getLayoutParams();
        int indicatorTextMarginLeft =
                arr.getDimensionPixelSize(
                        R.styleable.SeekBarIndicated_indicator_textMarginLeft,
                        layoutParams.leftMargin);
        int indicatorTextMarginTop =
                arr.getDimensionPixelSize(
                        R.styleable.SeekBarIndicated_indicator_textMarginTop,
                        getContext().getResources()
                                .getDimensionPixelSize(R.dimen.indicator_txt_margin_top));
        int indicatorTextMarginRight =
                arr.getDimensionPixelSize(
                        R.styleable.SeekBarIndicated_indicator_textMarginRight,
                        layoutParams.rightMargin);
        int indicatorTextMarginBottom =
                arr.getDimensionPixelSize(
                        R.styleable.SeekBarIndicated_indicator_textMarginBottom,
                        layoutParams.bottomMargin);

        int indicatorTextColor = arr.getColor(R.styleable.SeekBarIndicated_indicator_textColor, Color.WHITE);

        if(arr.hasValue(R.styleable.SeekBarIndicated_indicator_textCenterHorizontal)) {
            boolean centerHorizontal = arr.getBoolean(
                    R.styleable.SeekBarIndicated_indicator_textCenterHorizontal, false);
            if(centerHorizontal) {
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                if(!arr.hasValue(R.styleable.SeekBarIndicated_indicator_textMarginTop))
                    indicatorTextMarginTop = 0;
            }
        } else {
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }
        if(arr.hasValue(R.styleable.SeekBarIndicated_indicator_textCenterVertical)) {
            boolean centerVertical = arr.getBoolean(
                    R.styleable.SeekBarIndicated_indicator_textCenterVertical, false);
            if(centerVertical)
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        }

        mTextViewProgress.setTextColor(indicatorTextColor);

        layoutParams.setMargins(indicatorTextMarginLeft,
                indicatorTextMarginTop,
                indicatorTextMarginBottom,
                indicatorTextMarginRight);



        mTextViewProgress.setLayoutParams(layoutParams);
    }

    private void setIndicatorImage(TypedArray arr) {
        int imageResourceId = arr.getResourceId(R.styleable.SeekBarIndicated_indicator_src,
                R.drawable.indicator_icon);
        mImageViewIndicator.setImageResource(imageResourceId);

    }


    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    public void setTextProviderIndicator(TextProvider textProviderIndicator) {
        mTextProviderIndicator = textProviderIndicator;
    }

    public int getProgress() {
        return mSeekBar.getProgress();
    }

    public interface TextProvider {
        String provideText(int progress);
    }

}
