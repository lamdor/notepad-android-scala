package eg.notepad

import _root_.android.app.{ Activity, ListActivity }
import _root_.android.os.Bundle
import _root_.android.content.Intent
import _root_.android.database.Cursor
import _root_.android.widget._
import _root_.android.view.{ Menu, MenuItem, ContextMenu, View }
import _root_.android.util.Log

object NotepadMenu {
  val INSERT_ID = Menu.FIRST
  val DELETE_ID = Menu.FIRST + 1
}

object NotepadActivities {
  val ACTIVITY_CREATE = 0
  val ACTIVITY_EDIT = 1
}

class NoteEdit extends Activity {
  var currentNotesCursor: Cursor = _
  val dbHelper = new NotesDbAdapter(this)
  var rowId: Long = -1

  lazy val titleText = findViewById(R.id.title).asInstanceOf[EditText]
  lazy val bodyText = findViewById(R.id.body).asInstanceOf[EditText]
  lazy val confirmButton = findViewById(R.id.confirm).asInstanceOf[Button]

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.note_edit)
    dbHelper.open

    if (savedInstanceState != null) {
      savedInstanceState.getLong(NotesDbSchema.KEY_ROW_ID)
    }

    if (rowId == -1) {
      val extras = getIntent().getExtras
      if (extras != null) {
        rowId = extras.getLong(NotesDbSchema.KEY_ROW_ID)
      }
    }

    populateFields()

    confirmButton.setOnClickListener(new View.OnClickListener {
      override def onClick(view: View) {
        setResult(Activity.RESULT_OK)
        finish()
      }
    })
  }

  override def onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putLong(NotesDbSchema.KEY_ROW_ID, rowId)
  }

  override def onPause() {
    super.onPause()
    saveState()
  }

  override def onResume() {
    super.onResume()
    populateFields()
  }

  private def populateFields() {
    if (rowId != -1) {
      val noteCursor = dbHelper.fetchNote(rowId)
      startManagingCursor(noteCursor)
      titleText.setText(noteCursor.getString(noteCursor.getColumnIndexOrThrow(NotesDbSchema.KEY_TITLE)))
      bodyText.setText(noteCursor.getString(noteCursor.getColumnIndexOrThrow(NotesDbSchema.KEY_BODY)))
    }
  }

  private def saveState() {
    val title = titleText.getText.toString
    val body = bodyText.getText.toString
    if (rowId == -1) {
      rowId = dbHelper.createNote(title, body)
    } else {
      dbHelper.updateNote(rowId, title, body)
    }
  }

}

class Notepad extends ListActivity {

  val dbHelper = new NotesDbAdapter(this)

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.notes_list)
    dbHelper.open
    fillData
    registerForContextMenu(getListView)
  }

  override def onCreateOptionsMenu(menu: Menu) = {
    val result = super.onCreateOptionsMenu(menu)
    menu.add(0, NotepadMenu.INSERT_ID, 0, R.string.menu_insert)
    result
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    item.getItemId match {
      case NotepadMenu.INSERT_ID => {
        createNote()
        true
      }
      case _ => super.onOptionsItemSelected(item)
    }
  }

  override def onCreateContextMenu(menu: ContextMenu, view: View, menuInfo: ContextMenu.ContextMenuInfo) = {
    val result = super.onCreateContextMenu(menu, view, menuInfo)
    menu.add(0, NotepadMenu.DELETE_ID, 0, R.string.menu_delete)
    result
  }

  override def onContextItemSelected(item: MenuItem) = {
    item.getItemId match {
      case NotepadMenu.DELETE_ID => {
        val info = item.getMenuInfo.asInstanceOf[AdapterView.AdapterContextMenuInfo]
        dbHelper.deleteNote(info.id)
        fillData
        true
      }
      case _ => super.onContextItemSelected(item)
    }
  }

  override def onListItemClick(listView: ListView, view: View, position: Int, id: Long) {
    super.onListItemClick(listView, view, position, id)
    val intent = new Intent(this, classOf[NoteEdit])
    intent.putExtra(NotesDbSchema.KEY_ROW_ID, id)
    startActivityForResult(intent, NotepadActivities.ACTIVITY_EDIT)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(resultCode, resultCode, data)
    fillData
  }

  private def createNote() {
    val intent = new Intent(this, classOf[NoteEdit])
    startActivityForResult(intent, NotepadActivities.ACTIVITY_CREATE)
  }

  private def fillData() {
    val currentNotesCursor = dbHelper.fetchAllNotes
    startManagingCursor(currentNotesCursor)

    val from = Array(NotesDbSchema.KEY_TITLE)
    val to = Array(R.id.text1)

    val notes = new SimpleCursorAdapter(this, R.layout.notes_row, currentNotesCursor, from, to)
    setListAdapter(notes)
  }

}
