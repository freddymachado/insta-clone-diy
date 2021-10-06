package com.gvm.diy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.gvm.diy.adapter.ExploreAdapter;
import com.gvm.diy.adapter.ViewPagerAdapter;
import com.gvm.diy.models.ExploreItem;
import com.gvm.diy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    // TODO: Hacer 2do recycler al pulsar la búsqueda (last entrega)
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG ="SearchFragment";

    private static final String URL_POSTS = "https://diys.co/punto.php";

    private RecyclerView recycler_view;
    public EditText editTextSearch;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_search, container, false);
        recycler_view = itemView.findViewById(R.id.recycler_view);
        editTextSearch = itemView.findViewById(R.id.editTextSearch);
        tabLayout = itemView.findViewById(R.id.tabLayout);
        viewPager = itemView.findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPager.setAdapter(viewPagerAdapter);

        recycler_view.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH){
                    if (viewPagerAdapter.getPageTitle(viewPagerAdapter.getItemPosition(viewPager)).equals("Usuarios")){

                        viewPagerAdapter.addFragment(new SearchUsersFragment(),"Usuarios",editTextSearch.getText().toString());
                    }else{
                        viewPagerAdapter.addFragment(new SearchHashtagsFragment(),"Hashtags",editTextSearch.getText().toString()
                        );

                    }
                }
                return false;
            }
        });


        List<ExploreItem> exploreItems = new ArrayList<>();
/*
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("server_key","1539874186")
                .addFormDataPart("access_token",getAccessToken())
                .addFormDataPart("password",getPass())
                .addFormDataPart("username",getUsername())
                .addFormDataPart("offset","633")
                .addFormDataPart("limit","2")
                .addFormDataPart("v","1")
                .addFormDataPart("resource","post")
                .addFormDataPart("resource_id","fetch_home_posts")
                .build();
        Request request = new Request.Builder()
                .url("https://diys.co/endpoints/v1/post/fetch_explore")
                .method("POST",body)
                .build();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    JSONObject json = new JSONObject(response.body().string());
                    Log.d(TAG, String.valueOf(json));
                    Log.d(TAG, response.body().string());
                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject post = array.getJSONObject(2).getJSONObject();

                        exploreItems.add(new ExploreItem(
                                post.getString("image")
                        ));
                    }
                    ExploreAdapter adapter = new ExploreAdapter(getContext(), exploreItems);
                    recycler_view.setAdapter(adapter);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
*/
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_POSTS,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            Log.d("ApiResponse", response);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject post = array.getJSONObject(i);

                                exploreItems.add(new ExploreItem(
                                        post.getString("file")
                                ));
                            }
                            ExploreAdapter adapter = new ExploreAdapter(getContext(), exploreItems);
                            recycler_view.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ApiResponse", String.valueOf(error));

            }
        });

        //Volley.newRequestQueue(getContext()).add(stringRequest); 

        editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Probar comportamiento de la búsqueda y diseño del itemSearch
                recycler_view.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
            }
        });
        return itemView;
    }
    public String getData(){
        return editTextSearch.getText().toString();
    }
