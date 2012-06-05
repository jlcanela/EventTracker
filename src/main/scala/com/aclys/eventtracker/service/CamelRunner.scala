package com.aclys.eventtracker.service

trait CamelRunner {

  lazy val cs = new CamelService

  def start = cs.start

  def stop = cs.shutdown


}
