import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.remote.RemoteActor
import scala.actors.remote.RemoteActor._
import scala.actors.remote.Node

object ChatClient{
	
	/*
	*
	* Argument one is host name
	* Argument two is port
	* Argument 3 is client name
	*/
	def main(args:Array[String]){
		print("Identify yourself: ")
		val clientId = readLine()
		val peer = Node("localhost", 2552)
		val client = new ClientActor(peer);
		client.setId(clientId)
		client.start()
	}
	
}


class ClientActor(peer:Node) extends Actor{

	RemoteActor.classLoader = getClass().getClassLoader()
	var id:String = "client"
	def setId(id: String) = {this.id = id}
	def getId:String = this.id

	def act() {
		alive(2552)//@TODO: make this a param
		register('ChatClient, self)
		val server = select(peer, 'ChatServer)
		link(server)
		
		while (true){
			print(">> ")
			val cinput = readLine()
			if(cinput != null){
				cinput match {
					case "disconnect" => {
						println(getId + " disconnecting...")
						unlink(server)
					}

					case "connect" => {
						println(getId + " connecting...")
						link(server)
					}

					case "exit" => {
						unlink(server)
						exit()
					}

					case _ => server ! new ChatMessage(getId, cinput)
				}

				receive {
					case ChatMessage(c:String, x: String) => {
						println("From "+c+": "+x)
					}

					case ChatInfo(x:String) => {
						println("INFO: "+x)
					}
					case _ => println("Server received invalid message format")
				}
			}
			
		}
	}
	
}
