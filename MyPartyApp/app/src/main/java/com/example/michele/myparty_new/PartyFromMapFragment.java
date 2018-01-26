package com.example.michele.myparty_new;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PartyFromMapFragment extends Fragment {

    private static String URL;
    private String partyId;
    private String partyName;
    private String partyOrgName;
    private String partyCity;
    private String partyAddress;
    private String dateParsed;
    private String partyLocation;
    private String partyDate;
    private String userId;
    private int partyTickets;
    private double partyPrice;
    private String partyDescription;
    private TextView txtName;
    private TextView txtOrgName;
    private TextView txtDate;
    private TextView txtLocation;
    private TextView txtTickets;
    private TextView txtPrice;
    private TextView txtDescription;
    private Button btnTicket;
    private LinearLayout l1;
    private LinearLayout l2;
    private LinearLayout progress;

    public static PartyFromMapFragment newInstance() {
        PartyFromMapFragment fragment = new PartyFromMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL = getString(R.string.server_address);
        partyId = ((HomeActivity) getActivity()).getItemId();
        userId = ((HomeActivity) getActivity()).getUserId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.party_details_fragment, container, false);
        l1 = v.findViewById(R.id.layout_progress);
        l2 = v.findViewById(R.id.linear_layout);
        progress = v.findViewById(R.id.layout_progress1);
        txtName = v.findViewById(R.id.name);
        txtName.setSelected(true);
        txtOrgName = v.findViewById(R.id.orgName);
        txtOrgName.setSelected(true);
        txtDate = v.findViewById(R.id.date);
        txtLocation = v.findViewById(R.id.location);
        txtLocation.setSelected(true);
        txtTickets = v.findViewById(R.id.tickets);
        txtPrice = v.findViewById(R.id.price);
        txtDescription = v.findViewById(R.id.description);
        btnTicket = v.findViewById(R.id.get_ticket);
        actionPopulateActivity();
        return v;
    }

    // Populate the activity with the party data
    public void actionPopulateActivity() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL +"parties/details/" +partyId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            partyName= response.getString("name");
                            partyOrgName = response.getString("organizerName");
                            partyCity = response.getString("city");
                            partyAddress = response.getString("address");
                            partyLocation = partyCity+ ", " +partyAddress;
                            partyTickets = response.getInt("tickets");
                            partyPrice = response.getDouble("price");
                            partyDescription = response.getString("description");
                            partyDate = response.getString("date");
                            txtName.setText(partyName);
                            txtOrgName.setText(partyOrgName);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date d = format.parse(partyDate);
                                format.applyPattern("dd/MM/yyyy");
                                dateParsed = String.valueOf(format.format(d));
                                txtDate.setText(dateParsed);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            txtLocation.setText(partyLocation);
                            String t = getString(R.string.participants)+ " " +String.valueOf(partyTickets);
                            txtTickets.setText(t);
                            DecimalFormat formatter = new DecimalFormat("€ 0.00");
                            txtPrice.setText(formatter.format(partyPrice));
                            txtDescription.setText(partyDescription);
                            btnTicket.setOnClickListener(new Button.OnClickListener() {
                                public void onClick(View v) {
                                    try {
                                        actionAddTicket();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            l1.setVisibility(View.GONE);
                            l2.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
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
        requestQueue.add(jsonObjectRequest);
    }

    // Create a ticket checking if the user already has it
    public void actionAddTicket() throws JSONException {
        btnTicket.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("partyId", partyId);
        jsonBody.put("userId", userId);
        final String requestBody = jsonBody.toString();
        StringRequest req = new StringRequest(Request.Method.POST,
                URL + "tickets/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response) {
                            case "Ok":
                                AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
                                build.setTitle(R.string.ticket_obtained);
                                build.setMessage(R.string.ticket_obtained_dialog);
                                build.setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface d, int id) {
                                                // Do nothing
                                            }
                                        });
                                build.setIcon(android.R.drawable.ic_dialog_info);
                                build.show();
                                break;
                            case "Already have ticket":
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(R.string.user_has_ticket);
                                builder.setMessage(R.string.user_has_ticket_dialog);
                                builder.setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface d, int id) {
                                                // Do nothing
                                            }
                                        });
                                builder.setIcon(android.R.drawable.ic_dialog_info);
                                builder.show();
                                break;
                            default:
                                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                                b.setTitle(R.string.error);
                                b.setMessage(R.string.error_dialog);
                                b.setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface d, int id) {
                                                // Do nothing
                                            }
                                        });
                                b.setIcon(android.R.drawable.ic_dialog_alert);
                                b.show();
                                break;
                        }
                        progress.setVisibility(View.GONE);
                        btnTicket.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
                        progress.setVisibility(View.GONE);
                        btnTicket.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(req);
    }

}
