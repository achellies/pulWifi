package es.pulimento.wifi.ui.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.os.AsyncTask;
import es.pulimento.wifi.ui.utils.github.GithubApi;
import es.pulimento.wifi.ui.utils.github.Issue;

public class ExceptionHandler implements UncaughtExceptionHandler {

	// Constants.
	private static String PACKAGE = "es.pulimento.wifi";

	// Variables.
	private GithubApi mGithubApi;
	private UncaughtExceptionHandler mDefaultHandler;

	public ExceptionHandler() {
		mGithubApi = new GithubApi("2e567f61e1803ce46935f5c57bc715de2356a1f5");
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Issue i = new Issue("Exception in "+getFileName(e), "TRACE:\n"+getStackTrace(e)+"\n\nCAUSE TRACE:\n"+getStackTrace(e.getCause()), "Automated Report");
		new ReportTask().execute(new ReportItem(i, t, e));
	}

	public String getFileName(Throwable e) {
		for(StackTraceElement s : e.getCause().getStackTrace())
			if(PACKAGE.equals(s.getClassName().substring(0, PACKAGE.length())))
				return s.getClassName().substring(s.getClassName().lastIndexOf('.')+1);
		return "Unknown";
	}

	public String getStackTrace(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
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

	class ReportTask extends AsyncTask<ReportItem, Void, Void> {
		@Override
		protected Void doInBackground(ReportItem... params) {
			mGithubApi.reportIssue(params[0].getIssue());
			mDefaultHandler.uncaughtException(params[0].getThread(), params[0].getThrowable());
			return null;
		}
	}
}