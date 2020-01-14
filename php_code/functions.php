<?php
session_start();//start the session




function SessionUserId($userId)
{
	$_SESSION["userID"] = $userId;
}

//email function
function sendMessage($emailTo,$message)
{

	$body="<html><head>Activation link</head><body>Please click <a href='https://flirtapp.azurewebsites.net/activation.php?strvld=$message'>link</a>to activate your account</body></html>";
	$headers  = 'MIME-Version: 1.0' . "\r\n";
	$headers .= 'Content-type: text/html; charset=iso-8859-1' . "\r\n";

	

	mail($emailTo, "Activation link", $body);
	
}

?>