package io.github.epi155.recfm.scala

object UnderflowAction extends Enumeration {
  type UnderflowAction = Value
  val Error, PadRight, PadLeft = Value
}
