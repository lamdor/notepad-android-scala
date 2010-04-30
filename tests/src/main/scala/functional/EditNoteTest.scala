package functional

import junit.framework.Assert._

class EditNoteTest extends FunctionalTest {

  def testEditNote() {
    addNote("Edit Me", "changeme")
    solo.clickOnText("Edit Me")
    // solo.clearEditText(0)
    // solo.clearEditText(1)
    solo.enterText(0, "editted")
    solo.enterText(1, "changed")
    solo.clickOnButton("Confirm")
    assertTrue(solo.searchText("editted"))
  }

}
