package plotlyPotBot

import org.scalacheck.{Arbitrary, Properties}
import org.scalacheck.Prop.forAll
import spray.json.JsonParser
import Bot._

object botSuite extends Properties("botSuite") {
  property("json_full") = forAll { (updateId: Int, messageId: Int, chatId: Int, fileId: Int) =>
    val text: String =
      """{"ok": true,
        | "result": [
        |   { "update_id": %d,
        |     "message":
        |     { "message_id": %d,
        |       "chat": {"id": %d},
        |       "document": {"file_id": "%s"}
        |     }
        |   }
        | ]
        |}""".stripMargin.format(updateId, messageId, chatId, fileId.toString)
    val update = JsonParser(text).convertTo[Update]
    (update.result.map(_.update_id).head == updateId) &&
      (update.result.map(_.message.get.message_id).head == messageId) &&
      (update.result.map(_.message.get.chat.id).head == chatId) &&
      (update.result.map(_.message.get.document.get.file_id).head == fileId.toString)
  }

  property("json_no_document") = forAll { (updateId: Int, messageId: Int, chatId: Int) =>
    val text: String =
      """{"ok": true,
        | "result": [
        |   { "update_id": %d,
        |     "message":
        |     { "message_id": %d,
        |       "chat": {"id": %d}
        |     }
        |   }
        | ]
        |}""".stripMargin.format(updateId, messageId, chatId)
    val update = JsonParser(text).convertTo[Update]
    (update.result.map(_.update_id).head == updateId) &&
      (update.result.map(_.message.get.message_id).head == messageId) &&
      (update.result.map(_.message.get.chat.id).head == chatId)
  }

  property("json_no_message") = forAll { (updateId: Int) =>
    val text: String =
      """{"ok": true,
        | "result": [
        |   { "update_id": %d }
        | ]
        |}""".stripMargin.format(updateId)
    val update = JsonParser(text).convertTo[Update]
    (update.result.map(_.update_id).head == updateId)
  }
}
