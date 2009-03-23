package org.jlas

import java.io._
import java.text.DecimalFormat

trait LasWriter {
  def writeLasFile(lf:LasFile, file:File)
  def writeLasFile(lf:LasFile, path:String)
}

object DefaultLasWriter extends LasWriter {

  override def writeLasFile(lf: LasFile, file:File) { 
    val writer = new BufferedWriter(new FileWriter(file))
    try {
      writeHeaders(lf, writer)
      writeCurves(lf, writer)
    } finally {
      writer.close()
    }
  }

  override def writeLasFile(lf: LasFile, path:String) { 
    writeLasFile(lf, new File(path))
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
