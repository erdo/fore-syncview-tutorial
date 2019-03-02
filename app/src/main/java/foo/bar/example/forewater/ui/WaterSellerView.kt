package foo.bar.example.forewater.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import co.early.fore.core.logging.Logger
import co.early.fore.core.ui.SyncableView
import foo.bar.example.forewater.App
import foo.bar.example.forewater.feature.Basket
import foo.bar.example.forewater.ui.widget.Digit
import kotlinx.android.synthetic.main.activity_main.view.*

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class WaterSellerView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), SyncableView {

    //models that we need
    private lateinit var basket: Basket
    private lateinit var logger: Logger

    override fun onFinishInflate() {
        super.onFinishInflate()

        //(get view references handled for us by kotlin tools)

        getModelReferences()

        setClickListeners()
    }

    private fun getModelReferences() {
        basket = App.inst.appComponent.basket
        logger = App.inst.appComponent.logger
    }

    private fun setClickListeners() {
        waterseller_add_btn.setOnClickListener { basket.addBottle() }
        waterseller_remove_btn.setOnClickListener { basket.removeBottle() }
        waterseller_discount_btn.setOnCheckedChangeListener { _, isChecked -> basket.setIsDiscounted(isChecked) }
    }

    override fun syncView() {
        waterseller_basketcount_img.setImageResource(Digit.values()[basket.getTotalItems()].resId)
        waterseller_totalprice_handwrittenprice.setPrice(basket.getTotalPrice())
        waterseller_savingsprice_handwrittenprice.setPrice(basket.getTotalSaving())
        waterseller_add_btn.isEnabled = basket.canIncrease()
        waterseller_remove_btn.isEnabled = basket.canDecrease()
    }
}
