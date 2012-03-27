'''
Created on Mar 27, 2012

@author: andres
'''
import base64
import hmac
import imaplib
from optparse import OptionParser
import random
import sha
import smtplib
import sys
import time
import datetime
import urllib
import xoauth
import email

from xoauth import SetupOptionParser, OAuthEntity, GoogleAccountsUrlGenerator,\
    GenerateXOauthString, TestImapAuthentication, TestSmtpAuthentication,\
    GenerateRequestToken, GetAccessToken
    
def TestFetch(imap_hostname, user, xoauth_string):
  """Authenticates to IMAP with the given xoauth_string.

  Prints a debug trace of the attempted IMAP connection.

  Args:
    imap_hostname: Hostname or IP address of the IMAP service.
    user: The Google Mail username (full email address)
    xoauth_string: A valid XOAUTH string, as returned by GenerateXOauthString.
        Must not be base64-encoded, since IMAPLIB does its own base64-encoding.
  """
  print
  imap_conn = imaplib.IMAP4_SSL(imap_hostname)
  imap_conn.debug = 4
  imap_conn.authenticate('XOAUTH', lambda x: xoauth_string)
  imap_conn.select('[Gmail]/All Mail', True)
#  result, data = imap_conn.uid('search', None, "ALL") # search and return uids instead
  date = (datetime.date.today() - datetime.timedelta(1)).strftime("%d-%b-%Y")
  result, data = imap_conn.uid('search', None, '(SENTSINCE {date})'.format(date=date))
  latest_email_uid = data[0].split()[-1]
  result, data = imap_conn.uid('fetch', latest_email_uid, '(RFC822)')
  
  raw_email = data[0][1]
  email_message = email.message_from_string(raw_email)
  print email.Utils.parseaddr(email_message['From'])
  print email_message.items()


def main(argv):
  options_parser = SetupOptionParser()
  (options, args) = options_parser.parse_args()
  if not options.user:
    options_parser.print_help()
    print "ERROR: --user is required."
    return
  consumer = OAuthEntity(options.consumer_key, options.consumer_secret)
  google_accounts_url_generator = GoogleAccountsUrlGenerator(options.user)
  if (options.generate_xoauth_string or options.test_imap_authentication or
      options.test_smtp_authentication or options.test_fetch):
    if options.test_smtp_authentication:
      options.proto = 'smtp'
    ok_for_3_legged = (options.oauth_token and options.oauth_token_secret)
    ok_for_2_legged = (options.xoauth_requestor_id and
                       options.consumer_secret != "anonymous" and
                       options.consumer_key != "anonymous")
    if not (ok_for_3_legged or ok_for_2_legged):
      options_parser.print_help()
      print 'ERROR: Insufficient parameters.'
      print 'For 3-legged OAuth, supply --oauth_token and --oauth_token_secret.'
      print ('For 2-legged OAuth, supply --consumer_key, --consumer_secret, and'
             '--xoauth_requestor_id')
      return
    access_token = OAuthEntity(options.oauth_token, options.oauth_token_secret)
    xoauth_string = GenerateXOauthString(
        consumer, access_token, options.user, options.proto,
        options.xoauth_requestor_id, options.nonce, options.timestamp)
    print 'XOAUTH string (base64-encoded): %s' % base64.b64encode(xoauth_string)
    if options.test_fetch:
      TestFetch(options.imap_hostname, options.user, xoauth_string)
    if options.test_imap_authentication:
      TestImapAuthentication(options.imap_hostname, options.user, xoauth_string)
    if options.test_smtp_authentication:
      TestSmtpAuthentication(options.smtp_hostname, options.user, xoauth_string)
  elif options.generate_oauth_token:
    request_token = GenerateRequestToken(consumer, options.scope, options.nonce,
                                         options.timestamp,
                                         google_accounts_url_generator)
    oauth_verifier = raw_input('Enter verification code: ').strip()
    access_token = GetAccessToken(consumer, request_token, oauth_verifier,
                                  google_accounts_url_generator)
  else:
    options_parser.print_help()
    print 'Nothing to do, exiting.'
    return

if __name__ == '__main__':
  main(sys.argv)