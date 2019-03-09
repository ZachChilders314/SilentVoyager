package com.x10host.burghporter31415.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.x10host.burghporter31415.silentvoyager.ConnectionAdd;
import com.x10host.burghporter31415.silentvoyager.DashboardInfo;
import com.x10host.burghporter31415.silentvoyager.R;
import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;

import java.util.ArrayList;

public class RequestsFragment extends Fragment implements AdapterView.OnItemClickListener {

    final FormPost<String, String> resultFormPost = new FormPost<>();
    final PHPPage resuleDeletePage = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/user_match.php");

    private ArrayAdapter<String> adapter;

    private ListPopupWindow listPopupWindow;
    private Button btnRequestType;

    private String[] requestType = {"Received Requests", "Sent Requests"};

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_requests, container, false);

        final Bundle bundle = this.getArguments();

        String[] arr = bundle.getStringArray("requestResults");

        /*Need to create a custom list popup window due to glitch in current Android version*/
        /*USED THIS RESOURCE TO COPY CODE: http://www.informit.com/articles/article.aspx?p=2078060&seqNum=4*/

        btnRequestType = (Button) rootView.findViewById(R.id.btnRequestType);

        listPopupWindow = new ListPopupWindow(getContext());
        listPopupWindow.setAdapter(new ArrayAdapter(
                getContext(), R.layout.text_view_list, requestType)
        );

        listPopupWindow.setAnchorView(btnRequestType);
        listPopupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(RequestsFragment.this);

        btnRequestType.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               listPopupWindow.show();
           }
        });

        /*************************************************************************************/

        resultFormPost.addPair("username", bundle.getString("username"));
        resultFormPost.addPair("password", bundle.getString("password"));

        final ArrayList<String> listItems = new ArrayList<String>();

        if(arr.length == 0 || arr[0].isEmpty()) { return rootView; }

        for(int i = 0; i < arr.length; i++) {
            listItems.add(arr[i].trim());
        }

        adapter = new ArrayAdapter<String>(getContext(), R.layout.text_view_list, listItems);

        ListView listView = (ListView)rootView.findViewById(R.id.list_view_requests);
        listView.setAdapter(adapter);

        /*Start a new Activity upon a click--sending over the appropriate data in a Bundle*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final int pos = (int) id;

                /*FROM: https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android*/
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){

                            case DialogInterface.BUTTON_POSITIVE:

                                resultFormPost.addPair("requested", listItems.get((int)pos));

                                Thread thread = new Thread(new Runnable(){
                                    @Override
                                    public void run() {
                                        resultFormPost.submitPost(resuleDeletePage, MethodType.POST);
                                    }
                                });

                                try {
                                    thread.start();
                                    thread.join();

                                    listItems.remove(pos);
                                    adapter.notifyDataSetChanged(); /*Update the Data*/

                                    Toast.makeText(getContext(), "Request to " + listItems.get(pos) + " has been removed.",
                                            Toast.LENGTH_LONG).show();

                                } catch (Exception e) {
                                    //TODO
                                }

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                /*FROM: https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android*/
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Undo Request?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }

        });

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {

        btnRequestType.setText(requestType[position]);
        listPopupWindow.dismiss();

    }
}
