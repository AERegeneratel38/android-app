package com.github.doomsdayrs.apps.shosetsu.ui.reader.async;

import android.app.Activity;
import android.os.AsyncTask;

import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewChapterView;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getY;
import static com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper.docFromURL;

public class NewChapterReaderViewLoader extends AsyncTask<Object, Void, Void> {

    private final NewChapterView newChapterView;

    public NewChapterReaderViewLoader(NewChapterView holder) {
        newChapterView = holder;
    }

    @Override
    protected Void doInBackground(Object... objects) {

        Activity activity = newChapterView.newChapterReader;
        //activity.runOnUiThread(() -> chapterView.errorView.errorView.setVisibility(View.GONE));
        try {
            newChapterView.unformattedText = newChapterView.newChapterReader.formatter.getNovelPassage(docFromURL(newChapterView.CHAPTER_URL, newChapterView.newChapterReader.formatter.hasCloudFlare()));
            activity.runOnUiThread(newChapterView::setUpReader);
            activity.runOnUiThread(() -> newChapterView.scrollView.post(() -> newChapterView.scrollView.scrollTo(0, getY(newChapterView.CHAPTER_ID))));
            activity.runOnUiThread(() -> newChapterView.ready = true);
        } catch (Exception e) {
            // activity.runOnUiThread(() -> { chapterView.errorView.errorView.setVisibility(View.VISIBLE);chapterView.errorView.errorMessage.setText(e.getMessage());chapterView.errorView.errorButton.setOnClickListener(view -> new ReaderViewLoader(chapterView).execute()); });
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //TODO
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //TODO
    }
}
