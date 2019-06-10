// @GENERATOR:play-routes-compiler
// @SOURCE:/home/juan/rbs/rad-cics-api/radCicsApi/conf/routes
// @DATE:Mon Jun 10 17:43:53 ART 2019

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseMovimientosController MovimientosController = new controllers.ReverseMovimientosController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAssets Assets = new controllers.ReverseAssets(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseMovimientosController MovimientosController = new controllers.javascript.ReverseMovimientosController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAssets Assets = new controllers.javascript.ReverseAssets(RoutesPrefix.byNamePrefix());
  }

}
