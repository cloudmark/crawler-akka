package com.cloudmark.crawler

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.cloudmark.crawler.HelloWorld.{Hello};

object HelloWorld {
  case class Hello()
}

class HelloWorld extends Actor {
  override def receive = {
    case Hello => println("World!")
  }
}

object HelloWorldMain extends App {
  val system:ActorSystem = ActorSystem("HelloWorldSystem")
  val helloWorld: ActorRef = system.actorOf(Props[HelloWorld], "HelloWorld")
  helloWorld ! Hello
}