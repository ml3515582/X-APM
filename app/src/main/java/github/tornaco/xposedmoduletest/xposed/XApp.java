package github.tornaco.xposedmoduletest.xposed;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.android.internal.os.BinderInternal;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiProvider;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.emoji.EmojiCategory;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import org.newstand.logger.Logger;
import org.newstand.logger.Settings;

import github.tornaco.apigen.BuildHostInfo;
import github.tornaco.apigen.GithubCommitSha;
import github.tornaco.xposedmoduletest.R;
import github.tornaco.xposedmoduletest.license.XActivation;
import github.tornaco.xposedmoduletest.provider.XSettings;
import github.tornaco.xposedmoduletest.service.VerboseLogService;
import github.tornaco.xposedmoduletest.util.EmojiUtil;
import github.tornaco.xposedmoduletest.xposed.app.XAppGuardManager;
import github.tornaco.xposedmoduletest.xposed.app.XAshmanManager;

/**
 * Created by guohao4 on 2017/10/17.
 * Email: Tornaco@163.com
 */
@GithubCommitSha
@BuildHostInfo
public class XApp extends Application implements Runnable {

    @SuppressLint("StaticFieldLeak")
    private static XApp xApp;

    public static XApp getApp() {
        return xApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        xApp = this;
        Logger.config(Settings.builder().tag("XAppGuardApp")
                .logLevel(XSettings.isDevMode(this)
                        ? Logger.LogLevel.DEBUG : Logger.LogLevel.WARN)
                .build());
        XAppGuardManager.init();
        XAshmanManager.init();
        XActivation.reloadAsync(this);
        BinderInternal.addGcWatcher(this);
        EmojiManager.install(new MyEmojiProvider());
        startService(new Intent(this, VerboseLogService.class));
    }

    public void hideAppIcon(boolean enable) {
//        PackageManager pm = getPackageManager();
//        ComponentName enabled = enable ? new ComponentName(this,
//                GuardAppNavActivityNoLauncher.class) : new ComponentName(this, GuardAppNavActivity.class);
//        ComponentName disabled = enable ? new ComponentName(this,
//                GuardAppNavActivity.class) : new ComponentName(this, GuardAppNavActivityNoLauncher.class);
//        Logger.d(enabled);
//        Logger.d(disabled);
//        pm.setComponentEnabledSetting(enabled,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//        pm.setComponentEnabledSetting(disabled,
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void run() {
        Logger.v("onGC");
    }

    private class MyEmojiProvider implements EmojiProvider {

        private GoogleEmojiProvider googleEmojiProvider = new GoogleEmojiProvider();

        @NonNull
        @Override
        public EmojiCategory[] getCategories() {
            EmojiCategory[] google = googleEmojiProvider.getCategories();
            EmojiCategory[] our = new EmojiCategory[google.length + 1];
            System.arraycopy(google, 0, our, 0, google.length);
            our[our.length - 1] = new EmojiCategory() {
                @NonNull
                @Override
                public Emoji[] getEmojis() {
                    return new Emoji[]{
                            new Emoji(EmojiUtil.HEIHEIHEI, R.drawable.d_heiheihei),
                            new Emoji(EmojiUtil.DOG, R.drawable.doge_lv),
                    };
                }

                @Override
                public int getIcon() {
                    return R.drawable.d_heiheihei;
                }
            };
            return our;
        }
    }
}