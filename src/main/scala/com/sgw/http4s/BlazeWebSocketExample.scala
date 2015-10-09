package com.sgw.http4s

import org.http4s.headers.`Content-Type`
import org.http4s.server.HttpService
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.websocket.WebsocketBits._
import org.http4s.dsl._
import org.http4s.server.websocket._
import org.http4s.MediaType._

import scala.concurrent.duration._

import scalaz.concurrent.Task
import scalaz.concurrent.Strategy
import scalaz.stream.async.unboundedQueue
import scalaz.stream.{Process, Sink}
import scalaz.stream.{DefaultScheduler, Exchange}
import scalaz.stream.time.awakeEvery

object BlazeWebSocketExample extends App {

  val host = "localhost"
  val port = 9123

  val webSocketsHTML =
    s"""
       |<html>
       |<head>
       |  <script>
       |var connection = new WebSocket('ws://$host:$port/ws');
       |
       |// When the connection is open, send some data to the server
       |connection.onopen = function () {
       |  connection.send('Ping'); // Send the message 'Ping' to the server
       |};
       |
       |// Log errors
       |connection.onerror = function (error) {
       |  console.log('WebSocket Error ' + error);
       |};
       |
       |// Log messages from the server
       |connection.onmessage = function (e) {
       |  console.log('Received: ' + e.data);
       |  connection.send('Ping');
       |};
       |  </script>
       |</head>
       |<body>
       |  <h1>Web Sockets Test</h1>
       |</body>
       |</html>
     """.stripMargin

  val route = HttpService {
    case GET -> Root / "hello" =>
      Ok(webSocketsHTML).withContentType(Some(`Content-Type`(`text/html`)))

    case req@ GET -> Root / "ws" =>
      val src = awakeEvery(1.seconds)(Strategy.DefaultStrategy, DefaultScheduler).map{ d => Text(s"${Thread.currentThread().getName}: Pong! $d") }
      val sink: Sink[Task, WebSocketFrame] = Process.constant {
        case Text(t, _) => Task.delay(println(s"${Thread.currentThread().getName}: Received: $t"))
        case f       => Task.delay(println(s"Unknown type: $f"))
      }
      WS(Exchange(src, sink))

    case req@ GET -> Root / "wsecho" =>
      val q = unboundedQueue[WebSocketFrame]
      val src = q.dequeue.collect {
        case Text(msg, _) => Text("You sent the server: " + msg)
      }
      WS(Exchange(src, q.enqueue))
  }

  BlazeBuilder.bindHttp(port)
    .withWebSockets(true)
    .mountService(route, "")
    .run
    .awaitShutdown()
}