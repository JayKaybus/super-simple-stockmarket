# super-simple-stockmarket
Features of the Project

* Developed using Spring Boot and Java version 8.
* Created REST API’s for the functionality mentioned in the assignment document.
  Once app is built and started, 
  Open http://localhost:8080/stockmarket/test to see the message 
  “Welcome to Simple Super Stock Mark App” for successful app launch. 

Technical Design and Solution:

Service oriented architecture(SOA) approach along with REST based service layer wrapper design has been followed for this app to make the functional building blocks accessible over http protocol and to be independent of platforms and programming languages accessing it.

RestController:(rest)
All the functionality of the application mentioned in the document are exposed using RESTful API's 
Spring RestController annotation is used to create RESTful web services using Spring framework. Spring RestController takes care of mapping request data to the defined request handler method.

Service: (service)
The SimpleStockService class is a service that satisfy the concrete definition of implementing the Super Simple Stock Market application. It provides the list of methods that calculate the trades on set of given stocks and records all the trades with timestamp. 

DAO:(dao)
Created for managing the data access layer logic persistance in a separate layer for future use.
But, currently all data are stored only in InMemory in this layer.

Unit Tests:(src/test)
Unit tests cases for all service layer methods is created under the folder src/test using SpringRunner 

Sample Tests API's: 
Valid Input cases:

http://localhost:8080/stockmarket/calcDividendYield/TEA/10
http://localhost:8080/stockmarket/calcPERatio/POP/10

Method type - Post:

http://localhost:8080/stockmarket/recordATrade

Sample RequestBody : 
{
	"stockSymbol":"TEA",
	"quantity": 20,
	"indicator": "SELL", or (BUY)
	"price": 100
}

http://localhost:8080/stockmarket/calcVolumeWtStockPrice/TEA
http://localhost:8080/stockmarket/GBCEAllShareIndex


Invalid input cases:

http://localhost:8080/stockmarket/calcDividendYield/TEAP/10
http://localhost:8080/stockmarket/calcDividendYield/POP/0
http://localhost:8080/stockmarket/calcDividendYield/POP/-10


