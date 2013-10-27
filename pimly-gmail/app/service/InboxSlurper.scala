package service

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import mail.ImapOAuth
import javax.mail.FetchProfile
import com.sun.mail.gimap.GmailFolder
import javax.mail.Folder
import com.sun.mail.gimap.GmailMessage
import play.api.libs.iteratee.{ Concurrent, Enumerator }
import javax.mail.Message
import play.api.libs.concurrent.Execution.Implicits._
import play.Logger
import play.api.libs.iteratee.Iteratee
import play.api.libs.concurrent.Akka
import db.MailMetaDao
import javax.mail.Flags
import javax.mail.search.FlagTerm
import javax.mail.search.ReceivedDateTerm
import javax.mail.search.ComparisonTerm
import org.joda.time.LocalDateTime
import javax.mail.search.AndTerm
import model.User



class InboxSlurper extends Actor {

  def receive = {
    case user: User => {

      val store = ImapOAuth.connect("imap.gmail.com",
        993,
        user.email,
        user.oAuth2Info,
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
      inbox.open(Folder.READ_ONLY)

      //        val count = inbox.getMessageCount()
      // limit this to 20 message during testing
      //        val limit = 10
      val ginbox = inbox.asInstanceOf[GmailFolder]
      val seen = new Flags(Flags.Flag.SEEN)
      val unseenFlagTerm = new FlagTerm(seen, false)
      val newerThan = new ReceivedDateTerm(ComparisonTerm.GT, LocalDateTime.now().minusDays(1).toDate())
      val andTerm = new AndTerm(unseenFlagTerm, newerThan)
      val messages = ginbox.search(newerThan)
      //        val messages = ginbox.getMessages(count - limit + 1, count)
      inbox.fetch(messages, profile)
      val emailFeed = Concurrent.unicast[Message](
        onStart = {
          pushee =>
            {
              Logger.debug("Pushing all")
              messages.foreach(msg => {
                Logger.debug("Pushing: " + msg)
                pushee.push(msg)
              })
              pushee.eofAndEnd
            }
        },
        onComplete = {
          Logger.debug("Done with pushee")
          //            inbox.close(true)
          //            store.close()

        },
        onError = {
          (msg, in) => Logger.error(msg)
        })
      val db = context.actorOf(Props[MailMetaDao])
      val iter = Iteratee.foreach[Message] { msg =>
        Logger.debug("sending msg to db")
        db ! (user, msg)
      }
      emailFeed |>>> iter

    }
    case _ => Logger.error("Unknown message")
  }
}