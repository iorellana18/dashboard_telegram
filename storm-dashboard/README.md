# Template App
This application is the template for the constructor graph system, where we can select the bolt that we need for our application.

The bolts availability are:
- `ContienePalabra`: This bolt chooses the text that have the words about a bug of words.
- `Geolocalizacion`: This bolt chooses the tweet that are in a area geographic.
- `Lenguaje`: This bolt chooses the text about a language specific.
- `Localizacion`: This bolt chooses the text about a place.
- `MongoDB`: This bolt stores the tuples in MongoDB.
- `Redis`: This bolt stores the tuples in Redis.
- `Sentimientos`: This bolt classifies the text in positive or negative.
- `SinPalabra`: This bolt chooses the text that have not the words about a bug of words.
- `Stopword`: This bolt remove the words of text about a bug of words.
	
The spouts availability are:
- `TwitterSpout`: Data source of Twitter.
- `KafkaSpout`: Data source of Apache Kafka.
	
## Run
For packaking to run the command `mvn clean package`.

If you want run the application without flux, to run the command:

	storm jar target/templateApp-1.0.3-SNAPSHOT-jar-with-dependencies.jar cl.citiaps.templateApp.topology.Topology templateApp
	
Else:

	storm jar target/templateApp-1.0.3-SNAPSHOT-jar-with-dependencies.jar org.apache.storm.flux.Flux --remote config.yaml
	