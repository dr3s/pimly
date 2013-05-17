package service

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import mail.ImapOAuth
import javax.mail.FetchProfile
import com.sun.mail.gimap.GmailFolder
import javax.mail.Folder
import com.sun.mail.gimap.GmailMessage
import service.InboxAuth._

object InboxAuth {
  
  case class OauthIdentity(email: String, token: String)
}

class InboxSlurper extends Actor {
  val log = Logging(context.system, this)


  def receive = {
    case id:OauthIdentity => {

      val store = ImapOAuth.connect("imap.gmail.com",
        993,
        id.email,
        id.token,
        false)

      val inbox = store.getFolder("Inbox")

      val isGmail = store.hasCapability("X-GM-EXT-1")

      val profile = new FetchProfile()
      profile.add(FetchProfile.Item.ENVELOPE)
      profile.add(FetchProfile.Item.CONTENT_INFO)
      profile.add(FetchProfile.Item.FLAGS)
      profile.add("X-GM-MSGID")
      profile.add("X-GM-THRID")
      profile.add("X-GM-LABELS")
      try {
        inbox.open(Folder.READ_ONLY)

        val count = inbox.getMessageCount()
        // limit this to 20 message during testing
        val limit = 20
        val ginbox = inbox.asInstanceOf[GmailFolder]
        val messages = ginbox.getMessages(count - limit, count)
        inbox.fetch(messages, profile)
        for (message <- messages) {
          val gmailMsg = message.asInstanceOf[GmailMessage]
          log.debug(message.getSubject())
          log.debug("GMail Msg Id: " + gmailMsg.getMsgId())
          log.debug("GMail Labels: " + gmailMsg.getLabels())
        }
        sender ! count
      } finally {

        inbox.close(true)
        store.close()
      }
    }
    case _ => log.error("Unknown message")
  }
}