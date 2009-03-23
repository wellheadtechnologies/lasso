package org.jlas
import org.scalatest._

class LasTest extends FunSuite {
  def not_null[A](x:A) { assert(x != null, "something was null") }
}
