package foo.bar.example.forewater.feature

import co.early.fore.core.WorkMode
import co.early.fore.core.logging.Logger
import co.early.fore.core.observer.ObservableImp
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
@Singleton
class Basket @Inject constructor(
    private val logger: Logger,
    workMode: WorkMode
) : ObservableImp(workMode) {

    private val pricePerBottle = 100
    private val maxBottles = 9
    private var bottles = 0
    private var discountOn = false
    private var discountedPrice = 0
    private var nonDiscountedPrice = 0

    fun addBottle() {

        logger.i(LOG_TAG, "addBottle()")

        if (canIncrease()) {
            bottles++
            calculateTotals()
            notifyObservers()
        }
    }

    fun removeBottle() {

        logger.i(LOG_TAG, "removeBottle()")

        if (canDecrease()) {
            bottles--
            calculateTotals()
            notifyObservers()
        }
    }

    fun setIsDiscounted(isDiscounted: Boolean) {
        discountOn = isDiscounted
        notifyObservers()
    }

    fun getTotalItems(): Int {
        return bottles
    }

    fun getTotalPrice(): Int {
        return if (discountOn) discountedPrice else nonDiscountedPrice
    }

    fun getTotalSaving(): Int {
        return if (!discountOn) 0 else nonDiscountedPrice - discountedPrice
    }

    fun getIsDiscounted(): Boolean {
        return discountOn
    }

    fun canIncrease(): Boolean {
        return bottles < maxBottles
    }

    fun canDecrease(): Boolean {
        return bottles > 0
    }

    private fun calculateTotals() {
        nonDiscountedPrice = bottles * pricePerBottle
        discountedPrice = (nonDiscountedPrice * 0.9).toInt()
    }

    companion object {
        private val LOG_TAG = Basket::class.java.simpleName
    }
}
