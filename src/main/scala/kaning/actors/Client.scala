package kaning.actors

import akka.actor._
import akka.remote.RemoteScope
import com.typesafe.config.ConfigFactory
import kaning.messages.Unregister
import kaning.messages.RegisteredClientList
import kaning.messages.ChatMessage
import kaning.messages.RegisterClientMessage
import kaning.messages.ChatInfo
import kaning.messages.PrivateMessage
import kaning.messages.RegisteredClients
import scala.tools.jline.console.ConsoleReader
import java.net.{NetworkInterface, InetAddress}
import scala.collection.JavaConversions._

object ChatClientApplication {

  def main(args:Array[String]) {
    println("Start Akka Chat Client Actor")

     // construct client with current machine's IP address instead of using the config value
    val interfaces = new JEnumerationWrapper(NetworkInterface.getNetworkInterfaces).toList.filter(!_.isLoopback).filter(_.isUp)
    // Ideally this should give a list of
    val ipAddress = interfaces.head.getInterfaceAddresses.filter(_.getBroadcast != null).head.getAddress.getHostAddress
    val clientAddress = Address("akka.tcp", "AkkaChat", ipAddress, 2552)


    val identity = new ConsoleReader().readLine("identify yourself: ")
    val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("chatclient"))
    val serverAddress = system.settings.config.getString("actor-chat.server.address")
    val serverPort = system.settings.config.getString("actor-chat.server.port")
    val serverPath = s"akka.tcp://AkkaChat@$serverAddress:$serverPort/user/chatserver"
    val privateMessageRegex = """^@([^\s]+) (.*)$""".r
    val server = system.actorSelection(serverPath)

    val client = system.actorOf(Props(classOf[ChatClientActor], server, identity).withDeploy(Deploy(scope = RemoteScope(clientAddress))), name = identity)
	
	print("Client constructed: ")
	println(client)

    Iterator.continually(new ConsoleReader().readLine("> ")).takeWhile(_ != "/exit").foreach { msg =>
      msg match {
        case "/list" =>
          server.tell(RegisteredClients, client)

        case "/join" =>
          server.tell(RegisterClientMessage(client, identity), client)

        case "/leave" => 
          server.tell(Unregister(identity), client)
          
        case privateMessageRegex(target, msg) =>
          server.tell(PrivateMessage(target, msg), client)

        case _ =>
          server.tell(ChatMessage(msg), client)
      }
    }

    println("Exiting...")
    server.tell(Unregister(identity), client)
	exit()
  }
}

class ChatClientActor extends Actor {

    def receive = {

      case ChatMessage(message) =>
        println(s"${sender.path.name}: $message")

      case ChatInfo(msg) =>
        println ("INFO: ["+ msg +"]")

      case PrivateMessage(_, message) =>
        println(s"- ${sender.path.name}: $message")

      case RegisteredClientList(list) =>
        for (x <- list) println(x)

      case _ => println("Client Received something")
   }
}