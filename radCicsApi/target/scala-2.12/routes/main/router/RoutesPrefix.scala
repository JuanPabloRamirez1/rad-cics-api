// @GENERATOR:play-routes-compiler
// @SOURCE:/home/juan/rbs/rad-cics-api/radCicsApi/conf/routes
// @DATE:Mon Jun 10 17:43:53 ART 2019


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
