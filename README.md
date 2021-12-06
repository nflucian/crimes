## Preparation
Start by downloading the data files and saving it in __*dataset*__ dir in the project directory.

*data source:* https://data.police.uk/data/
```shell
./scripts/dataset.sh
```
__Note:__ the dataset dir will be setup as docker volume to the spark cluster, therefore it is mandatory to have the data before starting the cluster       

## Infrastructure 
The next step is to start the necessary infrastructure.
Before starting you need Docker Compose to be installed on your machine.  
*install steps:* https://docs.docker.com/compose/install/

### Cluster Service
* 1 spark master
* 3 spark workers
* 3 elasticsearch nodes 
* 1 kibana

### Commands 
* start cluster 
```shell
docker-compose up -d
```
* list the running containers
```shell
docker-compose ps
```
* stop running containers
```shell
docker-compose stop
```

### Access
* Spark UI: http://localhost:8080
* Kibana: http://localhost:5601
* ElasticSearch: http://localhost:9200

## Job Application 
Make sure all services are up. 

### Compile and Build Docker images
```shell
./scripts/build.sh
```

### Deploy 
```shell
./scripts/run.sh
```

### Run App 
* main class: __ro.neghina.crime.CrimeJob__
* program arguments: __-i /path/to/crime-proj/dataset/ -o es-index__
```
Available options:
  -i, --input <value>   directory path
  -o, --output <value>  elasticsearch index
  --es k1=v1,k2=v2,...  elasticsearch arguments
```
_Nota:_ 
- elasticsearch arguments default values: __es.nodes=localhost, es.port=9200__
- _more info:_ https://www.elastic.co/guide/en/elasticsearch/hadoop/current/configuration.html 

## Data Consumption

### API
Elasticsearch exposes REST API that can be called directly.
Ex: 
* get 100 crimes from metropolitan district
```shell
curl -X GET 'http://localhost:9200/crimes/_search?q=districtName:metropolitan&size=100'

# pretty json
curl -X GET 'http://localhost:9200/crimes/_search?pretty&size=100&q=districtName:metropolitan'
```
* for more complex query 
```shell
curl -X GET 'http://localhost:9200/crimes/_search?pretty' -H 'Content-Type: application/json' \
-d '{ 
  "query": {
    "term": {
      "districtName": "metropolitan"
    }
  } 
}'
```

### Apache Spark
Run spark-shell with ElasticSearch support on spark-master container 
```shell
./script/spark-shell.sh
```
* reading using dataframe 
```
val df = spark.read
  .format("es")
  .load("crimes").na.drop
  .filter("districtName = 'metropolitan'")
  
df.show(10, false)  
```
* using es query 
```
val myQuery = """{ 
      |  "query": {
      |    "term": {
      |      "districtName": "metropolitan"
      |    }
      |  } 
      |}""".stripMargin
val df = spark.read.format("org.elasticsearch.spark.sql")
                     .option("query", myQuery)
                     .option("pushdown", "true")
                     .load("crimes")
                     .limit(10) // instead of size
```
### Kibana
* create index pattern 
  * http://localhost:5601/app/management/kibana/indexPatterns
  * Name: __crimes*__ -> Create index pattern
* Discover: http://localhost:5601/app/discover#/

## Crime Statistics

### Different crimes
```json
{
  "fields": ["crimeType"],
  "_source": false,
  "collapse": {
    "field": "crimeType.keyword"
  }
}
```
### Location hotspots
```json
{
  "size": 0,
  "aggs": {
    "hotspots": {
      "terms": {
        "field": "districtName.keyword",
        "size": 10
      }
    }
  }
}
```
### Crimes by location
```json
{ 
  "query": {
    "term": {
      "districtName": "metropolitan"
    }
  } 
}
```
### Crimes by CrimeTypes
```json
{ 
  "query": {
    "term": {
      "crimeType": "Bicycle theft"
    }
  } 
}
```
### Geo query
We need a __geo_point__ field which is a lat/lon pairs for Geo queries.
_more info:_ https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-queries.html
