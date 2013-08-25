package com.wordnik.swagger.client

class ApiException(resp: ClientResponse) extends RuntimeException(resp.statusText)