
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/annalisa/git/DS4DM/DS4DM_webservice/conf/routes
// @DATE:Fri Aug 12 16:56:45 CEST 2016

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._

import _root_.controllers.Assets.Asset
import _root_.play.libs.F

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:6
  Application_1: controllers.Application,
  // @LINE:7
  ExtendTable_2: controllers.ExtendTable,
  // @LINE:15
  Assets_0: controllers.Assets,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:6
    Application_1: controllers.Application,
    // @LINE:7
    ExtendTable_2: controllers.ExtendTable,
    // @LINE:15
    Assets_0: controllers.Assets
  ) = this(errorHandler, Application_1, ExtendTable_2, Assets_0, "/")

  import ReverseRouteContext.empty

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, Application_1, ExtendTable_2, Assets_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.Application.index()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """ind""", """controllers.ExtendTable.ind()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """fetchTable""", """controllers.ExtendTable.fetchTable(name:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """search""", """controllers.ExtendTable.search()"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """suggestAttributes""", """controllers.ExtendTable.suggestAttributes()"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """fetchTablePOST""", """controllers.ExtendTable.fetchTablePOST()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""", """controllers.Assets.versioned(path:String = "/public", file:Asset)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:6
  private[this] lazy val controllers_Application_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val controllers_Application_index0_invoker = createInvoker(
    Application_1.index(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "index",
      Nil,
      "GET",
      """ Home page""",
      this.prefix + """"""
    )
  )

  // @LINE:7
  private[this] lazy val controllers_ExtendTable_ind1_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("ind")))
  )
  private[this] lazy val controllers_ExtendTable_ind1_invoker = createInvoker(
    ExtendTable_2.ind(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.ExtendTable",
      "ind",
      Nil,
      "GET",
      """""",
      this.prefix + """ind"""
    )
  )

  // @LINE:8
  private[this] lazy val controllers_ExtendTable_fetchTable2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("fetchTable")))
  )
  private[this] lazy val controllers_ExtendTable_fetchTable2_invoker = createInvoker(
    ExtendTable_2.fetchTable(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.ExtendTable",
      "fetchTable",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """fetchTable"""
    )
  )

  // @LINE:10
  private[this] lazy val controllers_ExtendTable_search3_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("search")))
  )
  private[this] lazy val controllers_ExtendTable_search3_invoker = createInvoker(
    ExtendTable_2.search(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.ExtendTable",
      "search",
      Nil,
      "POST",
      """""",
      this.prefix + """search"""
    )
  )

  // @LINE:11
  private[this] lazy val controllers_ExtendTable_suggestAttributes4_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("suggestAttributes")))
  )
  private[this] lazy val controllers_ExtendTable_suggestAttributes4_invoker = createInvoker(
    ExtendTable_2.suggestAttributes(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.ExtendTable",
      "suggestAttributes",
      Nil,
      "POST",
      """""",
      this.prefix + """suggestAttributes"""
    )
  )

  // @LINE:12
  private[this] lazy val controllers_ExtendTable_fetchTablePOST5_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("fetchTablePOST")))
  )
  private[this] lazy val controllers_ExtendTable_fetchTablePOST5_invoker = createInvoker(
    ExtendTable_2.fetchTablePOST(),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.ExtendTable",
      "fetchTablePOST",
      Nil,
      "POST",
      """""",
      this.prefix + """fetchTablePOST"""
    )
  )

  // @LINE:15
  private[this] lazy val controllers_Assets_versioned6_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("assets/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_versioned6_invoker = createInvoker(
    Assets_0.versioned(fakeValue[String], fakeValue[Asset]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "versioned",
      Seq(classOf[String], classOf[Asset]),
      "GET",
      """ Map static resources from the /public folder to the /assets URL path""",
      this.prefix + """assets/$file<.+>"""
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:6
    case controllers_Application_index0_route(params) =>
      call { 
        controllers_Application_index0_invoker.call(Application_1.index())
      }
  
    // @LINE:7
    case controllers_ExtendTable_ind1_route(params) =>
      call { 
        controllers_ExtendTable_ind1_invoker.call(ExtendTable_2.ind())
      }
  
    // @LINE:8
    case controllers_ExtendTable_fetchTable2_route(params) =>
      call(params.fromQuery[String]("name", None)) { (name) =>
        controllers_ExtendTable_fetchTable2_invoker.call(ExtendTable_2.fetchTable(name))
      }
  
    // @LINE:10
    case controllers_ExtendTable_search3_route(params) =>
      call { 
        controllers_ExtendTable_search3_invoker.call(ExtendTable_2.search())
      }
  
    // @LINE:11
    case controllers_ExtendTable_suggestAttributes4_route(params) =>
      call { 
        controllers_ExtendTable_suggestAttributes4_invoker.call(ExtendTable_2.suggestAttributes())
      }
  
    // @LINE:12
    case controllers_ExtendTable_fetchTablePOST5_route(params) =>
      call { 
        controllers_ExtendTable_fetchTablePOST5_invoker.call(ExtendTable_2.fetchTablePOST())
      }
  
    // @LINE:15
    case controllers_Assets_versioned6_route(params) =>
      call(Param[String]("path", Right("/public")), params.fromPath[Asset]("file", None)) { (path, file) =>
        controllers_Assets_versioned6_invoker.call(Assets_0.versioned(path, file))
      }
  }
}