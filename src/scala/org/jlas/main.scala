package org.jlas
import org.slf4j.{Logger,LoggerFactory}
import scala.collection.mutable.{ListBuffer}
import java.io.{BufferedWriter,OutputStreamWriter, FileWriter}

object Main {
  var cursor = 0
  var args:Array[String] = null

  def main(_args:Array[String]) {
    args = _args
    try {
      command()
    } finally {
      args = null
      cursor = 0
    }
  }

  def command() {
    argument() match {
      case "parse" => parseLasFiles()
    }
  }

  def parseLasFiles(){
    val fs = files()
    val out = outputFormat()
    val lasfiles= fs.map(f => DefaultLasParser.parseLasFile(f))
    for(lasfile <- lasfiles){
      out.writeLasFile(lasfile, new BufferedWriter(new FileWriter("parse_out.clj")))
    }
  }

  def files() = {
    val fs = new ListBuffer[String]
    while(peek() != "output"){
      fs += argument()
    }
    fs.toList
  }

  def outputFormat() = {
    guard(argument() == "output", "expected output")
    argument() match {
      case "clojure" => ClojureWriter
    }
  }

  def argument():String = {
    val ret = args(cursor)
    cursor += 1
    ret 
  }

  def peek():String = args(cursor)
  
  def guard(cond:Boolean, msg:String){
    if(!cond){
      throw new RuntimeException(msg)
    }
  }
    

}

