package org.time2java.tRussianBank

import java.io._
import java.util.Arrays

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.api.services.sheets.v4.{Sheets, SheetsScopes}
import com.typesafe.config.{Config, ConfigFactory}

object NGA {
  val conf: Config = ConfigFactory.load
  val APPLICATION_NAME: String = "tExample"
  val DATA_STORE_DIR: File = new File("./")
  val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance
  val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
  val DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR)
  val SCOPES: java.util.List[String] = Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY)

  def authorize: Credential = {
    val in: InputStream = new FileInputStream(new File("client_secret.json"))
    val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in))
    val flow: GoogleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
      .setDataStoreFactory(DATA_STORE_FACTORY)
      .setAccessType("offline").build

    val credential: Credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver).authorize("user")
    System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath)
    credential
  }

  def getSheetsService: Sheets = {
    val credential: Credential = authorize
    new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build
  }

  def getStatus(): String = {
    val service: Sheets = getSheetsService
    val range: String = "A5:B7"
    val response: ValueRange = service.spreadsheets.values.get(conf.getString("docId"), range).execute

    import collection.JavaConverters._
    val values = response.getValues.asScala.map(_.asScala.toList.map(_.toString)).toList

    val result: StringBuilder = new StringBuilder
    for (iter1 <- values) {
      for (iter2 <- iter1) {
        result.append(iter2)
        result.append(" ")
      }
      result.append("\n")
    }

    var indexR: Int = result.indexOf("\u20BD")
    while (indexR != -1) {
      result.setCharAt(indexR, 'р')
      indexR = result.indexOf("\u20BD")
    }

    result.toString
  }
}