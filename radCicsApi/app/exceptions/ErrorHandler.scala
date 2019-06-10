import javax.inject._

import exceptions.InvalidToken
import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router

import scala.concurrent._

@Singleton
class ErrorHandler @Inject() (
                               env: Environment,
                               config: Configuration,
                               sourceMapper: OptionalSourceMapper,
                               router: Provider[Router]
                             ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override def onProdServerError(request: RequestHeader, exception: UsefulException) = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }

  override protected def onDevServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    Future.successful(
      exception.cause match {
        case e: InvalidToken => BadRequest("Invalid token received")
        case _ => InternalServerError("Internal error")
      }
    )
  }

  override protected def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(
      BadRequest(message)
    )
  }

  override def onForbidden(request: RequestHeader, message: String) = {
    Future.successful(
      Forbidden("You're not allowed to access this resource.")
    )
  }
}