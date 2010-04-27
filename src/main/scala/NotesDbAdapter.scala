package eg.notepad

import android.content._
import android.database._
import android.database.sqlite._
import android.util.Log

object NotesDbSchema {
  val KEY_TITLE = "title"
  val KEY_BODY = "body"
  val KEY_ROW_ID = "_id"
  val DATABASE_CREATE = "create table notes (_id integer primary key autoincrement, title text not null, body text not null);"

  val DATABASE_NAME = "data"
  val DATABASE_TABLE = "notes"
  val DATABASE_VERSION = 2
}

class DatabaseHelper(val context: Context) extends SQLiteOpenHelper(context, NotesDbSchema.DATABASE_NAME, null, NotesDbSchema.DATABASE_VERSION) {

  override def onCreate(db: SQLiteDatabase) = db.execSQL(NotesDbSchema.DATABASE_CREATE)

  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    Log.w("DatabaseHelper", "Upgrading database from version %s to %s, which will destroy all old data".format(oldVersion, newVersion))
    db.execSQL("DROP TABLE IF EXISTS notes")
    onCreate(db)
  }

}

class NotesDbAdapter(val ctx: Context) {
  import NotesDbSchema._

  lazy val dbHelper = new DatabaseHelper(ctx)
  lazy val db = dbHelper.getWritableDatabase

  def open {
    dbHelper
  }

  def open(f: (SQLiteDatabase) => Unit) {
    f(db)
    dbHelper.close
  }

  def createNote(title: String, body: String) = {
    db.insert(DATABASE_TABLE, null, valuesFor(title, body))
  }

  def deleteNote(id: Long) = db.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + id, null) > 0

  def fetchAllNotes = db.query(DATABASE_TABLE, Array(KEY_ROW_ID, KEY_TITLE, KEY_BODY), null, null, null, null, null)

  def fetchNote(id: Long) = {
    val cursor = db.query(true, DATABASE_TABLE, Array(KEY_ROW_ID, KEY_TITLE, KEY_BODY), KEY_ROW_ID + "=" + id, null, null, null, null, null)
    if (cursor != null) cursor.moveToFirst
    cursor
  }

  def updateNote(id: Long, title: String, body: String) =
    db.update(DATABASE_TABLE, valuesFor(title, body), KEY_ROW_ID + "=" + id, null) > 0

  private def valuesFor(title: String, body: String) = {
    val values = new ContentValues
    values.put(KEY_TITLE, title)
    values.put(KEY_BODY, body)
    values
  }
}
