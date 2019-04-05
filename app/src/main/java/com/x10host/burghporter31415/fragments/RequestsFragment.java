package com.x10host.burghporter31415.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.Toast;

import com.x10host.burghporter31415.silentvoyager.R;
import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class RequestsFragment extends Fragment implements AdapterView.OnItemClickListener {

    /*https://developer.android.com/training/basics/fragments/communicating*/
    static OnConnectionAddedListener callback;

    public static void setOnConnectionAddedListener(OnConnectionAddedListener callbackOrig) {
        callback = callbackOrig;
    }

    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    public interface OnConnectionAddedListener {
        public void onConnectionAdded(String connection);
    }

    private static ArrayAdapter<String> adapter;
    final ArrayList<String> listItems = new ArrayList<String>();

    private ListPopupWindow listPopupWindow;
    private Button btnRequestType;

    private String[] requestType = {"Received Requests", "Sent Requests"};
    private DialogType currentType = DialogType.RECEIVED_REQUEST;

    private String[][] resultDuo;

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        listItems.clear();

        View rootView = inflater.inflate(R.layout.fragment_requests, container, false);

        final Bundle bundle = this.getArguments();
        resultDuo = new String[][] {bundle.getStringArray("receivedResults"), bundle.getStringArray("requestResults")};

        String[] arr = bundle.getStringArray("receivedResults");

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
                AlertDialog.Builder builder = null;

                if(currentType == DialogType.RECEIVED_REQUEST) {
                    builder = getDialogInterfaceBuilder(bundle, DialogType.RECEIVED_REQUEST, position, listItems);
                } else {
                    builder = getDialogInterfaceBuilder(bundle, DialogType.SENT_REQUEST, position, listItems);
                }

                builder.show();
                adapter.notifyDataSetChanged(); /*Update the Data*/

            }

        });

        return rootView;
    }

    private AlertDialog.Builder getDialogInterfaceBuilder(final Bundle bundle,
                                                                 DialogType type, final int itemPosition, final ArrayList<String> listItems) {

        final PHPPage removeRequest = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/remove_request.php");
        final PHPPage acceptConnection = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/add_connection.php");

        final FormPost<String, String> resultFormPost = new FormPost<>();
        final String selectedItem = listItems.get(itemPosition);

        resultFormPost.addPair("username", bundle.getString("username"));
        resultFormPost.addPair("password", bundle.getString("password"));

        DialogInterface.OnClickListener[] listeners = {

            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {

                        case DialogInterface.BUTTON_POSITIVE:

                            resultFormPost.addPair("user2", FragmentUtils.returnParsedUsernameCluster(selectedItem));
                            resultFormPost.addPair("requested", FragmentUtils.returnParsedUsernameCluster(selectedItem));

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //resultFormPost.submitPost(acceptConnection, MethodType.POST);
                                    //resultFormPost.submitPost(removeRequest, MethodType.POST);
                                }
                            });

                            try {
                                thread.start();
                                thread.join();

                                Toast.makeText(getActivity(), "Accepted Connection for " + listItems.get((int) itemPosition), Toast.LENGTH_LONG).show();

                                /*Callback to dashboard so that the information can be updated in other fragments*/


                                listItems.remove(itemPosition);
                                adapter.notifyDataSetChanged();

                                callback.onConnectionAdded(selectedItem);

                            } catch (Exception e) {
                                //TODO
                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            resultFormPost.addPair("requested", bundle.getString("username"));

                            resultFormPost.removePair("username");
                            resultFormPost.addPair("username", selectedItem);

                            Thread thread2 = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    resultFormPost.submitPost(removeRequest, MethodType.POST);
                                }
                            });

                            try {
                                thread2.start();
                                thread2.join();

                                listItems.remove(itemPosition);
                                adapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                //TODO
                            }

                            break;
                    }
                }
            },

            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {

                        case DialogInterface.BUTTON_POSITIVE:

                            resultFormPost.addPair("requested", selectedItem);

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    resultFormPost.submitPost(removeRequest, MethodType.POST);
                                }
                            });

                            try {
                                thread.start();
                                thread.join();

                                listItems.remove(itemPosition);
                                adapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                //TODO
                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No need to do anything here
                            break;
                    }
                }
            }
        };

        AlertDialog.Builder[] builders = {new AlertDialog.Builder(getContext()), new AlertDialog.Builder(getContext())};


        builders[0].setMessage("Accept Connection?").setPositiveButton("Yes", listeners[0])
                .setNegativeButton("No", listeners[0]);

        builders[1].setMessage("Undo Request?").setPositiveButton("Yes", listeners[1])
                .setNegativeButton("No", listeners[1]);

        if(type==DialogType.RECEIVED_REQUEST) {
            return builders[0];
        } else {
            return builders[1];
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {

        listItems.clear();

        if(!this.resultDuo[position][0].isEmpty()) {
            for(String item : this.resultDuo[position]) {
                listItems.add(item);
            }
        }

        if(position == 0) {
            currentType = DialogType.RECEIVED_REQUEST;
        }
        else {
            currentType = DialogType.SENT_REQUEST;
        }

        btnRequestType.setText(requestType[position]);
        listPopupWindow.dismiss();

        adapter.notifyDataSetChanged();

    }
}
