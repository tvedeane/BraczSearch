import com.typesafe.config.ConfigFactory

import scala.annotation.tailrec
import scala.io.StdIn

object ConsoleBoot extends App {
  lazy val searchLink = ConfigFactory.load().getString("braczsearch.link")

  programLoop()
//  private val ISBN = "9788380620438"
//	9788374805537

  @tailrec
  def programLoop(link : String = ""): Unit = {
    val command: String = StdIn.readLine("*****\nPlease enter ISBN of book: (h + enter => shows help)\n")
    val (text, anotherLink: String) = command match {
      case "q" | "Q" => return
      case "h" | "H" =>
        ("Available commands:\nq - quits\nh - shows this help\nl - displays previously used link", link)
      case "l" | "L" =>
        if (isEmpty(link))
          ("Previous link is not available.", link)
        else
          (s"Previously used link: $link", link)
      case _ =>
        if (isISBN(command)) {
          val newLink = searchLink.format(command)
          val places: Map[Boolean, List[BookLocation]] = CatalogScraper.getPlacesGrouped(newLink)
          if (places.nonEmpty)
            places.map( place =>
              if (place._1) {
                ("This book is available at:\n" + place._2.mkString("\n"), newLink)
              } else {
                ("This book will be available at:\n" + place._2.mkString("\n"), newLink)
              }
            ).fold(("",""))((a,b) => (a._1 + "\n" + b._1, a._1))
          else
            (s"Sorry, could not find available locations for: $command", newLink)
        } else {
          ("Please enter 10 or 13 digits only.", link)
        }
    }
    println(text + "\n")
    programLoop(anotherLink)
  }

  private def isISBN(command: String): Boolean = {
    (command.length() == 10 || command.length == 13) && command.matches("\\d.*")
  }

  private def isEmpty(x: String) = x == null || x.isEmpty
}
