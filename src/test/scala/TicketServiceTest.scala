import org.scalatest.funsuite.AnyFunSuite
import models._
import service._
import java.time.Duration

class TicketServiceTest extends AnyFunSuite {

  val route1 = Route("Kyiv-Lviv", BigDecimal(100), 500, Duration.ofHours(7))
  val route2 = Route("Kyiv-Odessa", BigDecimal(120), 600, Duration.ofHours(8))

  val bus = new Bus(1, 2, route1, BigDecimal(50))
  val train = new Train(2, 3, route2, BigDecimal(100), 5)
  val plane = new Plane(3, 1, route1, BigDecimal(500), "A1")

  val transports = List(bus, train, plane)

  val passenger1 = Passenger("Ivan", "Ivanov", "111")
  val passenger2 = Passenger("Petro", "Petrov", "222")
  val passenger3 = Passenger("Anna", "Shevchenko", "333")

  test("findTransport should return correct transport by type and route") {
    val result = TicketService.findTransport(transports, "Bus", "Kyiv-Lviv")
    assert(result.contains(bus))

    val result2 = TicketService.findTransport(transports, "Plane", "Kyiv-Lviv")
    assert(result2.contains(plane))

    val notFound = TicketService.findTransport(transports, "Bus", "Kyiv-Odessa")
    assert(notFound.isEmpty)
  }

  test("buyTicket should decrease availableSeats") {
    val ticketResult = TicketService.buyTicket(passenger1, "Bus", "Kyiv-Lviv", transports)
    assert(ticketResult.isRight)
    val updatedBus = ticketResult.toOption.get.updatedTransports.find(_.id == bus.id).get
    assert(updatedBus.availableSeats == bus.availableSeats - 1)
  }

  test("buyTicket should return Left if no transport found") {
    val ticketResult = TicketService.buyTicket(passenger1, "Bus", "Nonexistent", transports)
    assert(ticketResult.isLeft)
  }

  test("buyTicket should return Left if no available seats") {
    val first = TicketService.buyTicket(passenger1, "Plane", "Kyiv-Lviv", transports)
    val second = TicketService.buyTicket(passenger2, "Plane", "Kyiv-Lviv", first.toOption.get.updatedTransports)
    assert(second.isLeft)
  }

  test("IncomeService should calculate correct income") {
    val tickets = List(
      Ticket(passenger1, bus, bus.route.price),
      Ticket(passenger2, bus, bus.route.price),
      Ticket(passenger3, train, train.route.price)
    )
    val busIncome = IncomeService.incomeByTransport(bus, tickets)
    assert(busIncome == (bus.route.price * 2 - bus.fuelCosts))

    val total = IncomeService.totalIncome(tickets, transports)
    assert(total == ((bus.route.price * 2 - bus.fuelCosts) + (train.route.price - train.fuelCosts) + (plane.route.price * 0 - plane.fuelCosts)))
  }
}
