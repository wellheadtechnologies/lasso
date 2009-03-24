package org.jlas

import java.io._
import java.text.DecimalFormat

trait LasWriter {
  def writeLasFile(lf:LasFile, file:File)
  def writeLasFile(lf:LasFile, path:String)
  def writeLasFile(lf:LasFile, writer:BufferedWriter)
}

object DefaultLasWriter extends LasWriter {

  override def writeLasFile(lf: LasFile, file:File) { 
    val writer = new BufferedWriter(new FileWriter(file))
    writeLasFile(lf, writer)
  }

  override def writeLasFile(lf: LasFile, path:String) { 
    writeLasFile(lf, new File(path))
  }

  override def writeLasFile(lf:LasFile, writer:BufferedWriter) {
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
    writeDescriptors(h.getDescriptors,
		     writer)
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
    write(descriptor.getUnit.toString)
    write(" ")
    write(descriptor.getData.toString)
    write(" : ")
    write(descriptor.getDescription.toString)
    writer.newLine
  }

  private def writeCurves(lf: LasFile, writer: BufferedWriter) {
    writer.write("~A")
    writer.newLine
    val curves =  lf.getIndex :: lf.getCurves
    val columns = curves.size
    val rows = curves.first.getLasData.size
    val form = new DecimalFormat
    form.setMaximumFractionDigits(20)
    form.setMaximumIntegerDigits(20)
    form.setGroupingUsed(false)
    def row_data(r:Int) = curves.map(c => {
      val data = c.getLasData
      form.format(data(r))
    })

    for(r <- 0 until rows){
      writer.write(row_data(r).mkString(" "))
      writer.newLine
    }
  }			     
    
}

object ClojureWriter extends LasWriter {

  override def writeLasFile(lf: LasFile, file:File) { 
    val writer = new BufferedWriter(new FileWriter(file))
    writeLasFile(lf, writer)
  }

  override def writeLasFile(lf: LasFile, path:String) { 
    writeLasFile(lf, new File(path))
  }

  override def writeLasFile(lf:LasFile, writer:BufferedWriter) {
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
    val write = (s:String) => writer.write(s)
    write("{")
    write(":type ")
    write(h.getType)
    
    write(", ")
    
    write(":prefix ")
    write(h.getPrefix)
    
    write(", ")

    write(":descriptors ")
    writeDescriptors(h.getDescriptors, writer)
    write("}")
  }

  private def writeDescriptors(descriptors:List[Descriptor], writer: BufferedWriter){
    val write = (s:String) => writer.write(s)
    write("[")
    for(d <- descriptors){
      writeDescriptor(d, writer)
      write(", ")
    }
    write("]")
  }

  private def writeDescriptor(descriptor:Descriptor, writer: BufferedWriter) {
    val write = (s:String) => writer.write(s)
    write("{")
    write(":mnemonic ")
    write(descriptor.getMnemonic)
    
    write(", ")

    write(":unit ")
    write(descriptor.getUnit.toString)

    write(", ")

    write(":data ")
    write(descriptor.getData.toString)

    write(", ")

    write(":description ")
    write(descriptor.getDescription.toString)
    write("}")
  }

  private def writeCurves(lf: LasFile, writer: BufferedWriter) {
    val write = (s:String) => writer.write(s)
    val index = lf.getIndex
    write(":index ")
    writeCurve(index, writer)
    for(curve <- lf.getCurves){
      writeCurve(curve, writer)
    }
  }			     

  private def writeCurve(curve:Curve, writer: BufferedWriter) {
    val write = (s:String) => writer.write(s)
    write("{")
    write(":descriptor ")
    writeDescriptor(curve.getDescriptor, writer)
    
    write(", ")

    write("[")
    for(d <- curve.getLasData){
      write(d.toString)
      write(", ")
    }
    write("]")
  }
}

