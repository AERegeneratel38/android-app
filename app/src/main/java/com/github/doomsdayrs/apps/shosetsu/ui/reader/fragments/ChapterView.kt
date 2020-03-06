package com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.ui.errorView.ErrorAlert
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader
import com.github.doomsdayrs.apps.shosetsu.ui.reader.async.ChapterViewLoader
import com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions.*
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.ToolbarHideOnClickListener
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getSavedNovelPassage
import com.github.doomsdayrs.apps.shosetsu.variables.ext.openInWebview
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import kotlinx.android.synthetic.main.chapter_view.*

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */
/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class ChapterView : Fragment() {
    private val demarkActions = arrayOf(TextSizeChange(this), ParaSpacingChange(this), IndentChange(this), ReaderChange(this), ThemeChange(this))

    // Order of values. Night, Light, Sepia
    private lateinit var themes: Array<MenuItem>

    // Order of values. Small,Medium,Large
    private lateinit var textSizes: Array<MenuItem>

    // Order of values. Non,Small,Medium,Large
    private lateinit var paragraphSpaces: Array<MenuItem>

    // Order of values. Non,Small,Medium,Large
    private lateinit var indentSpaces: Array<MenuItem>

    // Order of values. Default, Markdown
    @Suppress("unused")
    private lateinit var readers: Array<MenuItem>


    var chapterReader: ChapterReader? = null
    var url: String = ""
    var chapterID: Int = 0
        set(value) {
            field = value
            url = DatabaseIdentification.getChapterURLFromChapterID(value)
        }

    private var bookmarked = false

    //public View coverView;
// public ViewPager2 viewPager2;
//public NewReader currentReader;
    var ready = false
    var unformattedText: String = ""
    var text: String? = null


    private var bookmark: MenuItem? = null
    private var tapToScroll: MenuItem? = null


    init {
        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_chapter_view, menu)
        // Night mode
        run {
            themes = arrayOf(
                    menu.findItem(R.id.chapter_view_reader_night),
                    menu.findItem(R.id.chapter_view_reader_light),
                    menu.findItem(R.id.chapter_view_reader_sepia))
            when (Utilities.getReaderColor(context!!)) {
                0 -> {
                    themes[0].setChecked(true)
                }
                1 -> {
                    themes[1].setChecked(true)
                }
                2 -> {
                    themes[2].setChecked(true)
                }
                else -> {
                    Utilities.setLightMode()
                    themes[1].setChecked(true)
                }
            }
        }
        //  Bookmark
        run {
            bookmark = menu.findItem(R.id.chapter_view_bookmark)
            bookmarked = Database.DatabaseChapter.isBookMarked(chapterID)
            updateBookmark()
        }
        // Tap To Scroll
        run {
            tapToScroll = menu.findItem(R.id.tap_to_scroll)
            tapToScroll?.setChecked(Utilities.isTapToScroll)
        }
        // Text size
        run {
            textSizes = arrayOf(
                    menu.findItem(R.id.chapter_view_textSize_small),
                    menu.findItem(R.id.chapter_view_textSize_medium),
                    menu.findItem(R.id.chapter_view_textSize_large)
            )
            when (Settings.ReaderTextSize.toInt()) {
                14 -> textSizes[0].setChecked(true)
                17 -> textSizes[1].setChecked(true)
                20 -> textSizes[2].setChecked(true)
                else -> {
                    Settings.ReaderTextSize = 14F
                    textSizes[0].setChecked(true)
                }
            }
        }
        // Paragraph Space
        run {
            paragraphSpaces = arrayOf(
                    menu.findItem(R.id.chapter_view_paragraphSpace_none),
                    menu.findItem(R.id.chapter_view_paragraphSpace_small),
                    menu.findItem(R.id.chapter_view_paragraphSpace_medium),
                    menu.findItem(R.id.chapter_view_paragraphSpace_large)
            )

            paragraphSpaces[Settings.paragraphSpacing].setChecked(true)
        }
        // Indent Space
        run {
            indentSpaces = arrayOf(
                    menu.findItem(R.id.chapter_view_indent_none),
                    menu.findItem(R.id.chapter_view_indent_small),
                    menu.findItem(R.id.chapter_view_indent_medium),
                    menu.findItem(R.id.chapter_view_indent_large)
            )

            indentSpaces[Settings.indentSize].setChecked(true)
        }
        /* Reader
        {

            readers[0] = menu.findItem(R.id.reader_0);
            readers[1] = menu.findItem(R.id.reader_1);
            readerType = getReaderType(novelID);

            switch (readerType) {
                case 1:
                    demarkMenuItems(readers, 1, null);
                    break;
                case 0:
                case -1:
                    demarkMenuItems(readers, 0, null);
                    break;
                case -2:
                default:
                    throw new RuntimeException("Invalid chapter?!? How are you reading this without the novel loaded in");
            }
        }*/
    }


    /**
     * What to do when an menu item is selected
     *
     * @param item item selected
     * @return true if processed
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("item", item.toString())
        return when (item.itemId) {
            R.id.chapter_view_reader_night -> {
                Utilities.unmarkMenuItems(themes, 0, demarkActions[4])
                true
            }

            R.id.chapter_view_reader_light -> {
                Utilities.unmarkMenuItems(themes, 1, demarkActions[4])
                true
            }
            R.id.chapter_view_reader_sepia -> {
                Utilities.unmarkMenuItems(themes, 2, demarkActions[4])
                true
            }
            R.id.tap_to_scroll -> {
                Utilities.regret(context!!)
                // tapToScroll!!.isChecked = Utilities.toggleTapToScroll()
                true
            }
            R.id.chapter_view_bookmark -> {
                bookmarked = Utilities.toggleBookmarkChapter(chapterID)
                updateBookmark()
                true
            }
            R.id.chapter_view_textSize_small -> {
                Utilities.unmarkMenuItems(indentSpaces, 0, demarkActions[0])
                true
            }
            R.id.chapter_view_textSize_medium -> {
                Utilities.unmarkMenuItems(textSizes, 1, demarkActions[0])
                true
            }
            R.id.chapter_view_textSize_large -> {
                Utilities.unmarkMenuItems(textSizes, 2, demarkActions[0])
                true
            }
            R.id.chapter_view_paragraphSpace_none -> {
                Utilities.unmarkMenuItems(paragraphSpaces, 0, demarkActions[1])
                true
            }
            R.id.chapter_view_paragraphSpace_small -> {
                Utilities.unmarkMenuItems(paragraphSpaces, 1, demarkActions[1])
                true
            }
            R.id.chapter_view_paragraphSpace_medium -> {
                Utilities.unmarkMenuItems(paragraphSpaces, 2, demarkActions[1])
                true
            }
            R.id.chapter_view_paragraphSpace_large -> {
                Utilities.unmarkMenuItems(paragraphSpaces, 3, demarkActions[1])
                true
            }
            R.id.chapter_view_indent_none -> {
                Utilities.unmarkMenuItems(indentSpaces, 0, demarkActions[2])
                true
            }
            R.id.chapter_view_indent_small -> {
                Utilities.unmarkMenuItems(indentSpaces, 1, demarkActions[2])
                true
            }
            R.id.chapter_view_indent_medium -> {
                Utilities.unmarkMenuItems(indentSpaces, 2, demarkActions[2])
                true
            }
            R.id.chapter_view_indent_large -> {
                Utilities.unmarkMenuItems(indentSpaces, 3, demarkActions[2])
                true
            }
            R.id.browser -> {
                activity?.let { url.isNotEmpty().let { Utilities.openInBrowser(activity!!, url) } }
                true
            }
            R.id.webview -> {
                activity?.let { url.isNotEmpty().let { openInWebview(activity!!, url) } }
                true
            }
            R.id.reader_0 -> {
                Utilities.regret(context!!)
                //Utilities.unmarkMenuItems(readers, 0, demarkActions[3])
                true
            }
            R.id.reader_1 -> {
                Utilities.regret(context!!)
                //Utilities.unmarkMenuItems(readers, 1, demarkActions[3])
                true
            }
            else -> false
        }
    }

    private fun updateBookmark() {
        if (bookmark != null) if (bookmarked) bookmark!!.setIcon(R.drawable.ic_bookmark_black_24dp) else bookmark!!.setIcon(R.drawable.ic_bookmark_border_black_24dp)
    }

    override fun onResume() {
        super.onResume()
        val title = Database.DatabaseChapter.getTitle(chapterID)
        chapterReader?.getToolbar()?.let { it.title = title }
        Log.i("ChapterView", "Resuming:${appendID()}")
        Log.i("ChapterView", "${appendID()} \n ${text.isNullOrEmpty()} | ${unformattedText.isEmpty()} | $bookmarked | $ready ")
        if (text.isNullOrEmpty() && unformattedText.isEmpty()) {
            Log.i("ChapterView", "Text and unformatted text is null, resetting${appendID()}")
            dataSet()
        } else progress.visibility = View.GONE
    }

    fun appendID(): String {
        return "\tURL/ID( $url | $chapterID )"
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("id", chapterID)
        outState.putString("url", url)
        outState.putString("text", text)
        outState.putString("unform", unformattedText)
        outState.putBoolean("book", bookmarked)
        outState.putBoolean("ready", ready)
        Log.i("ChapterView", "Saved:${appendID()}")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.chapter_view, container, false)
        if (savedInstanceState != null) {
            chapterID = savedInstanceState.getInt("id")
            url = savedInstanceState.getString("url", "")
            chapterReader = activity as ChapterReader?
            unformattedText = savedInstanceState.getString("unform", "")
            text = savedInstanceState.getString("text")
            bookmarked = savedInstanceState.getBoolean("book")
            ready = savedInstanceState.getBoolean("ready")
            Log.i("ChapterView", "Restored:${appendID()}")
        }
        Log.i("ChapterView", "Created:${appendID()}")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addBottomListener()
        chapterReader?.getToolbar()?.let { textView.setOnClickListener(ToolbarHideOnClickListener(it)) }
        textView.setBackgroundColor(Settings.ReaderTextBackgroundColor)
        textView.setTextColor(Settings.ReaderTextColor)
        textView.textSize = Settings.ReaderTextSize
        next_chapter.setOnClickListener {
            val position = chapterReader!!.findCurrentPosition(chapterID)
            if (chapterReader!!.chapterIDs.isNotEmpty() && chapterReader!!.getViewPager() != null) {
                if (position + 1 < chapterReader!!.chapterIDs.size) {
                    next_chapter.visibility = View.GONE
                    chapterReader!!.getViewPager()?.currentItem = position + 1
                } else chapterReader?.toast("No more chapters!")
            }
        }
        //holder.viewPager2.setUserInputEnabled(false);
        //NewChapterReaderTypeAdapter newChapterReaderTypeAdapter = new NewChapterReaderTypeAdapter(newChapterReader);
        //holder.viewPager2.setAdapter(newChapterReaderTypeAdapter);
        //holder.viewPager2.setCurrentItem(getReaderType(newChapterReader.novelID));

        ready = false

        if (savedInstanceState == null) {
            dataSet()
        } else {
            Log.d("ChapterView", "Load, Data present${appendID()}")
            setUpReader()
        }
    }

    private fun dataSet() {
        if (Database.DatabaseChapter.isSaved(chapterID)) {
            Log.d("ChapterView", "Loading from storage${appendID()}")
            val r = Database.DatabaseChapter.getSavedNovelPassage(chapterID)
            if (r.succeeded) {
                unformattedText = r.value!!
                setUpReader()
                scrollView.post { scrollView.scrollTo(0, Database.DatabaseChapter.getY(chapterID)) }
                ready = true
            } else {
                ErrorAlert(activity!!.parent)
                        .setMessage(r.e?.message)
                        .runOnUI()
            }
        } else {
            Log.d("ChapterView", "Loading from online${appendID()}")
            unformattedText = ""
            setUpReader()
            ChapterViewLoader(this).execute()
        }
    }


    fun setUpReader() {
        scrollView!!.setBackgroundColor(Settings.ReaderTextBackgroundColor)
        textView!!.setBackgroundColor(Settings.ReaderTextBackgroundColor)
        textView!!.setTextColor(Settings.ReaderTextColor)
        textView!!.textSize = Settings.ReaderTextSize
        if (unformattedText.isNotEmpty()) {
            val replaceSpacing = StringBuilder("\n")
            for (x in 0 until Settings.paragraphSpacing) replaceSpacing.append("\n")
            for (x in 0 until Settings.indentSize) replaceSpacing.append("\t")
            text = unformattedText.replace("\n".toRegex(), replaceSpacing.toString())
            if (text!!.length > 100)
                Log.d("ChapterView", "TextSet\t" + text!!.substring(0, 100).replace("\n", "\\n") + "\n" + appendID())
            else if (text!!.isNotEmpty())
                Log.d("ChapterView", "TextSet\t" + text!!.substring(0, text!!.length - 1).replace("\n", "\\n") + "\n" + appendID())
            textView!!.text = text
            // viewPager2.post(() -> currentReader.setText(text));
        }
    }

    private var marked: Boolean = false

    /**
     * What to do when scroll hits bottom
     */
    private fun scrollHitBottom() {
        val total = scrollView!!.getChildAt(0).height - scrollView!!.height
        if (ready) if (scrollView!!.scrollY / total.toFloat() < .99) {
            // Inital mark of reading
            if (!marked && Settings.ReaderMarkingType == Settings.MarkingTypes.ONSCROLL.i) {
                Log.d("ChapterView", "Marking as Reading")
                Database.DatabaseChapter.setChapterStatus(chapterID, Status.READING)
                marked = !marked
            }

            val y = scrollView!!.scrollY

            if (y % 5 == 0)
                if (Database.DatabaseChapter.getChapterStatus(chapterID) != Status.READ) Database.DatabaseChapter.updateY(chapterID, y)
        } else {
            Log.i("Scroll", "Marking chapter as READ${appendID()}")
            Database.DatabaseChapter.setChapterStatus(chapterID, Status.READ)
            Database.DatabaseChapter.updateY(chapterID, 0)
            next_chapter!!.visibility = View.VISIBLE
            //TODO Get total word count of passage, then add to a storage counter that memorizes the total (Chapters read, Chapters Unread, Chapters reading, Word count)
        }
    }

    /**
     * Sets up the hitting bottom listener
     */
    private fun addBottomListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView!!.setOnScrollChangeListener { _: View?, _: Int, _: Int, _: Int, _: Int -> scrollHitBottom() }
        } else {
            scrollView!!.viewTreeObserver.addOnScrollChangedListener { scrollHitBottom() }
        }
    }
}