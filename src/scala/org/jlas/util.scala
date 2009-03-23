package org.jlas

import scala.collection.jcl.Conversions._
import scala.util.Sorting

object Util {
  implicit def fun2Run[T](x: => T) : Runnable = new Runnable() { def run = x }
  
  def repeat[A](n:Int, fn:() => A) = (1 to n).map(_ => fn()).toList

  def smallToLarge(xs:List[Double]):List[Double] = {
    val arr = xs.toArray.asInstanceOf[Array[Double]]
    Sorting.quickSort(arr)
    return arr.toList.asInstanceOf[List[Double]]
  }
    
}
