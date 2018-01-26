package com.example.michele.myparty_new;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PartyFragmentQR extends Fragment {

    private String URL;
    private String partyId;
    private String userId;
    private String partyName;
    private String partyOrgName;
    private String partyCity;
    private String dateParsed;
    private String partyAddress;
    private String partyLocation;
    private int partyTickets;
    private double partyPrice;
    private String partyDescription;
    private String ticketValue;
    private TextView txtName;
    private TextView txtOrgName;
    private TextView txtDate;
    private TextView txtLocation;
    private TextView txtTickets;
    private TextView txtPrice;
    private TextView txtDescription;
    private ImageView imgQrCode;
    private LinearLayout l;
    private LinearLayout l1;
    private LinearLayout l2;


    public static PartyFragmentQR newInstance() {
        PartyFragmentQR fragment = new PartyFragmentQR();
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
        View v = inflater.inflate(R.layout.party_details_fragment_qr, container, false);
        l = v.findViewById(R.id.linear_layout);
        l1 = v.findViewById(R.id.layout_progress);
        l2 = v.findViewById(R.id.layout_progress2);
        txtName = v.findViewById(R.id.name);
        // Let the view scroll if name is too long
        txtName.setSelected(true);
        txtOrgName = v.findViewById(R.id.orgName);
        txtOrgName.setSelected(true);
        txtDate = v.findViewById(R.id.date);
        txtLocation = v.findViewById(R.id.location);
        txtLocation.setSelected(true);
        txtTickets = v.findViewById(R.id.tickets);
        txtPrice = v.findViewById(R.id.price);
        txtDescription = v.findViewById(R.id.description);
        imgQrCode = v.findViewById(R.id.qr_code);
        actionPopulateActivity();
        actionGetTicketValue(imgQrCode);
        return v;
    }

    // Populate the activity with the party data
    public void actionPopulateActivity() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL + "parties/details/" +partyId,
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
                            l.setVisibility(View.GONE);
                            txtName.setText(partyName);
                            txtOrgName.setText(partyOrgName);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date d = format.parse(String.valueOf(((HomeActivity) getActivity()).getItemDate()));
                                format.applyPattern("dd/MM/yyyy");
                                dateParsed = String.valueOf(format.format(d));
                                txtDate.setText(String.valueOf(format.format(d)));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            txtLocation.setText(partyLocation);
                            String t = getString(R.string.participants)+ " " +String.valueOf(partyTickets);
                            txtTickets.setText(t);
                            DecimalFormat formatter = new DecimalFormat("€ 0.00");
                            txtPrice.setText(formatter.format(partyPrice));
                            txtDescription.setText(partyDescription);
                            l1.setVisibility(View.GONE);
                            l.setVisibility(View.VISIBLE);
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

    // Get the ticket value with which create the qr code
    public void actionGetTicketValue(final ImageView v) {
        StringRequest req = new StringRequest(Request.Method.GET,
                URL + "tickets/" +partyId+ "/" +userId ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response) {
                            case "No ticket":
                                AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
                                build.setTitle(R.string.error);
                                build.setMessage(R.string.error_id_dialog);
                                build.setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface d, int id) {
                                                // Do nothing
                                            }
                                        });
                                build.setIcon(android.R.drawable.ic_dialog_alert);
                                build.show();
                                break;
                            default:
                                ticketValue = response;
                                new DownloadImageTask(v).execute("http://api.qrserver.com/v1/create-qr-code/?data=[" +ticketValue+ "]&size=[300]x[300]");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(req);
    }

    // Intern class to download the qr code
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        boolean error = false;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                error = true;
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            if (!error) {
                l2.setVisibility(View.GONE);
                imgQrCode.setVisibility(View.VISIBLE);
            }
        }
    }

}
