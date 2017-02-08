
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/annalisa/git/DS4DM/DS4DM_webservice/conf/routes
// @DATE:Tue Feb 07 14:57:36 CET 2017


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
