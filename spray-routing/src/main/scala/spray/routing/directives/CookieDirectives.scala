/*
 * Copyright (C) 2011-2013 spray.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spray.routing
package directives

import spray.util._
import spray.http._
import HttpHeaders._

trait CookieDirectives {
  import RouteDirectives._
  import HeaderDirectives._
  import RespondWithDirectives._

  /**
   * Extracts an HttpCookie with the given name. If the cookie is not present the
   * request is rejected with a respective [[spray.routing.MissingCookieRejection]].
   */
  def cookie(name: String): Directive1[HttpCookie] =
    headerValue(findCookie(name)) | reject(MissingCookieRejection(name))

  /**
   * Extracts an HttpCookie with the given name.
   * If the cookie is not present a value of `None` is extracted.
   */
  def optionalCookie(name: String): Directive1[Option[HttpCookie]] =
    optionalHeaderValue(findCookie(name))

  private def findCookie(name: String): HttpHeader ⇒ Option[HttpCookie] = {
    case Cookie(cookies) ⇒ cookies.find(_.name == name)
    case _               ⇒ None
  }

  /**
   * Adds a Set-Cookie header with the given cookies to all responses of its inner route.
   */
  def setCookie(first: HttpCookie, more: HttpCookie*): Directive0 =
    respondWithHeaders((first :: more.toList).map(`Set-Cookie`(_)))

  /**
   * Adds a Set-Cookie header expiring the given cookies to all responses of its inner route.
   */
  def deleteCookie(first: HttpCookie, more: HttpCookie*): Directive0 =
    respondWithHeaders((first :: more.toList).map { c ⇒
      `Set-Cookie`(c.copy(content = "deleted", expires = Some(DateTime.MinValue)))
    })

  /**
   * Adds a Set-Cookie header expiring the given cookie to all responses of its inner route.
   */
  def deleteCookie(name: String, domain: String = "", path: String = ""): Directive0 =
    deleteCookie(HttpCookie(name, "", domain = domain.toOption, path = path.toOption))

}

object CookieDirectives extends CookieDirectives
