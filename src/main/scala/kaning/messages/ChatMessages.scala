package kaning.messages

import akka.actor.{ActorRef}
import kaning.actors._
import collection.mutable.Set

sealed trait  Message
@serializable
case class StartUp extends Message
case class Register extends Message
case class Disconnect extends Message
case class ChatMessage(msg: String) extends Message
case class PrivateMessage(target: String, msg: String) extends Message
case class ChatInfo(inf: String) extends Message
case class RegisterClientMessage(client:ActorRef) extends Message
case class Unregister(client: ActorRef) extends Message
case class RegisteredClients extends Message
case class RegisteredClientList(list: Set[ActorRef]) extends Message
case class Broadcast(msg: String) extends Message
