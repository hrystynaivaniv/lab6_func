package service

import models._

object IncomeService {

  def incomeByTransport(transport: Transport, tickets: List[Ticket]): BigDecimal = {
    val transportTickets = tickets.filter(_.transport.id == transport.id)
    transportTickets.map(_.price).fold(BigDecimal(0))(_ + _) - transport.fuelCosts
  }

  def incomeByTransportAndRoute(transport: Transport, routeName: String, tickets: List[Ticket]): BigDecimal = {
    val transportTickets = tickets.filter(t => t.transport.id == transport.id && t.transport.route.name == routeName)
    transportTickets.map(_.price).fold(BigDecimal(0))(_ + _) - transport.fuelCosts
  }

  def totalIncome(tickets: List[Ticket], transports: List[Transport]): BigDecimal =
    transports.map(t => incomeByTransport(t, tickets)).fold(BigDecimal(0))(_ + _)

  def incomeByRoute(routeName: String, tickets: List[Ticket], transports: List[Transport]): BigDecimal =
    transports.filter(_.route.name == routeName).map(t => incomeByTransport(t, tickets)).fold(BigDecimal(0))(_ + _)
}
