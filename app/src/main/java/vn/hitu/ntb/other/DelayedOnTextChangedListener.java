package techres.vn.tms.app.helper;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

public abstract class DelayedOnTextChangedListener implements TextWatcher {
    private final Handler handler = new Handler(Looper.myLooper());
    private Runnable runnable;
    private int timeMillis; //Truyền vào thấp nhất là 500 millis

    protected DelayedOnTextChangedListener(int timeMillis) {
        this.timeMillis = timeMillis;
    }

    @Override
    public void afterTextChanged(Editable s) {
        handler.removeCallbacks(runnable);
        runnable = () -> onDelayerQueryTextChange(s.toString());
        handler.postDelayed(runnable, timeMillis);
    }
    public abstract void onDelayerQueryTextChange(String s);

}
