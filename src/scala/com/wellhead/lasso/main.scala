package com.wellhead.lasso
import org.slf4j.{Logger,LoggerFactory}
import scala.collection.mutable.{ListBuffer}
import java.io.{BufferedWriter,OutputStreamWriter, FileWriter}
import joptsimple.{OptionParser,OptionSet}

object Main {
  def main(args:Array[String]) {
    val parser = new OptionParser("p:o:")
    val options = parser.parse(args:_*)
    var lasfile:LasFile = null
    if(options.has("p")){
      lasfile = DefaultLasParser.parseLasFile(options.valueOf("p").toString)
      if(options.has("o")){
	options.valueOf("o") match {
	  case "database" => {
	    val lfdb = new LasFileDB
	    lfdb.saveLasFile(lasfile)
	  }
	}
      }
    }
  }
}


