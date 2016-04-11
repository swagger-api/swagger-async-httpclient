package io.swagger.client

import scala.concurrent.duration._
import java.io.StringWriter
import org.json4s.jackson.JsonMethods
import org.json4s._
import org.json4s.Xml._
import java.net.URI

object SwaggerConfig {


  private case class DefaultSwaggerConfig(
       locator: ServiceLocator,
       override val userAgent: String = RestClient.DefaultUserAgent,
       override val idleTimeout: Duration = 5.minutes,
       override val connectTimeout: Duration = 5.seconds,
       override val maxMessageSize: Int = 8912,
       override val enableCompression: Boolean = true,
       override val followRedirects: Boolean = true,
       override val identity: String = "0",
       override val name: String = "no-name",
       override val contentType: ContentType = ContentType("json", "application/json;charset=utf-8")) extends SwaggerConfig

  def forUrl(
      baseUrl: URI,
      userAgent: String = RestClient.DefaultUserAgent,
      idleTimeout: Duration = 5.minutes,
      connectTimeout: Duration = 5.seconds,
      maxMessageSize: Int = 8912,
      enableCompression: Boolean = true,
      followRedirects: Boolean = true,
      identity: String = "0"): SwaggerConfig =
    new DefaultSwaggerConfig(BaseUrl(baseUrl), userAgent, idleTimeout, connectTimeout, maxMessageSize, enableCompression, followRedirects, identity)

  def forLocator(
      locator: ServiceLocator,
      name: String,
      userAgent: String = RestClient.DefaultUserAgent,
      idleTimeout: Duration = 5.minutes,
      connectTimeout: Duration = 5.seconds,
      maxMessageSize: Int = 8912,
      enableCompression: Boolean = true,
      followRedirects: Boolean = true,
      identity: String = "0"): SwaggerConfig =
    new DefaultSwaggerConfig(locator, userAgent, idleTimeout, connectTimeout, maxMessageSize, enableCompression, followRedirects, identity, name)
  }

  case class ContentType(name: String, headerValue: String)
  trait SwaggerConfig {
    def locator: ServiceLocator
    def userAgent: String
    def idleTimeout: Duration
    def connectTimeout: Duration
    def maxMessageSize: Int
    def enableCompression: Boolean
    def followRedirects: Boolean
    def identity: String
    def name: String
    def contentType: ContentType
  }


