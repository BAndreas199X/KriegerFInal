# KriegerFinal
Andreas Bauer: Final submission for Krieger's Challenge

To get an overview over the APIs, visit "http://localhost:8080/v3/api-docs" or "http://localhost:8080/swagger-ui/index.html".
To download the yaml file, visit "http://localhost:8080/v3/api-docs.yaml"
	
Before running the application (if you want to use its Kafka capacities at least) you need to open the command line and 
execute the "docker-compose.yml" file using "docker compose up -d". The file is in the top folder.
	
The username neccesary for accessing this web-app is 'PowerfulPowerUser', the password is "zO3:7TiC)9'/"
Any update you make to existing Authors to Documents will be send to Kafka-queue 'AndreasB4KriegerUpdates'

If you want to delete an Author and all his authored Documents, send a String to Kafka-queue 'AndreasB4KriegerDeletion'
It must be a JSON-String with the field "ID" and the ID of the Author whose entries you want to delete!
