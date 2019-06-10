package authentication

import javax.inject.Inject

import controllers.Assets
import exceptions.InvalidToken
import play.api.{Configuration, Logger}
import utils.Encryptor
import play.api.mvc._
import play.shaded.ahc.io.netty.handler.codec.http.HttpResponseStatus

import scala.concurrent.{ExecutionContext, Future}

case class AuthRequest[A](val nroCuenta: BigInt, val fechaCierre: String, val plataforma: String, request: Request[A]) extends WrappedRequest[A](request)

class AuthAction @Inject()(val parser: BodyParsers.Default, config: Configuration)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AuthRequest, AnyContent] with ActionTransformer[Request, AuthRequest] {
  def transform[A](request: Request[A]): Future[AuthRequest[A]] = {

    val f = handleToken(request) match {
      case Some(t) => Future.successful(t)
      case None => Future.failed(new InvalidToken("Invalid token"))
    }
    f
  }

  private def handleToken[A](request: Request[A]): Option[AuthRequest[A]] = {
    val token = request.headers.get("Authorization")
    token match {
      case Some(string) =>

        val decoded: Option[AuthRequest[A]] = {
          try {
            val dec = Encryptor.decrypt(string, config.get[String]("encrypt.key"), config.get[String]("encrypt.initialVector"))
            val tokenSplit = dec.split('|')
            Option(new AuthRequest(BigInt(tokenSplit(0)), tokenSplit(1), tokenSplit(2), request))
          } catch {
            case e: Exception => {
              Logger.error(e.getMessage)
              None
            }
          }
        }
        decoded
      case None =>
        None
    }
  }
}
