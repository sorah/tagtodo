package org.ajunk.android.tagtodo

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.net.Uri
import android.content.Intent


class Receiver extends Activity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    val intent = getIntent()
    if (intent.getAction() != NfcAdapter.ACTION_TECH_DISCOVERED) return

    val tag:Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
    val tag_id = tag.getId().map("%02X" format _).mkString

    val settings = getSharedPreferences("tagtodo", 0)
    val url = settings.getString(tag_id+"-url", "")
    val desc = settings.getString(tag_id+"-desc", "")

    if (url == "") finish()

    val dialog = ProgressDialog.show(this, "", desc + " " + url, true);

    val open_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
    dialog.dismiss()

    startActivity(open_intent)
  }

  override def onResume() {
    super.onResume()
    finish()
  }
}
