function showPatients(hospitalName)
{
  var xmlhttp;
  if (hospitalName==""){
      document.getElementById("txtHint").innerHTML="";
      return;
  }
  if (window.XMLHttpRequest){
      xmlhttp=new XMLHttpRequest();
  }else{
      xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  }

  xmlhttp.onreadystatechange = function(){
    if (xmlhttp.readyState == 4 && xmlhttp.status == 200){
        document.getElementById("txtHint").innerHTML=xmlhttp.responseText;
      }
  }

  xmlhttp.open("GET","result.php?patientID=\"null\"&patientName=\"null\"&request=patientsList&hospitalName="+hospitalName,true);
  xmlhttp.send();
}

function deletePatient()
{
  var xmlhttp;
  if (window.XMLHttpRequest){
      xmlhttp=new XMLHttpRequest();
  }else{
      xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  }
  xmlhttp.onreadystatechange = function(){
    if (xmlhttp.readyState == 4 && xmlhttp.status == 200){
        document.getElementById("txtHint").innerHTML=xmlhttp.responseText;
      }
  }
  var hospitalName = document.getElementById("hospitalName").value;
  var rowCount = document.getElementById("mTable").rows.length;
  var patientName = "";
  for(i = 0;i < rowCount;i++){
  	var x = document.getElementById("checkbox"+i);
  	console.log(x.checked);
  	if(x.checked){
  		var row = document.getElementById("mTable").rows[i].cells;
  		patientName = row[0].innerHTML;
  	}
  }
   	xmlhttp.open("GET","result.php?patientID=\"null\"&patientName=" + 
  		patientName + "&request=deletePatient&hospitalName=" + hospitalName,true);
    xmlhttp.send();
}

function addPatient()
{
	var patientName = document.getElementById("patientName").value;
	var patientID = document.getElementById("patientID").value;
	var hospitalName = document.getElementById("hospitalName").value;
  	var xmlhttp;
  	if (window.XMLHttpRequest){
     	 xmlhttp=new XMLHttpRequest();
	  }else{
  	    xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
 	 }
 	 xmlhttp.onreadystatechange = function(){
  	  if (xmlhttp.readyState == 4 && xmlhttp.status == 200){
  	      document.getElementById("txtHint").innerHTML=xmlhttp.responseText;
  	    }
 	 }
  	xmlhttp.open("GET","result.php?patientID=" + patientID +
  		"&patientName=" + patientName + "&request=addPatient&hospitalName=" + hospitalName,true);
 	xmlhttp.send();
}


