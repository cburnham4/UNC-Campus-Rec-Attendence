package letshangllc.uncrecattendence;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Applies system bar insets for Android 15+ edge-to-edge without redesigning layouts.
 */
public final class EdgeToEdgeHelper {
    private EdgeToEdgeHelper() {
    }

    public static void apply(Activity activity, View root) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return windowInsets;
        });
        root.requestApplyInsets();
    }

    public static void applyToContentRoot(Activity activity) {
        View content = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        if (content != null) {
            apply(activity, content);
        }
    }
}
