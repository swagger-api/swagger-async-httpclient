package com.wordnik.swagger.client

class ApiException(val response: ClientResponse) extends RuntimeException(response.statusText) {
  def code = response.statusCode
  def body = response.body
}