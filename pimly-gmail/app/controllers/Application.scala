package controllers

import play.api._
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.mvc._
import mail.ImapOAuth
import javax.mail.Folder
import javax.mail.NoSuchProviderException
import javax.mail.MessagingException
import javax.mail.FetchProfile
import javax.mail.search.SearchTerm
import com.sun.mail.imap.IMAPMessage
import com.sun.mail.imap.protocol.FetchItem
import javax.mail.FetchProfile.Item
import com.sun.mail.gimap.GmailMessage
import com.sun.mail.gimap.GmailFolder
import akka.actor._
import akka.pattern._
import service.InboxSlurper
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits._
import javax.mail.Message
import play.api.libs.iteratee._
import scala.concurrent.Await
import scala.concurrent.duration._
import model.User
import securesocial.core.SecureSocial._
import securesocial.core.SecuredRequest

object Application extends Controller with securesocial.core.SecureSocial {

  val inboxSlurper = Akka.system.actorOf(Props[InboxSlurper], name = "inboxSlurper")

  def index = SecuredAction {
    implicit request =>
      val user: User = request.user.asInstanceOf[User]

      inboxSlurper ! user

      Ok(views.html.index("ALL DONE"))

  }

}