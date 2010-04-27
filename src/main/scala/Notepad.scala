package eg.notepad

import _root_.android.app.ListActivity
import _root_.android.os.Bundle
import _root_.android.widget.TextView
import _root_.android.view.{ Menu, MenuItem }

object NotepadMenu {
  val INSERT_ID = Menu.FIRST
}

class Notepad extends ListActivity {

  val dbHelper = new NotesDbAdapter(this)

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.notepad_list)
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
      case _ => false
    }
  }

  private def createNote() {
  }

  private def fillData() {
  }

}
