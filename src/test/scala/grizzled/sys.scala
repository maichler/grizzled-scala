/*
  ---------------------------------------------------------------------------
  This software is released under a BSD license, adapted from
  http://opensource.org/licenses/bsd-license.php

  Copyright (c) 2009-2014 Brian M. Clapper. All rights reserved.

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

import org.scalatest.FunSuite
import grizzled.sys._

/**
  * Tests the grizzled.file functions.
  */
class SysTest extends FunSuite {
  test("system properties") {
    for ((k, v) <- systemProperties) {
      val javaVal = System.getProperty(k)
      assertResult(javaVal, "property \"" + k + "\" must be \"" + v + "\"") {v}
    }
  }

  test("operating system name") {
    import OperatingSystem._

    val data = Map("mac"        -> Mac,
                   "windows ce" -> WindowsCE,
                   "windows"    -> Windows,
                   "windows xp" -> Windows,
                   "os/2"       -> OS2,
                   "netware"    -> NetWare,
                   "openvms"    -> VMS,
                   "linux"      -> Posix,
                   "foo"        -> Posix)

    for ((osName, osType) <- data;
         name <- List(osName.capitalize, osName.toUpperCase, osName)) {
      assertResult(osType, "OS name \"" + osName + "\" -> " + osType)  {
        getOS(osName)
      }
    }
  }
}
