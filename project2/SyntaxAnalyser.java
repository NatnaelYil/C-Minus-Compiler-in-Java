import java.util.*;
import java.io.*;
import java.util.ArrayList;


class SyntaxAnalyser
{

   String[] Keywords= {" if", " else", " int", " return", " void", " while" };
   String[] SpecialSymbols = { "+", "-", "*", "/", "<", "<=", ">=", "==", "!=", "=", ";", ",", "(", ")", "[", "]", "{", "}" , "/*", "*/"};
   int LineNo=0, Nooftoken=0,size=0;  
   String[][][] Tokens = new String[100][80][2];  
   Stack<String> stack = new Stack<>();

   public void syn (File file, String[][][] tokens)
   {
      size =getsize(file);
      SymbolTable symTable = new SymbolTable(size);
      Tokens=tokens;
      
      
      if (declarationlist())
         System.out.println("Accept");
      else
         System.out.println("Reject");
   }
   
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   

   public boolean declarationlist ()
   {
      if ( variableDec() || functionDec() )
      {  
         if ( iscurrenttoken("$"))
            return true ;
         else 
            return  declarationlist();       
      }
      
      else 
         return false;
   
   
   }// declaraiton list
 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
   public boolean variableDec()
   { int templineno=LineNo, tempnofotoken=Nooftoken;
      if (iscurrenttoken(" int"))
      {
         if ( typespecifier("ID"))
         {
            if ( iscurrenttoken(";"))
               return true;            /////////insert into symbole table
               
            else if(iscurrenttoken("["))
            {
               stack.push("[");
               
               if (typespecifier("Num"))
               {  
                  if (iscurrenttoken("]"))
                  {
                     stack.pop();
                     if(iscurrenttoken(";"))
                        return true;   //////////////////////insert into symbole table
                        
                     else
                        return false;     
                  }
                  else
                     return false;     
               }
               else
                  return false;
            }
            else 
            {
               LineNo= templineno;
               Nooftoken=tempnofotoken;              ///it could be a function call
               return false;
                             
            }
         }     
               
         else
         
            return false;
         
      }
      
      else 
         return false;     
   
   
   }//vardecalaration
   
   
 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
   
   public boolean functionDec()
   { 
      int templineno=LineNo, tempnofotoken=Nooftoken;
      //System.out.println( Tokens[LineNo][Nooftoken][0]+": "+Tokens[LineNo][Nooftoken][1]);
      if  (iscurrenttoken(" int") || iscurrenttoken(" void") )
      {
         if ( typespecifier("ID"))
         {
            if (iscurrenttoken("(")) 
            {
               stack.push("(");
               if (parameters())
               {
                  if (iscurrenttoken(")") && stack.peek()== "(")
                  {
                     stack.pop();
                     
                     if (compoundstm())
                        return true;
                        
                     else 
                        return false;
                  }
                  else 
                     return false;
               }
               else 
                  return false ; 
            }
            else
               return false ;   
         }
         else
            return false; 
      }
      else 
         return false;
   }
 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 

   public boolean parameters()
   {
      if (iscurrenttoken(" void") || paramlist())
         return true;
      else 
         return false ;     
   }

 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
 
   public boolean paramlist ()
   {  
      if (param())
      {
         if (iscurrenttoken(","))
            return paramlist();
            
         else 
            return true;
      }
      
      else 
         return false ;
   }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
   
   public boolean param ()
   {
      if (iscurrenttoken(" int"))
      {   
         if (typespecifier("ID"))
         {
            int templineno=LineNo, tempnofotoken=Nooftoken;
                
            if (iscurrenttoken(",") || iscurrenttoken(")"))
            {
               LineNo= templineno;
               Nooftoken=tempnofotoken;
               return true;                                       /// insert in symbole table
            }  
            else if (iscurrenttoken("[") && iscurrenttoken("]") )
               return true;                                         /// insert in symbole table
            else 
               return false;         
                  
         }
         else 
            return false;
      }
      else 
         return false;
   }
   
   
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   
  
