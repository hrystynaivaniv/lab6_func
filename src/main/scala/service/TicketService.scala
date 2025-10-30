package service
import models.*

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


case class TicketPurchaseResult(ticket: Ticket, updatedTransports: List[Transport])


object TicketService {

  def findTransport(transports: List[Transport], transportType: String, routeName: String): Option[Transport] = {
    transports.find(t =>
      t.getClass.getSimpleName == transportType &&
        t.route.name == routeName
    )
  }

  def buyTicket(passenger: Passenger, transportType: String, routeName: String, transports: List[Transport]): Either[String, TicketPurchaseResult] = {
    for {
      transport <- findTransport(transports, transportType, routeName)
        .toRight(s"No transport found for type '$transportType' on route '$routeName'")
      updatedTransport <- transport.bookSeat()
    } yield {
      val updatedList = transports.map {
        case t if t.id == updatedTransport.id => updatedTransport
        case t => t
      }
      TicketPurchaseResult(Ticket(passenger, updatedTransport, transport.route.price), updatedList)
    }
  }

  def buyTicketParallel(transportStock: mutable.Map[Int, Int], passenger: Passenger, transportType: String, routeName: String, transports: List[Transport])
  : Future[Either[String, TicketPurchaseResult]] = Future {
    
    transportStock.synchronized {
      findTransport(transports, transportType, routeName) match {
        case Some(transport) =>
          val available = transportStock.getOrElse(transport.id, 0)
          if (available > 0) {
            transportStock(transport.id) = available - 1
            Right(TicketPurchaseResult(
              Ticket(passenger, transport.withAvailableSeats(available - 1), transport.route.price),
              transports
            ))
          } else {
            Left(s"No available seats on $transportType $routeName (id ${transport.id})")
          }
        case None =>
          Left(s"No transport found for type '$transportType' on route '$routeName'")
      }
    }
  }.recover {
    case e: Exception => Left(e.getMessage)
  }

  def buyTicketParallelRace(transportStock: mutable.Map[Int, Int], passenger: Passenger, transportType: String, routeName: String, transports: List[Transport])
  : Future[Either[String, TicketPurchaseResult]] = Future {

    findTransport(transports, transportType, routeName) match {
      case Some(transport) =>
        val available = transportStock.getOrElse(transport.id, 0)

        if (available > 0) {
          Thread.sleep(scala.util.Random.nextInt(300))

          transportStock(transport.id) = available - 1
          Right(
            TicketPurchaseResult(
              Ticket(passenger, transport.withAvailableSeats(available - 1), transport.route.price),
              transports
            )
          )
        } else {
          Left(s"No available seats on $transportType $routeName (id ${transport.id})")
        }

      case None =>
        Left(s"No transport found for type '$transportType' on route '$routeName'")
    }
  }.recover {
    case e: Exception => Left(e.getMessage)
  }

}