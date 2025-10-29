package models

case class Bus(id: Int, capacity: Int, availableSeats: Int, route: Route, fuelCosts: BigDecimal) extends Transport
{
  def this(id: Int, capacity: Int, route: Route, fuelCosts: BigDecimal) =
    this(id, capacity, capacity, route, fuelCosts)

  override def withAvailableSeats(newAvailable: Int): Bus =
    copy(availableSeats = newAvailable)
}