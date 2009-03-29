package com.wellhead.lasso
import java.sql.{Connection,DriverManager,ResultSet, SQLException, Statement, PreparedStatement}
import java.io.File
import Util.withConnection
import scala.collection.jcl.Conversions._
/*
class SqliteReader extends LasReader {
  Class.forName("org.sqlite.JDBC")
  var connection:Connection = null

  def query(string:String) = {
    val statement = connection.createStatement
    statement.executeQuery(string)
  }

  def lasfilePrimaryKey(name:String) = {
    query("SELECT id FROM lasfiles WHERE name='" + name + "'").getInt(1)
  }

  def headerDescriptors(headerId:Int) = {
    val rs = query("SELECT * FROM descriptors WHERE header_id="+headerId)
    val descriptors = new ArrayList[Descriptor]
    while(rs.next){
      val id = rs.getInt(1)
      val mnemonic = rs.getInt(2)
      val unit = rs.getString(3)
      val data = rs.getString(4)
      val description = rs.getString(5)
      val isIndex = rs.getBoolean(6)
      
    
    
	
}
*/
