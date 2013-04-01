package com.wordnik.swagger.client

import com.wordnik.logger.Logger
import collection.mutable.HashMap
import akka.actor.Cancellable
import com.wordnik.discovery.aws.InstanceMetaDetector
import com.wordnik.discovery.api.{DiscoveryAPI, ServiceConfigurator, DiscoveryService}
import scala.collection.JavaConverters._
import akka.actor._
import akka.util.Duration
import akka.util.duration._
import java.util.concurrent.TimeUnit

class SwaggerConfigurator extends ServiceConfigurator {
  def configure(configString: String) {}
}

class IPBasedServiceLocator(serviceIp:String) extends ServiceLocator {

  override def getServerInstance(serviceName:String):String = {
    serviceIp
  }
}

class ServiceLocator {

  val LOGGER = Logger.getLogger(this.getClass)

  def getServerInstance(serviceName:String):String = {
    ServiceLocator.endpointFor(serviceName) match {
      case Some(serviceInstance) => LOGGER.debug("returning service instance " + serviceInstance); serviceInstance
      case _ => throw new RuntimeException("Unable to find service instance for service type " + serviceName)
    }
  }
}

object ServiceLocator {

  val LOGGER = Logger.getLogger("com.wordnik.swagger.client.ServiceLocator")
  val STATUS_AVAILABLE = "available"
  val serviceMap = new HashMap[String, List[String]]
  val discoveryInvokers = HashMap[String, com.wordnik.swaggerDiscovery.runtime.common.APIInvoker]().empty
  var cancellable: Option[Cancellable] = None
  val rand = new java.util.Random(System.currentTimeMillis())

  DiscoveryService.connect(new SwaggerConfigurator())
  //  val svcs = DiscoveryService.getServiceInstancesByStatus(STATUS_AVAILABLE)
  val loader = DiscoveryService.getConfigFileLoader()
  val detector = new InstanceMetaDetector()

  start

  def endpointFor(serviceName: String): Option[String] = {
    val servers = serviceMap.getOrElse(serviceName, updateEndpoint(serviceName))
    servers.size match {
      case 0 => None
      case i: Int => Some(servers(rand.nextInt(i)))
    }
  }

  def start = {
    val system = ActorSystem("ServiceLocator")
    cancellable = Some(system.scheduler.schedule(5 seconds, Duration.create(10, TimeUnit.SECONDS), new Runnable {
      def run() {
        LOGGER.debug("running " + serviceMap.size)
        serviceMap.map(m => {
          val serviceName = m._1
          LOGGER.debug("updating " + serviceName)
          val ep = ServiceLocator.updateEndpoint(serviceName)
          LOGGER.debug("endpoints: " + ep.size)
        })
      }
    }))
  }

  def cancel = {
    cancellable match {
      case Some(c) => c.cancel
      case _ =>
    }
  }

  def updateEndpoint(serviceName: String): List[String] = {
    LOGGER.debug("getting instances of " + serviceName)

    val crossZoneHost = detector.getZone() match {
      case e: String if (e == "us-west-1a") => loader.getDSLConfigEntry("us-west-1c").asScala.head
      case e: String if (e == "us-west-1c") => loader.getDSLConfigEntry("us-west-1a").asScala.head
      case e: String => LOGGER.error("unknown zone " + e); ""
    }
    LOGGER.debug("crossZoneHost: " + crossZoneHost)

    if (crossZoneHost != "" && !discoveryInvokers.contains(crossZoneHost)) {
      discoveryInvokers += crossZoneHost -> com.wordnik.swaggerDiscovery.runtime.common.APIInvoker.initialize(null, "http://" + crossZoneHost + "/", false)
    }
    val sameZoneHost = loader.getDSLConfigEntry(detector.getZone()).asScala.head
    LOGGER.debug("sameZoneHost: " + sameZoneHost)

    if (!discoveryInvokers.contains(sameZoneHost)) {
      discoveryInvokers += sameZoneHost -> com.wordnik.swaggerDiscovery.runtime.common.APIInvoker.initialize(null, "http://" + sameZoneHost + "/", false)
    }

    // remove any stale discoveryInvokers
    (discoveryInvokers.keys.toSet -- Set(crossZoneHost, sameZoneHost)).foreach(hostToRemove =>
      discoveryInvokers.remove(hostToRemove))

    DiscoveryAPI.setApiInvoker(discoveryInvokers(crossZoneHost))
    val crossZoneServices = DiscoveryService.getServiceInstances(serviceName).asScala.toList

    DiscoveryAPI.setApiInvoker(discoveryInvokers(sameZoneHost))
    val sameZoneServices = DiscoveryService.getServiceInstances(serviceName).asScala.toList

    LOGGER.debug("crossZoneServices: " + crossZoneServices)
    LOGGER.debug("sameZoneServices: " + sameZoneServices)

    val ep = (crossZoneServices ++ sameZoneServices).toList
    serviceMap += serviceName -> ep
    ep
  }
}
