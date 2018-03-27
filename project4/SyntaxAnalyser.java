import java.util.*;
import java.io.*;
import java.util.ArrayList;


class SyntaxAnalyser
{

   String[] Keywords= {" if", " else", " int", " return", " void", " while" };
   String[] SpecialSymbols = { "+", "-", "*", "/", "<", "<=", ">=", "==", "!=", "=", ";", ",", "(", ")", "[", "]", "{", "}" , "/*", "*/"};
   int LineNo=0, Nooftoken=0,size=0;  
   String[][][] Tokens = new String[100][150][2]; 
   String[][] codeGeneration = new String[150][4];                     //used to store code generation  
   int currentArrPostion=0;
   Stack<String> stack = new Stack<>();
   SymbolTable symTable;
   boolean mainflag= false;
   int numberofparameters=0;
   cell data;
   String funparams="";
   String funtype="";
   String returntype="";
   boolean returnthere=false;
   String ls ="", rs="";
   String arguments="";
   boolean re= false;
   ArrayList<cell> temParms = new ArrayList<cell>();
   //ArrayList<String[]> codeGeneration = new ArrayList<String[]>();     
   String[] code= new String[4];                                        //used to store line of instruction for code generation
   int varT=0;  
   String[] code2=new String [4];
   String[] code3=new String [4];
   public void syn (File file, String[][][] tokens)
   {
      size =getsize(file);
      symTable = new SymbolTable(size);
      Tokens=tokens;
    
      
      if (declarationlist())
         System.out.println("");
      else
         System.out.println("");
        
      displayCodeGeneration();
     // symTable.displayArray();   
   }
   
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   

