package kaning.actors

import akka.actor.{Actor, Props, ActorSystem, ActorRef, PoisonPill}
import collection.mutable.Set
import kaning.messages._
import kaning.actors._
import com.typesafe.config.ConfigFactory

object ChatServerApplication extends App{
	println("Starting Akka Chat Server Actor") 
	val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("chatserver"))
	val server = system.actorOf(Props[ChatServerActor], name = "chatserver")
	server ! StartUp
}

class ChatServerActor extends Actor {

	val connectedClients:Set[ActorRef] = Set()

	def receive = {

		case m @ ChatMessage(x: String) => {
			println(sender + ": " + x)

      connectedClients.foreach(_.forward(m))

			sender ! new ChatInfo("ACK")
		}

		case RegisterClientMessage(client: ActorRef) => {
  			println("Client registering with server")
  			this.connectedClients += client
  			sender ! ChatInfo("REGISTERED")
  		}

  		case StartUp => {
  			println("Received Start Server Signal")
  			println(self)
  		}

  		case RegisteredClients => {
  			sender ! RegisteredClientList(connectedClients)
  		}
        
        case Unregister(client: ActorRef) => {
            // remove client from registered client set and send poison pill
            this.connectedClients -= client
            sender ! PoisonPill
        }

		case _ => println("Server received message")
	}
}
