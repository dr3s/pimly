

package security
import java.util.logging.Logger
import javax.security.auth.callback.CallbackHandler
import javax.security.sasl.SaslClient
import javax.security.auth.callback.NameCallback
import javax.security.auth.callback.Callback
import javax.security.sasl.SaslException
import javax.security.auth.callback.UnsupportedCallbackException
import java.io.IOException

/**
 * An OAuth2 implementation of SaslClient.
 */
class OAuth2SaslClient(oauthToken: String, callbackHandler: CallbackHandler) extends SaslClient {
  private val logger = Logger.getLogger(getClass.getName)

  var isComplete = false;

  def getMechanismName() = "XOAUTH2"
  def hasInitialResponse() = true

  @throws(classOf[SaslException])
  def evaluateChallenge(challenge: Array[Byte]): Array[Byte] = {
    if (isComplete) {
      // Empty final response from server, just ignore it.
      return Array[Byte]()
    }

    val nameCallback = new NameCallback("Enter name")
    val callbacks = Array[Callback](nameCallback)
    try {
      callbackHandler.handle(callbacks);
    } catch {
      case e: UnsupportedCallbackException => throw new SaslException("Unsupported callback: " + e)
      case e: IOException => throw new SaslException("Failed to execute callback: " + e)
    }
    val email = nameCallback.getName()

    val response = "user=%s\1auth=Bearer %s\1\1".format(email, oauthToken).getBytes
    isComplete = true
    return response
  }

  @throws(classOf[SaslException])
  def unwrap(incoming: Array[Byte], offset: Int, len: Int): Array[Byte] = {
    throw new IllegalStateException()
  }

  @throws(classOf[SaslException])
  def wrap(outgoing: Array[Byte], offset: Int, len: Int): Array[Byte] = {
    throw new IllegalStateException()
  }

  def getNegotiatedProperty(propName: String): Object = {
    if (!isComplete) {
      throw new IllegalStateException()
    }
    return null
  }

  @throws(classOf[SaslException])
  def dispose() = {}

}
