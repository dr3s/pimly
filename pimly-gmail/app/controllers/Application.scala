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
import javax.mail.Message
import play.api.libs.iteratee._
import scala.concurrent.Await
import scala.concurrent.duration._
import model.User
import securesocial.core.SecureSocial._
import service.InboxAuth.OauthIdentity

object Application extends Controller with securesocial.core.SecureSocial {

  
  val inboxSlurper = Akka.system.actorOf(Props[InboxSlurper], name = "inboxSlurper")
    
  def index = SecuredAction { implicit request =>
    implicit val timeout = Timeout(10 seconds)
    val user:User = request.user.asInstanceOf[User]

    val f = inboxSlurper.ask(

      OauthIdentity(
        user.email.get,
        user.oAuth2Info.get.accessToken)
    )

    Async {
      f.map { reply =>
        val emailFeed = reply.asInstanceOf[Enumerator[Message]]
        val iter =  Iteratee.fold[Message,String] ("") {
          (result, msg) =>   {
             Logger.debug("appending subj")
             result ++ msg.getSubject()
             Logger.debug(" subj appended")
             result
          }
        }
        val eventuallyResult: Future[String] = {
          Iteratee.flatten(emailFeed |>> iter).run
        }
        val s = Await.result(eventuallyResult, 10 seconds)
        Ok(views.html.index("Subject: ".format(s)))
      }
    }
    

  }
    

}