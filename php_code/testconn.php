<html>
<body>
<?php

include_once("functions.php");//session function are here
include_once("databse.php");

echo "<p>" . DB_HOST;
echo "<p>" . DB_USER;
//echo "<p>" . DB_PASSWORD;
echo "<p>" . DB_DATABASE;

//put values in variables
$username="testuser";
$password="testpass";

$dataBase = new Database();//inicialze the databse instance

$dataBase->connect();//connect to database
echo  json_encode($dataBase->login($username,$password));
$dataBase->close();//close the database connection
?>
</body>
</html?