sealed trait Message
case class ChatMessage(msg: String) extends Message
case class ChatInfo(inf: String) extends Message