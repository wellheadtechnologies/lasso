package org.jlas
import Util._
import org.slf4j.{Logger,LoggerFactory}
import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock}

trait Curve {
  def getDescriptor:Descriptor
  def getLasData:List[Double]
  def getIndex:Curve
  def getMnemonic:String
  def getUnit:Any
  def getData:Any
  def getDescription:String
  def sampleRate:Double
  def adjustedCurve(pindex:Curve):Curve

  val logger = LoggerFactory.getLogger("org.jlas.Curve")

  override def equals(_that:Any):Boolean = {
    logger.debug("testing curve equality for {}", this.getMnemonic)
    if(!_that.isInstanceOf[Curve]) {
      logger.debug("{} is not an instance of Curve", _that)
      return false
    }
    val that = _that.asInstanceOf[Curve]
    logger.debug("testing descriptors")
    if(this.getDescriptor != that.getDescriptor){
      logger.debug("descriptors: {} != {}", this.getDescriptor, that.getDescriptor)
      return false
    }
    logger.debug("testing lasdata")
    if(this.getLasData == null || that.getLasData == null){
      logger.debug("lasdata is null")
    }
    val size = this.getLasData.size
    if(size != that.getLasData.size){
      logger.debug("data-sizes: {} != {}", size, that.getLasData.size)
      return false
    }
    for(i <- 0 until size){
      val (x,y) = (this.getLasData(i), that.getLasData(i));
      if(x != y){
	logger.debug("{} != {}", x, y)
	return false
      }
    }
    logger.debug("curves equal")
    return true
  }
}

trait IsMutableCurve extends Curve {
  def setDescriptor(d:Descriptor)
  def setLasData(d:List[Double])
  def setIndex(c:Curve)
}

//Assumption of Immutability
final class ImmutableCurve(descriptor:Descriptor, index:Curve, data:List[Double]) extends Curve {
  import Curve._
  override def getDescriptor = descriptor
  override def getLasData = data
  override def getIndex = index
  override def getMnemonic = descriptor.getMnemonic
  override def getUnit = descriptor.getUnit
  override def getData = descriptor.getData
  override def getDescription = descriptor.getDescription
  override def toString = getMnemonic + " " + getLasData.size

  override def sampleRate = Curve.sampleRate(this)

  override def adjustedCurve(pindex:Curve):Curve = adjustCurve(pindex, this)

  override def equals(_that:Any):Boolean = {
    logger.debug("testing curve equality for {}", this.getMnemonic)
    if(!_that.isInstanceOf[Curve]) {
      logger.debug("{} is not an instance of Curve", _that)
      return false
    }
    val that = _that.asInstanceOf[Curve]
    logger.debug("testing descriptors")
    if(this.getDescriptor != that.getDescriptor){
      logger.debug("descriptors: {} != {}", this.getDescriptor, that.getDescriptor)
      return false
    }
    logger.debug("testing lasdata")
    if(getLasData == null || that.getLasData == null){
      logger.debug("lasdata is null")
    }
    val size = getLasData.size
    if(size != that.getLasData.size){
      logger.debug("data-sizes: {} != {}", size, that.getLasData.size)
      return false
    }
    for(i <- 0 until size){
      val (x,y) = (this.getLasData(i), that.getLasData(i));
      if(x != y){
	logger.debug("{} != {}", x, y)
	return false
      }
    }
    logger.debug("curves equal")
    return true
  }
}

final class MutableCurve(private var descriptor:Descriptor,
			 private var index:Curve,
			 private var data:List[Double])
extends IsMutableCurve with MutexLocked {
  import Curve._
  override def getDescriptor = guardLock { descriptor }
  override def setDescriptor(d:Descriptor) {
    guardLock { this.descriptor = d }
  }

  override def getLasData = guardLock { data }
  override def setLasData(d:List[Double]){
    guardLock { this.data = d }
  }

  override def getIndex = guardLock { index }
  override def setIndex(c:Curve) {
    guardLock { this.index = c }
  }
  override def getMnemonic = guardLock { descriptor.getMnemonic }
  override def getUnit = guardLock { descriptor.getUnit }
  override def getData = guardLock { descriptor.getData }
  override def getDescription = guardLock { descriptor.getDescription }

  override def toString = guardLock { getMnemonic + " " + getLasData.size }

  override def sampleRate = guardLock { Curve.sampleRate(this) }

  override def adjustedCurve(pindex:Curve):Curve = guardLock { adjustCurve(pindex, this) }
}
  

object Curve {
  //assumes smallToLarge order
  def startOffset(pindex:List[Double], index:List[Double], srate:Double):Int = {
    ((pindex.first - index.first).abs / srate).intValue
  }

  //assumes smallToLarge order
  def endOffset(pindex:List[Double], index:List[Double], srate:Double):Int = {
    ((pindex.last - index.last).abs / srate).intValue
  }

  def replaceNullWithNaN(list:List[Double]) = {
    list.map(d => {
      val diff = (d.abs - 999.25).abs
      if(d < 0 && diff < 0.00001) java.lang.Double.NaN else d
    })
  }

  //assumes immutability of curve and index
  def sampleRate(curve:Curve) = {
    val index = curve.getIndex
    val idata = index.getLasData
    val first = idata(0)
    val second = idata(1)
    val rate = first - second 
    rate.abs
  }

  def adjustCurve(pindex:Curve, curve:Curve) = {
    val pdata = smallToLarge(pindex.getLasData)
    val cidata = smallToLarge(curve.getLasData)
    val start_offset = startOffset(pdata, cidata, curve.sampleRate)
    val end_offset = endOffset(pdata, cidata, curve.sampleRate)
    val startPadding = repeat(start_offset, () => java.lang.Double.NaN)
    val endPadding = repeat(end_offset, () => java.lang.Double.NaN)
    new ImmutableCurve(
      curve.getDescriptor,
      pindex,
      startPadding.concat(
	replaceNullWithNaN(curve.getLasData).concat(endPadding)).toList
    )
  }
}
