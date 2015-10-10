/*
 * Copyright (c) 2015, Peter 'Kevin' MacWhinnie
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions may not be sold, nor may they be used in a commercial
 *    product or activity.
 * 2. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 3. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.kevinmacwhinnie.fonz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kevinmacwhinnie.fonz.data.Preferences;

public class GameOverDialogFragment extends AppCompatDialogFragment
        implements Dialog.OnClickListener, TextView.OnEditorActionListener {
    public static String TAG = GameOverDialogFragment.class.getSimpleName();
    public static String RESULT_NAME = GameOverDialogFragment.class.getName() + ".RESULT_NAME";

    private SharedPreferences preferences;
    private EditText nameText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_game_over_title);
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(R.string.action_no_thanks, this);

        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(R.layout.dialog_game_over, null, false);
        this.nameText = (EditText) view.findViewById(R.id.dialog_game_over_name);
        nameText.setOnEditorActionListener(this);

        final String savedName = preferences.getString(Preferences.SAVED_SCORE_NAME,
                                                       getString(R.string.score_name_default));
        nameText.setText(savedName);

        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (nameText.isFocused()) {
            final Context context = nameText.getContext();
            final InputMethodManager imm =
                    (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(nameText.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment == null) {
            return;
        }

        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: {
                final String name = nameText.getText().toString();
                preferences.edit()
                           .putString(Preferences.SAVED_SCORE_NAME, name)
                           .apply();

                final Intent response = new Intent();
                response.putExtra(RESULT_NAME, name);
                targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, response);
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE: {
                targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                break;
            }
            default: {
                Log.w(getClass().getSimpleName(), "Unknown button " + which);
                break;
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        final boolean isAction = (actionId == EditorInfo.IME_ACTION_DONE);
        final boolean isEnter = !isAction && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                event.getAction() == KeyEvent.ACTION_DOWN);
        if (isAction || isEnter) {
            dismiss();
            onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);

            return true;
        }

        return false;
    }
}
