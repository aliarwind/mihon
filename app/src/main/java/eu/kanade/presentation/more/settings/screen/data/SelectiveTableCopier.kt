import android.content.Context
import android.util.Log
import app.cash.sqldelight.db.SqlDriver
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SelectiveTableDbHelper(private val context: Context, private val db: SqlDriver)  {

    companion object {
        private const val ASSETS_DB = "database/tachiyomi_prepare.db"

        // 定义需要复制的表名列表
        private val TABLES_TO_COPY = listOf("authors")
    }


    fun SqlDriver.executeRaw(sql: String) {
        this.execute(null, sql, 0)
    }

    fun copySelectedTables() {
        Log.d("Database copy", "开始复制预填充表...")
        // 临时数据库路径
        val tempDbPath = File(context.cacheDir, "temp_preload.db")

        try {
            // 1. 复制预填充数据库到临时位置
            context.assets.open(ASSETS_DB).use { input ->
                FileOutputStream(tempDbPath).use { output ->
                    input.copyTo(output)
                }
            }

            // 2. 使用ATTACH DATABASE方法
            db.executeRaw("ATTACH DATABASE '${tempDbPath.path}' AS preload")

            // 3. 复制特定表
            TABLES_TO_COPY.forEach { tableName ->
                db.executeRaw("INSERT INTO main.$tableName SELECT * FROM preload.$tableName")
            }

            // 4. 分离数据库
            db.executeRaw("DETACH DATABASE preload")
            Log.d("Database copy", "完成...")
        } catch (e: IOException) {
            throw RuntimeException("Failed to copy selected tables", e)
        } finally {
            tempDbPath.delete()
        }
    }

}
