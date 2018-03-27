/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author NATI-PC
 */
import java.util.*;
import java.io.*;
import java.util.ArrayList;
class Lexical 
{   
   boolean iscomment = false;    // variable use too indicate is a line is a nested comment
   String[] Keywords= {" if", " else", " int", " return", " void", " while" };
   String[] SpecialSymbols = { "+", "-", "*", "/", "<=", ">=", "<", ">", "==", "!=", "=", ";", ",", "(", ")", "[", "]", "{", "}" , "/*", "*/"};
   String line;                 
   String word,ID;               //strings used to store word and Id 
   String num,error;             //variable used to store number and error strings
   
   int i =0;                     // counter for the current character being proccesed from a line
   int depth=0;                  // used to count the deepth of nested comment 
   int size =0;  
   int LineNo=0, Nooftoken=0; 
   
   String[][][] Tokens = new String[100][150][2];  
     
     
    // This Function reads each line from the input file and calls the "lex ()" to run lexical analysis 
   public void  parse (File file)
   {
      
      
      try (BufferedReader br = new BufferedReader(new FileReader(file)))            // open reader and catch an error if it occurs
           
      {            
         
                
         while ((line = br.readLine()) != null)                                     // read each line until it reaches the end of file
         {  
                           
            lex(line);      
                                                                      // pass the line to function lex for lexical analysis
         }
         Tokens[LineNo][Nooftoken][0]="$";
         Tokens[LineNo][Nooftoken][1]="$";
         /*for (int i=0;i < 100;i++ )
         
         {
            for(int j=0;j<150;j++ )
            {
               if (Tokens[i][j][1] != null)
                  System.out.println(j+""+Tokens[i][j][1]);
            } 
         }*/
         
         SyntaxAnalyser syntax = new SyntaxAnalyser();
         syntax.syn(file, Tokens);
         
      
      }
         
      catch (IOException e)
      {
         System.out.println("Cannot read file ");                                    //handles exceptions
         System.exit(0);
      }
   
      
      
      
   }
      
     //This functon performs lexical analysis by accepting a line of string and validating each characer in the string
   public void lex (String input)
     
