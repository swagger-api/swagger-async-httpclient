package com.wordnik.swagger.client

import scala.concurrent.{Await, Future, ExecutionContext}
import scala.concurrent.duration._
import scala.collection.concurrent.TrieMap
import language.postfixOps
import java.net.URI
import java.util.concurrent.atomic.AtomicLong

/**
 * A trait for a load balancing strategy.
 * It takes a set of hosts and returns a single host
 * from the Set
 */
trait HostPicker {
  /**
   * Pick a host from the provided list of services
   * @param hosts The hosts to pick from
   * @return A Future with an Option that contains the host if there was one.
   */
  def apply(hosts: Set[String], serviceName: Option[String])(implicit executionContext: ExecutionContext): Future[Option[String]]
}

object HeadHostPicker extends HostPicker {
  def apply(hosts: Set[String], serviceName: Option[String] = None)(implicit executionContext: ExecutionContext): Future[Option[String]] = {
    Future.successful(hosts.headOption)
  }
}

object RandomHostPicker extends HostPicker {
  private[this] val rand = new util.Random()
  def apply(hosts: Set[String], serviceName: Option[String] = None)(implicit executionContext: ExecutionContext): Future[Option[String]] = {
    Future.successful(if (hosts.nonEmpty) Some(hosts.toList(rand.nextInt(hosts.size))) else None)
  }
}

object GlobalRoundRobinHostPicker extends HostPicker {
  private[this] val pickers = TrieMap.empty[String, RoundRobinHostPicker]
  private[this] val default = new RoundRobinHostPicker

  def apply(
    hosts: Set[String],
    serviceName: Option[String])(
      implicit executionContext: ExecutionContext): Future[Option[String]] = {

      val roundRobinPicker = serviceName map { name =>
        pickers.getOrElseUpdate(name, new RoundRobinHostPicker)
      } getOrElse(default)

      roundRobinPicker(hosts, None)
  }
}

final class RoundRobinHostPicker extends HostPicker {
  // Start at -1 so that the first call to incrementAndGet returns 0
  private[this] val counter = new AtomicLong(-1)

  def apply(hosts: Set[String], serviceName: Option[String] = None)(implicit executionContext: ExecutionContext): Future[Option[String]] = {
    Future.successful {
      val sortedHosts = hosts.toVector.sorted

      if (sortedHosts.length == 0) {
        None
      } else {
        // This cast is okay because vector.length is an Int.
        val index = (counter.incrementAndGet() % sortedHosts.length).toInt
        Some(sortedHosts(index))
      }
    }
  }
}

trait ServiceLocator {
  implicit protected def executionContext: ExecutionContext
  def locate(name: String): Future[Set[String]]

  def locateBlocking(name: String, atMost: FiniteDuration = 20 seconds): Set[String] = {
    Await.result(locate(name), atMost)
  }

  def pickOne(
    name: String,
    picker: HostPicker = GlobalRoundRobinHostPicker): Future[Option[String]]

  def pickOneBlocking(
    name: String,
    picker: HostPicker = GlobalRoundRobinHostPicker,
    atMost: FiniteDuration = 20 seconds): Option[String] = {

    Await.result(pickOne(name, picker), atMost)
  }

  def locateAsUris(name: String, path: String): Future[Set[String]]

  def locateAsUrisBlocking(
    name: String,
    path: String,
    atMost: FiniteDuration = 20 seconds): Set[String] = {

    Await.result(locateAsUris(name, path), atMost)
  }

  def pickOneAsUri(
    name: String,
    path: String,
    picker: HostPicker = GlobalRoundRobinHostPicker): Future[Option[String]]

  def pickOneAsUriBlocking(
    name: String,
    path: String,
    picker: HostPicker = GlobalRoundRobinHostPicker,
    atMost: FiniteDuration = 20 seconds): Option[String] = {

    Await.result(pickOneAsUri(name, path, picker), atMost)
  }
}

case class BaseUrl(url: URI)(implicit protected val executionContext: ExecutionContext = ExecutionContext.global) extends ServiceLocator {
  private[this] val withoutScheme = url.getHost
  private[this] val withScheme = url.getScheme + "://" + url.getAuthority + stripTrailingSlash(url.getPath)
  def locate(name: String): Future[Set[String]] = Future.successful(Set(withoutScheme))
  def pickOne(name: String, picker: HostPicker): Future[Option[String]] = Future.successful(Some(withoutScheme))
  def locateAsUris(name: String, path: String): Future[Set[String]] = Future.successful(Set(withScheme))
  def pickOneAsUri(name: String, path: String, picker: HostPicker): Future[Option[String]] = Future.successful(Some(withScheme))
  private def stripTrailingSlash(s: String): String = if (s endsWith "/") s.substring(0, s.length - 1) else s
}
