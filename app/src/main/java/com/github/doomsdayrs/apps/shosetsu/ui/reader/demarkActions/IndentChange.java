package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions;

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.changeIndentSize;

public class IndentChange implements Utilities.DemarkAction {
    private final ChapterReader chapterReader;

    public IndentChange(ChapterReader chapterReader) {
        this.chapterReader = chapterReader;
    }

    @Override
    public void action(int spared) {
        changeIndentSize(spared);
        chapterReader.setUpReader();
    }
}
