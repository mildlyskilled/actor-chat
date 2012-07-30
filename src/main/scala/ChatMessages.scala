sealed trait  Message
@serializable
case class ChatMessage(clientid:String, msg: String) extends Message
case class ChatInfo(inf: String) extends Message
