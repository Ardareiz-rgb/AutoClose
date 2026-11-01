package com.ardareiz.autoclose;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText minutesInput;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUi();
        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10);
        }
    }

    private void buildUi() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(28, 36, 28, 24);
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        page.setBackgroundColor(0xFF101318);

        TextView title = new TextView(this);
        title.setText("AutoClose");
        title.setTextSize(30);
        title.setTextColor(0xFFFFFFFF);
        title.setGravity(Gravity.CENTER);
        page.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText("Roblox zamanlayıcısı");
        subtitle.setTextSize(16);
        subtitle.setTextColor(0xFFB8C0CC);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = matchWrap();
        subLp.topMargin = 8;
        page.addView(subtitle, subLp);

        minutesInput = new EditText(this);
        minutesInput.setSingleLine(true);
        minutesInput.setInputType(2);
        minutesInput.setText("30");
        minutesInput.setTextColor(0xFFFFFFFF);
        minutesInput.setHintTextColor(0xFF9AA4B2);
        minutesInput.setHint("Dakika");
        minutesInput.setTextSize(22);
        minutesInput.setGravity(Gravity.CENTER);
        minutesInput.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF8B9BB4));
        LinearLayout.LayoutParams inputLp = new LinearLayout.LayoutParams(-1, -2);
        inputLp.topMargin = 34;
        page.addView(minutesInput, inputLp);

        Button start = new Button(this);
        start.setText("Başlat");
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(-1, -2);
        btnLp.topMargin = 18;
        page.addView(start, btnLp);
        start.setOnClickListener(v -> startTimer());

        Button stop = new Button(this);
        stop.setText("Zamanlayıcıyı durdur");
        page.addView(stop, matchWrap());
        stop.setOnClickListener(v -> {
            Intent i = new Intent(this, TimerService.class).setAction(TimerService.ACTION_STOP);
            startService(i);
            Toast.makeText(this, "Zamanlayıcı durduruldu", Toast.LENGTH_SHORT).show();
        });

        TextView info = new TextView(this);
        info.setText("Süre bitince AutoClose ana ekrana dönmeye çalışır. Android üçüncü taraf uygulamaların işlemini zorla sonlandırmaya izin vermez; Roblox tamamen öldürülmez, arka planda kalabilir.");
        info.setTextSize(13);
        info.setTextColor(0xFFB8C0CC);
        info.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams infoLp = matchWrap();
        infoLp.topMargin = 28;
        page.addView(info, infoLp);

        Button accessibility = new Button(this);
        accessibility.setText("Otomatik ana ekrana dönüş iznini ayarla");
        LinearLayout.LayoutParams accLp = matchWrap();
        accLp.topMargin = 18;
        page.addView(accessibility, accLp);
        accessibility.setOnClickListener(v ->
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));

        setContentView(page);
    }

    private void startTimer() {
        String raw = minutesInput.getText().toString().trim();
        int minutes;
        try { minutes = Integer.parseInt(raw); }
        catch (Exception e) { minutes = 0; }
        if (minutes < 1 || minutes > 1440) {
            Toast.makeText(this, "1 ile 1440 dakika arasında bir süre gir", Toast.LENGTH_LONG).show();
            return;
        }
        Intent i = new Intent(this, TimerService.class)
                .setAction(TimerService.ACTION_START)
                .putExtra(TimerService.EXTRA_MINUTES, minutes);
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
        Toast.makeText(this, "Zamanlayıcı başladı", Toast.LENGTH_SHORT).show();
        finish();
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(-1, -2);
    }
}
