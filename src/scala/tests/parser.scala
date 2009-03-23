package org.jlas
import org.scalatest._
import java.io.File

class ParserTest extends FunSuite {
  def not_null[A](x:A) { assert(x != null, "something was null") }

  test("Parser should parse test.las") {
    val lf = DefaultLasParser.parseLasFile("las_files/test.las")
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
    val lf = DefaultLasParser.parseLasFile("las_files/dollie.las")
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
    val lf = DefaultLasParser.parseLasFile("las_files/x4.las")
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
      DefaultLasParser.parseLasFile(file)
    }
  }
  
  test ("Writer should write lasfile") {
    def in_out(path:String) { 
      val lf1 = DefaultLasParser.parseLasFile(path)
      DefaultLasWriter.writeLasFile(lf1, "output_test.las") 
      val lf2 = DefaultLasParser.parseLasFile("output_test.las")
      assert(lf1 === lf2)
    }
    
    in_out("las_files/test.las")
    in_out("las_files/dollie.las")
    in_out("las_files/robert.las")
    in_out("las_files/x4.las")
  }
}
