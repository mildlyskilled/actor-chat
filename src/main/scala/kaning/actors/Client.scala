package kaning.actors

import akka.actor._
import com.typesafe.config.ConfigFactory
import kaning.messages.Unregister
import kaning.messages.RegisteredClientList
import kaning.messages.ChatMessage
import kaning.messages.RegisterClientMessage
import kaning.messages.ChatInfo
import kaning.messages.PrivateMessage
import kaning.messages.RegisteredClients

object ChatClientApplication {

  def main(args:Array[String]) {
    println("Start Akka Chat Client Actor")
    print ("identify yourself: ")
    val identity = readLine()
    val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("chatclient"))
    val serverAddress = system.settings.config.getString("actor-chat.server.address")
    val serverPort = system.settings.config.getString("actor-chat.server.port")
    val remotePath = s"akka.tcp://AkkaChat@$serverAddress:$serverPort/user/chatserver"

    val privateMessageRegex = """^@([^\s]+) (.*)$""".r

    val server = system.actorSelection(remotePath)

    val client = system.actorOf(Props(classOf[ChatClientActor], server, identity), name = identity)

    Iterator.continually(readLine()).takeWhile(_ != "/exit").foreach { msg =>
      msg match {
        case "/list" =>
          server.tell(RegisteredClients, client)

        case "/join" =>
          server.tell(RegisterClientMessage(client), client)

        case privateMessageRegex(target, msg) =>
          server.tell(PrivateMessage(target, msg), client)

        case _ =>
          server.tell(ChatMessage(msg), client)
      }
    }

    println("Exiting...")
    server.tell(Unregister, client)
  }
}

class ChatClientActor(server: ActorSelection, id: String) extends Actor {

    def receive = {

      case ChatMessage(message) =>
        println(s"$sender: $message")

      case ChatInfo(msg) =>
        println ("INFO: ["+ msg +"]")

      case PrivateMessage(_, message) =>
        println(s"- ${sender.path.name}: $message")

      case RegisteredClientList(list) =>
        for (x <- list) println(x)

      case _ => println("Client Received something")
   }
}
