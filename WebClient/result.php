<?php
$req = $_GET["request"];
$hName = $_GET["hospitalName"];
$pName = $_GET["patientName"];
$pID = $_GET["patientID"];


$address = '192.168.0.6';
$port = 8000;

$socket = socket_create(AF_INET, SOCK_STREAM, getprotobyname('tcp'));
socket_connect($socket, $address, $port);

class Request
{
	public $request = "";
	public $hospitalName = "";
	public $patientName = "";
	public $patientID = "";
}

$request = new Request();
$request->request = $req;
$request->hospitalName = $hName;
$request->patientName = $pName;
$request->patientID = $pID;
$message = json_encode($request);
$message = $message."\n";
$len = strlen($message);

$status = socket_sendto($socket, $message, $len, 0, $address, $port);
if($status !== FALSE)
{
    $message = socket_read($socket,8000,PHP_NORMAL_READ);
}
else
{
    echo "Failed";
}
socket_close($socket);
$result = json_decode($message);
$length = count($result->list);
    echo("<table id = \"mTable\">");
    for($i = 0; $i < $length; $i++) {
        echo("<tr><td>".$result->list[$i]->name."</td><td><input type='checkbox'  id='checkbox".$i."'></td></tr>");
    }
    echo("</table>");
?>

