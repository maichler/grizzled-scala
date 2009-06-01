import org.scalatest.FunSuite
import grizzled.file._

/**
 * Tests the grizzled.file functions.
 */
class FileTest extends GrizzledFunSuite
{
    test("basename")
    {
        val data = Map(("", "/")                 -> "",
                       ("foo", "/")              -> "foo",
                       ("foo/bar", "/")          -> "bar",
                       (".", "/")                -> ".",
                       ("../foo", "/")           -> "foo",
                       ("/foo/bar/baz", "/")     -> "baz",
                       ("////", "/")             -> "/",

                       ("", "\\")                -> "",
                       ("foo", "\\")             -> "foo",
                       ("foo\\bar", "\\")        -> "bar",
                       (".", "\\")               -> ".",
                       ("..\\foo", "\\")         -> "foo",
                       ("\\foo\\bar\\baz", "\\") -> "baz",
                       ("D:\\foo\\bar", "\\")    -> "bar")

        for(((path, sep), expected) <- data)
            expect(expected, "basename(\"" + path + "\", \"" + sep + "\")") 
            { 
                basename(path, sep)
            }
    }

    test("dirname")
    {
        val data = Map(("", "/")                  -> "",
                       ("foo", "/")               -> ".",
                       ("foo/bar", "/")           -> "foo",
                       (".", "/")                 -> ".",
                       ("../foo", "/")            -> "..",
                       ("/foo/bar/baz", "/")      -> "/foo/bar",
                       ("/foo", "/")             -> "/",
                       ("/foo", "/")             -> "/",
                       ("/", "/")                -> "/",
                       ("////", "/")             -> "/",

                       ("", "\\")                -> "",
                       ("foo", "\\")             -> ".",
                       ("foo\\bar", "\\")        -> "foo",
                       (".", "\\")               -> ".",
                       ("..\\foo", "\\")         -> "..",
                       ("\\foo\\bar\\baz", "\\") -> "\\foo\\bar",
                       ("\\foo", "\\")           -> "\\",
                       ("\\foo", "\\")           -> "\\",
                       ("\\", "\\")              -> "\\",
                       ("\\\\\\\\", "\\")        -> "\\")

        for(((path, sep), expected) <- data)
            expect(expected, "dirname(\"" + path + "\", \"" + sep + "\")") 
            {
                dirname(path, sep)
            }
    }

    test("dirnameBasename")
    {
        val data = Map(("", "/")                 -> ("", ""),
                       ("foo", "/")              -> (".", "foo"),
                       ("foo/bar", "/")          -> ("foo", "bar"),
                       (".", "/")                -> (".", ""),
                       ("../foo", "/")           -> ("..", "foo"),
                       ("./foo", "/")            -> (".", "foo"),
                       ("/foo/bar/baz", "/")     -> ("/foo/bar", "baz"),
                       ("/foo", "/")             -> ("/", "foo"),
                       ("/", "/")                -> ("/",  ""),

                       ("", "\\")                -> ("", ""),
                       ("foo", "\\")             -> (".", "foo"),
                       ("foo\\bar", "\\")        -> ("foo", "bar"),
                       (".", "\\")               -> (".", ""),
                       ("..\\foo", "\\")         -> ("..", "foo"),
                       (".\\foo", "\\")          -> (".", "foo"),
                       ("\\foo\\bar\\baz", "\\") -> ("\\foo\\bar", "baz"),
                       ("\\foo", "\\")           -> ("\\", "foo"),
                       ("\\", "\\")              -> ("\\",  ""),
                       ("D:\\foo\\bar", "\\")    -> ("D:\\foo", "bar"))

        for(((path, sep), expected) <- data)
            expect(expected, 
                   "dirnameBasename(\"" + path + "\", \"" + sep + "\")") 
            {
                dirnameBasename(path, sep)
            }
    }

    test("splitPath, Posix")
    {
        val data = Map(
            ("", "/")                  -> List[String](""),
            ("foo", "/")               -> List[String]("foo"),
            ("foo/bar", "/")           -> List[String]("foo", "bar"),
            (".", "/")                 -> List[String]("."),
            ("../foo", "/")            -> List[String]("..", "foo"),
            ("./foo", "/")             -> List[String](".", "foo"),
            ("/foo/bar/baz", "/")      -> List[String]("/foo", "bar", "baz"),
            ("foo/bar/baz", "/")       -> List[String]("foo", "bar", "baz"),
            ("/foo", "/")              -> List[String]("/foo"),
            ("/", "/")                 -> List[String]("/"),

            ("", "\\")                 -> List[String](""),
            ("foo", "\\")              -> List[String]("foo"),
            ("foo\\bar", "\\")         -> List[String]("foo", "bar"),
            (".", "\\")                -> List[String]("."),
            ("..\\foo", "\\")          -> List[String]("..", "foo"),
            (".\\foo", "\\")           -> List[String](".", "foo"),
            ("\\foo\\bar\\baz", "\\")  -> List[String]("\\foo", "bar", "baz"),
            ("foo\\bar\\baz", "\\")    -> List[String]("foo", "bar", "baz"),
            ("\\foo", "\\")            -> List[String]("\\foo"),
            ("\\", "\\")               -> List[String]("\\"),
            ("d:\\", "\\")             -> List[String]("d:\\")
        )
            
        for(((path, sep), expected) <- data)
            expect(expected, "splitPath(\"" + path + "\", \"" + sep + "\")") 
            {
                splitPath(path, sep)
            }
    }

