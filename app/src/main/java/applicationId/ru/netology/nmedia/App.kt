package applicationId.ru.netology.nmedia

import android.app.Application
import applicationId.ru.netology.nmedia.db.AppDb

class App : Application() {
    val db by lazy { AppDb.getInstance(this) }
}
