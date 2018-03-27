
import java.util.*;
import java.io.*;
import java.util.ArrayList;





class Lexical 
{   
   boolean iscomment = false;    // variable use too indicate is a line is a nested comment
   String[] Keywords= {" if", " else", " int", " return", " void", " while" };
   String[] SpecialSymbols = { "+", "-", "*", "/", "<", "<=", ">=", "==", "!=", "=", ";", ",", "(", ")", "[", "]", "{", "}" , "/*", "*/"};
   String line;                 
   String word,ID;               //strings used to store word and Id 
   String num,error;             //variable used to store number and error strings
   
   int i =0;                     // counter for the current character being proccesed from a line
   int depth=0;                  // used to count the deepth of nested comment 
     
     
     
    // This Function reads each line from the input file and calls the "lex ()" to run lexical analysis 
   public void  parse (File file)
   {
      
      
      try (BufferedReader br = new BufferedReader(new FileReader(file)))            // open reader and catch an error if it occurs
           
      {            
         
                
         while ((line = br.readLine()) != null)                                     // read each line until it reaches the end of file
         {  
            if (line.length() != 0)                                                 // if there is data on the current line, print it to the screen as input for analysis
               System.out.println("INPUT:" + line);                
            lex(line);                                                              // pass the line to function lex for lexical analysis
         }
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
      System.out.println();
        
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
            { System.out.println(input.charAt(i));
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
               System.out.println("/");                                       // if it is not a start of a nest loop ...print the '/' symbol
            
         }//if
         
         
         else  if ((input.charAt(i) >= 'a' && input.charAt(i) <= 'z') || (input.charAt(i) >= 'A' && input.charAt(i) <= 'Z')) // if the starting letter is a character
         {
         
         
            word=" " +input.charAt(i);                                        // add it to the word variable
                                                                              
                                                                              //if the next characters are number or letters ...add them to the word variable                                                                  
            while    ((input.charAt(i+1) >= 'a' && input.charAt(i+1) <= 'z') || (input.charAt(i+1) >= 'A' && input.charAt(i+1) <= 'Z') )     
            {
               i++;
               word= word + input.charAt(i);
              
               if (i==(input.length()-1))                                     //if reached while counting ...exist the loop
               {
                  if(identify(word)) 
                     System.out.println("Keyword= " + word);                  // identify the word if its a keyword
                           
                  else 
                     System.out.println("ID= " + word);                       // or its an ID
                  word=" ";
               
                  break OUTTER ;                                              // exist to the outer loop
               }
               
            }
              // System.out.println("word= " + word );
            
            if(identify(word)) 
               System.out.println("Keyword= " + word);                         // identify the word if its a keyword
              
                           
            else 
               System.out.println("ID= " + word);                                // or its an ID
         
            word=" ";
               
         }
         
         
         
         // algorithm to deal with numbers,digits and floating number
            
            
         else if ( (input.charAt(i) >= '0' )  && (input.charAt(i) <= '9'))       //if the character starts with a number
         { 
            num=" " +input.charAt(i);   
                                                                               //add it to num variable
            if (input.charAt(i+1)== ' ' )                                      //  if there is a space contiue to the next itteration
            { i++;
               System.out.println("Num: " + num);
               continue;
            }   
            while ((input.charAt(i+1) >= '0' ) && (input.charAt(i+1) <= '9'))    //if the next characters are numbers append them to num variable
            {   
               i++;
               num=num +input.charAt(i); 
               
               if (input.charAt(i+1)== ' ' )                                      //  if there is a space contiue to the next itteration
               { 
                  System.out.println("Num: " + num);
                  
               } 
               
               if (i==(input.length()-1))                                        // if reached the end of line while doing so ...print the number and exist to outter while loop
               {
                  System.out.println("Num: " + num);
                  break OUTTER ;
               }
            }
            i++;        
            if(input.charAt(i+1)== '.')                                          // if the number is a floating num                              
            { 
               num=num + input.charAt(i);                                        // add the decimal to the num
               i++;
               
               if ((input.charAt(i) >= '0') && (input.charAt(i) <= '9') )        //check to see if the nxt char is a number
               {  
                  num=num +input.charAt(i);
                  i++;
                  
                  while((input.charAt(i) >= '0') && (input.charAt(i) <= '9') )     // append every number after that
                  {
                     num=num +input.charAt(i);
                     i++;
                     if (i==(input.length()-1))                                    // if end of line reached ....print and exist to outer loop
                     {
                        System.out.println("Num: " + num);  
                        break OUTTER ; 
                     }
                  }  
                  
                  if ( input.charAt(i) == 'E')     
                  {  num=num +input.charAt(i);                                     // if the next character is E
                     i++;                                                           //add it to the num variable
                     
                     if ((input.charAt(i) >= '0') && (input.charAt(i) <= '9') || input.charAt(i) == '-' || input.charAt(i) == '+')  //if the next char is a +/-signn or just a number
                     {
                        num=num +input.charAt(i);
                        
                     
                        while((input.charAt(i) >= '0' ) && (input.charAt(i) <= '9' ))    // append every number after it                                  
                        {
                           i++;
                           num=num +input.charAt(i);
                          
                           if (i==(input.length()-1))
                           {
                              System.out.println("Num: " + num);                            // if end of line reached ....print and exist to outer loop
                              break OUTTER ;
                           }
                        
                        }
                        if (input.charAt(i)== ' ' )                                      //  if there is a space contiue to the next itteration
                           continue;
                           
                        else if ( specialsybidf (input, i) != 0 )                        // if it is among the special characters i dentify it and print
                        {     
                           if  (specialsybidf (input, i)==3) 
                           { System.out.println("Num="+ num);
                              continue;
                           
                           }
                        
                           if (specialsybidf(input, i) == 2)
                              System.out.println( input.charAt(i) + input.charAt(i+1));
                           
                           else 
                              System.out.println(input.charAt(i)); 
                        }
                        
                        else 
                        {
                           error= Error(input);                                           // error condition
                        }
                     
                     } 
                     else 
                     {
                        error=Error(input);                                               // error condition
                     }
                  }
                  else if ( specialsybidf (input, i) != 0 )                               //// if it is among the special characters i dentify it and print
                  {  
                     if  (specialsybidf (input, i)==3) 
                     { System.out.println("Num="+ num);
                        continue;
                     
                     }
                     if (specialsybidf(input, i) == 2)                                    //// if it is among the special characters i dentify it and print
                        System.out.println( input.charAt(i) + input.charAt(i+1));
                     
                     else 
                        System.out.println(input.charAt(i)); 
                  }
                  else 
                  {
                     error= Error(input);                                                 // error condition
                  }
               }
               
               else 
               { 
                  error=Error(input);                                                        // error condition
               }
               
             
            }
            
            else if   (input.charAt(i) == 'E')                                                // if the next character is E
            
            {  
               num=num+input.charAt(i);
               i++; 
               
               if ( (input.charAt(i) >= '0') && (input.charAt(i) <= '9') || input.charAt(i) == '-' || input.charAt(i) == '+')
               {  
                  i++;
                  num=num+input.charAt(i);
                 
                  while((input.charAt(i+1) >= '0') && (input.charAt(i+1) <= '9'))  
                  {   
                     i++;
                     num=num+input.charAt(i);
                    
                     if (i==(input.length()-1))
                     {
                        System.out.println("Num: " + num);
                        break OUTTER ;
                     }
                  }
                  
                  if (input.charAt(i)== ' ' )
                     continue;
                           
                  else if ( specialsybidf (input, i) != 0 )
                  {  
                     if  (specialsybidf (input, i)==3) 
                     {
                        System.out.println("Num="+ num);
                        continue;
                     }
                     if (specialsybidf(input, i) == 2)
                        System.out.println( input.charAt(i) + input.charAt(i+1));
                           
                     else 
                        System.out.println(input.charAt(i)); 
                  }
                        
                  else 
                  {
                     error= Error(input);
                  }
               
                  
               } 
               else 
               {
                  error=Error(input);   
               
               }
            }
            
            else if (input.charAt(i) == ' ')
               continue; 
                 
            else if ( specialsybidf (input, i) != 0 )
            {  
               if  (specialsybidf (input, i)==3) 
               { System.out.println("Num="+ num);
                  continue;
               
               }
            
               if (specialsybidf(input, i) == 2)
                  System.out.println( input.charAt(i) + input.charAt(i+1));
                           
               else 
                  System.out.println(input.charAt(i)); 
            }
                  
            else 
            { 
               error=Error(input);   
            }  
            
                               
            System.out.println("Num="+ num);                                                       //print the numbers and errors if present
               
            if (error != null )
            { System.out.println("Error="+ error);
               error=" "; 
            }  
                     
         }               
            
            
            
         
         
         //else if symbol
         else if ( specialsybidf (input, i) != 0 )
         {  
            if  (specialsybidf (input, i)==3) 
            { System.out.println("Num="+ num);
               continue;
               
            }
         
            if (specialsybidf(input, i) == 2)
            {System.out.println( input.charAt(i)+""+ input.charAt(i+1));
               i++;
            }
            else 
               System.out.println(input.charAt(i)); 
         }
         
         //if (i<input.length())
            //System.out.print(input.charAt(i));
         
             
         else 
         { 
            error=Error(input);  
            System.out.println("Error:" + error); 
         }  
      
      
         i++;
      
      } // outter while- 0
   
   
   
   
   
   
   
   
   }//lex
   
   
   //This function identifys the special symbols that are hard coded into the array and returns the number of characters in one slot
   public  int specialsybidf (String input, int i)
   {  
      
      int k=0;                                                       //counter to go through the array
      
      
      if  ( (i+1)<input.length())                                   // if the character pointer is at the last character in the line
      {  
         if (  ((SpecialSymbols[18].charAt(0)) == (input.charAt(i)))  && ((SpecialSymbols[18].charAt(1)) == (input.charAt(i+1))) )      //  check if its a start of a  nested comment 
            return 3;
      }
   
      
      while (k < SpecialSymbols.length)                              // until ever element in the array is visited
      { 
               
      
         if (SpecialSymbols[k].length()==2)                          // cheack to see if the array element as two characters in it
         {
            if ( (i< input.length()-1) && ((SpecialSymbols[k].charAt(0)) == (input.charAt(i)))  && ((SpecialSymbols[k].charAt(1)) == (input.charAt(i+1))) )       // cheack the next two characters in from the input line
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
   
  
  
   
   
   
   
   
   
}//parse

