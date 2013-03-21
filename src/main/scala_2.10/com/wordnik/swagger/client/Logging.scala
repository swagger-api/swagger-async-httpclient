package com.wordnik.swagger.client

import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory

trait Logging {
  @transient protected val logger = Logger(LoggerFactory.getLogger(getClass))
}