package com.wordnik.swagger.client

import scala.concurrent.{Future, Promise}
import org.json4s.jackson.JsonMethods
import util.{Failure, Success, Try}

abstract class ApiClient(client: TransportClient, config: SwaggerConfig) extends JsonMethods {
  protected implicit val execContext = client.execContext

  protected def addFmt(pth: String) = pth.replace("{format}", "json")

  protected def process[T](fn: => T): Future[T]  = {
    val fut = Promise[T]()
    try {
      val r = fn
      (r: @unchecked) match {
        case tr: Try[T] => fut.complete(tr)
        case t: Throwable => fut.complete(Failure(t))
        case s => fut.complete(Success(s))
      }
    } catch {
      case t: Throwable => fut.complete(Failure(t))
    }
    fut.future
  }
}
