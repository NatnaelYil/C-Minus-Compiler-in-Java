class SymbolTable
{
   private String[][] hashArray;    // array holds hash table
   private int arraySize;           // hold the number of data from text
    // ------------------------------------------------------------- 
   public SymbolTable(int size)       // constructor
   {
      arraySize = size;
      hashArray = new String[arraySize][3];
   }
// -------------------------------------------------------------
   public void displayArray()       //  function that displays each elements in the array
   {
      System.out.println("\nThe elements in the array ...");
   
      for(int j=0; j<arraySize; j++)      // until all the elements have been visited
      {
         if(hashArray[j][0] != null)      // check to see if there is nothing their
            System.out.println("At Index-"+j+"\t " +hashArray[j][0]+" "+hashArray[j][1]);
      }
   }
    // -------------------------------------------------------------
   public int hashFunc(String key)            // hash function
   
   { 
      int i=0,result,count=1;      
      result=(int)key.charAt(0);
      while (key.length()>count)
      { 
         result = (result * 26 + (int)key.charAt(count++))%arraySize;
         i++;
      }
   
      return result;           
   }
// -------------------------------------------------------------
  
   public void insertquad(String type, String id, String item3)                    // insert a the element using hashing 
                                                                         // (assumes table not full)
   {
      int j=0, hashVal = hashFunc(id);                                // hash the key
                   
                                                                         // until empty cell
      while(hashArray[hashVal][0] != null && hashArray[hashVal][0]!= null)
      {               
         if ( hashArray[hashVal][0].equals(id))                          // if the element already exit,dont insert it into the array
         {System.out.println("Error '"+id+"' already exist at location -> "+ hashVal );
            return ;
         }
         System.out.println("Collisions in position ---"+hashVal+" with  "+hashArray[hashVal][0] );  // display collisions
      
         hashVal=hashVal+(j*j);                                         // go to next cell
         hashVal %= arraySize;                                       // wraparound if necessary
         j++;
         
      }
      //System.out.println("Hashing in position "+hashVal+" - "+item );      
   
      hashArray[hashVal][0] = type ;                                        // insert item
      hashArray[hashVal][1]=id;
      hashArray[hashVal][1]=item3;
   }   
    //-------------------------------------------------------------------------------------
   
   public  void search ( String key)                              // function that searchs for elements in the array
   { 
      int i=0,hashVal = hashFunc(key);
      while (i<arraySize)                                          // until all the elements are visited
      {  
         if (hashArray[hashVal][0]== null)                          // check to see if the cell is empty,if so iterate one loop                      
            i++;
         else if  ( hashArray[hashVal][0].equals(key))            // if found display and exit
         
         {System.out.println("'"+key+"' Found at location "+hashVal+" with value "+hashArray[hashVal][1]);
            return;
         }
         hashVal=hashVal+(i*i);                                     // go to next cell 
         hashVal %= arraySize;                                      // wraparound if necessary
         
      
              
         i++;
      }
      System.out.println("ERROR '"+ key+"' not found.");           //Not found 
   }

}