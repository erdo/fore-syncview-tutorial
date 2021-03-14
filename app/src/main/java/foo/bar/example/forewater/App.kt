package foo.bar.example.forewater

import android.app.Application

/**
 * We're using Dagger for DI here, for a pure DI solution please see the
 * sample apps in the fore repo - the repo sample apps are also likely
 * to be more up to date than this sample
 * see: https://erdo.github.io/android-fore/#sample-apps
 *
 * Copyright © 2019 early.co. All rights reserved.
 */
class App : Application() {

    lateinit var appComponent: AppComponent private set

    override fun onCreate() {
        super.onCreate()

        inst = this
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

    fun injectTestAppModule(testAppModule: AppModule) {
        appComponent = DaggerAppComponent.builder().appModule(testAppModule).build()
    }

    companion object {

        lateinit var inst: App private set

        fun init() {
            // run any initialisation code here
        }
    }
}
