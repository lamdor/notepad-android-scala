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
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.note_edit)

    val titleText = findViewById(R.id.title).asInstanceOf[EditText]
    val bodyText = findViewById(R.id.body).asInstanceOf[EditText]
    val confirmButton = findViewById(R.id.confirm).asInstanceOf[Button]

    val extras = getIntent().getExtras
    var rowId: Long = 0

    if (extras != null) {
      val title = extras.getString(NotesDbSchema.KEY_TITLE)
      val body = extras.getString(NotesDbSchema.KEY_BODY)
      rowId = extras.getLong(NotesDbSchema.KEY_ROW_ID)

      if (title != null) titleText.setText(title)
      if (body != null) bodyText.setText(body)
    }

    confirmButton.setOnClickListener(new View.OnClickListener {
      override def onClick(view: View) {
        val bundle = new Bundle
        bundle.putString(NotesDbSchema.KEY_TITLE, titleText.getText.toString)
        bundle.putString(NotesDbSchema.KEY_BODY, bodyText.getText.toString)
        bundle.putLong(NotesDbSchema.KEY_ROW_ID, rowId)

        val i = new Intent
        i.putExtras(bundle)
        setResult(Activity.RESULT_OK, i)
        finish()
      }
    })

  }
}

class Notepad extends ListActivity {

  var currentNotesCursor: Cursor = _
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
    val c = currentNotesCursor
    c.moveToPosition(position)
    val intent = new Intent(this, classOf[NoteEdit])
    intent.putExtra(NotesDbSchema.KEY_ROW_ID, id)
    intent.putExtra(NotesDbSchema.KEY_TITLE, c.getString(c.getColumnIndexOrThrow(NotesDbSchema.KEY_TITLE)))
    intent.putExtra(NotesDbSchema.KEY_BODY, c.getString(c.getColumnIndexOrThrow(NotesDbSchema.KEY_BODY)))
    startActivityForResult(intent, NotepadActivities.ACTIVITY_EDIT)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(resultCode, resultCode, data)
    val extras = data.getExtras

    val title = extras.getString(NotesDbSchema.KEY_TITLE)
    val body = extras.getString(NotesDbSchema.KEY_BODY)

    requestCode match {
      case NotepadActivities.ACTIVITY_CREATE =>
        dbHelper.createNote(title, body)
      case NotepadActivities.ACTIVITY_EDIT =>
        val id = extras.getLong(NotesDbSchema.KEY_ROW_ID)
        dbHelper.updateNote(id, title, body)
    }

    fillData
  }

  private def createNote() {
    val intent = new Intent(this, classOf[NoteEdit])
    startActivityForResult(intent, NotepadActivities.ACTIVITY_CREATE)
  }

  private def fillData() {
    currentNotesCursor = dbHelper.fetchAllNotes
    startManagingCursor(currentNotesCursor)

    val from = Array(NotesDbSchema.KEY_TITLE)
    val to = Array(R.id.text1)

    val notes = new SimpleCursorAdapter(this, R.layout.notes_row, currentNotesCursor, from, to)
    setListAdapter(notes)
  }

}
