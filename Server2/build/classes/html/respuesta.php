<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;" />
<link rel=”shortcut icon” href=”favicon.ico” type=”image/x-icon” />
<title>Request for Comments</title>
<style>
.centrado {
  text-align: center;
}
</style>
</head>
<body bgcolor="1295DF">

<center>
<h2>Llamadas RFC (Request For Comments)</h2>
<h3>GET - HEAD - POST - DELETE</h3>
<img src="rfc.jpg"/ style="width: 30%; border-radius: 50%; box-shadow: 0 0 10px #333;">
</center>
<br>
<p class="centrado">
<a href="http://127.0.0.1:5500">Volver al formulario</a>
</p>
<br>
<h3>
Datos del formulario
</h3>
<?php
echo  " Nombre: " . $_POST["nombre"];
?>
<br>
<?php
echo "Apellido: " . $_POST["apellido"];
?>
</body>
</html>