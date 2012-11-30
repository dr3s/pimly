package security;

import java.util.Map;

import java.util.logging.Logger;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;

/**
 * A SaslClientFactory that returns instances of OAuth2SaslClient.
 *
 * <p>Only the "XOAUTH2" mechanism is supported. The {@code callbackHandler} is
 * passed to the OAuth2SaslClient. Other parameters are ignored.
 */
class OAuth2SaslClientFactory extends SaslClientFactory {
  private val logger = Logger.getLogger(getClass.getName)

  def createSaslClient(mechanisms: Array[String],
    authorizationId: String,
    protocol: String,
    serverName: String,
    props: Map[String, _],
    callbackHandler: CallbackHandler): SaslClient = {

    if (mechanisms.contains("XOAUTH2")) {
      new OAuth2SaslClient(props.get(OAuth2SaslClientFactory.OAUTH_SASL_IMAPS_TOKEN_PROP).asInstanceOf[String], callbackHandler)
    } else {
      logger.info("Failed to match any mechanisms")
      null
    }

  }

  def getMechanismNames(props: Map[String, _]) = Array("XOAUTH2")

}

object OAuth2SaslClientFactory {
  val OAUTH_SASL_IMAPS_TOKEN_PROP = "mail.imaps.sasl.mechanisms.oauth2.oauthToken"
}
