
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/annalisa/git/DS4DM/DS4DM_webservice/conf/routes
// @DATE:Fri Jan 27 14:10:36 GMT 2017

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseExtendTable ExtendTable = new controllers.ReverseExtendTable(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAssets Assets = new controllers.ReverseAssets(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseApplication Application = new controllers.ReverseApplication(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseExtendTable ExtendTable = new controllers.javascript.ReverseExtendTable(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAssets Assets = new controllers.javascript.ReverseAssets(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseApplication Application = new controllers.javascript.ReverseApplication(RoutesPrefix.byNamePrefix());
  }

}
