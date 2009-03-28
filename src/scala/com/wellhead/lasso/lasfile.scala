package com.wellhead.lasso

import scala.collection.jcl.Conversions._
import org.slf4j.{Logger,LoggerFactory}
import java.util.{List,ArrayList,LinkedList,Collections}

final class WHLasFile extends LasFile {
  private val logger = LoggerFactory.getLogger("WHLasFile")
  private var name:String = null
  private var curves:List[Curve] = null
  private var index:Curve = null

  private var versionHeader:VersionHeader = null
  private var wellHeader:WellHeader = null
  private var curveHeader:CurveHeader = null
  private var parameterHeader:ParameterHeader = null

  override def getName = name
  override def getCurves = curves
  override def getIndex = index
  override def getCurve(mnemonic:String) = {
    if(getIndex.getMnemonic == mnemonic) {
      index
    } else {
      curves.find(_.getMnemonic == mnemonic).get
    }
  }
  override def getVersionHeader = versionHeader
  override def getWellHeader = wellHeader
  override def getCurveHeader = curveHeader
  override def getParameterHeader = parameterHeader
  override def getHeaders = {
    val list = new ArrayList[Header](4)
    list.add(versionHeader)
    list.add(wellHeader)
    list.add(curveHeader)
    list.add(parameterHeader)
    list
  }

  override def setIndex(index:Curve) { this.index = index }
  override def setCurves(curves:List[Curve]) { this.curves = curves }
  override def setName(name:String) { this.name = name }
  override def setVersionHeader(vh:VersionHeader) { this.versionHeader = vh }
  override def setWellHeader(wh:WellHeader) { this.wellHeader = wh }
  override def setCurveHeader(ch:CurveHeader) { this.curveHeader = ch }
  override def setParameterHeader(ph:ParameterHeader) { this.parameterHeader = ph }
  override def setHeaders(headers:List[Header]) { 
    for(header <- headers){
      header match {
	case (h:VersionHeader) => setVersionHeader(h)
	case (h:WellHeader) => setWellHeader(h)
	case (h:CurveHeader) => setCurveHeader(h)
	case (h:ParameterHeader) => setParameterHeader(h)
      }
    }
  }

  override def equals(_that:Any):Boolean = {
    if(!_that.isInstanceOf[LasFile]) {
      logger.debug("that isn't a LasFile")
      return false
    }
    val that = _that.asInstanceOf[LasFile]
    if(this.getName != that.getName) {
      logger.debug("names don't match: {} {}", this.getName, that.getName)
      return false
    }
    return this.contentEquals(that).booleanValue
  }

  override def contentEquals(that:LasFile):java.lang.Boolean = {
    if(this.getIndex != that.getIndex){
      logger.debug("indexes don't match: {} {}", this.getIndex, that.getIndex)
      return false
    }
    if(this.getHeaders != that.getHeaders){
      logger.debug("headers don't match: {} {}", this.getHeaders, that.getHeaders)
      return false
    }
    if(this.getCurves != that.getCurves) {
      logger.debug("curves don't match: {} {}", this.getCurves, that.getCurves)
      return false
    }
    return true
  }    
}

object WHLasFile {
  def apply(name:String, headers:List[Header], index:Curve, curves:List[Curve]):WHLasFile = {
    val lf = new WHLasFile
    lf.setName(name)
    lf.setHeaders(headers)
    lf.setIndex(index)
    lf.setCurves(curves)
    lf
  }
}
