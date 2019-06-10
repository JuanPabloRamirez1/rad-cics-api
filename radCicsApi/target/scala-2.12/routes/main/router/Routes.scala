// @GENERATOR:play-routes-compiler
// @SOURCE:/home/juan/rbs/rad-cics-api/radCicsApi/conf/routes
// @DATE:Mon Jun 10 17:43:53 ART 2019

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:6
  MovimientosController_1: controllers.MovimientosController,
  // @LINE:9
  Assets_0: controllers.Assets,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:6
    MovimientosController_1: controllers.MovimientosController,
    // @LINE:9
    Assets_0: controllers.Assets
  ) = this(errorHandler, MovimientosController_1, Assets_0, "/")

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, MovimientosController_1, Assets_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """movimientos""", """controllers.MovimientosController.getMovimientosPorTarjeta"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """assets/""" + "$" + """file<.+>""", """controllers.Assets.versioned(path:String = "/public", file:Asset)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:6
  private[this] lazy val controllers_MovimientosController_getMovimientosPorTarjeta0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("movimientos")))
  )
  private[this] lazy val controllers_MovimientosController_getMovimientosPorTarjeta0_invoker = createInvoker(
    MovimientosController_1.getMovimientosPorTarjeta,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MovimientosController",
      "getMovimientosPorTarjeta",
      Nil,
      "GET",
      this.prefix + """movimientos""",
      """""",
      Seq()
    )
  )

  // @LINE:9
  private[this] lazy val controllers_Assets_versioned1_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("assets/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_versioned1_invoker = createInvoker(
    Assets_0.versioned(fakeValue[String], fakeValue[Asset]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "versioned",
      Seq(classOf[String], classOf[Asset]),
      "GET",
      this.prefix + """assets/""" + "$" + """file<.+>""",
      """ Map static resources from the /public folder to the /assets URL path""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:6
    case controllers_MovimientosController_getMovimientosPorTarjeta0_route(params@_) =>
      call { 
        controllers_MovimientosController_getMovimientosPorTarjeta0_invoker.call(MovimientosController_1.getMovimientosPorTarjeta)
      }
  
    // @LINE:9
    case controllers_Assets_versioned1_route(params@_) =>
      call(Param[String]("path", Right("/public")), params.fromPath[Asset]("file", None)) { (path, file) =>
        controllers_Assets_versioned1_invoker.call(Assets_0.versioned(path, file))
      }
  }
}
