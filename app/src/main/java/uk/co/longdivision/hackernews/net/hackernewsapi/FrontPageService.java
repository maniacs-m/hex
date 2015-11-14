package uk.co.longdivision.hackernews.net.hackernewsapi;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;

import java.util.List;

import uk.co.longdivision.hackernews.model.Item;
import uk.co.longdivision.hackernews.net.hackernewsapi.marshall.FrontPageMarshaller;


public class FrontPageService {

    private final RequestQueue mRequestQueue;

    public FrontPageService(RequestQueue requestQueue) {
        this.mRequestQueue = requestQueue;
    }

    public List<? extends Item> getTopItems() {
        String API_URL = "https://hacker-news-api.herokuapp.com";
        String FRONT_PAGE_PATH = "/front-page";
        String frontPagePath = API_URL + FRONT_PAGE_PATH;

        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(frontPagePath, future, future);
        mRequestQueue.add(request);

        try {
            JSONArray response = future.get();
            return FrontPageMarshaller.marshall(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
