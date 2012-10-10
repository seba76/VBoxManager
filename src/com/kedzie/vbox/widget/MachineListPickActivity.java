package com.kedzie.vbox.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.machine.MachineListBaseFragment;
import com.kedzie.vbox.machine.MachineListBaseFragment.OnSelectMachineListener;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.DialogTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype activity
 */
public class MachineListPickActivity extends BaseActivity implements OnSelectMachineListener {
	
	/** VirtualBox API */
	private VBoxSvc _vmgr;
	private int mAppWidgetId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("Select Virtual Machine");
		_vmgr = (VBoxSvc)getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		 mAppWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

		if (savedInstanceState==null) {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.add(android.R.id.content, new MachineListBaseFragment());
			tx.commit();
		}
	}
	
	@Override
	public void onMachineSelected(IMachine machine) {
	    Provider.savePrefs(this, _vmgr, machine, mAppWidgetId);
        setResult(RESULT_OK, new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));
        finish();
	}
	
	@Override 
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		if(_vmgr.getVBox()!=null)  
			new LogoffTask(_vmgr). execute();
		super.onBackPressed();
	}
	
	private class LogoffTask extends DialogTask<Void, Void>	{
		public LogoffTask(VBoxSvc vmgr) { 
			super( "LogoffTask", MachineListPickActivity.this, vmgr, "Logging Off");
		}
		@Override
		protected Void work(Void... params) throws Exception {
			_vmgr.logoff();
			return null;
		}
	}
}
