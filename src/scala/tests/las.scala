package com.wellhead.lasso
import org.scalatest._
import Util.fun2Run

class CurveTest extends FunSuite {

  test("mutating curves") {
    val curve = new MutableCurve(
      new ImmutableDescriptor("depth", null, null, "depth"),
      null, List[Double](1,2,3,4,5,6,7,8,9))

    errorOrFail { 
      curve.setDescriptor(null)
    }
    errorOrFail {
      curve.setLasData(List[Double](5,3,2,6,8,9))
    }
    errorOrFail {
      curve.getDescriptor
    }

    val mutated = new ImmutableDescriptor("mutated", null, null, "hahaha")
    curve.grabLock {
      curve.setDescriptor(mutated)
      curve.setIndex(null)
      curve.setLasData(List[Double](60,70,80))
    }
    
    errorOrFail { curve.getDescriptor }
    errorOrFail { curve.getLasData }

    curve.grabLock {
      assert(curve.getDescriptor === mutated)
      assert(curve.getLasData === List[Double](60,70,80))
    }
  }

  test("blocking curve mutation") {
    var foo = ""
    val curve = new MutableCurve(
      new ImmutableDescriptor("depth", null, null, "depth"),
      null, List[Double](1,2,3,4,5,6,7,8,9))
    new Thread(
      curve.grabLock {
	Thread.sleep(2000)
	foo = "bar"
      }).start()
    Thread.sleep(500)
    curve.grabLock {
      assert(foo == "bar")
      curve.setDescriptor(new ImmutableDescriptor("mutated", null, null, "hahaha"))
      curve.setLasData(List[Double](60,70,80))
    }
  }

  test("test mutating descriptors") {
    val desc = new MutableDescriptor("depth", "1","2", "distance under the earth")
    errorOrFail { desc.setMnemonic("foo") }
    errorOrFail { desc.setUnit("1.2") }
    errorOrFail { desc.setData("bar") }
    errorOrFail { desc.setDescription("rubarb") }
    desc.grabLock {
      desc.setMnemonic("foo")
      desc.setUnit("1.2")
      desc.setData("bar")
      desc.setDescription("rubarb")
    }
    errorOrFail { desc.getMnemonic }
    errorOrFail { desc.getUnit }
    errorOrFail { desc.getData } 
    errorOrFail { desc.getDescription }
    desc.grabLock { 
      assert(desc.getMnemonic === "foo")
      assert(desc.getUnit === "1.2")
      assert(desc.getData === "bar")
      assert(desc.getDescription === "rubarb")
    }
  } 

  test("test mutating headers") {
    val header = new MutableHeader("VersionHeader", "~V", 
				   List[Descriptor](
				     new ImmutableDescriptor("foo",null,null,null),
				     new ImmutableDescriptor("bar",null,null,null)))

    errorOrFail { header.getType }
    errorOrFail { header.getPrefix }
    errorOrFail { header.getDescriptors }
    errorOrFail { header.getDescriptor("foo") }
    
    header.grabLock {
      header.setType("WellHeader")
      header.setPrefix("~W")
      header.setDescriptors(List[Descriptor](
	new ImmutableDescriptor("hat", null,null,null),
	new ImmutableDescriptor("mad", null,null,"hatter")))
    }
    errorOrFail { header.getType }
    errorOrFail { header.getPrefix }
    errorOrFail { header.setPrefix("blah") }
    errorOrFail { header.setDescriptors(null) }
    header.grabLock {
      assert(header.getType === "WellHeader")
      assert(header.getPrefix === "~W")
      assert(header.getDescriptor("hat").getUnit === null)
      assert(header.getDescriptor("mad").getDescription === "hatter")
    }
  }

  def errorOrFail[A](fn: => A) {
    try {
      fn 
      fail()
    } catch {
      case e => println("caught " + e)
    } 
  }
  
}

