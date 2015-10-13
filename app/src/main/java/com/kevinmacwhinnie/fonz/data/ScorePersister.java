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

package com.kevinmacwhinnie.fonz.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.kevinmacwhinnie.fonz.async.Task;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class ScorePersister {
    private static final String STORAGE_DIR_NAME = "Scores";
    private static final String STORAGE_FILE_NAME = "Storage.tsv";
    private static final char SEPARATOR = '\t';

    private final Context context;
    private final Handler callbackHandler = new Handler(Looper.getMainLooper());

    public ScorePersister(@NonNull Context context) {
        this.context = context;
    }

    File getStorageLocation() {
        return new File(context.getDir(STORAGE_DIR_NAME, Context.MODE_PRIVATE),
                        STORAGE_FILE_NAME);
    }

    List<Scores.Entry> readAll(@NonNull Reader source) throws IOException {
        try (final CSVReader reader = new CSVReader(source, SEPARATOR)) {
            final List<Scores.Entry> accumulator = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {
                final String name = row[0];
                final int score = Integer.parseInt(row[1], 10);
                final long timestamp = Long.parseLong(row[2], 10);
                accumulator.add(new Scores.Entry(name, score, timestamp));
            }
            return accumulator;
        }
    }

    void writeAll(@NonNull List<Scores.Entry> entries, @NonNull Writer destination) throws IOException {
        try (final CSVWriter writer = new CSVWriter(destination, SEPARATOR)) {
            for (final Scores.Entry entry : entries) {
                final String[] row = {
                        entry.name,
                        Integer.toString(entry.score, 10),
                        Long.toString(entry.timestamp, 10),
                };
                writer.writeNext(row);
            }
        }
    }

    public Task<List<Scores.Entry>> read() {
        return new Task<List<Scores.Entry>>(callbackHandler) {
            @Override
            protected List<Scores.Entry> onRun() throws Throwable {
                final File storage = getStorageLocation();
                final FileReader reader = new FileReader(storage);
                return readAll(reader);
            }
        };
    }

    public Task<Void> write(@NonNull final List<Scores.Entry> entries) {
        return new Task<Void>(callbackHandler) {
            @Override
            protected Void onRun() throws Throwable {
                final File storage = getStorageLocation();
                final FileWriter writer = new FileWriter(storage);
                writeAll(entries, writer);
                return null;
            }
        };
    }
}
