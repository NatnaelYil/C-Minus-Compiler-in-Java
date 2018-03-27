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
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.lang.Math;
import java.util.PriorityQueue;   


public class N00973866_Compiler
{



    
   public static void main(String[] args) 
   {
                 
      if (args.length <1 )                                                       
      {   
         System.out.print(" Unable to read file\n Please provide an input text file or refere to the documentation to excute the program\n");
         System.exit (0);
      }
                    
      File file = new File(args[0]);                                            
      File file2 = new File(args[0]);  
      Lexical parser = new Lexical ();
        
      parser.parse(file);
    
        
          
   }
    
    
    
    
    
}
