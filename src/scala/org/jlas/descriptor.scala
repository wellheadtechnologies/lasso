package org.jlas

trait Descriptor {
  def getMnemonic:String
  def getUnit:Any
  def getData:Any
  def getDescription:String
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
  def setUnit(a:Any)
  def setData(a:Any)
  def setDescription(s:String)
}

final class ImmutableDescriptor(mnemonic:String, unit:Any, data:Any, description:String)
extends Descriptor {
  override def getMnemonic = mnemonic
  override def getUnit = unit
  override def getData = data
  override def getDescription = description
  override def toString = mnemonic + " " + unit + " " + data + " " + description
}
  
final class MutableDescriptor(private var mnemonic:String,
			      private var unit:Any,
			      private var data:Any,
			      private var description:String)
extends IsMutableDescriptor with MutexLocked {
  override def getMnemonic = guardLock { mnemonic }
  override def getUnit = guardLock { unit }
  override def getData = guardLock { data }
  override def getDescription = guardLock { description }

  override def setMnemonic(m:String) {
    guardLock { this.mnemonic = m }
  }

  override def setUnit(u:Any) { 
    guardLock { this.unit = u }
  }

  override def setData(d:Any) {
    guardLock { this.data = d }
  }

  override def setDescription(d:String) { 
    guardLock { this.description = d }
  }

  override def toString = guardLock { mnemonic + " " + unit + " " + data + " " + description }

  override def equals(that:Any) = guardLock { super.equals(that) }
}
