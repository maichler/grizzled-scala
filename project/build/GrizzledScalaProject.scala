/*
  ---------------------------------------------------------------------------
  This software is released under a BSD license, adapted from
  http://opensource.org/licenses/bsd-license.php

  Copyright (c) 2009, Brian M. Clapper
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.

  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.

  * Neither the names "clapper.org", "Grizzled Scala Library", nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  ---------------------------------------------------------------------------
*/

import sbt._

import java.io.File

/**
 * Project build file, for SBT.
 */
class GrizzledScalaProject(info: ProjectInfo)
    extends DefaultProject(info)
    with posterous.Publish
{
    /* ---------------------------------------------------------------------- *\
                         Compiler and SBT Options
    \* ---------------------------------------------------------------------- */

    // Use a different name for the jar than the one generated by SBT.
    override def artifactID = "grizzled-scala"

    override def compileOptions = Unchecked :: super.compileOptions.toList

    // 2.8 and xsbt: Override documentOptions, because they're for 2.7, not
    // 2.8, and they've changed.
    private val docTitle = projectName + " " + projectVersion + " API"
    override def documentOptions = List(CompoundDocOption("-doc-title",
                                                          docTitle))
                                                     
    override def parallelExecution = true // why not?

    // Disable cross-paths, since we're only building under one version.
    // This simplifies publishing and importing. See
    // http://groups.google.com/group/simple-build-tool/browse_thread/thread/973b5a2956b5ecbe

    override def disableCrossPaths = true

    /* ---------------------------------------------------------------------- *\
                                   Tasks
    \* ---------------------------------------------------------------------- */

    // Override the default "package" action to make it dependent on "test"
    // and "doc". Also, temporarily copy the showdown.js file into the
    // resources directory.
    override def packageAction = task
    {
        val resourceDir = mainResourcesPath / "grizzled"
        val copiedShowdown = resourceDir / ShowdownJS

        FileUtilities.createDirectory(resourceDir, log)
        try
        {
            noisyCopy(ShowdownLocal, copiedShowdown)
            super.packageAction.run
        }

        finally
        {
            for (path <- List(copiedShowdown, resourceDir, mainResourcesPath))
            {
                log.info("Deleting " + path.asFile)
                path.asFile.delete
            }
        }
    }
    .dependsOn(test, doc)

    override def updateAction = customUpdate dependsOn(super.updateAction)

    lazy val customUpdate = task { doManualDownloads }

    override def compileAction = postCompile dependsOn(super.compileAction)

    lazy val postCompile = task { doPostCompile }

    override def cleanLibAction = super.cleanLibAction dependsOn(localCleanLib)
    lazy val localCleanLib = task { doLocalCleanLib }

    /* ---------------------------------------------------------------------- *\
                                Publishing
    \* ---------------------------------------------------------------------- */

    // "publish" will prompt (via a Swing pop-up) for the username and
    // password.
    lazy val publishTo = Resolver.sftp("clapper.org Maven Repo",
                                       "maven.clapper.org",
                                       "/var/www/maven.clapper.org/html")

    override def managedStyle = ManagedStyle.Maven

    /* ---------------------------------------------------------------------- *\
                       Managed External Dependencies
    \* ---------------------------------------------------------------------- */

    val scalaToolsRepo = "Scala-Tools Maven Repository" at
        "http://scala-tools.org/repo-releases"

    val jline = "jline" % "jline" % "0.9.94"

    val newReleaseToolsRepository = "Scala Tools Repository" at
        "http://nexus.scala-tools.org/content/repositories/snapshots/"
    val scalatest = "org.scalatest" % "scalatest" %
        "1.0.1-for-scala-2.8.0.Beta1-with-test-interfaces-0.3-SNAPSHOT"

    val rhino = "rhino" % "js" % "1.7R2"

    val ShowdownJS = "showdown.js"
    val ShowdownURL = "http://attacklab.net/showdown/showdown-v0.9.zip"
    val LocalLibDir = "local_lib"
    val ShowdownLocal = LocalLibDir / ShowdownJS
    val ShowdownClassdir = "target" / "classes" / "grizzled" / ShowdownJS

    /* ---------------------------------------------------------------------- *\
                          Private Helper Methods
    \* ---------------------------------------------------------------------- */

    private def noisyCopy(source: Path, target: Path) =
    {
        log.info("Copying " + source + " to " + target)
        FileUtilities.copyFile(source, target, log)
    }

    private def doManualDownloads: Option[String] =
    {
        // Download, unpack, and save the Showdown package.

        import java.net.URL

        FileUtilities.createDirectory(LocalLibDir, log)
        if (! ShowdownLocal.exists)
        {
            FileUtilities.doInTemporaryDirectory[String](log)
            {
                tempDir: File =>

                log.info("Downloading and unpacking: " + ShowdownURL)
                FileUtilities.unzip(new URL(ShowdownURL),
                                    Path.fromFile(tempDir),
                                    log)

                val js = Path.fromFile(tempDir) / "src" / ShowdownJS
                noisyCopy(js, ShowdownLocal)

                Right("")
            }
        }

        None
    }

    private def doPostCompile: Option[String] =
    {
        noisyCopy(ShowdownLocal, ShowdownClassdir)
    }

    private def doLocalCleanLib: Option[String] =
    {
        if (ShowdownLocal.exists)
        {
            log.info("Deleting " + LocalLibDir);
            FileUtilities.clean(LocalLibDir, log);
        }

        None
    }
}
