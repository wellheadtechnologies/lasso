package org.jlas
import Util._
import org.slf4j.{Logger,LoggerFactory}

trait Curve {
  def getDescriptor:Descriptor
  def getLasData:List[Double]
  def getIndex:Curve
  def getMnemonic:String
  def getUnit:Any
  def getData:Any
  def getDescription:String
  def getSampleRate:Double
  def getAdjustedCurve(pindex:Curve):Curve
}

//Assumption of Immutability
final class DefaultCurve(descriptor:Descriptor, index:Curve, data:List[Double]) extends Curve {
  import Curve._
  private val logger = LoggerFactory.getLogger("core.Curve")
  override def getDescriptor = descriptor
  override def getLasData = data
  override def getIndex = index
  override def getMnemonic = descriptor.getMnemonic
  override def getUnit = descriptor.getUnit
  override def getData = descriptor.getData
  override def getDescription = descriptor.getDescription
  override def toString = getMnemonic + " " + getLasData.size

  override def getSampleRate = {
    val index = getIndex
    val idata = index.getLasData
    val first = idata(0)
    val second = idata(1)
    val rate = first - second 
    rate.abs
  }

  override def getAdjustedCurve(pindex:Curve):Curve = {
    val pdata = smallToLarge(pindex.getLasData)
    val cidata = smallToLarge(getLasData)
    val start_offset = startOffset(pdata, cidata, getSampleRate)
    val end_offset = endOffset(pdata, cidata, getSampleRate)
    val startPadding = repeat(start_offset, () => java.lang.Double.NaN)
    val endPadding = repeat(end_offset, () => java.lang.Double.NaN)
    return new DefaultCurve(
      getDescriptor,
      pindex,
      startPadding.concat(replaceNullWithNaN(getLasData).concat(endPadding)).toList
    )
  }

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
    if(this.getLasData != that.getLasData) {
      logger.debug("lasdata not equal")
      val size = getLasData.size
      if(size != that.getLasData.size){
	logger.debug("data-sizes: {} != {}", size, that.getLasData.size)
      }
      for(i <- 0 until size){
	val (x,y) = (this.getLasData(i), that.getLasData(i));
	if(x != y){
	  logger.debug("{} != {}", x, y)
	}
      }
      return false
    }
    logger.debug("curves equal")
    return true
  }

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
}

