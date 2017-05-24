package io.softape.burst_espresso;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.squareup.burst.BurstJUnit4;
import com.squareup.burst.annotation.Burst;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(BurstJUnit4.class)
public class DeeplinkTest {


    static final String MAIN_ACTIVITY = MainActivity.class.getCanonicalName();

    @SuppressWarnings("unused")
    public enum SchemaConfiguration {
        https("https://bloglovin.com"),
        https_www("http://www.bloglovin.com"),
        http("http://bloglovin.com"),
        http_www("http://www.bloglovin.com");

        public final String domain;

        SchemaConfiguration(final String domain) {
            this.domain = domain;
        }
    }

    @SuppressWarnings("unused")
    public enum DeeplinkTestCase {

        collection("/collections/1239", MAIN_ACTIVITY),
        collection2("/collections/abc-1239", MAIN_ACTIVITY);

        public final String path;
        private final String resolvedActivityName;

        DeeplinkTestCase(String path, String resolvedActivityName) {
            this.path = path;
            this.resolvedActivityName = resolvedActivityName;
        }
    }

    @Burst
    SchemaConfiguration schemaConfiguration;
    @Burst
    DeeplinkTestCase deeplinkTestCase;

    @Test
    public void testDeeplinks() throws Exception {
        String url = schemaConfiguration.domain + deeplinkTestCase.path;

        Context appContext = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PackageManager packageManager = appContext.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        for (ResolveInfo single : resolveInfos) {
            //This is to handle when we have different flavor of the app installed on the device，we want to make sure the package name is the same.
            if (single.activityInfo.packageName.equalsIgnoreCase(appContext.getPackageName())) {
                if (single.activityInfo.name.equalsIgnoreCase(deeplinkTestCase.resolvedActivityName)) {
                    return;
                }
            }

        }
        junit.framework.Assert.fail(url + " " + "is not resolved while it should be resolvable as " + deeplinkTestCase.resolvedActivityName);
    }
}

