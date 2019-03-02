package foo.bar.example.forewater

import android.app.Application
import co.early.fore.core.WorkMode
import co.early.fore.core.logging.AndroidLogger
import co.early.fore.core.logging.Logger
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideApplication(): Application {
        return app
    }

    @Provides
    @Singleton
    fun provideWorkMode(): WorkMode {
        return WorkMode.ASYNCHRONOUS
    }

    @Provides
    @Singleton
    fun provideLogger(androidLogger: AndroidLogger): Logger {
        return androidLogger
    }

    @Provides
    @Singleton
    fun provideAndroidLogger(): AndroidLogger {
        val androidLogger = AndroidLogger("forewater_")
        androidLogger.i(TAG, "created logger")
        return androidLogger
    }

    companion object {
        val TAG = AppModule::class.java.simpleName
    }
}
