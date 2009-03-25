package org.jlas
import java.sql.{Connection,DriverManager,ResultSet,SQLException,Statement}

object LasFileDB {
  val create_lasfiles = """
CREATE TABLE lasfiles 
(
id INTEGER PRIMARY KEY AUTOINCREMENT,
name STRING
)
"""
  val create_headers = """
CREATE TABLE headers
(
id INTEGER PRIMARY KEY AUTOINCREMENT,
type STRING,
prefix STRING,
lasfile_id INTEGER,  
FOREIGN KEY (lasfile_id) REFERENCES lasfiles(id)
)
"""

  val create_descriptors = """
CREATE TABLE descriptors
(
id INTEGER PRIMARY KEY AUTOINCREMENT,
mnemonic STRING,
unit STRING,
data STRING,
description STRING,
header_id INTEGER,
FOREIGN KEY (header_id) REFERENCES headers(id)
)
"""

  val create_data = """
CREATE TABLE data
(
id INTEGER PRIMARY KEY AUTOINCREMENT,
row INTEGER,
value STRING,
descriptor_id INTEGER,
FOREIGN KEY (descriptor_id) REFERENCES descriptors(id)
)
"""

  def createDB {
    Class.forName("org.sqlite.JDBC")
    var connection:Connection = null
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:las.db")
      val statement = connection.createStatement
      statement.setQueryTimeout(30)
      def update(s:String) = statement.executeUpdate(s)
      statement.executeUpdate(create_lasfiles)
      statement.executeUpdate(create_headers)
      statement.executeUpdate(create_descriptors)
      statement.executeUpdate(create_data)
    } finally {
      if(connection != null){
	connection.close()
      }
    }
  }

  def saveLasFile(lf:LasFile) { 
    var connection:Connection = null
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:las.db")
      val statement = connection.createStatement
      statement.setQueryTimeout(30)
      def update(s:String) = statement.executeUpdate(s)
      def query(s:String) = statement.executeQuery(s)
      update("BEGIN EXCLUSIVE TRANSACTION")
      update("INSERT INTO lasfiles(name) VALUES(" + quote(lf.getName) + ")")
      val lf_pk = query("SELECT MAX(id) FROM lasfiles").getInt(1)
      insertHeaders(lf_pk, lf, statement)      
      update("END TRANSACTION")
    } finally {
      if(connection != null){
	connection.close()
      }
    }
  } 

  def insertHeaders(lf_pk:Integer, lasfile:LasFile, statement:Statement){
    insertHeader(lf_pk, lasfile.getVersionHeader, statement)
    insertHeader(lf_pk, lasfile.getWellHeader, statement)
    insertHeader(lf_pk, lasfile.getParameterHeader, statement)
    insertCurveHeader(lf_pk, lasfile, statement)
  }

  def insertHeader(lf_pk:Integer, header:Header, statement:Statement){
    statement.executeUpdate("INSERT INTO headers(type,prefix,lasfile_id) VALUES("+
			    quote(header.getType) + "," +
			    quote(header.getPrefix) + "," +
			    quote(lf_pk.toString) + ")")
    val h_pk = statement.executeQuery("SELECT MAX(id) from headers").getInt(1)
    for(d <- header.getDescriptors){
      statement.executeUpdate("INSERT INTO descriptors(mnemonic,unit,data,description,header_id) VALUES("+
			      quote(d.getMnemonic) + ", " +
			      quote(d.getUnit) + ", " + 
			      quote(d.getData) + ", " +
			      quote(d.getDescription) + ", " +
			      quote(h_pk.toString) + ")")
    }
  }

def insertCurveHeader(lf_pk:Integer, lasfile:LasFile, statement:Statement) {
  val header = lasfile.getCurveHeader
  statement.executeUpdate("INSERT INTO headers(type,prefix,lasfile_id) VALUES("+
			  quote(header.getType) + "," +
			  quote(header.getPrefix) + "," +
			  quote(lf_pk.toString) + ")")
  val h_pk = statement.executeQuery("SELECT MAX(id) from headers").getInt(1)
  for(d <- header.getDescriptors){
    println("finding curve " + d.getMnemonic)
    val curve = 
      if(lasfile.getIndex.getMnemonic == d.getMnemonic) 
	lasfile.getIndex 
      else 
	lasfile.getCurve(d.getMnemonic)

    statement.executeUpdate("INSERT INTO descriptors(mnemonic,unit,data,description,header_id) VALUES("+
			    quote(d.getMnemonic) + ", " +
			    quote(d.getUnit) + ", " + 
			    quote(d.getData) + ", " +
			    quote(d.getDescription) + ", " +
			    quote(h_pk.toString) + ")")
    val d_pk = statement.executeQuery("SELECT MAX(id) from descriptors").getInt(1)
    var row = 0
    for(ld <- curve.getLasData){
      statement.executeUpdate("INSERT INTO data(row,value,descriptor_id) VALUES("+
			      quote(row.toString) + ", " + 
			      quote(ld.toString) + ", " +
			      quote(d_pk.toString) + ")")
      row += 1
    }
  }
}
  def quote(s:String) = "'"+s+"'"
}
  
