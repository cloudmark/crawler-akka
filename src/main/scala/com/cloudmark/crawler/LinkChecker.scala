package com.cloudmark.crawler

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import com.cloudmark.crawler.Getter.Done
import com.cloudmark.crawler.LinkChecker.{CheckUrl, Result}

import scala.concurrent.duration._

object LinkChecker {

  case class CheckUrl(url: String, depth: Int) {}

  case class Result(url: String, links: Set[String]) {}

}

class LinkChecker(root: String, originalDepth: Integer) extends Actor {

  var cache = Set.empty[String]
  var children = Set.empty[ActorRef]

  self ! CheckUrl(root, originalDepth)
  context.setReceiveTimeout(10 seconds)


  def receive = {
    case CheckUrl(url, depth) =>
      if (!cache(url) && depth > 0)
        children += context.actorOf(Props[Getter](new Getter(url, depth - 1)))
      cache += url

    case Done =>
      children -= sender
      if (children.isEmpty) context.parent ! Result(root, cache)

    case ReceiveTimeout => children foreach (_ ! Getter.Abort)
  }
}
