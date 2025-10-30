package models
import java.time.Duration

case class Route(name: String, price: BigDecimal, distance: Double, time: Duration)

trait Transport {
  def id: Int
  def capacity: Int
  def availableSeats: Int
  def route: Route
  def fuelCosts: BigDecimal

  def withAvailableSeats(newAvailable: Int): Transport

  def bookSeat(): Either[String, Transport] =
    if (availableSeats > 0)
      Right(withAvailableSeats(availableSeats - 1))
    else
      Left("No available seats")
}
