import sbt._

class GrizzledScalaProject(info: ProjectInfo) extends DefaultProject(info)
{
    // Use a different name for the jar than the one generated by SBT.
    override def artifactID = "grizzled-scala"

    // Compiler options
    override def compileOptions = Unchecked :: super.compileOptions.toList

    // External dependencies

    val scalaToolsRepo = "Scala-Tools Maven Repository" at 
        "http://scala-tools.org/repo-releases/org/scala-tools/testing/scalatest/0.9.5/"

    val jline = "jline" % "jline" % "0.9.94"
    val scalatest = "org.scala-tools.testing" % "scalatest" % "0.9.5"
}
