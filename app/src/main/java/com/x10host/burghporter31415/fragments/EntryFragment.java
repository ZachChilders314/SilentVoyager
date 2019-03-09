package com.x10host.burghporter31415.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.x10host.burghporter31415.silentvoyager.DashboardInfo;
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

public class EntryFragment extends Fragment {


    private ArrayAdapter<String> adapter;

    public EntryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_entry, container, false);

        final Bundle bundle = this.getArguments();
        String[] arr = bundle.getStringArray("results");

        ArrayList<String> listItems = new ArrayList<String>();

        if(arr.length == 0 || arr[0].isEmpty()) {return rootView; }

        for(int i = 0; i < arr.length; i++) {
            /*Unfold to get Date Format Displayed*/
            String[] dateStampArr = arr[i].split(",")[5]
                                            .split("_"); /*Username, Lat, Long, Altitude, City, DateStamp --> YEAR_MONTH_DAY_HOUR_MINUTE_SECOND*/

            listItems.add(FragmentUtils.returnDateStamp(dateStampArr, true));
        }

        adapter = new ArrayAdapter<String>(getContext(), R.layout.text_view_list, listItems);

        ListView listView = (ListView)rootView.findViewById(R.id.list_view_entry);
        listView.setAdapter(adapter);

        final String[] arr2 = arr; //We need a FINAL string array for the onclick listener--it cannot have a changed state.

        /*Start a new Activity upon a click--sending over the appropriate data in a Bundle*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String[] rowElements = arr2[(int)id].split(",");

                Intent intent = new Intent(getContext(), DashboardInfo.class);

                intent.putExtra("username", rowElements[0]);
                intent.putExtra("latitude", rowElements[1]);
                intent.putExtra("longitude", rowElements[2]);
                intent.putExtra("altitude", rowElements[3]);
                intent.putExtra("city", rowElements[4]);
                intent.putExtra("datestamp", rowElements[5]);
                intent.putExtra("PATH", bundle.getString("PATH"));

                startActivity(intent);

            }

        });

        return rootView;
    }

}
