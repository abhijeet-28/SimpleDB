package simpledb.parse;

import java.text.ParseException;
import java.util.*;
import simpledb.query.*;
import simpledb.record.Schema;
import java.text.SimpleDateFormat;

/**
 * The SimpleDB parser.
 * @author Edward Sciore
 */
public class Parser {
   private Lexer lex;
   
   public Parser(String s) {
      lex = new Lexer(s);
   }
   
// Methods for parsing predicates, terms, expressions, constants, and fields
   
   public String field() {
      return lex.eatId();
   }
   
   public Constant constant()  {

      if (lex.matchIntConstant())
      {
        
         return new IntConstant(lex.eatIntConstant());
      }

      else if(lex.matchTimeStampConstant())

      {

         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         dateFormat.setLenient(false);

         try{

            String ss=lex.eatStringConstant();
            dateFormat.parse(ss);
           
         return new TimeStampConstant(ss);}
         catch(ParseException e)
         {
            
            throw new RuntimeException("InValidDateFormatError");
         }
      }

      else
         {
            String st=lex.eatStringConstant();
            

             return new StringConstant(st);      
         }
      
     
      
   }


public Constant constant1()
   {
      String st=lex.eatStringConstant();
         String en=lex.eatStringConstant();
         
      try
      {
         
         return new DualTimeStampConstant(st,en);

      }
      catch(ParseException e)
      {
         return new StringConstant(st +en);
      }

   }

   public Expression newexpr()
   {
      return new ConstantExpression(constant1());
   }



   
   
   public Expression expression() {
      if (lex.matchId())
         return new FieldNameExpression(field());
      else
         return new ConstantExpression(constant());
   }
   
   // public Term term() {
   //    Expression lhs = expression();
   //    lex.eatDelim('=');
   //    Expression rhs = expression();
   //    System.out.println(lhs.toString());
   //    System.out.println("---------------");
   //    System.out.println(rhs.toString());
   //    return new Term(lhs, rhs);
   // }


public Term term() {
      Expression lhs = expression();
      
      Expression rhs;
      if(lex.matchKeyword("between"))
      {
         lex.eatKeyword("between");
         rhs=newexpr();
         

      }
      else
      {
         lex.eatDelim('=');
         rhs = expression();
      }
      
      
      return new Term(lhs, rhs);
   }



   
   public Predicate predicate() {

      Predicate pred = new Predicate(term());
      if (lex.matchKeyword("and")) {
         lex.eatKeyword("and");
         pred.conjoinWith(predicate());
      }
      
      return pred;
   }
   
// Methods for parsing queries
   
   public QueryData query() {
      lex.eatKeyword("select");
      Collection<String> fields = selectList();
      lex.eatKeyword("from");
      Collection<String> tables = tableList();
      Predicate pred = new Predicate();
      if (lex.matchKeyword("where")) {
         lex.eatKeyword("where");
         pred = predicate();
      }
      return new QueryData(fields, tables, pred);
   }
   
   private Collection<String> selectList() {
      Collection<String> L = new ArrayList<String>();
      L.add(field());
      if (lex.matchDelim(',')) {
         lex.eatDelim(',');
         L.addAll(selectList());
      }
      return L;
   }
   
   private Collection<String> tableList() {
      Collection<String> L = new ArrayList<String>();
      L.add(lex.eatId());
      if (lex.matchDelim(',')) {
         lex.eatDelim(',');
         L.addAll(tableList());
      }
      return L;
   }
   
// Methods for parsing the various update commands
   
   public Object updateCmd() {
      if (lex.matchKeyword("insert"))
         return insert();
      else if (lex.matchKeyword("delete"))
         return delete();
      else if (lex.matchKeyword("update"))
         return modify();
      else
         return create();
   }
   
   private Object create() {
      lex.eatKeyword("create");
      if (lex.matchKeyword("table"))
         return createTable();
      else if (lex.matchKeyword("view"))
         return createView();
      else
         return createIndex();
   }
   
// Method for parsing delete commands
   
