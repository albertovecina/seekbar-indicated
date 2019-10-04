package com.vsa.sbi

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import kotlinx.android.synthetic.main.view_seekbar_indicated.view.*

/**
 * Created by albertovecinasanchez on 27/8/15.
 */
class SeekBarIndicated : FrameLayout, SeekBar.OnSeekBarChangeListener {

    private lateinit var minValueFormatString: String
    private lateinit var maxValueFormatString: String

    private lateinit var indicatorFormatString: String

    private var seekBarMinValue = 0

    private var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener? = null
    private var textProviderIndicator: TextProvider? = null

    private var seekBarWidth: Int = 0

    private val progress: Int
        get() {
            val unsignedMin = if (seekBarMinValue < 0)
                seekBarMinValue * -1
            else
                seekBarMinValue
            return seekBar.progress + unsignedMin
        }

    constructor(context: Context) : super(context) {
        if (!isInEditMode)
            init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        if (!isInEditMode)
            init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (!isInEditMode)
            init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) {
        LayoutInflater.from(context).inflate(R.layout.view_seekbar_indicated, this)

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBarIndicated, defStyle, 0)
            setProgressBarAttributes(typedArray)
            setIndicatorImageAttrs(typedArray)
            setIndicatorTextStyleAttrs(typedArray)
            setIndicatorTextPositionAttrs(typedArray)
            typedArray.recycle()
        }

        seekBar.setOnSeekBarChangeListener(this)

        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                val indicatorWidth = wrapperSeekBarIndicator.width
                val thumbWidth = seekBar.thumb.intrinsicWidth
                var indicatorOffset = 0

                if (indicatorWidth > thumbWidth)
                    indicatorOffset = (indicatorWidth - thumbWidth) / 2


                seekBar.setPadding(seekBar.paddingLeft + indicatorOffset,
                        seekBar.paddingTop + wrapperSeekBarIndicator.height, //This makes the indicator clickable
                        seekBar.paddingRight + indicatorOffset,
                        seekBar.paddingBottom)
                seekBarWidth = (seekBar.width
                        - seekBar.paddingLeft
                        - seekBar.paddingRight)

                updateIndicatorPosition()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        updateIndicatorPosition()
        onSeekBarChangeListener?.onProgressChanged(seekBar, progress, fromUser)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        onSeekBarChangeListener?.onStartTrackingTouch(seekBar)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        onSeekBarChangeListener?.onStopTrackingTouch(seekBar)
    }

    private fun updateIndicatorPosition() {
        if (textProviderIndicator != null)
            textViewIndicatorProgress.text = textProviderIndicator?.provideText(progress)
        else
            textViewIndicatorProgress.text = String.format(indicatorFormatString, progress)

        wrapperSeekBarIndicator.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val thumbPos = seekBar.paddingLeft + (seekBar.progress * (seekBarWidth / seekBar.max.toFloat()))

                wrapperSeekBarIndicator.x = thumbPos - wrapperSeekBarIndicator.width / 2.0f
                wrapperSeekBarIndicator.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun setProgressBarAttributes(typedArray: TypedArray) {

        minValueFormatString = typedArray.getString(R.styleable.SeekBarIndicated_seekbar_minValueFormatString)
                ?: context.getString(R.string.default_format_string)
        maxValueFormatString = typedArray.getString(R.styleable.SeekBarIndicated_seekbar_maxValueFormatString)
                ?: context.getString(R.string.default_format_string)

        seekBarMinValue = typedArray.getInt(R.styleable.SeekBarIndicated_seekbar_minValue, 0)
        val seekBarMaxValue = typedArray.getInt(R.styleable.SeekBarIndicated_seekbar_maxValue, 100)
        setMinValue(seekBarMinValue)
        setMaxValue(seekBarMaxValue)


        val seekBarThumbId = typedArray.getResourceId(R.styleable.SeekBarIndicated_seekbar_thumb, 0)
        val seekBarProgressDrawableId = typedArray.getResourceId(R.styleable.SeekBarIndicated_seekbar_progressDrawable, 0)

        val indicatorDistance = typedArray.getDimensionPixelSize(R.styleable.SeekBarIndicated_seekbar_indicatorDistance, 0)


        if (seekBarThumbId > 0)
            seekBar.thumb = ContextCompat.getDrawable(context, seekBarThumbId)

        if (seekBarProgressDrawableId > 0)
            seekBar.progressDrawable = ContextCompat.getDrawable(context, seekBarProgressDrawableId)

        wrapperSeekBarMaxMinValues.setPadding(
                seekBar.paddingLeft,
                0,
                seekBar.paddingRight,
                0)

        seekBar.setPadding(
                seekBar.paddingLeft,
                seekBar.paddingTop + indicatorDistance,
                seekBar.paddingRight,
                seekBar.paddingBottom)

    }

    private fun setIndicatorTextStyleAttrs(typedArray: TypedArray) {
        indicatorFormatString = typedArray.getString(R.styleable.SeekBarIndicated_indicator_formatString)
                ?: context.getString(R.string.default_format_string)

        val indicatorTextStyle = typedArray.getInt(R.styleable.SeekBarIndicated_indicator_textStyle, 0)
        textViewIndicatorProgress.setTypeface(textViewIndicatorProgress.typeface, indicatorTextStyle)
        textViewSeekBarMinValue.setTypeface(textViewIndicatorProgress.typeface, indicatorTextStyle)
        textViewSeekBarMaxValue.setTypeface(textViewIndicatorProgress.typeface, indicatorTextStyle)

        val indicatorTextColor = typedArray.getColor(R.styleable.SeekBarIndicated_indicator_textColor, Color.WHITE)
        textViewIndicatorProgress.setTextColor(indicatorTextColor)

    }

    private fun setIndicatorTextPositionAttrs(typedArray: TypedArray) {
        val layoutParams = textViewIndicatorProgress.layoutParams as RelativeLayout.LayoutParams
        val indicatorTextMarginLeft = typedArray.getDimensionPixelSize(
                R.styleable.SeekBarIndicated_indicator_textMarginLeft,
                layoutParams.leftMargin)
        var indicatorTextMarginTop = typedArray.getDimensionPixelSize(
                R.styleable.SeekBarIndicated_indicator_textMarginTop,
                context.resources
                        .getDimensionPixelSize(R.dimen.indicator_txt_margin_top))
        val indicatorTextMarginRight = typedArray.getDimensionPixelSize(
                R.styleable.SeekBarIndicated_indicator_textMarginRight,
                layoutParams.rightMargin)
        val indicatorTextMarginBottom = typedArray.getDimensionPixelSize(
                R.styleable.SeekBarIndicated_indicator_textMarginBottom,
                layoutParams.bottomMargin)


        if (typedArray.hasValue(R.styleable.SeekBarIndicated_indicator_textCenterHorizontal)) {
            val centerHorizontal = typedArray.getBoolean(
                    R.styleable.SeekBarIndicated_indicator_textCenterHorizontal, false)
            if (centerHorizontal) {
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                if (!typedArray.hasValue(R.styleable.SeekBarIndicated_indicator_textMarginTop))
                    indicatorTextMarginTop = 0
            }
        } else {
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        }
        if (typedArray.hasValue(R.styleable.SeekBarIndicated_indicator_textCenterVertical)) {
            val centerVertical = typedArray.getBoolean(
                    R.styleable.SeekBarIndicated_indicator_textCenterVertical, false)
            if (centerVertical)
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        }

        layoutParams.setMargins(indicatorTextMarginLeft,
                indicatorTextMarginTop,
                indicatorTextMarginBottom,
                indicatorTextMarginRight)



        textViewIndicatorProgress.layoutParams = layoutParams
    }

    private fun setIndicatorImageAttrs(typedArray: TypedArray) {
        val imageResourceId = typedArray.getResourceId(R.styleable.SeekBarIndicated_indicator_src,
                R.drawable.indicator_icon)
        imageViewSeekBarIndicator.setImageResource(imageResourceId)

    }

    fun setMaxValue(max: Int, formatString: String = maxValueFormatString) {
        seekBar.max = max - seekBarMinValue
        textViewSeekBarMaxValue.text = String.format(formatString, max)
    }

    fun setMinValue(min: Int, formatString: String = minValueFormatString) {
        seekBarMinValue = min
        textViewSeekBarMinValue.text = String.format(formatString, min)
    }

    fun setValue(value: Int) {
        seekBar.progress = value
        updateIndicatorPosition()
    }

    fun setOnSeekBarChangeListener(onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener
    }

    interface TextProvider {
        fun provideText(progress: Int): String
    }

}
