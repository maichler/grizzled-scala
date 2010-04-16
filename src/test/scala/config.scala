/*---------------------------------------------------------------------------*\
  This software is released under a BSD license, adapted from
  http://opensource.org/licenses/bsd-license.php

  Copyright (c) 2009-2010 Brian M. Clapper. All rights reserved.

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
\*---------------------------------------------------------------------------*/

import org.scalatest.FunSuite
import grizzled.config.Configuration
import scala.io.Source

/**
 * Tests the grizzled.io functions.
 */
class ConfigTest extends GrizzledFunSuite
{
    test("basic configuration")
    {
        import java.io.StringReader

        val configText = """
[section1]
var1 = val1
var2 = val2
[section2]
var1 = foo bar
"""

        val expected = Map(
            Some("val1")    -> ("retrieval", "section1", "var1"),
            Some("val2")    -> ("retrieval", "section1", "var2"),
            Some("foo bar") -> ("retrieval", "section2", "var1"),
            None            -> ("bad variable", "section2", "var2"),
            None            -> ("bad section", "section3", "anything")
        )

        doTest(configText, expected)
    }

    test("variable substitution")
    {
        import java.io.StringReader

        val configText = """
[vars]
foo = foobar

[section1]
home = ${env.HOME}
home2 = ${system.user.home}
foo = ${vars.foo}
bar = ${vars.bar}
"""
        val home = System.getProperty("user.home")

        val expected = Map(
            Some("foobar") -> ("retrieval", "vars", "foo"),
            Some(home)     -> ("substitution", "section1", "home"),
            Some(home)     -> ("substitution", "section1", "home2"),
            Some("foobar") -> ("substitution", "section1", "foo"),
            None           -> ("bad substitution", "section1", "bar")
        )

        doTest(configText, expected, safe=true)
    }

    private def doTest(configString: String,
                       data: Map[Option[String],Tuple3[String,String,String]],
                       safe: Boolean = false) =
    {
        val config = Configuration(Source.fromString(configString), safe)

        for ((expectedResult, inputs) <- data)
        {
            val opIdent  = inputs._1
            val section  = inputs._2
            val variable = inputs._3

            expect(expectedResult, opIdent) {config.get(section, variable)}
        }
    }
}