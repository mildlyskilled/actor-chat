package kaning

import kaning.messages._
import akka.actor.{Actor, Props, ActorSystem, ActorRef, PoisonPill}
import collection.mutable.Set

class ChatServer extends Actor {

  println("Started Chat Server")
	val connectedClients:Set[ActorRef] = Set()

	def receive = {

		case ChatMessage(x: String) => {
			println(sender + ": " + x)
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
