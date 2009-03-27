package com.wellhead.lasso

trait Descriptor {
  def getMnemonic():String
  def getUnit():String
  def getData():String
  def getDescription():String
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

trait IsMutableDescriptor extends Descriptor {
  def setMnemonic(s:String)
  def setUnit(a:String)
  def setData(a:String)
  def setDescription(s:String)
}

final class ImmutableDescriptor(mnemonic:String, unit:String, data:String, description:String)
extends Descriptor {
  override def getMnemonic = mnemonic
  override def getUnit = unit
  override def getData = data
  override def getDescription = description
  override def toString = mnemonic + " " + unit + " " + data + " " + description
}
  
final class MutableDescriptor(private var mnemonic:String,
			      private var unit:String,
			      private var data:String,
			      private var description:String)
extends IsMutableDescriptor with MutexLocked {
  override def getMnemonic = guardLock { mnemonic }
  override def getUnit = guardLock { unit }
  override def getData = guardLock { data }
  override def getDescription = guardLock { description }

  override def setMnemonic(m:String) {
    guardLock { this.mnemonic = m }
  }

  override def setUnit(u:String) { 
    guardLock { this.unit = u }
  }

  override def setData(d:String) {
    guardLock { this.data = d }
  }

  override def setDescription(d:String) { 
    guardLock { this.description = d }
  }

  override def toString = guardLock { mnemonic + " " + unit + " " + data + " " + description }

  override def equals(that:Any) = guardLock { super.equals(that) }
}
