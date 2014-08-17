<?php

error_reporting(E_ERROR);
ini_set('display_errors', 1);

// MySQL host
$mysql_host = 'localhost';
// MySQL username
$mysql_username = 'root';
// MySQL password
$mysql_password = '';

//
// split_sql_file will split an uploaded sql file into single sql statements.
// Note: expects trim() to have already been run on $sql.
//
function split_sql_file($sql, $delimiter)
{
   // Split up our string into "possible" SQL statements.
   $tokens = explode($delimiter, $sql);

   // try to save mem.
   $sql = "";
   $output = array();

   // we don't actually care about the matches preg gives us.
   $matches = array();

   // this is faster than calling count($oktens) every time thru the loop.
   $token_count = count($tokens);
   for ($i = 0; $i < $token_count; $i++)
   {
      // Don't wanna add an empty string as the last thing in the array.
      if (($i != ($token_count - 1)) || (strlen($tokens[$i] > 0)))
      {
         // This is the total number of single quotes in the token.
         $total_quotes = preg_match_all("/'/", $tokens[$i], $matches);
         // Counts single quotes that are preceded by an odd number of backslashes,
         // which means they're escaped quotes.
         $escaped_quotes = preg_match_all("/(?<!\\\\)(\\\\\\\\)*\\\\'/", $tokens[$i], $matches);

         $unescaped_quotes = $total_quotes - $escaped_quotes;

         // If the number of unescaped quotes is even, then the delimiter did NOT occur inside a string literal.
         if (($unescaped_quotes % 2) == 0)
         {
            // It's a complete sql statement.
            $output[] = $tokens[$i];
            // save memory.
            $tokens[$i] = "";
         }
         else
         {
            // incomplete sql statement. keep adding tokens until we have a complete one.
            // $temp will hold what we have so far.
            $temp = $tokens[$i] . $delimiter;
            // save memory..
            $tokens[$i] = "";

            // Do we have a complete statement yet?
            $complete_stmt = false;

            for ($j = $i + 1; (!$complete_stmt && ($j < $token_count)); $j++)
            {
               // This is the total number of single quotes in the token.
               $total_quotes = preg_match_all("/'/", $tokens[$j], $matches);
               // Counts single quotes that are preceded by an odd number of backslashes,
               // which means they're escaped quotes.
               $escaped_quotes = preg_match_all("/(?<!\\\\)(\\\\\\\\)*\\\\'/", $tokens[$j], $matches);

               $unescaped_quotes = $total_quotes - $escaped_quotes;

               if (($unescaped_quotes % 2) == 1)
               {
                  // odd number of unescaped quotes. In combination with the previous incomplete
                  // statement(s), we now have a complete statement. (2 odds always make an even)
                  $output[] = $temp . $tokens[$j];

                  // save memory.
                  $tokens[$j] = "";
                  $temp = "";

                  // exit the loop.
                  $complete_stmt = true;
                  // make sure the outer loop continues at the right point.
                  $i = $j;
               }
               else
               {
                  // even number of unescaped quotes. We still don't have a complete statement.
                  // (1 odd and 1 even always make an odd)
                  $temp .= $tokens[$j] . $delimiter;
                  // save memory.
                  $tokens[$j] = "";
               }

            } // for..
         } // else
      }
   }

   return $output;
}

// import sql file
function import_sql($database,$sql_file){

   global $link;

   $raw_sql = @fread(@fopen($sql_file, 'r'), @filesize($sql_file)) or die('problem ');

   mysql_select_db($database, $link);

   $raw_sql_array = preg_split('/DELIMITER\s(\S+)\s/', $raw_sql, -1, PREG_SPLIT_DELIM_CAPTURE);

   $delimiter = ";";
   if(count($raw_sql_array) > 0)
   {
      $sql_query = split_sql_file($raw_sql_array[0], $delimiter);
   }
   else{
      $sql_query = split_sql_file($raw_sql, $delimiter);
   }


   foreach($sql_query as $sql){  
      mysql_query($sql) or die(mysql_error());
   }

   for ($j = 1; $j < count($raw_sql_array); $j=$j+2) {

      $delimiter = $raw_sql_array[$j]; 
      $sql_query = split_sql_file($raw_sql_array[$j+1], $delimiter);

      foreach($sql_query as $sql){  
         mysql_query($sql) or die(mysql_error());  
      }
   }

}

// create database if not exists
function create_database($database_name){
   mysql_query('CREATE DATABASE IF NOT EXISTS '.$database_name) or die(mysql_error());
}

// connect to MySQL
$link = mysql_connect($mysql_host, $mysql_username, $mysql_password);
if (!$link) {
    die('Could not connect: ' . mysql_error());
}

function backup_tables($database,$tables = '*')
{
    
  mysql_select_db($database,$link);
  
  //get all of the tables
  if($tables == '*')
  {
    $tables = array();
    $result = mysql_query('SHOW TABLES');
    while($row = mysql_fetch_row($result))
    {
      $tables[] = $row[0];
    }
  }
  else
  {
    $tables = is_array($tables) ? $tables : explode(',',$tables);
  }
  
  //cycle through
  foreach($tables as $table)
  {
    $result = mysql_query('SELECT * FROM '.$table);
    $num_fields = mysql_num_fields($result);
    
    $return.= 'DROP TABLE '.$table.';';
    $row2 = mysql_fetch_row(mysql_query('SHOW CREATE TABLE '.$table));
    $return.= "\n\n".$row2[1].";\n\n";
    
    for ($i = 0; $i < $num_fields; $i++) 
    {
      while($row = mysql_fetch_row($result))
      {
        $return.= 'INSERT INTO '.$table.' VALUES(';
        for($j=0; $j<$num_fields; $j++) 
        {
          $row[$j] = addslashes($row[$j]);
          $row[$j] = ereg_replace("\n","\\n",$row[$j]);
          if (isset($row[$j])) { $return.= '"'.$row[$j].'"' ; } else { $return.= '""'; }
          if ($j<($num_fields-1)) { $return.= ','; }
        }
        $return.= ");\n";
      }
    }
    $return.="\n\n\n";
  }
  
  //save file
  $file_name = 'vesuvius-backup-'.time().'.sql';
  $handle = fopen("../../db_exports/"+$file_name,'w+') or die('Erorr : cannot create file');
  fwrite($handle,$return);
  fclose($handle);
  echo "Database was successfully exported to sdcard/vesuvius_server/db_exports as ".$file_name;
}


$action = $_GET['action'];

if ($action == "import"){
   create_database("vesuvius");

   $sql_files = glob("../vesuvius/vesuvius/backups/*.sql");
   $vesuvius_sql_file = $sql_files[0];
   import_sql("vesuvius",$vesuvius_sql_file);
   import_sql("mysql","timezones.sql");

   mysql_close($link);
   echo "database imported successfully";
}

if ($action == "export"){
    backup_tables($mysql_host, $mysql_username, $mysql_password,'vesuvius');
}

?>
