package es.pulimento.wifi.ui.utils.github;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

public class GithubApi {

	// Constants...
	private static String BASEURL = "https://api.github.com/repos/pulWifi/pulWifi/";

	// Variables...
	private Boolean mAuthed;
	private String mToken;

	public GithubApi() {
		mAuthed = false;
	}

	public GithubApi(String token) {
		mAuthed = true;
		mToken = token;
	}

	public Download getLastDownload() {
		try {
			URL url = new URL(BASEURL + "downloads");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder sb = new StringBuilder();
			String s;
			while((s = in.readLine()) != null)
				sb.append(s);
			in.close();
			return new Download((new JSONArray(sb.toString())).getJSONObject(0));
		} catch (IOException e) {
			// No internet...
			return null;
		} catch (JSONException e) {
			// Should not happen...
			return null;
		}
	}

	/**
	 * Returns a list of issues of the project.
	 * @return Issues list.
	 */
	public List<Issue> getIssues() {
		List<Issue> issues = new ArrayList<Issue>();

		// Get the list of issues...
		try {
			URL url = new URL(BASEURL + "issues");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder sb = new StringBuilder();
			String s;
			while((s = in.readLine()) != null)
				sb.append(s);
			in.close();
			JSONArray a = new JSONArray(sb.toString());
			for(int i = 0; i < a.length(); i++)
				issues.add(new Issue(a.getJSONObject(i)));
		} catch (MalformedURLException e) {
			// Will never happen...
		} catch (IOException e) {
			// No internet...
		} catch (JSONException e) {
			// Should not happen...
		}

		return issues;
	}

	/**
	 * Reports a new issue.
	 * @param i Issue to post in Github.
	 */
	public void reportIssue(Issue i) {
		if(!mAuthed)		
			return;
		
		// This made report system not to work
		
//		 If issue was already reported do nothing.
//		if(getIssues().contains(i))
//			return;

		HttpPost httpPost = new HttpPost(BASEURL + "issues");
		httpPost.setHeader("Authorization", "token " + mToken);
		httpPost.setHeader("Content-Type", "application/json");
		try {
			httpPost.setEntity(new StringEntity(i.toJSONString(), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// Should never happen...
		}
		try {
			(new DefaultHttpClient()).execute(httpPost);
		} catch (ClientProtocolException e) {
			// Should not happen
		} catch (IOException e) {
			// No internet...
		}
	}
}
