package kaning.actors

import akka.actor.{Actor, Props, ActorSystem, ActorRef}
import akka.actor.Identify
import akka.actor.ActorIdentity
import kaning.messages._
import kaning.actors._
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

object ChatClientApplication {

	def main(args:Array[String]){
		println("Start Akka Chat Client Actor") 
		print ("identify yourself: ")
		val identity = readLine()
		val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("chatclient"))
    val serverAddress = system.settings.config.getString("actor-chat.server.address")
    val serverPort = system.settings.config.getString("actor-chat.server.port")
		val remotePath = s"akka.tcp://AkkaChat@$serverAddress:$serverPort/user/chatserver"

    val privateMessageRegex = """^@([^\s]+) (.*)$""".r

    val server = system.actorSelection(remotePath)

    val client = system.actorOf(Props(classOf[ChatClientActor], remotePath, identity), name = identity)
        var chatmessage = ""
        var cursor = true
        while (cursor) {
            chatmessage = readLine()
            chatmessage match {
                case "/list" => {
                    server.tell(RegisteredClients, client)
                }

                case "/join" => {
                  server.tell(RegisterClientMessage(client), client)
//                    client ! Register
                }

                case "/exit" => {
                  server.tell(Unregister(client), client)
//                    client ! Disconnect
                    cursor = false
                }

                case privateMessageRegex(target, msg) =>
                  server.tell(PrivateMessage(target, msg), client)

                case _ => client ! Broadcast(chatmessage)
            } 
        }

        "Client disconnected!"
    }
}

class ChatClientActor(serverpath: String, id: String) extends Actor {

  	//context.setReceiveTimeout(3.seconds)
  	val server = context.actorSelection(serverpath)


  	def receive = {

      case ChatMessage(message) =>
        println(s"$sender: $message")

  		case ChatInfo(msg) =>
  			println ("INFO: ["+ msg +"]")

  		case Broadcast(msg) =>
  			server ! ChatMessage(msg)

      case PrivateMessage(sender, message) =>
        println(s"- $sender: $message")

      case RegisteredClientList(list) =>
        for (x <- list) println(x)

      case _ => println("Client Received something")
   }

 }
