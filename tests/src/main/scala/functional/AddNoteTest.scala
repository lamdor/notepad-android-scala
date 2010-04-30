package functional

import junit.framework.Assert._

class AddNoteTest extends FunctionalTest {

  def testAddNote() {
    solo.pressMenuItem(0)
    solo.enterText(0, "Test Note")
    solo.enterText(1, "Body")
    solo.clickOnButton("Confirm")
    assertTrue(solo.searchText("Test Note"))
  }

}
