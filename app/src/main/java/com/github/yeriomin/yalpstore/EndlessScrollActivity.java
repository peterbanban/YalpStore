package com.github.yeriomin.yalpstore;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.DetailTask;
import com.github.yeriomin.yalpstore.task.playstore.DetailsTask;
import com.github.yeriomin.yalpstore.task.playstore.EndlessScrollTask;
import com.github.yeriomin.yalpstore.task.playstore.SearchTask;
import com.github.yeriomin.yalpstore.view.ListItem;
import com.github.yeriomin.yalpstore.view.ProgressIndicator;
import com.github.yeriomin.yalpstore.view.SearchResultAppBadge;

import java.util.List;

abstract public class EndlessScrollActivity extends AppListActivity {

    protected AppListIterator iterator;

    abstract protected EndlessScrollTask getTask();
    abstract protected DetailTask getDetailTask();

    public void setIterator(AppListIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onNewIntent(getIntent());
        getListView().setOnScrollListener(new ScrollEdgeListener() {

            @Override
            protected void loadMore() {
                loadApps();
            }
        });
    }

    @Override
    protected ListItem getListItem(App app) {
        SearchResultAppBadge appBadge = new SearchResultAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    @Override
    public void addApps(List<App> appsToAdd) {
        AppListAdapter adapter = (AppListAdapter) getListView().getAdapter();
        if (!adapter.isEmpty()) {
            ListItem last = adapter.getItem(adapter.getCount() - 1);
            if (last instanceof ProgressIndicator) {
                adapter.remove(last);
            }
        }
        super.addApps(appsToAdd, false);
        if (!appsToAdd.isEmpty()) {
            adapter.add(new ProgressIndicator());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearApps() {
        super.clearApps();
        iterator = null;
    }

//    protected EndlessScrollTask prepareTask(EndlessScrollTask task) {
//        task.setContext(this);
//        task.setErrorView((TextView) getListView().getEmptyView());
//        task.setProgressIndicator(findViewById(R.id.progress));
//        return task;
//    }
    protected DetailTask prepareTask(DetailTask task) {
        task.setContext(this);
        task.setProgressIndicator(findViewById(R.id.progress));
        return task;
}

    @Override
    public void loadApps() {
//        prepareTask(getTask()).execute();
        Log.d("tag","loadApps");
        prepareTask(getDetailTask()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_filter).setVisible(true);
        menu.findItem(R.id.filter_apps_with_ads).setVisible(true);
        menu.findItem(R.id.filter_paid_apps).setVisible(true);
        menu.findItem(R.id.filter_gsf_dependent_apps).setVisible(true);
        menu.findItem(R.id.filter_rating).setVisible(true);
        menu.findItem(R.id.filter_downloads).setVisible(true);
        return result;
    }
}
