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

        // unfortunately the android test runner calls Application.onCreate() once _before_ we get a
        // chance to call createApplication() in ApplicationTestCase (contrary to the documentation).
        // So to prevent initialisation stuff happening before we have had a chance to set our mocks
        // during tests, we need to separate out the init() stuff, which is why we put it here,
        // to be called by the base activity of the app
        // http://stackoverflow.com/questions/4969553/how-to-prevent-activityunittestcase-from-calling-application-oncreate
        fun init() {

            // run any initialisation code here

        }
    }
}