   public boolean declarationlist ()
   {
      if ( variableDec() || functionDec() )
      {  
         if ( iscurrenttoken("$") && mainflag)
            return true ;
         else 
            return  declarationlist();       
      }
      
      else 
         return false;
   
   
   }// declaraiton list
 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
   public boolean variableDec()
   {
      int templineno=LineNo, tempnofotoken=Nooftoken;
      
      
      if (iscurrenttoken(" int") || iscurrenttoken(" float") )                        // or float
      {
         if ( typespecifier("ID"))
         {
            if ( iscurrenttoken(";"))
            { 
               
               codeGeneration[currentArrPostion][0]="alloc";
               codeGeneration[currentArrPostion][1]="4";
               codeGeneration[currentArrPostion][2]="     ";
               codeGeneration[currentArrPostion][3]=Tokens[templineno][tempnofotoken+1][1];   
               currentArrPostion++;
               
               data = new cell (Tokens[templineno][tempnofotoken+1][1]);           // Id name
               data.next= new cell (Tokens[templineno][tempnofotoken][1]);;              // type /int,float
                                   
               if (symTable.insertcell(data))                             //insert into symbole table by passing a cell of data that consist if Id and the type
               {
                  return true;
               }                                             // if the ID hasnt been declared before , it is inserted           
               else 
                  return false;                                            // Id already exist
            }
                  
            else if(iscurrenttoken("["))
            {
               stack.push("[");
               
               if (typespecifier("Num"))
               {  
                  //System.out.println("-----"+Tokens[templineno][tempnofotoken+3][1]);
                  if (iscurrenttoken("]"))
                  {
                     stack.pop();
                     if(iscurrenttoken(";"))
                     { 
                         
                        data = new cell (Tokens[templineno][tempnofotoken+1][1]);           // Id name
                        data.next= new cell (Tokens[templineno][tempnofotoken][1]);              // type /int,float
                        data.next.next=new cell (Tokens[templineno][tempnofotoken+3][1]); 
                        data.array=true;
                                                  
                       
                        codeGeneration[currentArrPostion][0]="alloc";
                        codeGeneration[currentArrPostion][1]=""+ (4*parseIntergerfromString(Tokens[templineno][tempnofotoken+3][1]) );
                        codeGeneration[currentArrPostion][2]="     ";
                        codeGeneration[currentArrPostion][3]=Tokens[templineno][tempnofotoken+1][1];   
                        currentArrPostion++;
                     
                        if (symTable.insertcell(data))                             //insert into symbole table by passing a cell of data that consist if Id and the type
                           return true;                                             // if the ID hasnt been declared before , it is inserted           
                        else 
                           return false;                                            // Id already exist
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
      int currentPostion=currentArrPostion;
      code3= new String[4];
      code3[0]="func";
      //System.out.println(currentPostion);
      if (mainflag == false)
      { 
         if  (iscurrenttoken(" int") || iscurrenttoken(" void") || iscurrenttoken(" float"))
         {  
            funtype=Tokens[templineno][tempnofotoken][1];                        //save the return type 
            
                        
            if ( typespecifier("ID"))
            {      
               if (Tokens[templineno][tempnofotoken +1][1].equals(" main"))
                  mainflag= true;
               
               code3[1]=Tokens[templineno][tempnofotoken +1][1];                     //code generation...function name
               code3[2]=funtype;                                                     //code generation...function type
               codeGeneration[currentPostion]=code;
               currentArrPostion++;
               
               if (iscurrenttoken("(")) 
               {
                  stack.push("(");
                  
                  if (parameters())
                  {  
                     code3[3]=""+numberofparameters;
                     codeGeneration[currentPostion]=code3;
                     
                     
                     
                     data= new cell (Tokens[templineno][tempnofotoken +1][1]);      // holds function name
                     data.function=true;
                     data.next= new cell (Tokens[templineno][tempnofotoken ][1]);   // holds return type
                     data.next.next= new cell (""+numberofparameters);  
                     data.next.next.next= new cell (funparams);                        
                   
                     if (symTable.insertcell(data) && iscurrenttoken(")") && stack.peek()== "(") //insert function declaration  into the symbol table 
                     { 
                         
                        funparams="";
                        numberofparameters=0;
                        stack.pop();
                        
                        if (funtype.equals(" void"))
                        {
                           returntype= " void";
                           returnthere=true;
                        }
                        
                      //  System.out.println("\nonesym");
                        //symTable.displayArray();
                        newsymboletable();
                        
                        for (cell blockofparms : temParms)
                        { int hg=0;                             
                           if (symTable.insertcell(blockofparms))
                              hg++;
                           else 
                              return false;
                        }
                        temParms.removeAll(temParms);
                       // System.out.println("\n2sym");
                        //symTable.displayArray();
                     
                        if (compoundstm()&&  returnthere && funtype.equals(returntype))                     //checks if there is a return statement 
                        { 
                           
                           returnthere=false;
                           funtype="";
                           removesysmboletable();
                            // System.out.println("\n3sym");
                           //symTable.displayArray();
                           return true;
                        }
                        
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
         numberofparameters++;
         //System.out.println(Tokens[LineNo][Nooftoken ][1]);
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
   {  int tn=LineNo,tk=Nooftoken;
      cell var;
      
      if (iscurrenttoken(" int") || iscurrenttoken(" float") )
      {   
         if (typespecifier("ID"))
         {
            int templineno=LineNo, tempnofotoken=Nooftoken;
                
            if (iscurrenttoken(",") || iscurrenttoken(")"))
            {
              
               //System.out.println(currentArrPostion);
               codeGeneration[currentArrPostion][0]="alloc";
               codeGeneration[currentArrPostion][1]="4";
               codeGeneration[currentArrPostion][2]="     ";
               codeGeneration[currentArrPostion][3]=Tokens[tn][tk+1][1];    
               currentArrPostion++;
            
               funparams=funparams+Tokens[tn][tk ][1]+"";
               
               var = new cell (Tokens[tn][tk+1][1]);           // Id name
               var.next= new cell (Tokens[tn][tk][1]);;              // type /int,float
               temParms.add(var);                
            
               LineNo=templineno;
               Nooftoken=tempnofotoken;
               return true;
            }  
            else if (iscurrenttoken("[") && iscurrenttoken("]") )
            {  
               var = new cell (Tokens[tn][tk+1][1]);           // Id name
               var.next= new cell (Tokens[tn][tk][1]);
               var.array=true;
               temParms.add(var);  
               funparams=funparams+" "+Tokens[tn][tk ][1]+Tokens[tn][tk+2 ][1]+Tokens[tn][tk+3 ][1]; 
               
               codeGeneration[currentArrPostion][0]="alloc";
               codeGeneration[currentArrPostion][1]="4";
               codeGeneration[currentArrPostion][2]="     ";
               codeGeneration[currentArrPostion][3]=Tokens[tn][tk+1][1];    
               currentArrPostion++;
            
                 
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
   
   
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   
  
   public boolean compoundstm()
   {
      if (iscurrenttoken("{"))
      {  
         newsymboletable();
         if (localdeclaration() && statementlist() )
         {  
            return true;
         }
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
      {         
         removesysmboletable();
                  
         return true;
      }
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
      { 
         returnthere= true;                                 // validates there is a return statement 
        
      
         return true;
         
      }
         
      else 
         return false;
   }
 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 

   public boolean expressionstmt()
   {
      if (iscurrenttoken(";"))
      {
         ls="";
         rs="";
         return true;
      }
      else if (expression())
      {
         if (iscurrenttoken(";") )
         {
            ls="";
            return true;
         }
         else
            return false;
      }
      else 
         return false;
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
   public boolean selectionstmt()
   { 
      int currentpos;
      if (iscurrenttoken("("))
      {   
         stack.push("(");
         if (expression())
         {
            currentpos=currentArrPostion;
            codeGeneration[currentArrPostion][0]="br";
            codeGeneration[currentArrPostion][1]="";
            codeGeneration[currentArrPostion][2]="";
            codeGeneration[currentArrPostion][3]="";    
            currentArrPostion++;
         
            
            if (iscurrenttoken(")") && stack.peek()== "(")
            {
               stack.pop();
               if (statement()) 
               {
                  codeGeneration[currentpos][3]=""+currentArrPostion;
                  if (iscurrenttoken(" else"))
                  {
                     currentpos=currentArrPostion;
                     codeGeneration[currentArrPostion][0]="br";
                     codeGeneration[currentArrPostion][1]="";
                     codeGeneration[currentArrPostion][2]="";
                     codeGeneration[currentArrPostion][3]="";    
                     currentArrPostion++;
                  
                     if (statement())
                     {
                        codeGeneration[currentpos][3]=""+currentArrPostion;
                        return true;
                     }
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
      int currentpos=currentArrPostion;
      int currentpos2=currentArrPostion;
    
      if (iscurrenttoken("("))
      {   
         stack.push("(");
         if (expression())
         { 
            currentpos2=currentArrPostion;
            codeGeneration[currentArrPostion][0]="br";
            codeGeneration[currentArrPostion][1]="";
            codeGeneration[currentArrPostion][2]="";
            codeGeneration[currentArrPostion][3]="";    
            currentArrPostion++;
         
            if (iscurrenttoken(")") && stack.peek()== "(")
            {
               stack.pop();
               if (statement()) 
               {
                  codeGeneration[currentArrPostion][0]="br";
                  codeGeneration[currentArrPostion][1]="";
                  codeGeneration[currentArrPostion][2]="";
                  codeGeneration[currentArrPostion][3]=""+currentpos;    
                  currentArrPostion++;
                  codeGeneration[currentpos2][3]=""+currentArrPostion;   
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
 
   public boolean returnstmt()
   {  int t=varT;
      if (iscurrenttoken(";") && (funtype.equals(" void")) )               // only return true if the function has a void return type
      { ls="";
         codeGeneration[currentArrPostion][0]="return";
         codeGeneration[currentArrPostion][1]="";
         codeGeneration[currentArrPostion][2]="";
         codeGeneration[currentArrPostion][3]="";    
         currentArrPostion++;
      
         return true;
      }
      else if ( expression())
      {  
         if (iscurrenttoken(";") && !(funtype.equals(" void")) )           // only return true if the function has an int/float return type
         {  returntype=ls;
            ls="";
            
            if (t==varT)
               codeGeneration[currentArrPostion][3] =Tokens[LineNo][Nooftoken-2][1];
            else 
               codeGeneration[currentArrPostion][3] ="t"+varT;
            
            
            codeGeneration[currentArrPostion][0]="return";
            codeGeneration[currentArrPostion][1]="";
            codeGeneration[currentArrPostion][2]="";
            
            currentArrPostion++;
         
            return true;
         }
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
         if (  !( searchsymbtable( Tokens[templineno][tempnofotoken][1]) ) )
            return false;
           
          
         ls=data.next.id;
         if (iscurrenttoken("="))
         { 
            if (!(ls.equals(data.next.id)))
               return false; 
                  
            if (expression())
            {
               codeGeneration[currentArrPostion][0]="assign";
               codeGeneration[currentArrPostion][1]="t"+varT;
               codeGeneration[currentArrPostion][2]="     ";
               codeGeneration[currentArrPostion][3]=Tokens[templineno][tempnofotoken][1];    
               currentArrPostion++;
            
               
               return true;
            }  
            else
               return false;
         }
         else  if (iscurrenttoken("["))
         {
            stack.push("[");
            String index=ls;
            if (expression())
            {
                    
               if (!(ls.equals(" int")))
                  return false;
                    
               if (iscurrenttoken("]") && stack.peek()== "[") 
               { stack.pop();
               
               
                  if(Tokens[LineNo][Nooftoken][1].equals(")"))
                     return true;
                  else if (iscurrenttoken("="))
                  { 
                     //System.out.println( "now" );
                  
                     if (   (Tokens[templineno][tempnofotoken+1][1].equals("[")) && (Tokens[templineno][tempnofotoken+2][0].equals("Num"))   && (  (Tokens[templineno][tempnofotoken+3][1].equals("]")  )  ))
                     {
                        //System.out.println( Tokens[templineno][tempnofotoken+1][1]+"now" );
                        //System.out.println( Tokens[templineno][tempnofotoken+2][0]+"now" );
                        //System.out.println( Tokens[templineno][tempnofotoken+3][1]+"now" ); 
                     
                        varT++;
                        codeGeneration[currentArrPostion][0]="disp";
                        codeGeneration[currentArrPostion][1]=Tokens[templineno][tempnofotoken][1];
                        codeGeneration[currentArrPostion][2]=Tokens[templineno][tempnofotoken+2][1];
                        codeGeneration[currentArrPostion][3]="t"+varT;    
                        currentArrPostion++;
                     }
                     else if ((Tokens[templineno][tempnofotoken+1][1].equals("[")) && ((Tokens[templineno][tempnofotoken+2][0].equals("ID")) )  && ((Tokens[templineno][tempnofotoken+3][1].equals("]"))))
                     {
                        varT++;
                        codeGeneration[currentArrPostion][0]="multi";
                        codeGeneration[currentArrPostion][1]=Tokens[templineno][tempnofotoken+2][1];
                        codeGeneration[currentArrPostion][2]="  4";
                        codeGeneration[currentArrPostion][3]="t"+varT;    
                        currentArrPostion++;
                     
                     
                        codeGeneration[currentArrPostion][0]="disp";
                        codeGeneration[currentArrPostion][1]=Tokens[templineno][tempnofotoken][1];
                        codeGeneration[currentArrPostion][2]="t"+varT;
                        varT++;
                        codeGeneration[currentArrPostion][3]="t"+varT;    
                        currentArrPostion++;
                     
                     
                     
                     }
                  
                     int t=varT;
                  
                     if (expression())
                     {
                        codeGeneration[currentArrPostion][0]="assign";
                        codeGeneration[currentArrPostion][1]="t"+varT;
                        codeGeneration[currentArrPostion][2]="     ";
                        codeGeneration[currentArrPostion][3]="t"+t;    
                        currentArrPostion++;
                     
                        return true;
                     
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
      int t=varT;
      if ( additiveexpression() )
      {  
         re=false;
         
         if (t==varT)
            code[1]=Tokens[LineNo][Nooftoken-1][1];
         else 
            code[1]="t"+varT;
          
         if (relop(t))
         { 
            t=varT;
            if ( additiveexpression())
            {
               if (t==varT)
                  code[2]=Tokens[LineNo][Nooftoken-1][1];
               else 
                  code[2]="t"+varT;
               
               codeGeneration[currentArrPostion][0]="comp";
               codeGeneration[currentArrPostion][1]=code[1];
               codeGeneration[currentArrPostion][2]=code[2];         //compare
               varT++;
               codeGeneration[currentArrPostion][3]="t"+varT;    
               currentArrPostion++;
               
               
               codeGeneration[currentArrPostion][0]=code[3];          //holds relop type
               codeGeneration[currentArrPostion][1]="t"+varT;              //br/gr
               codeGeneration[currentArrPostion][2]="     ";
               codeGeneration[currentArrPostion][3]=""+(currentArrPostion+2);    
               currentArrPostion++;
               
            
               return true ;
            }
            else 
               return false;
         }
         
         else if ( Tokens[LineNo][Nooftoken][1].equals(")") || Tokens[LineNo][Nooftoken][1].equals(";") || Tokens[LineNo][Nooftoken][1].equals("]") )   //return true on the followset of simple expression
            return true;
            
         else 
            return true;
      }
      
      else 
         return false;
   
   }
   
 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
 
   public boolean relop(int t)
   {
   
      if (iscurrenttoken("<="))
      {  re=true;
         code[3]="Br/LEq";
         return true;
      }
      else if(iscurrenttoken(">="))
      {
         code[3]="Br/GEq";
         return true;
      }
      else if(iscurrenttoken("<"))
      {
         code[3]="Br/LE";
         return true;
      }
      else if(iscurrenttoken(">"))
      {
         code[3]="Br/GR";
         return true;
      }
      else if(iscurrenttoken("=="))
      {
         code[3]="Br/Eq";
         return true;
      }
      else if( iscurrenttoken("!="))
      {
         code[3]="Br/NEq";
         return true;
      }
      else 
         return false;
      
   }
     ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   

 
   public boolean  additiveexpression()
   { int t=varT;
      if (term())
      {
                
         if (addop(t))
         {
            t=varT;
            if ( additiveexpression())
            {    
               if (t==varT)
                  code2[2]=Tokens[LineNo][Nooftoken-1][1];
               else 
                  code2[2]="t"+varT;
            
               codeGeneration[currentArrPostion][0]=code2[0];
               codeGeneration[currentArrPostion][1]=code2[1];
               codeGeneration[currentArrPostion][2]=code2[2];         //compare
               varT++;
               codeGeneration[currentArrPostion][3]="t"+varT;    
               currentArrPostion++;
               
               return true;
            }
            
            else
               return false;     
         }
           
               
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
   
   public boolean addop(int t)
   {
      if (iscurrenttoken("+")) 
      {  
         if (t==varT)
            code2[1]=Tokens[LineNo][Nooftoken-2][1];
         else 
            code2[1]="t"+varT;
         code2[0]="add";
      
         return true;
      }
      else if(iscurrenttoken("-"))
      {
         if (t==varT)
            code2[1]=Tokens[LineNo][Nooftoken-2][1];
         else 
            code2[1]="t"+varT;
            
         code2[0]="sub";
         
         return true;
      }
         
      else
         return false;
          
   } 
   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public boolean term()
   {  int t=varT;
      if (factor())
      { 
         
         if (mulop(t))
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
   public boolean mulop(int t)
   {
      
      if (iscurrenttoken("*"))
      {
         if (t == varT)
         {
         
            codeGeneration[currentArrPostion][0]="mult";
            codeGeneration[currentArrPostion][1]=Tokens[LineNo][Nooftoken-2][1];
            codeGeneration[currentArrPostion][2]=Tokens[LineNo][Nooftoken][1];
            varT++;
            codeGeneration[currentArrPostion][3]="t"+varT;    
            currentArrPostion++;
         
         }
         
         else
         {
            codeGeneration[currentArrPostion][0]="mult";
            codeGeneration[currentArrPostion][1]="t"+varT;
            codeGeneration[currentArrPostion][2]=Tokens[LineNo][Nooftoken][1];
            varT++;
            codeGeneration[currentArrPostion][3]="t"+varT;    
            currentArrPostion++;
         
         
         }
      
         return true;
      }  
      else if (iscurrenttoken("/"))
      {
         if (t == varT)
         {
         
            codeGeneration[currentArrPostion][0]="div";
            codeGeneration[currentArrPostion][1]=Tokens[LineNo][Nooftoken-2][1];
            codeGeneration[currentArrPostion][2]=Tokens[LineNo][Nooftoken][1];
            varT++;
            codeGeneration[currentArrPostion][3]="t"+varT;    
            currentArrPostion++;
         
         }
         
         else
         {
            codeGeneration[currentArrPostion][0]="div";
            codeGeneration[currentArrPostion][1]="t"+varT;
            codeGeneration[currentArrPostion][2]=Tokens[LineNo][Nooftoken][1];
            varT++;
            codeGeneration[currentArrPostion][3]="t"+varT;    
            currentArrPostion++;
         
         
         }
      
         
         return true;
      }
      else
         return false;
         
   } 
 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
       
   public boolean factor()
   {   int templineno=LineNo, tempnofotoken=Nooftoken;
      cell data2;
      
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
         if (  !( searchsymbtable( Tokens[templineno][tempnofotoken][1]) ) )
            return false;
            
         data2=data;
         //if(!(ls.equals(data2.next.id)) && !re )
            //return false;
               
         if (iscurrenttoken("["))
         {
            stack.push("[");
            if (expression())
            {
               if (iscurrenttoken("]") && stack.peek()== "[")
               {
               
                  if (   (Tokens[templineno][tempnofotoken+1][1].equals("[")) && (Tokens[templineno][tempnofotoken+2][0].equals("Num"))   && (  (Tokens[templineno][tempnofotoken+3][1].equals("]")  )  ))
                  {
                        //System.out.println( Tokens[templineno][tempnofotoken+1][1]+"now" );
                        //System.out.println( Tokens[templineno][tempnofotoken+2][0]+"now" );
                        //System.out.println( Tokens[templineno][tempnofotoken+3][1]+"now" ); 
                     
                     varT++;
                     codeGeneration[currentArrPostion][0]="disp";
                     codeGeneration[currentArrPostion][1]=Tokens[templineno][tempnofotoken][1];
                     codeGeneration[currentArrPostion][2]=Tokens[templineno][tempnofotoken+2][1];
                     codeGeneration[currentArrPostion][3]="t"+varT;    
                     currentArrPostion++;
                  }
                  else if ((Tokens[templineno][tempnofotoken+1][1].equals("[")) && ((Tokens[templineno][tempnofotoken+2][0].equals("ID")) )  && ((Tokens[templineno][tempnofotoken+3][1].equals("]"))))
                  {
                     varT++;
                     codeGeneration[currentArrPostion][0]="multi";
                     codeGeneration[currentArrPostion][1]=Tokens[templineno][tempnofotoken+2][1];
                     codeGeneration[currentArrPostion][2]="  4";
                     codeGeneration[currentArrPostion][3]="t"+varT;    
                     currentArrPostion++;
                     
                     
                     codeGeneration[currentArrPostion][0]="disp";
                     codeGeneration[currentArrPostion][1]=Tokens[templineno][tempnofotoken][1];
                     codeGeneration[currentArrPostion][2]="t"+varT;
                     varT++;
                     codeGeneration[currentArrPostion][3]="t"+varT;    
                     currentArrPostion++;
                     
                     
                     
                  }
               
               
               
               
               
                  stack.pop();
                  return true;
               }
               else
                  return false;
                
            }
            else
               return false;
         }
         else if  (iscurrenttoken("(") && data2.function)
         {   
            stack.push("(");
            numberofparameters=0;
            int cp=currentArrPostion;
            codeGeneration[currentArrPostion][0]="call";
            codeGeneration[currentArrPostion][1]=Tokens[templineno][tempnofotoken][1];
            codeGeneration[currentArrPostion][2]="     ";
            varT++;
            codeGeneration[currentArrPostion][3]="t"+varT;    
            currentArrPostion++;
         
            
            if (args())
            {  
               String numberofarguments=""+numberofparameters;
               
               if(iscurrenttoken(")") && stack.peek()== "(" && (data2.next.next.id ).equals(numberofarguments) && (data2.next.next.next.id ).equals(arguments))
               {  
               //Tokens[templineno][tempnofotoken][1]
                  codeGeneration[cp][2]=""+numberofparameters;
               
                  return true;
               }
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
      int t= varT;
      if (expression())
      {  
         //System.out.println(Tokens[LineNo][Nooftoken-1][1]);
         if (t==varT)
         {
            codeGeneration[currentArrPostion][0]="arg";
            codeGeneration[currentArrPostion][1]="     ";
            codeGeneration[currentArrPostion][2]="     ";
            codeGeneration[currentArrPostion][3]=Tokens[LineNo][Nooftoken-1][1];    
            currentArrPostion++;
         
         }
         else
         {
            codeGeneration[currentArrPostion][0]="arg";
            codeGeneration[currentArrPostion][1]="     ";
            codeGeneration[currentArrPostion][2]="     ";
            codeGeneration[currentArrPostion][3]="t"+ varT;    
            currentArrPostion++;
         
          
         }
         arguments=arguments+ls;
         numberofparameters++;
         if (iscurrenttoken(","))
            return args();
            
         else
         { //System.out.println("Args"+arguments);
            return true;
         }}
      
      else if (Tokens[LineNo][Nooftoken][1].equals(")"))
         return true;
         
      else
         return false;
      
      
   }
   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public void newsymboletable()
   {
      symTable.next=new SymbolTable(size);
      symTable.next.parent=symTable;
      symTable=symTable.next;
    
   }
   
       //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public void removesysmboletable()
   {
      if (symTable.parent != null)
      {
         symTable=symTable.parent;
         symTable.next = null;
      }
   }

       //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
   public boolean typespecifier( String type )
   {  
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
      try {
         if (Tokens[LineNo][Nooftoken][1].equals(str))
         {  nexttoken();
            return true;
         }
         else 
            return false;  
      }
      catch (IndexOutOfBoundsException e) {
         return false;
      }
      catch (NullPointerException e) {
      
         return false;
      
      }           
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

 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public boolean searchsymbtable(String id)
   { cell dt;
      SymbolTable Temptable= new SymbolTable( size);
      Temptable= symTable;
      
      while (Temptable != null )
      { 
         for (cell s : Temptable.table)
         {  
            if (s.id != null)
            {  
               if (s.id.equals(id))
               {  data= s;
                  return true;
               } 
            }
         }
         Temptable=Temptable.parent;
      }
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

   public void displayCodeGeneration()
   {     int i=0;
      
      System.out.println(" "+"  "+"Operations  "+"  Op1  "+"   Op2 "+"  Result\n");
    /*  for (int i=0; i<codeGeneration.length;i++)
      {  if(codeGeneration[i][0].equals(""))
            break;
            System.out.println(i+"  "+codeGeneration[i][0]+"  "+codeGeneration[i][1]+"  "+codeGeneration[i][2]+"  "+codeGeneration[i][3]);
      }*/
      for (String[] s : codeGeneration)
      {  
         if (s[0] != null)
            System.out.println(i+"  "+s[0]+" \t\t\t "+s[1]+"   "+s[2]+"    "+s[3]);
             
         i++;
      }
      
   }
   public int parseIntergerfromString (String s)
   { int num;
   // String str=Tokens[templineno][tempnofotoken+3][1];
      String[] strs=s.split(" ");
      num=Integer.parseInt(strs[1]);
   
   
      return num;
   }
   
   
   
   


}// syntaxclass


