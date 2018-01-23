package com.github.yeriomin.yalpstore.task.playstore;

import android.text.TextUtils;
import android.util.Log;
import com.github.yeriomin.playstoreapi.DetailsResponse;
import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.AppListIterator;
import com.github.yeriomin.yalpstore.EndlessScrollActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by huqx on 2018/1/23.
 */

public class DetailTask extends PlayStorePayloadTask<App>{
  protected String mPackageName;
  protected App app;
  @Override
  protected App getResult(GooglePlayAPI api, String... arguments) throws IOException {
     DetailsResponse detailsResponse =  api.details(mPackageName);
     DocV2 docV2= detailsResponse.getDocV2();
     app = AppBuilder.build(docV2);
    Log.d("tag","getResult通过response获取："+app.getPackageName());
    return app;
  }

  @Override
  protected App doInBackground(String... arguments) {
    return super.doInBackground(arguments);
  }

  @Override
  protected void onPostExecute(App app) {
      EndlessScrollActivity activity = (EndlessScrollActivity) context;

      if (!success() && !activity.getListView().getAdapter().isEmpty()) {
        errorView = null;
      }
      super.onPostExecute(app);
      ArrayList <App> apps = new ArrayList<>();
      apps.add(app);
      activity.addApps(apps);
      Log.d("tag","onPostExecute通过response获取："+app.getPackageName());
    }

   public void setmPackageName(String name){
     mPackageName = name;
   }

  }

