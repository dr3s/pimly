package controllers

import play.api._
import play.api.mvc._
import mail.ImapOAuth
import javax.mail.Folder
import javax.mail.NoSuchProviderException
import javax.mail.MessagingException

object Application extends Controller with securesocial.core.SecureSocial {

  def index = SecuredAction() { implicit request =>
    val store = ImapOAuth.connect("imap.gmail.com",
      993,
      request.user.email.get,
      request.user.oAuth2Info.get.accessToken,
      true)

    val inbox = store.getFolder("Inbox")
    try {
      inbox.open(Folder.READ_ONLY)

      // limit this to 20 message during testing
      val limit = 20
      val messages = inbox.getMessages(1, limit)
      for (message <- messages) {

        println(message.getSubject())
      }

      Ok(views.html.index("You have %d total messages".format(inbox.getMessageCount())))
    } finally {

      inbox.close(true)
      store.close()
    }

  }

}