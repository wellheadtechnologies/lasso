package com.wellhead.lasso
import scala.xml._
import org.slf4j.{Logger,LoggerFactory}
import scala.collection.mutable.{ListBuffer}
import scala.collection.jcl.Conversions._
import java.io.{BufferedWriter,OutputStreamWriter, FileWriter, File}
import joptsimple.{OptionParser,OptionSet}

/*object Main {
  def main(_args:Array[String]) {
    val cmd = _args.first
    val args = _args.drop(1)
    val parser = new OptionParser("o:")
    val optionSet = parser.parse(args:_*)
    val nonOptions = optionSet.nonOptionArguments

    if(isFile(cmd)){ 
      val sources = new ListBuffer[String]
      var destination:String = null
      sources += cmd
      if(optionSet.has("o")){
	for(source <- nonOptions){
	  sources += source
	}
	destination = optionSet.valueOf("o").asInstanceOf[String]
      }
      else {
	sources ++= nonOptions.reverse.drop(1)
	destination = nonOptions.last
      }
      val lasfiles = readSources(sources.toList)
      writeLasFiles(lasfiles, destination)
    }
    else if("pad" == cmd){
      val index = nonOptions(0)
      val target = nonOptions(1)
      val result = padCurve(index, target)
      if(optionSet.has("o")){
	val destination = optionSet.valueOf("o")
	writeCurve(result, destination)
      }
      else {
	writeCurve(result, StdOut)
      }
    }
  }

  def isFile(path:String) = (new File(path)).exists()
  
  def readSources(sources:List[String]):List[LasFile] = {
    sources.map(source => {
      val reader = resolveReader(source)
      reader.readLasFile(source)
    })
  }

  def resolveReader(path:String) = readers.find(_.canRead(path))
  
  lazy val readers = {
    val manifest = XML.loadFile("io_manifest.xml")
    (manifest \\ "reader" \ "@class").map(Class.forName)
  }
    
    
}


*/
