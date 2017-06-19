package simpledb.query;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat; 
import java.lang.ClassCastException; 

/**
 * The class that wraps Java strings as database constants.
 * @author Edward Sciore
 */
public class TimeStampConstant implements Constant {
   //private String val;
   private Date datetime;
   
   
   
   /**
    * Create a constant by wrapping the specified string.
    * @param s the string value
 * @throws ParseException 
    */
   public TimeStampConstant(String s) throws ParseException {
    
      datetime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);  
   }
   
   /**
    * Unwraps the string and returns it.
    * @see simpledb.query.Constant#asJavaVal()
    */
   public String asJavaVal() {
     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
     String datestring = dateFormat.format(datetime); 
     return datestring;
    //return datetime;
   }
   
   




public boolean equals(Object obj) {



 

try{
      TimeStampConstant sc = (TimeStampConstant) obj;
      
      if( datetime.compareTo(sc.datetime)==0)
      {
        
        
        return true;
      }
      else
      {
        
        return false;
      }
    }
      catch(ClassCastException e)
      {


        try{

      DualTimeStampConstant sc = (DualTimeStampConstant) obj;

      if(sc.startdatetime.compareTo(sc.enddatetime)>0)
      {
        throw new RuntimeException("InvalidIntervalError");
      }
      if(datetime.equals(sc.startdatetime) || datetime.equals(sc.enddatetime))
      {
        
        return true;
          }

      if(datetime.after(sc.startdatetime)  && datetime.before(sc.enddatetime))
      {
       
        return true;
      }
      else
      {
        
        return false;
      }
    }
    catch(ClassCastException ex)
    {
      return false;
    }


      }








   }




   
   // public int compareTo(Constant c) {
   //    TimeStampConstant sc = (TimeStampConstant) c;
   //    return datetime.compareTo(sc.datetime);
   // }








public int compareTo(Constant c) {



  

      try{
      TimeStampConstant sc = (TimeStampConstant) c;
      return datetime.compareTo(sc.datetime);}
      catch(ClassCastException e)
      {


        try{


          
          



      DualTimeStampConstant sc = (DualTimeStampConstant) c;




      if(datetime.before(sc.startdatetime))
      {
        
        return -1;
      }
      else if(datetime.after(sc.enddatetime) || datetime.equals(sc.enddatetime) || datetime.equals(sc.startdatetime))
      {
        
        return 1;
      }
      else

        {

          return 0;

        }












     
    }
    catch(ClassCastException ex)
    {
      return 0;
    }


      }
   }

















   
   public int hashCode() {
      return datetime.hashCode();
   }



   
   public String toString() {
	   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  
	   String datestring = dateFormat.format(datetime); 
     
	   return datestring;
   }
   public Date getdatetime()
   {
    return datetime;
   }

}
