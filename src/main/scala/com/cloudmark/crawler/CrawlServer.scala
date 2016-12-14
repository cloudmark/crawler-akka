package com.cloudmark.crawler

import akka.actor.{Actor, ActorRef, Props}
import com.cloudmark.crawler.CrawlServer.{CrawlRequest, CrawlResponse}
import com.cloudmark.crawler.LinkChecker.Result

import scala.collection.mutable

object CrawlServer {
  case class CrawlRequest(url: String, depth: Integer) {}
  case class CrawlResponse(url: String, links: Set[String]) {}
}

class CrawlServer extends Actor {

  val clients: mutable.Map[String, Set[ActorRef]] = collection.mutable.Map[String, Set[ActorRef]]()
  val controllers: mutable.Map[String, ActorRef] = mutable.Map[String, ActorRef]()

  def receive = {

    case CrawlRequest(url, depth) =>
      val controller = controllers get url
      if (controller.isEmpty) {
        controllers += (url -> context.actorOf(Props[LinkChecker](new LinkChecker(url, depth))))
        clients += (url -> Set.empty[ActorRef])
      }
      clients(url) += sender

    case Result(url, links) =>
      context.stop(controllers(url))
      clients(url) foreach (_ ! CrawlResponse(url, links))
      clients -= url
      controllers -= url
  }

}
