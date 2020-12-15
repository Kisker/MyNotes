package net.learn2develop.mynotes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.AlertDialog;
import android.app.Dialog;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import net.learn2develop.mynotes.adapters.DrawerListAdapter;
import net.learn2develop.mynotes.database.DataBaseHelper;
import net.learn2develop.mynotes.dialogs.AboutDialog;
import net.learn2develop.mynotes.fragments.DetailFragment;
import net.learn2develop.mynotes.fragments.ListFragment;
import net.learn2develop.mynotes.model.NavigationItem;
import net.learn2develop.mynotes.model.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListFragment.OnProductSelectedListener {

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;

    private ArrayList<NavigationItem> navigationItems = new ArrayList<NavigationItem>();

    private AlertDialog dialog;

    private boolean listShown = false;
    private boolean detailShown = false;

    private DataBaseHelper databaseHelper;


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            selectItemFromDrawer(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Draws navigation items
        navigationItems.add(new NavigationItem(getString(R.string.drawer_home), getString(R.string.drawer_home_long), R.drawable.ic_action_product));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_settings), getString(R.string.drawer_Settings_long), R.drawable.ic_action_settings));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_about), getString(R.string.drawer_about_long), R.drawable.ic_action_about));

        title = drawerTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.navList);

        // Populate the Navigtion Drawer with options
        DrawerListAdapter adapter = new DrawerListAdapter(this, navigationItems);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(adapter);

        // Enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportActionBar().setTitle("");
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("");
                super.onDrawerOpened(drawerView);
            }
        };

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ListFragment listFragment = new ListFragment();
            ft.add(R.id.displayList, listFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            selectItemFromDrawer(0);
        }

        listShown = true;
        detailShown = false;
    }

    // Reset method
    private void reset(){
    }

    private void selectItemFromDrawer(int position) {
        if (position == 0){

        } else if (position == 1){
            Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settings);
        } else if (position == 2){
            if (dialog == null){
                dialog = new AboutDialog(MainActivity.this).prepareDialog();
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            dialog.show();
        }

        drawerList.setItemChecked(position, true);
    }

    // The final method CategorySpinner, unlike the previous ones, is associated with the DatabaseHelper class and through it we invite it to save
    // changes to the dialog spinner. In case we don't call getDatabaseHelper (). GetCategoryDao (). QueryForAll () our application
    // would crash, that is, it would not save any additions.

    private void addItem() throws SQLException {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_about);
        final EditText productName = (EditText) dialog.findViewById(R.id.product_name);
        final EditText productDescr = (EditText) dialog.findViewById(R.id.product_description);

    Button ok = (Button) dialog.findViewById(R.id.ok);
    ok.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!productName.getText().toString().equals("") && !productDescr.getText().toString().equals("")) {
                try {

                    String name = productName.getText().toString();
                    String desct = productDescr.getText().toString();

                    Product product = new Product();
                    product.setName(name);
                    product.setDescription(desct);

                    getDatabaseHelper().getProductDao().create(product);
                    refresh();
                    Toast.makeText(MainActivity.this, "Data is inserted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // reset (), which we specified in the previous step, must be set here. He indicates that he will
                    // reset all our data and return it to the starting position
                    reset();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "Fill all fields with data", Toast.LENGTH_SHORT).show();
            }
        }
        });

    Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    });

        dialog.show();
  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_item_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // onCreateOptionsMenu and onOptionsItemSelected are linked to our Action Bar - in this case adding and refresh

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.action_add:
                try {
                    addItem();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // refresh () displays new content.
    private void refresh() {
        ListView listview = (ListView) findViewById(R.id.products);

        if (listview != null){
            ArrayAdapter<Product> adapter = (ArrayAdapter<Product>) listview.getAdapter();

            if(adapter!= null)
            {
                try {
                    adapter.clear();
                    List<Product> list = getDatabaseHelper().getProductDao().queryForAll();

                    adapter.addAll(list);

                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Data is refreshed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    // The onProductSelected and onBackPressed methods always go together. The first signifies some change and preservation, and the second that when we make
    // the previous action, we can be returned to the "initial" application window.
    @Override
    public void onProductSelected(int id) {
        try {
            Product product = getDatabaseHelper().getProductDao().queryForId(id);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setProduct(product);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.displayList, detailFragment, "Detail_Fragment2");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack("Detail_Fragment2");
            ft.commit();
            // If we put true for listShown, every time we clicked on the "save" console, our app would save it that
            // information, but an App crash would occur immediately. So we put on false, and detailShown on true!
            listShown = false;
            detailShown = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if (listShown == true) {
            finish();
        } else if (detailShown == true) {
            getSupportFragmentManager().popBackStack();
            ListFragment listFragment = new ListFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();;
            ft.replace(R.id.displayList, listFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            listShown = true;
            detailShown = true;
        }

    }
    // DataBase
    public DataBaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // after working with the database is required
        // to free resources! All the time.
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}