package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.SearchView;

import com.github.yeriomin.yalpstore.fragment.FilterMenu;

import static com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator.PREFERENCE_EMAIL;

public abstract class YalpStoreActivity extends Activity {

    static protected boolean logout = false;

    public static void cascadeFinish() {
        YalpStoreActivity.logout = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(getClass().getSimpleName(), "Starting activity");
        logout = false;
        if (((YalpStoreApplication) getApplication()).isTv()) {
            requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
        }
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        Log.v(getClass().getSimpleName(), "Resuming activity");
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            invalidateOptionsMenu();
        }
        if (logout) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        Log.v(getClass().getSimpleName(), "Pausing activity");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(getClass().getSimpleName(), "Stopping activity");
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (!TextUtils.isEmpty(PreferenceActivity.getString(this, PREFERENCE_EMAIL))) {
            menu.findItem(R.id.action_logout).setVisible(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            addQueryTextListener(menu.findItem(R.id.action_search));
        }
        new FilterMenu(this).onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
            case R.id.action_logout:
                showLogOutDialog();
                break;
            case R.id.action_search:
                if (!onSearchRequested()) {
                    showFallbackSearchDialog();
                }
                break;
            case R.id.action_updates:
                startActivity(new Intent(this, UpdatableAppsActivity.class));
                break;
            case R.id.action_installed_apps:
                startActivity(new Intent(this, InstalledAppsActivity.class));
                break;
            case R.id.action_categories:
                startActivity(new Intent(this, CategoryListActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.action_bug_report:
                startActivity(new Intent(this, BugReportActivity.class));
                break;
            case R.id.filter_system_apps:
            case R.id.filter_apps_with_ads:
            case R.id.filter_paid_apps:
            case R.id.filter_gsf_dependent_apps:
            case R.id.filter_category:
            case R.id.filter_rating:
            case R.id.filter_downloads:
                new FilterMenu(this).onOptionsItemSelected(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (null == receiver) {
            return;
        }
        try {
            super.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // Ignoring
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void addQueryTextListener(MenuItem searchItem) {
        SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(YalpStoreActivity.this, SearchActivity.class);
                i.setAction(Intent.ACTION_SEARCH);
                i.putExtra(SearchManager.QUERY, query);
                startActivity(i);
                return false;
            }
        });
    }

    private AlertDialog showLogOutDialog() {
        return new AlertDialog.Builder(this)
            .setMessage(R.string.dialog_message_logout)
            .setTitle(R.string.dialog_title_logout)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new PlayStoreApiAuthenticator(getApplicationContext()).logout();
                    dialogInterface.dismiss();
                    finishAll();
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    private AlertDialog showFallbackSearchDialog() {
        final EditText textView = new EditText(this);
        return new AlertDialog.Builder(this)
            .setView(textView)
            .setPositiveButton(android.R.string.search_go, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                    i.setAction(Intent.ACTION_SEARCH);
                    i.putExtra(SearchManager.QUERY, textView.getText().toString());
                    startActivity(i);
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    protected void finishAll() {
        logout = true;
        finish();
    }
}
