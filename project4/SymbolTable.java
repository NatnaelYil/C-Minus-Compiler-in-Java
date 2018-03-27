import java.util.ArrayList;

class cell  

{     
       
  
   public String id = "";              // variable to store the x- coordinat
   public cell next;
   public boolean array= false;
   public boolean function=false;
   // --------------------------------------------------------------------------------------------
   public cell(String Id) // constructor
   {
      id = Id;                 // initialize the coordinates
                     
   }                           
   
}  // end class Link
/////////////////////////////////////////////////////////////////////////////////////////////////////
   

class SymbolTable
{


   public ArrayList<cell> table;    // array holds hash table
   private int arraySize;           // hold the number of data from text
   public SymbolTable next;
   public SymbolTable parent;
   
    // --------------------------------------------------------------------------------------------- 
   public SymbolTable(int size)       // constructor
   {
      arraySize = size;
      table = new ArrayList<cell>();
   }
// --------------------------------------------------------------------------------------------------
   public void displayArray()       //  function that displays each elements in the array
   {
      for (cell s : table)
      {   System.out.println();
         if (s.id != null)
         {   
            while (s.next != null)
            {
               System.out.print( s.id +s.next.id  +s.array +s.function);        
               s=s.next;   
            }
         }
      }
   }
    // -----------------------------------------------------------------------------------------------
   
   
   public boolean insertcell( cell idf)
   
   {
      for (cell s : table)
      {
         if (s.id.equals(idf.id))
            return false;
      }
      //System.out.println("Inserted"+idf.id + idf.next.id);
      table.add(idf);
      return true;
   }
     
}// class