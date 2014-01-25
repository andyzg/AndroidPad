package com.droidpad.android.touch.scaledetector;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;


import android.app.Activity;
import android.os.Bundle;

public class ScaleDetectorTestActivity extends Activity {
  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(new ImageViewWithZoom(this));
  }
} 
