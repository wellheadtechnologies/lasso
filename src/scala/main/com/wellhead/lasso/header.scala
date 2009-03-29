package com.wellhead.lasso
import org.slf4j.{Logger,LoggerFactory}
import java.util.{List,LinkedList,ArrayList}
import scala.collection.jcl.Conversions._

trait WHHeader extends Header {
  var descriptors:List[Descriptor] = null
  override def getDescriptors = descriptors
  override def getDescriptor(name:String) =
    descriptors.find(_.getMnemonic == name).get
  override def setDescriptors(descriptors:List[Descriptor]) { this.descriptors = descriptors }
  override def equals(_that:Any):Boolean = {
    if(!_that.isInstanceOf[Header]) return false
    val that = _that.asInstanceOf[Header]
    if(this.getType != that.getType || 
       this.getPrefix != that.getPrefix || 
       this.getDescriptors != that.getDescriptors) return false
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
