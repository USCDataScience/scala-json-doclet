package edu.usc.irds.scala

import org.slf4j.{Logger, LoggerFactory}

/**
  * This trait is a syntactic sugar for enabling logging
  *
  * @author Thamme Gowda
  */
trait Loggable {
  val log:Logger = LoggerFactory.getLogger(this.getClass)
}
