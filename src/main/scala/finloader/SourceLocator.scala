package finloader

import java.net.URL
import java.io.{FilenameFilter, File}
import scala.collection.JavaConversions._

/**
 * @author Paul Lysak
 *         Date: 25.07.13
 *         Time: 23:31
 */
class SourceLocator(prefix: String) {

  private val filter = new PrefixFilter(prefix)

  def locate(baseUrl: URL): Seq[File] =  {
    val file = new File(baseUrl.toURI)
    val files = file.listFiles(filter)
    files.toSeq
  }

 class PrefixFilter(prefix: String) extends FilenameFilter {
    private def sub(dir: File, name: String): File =
      new File(dir.getAbsolutePath + "/" + name)

    def accept(dir: File, name: String) = {
       name.endsWith(".csv") && name.startsWith(prefix) && sub(dir, name).isFile
    }
  }

}


