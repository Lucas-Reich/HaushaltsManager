package com.example.lucas.haushaltsmanager.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.Tag;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private MultiAutoCompleteTextView mTagAutoCompTxt;
    private List<Tag> mTagsToCreate = new ArrayList<>();
    private List<Tag> mTags = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.test_activity);

        mTags.add(new Tag("eins"));
        mTags.add(new Tag("zwei"));
        mTags.add(new Tag("drei"));
        mTags.add(new Tag("vier"));
        mTags.add(new Tag("fünf"));
        mTags.add(new Tag("sechs"));
        mTags.add(new Tag("sieben"));
        mTags.add(new Tag("acht"));
        mTags.add(new Tag("neun"));
        mTags.add(new Tag("zehn"));


        mTagAutoCompTxt = (MultiAutoCompleteTextView) findViewById(R.id.multiTextView);
        initializeAutComTxtView(mTags);

    }

    /**
     * Methode um die MultiAutocompleteTextView mit der die Tags angezeigt werden zu initialisieren.
     *
     * @param tags Liste von tags aus denen der User auswählen kann
     */
    private void initializeAutComTxtView(List<Tag> tags) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getTagNames(tags));
        mTagAutoCompTxt.setAdapter(adapter);

        mTagAutoCompTxt.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mTagAutoCompTxt.setHint(R.string.hint_tag_input);
        mTagAutoCompTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String input = s.toString();
                if (input.endsWith(","))
                    removeLastCharacterFromInput();

                if (mTagAutoCompTxt.getText().toString().length() == 0)
                    return;

                String tags[] = input.split(",");
                String lastTag = tags[tags.length - 1];

                if (stringContainsText(lastTag) && lastTag.endsWith(" ")) {

                    removeLastCharacter(lastTag);
                    if (tagNotExisting(lastTag)) {

                        mTagsToCreate.add(createTag(lastTag));
                        appendTokenizer();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                //do nothing
            }
        });

        mTagAutoCompTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(v);
                    mTagAutoCompTxt.clearFocus();

                    return true;
                }

                return false;
            }
        });
    }

    /**
     * Methode um das Keyboard zu verstecken.
     *
     * @param view View
     */
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Methode um einen neuen tag zu erstellen.
     *
     * @param tagName Name des Tags.
     * @return Tag
     */
    private Tag createTag(String tagName) {
        return new Tag(tagName);
    }

    /**
     * Methode um zu überprüfen ob der einegegbene Tag bereits existiert.
     *
     * @param tagName Name des zu prüfenden Tags.
     * @return TRUE wenn der Tag bereits existiert, FALSE wenn nicht.
     */
    private boolean tagNotExisting(String tagName) {
        for (Tag tag : mTags) {
            if (tag.getName().equals(tagName))
                return false;
        }

        return true;
    }

    /**
     * Methode um aus einer Liste von Tags die Namen zu extrahieren
     *
     * @param tags Liste der Tags
     * @return Array der Tag Namen
     */
    private String[] getTagNames(List<Tag> tags) {
        String[] tagNames = new String[tags.size()];
        for (Tag tag : tags)
            tagNames[tags.indexOf(tag)] = tag.getName();

        return tagNames;
    }

    /**
     * Methode um einen zusätzlichen Separator einzufügen
     */
    private void appendTokenizer() {

        String userInput = mTagAutoCompTxt.getText().toString();
        userInput = removeLastCharacter(userInput);
        mTagAutoCompTxt.setText(String.format("%s, ", userInput));

        placeCursorAtPosition(mTagAutoCompTxt.getText().length());
    }

    /**
     * Methode um den letzten Character eines String zu entfernen.
     *
     * @param text String
     * @return String mit einem Character weniger
     */
    private String removeLastCharacter(String text) {
        return text.substring(0, text.length() - 1);
    }

    /**
     * Methode um den letzten Character des Userinputs zu löschen
     */
    private void removeLastCharacterFromInput() {

        String input = mTagAutoCompTxt.getText().toString();
        mTagAutoCompTxt.setText(removeLastCharacter(input));
        placeCursorAtPosition(input.length() - 1);
    }

    /**
     * Methode um den Cursor an die angegebene Position zu setzen.
     *
     * @param position Angezielte Position des Cursors
     */
    private void placeCursorAtPosition(int position) {
        mTagAutoCompTxt.setSelection(position);
    }

    /**
     * Methode um zu überprüfen ob in einem String Buchustaben stehen oder ob dieser leer ist.
     *
     * @param text Zu überprüfender Text
     * @return TRUE, wenn Buchstaben im string stehen, FALSE wenn nicht
     */
    private boolean stringContainsText(String text) {
        return text.trim().length() > 0;
    }
}