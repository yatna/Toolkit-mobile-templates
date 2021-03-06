/* Copyright (c) 2012, BuildmLearn Contributors listed at http://buildmlearn.org/people/
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

 * Neither the name of the BuildmLearn nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.buildmlearn.spellingstemplate;

import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SpellingActivity extends ActionBarActivity implements
		TextToSpeech.OnInitListener {
	private TextToSpeech textToSpeech;
	private DataManager mManager;
	private ArrayList<WordModel> mWordList;
	private int count;
	private AlertDialog mAlert;
	private TextView mTv_WordNumber;
	private Button mBtn_Spell, mBtn_Skip;
	private EditText mEt_Spelling;
	private SeekBar mSb_SpeechRate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spelling);
		mBtn_Spell = (Button) findViewById(R.id.btn_ready);

		mBtn_Skip = (Button) findViewById(R.id.btn_skip);
		mSb_SpeechRate = (SeekBar) findViewById(R.id.sb_speech);

		mTv_WordNumber = (TextView) findViewById(R.id.tv_word_number);
		textToSpeech = new TextToSpeech(this, this);
		mManager = DataManager.getInstance();
		mWordList = mManager.getList();
		count = mManager.getActiveWordCount();
		mTv_WordNumber.setText("Word #" + (count + 1) + " of "
				+ mWordList.size());
	}

	public void click(View view) {
		switch (view.getId()) {
		case R.id.btn_skip:
			if (count < mWordList.size() - 1) {

				count++;
				mManager.increaseCount();
				mTv_WordNumber.setText("Word #" + (count + 1) + " of "
						+ mWordList.size());
				mBtn_Spell.setEnabled(false);
				mBtn_Skip.setEnabled(false);
				mBtn_Skip.setTextColor(Color.WHITE);
				mBtn_Spell.setTextColor(Color.WHITE);
			} else {
				Intent resultIntent = new Intent(this, ResultActivity.class);
				startActivity(resultIntent);
				finish();

			}
			break;

		case R.id.btn_speak:
			convertTextToSpeech(mWordList.get(count).getWord());
			mBtn_Spell.setEnabled(true);
			mBtn_Skip.setEnabled(true);
			mBtn_Skip.setTextColor(Color.RED);
			mBtn_Spell.setTextColor(Color.GREEN);
			// mBtn_Spell.setBackgroundColor(Color.GREEN);
			// mBtn_Skip.setBackgroundColor(Color.RED);
			break;
		case R.id.btn_ready:

			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(
					R.layout.dialog_spellinginput, null);
			Builder builder = new Builder(this);
			mAlert = builder.create();
			mAlert.setCancelable(true);
			mAlert.setView(textEntryView, 10, 10, 10, 10);
			if (mAlert != null && !mAlert.isShowing()) {
				mAlert.show();
			}
			mEt_Spelling = (EditText) mAlert.findViewById(R.id.et_spelling);
			Button mBtn_Submit = (Button) mAlert.findViewById(R.id.btn_submit);
			mBtn_Submit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					submit();
				}
			});

			break;

		}

	}

	/**
	 * Releases the resources used by the TextToSpeech engine. It is good
	 * practice for instance to call this method in the onDestroy() method of an
	 * Activity so the TextToSpeech engine can be cleanly stopped.
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		textToSpeech.shutdown();
	}

	/**
	 * Speaks the string using the specified queuing strategy and speech
	 * parameters.
	 */
	private void convertTextToSpeech(String text) {

		float speechRate = getProgressValue(mSb_SpeechRate.getProgress());
		textToSpeech.setSpeechRate(speechRate);
		textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = textToSpeech.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("error", "This Language is not supported");
			}
		} else {
			Log.e("error", "Initilization Failed!");
		}
	}

	public void submit() {
		String input = mEt_Spelling.getText().toString().trim();
		if (input == null || input.length() == 0) {
			Toast.makeText(SpellingActivity.this, "Please enter the spelling",
					Toast.LENGTH_SHORT).show();

		} else {
			mAlert.dismiss();
			boolean isCorrect = false;
			if (mEt_Spelling.getText().toString()
					.equalsIgnoreCase(mWordList.get(count).getWord())) {
				isCorrect = true;
				mManager.incrementCorrect();
			} else {
				mManager.incrementWrong();
			}
			Intent wordInfoIntent = new Intent(SpellingActivity.this,
					WordInfoActivity.class);
			wordInfoIntent.putExtra("result", isCorrect);
			wordInfoIntent.putExtra("index", count);
			wordInfoIntent.putExtra("word", input);

			startActivity(wordInfoIntent);
			finish();
		}
	}

	private float getProgressValue(int percent) {
		float temp = ((float) percent / 100);
		float per = temp * 2;
		return per;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_info) {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					SpellingActivity.this);

			// set title
			alertDialogBuilder.setTitle("About Us");

			// set dialog message
			alertDialogBuilder
					.setMessage(getString(R.string.about_us))
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									dialog.dismiss();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			TextView msg = (TextView) alertDialog
					.findViewById(android.R.id.message);
			Linkify.addLinks(msg, Linkify.WEB_URLS);

			return super.onOptionsItemSelected(item);
		}
		return true;
	}

}
