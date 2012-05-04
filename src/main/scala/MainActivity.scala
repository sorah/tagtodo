package org.ajunk.android.tagtodo

import collection.JavaConversions._

import android.app.Activity
import android.os.Bundle

import android.nfc.NfcAdapter
import android.nfc.Tag

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter

import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.content.DialogInterface

import android.widget.ListView
import android.widget.TextView
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.content.Context
import android.view.View
import android.view.LayoutInflater

import android.util.Log

import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight


class MainActivity extends TypedActivity {
  import ConverterHelper._

  var nfc_adapter:NfcAdapter = null
  var nfc_filters:Array[IntentFilter] = null
  var nfc_pending_intent:PendingIntent = null
  var nfc_techs:Array[Array[String]] = null

  var list:ListView = null

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    this.nfc_adapter = NfcAdapter.getDefaultAdapter(this)
    this.nfc_filters = Array(new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED));
    this.nfc_pending_intent = PendingIntent.getActivity(
      this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    this.nfc_techs = Array(Array(classOf[IsoDep].getName()),
                           Array(classOf[NfcA].getName()),
                           Array(classOf[NfcB].getName()),
                           Array(classOf[NfcF].getName()),
                           Array(classOf[NfcV].getName()),
                           Array(classOf[Ndef].getName()),
                           Array(classOf[NdefFormatable].getName()),
                           Array(classOf[MifareClassic].getName()),
                           Array(classOf[MifareUltralight].getName()))

    setContentView(R.layout.main)

    this.list = findView(TR.list)
  }

  override def onResume() {
    super.onResume()

    if (this.nfc_adapter != null)
      this.nfc_adapter.enableForegroundDispatch(this, nfc_pending_intent, nfc_filters, nfc_techs)

    update_list()
  }

  override def onPause() {
    super.onPause()

    if (this.nfc_adapter != null)
      this.nfc_adapter.disableForegroundDispatch(this)
  }

  def start_editor(tag_id:String) {
    val editor:Intent = new Intent(this, classOf[Editor])
    editor.putExtra("TagtodoTagId", tag_id)
    startActivity(editor)
  }

  override def onNewIntent(intent:Intent) {
    if (intent.getAction() != NfcAdapter.ACTION_TECH_DISCOVERED) return

    Log.i("Tagtodo", "TECH_DISCOVERED!" + intent)

    val tag:Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
    val tag_id = tag.getId().map("%02X" format _).mkString

    start_editor(tag_id)
  }

  def update_list() {
    val settings = getSharedPreferences("tagtodo", 0).getAll()

    val tags = settings.filter( _._1 contains "-desc" ).toList
    val tags_titles:Array[String] = tags.map( _._2 toString ).toArray

    Log.i("Tagtodo", "Tags:" + tags_titles.mkString(" "))
    val adapter = new ArrayAdapter[String](this, R.layout.main_list, tags_titles)

    this.list.setAdapter(adapter)

    this.list.setOnItemClickListener( (adapter:AdapterView[_], view:View, pos:Int, id:Long) => {
      start_editor(tags(pos)._1.replaceAll("-desc$",""))
    })
    this.list.setOnItemLongClickListener( (adapter:AdapterView[_], view:View, pos:Int, id:Long) => {
      val tag = tags(pos)._1.replaceAll("-desc$","")
      val builder = new AlertDialog.Builder(this)
      builder.setMessage("Delete?")
             .setCancelable(true)
             .setPositiveButton("Yes", (dialog: DialogInterface) => {
                val editor = getSharedPreferences("tagtodo", 0).edit()
                Log.i("Tagtodo", "Remove "+tag+"-url")
                Log.i("Tagtodo", "Remove "+tag+"-desc")
                editor.remove(tag+"-url")
                editor.remove(tag+"-desc")
                editor.commit()

                dialog.cancel()
                update_list()
              })
              .setNegativeButton("No", (dialog: DialogInterface) => {
                dialog.cancel()
              })
      builder.create().show()
      true
    })
  }
}