    test("joinPath")
    {
        val data = Map(
            ("/", List(""))                       -> "",
            ("/", List("foo"))                    -> "foo",
            ("/", List("foo", "bar"))             -> "foo/bar",
            ("/", List("."))                      -> ".",
            ("/", List("foo", "bar", "baz"))      -> "foo/bar/baz",
            ("/", List("foo", "bar", "baz", ""))  -> "foo/bar/baz",
            ("/", List("/foo", "bar", "baz"))     -> "foo/bar/baz",
            ("/", List("/foo"))                   -> "/foo",
            ("/", List("/"))                      -> "/",

            ("\\", List(""))                      -> "",
            ("\\", List("foo"))                   -> "foo",
            ("\\", List("foo", "bar"))            -> "foo\\bar",
            ("\\", List("."))                     -> ".",
            ("\\", List("foo", "bar", "baz"))     -> "foo\\bar\\baz",
            ("\\", List("foo", "bar", "baz", "")) -> "foo\\bar\\baz",
            ("\\", List("\\foo", "bar", "baz"))   -> "foo\\bar\\baz",
            ("\\", List("\\foo"))                 -> "\\foo",
            ("\\", List("\\"))                    -> "\\",
            ("\\", List("d:\\"))                  -> "d:\\"
        )
            
        for(((sep, pieces), expected) <- data)
            expect(expected, "joinPath(\"" + pieces + "\", \"" + sep + "\")") 
            {
                joinPath(pieces, sep)
            }
    }

    test("splitDrivePath")
    {


        val data = Map(
            ""                 -> ("", ""),
            ":"                -> ("", ""),
            "D:"               -> ("D:", ""),
            "c:\\"             -> ("c:", "\\"),
            "c:foo"            -> ("c:", "foo"),
            "c:foo\\bar"       -> ("c:", "foo\\bar"),
            "c:\\foo"          -> ("c:", "\\foo"),
            "\\foo"            -> ("", "\\foo"),
            "foo"              -> ("", "foo")
        )

        for((path, expected) <- data)
            expect(expected, "splitDrivePath(\"" + path + "\"") 
            {
                splitDrivePath(path)
            }
    }

    test("fnmatch")
    {
        val data = Map(("foo", "f*")          -> true,
                       ("foo", "f*o")         -> true,
                       ("foo", "f*b")         -> false,
                       ("foo", "*")           -> true,
                       ("foo", "*o")          -> true,
                       ("a.c", "*.c")         -> true,
                       ("abc", "[!a-r]*")     -> false,
                       ("radfa.c", "[!a-r]*") -> false,
                       ("radfa.c", "[^a-r]*") -> false,
                       ("sabc", "[!a-r]*")    -> true,
                       ("sabc", "[^a-r]*")    -> true)

        for(((string, pattern), expected) <- data)
        {
            expect(expected, 
                   "fnmatch(\"" + string + "\", \"" + pattern + "\")")
            {
                fnmatch(string, pattern) 
            }
        }
    }

    test("normalizePosixPath")
    {
        val data = Map("/foo/../bar/////baz" -> "/bar/baz",
                       "///////foo/bar/" -> "/foo/bar",
                       "." -> ".",
                       "" -> ".",
                       "/" -> "/",
                       "//" -> "/",
                       "./." -> ".",
                       "./bar" -> "bar",
                       "///" -> "/",
                       "//////////////////." -> "/")

        for ((path, expected) <- data)
            expect(expected, "normalizePosixPath(\"" + path + "\")") 
            {
                normalizePosixPath(path)
            }
    }

    test("normalizeWindowsPath")
    {
        val data = Map(
            "\\" -> "\\",
            "c:\\foo\\" -> "c:\\foo",
            "c:\\foo" -> "c:\\foo",
            "c:\\foo\\bar" -> "c:\\foo\\bar",
            "\\\\server\\foo" -> "\\\\server\\foo",
            "\\\\server\\foo\\bar\\..\\baz" -> "\\\\server\\foo\\baz",
            "\\foo\\..\\bar\\\\\\\\\\baz" -> "\\bar\\baz",
            "c:\\foo\\bar\\..\\baz" -> "c:\\foo\\baz",
            "\\.." -> "\\",
            "\\..\\.." -> "\\"
        )

        for ((path, expected) <- data)
            expect(expected, "normalizeWindowsPath(\"" + path + "\")") 
            {
                normalizeWindowsPath(path)
            }
    }
}
