package controllers

import authentication.AuthAction
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import services.MicrofilmService

import scala.concurrent.ExecutionContext

class MicrofilmController @Inject()(cc: ControllerComponents, microfilmService: MicrofilmService, authAction: AuthAction)(implicit exec: ExecutionContext)
  extends AbstractController(cc) {

  def getMicrofilmData(microfilmId: String): Unit ={

    val serviceResponse = microfilmService.callMicrofilmData(microfilmId)
    ???
  }

}
