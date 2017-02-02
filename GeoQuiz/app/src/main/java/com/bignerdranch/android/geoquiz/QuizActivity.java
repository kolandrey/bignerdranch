package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String IS_CHEATING = "cheating";
    private boolean mIsCheater;
    private Button mCheatButton;
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private static final String TAG = "QuizActivity";
    public static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private boolean isGivenAnswer = false;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        TextView version = (TextView) findViewById(R.id.version_view_text);

        // Как правильно записать????!!!!
        version.setText(getText(R.string.sdk_version) + String.valueOf(Build.VERSION.SDK_INT));

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mQuestionTextView = (TextView) findViewById(R.id.question_view_text);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(IS_CHEATING);
        }

        updateQuestion();


        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGivenAnswer) {
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    mIsCheater = false;
                    updateQuestion();
                    isGivenAnswer = false;
                } else {
                    giveAnswerToast();
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGivenAnswer) {
                    if (mCurrentIndex == 0) {
                        mCurrentIndex = mQuestionBank.length - 1;
                    } else {
                        mCurrentIndex = --mCurrentIndex;
                    }
                    updateQuestion();
                    isGivenAnswer = false;
                } else {
                    giveAnswerToast();
                }
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].ismAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHEAT && data != null) {
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(IS_CHEATING, mIsCheater);
    }

    private void giveAnswerToast() {
        Toast.makeText(QuizActivity.this, R.string.give_answer, Toast.LENGTH_SHORT).show();
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getmTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].ismAnswerTrue();
        int messageResId;
        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        isGivenAnswer = true;
        Toast.makeText(QuizActivity.this, messageResId, Toast.LENGTH_SHORT).show();
    }
}
