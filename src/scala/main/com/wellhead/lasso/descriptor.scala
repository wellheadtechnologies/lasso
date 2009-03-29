package com.wellhead.lasso

final class WHDescriptor extends Descriptor {
  var mnemonic:String = null
  var unit:String = null
  var data:String = null
  var description:String = null
  override def getMnemonic = mnemonic
  override def getUnit = unit
  override def getData = data
  override def getDescription = description
  override def setMnemonic(mnemonic:String) { this.mnemonic = mnemonic }
  override def setUnit(unit:String) { this.unit = unit }
  override def setData(data:String) { this.data = data }
  override def setDescription(description:String) { this.description = description }
  override def toString = mnemonic + " " + unit + " " + data + " " + description
  override def equals(_that:Any):Boolean = {
    if(!_that.isInstanceOf[Descriptor]) return false
    val that = _that.asInstanceOf[Descriptor]
    if(this.getMnemonic != that.getMnemonic || 
       this.getUnit != that.getUnit || 
       this.getData != that.getData ||
       this.getDescription != that.getDescription) return false
    return true
  }
}

object WHDescriptor {
  def apply(mnemonic:String, unit:String, data:String, description:String) = {
    val descriptor = new WHDescriptor
    descriptor.setMnemonic(mnemonic)
    descriptor.setUnit(unit)
    descriptor.setData(data)
    descriptor.setDescription(description)
    descriptor 
  }
}
