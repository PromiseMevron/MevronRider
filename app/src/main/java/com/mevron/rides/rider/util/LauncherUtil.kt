package com.mevron.rides.rider.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.mevron.rides.rider.R

object LauncherUtil {

    fun showSnackbar(view: View, msg: String?, btnText: String? = null, action:() -> Unit = {}) {
        try{
            val message = msg ?: "No message specified"
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            snackbar.setAction(btnText ?: "Close") {
                action()
                snackbar.dismiss()
            }
            snackbar.show()
        }catch (ex: Exception){
            ex.printStackTrace()
        }
    }

    fun showSnackbarIndefinite(context: Context, msg: String, title: String, action: () -> Unit,
                               rootView: View
    ){
        Snackbar.make(rootView,msg, Snackbar.LENGTH_INDEFINITE)
            .apply {
                setAction(title) {
                    action()
                    dismiss()
                }
                setActionTextColor(ContextCompat.getColor(context,R.color.black))
                show()
            }
    }


    fun toast(context: Context, msg: String){
        try{
            Toast.makeText(context,msg, Toast.LENGTH_SHORT).show()
        }catch (ex: Exception){
            ex.printStackTrace()
        }

    }

    fun longToast(context: Context, msg: String){
        Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
    }

    fun showPopUp(context: Context, view: View, desc: String? = null): Dialog {
        val builder = AlertDialog.Builder(context)
        if(desc != null){
            val tv = view.findViewById<TextView>(R.id.desc_tv)
            tv?.text = desc
        }
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun showPopUpSoft(context: Context, title: String, desc: String, nString: String): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(desc)
        builder.setCancelable(true)
        builder.setNegativeButton(nString){dialog, _ ->
            dialog.dismiss()

        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    fun showPopUp(context: Context, title: String, desc: String?, positive: String,
                  negative: String, pAction:() -> Unit, nAction:() -> Unit){
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setTitle(title)
        builder.setMessage(desc)
        builder.setPositiveButton(positive
        ) { dialog, _ -> dialog.dismiss()
            pAction()
        }
        builder.setNegativeButton(negative
        ) { dialog, _ ->
            nAction()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun showPopUpSoft(context: Context, title: String, desc: String?, positive: String,
                      pAction:() -> Unit){
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(desc)
        builder.setPositiveButton(positive
        ) { dialog, _ -> dialog.dismiss()
            pAction()
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    fun showPopUpSoft(context: Context, view: View, transparent: Boolean = false): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(true)
        if(transparent)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun showPopUp(context: Context, title: String, desc: String?, positive: String,
                  negative: String, neutral: String, pAction:() -> Unit, nAction:() -> Unit,
                  qAction:() -> Unit){
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setTitle(title)
        builder.setMessage(desc)
        builder.setPositiveButton(positive
        ) { dialog, _ -> dialog.dismiss()
            pAction()
        }
        builder.setNegativeButton(negative
        ) { dialog, _ ->
            nAction()
            dialog.dismiss()
        }
        builder.setNeutralButton(neutral){dialog, _ ->
            qAction()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun showPopUp(context: Context, title: String, desc: String?, positive: String,
                  pAction:() -> Unit){
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setTitle(title)
        builder.setMessage(desc)
        builder.setPositiveButton(positive
        ) { dialog, _ -> dialog.dismiss()
            pAction()
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun showPopUp(context: Context, title: String, view: View, pString: String, nString: String): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setView(view)
        builder.setPositiveButton(pString){_,_ ->
            //pAction(dialog)
        }
        builder.setNegativeButton(nString){_,_ ->
            //dialog.dismiss()
            //nAction()
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }


    fun showFragment(fragMan: FragmentManager, frag: Fragment, containerId: Int, addToBackStack: Boolean) {
        fragMan.inTransaction {
            if (addToBackStack) {
                replace(containerId, frag)
                    .addToBackStack(null)
            } else {
                replace(containerId, frag)
            }
        }
    }

    fun showFragment(fragMan: FragmentManager, frag: Fragment, containerId: Int, tag: String?) {
        fragMan.inTransaction {
            if(fragMan.backStackEntryCount > 0)
                replace(containerId, frag).addToBackStack(tag)
            else
                add(containerId,frag)
        }
    }


    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val trans = beginTransaction()
        trans.setCustomAnimations(
            R.animator.slide_up_animator, R.animator.slide_down_animator,
            R.animator.slide_up_animator, R.animator.slide_down_animator)
        trans.func()
        //trans.commitAllowingStateLoss()
        trans.commit()
    }
}