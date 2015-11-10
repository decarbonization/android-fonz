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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kevinmacwhinnie.fonz.data.Formatting;
import com.kevinmacwhinnie.fonz.data.Preferences;
import com.kevinmacwhinnie.fonz.events.BaseValueEvent;
import com.kevinmacwhinnie.fonz.graph.Injection;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class HighScoreDialogFragment extends AppCompatDialogFragment
        implements Dialog.OnClickListener, TextView.OnEditorActionListener {
    public static final String TAG = HighScoreDialogFragment.class.getSimpleName();
    public static final String ARG_SCORE = HighScoreDialogFragment.class.getName() + ".ARG_SCORE";

    @Inject Bus bus;
    @Inject Preferences preferences;
    @Inject Formatting formatting;

    private int score;
    private EditText nameText;


    //region Lifecycle

    public static HighScoreDialogFragment newInstance(int score) {
        final HighScoreDialogFragment fragment = new HighScoreDialogFragment();

        final Bundle arguments = new Bundle();
        arguments.putInt(ARG_SCORE, score);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injection.inject(getActivity(), this);

        this.score = getArguments().getInt(ARG_SCORE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_game_over_title);
        builder.setMessage(getString(R.string.alert_game_over_message_fmt,
                                     formatting.formatScore(score)));
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(R.string.action_no_thanks, this);

        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(R.layout.dialog_game_over, null, false);
        this.nameText = (EditText) view.findViewById(R.id.dialog_game_over_name);
        nameText.setOnEditorActionListener(this);
        nameText.setText(preferences.getSavedScoreName());

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

    //endregion


    //region Events

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            final String name = nameText.getText().toString();
            preferences.setSavedScoreName(name);
            bus.post(new NameSubmitted(name));
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

    //endregion


    public static class NameSubmitted extends BaseValueEvent<String> {
        public NameSubmitted(@NonNull String value) {
            super(value);
        }
    }
}
