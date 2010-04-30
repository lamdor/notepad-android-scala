package functional

import junit.framework.Assert._

class AddNoteTest extends FunctionalTest {

  def testAddNote() {
    addNote("Test Note", "Body")
    assertTrue(solo.searchText("Test Note"))
  }

}
