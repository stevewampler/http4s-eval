package com.sgw.http4s

import org.http4s.server._
import org.http4s.dsl._
import org.http4s.server.blaze.BlazeBuilder

/**
 * Run this and then visit http://localhost:9123/http4s/hello
 */
object HelloWorld  extends App {
  val route = HttpService {
    case GET -> Root / "hello" =>
      Ok("Hello world.")
  }

//    case req@ GET -> Root / "ws" =>
//      val src = awakeEvery(1.seconds)(Strategy.DefaultStrategy, DefaultScheduler).map{ d => Text(s"Ping! $d") }
//      val sink: Sink[Task, WebSocketFrame] = Process.constant {
//        case Text(t, _) => Task.delay( println(t))
//        case f       => Task.delay(println(s"Unknown type: $f"))
//      }
//      WS(Exchange(src, sink))
//
//    case req@ GET -> Root / "wsecho" =>
//      val q = unboundedQueue[WebSocketFrame]
//      val src = q.dequeue.collect {
//        case Text(msg, _) => Text("You sent the server: " + msg)
//      }
//
//      WS(Exchange(src, q.enqueue))

  BlazeBuilder.bindHttp(9123)
    .withWebSockets(true)
    .mountService(route, "/http4s")
    .run
    .awaitShutdown()
}