package com.gmail.naetmul.copytoclipboard

/**
 * Created by Haneul on 2014-08-01.
 */
sealed trait DataType
case object PlainTextType extends DataType
case object HtmlTextType extends DataType
case object UriType extends DataType
case object IntentType extends DataType