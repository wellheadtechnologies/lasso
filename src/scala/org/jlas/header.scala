package org.jlas

import org.slf4j.{Logger,LoggerFactory}

trait Header {
  def getType:String
  def getPrefix:String
  def getDescriptors:List[Descriptor]
  def getDescriptor(name:String):Descriptor
}

class DefaultHeader(htype:String, prefix:String, descriptors:List[Descriptor]) 
extends Header {
  private val logger = LoggerFactory.getLogger("core.Header")

  override def getType = htype
  override def getPrefix = prefix
  override def getDescriptors = descriptors
  override def getDescriptor(name:String) =
    descriptors.find(_.getMnemonic == name).get

  override def toString = {
    val buf = new StringBuffer 
    def a(s:String) { buf.append(s) }
    a(htype)
    a("\n")
    for(d <- descriptors){
      a(d.toString)
      a("\n")
    }
    buf.toString
  }

  override def equals(_that:Any):Boolean = {
    if(!_that.isInstanceOf[Header]) {
      logger.debug("{} is not an instance of Header", _that)
      return false
    }
    val that = _that.asInstanceOf[Header]
    if(this.getType != that.getType){
      logger.debug("types: {} != {}", this.getType, that.getType)
      return false
    }
    if(this.getDescriptors != that.getDescriptors){
      logger.debug("descriptors: {} != {}", this.getDescriptors, that.getDescriptors)
      return false
    }
    logger.debug("header equal")
    return true
  }

}


object Header {
  def VersionHeader(version:String, wrap:String) = {
    new DefaultHeader("VersionHeader", "~V", 
		      new DefaultDescriptor("VERS", null, version, null) :: 
		      new DefaultDescriptor("WRAP", null, wrap, null) :: Nil)
  }

  def CurveHeader(descriptors:List[Descriptor]) = {
    new DefaultHeader("CurveHeader", "~C", descriptors)
  }

  def WellHeader(descriptors:List[Descriptor]) = {
    new DefaultHeader("WellHeader", "~W", descriptors)
  }

  def ParameterHeader(descriptors:List[Descriptor]) = {
    new DefaultHeader("ParameterHeader", "~P", descriptors)
  }
}
