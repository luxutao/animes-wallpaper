package cn.animekid.animeswallpaper.utils


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DBOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "anime", null, 1) {
    companion object {
        private var instance: DBOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DBOpenHelper {
            if (instance == null) {
                instance = DBOpenHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable("anime_users", true,
                "id" to INTEGER + PRIMARY_KEY  + AUTOINCREMENT,
                "userid" to INTEGER,
                "name" to TEXT,
                "sex" to TEXT,
                "email" to TEXT,
                "create_time" to TEXT,
                "modify_time" to TEXT,
                "token" to TEXT,
                "avatar" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("anime_users", true)
    }
}

// Access property for Context
val Context.database: DBOpenHelper
    get() = DBOpenHelper.getInstance(getApplicationContext())