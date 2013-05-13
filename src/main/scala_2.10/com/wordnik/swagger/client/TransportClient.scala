package com.wordnik.swagger.client

import com.ning.http._
import client._
import scala.concurrent.{Promise, ExecutionContext, Future}
import scala.concurrent.duration._
import java.net.URI
import org.json4s._


trait ClientResponse {
  def cookies: Map[String, RestClient.Cookie]
  def headers: Map[String, Seq[String]]
  def status: ResponseStatus
  def contentType: String
  def mediaType: Option[String]
  def charset: Option[String]
  def uri: URI
  def statusCode: Int = status.code
  def statusText: String = status.line
  def body: String

}

trait TransportClient {
  protected def locator: ServiceLocator
  protected def clientConfig: AsyncHttpClientConfig
  implicit def execContext: ExecutionContext
  def open(): Future[Unit] = Promise.successful(()).future
  def submit(method: String, uri: String, params: Iterable[(String, Any)], headers: Iterable[(String, String)], body: String, timeout: Duration = 90.seconds): Future[ClientResponse]
  def close(): Future[Unit]
}
