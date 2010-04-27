package eg.notepad.tests

import junit.framework.Assert._
import _root_.android.test.AndroidTestCase

class UnitTests extends AndroidTestCase {
  def testPackageIsCorrect {
    assertEquals("eg.notepad", getContext.getPackageName)
  }
}