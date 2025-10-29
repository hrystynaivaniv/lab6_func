import org.scalatest.funsuite.AnyFunSuite
import models._
import service._
import java.time.Duration

class TransportRegistrationServiceTest extends AnyFunSuite {

  val route1 = Route("Kyiv-Lviv", BigDecimal(100), 500, Duration.ofHours(7))
  val route2 = Route("Kyiv-Odessa", 120, 600, Duration.ofHours(8))

  val bus1 = new Bus(1, 2, route1, BigDecimal(50))
  val bus2 = new Bus(2, 3, route2, BigDecimal(60))
  val train1 = new Train(3, 5, route1, BigDecimal(100), 5)
  val plane1 = new Plane(4, 1, route2, BigDecimal(500), "A1")

  test("addRoute should add new route") {
    val service = TransportRegistrationService()
    val updatedService = service.addRoute(route1).addRoute(route2)
    assert(updatedService.routes.contains(route1))
    assert(updatedService.routes.contains(route2))
  }

  test("addTransport should add new transport") {
    val service = TransportRegistrationService()
    val updatedService = service.addTransport(bus1).addTransport(train1)
    assert(updatedService.transports.contains(bus1))
    assert(updatedService.transports.contains(train1))
  }

  test("findTransportByRoute should return transports for given route") {
    val service = TransportRegistrationService(List(bus1, bus2, train1, plane1))
    val kyivLviv = service.findTransportByRoute("Kyiv-Lviv")
    assert(kyivLviv.contains(bus1))
    assert(kyivLviv.contains(train1))
    assert(!kyivLviv.contains(bus2))
  }

  test("findAvailableTransports should return only transports with available seats") {
    val busFull = bus1.copy(availableSeats = 0)
    val service = TransportRegistrationService(List(busFull, bus2, train1))
    val available = service.findAvailableTransports()
    assert(!available.contains(busFull))
    assert(available.contains(bus2))
    assert(available.contains(train1))
  }

  test("findByTransportType should filter transports by type") {
    val service = TransportRegistrationService(List(bus1, bus2, train1, plane1))
    val buses = service.findByTransportType[Bus]
    assert(buses.forall(_.isInstanceOf[Bus]))
    assert(buses.contains(bus1))
    assert(buses.contains(bus2))
    assert(!buses.contains(train1))
  }

  test("findByPrice should filter transports under max price") {
    val service = TransportRegistrationService(List(bus1, bus2, train1, plane1))
    val cheap = service.findByPrice(100)
    assert(cheap.contains(bus1))
    assert(cheap.contains(train1))
    assert(!cheap.contains(bus2))
    assert(!cheap.contains(plane1))
  }
}