   {  i=0;
    
        
      if (iscomment == true)                                // check to see if the current line is in a nest loop
      {
         while ( i<input.length())                          //until it reachs the end of line
         {   
            if (i==(input.length()-1) && (depth > 0))      //insures that the next two statemets dont try to access out side the avaible characters in a line
            {  i++;
               break;                                      // exist and proccess the next line
               
            }
         
         
            if (input.charAt(i)== '/' && input.charAt(i+1)== '*')       // if the curent and next charaacter is a start of nested comment
            {  
               depth++;                                                 // increment the depth by one                  
               i=i+1;                                                   // increament to the next character 
            }                          
            else if (input.charAt(i)== '*' && input.charAt(i+1)== '/')  //if the curent and next charaacter is the end of nested comment
            {
               depth--;                                                 //decrement the depth by one
               i=i+1;                                                   // increament to the next character 
            }
            if (depth==0)                                               // if out of a comment ...exist the loop and continue down     
            {  iscomment=false;                                         // indicate the nested loop is over
               i++;                                                              
               break;                                                   // exist the while loop
            }                            
            i++; 
         }//while
      
        
      }//if
        
        
        
      OUTTER:                                                           // helps to exist from a loop
      while (i<input.length())                                          //until the end of line
      {
        
      
      
         while (  i<input.length() && (input.charAt(i)== '\t' || input.charAt(i)== ' ' || input.charAt(i)== '\n') )      //if the character is empty,a tab or new line character continue to the next character        
         { 
            //System.out.println("i= " + i +"--->  "+ input.charAt(i) );
            i++;
            if (i==input.length())                                       //  if the counter reaches endof line exist the loop 
               break OUTTER;
         }
      
      
         if (input.charAt(i)== '/')                                     // if the character is a start of a comment character
         {  
         
         
            if ((i+1)==input.length())                                  // if its the last character in the line , print it and exit
            { //System.out.println(input.charAt(i));
               break;
            }
            if(i<input.length() && input.charAt(i+1)== '/')             // check if the counter doesnt pass the no of line and if the next character in the line constructs a line comment ,skip the line
            {  
               i= input.length() +1;
               break;
                                
                                 
            }
            
            else if (input.charAt(i+1)== '*')                           //if the next char is a start of nest loop
            
            { 
            
            
               i=i+2;
               depth++;                                                 //increament the depth by one
               iscomment=true;                                          //indicate it is a nested comment
               
              
               while ( i<input.length())                                // until the end of line
               
               { 
                
                  if (i==(input.length()-1) && (depth > 0))             //exist condition from the loop if a line ends while in a nested comment
                  {  i++;
                     break;
                  }
                
                  if (input.charAt(i)== '/' && input.charAt(i+1)== '*') // if there is a nested comment inside the nested comment , increament the depth by 1 and move to the second char 
                  {  depth++;
                     i=i+1;
                  }                          
                  else if (input.charAt(i)== '*' && input.charAt(i+1)== '/') // if a nested comment ends
                  {     
                     depth--;                                                // decrease the depth of the comment by 1
                     i=i+1;                                                  // move to the next char
                  }
                  if (depth==0)                                               //exist condition from the loop if there is no more nested comment
                  {  iscomment=false;                                         // indicate that the nested comment is no more for the next line to be proccessed
                     
                     break ;                                                  //exist from this loop
                  }                            
                  i++; 
                  
               } //nested comment while-1
               
            
               
            } //else if
            else
            {//System.out.println(input.charAt(i)); 
               Tokens[LineNo][Nooftoken][0]="SpecialSymbols";
               Tokens[LineNo][Nooftoken][1]= ""+input.charAt(i);
               tokenittroter();
            }
                                      // if it is not a start of a nest loop ...print the '/' symbol
            
         }//if
         
         
         else  if ((input.charAt(i) >= 'a' && input.charAt(i) <= 'z') || (input.charAt(i) >= 'A' && input.charAt(i) <= 'Z')) // if the starting letter is a character
         {
         
         
            word=" " +input.charAt(i);                                        // add it to the word variable
                                                                              
            if(!isEndofLine(input))                                                                //if the next characters are number or letters ...add them to the word variable                                                                  
            {
               while    ((input.charAt(i+1) >= 'a' && input.charAt(i+1) <= 'z') || (input.charAt(i+1) >= 'A' && input.charAt(i+1) <= 'Z') )     
               {
                  i++;
                  word= word + input.charAt(i);
               
                  if (i==(input.length()-1))                                     //if reached while counting ...exist the loop
                  {
                     if(identify(word)) 
                     {//System.out.println("Keyword= " + word);                  // identify the word if its a keyword
                        Tokens[LineNo][Nooftoken][0]="Keyword";
                        Tokens[LineNo][Nooftoken][1]=word;
                        tokenittroter();
                     }   
                     
                     else 
                     {//System.out.println("ID= " + word);                       // or its an ID
                        Tokens[LineNo][Nooftoken][0]="ID";
                        Tokens[LineNo][Nooftoken][1]=word;
                        tokenittroter();
                     } 
                     word=" ";
                  
                     break OUTTER ;                                              // exist to the outer loop
                  }
               
               }
            }  // System.out.println("word= " + word );
            
            if(identify(word)) 
            {//System.out.println("Keyword= " + word);                  // identify the word if its a keyword
               Tokens[LineNo][Nooftoken][0]="Keyword";
               Tokens[LineNo][Nooftoken][1]=word;
               tokenittroter();
            }   
                     
            else 
            {//System.out.println("ID= " + word);                       // or its an ID
               Tokens[LineNo][Nooftoken][0]="ID";
               Tokens[LineNo][Nooftoken][1]=word;
               tokenittroter();
            } 
         
            word=" ";
               
         }
         
         
         
         // algorithm to deal with numbers,digits and floating number
            
            
         else if ( (input.charAt(i) >= '0' )  && (input.charAt(i) <= '9'))       //if the character starts with a number
         {  
            num=" ";
          
            do {
            
               num = num+input.charAt(i);  
               i++;
               
               if (i>= input.length())                                        // if reached the end of line while doing so ...print the number and exist to outter while loop
               { 
                  //System.out.println("Num: " + num);
                  Tokens[LineNo][Nooftoken][0]="Num";
                  Tokens[LineNo][Nooftoken][1]=""+num;
                  tokenittroter();
                  break OUTTER ;
               }
            
            
            } while ((input.charAt(i) >= '0' ) && (input.charAt(i) <= '9'));
          
         // System.out.println("checking in symbol ="+ input.charAt(i));
          
                
            if(input.charAt(i)== '.')                                          // if the number is a floating num                              
            { 
              
               
               if (  (!isEndofLine(input))   &&  ((input.charAt(i+1) >= '0') && (input.charAt(i+1) <= '9')) )        //check to see if the nxt char is a number
               {  
                  do {
                  
                     num = num+input.charAt(i);  
                     i++;
                  
                     if (i>= input.length())                                       // if reached the end of line while doing so ...print the number and exist to outter while loop
                     {
                       //System.out.println("Num: " + num);
                        Tokens[LineNo][Nooftoken][0]="Num";
                        Tokens[LineNo][Nooftoken][1]=""+num;
                        tokenittroter();
                        break OUTTER ;
                     }
                  
                  
                  } while ((input.charAt(i) >= '0' ) && (input.charAt(i) <= '9'));
                  
                  
                  if ( input.charAt(i) == 'E')     
                  { 
                                                                         
                     
                     if (   (!isEndofLine(input))   &&   ((input.charAt(i+1) >= '0') && (input.charAt(i+1) <= '9') || input.charAt(i+1) == '-' || input.charAt(i+1) == '+'))  //if the next char is a +/-signn or just a number
                     {  
                        num = num+input.charAt(i);  
                        i++;
                     
                        do {
                        
                           num = num+input.charAt(i);  
                           i++;
                               
                           if (i>= input.length())                                         // if reached the end of line while doing so ...print the number and exist to outter while loop
                           {
                              //System.out.println("Num: " + num);
                              Tokens[LineNo][Nooftoken][0]="Num";
                              Tokens[LineNo][Nooftoken][1]=""+num;
                              tokenittroter();
                              break OUTTER ;
                           }
                        
                        
                        } while ((input.charAt(i) >= '0' ) && (input.charAt(i) <= '9'));
                        
                        //String[][][] Tokens = new String[100][80][2];
                     
                     }
                     
                     else 
                     {  
                        error=Error(input);                                               // error condition
                     }
                  }
                
               }
               
               else 
               { 
                  error=Error(input);                                                        // error condition
               }
              
             
            }
            
            else if   (input.charAt(i) == 'E')                                                // if the next character is E
            
            {  
               if (   (!isEndofLine(input))   &&   ((input.charAt(i+1) >= '0') && (input.charAt(i+1) <= '9') || input.charAt(i+1) == '-' || input.charAt(i+1) == '+'))  //if the next char is a +/-signn or just a number
               {  
                  num = num+input.charAt(i);  
                  i++;
                     
                  do {
                        
                     num = num+input.charAt(i);  
                     i++;
                               
                     if (i>= input.length())                                       // if reached the end of line while doing so ...print the number and exist to outter while loop
                     {
                        //System.out.println("Num: " + num);
                        Tokens[LineNo][Nooftoken][0]="Num";
                        Tokens[LineNo][Nooftoken][1]=""+num;
                        tokenittroter();
                        break OUTTER ;
                     }
                        
                        
                  } while ((input.charAt(i) >= '0' ) && (input.charAt(i) <= '9'));
                  
                  
               
               
               }
                     
               else 
               {  
                  error=Error(input);                                               // error condition
               }              
               
            }  
                               
            //System.out.println("Num: " + num);
            Tokens[LineNo][Nooftoken][0]="Num";
            Tokens[LineNo][Nooftoken][1]=""+num;
            tokenittroter();
                                                     //print the numbers and errors if present
               
            if (error != null )
            {// System.out.println("Error="+ error);
               error=null; 
            }
            else if ( specialsybidf (input, i) != 0 )
            { // 
               if  (specialsybidf (input, i)==3) 
               { //
                  continue;
               
               }
            
               if (specialsybidf(input, i) == 2)
               {//System.out.println( input.charAt(i) + input.charAt(i+1));
                  Tokens[LineNo][Nooftoken][0]="SpecialSymbols";
                  Tokens[LineNo][Nooftoken][1]= ""+input.charAt(i) +""+ input.charAt(i+1);
                  tokenittroter();
                  i++;
               }
               else 
               {//System.out.println(input.charAt(i)); 
                  Tokens[LineNo][Nooftoken][0]="SpecialSymbols";
                  Tokens[LineNo][Nooftoken][1]= ""+input.charAt(i);
                  tokenittroter();
               }
            }
            else
               i--;
              
                     
         
         }           
            
            
            
         
         
         //else if symbol
         else if ( specialsybidf (input, i) != 0 )
         { // 
            if  (specialsybidf (input, i)==3) 
            { //
               continue;
               
            }
         
            if (specialsybidf(input, i) == 2)
            {//System.out.println( input.charAt(i)+""+ input.charAt(i+1));
               Tokens[LineNo][Nooftoken][0]="SpecialSymbols";
               Tokens[LineNo][Nooftoken][1]= ""+input.charAt(i) +""+ input.charAt(i+1);
               tokenittroter();
               i++;
            }
            else 
            {//System.out.println(input.charAt(i)); 
               Tokens[LineNo][Nooftoken][0]="SpecialSymbols";
               Tokens[LineNo][Nooftoken][1]= ""+input.charAt(i);
               tokenittroter();
            }
         }
         
         //if (i<input.length())
            //System.out.print(input.charAt(i));
         
             
         else 
         { 
            error=Error(input);  
            //System.out.println("Error:" + error); 
            error=null; 
         }  
      
      
         i++;
      
      } // outter while- 0
   
   
   
   
   
   
   
   
   }//lex
   
   
   //This function identifys the special symbols that are hard coded into the array and returns the number of characters in one slot
   public  int specialsybidf (String input, int i)
   {   //System.out.println("checking in symbol ="+ input.charAt(i));
      
      int k=0;                                                       //counter to go through the array
      if( (input.charAt(i))== '}')
         return 1;
      
      if  ( (i+1)<input.length())                                   // if the character pointer is at the last character in the line
      {  
         if (  ((SpecialSymbols[18].charAt(0)) == (input.charAt(i)))  && ((SpecialSymbols[18].charAt(1)) == (input.charAt(i+1))) )      //  check if its a start of a  nested comment 
            return 3;
      }
   
      
      while (k < SpecialSymbols.length)                              // until ever element in the array is visited
      { 
               
      
         if (SpecialSymbols[k].length()==2)                          // cheack to see if the array element as two characters in it
         {
            if ( !(isEndofLine(input)) && ((SpecialSymbols[k].charAt(0)) == (input.charAt(i)))  && ((SpecialSymbols[k].charAt(1)) == (input.charAt(i+1))) )       // cheack the next two characters in from the input line
               return 2;                                             //return true
         }
               
         else if ( (SpecialSymbols[k].charAt(0)) == (input.charAt(i)) )       // if its one character match 
         { 
            return 1;                                                //return true
         }
         k++;
         
      }
      
      return 0;                                                      // return false
   }
     
    
    //This function takes a string as input identifys if a word is a keyword or an ID 
   public  boolean  identify(String word)
   {
      boolean found=false;                                           // flase- to start the search
      
      for (String element : Keywords)                                // for ever element in the array 
      {
         if (element.equals(word)) {                                  //if the keyword matchs the the string requested to be checked
            found = true;                                            // indicate you have found a key word and exist the while loop
            break;
         }
      }
      return found;                                                     // return True /false
   }
  
  
  // This function takes an input line and consumse all the error characters and stops when it finds a space ,tab, or new line or special symbole characters
   public String Error (String input)
   {
      error = " " ;
      
      
      if (input.charAt(i)== ' ')                                                   
         return error;
         
         
      error=error + input.charAt(i);                                     //add the first character from the line that is reported to be an error
      
      
      if ((i+1)==input.length())                                         //if the next char in the line is EOL then return error   
      { 
         return error;
      }
      
                                                                        // until the next char isspace ,tab, or new line or special symbole characters 
      while (!(input.charAt(i+1)== '\t' || input.charAt(i+1)== ' ' || input.charAt(i+1)== '\n' || (specialsybidf (input,i+1)== 2) || (specialsybidf (input,i+1)==1) ))
      {  
         i++;                                                           // increment to that character
         error=error + input.charAt(i);                                 // add it to error variable
      
         if ((i+1)==input.length())                                     // if the next char in the line is EOL then return error 
            break ;
      }
      
      
      return error;                                                     //return the error string
   
   } 
   
  
   public boolean isEndofLine(String input)
   {
      if (i< input.length()-1)
         return false;
        
         
      return true;
   
   
   }
   
   public void tokenittroter()
   {
   
      if (Nooftoken==79)
      {   Nooftoken=0;
         LineNo++;
      }
          
      else 
         Nooftoken++;
   }
   
   
   
   
}//parse

