package org.ajunk.android.tagtodo

object ConverterHelper {
  import android.view.View.OnClickListener
  import android.view.View
  import android.widget.AdapterView
  import android.widget.AdapterView.OnItemClickListener
  import android.widget.AdapterView.OnItemLongClickListener
  import android.content.DialogInterface

  implicit def funcToClicker(f: View => Unit):OnClickListener =
    new OnClickListener() { def onClick(v:View) = f.apply(v)}

  implicit def funcToClicker0(f: () => Unit):OnClickListener =
    new OnClickListener() { def onClick(v:View) = f.apply }

  implicit def funcToItemClicker(f: (AdapterView[_], View, Int, Long) => Unit):OnItemClickListener =
    new OnItemClickListener() { def onItemClick(a:AdapterView[_], v:View, p:Int, i:Long) = f.apply(a,v,p,i) }
  implicit def funcToItemLongClicker(f: (AdapterView[_], View, Int, Long) => Boolean):OnItemLongClickListener =
    new OnItemLongClickListener() { def onItemLongClick(a:AdapterView[_], v:View, p:Int, i:Long):Boolean = f.apply(a,v,p,i) }

  implicit def funcToDialogClicker(f: (DialogInterface) => Unit):DialogInterface.OnClickListener =
    new DialogInterface.OnClickListener() { def onClick(dialog:DialogInterface, id:Int) = f.apply(dialog) }
}
