package com.wellhead.lasso
import scala.xml._
import org.slf4j.{Logger,LoggerFactory}
import scala.collection.mutable.{ListBuffer}
import scala.collection.jcl.Conversions._
import java.io.{BufferedWriter,OutputStreamWriter, FileWriter, File}
import joptsimple.{OptionParser,OptionSet}

object Main {
  def main(_args:Array[String]) {
    val cmd = _args.first
    val args = _args.drop(1).toArray
    if(isSource(cmd)){
      val sources = _args.take(_args.length - 1)
      val destination = _args.last
      val lasfiles = readSources(sources.toList)
      writeLasFiles(lasfiles, destination)
    }
    else {
      throw new UnsupportedOperationException("ooops, haven't implemented this yet")
    }
  }

  def isSource(path:String) = isFile(path) || path.split("://").size == 2

  def isFile(path:String) = (new File(path)).exists()
  
  def readSources(sources:List[String]):List[LasFile] = {
    sources.map(source => {
      resolveReader(protocolOf(source)) match {
	case Some(r) => r.readLasFile(pathOf(source))
	case None => throw new RuntimeException("no reader found for source : " +
						source + " with proto: " + protocolOf(source))
      }
    })
  }

  def resolveReader(protocol:String) = readers.find(_.canRead(protocol).booleanValue)
  def resolveWriter(protocol:String) = writers.find(_.canWrite(protocol).booleanValue)
  def protocolOf(source:String) = {
    val parts = source.split("://")
    if(parts.size == 1) "file" else parts.first
  }

  def pathOf(source:String) = {
    val parts = source.split("://")
    if(parts.size == 1) source else parts(1)
  }

  lazy val manifest = XML.loadFile("io_manifest.xml")
  lazy val readers = {
    (manifest \\ "reader").map(r => {
      val clazzName = r.attribute("class").get.text
      val clazz = Class.forName(clazzName)
      clazz.newInstance.asInstanceOf[LasReader]
    })
  }
  lazy val writers = {
    val ws =(manifest \\ "writer").map(w => {
      val clazzName = w.attribute("class").get.text
      val clazz = Class.forName(clazzName)
      clazz.newInstance.asInstanceOf[LasWriter]
    })
    ws
  }

  def writeLasFiles(files:List[LasFile], destination:String) {
    resolveWriter(protocolOf(destination)) match {
      case Some(w) => files.foreach(f => w.writeLasFile(f, pathOf(destination)))
      case None => throw new RuntimeException("no writer found for destination : " +
					      destination + " with proto: " + protocolOf(destination))
    }
  }

  def resolveCurve(path:String):Curve = null
  def writeCurve(curve:Curve, path:String) { }
}

