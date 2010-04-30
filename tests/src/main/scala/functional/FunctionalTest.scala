package functional

import eg.notepad._
import android.test.ActivityInstrumentationTestCase2
import com.jayway.android.robotium.solo.Solo

abstract class FunctionalTest extends ActivityInstrumentationTestCase2[Notepad]("eg.notepad", classOf[Notepad]) {
  lazy val solo = new Solo(getInstrumentation, getActivity)
  lazy val dbHelper = new DatabaseHelper(getActivity)
  lazy val db = dbHelper.getWritableDatabase

  override def setUp() {
    super.setUp()
    dbHelper.onUpgrade(db, 0, 0)
  }

  override def tearDown() {
    super.tearDown()
    dbHelper.onUpgrade(db, 0, 0)
  }
}
