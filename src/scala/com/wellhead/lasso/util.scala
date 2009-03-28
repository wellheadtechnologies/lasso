package com.wellhead.lasso

import scala.collection.jcl.Conversions._
import scala.util.Sorting
import java.sql.{Connection,DriverManager,ResultSet,SQLException,Statement}
import java.lang.Double

object Util {
  implicit def fun2Run[T](x: => T) : Runnable = new Runnable() { def run = x }
  implicit def double2Double(d:java.lang.Double):scala.Double = d.doubleValue
  implicit def Doubl2double(d:scala.Double):java.lang.Double = new Double(d)

  
  def repeat[A](n:Int, fn:() => A) = (1 to n).map(_ => fn()).toList

  def time[A](msg:String)(fn: => A):A = {
    val start = System.currentTimeMillis
    val ret = fn
    val end = System.currentTimeMillis
    println(msg + (end - start))
    ret 
  }

  def withConnection[A](url:String)(fn: (Connection) => A):A = {
    val connection = DriverManager.getConnection(url)
    try {
      fn(connection)
    } finally {
      connection.close()
    }
  }
  
}
