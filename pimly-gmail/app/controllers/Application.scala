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
import service.InboxAuth._
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits._

object Application extends Controller with securesocial.core.SecureSocial {

  
    val inboxSlurper = Akka.system.actorOf(Props[InboxSlurper], name = "inboxSlurper")
    
  def index = SecuredAction { implicit request =>
    implicit val timeout = Timeout(10 seconds)
    val f = inboxSlurper.ask(OauthIdentity(
      request.user.email.get,
      request.user.oAuth2Info.get.accessToken))

      Async { 
    	f.map(count => Ok(views.html.index("You have %d total messages".format(count))) )
    }
    

  }

}