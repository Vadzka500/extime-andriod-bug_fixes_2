package ai.extime.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Interfaces.PopupsInter;
import ai.extime.Adapters.PlacesAdapter;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.Contact;
import ai.extime.Models.ContactInfo;
import ai.extime.Models.Place;
import com.extime.R;

/**
 * Created by patal on 21.09.2017.
 */

public class PlacesFragment extends Fragment implements PopupsInter, OnMapReadyCallback {

    private View mainView;

    private MapView mapView;

    private GoogleMap googleMap;

    private ClusterManager<Place> mClusterManager;

    private RecyclerView containerPlaces;

    private PlacesAdapter placesAdapter;

    private ArrayList<Place> listOfPlaces;

    private LinearLayoutManager mLinearLayoutManager;

    private ArrayList<View> openedViews;

    private ArrayList<Contact> listOfContacts;

    private ArrayList<ContactInfo> contactInfoArrayList;

    private Boolean mapChecker = true;

    private Boolean listChecker = true;

    private TextView placesList;

    private TextView placesMap;

    public FrameLayout placesPopup = null;

    private Toolbar toolbarC;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        //mainView = inflater.inflate(R.layout.places_layout_content, viewGroup, false);

        mainView = inflater.inflate(R.layout.layout_places_undes_construct, viewGroup, false);

        toolbarC = ((Postman) getActivity()).getToolbar();

        setHasOptionsMenu(true);

        //initViews(bundle);
        //initRecycler();
        //displayDataManager();
        //setListeners();
        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(!ContactAdapter.checkMerge)
        menu.getItem(0).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        ((MainActivity) mainView.getContext()).setContactsToContent();

        ((TextView) toolbarC.findViewById(R.id.toolbar_title)).setText("Places");

        if(!ContactAdapter.checkMerge)
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.GONE);

        toolbarC.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);

        super.onPrepareOptionsMenu(menu);
    }

    private void initViews(Bundle bundle){
        mapView = (MapView) mainView.findViewById(R.id.mapView);
        mapView.onCreate(bundle);
        mapView.onResume();
        mapView.getMapAsync(this);
        containerPlaces = (RecyclerView) mainView.findViewById(R.id.containerPlaces);
        mLinearLayoutManager = new LinearLayoutManager(mainView.getContext());
        containerPlaces.setLayoutManager(mLinearLayoutManager);
        openedViews = new ArrayList<>();
        placesList = (TextView) getActivity().findViewById(R.id.placesList);
        placesMap = (TextView) getActivity().findViewById(R.id.placesMap);
    }

    private void initRecycler(){
        placesAdapter = new PlacesAdapter(getActivity(),getData(),this);
        containerPlaces.setAdapter(placesAdapter);

    }


    private ArrayList<Place> getData(){
        listOfPlaces = new ArrayList<>();
        for (int indexI = 0; indexI < 25; indexI++){
            Place place = new Place();
            listOfPlaces.add(place);
        }
        return listOfPlaces;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (map != null) {
            googleMap = map;
            mClusterManager = new ClusterManager<>(mainView.getContext(), googleMap);
            googleMap.setOnCameraChangeListener(mClusterManager);
            listOfContacts = new ArrayList<>();
            listOfContacts.addAll(((Postman) getActivity()).getListOfContactsMain());

            contactInfoArrayList =  new ArrayList<>();
            for (Contact contact: listOfContacts) {
                contactInfoArrayList.clear();
                contactInfoArrayList.addAll(contact.listOfContactInfo);
                for (ContactInfo contactInfo : contactInfoArrayList) {
                    getLocationFromAddress(mainView.getContext(), contactInfo.value);
                    try {
                        if (contactInfo.isGeo) {
                            mClusterManager.addItem(new Place("asedff", getLocationFromAddress(mainView.getContext(), contactInfo.value)));
                        }
                    }catch (Exception e){}
                }
            }

            mClusterManager.cluster();

            LatLng testplace = new LatLng(45.118879, 2.069558);

            View marker = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
            googleMap.addMarker(new MarkerOptions()
                    .position(testplace)
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mainView.getContext(), marker))));
        }
    }

    public void setUpMap(){
        View marker = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);

