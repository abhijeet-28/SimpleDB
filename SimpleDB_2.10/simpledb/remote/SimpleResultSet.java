package simpledb.remote;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.rmi.RemoteException;
import java.text.ParseException;

/**
 * An adapter class that wraps RemoteResultSet.
 * Its methods do nothing except transform RemoteExceptions
 * into SQLExceptions.
 * @author Edward Sciore
 */
public class SimpleResultSet extends ResultSetAdapter {
   private RemoteResultSet rrs;
   
   public SimpleResultSet(RemoteResultSet s) {
      rrs = s;
   }
   
   public boolean next() throws SQLException {
      try {
         return rrs.next();
      }
      catch (Exception e) {
         throw new SQLException(e);
      }
   }
   
   public int getInt(String fldname) throws SQLException {
      try {
         return rrs.getInt(fldname);
      }
      catch (Exception e) {
         throw new SQLException(e);
      }
   }
   
   public String getString(String fldname) throws SQLException {
      try {
         return rrs.getString(fldname);
      }
      catch (Exception e) {
         throw new SQLException(e);
      }
   }
   public java.sql.Date getTimestampP2(String fldname) throws RemoteException {
      try {
         String s1= rrs.getString(fldname);
          java.sql.Date datetime= new Date((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s1)).getTime());
          return datetime;

      }
      catch (RemoteException e) {
         e.printStackTrace();
      }
      catch (ParseException e) {
         e.printStackTrace();
      }
      return null;
   }
   
   
   public ResultSetMetaData getMetaData() throws SQLException {
      try {
         RemoteMetaData rmd = rrs.getMetaData();
         return new SimpleMetaData(rmd);
      }
      catch (Exception e) {
         throw new SQLException(e);
      }
   }
   
   public void close() throws SQLException {
      try {
         rrs.close();
      }
      catch (Exception e) {
         throw new SQLException(e);
      }
   }
}

