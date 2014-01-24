package com.andyzg.droidpad;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
	}

    /**
     * Switch to fragment.
     * @param newFrag The fragment to switch to.
     * @param addToBackStack Whether the transaction should be added to back stack.
     */
    private void switchToFragment(Fragment newFrag, boolean addToBackStack) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, newFrag);
            if (addToBackStack)
                    transaction.addToBackStack(null);
            transaction.commit();

    }
    
    public void onTrackpadClicked() {
            switchToFragment(new BalanceFragment());
    }

    public void onSettingsClicked() {
            switchToFragment(new TransactionFragment());
    }

    public void onAboutClicked() {                
    	switchToFragment(new AboutFragment());
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
