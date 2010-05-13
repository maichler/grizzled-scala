/*---------------------------------------------------------------------------*\
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
import grizzled.string.GrizzledString._

/**
 * Tests the GrizzledString class.
 */
class GrizzledStringTest extends GrizzledFunSuite
{
    test("ltrim")
    {
        val data = Map(
            "a b c"                        -> "a b c",
            "                     a"       -> "a",
            "                     a  "     -> "a  ",
            "                      "       -> "",
            ""                             -> ""
        )

        for((input, expected) <- data)
        {
            expect(expected, "\"" + input + "\" -> " + expected.toString)
            {
                input.ltrim
            }
        }
    }

    test("rtrim")
    {
        val data = Map(
            "a b c"                        -> "a b c",
            "a                     "       -> "a",
            "  a                     "     -> "  a",
            "                      "       -> "",
            ""                             -> ""
        )

        for((input, expected) <- data)
        {
            expect(expected, "\"" + input + "\" -> " + expected.toString)
            {
                input.rtrim
            }
        }
    }

    test("tokenize")
    {
        val data = Map(
            ""                       -> Nil,
            " "                      -> Nil,
            "      "                 -> Nil,
            "\t  "                   -> Nil,
            "   a b    c"            -> List("a", "b", "c"),
            "one  two   three four " -> List("one", "two", "three", "four")
        )

        for((input, expected) <- data)
        {
            expect(expected, "\"" + input + "\" -> " + expected.toString)
            {
                input.tokenize
            }
        }
    }

    test("translateMetachars")
    {
        val data = Map(
            "a b c"                        -> "a b c",
            "\\u2122"                      -> "\u2122",
            "\\t\\n\\afooness"             -> "\t\n\\afooness",
            "\\u212a"                      -> "\u212a",
            "\\u212x"                      -> "\\u212x",
            "\\\\t"                        -> "\\t"
        )

        for ((input, expected) <- data)
        {
            expect(expected, "\"" + input + "\" -> " + expected.toString)
            {
                input.translateMetachars
            }
        }
    }
}
