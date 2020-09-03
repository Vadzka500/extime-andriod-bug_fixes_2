package ai.extime.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import ai.extime.Interfaces.CompanySelectInterface;
import ai.extime.Models.Contact;
import com.extime.R;

/**
 * Created by patal on 05.10.2017.
 */

public class SelectCompanyAdapter extends RecyclerView.Adapter<SelectCompanyAdapter.SelectCompanyViewHolder>  {

    private View mainView;

    private Context context;

    private CompanySelectInterface companySelectInterface;

    private List<Contact> listOfCompanies = new ArrayList<>();

    static class SelectCompanyViewHolder extends RecyclerView.ViewHolder {

        TextView hashTagValue;
        View card;

        SelectCompanyViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            hashTagValue = (TextView) itemView.findViewById(R.id.companyValue);
        }
    }

    public SelectCompanyAdapter(List<Contact> listOfCompanies,  CompanySelectInterface companySelectInterface){
        this.listOfCompanies = listOfCompanies;
        this.companySelectInterface = companySelectInterface;
    }

    @Override
    public SelectCompanyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_company_select, viewGroup, false);
        return new SelectCompanyViewHolder(mainView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final SelectCompanyViewHolder holder, int position) {
        holder.hashTagValue.setText(listOfCompanies.get(position).getName());
        holder.hashTagValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companySelectInterface.addSelectedCompany(listOfCompanies.get(position).getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfCompanies.size();
    }

}
