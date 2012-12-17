package es.pulimento.wifi.ui.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.util.Log;
import es.pulimento.wifi.ui.utils.github.GithubApi;
import es.pulimento.wifi.ui.utils.github.Issue;

public class ExceptionHandler implements UncaughtExceptionHandler {

	// Constants.
	private static String PACKAGE = "es.pulimento.wifi";

	// Variables.
	private GithubApi mGithubApi;
	private UncaughtExceptionHandler mDefaultHandler;
	private PackageInfo info;
	private ReportItem mReportItem;

	public ExceptionHandler(PackageInfo info) {
		mGithubApi = new GithubApi("2e567f61e1803ce46935f5c57bc715de2356a1f5");
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		this.info = info;
	}

	public ExceptionHandler() {
		mGithubApi = new GithubApi("2e567f61e1803ce46935f5c57bc715de2356a1f5");
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	@SuppressLint("NewApi")
	// Code requiring API > 11 here really doesn't run on pre-HoneyComb devices
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Log.e(Constants.TAG, "Uncaught exception!! Reporting...");
		// String version = Resources.getSystem().getString(R.string.app_version);

		// Format string version to add to the report
		String version = null;
		if(info != null) {
			version = "PackageName = " + info.packageName + "\nVersionCode = " + info.versionCode
					+ "\nVersionName = " + info.versionName + "\n";
		} else {
			version = "<ERROR GETTING PACKAGE INFO>";
		}

		// Create issue to report
		Issue i = new Issue("Exception in " + getFileName(e), version + "\nTRACE:\n"
				+ getStackTrace(e) + "\n\nCAUSE TRACE:\n" + getStackTrace(e.getCause()),
				"Automated Report", info.versionName);

		mReportItem = new ReportItem(i, t, e);

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				ReportItem reportItem = mReportItem;
				mGithubApi.reportIssue(reportItem.getIssue());
				mDefaultHandler
						.uncaughtException(reportItem.getThread(), reportItem.getThrowable());
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
		thread.run();

		// Fallback
		mDefaultHandler.uncaughtException(t, e);
	}

	public String getFileName(Throwable e) {
		// Sometimes stack trace are null in passed exception, why??
		if(e.getStackTrace() != null) {
			for(StackTraceElement s : e.getStackTrace())
				if(PACKAGE.equals(s.getClassName().substring(0, PACKAGE.length())))
					return s.getClassName().substring(s.getClassName().lastIndexOf('.') + 1);
		}
		return "<Unknown>";
	}

	public String getStackTrace(Throwable t) {
		if(t != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			t.printStackTrace(pw);
			pw.flush();
			sw.flush();
			return sw.toString();
		}
		return "<No stack trace>";
	}

	class ReportItem {

		private Issue mIssue;
		private Thread mThread;
		private Throwable mException;

		public ReportItem(Issue i, Thread t, Throwable e) {
			mIssue = i;
			mThread = t;
			mException = e;
		}

		public Issue getIssue() {
			return mIssue;
		}

		public Thread getThread() {
			return mThread;
		}

		public Throwable getThrowable() {
			return mException;
		}
	}

	/*
	class ReportTask extends AsyncTask<ReportItem, Void, Void> {

		@Override
		protected Void doInBackground(ReportItem... params) {

			ReportItem reportItem = params[0];
			mGithubApi.reportIssue(reportItem.getIssue());
			mDefaultHandler.uncaughtException(reportItem.getThread(), reportItem.getThrowable());

			return null;
		}
	}*/
}