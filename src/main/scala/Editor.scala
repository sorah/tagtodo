package org.ajunk.android.tagtodo

import android.app.Activity
import android.os.Bundle
import android.content.Intent

import android.view.View

import android.widget.EditText
import android.widget.Button

import android.content.SharedPreferences

class Editor extends TypedActivity {
  import ConverterHelper._

  var tag_id:String = null

  var edit_id:EditText = null
  var edit_url:EditText = null
  var edit_desc:EditText = null
  var button_save:Button = null

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.edit)

    val intent = getIntent()
    tag_id = intent.getStringExtra("TagtodoTagId")

    this.edit_id = findView(TR.id_input)
    this.edit_url = findView(TR.url_input)
    this.edit_desc = findView(TR.description)
    this.button_save = findView(TR.save_button)

    this.button_save.setOnClickListener((v: View) => {
      val editor = getSharedPreferences("tagtodo", 0).edit()
      val tag = edit_id.getText()

      editor.putString(tag+"-url", this.edit_url.getText().toString())
      editor.putString(tag+"-desc", this.edit_desc.getText().toString())
      editor.commit()

      finish()
    })
  }

  override def onResume() {
    super.onResume()

    val store:SharedPreferences = getSharedPreferences("tagtodo", 0)
    this.edit_id.setText(tag_id)
    this.edit_url.setText(store.getString(tag_id+"-url",""))
    this.edit_desc.setText(store.getString(tag_id+"-desc",""))
  }
}
