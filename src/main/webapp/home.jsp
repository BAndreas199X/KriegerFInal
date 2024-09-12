<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Andreas Bauer for Krieger - Web application default page</title>
</head>

<body>

    <h1>Welcome to the default page of Andreas Bauer's webapp for Krieger!</h1>
	<p>To get an overview over the APIs, visit  <a href="http://localhost:8080/v3/api-docs" target="_blank">api-docs</a> or <a href="http://localhost:8080/swagger-ui/index.html" target="_blank">swagger</a>.</p>
	<p>To download the yaml file, visit <a href="http://localhost:8080/v3/api-docs.yaml">api-docs yaml</a>!</p>
	
	<p>Before running the application (if you want to use its Kafka capacities at least) you need to open the command line and 
		execute the "docker-compose.yml" file using "docker compose up -d".</p> 
	<p>The file is in the top folder.</p>
	
	<p>The username neccesary for accessing this web-app is 'PowerfulPowerUser', the password is "zO3:7TiC)9'/"</p>
	<p>Any update you make to existing Authors to Documents will be send to Kafka-queue 'AndreasB4KriegerUpdates'</p>
	<p></p>
	<p>If you want to delete an Author and all his authored Documents, send a String to Kafka-queue 'AndreasB4KriegerDeletion'</p>
	<p>It must be a JSON-String with the field "ID" and the ID of the Author whose entries you want to delete!</p>

</body>

</html>