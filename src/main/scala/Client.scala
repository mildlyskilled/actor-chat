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
		val peer = Node("localhost", 2552)
		val client = new ClientActor(peer);
		client.setId("nana")
		client.start()
	}
	
}


class ClientActor(peer:Node) extends Actor{

	var id:String = "client"
	def setId(id: String) = {this.id = id}
	def getId = this.id

	def act() {
		alive(2552)//@TODO: make this a param
		register('ChatClient, self)
		val server = select(peer, 'ChatServer)
		link(server)
		
		while (true){
			print(">> ")
			val cinput = readLine()
			if(cinput != null){
				var mymessage = cinput
				server ! mymessage

				receive {
					case (x: String) => {
						if(x == "exit"){
							println("Closing thread...")
							unlink(server)
							exit()
						}
						println("From Server: "+x)
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
