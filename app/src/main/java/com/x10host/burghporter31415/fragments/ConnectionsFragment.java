package com.x10host.burghporter31415.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.x10host.burghporter31415.silentvoyager.ConnectionSelected;
import com.x10host.burghporter31415.silentvoyager.Dashboard;
import com.x10host.burghporter31415.silentvoyager.DashboardInfo;
import com.x10host.burghporter31415.silentvoyager.Filter;
import com.x10host.burghporter31415.silentvoyager.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EntryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ConnectionsFragment extends Fragment {


    private ArrayAdapter<String> adapter;

    public ConnectionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_connections, container, false);

        final Bundle bundle = this.getArguments();
        //String[] arr = bundle.getStringArray("arr");
        String[] arr = bundle.getStringArray("connectionResults");

        ArrayList<String> listItems = new ArrayList<String>();

        if(arr.length == 0 || arr[0].isEmpty()) {return rootView; }

        for(int i = 0; i < arr.length; i++) { listItems.add(arr[i].trim()); }

        adapter = new ArrayAdapter<String>(getContext(), R.layout.text_view_list, listItems);

        ListView listView = (ListView)rootView.findViewById(R.id.list_view_connections);
        listView.setAdapter(adapter);

        final String[] arr2 = arr; //We need a FINAL string array for the onclick listener--it cannot have a changed state.

        /*Start a new Activity upon a click--sending over the appropriate data in a Bundle*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String connectionName = arr2[(int)id].substring(
                        0,
                        arr2[(int)id].indexOf("(@")
                );

                String connectionUsername = arr2[(int)id].substring(
                        arr2[(int)id].indexOf("(@") + 2,
                        arr2[(int)id].indexOf(")")
                );

                Intent intent = new Intent(getContext(), ConnectionSelected.class);

                intent.putExtra("connectionName", connectionName);
                intent.putExtra("connectionUsername", connectionUsername);

                intent.putExtra("connectionPrelim", arr2[(int)id]);

                intent.putExtra("username", bundle.getString("username"));
                intent.putExtra("password", bundle.getString("password"));

                intent.putExtra("PATH", bundle.getString("PATH"));

                startActivityForResult(intent, 100);

            }

        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*The request and result codes need to be set accordingly in the activities*/
        if (!(data == null) && !(data.getStringExtra("removed") == null)) {
            adapter.remove(data.getStringExtra("removed"));
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

}
