# Charging Station Management System (CSMS) Documentation

## Overview

This document provides an overview of the Charging Station Management System (CSMS), akin to systems like the ChargePoint backend system. The CSMS is designed to manage charging stations, charging processes, and customer interactions for electric vehicle drivers (eDrivers).

## Structure
There are two services named `Transaction Service` and `Authentication Service`.
### Technologies & Features
* Kotlin + Spring Boot
* Kafka (as an asynchronous queuing mechanism between services)
* Validation mechanism for incoming request's body
* Docker & Docker-Compose

## Transaction Service
This service is a REST API that has a responsibility to handle communication between clients(charging stations) and `Authentication Service`. It has the following route:
### Authorization Request

**Endpoint**: `POST /transaction/authorize`

**Request Body**:
```json
{
  "stationUuid": "<uuid of station>",
  "driverIdentifier": {"id": "<identifier-string>"}
}
```

**Response Body**:

```json
{
  "authorizationStatus": "Accepted|Invalid|Unknown|Rejected"
}
```

| Identifier Status                                | Authorization Status |
|--------------------------------------------------|:--------------------:|
| Identifier is known and the card is allowed to charge |       Accepted       |
| Identifier is not known                           |       Unknown        |
| Identifier is not valid                           |       Invalid        |
| Identifier is known but card is not allowed for charging |       Rejected       |

## Authentication Service
The service is responsible for listening to a specific topic(`AuthenticationRequests`). When a new event arrives, it checks whether the customer is allowed to charge. Finally, it produces another topic(`AuthenticationResponses`)'s event to express the customer authorization status.

## Kafka's Topics
- **Topic**: AuthenticationRequests

    **Payload**:
    ```json
    {
      "requestId": "<uuid of request>",
      "userIdentifierId": "<identifier-string>"
    }
    ```
- **Topic**: AuthenticationResponses

  **Payload**:
  ```json
  {
    "requestId": "<uuid of request>",
    "authorizationStatus": "Accepted|Invalid|Unknown|Rejected"
  }
  ```
## How to start
You should have installed [**Docker**](https://www.docker.com/) only. To run the applications, please execute the following command at the root of the repository:
```shell
docker compose up -d --build
```
After this, the `Transaction Service` endpoint is accessible from the [localhost:8080](http://localhost:8080) URL.

## Default Drivers
| User Identifier                                                               | Allowed to Charge |
|---------------------------------------------------------------------------------|:-----------------:|
| af22009d-efc8-4cc4-8802-fa7b2bb98330                                            |        Yes        |
| bb72649d-9246-423c-a56e-c8b92117dcdd                                            |        No         |
| af22009d-efc8-4cc4-8                                                            |        Yes        |
| 68276308-0731-4961-b                                                            |       No       |
| 018f0127-8ee2-77a5-9f85-f4ad4f40a068-018f0127-8ee2-77a5-9f85-f4ad4f40a068-018f01 |        Yes        |
| 018f0128-d256-7041-b35f-851d55182364-018f0128-d256-7041-b35f-851d55182364-018f01 |       No       |