//        String address = "СѓР». РџСѓС€РєРёРЅР° 20Р±, Р“СЂРѕРґРЅРѕ, Р‘РµР»Р°СЂСѓСЃСЊ";
//        String address2 = "СѓР». РћРіРёРЅСЃРєРѕРіРѕ, Р“СЂРѕРґРЅРѕ, Р‘РµР»Р°СЂСѓСЃСЊ";
//
//        LatLng currentLocation = getLocationFromAddress(mainView.getContext(), address);
//        LatLng currentLocation2 = getLocationFromAddress(mainView.getContext(), address2);
//
//        listOfPlaces.add(new Place(currentLocation));
//        listOfPlaces.add(new Place(currentLocation2));
//
//        googleMap.addMarker(new MarkerOptions()
//                .position(currentLocation)
//                .title("Р”Р РћРќ")
//                .snippet("РЎРЈРџР•Р РҐРђРљР•Р ")
//                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mainView.getContext(), marker))));
//
//        googleMap.addMarker(new MarkerOptions()
//                .position(currentLocation2)
//                .title("Р”Р РћРќ")
//                .snippet("РЎРЈРџР•Р РҐРђРљР•Р ")
//                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mainView.getContext(), marker))));
//
//        moveToCurrentLocation(currentLocation);
        clusterManager();

    }


    private void clusterManager() {
        mClusterManager = new ClusterManager<>(mainView.getContext(), googleMap);
        googleMap.setOnCameraChangeListener(mClusterManager);
//        addPlace();
    }

//    private void addPlace() {
//        mClusterManager.addItem(new Place(56.426054, 26.191407));
//        mClusterManager.addItem(new Place(56.423977, 26.176239));
//        mClusterManager.addItem(new Place(56.426678, 26.191498));
//        mClusterManager.addItem(new Place(56.426041, 26.191405));
//    }

    private void moveToCurrentLocation(LatLng currentLocation)  {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng contactAdress = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            contactAdress = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {}
        return contactAdress;
    }

    private void displayDataManager () {
        getActivity().findViewById(R.id.placesMap).setOnClickListener(v -> {
            if (mapChecker) {
                closeMap();
                mapChecker = false;
            } else {
                showMap();
                mapChecker = true;
            }
            if (!listChecker & !mapChecker){
                showList();
                listChecker = true;
            }
        });

        getActivity().findViewById(R.id.placesList).setOnClickListener(v -> {
            if (listChecker) {
                closeList();
                listChecker = false;
            } else {
                showList();
                listChecker = true;
            }
            if (!listChecker & !mapChecker){
                showMap();
                mapChecker = true;
            }
        });
    }

    private void showMap () {
        mainView.findViewById(R.id.placesMapView).setVisibility(View.VISIBLE);
        placesMap.setTextColor(Color.parseColor("#567bac"));
    }

    private void closeMap () {
        mainView.findViewById(R.id.placesMapView).setVisibility(View.GONE);
        placesMap.setTextColor(Color.parseColor("#9e9e9e"));
    }

    private void showList () {
        mainView.findViewById(R.id.placesListView).setVisibility(View.VISIBLE);
        placesList.setTextColor(Color.parseColor("#567bac"));
    }

    private void closeList () {
        mainView.findViewById(R.id.placesListView).setVisibility(View.GONE);
        placesList.setTextColor(Color.parseColor("#9e9e9e"));
    }

    private void setListeners(){
        mainView.setOnClickListener(v -> {
            closeOtherPopup();}
        );
        getActivity().findViewById(R.id.geoPlacesBlock).setOnClickListener(v -> {
            if(openedViews.contains(placesPopup)) {
                mainView.getRootView().findViewById(R.id.popup_places).setVisibility(View.GONE);
                openedViews.remove(placesPopup);
            }else
                showPlacesPopup();
        });
    }

    public void showPlacesPopup() {
        closeOtherPopup();
        placesPopup = (FrameLayout) mainView.getRootView().findViewById(R.id.popup_places);
        if(placesPopup.getVisibility() == View.VISIBLE){
            placesPopup.setVisibility(View.GONE);
            return;
        }
        placesPopup.setVisibility(View.VISIBLE);
        placesPopup.setFocusable(true);
        placesPopup.setClickable(true);
        placesPopup.requestFocus();
        if(openedViews != null) openedViews.add(placesPopup);
    }


    @Override
    public void closeOtherPopup() {
        if(openedViews != null) {
            for (View view : openedViews) {
                view.setVisibility(View.GONE);
            }
            openedViews.clear();
        }
    }


}
