package db

import akka.actor.Actor
import com.netflix.astyanax.model.ColumnFamily
import com.netflix.astyanax.serializers.StringSerializer
import play.api.libs.iteratee.Enumerator
import javax.mail.Message
import play.api.libs.iteratee.Iteratee
import play.api.libs.concurrent.Execution.Implicits._
import play.Logger
import com.sun.mail.gimap.GmailMessage
import scala.concurrent.Future
import model.User

class MailMetaDao extends Actor {

  val family = ColumnFamily.newColumnFamily("mailmeta",
    StringSerializer.get(), StringSerializer.get())
  val session = Cassandra.context.getClient()

  val stmt = session.prepareQuery(family)
    .withCql("INSERT INTO mailmeta(key, labels) VALUES (?, ?);")

  def save(user: User, msg: Message): Unit = {
    val gmailMsg = msg.asInstanceOf[GmailMessage]
    Logger.debug(msg.getSubject())
    Logger.debug("GMail Msg Id: " + gmailMsg.getMsgId())
    Logger.debug("GMail Labels: " + gmailMsg.getLabels())
    val result = stmt.asPreparedStatement().withStringValue(user.email.get + "-" + gmailMsg.getMsgId())
    gmailMsg.getLabels() foreach (lbl => result.withStringValue(lbl))
    result.executeAsync()
  }

  def receive: Receive = {
    case (user: User, msg: Message) => save(user, msg)
    case _ => Logger.error("Unknown message")
  }
}