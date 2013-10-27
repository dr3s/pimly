package mail;

import com.sun.mail.imap.IMAPStore
import com.sun.mail.imap.IMAPSSLStore
import com.sun.mail.smtp.SMTPTransport
import java.security.Provider
import java.security.Security
import java.util.Properties
import java.util.logging.Logger
import javax.mail.Session
import javax.mail.URLName
import security.OAuth2SaslClientFactory._
import com.sun.mail.gimap.GmailStore
import com.sun.mail.gimap.GmailSSLStore
import securesocial.core.OAuth2Info

/**
 * Performs OAuth2 authentication.
 *
 * <p>Before using this class, you must call {@code initialize} to install the
 * OAuth2 SASL provider.
 */

object ImapOAuth  {
  private val logger = Logger.getLogger(getClass.getName)
  private val serialVersionUID = 1L;
  
  

  /**
   * Connects and authenticates to an IMAP server with OAuth2. 
   *
   * @param host Hostname of the imap server, for example {@code
   *     imap.googlemail.com}.
   * @param port Port of the imap server, for example 993.
   * @param userEmail Email address of the user to authenticate, for example
   *     {@code oauth@gmail.com}.
   * @param oauthToken The user's OAuth token.
   * @param debug Whether to enable debug logging on the IMAP connection.
   *
   * @return An authenticated IMAPStore that can be used for IMAP operations.
   */
  def connect(host: String,
    port: Int,
    userEmail: Option[String],
    oAuth2Info: Option[OAuth2Info],
    debug: Boolean): GmailStore = {
    val m = Map("mail.gimaps.sasl.enable" -> "true",
      "mail.gimaps.sasl.mechanisms" -> "XOAUTH2",
      OAUTH_SASL_IMAPS_TOKEN_PROP -> oAuth2Info.get.accessToken)
    val p = new Properties();
    m.foreach { case (k, v) => p.put(k, v) }
    val session = Session.getInstance(p)
    session.setDebug(debug)
    val store = new GmailSSLStore(session, null)
    store.connect(host, port, userEmail.get, "")
    return store
  }

}


