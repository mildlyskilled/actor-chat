package kaning

import kaning.messages._
import akka.actor.{ ActorSystem, Props, Actor }
import akka.event.Logging
import akka.io.IO
import spray.can.Http
import spray.http.StringRendering
import com.typesafe.config.ConfigFactory


object Main extends App {
  implicit val system = ActorSystem("webchat")
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  val api = system.actorOf(Props(new RestInterface()), "httpInterface")
  IO(Http) ! Http.Bind(api, interface = host, port = port)

}