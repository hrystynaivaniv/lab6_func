package service
import models._

case class TransportRegistrationService(transports: List[Transport] = List(), routes: List[Route] = List()) {

  def addRoute(route: Route): TransportRegistrationService =
    copy(routes = routes :+ route)

  def addTransport(transport: Transport): TransportRegistrationService =
    copy(transports = transports :+ transport)

  def findTransportByRoute(routeName: String): List[Transport] =
    transports.filter(_.route.name == routeName)

  def findAvailableTransports(): List[Transport] =
    transports.filter(_.availableSeats > 0)

  def findByTransportType[T <: Transport](implicit m: Manifest[T]): List[Transport] =
    transports.collect { case t: T => t }

  def findByPrice(maxPrice: BigDecimal): List[Transport] =
    transports.filter(_.route.price <= maxPrice)
}
