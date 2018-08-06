# spring-boot-starter-swagger


<a name="overview"></a>
## Overview
Starter for swagger 2.x


### Version information
*Version* : 1.1.0.RELEASE


### Contact information
*Contact* : ï¿½ï¿½ï¿½ï¿½Æ¼ï¿½  
*Contact Email* : XXXX@qq.com


### License information
*License* : Apache License, Version 2.0  
*License URL* : https://www.apache.org/licenses/LICENSE-2.0.html  
*Terms of service* : https://github.com/dyc87112/spring-boot-starter-swagger


### URI scheme
*Host* : localhost:10702  
*BasePath* : /


### Tags

* alert-api-controller : Alert Api Controller




<a name="paths"></a>
## Paths

<a name="getsendusingpost"></a>
### alert告警
```
POST /api/alert/send
```


#### Description
alert告警notes信息


#### Parameters

|Type|Name|Description|Schema|
|---|---|---|---|
|**Header**|**access_token**  <br>*required*|user access token|string|
|**Header**|**timestamp**  <br>*optional*|access timestamp|integer (int32)|
|**Body**|**sysAlert**  <br>*required*|告警实体sysAlert|[SysAlert](#sysalert)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[ResultBean](#resultbean)|
|**201**|Created|No Content|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json;charset=UTF-8`


#### Produces

* `*/*`


#### Tags

* alert-api-controller


#### Security

|Type|Name|Scopes|
|---|---|---|
|**apiKey**|**[Authorization](#authorization)**|global|




<a name="definitions"></a>
## Definitions

<a name="resultbean"></a>
### ResultBean

|Name|Schema|
|---|---|
|**code**  <br>*optional*|integer (int32)|
|**data**  <br>*optional*|object|
|**message**  <br>*optional*|string|


<a name="sysalert"></a>
### SysAlert

|Name|Schema|
|---|---|
|**companyId**  <br>*optional*|string|
|**contentId**  <br>*optional*|integer (int64)|
|**createdAt**  <br>*optional*|string (date-time)|
|**description**  <br>*optional*|string|
|**endTime**  <br>*optional*|integer (int64)|
|**errorCode**  <br>*optional*|string|
|**flag**  <br>*optional*|integer (int32)|
|**id**  <br>*optional*|integer (int64)|
|**ip**  <br>*optional*|string|
|**level**  <br>*optional*|string|
|**relId**  <br>*optional*|string|
|**serverType**  <br>*optional*|enum (RECORDER, CONVENE, LIVE, SUPERVISOR, DELAYER, ENCODER, IPSWITCH, PGC)|
|**startTime**  <br>*optional*|integer (int64)|
|**taskId**  <br>*optional*|string|
|**type**  <br>*optional*|string|
|**userId**  <br>*optional*|string|
|**username**  <br>*optional*|string|




<a name="securityscheme"></a>
## Security

<a name="authorization"></a>
### Authorization
*Type* : apiKey  
*Name* : TOKEN  
*In* : HEADER



