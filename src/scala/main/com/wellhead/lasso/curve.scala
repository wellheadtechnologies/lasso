package com.wellhead.lasso
import Util._
import org.slf4j.{Logger,LoggerFactory}
import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock}
import scala.collection.jcl.Conversions._
import java.util.{List, Collections, ArrayList, LinkedList}
import java.lang.Double

final class WHCurve extends Curve {
  private val logger = LoggerFactory.getLogger("WHCurve")
  private var descriptor:Descriptor = null
  private var data:List[Double] = null
  private var index:Curve = null
  override def getDescriptor = descriptor
  override def getLasData = data
  override def getIndex = index
  override def getMnemonic = descriptor.getMnemonic
  override def getUnit = descriptor.getUnit
  override def getData = descriptor.getData
  override def getDescription = descriptor.getDescription

  override def setDescriptor(descriptor:Descriptor) { this.descriptor = descriptor }
  override def setLasData(data:List[Double]) { this.data = data }
  override def setIndex(index:Curve) { this.index = index }
  override def setMnemonic(m:String) { getDescriptor.setMnemonic(m) }
  override def setUnit(m:String) { getDescriptor.setUnit(m) }
  override def setData(m:String) { getDescriptor.setData(m) }
  override def setDescription(m:String) { getDescriptor.setDescription(m) }

  override def toString = getMnemonic + " " + getLasData.size
  override def equals(_that:Any):Boolean = {
    if(!_that.isInstanceOf[Curve]) return false
    val that = _that.asInstanceOf[Curve]
    if(this.getDescriptor != that.getDescriptor){
      logger.debug("descriptors not equal: {} {}", this.getDescriptor, that.getDescriptor)
      return false
    }
    if(this.getLasData != that.getLasData) {
      logger.debug("data not equal: {} {}", this.getLasData, that.getLasData)
      return false
    }
    if(this.getIndex != that.getIndex) {
      logger.debug("indexes not equal: {} {}", this.getIndex, that.getIndex)
      return false
    }
    return true
  }
}

object WHCurve {
  def apply(descriptor:Descriptor, index:Curve, data:List[Double]):WHCurve = {
    val curve = new WHCurve
    curve.setDescriptor(descriptor)
    curve.setIndex(index)
    curve.setLasData(data)
    curve
  }
}
