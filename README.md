
Parking Lot service :

Steps to run the project : 

Download elastic from here : https://www.elastic.co/downloads/past-releases/elasticsearch-6-4-0
and add these configuration :
cluster.name: gojek-parking-service
transport.tcp.port: 9300

to elasticsearch-6.4.0\config\elasticsearch.yml

Download postresql from here : https://www.enterprisedb.com/downloads/postgres-postgresql-downloads

Start elasticsearch and Execute these curl commands to create elastic schema.

curl -X PUT "localhost:9200/parkingservice"

curl -X PUT "localhost:9200/parkingservice/_mapping/geoservice" -H 'Content-Type: application/json' -d'
{
	"properties" : {
		"area" : {
			"type" : "text"
		},
                    "location": {
                        "type": "geo_point"
                    }
	}
}
'

curl -X PUT "localhost:9200/customers"

curl -X PUT "localhost:9200/customers/_mapping/customers_detail" -H 'Content-Type: application/json' -d'
{
	"properties" : {
		"customer_id" : {
			"type" : "integer"
		},
		"customer_name" : {
			"type" : "text"
		},
		"vehicle_num" : {
			"type" : "text"
		},
		"owner_name" : {
			"type" : "text"
		},
		"color" : {
			"type" : "text"
		},
		"vehicle_class_name" : {
			"type" : "text"
		}
	}
}
'


1) Start the postgresql server on port 5432(default port) with one database with name "postgres" and modify application.properties accordingly for (username, password, database.url).
2) Start elasticsearch cluster on port 9200.
3) Java -jar parkinglot-1.0-SNAPSHOT.jar(Build project using "maven clean install" command).
4) Go to localhost:50165/health to check if application is up or not (Application health check endpoint).
5) If application is up go to http://localhost:65432/parking_service/swagger-ui.html .
6) First regsiter the user who wants parking service.
7) Create parking lots.
8) Get list of all parking lots in a given range of distance.
9) Choose any parking lot where you want to book form the above list and book a parking lot.

How this app works : 

We first create parking lot that any customer can book. For creating parking lot we take following parameters
1) Licence_id : Licence of any parking service.
2) Latitude and Longitude : Geolocation of the parking service.
3) Area : Area in which this parking service falls.
4) ParkingLotClasses : There can be many types of parking lots or classes in a given paking service. Provide  all types of classes present in the parking servcie.
5) Size : Provide list of size for all parking lot classes provided in step 4.
6) Price : Provide list of price for all paking lot classes provided in step 4.

For booking parking lot, a customer should be registered.
For registering a customer we take following parameters
1) Customer_id : Provide the customer id.
2) Customer_name : Provide the customer name.
3) Vehicle number : Provide the customer's vehicle number.
4) Vehicle color : Color of the vehicle.
5) Vehicle model name : Model name of the vehicle. Choose one form existing models(one from class com.gojek.parkinglot.model.VehicleSizeForClass or add new vehicle model to it.)
6) Owner name : Name of the vehicle real owner.

Get all parking services for a customer from the provided geopoint to a maximium distance (present geo location
of the vehicle provided by customer) that customer can travel to park his/her vehicle.

For getting parking services in a given range we expect following parameters
1) Customer_id : Provide the customer id.
2) Lat and Lon : Geolocation of the vehicle he wants to park.
3) Maximum distance : Maximum distance from the above geo point.

After getting list of parking services in a given range of distance based on vacancy and size of vehicle.
We can now choose one of the parking class in a parking service.
1) Customer_id : Customer_id of the customer.
2) Licence_id  : Licence id of the parking servcie.
3) Class opted : Name of the parking class choosen by customer.

After booking customer can release parking when needed.
Parameters to release parking 
1) Licence_id : licence id of the parking service.
2) Customer_id : customer id of the customer.

Feature to be added : 
1) Making parking service thread safe to maintain consistency.
2) One customer can have more than one vehicle. For now it can have only one.
