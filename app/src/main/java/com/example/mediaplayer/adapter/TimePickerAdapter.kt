package com.example.mediaplayer.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cheonjaeung.powerwheelpicker.android.WheelPicker
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.NumberPickerBinding
import kotlin.math.abs

class TimePickerAdapter(isHour: Boolean = false) :
    RecyclerView.Adapter<TimePickerAdapter.ViewHolder>() {
    val items: List<Int> = if (isHour) (0..23).toList() else (0..59).toList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            NumberPickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = items[position]
        holder.bind(number)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: NumberPickerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(number: Int) {
            binding.timeValueText.text = number.toString()
            binding.timeValueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
        }
    }
}

class SampleItemEffector(
    context: Context,
    private val wheelPicker: WheelPicker
) : WheelPicker.ItemEffector() {
    private val orangeColor = ContextCompat.getColor(context, R.color.orange)
    private val whiteColor = ContextCompat.getColor(context, R.color.white)

    private var targetColor = orangeColor
    private var currentColor = whiteColor

    private var colorAnimator: ValueAnimator? = null

    private val wheelPickerHeight: Float
        get() = wheelPicker.measuredHeight.toFloat()

    override fun applyEffectOnScrollStateChanged(
        view: View,
        newState: Int,
        positionOffset: Int,
        centerOffset: Int
    ) {
        val textView = view.findViewById<TextView>(R.id.timeValueText)
        targetColor = if (newState != WheelPicker.SCROLL_STATE_IDLE) {
            orangeColor
        } else {
            whiteColor
        }

        colorAnimator = ValueAnimator.ofArgb(textView.currentTextColor, targetColor).apply {
            duration = 250
            addUpdateListener {
                currentColor = it.animatedValue as Int
                textView?.setTextColor(currentColor)
            }
        }

        colorAnimator?.start()
    }

    override fun applyEffectOnScrolled(
        view: View,
        delta: Int,
        positionOffset: Int,
        centerOffset: Int
    ) {
        view.alpha = 1f - abs(centerOffset) / (wheelPickerHeight / 2f)
        view.scaleX = 1f - abs(centerOffset) / (wheelPickerHeight / 1.2f)
        view.scaleY = 1f - abs(centerOffset) / (wheelPickerHeight / 1.2f)
        val textView = view.findViewById<TextView>(R.id.timeValueText)
        textView?.setTextColor(currentColor)
    }

    override fun applyEffectOnItemSelected(view: View, position: Int) {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)

        val textView = view.findViewById<TextView>(R.id.timeValueText)
        textView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
    }
}