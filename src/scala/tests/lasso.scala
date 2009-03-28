package com.wellhead.lasso
import org.scalatest._

class LassoTest extends FunSuite {
  test("lasso las_files/test.las sqlite://las.db"){
    Main.main("las_files/test.las sqlite://las.db".split(" "))
  }
  test("lasso las_files/x4.las sqlite://las.db"){
    Main.main("las_files/x4.las sqlite://las.db".split(" "))
  }
  test("lasso las_files/test.las clojure://my_clj.clj"){
    Main.main("las_files/test.las clojure://my_clj.clj".split(" "))
  }
/*  test("lasso sqlite://las.db/test.las from_db_test.las") {
    Main.main("sqlite://las.db from_db_test.las".split(" "))
  }
  test("lasso sqlite://las.db/x4.las from_db_x4.las") {
    Main.main("sqlite://las.db/x4.las from_db_x4.las".split(" "))
  }
*/
}

