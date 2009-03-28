package com.wellhead.lasso
import java.sql.{Connection,DriverManager,ResultSet,SQLException,Statement, PreparedStatement}
import java.io.File
import Util.withConnection
import scala.collection.jcl.Conversions._

object DatabaseCreator {
  Class.forName("org.sqlite.JDBC")
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
  isindex BOOLEAN,
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

  def update(s:String)(implicit statement:Statement) { 
    statement.executeUpdate(s)
  }

  def createDB(path:String) {
    withConnection("jdbc:sqlite:"+path){ 
      connection => {
	val statement = connection.createStatement
	statement.executeUpdate(create_lasfiles)
	println("executed\n" + create_lasfiles)
	statement.executeUpdate(create_headers)
	println("executed\n" + create_headers)
	statement.executeUpdate(create_descriptors)
	println("executed\n" + create_descriptors)
	statement.executeUpdate(create_data)
	println("executed\n" + create_data)
      }
    }
  }
}

class LasFileDB extends LasWriter {
  Class.forName("org.sqlite.JDBC")
  private var connection:Connection = null
  private var insert_lasfile_statement:PreparedStatement = null
  private var insert_header_statement:PreparedStatement = null
  private var insert_descriptor_statement:PreparedStatement = null
  private var insert_data_statement:PreparedStatement = null

  def prepare_insert_lasfile_statement = {
    connection.prepareStatement("INSERT INTO lasfiles(name) VALUES(?)")
  }

  def prepare_insert_header_statement = {
    connection.prepareStatement("INSERT INTO headers(type,prefix,lasfile_id) VALUES(?,?,?)")
  }

  def prepare_insert_descriptor_statement = {
    connection.prepareStatement("INSERT INTO descriptors(mnemonic,unit,data,description,isindex,header_id)" + 
				" VALUES(?,?,?,?,?,?)")
  }

  def prepare_insert_data_statement = {
    connection.prepareStatement("INSERT INTO data(row,value,descriptor_id) VALUES(?,?,?)")
  }

  override def writeLasFile(lf:LasFile, path:String) { 
    if(! (new File(path).exists)) { DatabaseCreator.createDB(path) }
    withConnection("jdbc:sqlite:"+path){
      connection => {
	setup(connection)
	val statement = connection.createStatement
	statement.executeUpdate("BEGIN EXCLUSIVE TRANSACTION")
	val insert_lasfile = insert_lasfile_statement
	insert_lasfile.setString(1, lf.getName)
	insert_lasfile.executeUpdate()
	val lf_pk = statement.executeQuery("SELECT MAX(id) FROM lasfiles").getInt(1)
	insertHeaders(lf_pk, lf)      
	statement.executeUpdate("END TRANSACTION")
      }
    }
  } 

  def setup(connection:Connection) {
    this.connection = connection
    insert_lasfile_statement = prepare_insert_lasfile_statement
    insert_header_statement = prepare_insert_header_statement
    insert_descriptor_statement = prepare_insert_descriptor_statement
    insert_data_statement = prepare_insert_data_statement
  }

  def insertHeaders(lf_pk:Integer, lasfile:LasFile){
    insertHeader(lf_pk, lasfile.getVersionHeader)
    insertHeader(lf_pk, lasfile.getWellHeader)
    insertHeader(lf_pk, lasfile.getParameterHeader)
    insertCurveHeader(lf_pk, lasfile)
  }

  def insertHeader(lf_pk:Integer, header:Header){
    val insert_header = insert_header_statement
    insert_header.setString(1,header.getType)
    insert_header.setString(2,header.getPrefix)
    insert_header.setString(3,lf_pk.toString)
    insert_header.executeUpdate()

    val h_pk = connection.createStatement.executeQuery("SELECT MAX(id) from headers").getInt(1)
    for(d <- header.getDescriptors){
      val insert_descriptor = insert_descriptor_statement
      insert_descriptor.setString(1, d.getMnemonic)
      insert_descriptor.setString(2, d.getUnit)
      insert_descriptor.setString(3, d.getData)
      insert_descriptor.setString(4, d.getDescription)
      insert_descriptor.setBoolean(5, false)
      insert_descriptor.setString(6, h_pk.toString)
      insert_descriptor.executeUpdate()
    }
  }

  def insertCurveHeader(lf_pk:Integer, lasfile:LasFile) {
    val header = lasfile.getCurveHeader
    val insert_header = insert_header_statement
    insert_header.setString(1, header.getType)
    insert_header.setString(2, header.getPrefix)
    insert_header.setString(3, lf_pk.toString)
    insert_header.executeUpdate()

    val h_pk = connection.createStatement.executeQuery("SELECT MAX(id) from headers").getInt(1)
    for(d <- header.getDescriptors){
      println("finding curve " + d.getMnemonic)
      val isIndex = lasfile.getIndex.getMnemonic == d.getMnemonic
      val curve = if(isIndex) lasfile.getIndex else lasfile.getCurve(d.getMnemonic)

      val insert_descriptor = insert_descriptor_statement
      insert_descriptor.setString(1, d.getMnemonic)
      insert_descriptor.setString(2, d.getUnit)
      insert_descriptor.setString(3, d.getData)
      insert_descriptor.setString(4, d.getDescription)
      insert_descriptor.setBoolean(5, isIndex)
      insert_descriptor.setString(6, h_pk.toString)
      insert_descriptor.executeUpdate()

      val d_pk = connection.createStatement.executeQuery("SELECT MAX(id) from descriptors").getInt(1)
      var row = 0
      for(ld <- curve.getLasData){
	val insert_data = insert_data_statement
	insert_data.setString(1,row.toString)
	insert_data.setString(2,ld.toString)
	insert_data.setString(3,d_pk.toString)
	insert_data.executeUpdate()
	row += 1
      }
    }
  }
}

