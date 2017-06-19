package simpledb.planner;

import simpledb.tx.Transaction;
import simpledb.parse.*;
import simpledb.query.*;
import java.util.*;

/**
 * The object that executes SQL statements.
 * @author sciore
 */
public class Planner {
   private QueryPlanner qplanner;
   private UpdatePlanner uplanner;
   private static HashMap<String,Integer> size1=new HashMap<String,Integer>();
   
   public Planner(QueryPlanner qplanner, UpdatePlanner uplanner) {
      this.qplanner = qplanner;
      this.uplanner = uplanner;
   }
   
   /**
    * Creates a plan for an SQL select statement, using the supplied planner.
    * @param qry the SQL query string
    * @param tx the transaction
    * @return the scan corresponding to the query plan
    */
   public Plan createQueryPlan(String qry, Transaction tx) {
      Parser parser = new Parser(qry);
      QueryData data = parser.query();
      return qplanner.createPlan(data, tx);
   }
   
   /**
    * Executes an SQL insert, delete, modify, or
    * create statement.
    * The method dispatches to the appropriate method of the
    * supplied update planner,
    * depending on what the parser returns.
    * @param cmd the SQL update string
    * @param tx the transaction
    * @return an integer denoting the number of affected records
    */
   public int executeUpdate(String cmd, Transaction tx) {

    
      Parser parser = new Parser(cmd);
      Object obj = parser.updateCmd();
      if (obj instanceof InsertData)
      {
        int xx= uplanner.executeInsert((InsertData)obj, tx);
        String name=((InsertData)obj).tableName();
        //System.out.println(name+"in insert");
        
        if(size1.containsKey(name))
        {
          
          int pre=size1.get(name);

          if(pre<100000)
          {
            
            pre+=xx;
           
          size1.put(name,pre);
          }
          else
          {
            throw new RuntimeException("MemoryError");
          }
          

        }
        else
        {
          size1.put(name,xx);

        }

        return xx;



         
       }
      else if (obj instanceof DeleteData)
      {

         int xx= uplanner.executeDelete((DeleteData)obj, tx);
         String name=((DeleteData)obj).tableName();
          if(size1.containsKey(name))
        {
          
          int pre=size1.get(name);
             pre-=xx;
           
          size1.put(name,pre);
          
          

        }
        

        return xx;
       }
      else if (obj instanceof ModifyData)
         return uplanner.executeModify((ModifyData)obj, tx);
      else if (obj instanceof CreateTableData)
      {
        String name=((CreateTableData)obj).tableName();
          int xx=uplanner.executeCreateTable((CreateTableData)obj, tx);
          size1.put(name,0);
          System.out.println(name+"in insert");
          return xx;
       }
      else if (obj instanceof CreateViewData)
         return uplanner.executeCreateView((CreateViewData)obj, tx);
      else if (obj instanceof CreateIndexData)
         return uplanner.executeCreateIndex((CreateIndexData)obj, tx);
      else
         return 0;
   }
}
