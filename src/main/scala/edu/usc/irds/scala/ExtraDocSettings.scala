package edu.usc.irds.scala

import scala.tools.nsc._

class ExtraDocSettings(error: String => Unit) extends doc.Settings(error) {

  override def ChoiceSetting(name: String, helpArg: String, descr: String,
                             choices: List[String], default: String) = {
    if(name == "-doc-format") super.ChoiceSetting(name, helpArg, descr, List("json"), "json")
    else super.ChoiceSetting(name, helpArg, descr, choices, default)
  }

}
