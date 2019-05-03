package com.softwaremill.sttp

import java.nio.charset.StandardCharsets

import org.scalatest.{FlatSpec, Matchers}

class ToCurlConverterTest extends FlatSpec with Matchers {

  it should "convert base request" in {
    sttp
      .get(uri"http://localhost")
      .toCurl shouldBe """curl -L --max-redirs=32 -X GET -H "Accept-Encoding: gzip, deflate" http://localhost"""
  }

  it should "convert request with method to curl" in {
    sttp.get(uri"http://localhost").toCurl should include("-X GET")
    sttp.post(uri"http://localhost").toCurl should include("-X POST")
    sttp.put(uri"http://localhost").toCurl should include("-X PUT")
    sttp.delete(uri"http://localhost").toCurl should include("-X DELETE")
    sttp.patch(uri"http://localhost").toCurl should include("-X PATCH")
    sttp.head(uri"http://localhost").toCurl should include("-X HEAD")
    sttp.options(uri"http://localhost").toCurl should include("-X OPTIONS")
  }

  it should "convert request with header" in {
    sttp.header("User-Agent", "myapp").get(uri"http://localhost").toCurl should include(
      """-H "User-Agent: myapp""""
    )
  }

  it should "convert request with body" in {
    sttp.body(Map("name" -> "john", "org" -> "sml")).post(uri"http://localhost").toCurl should include(
      """-H "Content-Type: application/x-www-form-urlencoded" -H "Content-Length: 17" -F 'name=john&org=sml'"""
    )
    sttp.body("name=john").post(uri"http://localhost").toCurl should include(
      """-H "Content-Type: text/plain; charset=utf-8" -H "Content-Length: 9" --data 'name=john'"""
    )
    sttp.body("name=john", StandardCharsets.ISO_8859_1.name()).post(uri"http://localhost").toCurl should include(
      """ -H "Content-Type: text/plain; charset=ISO-8859-1" -H "Content-Length: 9" --data 'name=john'"""
    )
//    ToCurlConverter(sttp.body(Entity("e1")).post(uri"http://localhost")) should include(
//      """-H "Content-Type: application/json; charset=utf-8" --data '{"name":"e1"}'""")
  }
  case class Entity(name: String)

  it should "convert request with options" in {
    sttp.followRedirects(false).get(uri"http://localhost").toCurl should not include "-L"
    sttp.maxRedirects(11).get(uri"http://localhost").toCurl should include("--max-redirs=11")
  }
}
