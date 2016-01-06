package uk.co.longdivision.hex;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import uk.co.longdivision.hex.adapter.StorySlidePagerAdapter;
import uk.co.longdivision.hex.asynctask.GetItem;
import uk.co.longdivision.hex.asynctask.ItemHandler;
import uk.co.longdivision.hex.model.Item;
import uk.co.longdivision.hex.model.Story;

public class StoryActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, ItemHandler {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Item mItem;
    private enum Page { WEBVIEW, COMMENTS }
    private Page mPage;
    private final static String STORY_ID_INTENT_EXTRA_NAME = "storyId";
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadItem();

        mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                if (mPager != null) {
                    mPager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }
        };
        mActionBar.addTab(mActionBar.newTab().setText("Article").setTabListener(tabListener));
        mActionBar.addTab(mActionBar.newTab().setText("Comments").setTabListener(tabListener));

        setContentView(R.layout.activity_story);
        setTitle("");

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new StorySlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(this);

        mPage = Page.WEBVIEW;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_share) {
            handleShareRequest();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_story_icons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mActionBar.selectTab(mActionBar.getTabAt(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onItemReady(Item item) {
        this.mItem = item;
    }

    private void handleShareRequest() {
        String intentMessage = getString(R.string.shareArticle);
        String url = ((Story) mItem).getUrl();

        if (mPage.equals(Page.COMMENTS)) {
            intentMessage = getString(R.string.shareComments);
            url = ((Story) mItem).getCommentsUrl();
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(shareIntent, intentMessage));
    }

    private void loadItem() {
        String storyId = this.getIntent().getStringExtra(STORY_ID_INTENT_EXTRA_NAME);
        HexApplication appContext = (HexApplication) this.getApplicationContext()
                .getApplicationContext();
        new GetItem(this, appContext).execute(storyId);
    }
}
