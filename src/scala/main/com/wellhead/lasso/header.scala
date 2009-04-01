package com.wellhead.lasso
import org.slf4j.{Logger,LoggerFactory}
import java.util.{List,LinkedList,ArrayList}
import scala.collection.jcl.Conversions._

trait WHHeader extends Header {
  private val logger = LoggerFactory.getLogger("WHHeader")
  var descriptors:List[Descriptor] = null
  override def getDescriptors = descriptors
  override def getDescriptor(name:String) =
    descriptors.find(_.getMnemonic == name).get
  override def setDescriptors(descriptors:List[Descriptor]) { this.descriptors = descriptors }
  override def equals(_that:Any):Boolean = {
    if(!_that.isInstanceOf[Header]) return false
    val that = _that.asInstanceOf[Header]
    if(this.getType != that.getType){
      logger.debug("types not equal : {} {}", this.getType, that.getType)
      return false
    }
    if(this.getPrefix != that.getPrefix){
      logger.debug("prefixes not equal : {} {}", this.getPrefix, that.getPrefix)
      return false
    }
    if(this.getDescriptors != that.getDescriptors){
      if(logger.isDebugEnabled){
	for(i <- 0 until this.getDescriptors.size){
	  if(this.getDescriptors.get(i) != that.getDescriptors.get(i)){
	    logger.debug("descriptors not equal : {} != \n {}", 
			 this.getDescriptors.get(i), that.getDescriptors.get(i))
	  }
	}
      }
      return false
    }
    return true
  }
}

final class WHWellHeader extends WellHeader with WHHeader
final class WHVersionHeader extends VersionHeader with WHHeader
final class WHCurveHeader extends CurveHeader with WHHeader
final class WHParameterHeader extends ParameterHeader with WHHeader

object WHHeader {
  def apply(htype:String, prefix:String, descriptors:List[Descriptor]):Header = {
    val header = htype match {
      case "VersionHeader" => new WHVersionHeader
      case "WellHeader" => new WHWellHeader
      case "CurveHeader" => new WHCurveHeader
      case "ParameterHeader" => new WHParameterHeader
    }
    header.setDescriptors(descriptors)
    header
  }
}
