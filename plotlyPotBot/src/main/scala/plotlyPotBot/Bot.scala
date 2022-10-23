package plotlyPotBot

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.actor.{ClassicActorSystemProvider}
import scala.concurrent.{ExecutionContext, Future}

import spray.json._
import DefaultJsonProtocol._

object Bot {
  // classes to describe Telegram response to `getUpdate` request
  case class Document(file_id: String)
  case class Chat(id: Int)
  case class Message(message_id: Int, chat: Chat, document: Option[Document])
  case class Result(update_id: Int, message: Option[Message])
  case class Update(ok: Boolean, result: List[Result])
  implicit val documentFormat = jsonFormat1(Document)
  implicit val chatFormat = jsonFormat1(Chat)
  implicit val messageFormat = jsonFormat3(Message)
  implicit val resultFormat = jsonFormat2(Result)
  implicit val updateFormat = jsonFormat2(Update)
}

class Bot (botToken: String)
          (implicit classicActorSystemProvider: ClassicActorSystemProvider,
           executionContext: ExecutionContext) {
  import Bot._

  // do a `getUpdates` request of Telegram API
  def getUpdates: Future[Update] = {
    val requestString: String =
      "https://api.telegram.org/bot" + botToken + "/getUpdates"
    val httpRequest = HttpRequest(HttpMethods.GET, uri = requestString)
    // Futures:
    val response: Future[HttpResponse] = Http().singleRequest(httpRequest)
    val responseString: Future[String] =
      response.flatMap (_.entity.dataBytes.runReduce(_ ++ _).map(_.utf8String))
    responseString.map { JsonParser(_).convertTo[Update] }
  }

  // note that `text` should contain only URL-safe characters
  def writeInChat(chatId: Int, textToWrite: String): Future[HttpResponse] = {
    val requestString =
      "https://api.telegram.org/bot" + botToken +
      "/sendMessage?chat_id=" + chatId +
      "&text=" + textToWrite
    val httpRequest = HttpRequest(HttpMethods.POST, uri = requestString)
    Http().singleRequest(httpRequest)
  }

  // note that `text` should contain only URL-safe characters
  def replyToMessage(message: Message, replyText: String): Future[HttpResponse] = {
    val requestString =
      "https://api.telegram.org/bot" + botToken +
        "/sendMessage?chat_id=" + message.chat.id +
        "&text=" + replyText +
        "&reply_to_message_id=" + message.message_id
    val httpRequest = HttpRequest(HttpMethods.POST, uri = requestString)
    Http().singleRequest(httpRequest)
  }
}