   public boolean compoundstm()
   {
      if (iscurrenttoken("{"))
      {
         if (localdeclaration() && statementlist())
            return true;
            
         else 
            return false;
      }
      else 
         return false ;
   }



 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 

   public boolean localdeclaration()
   {  
      int templineno=LineNo, tempnofotoken=Nooftoken;
     
      while (variableDec())
      {  
         templineno=LineNo;
         tempnofotoken=Nooftoken;
      }
      LineNo=templineno;
      Nooftoken=tempnofotoken;
      
      return true;
   }
   
 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
   public boolean statementlist()
   {
      while (statement()) {}
      
      if ( iscurrenttoken("}") )
         return true;
         
      else 
         return false;
      
   }

 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
   public boolean statement()
   {
      if ( (Tokens[LineNo][Nooftoken][1].equals(";") || Tokens[LineNo][Nooftoken][1].equals("+") || Tokens[LineNo][Nooftoken][1].equals("-") || 
            Tokens[LineNo][Nooftoken][1].equals("*") || Tokens[LineNo][Nooftoken][1].equals("/") || Tokens[LineNo][Nooftoken][1].equals("(") ||
            Tokens[LineNo][Nooftoken][0].equals("ID")|| Tokens[LineNo][Nooftoken][0].equals("Num")) && (expressionstmt()))                                           // goes in on the first of expressionstmt()
         return true;
      else if ( (Tokens[LineNo][Nooftoken][1].equals("{")) && compoundstm() ) 
         return true;
      else if ( iscurrenttoken(" if") && selectionstmt() )  
         return true;
      else if ( iscurrenttoken(" while") && itterationstmt() )  
         return true;
      else if ( iscurrenttoken(" return") && returnstmt() ) 
         return true;
      else 
         return false;
   }
 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 

