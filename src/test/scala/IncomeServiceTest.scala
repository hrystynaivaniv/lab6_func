import org.scalatest.funsuite.AnyFunSuite
import models._
import service._
import java.time.Duration

class IncomeServiceTest extends AnyFunSuite {

  val route1 = Route("Kyiv-Lviv", BigDecimal(100), 500, Duration.ofHours(7))
  val route2 = Route("Kyiv-Odessa", BigDecimal(150), 600, Duration.ofHours(8))

  val bus = new Bus(1, 2, route1, BigDecimal(50))
  val train = new Train(2, 3, route2, BigDecimal(100), 5)
  val plane = new Plane(3, 1, route1, BigDecimal(500), "A1")

  val passenger1 = Passenger("Ivan", "Ivanov", "111")
  val passenger2 = Passenger("Petro", "Petrov", "222")
  val passenger3 = Passenger("Anna", "Shevchenko", "333")

  val ticket1 = Ticket(passenger1, bus, bus.route.price)
  val ticket2 = Ticket(passenger2, bus, bus.route.price)
  val ticket3 = Ticket(passenger3, train, train.route.price)

  val tickets = List(ticket1, ticket2, ticket3)
  val transports = List(bus, train, plane)

  test("incomeByTransport should calculate correct income for a transport") {
    val busIncome = IncomeService.incomeByTransport(bus, tickets)
    assert(busIncome == (bus.route.price * 2 - bus.fuelCosts))

    val trainIncome = IncomeService.incomeByTransport(train, tickets)
    assert(trainIncome == (train.route.price - train.fuelCosts))

    val planeIncome = IncomeService.incomeByTransport(plane, tickets)
    assert(planeIncome == (-plane.fuelCosts)) // бо немає квитків
  }

  test("incomeByTransportAndRoute should calculate income for specific route") {
    val busRouteIncome = IncomeService.incomeByTransportAndRoute(bus, "Kyiv-Lviv", tickets)
    assert(busRouteIncome == (bus.route.price * 2 - bus.fuelCosts))

    val trainRouteIncome = IncomeService.incomeByTransportAndRoute(train, "Kyiv-Lviv", tickets)
    assert(trainRouteIncome == -train.fuelCosts) // train не має квитків на цей маршрут
  }

  test("totalIncome should sum income for all transports") {
    val total = IncomeService.totalIncome(tickets, transports)
    val expected = (bus.route.price * 2 - bus.fuelCosts) + (train.route.price - train.fuelCosts) + (-plane.fuelCosts)
    assert(total == expected)
  }

  test("incomeByRoute should sum income for all transports on a specific route") {
    val route1Income = IncomeService.incomeByRoute("Kyiv-Lviv", tickets, transports)
    val expected = (bus.route.price * 2 - bus.fuelCosts) + (-plane.fuelCosts)
    assert(route1Income == expected)

    val route2Income = IncomeService.incomeByRoute("Kyiv-Odessa", tickets, transports)
    val expected2 = (train.route.price - train.fuelCosts)
    assert(route2Income == expected2)
  }
}
