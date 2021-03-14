package foo.bar.example.forewater

import co.early.fore.kt.core.logging.Logger
import dagger.Component
import foo.bar.example.forewater.feature.Basket
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    //expose application scope dependencies we want accessible from anywhere
    val logger: Logger
    val basket: Basket

    //submodules follow
    //operator fun plus(xxxModule: XxxModule): XxxComponent
}
