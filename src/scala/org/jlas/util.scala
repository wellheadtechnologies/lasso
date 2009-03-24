package org.jlas

import scala.collection.jcl.Conversions._
import scala.util.Sorting
import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock, ReentrantLock, Lock}

object Util {
  implicit def fun2Run[T](x: => T) : Runnable = new Runnable() { def run = x }
  
  def repeat[A](n:Int, fn:() => A) = (1 to n).map(_ => fn()).toList

  def smallToLarge(xs:List[Double]):List[Double] = {
    val arr = xs.toArray.asInstanceOf[Array[Double]]
    Sorting.quickSort(arr)
    return arr.toList.asInstanceOf[List[Double]]
  }

  def writeLock[A](lock:ReadWriteLock)(fn: => A):A = {
    lock.writeLock.lock()
    try {
      fn
    }finally {
      lock.writeLock.unlock()
    }
  }

  def readLock[A](lock:ReadWriteLock)(fn: => A):A = {
    lock.readLock.lock()
    try {
      fn
    } finally {
      lock.readLock.unlock()
    }
  }
  
  def withLock[A](lock:Lock)(fn: => A):A = {
    lock.lock()
    try {
      fn 
    } finally {
      lock.unlock()
    }
  }

  def time[A](msg:String)(fn: => A):A = {
    val start = System.currentTimeMillis
    val ret = fn
    val end = System.currentTimeMillis
    println(msg + (end - start))
    ret 
  }
    
  
}


trait MutexLocked {
  import Util.withLock
  val lock = new ReentrantLock(true)
  var thread:Thread = null
  
  def grabLock[A](fn: => A):A = {
    withLock(lock){
      thread = Thread.currentThread
      val ret = fn
      thread = null
      ret
    }
  }

  def guardLock[A](fn: => A):A = {
    withLock(lock){
      if(thread eq Thread.currentThread){
	fn
      }
      else {
	throw new RuntimeException("thread does not hold lock")
      }
    }
  }
}


      
