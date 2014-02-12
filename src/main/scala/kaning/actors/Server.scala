package kaning.actors

import akka.actor.{Actor, Props, ActorSystem, ActorRef, PoisonPill}
import collection.mutable.Map
import kaning.messages._
import kaning.actors._
import com.typesafe.config.ConfigFactory

object ChatServerApplication extends App {
  println("Starting Akka Chat Server Actor")
  val system = ActorSystem("AkkaChat", ConfigFactory.load.getConfig("chatserver"))
  val server = system.actorOf(Props[ChatServerActor], name = "chatserver")
  server ! StartUp
}

class ChatServerActor extends Actor {

  val connectedClients:Map[String, ActorRef] = Map()

  def receive = {

    case m @ ChatMessage(x: String) =>
      println(sender.path.name + ": " + x)
      connectedClients.values.filter(_ != sender).foreach(_.forward(m))
      sender ! new ChatInfo("ACK")

    case RegisterClientMessage(client: ActorRef, identity: String) =>
        println(s"${identity} joined this room")
        if(connectedClients.contains(identity)){
          sender ! ChatInfo(s"REGISTRATION FAILED: ${identity} is already registered")
        }else{
          connectedClients += (identity -> client)
          sender ! ChatInfo("REGISTERED")
        }

    case m @ PrivateMessage(target, _) =>
      connectedClients.values.filter(_.path.name.contains(target)).foreach(_.forward(m))
      sender ! new ChatInfo("P_ACK")

    case StartUp =>
      println("Received Start Server Signal")
      println(self)

    case RegisteredClients =>
      sender ! RegisteredClientList(connectedClients.keys)

    case Unregister(identity) =>
        // remove client from registered client set and send poison pill
        println(s"${identity} left this room")
        connectedClients.remove(identity).foreach(_ ! PoisonPill)

    case _ => println("Server received message")
  }
}