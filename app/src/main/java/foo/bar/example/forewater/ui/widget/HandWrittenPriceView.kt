package foo.bar.example.forewater.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import foo.bar.example.forewater.R
import kotlin.math.pow


/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class HandWrittenPriceView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.widget_handwritten_price, this)
    }

    private lateinit var currentDisplay: MutableList<Digit>
    private var currentPrice = -1

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount < 5) {
            throw RuntimeException("This view needs a layout with at least 5 child imageViews to work")
        }
        setPrice(0)
    }

    fun setPrice(minorUnits: Int) {
        if (currentPrice != minorUnits) {
            currentPrice = minorUnits
            if (numberWithinRange(minorUnits)) {
                currentDisplay = MutableList(childCount) { index ->
                    when (index) {
                        0 -> Digit.DOLLAR
                        (childCount - 3) -> Digit.DOT
                        else -> getDigitForPosition(
                            minorUnits,
                            index,
                            offSet = if (index > (childCount - 3)) 2 else 1 //offset takes into account position of . and $ symbols
                        )
                    }
                }
            } else {
                currentDisplay = MutableList(childCount) { _ -> Digit.DOT }
            }
            currentDisplay.forEachIndexed { index, digit -> (getChildAt(index) as ImageView).setImageResource(digit.resId) }
        }
    }

    private fun numberWithinRange(number: Int): Boolean {
        return bigEnough(number) && smallEnough(number)
    }

    private fun bigEnough(number: Int): Boolean {
        return number > -1
    }

    private fun smallEnough(number: Int): Boolean {
        return number < 10.0.pow(childCount - 2) //2 accounting for $ and .
    }

    private fun getDigitForPosition(amountMinorUnits: Int, index: Int, offSet: Int): Digit {
        var effectiveDigitsSize = childCount - 2  //2 accounting for $ and .
        var effectiveIndex = index - offSet
        return Digit.values()[(amountMinorUnits / 10.0.pow(effectiveDigitsSize - effectiveIndex - 1) % 10).toInt()]
    }
}
