package org.jlas

import org.slf4j.{Logger,LoggerFactory}

trait Header {
  def getType:String
  def getPrefix:String
  def getDescriptors:List[Descriptor]
  def getDescriptor(name:String):Descriptor

  val logger = LoggerFactory.getLogger("core.Header")
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

trait IsMutableHeader extends Header {
  def setType(s:String)
  def setPrefix(s:String)
  def setDescriptors(ls:List[Descriptor])
}

final class ImmutableHeader(htype:String, prefix:String, descriptors:List[Descriptor]) 
extends Header {
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
}

final class MutableHeader(private var htype:String,
			  private var prefix:String,
			  private var descriptors:List[Descriptor])
extends IsMutableHeader with MutexLocked {
  override def getType = guardLock { htype }
  override def getPrefix = guardLock { prefix }
  override def getDescriptors = guardLock { descriptors }
  override def getDescriptor(name:String) = guardLock {
    descriptors.find(_.getMnemonic == name).get
  }
  override def setType(t:String) {
    guardLock { this.htype = t }
  }
  override def setPrefix(p:String) {
    guardLock { this.prefix = p }
  }
  override def setDescriptors(ds:List[Descriptor]){
    guardLock { this.descriptors = ds }
  }
    
}
  
object Header {
  def VersionHeader(version:String, wrap:String) = {
    new ImmutableHeader("VersionHeader", "~V", 
			new ImmutableDescriptor("VERS", null, version, null) :: 
			new ImmutableDescriptor("WRAP", null, wrap, null) :: Nil)
  }

  def CurveHeader(descriptors:List[Descriptor]) = {
    new ImmutableHeader("CurveHeader", "~C", descriptors)
  }

  def WellHeader(descriptors:List[Descriptor]) = {
    new ImmutableHeader("WellHeader", "~W", descriptors)
  }

  def ParameterHeader(descriptors:List[Descriptor]) = {
    new ImmutableHeader("ParameterHeader", "~P", descriptors)
  }
}
