package kaning.messages

import akka.actor.{ActorRef}
import kaning.actors._
import collection.mutable.Set

sealed trait  Message
case object StartUp extends Message
case class ChatMessage(msg: String) extends Message
case class PrivateMessage(target: String, msg: String) extends Message
case class ChatInfo(inf: String) extends Message
case class RegisterClientMessage(client:ActorRef) extends Message
case object Unregister extends Message
case object RegisteredClients extends Message
case class RegisteredClientList(list: Set[ActorRef]) extends Message
