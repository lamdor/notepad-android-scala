package eg.notepad

import _root_.android.app.ListActivity
import _root_.android.os.Bundle
import _root_.android.database.Cursor
import _root_.android.widget.{ TextView, SimpleCursorAdapter }
import _root_.android.view.{ Menu, MenuItem }

object NotepadMenu {
  val INSERT_ID = Menu.FIRST
  val DELETE_ID = Menu.FIRST + 1
}

object NotepadActivities {
  val ACTIVITY_CREATE = 0
  val ACTIVITY_EDIT = 1
}

class Notepad extends ListActivity {

  var currentNotesCursor: Cursor = _
  val dbHelper = new NotesDbAdapter(this)

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.notes_list)
    dbHelper.open
    fillData
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

  private def createNote() {
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
