package com.example.michele.myparty_new;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import pojo.PartyRow;
import utils.MyListAdapter;

public class TicketsFragment extends Fragment {

    private String URL;
    private LinearLayout progress;
    private LinearLayout no_parties;
    private ListView lv;

    public TicketsFragment() {
    }

    public static TicketsFragment newInstance() {
        TicketsFragment fragment = new TicketsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL = getString(R.string.server_address) + "tickets/" +((HomeActivity) getActivity()).getUserId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tickets_fragment, container, false);
        progress = v.findViewById(R.id.layout_progress);
        lv = v.findViewById(R.id.list_view);
        no_parties = v.findViewById(R.id.no_parties);
        actionGetPartiesTickets();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final PartyRow item = (PartyRow) parent.getItemAtPosition(position);
                ((HomeActivity) getActivity()).setItemId(item.getId());
                ((HomeActivity) getActivity()).setItemDate(String.valueOf(item.getDate()));
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, PartyFragmentQR.newInstance(), "party_details_qr")
                        .commit();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        lv.setVisibility(View.GONE);
        no_parties.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        actionGetPartiesTickets();
    }

    // Get the parties of which the user has ticket
    public void actionGetPartiesTickets() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<PartyRow> partyList = new ArrayList<>();
                        // Process the JSON
                        try{
                            // Loop through the array elements
                            for(int i=0;i<response.length();i++){
                                // Get current json object
                                JSONObject party = response.getJSONObject(i);
                                // Get the current party (json object) data
                                String partyId = party.getString("id");
                                String partyName = party.getString("name");
                                String partyOrgName = party.getString("organizerName");
                                String partyDate = party.getString("date");
                                int partyTickets = party.getInt("tickets");
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                java.util.Date parsed = format.parse(partyDate);
                                java.sql.Date sql = new java.sql.Date(parsed.getTime());
                                PartyRow pr = new PartyRow(partyId, partyName,partyOrgName,sql, partyTickets);
                                partyList.add(pr);
                            }
                            progress.setVisibility(View.GONE);
                            if (partyList.isEmpty())
                                no_parties.setVisibility(View.VISIBLE);
                            else {
                                MyListAdapter a = new MyListAdapter(getActivity(), R.layout.party, partyList);
                                lv.setAdapter(a);
                                lv.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException | ParseException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }

}