   public boolean expressionstmt()
   {
      if (iscurrenttoken(";"))
         return true;
      else if (expression())
      {
         if (iscurrenttoken(";"))
            return true;
            
         else
            return false;
      }
      else 
         return false;
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
   public boolean selectionstmt()
   {
      if (iscurrenttoken("("))
      {   
         stack.push("(");
         if (expression())
         {
            if (iscurrenttoken(")") && stack.peek()== "(")
            {
               stack.pop();
               if (statement()) 
               {
                  if (iscurrenttoken(" else"))
                  {
                     if (statement())
                        return true;
                        
                     else 
                        return false;
                  }
                  else 
                     return true;
               }
               else 
                  return false;   
            }
            else 
               return false;
         
         }
         else 
            return false;
      }
      else 
         return false;
   }
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
 
   public boolean itterationstmt()
   {
      if (iscurrenttoken("("))
      {   
         stack.push("(");
         if (expression())
         {
            if (iscurrenttoken(")") && stack.peek()== "(")
            {
               stack.pop();
               if (statement()) 
                  return true;
                  
               else 
                  return false;   
            }
            else 
               return false;
         
         }
         else 
            return false;
      }
      else 
         return false;  
   
   }
 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
 
   public boolean returnstmt()
   {
      if (iscurrenttoken(";"))
         return true;
      else if ( expression())
      {
         if (iscurrenttoken(";"))
            return true;
            
         else
            return false;  
      }
      else 
         return false; 
         
   }
     /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
   
   public boolean expression()
   {
      int templineno=LineNo, tempnofotoken=Nooftoken;
    
      if (typespecifier("ID"))
      {
         if (iscurrenttoken("="))
         {
            if (expression())
               return true;
               
            else
               return false;
         }
         else  if (iscurrenttoken("["))
         {
            stack.push("[");
            
            if (expression())
            {
               if (iscurrenttoken("]") && stack.peek()== "[") 
               { stack.pop();
               
                  if(Tokens[LineNo][Nooftoken][1].equals(")"))
                     return true;
                  else if (iscurrenttoken("="))
                  { 
                     if (expression())
                        return true;
                     else
                        return false;
                  }
                  else
                  {
                     LineNo=templineno;
                     Nooftoken=tempnofotoken;
                  
                     if (simpleexpression())
                        return true;
                     
                     else 
                        return false;
                  
                  }
               
                     
               }
               else 
                  return false;   
            }
            else
               return false;
         
         }
         else
         {
            LineNo=templineno;
            Nooftoken=tempnofotoken;
            
            if (simpleexpression())
               return true;
               
            else 
               return false;
            
         }
          
      }
      else if (simpleexpression())
         return true;
         
      else
       
         return false;
      
   }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
   public boolean simpleexpression()
   {
      if ( additiveexpression() )
      {
         if (relop())
            return  additiveexpression();
            
         else if ( Tokens[LineNo][Nooftoken][1].equals(")") || Tokens[LineNo][Nooftoken][1].equals(";") || Tokens[LineNo][Nooftoken][1].equals("]") )   //return true on the followset of simple expression
            return true;
            
         else 
            return true;
      }
      else 
         return false;
   
   }
   
 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
 
   public boolean relop()
   {
      if (iscurrenttoken("<=") || iscurrenttoken(">=") || iscurrenttoken("<") || iscurrenttoken(">") || iscurrenttoken("==") || iscurrenttoken("!="))
         return true;
         
      else 
         return false;
      
   }
     ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   

 
   public boolean  additiveexpression()
   {
      if (term())
      {
         if (addop())
            return  additiveexpression();
               
         else if (Tokens[LineNo][Nooftoken][1].equals(")") || Tokens[LineNo][Nooftoken][1].equals(";") || Tokens[LineNo][Nooftoken][1].equals("]")||
         Tokens[LineNo][Nooftoken][1].equals("<=") || Tokens[LineNo][Nooftoken][1].equals("<") || Tokens[LineNo][Nooftoken][1].equals(">") ||
         Tokens[LineNo][Nooftoken][1].equals(">=") || Tokens[LineNo][Nooftoken][1].equals("==") || Tokens[LineNo][Nooftoken][1].equals("!=") ||
          Tokens[LineNo][Nooftoken][1].equals("+") || Tokens[LineNo][Nooftoken][1].equals("-") || Tokens[LineNo][Nooftoken][1].equals("*") || 
           Tokens[LineNo][Nooftoken][1].equals("/")|| Tokens[LineNo][Nooftoken][1].equals(","))
            return true;
               
         else 
            return false;
           
      }
      else 
         return false;
   }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
   
   public boolean addop()
   {
      if (iscurrenttoken("+")|| iscurrenttoken("-"))
         return true;
         
      else
         return false;
          
   } 
   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public boolean term()
   {
      if (factor())
      {
         if (mulop())
            return term();
         
         else if (Tokens[LineNo][Nooftoken][1].equals(")") || Tokens[LineNo][Nooftoken][1].equals(";") || Tokens[LineNo][Nooftoken][1].equals("]")||
         Tokens[LineNo][Nooftoken][1].equals("<=") || Tokens[LineNo][Nooftoken][1].equals("<") || Tokens[LineNo][Nooftoken][1].equals(">") ||
         Tokens[LineNo][Nooftoken][1].equals(">=") || Tokens[LineNo][Nooftoken][1].equals("==") || Tokens[LineNo][Nooftoken][1].equals("!=") ||
         Tokens[LineNo][Nooftoken][1].equals("+")  || Tokens[LineNo][Nooftoken][1].equals("-") || Tokens[LineNo][Nooftoken][1].equals("*") ||
         Tokens[LineNo][Nooftoken][1].equals("/")  || Tokens[LineNo][Nooftoken][1].equals(",") ||  Tokens[LineNo][Nooftoken][1].equals("=") )
            return true;
            
         else 
            return false;
      }
      else
         return false;
   
   }
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////     
   public boolean mulop()
   {
      if (iscurrenttoken("*") || iscurrenttoken("/"))
         return true;
         
      else
         return false;
         
   } 
 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
       
   public boolean factor()
   {
      if (iscurrenttoken("("))
      {
         stack.push("(");
         if (expression())
         {
            if (iscurrenttoken(")") && stack.peek()== "(") 
            { 
               stack.pop();
               return true;
            }
            else 
               return false;    
         }
         
         else
            return false;
      }  
      else if (typespecifier("ID"))
      {
         if (iscurrenttoken("["))
         {
            stack.push("[");
            if (expression())
            {
               if (iscurrenttoken("]") && stack.peek()== "[")
               {
                  stack.pop();
                  return true;
               }
               else
                  return false;
                
            }
            else
               return false;
         }
         else if  (iscurrenttoken("("))
         {
            stack.push("(");
            
            if (args())
            {
               if(iscurrenttoken(")") && stack.peek()== "(") 
                  return true;
                  
               else
                  return false;
            }
            
            else
               return false;
            
         }
         else if (Tokens[LineNo][Nooftoken][1].equals(")") || Tokens[LineNo][Nooftoken][1].equals(";") || Tokens[LineNo][Nooftoken][1].equals("]") ||
         Tokens[LineNo][Nooftoken][1].equals("<=") || Tokens[LineNo][Nooftoken][1].equals("<") || Tokens[LineNo][Nooftoken][1].equals(">") ||
         Tokens[LineNo][Nooftoken][1].equals(">=") || Tokens[LineNo][Nooftoken][1].equals("==") || Tokens[LineNo][Nooftoken][1].equals("!=") ||
          Tokens[LineNo][Nooftoken][1].equals("+") || Tokens[LineNo][Nooftoken][1].equals("-") || Tokens[LineNo][Nooftoken][1].equals("*") || 
           Tokens[LineNo][Nooftoken][1].equals("/")  || Tokens[LineNo][Nooftoken][1].equals(","))                                 // return in the follow of additiveepression() and expression()
            return true;
            
         else 
            return false;
           
      
      }
      else if (typespecifier("Num"))
         return true;
         
      else
         return false;
   }
   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   public boolean args()
   {
      if (expression())
      {
         if (iscurrenttoken(","))
            return args();
            
         else
            return true;
      }
      
      else if (Tokens[LineNo][Nooftoken][1].equals(")"))
         return true;
         
      else
         return false;
      
      
   }
   
   
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////     
  
 
  
   public boolean typespecifier( String type )
   {  // System.out.println( Tokens[LineNo][Nooftoken][0]+"WITH"+type);
      if ( Tokens[LineNo][Nooftoken][0].equals(type))
      {
         nexttoken();
         return true;
      }
      else 
         return false;      
   
   }
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////     
   public boolean iscurrenttoken(String str)
   
   {
      if (Tokens[LineNo][Nooftoken][1].equals(str))
      {  nexttoken();
         return true;
      }
      else 
         return false;   
   }
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
   public boolean peekatcurrenttoken(String str)
   
   {
      if (Tokens[LineNo][Nooftoken][1].equals(str))
      {  nexttoken();
         return true;
      }
      else 
         return false;  
   }
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  







   public int getsize(File file)
   {
      int size=0;
      try (Scanner filescan = new Scanner(new FileReader(file)))
      {
         while (filescan.hasNextLine())                                  //unitl file is scanned to the end
         { 
            filescan.nextLine();                                     //scan and determine the number of lines present
            size++;
         }
      
      }
      catch (IOException e)
      {
         System.out.println("Cannot read file ");                                    //handles exceptions
         System.exit(0);
      }
   
   
      
      
      size =getprime(2*size);                                     //get the size of the array to built from the number of possible elements from file
      
      
      
      
      return size;
   }
  
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////     
   public  int getprime (int min)                       // get the next prime number for size of array
   { 
      for (int j=min+1;true;j++)
         if (isprime(j))
            return j;
   }
//---------------------------------------------------------------  


   public  boolean isprime(int n)                       // checks whether the number is prime or not
   {
      for (int j=2;(j*j<=n);j++)
         if (n%j==0)
            return false;
      return true;
   }
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////     
   public void nexttoken  ()
   {
   
      if (Nooftoken==79)
      {   Nooftoken=0;
         LineNo++;
      }
          
      else 
         Nooftoken++;
   }


}// syntaxclass


