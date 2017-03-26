/* This source file is based on NSC -- new Scala compiler -- Copyright 2007-2010 LAMP/EPFL */

package edu.usc.irds.scala


import scala.tools.nsc._
import scala.tools.nsc.doc._
import scala.tools.nsc.reporters.Reporter


/** A documentation processor controls the process of generating Scala documentation, which is as follows.
  *
  * * A simplified compiler instance (with only the front-end phases enabled) is created, and additional
  *   ''sourceless'' comments are registered.
  * * Documentable files are compiled, thereby filling the compiler's symbol table.
  * * A documentation model is extracted from the post-compilation symbol table.
  * * A generator is used to transform the model into the correct final format (HTML).
  *
  * A processor contains a single compiler instantiated from the processor's `settings`. Each call to `document`
  * uses the same compiler instance with the same symbol table. In particular, this implies that the scaladoc site
  * obtained from a call to `run` will contain documentation about files compiled during previous calls to the same
  * processor's `run` method.
  *
  * @param reporter The reporter to which both documentation and compilation errors will be reported.
  * @param settings The settings to be used by the documenter and compiler for generating documentation.
  *
  * @author Gilles Dubochet (modified by Thamme Gowda)
  * */
class DocFactory (val reporter: Reporter, val settings: doc.Settings) extends Loggable { processor =>

  object compiler extends ScaladocGlobal(settings, reporter)

  def makeUniverse(sources:List[String]): Option[Universe] ={
    new compiler.Run().compile(sources)
    if (reporter.hasErrors)
      return None

    val extraTemplatesToDocument: Set[compiler.Symbol] = {
      if (settings.docUncompilable.isDefault) Set()
      else {
        val uncompilable = new {
          val global: compiler.type = compiler
          val settings:doc.Settings = processor.settings
        } with Uncompilable { }

        compiler.docComments ++= uncompilable.comments
        log.debug(">>" + uncompilable)
        uncompilable.templates
      }
    }

    val modelFactory = new {
      override val global: compiler.type = compiler
    } with model.ModelFactory(compiler, settings)
      with model.ModelFactoryImplicitSupport
      with model.ModelFactoryTypeSupport
      with model.diagram.DiagramFactory
      with model.CommentFactory
      with model.TreeFactory
      with model.MemberLookup {
      override def templateShouldDocument(sym: compiler.Symbol, inTpl: DocTemplateImpl) =
        extraTemplatesToDocument(sym) || super.templateShouldDocument(sym, inTpl)
    }
    val res = modelFactory.makeModel
    log.info("Found {} templates", modelFactory.templatesCount)
    res
  }

  /** Creates a scaladoc site for all symbols defined in this call's `files`, as well as those defined in `files` of
    * previous calls to the same processor.
    * @param files The list of paths (relative to the compiler's source path, or absolute) of files to document. */
  def document(files: List[String]): Unit = {
    val docModel = makeUniverse(files) getOrElse  {throw new RuntimeException("Invalid State")}
    new json.JsonFactory(docModel).generate
  }
}

