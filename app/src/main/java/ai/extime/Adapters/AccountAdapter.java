package ai.extime.Adapters;

import android.accounts.Account;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.extime.R;

import java.util.ArrayList;

import ai.extime.Models.Contact;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private View mainView;

    private ArrayList<Account> listOfAccount;

    private Context context;

    private ArrayList<Account> listSelectId;

    private CheckBox main_checkBox;

    private TextView apply;

    ArrayList<Boolean> listCheck;

    TextView textCount;

    TextView countNew;
    CheckBox checkBox;



    public AccountAdapter(ArrayList<Account> list, Context context, CheckBox main_checkBox, TextView textView, TextView textCount, ArrayList<Boolean> listCheck, TextView countNew, CheckBox checkBox) {
        this.listOfAccount = list;
        this.context = context;
        this.listSelectId = new ArrayList<>();
        this.main_checkBox = main_checkBox;
        this.apply = textView;
        this.listCheck = listCheck;
        this.textCount = textCount;

        this.checkBox = checkBox;
        this.countNew = countNew;

        if (listCheck != null) {
            for (int i = 0; i < listCheck.size(); i++) {
                if (listCheck.get(i)) {
                    listSelectId.add(this.listOfAccount.get(i));
                }
            }
        }

    }


    public ArrayList<Account> getListSelectId() {
        return listSelectId;
    }

    public void updateAllSelect(boolean check) {
        if (check) {
            listSelectId.clear();
            for (Account a : listOfAccount) {
                listSelectId.add(a);
            }
        } else {
            listSelectId.clear();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_card, viewGroup, false);


        return new AccountViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int i) {
        final Account account = listOfAccount.get(i);


        int nameHash = account.name.hashCode();
        int color = Color.rgb(Math.abs(nameHash * 28439) % 255, Math.abs(nameHash * 211239) % 255, Math.abs(nameHash * 42368) % 255);


        GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.blue_circle).mutate();
        circle.setColor(color);
        holder.avatar.setBackground(circle);
        String initials = getInitials(account.name);
        holder.initials.setText(initials);

        holder.name.setText("Error get data");

        holder.email.setText(account.name);

        if(listOfAccount.size() == listSelectId.size()){
            checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }

        if (listSelectId.contains(account)) {
            holder.checkbox.setChecked(true);
            if (listOfAccount.size() > 1)
                textCount.setText(listOfAccount.size() + " accounts " + listSelectId.size() + " selected");
            else
                textCount.setText("1 account " + listSelectId.size() + " selected");



            countNew.setText(String.valueOf(listSelectId.size()));

        } else {
            holder.checkbox.setChecked(false);
            if (listOfAccount.size() > 1)
                textCount.setText(listOfAccount.size() + " accounts " + listSelectId.size() + " selected");
            else
                textCount.setText("1 account " + listSelectId.size() + " selected");

            countNew.setText(String.valueOf(listSelectId.size()));
        }


        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkbox.isChecked()) {
                    listSelectId.add(account);
                    if (listOfAccount.size() == listSelectId.size()) {
                        main_checkBox.setChecked(true);
                    }

                    apply.setTextColor(context.getResources().getColor(R.color.primary));

                    if (listOfAccount.size() > 1)
                        textCount.setText(listOfAccount.size() + " accounts " + listSelectId.size() + " selected");
                    else
                        textCount.setText("1 account " + listSelectId.size() + " selected");

                    countNew.setText(String.valueOf(listSelectId.size()));

                    if(listOfAccount.size() == listSelectId.size()){
                        checkBox.setChecked(true);
                    }else{
                        checkBox.setChecked(false);
                    }

                } else {
                    listSelectId.remove(account);

                    if (listSelectId.size() == 0) {
                        apply.setTextColor(context.getResources().getColor(R.color.gray));
                    }
                    main_checkBox.setChecked(false);

                    if (listOfAccount.size() > 1)
                        textCount.setText(listOfAccount.size() + " accounts " + listSelectId.size() + " selected");
                    else
                        textCount.setText("1 account " + listSelectId.size() + " selected");

                    countNew.setText(String.valueOf(listSelectId.size()));

                    if(listOfAccount.size() == listSelectId.size()){
                        checkBox.setChecked(true);
                    }else{
                        checkBox.setChecked(false);
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfAccount.size();
    }


    private String getInitials(String name) {
        String initials = "";
        if (name != null && !name.isEmpty()) {
            String names[] = name.split("\\s+");
            for (String namePart : names) {
                if (namePart != null && namePart.length() > 0)
                    initials += namePart.charAt(0);
            }
        }
        return initials.toUpperCase();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        TextView name, email, initials;
        CheckBox checkbox;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.accountAvatar);
            name = itemView.findViewById(R.id.accountName);
            email = itemView.findViewById(R.id.accountEmail);
            checkbox = itemView.findViewById(R.id.all_accounts_select);
            initials = itemView.findViewById(R.id.accountInitials);

        }
    }
}
