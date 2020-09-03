package ai.extime.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.extime.R;

import ai.extime.Adapters.CompanyContactAdapterNewTab;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.Contact;

public class CompanyContactsFragment extends Fragment {

    public View mainView;

    private Contact company;

    private ContactAdapter adapterC;

    private Toolbar toolbarC;

    private Activity activityApp;

    private RecyclerView recyclerViewContacts;

    public SharedPreferences mPref;

    CompanyContactAdapterNewTab companyContactAdapterNewTab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_company_contacts, viewGroup, false);

        adapterC = ((Postman) activityApp).getAdapter();
        toolbarC = ((Postman) activityApp).getToolbar();

        getDataFromBundle();

        initViews();

        return mainView;
    }

    public void updateConatcts(){
        ((ProfileFragment) getParentFragment()).upd();
    }

    public void setTime(){

        ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
        ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));

        //((ProfileFragment) getParentFragment()).companyContactsFragment
        //sortByTime();
    }

    public void setTAsc(){
        ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
        ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));

        TextView sortText = (TextView) mainView.findViewById(R.id.sortText);
        sortText.setText("A-Z");
    }

    public void setTimeD(){
        ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
        ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));

        TextView sortText = (TextView) mainView.findViewById(R.id.sortText);
        sortText.setText("Z-A");
    }

    public void initViews(){

        mainView.findViewById(R.id.sortElementsCompany).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortList();


            }
        });

        mainView.findViewById(R.id.sortByPopular).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mainView.findViewById(R.id.sortByTimeCompany).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
                ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
                sortByTime();

                updateConatcts();
            }
        });

        if(company.listOfContacts.size() == 1){
            ((TextView)mainView.findViewById(R.id.count_contact_company_preview)).setText("1 contact");
        }else{
            ((TextView)mainView.findViewById(R.id.count_contact_company_preview)).setText(String.valueOf(company.listOfContacts.size())+" contacts");
        }

        recyclerViewContacts = mainView.findViewById(R.id.recycler_view);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(activityApp));
        companyContactAdapterNewTab = new CompanyContactAdapterNewTab(company.listOfContacts, mainView.getContext(), getParentFragment(), company);
        recyclerViewContacts.setAdapter(companyContactAdapterNewTab);

        initAdapter();
    }

    public void initAdapter() {
        mPref = getContext().getSharedPreferences("Sort", Context.MODE_PRIVATE);
        String sort = mPref.getString("typeSortCompany", "sortByAsc");

        if (sort.equals("sortByAsc")) {
            companyContactAdapterNewTab.sortByAsc();
            sortedDesc = false;
            ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
            ((TextView) mainView.findViewById(R.id.sortText)).setText("A-Z");
            ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else if (sort.equals("sortByDesc")) {
            companyContactAdapterNewTab.sortByDesc();
            sortedDesc = true;
            ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
            ((TextView) mainView.findViewById(R.id.sortText)).setText("Z-A");
            ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        } else if (sort.equals("sortByTimeAsc")) {
            companyContactAdapterNewTab.sortByTimeAsc();
            sortTimeAsc = false;
            ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
            ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
        } else if (sort.equals("sortByTimeDesc")) {
            companyContactAdapterNewTab.sortByTimeDesc();
            sortTimeAsc = true;
            ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.gray));
            ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
        }
    }

    private boolean sortedDesc;

    private boolean sortTimeAsc = false;

    public void sortByTime() {

        if (!sortTimeAsc) {
            sortByTimeDesc();

            sortTimeAsc = true;
        } else {
            sortByTimeAsc();
            sortTimeAsc = false;
        }
        //companyAdapter.notifyDataSetChanged();
    }

    public void sortByTimeAsc() {
        sortTimeAsc = false;
        companyContactAdapterNewTab.sortByTimeAsc();
    }

    public void sortByTimeDesc() {
        sortTimeAsc = true;
        companyContactAdapterNewTab.sortByTimeDesc();
    }

    public void sortList() {
        if (sortedDesc) {
            sortArrayByAsc();
            sortedDesc = false;

            updateConatcts();
            return;
        }
        sortArrayByDesc();
        sortedDesc = true;

        updateConatcts();
    }

    private void sortArrayByDesc() {
        ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
        ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        companyContactAdapterNewTab.sortByDesc();
        TextView sortText = (TextView) mainView.findViewById(R.id.sortText);
        sortText.setText("Z-A");
    }

    private void sortArrayByAsc() {
        ((TextView) mainView.findViewById(R.id.sortText)).setTextColor(getResources().getColor(R.color.primary));
        ((ImageView) mainView.findViewById(R.id.timeSort)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        companyContactAdapterNewTab.sortByAsc();
        TextView sortText = (TextView) mainView.findViewById(R.id.sortText);
        sortText.setText("A-Z");
    }

    private void getDataFromBundle() {
        Bundle args = getArguments();
        company = (Contact) args.getSerializable("selectedContact");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            activityApp = (Activity) context;
        }
    }

}
