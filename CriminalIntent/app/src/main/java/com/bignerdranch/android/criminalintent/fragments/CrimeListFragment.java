package com.bignerdranch.android.criminalintent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bignerdranch.android.criminalintent.R;
import com.bignerdranch.android.criminalintent.activities.CrimePagerActivity;
import com.bignerdranch.android.criminalintent.model.Crime;
import com.bignerdranch.android.criminalintent.model.CrimeLab;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private static final int REQUEST_CRIME = 1;
    private RecyclerView mCrimeRecycleView;
    private CrimeAdapter mAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecycleView = (RecyclerView) view.findViewById(R.id.crime_recycle_view);
        mCrimeRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getmCrimes();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecycleView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> mCrimes) {
            this.mCrimes = mCrimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private Crime mCrime;

        public CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(crime.getmTitle());
            mDateTextView.setText(crime.getmDate().toString());
            mSolvedCheckBox.setChecked(crime.ismSolved());
        }

        @Override
        public void onClick(View view) {
            startActivityForResult(CrimePagerActivity.newIntent(getActivity(), mCrime.getmId()), REQUEST_CRIME);
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                newCrimeButton();
                return true;
            case R.id.menu_item_show_subtitle:
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void newCrimeButton() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getmId());
        startActivity(intent);
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getmCrimes().size();
        String subtitle = getString(R.string.subtitle_format, String.valueOf(crimeCount));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(subtitle);
    }
}
