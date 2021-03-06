package com.kedzie.vbox.metrics;

import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.kedzie.vbox.app.LoopingThread;
import com.kedzie.vbox.soap.VBoxSvc;

public class DataThread extends LoopingThread {
		private static final String TAG = "MetricDataThread";

		private VBoxSvc _vmgr;
		private MetricView []_views;
		private String _object;
		private int _period;
		
		public DataThread(Context context, VBoxSvc vmgr, String object, int period, MetricView...views){
			super("Metric Data");
			_vmgr=vmgr;
			_object=object;
			_period=period;
			_views=views;
		}

		@Override
		public void loop() {
			try {
				Map<String, MetricQuery> data = _vmgr.queryMetrics(_object, "*:");
				for(MetricView v : _views)
				    if(v!=null) v.setQueries(data);
			} catch (Exception e) {
				Log.e(TAG, "Error querying metrics", e);
			} finally {
				try { Thread.sleep(_period*1000); } catch (InterruptedException e) { 
					_running=false;
				}
			}
		}
}
