/*
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2009 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2009 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "The Grizzled Scala Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M. Clapper.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
  NO EVENT SHALL BRIAN M. CLAPPER BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  ---------------------------------------------------------------------------
*/

package grizzled.string

/**
 * Useful string-related utility functions.
 */
object util
{
    private lazy val QUOTED_REGEX = """(["'])(?:\\?+.)*?\1""".r
    private lazy val WHITE_SPACE_REGEX = """\s+""".r
    private lazy val QUOTE_SET = Set('\'', '"')

    /**
     * <p>Tokenize a string the way a command line shell would, honoring quoted
     * strings and embedded escaped quotes. Single quoted strings must start
     * and end with single quotes. Double quoted strings must start and end
     * with double quotes. Within quoted strings, the quotes themselves may
     * be backslash-escaped. Quoted and non-quoted tokens may be mixed in
     * the string; quotes are stripped.</p>
     *
     * <p>Examples:</p>
     *
     * <blockquote><pre>
     * val s = """one two "three four" ""
     * for (t <- tokenizeWithQuotes(s)) println("|" + t + "|")
     * // Prints:
     * // |one|
     * // |two|
     * // |three four|
     *
     * val s = """one two 'three "four'"""
     * for (t <- tokenizeWithQuotes(s)) println("|" + t + "|")
     * // Prints:
     * // |one|
     * // |two|
     * // |three "four|
     *
     * val s = """one two 'three \'four ' fiv"e"""
     * for (t <- tokenizeWithQuotes(s)) println("|" + t + "|")
     * // Prints:
     * // |one|
     * // |two|
     * // |three 'four |
     * // |fiv"e|
     * </pre></blockquote>
     *
     * @param s  the string to tokenize
     *
     * @return the tokens, as a list of strings
     */
    def tokenizeWithQuotes(s: String): List[String] =
    {
        def fixedQuotedString(qs: String): String =
        {
            val stripped = qs.substring(1, qs.length - 1)
            if (qs(0) == '"')
                stripped.replace("\\\"", "\"")
            else
                stripped.replace("\\'", "'")
        }

        val trimmed = s.trim()

        if (trimmed.length == 0)
            Nil

        else if (QUOTE_SET.contains(trimmed(0)))
        {
            val mOpt = QUOTED_REGEX.findFirstMatchIn(trimmed)
            if (mOpt == None)  // to eol
                List(trimmed)

            else
            {
                val matched = mOpt.get
                val matchedString = matched.toString
                val token = fixedQuotedString(matchedString)
                val past = trimmed.substring(matched.end)
                List(token) ++ tokenizeWithQuotes(past)
            }
        }

        else
        {
            val mOpt = WHITE_SPACE_REGEX.findFirstMatchIn(trimmed)
            if (mOpt == None) // to eol
                List(trimmed)

            else
            {
                val matched = mOpt.get
                val token = trimmed.substring(0, matched.start)
                val past = trimmed.substring(matched.end)
                List(token) ++ tokenizeWithQuotes(past)
            }
        }
    }
}