/*
D/SearchFragment: {"code":"200","status":"OK","data":[{
                                                  "post_id":1413,
                                                  "post_key":"",
                                                  "user_id":3,
                                                  "description":"⁣ ¿Cuánto metros en tela se necesitan para hacer juego de sábanas? Tela: para  cama infividual: 3,75 metros de tela de 2,40 de ancho.  Tela: para  cama matrimonial: 5 metros de tela de 2,40 de ancho. 1 metro cinta elástica delgada.  #sabanas #costura #moldesderopa #confeccion",
                                                  "link":"",
                                                  "thumbnail":"",
                                                  "video_location":null,
                                                  "youtube":"",
                                                  "vimeo":"",
                                                  "dailymotion":"",
                                                  "playtube":"",
                                                  "mp4":null,
                                                  "time":"1630417398",
                                                  "type":"image",
                                                  "registered":"2021\/8",
                                                  "views":0,"boosted":0,
                                                  "stream_name":"",
                                                  "live_time":0,
                                                  "agora_resource_id":null,
                                                  "agora_sid":null,
                                                  "live_ended":0,
                                                  "avatar":"https:\/\/diys.co\/media\/upload\/photos\/2021\/04\/ybe6SmBOxEFRNCYo77cY5IgXFpC19pmzbn9yjm5E4E87mmi58n_20_05aa82c747723cb193dd9cbd95a70656_image.jpg",
                                                  "username":"PatronesGratisdeCostura",
                                                  "likes":0,
                                                  "votes":0,
                                                  "media_set":[{
                                                    "id":693,
                                                    "post_id":1413,
                                                    "user_id":3,
                                                    "file":"https:\/\/diys.co\/media\/upload\/photos\/2021\/08\/brOkvVzREdDGUUzSV52e891FaEDcikMhee3HWURALdTAXFWYEu_31_ca00adb82ffabea6f341197e61912ed0_image.jpg",
                                                    "extra":"moldes de sabanas.jpg"}],
                                                  "comments":[],
                                                  "is_owner":false,
                                                  "is_liked":false,
                                                  "is_saved":false,
                                                  "reported":false
                                                  ,"user_data":{
                                                    "user_id":3,
                                                    "username":"PatronesGratisdeCostura",
                                                    "email":"angelica@gmail.com",
                                                    "ip_address":"186.54.34.0",
                                                    "password":"$2y$10$ifyY\/n2nQx6DJsZtBgBJaOHg01kmx9mFQkZF0zT87EeLytNrtOSDm",
                                                    "fname":"Patrones",
                                                    "lname":"Gratis",
                                                    "gender":"female",
                                                    "email_code":"",
                                                    "language":"spanish",
                                                    "avatar":"https:\/\/diys.co\/media\/upload\/photos\/2021\/04\/ybe6SmBOxEFRNCYo77cY5IgXFpC19pmzbn9yjm5E4E87mmi58n_20_05aa82c747723cb193dd9cbd95a70656_image.jpg",
                                                    "cover":"media\/img\/d-cover.jpg",
                                                    "country_id":12,
                                                    "about":"Patrones Gratis de Costura Para modistas y costureras.",
                                                    "google":"",
                                                    "facebook":"",
                                                    "twitter":"",
                                                    "website":"https:\/\/mimundodemoda.com\/","active":1,"admin":0,"verified":0,
                                                    "last_seen":"1630420177",
                                                    "registered":"2021\/1","is_pro":0,"posts":0,"p_privacy":"2"
                                                    ,"c_privacy":"1","n_on_like":"1","n_on_mention":"1","n_on_comment":"1",
                                                    "n_on_follow":"1","n_on_comment_like":"1","n_on_comment_reply":"1","startup_avatar":1,
                                                    "startup_info":1,"startup_follow":1,"src":"","login_token":"","search_engines":"1",
                                                    "mode":"day","device_id":"15eb31cc-e880-4efe-ac36-9a173ae6b7e4","balance":"0",
                                                    "wallet":"0.00","conversation_id":"","referrer":0,"profile":1,"business_account":0,
                                                    "paypal_email":"","b_name":"","b_email":"","b_phone":"","b_site":"","b_site_action":"",
                                                    "uploads":0,"address":"","city":"","state":"","zip":0,"phone_number":"","name":"Patrones Gratis",
                                                    "uname":"PatronesGratisdeCostura","url":"https:\/\/diys.co\/PatronesGratisdeCostura",
                                                    "edit":"https:\/\/diys.co\/settings\/general\/PatronesGratisdeCostura","followers":false,"following":false,"favourites":0,"posts_count":31},
                                                    "is_verified":0,"is_should_hide":false,"name":"Patrones Gratis","time_text":"6 dias hace"}
                                                    ,{
                                                    "post_id":1399,
                                                    "post_key":"",
                                                    "user_id":897,
                                                    "description":"#ara",
                                                    "link":"","thumbnail":"","video_location":null,"youtube":"","vimeo":"","dailymotion":"","playtube":"","mp4":null,
                                                    "time":"1622506644","type":"image","registered":"2021\/6","views":0,"boosted":0,"stream_name":"",
                                                    "live_time":0,"agora_resource_id":null,"agora_sid":null,"live_ended":0,
                                                    "avatar":"https:\/\/diys.co\/media\/upload\/photos\/2021\/06\/8PLNhW7R3oGET9mqeFYpEAUFw1JgMrp78CYJpTEfguGbFGGOG1_01_b291e5922d5f053973f397776e675462_image.jpg",
                                                    "username":"AraceliBriseñoValdez","likes":0,"votes":0,
                                                    "media_set":[{"id":682,"post_id":1399,"user_id":897,"file":"https:\/\/diys.co\/media\/upload\/photos\/2021\/06\/9H1MfgBtH1GUzcmh4iuBHOdazx5ueATMLvxJQ281BCLz9UwQ3y_01_f04658c94e7873f85f6eafb212aba95a_image.jpg","extra":"inbound5983883970020459664.jpg"}],"comments":[],"is_owner":false,"is_liked":false,"is_saved":false,"reported":false,"user_data":{"user_id":897,"username":"AraceliBriseñoValdez","email":"ara_22azul@hotmail.com","ip_address":"","password":"$2y$10$2M25IpXP7KTWGr\/nFXjQx.eZASL7oprJAWm86vmxpnZVg7B1AVVDW","fname":"Araceli","lname":"Briseño Valdez","gender":"male","email_code":"ec6b92e85828b424a2a3e86ca27110f9d08f2
*/
}