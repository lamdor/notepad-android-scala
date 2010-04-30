import sbt._

trait Defaults {
  def androidPlatformName = "android-7"
}

class Notepad(info: ProjectInfo) extends ParentProject(info) {
  override def shouldCheckOutputDirectories = false
  override def updateAction = task { None }

  lazy val main  = project(".", "Notepad", new MainProject(_))
  lazy val tests = project("tests",  "tests", new TestProject(_), main)

  class MainProject(info: ProjectInfo) extends AndroidProject(info) with Defaults {
    val scalatest = "org.scalatest" % "scalatest" % "1.0" % "test"
  }

  class TestProject(info: ProjectInfo) extends AndroidTestProject(info) with Defaults {
    override def proguardInJars = runClasspath --- proguardExclude

    lazy val robotium = "com.jayway.android" % "robotium" % "1.4.0" from "http://robotium.googlecode.com/files/robotium-solo-1.4.0.jar"
  }
}
