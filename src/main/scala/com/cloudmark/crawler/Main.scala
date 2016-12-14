package com.cloudmark.crawler

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.cloudmark.crawler.CrawlServer.{CrawlRequest, CrawlResponse}

object Main extends App {
  println(s"Current Time ${System.currentTimeMillis}")
  val system = ActorSystem("Crawler")
  val receptionist = system.actorOf(Props[CrawlServer], "CrawlServer")
  val main = system.actorOf(Props[Main](new Main(receptionist, "https://www.bbc.co.uk/", 2)), "BBCActor")
}

class Main(receptionist: ActorRef, url: String, depth: Integer) extends Actor {
  receptionist ! CrawlRequest(url, depth)
  def receive = {
    case CrawlResponse(root, links) =>
      println(s"Root: $root")
      println(s"Links: ${links.toList.sortWith(_.length < _.length).mkString("\n")}")
      println("=========")
      println(s"Current Time ${System.currentTimeMillis}")
  }
}