package ai.extime.Fragments;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.extime.R;

import java.util.ArrayList;

import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Utils.ClipboardType;
import de.hdodenhof.circleimageview.CircleImageView;

public class AvatarFragment extends Fragment {

    private View mainView;

    private Contact contact;

    private boolean isKeyboard;

    private boolean w1 = false, w2 = false;

    private ViewTreeObserver.OnGlobalLayoutListener checkKeyboardListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            mainView.getWindowVisibleDisplayFrame(r);
            int screenHeight = mainView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                isKeyboard = true;
                /*if (!w1) {
                    System.out.println("K OPEN NEW");

                    mainView.findViewById(R.id.avatarContact).setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

                    mainView.requestLayout();
                    initAvatar();
                    w1 = true;
                    w2 = false;
                }*/

                //mainView.findViewById(R.id.lin_p).setVisibility(View.GONE);

                if(mainView.findViewById(R.id.linear_no_photo).getVisibility() == View.VISIBLE){
                    mainView.findViewById(R.id.linear_no_photo).setVisibility(View.GONE);
                    mainView.findViewById(R.id.linear_no_photo_little).setVisibility(View.VISIBLE);
                }

            } else {
                isKeyboard = false;
                if(mainView.findViewById(R.id.linear_no_photo_little).getVisibility() == View.VISIBLE){
                    mainView.findViewById(R.id.linear_no_photo).setVisibility(View.VISIBLE);
                    mainView.findViewById(R.id.linear_no_photo_little).setVisibility(View.GONE);
                }

              /*  mainView.findViewById(R.id.lin_p).setVisibility(View.INVISIBLE);
                mainView.findViewById(R.id.lin_p).requestLayout();
                mainView.findViewById(R.id.lin_p).setVisibility(View.VISIBLE);*/
            /*

                //  if(!w2) {
                System.out.println("K CLOSE NEW");

                mainView.findViewById(R.id.avatarContact).setLayoutParams(new FrameLayout.LayoutParams(
                        mainView.getMeasuredWidth(), FrameLayout.LayoutParams.MATCH_PARENT));

                mainView.requestLayout();

                mainView.findViewById(R.id.avatarContact).invalidate();
                mainView.findViewById(R.id.avatarContact).requestLayout();
                initAvatar();
                w2 = true;
                w1 = false;
                //   }

                */

            }

        }
    };

    public static AvatarFragment newInstance(Contact contact) {
        Bundle args = new Bundle();
        args.putSerializable("contact", contact);
        AvatarFragment fragment = new AvatarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().getSerializable("contact") != null) {
            contact = (Contact) getArguments().getSerializable("contact");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.avatar_fragment, container, false);
        setHasOptionsMenu(true);

        getActivity().findViewById(R.id.fabMenuContainer).setVisibility(View.GONE);

       /* InputMethodManager imm = (InputMethodManager) mainView.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainView.getWindowToken(), 0);*/

        initKeyboard();


        initAvatar();

        initViews();

        initData();

        return mainView;
    }

    public void initKeyboard() {
        mainView.getViewTreeObserver().addOnGlobalLayoutListener(checkKeyboardListener);
    }

    public void initData(){

        mainView.findViewById(R.id.mobile_avatar_linear).setVisibility(View.VISIBLE);
        if(contact.getPhotoURL() != null){
            ((CircleImageView) mainView.findViewById(R.id.mobile_avatar)).setImageURI(Uri.parse(contact.getPhotoURL()));
        }else{
            ((CircleImageView) mainView.findViewById(R.id.mobile_avatar)).setImageDrawable(mainView.getResources().getDrawable(R.drawable.no_photo_primary));
        }
        ((TextView)mainView.findViewById(R.id.mobile_id)).setText(contact.getName());

        if(contact.getSocialModel() != null){


            if(contact.getSocialModel().getFacebookLink() != null) {
                String link = contact.getSocialModel().getFacebookLink();
                boolean checkich = false;
                try {
                    if(link.contains("http") && !link.contains("https"))
                        link = link.replace("http","https");
                    String checkFacebook = link.substring(0, 21);
                    String checkFacebook2 = link.substring(0, 23);
                    String checkFacebook3 = link.substring(0, 25);
                    if (checkFacebook.equals("https://facebook.com/")) {
                        link = link.substring(21, link.length());
                        if (link.contains("profile.php")) {
                            link = link.substring(link.indexOf(".php") + 5, link.length());
                        }

                        ((TextView) mainView.findViewById(R.id.facebook_id)).setText(link);
                        checkich = true;
                    } else if (checkFacebook2.equals("https://m.facebook.com/")) {
                        link = link.substring(23, link.length());
                        if (link.contains("profile.php")) {
                            link = link.substring(link.indexOf(".php") + 5, link.length());
                        }
                        ((TextView) mainView.findViewById(R.id.facebook_id)).setText(link);
                        checkich = true;
                    } else if (checkFacebook3.equals("https://www.facebook.com/") || checkFacebook3.equals("https://web.facebook.com/")) {
                        link = link.substring(25, link.length());
                        ((TextView) mainView.findViewById(R.id.facebook_id)).setText(link);
                        checkich = true;
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                if (!checkich) {
                    try {
                        String checkFacebook = link.substring(0, 25);
                        if (checkFacebook.equals("https://www.facebook.com/")) {
                            link = link.substring(25, link.length());
                            if (link.contains("profile.php")) {
                                link = link.substring(link.indexOf(".php") + 5, link.length());
                            }
                            ((TextView) mainView.findViewById(R.id.facebook_id)).setText(link);
                            checkich = true;
                        } else if (link.contains("php?")) {
                            int index = link.indexOf("php?");
                            link = link.substring(index + 4, link.length());
                            ((TextView) mainView.findViewById(R.id.facebook_id)).setText(link);
                            checkich = true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (!checkich)
                    ((TextView) mainView.findViewById(R.id.facebook_id)).setText(contact.getSocialModel().getFacebookLink());

                mainView.findViewById(R.id.facebook_avatar_linear).setVisibility(View.VISIBLE);
            }else mainView.findViewById(R.id.facebook_avatar_linear).setVisibility(View.GONE);

            if(contact.getSocialModel().getTwitterLink() != null){
                try {
                    /*String link = socialModel.getLinkedInLink();
                    String sub = link.substring(link.length() - 20, link.length());*/
                    if (contact.getSocialModel().getTwitterLink().contains(".com/")) {
                        ((TextView) mainView.findViewById(R.id.twitter_id)).setText(contact.getSocialModel().getTwitterLink().substring(contact.getSocialModel().getTwitterLink().indexOf(".com/") + 5));
                    } else
                        ((TextView) mainView.findViewById(R.id.twitter_id)).setText(contact.getSocialModel().getTwitterLink());
                } catch (Exception e) {
                    e.printStackTrace();

                }

                mainView.findViewById(R.id.twitter_avatar_linear).setVisibility(View.VISIBLE);
            }else mainView.findViewById(R.id.twitter_avatar_linear).setVisibility(View.GONE);

            if(contact.getSocialModel().getLinkedInLink() != null){

                try {
                    String link = contact.getSocialModel().getLinkedInLink();
                    if (link.contains("/in/")) {
                        String sub = link.substring(link.indexOf("/in/") + 4, link.length());
                        ((TextView) mainView.findViewById(R.id.linkedin_id)).setText(sub);
                    } else {
                        String sub = link.substring(link.length() - 20, link.length());
                        ((TextView) mainView.findViewById(R.id.linkedin_id)).setText(sub);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ((TextView) mainView.findViewById(R.id.linkedin_id)).setText(contact.getSocialModel().getLinkedInLink());
                }

                mainView.findViewById(R.id.linkedin_avatar_linear).setVisibility(View.VISIBLE);
            }else mainView.findViewById(R.id.linkedin_avatar_linear).setVisibility(View.GONE);

            if(contact.getSocialModel().getInstagramLink() != null){

                String inst = contact.getSocialModel().getInstagramLink();

                if (inst.contains(".com/")) {
                    int ind = inst.indexOf(".com/");
                    String outLink = inst.substring(ind + 5, inst.length());

                    if (outLink.contains("?")) {
                        int in = outLink.indexOf("?");
                        outLink = outLink.substring(0, in);
                    }

                    ((TextView) mainView.findViewById(R.id.instagram_id)).setText(outLink);
                } else
                    ((TextView) mainView.findViewById(R.id.instagram_id)).setText(contact.getSocialModel().getInstagramLink());

                mainView.findViewById(R.id.instagram_avatar_linear).setVisibility(View.VISIBLE);
            }else mainView.findViewById(R.id.instagram_avatar_linear).setVisibility(View.GONE);

            if(contact.getSocialModel().getYoutubeLink() != null){

                try {
                    /*String link = socialModel.getLinkedInLink();
                    String sub = link.substring(link.length() - 20, link.length());*/
                    if (contact.getSocialModel().getYoutubeLink().contains("user/") || contact.getSocialModel().getYoutubeLink().contains("channel/")) {
                        if (contact.getSocialModel().getYoutubeLink().contains("user/")) {
                            String text = contact.getSocialModel().getYoutubeLink().substring(contact.getSocialModel().getYoutubeLink().indexOf("user/") + 5);
                            ((TextView) mainView.findViewById(R.id.youtube_id)).setText(text);
                        } else if (contact.getSocialModel().getYoutubeLink().contains("channel/")) {
                            String text = contact.getSocialModel().getYoutubeLink().substring(contact.getSocialModel().getYoutubeLink().indexOf("channel/") + 8);
                            ((TextView) mainView.findViewById(R.id.youtube_id)).setText(text);
                        }
                    } else
                        ((TextView) mainView.findViewById(R.id.youtube_id)).setText(contact.getSocialModel().getYoutubeLink());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mainView.findViewById(R.id.youtube_avatar_linear).setVisibility(View.VISIBLE);
            }else mainView.findViewById(R.id.youtube_avatar_linear).setVisibility(View.GONE);

            if(contact.getSocialModel().getVkLink() != null){

                try {
                    String link = contact.getSocialModel().getVkLink();
                    String checkVK = link.substring(0, 8);
                    if (link.contains("https://vk.com/")) {
                        System.out.println("TRUE VK LINK");
                        link = link.substring(15, link.length());
                        System.out.println("TRUE VK LINK2 = " + link);
                        ((TextView) mainView.findViewById(R.id.vk_id)).setText(link);
                    } else if (link.contains("https://m.vk.com/")) {
                        link = link.substring(17, link.length());
                        ((TextView) mainView.findViewById(R.id.vk_id)).setText(link);

                    } else if (link.contains("m.vk.com/")) {
                        link = link.substring(9, link.length());
                        ((TextView) mainView.findViewById(R.id.vk_id)).setText(link);
                    } else if (link.contains("vk.com/")) {
                        link = link.substring(7, link.length());
                        ((TextView) mainView.findViewById(R.id.vk_id)).setText(link);
                    } else if (link.contains("https://www.vk.com/")) {
                        link = link.substring(19, link.length());
                        ((TextView) mainView.findViewById(R.id.vk_id)).setText(link);

                    } else
                        ((TextView) mainView.findViewById(R.id.vk_id)).setText(contact.getSocialModel().getVkLink());
                } catch (Exception e) {
                    e.printStackTrace();
                    ((TextView) mainView.findViewById(R.id.vk_id)).setText(contact.getSocialModel().getVkLink());
                }

                mainView.findViewById(R.id.vk_avatar_linear).setVisibility(View.VISIBLE);
            }else mainView.findViewById(R.id.vk_avatar_linear).setVisibility(View.GONE);

            if(contact.getSocialModel().getMediumLink() != null){

                String inst = contact.getSocialModel().getMediumLink();

                if (inst.contains(".com/")) {
                    int ind = inst.indexOf(".com/");
                    String outLink = inst.substring(ind + 5, inst.length());

                    if (outLink.contains("?")) {
                        int in = outLink.indexOf("?");
                        outLink = outLink.substring(0, in);
                    }

                    ((TextView) mainView.findViewById(R.id.medium_id)).setText(outLink);
                } else
                    ((TextView) mainView.findViewById(R.id.medium_id)).setText(contact.getSocialModel().getMediumLink());

                mainView.findViewById(R.id.medium_avatar_linear).setVisibility(View.VISIBLE);
            }else mainView.findViewById(R.id.medium_avatar_linear).setVisibility(View.GONE);

            if(contact.getSocialModel().getWhatsappLink() != null){

                ((TextView) mainView.findViewById(R.id.whatsapp_id)).setText(contact.getSocialModel().getWhatsappLink());

                mainView.findViewById(R.id.whatsapp_avatar_linear).setVisibility(View.VISIBLE);
            }else mainView.findViewById(R.id.whatsapp_avatar_linear).setVisibility(View.GONE);

            if(contact.getSocialModel().getSkypeLink() != null){

                ((TextView) mainView.findViewById(R.id.skype_id)).setText(contact.getSocialModel().getSkypeLink());

                mainView.findViewById(R.id.skype_avatar_linear).setVisibility(View.VISIBLE);
            }else mainView.findViewById(R.id.skype_avatar_linear).setVisibility(View.GONE);


        }else{
            mainView.findViewById(R.id.facebook_avatar_linear).setVisibility(View.GONE);
            mainView.findViewById(R.id.twitter_avatar_linear).setVisibility(View.GONE);
            mainView.findViewById(R.id.linkedin_avatar_linear).setVisibility(View.GONE);
            mainView.findViewById(R.id.instagram_avatar_linear).setVisibility(View.GONE);
            mainView.findViewById(R.id.youtube_avatar_linear).setVisibility(View.GONE);
            mainView.findViewById(R.id.vk_avatar_linear).setVisibility(View.GONE);
            mainView.findViewById(R.id.medium_avatar_linear).setVisibility(View.GONE);
            mainView.findViewById(R.id.whatsapp_avatar_linear).setVisibility(View.GONE);
            mainView.findViewById(R.id.skype_avatar_linear).setVisibility(View.GONE);
        }

        if(contact.getListOfContactInfo() != null && !contact.getListOfContactInfo().isEmpty()){
            boolean check_gmail = false;
            for (ContactInfo ci : contact.getListOfContactInfo()){
                if(ClipboardType.isEmail(ci.value) && ci.value.contains("gmail")){
                    check_gmail = true;
                    ((TextView) mainView.findViewById(R.id.gmail_id)).setText(ci.value);
                    mainView.findViewById(R.id.gmail_avatar_linear).setVisibility(View.VISIBLE);
                    break;
                }

            }
            if(!check_gmail) mainView.findViewById(R.id.gmail_avatar_linear).setVisibility(View.GONE);
        }else mainView.findViewById(R.id.gmail_avatar_linear).setVisibility(View.GONE);
    }

    private void initAvatar() {
        if (contact.getPhotoURL() != null) {
            mainView.findViewById(R.id.avatarContact).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.linear_no_photo).setVisibility(View.GONE);
            mainView.findViewById(R.id.linear_no_photo_little).setVisibility(View.GONE);

            Glide.with(mainView.getContext()).load(contact.getPhotoURL())/*.listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    // progressBar.setVisibility(View.GONE);



                    return false;
                }
            })*/.into((ImageView) mainView.findViewById(R.id.avatarContact));

            //((ImageView) mainView.findViewById(R.id.avatarContact)).setImageURI(Uri.parse(contact.getPhotoURL()));

        } else {
            mainView.findViewById(R.id.avatarContact).setVisibility(View.GONE);
            if(isKeyboard) {
                mainView.findViewById(R.id.linear_no_photo_little).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.linear_no_photo).setVisibility(View.GONE);
            }else {
                mainView.findViewById(R.id.linear_no_photo_little).setVisibility(View.GONE);
                mainView.findViewById(R.id.linear_no_photo).setVisibility(View.VISIBLE);

            }
        }
    }

    private void initViews() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {



        menu.findItem(R.id.save_contact).setVisible(false);

        menu.findItem(R.id.edit_contact).setVisible(false);
        menu.findItem(R.id.download_avatar_profile).setVisible(true);

        Toolbar mainToolBar = (Toolbar) getActivity().findViewById(R.id.main_toolbar);
        TextView cancel = (TextView) getActivity().findViewById(R.id.cancel_toolbar);

        mainToolBar.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_share).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_edit).setVisibility(View.GONE);
        mainToolBar.findViewById(R.id.toolbar_menu).setVisibility(View.GONE);

        ((TextView) mainToolBar.findViewById(R.id.toolbar_title)).setText("Avatar");

        menu.findItem(R.id.menu_profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainView.getViewTreeObserver().removeOnGlobalLayoutListener(checkKeyboardListener);
    }
}
