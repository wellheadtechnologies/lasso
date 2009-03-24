package org.jlas

import org.slf4j.{Logger,LoggerFactory}

trait LasFile {
  def getName:String
  def getCurves:List[Curve]
  def getHeaders:List[Header]
  def getIndex:Curve
  def getCurve(name:String):Curve
  def getVersionHeader:Header
  def getWellHeader:Header
  def getCurveHeader:Header
  def getParameterHeader:Header

  private val logger = LoggerFactory.getLogger("core.LasFile")
  override def equals(_that:Any):Boolean = {
    logger.debug("testing equality")
    if(!_that.isInstanceOf[LasFile]) {
      logger.debug("{} is not an instance of LasFile", _that)
      return false
    }
    val that = _that.asInstanceOf[LasFile]
    if(this.getHeaders != that.getHeaders) {
      logger.debug("headers do not equal")
      logger.debug("this.headers = {}", this.getHeaders)
      logger.debug("that.headers = {}", that.getHeaders)
      return false
    }
    if(this.getCurves != that.getCurves) {
      logger.debug("curves do not equal")
      return false
    }
    logger.debug("lasfiles equal")
    return true
  }

}    

trait IsMutableLasFile extends LasFile {
  def setName(s:String)
  def setCurves(cs:List[Curve])
  def setHeaders(hs:List[Header])
  def setIndex(i:Curve)
}

final class ImmutableLasFile(name:String, headers: List[Header], index: Curve, curves: List[Curve])
extends LasFile {
  override def getName = name
  override def getCurves = curves
  override def getIndex = index
  override def getCurve(mnemonic:String) = curves.find(_.getMnemonic == mnemonic).get
  override def getHeaders = headers

  override def getVersionHeader = {
    headers.find(_.getType == "VersionHeader").get
  }
  override def getWellHeader = {
    headers.find(_.getType == "WellHeader").get
  }
  override def getCurveHeader = {
    headers.find(_.getType == "CurveHeader").get
  }
  override def getParameterHeader = {
    headers.find(_.getType == "ParameterHeader").get
  }

  override def toString = {
    val buf = new StringBuffer
    buf.append("LasFile")
    for(h <- headers){
      buf.append(h)
      buf.append("\n")
    }
    for(c <- curves){
      buf.append(c)
      buf.append("\n")
    }
    buf.toString
  }
}

/* final class MutableLasFile(private var name:String,
			   private var headers:List[Header],
			   private var index:Curve,
			   private var curves:List[Curve])
extends IsMutableLasFile with MutexLocked {
  override def getName = guardLock { name }
  override def getCurves = guardLock { curves }
  override def getIndex = guardLock { index } 
  override def getCurve(mnemonic:String) = guardLock { 
    curves.find(_.getMnemonic == mnemonic).get
  }
  override def getHeaders = guardLock { headers }
  override def getVersionHeader = guardLock {
    headers.find(_.getType == "VersionHeader").get
  }
  override def getWellHeader = guardLock { 
    headers.find(_.getType == "WellHeader").get
  }
  override def getCurveHeader = guardLock {
    headers.find(_.getType == "CurveHeader").get
  }
  override def getParameterHeader = guardLock { 
    headers.find(_.getType == "ParameterHeader").get
  }
*/
