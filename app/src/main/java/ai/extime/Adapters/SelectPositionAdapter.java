package ai.extime.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ai.extime.Interfaces.CompanySelectInterface;

import com.extime.R;

/**
 * Created by patal on 05.10.2017.
 */

public class SelectPositionAdapter extends RecyclerView.Adapter<SelectPositionAdapter.SelectCompanyViewHolder>  {

    private View mainView;

    private Context context;

    private CompanySelectInterface companySelectInterface;

    private List<String> listOfPosition = new ArrayList<>();

    private EditText fieldForSet;

    static class SelectCompanyViewHolder extends RecyclerView.ViewHolder {

        TextView hashTagValue;
        View card;

        SelectCompanyViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            hashTagValue = (TextView) itemView.findViewById(R.id.companyValue);
        }
    }

    public SelectPositionAdapter(List<String> listOfPosition, EditText fieldForSet){
        this.listOfPosition = listOfPosition;
        this.companySelectInterface = companySelectInterface;
        this.fieldForSet = fieldForSet;
    }

    @Override
    public SelectCompanyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_company_select, viewGroup, false);
        return new SelectCompanyViewHolder(mainView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final SelectCompanyViewHolder holder, int position) {
        holder.hashTagValue.setText(listOfPosition.get(position));
        holder.hashTagValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldForSet.setText(listOfPosition.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfPosition.size();
    }

}
