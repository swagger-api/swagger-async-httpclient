package com.wordnik.swaggger.client

class ApiException(val code: Int, message: String, val body: String) extends RuntimeException(message)