   public DeleteData delete() {
      lex.eatKeyword("delete");
      lex.eatKeyword("from");
      String tblname = lex.eatId();
      Predicate pred = new Predicate();
      if (lex.matchKeyword("where")) {
         lex.eatKeyword("where");
         pred = predicate();
      }

      return new DeleteData(tblname, pred);
   }
   
// Methods for parsing insert commands
   
   public InsertData insert() {
      lex.eatKeyword("insert");
      lex.eatKeyword("into");
      String tblname = lex.eatId();
      lex.eatDelim('(');
      List<String> flds = fieldList();

      lex.eatDelim(')');
      lex.eatKeyword("values");
      lex.eatDelim('(');
      List<Constant> vals = constList();
      // for (Constant x:vals)
      // {
      //    System.out.println(x.toString());
      // }
      lex.eatDelim(')');
      return new InsertData(tblname, flds, vals);
   }
   
   private List<String> fieldList() {
      List<String> L = new ArrayList<String>();
      L.add(field());
      if (lex.matchDelim(',')) {
         lex.eatDelim(',');
         L.addAll(fieldList());
      }
      return L;
   }
   
   private List<Constant> constList() {
      List<Constant> L = new ArrayList<Constant>();
      L.add(constant());
      if (lex.matchDelim(',')) {
         lex.eatDelim(',');
         L.addAll(constList());
      }
      return L;
   }
   
// Method for parsing modify commands
   
   public ModifyData modify() {
      lex.eatKeyword("update");
      String tblname = lex.eatId();
      lex.eatKeyword("set");
      String fldname = field();
      lex.eatDelim('=');
      Expression newval = expression();
      Predicate pred = new Predicate();
      if (lex.matchKeyword("where")) {
         lex.eatKeyword("where");
         pred = predicate();
      }
      return new ModifyData(tblname, fldname, newval, pred);
   }
   
// Method for parsing create table commands
   
   public CreateTableData createTable() {
      lex.eatKeyword("table");
      String tblname = lex.eatId();
      lex.eatDelim('(');
      Schema sch = fieldDefs();

      lex.eatDelim(')');
      return new CreateTableData(tblname, sch);
   }
   
   private Schema fieldDefs() {
      Schema schema = fieldDef();
      if (lex.matchDelim(',')) {
         lex.eatDelim(',');
         Schema schema2 = fieldDefs();
         schema.addAll(schema2);
      }
      return schema;
   }
   
   private Schema fieldDef() {
      String fldname = field();

      return fieldType(fldname);
   }
   
   private Schema fieldType(String fldname) {
      Schema schema = new Schema();
      if (lex.matchKeyword("int")) {
         lex.eatKeyword("int");
         schema.addIntField(fldname);
      }
      else if(lex.matchKeyword("timestamp")) {
         lex.eatKeyword("timestamp");

         schema.addtimestampField(fldname);
      }      

      else {
         lex.eatKeyword("varchar");
         lex.eatDelim('(');
         int strLen = lex.eatIntConstant();
         lex.eatDelim(')');
         schema.addStringField(fldname, strLen);
      }
      return schema;
   }
   
// Method for parsing create view commands
   
   public CreateViewData createView() {
      lex.eatKeyword("view");
      String viewname = lex.eatId();
      lex.eatKeyword("as");
      QueryData qd = query();
      return new CreateViewData(viewname, qd);
   }
   
   
//  Method for parsing create index commands
   
   public CreateIndexData createIndex() {
      lex.eatKeyword("index");
      String idxname = lex.eatId();
      lex.eatKeyword("on");
      String tblname = lex.eatId();
      lex.eatDelim('(');
      String fldname = field();
      lex.eatDelim(')');
      return new CreateIndexData(idxname, tblname, fldname);
   }
}

