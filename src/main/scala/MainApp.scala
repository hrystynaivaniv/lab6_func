import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.collection.mutable
import models._
import service._

object MainApp extends App {

  val routeA = Route("Route A", 50, 120.0, java.time.Duration.ofMinutes(90))
  val routeB = Route("Route B", 70, 200.0, java.time.Duration.ofMinutes(150))

  val bus1 = new Bus(1, 2, routeA, 100)
  val bus2 = new Bus(2, 1, routeB, 150)

  val transports: List[Transport] = List(bus1, bus2)

  val transportStock: mutable.Map[Int, Int] = mutable.Map(
    bus1.id -> bus1.availableSeats,
    bus2.id -> bus2.availableSeats
  )

  val passengers = List(
    Passenger("Alice", "Smith", "111-111"),
    Passenger("Bob", "Johnson", "222-222"),
    Passenger("Charlie", "Brown", "333-333"),
    Passenger("David", "Lee", "444-444")
  )

  println("Without synchronization")
  val raceFutures = passengers.map { p =>
    TicketService.buyTicketParallelRace(transportStock, p, "Bus", "Route A", transports)
      .map(result => (p, result))
  }

  val raceResults = Await.result(Future.sequence(raceFutures), 5.seconds)

  raceResults.foreach {
    case (p, Right(r)) =>
      println(s"${p.name} successfully bought a ticket. Seats left: ${r.ticket.transport.availableSeats}")
    case (p, Left(err)) =>
      println(s"${p.name} failed to buy a ticket: $err")
  }

  println(s"Current seat status: ${transportStock}")

  transportStock(bus1.id) = 2

  println("\nWith synchronization")
  val syncFutures = passengers.map { p =>
    TicketService.buyTicketParallel(transportStock, p, "Bus", "Route A", transports)
      .map(result => (p, result))
  }

  val syncResults = Await.result(Future.sequence(syncFutures), 5.seconds)

  syncResults.foreach {
    case (p, Right(r)) =>
      println(s"${p.name} successfully bought a ticket. Seats left: ${r.ticket.transport.availableSeats}")
    case (p, Left(err)) =>
      println(s"${p.name} failed to buy a ticket: $err")
  }

  println(s"Current seat status: ${transportStock}\n")

  transportStock(bus1.id) = 2
  transportStock(bus2.id) = 3
  val service = new TransportService(transports, transportStock)

  val futureResults: Future[List[(Passenger, Boolean)]] =
    service.checkPassengersAvailability(passengers, bus1.id, bus2.id)

  val results = Await.result(futureResults, 5.seconds)

  results.foreach { case (p, avail) =>
    val status = if (avail) "can book a seat" else "cannot book a seat"
    println(s"${p.name} $status")
  }

  println(s"Remaining seats: $transportStock")
}
