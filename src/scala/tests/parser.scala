package com.wellhead.lasso
import org.scalatest._
import Util.time
import java.io.File
import scala.collection.jcl.Conversions._

class ParserTest extends FunSuite {
  def not_null[A](x:A) { assert(x != null, "something was null") }
  val parser = new LasFileParser

  test("Parser should parse test.las") {
    val lf = parser.readLasFile("las_files/test.las")
    val dept = lf.getIndex
    val gamma = lf.getCurve("Gamma")
    val porosity = lf.getCurve("Porosity")
    assert(1501.629 === dept.getLasData.first)
    assert("gAPI" === gamma.getUnit)
    assert("m3/m3" === porosity.getUnit)
    assert("DEPTH" === dept.getDescription)
    
    val headers = lf.getHeaders
    assert(headers.size === 4)
    not_null(lf.getVersionHeader)
    not_null(lf.getCurveHeader)
    not_null(lf.getWellHeader)
    not_null(lf.getParameterHeader)
  }

  test ("Parser should parse dollie.las"){ 
    val lf = parser.readLasFile("las_files/dollie.las")
    val dept = lf.getIndex
    val wtoc = lf.getCurve("WTOC")
    not_null(dept)
    not_null(wtoc)
    var d = 7800.0
    val deptData = dept.getLasData
    for(n <- 0 until deptData.size){
      assert(d === deptData(n))
      d -= 0.5
    }
    assert("LBF/LBF" === wtoc.getUnit)
  }

  test ("Parser should parse x4.las") {
    val lf = parser.readLasFile("las_files/x4.las")
    val wh = lf.getWellHeader
    val strt = wh.getDescriptor("STRT").getData
    val stop = wh.getDescriptor("STOP").getData
    not_null(wh)
    not_null(strt)
    not_null(stop)
    assert(strt === "57.000000000")
    assert(stop === "5817.0000000")
  }

  test ("Parser should parse all") { 
    val directory = new File("las_files")
    val files = directory.listFiles
    for(file <- files){ 
      parser.readLasFile(file.getPath)
    }
  }
  
  test ("Writer should write lasfile") {
    val writer = new LasFileWriter()
    def in_out(file:File) { 
      val lf1 = time("parsing took: ") { parser.readLasFile(file.getPath) }
      time("writing took: ") { writer.writeLasFile(lf1, "output_test.las") }
      val lf2 = time("parsing again took: ") { parser.readLasFile("output_test.las") }
      assert(lf1.contentEquals(lf2).booleanValue)
    }
    val directory = new File("las_files")
    for(file <- directory.listFiles){
      in_out(file)
    }
  }

  test ("Clojure Writer should write lasfile") {
    val writer = new ClojureWriter()
    val reader = new ClojureReader()
    def in_out(file:File) {
      val lf1 = time("parsing took: ") { parser.readLasFile(file.getPath) }
      time("clojure writing took: ") { writer.writeLasFile(lf1, "output_test.clj") }
      val lf2 = time("clojure reading took: ") { reader.readLasFile("output_test.clj") }
      assert(lf1.contentEquals(lf2).booleanValue)
    }
    val directory = new File("las_files")
    for(file <- directory.listFiles){
      in_out(file)
    }
  }
}

