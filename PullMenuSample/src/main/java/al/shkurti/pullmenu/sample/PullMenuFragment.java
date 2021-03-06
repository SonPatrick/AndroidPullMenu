package al.shkurti.pullmenu.sample;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import java.util.ArrayList;

import al.shkurti.pullmenu.library.ActionBarPullMenu;
import al.shkurti.pullmenu.library.PullMenuLayout;
import al.shkurti.pullmenu.library.listeners.OnRefreshListener;

/**
 * Created by Armando Shkurti on 2015-01-19.
 */
public class PullMenuFragment extends Fragment implements OnRefreshListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private PullMenuLayout mPullMenuLayout;
    ToolTipView myToolTipView;

    public static PullMenuFragment newInstance(int sectionNumber) {
        PullMenuFragment fragment = new PullMenuFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PullMenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Now find the PullMenuLayout and set it up
        mPullMenuLayout = (PullMenuLayout) rootView.findViewById(R.id.pm_layout);

        ArrayList<String> mList = new ArrayList<String>();
        mList.add("Top Stories");
        mList.add("Most Recent");
        mList.add("Interest");
        mList.add("Refresh");

        // We can now setup the PullMenuLayout
        ActionBarPullMenu.from(getActivity())
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullMenuLayout,android.R.color.white,
                        getResources().getColor(R.color.menuColor),
                        getResources().getColor(R.color.progressBarColor),mList);


        SharedPreferences prefs = getActivity().getSharedPreferences("pullMenu", 0);
        String restoredText = prefs.getString("menu", null);
        if(restoredText==null) {
            TextView mTextView = (TextView) rootView.findViewById(R.id.filler_text);

            int actionBarHeight = 0;
            TypedValue tv = new TypedValue();
            if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            }
            mTextView.setPadding(0,actionBarHeight , 0, 0);

            ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) rootView.findViewById(R.id.tooltipframelayout);

            ToolTip toolTip = new ToolTip()
                    .withText(getResources().getString(R.string.tip_descr))
                    .withTextColor(getResources().getColor(android.R.color.white))
                    .withColor(getResources().getColor(R.color.tip_color));

            myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, mTextView);
        }
        return rootView;
    }

    @Override
    public void onRefreshStarted(View view, int position, String selectedField) {
        // Hide the list
        Toast.makeText(getActivity(), position + " # " + selectedField, Toast.LENGTH_SHORT).show();

        if (myToolTipView != null) {
            myToolTipView.remove();
            myToolTipView = null;

            TextView mTextView = (TextView) getActivity().findViewById(R.id.filler_text);
            mTextView.setPadding(0,0,0,0);

            SharedPreferences.Editor editor = getActivity().getSharedPreferences("pullMenu", 0).edit();
            editor.putString("menu", "Opened");
            editor.commit();
        }

        /**
         * Simulate Refresh with 5 seconds sleep
         */
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(Constants.SIMULATED_REFRESH_LENGTH);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                // Notify PullMenuLayout that the refresh has finished
                mPullMenuLayout.setRefreshComplete();

            }
        }.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((PullMenuActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

}
