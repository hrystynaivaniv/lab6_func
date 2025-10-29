package models

case class Plane(id: Int, capacity: Int, availableSeats: Int, route: Route, fuelCosts: BigDecimal, boardingGate: String) extends Transport
{
  def this(id: Int, capacity: Int, route: Route, fuelCosts: BigDecimal, boardingGate: String) =
  this(id, capacity, capacity, route, fuelCosts, boardingGate)
  
  override def withAvailableSeats(newAvailable: Int): Plane =
    copy(availableSeats = newAvailable)
}