package com.cdelg4do.madridguide.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.cdelg4do.madridguide.R;

import static com.cdelg4do.madridguide.util.Utils.MessageType.DIALOG;
import static com.cdelg4do.madridguide.util.Utils.MessageType.NONE;
import static com.cdelg4do.madridguide.util.Utils.MessageType.SNACK;
import static com.cdelg4do.madridguide.util.Utils.MessageType.TOAST;


/**
 * This class provides useful common auxiliary functions.
 * This class is abstract, and all its methods are static.
 */
public abstract class Utils {

    public static enum MessageType {

        DIALOG,
        TOAST,
        SNACK,
        NONE
    }


    // Shows the user a message of the given type (toast, snackbar, dialog or none)
    // If the type is dialog, a title must be provided.
    public static void showMessage(Context ctx, String msg, MessageType type, String title) {

        if ( type==NONE ) {
            return;
        }

        else if ( type==TOAST ) {
            Toast.makeText(ctx,msg, Toast.LENGTH_LONG).show();
        }

        else if ( type==SNACK ) {
            View rootView = ( (Activity)ctx ).getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView, msg, Snackbar. LENGTH_LONG).show();
        }

        else if ( type==DIALOG ) {

            AlertDialog dialog = new AlertDialog.Builder(ctx).create();
            dialog.setTitle(title);
            dialog.setMessage(msg);

            // OK buton
            dialog.setButton(
                    ctx.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }
            );

            dialog.show();
        }
    }


    public static ProgressDialog newProgressDialog(Context ctx, String msg) {

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setTitle( ctx.getString(R.string.defaultProgressTitle) );
        pDialog.setMessage(msg);
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);

        return pDialog;
    }
}
