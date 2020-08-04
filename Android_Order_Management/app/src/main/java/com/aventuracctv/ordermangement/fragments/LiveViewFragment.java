package com.aventuracctv.ordermangement.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.aventuracctv.ordermangement.R;
import com.aventuracctv.ordermangement.activities.MainActivity;
import com.aventuracctv.ordermangement.adapters.BuildListAdapter;
import com.aventuracctv.ordermangement.adapters.ItemTouchAdapter;
import com.aventuracctv.ordermangement.base.App;
import com.aventuracctv.ordermangement.data.BuildItem;
import com.aventuracctv.ordermangement.data.User;
import com.aventuracctv.ordermangement.interfaces.AssignBuildCallback;
import com.aventuracctv.ordermangement.interfaces.BuildSwapCallback;
import com.aventuracctv.ordermangement.interfaces.OpenPDFCallback;
import com.aventuracctv.ordermangement.net.ApiRequests;
import com.aventuracctv.ordermangement.net.GsonGetRequest;

import java.util.ArrayList;
import java.util.List;

public class LiveViewFragment extends Fragment implements AssignBuildCallback, OpenPDFCallback, BuildSwapCallback {

    private static final String TAG = "LiveViewFragment";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BuildListAdapter mAdapter;

    private LinearLayout mErrorView;
    private List<BuildItem> buildItems;
    private ProgressBar mProgressBar;
    private TextView customerName, buildItem;
    private Button btnAssign;
    ImageButton pdfPath;
    String currentUser;
    private String api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        customerName = (TextView) view.findViewById(R.id.tvCustomerName);
        buildItem = (TextView) view.findViewById(R.id.tvBuildItem);
        pdfPath = (ImageButton) view.findViewById(R.id.btnPDF);
        btnAssign = (Button) view.findViewById(R.id.btnAssign);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mErrorView = (LinearLayout) view.findViewById(R.id.error_view);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new UpdateRV().execute();
    }

    private void setView(@NonNull final ArrayList<BuildItem> buildItems) {
        this.buildItems = buildItems;

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mAdapter = new BuildListAdapter(this.getActivity(), this, this, this, buildItems);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchAdapter(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    public void getGsonData() {
        final GsonGetRequest<ArrayList<BuildItem>> gsonGetRequest =
                ApiRequests.getBuildItemArray
                        (
                                new Response.Listener<ArrayList<BuildItem>>() {
                                    @Override
                                    public void onResponse(ArrayList<BuildItem> buildItems) {
                                        // Deal with the DummyObject here
                                        mProgressBar.setVisibility(View.GONE);
                                        setView(buildItems);
                                    }
                                }
                                ,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Deal with the error here
                                        mProgressBar.setVisibility(View.GONE);
                                        //mErrorView.setVisibility(View.VISIBLE);
                                    }
                                },
                                api
                        );

        App.addRequest(gsonGetRequest, TAG);
    }

    @Override
    public void assignBuildItem(Button btn, BuildItem buildItem) {
        Intent intent;
        if (buildItem.isAssigned()) {
            //intent = new Intent("android.intent.action.AVENTURA_ASSIGN_BUILD_ITEM");
            //intent.putExtra("position", buildItem.getPosition());

            buildItem.setAssigned(false);
            btn.setSelected(false);
            btn.setText(R.string.btn_assign);

        } else {
            //intent = new Intent("android.intent.action.AVENTURA_UNASSIGN_BUILD_ITEM");
            //intent.putExtra("position", buildItem.getPosition());

            buildItem.setAssigned(true);
            btn.setSelected(true);
            btn.setText(R.string.btn_unassign);
        }
        //sendBroadcast(intent);
    }

    @Override
    public void openPDF(ImageButton btn, BuildItem buildItem) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file:" + buildItem.getpdfPath()), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void buildSwap(int fromPosition, int toPosition) {
        Intent intent = new Intent("android.intent.action.AVENTURA_SWAP_ITEM");
        intent.putExtra("fromposition", fromPosition);
        intent.putExtra("toposition", toPosition);
        getActivity().sendBroadcast(intent);
    }

    private class UpdateRV extends AsyncTask<Void, Void, Void> {

        protected void onPostExecute(Void result) {
            //adapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged(0, mAdapter.getItemCount());
            recyclerView.setAdapter(mAdapter);
        }

        //this gets an error when trying to register
        @Override
        protected Void doInBackground(Void... params) {
            try {
                api = User.apiKey;
                getGsonData();
                return null;

            } catch (Exception e) {
                Log.e("ServerList", e.getLocalizedMessage());
                return null;
            }
        }
    }

    void refreshItems() {
        // Load items
        // ...
        new UpdateRV().execute();
        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

}