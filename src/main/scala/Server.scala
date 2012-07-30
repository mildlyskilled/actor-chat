import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.remote.RemoteActor
import scala.actors.remote.RemoteActor._
import scala.actors.remote.Node
import collection.mutable.Set



object ChatServer extends App{
	val server = new ServerActor();
	server.start()
	
}

class ServerActor() extends Actor{
	RemoteActor.classLoader = getClass().getClassLoader()
	val connectedClients:Set[String] = Set()

	def getClients = { this.connectedClients }

	def registerClient(client: ClientActor) = {
		if(!this.connectedClients.contains(client.getId)){
			this.connectedClients += client.getId
			println(this.connectedClients)
		}else{
			client ! "This client name is not unique"
		}
	}

	def act() {
		
		alive(2552)//@TODO: make this a param
		register('ChatServer, self)
		println("Server is ready")
		while(true) {
			receive {
				case ChatMessage(c:String, x: String) => {
					println("Client Message Received from "+ c + ": "+x+" acknowledged")
					sender ! new ChatInfo("ACK")
				}
				case _ => println("Server received invalid message format")
			}
		}
	}
}
