/* Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.security.{ PrivilegedAction, AccessController, Provider, Security }

/**
 * Performs OAuth2 authentication.
 *
 * <p>Before using this class, you must call {@code initialize} to install the
 * OAuth2 SASL provider.
 */
trait ImapOAuth {
  private val logger = Logger.getLogger(getClass.getName)
  private val serialVersionUID = 1L;
  
  
}

object ImapOAuth  {
  
	Security addProvider(SaslProvider)

  /**
   * Connects and authenticates to an IMAP server with OAuth2. You must have
   * called {@code initialize}.
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
    userEmail: String,
    oauthToken: String,
    debug: Boolean): IMAPStore = {
    val m = Map("mail.imaps.sasl.enable" -> "true",
      "mail.imaps.sasl.mechanisms" -> "XOAUTH2",
      OAUTH_SASL_IMAPS_TOKEN_PROP -> oauthToken)
    val p = new Properties();
    m.foreach { case (k, v) => p.put(k, v) }
    val session = Session.getInstance(p)
    session.setDebug(debug)

    val store = new IMAPSSLStore(session, null)
    store.connect(host, port, userEmail, "")
    return store
  }

}

object SaslProvider extends Provider("OAuth2 Provider", 1.0,
      "Provides the XOAUTH2 SASL Mechanism") {
	  AccessController.doPrivileged(new PrivilegedAction[this.type] {
	    def run = {
	       put("SaslClientFactory.XOAUTH2",
	    		   "security.OAuth2SaslClientFactory")
	      null //Magic null is magic
	    }
	  })
	}
