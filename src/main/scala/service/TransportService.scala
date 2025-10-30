import scala.concurrent.{Future, ExecutionContext}
import scala.collection.mutable
import models._

class TransportService(transports: List[Transport], transportStock: mutable.Map[Int, Int])
                      (implicit ec: ExecutionContext) {

  def checkBusAvailability(busId: Int): Future[Boolean] = Future {
    transportStock.synchronized {
      transportStock.get(busId) match {
        case Some(seats) if seats > 0 =>
          transportStock(busId) = seats - 1
          true
        case _ => false
      }
    }
  }

  def checkBusWithFallback(busId: Int, fallbackBusId: Int): Future[Boolean] =
    checkBusAvailability(busId).flatMap {
      case true => Future.successful(true)
      case false =>
        println(s"Bus $busId unavailable, trying fallback bus $fallbackBusId")
        checkBusAvailability(fallbackBusId)
    }


  def checkPassengersAvailability(passengers: List[Passenger], primaryBusId: Int, fallbackBusId: Int)
  : Future[List[(Passenger, Boolean)]] = {
    val availabilityFutures: List[Future[(Passenger, Boolean)]] = passengers.map { p =>
      checkBusWithFallback(primaryBusId, fallbackBusId).map(avail => (p, avail))
    }
    Future.sequence(availabilityFutures)
  }
}
