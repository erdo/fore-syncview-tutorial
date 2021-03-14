---
title: Tutorial: Spot the deliberate bug
published: true
description:  We implement the syncView() example discussed in the fore docs as a real life android app
cover_image: https://thepracticaldev.s3.amazonaws.com/i/srs8486xrglptufb8e6y.png
tags: Android, Kotlin, fore, MVO
---

_This is part of a series on android [fore](https://erdo.github.io/android-fore/)_

| Tutorials in Series                 |
|:------------------------------------|
|1) Tutorial: [Spot the deliberate bug](https://dev.to/erdo/tutorial-spot-the-deliberate-bug-165k) |
|2) Tutorial: [Android fore basics](https://dev.to/erdo/tutorial-android-fore-basics-1155) |
|3) Tutorial: [Android architecture, full todo app (MVO edition)](https://dev.to/erdo/tutorial-android-architecture-blueprints-full-todo-app-mvo-edition-259o) |
|4) Tutorial: [Android state v. event](https://dev.to/erdo/tutorial-android-state-v-event-3n31)|
|5) Tutorial: [Kotlin Coroutines, Retrofit and fore](https://dev.to/erdo/tutorial-kotlin-coroutines-retrofit-and-fore-3874)|

Don't worry if you're not an Android developer for this one, anyone can spot this bug (but surprisingly few do until it's too late - even with this warning!).

We are about to illustrate how complicated UI code can become (even for simple views), and then what to do about it.

Here's an ugly looking UI for a queue busting app that lets roaming staff sell $1 bottles of water at a festival.

![ugly water seller app](https://thepracticaldev.s3.amazonaws.com/i/6u44lbyharznmnht6sfq.png)
<figcaption>ugly water seller app</figcaption>

As usual with [MVO](https://erdo.github.io/android-fore/00-architecture.html#shoom), we started by writing a model first (see the [**second tutorial**](https://dev.to/erdo/tutorial-android-fore-basics-1155) if you want to know how). We won't discuss the model here any further, other than to say: we called it **Basket**; it is _Observable_; it's quick and easy to unit test; the full kotlin source is in the github repo linked to below; and it has these public functions:

``` kotlin
fun addBottle()
fun removeBottle()
fun setIsDiscounted(isDiscounted: Boolean)

fun getTotalItems(): Int
fun getTotalPrice(): Int
fun getIsDiscounted(): Boolean
fun getTotalSaving(): Int
```

# The View Code
The rule of thumb for applying syncView() in MVO is: **_"If a model being observed changes in any way, then the entire view is refreshed."_**.

Let's first see what happens when we **don't** do that (i.e. how a lot of view layer code is written - especially in event driven architectures like MVP for example):

**Step 1 of 3**

Let's start super simple:

![ugly water seller app, first version](https://thepracticaldev.s3.amazonaws.com/i/0ah0red6lvbb6e6nhmwi.png)
<figcaption>ugly water seller app, first version</figcaption>

All we need to do is hook up the **add** and **remove** buttons in the UI and make sure we remember to update the **total price**. Something like this would be typical:

``` kotlin
addButton.setOnClickListener {
    basket.addBottle()
    updateTotalPriceView()
}

removeButton.setOnClickListener {
    basket.removeBottle()
    updateTotalPriceView()
}
```
Don't worry about how we get the references for **addButton** and **removeButton** - this works fine as pseudo code (you can check the github source below if you're interested).

For the **updateTotalPriceView()** call we will end up setting the text in the UI to what the model tells us. In a real app, depending on the architecture in place, the code will often go through various components first, but if we follow it through to the end, we will eventually reach a piece of code that does something like this:
``` kotlin
totalPriceView.text = basket.getTotalPrice()
```


**Step 2 of 3**

The designers have some inspiration and decide they want to add a basket icon:

![ugly water seller app, second version](https://thepracticaldev.s3.amazonaws.com/i/00tyvjpa6tpnrtj8nqhv.png)
<figcaption>ugly water seller app, second version</figcaption>

This change is pretty easy as our model already supports it, so we just add an **updateTotalNumberOfItemsView()** method, which does exactly what you think it does. We'll also change the button click listeners so that they call both updateTotalPriceView() and updateTotalNumberOfItemsView()

``` kotlin
addButton.setOnClickListener {
    basket.addBottle()
    updateTotalPriceView()
    updateTotalNumberOfItemsView()
}

removeButton.setOnClickListener {
    basket.removeBottle()
    updateTotalPriceView()
    updateTotalNumberOfItemsView()
}
```


**Step 3 of 3**

The business asks us to support a 10% discount for students. No problem, because we read ahead (...ahem), our model already has that capability.

![ugly water seller app, final version](https://thepracticaldev.s3.amazonaws.com/i/zdak5inya4lluftj7zv9.png)
<figcaption>ugly water seller app, final version</figcaption>

So for the discount checkbox, we call **basket.setIsDiscounted(isChecked)** and then refresh the UI: **updateTotalSavingsView()** which shows the discount amount that has been applied, and also **updateTotalPriceView()** as that will have changed.

We **don't** call **updateTotalNumberOfItemsView()** (because of course, discounts have no effect there).

``` kotlin
apply10PercOff.setOnCheckedChangeListener { isChecked ->
    basket.setIsDiscounted(isChecked)
    updateTotalPriceView()
    updateTotalSavingsView()
}
```

We end up with something like this to implement the view layer (this doesn't compile as we're only showing the important bits here - see the github repo for the full code):

``` kotlin
lateinit var addItemButton: Button
lateinit var removeItemButton: Button
lateinit var apply10PercOff: CheckBox

lateinit var totalItemsView: TextView
lateinit var totalPriceView: TextView
lateinit var totalSavingView: TextView

private fun setupButtonListeners() {

  addButton.setOnClickListener {
      basket.addBottle()
      updateTotalPriceView()
      updateTotalNumberOfItemsView()
  }

  removeButton.setOnClickListener {
      basket.removeBottle()
      updateTotalPriceView()
      updateTotalNumberOfItemsView()
  }

  apply10PercOff.setOnCheckedChangeListener { isChecked ->
      basket.setIsDiscounted(isChecked)
      updateTotalPriceView()
      updateTotalSavingsView()
  }
}

private fun updateTotalNumberOfItemsView() {
    totalItemsView.text = basket.getTotalItems()
}

private fun updateTotalPriceView() {
    totalPriceView.text = basket.getTotalPrice()
}

private fun updateTotalSavingsView() {
    totalSavingView.text = basket.getTotalSaving()
}

private fun updatePostRotation(){
    updateTotalNumberOfItemsView()
    updateTotalPriceView()
    updateTotalSavingsView()
}
```
The **updatePostRotation()** method is there to handle android rotations. We probably also want some code to disable the remove button when the basket is empty etc, but this will do for our purposes - **it's already complicated enough**.

# The Bug

The reason I say it's complicated enough, is that we already managed to introduce the bug. Did you spot it? How about if we just concentrate on the click listeners? (it's not a syntax bug, the bug is still present below if we treat the click listeners as psuedo code)

``` kotlin
  addButton.setOnClickListener {
      basket.addBottle()
      updateTotalPriceView()
      updateTotalNumberOfItemsView()
  }

  removeButton.setOnClickListener {
      basket.removeBottle()
      updateTotalPriceView()
      updateTotalNumberOfItemsView()
  }

  apply10PercOff.setOnCheckedChangeListener { isChecked ->
      basket.setIsDiscounted(isChecked)
      updateTotalPriceView()
      updateTotalSavingsView()
  }
```

In case you haven’t worked out the bug yet, follow the code in the click listeners when these actions are performed in sequence (focus on the amount in the savings field):

* start with an empty basket
* select the discount checkbox first
* then add an item

![recreating the bug](https://thepracticaldev.s3.amazonaws.com/i/txm92nh2ob03gwlnxfgs.png)
<figcaption>recreating the bug</figcaption>

**We forgot to call updateTotalSavingsView() from the add and remove click listeners**, so the savings value will be incorrect in the view unless the discount checkbox is toggled again.

UI consistency bugs like this happen all the time, even in simple views.

Unfortunately the view layer is slow and annoying to test (at least on Android), and even with automated tests, these types of bugs are very hard to spot. In this case, an automated test or human tester would have had to have performed specific actions **in the correct sequence** just to see it.

And this is why syncView() does what it does. **_"If a model being observed changes in any way, then the entire view is refreshed."_**.

Applying that principle, our code can be re-written like this:

``` kotlin

lateinit var addItemButton: Button
lateinit var removeItemButton: Button
lateinit var apply10PercOff: CheckBox

lateinit var totalItemsView: TextView
lateinit var totalPriceView: TextView
lateinit var totalSavingView: TextView

private fun setupButtonListeners() {

  addButton.setOnClickListener {
      basket.addBottle()
  }

  removeButton.setOnClickListener {
      basket.removeBottle()
  }

  apply10PercOff.setOnCheckedChangeListener { isChecked ->
      basket.setIsDiscounted(isChecked)
  }
}

fun syncView() {
    totalItemsView.text = basket.getTotalItems()
    totalPriceView.text = basket.getTotalPrice()
    totalSavingView.text = basket.getTotalSaving()
}
```

This skips some details, if you want to know how it's all hooked up that's discussed [here](https://erdo.github.io/android-fore/03-reactive-uis.html#connecting-views-and-models). For our purposes it's good enough to know that whenever our basket model changes, syncView() gets called.

_What's surprising about this, is that it's not only **more robust**, it’s also **less code**. (And any android view can be written like this, including those using adapters)._

Here are some [tips](https://erdo.github.io/android-fore/03-reactive-uis.html#writing-an-effective-syncview-method) for writing great syncView() functions.

![gif showing the app rotating](https://thepracticaldev.s3.amazonaws.com/i/eox7auhypfsnx0j18pgl.gif)
<figcaption>full app, rotation support as standard</figcaption>

This very simple app has no animation code, but it is: clear; robust; has no memory leaks; it's testable; and it supports rotation - which is a great place to be with android before you start adding beautiful animations and finishing touches.

-----

Thanks for reading! I hope you got something out of that, even if you're not an Android developer. If you're thinking of using fore in your team for an android project, the fore docs have most of the basics covered in easy to digest sample apps, e.g. [adapters](https://erdo.github.io/android-fore/#fore-3-adapter-example), [networking](https://erdo.github.io/android-fore/#fore-4-retrofit-example) or [databases](https://erdo.github.io/android-fore/#fore-6-db-example-room-db-driven-to-do-list).

here’s the [complete code](https://github.com/erdo/fore-syncview-tutorial) for the tutorial
