package kaning.actors

import akka.actor._
import kaning.messages._
import kaning.actors._
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import kaning.messages.Unregister
import kaning.messages.RegisteredClientList
import kaning.messages.ChatMessage
import kaning.messages.RegisterClientMessage
import kaning.messages.ChatInfo
import kaning.messages.PrivateMessage
import kaning.messages.RegisteredClients
import kaning.messages.Broadcast

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

    val client = system.actorOf(Props(classOf[ChatClientActor], server, identity), name = identity)
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

class ChatClientActor(server: ActorSelection, id: String) extends Actor {

  	//context.setReceiveTimeout(3.seconds)
//  	val server = context.actorSelection(serverpath)


  	def receive = {

      case ChatMessage(message) =>
        println(s"$sender: $message")

  		case ChatInfo(msg) =>
  			println ("INFO: ["+ msg +"]")

  		case Broadcast(msg) =>
  			server ! ChatMessage(msg)

      case PrivateMessage(_, message) =>
        println(s"- $sender: $message")

      case RegisteredClientList(list) =>
        for (x <- list) println(x)

      case _ => println("Client Received something")
   }

 }
