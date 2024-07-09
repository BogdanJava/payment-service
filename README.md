## Payments Service

## Tools used
- Kotlin, Spring boot, MongoDB

## Prerequisites 
To run the app you will need:
- Java 17
- docker
- docker compose

## How to run
Just run `./startup.sh`
or 
```bash
./gradlew bootBuildImage
docker-compose up
```

### Kubernetes
Create kubernetes resources
```bash
kubectl create -f ./deployment/k8s
```

## APIs
The application has 2 types of API:
- RESTFul
- GraphQL
You can find sample requests in the [postman collection](payment-service.postman_collection.json)

## Example requests
1. Make a payment
```
curl --location --request POST 'http://localhost:8080/payment' \
--header 'Content-Type: application/json' \
--data-raw '{
    "customer_id": "1",
    "price": "1230.00",
    "price_modifier": 0.97,
    "payment_method": "CASH",
    "datetime": "2022-09-01T00:00:00Z"
}'
```
2. Get sales report
```
curl --location --request POST 'http://localhost:8080/sales' \
--header 'Content-Type: application/json' \
--data-raw '{
    "startDateTime": "2022-09-01T00:00:00Z",
    "endDateTime": "2022-09-01T05:13:00Z"
}'
```

Have fun!
