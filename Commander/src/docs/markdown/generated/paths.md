
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



