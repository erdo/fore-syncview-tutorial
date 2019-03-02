package foo.bar.example.forewater.ui

import co.early.fore.lifecycle.LifecycleSyncer
import co.early.fore.lifecycle.activity.SyncableAppCompatActivity
import foo.bar.example.forewater.App
import foo.bar.example.forewater.R

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class MainActivity : SyncableAppCompatActivity() {

    override fun getThingsToObserve(): LifecycleSyncer.Observables {
        return LifecycleSyncer.Observables(App.inst.appComponent.basket)
    }

    override fun getResourceIdForSyncableView(): Int {
        return R.layout.activity_main
    }
}
