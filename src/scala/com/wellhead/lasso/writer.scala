package com.wellhead.lasso

import java.io._
import java.text.DecimalFormat
import scala.collection.jcl.Conversions._
import java.util.{List,LinkedList}

trait LasWriter {
  def writeLasFile(lf:LasFile, path:String)
}

class LasFileWriter extends LasWriter {
  val form = new DecimalFormat
  form.setMaximumFractionDigits(20)
  form.setMaximumIntegerDigits(20)
  form.setGroupingUsed(false)
  
  override def writeLasFile(lf: LasFile, path:String) { 
    writeLasFile(lf, new File(path))
  }

  private def writeLasFile(lf: LasFile, file:File) { 
    val writer = new BufferedWriter(new FileWriter(file))
    writeLasFile(lf, writer)
  }

  private def writeLasFile(lf:LasFile, writer:BufferedWriter) {
    val write = (s:String) => writer.write(s)
    try {
      writeHeaders(lf, writer)
      writeCurves(lf, writer)
    } finally {
      writer.close()
    }
  }    

  private def writeHeaders(lf: LasFile, writer:BufferedWriter) {
    val headers = lf.getHeaders
    for(h <- headers) {
      writeHeader(h, writer)
    }
  }

  private def writeHeader(h:Header, writer:BufferedWriter) {
    writer.write(h.getPrefix); writer.newLine
    writeDescriptors(h.getDescriptors, writer)
  }

  private def writeDescriptors(descriptors:List[Descriptor], writer: BufferedWriter){
    for(d <- descriptors){
      writeDescriptor(d, writer)
    }
  }

  private def writeDescriptor(descriptor:Descriptor, writer: BufferedWriter) {
    val write = (s:String) => writer.write(s)
    write(descriptor.getMnemonic)
    write(" .")
    write(descriptor.getUnit)
    write(" ")
    write(descriptor.getData)
    write(" : ")
    write(descriptor.getDescription)
    writer.newLine
  }

  private def writeCurves(lf: LasFile, writer: BufferedWriter) {
    writer.write("~A")
    writer.newLine
    val curves = {
      val list = new LinkedList[Curve]
      list.add(lf.getIndex)
      lf.getCurves.foreach(c => list.add(c))
      list
    }      
    val columns = curves.size
    val rows = curves.first.getLasData.size
    for(r <- 0 until rows){
      for(c <- curves){
	val data = c.getLasData
	writer.write(form.format(data.get(r)))
	writer.write(" ")
      }
      writer.newLine
    }
  }			     
    
}

object ClojureWriter extends LasWriter {

  override def writeLasFile(lf: LasFile, path:String) { 
    writeLasFile(lf, new File(path))
  }

  private def writeLasFile(lf: LasFile, file:File) { 
    val writer = new BufferedWriter(new FileWriter(file))
    writeLasFile(lf, writer)
  }

  private def writeLasFile(lf:LasFile, writer:BufferedWriter) {
    val write = (s:String) => writer.write(s)
    try {
      write("{")
      write(":headers ")
      writeHeaders(lf, writer)
      
      write(", ")
      write(":curves ")
      writeCurves(lf, writer)
      write("}")
    } finally {
      writer.close()
    }
  }    

  private def writeHeaders(lf: LasFile, writer:BufferedWriter) {
    val headers = lf.getHeaders
    writer.write("[")
    for(h <- headers) {
      writeHeader(h, writer)
      writer.newLine
    }
    writer.write("]")
    writer.newLine
  }

  private def writeHeader(h:Header, writer:BufferedWriter) {
    val write = (s:String) => writer.write(s)
    write("{")
    write(":type ")
    write(quote(h.getType))
    
    write(", ")
    
    write(":prefix ")
    write(quote(h.getPrefix))
    
    writer.newLine

    write(":descriptors ")
    writeDescriptors(h.getDescriptors, writer)
    write("}")
  }

  private def writeDescriptors(descriptors:List[Descriptor], writer: BufferedWriter){
    val write = (s:String) => writer.write(s)
    write("[")
    for(d <- descriptors){
      writeDescriptor(d, writer)
      writer.newLine
    }
    write("]")
  }

  private def writeDescriptor(descriptor:Descriptor, writer: BufferedWriter) {
    val write = (s:String) => writer.write(s)
    write("{")
    write(":mnemonic ")
    write(quote(descriptor.getMnemonic))
    
    write(", ")

    write(":unit ")
    write(quote(descriptor.getUnit.toString))

    write(", ")

    write(":data ")
    write(quote(descriptor.getData.toString))

    write(", ")

    write(":description ")
    write(quote(descriptor.getDescription))
    write("}")
  }

  private def writeIndex(lf: LasFile, writer: BufferedWriter) {
    val write = (s:String) => writer.write(s)
    write(":index ")
    write(quote(lf.getIndex.getMnemonic))
    writer.newLine
  }


  private def writeCurves(lf: LasFile, writer: BufferedWriter) {
    val write = (s:String) => writer.write(s)
    write("[")
    for(curve <- lf.getCurves){
      writeCurve(curve, writer)
      writer.newLine
    }
    write("]")
  }			     

  private def writeCurve(curve:Curve, writer: BufferedWriter) {
    val write = (s:String) => writer.write(s)
    write("{")
    write(":descriptor ")
    writeDescriptor(curve.getDescriptor, writer)
    writer.newLine
    
    write(":data ")

    write("[")
    for(d <- curve.getLasData){
      write(d.toString)
      write(" ")
    }
    write("]")
    write("}")
  }
  
  private def quote(s:String) = "\"" + s + "\""
}

