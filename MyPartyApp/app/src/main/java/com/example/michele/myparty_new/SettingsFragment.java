package com.example.michele.myparty_new;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.security.NoSuchAlgorithmException;

public class SettingsFragment extends Fragment implements View.OnClickListener{

    private String URL;
    private Button logout;
    private Button deleteAccount;
    private RelativeLayout rl;
    private LinearLayout progress;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, container, false);
        URL = getString(R.string.server_address) + "users/";
        logout = v.findViewById(R.id.logout);
        deleteAccount = v.findViewById(R.id.delete_account);
        rl = v.findViewById(R.id.relative_layout);
        progress = v.findViewById(R.id.layout_progress);
        logout.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);
        return v;
    }

    // Show the AlertDialog which asks to the user if he wants to delete his account
    public void actionDeleteAccount() {
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setTitle(R.string.delete_account);
        build.setMessage(R.string.delete_account_dialog);
        build.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int id) {
                        try {
                            deleteAccount();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                });
        build.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int id) {
                        // Do nothing
                    }
                });
        build.setIcon(android.R.drawable.ic_dialog_alert);
        build.show();
    }

    // Delete account
    public void deleteAccount() throws NoSuchAlgorithmException {
        rl.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        String userId = ((HomeActivity) getActivity()).getUserId();
        StringRequest req = new StringRequest(Request.Method.DELETE, URL +userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Ok")) {
                            AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
                            build.setTitle(R.string.account_deleted);
                            build.setMessage(R.string.account_deleted_dialog);
                            build.setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            Intent i = new Intent(getActivity(), MainActivity.class);
                                            startActivity(i);
                                            getActivity().finish();
                                        }
                                    });
                            build.setIcon(android.R.drawable.ic_dialog_info);
                            build.show();
                        }
                        else {
                            AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
                            build.setTitle(R.string.error);
                            build.setMessage(R.string.error_dialog);
                            build.setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            // Do nothing
                                        }
                                    });
                            build.setIcon(android.R.drawable.ic_dialog_alert);
                            build.show();
                        }
                        progress.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),error.toString(), Toast.LENGTH_LONG).show();
                        progress.setVisibility(View.GONE);
                        rl.setVisibility(View.VISIBLE);
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(req);
    }

    public void actionLogOut() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        if(v==logout)
            actionLogOut();
        else if (v==deleteAccount)
            actionDeleteAccount();
    }

}
