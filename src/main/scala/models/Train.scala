package models

case class Train(id: Int, capacity: Int,availableSeats: Int, route: Route, fuelCosts: BigDecimal, stopsAmount: Int) extends Transport
{
  def this(id: Int, capacity: Int, route: Route, fuelCosts: BigDecimal, stopsAmount: Int) =
    this(id, capacity, capacity, route, fuelCosts, stopsAmount)


  override def withAvailableSeats(newAvailable: Int): Train =
    copy(availableSeats = newAvailable)
}