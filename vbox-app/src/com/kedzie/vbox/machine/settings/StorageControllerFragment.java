package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IStorageController;
import com.kedzie.vbox.api.jaxb.StorageControllerType;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.task.ActionBarTask;
import roboguice.fragment.RoboSherlockFragment;
import roboguice.inject.InjectView;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class StorageControllerFragment extends RoboSherlockFragment {

    private class LoadInfoTask extends ActionBarTask<IStorageController, IStorageController> {
    	
        public LoadInfoTask() { 
        	super(getSherlockActivity(), null); 
        }
        
        @Override 
        protected IStorageController work(IStorageController...params) throws Exception {
        	params[0].getName();
        	params[0].getBus();
        	params[0].getUseHostIOCache();
            return params[0];
        }
        @Override
        protected void onSuccess(IStorageController result) {
        	_controller=result;
            populate();
        }
    }

    private IStorageController _controller;

	@InjectView(R.id.controller_host_io_cache)
    private CheckBox _hostIOCheckbox;
	@InjectView(R.id.controller_name)
    private TextView _nameText;
	@InjectView(R.id.controller_type)
	private Spinner _typeSpinner;
	private StorageControllerType[] _types;
	private ArrayAdapter<StorageControllerType> _typeAdapter;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_controller = getArguments().getParcelable(IStorageController.BUNDLE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings_storage_controller, null);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		new LoadInfoTask().execute(_controller);
	}

	private void populate() {
		_nameText.setText( _controller.getName() );
		_hostIOCheckbox.setChecked(_controller.getUseHostIOCache());
		_types = StorageControllerType.getValidTypes(_controller.getBus());
		_typeAdapter = new ArrayAdapter<StorageControllerType>(getActivity(), android.R.layout.simple_spinner_item, _types);
		_typeSpinner.setAdapter(_typeAdapter);
		_typeSpinner.setSelection(Utils.indexOf(_types, _controller.getControllerType()));
	}
}
