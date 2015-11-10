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

package com.kevinmacwhinnie.fonz.view;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;

import com.kevinmacwhinnie.fonz.R;
import com.kevinmacwhinnie.fonz.achievements.Achievement;
import com.kevinmacwhinnie.fonz.view.util.Drawing;

public class AchievementHeadsUp {
    public static void show(@NonNull View from, @NonNull Achievement achievement) {
        final Resources resources = from.getResources();

        final String rawMessage = resources.getString(R.string.achievement_unlocked_template);
        final SpannableStringBuilder message = new SpannableStringBuilder(rawMessage);

        final int nameStart = TextUtils.indexOf(message, '{');
        final int nameEnd = TextUtils.indexOf(message, '}') + 1;
        message.setSpan(new StyleSpan(Typeface.BOLD),
                        nameStart, nameEnd,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        final String achievementName = resources.getString(achievement.name);
        message.replace(nameStart, nameEnd, achievementName);

        Snackbar.make(from, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_dismiss, new View.OnClickListener() {
                    @Override
                    public void onClick(View sender) {
                        // Do nothing.
                    }
                })
                .setActionTextColor(Drawing.getColor(resources, R.color.color_accent))
                .show();
    }
}
