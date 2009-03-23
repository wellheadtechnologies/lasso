package org.jlas

trait Descriptor {
  def getMnemonic:String
  def getUnit:Any
  def getData:Any
  def getDescription:String
}

class DefaultDescriptor(mnemonic:String, unit:Object, data:Object, description:String)
extends Descriptor {
  override def getMnemonic = mnemonic
  override def getUnit = unit
  override def getData = data
  override def getDescription = description
  override def toString = mnemonic + " " + unit + " " + data + " " + description
  override def equals(_that:Any):Boolean = {
    if(!_that.isInstanceOf[Descriptor]) return false
    val that = _that.asInstanceOf[Descriptor]
    if(that.getMnemonic != this.getMnemonic) return false
    if(that.getUnit != this.getUnit) return false
    if(that.getData != this.getData) return false
    if(that.getDescription != this.getDescription) return false
    return true
  }
}
  
