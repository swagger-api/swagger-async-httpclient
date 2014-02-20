package com.wordnik.swagger.client

import com.ning.http._
import client._
import scala.concurrent.{Promise, ExecutionContext, Future}
import scala.concurrent.duration._
import java.net.URI
import org.json4s._
import scala.util.Try
import scala.annotation.implicitNotFound


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

@implicitNotFound(
  "No ClientResponseReader found for type ${T}. Try to implement an implicit ClientResponseReader for this type, or perhaps you're just missing an import like ClientResponseReader._."
)
trait ClientResponseReader[T] {
  def read(resp: ClientResponse): T
}

object ClientResponseReaders {

  private def rdr[T](fn: ClientResponse => T): ClientResponseReader[T] = new ClientResponseReader[T] {
    def read(resp: ClientResponse): T = fn(resp)
  }

  implicit val StringReader: ClientResponseReader[String] = rdr(_.body)
  implicit val JValueReader: ClientResponseReader[JValue] = rdr(r => jackson.parseJson(r.body))
  implicit val UnitReader: ClientResponseReader[Unit] = rdr(_ => ())
  implicit def OptionReader[T](implicit reader: ClientResponseReader[T]): ClientResponseReader[Option[T]] = rdr { resp =>
    try {
      if (resp.statusCode / 100 == 2) Option(reader.read(resp)) else None
    } catch {
      case _: Throwable => None
    }
  }
  implicit def TryReader[T](implicit reader: ClientResponseReader[T]): ClientResponseReader[Try[T]] =
    rdr { resp =>
      try {
        if (resp.statusCode / 100 == 2) Try(reader.read(resp))
        else scala.util.Failure(new ApiException(resp))
      } catch {
        case t: Throwable => scala.util.Failure(t)
      }
    }


  object JsonTypeClassReader {
    implicit def JsonFormatsReader[T](implicit jsonReader: org.json4s.Reader[T]): ClientResponseReader[T] =
      rdr(resp => jsonReader.read(JValueReader.read(resp)))
  }

  object Json4sFormatsReader {
    implicit def JsonFormatsReader[T](implicit formats: org.json4s.Formats, mf: Manifest[T]): ClientResponseReader[T] =
      rdr(resp => jackson.parseJson(resp.body).extract[T])
  }

}

@implicitNotFound(
  "No RequestWriter found for type ${T}. Try to implement an implicit RequestWriter for this type, or perhaps you're just missing an import like RequestWriters._."
)
trait RequestWriter[T] {
  def write(body: T): String
}
object RequestWriters {
  private def wrtr[T](fn: T => String) = new RequestWriter[T] {
    def write(body: T): String = fn(body)
  }

  implicit val StringWriter: RequestWriter[String] = wrtr(identity)
  implicit val JValueWriter: RequestWriter[JValue] = wrtr(jackson.compactJson)
  implicit def OptionWriter[T](implicit writer: RequestWriter[T]): RequestWriter[Option[T]] =
    wrtr(o => o map writer.write getOrElse "")

  object JsonTypeClassWriter {
    implicit def JsonFormatsWriter[T](implicit jsonWriter: org.json4s.Writer[T]): RequestWriter[T] =
      wrtr(bd => JValueWriter write jsonWriter.write(bd))
  }

  object Json4sFormatsWriter {
    implicit def JsonFormatsWriter[T](implicit formats: Formats, mf: Manifest[T]): RequestWriter[T] =
      wrtr(bd => JValueWriter write Extraction.decompose(bd))
  }
}

trait TransportClient {
  protected def locator: ServiceLocator
  protected def clientConfig: AsyncHttpClientConfig
  protected def createClient(): AsyncHttpClient
  implicit def execContext: ExecutionContext
  def open(): Future[Unit] = Promise.successful(()).future
  def submit(method: String, uri: String, params: Iterable[(String, Any)], headers: Iterable[(String, String)], body: String, timeout: Duration = 90.seconds): Future[ClientResponse]
  def close(): Unit
}
