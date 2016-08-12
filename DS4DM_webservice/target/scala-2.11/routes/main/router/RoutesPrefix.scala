
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/annalisa/git/DS4DM/DS4DM_webservice/conf/routes
// @DATE:Fri Aug 12 16:56:45 CEST 2016


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
