package kaning

import akka.actor._
import akka.util.Timeout
import akka.io.IO
import spray.http._
import spray.routing._
import scala.concurrent.duration._


class RestInterface extends HttpServiceActor
                    with RestApi {
  def receive = runRoute(routes)
}

trait RestApi extends HttpServiceActor { actor: Actor =>
  import kaning.messages._

  implicit val timeout = Timeout(10 seconds)
  import akka.pattern.ask
  import akka.pattern.pipe

  println("Starting Chat Server")
  val server = context.actorOf(Props[ChatServer])

  def routes: Route =
    /*
    path("events") {
      put {
        entity(as[Event]) { event => requestContext =>
          val responder = createResponder(requestContext)
          boxOffice.ask(event).pipeTo(responder)
        }
      } ~
      get { requestContext =>
        val responder = createResponder(requestContext)
        boxOffice.ask(GetEvents).pipeTo(responder)
      }
    } ~
    path("ticket") {
      get {
        entity(as[TicketRequest]) { ticketRequest => requestContext =>
          val responder = createResponder(requestContext)
          boxOffice.ask(ticketRequest).pipeTo(responder)
        }
      }
    } ~
    path("ticket" / PathElement) { eventName => requestContext =>
      val req = TicketRequest(eventName)
      val responder = createResponder(requestContext)
      boxOffice.ask(req).pipeTo(responder)
    }
    */
    path("home") {
      get {requestContext => 
        val responder = createResponder(requestContext)
        println("HOME PAGE REQUESTED")
        //boxOffice.ask(GetEvents).pipeTo(responder) 
      }
    }
  def createResponder(requestContext:RequestContext) = {
    context.actorOf(Props(new Responder(requestContext, server)))
  }

}

class Responder(requestContext:RequestContext, ticketMaster:ActorRef) extends Actor with ActorLogging {
  import kaning.messages._
  import spray.httpx.SprayJsonSupport._

  def receive = {

    /*case ticket:Ticket =>
      requestContext.complete(StatusCodes.OK, ticket)
      self ! PoisonPill

    case EventCreated =>
      requestContext.complete(StatusCodes.OK)
      self ! PoisonPill

    case SoldOut =>
      requestContext.complete(StatusCodes.NotFound)
      self ! PoisonPill

    case Events(events) =>
      requestContext.complete(StatusCodes.OK, events)
      self ! PoisonPill
    */
    case _ => {
      println("Got something")
    }

  }
}