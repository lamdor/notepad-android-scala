package functional

import junit.framework.Assert._

class DeleteNoteTest extends FunctionalTest {

  def testDeleteNote() {
    addNote("Delete Me", "")
    solo.clickLongOnTextAndPress("Delete Me", 0)
    assertFalse(solo.searchText("Delete Me"))
  }
}
