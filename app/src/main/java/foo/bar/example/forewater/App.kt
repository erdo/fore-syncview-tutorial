package foo.bar.example.forewater

import android.app.Application

/**
 * Try not to fill this class with lots of code, if possible move it to a model somewhere
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
