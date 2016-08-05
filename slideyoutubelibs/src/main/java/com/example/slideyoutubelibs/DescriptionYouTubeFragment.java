package com.example.slideyoutubelibs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DescriptionYouTubeFragment extends Fragment {
    public static final String YOUTUBE_API_KEY = "youtube_pai_key";
    private static final String VIDEO_ID_KEY = "video_id";
    private String videoId, youTubeAPIkey;
    private GetStatisticsTask GSTask = null;
    private TextView tvTitle, tvDescription, tvDate, tvLikeCount, tvDisLikeCount,tvViewCount;

    public static DescriptionYouTubeFragment newInstance(String... params) {
        DescriptionYouTubeFragment fragment = new DescriptionYouTubeFragment();
        Bundle args = new Bundle();
        args.putString(YOUTUBE_API_KEY, params[0]);
        args.putString(VIDEO_ID_KEY, params[1]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoId = getArguments().getString(VIDEO_ID_KEY);
            youTubeAPIkey = getArguments().getString(YOUTUBE_API_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_description_youtube, container, false);
        tvTitle =(TextView)mView.findViewById(R.id.video_title);
        tvDescription =(TextView)mView.findViewById(R.id.video_descr);
        tvDate =(TextView)mView.findViewById(R.id.video_publish_date);
        tvLikeCount =(TextView)mView.findViewById(R.id.video_likes);
        tvDisLikeCount =(TextView)mView.findViewById(R.id.video_dislikes);
        tvViewCount =(TextView)mView.findViewById(R.id.video_views_count);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (GSTask == null) {
            GSTask = new GetStatisticsTask();
            GSTask.execute(youTubeAPIkey, videoId);
        }

    }

    private class GetStatisticsTask extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            //NullPointerEcxeption
            if (data != null) {

                JSONObject statistics = null;
                try {
                    statistics = data.getJSONObject("statistics");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject snippet = null;
                try {
                    snippet = data.getJSONObject("snippet");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                tvTitle.setText(snippet.optString("title"));
                tvDescription.setText(snippet.optString("description"));
                tvLikeCount.setText(statistics.optString("likeCount"));
                tvDisLikeCount.setText(statistics.optString("dislikeCount"));
                tvViewCount.setText(statistics.optString("viewCount"));

                    String videoDate = snippet.optString("publishedAt");
                    SimpleDateFormat youtubeDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    SimpleDateFormat normalDate = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
                    Date parseDate = null;
                    try {
                        parseDate = youtubeDate.parse(videoDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String publishDate = normalDate.format(parseDate);
                    tvDate.setText(publishDate);


            GSTask = null;
        }
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("https://www.googleapis.com/youtube/v3/videos?part=snippet,statistics&id="
                        + params[1] + "&key=" + params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                String line = "";
                StringBuffer buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                JSONObject response = new JSONObject(buffer.toString());
                JSONArray items = response.getJSONArray("items");

                JSONObject data = items.getJSONObject(0);

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

