package simpledb.query;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat; 
import java.lang.ClassCastException; 

/**
 * The class that wraps Java strings as database constants.
 * @author Edward Sciore
 */
public class DualTimeStampConstant implements Constant {
   //private String val;
   public Date startdatetime;
   public Date enddatetime;
   
   
   
   /**
    * Create a constant by wrapping the specified string.
    * @param s the string value
 * @throws ParseException 
    */
   public DualTimeStampConstant(String s,String e) throws ParseException {
    
      startdatetime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);  
      enddatetime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(e);
      if(startdatetime.compareTo(enddatetime)>0)
      {
        throw new RuntimeException("InvalidIntervalError");
      }
   }
   
   /**
    * Unwraps the string and returns it.
    * @see simpledb.query.Constant#asJavaVal()
    */
   public String asJavaVal() {
     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
     String datestring1 = dateFormat.format(startdatetime); 
     String datestring2 = dateFormat.format(enddatetime); 
     return datestring1+" "+datestring2;
    //return datetime;
   }
   
   public boolean equals(Object obj) {


    

try{
      DualTimeStampConstant sc = (DualTimeStampConstant) obj;
      if(sc==null)
        return false;
      if( startdatetime.compareTo(sc.startdatetime)==0)
      {
        return false;
      }
      else if(enddatetime.compareTo(sc.enddatetime)==0)
      {
        return false;
      }
      return true;
    }
      catch(ClassCastException e)
      {


        try{



      TimeStampConstant sc = (TimeStampConstant) obj;
      if(sc.getdatetime().equals(startdatetime))

      {
        
        return true;
      }
      if(sc.getdatetime().equals(enddatetime))
      {
        return true;
      }
      // if(sc.getdatetime().equals(startdatetime) || sc.getdatetime().equals(enddatetime))
      // {
      //   return true;
      // }
      if(sc.getdatetime().after(startdatetime)  && sc.getdatetime().before(enddatetime))
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

  




   
   public int compareTo(Constant c) {
    


      DualTimeStampConstant sc = (DualTimeStampConstant) c;
      return startdatetime.compareTo(sc.startdatetime);
   }
   
   public int hashCode() {
      return startdatetime.hashCode();
   }



   
   public String toString() {
	   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  
	   String datestring1 = dateFormat.format(startdatetime);
     String datestring2 = dateFormat.format(enddatetime); 
     
	   return datestring1+" "+datestring2;
   }

}
