package foo.bar.example.forewater.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.early.fore.core.logging.Logger
import co.early.fore.core.observer.Observer
import co.early.fore.core.ui.SyncableView
import foo.bar.example.forewater.App
import foo.bar.example.forewater.R
import foo.bar.example.forewater.feature.Basket
import foo.bar.example.forewater.ui.widget.Digit
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class WaterSellerActivity : AppCompatActivity(R.layout.activity_main), SyncableView {

    //models that we need
    private lateinit var basket: Basket
    private lateinit var logger: Logger

    //single observer reference
    var observer = Observer { syncView() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        waterseller_discount_btn.setOnCheckedChangeListener { _, isChecked ->
            basket.setIsDiscounted(
                isChecked
            )
        }
    }


    //reactive UI stuff below

    override fun syncView() {
        waterseller_basketcount_img.setImageResource(Digit.values()[basket.getTotalItems()].resId)
        waterseller_totalprice_handwrittenprice.setPrice(basket.getTotalPrice())
        waterseller_savingsprice_handwrittenprice.setPrice(basket.getTotalSaving())
        waterseller_add_btn.isEnabled = basket.canIncrease()
        waterseller_remove_btn.isEnabled = basket.canDecrease()
        waterseller_discount_btn.isChecked = basket.getIsDiscounted()
    }

    override fun onStart() {
        super.onStart()
        basket.addObserver(observer)
        syncView() //  <- don't forget this
    }

    override fun onStop() {
        super.onStop()
        basket.removeObserver(observer)
    }
}
