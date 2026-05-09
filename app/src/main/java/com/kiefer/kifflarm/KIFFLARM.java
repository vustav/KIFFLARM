package com.kiefer.kifflarm;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.alarm.AlarmManager;
import com.kiefer.kifflarm.alarm.AlarmsAdapter;
import com.kiefer.kifflarm.alarm.AlarmsTouchHelper;
import com.kiefer.kifflarm.drawables.DrawablePlus;
import com.kiefer.kifflarm.files.FileManager;
import com.kiefer.kifflarm.profiles.AlarmsAdapterProfileMain;
import com.kiefer.kifflarm.profiles.Profile;
import com.kiefer.kifflarm.profiles.ProfilesPopup;
import com.kiefer.kifflarm.profiles.QuickProfilesTouchHelper;
import com.kiefer.kifflarm.sound.PromptPopup;
import com.kiefer.kifflarm.sound.VolumePopup;
import com.kiefer.kifflarm.profiles.ProfilesManager;
import com.kiefer.kifflarm.profiles.QuickProfilesAdapter;
import com.kiefer.kifflarm.sound.SoundManager;
import com.kiefer.kifflarm.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class KIFFLARM extends AppCompatActivity {
    private SoundManager soundManager;
    private FileManager fileManager;
    private ProfilesManager profilesManager;
    private RelativeLayout layout;
    private AlarmManager alarmManager;
    private AlarmsAdapter alarmsAdapter;
    private AlarmsAdapterProfileMain profileAlarmsAdapter;
    private QuickProfilesAdapter quickProfilesAdapter;
    private RelativeLayout profileLblLayoutOuter, profileLblLayoutInner;
    private TextView profileLblTV;
    private FrameLayout profilesLayout;
    //private ArrayList<Alarm> alarms;
    private final boolean SHOW_TRIGGER = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         */

        readSharedPreferences();

        //MONEY
        if(!fullVersion) {
            setupBilling();
        }

        //create a timer to check the version after a little delay. Doing it right away does't work since it takes a while to get the return from play store
        Handler handler = new Handler();
        handler.postDelayed(this::checkFullVersion, 1000); //do this when checking volume???

        //MEMBERS
        fileManager = new FileManager(this);
        profilesManager = new ProfilesManager(this);
        alarmManager = new AlarmManager(this, getResources().getString(R.string.custom_alarms_folder));
        soundManager = new SoundManager(this);

        //LAYOUT
        setupLayout();

        //PERMISSIONS
        checkPermissions();

        //this ensures the layout is ready when the popup is created
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                checkVolume(layout);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        //Log.e("KIFFLARM ZZZ", "onResume");

        /*
        if an alarm goes off when the main Activity is running nothing in it it gets updated .This
        means that the toggle on the alarm that just went of will still be on. Since the alarm is its
        own it can only save the change, not update it directly, so the get it we need to reload alarms
        and update the adapter
         */
        //loadAlarms(); //load here instead of onCreate since turning an alarm off in AlarmActivity does not update alarms here, they are saved there and needs to be reloaded here
        alarmManager.loadAlarms(fileManager);
        profilesManager.loadProfiles(fileManager);

        if(alarmsAdapter != null){
            alarmsAdapter.onResume();
        }

        if(ongoingAlarm != null){
            Log.e("KIFFLARM ZZZ", "ongoing: "+ongoingAlarm.getId());
        }
        else{
            Log.e("KIFFLARM ZZZ", "NO ongoing");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        saveSharedPreferences();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        saveSharedPreferences();
    }

    /** SHARED PREFERENCES **/
    private String FULL_VERSION_PREF = "full_version";
    public void saveSharedPreferences() {
        Log.e("KIFFNOTES ZZZ", "saveSharedPreferences");
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(FULL_VERSION_PREF, fullVersion);
        editor.apply();
    }

    private void readSharedPreferences() {
        Log.e("KIFFNOTES ZZZ", "readSharedPreferences");
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        boolean defaultBool = false;
        fullVersion = sharedPref.getBoolean(FULL_VERSION_PREF, defaultBool);
    }

    /** PERMISSIONS **/
    //if the permission is denied the dialog will not show again...
    public static int POST_NOTIFICATIONS_PERMISSION_CODE = 34564576;
    public void checkPermissions(){
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }
    }

    public void askPermission(){
        //Log.e("KIFFLARM ZZZ", "askPermission");
        // Should we show an explanation?
        //if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

        // Explain to the user why we need this permission
        new AlertDialog.Builder(this)
                .setTitle("PERMISSION TO POST NOTIFICATIONS")
                .setMessage("NEED THIS FOR ALARMS TO WORK")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, POST_NOTIFICATIONS_PERMISSION_CODE);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        //}
    }

    /** MONEY **/
    private boolean fullVersion = false;
    private BillingClient billingClient;
    private List<ProductDetails> productDetails = new ArrayList<>();

    private void setupBilling(){
        createBillingClient();
        startConnection();
    }

    private void createBillingClient(){
        if(billingClient == null) {
            PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
                @Override
                public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (Purchase purchase : purchases) {
                            // Process the purchase as described in the next section.
                            handlePurchase(purchase);
                        }
                    } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                        // Handle an error caused by a user canceling the purchase flow.
                    } else {
                        // Handle any other error codes.
                    }
                }
            };

            PendingPurchasesParams params = PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build();

            billingClient = BillingClient.newBuilder(this)
                    .setListener(purchasesUpdatedListener)
                    .enablePendingPurchases(params)
                    .enableAutoServiceReconnection() // Add this line to enable reconnection
                    // Configure other settings.
                    .build();
        }
    }

    private void startConnection(){
        if(billingClient != null) {
            billingClient.startConnection(
                    new BillingClientStateListener() {
                        @Override
                        public void onBillingSetupFinished(BillingResult billingResult) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                // The BillingClient is ready. You can query purchases here.
                                // It's a good practice to query products after the connection is established.
                                queryProductDetails();

                                //this is because sometimes this finishes after the other checks and the app starts without full version even if it has full access
                                checkFullVersion();
                            }
                        }

                        @Override
                        public void onBillingServiceDisconnected() {
                            // Try to restart the connection on the next request to
                            // Google Play by calling the startConnection() method.
                            // This is automatically handled by the library when you call a method that requires a connection.

                            //startConnection();
                            //enableAutoServiceReconnection() when creating the billingClient should handle this, otherwise uncomment startConnection above
                        }
                    });
        }
        else{
            setupBilling();
        }
    }

    private void queryProductDetails(){
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                List.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("full_version")
                                                .setProductType(BillingClient.ProductType.INAPP)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult, QueryProductDetailsResult queryProductDetailsResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            productDetails.clear();
                            productDetails.addAll(queryProductDetailsResult.getProductDetailsList());
                            /*
                            for (ProductDetails pd : queryProductDetailsResult.getProductDetailsList()) {
                                // Process successfully retrieved product details here.
                            }

                            for (UnfetchedProduct unfetchedProduct : queryProductDetailsResult.getUnfetchedProductList()) {
                                // Handle any unfetched products as appropriate.
                            }

                             */
                        }
                    }
                }
        );
    }

    private void buyFullVersion(){
        if(billingClient != null) {
            if (!productDetails.isEmpty()) {
                List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                        List.of(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()

                                        .setProductDetails(productDetails.get(0)) //THIS SEEMS TO WORK WITH ONLY ONE PRODUCT AT LEAST

                                        // Get the offer token:
                                        // a. For one-time products, call ProductDetails.getOneTimePurchaseOfferDetailsList()
                                        // for a list of offers that are available to the user.
                                        // b. For subscriptions, call ProductDetails.getSubscriptionOfferDetails()
                                        // for a list of offers that are available to the user.

                                        //WHAT IS THIS??
                                        //.setOfferToken(ProductDetails.getOneTimePurchaseOfferDetailsList())
                                        .build()
                        );

                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .setIsOfferPersonalized(true)
                        .build();

                // Launch the billing flow
                BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message = "DIDN'T FIND ANY PRODUCTS";
                        Toast toast = Toast.makeText(KIFFLARM.this, message, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

                Log.e("KIFFLMARM ZZZ", "list empty in KIFFLARM.buyFullVersion");
            }
        }
        else{
            setupBilling();
            buyFullVersion();
        }
    }

    void handlePurchase(Purchase purchases) {

        //got a weird crash here where billingClient was null after purchase. Maybe because of screen rotation when entering the store??
        if(billingClient == null){
            createBillingClient();
            handlePurchase(purchases);
        }

        if (!purchases.isAcknowledged()) {
            billingClient.acknowledgePurchase(AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchases.getPurchaseToken())
                    .build(), billingResult -> {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    setFullVersion(true);
                }
            });
        }
    }

    //ArrayList<Button> buyBtns = new ArrayList<>();
    public void promptFullVersion(){
        checkFullVersion();

        if(!fullVersion) {
            String txt = "THIS FEATURE IS ONLY AVAILABLE IN THE FULL VERSION.";
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyFullVersion();
                }
            };

            new PromptPopup(this, listener, txt, "BUY");
        }
    }

    private void checkFullVersion(){
        if(billingClient == null){
            setupBilling();
            return;
        }

        QueryPurchasesParams queryPurchasesParams = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build();
        billingClient.queryPurchasesAsync(queryPurchasesParams, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                //since we only have one in app-purchase there's no need to parse the result
                setFullVersion(!list.isEmpty());
            }
        });
    }

    public void setFullVersion(boolean fullVersion){
        Log.e("KIFFLARM ZZZ", "setFull: "+fullVersion);
        this.fullVersion = fullVersion;
        enableProfiles(fullVersion);
    }

    /** VOLUME **/
    boolean volumeWarned = false; //without this you get two popups. Probably something with getViewTreeObserver
    private void checkVolume(ViewGroup layout){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        float volume = ((float) currentVolume) / maxVolume;

        if(volume < .5f){
            if(!volumeWarned) {
                new VolumePopup(KIFFLARM.this);
                volumeWarned = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*
        for (int i=0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];
            if (permission.equals(Manifest.permission.POST_NOTIFICATIONS ) && grantResult == PackageManager.PERMISSION_GRANTED) {
                //
            } else {
                //
            }
        }
         */
    }

    /** LAYOUT **/
    private void setupLayout(){
        //layout = (RelativeLayout) kifflarm.getLayoutInflater().inflate(R.layout.layout_main_view, null);

        layout = findViewById(R.id.main);

        //NICE BG
        TextView bgTVtv = layout.findViewById(R.id.mainBgTV);
        Utils.createNiceBg(layout, bgTVtv, 100);

        //PROFILES
        profilesLayout = layout.findViewById(R.id.profilesLayout);

        RelativeLayout profilesBg = layout.findViewById(R.id.profilesBg);
        int profilesBgColor1 = Utils.getRandomColor();
        profilesBg.setBackground(Utils.getGradientDrawable(profilesBgColor1, Utils.getRandomColor(), Utils.HORIZONTAL));

        //LABEL
        /*
        TextView profilesLbl1 = layout.findViewById(R.id.profilesTV1);
        TextView profilesLbl2 = layout.findViewById(R.id.profilesTV2);
        TextView profilesLbl3 = layout.findViewById(R.id.profilesTV3);
        profilesLbl1.setBackgroundColor(Utils.getContrastColor(profilesBgColor1));
        profilesLbl2.setBackgroundColor(Utils.getContrastColor(profilesBgColor1));
        profilesLbl3.setBackgroundColor(Utils.getContrastColor(profilesBgColor1));
        profilesLbl1.setTextColor(profilesBgColor1);
        profilesLbl2.setTextColor(profilesBgColor1);
        profilesLbl3.setTextColor(profilesBgColor1);

         */

        layout.findViewById(R.id.profilesMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fullVersion) {
                    new ProfilesPopup(KIFFLARM.this, profilesManager);
                }
                else{
                    promptFullVersion();
                }
            }
        });

        layout.findViewById(R.id.profilesDivider).setBackgroundColor(Utils.getContrastColor(profilesBgColor1));

        //set up the quick recyclerView
        RecyclerView quickRecyclerView = layout.findViewById(R.id.quickProfilesRecyclerView);
        quickRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        quickProfilesAdapter = new QuickProfilesAdapter(this, quickRecyclerView, profilesManager);
        quickRecyclerView.setAdapter(quickProfilesAdapter);

        QuickProfilesTouchHelper quickTouchHelper = new QuickProfilesTouchHelper(quickProfilesAdapter, profilesManager);
        ItemTouchHelper quickHelper = new ItemTouchHelper(quickTouchHelper);
        quickHelper.attachToRecyclerView(quickRecyclerView);

        //PROFILE ALARMS RECYCLER
        RecyclerView profileAlarmsRecyclerView = layout.findViewById(R.id.profileAlarmsRecyclerView);
        profileAlarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        profileAlarmsAdapter = new AlarmsAdapterProfileMain(this, profilesManager);
        profileAlarmsRecyclerView.setAdapter(profileAlarmsAdapter);

        //ACTIVE PROFILE LBL
        profileLblLayoutInner = layout.findViewById(R.id.profilesLblLayoutInner);
        profileLblLayoutOuter = layout.findViewById(R.id.profilesLblLayoutOuter);
        profileLblTV = layout.findViewById(R.id.profileLblTV);
        profileLblTV.setTextColor(Utils.getContrastColor(profilesBgColor1));
        Button profileLblDelBtn = layout.findViewById(R.id.profileLblDelBtn);
        profileLblDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("KIFFLARM ZZZ", "deactivate");
                profilesManager.deactivateAllProfiles();
                updateProfilesUI();
            }
        });
        RelativeLayout profileLblDelBtnBg = layout.findViewById(R.id.profileLblDelBtnBg);
        int delColor = Utils.getRandomColor();
        profileLblDelBtnBg.setBackground(Utils.getGradientDrawable(delColor, Utils.getRandomColor(), Utils.HORIZONTAL));
        TextView profileLblDelBtnTV = layout.findViewById(R.id.profileLblDelBtnTV);
        profileLblDelBtnTV.setTextColor(Utils.getContrastColor(delColor));

        enableProfiles(fullVersion);

        //updateProfilesUI(); //no use doing this update here since profiles are loaded in onResume, whicj hasn't happened yet

        /*
        if(profilesManager.getActiveProfile() != null){
            setProfileLbl(profilesManager.getActiveProfile());
        }
        else{
           enableProfileLbl(false);
        }

         */

        //AlarmsTouchHelper touchHelper = new AlarmsTouchHelper(profileAlarmsAdapter);
        //ItemTouchHelper helper = new ItemTouchHelper(touchHelper);
        //helper.attachToRecyclerView(profileAlarmsRecyclerView);

        //CUSTOM ALARMS RECYCLER
        RecyclerView alarmsRecyclerView = layout.findViewById(R.id.alarmsRecyclerView);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        alarmsAdapter = new AlarmsAdapter(this, alarmManager);
        alarmsRecyclerView.setAdapter(alarmsAdapter);

        AlarmsTouchHelper touchHelper = new AlarmsTouchHelper(alarmsAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(touchHelper);
        helper.attachToRecyclerView(alarmsRecyclerView);

        //ADD
        RelativeLayout addBtn = layout.findViewById(R.id.addAlarmBg);
        addBtn.setBackground(Utils.getRandomGradientDrawable());
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(addBtn);

                if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    askPermission();
                }
                else {
                    alarmsAdapter.openNewAlarmDialog(alarmsAdapter);
                }
            }
        });

        ImageView addIcon = layout.findViewById(R.id.addAlarmIcon);
        addIcon.setImageDrawable(new DrawablePlus());

        //TEST ALARM
        Button shortAlarmBtn = layout.findViewById(R.id.createShortAlarmBtn);
        if(SHOW_TRIGGER) {
            shortAlarmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    //TRIGGER ALARM
                    Intent intent = new Intent(KIFFLARM.this, AlarmActivity.class);
                    intent.putExtra(Alarm.ALRM_ID_TAG, Integer.toString(alarmManager.getAlarms().get(0).getId()));

                    //new AlarmCannonActivity(KIFFLARM.this, intent);
                    new AlarmCannonNotification(KIFFLARM.this, intent);

                     */

                    /*
                    //LIST ALL SAVED ALARMS
                    ArrayList<ArrayList<Param>> params = fileManager.getAllParamsArrays();
                    for(ArrayList<Param> ap : params){
                        for(Param p : ap){
                            if(p.key.equals(Alarm.HOUR_TAG)){
                                Log.e("KIFFLARM ZZZ", "minute: "+p.value);
                            }
                            if(p.key.equals(Alarm.MINUTE_TAG)){
                                Log.e("KIFFLARM ZZZ", "minute: "+p.value);
                                Log.e("KIFFLARM ZZZ", "----------------------");
                            }
                        }
                    }

                     */
                }
            });
        }
        else{
            shortAlarmBtn.setVisibility(View.INVISIBLE);
        }
    }

    /** PROFILES **/
    private void enableProfiles(boolean enable){
        enableView(profilesLayout, enable);
    }
    private void enableView(View v, boolean enable){
        if(v instanceof ViewGroup){
            ViewGroup vg = (ViewGroup) v;
            for(int i = 0; i<vg.getChildCount(); i++){
                enableView(vg.getChildAt(i), enable);
            }
        }
        else{
            //v.setEnabled(enable);

            if(enable){
                v.setAlpha(1);
            }
            else{
                v.setAlpha(.5f);
            }
        }
    }
    public void updateProfilesUI(){
        Log.e("KIFFLARM ZZZ", "updateUI, profilesManager == null: "+(profilesManager == null));
        quickProfilesAdapter.notifyDataSetChanged();
        profileAlarmsAdapter.notifyDataSetChanged();

        if(profilesManager.getActiveProfile() != null){
            enableProfileLbl(true);
            setProfileLbl(profilesManager.getActiveProfile());
        }
        else{
            enableProfileLbl(false);
        }
    }
    public void setProfileLbl(Profile profile){
        profileLblTV.setText(profile.getName());
        /*
        profileLblTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditProfilePopup(this, profilesManager, profilesPopup, profilesManager.getProfiles().get(viewHolder.getAdapterPosition()), false);
            }
        });

         */
    }
    public void enableProfileLbl(boolean enable){
        if(enable){
            //profileLblLayoutOuter.setVisibility(View.VISIBLE);
            if(profileLblLayoutInner.getParent() == null) {
                profileLblLayoutOuter.addView(profileLblLayoutInner);
            }
        }
        else{
            //profileLblLayoutOuter.setVisibility(View.INVISIBLE);
            if(profileLblLayoutInner.getParent() != null) {
                profileLblLayoutOuter.removeView(profileLblLayoutInner);
            }
        }
    }

    /** GET **/
    public FileManager getFileManager() {
        return fileManager;
    }
/*
    public QuickProfilesAdapter getQuickProfilesAdapter() {
        return quickProfilesAdapter;
    }

 */

    public AlarmsAdapter getAlarmsAdapter() {
        return alarmsAdapter;
    }

    public AlarmsAdapter getProfileAlarmsAdapter() {
        return profileAlarmsAdapter;
    }



    public SoundManager getSoundManager() {
        return soundManager;
    }

    public RelativeLayout getLayout(){
        return layout;
    }

    /** ONGOING ALARM **/
    private static Alarm ongoingAlarm;
    public static void setOngoingAlarm(Alarm alarm){
        ongoingAlarm = alarm;
    }
    public static void resetOngoingAlarm(){
        ongoingAlarm = null;
    }

    /** DESTRUCTION **/
    @Override
    public void onStop(){
        super.onStop();
    }
}