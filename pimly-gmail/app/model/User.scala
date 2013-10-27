package model

import securesocial.core._
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import securesocial.core.PasswordInfo
import scala.Some
import securesocial.core.OAuth2Info
import securesocial.core.OAuth1Info

@Entity
class User() extends securesocial.core.Identity {

  def this(other: Identity) = {
    this()
    emailV = other.email.get
    firstNameV = other.firstName
    fullNameV = other.fullName
    userIdV = other.identityId.userId
    providerIdV = other.identityId.providerId
    idV = providerIdV + "-" + userIdV
    lastNameV = other.lastName
    oAuth2Token = other.oAuth2Info.get.accessToken
  }

  var authMethodV: AuthenticationMethod = null
  var avatarUrlV: Option[String] = None
  @Column var emailV: String =  null
  @Column var firstNameV: String = null
  @Column var fullNameV: String = null
  @Id var idV: String = null
  @Column var userIdV: String = null
  @Column var providerIdV: String = null
  @Column var lastNameV: String = null
  var oAuth1InfoV: Option[OAuth1Info] = None
  @Column var oAuth2Token: String = null
  var passwordInfoV: Option[PasswordInfo] = None

  def authMethod: AuthenticationMethod = authMethodV
  def avatarUrl: Option[String] = avatarUrlV
  def email: Option[String] = Some(emailV)
  def firstName: String = firstNameV
  def fullName: String = fullNameV
  def identityId: IdentityId = new IdentityId(userIdV, providerIdV)
  def userId: String = userIdV
  def providerId: String = providerIdV
  def lastName: String = lastNameV
  def oAuth1Info: Option[OAuth1Info] = oAuth1InfoV
  def oAuth2Info: Option[OAuth2Info] =  Some(OAuth2Info(oAuth2Token, None, null, null))
  def passwordInfo: Option[PasswordInfo] = passwordInfoV
  
   def this(id: IdentityId) = {
    this()
    userIdV = id.userId
    providerIdV = id.providerId
    idV = providerIdV + "-" + userIdV
  }

}