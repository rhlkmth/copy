package com.gmail.naetmul.copytoclipboard

import android.app.Activity
import android.content.{ClipData, ClipboardManager, Context, Intent}
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

/**
 * Created by Haneul on 2014-08-01.
 */
class ReceiverActivity extends Activity {
  override protected def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    val intent = getIntent
    val action = intent.getAction

    action match {
      case Intent.ACTION_SEND =>
        processActionSend(intent)
      case Intent.ACTION_SEND_MULTIPLE =>
      /* Not yet supported.
       * To support this, add this intent-filter to AndroidManifest.
       */
    }

    /* No UI */
    finish()
  }

  private def processActionSend(intent: Intent): Unit = {
    val mimeType = intent.getType

    mimeType match {
      case "text/plain" =>
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (text != null) {
          copyPlainText(text)
          notifySuccess(PlainTextType)
        } else {
          notifyFailure()
        }

      case "text/html" =>
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        val htmlText = intent.getStringExtra(Intent.EXTRA_HTML_TEXT)

        if (text != null && htmlText != null) {
          copyHtmlText(text, htmlText)
          notifySuccess(HtmlTextType)
        } else if (text != null) {
          copyPlainText(text)
          notifySuccess(PlainTextType)
        } else {
          notifyFailure()
        }

      case t if t.startsWith("text/") =>
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (text != null) {
          copyPlainText(text)
          notifySuccess(PlainTextType)
        } else {
          notifyFailure()
        }

      case _ =>
        notifyFailure()
    }
  }

  private def copyPlainText(text: String): Unit = {
    val clip = ClipData.newPlainText("Copy to Clipboard - PlainText", text)
    copyToClipboard(clip)
  }

  private def copyHtmlText(text: String, htmlText: String): Unit = {
    val clip = ClipData.newHtmlText("Copy to Clipboard - HtmlText", text, htmlText)
    copyToClipboard(clip)
  }

  /* Defined for future uses */
  private def copyUri(uri: Uri): Unit = {
    val clip = ClipData.newUri(getContentResolver, "Copy to Clipboard - URI", uri)
    copyToClipboard(clip)
  }

  /* Defined for future uses */
  private def copyIntent(intent: Intent): Unit = {
    val clip = ClipData.newIntent("Copy to Clipboard - Intent", intent)
    copyToClipboard(clip)
  }

  private def copyToClipboard(clip: ClipData): Unit = {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]
    clipboard.setPrimaryClip(clip)
  }

  private def notifySuccess(dataType: DataType): Unit = {
    val resId = dataType match {
      case PlainTextType => R.string.copy_succeeded_plain_text
      case HtmlTextType => R.string.copy_succeeded_html_text
      case UriType => R.string.copy_succeeded_uri
      case IntentType => R.string.copy_succeeded_intent
    }
    Toast.makeText(getApplicationContext, resId, Toast.LENGTH_SHORT).show()
  }

  private def notifyFailure(): Unit = {
    Toast.makeText(getApplicationContext, R.string.copy_failed, Toast.LENGTH_SHORT).show()
  }
}